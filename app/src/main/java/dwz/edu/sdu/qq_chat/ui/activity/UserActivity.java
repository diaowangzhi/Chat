package dwz.edu.sdu.qq_chat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Delayed;

import dwz.edu.sdu.qq_chat.R;


public class UserActivity extends AppCompatActivity {

    TextView title;
    Button send,return1;
    EditText send_frame;
    String message;
    Handler handler2;
    ChatManagerListener messageListener1;
    ChatManager chatManager;
    AlertDialog.Builder  dialog;
    Chat newChat;
    MessageListener messageListener;
    ListView chatMessage;
    ArrayAdapter<String> adapter;
    private  static List<String> mrecord;
    String username,nickname;
    private String response, acceptAdd, alertName, alertSubName;
    private Roster roster;
    private UserActivity.MyReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        dialog = new AlertDialog.Builder(this);
        mrecord = new ArrayList<String>();
        title = (TextView) findViewById(R.id.title);
        send = (Button) findViewById(R.id.send);
        return1 = (Button) findViewById(R.id.return1);
        send_frame = (EditText) findViewById(R.id.send_frame);
        send_frame.clearFocus();
        chatMessage = (ListView) findViewById(R.id.chat_line);
        roster = MyApplication.conn.getRoster();
        Intent intent1 = getIntent();

