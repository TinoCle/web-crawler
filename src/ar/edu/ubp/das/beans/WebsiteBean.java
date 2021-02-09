package ar.edu.ubp.das.beans;

public class WebsiteBean {
	private int user_id; // usado cuando se obtienen las páginas de un user
	private String websites; // usado cuando se obtienen las páginas de un user
	private int service_id;
	private String url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getService_id() {
		return service_id;
	}

	public void setService_id(int service_id) {
		this.service_id = service_id;
	}

	public int getUser_id() {
		return user_id;
	}
	
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getWebsites() {
		return websites;
	}

	public void setWebsites(String websites) {
		this.websites = websites;
	}
}
