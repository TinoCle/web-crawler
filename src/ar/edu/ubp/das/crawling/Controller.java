package ar.edu.ubp.das.crawling;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import ar.edu.ubp.das.beans.WebsiteBean;
import ar.edu.ubp.das.meilisearch.MeiliSearch;
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
		System.setProperty("ConnectionString", "jdbc:sqlserver://localhost;databaseName=users;user=sa;password=Charmander7!");
	}

	public static void main(String[] args) throws Exception {
        Websites websitesManager = new Websites();
        List<WebsiteBean> websites = websitesManager.getWebsitesPerUser();
        if (websites != null && websites.size() > 0) {
        	doCrawling(websites);
        } else {
        	System.out.println("No se encontraron páginas para reindexar, saltando crawling...");
        }
	}

	public static void doCrawling(List<WebsiteBean> websites) {
		System.out.println("Iniciando Crawling...");
		CrawlConfig config = new CrawlConfig();
		File dir = getFolderName(0);
        config.setCrawlStorageFolder(dir.toString());
        config.setIncludeHttpsPages(true);
        config.setPolitenessDelay(1000);
        config.setMaxDepthOfCrawling(2);
        config.setMaxPagesToFetch(50);
        config.setIncludeBinaryContentInCrawling(true);
        config.setResumableCrawling(false);
        Statistics stats = new Statistics();
        // Cramos la base acá para que los crawlers no se pisen los IDs entre ellos
        MeiliSearch meilisearch = new MeiliSearch();
		try {
			// Iteramos un usuario a la vez, cada uno con su set de páginas
			for (WebsiteBean user : websites) {
				PageFetcher pageFetcher = new PageFetcher(config);
				RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
				RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
				CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
				List<String> domains = Arrays.asList(user.getWebsites().split(","));
				for (String url : domains) {
					controller.addSeed(url);
				}
				int numberOfCrawlers = 8;
				CrawlController.WebCrawlerFactory<MyCrawler> factory = () -> new MyCrawler(stats, domains, user.getUser_id(), meilisearch);
				controller.start(factory, numberOfCrawlers);
				FileUtils.deleteDirectory(dir);
			}
			System.out.println(stats.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static File getFolderName(int count) {
		String path = "/tmp/crawler4j";
		File dir = new File(path + "/crawler" + count);
		if (!dir.exists()){
		    return dir;
		} else {
			return getFolderName(count + 1);
		}
	}
}
