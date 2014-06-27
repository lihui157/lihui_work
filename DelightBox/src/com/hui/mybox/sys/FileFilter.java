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
 * 本地媒体文件过滤线程 本线程完成工作如下： 1. 将过滤出来的媒体文件信息分发到图片、视频、音频三个分类索引中缓存 2.
 * 建立文件夹浏览方式的本地媒体文件索引
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
		// 获取根目录路径
		try {
			String rootPath = Environment.getExternalStorageDirectory()
					.getCanonicalPath();
			
			//临时缓存
//			MediaApp.folderMapTemp = new HashMap<String, LocalBrowseList>();
			MediaApp.imgListTemp = new ArrayList<MediaFileInfo>();
			MediaApp.videoListTemp = new ArrayList<MediaFileInfo>();
			MediaApp.audioListTemp = new ArrayList<MediaFileInfo>();
			
			//开始遍历
			parseDirectory(rootPath);
			
			//写入文件缓存
//			FileUtil.writeText(rootPath+Config.System.LOCAL_IMAGE_LIST_PATH, new Gson().toJson(MediaApp.imgListTemp));
//			FileUtil.writeText(rootPath+Config.System.LOCAL_AUDIO_LIST_PATH, new Gson().toJson(MediaApp.audioListTemp));
//			FileUtil.writeText(rootPath+Config.System.LOCAL_VIDEO_LIST_PATH, new Gson().toJson(MediaApp.videoListTemp));
//			FileUtil.writeText(rootPath+Config.System.LOCAL_FOLDER_LIST_PATH, new Gson().toJson(MediaApp.folderMapTemp));
//			
			//替换成正式缓存
//			MediaApp.folderMap = MediaApp.folderMapTemp;
			MediaApp.imgList = MediaApp.imgListTemp;
			MediaApp.videoList = MediaApp.videoListTemp;
			MediaApp.audioList = MediaApp.audioListTemp;
			
			//清空临时缓存
//			MediaApp.folderMapTemp = null;
			MediaApp.imgListTemp = null;
			MediaApp.videoListTemp = null;
			MediaApp.audioListTemp = null;
			
			//通知界面内容有更新
//			DlnaUtil.sendUIActionBroad(c, Config.Dlna.ACTION_RET_LOCAL_SEARCH); 
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	
	/**
	 * 解析本地媒体文件目录 此方法实现递归调用
	 * 
	 * @param path
	 *            指定要解析的目录，默认null则定位到sdcard
	 * @return boolean 当前目录是否有媒体文件，有 true 否 false
	 */
	private boolean parseDirectory(String path) {
		boolean b = false;
		try {
			File f;

			if (path == null || path.equals("")) {
				// 设置默认递归的根路径
				f = new File(Environment.getExternalStorageDirectory()
						.getCanonicalPath());
			} else {
				f = new File(path);
			}
			if (f != null) {
				// 每一级目录组合成一个Arraylist对象，通过目录的路径作为key存入MAP中
				ArrayList<MediaFileInfo> list = new ArrayList<MediaFileInfo>();
				File[] fs = f.listFiles();
				if (fs != null) {
					for (int i = 0; i < fs.length; i++) {
						boolean bTemp = false;
						// 判断文件性质
						if (fs[i].isDirectory()) {
							// 递归调用
							bTemp = parseDirectory(fs[i].getCanonicalPath());
						}
						MediaFileInfo mfi = new MediaFileInfo();
						mfi.setFileName(fs[i].getName());
						mfi.setPath(fs[i].getCanonicalPath());
						mfi.setLength(fs[i].length());
//						mfi.setFileType(DlnaUtil.getFileStyle(fs[i]
//								.getCanonicalPath()));
						// 如果文件类型是图片，加载该信息到图片索引
						if (mfi.getFileType() == MediaFileInfo.FILE_TYPE_IMG) {
							MediaApp.imgListTemp.add(mfi); 
							b = true;
						}
						// 如果文件类型是视频，加载该信息到视频索引
						if (mfi.getFileType() == MediaFileInfo.FILE_TYPE_VIDEO) {
							MediaApp.videoListTemp.add(mfi);
							b = true;
						}
						// 如果文件类型是音频，加载该信息到音频索引
						if (mfi.getFileType() == MediaFileInfo.FILE_TYPE_AUDIO) {
							MediaApp.audioListTemp.add(mfi);
							b = true;
						}
						//对于子文件夹中含有媒体文件的，父文件夹同样视为有媒体
						if(bTemp){
							b = true;
						}
						// 添加文件信息到list
						// LogUtil.debug(TAG, "add "+mfi.getFileName());
						if ((mfi.getFileType() != MediaFileInfo.FILE_TYPE_NO && mfi.getFileType()!=MediaFileInfo.FILE_TYPE_FOLDER)
								|| (bTemp && mfi.getFileType()==MediaFileInfo.FILE_TYPE_FOLDER)) {
							list.add(mfi);
						}

					}
					//当前目录包含媒体文件，则建立索引
					if(b){
						// 当前目录层级遍历完毕，将list put进map中，形成目录索引
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
//								"已遍历：" + f.getCanonicalPath() + " lbl.list:"
//										+ lbl.getObjlist().size()+" getPath:"+lbl.getPath());
					}
					
				}

			} else {
//				LogUtil.error(TAG, "parseDirectory", "解析出错，File可能不存在");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

}
