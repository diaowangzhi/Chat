package dwz.edu.sdu.qq_chat.ui.activity;

import android.app.Application;

import org.jivesoftware.smack.Connection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2016/12/5.
 */

public class MyApplication extends Application {
    public static Connection_of connection_of;
    public static Connection conn;
    public static List<String> friendsList, userList;
    public static List<ArrayList<String>> MessageList;
    //public static String Ip = "121.250.213.130";
    //public static String Ip = "10.0.2.2";
    public static String Ip = "211.87.229.13";
    @Override
    public void onCreate() {
        super.onCreate();
        connection_of = new Connection_of();
        conn = connection_of.getConn();
        friendsList = new ArrayList<String>();
        userList = new ArrayList<String>();
        MessageList = new ArrayList<ArrayList<String>>();
    }
}
