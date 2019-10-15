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
    //String passphrase = "Team24i";

    @ResponseBody
    @GetMapping("/get")
    public JSONArray get() throws SQLException{
        return db.selectAll();
    }

    //@ResponseBody
    //@GetMapping("/getFB")
    //public JSONArray getFB(HttpServletRequest request) throws SQLException, Exception {
    //    String password = request.getParameter("pw");
    //    if (passphrase.equals(password)){
    //        return db.selectAll();
    //    }
    //    else {
    //        throw new Exception();
    //    }
    //}

    @ResponseBody
    @GetMapping("/get/linecount")
    public JSONArray getLineCount() throws SQLException{
        return db.feedbackCount();
    }

    @ResponseBody
    @GetMapping("/get/linecount/smiley/{request}")
    public JSONArray smileyLineCount(@PathVariable("request") String request) throws SQLException, Exception {
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
    @GetMapping("/get/os/{request}")
    public JSONArray osCount(@PathVariable("request") String request) throws SQLException, Exception {
            return db.osCount(request);
    }

    @ResponseBody
    @GetMapping("/get/id/{request}")
    public JSONArray theId(@PathVariable("request") String request) throws SQLException, Exception {
        return db.theId(request);

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





}
