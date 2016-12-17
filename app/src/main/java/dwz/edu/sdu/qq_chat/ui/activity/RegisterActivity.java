package dwz.edu.sdu.qq_chat.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPException;

import dwz.edu.sdu.qq_chat.R;

import static org.jivesoftware.smackx.filetransfer.FileTransfer.Error.connection;

public class RegisterActivity extends AppCompatActivity {

    EditText user,passswd ,ack;
    String username,password,ackpasswd;
    TextView return_2;
    Button submit;
    AlertDialog.Builder  dialog;
    Activity activity =this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

      //  ConnectionConfiguration config = new ConnectionConfiguration("10.0.2.2",5222);
       // ConnectionConfiguration config = new ConnectionConfiguration("127.0.0.1", 5222);
     //   ConnectionConfiguration config = new ConnectionConfiguration("121.250.213.178", 5222);
        //    config.setServiceName("diaowz.com");   //还可以设置很多其他属性，如隐身登陆
      /*  config.setSASLAuthenticationEnabled(false);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//设置为disabled
        config.setCompressionEnabled(false);
        config.setReconnectionAllowed(true);
       conn = new XMPPConnection(config);
        */
        user = (EditText) findViewById(R.id.username);
        passswd = (EditText) findViewById(R.id.password);
        ack = (EditText) findViewById(R.id.ackpassword);
        submit = (Button) findViewById(R.id.submit);
        return_2 = (TextView) findViewById(R.id.return2);
        dialog = new  AlertDialog.Builder(this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = user.getText().toString();
                password = passswd.getText().toString();
                ackpasswd = ack.getText().toString();
                if(username==null||username.equals(""))
                {

                    dialog.setTitle("提示" );

                    dialog.setMessage("用户名不能为空" );

                    dialog.setPositiveButton("确定" ,  null );

                    dialog.show();
                    user.requestFocus();
                }
                else if (password==null||password.equals(""))
                {

                    dialog.setTitle("提示" );

                    dialog.setMessage("密码不能为空" );

                    dialog.setPositiveButton("确定" ,  null );

                    dialog.show();
                    passswd.requestFocus();
                }
                else if(!password.equals(ackpasswd))
                {
                    System.out.println(password);
                    System.out.println(ackpasswd);
                    dialog.setTitle("提示" );

                    dialog.setMessage("请确认密码" );

                    dialog.setPositiveButton("确定" ,  null );

                    dialog.show();
                    ack.setText("");
                    ack.requestFocus();
                }
                else
                {

                    if(!MyApplication.conn.isConnected())
                    {
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
                    if(MyApplication.conn.isConnected())
                    {

                        AccountManager amgr = MyApplication.conn.getAccountManager();
                        try {
                            amgr.createAccount(username, password);
                            dialog.setTitle("提示" );

                            dialog.setMessage("注册成功" );

                            dialog.setPositiveButton("确定" ,  null );

                            dialog.show();
                            Intent intent = new Intent();
                            intent.putExtra("username",username);
                            intent.setClass(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (XMPPException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, e.getMessage() + " " + e.getClass().toString(), Toast.LENGTH_SHORT).show();
                                System.out.println(e.getMessage());
                            if(e.getMessage().equals("conflict(409)"))
                            {
                                Toast.makeText(RegisterActivity.this,"该用户已存在", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else{
                        Looper.prepare();
                        dialog.setTitle("提示" );

                        dialog.setMessage("服务器连接失败" );

                        dialog.setPositiveButton("确定" ,  null );

                        dialog.show();
                    }
                }

            }
        });


        return_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }
}
