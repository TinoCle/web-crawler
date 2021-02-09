package ar.edu.ubp.das.websites;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ar.edu.ubp.das.beans.ServiceBean;
import ar.edu.ubp.das.beans.WebsiteBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;

public class Websites {
	private static final String PROTOCOL_REST = "REST";
	private static final HttpClient MyHttpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

	public List<WebsiteBean> getWebsitesPerUser() {
		try {
			this.updateServices();
			Dao<WebsiteBean, String> dao = DaoFactory.getDao("Websites", "ar.edu.ubp.das");
			List<WebsiteBean> websites = dao.select();
			if (websites.size() > 0) {
				System.out.println("Páginas encontradas!");
			}
//			for (WebsiteBean web : websites) {
//				System.out.println("Páginas del user " + web.getUser_id() + ": " + web.getWebsites());	
//			}		
			return websites;
		} catch (SQLException e) {
			System.out.println("Error al obtener el listado de páginas");
			System.out.println(e);
		}
		return null;
	}
	
	private void updateServices() {
		try {
			Dao<ServiceBean, String> serviceDao = DaoFactory.getDao("Services", "ar.edu.ubp.das");
			List<ServiceBean> services = serviceDao.select();
			if (services != null && services.size() > 0) {
				System.out.println("Actualizando servicios...");				
			} else {
				System.out.println("No se encontraron servicios para actualizar.");
			}
			for (ServiceBean service : services) {
				if (service.getProtocol().equals(PROTOCOL_REST)) {
					HttpResponse<String> response = null;
					try {
						response = this.restCall(service.getURLPing());
						if (response.statusCode() >= 400) {
							System.out.println("Servicio #" + service.getService_id() + " caído.");
							serviceDao.update(service); // marca como caído
						} else {
							System.out.println("Servicio #" + service.getService_id() + " funcionando, obteniendo páginas...");
							response = this.restCall(service.getURLResource());
							if (response.statusCode() >= 400) {
								System.out.println("Servicio #" + service.getService_id() + " no respondió con el listado de páginas.");
								serviceDao.update(service); // marca como caído
							} else {
								System.out.println("Limpiando páginas del servicio #" + service.getService_id());
								serviceDao.delete(service); // borro las páginas de ese servicio
								JSONParser parser = new JSONParser();
								JSONObject list = (JSONObject) ((JSONObject) parser.parse(response.body())).get("list");
								Set<String> keys = list.keySet();
								for (String key : keys) {
									this.insertWebsite((String) list.get(key), service);
								}
							}
						}
					} catch (IOException e) {
						System.out.println("Servicio #" + service.getService_id() + " caído.");
						serviceDao.update(service);
					}
				} else {
					try {
						JaxWsDynamicClientFactory jdcf = JaxWsDynamicClientFactory.newInstance();
						Client client = jdcf.createClient(service.getURLPing());
						client.invoke("ping");
						System.out.println("Servicio #" + service.getService_id() + " funcionando, obteniendo páginas...");
						Object res[] = client.invoke("getList");
						client.close();
						ArrayList<String> urls = (ArrayList) res[0];
						serviceDao.delete(service); // borro las páginas de ese servicio
						for (String url : urls) {
							this.insertWebsite(url, service);
						}
					} catch (Exception e) {
						System.out.println("Servicio #" + service.getService_id() + " caído.");
						System.out.println(e.getMessage());
						serviceDao.update(service); // marca como caído
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error al actualizar los servicios");
			System.out.println(e);
		}
	}
	
	private void insertWebsite(String url, ServiceBean service) throws SQLException {
		Dao<WebsiteBean, String> websitesDao = DaoFactory.getDao("Websites", "ar.edu.ubp.das");
		System.out.println("Insertando " + url);
		WebsiteBean website = new WebsiteBean();
		website.setUser_id(service.getUser_id());
		website.setUrl(url);
		website.setService_id(service.getService_id());
		websitesDao.insert(website);
	}
	
	private HttpResponse<String> restCall(String url) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(URI.create(url))
				.build();
		return MyHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}
}
