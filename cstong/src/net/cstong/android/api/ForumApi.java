package net.cstong.android.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.cstong.android.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;

public class ForumApi {
	public static final String TAG = "ForumApi";

	public static final class ForumInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public int fid; // 0 聚焦页
		public int tabId;
		public String title;
		public String url;
	}

	public static class ThreadInfo implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public int tid;
		public int fid;
		public String title;
		public int createUid;
		public int hits;
		public int replies;
		public String avatar;
		public List<String> imgs;
		public String createdUser;
		public String createdTime;
	}

	public static class ThreadDetail implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public int tid;
		public int fid;
		public int pid;
		public int lou;
		public String title;
		public int topicType;
		public String highlight;
		public int ifshield;
		public int topped;
		public int disabled;
		public int ischeck;
		public int replies;
		public int hits;
		public int likeCount;
		public int special;
		public int ifupload;
		public int createdTime;
		public String createdUsername;
		public String createdIp;
		public int createdUserid;
		public String modifiedIp;
		public int modifiedTime;
		public String modifiedUsername;
		public int lastPostTime;
		public int lastPostUserid;
		public String lastPostUsername;
		public int replyNotice;
		public int replyTopped;
		public int isdesc;
		public int useubb;
		public int usehtml;
		public int aids;
		public String content;
		public int sellCount;
		public String reminds;
		public int wordVersion;
		public String ipFrom;
		public String avatar;
	}

	public static class ThreadDetailList {
		public List<ThreadDetail> detailList;
		public int pageTotal; // 总页数
		public int pageIndex; // 页码
		public int pageSize; // 每页条数
		public int totalCount; // 总条数

		public ThreadDetailList() {
			detailList = new ArrayList<ThreadDetail>();
			pageSize = 20;
			pageIndex = 1;
			pageTotal = 1;
			totalCount = 1;
		}
	}

	public static void index(final Context context, final AbStringHttpResponseListener listener) {
		ApiHelper.post(context, ApiHelper.URL_FORUM, listener);
	}

	public static void readThread(final Context context, final AbRequestParams params, final AbStringHttpResponseListener listener) {
		ApiHelper.post(context, ApiHelper.URL_FORUM_READTHREAD, params, listener);
	}

	public static void threadList(final Context context, final AbRequestParams params, final AbStringHttpResponseListener listener) {
		ApiHelper.post(context, ApiHelper.URL_FORUM_THREADLIST, params, listener);
	}

	public static List<ForumInfo> parseIndexResponse(final String response) {
		JSONObject obj;
		try {
			obj = new JSONObject(response);
			int result = obj.getInt("result");
			if (result == 0) {
				String msg = obj.getString("message");
				JSONObject data = obj.getJSONObject("data");
				JSONObject forumsObj = data.getJSONObject("forums");
				Map<String, Object> forumsMap = ApiHelper.getJsonMap(forumsObj);

				List<ForumInfo> forums = new ArrayList<ForumInfo>();
				Iterator<String> ite = forumsMap.keySet().iterator();
				int i = 1;
				while (ite.hasNext()) {
					String key = ite.next();
					JSONObject value = (JSONObject) forumsMap.get(key);
					ForumInfo info = new ForumInfo();
					info.fid = value.getInt("fid");
					info.tabId = i;
					info.title = ApiHelper.getForumTabTitle(info.fid);
					if (info.title.length() == 0) {
						continue;
					}
					forums.add(info);
					i++;
				}
				return forums;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static List<ThreadInfo> parseThreadListResponse(final String response) {
		JSONObject obj;
		List<ThreadInfo> threads = new ArrayList<ThreadInfo>();
		try {
			obj = new JSONObject(response);
			int result = obj.getInt("result");
			if (result == 0) {
				String msg = obj.getString("message");
				JSONObject data = obj.getJSONObject("data");
				JSONObject threadList = data.getJSONObject("threadList");
				JSONArray threaddb = threadList.getJSONArray("threaddb");
				for (int i = 0; i < threaddb.length(); i++) {
					JSONObject tmp = threaddb.getJSONObject(i);
					ThreadInfo threadInfo = new ThreadInfo();
					threadInfo.fid = tmp.getInt("fid");
					threadInfo.tid = tmp.getInt("tid");
					threadInfo.createUid = tmp.getInt("created_userid");
					threadInfo.title = tmp.getString("subject");
					threadInfo.hits = tmp.getInt("hits");
					threadInfo.replies = tmp.getInt("replies");
					threadInfo.createdUser = tmp.getString("created_username");
					threadInfo.createdTime = Utils.timestampToString(tmp.getLong("created_time"));
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
			Log.d(TAG, "response:" + response);
		}
		return threads;
	}

	public static ThreadDetailList parseReadThreadResponse(final String response) {
		ThreadDetailList threadDetailList = new ThreadDetailList();
		threadDetailList.detailList = new ArrayList<ThreadDetail>();
		try {
			JSONObject obj = new JSONObject(response);
			int result = obj.getInt("result");
			if (result == 0) {
				String msg = obj.getString("message");
				JSONObject data = obj.getJSONObject("data");
				JSONObject threadDisplay = data.getJSONObject("threadDisplay");
				threadDetailList.pageTotal = threadDisplay.getInt("maxpage");
				threadDetailList.pageIndex = threadDisplay.getInt("page");
				threadDetailList.pageSize = threadDisplay.getInt("perpage");
				threadDetailList.totalCount = threadDisplay.getInt("total");
				JSONArray readdb = threadDisplay.getJSONArray("readdb");
				for (int i = 0; i < readdb.length(); i++) {
					JSONObject tmp = readdb.getJSONObject(i);
					ThreadDetail threadDetail = new ThreadDetail();
					threadDetail.fid = tmp.getInt("fid");
					threadDetail.tid = tmp.getInt("tid");
					if (tmp.has("pid")) {
						threadDetail.pid = tmp.getInt("pid");
					} else {
						threadDetail.pid = 0;
					}
					threadDetail.lou = tmp.getInt("lou");
					threadDetail.title = tmp.getString("subject");
					//threadDetail.topicType = tmp.getInt("topic_type");
					//threadDetail.highlight = tmp.getString("highlight");
					threadDetail.ifshield = tmp.getInt("ifshield");
					//threadDetail.topped = tmp.getInt("topped");
					threadDetail.disabled = tmp.getInt("disabled");
					threadDetail.ischeck = tmp.getInt("ischeck");
					if (tmp.has("hits")) {
						threadDetail.hits = tmp.getInt("hits");
					} else {
						threadDetail.hits = 0;
					}
					threadDetail.replies = tmp.getInt("replies");
					threadDetail.likeCount = tmp.getInt("like_count");
					//threadDetail.special = tmp.getInt("special");
					//threadDetail.ifupload = tmp.getInt("ifupload");
					threadDetail.createdTime = tmp.getInt("created_time");
					threadDetail.createdUsername = tmp.getString("created_username");
					threadDetail.createdIp = tmp.getString("created_ip");
					threadDetail.createdUserid = tmp.getInt("created_userid");
					threadDetail.modifiedIp = tmp.getString("modified_ip");
					threadDetail.modifiedUsername = tmp.getString("modified_username");
					threadDetail.modifiedTime = tmp.getInt("modified_time");
					threadDetail.content = tmp.getString("content");
					/**
					threadDetail.lastPostTime = tmp.getInt("lastpost_time");
					threadDetail.lastPostUserid = tmp.getInt("lastpost_userid");
					threadDetail.lastPostUsername = tmp.getString("lastpost_username");
					threadDetail.replyNotice = tmp.getInt("reply_notice");
					threadDetail.replyTopped = tmp.getInt("reply_topped");
					threadDetail.isdesc = tmp.getInt("isdesc");
					threadDetail.useubb = tmp.getInt("useubb");
					threadDetail.usehtml = tmp.getInt("usehtml");
					threadDetail.aids = tmp.getInt("aids");
					threadDetail.sellCount = tmp.getInt("sell_count");
					threadDetail.reminds = tmp.getString("reminds");
					threadDetail.wordVersion = tmp.getInt("word_version");
					**/
					threadDetail.ipFrom = tmp.getString("ipfrom");
					threadDetail.avatar = tmp.getString("avatar");
					threadDetailList.detailList.add(threadDetail);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return threadDetailList;
	}
}
