package ar.edu.ubp.das.crawling;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;

import ar.edu.ubp.das.beans.UserWebsitesBean;
import ar.edu.ubp.das.beans.WebsiteBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;
import ar.edu.ubp.das.logging.MyLogger;
import ar.edu.ubp.das.utils.Utils;
import ar.edu.ubp.das.websites.Websites;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	static {
		System.setProperty("DaoFactoryPrefix", "MS");
		System.setProperty("ProviderName", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		System.setProperty("ConnectionString", "jdbc:sqlserver://localhost;databaseName=users;user=sa;password=secret");
	}

	public static void main(String[] args) throws Exception {
		MyLogger logger = new MyLogger("Controller");
        Websites websitesManager = new Websites();
        List<UserWebsitesBean> websites = websitesManager.getWebsitesPerUser();
        if (websites != null && websites.size() > 0) {
        	doCrawling(websites);
        } else {
        	logger.log(MyLogger.INFO, "No se encontraron paginas para reindexar, saltando crawling");
        }
	}

	public static void doCrawling(List<UserWebsitesBean> usersWebsites) {
		MyLogger logger = new MyLogger("Controller");
		logger.log(MyLogger.INFO, "Iniciando crawling");
		CrawlConfig config = initConfig();
		Statistics stats = new Statistics();
		try {
			Dao<WebsiteBean, WebsiteBean> dao = DaoFactory.getDao("Website", "ar.edu.ubp.das");
			// Iteramos un usuario a la vez, cada uno con su set de paginas, sino se pisa la
			// frontera
			for (UserWebsitesBean userWebsites : usersWebsites) {
				File dir = Utils.getFolderName(0);
				config.setCrawlStorageFolder(dir.toString());
				PageFetcher pageFetcher = new PageFetcher(config);
				RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
				RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
				CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
				List<WebsiteBean> websitesUser = Utils.parseCsv(userWebsites);
				Map<String, Integer> domains = new HashMap<String, Integer>();
				for (WebsiteBean website : websitesUser) {
					if (Utils.pingURL(website.getUrl(), 5000)) {
						String domainName = Utils.getDomainName(website.getUrl());
						domains.put(domainName, website.getWebsiteId());
						controller.addSeed(website.getUrl());
					}
					else {
						logger.log(MyLogger.WARNING, website.getUrl() + " caida");
						website.setIsUp(false);
						// set isUp = false
						dao.update(website);
					}
				}
				int numberOfCrawlers = 8;
				CrawlController.WebCrawlerFactory<MyCrawler> factory = 
						() -> new MyCrawler(stats, domains, userWebsites.getUserId());
				controller.start(factory, numberOfCrawlers);
				FileUtils.deleteDirectory(dir);
				for (WebsiteBean website : websitesUser) {
					if (website.getIsUp()) {
						// set indexed
						dao.update(website.getWebsiteId());
					}
				}
			}
			logger.log(MyLogger.INFO, "Crawling terminado");
			logger.log(MyLogger.INFO, stats.toString());
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static CrawlConfig initConfig() {
		CrawlConfig config = new CrawlConfig();
		config.setIncludeHttpsPages(true);
		config.setPolitenessDelay(1000);
		config.setMaxDepthOfCrawling(2);
		config.setMaxPagesToFetch(50);
		config.setIncludeBinaryContentInCrawling(true);
		config.setResumableCrawling(false);
		return config;
	}
}
