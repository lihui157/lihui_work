package net.cstong.android.api;

import java.util.ArrayList;
import java.util.List;

import net.cstong.android.api.ForumApi.ThreadInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;

public class FocusApi {
	public static final String TAG = "FocusApi";

	public static void index(final Context context, final AbRequestParams params, final AbStringHttpResponseListener listener) {
		ApiHelper.post(context, ApiHelper.URL_FOCUS, params, listener);
	}

	public static void ad(final Context context, final AbStringHttpResponseListener listener) {
		ApiHelper.post(context, ApiHelper.URL_FOCUS_AD, listener);
	}

	public static List<ThreadInfo> parseFocusResponse(final String response) {
		JSONObject obj;
		List<ThreadInfo> threads = new ArrayList<ThreadInfo>();
		try {
			obj = new JSONObject(response);
			int result = obj.getInt("result");
			if (result == 0) {
				String msg = obj.getString("message");
				JSONObject data = obj.getJSONObject("data");
				JSONArray threadList = data.getJSONArray("threadList");
				for (int i = 0; i < threadList.length(); i++) {
					JSONObject tmp = threadList.getJSONObject(i);
					ThreadInfo threadInfo = new ThreadInfo();
					threadInfo.fid = tmp.getInt("fid");
					threadInfo.tid = tmp.getInt("tid");
					threadInfo.createUid = tmp.getInt("uid");
					threadInfo.title = tmp.getString("title");
					threadInfo.hits = tmp.getInt("hits");
					threadInfo.replies = tmp.getInt("replies");
					threadInfo.createdUser = tmp.getString("author");
					threadInfo.createdTime = tmp.getString("timestr");
					threadInfo.avatar = tmp.getString("avatar_m");
					if (tmp.isNull("imagelist")) {
						threadInfo.imgs = null;
					} else {
						threadInfo.imgs = new ArrayList<String>();
						JSONArray imgs = tmp.getJSONArray("imagelist");
						if (imgs != null) {
							for (int j = 0; j < imgs.length(); j++) {
								String img = imgs.getJSONObject(j).getString("thumbpath");
								threadInfo.imgs.add(img);
							}
						}
					}
					threads.add(threadInfo);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return threads;
	}

	public static JSONArray parseFocusAdResponse(final String response) {
		return null;
	}
}
