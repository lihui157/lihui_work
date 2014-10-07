package net.cstong.android.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;

public class MessageApi {
	public static final String TAG = "MessageApi";

	public static void noticeList(final Context context, final AbRequestParams params, final AbStringHttpResponseListener listener) {
		ApiHelper.post(context, ApiHelper.URL_MESSAGE_NOTICELIST, params, listener);
	}

	public static void messageList(final Context context, final AbRequestParams params, final AbStringHttpResponseListener listener) {
		ApiHelper.post(context, ApiHelper.URL_MESSAGE_MESSAGELIST, params, listener);
	}

	public static NoticeListInfo parseNoticeListResponse(final String response) {
		try {
			JSONObject obj = new JSONObject(response);
			int result = obj.getInt("result");
			String msg = obj.getString("message");
			if (result == 0) {
				JSONObject data = obj.getJSONObject("data");
				NoticeListInfo noticeListInfo = new NoticeListInfo();
				noticeListInfo.noticeData = new ArrayList<NoticeInfo>();
				noticeListInfo.unreadCount = data.getInt("unreadCount");
				JSONArray noticeList = data.getJSONArray("noticeList");
				for (int i = 0; i < noticeList.length(); i++) {
					NoticeInfo noticeInfo = new NoticeInfo();
					JSONObject info = (JSONObject) noticeList.get(i);
					noticeInfo.id = info.getInt("id");
					noticeInfo.uid = info.getInt("uid");
					noticeInfo.typeid = info.getInt("typeid");
					noticeInfo.param = info.getInt("param");
					noticeInfo.isRead = info.getInt("is_read") != 0 ? true : false;
					noticeInfo.isIgnored = info.getInt("is_ignore") != 0 ? true : false;
					noticeInfo.title = info.getString("title");
					noticeInfo.createdTime = info.getInt("created_time");
					noticeInfo.modifiedTime = info.getInt("modified_time");
					noticeInfo.content = info.getString("displayHtml");
					noticeListInfo.noticeData.add(noticeInfo);
				}
				return noticeListInfo;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static MessageListInfo parseMessageListResponse(final String response) {
		try {
			JSONObject obj = new JSONObject(response);
			int result = obj.getInt("result");
			String msg = obj.getString("message");
			Log.d(TAG, "result:" + String.valueOf(result) + " message:" + msg);
			if (result == 0) {
				JSONObject data = obj.getJSONObject("data");
				MessageListInfo messageListInfo = new MessageListInfo();
				messageListInfo.messageData = new ArrayList<MessageInfo>();
				messageListInfo.count = data.getInt("count");
				messageListInfo.pageIndex = data.getInt("page");
				messageListInfo.pageSize = data.getInt("perpage");
				JSONArray messageList = data.getJSONArray("dialogs");
				for (int i = 0; i < messageList.length(); i++) {
					MessageInfo messageInfo = new MessageInfo();
					JSONObject info = (JSONObject) messageList.get(i);
					messageInfo.dialogId = info.getInt("dialog_id");
					messageInfo.toUid = info.getInt("to_uid");
					messageInfo.fromUid = info.getInt("from_uid");
					messageInfo.unreadCount = info.getInt("unread_count");
					messageInfo.messageCount = info.getInt("message_count");
					messageInfo.fromAvatar = info.getString("from_avatar");
					messageInfo.modifiedTime = info.getInt("modified_time");
					JSONObject lastMessage = info.getJSONObject("last_message");
					messageInfo.lastFromUid = lastMessage.getInt("from_uid");
					messageInfo.lastFromUsername = lastMessage.getString("from_username");
					messageInfo.lastToUid = lastMessage.getInt("to_uid");
					messageInfo.lastToUsername = lastMessage.getString("to_username");
					messageInfo.lastContent = lastMessage.getString("content");
					messageListInfo.messageData.add(messageInfo);
				}
				return messageListInfo;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class NoticeInfo {
		public int id;
		public int uid;
		public int typeid;
		public int param;
		public boolean isRead;
		public boolean isIgnored;
		public String title;
		public String content;
		public int createdTime;
		public int modifiedTime;
		public String type;
	}

	public static class NoticeListInfo {
		public int unreadCount;
		public List<NoticeInfo> noticeData;
	}

	public static class MessageInfo {
		public int dialogId;
		public int toUid;
		public int fromUid;
		public int unreadCount;
		public int messageCount;
		public String fromAvatar;
		public int lastFromUid;
		public String lastFromUsername;
		public int lastToUid;
		public String lastToUsername;
		public String lastContent;
		public int modifiedTime;
	}

	public static class MessageListInfo {
		public int count;
		public int pageIndex;
		public int pageSize;
		public List<MessageInfo> messageData;
	}
}
