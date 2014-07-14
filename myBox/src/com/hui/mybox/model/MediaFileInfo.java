package com.hui.mybox.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 媒体文件信息类
 * 用于封装本地媒体的表面信息，方便FileFilter使用
 * @author Administrator
 *
 */
public class MediaFileInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 文件夹
	 */
	 public final static int FILE_TYPE_FOLDER = 0;
	 /**
		 * 图片
		 */
		final public static int FILE_TYPE_IMG = 1;
	/**
	 * 音频
	 */
	 public final  static int FILE_TYPE_AUDIO = 3;
	
	/**
	 * 视频
	 */
	 public final static int FILE_TYPE_VIDEO = 2;
	/**
	 * 未分类
	 */
	 public final static int FILE_TYPE_NO = 4;

	private String path;
	
	private String fileName;
	
	private int fileType;
	
	private long length;
	
	private long lastModifTime;
	

	public long getLastModifTime() {
		return lastModifTime;
	}

	public void setLastModifTime(long lastModifTime) {
		this.lastModifTime = lastModifTime;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}
	
	public static int getFileStyle(String path){
	       File f = new File(path);
	       if(f.isFile()){
	           String ext = getExtensionName(path);
	           if(ext.equals("")) return MediaFileInfo.FILE_TYPE_NO;
	           if(ext.equals("jpg")||ext.equals("tif")||ext.equals("png")||ext.equals("gif")||ext.equals("bmp")){
	               return MediaFileInfo.FILE_TYPE_IMG;
	           }
	           if(ext.equals("wav")||ext.equals("ram")||ext.equals("mid")||ext.equals("mp3")){
	               return MediaFileInfo.FILE_TYPE_AUDIO;
	           }
	           if(ext.equals("avi")||ext.equals("rm")||ext.equals("mpg")||ext.equals("mov")||ext.equals("asf")||ext.equals("mp4")){
	               return MediaFileInfo.FILE_TYPE_VIDEO;
	           }
	       }else{
	           return MediaFileInfo.FILE_TYPE_FOLDER; 
	       }
	       
	       return MediaFileInfo.FILE_TYPE_NO;
	   }
	
	
	private static String getExtensionName(String filename) {   
     if ((filename != null) && (filename.length() > 0)) {   
         int dot = filename.lastIndexOf('.');   
         if ((dot >-1) && (dot < (filename.length() - 1))) {   
             return filename.substring(dot + 1);   
         }   
     }   
     return "";   
 }

	
	
}
