package ar.edu.ubp.das.beans;

public class UserWebsitesBean {
	private int userId; // usado cuando se obtienen las páginas de un user
	private String websitesCSV; // usado cuando se obtienen las páginas de un user
	private String websitesIdCSV; // usado cuando se obtienen las páginas de un user
	private String url;
	private int serviceId;
	
	public String getWebsitesIdCSV() {
		return websitesIdCSV;
	}

	public void setWebsitesIdCSV(String websitesIdCSV) {
		this.websitesIdCSV = websitesIdCSV;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getWebsitesCSV() {
		return websitesCSV;
	}

	public void setWebsitesCSV(String websitesCSV) {
		this.websitesCSV = websitesCSV;
	}

	public int getUserId() {
		return userId;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public String toString() {
		String output = "UserId: " + userId;
		output += "| Websites: " + websitesCSV;
		output += "| ServiceId: " + serviceId;
		output += "| Url: " + url;
		return output;
	}
}
