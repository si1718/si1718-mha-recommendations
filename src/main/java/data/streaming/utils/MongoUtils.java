package data.streaming.utils;


import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;

import data.streaming.dto.KeywordDTO;
import data.streaming.dto.Recommendation;

public class MongoUtils {
	
	private static final String MONGO_DB = "mongodb://manuel:manuel@ds255455.mlab.com:55455/si1718-mha-projects";
	private static final String MONGO_RM = "recommendations";
	private static final String MONGO_RT = "ratings";
	
	public static MongoDatabase database= null; 
	
	public static void initialize() {
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		
		MongoClientURI uri = new MongoClientURI(MONGO_DB);
		@SuppressWarnings("resource")
		MongoClient mongoClient = new MongoClient(uri);
		database = mongoClient.getDatabase(uri.getDatabase()).withCodecRegistry(pojoCodecRegistry);
	}
	
	public static Set<KeywordDTO> getRatings() {
		Set<KeywordDTO> keywords = new HashSet<KeywordDTO>();
		MongoCollection<KeywordDTO> collection = database.getCollection(MONGO_RT, KeywordDTO.class);
		MongoCursor<KeywordDTO> cursor = collection.find().iterator();
		
		try {
		    while (cursor.hasNext()) {
		    	keywords.add(cursor.next());
		    }
		} finally {
		    cursor.close();
		}
		
		return keywords;
	}

	private static void addRecommendation(Recommendation x) {
		MongoCollection<Recommendation> collection = database.getCollection(MONGO_RM, Recommendation.class);
		BasicDBObject query = new BasicDBObject();
		query.append("idProject", x.getIdProject());
		
		try {
			collection.findOneAndUpdate(query, new Document("$set", x), (new FindOneAndUpdateOptions()).upsert(true));
		} catch (Exception e) {
			System.out.println("#ERROR: "+e.getMessage());
		}
	}
	
	public static void addRecommendations(List<Recommendation> recommendations) {
		recommendations.stream().forEach(x -> addRecommendation(x));
	}
}
