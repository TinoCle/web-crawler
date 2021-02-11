package ar.edu.ubp.das.crawling;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;

import ar.edu.ubp.das.beans.UserWebsitesBean;
import ar.edu.ubp.das.beans.WebsiteBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;
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
		Websites websitesManager = new Websites();
		List<UserWebsitesBean> websites = websitesManager.getWebsitesPerUser();
		if (websites != null && websites.size() > 0) {
			doCrawling(websites);
		} else {
			// TODO: Log
			System.out.println("No se encontraron paginas para reindexar, saltando crawling...");
		}
	}

	public static void doCrawling(List<UserWebsitesBean> usersWebsites) {
		System.out.println("Iniciando Crawling...");
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
				List<String> domains = new ArrayList<String>();
				Map<String, Integer> domainsId = new HashMap<String, Integer>();
				for (WebsiteBean website : websitesUser) {
					if (Utils.pingURL(website.getUrl(), 5000)) {
						domains.add(website.getUrl());
						String domainName = Utils.getDomainName(website.getUrl());
						domainsId.put(domainName, website.getWebsiteId());
						controller.addSeed(website.getUrl());
					} else {
						// TODO: Log
						System.out.println(website.getUrl() + " caida");
						dao.update(website.getWebsiteId());
						website.setIsUp(false);
					}
				}
				// controller.addSeed("https://stackoverflow.com/questions/1026723/how-to-convert-a-map-to-list-in-java");
				int numberOfCrawlers = 8;
				CrawlController.WebCrawlerFactory<MyCrawler> factory = () -> new MyCrawler(stats, domains, domainsId,
						userWebsites.getUserId());
				controller.start(factory, numberOfCrawlers);
				FileUtils.deleteDirectory(dir);

				for (WebsiteBean website : websitesUser) {
					if (website.getIsUp()) {
						dao.update(website);
					}
				}
			}
			System.out.println(stats.toString());
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
