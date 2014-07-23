package com.hui.mybox.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class MediaUtil {
	
	public Bitmap getImagefromMp3(Context context,String filePath) {  
	    String path = null;  
	    Cursor c = context.getContentResolver().query(  
	            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,  
	            MediaStore.Audio.Media.DEFAULT_SORT_ORDER);  
	    // System.out.println(c.getString(c.getColumnIndex("_data")));  
	    if (c.moveToFirst()) {  
	        do {  
	            // 通过Cursor 获取路径，如果路径相同则break；  
	            path = c.getString(c  
	                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));  
	            // 查找到相同的路径则返回，此时cursorPosition 便是指向路径所指向的Cursor 便可以返回了  
	            if (path.equals(filePath)) {  
	                // System.out.println("audioPath = " + path);  
	                // System.out.println("filePath = " + filePath);  
	                // cursorPosition = c.getPosition();  
	                break;  
	            }  
	        } while (c.moveToNext());  
	    }  
	    // 这两个没有什么作用，调试的时候用  
	    // String audioPath = c.getString(c  
	    // .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));  
	    //  
	    // System.out.println("audioPath = " + audioPath);  
	    int album_id = c.getInt(c  
                .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)) ;
	    Bitmap bm = null;  
	    String albumArt = getAlbumArt(context,album_id);  
	    if (albumArt == null) {  
	    } else {  
	        bm = BitmapFactory.decodeFile(albumArt);  
	    } 
	    return bm;  
	}  
	
	/** 
	 *  
	 * 功能 通过album_id查找 album_art 如果找不到返回null 
	 *  
	 * @param album_id 
	 * @return album_art 
	 */  
	private String getAlbumArt(Context context,int album_id) {  
	    String mUriAlbums = "content://media/external/audio/albums";  
	    String[] projection = new String[] { "album_art" };  
	    Cursor cur = context.getContentResolver().query(  
	            Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),  
	            projection, null, null, null);  
	    String album_art = null;  
	    if (cur.getCount() > 0 && cur.getColumnCount() > 0) {  
	        cur.moveToNext();  
	        album_art = cur.getString(0);  
	    }  
	    cur.close();  
	    cur = null;  
	    return album_art;  
	}

}
