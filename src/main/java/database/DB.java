package database;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DB {

    public void insert(JSONObject jsonObject){
        //get old data
        JSONArray jsonArray = this.selectAll();

        //add new data to old data
        jsonArray.add(jsonObject);

        //write to local data
        try (FileWriter file = new FileWriter("DB.json")) {
            file.write(jsonArray.toJSONString());
            file.flush();
            System.out.println("Added");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONArray selectAll(){
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("DB.json"))
        {
            //Read JSON file
            return (JSONArray) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
