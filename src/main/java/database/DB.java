package database;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
        dataSource.setUser("root");
        dataSource.setPassword("my24I");
        dataSource.setServerName("myDBHost.example.org");
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

    public String selectAll() throws SQLException {
        open();
        String query = "SELECT * FROM entry";
        rs = stmt.executeQuery(query);
        String result = rs.toString();
        close();
        return result;
    }

    


}
