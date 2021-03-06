package ar.edu.ubp.das.beans;

import java.util.List;

public class MetadataBean {
	private Integer userId;
	private String URL;
	private String type;
	private String title;
	private String text;
	private Integer textLength;
	private Boolean approved = false;
	private Integer popularity = 0;
	private Integer websiteId;
	private List<String> topWords;
	private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<String> getTopWords() {
		return topWords;
	}

	public void setTopWords(List<String> topWords) {
		this.topWords = topWords;
	}

	public Integer getWebsiteId() {
		return websiteId;
	}

	public void setWebsiteId(Integer websiteId) {
		this.websiteId = websiteId;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public Integer getPopularity() {
		return popularity;
	}

	public void setPopularity(Integer popularity) {
		this.popularity = popularity;
	}

	public Integer getUserId() {
		return userId;
	}

	public Integer getTextLength() {
		return textLength;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setTextLength(Integer textLength) {
		this.textLength = textLength;
	}

	public String getURL() {
		return URL;
	}
	
	public void setURL(String uRL) {
		URL = uRL;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
