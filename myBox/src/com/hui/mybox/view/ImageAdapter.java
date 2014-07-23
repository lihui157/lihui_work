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
 * @author lihui ͼƬ������ ˵�������߳��첽����ͼƬ������������߳�����֧�ֻ���ֹͣ����
 */
@SuppressLint("NewApi")
public class ImageAdapter extends BaseAdapter {

	private static final String TAG = "ImageAdapter";
//	private LayoutInflater mLayoutInflater;
	private int resource;// ����
//	private List<? extends Map<String, ?>> mSelfData;
	
//	private String[] from;
//	private int[] to;
	public static final String ITEM_LAYOUT = "item_layout";
	private boolean isLoading = true;
	private int start;// �������߳�������ʼֵ
	private int end;// �������߳���������ֵ
	private static final long fileLength = 1024*1024*2;//5M
	
	private List<GetLocalBitMapThread> localThreadList;//����ͼƬ�����߳�
	private final int localMax = 10;// ��ͼƬ�����߳������
	private int localCurrentNum = 0;// ��ǰ��ͼƬ�����߳���
	
	private List<GetBigBitMapThread> bigThreadList; //��ͼ�����߳�
	private final int bigMax = 1;// ��ͼƬ�����߳������
	private int bigCurrentNum = 0;// ��ǰ��ͼƬ�����߳���
	
	private List<GetSmallBitMapThread> smallThreadList; //Сͼ�����߳�
	private final int smallMax = 3;// СͼƬ�����߳������
	private int smallCurrentNum = 0;// ��ǰСͼƬ�����߳���
	
	private final long time = 100;// ͼƬ��������ʱ�䣬��λ����
	
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
		// ��ʼ������
//		caches = new HashMap<String, SoftReference<Bitmap>>();
		// ��ȡӦ�ó����������ڴ�  
        int caches_ = (int) Runtime.getRuntime().maxMemory();  
        int cacheSize = caches_ / 8;  
        // ����ͼƬ�����СΪ�����������ڴ��1/8  
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
			//�Զ�����ͼ   
	        ListItemView  listItemView = null;   
	        if (convertView == null) {   
	            listItemView = new ListItemView();    
	            //��ȡlist_item�����ļ�����ͼ   
	            convertView = listContainer.inflate(R.layout.img_list_item, null);   
	            //��ȡ�ؼ�����   
	            listItemView.name = (TextView)convertView.findViewById(R.id.tv_img_item_name);   
	            listItemView.icon = (ImageView)convertView.findViewById(R.id.iv_icon);   
	            listItemView.size = (TextView)convertView.findViewById(R.id.tv_img_item_size);   
	            listItemView.time = (TextView)convertView.findViewById(R.id.tv_img_item_time);   
	            
	            //���ÿؼ�����convertView   
	            convertView.setTag(listItemView);   
	        }else {   
	            listItemView = (ListItemView)convertView.getTag();   
	        } 
	        
