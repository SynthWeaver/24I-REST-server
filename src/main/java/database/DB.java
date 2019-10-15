package database;

import com.mysql.cj.jdbc.MysqlDataSource;
import objects.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.sql.*;

public class DB {

    private MysqlDataSource dataSource = new MysqlDataSource();
    private Connection conn ;
    private Statement stmt;
    ResultSet rs ;

    public DB(){
        dataSource.setURL(
                "jdbc:mysql://localhost/feedbacks?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
        );
        dataSource.setUser("root");
        dataSource.setPassword("Team24iDB");
    }

    private void open() throws SQLException {
        conn = dataSource.getConnection();
        stmt = conn.createStatement();

        //set a result set or you cant close it later
        rs = stmt.executeQuery("SELECT * FROM feedback WHERE id=-1");
        rs.close();
    }

    private void close() throws SQLException {
        rs.close();
        stmt.close();
        conn.close();
    }

    public JSONArray selectAll() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM feedback");

        // Fetch each row from the result set
        JSONArray jsonArray = printDB();
        close();
        return jsonArray;
    }

    // Category has to be either "bugreport", "suggestion" or "feedback"
    public void insert(JSONObject jsonObject) throws SQLException {
        open();

        String smiley = jsonObject.get("smiley").toString();
        String feedback = jsonObject.get("feedback").toString();
        String category = jsonObject.get("category").toString();
        String time = DateTime.now();
        String device = jsonObject.get("device").toString();
        String os = jsonObject.get("os").toString();
        String app = jsonObject.get("app").toString();
        String image = jsonObject.get("image").toString();

        String query = String.format("INSERT INTO feedbacks.feedback" +
                "(smiley,feedback,category,time,device,os,app,image)" +
                "VALUES" +
                "(%s,'%s','%s','%s','%s','%s','%s','%s');",
                smiley, feedback, category, time, device, os, app, image
                );

        stmt.executeUpdate(query);
        close();
    }

    // Line count of whole database
    public JSONArray feedbackCount() throws SQLException {

        open();
        rs = stmt.executeQuery("SELECT COUNT(*) AS lc FROM feedback");
        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("feedbacks in the database", rs.getInt("lc"));

            jsonArray.add(jsonObject);
        }
        close();
        return jsonArray;
    }

    // Line count of specific os
    public JSONArray osCount(String request) throws SQLException {

        open();

        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS lc FROM feedback WHERE os = ?");
        ps.setString(1, request);
        rs = ps.executeQuery();

        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("line count os "+request, rs.getInt("lc"));

            jsonArray.add(jsonObject);
        }
        close();
        return jsonArray;
    }

    // Line count of desired smiley value (1-10)
    public JSONArray smileyCount(String request) throws SQLException {
        open();

        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS lc FROM feedback WHERE smiley = ?");
        ps.setString(1, request);
        rs = ps.executeQuery();

        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("line count for smiley "+request, rs.getInt("lc"));

            jsonArray.add(jsonObject);
        }
        close();
        return jsonArray;
    }

    // Specific Id
    public JSONArray theId(String request) throws SQLException {
        open();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM feedback  WHERE id = ?");
        ps.setString(1, request);
        rs = ps.executeQuery();

        JSONArray jsonArray = printDB();

        close();
        return jsonArray;
    }

    // Sort by time
    public JSONArray time(String request) throws SQLException {
        open();

        if (request.equals("asc")) {
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY time ASC");
        } else{
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY time DESC");
        }

        JSONArray jsonArray = printDB();

        close();
        return jsonArray;
    }

    //  Sort by device
    public JSONArray device(String request) throws SQLException {
        open();

        if (request.equals("asc")) {
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY device ASC");
        } else{
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY device DESC");
        }

        JSONArray jsonArray = printDB();

        close();
        return jsonArray;
    }

    //  Sort by app
    public JSONArray app(String request) throws SQLException {
        open();

        if (request.equals("asc")) {
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY app ASC");
        } else{
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY app DESC");
        }

        JSONArray jsonArray = printDB();

        close();
        return jsonArray;
    }

    public JSONArray smiley(String request) throws SQLException {
        open();

        if (request.equals("asc")){
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY smiley ASC");

            JSONArray jsonArray = printDB();
            close();
            return jsonArray;

        } else if (request.equals("desc")){
            rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY smiley DESC");

            JSONArray jsonArray = printDB();

            close();
            return jsonArray;

        } else {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM feedback WHERE smiley = ?");
            ps.setString(1, request);
            rs = ps.executeQuery();

            JSONArray jsonArray = printDB();

            close();
            ps.close();
            return jsonArray;
        }
    }
    private JSONArray printDB() throws SQLException{
        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", rs.getInt("id"));
            jsonObject.put("smiley", rs.getInt("smiley"));
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
}
