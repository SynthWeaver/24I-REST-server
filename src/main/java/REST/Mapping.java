package REST;

import database.DB;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@Controller
public class Mapping {

    DB db = new DB();

    //
    // MAIN METHODS
    //

    // Basic select all feedback
    @ResponseBody
    @GetMapping("/get")
    public JSONArray getFeedback() throws SQLException {
        return db.getFeedback();
    }

    @ResponseBody
    @GetMapping("/get/GroupByTag")
    public JSONArray getGroupByTag() throws SQLException {
        return db.getAllGroupByTag();
    }

    // get all apps from DB
    @ResponseBody
    @GetMapping("/get/apps")
    public JSONArray getApps() throws SQLException {
        return db.selectAllAps();
    }

    // list of tags
    @ResponseBody
    @GetMapping("/get/tags")
    public JSONArray selectTags() throws SQLException {
        return db.selectTags();
    }


    //
    // POST
    //

    // POST query for adding feedback
    // (Category should be either "bugreport", "suggestion" or "feedback")
    @ResponseBody
    @RequestMapping(value = "/post", method = RequestMethod.POST, consumes = "text/plain")
    public void post(@RequestBody String json) throws ParseException, SQLException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
        db.insertFeedback(jsonObject);
    }

    // Adding an app
    //const { appName, logoURL, template, password,}
    @ResponseBody
    @RequestMapping(value = "/addAccount", method = RequestMethod.POST, consumes = "text/plain")
    public void addAccount(@RequestBody String json) throws ParseException, SQLException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
        db.insertAccount(jsonObject);
    }

    //
    // OTHER QUERIES
    //

    // list of tags and their templates
    @ResponseBody
    @GetMapping("/get/tagsWithTemplates")
    public JSONArray selectTagsWithTemplates() throws SQLException {
        return db.selectTagsWithTemplates();
    }


    // get all apps with questions from DB
    @ResponseBody
    @GetMapping("/get/appsWithFeedback")
    public JSONArray allAppsWithFeedback() throws SQLException {
        return db.allAppsWithFeedback();
    }

    // get all apps with questions from DB
    @ResponseBody
    @GetMapping("/get/appsWithQuestions")
    public JSONArray getAppsWithQuestions() throws SQLException {
        return db.selectAllAppsWithQuestions();
    }

    // get app data by searching with the app id (int)
    @ResponseBody
    @GetMapping("/get/apps/{id}")
    public JSONArray getAppFromId(@PathVariable("id") Integer id) throws SQLException {
        return db.selectAppFromId(id);
    }

    // Get app data by searching with the app name (string)
    // (like getAppFromId but the result is in a bit different form)
    @ResponseBody
    @GetMapping("/get/appByName/{name}")
    public JSONObject getAppByName(@PathVariable("name") String name) throws SQLException {
        return db.getAppByName(name);
    }

    // Show template config of a specific app
    @ResponseBody
    @GetMapping("/get/templates/{id}")
    public JSONArray getTemplateConfigFromApp(@PathVariable("id") Integer id) throws SQLException {
        return db.selectTemplateConfigByApp(id);
    }

    // feedbacks of specific app
    @ResponseBody
    @GetMapping("/get/FbByAppName/{name}")
    public JSONArray getFbByAppName(@PathVariable("name") String name) throws SQLException {
        return db.getFbOfApp(name);
    }

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JSONObject login(@RequestBody Map<String, String> json) throws Exception {
        return db.login(json);
    }

    //Category distribution
    @ResponseBody
    @GetMapping("/get/catDistr")
    public JSONArray catDistr() throws SQLException {
        return db.catDistr();
    }

    // count of all smileys (ratings) separately
    @ResponseBody
    @GetMapping("/get/linecount/smiley")
    public JSONArray smileyLineCountAll() throws SQLException, Exception {
        return db.smileyCountAll();
    }

    // Feedbacks per year
    @ResponseBody
    @GetMapping("/get/feedbacks/year/{year}")
    public JSONArray feedbacksPerYear(@PathVariable("year") String year) throws SQLException, Exception {
        return db.feedbacksPerMonth(year);
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

    // only feedback with a specific tag
    @ResponseBody
    @GetMapping("/get/tag/{request}")
    public JSONArray theTag(@PathVariable("request") String request) throws SQLException, Exception {
        return db.theTag(request);
    }

    // only feedback with a specific tag
    @ResponseBody
    @GetMapping("/get/nested/{request}")
    public JSONArray oneJsonPerTag(@PathVariable("request") String request) throws SQLException, Exception {
        return db.oneJsonPerTag(request);
    }

    // Sort by time: /get/time/asc or /get/time/desc
    @ResponseBody
    @GetMapping("/get/time/{request}")
    public JSONArray time(@PathVariable("request") String request) throws SQLException, Exception {
        if (request.equals("asc") || request.equals("desc")) {
            return db.time(request);
        } else {
            throw new Exception();
        }
    }

    // DELETE FEEDBACK BY tag
    @ResponseBody
    @GetMapping("/areyousure/delete/{tag}")
    public int deleteFeedback(@PathVariable("id") String tag) throws SQLException {
        return db.deleteFeedback(tag);
    }

    // Smiley averages per app
    @ResponseBody
    @GetMapping("/getAvgPerApp")
    public JSONArray getAvgPerApp() throws SQLException {
        return db.avgPerApp();
    }

    // Rating averages per app
    @ResponseBody
    @GetMapping("/getAvgRatingPerQuestPerApp/{app}")
    public JSONArray avgStarPerQuesPerApp(@PathVariable("app") String app) throws SQLException {
        return db.avgStarPerQuesPerApp(app);
    }


    // MAYBE UNNECESSARY

    //linecount; how many feedbacks
    @ResponseBody
    @GetMapping("/get/linecount")
    public JSONArray getLineCount() throws SQLException {
        return db.feedbackCount();
    }

    // Do we need this?
    // linecount of a specific os
    @ResponseBody
    @GetMapping("/get/os/{request}")
    public JSONArray osCount(@PathVariable("request") String request) throws SQLException, Exception {
        return db.osCount(request);
    }


    // Right now we're using osCountTwo() to get only data
    // about iOS and Android in the charts but keeping this
    // in case we'll need a more dynamic approach

    // Os distribution
    @ResponseBody
    @GetMapping("/get/osdist")
    public JSONArray osDist() throws SQLException {
        return db.osDist();

    }

    // Only feedbacks that use Template1 or Template2
    @ResponseBody
    @GetMapping("/get/oneLineFeedbacks")
    public JSONArray getOneLineFbs() throws SQLException {
        return db.getOneLineFbs();
    }
}