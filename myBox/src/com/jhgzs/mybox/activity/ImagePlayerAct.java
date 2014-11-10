package com.jhgzs.mybox.activity;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.jhgzs.mybox.R;
import com.jhgzs.mybox.activity.view.ViewScroll;
import com.jhgzs.mybox.activity.view.TouchView.OnTouchCallBack;
import com.jhgzs.utils.BoxUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

public class ImagePlayerAct extends Activity  implements AnimationListener {
	
	private static final String TAG = "ImagePlayerAct";

	/** Constant used as menu item id for setting zoom control type */
	private static final int MENU_ID_ZOOM = 0;

	/** Constant used as menu item id for setting pan control type */
	private static final int MENU_ID_PAN = 1;

	/** Constant used as menu item id for resetting zoom state */
	private static final int MENU_ID_RESET = 2;

	/** Decoded bitmap image */
	private Bitmap mBitmap;

	private ZoomControls m_ZoomControls = null;

	private Boolean m_create = false;

	private Timer settimer = null;
	private TimerTask task = null;
	private static int tick;

	public Button m_back = null;
	public Button m_about = null;

	private ViewScroll detail = null;
	private LinearLayout ll = null;
	private LinearLayout.LayoutParams parm = null;


	final private static int MESSAGE_LOADINGIMAGEEND = 51118;
	final private static int MESSAGE_ONREPLAY = 519;
	final private static int MESSAGE_ZOOMCONTROLS_HIDE = 520;

	final private static int fadeOutTime = 1800;

	private FadeTimer fadetimer = null;
	private Animation animation;
	public AnimationListener animationListener = this;
	private static boolean m_picshow = false;

	private static boolean m_ispause = false;

