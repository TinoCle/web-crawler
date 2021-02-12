package ar.edu.ubp.das.beans;

public class WebsiteBean extends StatusBean {
	private Integer websiteId;
	private Integer userId;
	private String url;
	private Integer serviceId;
	
	public Boolean getIsUp() {
		return isUp;
	}
	
	public void setIsUp(Boolean isUp) {
		this.isUp = isUp;
	}
	
	public Integer getUserId() {
		return userId;
	}
	
	public Integer getWebsiteId() {
		return websiteId;
	}
	
	public Integer getServiceId() {
		return serviceId;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public void setWebsiteId(Integer websiteId) {
		this.websiteId = websiteId;
	}
	
	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		String output = "\n URL:" + url;
		return output;
	}
}
