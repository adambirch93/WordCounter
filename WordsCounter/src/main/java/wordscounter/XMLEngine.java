package wordscounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author Adam
 */
public class XMLEngine {
    private MongoServer server;
    final static Logger logger = Logger.getLogger(XMLEngine.class);
    public XMLEngine(MongoServer server)
    {
        BasicConfigurator.configure();
        this.server = server;
    }
    
    public boolean readFile(String source, int startIndex, int lines)
    {
        HashMap<String, Object> wordMap  = new HashMap<>();
        try {
            File file = new File(source);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuffer = new StringBuilder();
            String line;
            line = bufferedReader.readLine();
            int count = 0; //REMOVE ME
            List<String> data = new ArrayList<String>();

            while ((line = bufferedReader.readLine()) != null && count < startIndex+lines) 
            {
                if (count >= startIndex) {
                    List currentLine = Arrays.asList(line.split("[^\\w']+"));
                    data.addAll(currentLine);

                    addCurrentLineToMap(currentLine, wordMap);
                }
               
                count++;
            }  
            if (line == null) {
                return false;
            }
            fileReader.close();
            
            System.out.println("Contents of file:");
//            System.out.println(wordMap.toString());
            server.addHashMap(wordMap);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally{
            return true;
        }
    }
    
    private static void addCurrentLineToMap(List<String> currentLine, HashMap<String, Object> wordMap)
    {
        for (String currentWord : currentLine) 
        {
            if(wordMap.containsKey(currentWord))
            {
                wordMap.put(currentWord, ((Integer)wordMap.get(currentWord))+1);
            }
            else 
            {
                wordMap.put(currentWord, 1);
            }
        } 
    }
}
