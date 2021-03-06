package ar.edu.ubp.das.elastic;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ar.edu.ubp.das.beans.MetadataBean;
import ar.edu.ubp.das.logging.MyLogger;

public class ElasticSearch {
	RestHighLevelClient client;
	Gson gson;
	static final String INDEX = "metadata";
	MyLogger logger;

	public ElasticSearch() {
		this.client = new RestHighLevelClient(
				RestClient.builder(new HttpHost("localhost", 9200, "http"), new HttpHost("localhost", 9201, "http")));
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.logger = new MyLogger(this.getClass().getSimpleName());
	}

	public void indexPage(MetadataBean metadata) {
		try {
			IndexRequest request = new IndexRequest(INDEX);
			request.id(UUID.randomUUID().toString());
			request.source(this.gson.toJson(metadata), XContentType.JSON);
			client.index(request, RequestOptions.DEFAULT);
			this.logger.log(MyLogger.INFO, "Metadato insertado");
		} catch (IOException e) {
			this.logger.log(MyLogger.ERROR, "Error al insertar metadato en elasticsearch: " + e.getMessage());
		}
	}
	
	public void deleteWebsiteId(Integer id) throws ElasticsearchException, Exception {
		DeleteByQueryRequest request = new DeleteByQueryRequest(INDEX);
		request.setQuery(new TermQueryBuilder("websiteId", id));
		request.setRefresh(true);
		ActionListener<BulkByScrollResponse> listener = new ActionListener<BulkByScrollResponse>() {
		    @Override
		    public void onResponse(BulkByScrollResponse bulkResponse) {
		    	logger.log(MyLogger.ERROR, "Base de Datos depurada");
		    }
		    @Override
		    public void onFailure(Exception e) {
		    	logger.log(MyLogger.ERROR, "Error al depurar base de datos: " + e.getMessage());
		    }
		};
		client.deleteByQueryAsync(request, RequestOptions.DEFAULT, listener);
	}
}
