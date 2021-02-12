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
import ar.edu.ubp.das.beans.UserWebsitesBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;
import ar.edu.ubp.das.logging.MyLogger;

public class Websites {
	private static final String PROTOCOL_REST = "REST";
	private static final HttpClient MyHttpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
			.connectTimeout(Duration.ofSeconds(5)).build();
	private MyLogger logger;
	
	public Websites() {
		this.logger = new MyLogger(this.getClass().getSimpleName());
	}

	public List<UserWebsitesBean> getWebsitesPerUser() {
		try {
			this.updateServices();
			Dao<UserWebsitesBean, String> dao = DaoFactory.getDao("Websites", "ar.edu.ubp.das");
			List<UserWebsitesBean> websites = dao.select();
			if (websites.size() > 0) {
				this.logger.log(MyLogger.INFO, "Se encontraron páginas para indexar");
			}

			return websites;
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "Error al obtener el listado de páginas: " + e.getMessage());
		}
		return null;
	}

	private void updateServices() {
		try {
			Dao<ServiceBean, String> serviceDao = DaoFactory.getDao("Services", "ar.edu.ubp.das");
			List<ServiceBean> services = serviceDao.select();
			if (services == null || services.size() <= 0) {
				this.logger.log(MyLogger.INFO, "No se encontraron servicios para actualizar");
			}
			for (ServiceBean service : services) {
				if (service.getProtocol().equals(PROTOCOL_REST)) {
					HttpResponse<String> response = null;
					try {
						response = this.restCall(service.getURLPing());
						if (response.statusCode() >= 400) {
							this.logger.log(MyLogger.WARNING, "Servicio #" + service.getServiceId() + " caído");
							serviceDao.update(service); // marca como caído
						} else {
							this.logger.log(MyLogger.INFO, "Servicio #" + service.getServiceId() + " funcionando, obteniendo páginas...");
							response = this.restCall(service.getURLResource());
							if (response.statusCode() >= 400) {
								this.logger.log(
									MyLogger.WARNING, "Servicio #" + service.getServiceId() +
									" no respondió con el listado de páginas. Marcado como caído"
								);
								serviceDao.update(service); // marca como caído
							} else {
								this.logger.log(MyLogger.INFO, "Limpiando páginas del servicio #" + service.getServiceId());
								serviceDao.delete(service); // borro las páginas de ese servicio
								JSONParser parser = new JSONParser();
								JSONObject list = (JSONObject) ((JSONObject) parser.parse(response.body())).get("list");
								Set<String> keys = list.keySet();
								for (String key : keys) {
									this.insertWebsite((String) list.get(key), service);
								}
							}
						}
						serviceDao.update(service.getServiceId()); // setear servicio reindex = 0
					} catch (IOException e) {
						this.logger.log(MyLogger.WARNING, "Servicio #" + service.getServiceId() + " caído");
						serviceDao.update(service);
					}
				} else {
					try {
						JaxWsDynamicClientFactory jdcf = JaxWsDynamicClientFactory.newInstance();
						Client client = jdcf.createClient(service.getURLPing());
						client.invoke("ping");
						this.logger.log(MyLogger.INFO, "Servicio #" + service.getServiceId() + " funcionando, obteniendo páginas...");
						Object res[] = client.invoke("getList");
						client.close();
						ArrayList<String> urls = (ArrayList<String>) res[0];
						serviceDao.delete(service); // borro las paginas de ese servicio
						for (String url : urls) {
							this.insertWebsite(url, service);
						}
						serviceDao.update(service.getServiceId()); // setear servicio reindex = 0
					} catch (Exception e) {
						this.logger.log(MyLogger.WARNING, "Servicio #" + service.getServiceId() + " caído");
						this.logger.log(MyLogger.ERROR, e.getMessage());
						serviceDao.update(service); // marca como caído
					}
				}
			}
		} catch (Exception e) {
			this.logger.log(MyLogger.ERROR, "Error al actualizar los servicios: " + e.getMessage());
		}
	}

	private void insertWebsite(String url, ServiceBean service) throws SQLException {
		Dao<UserWebsitesBean, String> websitesDao = DaoFactory.getDao("Websites", "ar.edu.ubp.das");
		this.logger.log(MyLogger.INFO, "Insertando " + url + " en la base de datos");
		UserWebsitesBean website = new UserWebsitesBean();
		website.setUserId(service.getUserId());
		website.setUrl(url);
		website.setServiceId(service.getServiceId());
		websitesDao.insert(website);
	}

	private HttpResponse<String> restCall(String url) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
		return MyHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}
}
