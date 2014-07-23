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
	            // ͨ��Cursor ��ȡ·�������·����ͬ��break��  
	            path = c.getString(c  
	                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));  
	            // ���ҵ���ͬ��·���򷵻أ���ʱcursorPosition ����ָ��·����ָ���Cursor ����Է�����  
	            if (path.equals(filePath)) {  
	                // System.out.println("audioPath = " + path);  
	                // System.out.println("filePath = " + filePath);  
	                // cursorPosition = c.getPosition();  
	                break;  
	            }  
	        } while (c.moveToNext());  
	    }  
	    // ������û��ʲô���ã����Ե�ʱ����  
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
	 * ���� ͨ��album_id���� album_art ����Ҳ�������null 
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
