package REST;

import database.DB;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Controller
public class Mapping {

    DB db = new DB();

    // Analytics disabled until it's fixed
    // Analytics an = new Analytics();

    //
    // MAIN METHODS
    //

    // basic get (whole table)
    @ResponseBody
    @GetMapping("/get")
    public JSONArray get() throws SQLException{
        return db.selectAll();
    }

    // get all apps from DB
    @ResponseBody
    @GetMapping("/get/apps")
    public JSONArray getApps() throws SQLException {
        return db.selectAllAps();
    }

    // get specific app from id
    @ResponseBody
    @GetMapping("/get/apps/{id}")
    public JSONArray getAppFromId(@PathVariable("id") Integer id) throws SQLException {
        return db.selectAppFromId(id);

    }

    // template for /post:
    // {"smiley" : 7, "feedback" : "Nice UI", "category" : "feedback", "device" : "Pixel", "os" : "Android", "app" : "SomeApp", "image" : ""}
    @ResponseBody
    @RequestMapping(value = "/post", method = RequestMethod.POST, consumes = "text/plain")
    public void post(@RequestBody String json) throws ParseException, SQLException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
        db.insert(jsonObject);
    }


    //
    // OTHER QUERIES
    //

    //linecount; how many feedbacks
    @ResponseBody
    @GetMapping("/get/linecount")
    public JSONArray getLineCount() throws SQLException{
        return db.feedbackCount();
    }

    //Category distribution
    @ResponseBody
    @GetMapping("/get/catDistr")
    public JSONArray catDistr() throws SQLException{
        return db.catDistr();
    }

    //how many smileys of a specific value
    @ResponseBody
    @GetMapping("/get/linecount/smiley/{request}")
    public JSONArray smileyLineCount(@PathVariable("request") String request ) throws SQLException, Exception {
        int reqTest = 0;
        try {
            reqTest = Integer.parseInt(request);
        } catch(Exception e) {
            reqTest = 0;
        }
        if ((reqTest >= 1 && reqTest <= 10)){
            return db.smileyCount(request);
        }
        else {
            throw new Exception();
        }
    }

    @ResponseBody
    @GetMapping("/get/linecount/smiley")
    public JSONArray smileyLineCountAll() throws SQLException, Exception {
        return db.smileyCountAll();
    }

    //only feedback from a specific os
    @ResponseBody
    @GetMapping("/get/os/{request}")
    public JSONArray osCount(@PathVariable("request") String request) throws SQLException, Exception {
        return db.osCount(request);
    }

    // only feedbacks from 2 specific os
    @ResponseBody
    @GetMapping("/get/os2/{os1}+{os2}")
    public JSONArray osCountTwo(@PathVariable("os1") String os1, @PathVariable("os2") String os2) throws SQLException, Exception {
        return db.osCountTwo(os1, os2);
    }

    // only feedback with a specific id
    @ResponseBody
    @GetMapping("/get/id/{request}")
    public JSONArray theId(@PathVariable("request") String request) throws SQLException, Exception {
        return db.theId(request);

    }

    // Sort by time: /get/time/asc or /get/time/desc
    @ResponseBody
    @GetMapping("/get/time/{request}")
    public JSONArray time(@PathVariable("request") String request) throws SQLException, Exception {
        if (request.equals("asc") || request.equals("desc")) {
            return db.time(request);
        }
        else {
            throw new Exception();
        }
    }

    @ResponseBody
    @GetMapping("/get/feedbacks")
    public JSONArray timeRange() throws SQLException, Exception {
        return db.feedbacksPerMonth();
    }

    // Sort by device: /get/device/asc or /get/device/desc
    @ResponseBody
    @GetMapping("/get/device/{request}")
    public JSONArray device(@PathVariable("request") String request) throws SQLException, Exception {
        if (request.equals("asc") || request.equals("desc")) {
            return db.device(request);
        }
        else {
            throw new Exception();
        }
    }

    // Sort by app: /get/app/asc or /get/app/desc
    @ResponseBody
    @GetMapping("/get/app/{request}")
    public JSONArray app(@PathVariable("request") String request) throws SQLException, Exception {
        if (request.equals("asc") || request.equals("desc")) {
            return db.app(request);
        }
        else {
            throw new Exception();
        }
    }

    // Get smileys for example with /get/smiley/1 (1-10)
    // or sort /get/smiley/asc or /get/smiley/desc
    @ResponseBody
    @GetMapping("/get/smiley/{request}")
    public JSONArray smiley(@PathVariable("request") String request) throws SQLException, Exception {

        int reqTest = 0;
        try {
            reqTest = Integer.parseInt(request);
        } catch(Exception e) {
            reqTest = 0;
        }
        if ((reqTest >= 1 && reqTest <= 10) || request.equals("asc") || request.equals("desc")){
            return db.smiley(request);
        }
        else {
            throw new Exception();
        }
    }

    // get feedbacks of specific app
    @ResponseBody
    @GetMapping("/get/{app}")
    public JSONArray selectAllAPP(@PathVariable("app") String request) throws SQLException {
        return db.selectAllAPP(request);
    }

    // DELETE A FEEDBACK BY ID
    @ResponseBody
    @GetMapping("/areyousure/delete/{id}")
    public int deleteFeedback(@PathVariable("id") Integer id) throws SQLException {
        return db.deleteFeedback(id);
    }


    // analyzed data for dashboard app
    // Disabled until it's fixed
/*    @ResponseBody
    @GetMapping("/getAnData")
    public JSONArray getAnData() throws SQLException{
        return an.analyzeData();
    }*/

    // Smiley averages per app
    @ResponseBody
    @GetMapping("/getAvgPerApp")
    public JSONArray getAvgPerApp() throws SQLException{
        return db.avgPerApp();
    }
}
