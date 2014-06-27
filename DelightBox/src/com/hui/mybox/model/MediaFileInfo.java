package com.hui.mybox.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ý���ļ���Ϣ��
 * ���ڷ�װ����ý��ı�����Ϣ������FileFilterʹ��
 * @author Administrator
 *
 */
public class MediaFileInfo {

	/**
	 * �ļ���
	 */
	 public final static int FILE_TYPE_FOLDER = 0;
	 /**
		 * ͼƬ
		 */
		final public static int FILE_TYPE_IMG = 1;
	/**
	 * ��Ƶ
	 */
	 public final  static int FILE_TYPE_AUDIO = 3;
	
	/**
	 * ��Ƶ
	 */
	 public final static int FILE_TYPE_VIDEO = 2;
	/**
	 * δ����
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
