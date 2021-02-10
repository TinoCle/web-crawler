package ar.edu.ubp.das.elastic;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ar.edu.ubp.das.beans.MetadataBean;

public class ElasticSearch {
	RestHighLevelClient client;
	Gson gson;
	
	public ElasticSearch() {
		this.client = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost("localhost", 9200, "http"),
		                new HttpHost("localhost", 9201, "http")));
		this.gson = new GsonBuilder().setPrettyPrinting().create();
	}
	
	public void indexPage(MetadataBean metadata) {
		IndexRequest request = new IndexRequest("metadata"); 
		request.id(UUID.randomUUID().toString());
		request.source(this.gson.toJson(metadata), XContentType.JSON);
		try {
			IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
			System.out.println("elastic: " + indexResponse.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
