package com.hui.mybox.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 媒体文件信息类
 * 用于封装本地媒体的表面信息，方便FileFilter使用
 * @author Administrator
 *
 */
public class MediaFileInfo {

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

	
	
}
