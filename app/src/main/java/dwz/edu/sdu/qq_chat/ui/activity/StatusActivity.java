package dwz.edu.sdu.qq_chat.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import dwz.edu.sdu.qq_chat.R;

public class StatusActivity extends AppCompatActivity {

    RadioButton message;
    RadioButton relative;
    RadioButton status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        message = (RadioButton) findViewById(R.id.message);
        relative = (RadioButton) findViewById(R.id.relative);
        status = (RadioButton) findViewById(R.id.status);

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(StatusActivity.this,NewsActivity.class);
                startActivity(intent);

                finish();
            }
        });

        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(StatusActivity.this,RelativeActivity.class);
                startActivity(intent);

                finish();
            }
        });



    }
}
