package database;

import objects.DateTime;
import objects.Password;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

public class DB {

    //
    // MAIN QUERIES
    //

    // basic select all
    public JSONArray getFeedback() throws SQLException {
        String query = "SELECT * FROM app_feedback";
        return this.getJaByQuery(query);
    }

    // basic select all group by tag
    public JSONArray getOneLineFbs() throws SQLException {
        Connection conn = DBConnection.connection();
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM app_feedback WHERE template = 'Template1' OR template = 'Template2' ORDER BY time");

        JSONArray jsonArray = printDB(rs);

        close(rs);
        close(stmt);
        return jsonArray;
    }

    // select all apps from DB
    public JSONArray selectAllAps() throws SQLException {
        return this.getJaByQuery("SELECT * FROM apps");
    }

    // List of tags
    public JSONArray selectTags() throws SQLException {
        Connection conn = DBConnection.connection();
        Statement stmt = conn.createStatement();
        JSONArray jsonArray = new JSONArray();

        ResultSet rs = stmt.executeQuery("SELECT DISTINCT tag FROM (SELECT tag, time FROM app_feedback ORDER BY time) a");
        while (rs.next()){
            JSONObject jso = new JSONObject();
            jso.put("tag", rs.getString("tag"));
            jsonArray.add(jso);
        }

        close(rs);
        close(stmt);
        return jsonArray;
    }

    //
    // POST
    //

    // For POST
    // (Category should be either "bugreport", "suggestion" or "feedback")
    public void insertFeedback(JSONObject jsonObject) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        //In frontend feedback moet het gemaakte jsonobject aangepast worden zodat er onder andere een feedback_id aangemaakt wordt
        String app = (jsonObject.get("app") != null ? jsonObject.get("app").toString() : null);
        String features = (jsonObject.get("feature") != null ? jsonObject.get("feature").toString() : null);
        String rating = (jsonObject.get("rating") != null ? jsonObject.get("rating").toString() : null);

        String feedback = (jsonObject.get("feedback") != null ? jsonObject.get("feedback").toString() : null);
        String category = (jsonObject.get("category") != null ? jsonObject.get("category").toString() : null);
        String time = DateTime.now();
        String device = (jsonObject.get("device") != null ? jsonObject.get("device").toString() : null);
        String os = (jsonObject.get("os") != null ? jsonObject.get("os").toString() : null);
        String question = (jsonObject.get("starQuestion") != null ? jsonObject.get("starQuestion").toString() : null);
        String stars = (jsonObject.get("stars") != null ? jsonObject.get("stars").toString() : null);
        String image = (jsonObject.get("image") != null ? jsonObject.get("image").toString() : null);
        String tag = (jsonObject.get("tag") != null ? jsonObject.get("tag").toString() : null);
        String template = (jsonObject.get("template") != null ? jsonObject.get("template").toString() : null);

        String query = String.format("INSERT INTO feedbacks.app_feedback" +
                        "(feedback, category, time, device,os,app,image,features,rating, tag, stars, star_question, template)" +
                        "VALUES" +
                        "('%s','%s','%s','%s','%s','%s','%s','%s','%s', '%s', '%s', '%s', '%s');",
                feedback, category, time, device, os, app, image, features, rating, tag, stars, question, template
        );

        stmt.executeUpdate(query);

