package com.example.chidouview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

@SuppressLint("NewApi") 
public class MainActivity extends AppCompatActivity {
	private YlcTimeProgress mChiDouTimeProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChiDouTimeProgress = (YlcTimeProgress) findViewById(R.id.chidou_progress);
        
        findViewById(R.id.start).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				mChiDouTimeProgress.start();
			}
		});
        
        findViewById(R.id.restart).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				mChiDouTimeProgress.reStart();
			}
		});
        
        findViewById(R.id.pause).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				mChiDouTimeProgress.pause();
			}
		});
        
        findViewById(R.id.resume).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				mChiDouTimeProgress.resume();
			}
		});
    }

  
}
