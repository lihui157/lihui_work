package com.jhgzs.mobsite.obj;

import java.util.List;

/**
 * 目录信息对象
 * @author lihui
 *
 */
public class DirInforObj {
	
	private String currentPath;
	
	private List<FileItemObj> fileList;

	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	public List<FileItemObj> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileItemObj> fileList) {
		this.fileList = fileList;
	}
	
	

}
