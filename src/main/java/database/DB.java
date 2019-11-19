package database;

import objects.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.*;
import java.util.ArrayList;

public class DB {

    //
    // MAIN QUERIES
    //

    // basic select all
    public JSONArray selectAll() throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM feedback");

        // Fetch each row from the result set
        JSONArray jsonArray = printDB(rs);
        close(rs);
        close(stmt);
        return jsonArray;
    }

    // select all apps from DB
    public JSONArray selectAllAps() throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM apps");

        // Fetch each row from the result set
        JSONArray jsonArray = printAppDB(rs);
        close(rs);
        close(stmt);
        return jsonArray;
    }


    // For POST method
    // (Category should be either "bugreport", "suggestion" or "feedback")
    public void insert(JSONObject jsonObject) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        String smiley = jsonObject.get("smiley").toString();
        String feedback = jsonObject.get("feedback").toString();
        String category = jsonObject.get("category").toString();
        String time = DateTime.now();
        String device = jsonObject.get("device").toString();
        String os = jsonObject.get("os").toString();
        String app = jsonObject.get("app").toString();
        String image = jsonObject.get("image").toString();

        String query = String.format("INSERT INTO feedbacks_feedback" +
                        "(smiley,feedback,category,time,device,os,app,image)" +
                        "VALUES" +
                        "('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s', '%s');",
                smiley, feedback, category, time, device, os, app, image
        );

        stmt.executeUpdate(query);
        close(stmt);
    }

    //const { appName, logoURL, template, password,}
    public void insertAccount(JSONObject jsonObject) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        String appName = jsonObject.get("appName").toString();
        String logoURL = jsonObject.get("logoURL").toString();
        String template = jsonObject.get("template").toString();
        String password = jsonObject.get("password").toString();

        String query = String.format("INSERT INTO feedbacks.apps" +
                        "(appName,logoURL,template,password)" +
                        "VALUES" +
                        "('%s','%s','%s','%s');",
                appName, logoURL, template, password
        );

        stmt.executeUpdate(query);
        close(stmt);
    }

    // (to be used by the queries), for putting JSONObjects into the JSONArray
    private JSONArray printDB(ResultSet rs) throws SQLException{
        JSONArray jsonArray = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        //int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", rs.getInt("id"));
            jsonObject.put("smiley", rs.getString("smiley"));
            jsonObject.put("feedback", rs.getString("feedback"));
            jsonObject.put("category", rs.getString("category"));
            jsonObject.put("time", rs.getString("time"));
            jsonObject.put("device", rs.getString("device"));
            jsonObject.put("os", rs.getString("os"));
            jsonObject.put("app", rs.getString("app"));
            jsonObject.put("image", rs.getString("image"));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    private JSONArray printAppDB(ResultSet rs) throws SQLException {
        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", rs.getInt("id"));
            jsonObject.put("appName", rs.getString("appName"));
            jsonObject.put("logoURL", rs.getString("logoURL"));
            jsonObject.put("template", rs.getString("template"));
            jsonObject.put("password", rs.getString("password"));

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

    // retrieves the app based on the passed id
    public JSONArray selectAppFromId(Integer id) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM apps WHERE id = ?");
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();
        // Fetch each row from the result set
        JSONArray jsonArray = printAppDB(rs);
        close(rs);
        close(stmt);
        return jsonArray;

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

    //
    // OTHER QUERIES
    //

    // Line count of whole database
    public JSONArray feedbackCount() throws SQLException {

        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS lc FROM feedback");
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

    // Amount of all smileys
    public JSONArray smileyCountAll() throws SQLException {

        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT DISTINCT smiley, count(smiley) AS CountOf FROM feedback Group By smiley ORDER BY CountOf ASC;");
        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("SmileyRange", rs.getInt("CountOf"));

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

        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS lc FROM feedback WHERE os LIKE ?");
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

    // Line count of 2 specific os's
    public JSONArray osCountTwo(String os1, String os2) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();

        stmt = conn.createStatement();

        //String android = "android";
        //String ios = "ios";

        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS lc FROM feedback WHERE os LIKE ?");
        PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) AS lc2 FROM feedback WHERE os LIKE ?");

        ps.setString(1, "%" + os1 + "%");
        ps2.setString(1, "%" + os2 + "%");

        ResultSet rs = ps.executeQuery();

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        while (rs.next()) {
            jsonObject.put("os", os1);
            jsonObject.put("count", rs.getInt("lc"));
        }
        rs = ps2.executeQuery();

        while (rs.next()) {
            jsonObject2.put("os", os2);
            jsonObject2.put("count", rs.getInt("lc2"));
        }

        jsonArray.add(jsonObject);
        jsonArray.add(jsonObject2);

        close(rs);
        close(stmt);
        return jsonArray;
    }

    // Line count of desired smiley value (1-10)
    public JSONArray smileyCount(String request) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS lc FROM feedback WHERE smiley = ?");
        ps.setString(1, request);
        ResultSet rs = ps.executeQuery();

        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("line count for smiley "+request, rs.getInt("lc"));

            jsonArray.add(jsonObject);
        }
        close(rs);
        close(stmt);
        return jsonArray;
    }

    // Specific Id
    public JSONArray theId(String request) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM feedback  WHERE id = ?");
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
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY time ASC");
        } else{
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY time DESC");
        }

        JSONArray jsonArray = printDB(rs);
        close(rs);
        close(stmt);
        return jsonArray;
    }

    public JSONArray feedbacksPerMonth() throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs;
        rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY time ASC");


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

    //  Sort by device
    public JSONArray device(String request) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs;

        if (request.equals("asc")) {
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY device ASC");
        } else{
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY device DESC");
        }

        JSONArray jsonArray = printDB(rs);
        close(rs);
        close(stmt);
        return jsonArray;
    }

    //  Sort by app
    public JSONArray app(String request) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs;

        if (request.equals("asc")) {
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY app ASC");
        } else{
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY app DESC");
        }

        JSONArray jsonArray = printDB(rs);
        close(rs);
        close(stmt);
        return jsonArray;
    }

    // Sort smileys by ascending or descending OR see only feedbacks with a specific smiley value
    public JSONArray smiley(String request) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();

        stmt = conn.createStatement();

        ResultSet rs;

        if (request.equals("asc")){
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY smiley ASC");

            JSONArray jsonArray = printDB(rs);
            return jsonArray;

        } else if (request.equals("desc")){
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY smiley DESC");

            JSONArray jsonArray = printDB(rs);

            return jsonArray;

        } else {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM feedback WHERE smiley = ?");
            ps.setString(1, request);
            rs = ps.executeQuery();

            JSONArray jsonArray = printDB(rs);

            close(rs);
            close(stmt);
            return jsonArray;
        }
    }

    // total line count as integer
    public int lineCount() throws SQLException {

        int linecount = 0;
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS lc FROM feedback");
        while (rs.next()) {
            linecount = rs.getInt("lc");
        }
        close(rs);
        close(stmt);
        return linecount;
    }

    // Only feedback column for analysis purposes
    public JSONArray onlyFB() throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT feedback FROM feedback");
        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            jsonArray.add(rs.getString("feedback"));
        }
        close(rs);
        close(stmt);
        return jsonArray;
    }

    // Category distribution
    public JSONArray catDistr() throws SQLException {

        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();


        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();


        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS lc FROM feedback WHERE category = 'feedback'");

        while (rs.next()) {
            jsonObject.put("feedback", rs.getInt("lc"));
        }

        rs = stmt.executeQuery("SELECT COUNT(*) AS lc2 FROM feedback WHERE category = 'bugreport'");
        while (rs.next()) {
            jsonObject.put("bugreport", rs.getInt("lc2"));
        }

        rs = stmt.executeQuery("SELECT COUNT(*) AS lc3 FROM feedback WHERE category = 'suggestion'");
        while (rs.next()) {
            jsonObject.put("suggestion", rs.getInt("lc3"));
        }

        jsonArray.add(jsonObject);
        close(rs);
        close(stmt);
        return jsonArray;
    }


    // average grade per app
    public JSONArray avgPerApp() throws SQLException {
        int sum = 0, max = 0, appCount = 0, thisInt = 0;
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
        ResultSet rs = stmt.executeQuery("SELECT DISTINCT app FROM feedback ORDER BY app");

        while (rs.next()) {

            apps.add(rs.getString("app"));
            max++;
        }

        // For every app in the array list, check the smileys, add them to variable sum
        for (int i = 0; i < max; i++){
            cur = apps.get(i);
            PreparedStatement ps = conn.prepareStatement("SELECT smiley AS num FROM feedback WHERE app = ?");
            ps.setString(1, cur);
            rs = ps.executeQuery();

            while (rs.next()) {
                sum += (rs.getInt("num"));
            }

            // Get line count for this app
            ps = conn.prepareStatement("SELECT COUNT(*) AS cn FROM feedback WHERE app = ? AND smiley IS NOT NULL");
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
            jsonOb.put("app", cur);
            jsonOb.put("avg", newAvg);
            jsonArray.add(jsonOb);
            sum = 0;
        }
        close(rs);
        close(stmt);
        return jsonArray;
    }

    //
    // FOR SPECIFIC APP INFO
    //

    // basic select all for a specific app
    public JSONArray selectAllAPP(String request) throws SQLException {
        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM feedback WHERE app = ?");
        ps.setString(1, request);
        ResultSet rs = ps.executeQuery();

        // Fetch each row from the result set
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
        PreparedStatement ps = conn.prepareStatement("DELETE FROM feedback WHERE feedback_id = ?");
        ps.setInt(1, request);
        Integer result = ps.executeUpdate();
        close(stmt);
        return result;
    }

    // OS Distribution
    public JSONArray osDist() throws SQLException {

        Statement stmt;
        Connection conn = DBConnection.connection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT DISTINCT os, count(os) AS CountOf FROM feedback Group By os ORDER BY os ASC;");
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

}
