package database;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

    private static Connection con = null;

    private static MysqlDataSource dataSource = new MysqlDataSource();

    static {
        dataSource.setURL(
                "jdbc:mysql://remotemysql.com/1WKvtfAKZ1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
        );
        //url remotemysql.com
        //password Team24iDB
        dataSource.setUser("1WKvtfAKZ1");
            dataSource.setPassword("KzGbcrkuv4");
    }

    public static Connection connection() throws SQLException {
        if (con == null) {
            con = dataSource.getConnection();
        }
        return con;
    }
}
