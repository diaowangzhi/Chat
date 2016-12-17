package dwz.edu.sdu.qq_chat.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.OfflineMessageManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dwz.edu.sdu.qq_chat.R;

import static org.jivesoftware.smackx.filetransfer.FileTransfer.Error.connection;

public class LoginActivity extends AppCompatActivity {

    EditText name, pwd;
    TextView register;
    Button login;
    SharedPreferences sp;
    String username, password;
    AlertDialog.Builder dialog;
    View read;
    Handler handler1;
    ChatManagerListener messageListener;
    ChatManager chatManager;
    private AccountManager accountManager;
    Map<String,ArrayList<String>> offlineMsgs;
    static int messagenum;
    static  String me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //  MyApplication.connectionConfiguration config = new MyApplication.connectionConfiguration("10.0.2.2", 5222);
        //  MyApplication.connectionConfiguration config = new MyApplication.connectionConfiguration("127.0.0.1", 5222);
        //  MyApplication.connectionConfiguration config = new MyApplication.connectionConfiguration("121.250.213.178", 5222);
        //    config.setServiceName("diaowz.com");   //还可以设置很多其他属性，如隐身登陆
      /*  config.setSASLAuthenticationEnabled(false);
        config.setSecurityMode(MyApplication.connectionConfiguration.SecurityMode.disabled);//设置为disabled
        config.setCompressionEnabled(false);
        config.setReMyApplication.connectionAllowed(true);
         MyApplication.conn = new XMPPMyApplication.connection(config);
         */

