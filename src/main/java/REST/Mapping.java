package REST;

import database.DB;
import objects.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Mapping {

    DB db;

    public Mapping(){
        db = new DB();
    }

    @ResponseBody
    @GetMapping("/get")
    public JSONArray get() {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("smiley", 1);
        jsonObject.put("feedback", "This is feedback");
        jsonObject.put("device", "Apple Iphone 4");
        jsonObject.put("time", DateTime.now());

        db.insert(jsonObject);

        return db.selectAll();
    }

    @PostMapping("/post")
    public void JSONArray post() {

        System.out.println("Yay");
    }

}
