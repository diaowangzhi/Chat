package dwz.edu.sdu.qq_chat.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import dwz.edu.sdu.qq_chat.R;


public class MainActivity extends AppCompatActivity {
    RadioButton message;
    RadioButton relative;
    RadioButton status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = (RadioButton) findViewById(R.id.message);
        relative = (RadioButton) findViewById(R.id.relative);
        status = (RadioButton) findViewById(R.id.status);

        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,NewsActivity.class);
                startActivity(intent);

                finish();
            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(MainActivity.this,StatusActivity.class);
                startActivity(intent);

                finish();
            }
        });


    }
}
