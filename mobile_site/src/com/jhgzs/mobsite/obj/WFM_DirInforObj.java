package com.jhgzs.mobsite.obj;

import java.util.List;

/**
 * Ŀ¼��Ϣ����
 * @author lihui
 *
 */
public class WFM_DirInforObj {
	
	private String currentPath;
	
	private List<WFM_FileItemObj> fileList;

	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	public List<WFM_FileItemObj> getFileList() {
		return fileList;
	}

	public void setFileList(List<WFM_FileItemObj> fileList) {
		this.fileList = fileList;
	}
	
	

}
