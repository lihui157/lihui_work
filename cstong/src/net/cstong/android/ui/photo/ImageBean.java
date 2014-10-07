package net.cstong.android.ui.photo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.cstong.android.util.Constant;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.ab.activity.AbActivity;

/**
 * GridView的每个item的数据对象
 * 
 * @author len
 *
 */
public class ImageBean {
	public List<String> imageList = new ArrayList<String>();

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
	 */
	public static void getImages(final List<String> imageList, final Context mContext, final Handler handler) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			((AbActivity) mContext).showToast("暂无外部存储");
			return;
		}

		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				try {
//					File pathDcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//					if(pathDcim.exists()){
//						for(File f:pathDcim.listFiles()){
//							if(f.isFile()){
//								imageList.add(f.getCanonicalPath());
//							}
//						}
//					}
//					
//					File pathPic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//					if(pathPic.exists()){
//						for(File f:pathPic.listFiles()){
//							if(f.isFile()){
//								imageList.add(f.getCanonicalPath());
//							}
//						}
//					}
//					
//				} catch (Exception e) {
//					Log.w("ImageBean", e.getMessage());
//				}
//				
//				handler.sendEmptyMessage(Constant.MSG_PHOTO_SCAN_OK);
//				
//			}
//		}).start();
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = mContext.getContentResolver();

				//只查询jpeg和png的图片,按时间降序
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED + " DESC");

				while (mCursor.moveToNext()) {
					//获取图片的路径
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
					if(path==null||path.equals("")) {
						break;
					}
					try {
						String pathDcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getCanonicalPath();
						String picDcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getCanonicalPath();
						if(path.toLowerCase().startsWith(pathDcim.toLowerCase())
								||path.toLowerCase().startsWith(picDcim.toLowerCase())){
							Log.d("ImageBean", "getImages() path:"+path);
							imageList.add(path);
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					/**

					//获取该图片的父路径名
					String parentName = new File(path).getParentFile().getName();

					//根据父路径名将图片放入到mGruopMap中
					if (!mGruopMap.containsKey(parentName)) {
						List<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						mGruopMap.get(parentName).add(path);
					}
					**/
				}

				mCursor.close();
				//通知Handler扫描图片完成
				handler.sendEmptyMessage(Constant.MSG_PHOTO_SCAN_OK);

			}
		}).start();

	}

	/**
	 * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
	 * 所以需要遍历HashMap将数据组装成List
	 * 
	 * @param mGruopMap
	 * @return
	 */
	/**
	private List<ImageBean> subGroupOfImage(final HashMap<String, List<String>> mGruopMap) {
		if (mGruopMap.size() == 0) {
			return null;
		}
		List<ImageBean> list = new ArrayList<ImageBean>();
		Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();

			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));//获取该组的第一张图片

			list.add(mImageBean);
		}

		return list;
	}
	**/
}
