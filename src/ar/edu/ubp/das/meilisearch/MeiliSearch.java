package ar.edu.ubp.das.meilisearch;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;

import ar.edu.ubp.das.beans.MetadataBean;

public class MeiliSearch {
	Client client;
	Index index;
	int id;
	
	public MeiliSearch() {
		this.id = 0;
		try {
			this.client = new Client(new Config("http://localhost:7700", "1234"));
			this.index = client.index("metadata");
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}
	
	public void addDocument(MetadataBean metadata) {
		try {
			//String uniqueID = UUID.randomUUID().toString();  //Capaz que es mejor esto para el id
			metadata.setId(this.id);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			this.index.addDocuments("[" + gson.toJson(metadata) + "]");
			this.id += 1;
		} catch (Exception e) {
			// También podríamos devolver un booleano según cómo se completó la operación
			System.out.println("ERROR: " + e.getMessage());
		}
	}
}
