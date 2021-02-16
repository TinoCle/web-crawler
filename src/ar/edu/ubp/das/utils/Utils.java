package ar.edu.ubp.das.utils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ar.edu.ubp.das.beans.UserWebsitesBean;
import ar.edu.ubp.das.beans.WebsiteBean;
import edu.uci.ics.crawler4j.url.WebURL;

public class Utils {
	static final DateFormat dfFormatter = new SimpleDateFormat("yyyy-MM-dd");

	public static String getDomainName(String url) {
		WebURL weburl = new WebURL();
		weburl.setURL(url);
		return weburl.getDomain();
	}

	public static List<WebsiteBean> parseCsv(UserWebsitesBean userWebsites) {
		List<WebsiteBean> websites = new ArrayList<WebsiteBean>();
		List<String> domains = Arrays.asList(userWebsites.getWebsitesCSV().split(","));
		List<Integer> websitesId = Arrays.stream(userWebsites.getWebsitesIdCSV().split(",")).map(Integer::parseInt)
				.collect(Collectors.toList());
		for (int i = 0; i < domains.size(); i++) {
			WebsiteBean website = new WebsiteBean();
			website.setUserId(userWebsites.getUserId());
			website.setUrl(domains.get(i));
			website.setIsUp(true);
			website.setWebsiteId(websitesId.get(i));
			website.setIsUp(true);
			websites.add(website);
		}
		return websites;
	}

	public static boolean pingURL(String url, int timeout) {
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

	public static File getFolderName(int count) {
		String path = "/tmp/crawler4j";
		File dir = new File(path + "/crawler" + count);
		if (!dir.exists()) {
			return dir;
		} else {
			return getFolderName(count + 1);
		}
	}

	public static String removeStopWords(String text) {
		try {
			List<String> stopwords = Files.readAllLines(Paths.get("stopwords.txt"));
			ArrayList<String> allWords = Stream.of(text.toLowerCase().split(" "))
					.collect(Collectors.toCollection(ArrayList<String>::new));
			allWords.removeAll(stopwords);
			return allWords.stream().collect(Collectors.joining(" "));
		} catch (IOException e) {
			return null;
		}
	}

	public static List<String> processText(String text) {
		try {
			List<String> stopwords = Files.readAllLines(Paths.get("stopwords.txt"));
			ArrayList<String> allWords = Stream.of(text.replaceAll("[^\\p{L} ]", "").toLowerCase().split("\\s+"))
					.collect(Collectors.toCollection(ArrayList<String>::new));
			allWords.removeAll(stopwords);
			Map<String, Long> map = allWords.stream().collect(Collectors.groupingBy(w -> w, Collectors.counting()));
			List<Map.Entry<String, Long>> result = map.entrySet().stream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(5)
					.collect(Collectors.toList());
			List<String> words = new ArrayList<String>();
			for (Map.Entry<String, Long> entry : result) {
				words.add(entry.getKey());
			}
			return words;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getDate() {
		return dfFormatter.format(new Date());
	}
	
	public static String getWebDate(String href) throws Exception {
		try {
			URL url = new URL(href);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setRequestMethod("HEAD");
			long lastModified = httpUrlConnection.getLastModified();
			return dfFormatter.format(new Date(lastModified));
		} catch (Exception e) {
			return getDate();
		}
	}

}
