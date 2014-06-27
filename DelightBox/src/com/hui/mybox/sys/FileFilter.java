package com.hui.mybox.sys;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;





import com.hui.mybox.model.MediaFileInfo;

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

	public FileFilter(Context c){
		this.c = c;
	}
	
	@Override
	public void run() {
		// ��ȡ��Ŀ¼·��
		try {
			String rootPath = Environment.getExternalStorageDirectory()
					.getCanonicalPath();
			
			//��ʱ����
//			MediaApp.folderMapTemp = new HashMap<String, LocalBrowseList>();
			MediaApp.imgListTemp = new ArrayList<MediaFileInfo>();
			MediaApp.videoListTemp = new ArrayList<MediaFileInfo>();
			MediaApp.audioListTemp = new ArrayList<MediaFileInfo>();
			
			//��ʼ����
			parseDirectory(rootPath);
			
			//д���ļ�����
//			FileUtil.writeText(rootPath+Config.System.LOCAL_IMAGE_LIST_PATH, new Gson().toJson(MediaApp.imgListTemp));
//			FileUtil.writeText(rootPath+Config.System.LOCAL_AUDIO_LIST_PATH, new Gson().toJson(MediaApp.audioListTemp));
//			FileUtil.writeText(rootPath+Config.System.LOCAL_VIDEO_LIST_PATH, new Gson().toJson(MediaApp.videoListTemp));
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
					for (int i = 0; i < fs.length; i++) {
						boolean bTemp = false;
						// �ж��ļ�����
						if (fs[i].isDirectory()) {
							// �ݹ����
							bTemp = parseDirectory(fs[i].getCanonicalPath());
						}
						MediaFileInfo mfi = new MediaFileInfo();
						mfi.setFileName(fs[i].getName());
						mfi.setPath(fs[i].getCanonicalPath());
						mfi.setLength(fs[i].length());
//						mfi.setFileType(DlnaUtil.getFileStyle(fs[i]
//								.getCanonicalPath()));
						// ����ļ�������ͼƬ�����ظ���Ϣ��ͼƬ����
						if (mfi.getFileType() == MediaFileInfo.FILE_TYPE_IMG) {
							MediaApp.imgListTemp.add(mfi); 
							b = true;
						}
						// ����ļ���������Ƶ�����ظ���Ϣ����Ƶ����
						if (mfi.getFileType() == MediaFileInfo.FILE_TYPE_VIDEO) {
							MediaApp.videoListTemp.add(mfi);
							b = true;
						}
						// ����ļ���������Ƶ�����ظ���Ϣ����Ƶ����
						if (mfi.getFileType() == MediaFileInfo.FILE_TYPE_AUDIO) {
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
//				LogUtil.error(TAG, "parseDirectory", "��������File���ܲ�����");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

}
