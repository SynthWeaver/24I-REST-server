package REST;

import database.DB;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@Controller
public class Mapping {

    DB db = new DB();
    String passphrase = "Team24i";

    @ResponseBody
    @GetMapping("/get")
    public JSONArray get(HttpServletRequest request) throws SQLException {
        String password = request.getParameter("pw");
        if (passphrase.equals(password)){
            return db.selectAll();
        } else {
            throw new SQLException();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/test", method = RequestMethod.POST, consumes = "text/plain")
    public void post(@RequestBody String json) throws ParseException, SQLException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
       db.insert(jsonObject);
    }

    @PostMapping("/post")
    public String handleFileUpload(@RequestParam("image") MultipartFile image,
                                   RedirectAttributes redirectAttributes) {

        return "redirect:/get";
    }
}
