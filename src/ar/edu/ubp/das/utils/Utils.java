package ar.edu.ubp.das.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ar.edu.ubp.das.beans.UserWebsitesBean;
import ar.edu.ubp.das.beans.WebsiteBean;
import edu.uci.ics.crawler4j.url.WebURL;

public class Utils {
	static final DateFormat dfFormatter = new SimpleDateFormat("yyyy-MM-dd");
	static final Map<String, String> mimeTypes = new HashMap<String, String>()
	{{
	     put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "doc"); // docx
	     put("application/vnd.openxmlformats-officedocument.wordprocessingml", "doc");
	     put("application/vnd.oasis.opendocument.text", "doc"); // openoffice word
	     put("application/msword", "doc");	// doc
	     put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "ppt"); // pptx
	     put("application/vnd.openxmlformats-officedocument.presentationml", "ppt"); // pptx
	     put("application/vnd.ms-powerpoint", "ppt"); // ppt
	     put("application/vnd.oasis.opendocument.presentation", "ppt"); // openoffice ppt
	     put("application/pdf", "pdf"); // pdf
	     put("text/html", "html"); // html
	}};
	
	public static String getDomainName(String url) {
		WebURL weburl = new WebURL();
		weburl.setURL(url);
		return weburl.getDomain();
	}
	
	public static String getType(String mime) {
		return mimeTypes.get(mime);
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
		url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates
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
	
	public static String cleanText(String text) {
		return text.replaceAll("[^\\p{L}0-9 ]", " ").replaceAll("\\s{2,}", " ");
	}

	public static List<String> topWords(String text) {
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
	
	public static String getHtmlDate(String href) throws IOException {
		Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("htmldate.exe -u " + href);
        BufferedReader lineReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        errorReader.lines().forEach(System.out::println);
        String date = lineReader.readLine(); 
        if (date == null || date.equals("")) {
        	return dfFormatter.format(new Date());
        } else {
        	return date;
        }
	}
}
