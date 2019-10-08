package database;

import com.mysql.cj.jdbc.MysqlDataSource;
import objects.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", rs.getInt("id"));
            jsonObject.put("smiley", rs.getInt("smiley"));
            jsonObject.put("feedback", rs.getString("feedback"));
            jsonObject.put("time", rs.getString("time"));
            jsonObject.put("device", rs.getString("device"));
            jsonObject.put("os", rs.getString("os"));
            jsonObject.put("app", rs.getString("app"));
            jsonObject.put("image", rs.getString("image"));

            jsonArray.add(jsonObject);
        }
        close();
        return jsonArray;
    }

    public JSONArray sortBy() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY time");

        // Fetch each row from the result set
        JSONArray jsonArray1 = new JSONArray();
        while (rs.next()) {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id", rs.getInt("id"));
            jsonObject1.put("smiley", rs.getInt("smiley"));
            jsonObject1.put("feedback", rs.getString("feedback"));
            jsonObject1.put("time", rs.getString("time"));
            jsonObject1.put("device", rs.getString("device"));
            jsonObject1.put("os", rs.getString("os"));
            jsonObject1.put("app", rs.getString("app"));
            jsonObject1.put("image", rs.getString("image"));

            jsonArray1.add(jsonObject1);
        }
        close();
        return jsonArray1;
    }

    public void insert(JSONObject jsonObject) throws SQLException {
        open();

        String smiley = jsonObject.get("smiley").toString();
        String feedback = jsonObject.get("feedback").toString();
        String time = DateTime.now();
        String device = jsonObject.get("device").toString();
        String os = jsonObject.get("os").toString();
        String app = jsonObject.get("app").toString();
        String image = jsonObject.get("image").toString();

        String query = String.format("INSERT INTO feedbacks.feedback" +
                "(smiley,feedback,time,device,os,app,image)" +
                "VALUES" +
                "(%s, '%s','%s','%s','%s','%s','%s');",
                smiley, feedback, time, device, os, app, image
                );

        stmt.executeUpdate(query);
        close();
    }
}
//  Sort by smiley, ascending:
//         rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY smiley");

// Sort by smiley, descending:
//        rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY smiley DESC");

// Oldest to newest
//         rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY time");

// Newest to oldest
//         rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY time DESC");

//         rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY device");
