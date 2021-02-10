package ar.edu.ubp.das.crawling;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import ar.edu.ubp.das.beans.WebsiteBean;
import ar.edu.ubp.das.beans.UserWebsitesBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;
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
		System.setProperty("ConnectionString", "jdbc:sqlserver://localhost;databaseName=users;user=sa;password=secret");
	}

	public static void main(String[] args) throws Exception {
        Websites websitesManager = new Websites();
        List<UserWebsitesBean> websites = websitesManager.getWebsitesPerUser();
        if (websites != null && websites.size() > 0) {
        	doCrawling(websites);
        } else {
        	// TODO: Log
        	System.out.println("No se encontraron páginas para reindexar, saltando crawling...");
        }
	}

	public static void doCrawling(List<UserWebsitesBean> usersWebsites) {
		System.out.println("Iniciando Crawling...");
		CrawlConfig config = initConfig();
        Statistics stats = new Statistics();
        MeiliSearch meilisearch = new MeiliSearch();
		try {
			Dao<WebsiteBean, WebsiteBean> dao = DaoFactory.getDao("Website", "ar.edu.ubp.das");
			// Iteramos un usuario a la vez, cada uno con su set de páginas, sino se pisa la frontera
			for (UserWebsitesBean userWebsites : usersWebsites) {
				File dir = getFolderName(0);
				config.setCrawlStorageFolder(dir.toString());
				PageFetcher pageFetcher = new PageFetcher(config);
				RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
				RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
				CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
				List<WebsiteBean> websitesUser = parseCsv(userWebsites);
				List<String> domains = new ArrayList<String>();
				for (WebsiteBean website : websitesUser) {
					System.out.println("URL: " + website.getUrl());
					if (pingURL(website.getUrl(), 5000)) {
						domains.add(website.getUrl());
						controller.addSeed(website.getUrl());
					}
					else {
						// TODO: Log
						System.out.println(website.getUrl() + " Caída");
						System.out.println("ID WEBSITE:" + website.getWebsiteId());
						dao.update(website.getWebsiteId());
						website.setIsUp(false);
					}
				}
				/*int numberOfCrawlers = 8;
				CrawlController.WebCrawlerFactory<MyCrawler> factory = () -> new MyCrawler(stats, domains, userWebsites.getUserId(), meilisearch);
				controller.start(factory, numberOfCrawlers);
				FileUtils.deleteDirectory(dir);*/
				
				for (WebsiteBean website : websitesUser) {
					if (website.getIsUp()) {
						dao.update(website);						
					}
				}
			}
			System.out.println(stats.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static List<WebsiteBean> parseCsv(UserWebsitesBean userWebsites) {
		List<WebsiteBean> websites = new ArrayList<WebsiteBean>();
		List<String> domains = Arrays.asList(userWebsites.getWebsitesCSV().split(","));
		List<Integer> websitesId = Arrays.stream(userWebsites.getWebsitesIdCSV()
				.split(",")).map(Integer::parseInt).collect(Collectors.toList());
		for (int i = 0; i < domains.size(); i++) {
			WebsiteBean website = new WebsiteBean();
			website.setUserId(userWebsites.getUserId());
			website.setUrl(domains.get(i));
			website.setIsUp(true);
			website.setWebsiteId(websitesId.get(i));
			websites.add(website);
		}
		return websites;
	}
	
	private static boolean pingURL(String url, int timeout) {
	    url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.
	    try {
	        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setConnectTimeout(timeout);
	        connection.setReadTimeout(timeout);
	        connection.setRequestMethod("HEAD");
	        int responseCode = connection.getResponseCode();
	        return (200 <= responseCode && responseCode <= 399);
	    } catch (IOException exception) {
	        return false;
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
