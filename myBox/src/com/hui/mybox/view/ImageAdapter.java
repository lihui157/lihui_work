package com.hui.mybox.view;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hui.mybox.R;
import com.hui.mybox.model.MediaFileInfo;
import com.hui.mybox.utils.BoxUtil;
import com.hui.mybox.utils.FileUtil;
import com.hui.mybox.utils.PicUtil;
import com.hui.mybox.view.MediaFileAdapter.ListItemView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author lihui 图片适配器 说明：多线程异步加载图片，可设置最大线程数，支持滑动停止加载
 */
@SuppressLint("NewApi")
public class ImageAdapter extends BaseAdapter {

	private static final String TAG = "ImageAdapter";
//	private LayoutInflater mLayoutInflater;
	private int resource;// 布局
//	private List<? extends Map<String, ?>> mSelfData;
	
//	private String[] from;
//	private int[] to;
	public static final String ITEM_LAYOUT = "item_layout";
	private boolean isLoading = true;
	private int start;// 可启动线程索引开始值
	private int end;// 可启动线程索引结束值
	private static final long fileLength = 1024*1024*2;//5M
	
	private List<GetLocalBitMapThread> localThreadList;//本地图片加载线程
	private final int localMax = 10;// 大图片加载线程最大数
	private int localCurrentNum = 0;// 当前大图片加载线程数
	
	private List<GetBigBitMapThread> bigThreadList; //大图加载线程
	private final int bigMax = 1;// 大图片加载线程最大数
	private int bigCurrentNum = 0;// 当前大图片加载线程数
	
	private List<GetSmallBitMapThread> smallThreadList; //小图加载线程
	private final int smallMax = 3;// 小图片加载线程最大数
	private int smallCurrentNum = 0;// 当前小图片加载线程数
	
	private final long time = 100;// 图片加载休眠时间，单位毫秒
	
//	private Map<String, SoftReference<Bitmap>> caches;
	private LruCache<String, Bitmap> caches;
	AsynImageLoader asynImageLoader;
	private int imageWith, imageHeight;
	
	private List<MediaFileInfo> data;
	
	private LayoutInflater listContainer;
	
	private Context context;
	
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
	
	public ImageAdapter(Context context,List<MediaFileInfo> dataList){
		this.data = dataList;
		listContainer = LayoutInflater.from(context);
		this.context = context;
		asynImageLoader = new AsynImageLoader();
		// 初始化变量
//		caches = new HashMap<String, SoftReference<Bitmap>>();
		// 获取应用程序最大可用内存  
        int caches_ = (int) Runtime.getRuntime().maxMemory();  
        int cacheSize = caches_ / 8;  
        // 设置图片缓存大小为程序最大可用内存的1/8  
        caches = new LruCache<String, Bitmap>(cacheSize) {  
            @Override  
            protected int sizeOf(String key, Bitmap bitmap) {  
                return bitmap.getByteCount();  
            }  
        };  
//		bigThreadList = new ArrayList<GetBigBitMapThread>();
//		smallThreadList = new ArrayList<GetSmallBitMapThread>();
		localThreadList = new ArrayList<GetLocalBitMapThread>();
	}



