package net.cstong.android;

import java.util.List;

import net.cstong.android.model.User;
import net.cstong.android.model.UserDao;
import net.cstong.android.util.Constant;
import net.cstong.android.util.CrashHandler;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ab.db.storage.AbSqliteStorage;
import com.ab.db.storage.AbSqliteStorageListener.AbDataInfoListener;
import com.ab.db.storage.AbSqliteStorageListener.AbDataInsertListener;
import com.ab.db.storage.AbStorageQuery;
import com.ab.global.AbConstant;

public class MyApplication extends Application {
	private static final String TAG = "MyApplication";
	// 登录用户
	public User mUser = new User();
	public String cityid = Constant.DEFAULTCITYID;
	public String cityName = Constant.DEFAULTCITYNAME;
	public boolean ad = false;
	public boolean isFirstStart = true;
	public SharedPreferences mSharedPreferences = null;
	public static int MAX_IMAGE_WIDTH = 250;
	public static int MAX_IMAGE_HEIGHT = 250;
	
		
	
	//数据库操作类
	public AbSqliteStorage mAbSqliteStorage = null;
	public UserDao mUserDao = null;
	
	//全局缓存
	public static LruCache<String, Bitmap> caches; 

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");

		mSharedPreferences = getSharedPreferences(AbConstant.SHAREPATH, Context.MODE_PRIVATE);
		mAbSqliteStorage = AbSqliteStorage.getInstance(this);
		mUserDao = new UserDao(this);

		initLoginParams();
		initIMConfig();
		initLruCache();
		
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());

		Constant.init(this);
	}
	
	private void initLruCache(){
		// LruCache通过构造函数传入缓存值，以KB为单位。 
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024); 
        // 使用最大可用内存值的1/8作为缓存的大小。 
        int cacheSize = maxMemory / 8; 
        caches = new LruCache<String, Bitmap>(cacheSize) { 
            @SuppressLint("NewApi")
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// 重写此方法来衡量每张图片的大小，默认返回图片数量。
				// return bitmap.getByteCount() / 1024;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
					return bitmap.getByteCount() / 1024;
				}
				// Pre HC-MR1
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			} 
        };
	}

	@Override
	public void onTerminate() {
		Log.d(TAG, "onTerminate");
		super.onTerminate();
	}

	public void updateLoginParams() {
		Editor editor = mSharedPreferences.edit();
		editor.putString(Constant.USERNAMECOOKIE, mUser.username);
		editor.putString(Constant.USERPASSWORDCOOKIE, mUser.password);
		editor.putString(Constant.USERCOOKIE, mUser.cookie);
		editor.putBoolean(Constant.FIRSTSTART, false);
		editor.commit();
		isFirstStart = false;
	}
	
	

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		Log.w(TAG, "onLowMemory");
		System.gc();
		super.onLowMemory();
	}

	/**
	 * 登录
	 */
	public void checkLogin() {
		// 查询本地数据
		AbStorageQuery mAbStorageQuery = new AbStorageQuery();
		mAbStorageQuery.equals("user_name", mUser.username);
		mAbStorageQuery.equals("password", mUser.password);
		//mAbStorageQuery.equals("is_login_user", true);
		mAbSqliteStorage.findData(mAbStorageQuery, mUserDao, new AbDataInfoListener() {
			@Override
			public void onFailure(final int errorCode, final String errorMessage) {
				//showToast(errorMessage);
			}

			@Override
			public void onSuccess(final List<?> paramList) {
				if ((paramList != null) && (paramList.size() > 0)) {
					//登录IM
					//loginIMTask((User) paramList.get(0));
				} else {
					//showToast("IM信息缺失");
				}
			}

		});
	}

	/**
	 * 清空上次登录参数
	 */
	public void clearLoginParams() {
		Editor editor = mSharedPreferences.edit();
		editor.clear();
		editor.commit();
		mUser.cookie = "";
	}

	/**
	 * 保存登录数据
	 * 
	 */
	public void saveUserData() {
		// 查询数据
		AbStorageQuery mAbStorageQuery = new AbStorageQuery();
		mAbStorageQuery.equals("username", mUser.username);
		//mAbStorageQuery.equals("is_login_user", true);

		mAbSqliteStorage.findData(mAbStorageQuery, mUserDao, new AbDataInfoListener() {
			@Override
			public void onFailure(final int errorCode, final String errorMessage) {
				//showToast(errorMessage);
			}

			@Override
			public void onSuccess(final List<?> paramList) {
				if ((paramList == null) || (paramList.size() == 0)) {
					mAbSqliteStorage.insertData(mUser, mUserDao, new AbDataInsertListener() {
						@Override
						public void onSuccess(final long id) {}

						@Override
						public void onFailure(final int errorCode, final String errorMessage) {
							//showToast(errorMessage);
						}
					});
				}
			}
		});
	}

	/**
	 * IM配置
	 */
	public void initIMConfig() {
		/**
		IMConfig mIMConfig = new IMConfig();

		mIMConfig.setXmppHost(mSharedPreferences.getString(
				IMConstant.XMPP_HOST,
				getResources().getString(R.string.xmpp_host)));

		mIMConfig.setXmppPort(mSharedPreferences.getInt(
				IMConstant.XMPP_PORT,
				getResources().getInteger(R.integer.xmpp_port)));

		mIMConfig.setXmppServiceName(mSharedPreferences.getString(
				IMConstant.XMPP_SEIVICE_NAME,
				getResources().getString(R.string.xmpp_service_name)));

		mIMConfig.setNovisible(mSharedPreferences.getBoolean(
				IMConstant.IS_NOVISIBLE,
				getResources().getBoolean(R.bool.is_novisible)));
		
		IMUtil.setIMConfig(this.getApplicationContext(),mIMConfig);
		**/
	}

	/**
	 * 上次登录参数
	 */
	private void initLoginParams() {
		String userName = mSharedPreferences.getString(Constant.USERNAMECOOKIE, null);
		String userPwd = mSharedPreferences.getString(Constant.USERPASSWORDCOOKIE, null);
		String userCookie = mSharedPreferences.getString(Constant.USERCOOKIE, null);
		//Boolean userPwdRemember = preferences.getBoolean(Constant.USERPASSWORDREMEMBERCOOKIE, false);
		if (userName != null) {
			mUser.username = userName;
			mUser.password = userPwd;
			mUser.cookie = userCookie;
		}
	}
}
