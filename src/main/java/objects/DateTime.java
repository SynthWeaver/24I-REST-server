package objects;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTime {

    private DateTime(){}

    public static String now(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }
}