	@Override
	public int getCount() {
		if (data != null) {
			return data.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}


	
	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		try {
			//自定义视图   
	        ListItemView  listItemView = null;   
	        if (convertView == null) {   
	            listItemView = new ListItemView();    
	            //获取list_item布局文件的视图   
	            convertView = listContainer.inflate(R.layout.img_list_item, null);   
	            //获取控件对象   
	            listItemView.name = (TextView)convertView.findViewById(R.id.tv_img_item_name);   
	            listItemView.icon = (ImageView)convertView.findViewById(R.id.iv_icon);   
	            listItemView.size = (TextView)convertView.findViewById(R.id.tv_img_item_size);   
	            listItemView.time = (TextView)convertView.findViewById(R.id.tv_img_item_time);   
	            
	            //设置控件集到convertView   
	            convertView.setTag(listItemView);   
	        }else {   
	            listItemView = (ListItemView)convertView.getTag();   
	        } 
	        
	        listItemView.name.setText(data.get(arg0).getFileName());
	        if(data.get(arg0).getFileType()==MediaFileInfo.FILE_TYPE_IMG){
	        	if(data.get(arg0).getPath().startsWith("http")){
		        	getHttpPic2ImageView(data.get(arg0).getPath(), listItemView.icon, arg0); //加载网络图片
				}else{
					getLocalPic2ImageView(data.get(arg0).getPath(),listItemView.icon,arg0); //加载本地图片
				}
	        }else if(data.get(arg0).getFileType()==MediaFileInfo.FILE_TYPE_VIDEO){
	        	listItemView.icon.setImageResource(R.drawable.video_type);
	        }else if(data.get(arg0).getFileType()==MediaFileInfo.FILE_TYPE_AUDIO){
	        	listItemView.icon.setImageResource(R.drawable.music_type);
	        }
	        
	        listItemView.size.setText((data.get(arg0).getFileType()==MediaFileInfo.FILE_TYPE_FOLDER)? "":BoxUtil.convertFileSize(data.get(arg0).getLength()));
	         
	        listItemView.time.setText(sdf.format((data.get(arg0).getLastModifTime())));
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.right_in);
		convertView.startAnimation(animation);
        return convertView;   
    
	}

	
	private void getLocalPic2ImageView(String path,View view,int num){
		view.setTag(path);
		Bitmap bitmap = caches.get(path);
		if(bitmap==null){
			caches.remove(path);
			Task task = new Task();
			task.path = path;
			task.num = num;
			
			
			task.callback = getImageCallback((ImageView) view);
			if(isLoading){
				new GetLocalBitMapThread(task).start();
			}else{
				new GetLocalBitMapThread(task);
			}

			((ImageView) view).setImageResource(R.drawable.image_type);
			
		} else {
			// 如果图片未被释放，直接返回该图片
			((ImageView) view).setImageBitmap(bitmap);
		}
		
	}
	

	
/**
 * 获取网络图片到ImageView
 * @param path
 * @param view
 * @param num
 * @return
 */
	private void getHttpPic2ImageView(String path, View view, int num) {
		view.setTag(path);
		Bitmap bitmap = caches.get(path);
		if(bitmap == null){
			caches.remove(path);
			final Task task = new Task();
			task.path = path;
			task.num = num;
			task.callback = getImageCallback((ImageView) view);
			new Thread(){
				public void run(){
					assign(task); 
				}
			}.start();
			((ImageView) view).setImageResource(R.drawable.image_type);
		}else {
			// 如果图片未被释放，直接返回该图片
			((ImageView) view).setImageBitmap(bitmap);
		}
		
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
		if(!isLoading){
			bigCurrentNum = 0;
			smallCurrentNum = 0;
			localCurrentNum = 0;
		}
		
	}

