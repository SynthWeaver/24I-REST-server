package database;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

    private static Connection con = null;

    private static MysqlDataSource dataSource = new MysqlDataSource();

    static {
        dataSource.setURL(
                "jdbc:mysql://localhost/feedbacks?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
        );
        dataSource.setUser("root");
        dataSource.setPassword("Team24iDB");
    }

    public static Connection connection() throws SQLException {
        // Wenn es bisher keine Conncetion zur DB gab, ...
        if (con == null) {
            con = dataSource.getConnection();
        }
        // Zurückgegeben der Verbindung
        return con;
    }
}
