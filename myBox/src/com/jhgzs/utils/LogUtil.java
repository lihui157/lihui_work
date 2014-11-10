package com.jhgzs.utils;


import android.util.Log;

/**
 * @author lihui
 * 日志工具类
 */
public class LogUtil {
	
	/**
	 * make a choice to display log message by level----- add by lihui 2013.5.8
	 * level number
	 * error : 0
	 * warm : 1
	 * info : 2
	 * debug: 3
	 */
	private static int PRINT_LEVEL = 3; //print control
	
	public static final void error(String tag, String msg) {
		
		if(PRINT_LEVEL>=0){
			Log.e(tag, "++++++>>  "+msg+"  <<+ +++++");
		}
		
		
	}
	
	/**
	 * add by lihui 2013.5.8
	 * @param tag
	 * @param msg
	 */
	public static void debug(String tag,String msg){
		if(PRINT_LEVEL>=3){
			Log.d(tag, "++++++>>  "+msg+"  <<++++++");
		}
	}
	/**
	 * add by lihui 2013.5.8
	 * @param tag
	 * @param msg
	 */
	public static void info(String tag,String msg){
		if(PRINT_LEVEL>=2){
			Log.i(tag, "++++++>>  "+msg+"  <<++++++");
		}
	}
	
	/**
	 * add by lihui 2013.5.8
	 * @param tag
	 * @param msg
	 */
	public static void warn(String tag,String msg){
		if(PRINT_LEVEL>=1){
			Log.w(tag, "++++++>>  "+msg+"  <<++++++");
		}
	}
	
	/**
	 * add by lihui 2013.5.8
	 * @param tag
	 * @param msg
	 */
	public static void debug(String tag,String methodName,String msg){
		if(PRINT_LEVEL>=3){
			Log.d(tag+"."+methodName, "++++++>> "+msg+"  <<++++++");
		}
	}
	/**
	 * add by lihui 2013.5.8
	 * @param tag
	 * @param msg
	 */
	public static void info(String tag,String methodName,String msg){
		if(PRINT_LEVEL>=2){
			Log.i(tag+"."+methodName, "++++++>> "+msg+"  <<++++++");
		}
	}
	
	/**
	 * add by lihui 2013.5.8
	 * @param tag
	 * @param msg
	 */
	public static void warn(String tag,String methodName,String msg){
		if(PRINT_LEVEL>=1){
			Log.w(tag+"."+methodName, "++++++>> "+msg+"  <<++++++");
		}
	}
	
	/**
	 * add by lihui 2013.5.8
	 * @param tag
	 * @param msg
	 */
	public static void error(String tag,String methodName,String msg){
		if(PRINT_LEVEL>=1){
			Log.e(tag+"."+methodName, "++++++>> "+msg+"  <<++++++");
		}
	}
	

}
