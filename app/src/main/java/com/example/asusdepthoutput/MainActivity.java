package com.example.asusdepthoutput;


import com.example.asus_xtion_depth_output_apk_test.R;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
    private Button rgbOutput;
    private Button depthOutput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rgbOutput = (Button)findViewById(R.id.rgb);
        depthOutput = (Button)findViewById(R.id.depth);

        ButtonListener buttonListener = new ButtonListener();
        rgbOutput.setOnClickListener(buttonListener);
        depthOutput.setOnClickListener(buttonListener);
	}

    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.rgb) {
                Intent it = new Intent();
                it.setClass(MainActivity.this, RGBActivity.class);
                startActivity(it);
            } else if(view.getId() == R.id.depth) {
                Intent it = new Intent();
                it.setClass(MainActivity.this, DepthActivity.class);
                startActivity(it);
            }
        }
    }
	
	protected void onDestroy() {
		super.onDestroy();
	}


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
