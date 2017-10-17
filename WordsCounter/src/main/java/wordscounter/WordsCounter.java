package wordscounter;

import java.util.HashMap;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Adam
 */
public class WordsCounter {

    /**
     * @param args the command line arguments
     */
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) {
        MongoServer db = null;
        int startIndex = 0, lines = 100;
        try {
            db = new MongoServer();
            
            startIndex = db.getUnit(lines);
            XMLEngine engine = new XMLEngine(db);
            int counter = 0;
            while (engine.readFile(startIndex, lines) && counter<5)
            {
                startIndex = db.getUnit(lines);
                counter++;
            }
            
            db.getMostOccurences();
        } catch (Exception e)
        {
            if (db!=null) {
                db.failedUnit(startIndex, lines);
            }
            e.printStackTrace();
        } finally
        {
            db.logout();
        }
    } 
}
