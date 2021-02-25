package ar.edu.ubp.das.crawling;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	static {
		System.setProperty("DaoFactoryPrefix", "MS");
		System.setProperty("ProviderName", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		System.setProperty("ConnectionString", "jdbc:sqlserver://localhost;databaseName=buscador;user=sa;password=secret");
	}

	public static void main(String[] args) throws Exception {
		MyLogger logger = new MyLogger("Controller");
		Websites websitesManager = new Websites();
		List<UserWebsitesBean> websites = websitesManager.getWebsitesPerUser();
		if (websites != null && websites.size() > 0) {
			crawl(websites);
		} else {
			logger.log(MyLogger.INFO, "No se encontraron paginas para reindexar, saltando crawling");
		}
	}

	public static void crawl(List<UserWebsitesBean> usersWebsites) {
		MyLogger logger = new MyLogger("Controller");
		logger.log(MyLogger.INFO, "Iniciando crawling");
		try {
			Dao<WebsiteBean, WebsiteBean> dao = DaoFactory.getDao("Website", "ar.edu.ubp.das");
			// Iteramos un usuario a la vez, cada uno con su set de paginas, sino se pisa la frontera
			for (UserWebsitesBean userWebsites : usersWebsites) {
				List<CrawlController> controllers = new ArrayList<CrawlController>();
				List<WebsiteBean> websitesUser = Utils.parseCsv(userWebsites);
				for (WebsiteBean website : websitesUser) {
					if (Utils.pingURL(website.getUrl(), 5000)) {
						controllers.add(startCrawler(website));
					} else {
						logger.log(MyLogger.WARNING, website.getUrl() + " caida");
						website.setIsUp(false);
						dao.update(website); // set isUp = false
					}
				}
				for (CrawlController controller : controllers) {
					controller.waitUntilFinish();
				}
				File dir = new File("/tmp/crawler4j");
				FileUtils.cleanDirectory(dir);
				for (WebsiteBean website : websitesUser) {
					if (website.getIsUp()) {
						dao.update(website.getWebsiteId()); // set indexed
					}
				}
			}
			logger.log(MyLogger.INFO, "Crawling terminado");
			System.exit(0);
		} catch (Exception e) {
			logger.log(MyLogger.ERROR, "Error al realizar el crawling. ERROR: " + e.getMessage());
		}
	}
	
	public static CrawlController startCrawler(WebsiteBean website) {
		CrawlController controller = null;
		CrawlConfig config = new CrawlConfig();
        File dir = Utils.getFolderName(0);
        config.setIncludeHttpsPages(true);
        config.setPolitenessDelay(200);
        config.setMaxDepthOfCrawling(2);
        config.setMaxPagesToFetch(10);
        config.setMaxDownloadSize(2097152);
        config.setIncludeBinaryContentInCrawling(true);
		config.setCrawlStorageFolder(dir.toString());
        config.setResumableCrawling(false);
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		try {
			controller = new CrawlController(config, pageFetcher, robotstxtServer);
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Lanzado Crawler para: " + website.getUrl());
		WebCrawlerFactory<MyCrawler> factory = () -> new MyCrawler(Utils.getDomainName(website.getUrl()), website.getUserId(), website.getWebsiteId());
        controller.addSeed(website.getUrl());
        controller.startNonBlocking(factory, 8);
        return controller;
    }
}
