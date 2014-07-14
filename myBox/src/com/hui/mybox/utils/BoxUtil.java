package com.hui.mybox.utils;

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

import com.hui.mybox.model.MediaFileInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

public class BoxUtil {
	
	
	public static String Img2Str(String imgFilePath) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		byte[] data = null;
		// 读取图片字节数组
		try {
			InputStream in = new FileInputStream(imgFilePath);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 对字节数组Base64编码
		return new String(Base64.encode(data));// 返回Base64编码过的字节数组字符串
	}

	public static boolean str2Img(String imgStr, String imgFilePath) {// 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) // 图像数据为空
			return false;
		try {
			// Base64解码
			byte[] bytes = Base64.decode(imgStr);
			for (int i = 0; i < bytes.length; ++i) {
				if (bytes[i] < 0) {// 调整异常数据
					bytes[i] += 256;
				}
			}
			// 生成jpeg图片
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(bytes);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private static byte[] getBytes(InputStream is) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;

		while ((len = is.read(b, 0, 1024)) != -1) {
			baos.write(b, 0, len);
			baos.flush();
		}
		byte[] bytes = baos.toByteArray();
		return bytes;
	}
	
	/**
	 * 根据path获取图片
	 * @param path
	 * @return
	 */
	public static Bitmap getBitmap(Context c,String path) {
		Bitmap mBitmap = null;
		try {
			FileInputStream fin = new FileInputStream(path);
			byte[] bt = getBytes(fin); //
			BitmapFactory.Options opt = new BitmapFactory.Options();
			// opt.inTempStorage = new byte[16*1024];

			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(bt, 0, bt.length, opt);
			opt.inJustDecodeBounds = false;

			Display d = ((Activity)c).getWindowManager().getDefaultDisplay();
			final int w = d.getWidth();
			final int h = d.getHeight();
			final int SampleSize = w > h ? h : w;
			if (opt.outWidth > SampleSize || opt.outHeight > SampleSize) {
				if (opt.outWidth > opt.outHeight) {
					opt.inSampleSize = opt.outWidth / SampleSize;
				} else {
					opt.inSampleSize = opt.outHeight / SampleSize;
				}
			}
			mBitmap = BitmapFactory.decodeByteArray(bt, 0, bt.length, opt);

			// mBitmap = BitmapFactory.decodeStream(in);
			// //濡傛灉閲囩敤杩欑瑙ｇ爜鏂瑰紡鍦ㄤ綆鐗堟湰鐨凙PI涓婁細鍑虹幇瑙ｇ爜闂

			fin.close();
			opt = null;
			bt = null;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return mBitmap;
	}
	
	/**
	 * 文件长度格式化输出
	 * @param filesize
	 * @return
	 */
	public static String convertFileSize(long filesize) {
		String strUnit = "Bytes";
		String strAfterComma = "";
		int intDivisor = 1;
		if (filesize >= 1024 * 1024)
		{
			strUnit = "MB";
			intDivisor = 1024 * 1024;
		}else if (filesize >= 1024){
			strUnit = "KB";
			intDivisor = 1024;
		}
		if (intDivisor == 1)
			return filesize + " " + strUnit;
		strAfterComma = "" + 100 * (filesize % intDivisor) / intDivisor;
		if (strAfterComma == "")
			strAfterComma = ".0";
		return filesize / intDivisor + "." + strAfterComma + " " + strUnit;
	}
	
	/**
	 * 发送广播
	 * @param context
	 * @param action
	 * @param bundle
	 */
	public static void sentBroadcast(Context context,String action,Bundle bundle){
		Log.e("", "sentBroadcast:"+action);
		Intent i = new Intent();
		i.setAction(action);
		if(bundle!=null)
		i.putExtras(bundle);
		context.sendBroadcast(i);
	}
	
	
	
}
