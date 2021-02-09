package ar.edu.ubp.das.crawling;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Statistics {
	private int visitedUrls, fetchAttempts, fetchSucceeded, fetchFailed, fetchAborted, skippedLinks, parsingFailed;
	private String startDate, finishDate;

	public Statistics() {
		this.setStartDate();
		this.visitedUrls = 0;
		this.fetchAttempts = 0;
		this.fetchSucceeded = 0;
		this.fetchFailed = 0;
		this.fetchAborted = 0;
		this.skippedLinks = 0;
		this.parsingFailed = 0;
	}

	public int getVisitedUrls() {
		return visitedUrls;
	}

	public int getFetchAttempts() {
		return fetchAttempts;
	}

	public int getFetchSucceeded() {
		return fetchSucceeded;
	}

	public int getFetchFailed() {
		return fetchFailed;
	}

	public int getFetchAborted() {
		return fetchAborted;
	}

	public int getSkippedLinks() {
		return skippedLinks;
	}
	
	public int getParsingFailed() {
		return parsingFailed;
	}

	public void increaseVisitedUrls() {
		this.visitedUrls++;
	}

	public void increaseFetchAttempts() {
		this.fetchAttempts++;
	}

	public void increaseFetchSucceeded() {
		this.fetchSucceeded++;
	}

	public void increaseFetchFailed() {
		this.fetchFailed++;
	}

	public void increaseFetchAborted() {
		this.fetchAborted++;
	}

	public void increaseSkippedLinks() {
		this.skippedLinks++;
	}
	
	public void increaseParsingFailed() {
		this.parsingFailed++;
	}
	
	public String getStartDate() {
		return this.startDate.toString();
	}
	
	public String getFinishDate() {
		return this.finishDate.toString();
	}
	
	public void setStartDate() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.startDate = formatter.format(date).toString();
	}
	
	public void setFinishDate() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.finishDate = formatter.format(date).toString();
	}
	
	@Override
	public String toString() {
		String output = "Crawling Stats:";
		output += "\n\t* Visited Urls: "		+ this.visitedUrls;
		output += "\n\t* Skipped Urls: "   		+ this.skippedLinks;
		output += "\n\t* Fetch Attemps: "  		+ this.fetchAttempts;
		output += "\n\t* Fetches Succeeded: " 	+ this.fetchSucceeded;
		output += "\n\t* Fetches Aborted: " 	+ this.fetchAborted;
		output += "\n\t* Fetches Failed: " 		+ this.fetchFailed;
		output += "\n\t* Parsings Failed: " 	+ this.parsingFailed;
		output += "\n\t* Start Date: " 			+ this.startDate;
		output += "\n\t* Finish Date: " 		+ this.finishDate;
		return output;
	}
}
