package dwz.edu.sdu.qq_chat.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.util.Collection;
import java.util.Iterator;

import dwz.edu.sdu.qq_chat.R;

import static org.jivesoftware.smackx.pubsub.AccessModel.roster;

public class AddFriendsActivity extends AppCompatActivity {


    Button add;
    EditText username,nickname;
    AlertDialog.Builder dialog;
    Activity activity = this;
    private static ProgressDialog dialog1;
    TextView returnq;

    private String name,password,response,acceptAdd,alertName,alertSubName;
    private ImageView img_searchFriend,img_addFriend;
    private TextView text_searchFriend,text_response;
    private Roster roster;
    private String user,nick;
    private MyReceiver receiver;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        add = (Button) findViewById(R.id.add);
        username = (EditText) findViewById(R.id.addFriend);
        nickname = (EditText) findViewById(R.id.nickname);
        returnq = (TextView) findViewById(R.id.return3);
        dialog = new AlertDialog.Builder(this);
        roster = MyApplication.conn.getRoster();
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);

        returnq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AddFriendsActivity.this, NewsActivity.class);
                startActivity(intent);
              //  unregisterReceiver(receiver);
                finish();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                        user = username.getText().toString();
                                       nick = nickname.getText().toString();
                                       if (user == null || user.equals("")) {
                                           dialog.setTitle("提示");

                                           dialog.setMessage("用户名不能为空");

                                           dialog.setPositiveButton("确定", null);

                                           dialog.show();
                                           username.requestFocus();
                                       } else if(nick.isEmpty() || nick.equals("")) {
                                           dialog.setTitle("提示");

                                           dialog.setMessage("昵称不能为空");

                                           dialog.setPositiveButton("确定", null);

                                           dialog.show();
                                           nickname.requestFocus();
                                       } else
                                       {
                                           if (!MyApplication.conn.isConnected()) {
                                               new Thread(new Runnable() {
                                                   @Override
                                                   public void run() {
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

                                               //添加好友的副线程
                                               Thread thread = new Thread(new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       try {
                                                           Thread.sleep(1000);
                                                           try {
                                                               roster.createEntry(user + "@" + MyApplication.conn.getServiceName(), nick, null);
                                                               System.out.println("添加好友成功！！");
                                                           } catch (XMPPException e) {
                                                               e.printStackTrace();
                                                               System.out.println("失败！！" + e);
                                                               Toast.makeText(AddFriendsActivity.this, "", Toast.LENGTH_SHORT).show();
                                                           }
                                                       } catch (Exception e) {
                                                           System.out.println("申请发生异常！！");
                                                           e.printStackTrace();
                                                       }
                                                   }
                                               });
                                               //启动线程和实例化handler
                                               UserSearchManager search = new UserSearchManager(MyApplication.conn);
                                               Form searchForm = null;
                                               try {
                                                   searchForm = search.getSearchForm("search." + MyApplication.conn.getServiceName());
                                               } catch (XMPPException e) {
                                                   e.printStackTrace();
                                               }
                                               Form answerForm = searchForm.createAnswerForm();
                                               answerForm.setAnswer("Username", true);
                                               answerForm.setAnswer("search", user);
                                               ReportedData data = null;
                                               try {
                                                   data = search.getSearchResults(answerForm, "search." + MyApplication.conn.getServiceName());
                                               } catch (XMPPException e) {
                                                   e.printStackTrace();
                                               }

                                               Iterator<ReportedData.Row> it = data.getRows();
                                               ReportedData.Row row = null;
                                               String ansS = "";
                                               while (it.hasNext()) {
                                                   row = it.next();
                                                   ansS += row.getValues("Username").next().toString() + "\n";
                                                   //Log.i("Username",row.getValues("Username").next().toString());
                                               }
                                               // Toast.makeText(activity, ansS, Toast.LENGTH_SHORT).show();
                                               if(!ansS.equals("")) {
                                                   thread.start();
                                                   Toast.makeText(activity, "消息发送成功", Toast.LENGTH_LONG).show();
                                                   username.setText("");
                                                   nickname.setText("");
                                               }
                                               else
                                               {
                                                   Toast.makeText(AddFriendsActivity.this, "该用户不存在", Toast.LENGTH_LONG).show();
                                                   username.setText("");
                                                   nickname.setText("");
                                               }

                                               receiver = new MyReceiver();
                                               IntentFilter intentFilter = new IntentFilter();
                                               intentFilter.addAction("dwz.edu.sdu.qq_chat.ui.activity.AddFriendsActivity");
                                               registerReceiver(receiver, intentFilter);


                                               //条件过滤器
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
                                                               intent.setAction("dwz.edu.sdu.qq_chat.ui.activity.AddFriendsActivity");
                                                               sendBroadcast(intent);
                                                           } else if (presence.getType().equals(
                                                                   Presence.Type.subscribed)) {
                                                               //发送广播传递response字符串
                                                               response = "恭喜，对方同意添加好友！";
                                                               Intent intent = new Intent();
                                                               intent.putExtra("response", response);
                                                               intent.setAction("dwz.edu.sdu.qq_chat.ui.activity.AddFriendsActivity");
                                                               sendBroadcast(intent);
                                                           } else if (presence.getType().equals(
                                                                   Presence.Type.unsubscribe)) {
                                                               //发送广播传递response字符串
                                                               response = "抱歉，对方拒绝添加好友，将你从好友列表移除！";
                                                               Intent intent = new Intent();
                                                               intent.putExtra("response", response);
                                                               intent.setAction("dwz.edu.sdu.qq_chat.ui.activity.AddFriendsActivity");
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


                                           }
                                       }

                                   }
                               });
    }


    //广播接收器
    public class MyReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            //接收传递的字符串response
           // context.unregisterReceiver(this);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddFriendsActivity.this);
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
                                if(!have) {
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



/*
        try {
                            UserSearchManager search = new UserSearchManager(MyApplication.conn);
                            Form searchForm = search.getSearchForm("search." + MyApplication.conn.getServiceName());
                            Form answerForm = searchForm.createAnswerForm();
                            answerForm.setAnswer("Username", true);
                            answerForm.setAnswer("search", user);
                            ReportedData data = search.getSearchResults(answerForm, "search." + MyApplication.conn.getServiceName());

                            Iterator<ReportedData.Row> it = data.getRows();
                            ReportedData.Row row = null;
                            String ansS = "";
                            while (it.hasNext()) {
                                row = it.next();
                                ansS += row.getValues("Username").next().toString() + "\n";
                                //Log.i("Username",row.getValues("Username").next().toString());
                            }
                            // Toast.makeText(activity, ansS, Toast.LENGTH_SHORT).show();

                            roster.createEntry(user + "@" + MyApplication.conn.getServiceName(), null, null);

                            Toast.makeText(activity, "消息发送成功", Toast.LENGTH_SHORT).show();

                            username.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity, e.getMessage() + " " + e.getClass().toString(), Toast.LENGTH_SHORT).show();
                        }
        */














