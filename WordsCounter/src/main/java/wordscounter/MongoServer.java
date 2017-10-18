package wordscounter;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document; 

/**
 *
 * @author Adam
 */
public class MongoServer {
    MongoClient mongoClient = null;
    MongoDatabase db = null;
    
    /**
     * Creates the MongoDB client server and the dictionary collection
     * @param host The host to connect to from cmd
     * @param port The port to connect to from cmd
     */
    public MongoServer(String host, int port)
    {
        mongoClient = new MongoClient( host , port );
        db = mongoClient.getDatabase("wordcountdb");
        if (db.getCollection("dictionary")==null) {
            db.createCollection("dictionary");    
        }
    }
    
    /**
     * Inserts the hashmap into the MongoDB
     * @param wordMap The hashmap to be inserted
     */
    public void insertHashMap(HashMap<String, Object> wordMap)
    {
        MongoCollection<Document> dictionary = db.getCollection("dictionary");
        
        for (Map.Entry<String, Object> entry : wordMap.entrySet()) {
            BasicDBObject items = new BasicDBObject();
            items.put("k", entry.getKey());
            items.put("v", entry.getValue());
            dictionary.insertOne(new Document(items));
        }
    }
    
    /**
     * The most occurring word in the file
     * @return String containing the most occurring and the total
     */
    public String getMostOccurrences()
    {
        return getOccurrences(-1);
    }
    
    /**
     * The least occurring word in the file
     * @return String containing the least occurring and the total
     */
    public String getLeastOccurrences()
    {
        return getOccurrences(1);
    }
    
    /**
     * The occurrences function to search the MongoDB
     * @param sort Whether the most(-1) or least(1) frequent is desired
     * @return A string containing the value and the total
     */
    private String getOccurrences(int sort)
    {
        MongoCollection<Document> dictionary = db.getCollection("dictionary");
        List<BasicDBObject> group = new ArrayList<>();
        group.add(new BasicDBObject(
            "$group", new BasicDBObject("_id", "$k").append(
                "total", new BasicDBObject( "$sum", "$v" )
            )
        ));
        group.add(new BasicDBObject(
                "$sort", new BasicDBObject("total", sort)
        ));
        Document occurences = dictionary.aggregate(group).first();
        return "Key " + occurences.getString("_id") + " Total " + occurences.getInteger("total");
    }
    
    /**
     * Gets the unit of work to be done by the user finding the start
     * and end index of the lines of the xml
     * @param lines The number of lines to be read
     * @return The starting index within the file
     */
    public int getUnit(int lines)
    {
        MongoCollection<Document> players = db.getCollection("players");
        //find missing or latest
        BasicDBObject query = new BasicDBObject();
        query.put("END", -1);
        Document prev = players.find().sort(query).limit(1).first();
        int next = 0;
        if (prev != null) 
        {
            next = Integer.valueOf(prev.get("END").toString());
        }
    
        Document document = new Document();
        document.put("START", next);
        document.put("END", next+lines);
        players.insertOne(document);
        return next;
    }
    
    /**
     * A fail safe when running the program has failed
     * @param startIndex The beginning of the computation
     * @param lines The unit of work for the computation
     */
    public void failedUnit(int startIndex, int lines)
    {
        MongoCollection<Document> players = db.getCollection("players");
        //find missing or latest
        BasicDBObject query = new BasicDBObject();
        query.put("START", startIndex);
        query.put("END", startIndex+lines);
        Document document = new Document();
        document.put("START", startIndex);
        document.put("END", startIndex);
        players.updateOne(query, document);
    }

    /**
     * To close the MongoDB connection on exit
     */
    public void logout()
    {
        if(mongoClient != null)
        {
            mongoClient.close();
        }
    }
}
