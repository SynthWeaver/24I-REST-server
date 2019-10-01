package database;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {

    private MysqlDataSource dataSource = new MysqlDataSource();
    private Connection conn;
    private Statement stmt;
    ResultSet rs;

    public DB(){
        dataSource.setURL(
                "jdbc:mysql://localhost/feedbacks?serverTimezone=TimeZone&useLegacyDatetimeCode=false"
        );
        dataSource.setUser("root");
        dataSource.setPassword("Team24iDB");
    }

    private void open() throws SQLException {
        conn = dataSource.getConnection();
        stmt = conn.createStatement();
    }

    private void close() throws SQLException {
        rs.close();
        stmt.close();
        conn.close();
    }

    public JSONArray selectAll() throws SQLException {
        open();
        rs = stmt.executeQuery("SELECT * FROM entry");

        // Fetch each row from the result set
        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", rs.getInt("id"));
            jsonObject.put("smiley", rs.getInt("smiley"));
            jsonObject.put("feedback", rs.getString("comment"));
            jsonObject.put("time", rs.getString("time"));
            jsonObject.put("device", rs.getString("device"));

            jsonArray.add(jsonObject);
        }
        close();
        return jsonArray;
    }




}
