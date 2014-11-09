package com.jhgzs.mybox.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.jhgzs.mybox.model.bean.MediaFileInfo;

public class VideoManager {
	
	private static final String TAG = "VideoManager";
	
	public List<MediaFileInfo> getVideos(Context context){
		
		List<MediaFileInfo> list = new ArrayList<MediaFileInfo>();
		
		Uri mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		ContentResolver mContentResolver = context.getContentResolver();

		//ֻ��ѯjpeg��png��ͼƬ,��ʱ�併��
		Cursor mCursor = mContentResolver.query(mVideoUri, null,
				null,//MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
				null,//new String[] { "image/jpeg", "image/png" }, 
				MediaStore.Video.Media.DATE_MODIFIED + " DESC");

		while (mCursor.moveToNext()) {
			MediaFileInfo info = new MediaFileInfo();
			info.setFileName(mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
			info.setFileType(MediaFileInfo.getFileStyle(mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA))));
			info.setLastModifTime(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)));
			info.setLength(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
			info.setPath(mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA)));
			list.add(info);
		}

		mCursor.close();
		//֪ͨHandlerɨ��ͼƬ���

	 return list;
	
	}

}
