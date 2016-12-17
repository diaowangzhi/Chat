package dwz.edu.sdu.qq_chat.ui.activity;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
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
import java.util.Map;

import dwz.edu.sdu.qq_chat.R;

public class RelativeActivity extends ExpandableListActivity {

    RadioButton message;
    RadioButton relative;
    RadioButton status;
    //  private String[] data={"我的好友"};
    private List<String> data_list;

    Activity activity = this;

    private String response, acceptAdd, alertName, alertSubName;
    private Roster roster;
    private RelativeActivity.MyReceiver receiver;




    /**
     * 创建一级条目容器
     */
    List<Map<String, String>> gruops = new ArrayList<Map<String, String>>();
    /**
     * 存放内容, 以便显示在列表中
     */
    List<List<Map<String, String>>> childs = new ArrayList<List<Map<String, String>>>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative);

        message = (RadioButton) findViewById(R.id.message);
        relative = (RadioButton) findViewById(R.id.relative);
        status = (RadioButton) findViewById(R.id.status);

        roster = MyApplication.conn.getRoster();
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

           Collection<RosterEntry> allrosters = MyApplication.conn.getRoster().getEntries();
            for (RosterEntry rosterEntry : allrosters) {
                //此处可获取用户 的JID
                System.out.print("name: " + rosterEntry.getName() + "jid: " + rosterEntry.getUser());
                if(!MyApplication.userList.contains(rosterEntry.getUser())) {
                    //更新联系人列表
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
                    MyApplication.MessageList.add(new ArrayList<String>());
                }
            }


                for (String user : MyApplication.userList) {
                    boolean have = false;
                    for (RosterEntry rosterEntry : allrosters) {
                        if (rosterEntry.getUser().equals(user)) {
                            have = true;
                        }
                    }
                    if (!have) {
                        MyApplication.friendsList.remove(MyApplication.userList.indexOf(user));
                       // MyApplication.MessageList.remove(MyApplication.userList.indexOf(user));
                        MyApplication.userList.remove(user);
                        System.out.println("delete");
                    }

            }


            System.out.println("我的好友列表：=======================");


            receiver = new RelativeActivity.MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("dwz.edu.sdu.qq_chat.ui.activity.RelativeActivity");
           // registerReceiver(receiver, intentFilter);
            PacketFilter filter = new AndFilter(new PacketTypeFilter(Presence.class));
            registerReceiver(receiver, intentFilter);
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
                            intent.setAction("dwz.edu.sdu.qq_chat.ui.activity.RelativeActivity");
                            sendBroadcast(intent);
                        } else if (presence.getType().equals(
                                Presence.Type.subscribed)) {
                            //发送广播传递response字符串
                            response = "恭喜，对方同意添加好友！";
                            Intent intent = new Intent();
                            intent.putExtra("response", response);
                            intent.setAction("dwz.edu.sdu.qq_chat.ui.activity.RelativeActivity");
                            sendBroadcast(intent);
                        } else if (presence.getType().equals(
                                Presence.Type.unsubscribe)) {
                            //发送广播传递response字符串
                            response = "抱歉，对方拒绝添加好友，将你从好友列表移除！";
                            Intent intent = new Intent();
                            intent.putExtra("response", response);
                            intent.setAction("dwz.edu.sdu.qq_chat.ui.activity.RelativeActivity");
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


            System.out.println("我的好友列表：=======================");

            /*
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RelativeActivity.this, android.R.layout.simple_list_item_1, data);
            ListView listView = (ListView) findViewById(R.id.listView3);
            listView.setAdapter(adapter);
            */

            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(RelativeActivity.this, NewsActivity.class);
                    unregisterReceiver(receiver);
                    startActivity(intent);
                    finish();
                }
            });

            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.setClass(RelativeActivity.this, StatusActivity.class);
                    unregisterReceiver(receiver);
                    startActivity(intent);
                    finish();
                }
            });
            setListData();
        }

    }

    public void setListData() {
        // 创建二个一级条目标题
        Map<String, String> title_1 = new HashMap<String, String>();
        title_1.put("group", "我的好友");
        gruops.add(title_1);

        // 创建二级条目内容
        // 内容一
        List<Map<String, String>> childs_1 = new ArrayList<Map<String, String>>();
        for (String friend : MyApplication.friendsList) {
            Map<String, String> title_1_content_1 = new HashMap<String, String>();
            title_1_content_1.put("child", friend);
            System.out.println(friend);
            childs_1.add(title_1_content_1);

        }
        childs.add(childs_1);


        /**
         * 创建ExpandableList的Adapter容器 参数: 1.上下文 2.一级集合 3.一级样式文件 4. 一级条目键值
         * 5.一级显示控件名 6. 二级集合 7. 二级样式 8.二级条目键值 9.二级显示控件名
         *
         */
        SimpleExpandableListAdapter sela = new SimpleExpandableListAdapter(
                this, gruops, R.layout.groups, new String[]{"group"},
                new int[]{R.id.textGroup}, childs, R.layout.childs,
                new String[]{"child"}, new int[]{R.id.textChild});
        // 加入列表
        setListAdapter(sela);
    }

    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        Toast.makeText(
                RelativeActivity.this,
                "您选择了"
                        + gruops.get(groupPosition).toString()
                        + "子编号"
                        + childs.get(groupPosition).get(childPosition)
                        .toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("nickname", MyApplication.friendsList.get(childPosition));
        intent.putExtra("username",MyApplication.userList.get(childPosition));
        System.out.println(MyApplication.userList.get(childPosition));
        System.out.println(MyApplication.userList.get(childPosition));
        intent.setClass(RelativeActivity.this, UserActivity.class);
        unregisterReceiver(receiver);
        startActivity(intent);
        finish();
        return super.onChildClick(parent, v, groupPosition, childPosition, id);

    }

    /**
     * 二级标题按下
     */
    @Override
    public boolean setSelectedChild(int groupPosition, int childPosition,
                                    boolean shouldExpandGroup) {
        return super.setSelectedChild(groupPosition, childPosition,
                shouldExpandGroup);
    }

    /**
     * 一级标题按下
     */

    public void setSelectedGroup(int groupPosition) {
        super.setSelectedGroup(groupPosition);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(RelativeActivity.this);
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


