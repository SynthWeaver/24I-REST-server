package REST;

import org.json.simple.JSONArray;
import database.DB;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.*;

public class Analytics {

    String feedbacks;
    DB db = new DB();
    JSONArray feedbackData = new JSONArray();

    private String feedback;



    public JSONArray analyzeData() throws SQLException {
        int linecount = db.lineCount();
        feedbackData = db.onlyFB();

        for (int i = 0; i < linecount; i++){
            feedbacks += (feedbackData.get(i).toString() + " ");
        }
        JSONArray finalArray = checkWords(feedbacks);

        return finalArray;
    }

    public JSONArray avgPerApp() throws SQLException {

        // Create empty array
        JSONArray newArray = db.avgPerApp();




        // return array
        return newArray;
    }


    private JSONArray checkWords(String givenString){
        JSONArray analyzedArray = new JSONArray();

        // Space character as word separator
        List<String> list = Arrays.asList(givenString.split(" "));
        Set<String> uniqueWords = new HashSet<String>(list);
        for (String word : uniqueWords) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(word, Collections.frequency(list, word));
            analyzedArray.add(jsonObject);
        }

        return analyzedArray;
    }


}
