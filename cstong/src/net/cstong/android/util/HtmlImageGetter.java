package net.cstong.android.util;

import java.io.InputStream;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

public class HtmlImageGetter implements ImageGetter {
	private TextView _htmlText;
	private String _imgPath;
	private Drawable _defaultDrawable;
	private String TAG = "HtmlImageGetter";

	public HtmlImageGetter(final TextView htmlText, final String imgPath, final Drawable defaultDrawable) {
		_htmlText = htmlText;
		_imgPath = imgPath;
		_defaultDrawable = defaultDrawable;
	}

	@Override
	public Drawable getDrawable( String imgUrl) {
		Log.e(TAG, "imgUrl"+imgUrl);
		imgUrl = imgUrl.replace("[img]", "");
		String ext = MimeTypeMap.getFileExtensionFromUrl(imgUrl);
		if (ext == null) {
			Log.d(TAG, "can't get imgUrl extension:" + imgUrl);
			return null;
		}
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
		if (type == null) {
			Log.d(TAG, "can't get imgUrl mimetype:" + imgUrl);
			return null;
		}
		String imgKey = String.valueOf(imgUrl.hashCode());
		String path = Environment.getExternalStorageDirectory() + _imgPath;
		FileUtil.createPath(path);

		String[] ss = imgUrl.split("\\.");
		String imgX = ss[ss.length - 1];
		imgKey = path + "/" + imgKey + "." + imgX;

		if (FileUtil.exists(imgKey)) {
			Drawable drawable = FileUtil.getImageDrawable(imgKey);
			
			if (drawable != null) {
				Log.d(TAG, "load img:" + imgKey +" width:"+drawable.getIntrinsicWidth()+" height:"+drawable.getIntrinsicHeight());
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//				drawable.setBounds(0, 0, 200, 200);
				return drawable;
			} else {
				Log.v(TAG, "load img:" + imgKey + ":null");
			}
		}

		URLDrawable urlDrawable = new URLDrawable(_defaultDrawable);
		new AsyncThread(urlDrawable).execute(imgKey, imgUrl);
		return urlDrawable;
	}

	private class AsyncThread extends AsyncTask<String, Integer, Drawable> {
		private String imgKey;
		private URLDrawable _drawable;

		public AsyncThread(final URLDrawable drawable) {
			_drawable = drawable;
		}

		@Override
		protected Drawable doInBackground(final String... strings) {
			
			imgKey = strings[0];
			InputStream inps = Network.getInputStream(strings[1]);
			if (inps == null) {
				return _drawable;
			}
			Drawable drawable = null;
			try {
				FileUtil.saveFile(imgKey, inps);
				inps.close();
				drawable = FileUtil.getImageDrawable(imgKey); //Drawable.createFromPath(imgKey);
			} catch (Exception e) {
				e.printStackTrace();
				return _drawable;
			} finally {
				inps = null;
			}
			return drawable;
		}

		@Override
		public void onProgressUpdate(final Integer... value) {
			_htmlText.setText("loading...");
		}

		@Override
		protected void onPostExecute( Drawable result) {
			
			if(result!=null){
				_drawable.setDrawable(result);
			}
			_htmlText.setText(_htmlText.getText());
			
//			_drawable.setDrawable(result);
//             Log.i(getClass().getSimpleName(), "更新界面：");
//             _htmlText.invalidate();
//             _htmlText.setHeight(_htmlText.getHeight()+result.getIntrinsicHeight());
//             _htmlText.setEllipsize(null);

//             mHandler.sendEmptyMessage(0);
		}
	}

	public class URLDrawable extends BitmapDrawable {
		private Drawable drawable;

		public URLDrawable( Drawable defaultDraw) {
			setDrawable(defaultDraw);
		}

		private void setDrawable( Drawable ndrawable) {
			if (ndrawable != null) {
				drawable = ndrawable;
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
				setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			}
		}

		@Override
		public void draw( Canvas canvas) {
			try {
				drawable.draw(canvas);
			} catch (StackOverflowError e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}
	}
}