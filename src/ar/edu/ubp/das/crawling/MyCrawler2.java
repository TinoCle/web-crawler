package ar.edu.ubp.das.crawling;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
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
import ar.edu.ubp.das.elastic.ElasticSearch;
import ar.edu.ubp.das.utils.Utils;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler2 extends WebCrawler {
	// we dont want js, css, mp4, etc
	private static final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|mid|mp2|mp3|mp4|json|wav|avi|flv|mov|mpeg|ram|m4v|rm|smil|wmv|swf|wma|zip|rar|gz|xml|bmp|gif|png|svg|svgz|ico|jpg|jpeg|jpe|jif|jfif|jfi|webp|tiff|tif|psd|raw|arw|cr2|nrw|k25|bmp|dib))$");

	private int userId;
	private int websiteId;

	HashMap<String, Integer> countPerDomain = new HashMap<String, Integer>();
	String domain;
	private ElasticSearch elastic;
	
	public MyCrawler2(String domain, int userId, int websiteId) {
		this.userId = userId;
		this.websiteId = websiteId;
		this.elastic = new ElasticSearch();
		this.domain = domain;
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
        		&& url.getDomain().equals(this.domain);
	}

	/**
	 * This function is called when a page is fetched and ready to be processed by
	 * your program. Here's where you need to do the parsing ie using jsoup or tika
	 * or another parser
	 */
	@Override
	public void visit(Page page) {
		MetadataBean metadata = new MetadataBean();
		metadata.setUserId(this.userId);
		metadata.setWebsiteId(this.websiteId);
		String url = page.getWebURL().getURL();
		metadata.setURL(url);
		System.out.println(page.getContentType());
		String mime = Utils.getType(page.getContentType().split(";")[0]);
		System.out.println(mime);
		metadata.setType(mime);
		try {
			if (page.getParseData() instanceof HtmlParseData) {
				HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				Document doc = Jsoup.parse(htmlParseData.getHtml());
				String description = "";
				if (doc.select("meta[name=description]").size() > 0) {
					description = doc.select("meta[name=description]").get(0).attr("content");
				}
				doc.prependText(description);
				String text = doc.text();
				metadata.setTitle(doc.title());
				text = Utils.cleanText(text);
				metadata.setTextLength(text.length());
				metadata.setText(Utils.removeStopWords(doc.text()));
				metadata.setDate(Utils.getHtmlDate(metadata.getURL()));
			} else {
				this.parseDoc(metadata, url);
				System.out.println(metadata.getText());
				if (metadata.getType().equals("pdf")) {
					metadata.setTitle(getPDFTitle(page.getContentData()));
				}
			}
			metadata.setApproved(false);
			metadata.setTopWords(Utils.topWords(metadata.getText()));
			this.elastic.indexPage(metadata);
			System.out.println("=============");
		} catch (Exception e) {
		}
	}

	private void parseDoc(MetadataBean data, String url) throws Exception {
		Metadata metadata = new Metadata();
		URL net_url = new URL(url);
		InputStream input = TikaInputStream.get(net_url, metadata);
		StringWriter any = new StringWriter();
		BodyContentHandler handler = new BodyContentHandler(any);
		BoilerpipeContentHandler textHandler = new BoilerpipeContentHandler(handler);
		ParseContext context = new ParseContext();
		Parser parser = new AutoDetectParser();
		parser.parse(input, textHandler, metadata, context);
		if (metadata.get("dcterms:created") != null) {
			data.setDate(metadata.getValues("dcterms:created")[0].substring(0, 10));
		} else {
			data.setDate(Utils.getDate());
		}
		String text = handler.toString();
		text = Utils.cleanText(text);
		data.setTextLength(text.length());
		data.setText(Utils.removeStopWords(text));
	}

	private String getPDFTitle(byte[] data) throws Exception {
		String title = null;
		try {
			PdfDataExtractor extractor = new PdfDataExtractor(data);
			title = extractor.extractTitle();
		} catch (Exception e) {
			throw e;
		}
		return title;
	}
}