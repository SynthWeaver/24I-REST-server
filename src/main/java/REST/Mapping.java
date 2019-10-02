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

import java.sql.SQLException;

@Controller
public class Mapping {

    DB db = new DB();

    @ResponseBody
    @GetMapping("/get")
    public JSONArray get() throws SQLException {
        return db.selectAll();
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
