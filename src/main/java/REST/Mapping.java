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
}
