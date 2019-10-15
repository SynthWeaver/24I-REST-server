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

    @ResponseBody
    @GetMapping("/get")
    public JSONArray get(HttpServletRequest request) throws SQLException {
        return db.selectAll();
    }

    @PostMapping("/post")
    public String handleFileUpload(@RequestParam("image") MultipartFile image,
                                   RedirectAttributes redirectAttributes) {

        return "redirect:/get";
    }
}
