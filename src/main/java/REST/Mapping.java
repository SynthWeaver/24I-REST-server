package REST;

import database.DB;
import objects.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class Mapping {

    DB db;

    public Mapping(){
        db = new DB();
    }

    @ResponseBody
    @GetMapping("/get")
    public JSONArray get() {

        return db.selectAll();
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @ResponseBody
    @RequestMapping(value = "/post", method = RequestMethod.POST, consumes = "text/plain")
    public void post(@RequestBody String json) throws ParseException {
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
        jsonObject.put("device", "Apple Iphone 4");
        jsonObject.put("time", DateTime.now());
        
        db.insert(jsonObject);
    }
}
