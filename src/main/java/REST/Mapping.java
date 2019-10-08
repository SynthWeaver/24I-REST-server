package REST;

import database.DB;
import objects.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.sql.SQLException;

@Controller
public class Mapping {

    DB db = new DB();
    String passphrase = "Team24i";

    @ResponseBody
    @GetMapping("/get")
    public RedirectView get(HttpServletRequest request){
        String password = request.getParameter("pw");
        if (passphrase.equals(password)){
            return new RedirectView("/getFB?pw=Team24i");
        } else {
            return new RedirectView("/pagenotfound");
        }
    }

    @ResponseBody
    @GetMapping("/getFB")
    public JSONArray getFB(HttpServletRequest request) throws SQLException, Exception {
        String password = request.getParameter("pw");
        if (passphrase.equals(password)){
            return db.selectAll();
        }
        else {
            throw new Exception();
        }
    }


    @ResponseBody
    @RequestMapping(value = "/post", method = RequestMethod.POST, consumes = "text/plain")
    public void post(@RequestBody String json) throws ParseException, SQLException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
       db.insert(jsonObject);
    }

    @ResponseBody
    @GetMapping("/get/time/asc")
    public JSONArray getTimeAsc() throws SQLException, Exception {
        return db.timeAsc();
    }

    @ResponseBody
    @GetMapping("/get/time/desc")
    public JSONArray getTimeDesc() throws SQLException, Exception {
        return db.timeDesc();
    }

    @ResponseBody
    @GetMapping("/get/smiley/asc")
    public JSONArray smileyAsc() throws SQLException, Exception {
        return db.smileyAsc();
    }

    @ResponseBody
    @GetMapping("/get/smiley/desc")
    public JSONArray smileyDesc() throws SQLException, Exception {
        return db.smileyDesc();
    }

    @ResponseBody
    @GetMapping("/get/device/asc")
    public JSONArray deviceAsc() throws SQLException, Exception {
        return db.deviceAsc();
    }

    @ResponseBody
    @GetMapping("/get/device/desc")
    public JSONArray deviceDesc() throws SQLException, Exception {
        return db.deviceDesc();
    }

    @ResponseBody
    @GetMapping("/get/smiley/1")
    public JSONArray smiley1() throws SQLException, Exception {
        return db.smiley1();
    }

    @ResponseBody
    @GetMapping("/get/smiley/2")
    public JSONArray smiley2() throws SQLException, Exception {
        return db.smiley2();
    }


    @ResponseBody
    @GetMapping("/get/smiley/3")
    public JSONArray smiley3() throws SQLException, Exception {
        return db.smiley3();
    }


    @ResponseBody
    @GetMapping("/get/smiley/4")
    public JSONArray smiley4() throws SQLException, Exception {
        return db.smiley4();
    }

    @ResponseBody
    @GetMapping("/get/smiley/5")
    public JSONArray smiley5() throws SQLException, Exception {
        return db.smiley5();
    }






}