	        listItemView.name.setText(data.get(arg0).getFileName());
	        if(data.get(arg0).getFileType()==MediaFileInfo.FILE_TYPE_IMG){
	        	if(data.get(arg0).getPath().startsWith("http")){
		        	getHttpPic2ImageView(data.get(arg0).getPath(), listItemView.icon, arg0); //��������ͼƬ
				}else{
					getLocalPic2ImageView(data.get(arg0).getPath(),listItemView.icon,arg0); //���ر���ͼƬ
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
			// ���ͼƬδ���ͷţ�ֱ�ӷ��ظ�ͼƬ
			((ImageView) view).setImageBitmap(bitmap);
		}
		
	}
	

	
/**
 * ��ȡ����ͼƬ��ImageView
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
			// ���ͼƬδ���ͷţ�ֱ�ӷ��ظ�ͼƬ
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
	 * �����߳�
	 * ��ͼ�������ͼ�����߳�
	 * Сͼ�����Сͼ�����߳�
	 */
	public void assign (final Task t){

		File cacheFile = null;
		try {
			long cacheTime = 1000 * 60 * 5;
			// �ȴӻ��洦��ȡ
			cacheFile = FileUtil.getCacheFile(t.path);
			if (cacheFile.exists()) {
				long time = System.currentTimeMillis()
						- cacheFile.lastModified();
				if (time > cacheTime) {
					// ʱ�䳬��5������ɾ������
					FileUtil.deleteFile(cacheFile.getCanonicalPath());
				}
			}

			if (!cacheFile.exists()) {
				// ��ʾ�����ϵ�ͼƬ
				long startTime = System.currentTimeMillis();
				URL myFileUrl = new URL(t.path);
				Log.e(TAG, "t.path:"+t.path);
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				int length = conn.getContentLength();
				
				Log.e(TAG, "assign ͼƬ���ȣ�"+length+" ��ǰ��ȡ��Ϣʱ�䣺"+(System.currentTimeMillis()-startTime));
				if(length>fileLength){
					//�����ͼ�߳�
					if(isLoading){
						new GetBigBitMapThread(t).start();
					}else{
						new GetBigBitMapThread(t);
					}
					
				}else{
					//����Сͼ�߳�
					if(isLoading){
						new GetSmallBitMapThread(t).start();
					}else{
						new GetSmallBitMapThread(t);
					}
				}
			}else{
				if(cacheFile.length()>fileLength){
					//�����ͼ�߳�
					if(isLoading){
						new GetBigBitMapThread(t).start();
					}else{
						new GetBigBitMapThread(t);
					}
				}else{
					//����Сͼ�߳�
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
	 * ����ͼƬ�����߳�
	 */
	class GetLocalBitMapThread extends Thread {
		
		private Task t;

		public GetLocalBitMapThread(Task t) {
			this.t = t;
			localThreadList.add(this);
//			Log.d(TAG, "GetLocalBitMapThread ���� "+t.getNum());
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
//					Log.e(TAG, "sleep ��ǰ����ͼƬ�߳�����"+localCurrentNum);
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
				// ������Ϣ���󣬲�����ɵ�������ӵ���Ϣ������
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = t;
				// ������Ϣ�����߳�
				handler.sendMessage(msg);
			}
		}

		public Task getTask() {
			return this.t;
		}
	}
	
	
	/**
	 * ��ͼƬ�����߳�
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
//					Log.e(TAG, "sleep ��ǰ��ͼ�߳�����"+bigCurrentNum);
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
				// ������Ϣ���󣬲�����ɵ�������ӵ���Ϣ������
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = t;
				// ������Ϣ�����߳�
				handler.sendMessage(msg);
			}
		}

		public Task getTask() {
			return this.t;
		}
	}
	
	/**
	 * ��ȡBitmap�߳�
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
//					Log.e(TAG, "sleep ��ǰСͼ�߳�����"+smallCurrentNum+ " isLoading:"+isLoading);
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
//			LogUtil.error(TAG, "����ͼƬ��smallCurrentNum:"+smallCurrentNum+" smallMax:"+smallMax);
			putImage2Cache(t);
			if (handler != null) {
				// ������Ϣ���󣬲�����ɵ�������ӵ���Ϣ������
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = t;
				// ������Ϣ�����߳�
				handler.sendMessage(msg);
			}
		}

		public Task getTask() {
			return this.t;
		}
	}
	
	/**
	 * ��Զ�̻򱾵�ͼƬ�ļ����ص�SoftReference����
	 * @param t
	 */
	private void putImage2Cache(Task t){
//		LogUtil.debug(TAG, "putImage2Cache", "--"+1);
		// �����ص�ͼƬ��ӵ�����
					long cacheTime = 1000 * 60 * 5;
					byte[] icon = null;
					BitmapFactory.Options opt = null;
					InputStream is = null;
					File cacheFile = null;
					try {

						// �ȴӻ��洦��ȡ
						cacheFile = FileUtil.getCacheFile(t.path);
						if (cacheFile.exists()) {
							long time = System.currentTimeMillis()
									- cacheFile.lastModified();
							if (time > cacheTime) {
								// ʱ�䳬��5������ɾ������
								FileUtil.deleteFile(cacheFile.getCanonicalPath());
							}
						}

						if (!cacheFile.exists()) {
							// ��ʾ�����ϵ�ͼƬ
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
							// �������ϵ�ͼƬ�洢������
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
						// �ӱ��ؼ���ͼƬ
						if(cacheFile.length()<1024*1024*5){ //����10M������ͼƬ
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
//						else{//����10M��������ԭͼ
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
		// �������������·��
		String path;
		// ���ص�ͼƬ
		Bitmap bitmap;
		// ����
		int num;
		// �ص�����
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
			// ���߳��з��ص�������ɵ�����
			Task task = (Task) msg.obj;
			switch(msg.what){
			case 0://��ͼ����
				// ����callback�����loadImage����������ͼƬ·����ͼƬ�ش���adapter
				task.callback.loadImage(task.path, task.bitmap);
				if(bigCurrentNum>0) bigCurrentNum--;
				break;
			case 1://Сͼ����
				// ����callback�����loadImage����������ͼƬ·����ͼƬ�ش���adapter
				task.callback.loadImage(task.path, task.bitmap);
				if(smallCurrentNum>0) smallCurrentNum--;
				break;
				
			case 2://���ִ�С
				// ����callback�����loadImage����������ͼƬ·����ͼƬ�ش���adapter
				task.callback.loadImage(task.path, task.bitmap);
				
				if(localCurrentNum>0) localCurrentNum--;
				break;
			}

		}

	};

	// �ص��ӿ�
	public interface ImageCallback {
		void loadImage(String path, Bitmap bitmap);
	}

	/**
	 * 
	 * @param imageView
	 * @param resId
	 *            ͼƬ�������ǰ��ʾ��ͼƬ��ԴID
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
		//���ش�ͼ
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
//								 LogUtil.error(TAG, "������ͼ�̣߳�" + t.getTask().getNum());
							} else {
								t.interrupt();
//								 LogUtil.error(TAG, "�жϴ�ͼ�̣߳�" + t.getTask().getNum());
							}
						}
					}
				} else {
					Log.e(TAG, "runLoading bigThreadList is null");
				}
			}
		}.start();
		
		//����Сͼ
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
//								 LogUtil.error(TAG, "����Сͼ�̣߳�" + t.getTask().getNum());
							} else {
								t.interrupt();
//								 LogUtil.error(TAG, "�ж�Сͼ�̣߳�" + t.getTask().getNum());
							}
						}else{
							
						}
					}
				} else {
					Log.e(TAG, "runLoading smallThreadList is null");
				}
			}
		}.start();
		
		//���ر���ͼƬ
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
	 * Fileתbitmap
	 * @param file
	 * @return
	 */
	private Bitmap getBitMap(File file){
		FileInputStream is = null;
		byte[] icon = null;
		BitmapFactory.Options opt = null;
		Bitmap tempBitmap = null;
		try{
			if(file.length()<1024*1024*5){ //����10M������ͼƬ
				is = new FileInputStream(file);
//				LogUtil.error(TAG,"���ؼ���ͼƬ��"+file.getName());
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
