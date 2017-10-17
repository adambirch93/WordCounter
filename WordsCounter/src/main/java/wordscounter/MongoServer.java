package wordscounter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Aggregates.limit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document; 
import org.bson.conversions.Bson;

/**
 *
 * @author Adam
 */
public class MongoServer {
    MongoClient mongoClient = null;
    MongoDatabase db = null;
    
    public MongoServer()
    {
        //connect to server
        mongoClient = new MongoClient( "Adam" , 27017 );
        //create db
        db = mongoClient.getDatabase("wordcountdb");
        if (db.getCollection("dictionary")==null) {
            db.createCollection("dictionary");    
        }
    }
    
    public void addHashMap(HashMap<String, Object> wordMap)
    {
        MongoCollection<Document> dictionary = db.getCollection("dictionary");
        
        for (Map.Entry<String, Object> entry : wordMap.entrySet()) {
            BasicDBObject items = new BasicDBObject();
            items.put("k", entry.getKey());
            items.put("v", entry.getValue());
            dictionary.insertOne(new Document(items));
        }
        //Get results
//        db.dictionary.aggregate([{$group:{_id:"$k", total:{$sum:"$v"}}}])
    }
    
    public Document getMostOccurences()
    {
        MongoCollection<Document> dictionary = db.getCollection("dictionary");
        List<BasicDBObject> group = new ArrayList<>();
        group.add(new BasicDBObject(
            "$group", new BasicDBObject("_id", "$k").append(
                "total", new BasicDBObject( "$sum", "$v" )
            )
        ));
        group.add(new BasicDBObject(
                "$sort", new BasicDBObject("total", -1)
        ));
        return dictionary.aggregate(group).first();
    }
    
    public Document getLeastOccurences()
    {
        MongoCollection<Document> dictionary = db.getCollection("dictionary");
        List<BasicDBObject> group = new ArrayList<>();
        group.add(new BasicDBObject(
            "$group", new BasicDBObject("_id", "$k").append(
                "total", new BasicDBObject( "$sum", "$v" )
            )
        ));
        group.add(new BasicDBObject(
                "$sort", new BasicDBObject("total", 1)
        ));
        return dictionary.aggregate(group).first();
    }
    
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

    public void logout()
    {
        if(mongoClient != null)
        {
            mongoClient.close();
        }
    }
}
