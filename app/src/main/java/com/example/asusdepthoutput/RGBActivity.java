package com.example.asusdepthoutput;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.asus.xtionstartkernel.PermissionCallbacks;
import com.asus.xtionstartkernel.XtionContext;
import com.asus.xtionstartlibs.RGBData;
import com.asus.xtionstartlibs.UserTracker;
import com.example.asus_xtion_depth_output_apk_test.R;

import org.openni.StatusException;

public class RGBActivity extends Activity {

	
	XtionContext m_xc;
	RGBData m_rgbdata;
	UserTracker m_user;
	boolean m_bexit = false;
	Button m_Button_video;
	private boolean isClose = false ;
	boolean m_bIsVideo_Register = false;
	boolean m_bIsVideo_start = false;
	
	private PermissionCallbacks m_callbacks = new PermissionCallbacks() {

		@Override
		public void onDevicePermissionGranted() 
		{ 
			try 
			{
				m_rgbdata = new RGBData(m_xc);
				m_rgbdata.setMapOutputMode(320, 240, 30);
			} catch (Exception e) 
			{
				e.printStackTrace();
			} 
 
			try 
			{ 
				m_xc.start();
			} catch (StatusException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace(); 
			}
			setContentView(R.layout.activity_rgb);

			LinearLayout layout = (LinearLayout) findViewById(R.id.RGBLayout);

			// Add the adView to it
			layout.addView(new XtionView(RGBActivity.this));
			
			m_Button_video=(Button)findViewById(R.id.button1);
			

			m_Button_video.setText("¼Ӱ"); 
			m_Button_video.setBackgroundColor(Color.WHITE);
			 
			  
			m_Button_video.setOnClickListener(new Button.OnClickListener()
			{
				@SuppressLint("SdCardPath")
				public void onClick(View v)
				{
					Toast toast = null;
					if(!m_bIsVideo_start)
					{
						toast = Toast.makeText(RGBActivity.this,"��ʼ¼Ӱ",Toast.LENGTH_LONG);
						if(m_bIsVideo_Register == false)
						{
							m_rgbdata.videoEncodeRegister("/sdcard/Movies/rgbdata_test.avi");
							m_bIsVideo_Register = true;
							m_Button_video.setBackgroundColor(Color.GREEN);
							
						}
						
						m_bIsVideo_start = true;//��ʼ¼��
						
					}
					else 
					{
						toast = Toast.makeText(RGBActivity.this,"���¼Ӱ",Toast.LENGTH_LONG);
						if(m_bIsVideo_Register)
						{
							isClose = true ;
							
							
						}
						m_bIsVideo_start = false;//���¼��
						m_Button_video.setBackgroundColor(Color.WHITE);
					}

					toast.setGravity(Gravity.TOP, 100, 100);
					toast.show();
				}
			});
			
		}

		
		
		
		@Override
		public void onDevicePermissionDenied() {

		}
	};	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			m_xc = new XtionContext(this, m_callbacks);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void onDestroy() {
		super.onDestroy();
		m_rgbdata.Close();
		m_xc.Close();
	}
	
	
	public class XtionView extends SurfaceView implements Callback, Runnable {

		private SurfaceHolder m_holder;
		private Thread m_thread = new Thread(this);
		private Canvas m_canvas;
		private Paint m_paint;
		

		public XtionView(Context context) {
			super(context);
			m_holder = this.getHolder();
			m_holder.addCallback(this);
			m_paint = new Paint();
			m_paint.setColor(Color.WHITE);
		}
   
		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			m_thread.start();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {

			m_bexit = true;
		}
		
		@Override
		public void run()
		{
			
			while (!m_bexit) 
			{


				try 
				{
					m_xc.waitforupdate();
				} catch (StatusException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				m_canvas = m_holder.lockCanvas();
				if (m_canvas != null) 
				{

					Bitmap bmp = m_rgbdata.GetImage();
					if (bmp != null) 
					{
						m_canvas.drawBitmap(bmp, 0, 0, null);
						

						if(m_bIsVideo_Register)
						{
							m_rgbdata.videoEncodeFill();
							if(isClose)
							{
								m_rgbdata.vedioEndcodeClose() ;
								m_bIsVideo_Register = false;
								isClose = false;
							}
						}			

					}
					
					m_holder.unlockCanvasAndPost(m_canvas);
				}

			}
		}

	
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