        close(stmt);
    }

    // Adding an app
    //const { appName, logoURL, template, password,}
    public void insertAccount(JSONObject jsonObject) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        if(jsonObject.isEmpty()){
            close(stmt);
            return;
        }

        String appName = jsonObject.get("appName").toString();
        String logoURL = jsonObject.get("logoURL").toString();
        String template = jsonObject.get("template").toString();
        String password = jsonObject.get("password").toString();
        String featureConfig = jsonObject.get("featureConfig").toString();
        String starQuestion = jsonObject.get("starQuestion").toString();

        if(     appName.isEmpty() ||
                logoURL.isEmpty() ||
                template.isEmpty() ||
                password.isEmpty()
            //featureConfig.isEmpty() ||
            //starQuestion.isEmpty()
        ){
            close(stmt);
            return;
        }

        String hashedPassword = null;
        try {
            hashedPassword = Password.getSaltedHash(password);
        } catch (Exception e) {
            close(stmt);
            e.printStackTrace();
        }

        String appTableQuery = String.format("INSERT IGNORE INTO feedbacks.apps" +

                        "(appName,logoURL,template,password)" +
                        "VALUES" +
                        "('%s','%s','%s','%s');",
                appName, logoURL, template, hashedPassword
        );

        stmt.executeUpdate(appTableQuery);

        int appId = selectAppIdFromAppName(appName);

        String configTableQuery = String.format("INSERT INTO feedbacks.TemplateConfig" +
                        "(Template, FeatureConfig, StarQuestion, App)" +
                        "VALUES" +
                        "('%s', '%s', '%s', '%s')",
                template, featureConfig, starQuestion, appId
        );

        stmt.executeUpdate(configTableQuery);
        close(stmt);
    }

    //
    // OTHER QUERIES
    //

    // List of tags and their templates
    public JSONArray selectTagsWithTemplates() throws SQLException {
        Statement stmt;
        ResultSet rs;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ArrayList<String> tags = new ArrayList<String>();
        String curTag = "";

        // get the list of tags from the database
        JSONArray tagArray = selectTags();

        // extract only tags into a list (without column labels)
        for (int i = 0; i < tagArray.size(); i++){
            JSONObject ob = (JSONObject)tagArray.get(i);
            String temptag = (String) ob.get("tag");
            tags.add(temptag);
        }

        // initialize new array that we
        JSONArray jsonArray = new JSONArray();

        // for all tags in our list, check what template they're using
        // and save in our array both tag and template
        for (int i = 0; i < tags.size(); i++){

            curTag = tags.get(i);
            PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT template FROM app_feedback WHERE tag = ?");
            ps.setString(1, curTag);

            rs = ps.executeQuery();

            while (rs.next()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tag", curTag);
                jsonObject.put("template", rs.getString("template"));

                jsonArray.add(jsonObject);
            }
            close(rs);
        }
        close(stmt);
        return jsonArray;
    }

    // select all different apps appearing in app_feedback
    public JSONArray allAppsWithFeedback() throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT DISTINCT app FROM app_feedback");

        // Fetch each row from the result set
        JSONArray jsonArray = new JSONArray();
        while (rs.next()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("app", rs.getString("app"));
            jsonArray.add(jsonObject);
        }
        close(rs);
        close(stmt);
        return jsonArray;
    }

    // select all apps that have questions
    public JSONArray selectAllAppsWithQuestions() throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT DISTINCT app FROM app_feedback WHERE star_question IS NOT NULL AND star_question <> \"\"");

        // Fetch each row from the result set
        JSONArray jsonArray = new JSONArray();
        while (rs.next()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("app", rs.getString("app"));
                jsonArray.add(jsonObject);
        }
        close(rs);
        close(stmt);
        return jsonArray;
    }

    // retrieves the app based on the passed id
    public JSONArray selectAppFromId(Integer id) throws SQLException {
        return getJaByQuery(String.format("SELECT * FROM apps WHERE id = %s", id));
    }

    public JSONObject getAppByName(String name) throws SQLException {
        String query = String.format("SELECT * FROM apps WHERE appName = '%s'", name);
        return this.getJoByQuery(query);
    }

    public JSONArray selectTemplateConfigByApp(Integer id) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        PreparedStatement ps = conn.prepareStatement("SELECT t.Id, t.Template, t.StarQuestion, t.FeatureConfig, t.App, a.appName FROM TemplateConfig t INNER JOIN apps a ON t.App = a.id WHERE App = ?");
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();
        // Fetch each row from the result set
        JSONArray jsonArray = printTemplateDB(rs);
        close(rs);
        close(stmt);
        return jsonArray;
    }

    // feedbacks of specific app
    public JSONArray getFbOfApp(String app) throws SQLException {
        String query = String.format("SELECT * FROM app_feedback WHERE app = '%s'", app);
        return this.getJaByQuery(query);
    }

    public JSONObject login(Map<String, String> json) throws Exception {
        String appName = json.get("name");
        String password = json.get("password");

        JSONObject result = new JSONObject();

        if(appName.isEmpty() || password.isEmpty()){
            result.put("result", false);
            return  result;
        }

        JSONObject app = this.getAppByName(appName);
        boolean loginResult;

        if(app != null){
            loginResult = Password.check(password, (String) app.get("password"));
        }else{
            loginResult = false;
        }

        result.put("result", loginResult);

        return result;

    }

    // Category distribution
    public JSONArray catDistr() throws SQLException {
        Statement stmt;
        ResultSet rs;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ArrayList<String> tags = new ArrayList<String>();
        String curTag = "";
        int feed = 0, bugr = 0, sugg = 0;

        // get the list of tags from the database
        JSONArray tagArray = selectTags();

        // extract only tags into a list (without column labels)
        for (int i = 0; i < tagArray.size(); i++){
            JSONObject ob = (JSONObject)tagArray.get(i);
            String temptag = (String) ob.get("tag");
            tags.add(temptag);
        }

        // for all tags in our list, check the category
        for (int i = 0; i < tags.size(); i++){

            curTag = tags.get(i);
            PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT category FROM app_feedback WHERE tag = ?");
            ps.setString(1, curTag);

            rs = ps.executeQuery();

            while (rs.next()){
                String curCat = rs.getString("category");
                if (curCat.equals("feedback")){
                    feed += 1;
                } else if (curCat.equals("bugreport")){
                    bugr += 1;
                } else {
                    sugg += 1;
                }
            }

            close(rs);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("feedback", feed);
        jsonObject.put("bugreport", bugr);
        jsonObject.put("suggestion", sugg);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);

        close(stmt);
        return jsonArray;
    }

    // Amount of all smileys
    public JSONArray smileyCountAll() throws SQLException {

        Statement stmt;
        PreparedStatement ps;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        JSONArray jsonArray = new JSONArray();

        for (int i = 1; i<=10; i++){
            ps = conn.prepareStatement("SELECT COUNT(*) AS sc FROM app_feedback WHERE rating = ?");
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("SmileyRange", rs.getInt("sc"));

                jsonArray.add(jsonObject);
            }
            close(rs);
        }
        close(stmt);
        return jsonArray;
    }

    public JSONArray feedbacksPerYear() throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs;
        rs = stmt.executeQuery("SELECT * FROM app_feedback ORDER BY time ASC");
        JSONArray jsonArray = printDB(rs);
        int[] yearlyData = new int[12];
        for (Object o : jsonArray) {
            JSONObject json = (JSONObject) o;
            String time = (String) json.get("time");
            String month = time.substring(5,7);
            int m = Integer.parseInt(month);
            switch (m) {
                case 1:
                    yearlyData[0] += 1;
                    break;
                case 5:
                    yearlyData[4] += 1;
                    break;
                case 9:
                    yearlyData[8] += 1;
                    break;
                case 10:
                    yearlyData[9] += 1;
                    break;
                case 11:
                    yearlyData[10] += 1;
                    break;
            }
        }
        close(rs);
        close(stmt);
        JSONArray result = new JSONArray();
        for (int i = 0; i < 12; i++) {
            result.add(yearlyData[i]);
        }
        return result;
    }

    // Line count of 2 specific os's
    public JSONArray osCountTwo(String os1, String os2) throws SQLException {

        Statement stmt;
        ResultSet rs;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ArrayList<String> tags = new ArrayList<String>();
        String curTag = "";
        int os1count = 0, os2count = 0;

        // get the list of tags from the database
        JSONArray tagArray = selectTags();

        // extract only tags into a list (without column labels)
        for (int i = 0; i < tagArray.size(); i++){
            JSONObject ob = (JSONObject)tagArray.get(i);
            String temptag = (String) ob.get("tag");
            tags.add(temptag);
        }

        // for all tags in our list, check the category
        for (int i = 0; i < tags.size(); i++){

            curTag = tags.get(i);


            PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT os FROM app_feedback WHERE tag = ? AND os = ?");
            PreparedStatement ps2 = conn.prepareStatement("SELECT DISTINCT os FROM app_feedback WHERE tag = ? AND os = ?");
            ps.setString(1, curTag);
            ps.setString(2, os1);
            ps2.setString(1, curTag);
            ps2.setString(2, os2);

            rs = ps.executeQuery();

            while (rs.next()){
                os1count += 1;
            }

            rs = ps2.executeQuery();

            while (rs.next()){
                os2count += 1;
            }

            close(rs);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(os1, os1count);
        jsonObject.put(os2, os2count);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);

        close(stmt);
        return jsonArray;
    }



    // Feedback with a specific Id
    public JSONArray theId(String request) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM app_feedback  WHERE feedback_id = ?");
        ps.setString(1, request);
        ResultSet rs = ps.executeQuery();

        JSONArray jsonArray = printDB(rs);
        close(rs);
        close(stmt);
        return jsonArray;
    }

    // Sort by time
    public JSONArray time(String request) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs;

        if (request.equals("asc")) {
            rs = stmt.executeQuery("SELECT * FROM app_feedback ORDER BY time ASC");
        } else{
            rs = stmt.executeQuery("SELECT * FROM app_feedback ORDER BY time DESC");
        }

        JSONArray jsonArray = printDB(rs);
        close(rs);
        close(stmt);
        return jsonArray;
    }

    // Delete a feedback
    public Integer deleteFeedback(Integer request) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        PreparedStatement ps = conn.prepareStatement("DELETE FROM app_feedback WHERE feedback_id = ?");
        ps.setInt(1, request);
        Integer result = ps.executeUpdate();
        close(stmt);
        return result;
    }

    // average grade per app
    public JSONArray avgPerApp() throws SQLException {
        int sum = 0, max = 0, appCount = 0;
        double avg = 0;

        double sumD;
        double countD;
        ArrayList<String> apps = new ArrayList<String>();
        String cur;


        JSONArray jsonArray = new JSONArray();

        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        // See how many different apps and put them in an array list
        ResultSet rs = stmt.executeQuery("SELECT DISTINCT app FROM app_feedback WHERE rating IS NOT NULL AND rating <> \"\" ORDER BY app");

        while (rs.next()) {

            apps.add(rs.getString("app"));
            max++;
        }

        // For every app in the array list, check the smileys, add them to variable sum
        for (int i = 0; i < max; i++){
            cur = apps.get(i);
            PreparedStatement ps = conn.prepareStatement("SELECT rating AS num FROM app_feedback WHERE app = ? AND rating IS NOT NULL AND rating <> \"\"");
            ps.setString(1, cur);
            rs = ps.executeQuery();

            while (rs.next()) {
                sum += (rs.getInt("num"));
            }

            // Get line count for this app
            ps = conn.prepareStatement("SELECT COUNT(*) AS cn FROM app_feedback WHERE app = ? AND rating IS NOT NULL AND rating <> \"\"");
            ps.setString(1, cur);
            rs = ps.executeQuery();

            while (rs.next()) {
                appCount = (rs.getInt("cn"));
            }

            //calculate average, convert into integer between 1-100 for the react native chart
            sumD = sum;
            countD = appCount;
            avg = sumD/countD;
            int newAvg = (int)(avg*100);

            JSONObject jsonOb = new JSONObject();
            jsonOb.put("x", cur);
            jsonOb.put("y", newAvg);
            jsonArray.add(jsonOb);
            sum = 0;
        }
        close(rs);
        close(stmt);
        return jsonArray;
    }

    // average stars per question per app
    public JSONArray avgStarPerQuesPerApp(String request) throws SQLException {
        int starsum = 0, max = 0, quesCount = 0;
        double avg = 0;

        double sumD;
        double countD;
        ArrayList<String> questions = new ArrayList<String>();
        String cur;

        JSONArray jsonArray = new JSONArray();

        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        // See how many different questions (in that app), put them in a list
        PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT star_question FROM app_feedback WHERE app = ? ORDER BY star_question");
        ps.setString(1, request);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            if(!"".equals(rs.getString("star_question"))){
                questions.add(rs.getString("star_question"));
                max++;
            }
        }

        // For every question in the array list, check the stars, add them to variable sum
        for (int i = 0; i < max; i++){
            cur = questions.get(i);
            ps = conn.prepareStatement("SELECT stars AS num FROM app_feedback WHERE star_question = ? AND app = ?");
            ps.setString(1, cur);
            ps.setString(2, request);

            rs = ps.executeQuery();

            while (rs.next()) {
                starsum += (rs.getInt("num"));
            }

            // Get line count for this app
            ps = conn.prepareStatement("SELECT COUNT(*) AS cn FROM app_feedback WHERE star_question = ? AND app = ? AND stars IS NOT NULL;");
            ps.setString(1, cur);
            ps.setString(2, request);
            rs = ps.executeQuery();

            while (rs.next()) {
                quesCount = (rs.getInt("cn"));
            }

            //calculate average
            sumD = starsum;
            countD = quesCount;
            avg = sumD/countD;

            // for formatting average
            DecimalFormat df2 = new DecimalFormat("#.00");

            JSONObject jsonOb = new JSONObject();
            jsonOb.put("question", cur);
            jsonOb.put("avg", df2.format(avg));
            jsonArray.add(jsonOb);
            starsum = 0;
        }
        close(rs);
        close(stmt);
        return jsonArray;
    }

    //
    // TO BE USED BY OTHER METHODS
    //

    //get data from database with query as array
    private JSONArray getJaByQuery(String query) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        PreparedStatement ps = conn.prepareStatement(query);

        ResultSet rs = ps.executeQuery();
        // Fetch each row from the result set

        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        while(rs.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i=1; i<=numColumns; i++) {
                String column_name = rsmd.getColumnName(i);
                obj.put(column_name, rs.getObject(column_name));
            }
            json.add(obj);
        }

        close(rs);
        close(stmt);

        if(json.isEmpty()){
            throw new RuntimeException("Json is empty");
        }

        return json;
    }

    //get data from database with query as object
    private JSONObject getJoByQuery(String query) throws SQLException {
        JSONArray jsonArray = getJaByQuery(query);

        JSONObject jsonObject = (JSONObject) jsonArray.get(0);

        return jsonObject;
    }

    public Integer selectAppIdFromAppName(String appName) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        PreparedStatement ps = conn.prepareStatement("SELECT id FROM apps WHERE appName = ?");
        ps.setString(1, appName);

        ResultSet rs = ps.executeQuery();

        int id = printAppId(rs);
        close(rs);
        close(stmt);
        return id;
    }

    private Integer printAppId(ResultSet rs) throws SQLException {
        int appId = 0;
        while (rs.next()) {
            appId = rs.getInt("id");
        }
        return appId;
    }


    // for putting JSONObjects into the JSONArray when all columns are needed
    private JSONArray printDB(ResultSet rs) throws SQLException{
        JSONArray jsonArray = new JSONArray();

        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", rs.getInt("feedback_id"));
            jsonObject.put("feedback", rs.getString("feedback"));
            jsonObject.put("category", rs.getString("category"));
            jsonObject.put("time", rs.getString("time"));
            jsonObject.put("device", rs.getString("device"));
            jsonObject.put("os", rs.getString("os"));
            jsonObject.put("app", rs.getString("app"));
            jsonObject.put("image", rs.getString("image"));
            jsonObject.put("features", rs.getString("features"));
            jsonObject.put("stars", rs.getString("stars"));
            jsonObject.put("rating", rs.getString("rating"));
            jsonObject.put("starQuestion", rs.getString("star_question"));

            jsonObject.put("tag", rs.getString("tag"));
            jsonObject.put("template", rs.getString("template"));

            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    private JSONArray printTemplateDB(ResultSet rs) throws SQLException {
        JSONArray jsonArray = new JSONArray();
        while(rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", rs.getInt("Id"));
            jsonObject.put("template", rs.getString("Template"));
            jsonObject.put("featureConfig", rs.getString("FeatureConfig"));
            jsonObject.put("starQuestion", rs.getString("StarQuestion"));
            jsonObject.put("app", rs.getInt("App"));
            jsonObject.put("appName", rs.getString("appName"));

            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    //
    // CLOSING STATEMENT/RESULTSET
    //


    // close Statement
    public void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // close ResultSet
    public void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //
    // MAYBE unnecessary
    //

    // Line count of whole database
    public JSONArray feedbackCount() throws SQLException {

        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS lc FROM app_feedback");
        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("feedbackAmount", rs.getInt("lc"));

            jsonArray.add(jsonObject);
        }
        close(rs);
        close(stmt);
        return jsonArray;
    }

    // Line count of specific os
    public JSONArray osCount(String request) throws SQLException {

        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS lc FROM app_feedback WHERE os LIKE ?");
        ps.setString(1, request);
        ResultSet rs = ps.executeQuery();

        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put( request, rs.getInt("lc"));

            jsonArray.add(jsonObject);
        }
        close(rs);
        close(stmt);
        return jsonArray;
    }

    // Using another solution for now, saving this in case
    // we need a more dynamic approach
    // OS Distribution
    public JSONArray osDist() throws SQLException {

        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT DISTINCT os, count(os) AS CountOf FROM app_feedback Group By os ORDER BY os ASC;");
        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("os", rs.getString("os"));
            jsonObject.put("count", rs.getInt("CountOf"));

            jsonArray.add(jsonObject);
        }
        close(rs);
        close(stmt);
        return jsonArray;
    }


}
