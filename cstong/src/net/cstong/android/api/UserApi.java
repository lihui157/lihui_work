package net.cstong.android.api;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.model.AbResult;

public class UserApi {
	public static final String TAG = "UserApi";

	public static void register(final Context context, final AbRequestParams params, final AbStringHttpResponseListener listener) {
		ApiHelper.post(context, ApiHelper.URL_USER_REGISTER, params, listener);
	}

	public static void login(final Context context, final AbRequestParams params, final AbStringHttpResponseListener listener) {
		ApiHelper.post(context, ApiHelper.URL_USER_LOGIN, params, listener);
	}

	public static UserInfo parseLoginResponse(final String response) {
		UserInfo userInfo = new UserInfo();
		try {
			JSONObject obj = new JSONObject(response);
			userInfo.setResultCode(obj.getInt("result"));
			userInfo.setResultMsg(obj.getString("message"));
			if (userInfo.getResultCode() == 0) {
				JSONObject data = obj.getJSONObject("data");
				JSONObject user = data.getJSONObject("user");
				JSONObject info = user.getJSONObject("info");
				userInfo.username = info.getString("username");
				userInfo.password = info.getString("password");
				userInfo.uid = info.getInt("uid");
				userInfo.gid = info.getInt("groupid");
				userInfo.memberid = info.getInt("memberid");
				userInfo.email = info.getString("email");
				userInfo.avatar_s = user.getString("avatar_small");
				userInfo.avatar_m = user.getString("avatar_middle");
				userInfo.avatar = user.getString("avatar");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userInfo;
	}

	public static String parseRegisterResponse(final String response) {
		try {
			JSONObject obj = new JSONObject(response);
			int result = obj.getInt("result");
			String msg = obj.getString("message");
			if (result == 0) {
				JSONObject data = obj.getJSONObject("data");
				String statu = data.getString("_statu");
				if (statu.length() > 0) {
					return statu;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class UserInfo extends AbResult implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String username;
		public String password;
		public String avatar;
		public String avatar_s;
		public String avatar_m;
		public int uid;
		public int gid;
		public String email;
		public int memberid;
		public String cookie;
		public String mobile;
	}
}
