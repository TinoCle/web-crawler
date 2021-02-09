package ar.edu.ubp.das.crawling;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.BoilerpipeContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.docear.pdf.PdfDataExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import ar.edu.ubp.das.beans.MetadataBean;
import ar.edu.ubp.das.meilisearch.MeiliSearch;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {
	MeiliSearch meilisearch;
	
	// we dont want js, css, mp4, etc
	private static final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|mid|mp2|mp3|mp4|json|wav"
			+ "|avi|flv|mov|mpeg|ram|m4v|rm|smil|wmv|swf|wma|zip|rar|gz|xml))$");
	private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*(\\.(bmp|gif|jpe?g|png))$");
	private static final int LIMIT_PER_DOMAIN = 10;
	private List<String> crawlDomains;
	private Statistics stats;
	private int user_id;
	
	HashMap<String, Integer> countPerDomain = new HashMap<String, Integer>();

	public MyCrawler(Statistics stats, List<String> crawlDomains, int user_id, MeiliSearch meilisearch) {
		this.stats = stats;
		this.crawlDomains = crawlDomains;
		this.user_id = user_id;
		this.meilisearch = meilisearch;
	}

	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		stats.increaseFetchAttempts();
		if (statusCode == 200) {
			stats.increaseFetchSucceeded();
		} else if (statusCode >= 300 && statusCode < 400) {
			stats.increaseFetchAborted();
		} else {
			stats.increaseFetchFailed();
		}
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		stats.increaseVisitedUrls();
		// If it's an image, take it
		if (IMAGE_EXTENSIONS.matcher(href).matches())
			return false;

		// only domain, not external sites
		if (!FILTERS.matcher(href).matches()) {
			for (String domain : crawlDomains) {
				if (href.startsWith(domain))			
					return true;					
			}
		}
		stats.increaseSkippedLinks();
		return false;
	}

	/**
	 * This function is called when a page is fetched and ready to be processed by
	 * your program. Here's where you need to do the parsing ie using jsoup or tika
	 * or another parser
	 */
	@Override
	public void visit(Page page) {
		if (!this.checkDomainLimit(page.getWebURL().getDomain())) {
//			System.out.println("Límite excedido, omitiendo dominio");
			return;
		}
		MetadataBean metadata = new MetadataBean();
		metadata.setUser_id(this.user_id);
		String url = page.getWebURL().getURL();
		System.out.println("Crawling " + url + ".");
		metadata.setURL(url);
		metadata.setExtension(url.substring(url.lastIndexOf('.')));
		metadata.setType(page.getContentType()); // can be: text/html, image/png, image/jpg, etc.
		
		// Conclusion:
		// getting the html from crawler and then parsing it whit Jsoup: OK
		// getting the text from already parsed html from crawler4j brings the metadata
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			Document doc = Jsoup.parse(htmlParseData.getHtml());
			String description = doc.select("meta[name=description]").get(0).attr("content");
			System.out.println("Description: " + description);
			// check https://jsoup.org/ for more options
			
			metadata.setText_length(doc.text().length());
			metadata.setTitle(doc.title());
		}
		// not a html page, can be a pdf, docx, odf, png, jpg, etc.
		else {
			metadata.setText(this.parseDoc(url));
			// if PDF, other files not implemented yet
			if (page.getContentType().equals("application/pdf")) {
				metadata.setTitle(getPDFTitle(page.getContentData()));
			}
		}
		this.meilisearch.addDocument(metadata);
		System.out.println("=============");
	}

	private String parseDoc(String url) {
		try {
			Metadata metadata = new Metadata();
			URL net_url = new URL(url);
			InputStream input = TikaInputStream.get(net_url, metadata);
			// InputStream input = TikaInputStream.get(data, metadata);
			StringWriter any = new StringWriter();
			BodyContentHandler handler = new BodyContentHandler(any);
			BoilerpipeContentHandler textHandler = new BoilerpipeContentHandler(handler);
			ParseContext context = new ParseContext();
			Parser parser = new AutoDetectParser();
			parser.parse(input, textHandler, metadata, context);
			String[] names = metadata.names();
			Arrays.sort(names);
//			for (String name : names) {
//				System.out.println(name + ": " + metadata.get(name));
//			}
			write("doc.txt", handler.toString());
			return handler.toString();
		} catch (Exception e) {
			stats.increaseParsingFailed();
			e.printStackTrace();
			return null;
		}
	}

	private String getPDFTitle(byte[] data) {
		String title = null;
		try {
			PdfDataExtractor extractor = new PdfDataExtractor(data);
			title = extractor.extractTitle();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return title;
	}
	
	/**
	 * @param filename
	 * @param text     String that is going to be written in the txt
	 */
	private void write(String filename, String text) {
		try {
			FileWriter myWriter = new FileWriter(filename);
			myWriter.write(text);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	private boolean checkDomainLimit(String domain) {
		this.countPerDomain.put(
				domain,
				this.countPerDomain.get(domain) != null ?
				this.countPerDomain.get(domain) + 1 : 1
		);
//		System.out.println(domain + ": " + this.countPerDomain.get(domain));
		return this.countPerDomain.get(domain) <= LIMIT_PER_DOMAIN;
	}
}