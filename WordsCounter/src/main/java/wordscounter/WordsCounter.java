package wordscounter;

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
        String source = "", host = "";
        int port = -1, lines = 100;
        for (int i = 0; i < args.length; i++) {
            if ("-source".equals(args[i]) && i+1 < args.length) {
                source = args[i+1];
            }
            if ("-mongo".equals(args[i]) && i+1 < args.length) {
                host = args[i+1].split(":")[0];
                port = Integer.valueOf(args[i+1].split(":")[1]);                
            }
            if ("-lines".equals(args[i]) && i+1 < args.length) {
                lines = Integer.valueOf(args[i+1]);                
            }
        }
        
        MongoServer db = null;
        int startIndex = 0;
        try {
            
            db = new MongoServer(host, port);
            startIndex = db.getUnit(lines);
            XMLEngine engine = new XMLEngine(db);
            int counter = 0;
            
            while (engine.readFile(source, startIndex, lines) && counter<5)
            {
                startIndex = db.getUnit(lines);
                counter++;
            }
            
            System.out.println("Most Occurences " + db.getMostOccurrences());
            System.out.println("Least Occurences " + db.getLeastOccurrences());
            
        } catch (Exception e)
        {
            if (db!=null) {
                db.failedUnit(startIndex, lines);
            }
            e.printStackTrace();
        } finally
        {
            if (db!=null) {
                db.logout();
            }
        }
    } 
}
