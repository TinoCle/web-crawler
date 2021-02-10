package ar.edu.ubp.das.beans;

public class MetadataBean {
	private String id;
	private Integer userId;
	private String URL;
	private String type;
	private String extension;
	private String title;
	private String text;
	private Integer textLength;
	private Boolean valid = false;
	private Integer popularity = 0;
	
		
	public Integer getPopularity() {
		return popularity;
	}

	public void setPopularity(Integer popularity) {
		this.popularity = popularity;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public String getId() {
		return id;
	}

	public Integer getUserId() {
		return userId;
	}

	public Integer getTextLength() {
		return textLength;
	}

	public void setId(String id) {
		this.id = id;
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
	
	public String getExtension() {
		return extension;
	}
	
	public void setExtension(String extension) {
		this.extension = extension;
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
