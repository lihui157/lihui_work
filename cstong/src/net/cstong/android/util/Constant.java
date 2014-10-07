package net.cstong.android.util;

import java.util.HashMap;
import java.util.Map;

import net.cstong.android.MyApplication;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Constant {
	public static MyApplication myApp = null;
	public static PackageInfo packageInfo = null;

	public static void init(final MyApplication app) {
		myApp = app;
		PackageManager pm = app.getPackageManager();
		try {
			packageInfo = pm.getPackageInfo(app.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
		}
	}

	public static final boolean DEBUG = true;
	public static final String sharePath = "cstong";
	public static final String USERSID = "user";
	//页面默认显示南京，登陆后显示注册用户的城市
	public static final String CITYID = "cityId";
	public static final String CITYNAME = "cityName";
	public static final String DEFAULTCITYID = "0731";
	public static final String DEFAULTCITYNAME = "长沙";

	//cookies
	public static final String USERNAMECOOKIE = "cookieName";
	public static final String USERPASSWORDCOOKIE = "cookiePassword";
	public static final String USERCOOKIE = "cookie";
	public static final String USERPASSWORDREMEMBERCOOKIE = "cookieRemember";
	public static final String FIRSTSTART = "firstStart";

	public static final String KEY_FORUMINFO = "forumInfo";
	public static final String KEY_THREADINFO = "threadInfo";
	public static final String KEY_MSGTYPE = "msgtype";
	public static final String KEY_USER_COOKIE = "EgG_winduser";
	public static final String HOST = "bbs.cstong.net";

	// 连接超时
	public static final int timeOut = 12000;
	// 建立连接
	public static final int connectOut = 12000;
	// 获取数据
	public static final int getOut = 60000;

	//1表示已下载完成
	public static final int downloadComplete = 1;
	//1表示未开始下载
	public static final int undownLoad = 0;
	//2表示已开始下载
	public static final int downInProgress = 2;
	//3表示下载暂停
	public static final int downLoadPause = 3;

	public static final String BASEURL = "http://www.418log.org/";

	//应用的key
	//1512528
	public final static String APPID = "1512528";

	//jfa97P4HIhjxrAgfUdq1NoKC
	public final static String APIKEY = "jfa97P4HIhjxrAgfUdq1NoKC";

	public static Map<Integer, String> forumNames = new HashMap<Integer, String>();

	public static final int INTENTRESULT_ADD_PHOTO = 1;
	public static final int INTENTRESULT_TAKE_PHOTO = 2;

	public static final int PHOTO_UPLOAD_LIMIT = 9;
	public static final int PHOTO_GENERAL_WIDTH = 580;
	public static final int PHOTO_GENERAL_HEIGHT = 580;
	public static final int PHOTO_APP_WIDTH = 300;
	public static final int PHOTO_APP_HEIGHT = 300;

	public static final int MSG_PHOTO_SCAN_OK = 1;

	public static final String PATH_SEPERATOR = ";";
}
