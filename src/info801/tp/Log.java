package info801.tp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Log {

    private static String getCurrentTime(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void write(String whoAmI, String message){
        System.out.println("At " + getCurrentTime() + " -> " + whoAmI + " " + message);
    }
}

