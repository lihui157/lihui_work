package com.jhgzs.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;


import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 *Http操作工具类
 *实现volley的请求封装，以及普通的httpClient方式请求结果
 * 
 * @author lihui
 * 
 */
public class HttpUtil {

	private static int timeoutConnection = 10000;
	private static int timeoutSocket = 15000;
	private static final String TAG = "HttpUtil";

	private static final int HTTP_CODE_200 = 200;

	
	public static String synHttpByGet(String url,HashMap<String, String> params) {

		LogUtil.debug(TAG,"getHttpContextByGet", url);
		HttpGet httpRequest = new HttpGet(url+ getParamsStrFromMap(params));
		HttpClient httpclient = null;
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);// Set the default socket timeout
										// (SO_TIMEOUT) // in milliseconds which
										// is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			
			httpclient = new DefaultHttpClient(httpParameters);
			// 执行httpclient，返回HttpResponse
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 提取返回字符串
				return EntityUtils.toString(httpResponse.getEntity());
			} else {
				LogUtil.error(TAG,"getHttpContextByGet",""+httpResponse.getStatusLine().getStatusCode());
				return null;
			}
			
		} catch (Exception e) {
			LogUtil.error(TAG,"getHttpContextByGet",e.getMessage().toString());
			return null;
		}finally{
			httpclient.getConnectionManager().shutdown();
		}
		
	}

	public static String synHttpByPost(String url,HashMap<String, String> params) {
		
		LogUtil.debug(TAG,"getHttpContextByPost", url);
		HttpPost httpRequest = new HttpPost(url);
		HttpClient httpclient = null;
		List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
		paramsList = getParamsListFromMap(params);
		
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);// Set the default socket timeout
										// (SO_TIMEOUT) // in milliseconds which
										// is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			// 参数编码
			HttpEntity httpentity = new UrlEncodedFormEntity(paramsList,"utf-8");
			httpRequest.setEntity(httpentity);

			httpclient = new DefaultHttpClient(httpParameters);
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				//提取返回内容
				return EntityUtils.toString(httpResponse.getEntity());
			} else {
				LogUtil.error(TAG,"getHttpContextByGet",""+httpResponse.getStatusLine().getStatusCode());
				return null;
			}
		} catch (Exception e) {
			LogUtil.error(TAG, "getHttpContextByPost",e.getMessage().toString());
			return null;
		}finally{
			httpclient.getConnectionManager().shutdown();
		}
	}

	public static String getParamsStrFromMap(HashMap<String, String> params) {
		if (params == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		Iterator<?> it = params.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			if (i == 0) {
				sb.append("?" + key + "=" + value);
			} else {
				sb.append("&" + key + "=" + value);
			}
			i++;
		}
		// LogUtil.i(TAG,sb.toString());
		return sb.toString();
	}

	public static List<BasicNameValuePair> getParamsListFromMap(
			HashMap<String, String> params) {
		if (params == null) {
			return null;
		}
		List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		Iterator<?> it = params.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			LogUtil.debug(TAG, "getParamsListFromMap","key=" + key + ",value=" + value);
			list.add(new BasicNameValuePair(key, value));
		}
		return list;

	}

	/**
	 * 清楚html标签，提取内容
	 * @param htmlStr
	 * @return
	 */
	public static String delHTMLTag(String htmlStr) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 匹配script
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 匹配样式表
		String regEx_html = "<[^>]+>"; // 

		Pattern p_script = Pattern.compile(regEx_script,
				Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 锟斤拷锟斤拷script锟斤拷签

		Pattern p_style = Pattern
				.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 锟斤拷锟斤拷style锟斤拷签

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 锟斤拷锟斤拷html锟斤拷签

		htmlStr = htmlStr.replace("&nbsp;", " ");

		return htmlStr.trim(); // 
	}

	/**
	 * 根据url获取图片
	 * @param imageUrl
	 * @return
	 */
	public static Bitmap getBitmap(String imageUrl) {
		Bitmap mBitmap = null;
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream is = conn.getInputStream();
			mBitmap = BitmapFactory.decodeStream(is);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mBitmap;
	}
	
	

}
