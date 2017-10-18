package wordscounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author Adam
 */
public class XMLEngine {
    private MongoServer server;
    
    /**
     * Create the Engine with the server
     * @param server The MongoDB client connection
     */
    public XMLEngine(MongoServer server)
    {
        BasicConfigurator.configure();
        this.server = server;
    }
    
    /**
     * The readfile engine process to calculate the values
     * @param source The source of the original xml file
     * @param startIndex The initial index to read the file from
     * @param lines The unit of work to read the file
     * @return Whether or not the file reading is successful
     */
    public boolean readFile(String source, int startIndex, int lines)
    {
        HashMap<String, Object> wordMap  = new HashMap<>();
        try {
            File file = new File(source);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            line = bufferedReader.readLine();
            int count = 0; //REMOVE ME

            while ((line = bufferedReader.readLine()) != null && count < startIndex+lines) 
            {
                if (count >= startIndex) {
                    List currentLine = Arrays.asList(line.split("[^A-Za-z0-9]"));

                    addCurrentLineToMap(currentLine, wordMap);
                }
               
                count++;
            }  
            if (line == null) {
                return false;
            }
            fileReader.close();
            server.insertHashMap(wordMap);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally{
            return true;
        }
    }
    
    /**
     * Adds the current line to the Hashmap upon splitting
     * @param currentLine The currentline split into a list of words
     * @param wordMap The containing Hashmap of the results
     */
    private static void addCurrentLineToMap(List<String> currentLine, HashMap<String, Object> wordMap)
    {
        for (String currentWord : currentLine) 
        {
            if (!currentWord.equals("")) {
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
}
