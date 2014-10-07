package net.cstong.android.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xbill.DNS.MBRecord;

import net.cstong.android.MyApplication;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ab.util.AbImageUtil;
import com.ab.util.AbStrUtil;

public class FileUtil {
	private static int FILE_SIZE = 4 * 1024;
	private static String TAG = "FileUtil";
	protected static int MAX_IMAGE_WIDTH = 250;
	protected static int MAX_IMAGE_HEIGHT = 250;
	
	static {
		if(MyApplication.MAX_IMAGE_WIDTH!=0){
			MAX_IMAGE_WIDTH = MyApplication.MAX_IMAGE_WIDTH;
		}
		if(MyApplication.MAX_IMAGE_HEIGHT!=0){
			MAX_IMAGE_HEIGHT = MyApplication.MAX_IMAGE_HEIGHT;
		}
	}
	
	

	public static boolean hasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public static boolean createPath(final String path) {
		File f = new File(path);
		if (!f.exists()) {
			Boolean o = f.mkdirs();
			Log.i(TAG, "create dir:" + path + ":" + o.toString());
			return o;
		}
		return true;
	}

	public static boolean exists(final String file) {
		return new File(file).exists();
	}

	public static File saveFile(final String file, final InputStream inputStream) {
		File f = null;
		OutputStream outSm = null;

		try {
			f = new File(file);
			String path = f.getParent();
			if (!createPath(path)) {
				Log.e(TAG, "can't create dir:" + path);
				return null;
			}

			if (!f.exists()) {
				f.createNewFile();
			}

			outSm = new FileOutputStream(f);
			byte[] buffer = new byte[FILE_SIZE];
			while ((inputStream.read(buffer)) != -1) {
				outSm.write(buffer);
			}
			outSm.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;

		} finally {
			try {
				if (outSm != null) {
					outSm.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		Log.v(TAG, "[FileUtil]save file:" + file + ":" + Boolean.toString(f.exists()));

		return f;
	}

	public static Drawable getImageDrawable(final String file) {
		if (!exists(file)) {
			return null;
		}
		Bitmap myBitmap = null;
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			/*
			 * If set to true, the decoder will return null (no bitmap), but the
			 * out... fields will still be set, allowing the caller to query the
			 * bitmap without having to allocate the memory for its pixels.
			 */
			//options.inJustDecodeBounds = true;
			opt.inDither = false; // Disable Dithering mode
			opt.inPurgeable = true; // Tell to gc that whether it needs free memory, the Bitmap can be cleared
			//options.inInputShareable = true; // Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
//			opt.inSampleSize = 2;
			opt.inSampleSize = computeSampleSize(opt,-1, 128*128);
			
		

			File imgFile = new File(file);
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			myBitmap = BitmapFactory.decodeStream((new FileInputStream(imgFile)),null, opt);
//			if ((myBitmap.getWidth() > MAX_IMAGE_WIDTH) || (myBitmap.getHeight() > MAX_IMAGE_HEIGHT)) {
//				myBitmap = AbImageUtil.scaleImg(imgFile, MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);
//			}
			
			BitmapDrawable newBmpDrawable = new BitmapDrawable(Constant.myApp.getApplicationContext().getResources(), myBitmap);
			
//	        if(myBitmap.getWidth() > MAX_IMAGE_WIDTH || myBitmap.getHeight() > 4096) {
//	            float scaleRate = myBitmap.getWidth() > myBitmap.getHeight() ? MAX_IMAGE_WIDTH / myBitmap.getWidth() : 4096 / myBitmap.getHeight();
//	            Matrix matrix = new Matrix();
//	            matrix.postScale(myBitmap.getWidth() * scaleRate, myBitmap.getHeight() * scaleRate);
//	            // 得到新的图片
//	            Bitmap tempBitmap = null;
//	            if(myBitmap.getHeight()>4096){
//	            	if(myBitmap.getWidth()>MAX_IMAGE_WIDTH){
//	            		tempBitmap = zoomImg(myBitmap, MAX_IMAGE_WIDTH, 4096);
//	            	}else{
//	            		tempBitmap = zoomImg(myBitmap, myBitmap.getWidth(), 4096);
//	            	}
//	            	
//	            }
//	            if(tempBitmap!=null){
//	            	newBmpDrawable = new BitmapDrawable(Constant.myApp.getApplicationContext().getResources(), tempBitmap);
//	            }
//	        }
			if(myBitmap.getWidth()>MAX_IMAGE_WIDTH||myBitmap.getHeight()>MAX_IMAGE_HEIGHT){
				myBitmap = zoomImg_(myBitmap, MAX_IMAGE_WIDTH, 4096);
				newBmpDrawable = new BitmapDrawable(Constant.myApp.getApplicationContext().getResources(), myBitmap);
			}
//			if (!myBitmap.isRecycled()) {
//				myBitmap.recycle(); // 回收图片所占的内存
//				// System.gc(); // 提醒系统及时回收
//			}
			myBitmap = null;
			
			
			return newBmpDrawable;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
			return null;
		}finally {
		}
//		return null;
	}
	
	
	public static Bitmap getbitmap(File file,int newWidth,int newHeight) {
		
		if(!file.exists()) return null;
		// 显示网络上的图片
		Bitmap bitmap = null;
		InputStream is = null;
		byte[] icon = null;
		BitmapFactory.Options opt = null;
		try {
			
			is = new FileInputStream(file);
			icon = getBytes(is);
			// bitmap = BitmapFactory.decodeStream(is);
			opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(icon, 0, icon.length, opt);
			if (opt.outWidth > opt.outHeight) {
				opt.inSampleSize = opt.outWidth / newWidth;
				;
			} else {
				opt.inSampleSize = opt.outHeight / newHeight;
			}
			if (opt.inSampleSize < 0) {
				opt.inSampleSize = 1;
			}
			opt.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeByteArray(icon, 0, icon.length, opt);
			// is.close();
			// icon = null;
			// opt = null;
			// conn.disconnect();
		} catch(OutOfMemoryError error){
			error.printStackTrace();
			return null;
		}catch (IOException e) {
			e.printStackTrace();
			return null;
		}finally{
			try {
				if (is != null)
					is.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			icon = null;
			opt = null;
		}
		return bitmap;
	}
	
	private static byte[] getBytes(InputStream is) throws IOException {

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] b = new byte[512];
			int len = 0;

			while ((len = is.read(b, 0, 512)) != -1) {
				// LogUtil.debug(TAG, "getBytes",
				// ""+len+"  baos lenth:"+baos.size());
				baos.write(b, 0, len);
				baos.flush();
			}
			byte[] bytes = baos.toByteArray();

			baos.reset();

			baos.close();

			baos = null;
			b = null;

			return bytes;
		} catch(OutOfMemoryError error){
			error.printStackTrace();
			System.gc();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	// 缩放图片
	private static Bitmap zoomImg(Bitmap bitmap, int newWidth ,int newHeight){
		 int width = bitmap.getWidth();
		  int height = bitmap.getHeight();
		  float scaleWidth = ((float) newWidth) / width;
		  float scaleHeight = ((float) newHeight) / height;
		  Matrix matrix = new Matrix();
		  matrix.postScale(scaleWidth, scaleHeight);
		  // create the new Bitmap object
		  Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
		    height, matrix, true);
	    return resizedBitmap;
	}
	
	

		// 将Drawable转化为Bitmap
		private static Bitmap drawableToBitmap(Drawable drawable) {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
					.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
					: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, width, height);
			drawable.draw(canvas);
			return bitmap;

		}
	
	private static Bitmap zoomImg_(Bitmap bitmap,int maxWidth,int maxHeight){
		double _w = 20.00;
		double _h = 20.00;
		if(bitmap.getWidth()>maxWidth||bitmap.getHeight()>maxHeight){
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			if(bitmap.getWidth()>maxWidth){
				_w = maxWidth*1.00;
				_h = _w*bitmap.getHeight()/bitmap.getWidth();
				if(_h>maxHeight){
					_h = maxHeight*1.00;
					_w = bitmap.getWidth()*_h/bitmap.getHeight();
				}
			}else if(bitmap.getHeight()>maxHeight){
				_h = maxHeight*1.00;
				_w = bitmap.getWidth()*_h/bitmap.getHeight();
			}
			Matrix matrix = new Matrix();
			Log.e(TAG, "_w :"+_w+" _h:"+_h);
			float scaleWidth = ((float) _w) / width;
			float scaleHeight = ((float) _h) / height;
			matrix.postScale((float)scaleWidth, (float)scaleHeight);
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
					height, matrix, true);
//			if (!bitmap.isRecycled()) {
//				bitmap.recycle(); // 回收图片所占的内存
//				// System.gc(); // 提醒系统及时回收
//			}
//			bitmap = null;
			return resizedBitmap;
		}
		
		return bitmap;
	}
	
	private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
	    int roundedSize;
	    if (initialSize <= 8) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }
	    return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;
	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
	    if (upperBound < lowerBound) {
	        // return the larger one when there is no overlapping zone.
	        return lowerBound;
	    }
	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
	    }
	} 

	/**
	 * 从相册得到的url转换为SD卡中图片路径
	 */
	public static String getPathFromUri(final Context context, final Uri uri) {
		if (AbStrUtil.isEmpty(uri.getAuthority())) {
			return null;
		}
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = ((Activity) context).managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		return path;
	}

	/** Create a file Uri for saving an image or video */
	public static Uri getOutputImageFileUri() {
		return Uri.fromFile(getOutputImageFile());
	}

	/** Create a File for saving an image or video */
	public static File getOutputImageFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		return mediaFile;
	}
}