	/**
	 * 分配线程
	 * 大图分配给大图加载线程
	 * 小图分配给小图加载线程
	 */
	public void assign (final Task t){

		File cacheFile = null;
		try {
			long cacheTime = 1000 * 60 * 5;
			// 先从缓存处获取
			cacheFile = FileUtil.getCacheFile(t.path);
			if (cacheFile.exists()) {
				long time = System.currentTimeMillis()
						- cacheFile.lastModified();
				if (time > cacheTime) {
					// 时间超过5分钟则删除缓存
					FileUtil.deleteFile(cacheFile.getCanonicalPath());
				}
			}

			if (!cacheFile.exists()) {
				// 显示网络上的图片
				long startTime = System.currentTimeMillis();
				URL myFileUrl = new URL(t.path);
				Log.e(TAG, "t.path:"+t.path);
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				int length = conn.getContentLength();
				
				Log.e(TAG, "assign 图片长度："+length+" 当前获取信息时间："+(System.currentTimeMillis()-startTime));
				if(length>fileLength){
					//分配大图线程
					if(isLoading){
						new GetBigBitMapThread(t).start();
					}else{
						new GetBigBitMapThread(t);
					}
					
				}else{
					//分配小图线程
					if(isLoading){
						new GetSmallBitMapThread(t).start();
					}else{
						new GetSmallBitMapThread(t);
					}
				}
			}else{
				if(cacheFile.length()>fileLength){
					//分配大图线程
					if(isLoading){
						new GetBigBitMapThread(t).start();
					}else{
						new GetBigBitMapThread(t);
					}
				}else{
					//分配小图线程
					if(isLoading){
						new GetSmallBitMapThread(t).start();
					}else{
						new GetSmallBitMapThread(t);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (cacheFile != null && cacheFile.exists()) {
					FileUtil.deleteFile(cacheFile.getCanonicalPath());
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	
		
	}
	
	
	/**
	 * 本地图片加载线程
	 */
	class GetLocalBitMapThread extends Thread {
		
		private Task t;

		public GetLocalBitMapThread(Task t) {
			this.t = t;
			localThreadList.add(this);
//			Log.d(TAG, "GetLocalBitMapThread 创建 "+t.getNum());
		}

		@Override
		public void run() {
			while (localCurrentNum >=localMax) {
				try {
					sleep(time);
					if(!isLoading){
						localCurrentNum = 0;
						interrupt();
						return;
					}
//					Log.e(TAG, "sleep 当前本地图片线程数："+localCurrentNum);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			localCurrentNum++;
			if (!isLoading) {
				localCurrentNum = 0;
				this.interrupt();
				return;
			}
			t.bitmap = getBitMap(new File(t.path));
//			Log.e(TAG, "t.bitmap:"+t.bitmap);
			if(t.bitmap!=null){
				caches.put(t.path, t.bitmap);
			}
			if (handler != null) {
				// 创建消息对象，并将完成的任务添加到消息对象中
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = t;
				// 发送消息回主线程
				handler.sendMessage(msg);
			}
		}

		public Task getTask() {
			return this.t;
		}
	}
	
	
	/**
	 * 大图片加载线程
	 */
	class GetBigBitMapThread extends Thread {
		
		private Task t;

		public GetBigBitMapThread(Task t) {
			this.t = t;
			bigThreadList.add(this);
		}

		@Override
		public void run() {
			while (bigCurrentNum >= bigMax) {
				try {
					sleep(time);
					if(!isLoading){
						bigCurrentNum = 0;
						interrupt();
						return;
					}
//					Log.e(TAG, "sleep 当前大图线程数："+bigCurrentNum);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			bigCurrentNum++;
			if (!isLoading) {
				bigCurrentNum = 0;
				this.interrupt();
				return;
			}
			putImage2Cache(t);
			if (handler != null) {
				// 创建消息对象，并将完成的任务添加到消息对象中
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = t;
				// 发送消息回主线程
				handler.sendMessage(msg);
			}
		}

		public Task getTask() {
			return this.t;
		}
	}
	
	/**
	 * 获取Bitmap线程
	 */
	class GetSmallBitMapThread extends Thread {
		
		private Task t;

		public GetSmallBitMapThread(Task t) {
			this.t = t;
			smallThreadList.add(this);
		}

		@Override
		public void run() {
			while (smallCurrentNum >= smallMax) {
				try {
					sleep(time);
					if(!isLoading){
						smallCurrentNum = 0;
						interrupt();
						return;
					}
//					Log.e(TAG, "sleep 当前小图线程数："+smallCurrentNum+ " isLoading:"+isLoading);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			smallCurrentNum++;
			if (!isLoading) {
				smallCurrentNum = 0;
				this.interrupt();
				return;
			}
//			LogUtil.error(TAG, "加载图片：smallCurrentNum:"+smallCurrentNum+" smallMax:"+smallMax);
			putImage2Cache(t);
			if (handler != null) {
				// 创建消息对象，并将完成的任务添加到消息对象中
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = t;
				// 发送消息回主线程
				handler.sendMessage(msg);
			}
		}

		public Task getTask() {
			return this.t;
		}
	}
	
	/**
	 * 将远程或本地图片文件加载到SoftReference缓存
	 * @param t
	 */
	private void putImage2Cache(Task t){
//		LogUtil.debug(TAG, "putImage2Cache", "--"+1);
		// 将下载的图片添加到缓存
					long cacheTime = 1000 * 60 * 5;
					byte[] icon = null;
					BitmapFactory.Options opt = null;
					InputStream is = null;
					File cacheFile = null;
					try {

						// 先从缓存处获取
						cacheFile = FileUtil.getCacheFile(t.path);
						if (cacheFile.exists()) {
							long time = System.currentTimeMillis()
									- cacheFile.lastModified();
							if (time > cacheTime) {
								// 时间超过5分钟则删除缓存
								FileUtil.deleteFile(cacheFile.getCanonicalPath());
							}
						}

						if (!cacheFile.exists()) {
							// 显示网络上的图片
							if (!isLoading) {
								smallCurrentNum = 0;
								bigCurrentNum = 0;
								return;
							}
							URL myFileUrl = new URL(t.path);
							HttpURLConnection conn = (HttpURLConnection) myFileUrl
									.openConnection();
							conn.setDoInput(true);
							conn.connect();
							if (!isLoading) {
								smallCurrentNum = 0;
								bigCurrentNum = 0;
								return;
							}
							is = conn.getInputStream();
							BufferedOutputStream bos = null;

							bos = new BufferedOutputStream(new FileOutputStream(
									cacheFile));

							byte[] buf = new byte[1024];
							int len = 0;
							// 将网络上的图片存储到本地
							while ((len = is.read(buf)) > 0) {
								bos.write(buf, 0, len);
							}

							is.close();
							bos.close();
						}
						if (!isLoading) {
							smallCurrentNum = 0;
							bigCurrentNum = 0;
							return;
						}
						// 从本地加载图片
						if(cacheFile.length()<1024*1024*5){ //低于10M，加载图片
							is = new FileInputStream(cacheFile);
							icon = getBytes(is);
							// bitmap = BitmapFactory.decodeStream(is);
							opt = new BitmapFactory.Options();
							opt.inPreferredConfig = Bitmap.Config.RGB_565;
							opt.inJustDecodeBounds = true;
							BitmapFactory.decodeByteArray(icon, 0, icon.length, opt);
							if (opt.outWidth < opt.outHeight) {
								opt.inSampleSize = opt.outWidth / 80;
							} else {
								opt.inSampleSize = opt.outHeight / 80;
							}
							if (opt.inSampleSize <= 0) {
								opt.inSampleSize = 1;
							}
							opt.inJustDecodeBounds = false;
							t.bitmap = BitmapFactory.decodeByteArray(icon, 0, icon.length,
									opt);
						}
//						else{//高于10M，不加载原图
//							t.bitmap = BitmapFactory.decodeResource(getResource(), id)
//						}
						

					} catch (IOException e) {

						e.printStackTrace();
						if (is != null) {
							try {
								is.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
						try {
							if (cacheFile != null && cacheFile.exists()) {
								FileUtil.deleteFile(cacheFile.getCanonicalPath());
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					caches.put(t.path, t.bitmap);
	}

	private byte[] getBytes(InputStream is) throws IOException {

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] b = new byte[512];
			int len = 0;

			while ((len = is.read(b, 0, 512)) != -1) {
				// LogUtil.debug(TAG, "getBytes",
				// ""+len+"  baos lenth:"+baos.size());
				baos.write(b, 0, len);
				baos.flush();
			}
			byte[] bytes = baos.toByteArray();

			baos.reset();

			baos.close();

			baos = null;
			b = null;

			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	class Task {
		// 下载任务的下载路径
		String path;
		// 下载的图片
		Bitmap bitmap;
		// 索引
		int num;
		// 回调对象
		ImageCallback callback;

		@Override
		public boolean equals(Object o) {
			Task task = (Task) o;
			return task.path.equals(path);
		}

		public int getNum() {
			return this.num;
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 子线程中返回的下载完成的任务
			Task task = (Task) msg.obj;
			switch(msg.what){
			case 0://大图更新
				// 调用callback对象的loadImage方法，并将图片路径和图片回传给adapter
				task.callback.loadImage(task.path, task.bitmap);
				if(bigCurrentNum>0) bigCurrentNum--;
				break;
			case 1://小图更新
				// 调用callback对象的loadImage方法，并将图片路径和图片回传给adapter
				task.callback.loadImage(task.path, task.bitmap);
				if(smallCurrentNum>0) smallCurrentNum--;
				break;
				
			case 2://不分大小
				// 调用callback对象的loadImage方法，并将图片路径和图片回传给adapter
				task.callback.loadImage(task.path, task.bitmap);
				
				if(localCurrentNum>0) localCurrentNum--;
				break;
			}

		}

	};

	// 回调接口
	public interface ImageCallback {
		void loadImage(String path, Bitmap bitmap);
	}

	/**
	 * 
	 * @param imageView
	 * @param resId
	 *            图片加载完成前显示的图片资源ID
	 * @return
	 */
	private ImageCallback getImageCallback(final ImageView imageView)
			 {
		return new ImageCallback() {

			@Override
			public void loadImage(String path, Bitmap bitmap) {
				if (path.equals(imageView.getTag().toString())
						&& bitmap != null) {
					imageView.setImageBitmap(bitmap);
					
				} 
			}
		};
	}

	public void runLoading() {
		//加载大图
		new Thread(){
			public void run(){
				if (bigThreadList != null) {
					while (bigThreadList.size() > 0) {
						GetBigBitMapThread t = bigThreadList.remove(0);
						if (!isLoading)
							return;
						if (t.getState().name().equals("NEW")) {
							if (t.getTask().getNum() >= start
									&& t.getTask().getNum() <= end) {
								try {
									t.start();
								} catch (Exception e) {
									// TODO: handle exception
								}
//								 LogUtil.error(TAG, "启动大图线程：" + t.getTask().getNum());
							} else {
								t.interrupt();
//								 LogUtil.error(TAG, "中断大图线程：" + t.getTask().getNum());
							}
						}
					}
				} else {
					Log.e(TAG, "runLoading bigThreadList is null");
				}
			}
		}.start();
		
		//加载小图
		new Thread(){
			public void run(){
				if (smallThreadList != null) {
					while (smallThreadList.size() > 0) {
						GetSmallBitMapThread t = smallThreadList.remove(0);
						if (!isLoading)
							return;
						if (t.getState().name().equals("NEW")) {
							if (t.getTask().getNum() >= start
									&& t.getTask().getNum() <= end) {
								try {
									t.start();
								} catch (Exception e) {
									// TODO: handle exception
								}
//								 LogUtil.error(TAG, "启动小图线程：" + t.getTask().getNum());
							} else {
								t.interrupt();
//								 LogUtil.error(TAG, "中断小图线程：" + t.getTask().getNum());
							}
						}else{
							
						}
					}
				} else {
					Log.e(TAG, "runLoading smallThreadList is null");
				}
			}
		}.start();
		
		//加载本地图片
				new Thread(){
					public void run(){
						if (localThreadList != null) {
							while (localThreadList.size() > 0) {
								GetLocalBitMapThread t = localThreadList.remove(0);
								if (!isLoading){
									return;
								}
								if (t.getState().name().equals("NEW")) {
									if (t.getTask().getNum() >= start
											&& t.getTask().getNum() <= end) {
										try {
											t.start();
										} catch (Exception e) {
											// TODO: handle exception
										}
										
									} else {
										t.interrupt();
									}
								}else{
								}
							}
						} else {
							Log.e(TAG, "runLoading smallThreadList is null");
						}
					}
				}.start();
		
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getImageWith() {
		return imageWith;
	}

	public void setImageWith(int imageWith) {
		this.imageWith = imageWith;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}
	
	/**
	 * File转bitmap
	 * @param file
	 * @return
	 */
	private Bitmap getBitMap(File file){
		FileInputStream is = null;
		byte[] icon = null;
		BitmapFactory.Options opt = null;
		Bitmap tempBitmap = null;
		try{
			if(file.length()<1024*1024*5){ //低于10M，加载图片
				is = new FileInputStream(file);
//				LogUtil.error(TAG,"本地加载图片："+file.getName());
				icon = getBytes(is);
				// bitmap = BitmapFactory.decodeStream(is);
				opt = new BitmapFactory.Options();
				opt.inPreferredConfig = Bitmap.Config.RGB_565;
				opt.inJustDecodeBounds = true;
				BitmapFactory.decodeByteArray(icon, 0, icon.length, opt);
				if (opt.outWidth < opt.outHeight) {
					opt.inSampleSize = opt.outWidth / 80;
				} else {
					opt.inSampleSize = opt.outHeight / 80;
				}
				if (opt.inSampleSize <= 0) {
					opt.inSampleSize = 1;
				}
				opt.inJustDecodeBounds = false;
				tempBitmap = BitmapFactory.decodeByteArray(icon, 0, icon.length,
						opt);
			}
		}catch (Exception e){
			e.printStackTrace();
		}catch(Error e2){
			e2.printStackTrace();
		}
		return tempBitmap;
		
	}
	

	
	class ListItemView{
		TextView time;
		TextView size;
		ImageView icon;
		TextView name;
	}

}