        dialog = new AlertDialog.Builder(this);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        name = (EditText) findViewById(R.id.name);
        pwd = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.register);
        read = findViewById(R.id.read);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        //sp = PreferenceManager.getDefaultSharedPreferences(this);
        //  username = sp.getString("username", "");

        if (!"".equals(username)) {
            name.setText(username);
        }

        //   if (!"".equals(password)) {
        //       pwd.setText(password);
        //   }

        if (read.isSelected()) {
            login.setClickable(true);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   SharedPreferences.Editor editor = sp.edit();
                //  editor.putString("username", name.getText().toString());
                //   editor.putString("password", pwd.getText().toString());
                //   editor.apply();
                username = name.getText().toString();
                password = pwd.getText().toString();
                if (username == null || username.equals("")) {

                    dialog.setTitle("提示");

                    dialog.setMessage("用户名不能为空");

                    dialog.setPositiveButton("确定", null);

                    dialog.show();
                    name.requestFocus();
                } else if (password == null || password.equals("")) {

                    dialog.setTitle("提示");

                    dialog.setMessage("密码不能为空");

                    dialog.setPositiveButton("确定", null);

                    dialog.show();
                    pwd.requestFocus();
                }
                if (!MyApplication.conn.isConnected()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //这里写入子线程需要做的工作


                            /** 创建MyApplication.connection链接 */
                            try {
                                MyApplication.conn.connect();
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                while (!MyApplication.conn.isConnected()) {

                }
                if (MyApplication.conn.isConnected()) {


                    try {
                       // Connection_of.config.setReconnectionAllowed(true);
                        Connection_of.config.setSendPresence(false);//不要告诉服务器自己的状态
                    //    Connection connection = new XMPPConnection(connectionConfig);

                           // connection.connect();// 开启连接
                            accountManager = MyApplication.conn.getAccountManager();// 获取账户管理类

                        MyApplication.conn.login(username, password);
                        OfflineMessageManager offlineManager = new OfflineMessageManager(MyApplication.conn);
                        try {
                            Iterator<org.jivesoftware.smack.packet.Message> it = offlineManager.getMessages();

                            System.out.println(offlineManager.supportsFlexibleRetrieval());
                            messagenum = offlineManager.getMessageCount();
                            System.out.println("离线消息数量: " + offlineManager.getMessageCount());

                           offlineMsgs = new HashMap<String,ArrayList<String>>();

                            while (it.hasNext()) {
                                org.jivesoftware.smack.packet.Message message = it.next();
                                System.out
                                        .println("收到离线消息, Received from 【" + message.getFrom()
                                                + "】 message: " + message.getBody());
                                String fromUser = message.getFrom().split("/")[0];

                                if (offlineMsgs.containsKey(fromUser)) {
                                    offlineMsgs.get(fromUser).add(message.getBody());
                                } else {
                                    ArrayList<String> temp = new ArrayList<String>();
                                    temp.add(message.getBody());
                                    offlineMsgs.put(fromUser, temp);
                                }

                               // offlineMsgs.put(fromUser,message.getBody());
                                   // MyApplication.MessageList.get(MyApplication.userList.indexOf(fromUser)).add(MyApplication.friendsList.get(MyApplication.userList.indexOf(fromUser))+":"+message.getBody());

                            }

                            // 在这里进行处理离线消息集合......
                         //   Set<String> keys = offlineMsgs.keySet();
                          //  Iterator<String> offIt = keys.iterator();
                        //    while (offIt.hasNext()) {
                          //      String key = offIt.next();
                         //       ArrayList<Message> ms = offlineMsgs.get(key);

                          //      for (int i = 0; i < ms.size(); i++) {
                          //          System.out.println("-->" + ms.get(i));
                          //      }
                          //  }

                            offlineManager.deleteMessages();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        offlineManager.deleteMessages();//删除所有离线消息
                        Presence presence = new Presence(Presence.Type.available);
                        MyApplication.conn.sendPacket(presence);//上线了
                       // MyApplication.conn.login(username, password);
                        chatManager = MyApplication.conn.getChatManager();
                        Collection<RosterEntry> allrosters = MyApplication.conn.getRoster().getEntries();
                        for (RosterEntry rosterEntry : allrosters) {
                            //此处可获取用户 的JID
                            System.out.print("name: " + rosterEntry.getName() + "jid: " + rosterEntry.getUser());
                            if (rosterEntry.getName() == null) {
                                MyApplication.friendsList.add(rosterEntry.getUser());
                                System.out.println(rosterEntry.getUser());
                            } else {
                                MyApplication.friendsList.add(rosterEntry.getName());
                                System.out.println(rosterEntry.getName());
                            }
                            MyApplication.userList.add(rosterEntry.getUser());
                            System.out.println(rosterEntry.getUser());
                            System.out.println("");
                           // List<String> messagelist=;

                            MyApplication.MessageList.add(new ArrayList<String>());

                        }

                           Set<String> keys = offlineMsgs.keySet();
                          Iterator<String> offIt = keys.iterator();
                            while (offIt.hasNext()) {
                              String key = offIt.next();
                             ArrayList<String> ms = offlineMsgs.get(key);
                                if(MyApplication.userList.contains(key))
                                {
                                    System.out.println(key);
                                   // System.out.println(ms);
                                    for (int i = 0; i < ms.size(); i++) {
                                        MyApplication.MessageList.get(MyApplication.userList.indexOf(key)).add(key+":"+ms.get(i));
                                    }
                                  //  MyApplication.MessageList.get(MyApplication.userList.indexOf(key)).add(key+":"+ms);
                                }
                          }


                        handler1 = new Handler(){
                            /* handleMessage主线程接收子线程发送过来的消息 */
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                System.out.println("Handler1");
                                MyApplication.MessageList.get(msg.what).add(MyApplication.friendsList.get(msg.what)+":"+(String)msg.obj);
                                Toast.makeText(LoginActivity.this,MyApplication.friendsList.get(msg.what)+"向你发了一条消息",Toast.LENGTH_SHORT).show();

                            }
                        };




                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("Message Thread");
                                messageListener = new ChatManagerListener() {
                                    @Override
                                    public void chatCreated(Chat chat, boolean b) {
                                        chat.addMessageListener(new MessageListener(){
                                            public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                                                String messageBody = message.getBody();
                                                if(!messageBody.isEmpty()) {
                                                    Message message1 = Message.obtain(handler1);
                                                    message1.what =MyApplication.userList.indexOf(message.getFrom().substring(0,message.getFrom().indexOf("/")));
                                                    message1.obj=message.getBody().toString();
                                                    me = message.getFrom().substring(0,message.getFrom().indexOf("/"));

                                                    message1.sendToTarget();

                                                }


                                            }
                                        });
                                    }

                                };
                                chatManager.addChatListener(messageListener);


                            }
                        }).start();


                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, NewsActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (XMPPException | IllegalStateException e) {
                       // Toast.makeText(LoginActivity.this, "该用户已登录", Toast.LENGTH_SHORT).show();
                        System.out.println(e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage() + " " + e.getClass().toString(), Toast.LENGTH_SHORT).show();
                        if(e.getMessage().equals("not-authorized(401)"))
                        {
                        Toast.makeText(LoginActivity.this,"用户名不存在或密码不正确", Toast.LENGTH_LONG).show();
                        }
                        if(e.getMessage().equals("Already logged in to server."))
                        {
                            Toast.makeText(LoginActivity.this,"该用户名已登录", Toast.LENGTH_LONG).show();
                        }

                    }



                } else {
                    Looper.prepare();
                    dialog.setTitle("提示");

                    dialog.setMessage("服务器连接失败");

                    dialog.setPositiveButton("确定", null);

                    dialog.show();
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
   /*     try {

            MyApplication.connection = new XMPPMyApplication.connection("admin@172.0.0.1");
            MyApplication.connection.MyApplication.connect();
        }
        catch (Exception e)
        {

        }
        */


    }
}
