package dwz.edu.sdu.qq_chat.ui.activity;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by john on 2016/12/5.
 */

public class Connection_of {

    Connection conn;
    public static ConnectionConfiguration config;
    private Thread thread;

    public Connection_of() {


    }

    public Connection getConn() {
        config = new ConnectionConfiguration(MyApplication.Ip, 5222);
        //    config.setServiceName("diaowz.com");   //还可以设置很多其他属性，如隐身登陆
        config.setSASLAuthenticationEnabled(false);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//设置为disabled
        config.setCompressionEnabled(false);
        config.setReconnectionAllowed(true);
        conn = new XMPPConnection(config);
        if (!conn.isConnected()) {

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        conn.connect();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        return conn;
    }


}
