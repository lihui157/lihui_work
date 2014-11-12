package com.jhgzs.mybox.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.jhgzs.mybox.model.bean.MediaFileInfo;

public class PhotoManager {
	
	private static final String TAG = "PhotoManager";
	
	public List<MediaFileInfo> getImages(Context context){
		
		List<MediaFileInfo> list = new ArrayList<MediaFileInfo>();
		
		Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		ContentResolver mContentResolver = context.getContentResolver();

		//ֻ��ѯjpeg��png��ͼƬ,��ʱ�併��
		Cursor mCursor = mContentResolver.query(mImageUri, null,
				null,//MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
				null,//new String[] { "image/jpeg", "image/png" }, 
				MediaStore.Images.Media.DATE_MODIFIED + " DESC");

		while (mCursor.moveToNext()) {
			MediaFileInfo info = new MediaFileInfo();
			info.setFileName(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
			info.setFileType(MediaFileInfo.getFileStyle(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA))));
			info.setLastModifTime(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)));
			info.setLength(mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE)));
			info.setPath(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
			if(info.getLength()!=0){
				list.add(info);
			}
			
		}

		mCursor.close();
		//֪ͨHandlerɨ��ͼƬ���

	 return list;
	
	}

}
