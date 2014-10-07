package net.cstong.android.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.cstong.android.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;

public class ApiHelper {
	public static String URL_BASE = "http://bbs.cstong.net/index.php?m=mobile&";
	public static String URL_FOCUS = "http://bbs.cstong.net/index.php?m=mobile&c=focus"; // 聚焦页列表
	public static String URL_FOCUS_AD = "http://bbs.cstong.net/index.php?m=mobile&c=focus&a=ad"; // 聚焦页广告
	public static String URL_FORUM = "http://bbs.cstong.net/index.php?m=mobile&c=forum"; // 板块列表
	public static String URL_FORUM_READTHREAD = "http://bbs.cstong.net/index.php?m=mobile&c=forum&a=readThread"; // 帖子信息
	public static String URL_FORUM_THREADLIST = "http://bbs.cstong.net/index.php?m=mobile&c=forum&a=threadList"; // 帖子列表
	public static String URL_USER_LOGIN = "http://bbs.cstong.net/index.php?m=mobile&c=user&a=login"; //
	public static String URL_USER_REGISTER = "http://bbs.cstong.net/index.php?m=mobile&c=user&a=register"; //
	public static String URL_MESSAGE_NOTICELIST = "http://bbs.cstong.net/index.php?m=mobile&c=message&a=noticeList";
	public static String URL_MESSAGE_MESSAGELIST = "http://bbs.cstong.net/index.php?m=mobile&c=message&a=messageList";
	public static String URL_MESSAGE_NOTICEINFO = "http://bbs.cstong.net/index.php?m=mobile&c=message&a=noticeInfo";
	public static String URL_MESSAGE_POSTMESSAGE = "http://bbs.cstong.net/index.php?m=mobile&c=message&a=postMessage";
	public static String URL_POST_GETPOSTVAR = "http://bbs.cstong.net/index.php?m=mobile&c=post&a=getPostVar";
	public static String URL_POST_POST = "http://bbs.cstong.net/index.php?m=mobile&c=post&a=post";
	public static String URL_POST_REPLY = "http://bbs.cstong.net/index.php?m=mobile&c=post&a=doreply";
	public static String URL_POST_UPLOAD = "http://bbs.cstong.net/index.php?m=mobile&c=post&a=upload";

	public static void post(final Context context, final String url, final AbStringHttpResponseListener responseListener) {
		MyHttpClient client = new MyHttpClient(context);
		AbRequestParams params = new AbRequestParams();
		params.put("appPf", "android");
		if (Constant.packageInfo != null) {
			params.put("v", String.valueOf(Constant.packageInfo.versionCode));
		}
		client.setDebug(Constant.DEBUG);
//		client.post(url, responseListener);
		client.post(url, params, responseListener);
		
	}

	public static void post(final Context context, final String url, final AbRequestParams params, final AbStringHttpResponseListener responseListener) {
		MyHttpClient client = new MyHttpClient(context);
		params.put("appPf", "android");
		if (Constant.packageInfo != null) {
			params.put("v", String.valueOf(Constant.packageInfo.versionCode));
		}
		client.setDebug(Constant.DEBUG);
		client.post(url, params, responseListener);
	}

	public static void get(final Context context, final String url, final AbRequestParams params, final AbStringHttpResponseListener responseListener) {
		MyHttpClient client = new MyHttpClient(context);
		client.setDebug(Constant.DEBUG);
		client.get(url, params, responseListener);
	}

	public static Map<String, Object> getJsonMap(final JSONObject jsonObj) {
		try {
			Iterator<String> keyIter = jsonObj.keys();
			String key;
			Object value;
			Map<String, Object> valueMap = new HashMap<String, Object>();
			while (keyIter.hasNext()) {
				key = keyIter.next();
				value = jsonObj.get(key);
				valueMap.put(key, value);
			}
			return valueMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 把json 转换为 ArrayList 形式
	public static List<Map<String, Object>> getJsonList(final JSONArray jsonArray) {
		List<Map<String, Object>> list = null;
		try {
			JSONObject jsonObject;
			list = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				list.add(getJsonMap(jsonObject));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static String getForumTabTitle(final int fid) {
		String title = "";
		switch (fid) {
		case 1:
			title = "购物";
			break;
		case 4:
			title = "美食";
			break;
		case 6:
			title = "越策";
			break;
		case 19:
			title = "装修";
			break;
		case 22:
			title = "美容";
			break;
		case 44:
			title = "婚嫁";
			break;
		case 88:
			title = "团购";
			break;
		case 127:
			title = "亲子";
			break;
		case 183:
			title = "妈咪";
			break;
		case 215:
			title = "穿搭";
			break;
		default:
			break;
		}
		return title;
	}
}
