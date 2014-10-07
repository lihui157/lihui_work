package net.cstong.android.api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.model.AbResult;

public class PostApi {
	protected static String TAG = "PostApi";

	public static class UploadInfo extends AbResult {
		public String path;
		public String thumbPath;
		public int aid;
	}

	public static class TopicTypeInfo {
		public int topicId;
		public String topicName;
	}

	public static class PostResult extends AbResult {
		public int fid;
		public int tid;
	}

	public static class TopicTypeList extends AbResult {
		public int fid;
		public ArrayList<TopicTypeInfo> typeList;
	}

	public static void getPostVar(final Context context, final AbStringHttpResponseListener listener) {
		ApiHelper.post(context, ApiHelper.URL_POST_GETPOSTVAR, listener);
	}

	public static void post(final Context context, final AbRequestParams params, final AbStringHttpResponseListener listener) {
		ApiHelper.post(context, ApiHelper.URL_POST_POST, params, listener);
	}

	public static void upload(final Context context, final AbRequestParams params, final AbStringHttpResponseListener listener) {
		ApiHelper.post(context, ApiHelper.URL_POST_UPLOAD, params, listener);
	}

	public static void reply(final Context context, final AbRequestParams params, final AbStringHttpResponseListener listener) {
		ApiHelper.post(context, ApiHelper.URL_POST_REPLY, params, listener);
	}

	public static AbResult parseReplyResponse(final String response) {
		AbResult result = new AbResult();
		try {
			JSONObject obj = new JSONObject(response);
			result.setResultCode(obj.getInt("result"));
			result.setResultMsg(obj.getString("message"));
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(1);
			result.setResultMsg("回复失败，请稍后再试");
		}
		return result;
	}

	public static PostResult parsePostResponse(final String response) {
		PostResult result = new PostResult();
		try {
			JSONObject obj = new JSONObject(response);
			result.setResultCode(obj.getInt("result"));
			result.setResultMsg(obj.getString("message"));
			JSONObject postObj = obj.getJSONObject("data").getJSONObject("post");
			result.tid = obj.getJSONObject("data").getInt("tid");
			result.fid = postObj.getJSONObject("forum").getInt("fid");
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(1);
			if ((result.getResultMsg() == null) || (result.getResultMsg() == "")) {
				result.setResultMsg("发贴失败，请稍后失败");
			}
		}
		return result;
	}

	public static TopicTypeList parseGetPostVarResponse(final String response, final int fid) {
		TopicTypeList info = new TopicTypeList();
		info.typeList = new ArrayList<TopicTypeInfo>();
		try {
			JSONObject obj = new JSONObject(response);
			int code = obj.getInt("result");
			if (code == 0) {
				info.setResultCode(code);
				info.setResultMsg(obj.getString("message"));
				JSONObject data = obj.getJSONObject("data").getJSONObject("forumList");
				JSONArray cate = data.getJSONArray("cate");
				JSONObject forums = data.getJSONObject("forum");
				for (int i = 0; i < cate.length(); i++) {
					JSONArray cateObj = cate.getJSONArray(i);
					int cateId = cateObj.optInt(0);
					JSONArray types = forums.getJSONArray(String.valueOf(cateId));
					for (int j = 0; j < types.length(); j++) {
						JSONArray alltypes = types.getJSONArray(j).getJSONObject(2).getJSONArray("all_types");
						for (int k = 0; k < alltypes.length(); k++) {
							JSONObject alltypesItem = alltypes.getJSONObject(k);
							int typefid = alltypesItem.getInt("fid");
							if ((fid != -1) && (typefid == fid)) {
								TopicTypeInfo typeInfo = new TopicTypeInfo();
								typeInfo.topicId = alltypesItem.getInt("id");
								typeInfo.topicName = alltypesItem.getString("name");
								info.typeList.add(typeInfo);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	public static UploadInfo parseUploadResponse(final String response) {
		UploadInfo info = new UploadInfo();
		try {
			JSONObject obj = new JSONObject(response);
			info.setResultCode(obj.getInt("result"));
			info.setResultMsg(obj.getString("message"));
			if (info.getResultCode() == 0) {
				JSONObject data = obj.getJSONObject("data").getJSONObject("data");
				info.path = data.getString("path");
				info.thumbPath = data.getString("thumbpath");
				info.aid = data.getInt("aid");
			}
		} catch (Exception e) {
			e.printStackTrace();
			info.setResultCode(1);
			info.setResultMsg("上传失败，请稍后再试");
		}
		return info;
	}
}
