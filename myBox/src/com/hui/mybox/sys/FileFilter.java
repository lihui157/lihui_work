package com.hui.mybox.sys;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;





import com.google.gson.Gson;
import com.hui.mybox.model.MediaFileInfo;
import com.hui.mybox.utils.FileUtil;
import com.hui.mybox.utils.LogUtil;

import android.content.Context;
import android.os.Environment;

/**
 * ����ý���ļ������߳� ���߳���ɹ������£� 1. �����˳�����ý���ļ���Ϣ�ַ���ͼƬ����Ƶ����Ƶ�������������л��� 2.
 * �����ļ��������ʽ�ı���ý���ļ�����
 * 
 * @author lihui
 * 
 */
public class FileFilter implements Runnable {

	private static final String TAG = "FileFilter";
	
	private Context c;
	
	private long size = 1024*50;
	
	private MediaFileInfo mfi;

	public FileFilter(Context c){
		this.c = c;
	}
	
	@Override
	public void run() {
		LogUtil.debug(TAG, "FileFilter is run... ");
		// ��ȡ��Ŀ¼·��
		try {
			String rootPath = Environment.getExternalStorageDirectory()
					.getCanonicalPath();
			String fileRoot = rootPath+Config.Sys.APP_ROOT;
			
			//��ʱ����
//			MediaApp.folderMapTemp = new HashMap<String, LocalBrowseList>();
			MediaApp.imgListTemp = new LinkedList<MediaFileInfo>();
			MediaApp.videoListTemp = new LinkedList<MediaFileInfo>();
			MediaApp.audioListTemp = new LinkedList<MediaFileInfo>();
			
			//��ʼ����
			parseDirectory(rootPath);
			
			//д���ļ�����
			FileUtil.newFile(fileRoot+Config.Sys.IMG_INDEX_FILE, new Gson().toJson(MediaApp.imgListTemp));
			FileUtil.newFile(fileRoot+Config.Sys.MUSIC_INDEX_FILE, new Gson().toJson(MediaApp.audioListTemp));
			FileUtil.newFile(fileRoot+Config.Sys.VIDEO_INDEX_FILE, new Gson().toJson(MediaApp.videoListTemp));
//			FileUtil.writeText(rootPath+Config.System.LOCAL_FOLDER_LIST_PATH, new Gson().toJson(MediaApp.folderMapTemp));
//			
			//�滻����ʽ����
//			MediaApp.folderMap = MediaApp.folderMapTemp;
			MediaApp.imgList = MediaApp.imgListTemp;
			MediaApp.videoList = MediaApp.videoListTemp;
			MediaApp.audioList = MediaApp.audioListTemp;
			
			//�����ʱ����
//			MediaApp.folderMapTemp = null;
			MediaApp.imgListTemp = null;
			MediaApp.videoListTemp = null;
			MediaApp.audioListTemp = null;
			
			//֪ͨ���������и���
//			DlnaUtil.sendUIActionBroad(c, Config.Dlna.ACTION_RET_LOCAL_SEARCH); 
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	
	/**
	 * ��������ý���ļ�Ŀ¼ �˷���ʵ�ֵݹ����
	 * 
	 * @param path
	 *            ָ��Ҫ������Ŀ¼��Ĭ��null��λ��sdcard
	 * @return boolean ��ǰĿ¼�Ƿ���ý���ļ����� true �� false
	 */
	private boolean parseDirectory(String path) {
		boolean b = false;
		try {
			File f;

			if (path == null || path.equals("")) {
				// ����Ĭ�ϵݹ�ĸ�·��
				f = new File(Environment.getExternalStorageDirectory()
						.getCanonicalPath());
			} else {
				f = new File(path);
			}
			if (f != null) {
				// ÿһ��Ŀ¼��ϳ�һ��Arraylist����ͨ��Ŀ¼��·����Ϊkey����MAP��
				ArrayList<MediaFileInfo> list = new ArrayList<MediaFileInfo>();
				File[] fs = f.listFiles();
				if (fs != null) {
//					for (int i = 0; i < fs.length; i++) {
					for(File f_temp:fs){
						boolean bTemp = false;
						// �ж��ļ�����
						if (f_temp.isDirectory()) {
							// �ݹ����
							bTemp = parseDirectory(f_temp.getCanonicalPath());
						}
						
						mfi = new MediaFileInfo();
						mfi.setFileName(f_temp.getName());
						mfi.setPath(f_temp.getCanonicalPath());
						mfi.setLength(f_temp.length());
						mfi.setLastModifTime(f_temp.lastModified());
						mfi.setFileType(MediaFileInfo.getFileStyle(f_temp
								.getCanonicalPath()));
						// ����ļ�������ͼƬ�����ظ���Ϣ��ͼƬ����
						if (mfi.getFileType() == MediaFileInfo.FILE_TYPE_IMG&&mfi.getLength()>size) {
							MediaApp.imgListTemp.add(mfi); 
							b = true;
						}
						// ����ļ���������Ƶ�����ظ���Ϣ����Ƶ����
						if (mfi.getFileType() == MediaFileInfo.FILE_TYPE_VIDEO&&mfi.getLength()>size) {
							MediaApp.videoListTemp.add(mfi);
							b = true;
						}
						// ����ļ���������Ƶ�����ظ���Ϣ����Ƶ����
						if (mfi.getFileType() == MediaFileInfo.FILE_TYPE_AUDIO&&mfi.getLength()>size) {
							MediaApp.audioListTemp.add(mfi);
							b = true;
						}
						//�������ļ����к���ý���ļ��ģ����ļ���ͬ����Ϊ��ý��
						if(bTemp){
							b = true;
						}
						// ����ļ���Ϣ��list
						// LogUtil.debug(TAG, "add "+mfi.getFileName());
						if ((mfi.getFileType() != MediaFileInfo.FILE_TYPE_NO && mfi.getFileType()!=MediaFileInfo.FILE_TYPE_FOLDER)
								|| (bTemp && mfi.getFileType()==MediaFileInfo.FILE_TYPE_FOLDER)) {
							list.add(mfi);
						}

					}
					//��ǰĿ¼����ý���ļ�����������
					if(b){
						// ��ǰĿ¼�㼶������ϣ���list put��map�У��γ�Ŀ¼����
//						LocalBrowseList lbl = new LocalBrowseList();
//						lbl.setObjlist(list);
//						lbl.setPath(f.getCanonicalPath());
						String[] arr = f.getCanonicalPath().split("/");
//						lbl.setPathArr((List<String>) Arrays.asList(arr));
//						lbl.setParentobjid(f.getCanonicalPath().substring(0,
//								f.getCanonicalPath().lastIndexOf("/")));

//						MediaApp.folderMapTemp.put(f.getCanonicalPath(), lbl);
//						String indexFileName = Environment.getExternalStorageDirectory()
//								.getCanonicalPath()+Config.System.INDEX_CACHE_PATH+"/"+MD5Util.MD5(f.getCanonicalPath())+".txt";
//						FileUtil.writeText(indexFileName, new Gson().toJson(lbl));
//						LogUtil.debug(TAG, "parseDirectory",
//								"�ѱ�����" + f.getCanonicalPath() + " lbl.list:"
//										+ lbl.getObjlist().size()+" getPath:"+lbl.getPath());
					}
					
				}

			} else {
				LogUtil.error(TAG, "parseDirectory", "��������File���ܲ�����");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return b;
	}

}