	private RelativeLayout m_titler = null;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String error = null;
			switch (msg.what) {
			case MESSAGE_LOADINGIMAGEEND:
				showimage();
				break;
//			case Config.Dlna.ACTION_RENDER_TOIMAGEREPLAY:
//				ImageViewOnRePlay();
//				break;
//			case Config.Dlna.ACTION_RENDER_TOVIDEOPLAY:
//				ToVideoPlay();
//				break;
			case MESSAGE_ZOOMCONTROLS_HIDE:
				ZoomControlsHide();
				break;
			}
		}
	};

	public int RendererOnRePlay() {
		sendMessage2UI(MESSAGE_ONREPLAY);
		return 0;
	}

	private void ImageViewOnRePlay() {
		if (ll != null && detail != null && parm != null) {
			if (null != mBitmap) {
				mBitmap.recycle();
				mBitmap = null;
			}
			// mBitmap = GetUrlBitmap();
			ll.removeView(detail);
			detail = null;

			GetImageOnThread();
		}

	}

	public void CanclTimer() {
		if (null != settimer) {
			settimer.cancel();
			settimer = null;
		}
		if (null != task) {
			task = null;
		}
	}

	public void StartTimer() {

		CanclTimer();

		settimer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (m_ZoomControls.isShown()) {
					tick++;
					if (tick == 3) {
						if (m_ZoomControls.hasFocus()) {
							tick = 0;
						} else {
							sendMessage2UI(MESSAGE_ZOOMCONTROLS_HIDE);
							// settimer.cancel();
						}
					}
				}

			}

		};
		settimer.schedule(task, 1000, 1000);
	}

	/**
	 * Resets the fade out timer to 0. Creating a new one if needed
	 */
	private void resetTimer() {
		// Only set the timer if we have a timeout of at least 1 millisecond
		if (fadeOutTime > 0) {
			// Check if we need to create a new timer
			if (fadetimer == null || fadetimer._run == false) {
				// Create and start a new timer
				fadetimer = new FadeTimer();
				fadetimer.execute();
			} else {
				// Reset the current tiemr to 0
				fadetimer.resetTimer();
			}
		}
	}

	/**
	 * Counts from 0 to the fade out time and animates the view away when
	 * reached
	 */
	private class FadeTimer extends AsyncTask<Void, Void, Void> {
		// The current count
		private int timer = 0;
		// If we are inside the timing loop
		private boolean _run = true;

		public void resetTimer() {
			timer = 0;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			while (_run) {
				try {
					// Wait for a millisecond
					Thread.sleep(1);
					// Increment the timer
					timer++;

					// Check if we've reached the fade out time
					if (timer == fadeOutTime) {
						// Stop running
						_run = false;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			animation = AnimationUtils.loadAnimation(getContext(),
					android.R.anim.fade_out);
			animation.setAnimationListener(animationListener);
			// startAnimation(animation);
			if (null != m_ZoomControls)
				m_ZoomControls.startAnimation(animation);
			if (null != m_titler) {
				m_titler.startAnimation(animation);
			}
		}
	}

	public Context getContext() {
		return this;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if (null != m_ZoomControls)
			m_ZoomControls.setVisibility(View.GONE);
		if (null != m_titler) {
			m_titler.setVisibility(View.GONE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	private void ZoomControlsShow() {

		if (null != m_ZoomControls && null != m_titler) {

			m_ZoomControls.setVisibility(View.VISIBLE);
			// m_ZoomControls.show();
			m_titler.setVisibility(View.VISIBLE);

			resetTimer();
		}

	}

	private void ZoomControlsHide() {
		if (null != m_ZoomControls) {
			m_ZoomControls.setVisibility(View.GONE);
		}
	}

	

	

	private void sendMessage2UI(int msg) {

		handler.sendEmptyMessage(msg);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}

	public void InitTitleView() {

		m_titler = (RelativeLayout) findViewById(R.id.titler);
		// GenieDlnaTab.m_about.setText(R.string.refresh);

		m_back = (Button) findViewById(R.id.back);
		m_about = (Button) findViewById(R.id.about);

		m_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImagePlayerAct.this.onBackPressed();
			}
		});

		m_about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		m_about.setVisibility(View.GONE);
	}

	private void GetImageOnThread() {
		ProgressBar temp = (ProgressBar) findViewById(R.id.imageloading);
		temp.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

//				mBitmap = GetUrlBitmap();
				try {
					mBitmap = BoxUtil.getBitmap(ImagePlayerAct.this,Environment.getExternalStorageDirectory().getCanonicalPath()+"/����չ��22.jpg");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sendMessage2UI(MESSAGE_LOADINGIMAGEEND);
			}
		}).start();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub

		if (mBitmap != null && null != detail && ll != null && parm != null) {
			ll.removeView(detail);
			detail = null;
			detail = new ViewScroll(ImagePlayerAct.this, mBitmap, null);
			detail.SetOnTouchCallBack(
					new OnTouchCallBack() {

				public void OnTouchAction(MotionEvent event) {
					// TODO Auto-generated method stub
					if (m_picshow) {
						if (null != m_ZoomControls)
							m_ZoomControls.setVisibility(View.VISIBLE);
						if (null != m_titler) {
							m_titler.setVisibility(View.VISIBLE);
						}
						resetTimer();
					}
				}
			});
			ll.addView(detail, parm);
		}

		super.onConfigurationChanged(newConfig);
	}

	private void showimage() {
		if (null != mBitmap && ll != null && parm != null) {
			detail = new ViewScroll(ImagePlayerAct.this, mBitmap, null);
			detail.SetOnTouchCallBack(new OnTouchCallBack() {

				public void OnTouchAction(MotionEvent event) {
					// TODO Auto-generated method stub
					if (m_picshow) {
						if (null != m_ZoomControls)
							m_ZoomControls.setVisibility(View.VISIBLE);
						if (null != m_titler) {
							m_titler.setVisibility(View.VISIBLE);
						}
						resetTimer();
					}
				}
			});
			ll.addView(detail, parm);
		} else {
		}
		ProgressBar temp = (ProgressBar) findViewById(R.id.imageloading);
		temp.setVisibility(View.GONE);
		
		m_picshow = true;
		resetTimer(); 
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Display d = getWindowManager().getDefaultDisplay();
		final int woindow_w = d.getWidth();
		final int woindow_h = d.getHeight();

		m_ispause = true;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.image_player_act);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		InitTitleView();
		
		m_create = true;
		m_picshow = false;

		ll = (LinearLayout) findViewById(R.id.twill);
		parm = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);

		detail = null;

		m_ZoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
		m_ZoomControls.setOnZoomInClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != detail) {
					detail.onZoomIn();
				}
			}
		});
		m_ZoomControls.setOnZoomOutClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != detail) {
					detail.onZoomOut();
				}
			}
		});
		ZoomControlsShow();
		GetImageOnThread();
	}

