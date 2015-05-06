package com.example.asusdepthoutput;


import java.util.Date;

import org.openni.DepthMap;
import org.openni.StatusException;

import com.asus.xtionstartkernel.DepthData;
import com.asus.xtionstartkernel.PermissionCallbacks;
import com.asus.xtionstartkernel.XtionContext;
import com.example.asus_xtion_depth_output_apk_test.R;




import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	XtionContext m_xc;
	DepthData m_dd;
	boolean m_bexit = false; 
	long last_time;
	
	
	static{ 
		System.loadLibrary("asus_xiton_depth_output_apk_test");
	}
	
	private native boolean CoventFromDepthTORGB(  int[] pdepth,int width, int height );
	
	private PermissionCallbacks m_callbacks = new PermissionCallbacks() {

		@Override
		public void onDevicePermissionGranted() 
		{ 
			try 
			{
				m_dd = new DepthData(m_xc); 
				m_dd.setMapOutputMode(320, 240, 30);//设置深度图像分辨率
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
			setContentView(R.layout.activity_main);

			LinearLayout layout = (LinearLayout) findViewById(R.id.DepthLayout);

			// Add the adView to it
			layout.addView(new XtionView(MainActivity.this));
			//GlobalOSDService.SetContext(m_xc);
			//Intent it = new Intent(MainActivity.this, GlobalOSDService.class);
			//startService(it);
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
		m_dd.Close();
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

		DepthMap getDepthData;

		
		int depth_value = 0;//实际深度值
		int grey_value = 0;//实际灰度值
		@Override
		public void run() {
			
			while (!m_bexit) {

				try {
					Thread.sleep(33);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					m_xc.waitforupdate();
				} catch (StatusException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				m_canvas = m_holder.lockCanvas();
				if (m_canvas != null) 
				{
					try 
					{
						getDepthData = m_dd.GetDepthMap();
					} catch (StatusException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
//					int []pixels = new int[getDepthData.getXRes()*getDepthData.getYRes()];
//					for(int y = 0;y < getDepthData.getYRes();y++)
//					{
//				 		for(int x = 0;x< getDepthData.getXRes();x++)
//						{
//				 			//depth_value 为实际每个像素的深度值
//							depth_value = getDepthData.readPixel(x, y);
//							pixels[getDepthData.getXRes()*y+x] = ((int) (depth_value * 0.0256)) << 16;
//							pixels[getDepthData.getXRes()*y+x] |= ((int) (depth_value * 0.0256)) << 8; 
//							pixels[getDepthData.getXRes()*y+x] |= 255 << 24; 
//							
//							//pixels[getDepthData.getXRes()*y+x] = 0xffffffff;
//							
//							Log.v("test_output", Integer.toString(depth_value));
//						}
//					}
					
					
//使用JNI繪製bitmap_start
					
					int Data_width = getDepthData.getXRes();
					int Data_height = getDepthData.getYRes();
					
					int []pixels = new int[Data_width*Data_height];
					int index = 0;
					
					for(int y = 0;y < Data_height;y++)
					{
				 		for(int x = 0;x< Data_width;x++)
						{
				 			pixels[index] = getDepthData.readPixel(x, y);
				 			index++;
						}
					}				

					CoventFromDepthTORGB(  pixels,Data_width,Data_height );

					
					
					long cur_time = System.nanoTime();
//					Log.v("cur_date", Long.toString(cur_time));
					if(last_time != 0)
					{
						long time_compare = cur_time - last_time;
						int temp = (int) (time_compare /1000000);
						//Log.v("cur_date_compare", Long.toString(cur_time - last_time));
						Log.v("cur_date_compare",Integer.toString(temp) );

					}

					last_time = cur_time;
					

					
					
//使用JNI繪製bitmap_end					
					Bitmap grey_bitmap = Bitmap.createBitmap(getDepthData.getXRes(), getDepthData.getYRes(), Config.ARGB_8888);
					grey_bitmap.setPixels(pixels, 0, getDepthData.getXRes(), 0, 0, getDepthData.getXRes(), getDepthData.getYRes());

					if (grey_bitmap != null) 
					{
						//m_canvas.drawBitmap(grey_bitmap, 0, 0, null);
						m_canvas.drawBitmap(grey_bitmap,new Rect( 0,0,Data_width,Data_height ) , new RectF(0f,0f,Data_width,Data_height), null);
					}
					
					
					m_holder.unlockCanvasAndPost(m_canvas);
				}

			}
		}

	
	}
	
	
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}
		
		public boolean GetXtionDepthData(  )
		{
			return false;
			
		}

	
}
