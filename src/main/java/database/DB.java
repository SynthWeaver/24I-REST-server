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


    // Sort by time, old to new
    public JSONArray timeAsc() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY time");

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

    // Sort by time, new to old
    public JSONArray timeDesc() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY time DESC");

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

    //  Sort by smiley, ascending
    public JSONArray smileyAsc() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY smiley");

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

    //  Sort by smiley, descending
    public JSONArray smileyDesc() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY smiley DESC");

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

    //  Sort by smiley, ascending
    public JSONArray deviceAsc() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY device");

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

    //  Sort by device, descending
    public JSONArray deviceDesc() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM feedback ORDER BY device DESC");

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

    //  Only smileys "1"
    public JSONArray smiley1() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM feedback WHERE smiley = 1");

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

    //  Only smileys "2"
    public JSONArray smiley2() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM feedback WHERE smiley = 2");

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

    //  Only smileys "3"
    public JSONArray smiley3() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM feedback WHERE smiley = 3");

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

    //  Only smileys "4"
    public JSONArray smiley4() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM feedback WHERE smiley = 4");

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

    //  Only smileys "5"
    public JSONArray smiley5() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM feedback WHERE smiley = 5");

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




}
