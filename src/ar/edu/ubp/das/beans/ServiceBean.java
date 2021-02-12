package ar.edu.ubp.das.beans;

public class ServiceBean extends StatusBean {
	private int userId;
	private int serviceId;
	private String URLResource;
	private String URLPing;
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

	public String getURLResource() {
		return URLResource;
	}

	public void setURLResource(String uRLResource) {
		URLResource = uRLResource;
	}

	public String getURLPing() {
		return URLPing;
	}

	public void setURLPing(String uRLPing) {
		URLPing = uRLPing;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
