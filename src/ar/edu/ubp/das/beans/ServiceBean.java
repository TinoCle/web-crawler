package ar.edu.ubp.das.beans;

public class ServiceBean {
	private int user_id;
	private int service_id;
	private String URLResource;
	private String URLPing;
	private String protocol;

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getService_id() {
		return service_id;
	}

	public void setService_id(int service_id) {
		this.service_id = service_id;
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
