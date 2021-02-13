package ar.edu.ubp.das.elastic;

import java.io.IOException;
import java.util.UUID;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
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

public class ElasticSearch {
	RestHighLevelClient client;
	Gson gson;
	static final String INDEX = "metadata";

	public ElasticSearch() {
		this.client = new RestHighLevelClient(
				RestClient.builder(new HttpHost("localhost", 9200, "http"), new HttpHost("localhost", 9201, "http")));
		this.gson = new GsonBuilder().setPrettyPrinting().create();
	}

	public void indexPage(MetadataBean metadata) {
		try {
			IndexRequest request = new IndexRequest(INDEX);
			request.id(UUID.randomUUID().toString());
			request.source(this.gson.toJson(metadata), XContentType.JSON);
			IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
			System.out.println("elastic: " + indexResponse.status().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteWebsiteId(Integer id) throws ElasticsearchException, Exception {
		DeleteByQueryRequest request = new DeleteByQueryRequest(INDEX);
		request.setQuery(new TermQueryBuilder("websiteId", id));
		request.setRefresh(true);
		ActionListener<BulkByScrollResponse> listener = new ActionListener<BulkByScrollResponse>() {
		    @Override
		    public void onResponse(BulkByScrollResponse bulkResponse) {
		        // TODO: Log
		    	System.out.println("Metadatos Actualizados");
		    }
		    @Override
		    public void onFailure(Exception e) {
		        // TODO: Log
		    }
		};
		client.deleteByQueryAsync(request, RequestOptions.DEFAULT, listener);
	}
}
