package dwz.edu.sdu.qq_chat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import dwz.edu.sdu.qq_chat.R;

import static java.lang.System.out;

public class NewsActivity extends AppCompatActivity {

    private  String[] data ={"one","two","three"};
    RadioButton message;
    RadioButton relative;
    RadioButton status;
    Button addFriend;
    AlertDialog.Builder  dialog;
    private String response, acceptAdd, alertName, alertSubName;
    private Roster roster;
    private NewsActivity.MyReceiver receiver;
    IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ListView list = (ListView) findViewById(R.id.listView1);
        dialog = new  AlertDialog.Builder(this);
        roster = MyApplication.conn.getRoster();
       // ConnectionConfiguration config = new ConnectionConfiguration("10.0.2.2", 5222);
      //  ConnectionConfiguration config = new ConnectionConfiguration("121.250.213.178", 5222);
        //    config.setServiceName("diaowz.com");   //还可以设置很多其他属性，如隐身登陆
       /* config.setSASLAuthenticationEnabled(false);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//设置为disabled
        config.setCompressionEnabled(false);
        config.setReconnectionAllowed(true);
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
        while(!MyApplication.conn.isConnected())
        {

        }
         if(MyApplication.conn.isConnected()) {
             receiver = new NewsActivity.MyReceiver();
             intentFilter = new IntentFilter();
             intentFilter.addAction("dwz.edu.sdu.qq_chat.ui.activity.NewsActivity");
             registerReceiver(receiver, intentFilter);
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
                             intent.setAction("dwz.edu.sdu.qq_chat.ui.activity.NewsActivity");
                             sendBroadcast(intent);
                         } else if (presence.getType().equals(
                                 Presence.Type.subscribed)) {
                             //发送广播传递response字符串
                             response = "恭喜，对方同意添加好友！";
                             Intent intent = new Intent();
                             intent.putExtra("response", response);
                             intent.setAction("dwz.edu.sdu.qq_chat.ui.activity.NewsActivity");
                             sendBroadcast(intent);
                         } else if (presence.getType().equals(
                                 Presence.Type.unsubscribe)) {
                             //发送广播传递response字符串
                             response = "抱歉，对方拒绝添加好友，将你从好友列表移除！";
                             Intent intent = new Intent();
                             intent.putExtra("response", response);
                             intent.setAction("dwz.edu.sdu.qq_chat.ui.activity.NewsActivity");
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


             final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
             for (int i = 0; i < 10; i++) {
                 HashMap<String, Object> map = new HashMap<String, Object>();
                 map.put("ItemImage", R.drawable.user);//图像资源的ID
                 map.put("ItemTitle", "Level " + i);
                 map.put("ItemText", "Finished in 1 Min 54 Secs, 70 Moves! ");
                 listItem.add(map);
             }
             //生成适配器的Item和动态数组对应的元素
             SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,//数据源
                     R.layout.list_items,//ListItem的XML实现
                     //动态数组与ImageItem对应的子项
                     new String[]{"ItemImage", "ItemTitle", "ItemText"},
                     //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                     new int[]{R.id.ItemImage, R.id.ItemTitle, R.id.ItemText}
             );

             //添加并且显示
             list.setAdapter(listItemAdapter);


             message = (RadioButton) findViewById(R.id.message);
             relative = (RadioButton) findViewById(R.id.relative);
             status = (RadioButton) findViewById(R.id.status);
             addFriend =(Button) findViewById(R.id.add_friend);

             addFriend.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     Intent intent = new Intent();
                     intent.setClass(NewsActivity.this,AddFriendsActivity.class);
                     startActivity(intent);
                     unregisterReceiver(receiver);
                     finish();
                 }
             });
             relative.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     Intent intent = new Intent();
                     intent.setClass(NewsActivity.this, RelativeActivity.class);
                     startActivity(intent);
                     unregisterReceiver(receiver);
                     finish();
                 }
             });

             status.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {

                     Intent intent = new Intent();
                     intent.setClass(NewsActivity.this, StatusActivity.class);
                     startActivity(intent);
                     unregisterReceiver(receiver);
                     finish();
                 }
             });

             list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                 @Override
                 public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                         long arg3) {
                /*
                     String name = listItem.get(arg2).get("ItemTitle").toString();
                     Intent intent = new Intent();
                     intent.putExtra("username", name);
                     intent.setClass(NewsActivity.this, UserActivity.class);
                     startActivity(intent);
                     finish();
                     */

                 }
             });

             //添加长按点击
             list.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

                 @Override
                 public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                     menu.setHeaderTitle("长按菜单-ContextMenu");
                     menu.add(0, 0, 0, "置顶");
                     menu.add(0, 1, 0, "删除");
                 }
             });
         }
        //list.setOnItemLongClickListener();

    }

        //长按菜单响应函数
   /*     public boolean onContextItemSelected (MenuItem item){
            System.out.println("点击了长按菜单里面的第" + item.getItemId() + "个项目");
            return super.onContextItemSelected(item);
        }
        */

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewsActivity.this);
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