        username = intent1.getStringExtra("username").toString();
        nickname = intent1.getStringExtra("nickname");
        title.setText(nickname);
       adapter = new ArrayAdapter<String>(UserActivity.this, android.R.layout.simple_list_item_1, MyApplication.MessageList.get(MyApplication.userList.indexOf(username)));
        chatMessage.setAdapter(adapter);
       // ConnectionConfiguration config = new ConnectionConfiguration("127.0.0.1", 5222);
      //  ConnectionConfiguration config = new ConnectionConfiguration("121.250.213.178", 5222);
       // ConnectionConfiguration config = new ConnectionConfiguration("10.0.2.2", 5222);
        //    config.setServiceName("diaowz.com");   //还可以设置很多其他属性，如隐身登陆
      /*(  config.setSASLAuthenticationEnabled(false);
        config.setReconnectionAllowed(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
        config.setTruststorePath("/system/etc/security/cacerts.bks");
        config.setTruststorePassword("changeit");
        config.setTruststoreType("bks");
        conn = new XMPPConnection(config);
        */
        if (!MyApplication.conn.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //这里写入子线程需要做的工作


                    /** 创建connection链接 */
                    try {
                       MyApplication.conn.connect();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }

       while (!MyApplication.conn.isConnected()) {
          // System.out.println("waiting for connection");
       }


        if (MyApplication.conn.isConnected()) {
            chatManager = MyApplication.conn.getChatManager();
            receiver = new UserActivity.MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("dwz.edu.sdu.qq_chat.ui.activity.UserActivity");
           // unregisterReceiver(receiver);
           // registerReceiver(receiver, intentFilter);
            PacketFilter filter = new AndFilter(new PacketTypeFilter(Presence.class));
            //packet监听器
            PacketListener listener = new PacketListener() {

                @Override
                public void processPacket(Packet packet) {
                    System.out.println("PresenceService-" + packet.toXML());
                    if (packet instanceof Presence) {
                        Presence presence = (Presence) packet;
                        String from = presence.getFrom();//发送方
                        String to = presence.getTo();//接收方
                        if (presence.getType().equals(Presence.Type.subscribe)) {
                            System.out.println("收到添加请求！");
                            //发送广播传递发送方的JIDfrom及字符串
                            acceptAdd = "收到添加请求！";
                            Intent intent = new Intent();
                            intent.putExtra("fromName", from);
                            intent.putExtra("acceptAdd", acceptAdd);
                            intent.setAction("dwz.edu.sdu.qq_chat.ui.activity.UserActivity");
                            sendBroadcast(intent);
                        } else if (presence.getType().equals(
                                Presence.Type.subscribed)) {
                            //发送广播传递response字符串
                            response = "恭喜，对方同意添加好友！";
                            Intent intent = new Intent();
                            intent.putExtra("response", response);
                            intent.setAction("dwz.edu.sdu.qq_chat.ui.activity.UserActivity");
                            sendBroadcast(intent);
                        } else if (presence.getType().equals(
                                Presence.Type.unsubscribe)) {
                            //发送广播传递response字符串
                            response = "抱歉，对方拒绝添加好友，将你从好友列表移除！";
                            Intent intent = new Intent();
                            intent.putExtra("response", response);
                            intent.setAction("dwz.edu.sdu.qq_chat.ui.activity.UserActivity");
                            sendBroadcast(intent);
                        } else if (presence.getType().equals(
                                Presence.Type.unsubscribed)) {
                        } else if (presence.getType().equals(
                                Presence.Type.unavailable)) {
                            System.out.println("好友下线！");
                        } else {
                            System.out.println("好友上线！");
                        }
                    }
                }
            };
            //添加监听
            MyApplication.conn.addPacketListener(listener, filter);


           /* String mes=intent1.getStringExtra("message");
            if(mes!=null){
                mrecord.add(nickname+":"+mes);
                adapter.notifyDataSetChanged();
                chatMessage.invalidate();
            }
            */

            handler2 = new Handler(){
                /* handleMessage主线程接收子线程发送过来的消息 */
                @Override
                public void handleMessage(android.os.Message msg) {
                    super.handleMessage(msg);
                    System.out.println("Handler2");
                   if(msg.what==0)
                   {
                       adapter.notifyDataSetChanged();
                   }


                }
            };




            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Message Thread");
                    messageListener1 = new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean b) {
                            chat.addMessageListener(new MessageListener(){
                                public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                                    String messageBody = message.getBody();
                                    if(!messageBody.isEmpty()) {
                                        android.os.Message message2 = android.os.Message.obtain(handler2);
                                        message2.what =0;
                                        message2.obj=adapter;
                                        message2.sendToTarget();

                                    }


                                }
                            });
                        }

                    };
                    chatManager.addChatListener(messageListener1);


                }
            }).start();


          /* messageListener = new MessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    String messageBody = message.getBody();
                    System.out.println("收到信息：" + messageBody + " "
                           + message.getFrom());
                    if(!messageBody.isEmpty()) {
                        //mrecord.clear();
                       // mrecord.add(nickname+":"+messageBody);
                        adapter.notifyDataSetChanged();
                        chatMessage.invalidate();




                    }


                }
            };*/





            newChat = MyApplication.conn.getChatManager().createChat(username, messageListener);
            System.out.println("chat started");

            return1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(UserActivity.this, RelativeActivity.class);
                    //unregisterReceiver(receiver);
                    startActivity(intent);
                    finish();
                }
            });
            send_frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send_frame.requestFocus();
                }
            });

            //    if (message != null || !message.equals("")) {
            //         send.setClickable(true);
            //     }

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    message = send_frame.getText().toString();
                    System.out.println(message);
                    if(message==null||message.isEmpty())
                        System.out.println("message is null");
                    else
                   // if(message != null || !message.equals(""))
                    {
                        try {
                            newChat.sendMessage(message);
                           // mrecord.clear();
                            MyApplication.MessageList.get(MyApplication.userList.indexOf(username)).add("本机:      "+message);
                            send_frame.clearFocus();
                            chatMessage.requestFocus();
                            chatMessage.clearFocus();
                            //send_frame.requestFocus();
                            adapter.notifyDataSetChanged();
                            chatMessage.invalidate();
                            send_frame.setText("");
                            //   send.setClickable(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

            });
            }




    }
    //广播接收器
    public class MyReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            //接收传递的字符串response
            Bundle bundle = intent.getExtras();
            response = bundle.getString("response");
            System.out.println("广播收到" + response);
            if (response == null) {
                //获取传递的字符串及发送方JID
                acceptAdd = bundle.getString("acceptAdd");
                alertName = bundle.getString("fromName");
                if (alertName != null) {
                    //裁剪JID得到对方用户名
                    alertSubName = alertName.substring(0, alertName.indexOf("@"));
                }
                if (acceptAdd.equals("收到添加请求！")) {
                    //弹出一个对话框，包含同意和拒绝按钮
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                    builder.setTitle("添加好友请求");
                    builder.setMessage("用户" + alertSubName + "请求添加你为好友");
                    builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                        //同意按钮监听事件，发送同意Presence包及添加对方为好友的申请
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            Presence presenceRes = new Presence(Presence.Type.subscribed);
                            presenceRes.setTo(alertName);
                            MyApplication.conn.sendPacket(presenceRes);
                            System.out.println(alertName);
                            Collection<RosterEntry> allrosters = MyApplication.conn.getRoster().getEntries();
                            boolean have = false;
                            for (RosterEntry rosterEntry : allrosters) {
                                //此处可获取用户 的JID
                                System.out.print("name: " + rosterEntry.getName() + "jid: " + rosterEntry.getUser());
                                if (rosterEntry.getUser().equals(alertName + "@" + MyApplication.conn.getServiceName())) {
                                    have = true;
                                }
                            }
                            if (!have) {
                                try {
                                    roster.createEntry(alertName, null, null);
                                } catch (XMPPException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                    builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        //拒绝按钮监听事件，发送拒绝Presence包

                        public void onClick(DialogInterface dialog, int arg1) {
                            Presence presenceRes = new Presence(Presence.Type.unsubscribe);
                            presenceRes.setTo(alertName);
                            MyApplication.conn.sendPacket(presenceRes);
                        }
                    });
                    builder.show();
                }
            }

        }
    }

}
