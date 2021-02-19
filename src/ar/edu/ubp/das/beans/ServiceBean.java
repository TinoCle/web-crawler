package ar.edu.ubp.das.beans;

public class ServiceBean extends StatusBean {
	private int userId;
	private int serviceId;
	private String url;
	private String protocol;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
