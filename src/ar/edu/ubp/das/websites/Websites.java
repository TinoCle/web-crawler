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

import javax.ws.rs.NotFoundException;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.elasticsearch.ElasticsearchException;

import com.google.gson.Gson;

import ar.edu.ubp.das.beans.ServiceBean;
import ar.edu.ubp.das.beans.UserWebsitesBean;
import ar.edu.ubp.das.beans.WebsiteBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;
import ar.edu.ubp.das.elastic.ElasticSearch;
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
				this.logger.log(MyLogger.INFO, "Se encontraron paginas para indexar");
			}
			return websites;
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "Error al obtener el listado de paginas: " + e.getMessage());
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
				this.logger.log(MyLogger.INFO, "Actualizando servicio #" + service.getServiceId()
						+ " mediante protocolo " + service.getProtocol());
				if (service.getProtocol().equals(PROTOCOL_REST)) {
					try {
						System.out.println("REST");
						String urls[] = this.makeRestCall(service);
						service.setIsUp(true);
						serviceDao.update(service);
						for (String url : urls) {
							try {
								this.insertWebsite(url, service);
							} catch (Exception e) {
								this.logger.log(MyLogger.ERROR, "Error al insertar " + url + " " + e.getMessage());
							}
						}
						serviceDao.update(service.getServiceId()); // setear servicio reindex = 0
					} catch (Exception e) {
						this.logger.log(MyLogger.ERROR, "Error al insertar páginas del servicio #"
								+ service.getServiceId() + ": " + e.getMessage());
						service.setIsUp(false);
						serviceDao.update(service);
						this.logger.log(MyLogger.WARNING, "Servicio #" + service.getServiceId() + " caído");
					}
				} else {
					try {
						System.out.println("CREATING CLIENT");
						JaxWsDynamicClientFactory jdcf = JaxWsDynamicClientFactory.newInstance();
						Client client = jdcf.createClient(service.getUrl());
						System.out.println("CLIENT CREATED");
						client.invoke("ping");
						this.logger.log(MyLogger.INFO,
								"Servicio #" + service.getServiceId() + " funcionando, obteniendo paginas...");
						Object res[] = client.invoke("getList");
						client.close();
						service.setIsUp(true);
						serviceDao.update(service);
						ArrayList<String> urls = (ArrayList<String>) res[0];
						for (String url : urls) {
							try {
								this.insertWebsite(url, service);
							} catch (Exception e) {
								this.logger.log(MyLogger.ERROR, "Error al insertar " + url + " " + e.getMessage());
							}
						}
						serviceDao.update(service.getServiceId()); // setear servicio reindex = 0
					} catch (Exception e) {
						this.logger.log(MyLogger.WARNING, "Servicio #" + service.getServiceId() + " caido");
						this.logger.log(MyLogger.ERROR, e.getMessage());
						service.setIsUp(false);
						serviceDao.update(service); // marcar como caido
					}
				}
			}
		} catch (Exception e) {
			this.logger.log(MyLogger.ERROR, "Error al actualizar los servicios: " + e.getMessage());
		}
	}

	private void insertWebsite(String url, ServiceBean service) throws ElasticsearchException, Exception {
		Dao<WebsiteBean, WebsiteBean> websiteDao = DaoFactory.getDao("Website", "ar.edu.ubp.das");
		WebsiteBean website = new WebsiteBean();
		website.setUserId(service.getUserId());
		website.setUrl(url);
		website.setServiceId(service.getServiceId());
		WebsiteBean websiteFound = websiteDao.find(website);
		if (websiteFound != null) {
			ElasticSearch elastic = new ElasticSearch();
			elastic.deleteWebsiteId(websiteFound.getWebsiteId());
		}
		Dao<WebsiteBean, WebsiteBean> websitesDao = DaoFactory.getDao("Website", "ar.edu.ubp.das");
		websitesDao.insert(website);
		this.logger.log(MyLogger.INFO, "Insertando " + url + " en la base de datos");
	}
	
	private String[] makeRestCall(ServiceBean service) throws IOException, SQLException, Exception {
		Dao<ServiceBean, ServiceBean> serviceDao = DaoFactory.getDao("Services", "ar.edu.ubp.das");
		HttpResponse<String> response = this.restCall(service.getUrl() + "ping");
		if (response.statusCode() >= 400) {
			this.logger.log(MyLogger.WARNING, "Servicio #" + service.getServiceId() + " caido");
			throw new NotFoundException("Servicio Caido");
		}
		this.logger.log(MyLogger.INFO, "Servicio # " + service.getServiceId() + " funcionando, obteniendo paginas...");
		response = this.restCall(service.getUrl() + "list");
		if (response.statusCode() >= 400) {
			this.logger.log(MyLogger.WARNING, "Servicio #" + service.getServiceId()
					+ " no respondio con el listado de paginas. Marcado como caido");
			throw new NotFoundException("Servicio Caido");
		}
		service.setIsUp(true);
		serviceDao.update(service);
		this.logger.log(MyLogger.INFO, "Limpiando paginas del servicio #" + service.getServiceId());
		return new Gson().fromJson(response.body(), String[].class);
	}


	private HttpResponse<String> restCall(String url) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
		return MyHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
	}
}
