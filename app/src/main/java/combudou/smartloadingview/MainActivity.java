package combudou.smartloadingview;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import combudou.smartloadingview.view.NextActivity;
import combudou.smartloadingview.view.SmartLoadingView;

public class MainActivity extends AppCompatActivity implements SmartLoadingView.onViewCompleteFinshListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SmartLoadingView loadingView = findViewById(R.id.smartView);
        loadingView.setCompleteFinshListener(this);
    }

    @Override
    public void complete() {
        Toast.makeText(this, "进度已经加载完毕，该执行对应的跳转方法了", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, NextActivity.class));
        this.finish();
    }


}
