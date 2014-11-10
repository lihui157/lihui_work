package com.jhgzs.mobsite.obj;

/**
 * Ä¿Â¼¶ÔÏó
 * @author lihui
 *
 */
public class FileItemObj {

	private String fileName;
	
	private String url;
	
	private boolean directory;
	
	private String fileLength;
	
	private String lastTime;

	
	
	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean isDirectory) {
		this.directory = isDirectory;
	}

	public String getFileLength() {
		return fileLength;
	}

	public void setFileLength(String fileLength) {
		this.fileLength = fileLength;
	}
	
	
	
}