//	public Bitmap GetUrlBitmap() {
//		m_picshow = false;
//
//		runOnUiThread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				if (null != m_ZoomControls)
//					m_ZoomControls.setVisibility(View.VISIBLE);
//				if (null != m_titler) {
//					m_titler.setVisibility(View.VISIBLE);
//				}
//			}
//		});
//
//		String imageurl = null;
//		try {
//
//			if (null == MediaApp.m_playurl) {
//				return null;
//			}
//			imageurl = MediaApp.m_playurl;
//
//			LogUtil.debug(TAG, "GetUrlBitmap imageurl =" + imageurl);
//
//			URL url = new URL(imageurl);
//			URLConnection conn;
//			conn = url.openConnection();
//			conn.connect();
//			InputStream in = conn.getInputStream();
//
//			byte[] bt = getBytes(in); //
//
//			if (null != mBitmap) {
//				mBitmap.recycle();
//				mBitmap = null;
//			}
//			System.gc();
//
//			BitmapFactory.Options opt = new BitmapFactory.Options();
//			// opt.inTempStorage = new byte[16*1024];
//
//			opt.inJustDecodeBounds = true;
//			BitmapFactory.decodeByteArray(bt, 0, bt.length, opt);
//			opt.inJustDecodeBounds = false;
//
//			Display d = getWindowManager().getDefaultDisplay();
//			final int w = d.getWidth();
//			final int h = d.getHeight();
//
//			LogUtil.debug(TAG,"GetUrlBitmap", " GetUrlBitmap w=" + w);
//			LogUtil.debug(TAG,"GetUrlBitmap", " GetUrlBitmap h=" + h);
//
//			final int SampleSize = w > h ? h : w;
//
//			LogUtil.debug(TAG,"GetUrlBitmap", " GetUrlBitmap SampleSize="
//					+ SampleSize);
//
//			if (opt.outWidth > SampleSize || opt.outHeight > SampleSize) {
//				if (opt.outWidth > opt.outHeight) {
//					opt.inSampleSize = opt.outWidth / SampleSize;
//				} else {
//					opt.inSampleSize = opt.outHeight / SampleSize;
//				}
//			}
//			mBitmap = BitmapFactory.decodeByteArray(bt, 0, bt.length, opt);
//
//			// mBitmap = BitmapFactory.decodeStream(in);
//			// //如果采用这��解码方式在低版本的API上会出现解码问题
//
//			in.close();
//			opt = null;
//
//			bt = null;
//
//			System.gc();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//			return null;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		} catch (Error e) {
//			e.printStackTrace();
//			return null;
//		} finally {
//
//		}
//
//		return mBitmap;
//	}

//	private byte[] getBytes(InputStream is) throws IOException {
//
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		byte[] b = new byte[1024];
//		int len = 0;
//
//		while ((len = is.read(b, 0, 1024)) != -1) {
//			baos.write(b, 0, len);
//			baos.flush();
//		}
//		byte[] bytes = baos.toByteArray();
//		return bytes;
//	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		ZoomControlsShow();
		return false;
	}

	public void onResume() {
		if (m_create) {
			m_create = false;
		} 

		super.onResume();

		m_ispause = false;
	}


	@Override
	protected void onStart() {

		super.onStart();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (null != mBitmap) {
			mBitmap.recycle();
			mBitmap = null;
		}
		CanclTimer();
		m_ispause = true;
		System.gc();
	}

}
