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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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
	
	private LinkedList<GetLocalBitMapThread> localThreadList;//����ͼƬ�����߳�
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
		bigThreadList = new ArrayList<GetBigBitMapThread>();
		smallThreadList = new ArrayList<GetSmallBitMapThread>();
		localThreadList = new LinkedList<GetLocalBitMapThread>();
	}

//	public ImageAdapter(Context c, List<? extends Map<String, ?>> data,
//			String[] from, int[] to) {
//		this.from = from;
//		this.to = to;
//		this.mSelfData = data;
//		this.mLayoutInflater = (LayoutInflater) c
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		asynImageLoader = new AsynImageLoader();
//		// ��ʼ������
//		caches = new HashMap<String, SoftReference<Bitmap>>();
//		bigThreadList = new ArrayList<GetBigBitMapThread>();
//		smallThreadList = new ArrayList<GetSmallBitMapThread>();
//		localThreadList = new ArrayList<GetLocalBitMapThread>();
//	}

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

//	@Override
//	public View getView(int arg0, View convertView, ViewGroup parent) {
//		// ��ȡ������ָ���Ĳ���
//		Map<String, ?> item = mSelfData.get(arg0);
//		resource = (Integer) item.get(ITEM_LAYOUT);
//		if (convertView == null) {
//			convertView = mLayoutInflater.inflate(resource, null);
//		}
//		int count = to.length;
//		for (int i = 0; i < count; i++) {
//			View v = convertView.findViewById(to[i]);
//			bindView(v, item, from[i], arg0);
//		}
//		convertView.setTag(arg0);
//		return convertView;
//	}
	
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
	        if(data.get(arg0).getPath().startsWith("http")){
//	        	listItemView.icon = getHttpPic2ImageView(data.get(arg0).getPath(), listItemView.icon, arg0); //��������ͼƬ
			}else{
//				listItemView.icon = getLocalPic2ImageView(data.get(arg0).getPath(),listItemView.icon,arg0); //���ر���ͼƬ
				getLocalPic2ImageView_(data.get(arg0).getPath(),listItemView.icon,arg0); //���ر���ͼƬ
			}
	        listItemView.size.setText((data.get(arg0).getFileType()==MediaFileInfo.FILE_TYPE_FOLDER)? String.valueOf(data.get(arg0).getLength()):"");
	        listItemView.time.setText(Long.toString(data.get(arg0).getLastModifTime()));
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
        return convertView;   
    
	}

	/**
	 * ��������ݰ�
	 * 
	 * @param view
	 *            ������ui���
	 * @param item
	 *            ����map
	 * @param from
	 *            map�е�key����
	 */
//	private void bindView(View view, Map<String, ?> item, String from, int num) {
//		Object data = item.get(from);
//		if (view instanceof TextView) {
//
//			((TextView) view).setText(data == null ? "" : data.toString());
//		}
//		if (view instanceof ImageView) {
//			if (data != null) {
//				if (data instanceof Integer) {// ����R.drawable.xx���͵�ͼƬ
//					((ImageView) view).setImageResource((Integer) data);
//				} else if (data instanceof byte[]) {// ����ͼƬ����������
//					Bitmap bm = BitmapFactory.decodeByteArray((byte[]) data, 0,
//							((byte[]) data).length);
//					((ImageView) view).setImageBitmap(bm);
//
//				} else if (data instanceof IconDesc) {// �����Զ���IconDesc����
//					Bitmap bm = BitmapFactory.decodeByteArray(
//							((IconDesc) data).getIconData(), 0,
//							((IconDesc) data).getIconData().length);
//					((ImageView) view).setImageBitmap(bm);
//				} else if ((data instanceof String)// �����ַ�����ͨ��Ϊhttp��ͼƬ
//						&& ((String) data).toLowerCase().startsWith("http")) {
//					// �жϻ������Ƿ��Ѿ����ڸ�ͼƬ
//					String path = (String) data;
//					getHttpPic2ImageView(path, view, num);
//				} else if (data instanceof MyImage) {// �����Զ���MyImage������Ϊ��Ϻ������л�ʱ�����Զ���ͼƬ�ĳߴ�
//					MyImage mi = (MyImage) data;
//					String path = mi.getHttpUrl();
//					if(path.startsWith("http")){
//						view = getHttpPic2ImageView(path, view, num); //��������ͼƬ
//					}else{
//						view = getLocalPic2ImageView(path,view,num); //���ر���ͼƬ
//					}
//					// �����ⲿ���õĿ���������ͼƬ�ߴ�
//					LayoutParams para;
//					para = view.getLayoutParams();
//					para.width = mi.getWidth();
//					para.height = mi.getHeight();
//					view.setLayoutParams(para);
//
//				} else if ((data instanceof Bitmap)) {
//					((ImageView) view).setImageBitmap((Bitmap) data);
//				}
//
//			} else {
//				((ImageView) view).setVisibility(View.GONE);
//			}
//
//		}
//	}
	
	
	private ImageView getLocalPic2ImageView_(String path,View view,int num){
		Bitmap bitmap = caches.get(path);
		if(bitmap==null){
			caches.remove(path);
			final Task task = new Task();
			task.path = path;
			task.num = num;
			view.setTag(path);
			task.callback = getImageCallback((ImageView) view,
					R.drawable.image_type);
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
		
		return null;
	}
	
	/**
	 * ��ȡ����ͼƬ��ImageView
	 * @param path
	 * @param view
	 * @param num
	 * @return
	 */
//	private ImageView getLocalPic2ImageView(String path,View view,int num){
//
//		view.setTag(path);
//		if (caches.containsKey(path)) {
//			// ȡ��������
//			SoftReference<Bitmap> rf = caches.get(path);
//			// ͨ�������ã���ȡͼƬ
//			Bitmap bitmap = rf.get();
//			// �����ͼƬ�Ѿ����ͷţ��򽫸�path��Ӧ�ļ���Map���Ƴ���
//			if (bitmap == null) {
//				// LogUtil.debug(TAG, "bindView", "caches.remove(path)" + path);
//				caches.remove(path);
//				final Task task = new Task();
//				task.path = path;
//				task.num = num;
//				task.callback = getImageCallback((ImageView) view,
//						R.drawable.image_type);
//				if(isLoading){
//					new GetLocalBitMapThread(task).start();
//				}else{
//					new GetLocalBitMapThread(task);
//				}
//
//				((ImageView) view).setImageResource(R.drawable.image_type);
//			} else {
//				// ���ͼƬδ���ͷţ�ֱ�ӷ��ظ�ͼƬ
//				((ImageView) view).setImageBitmap(bitmap);
//			}
//		} else {
//			// ��������в����ڸ�ͼƬ���򴴽�ͼƬ��������
//			// LogUtil.debug(TAG, "bindView", "�����в����ڸ�ͼƬ���򴴽�ͼƬ��������" + path);
//			final Task task = new Task();
//			task.path = path;
//			task.num = num;
//			task.callback = getImageCallback((ImageView) view,
//					R.drawable.image_type);
//			if(isLoading){
//				new GetLocalBitMapThread(task).start();
//			}else{
//				new GetLocalBitMapThread(task);
//			}
//			((ImageView) view).setImageResource(R.drawable.image_type);
//		}
//		return (ImageView) view;
//	
//	}
	
/**
 * ��ȡ����ͼƬ��ImageView
 * @param path
 * @param view
 * @param num
 * @return
 */
//	private ImageView getHttpPic2ImageView(String path, View view, int num) {
//		view.setTag(path);
//		if (caches.containsKey(path)) {
//			// ȡ��������
//			SoftReference<Bitmap> rf = caches.get(path);
//			// ͨ�������ã���ȡͼƬ
//			Bitmap bitmap = rf.get();
//			// �����ͼƬ�Ѿ����ͷţ��򽫸�path��Ӧ�ļ���Map���Ƴ���
//			if (bitmap == null) {
//				// LogUtil.debug(TAG, "bindView", "caches.remove(path)" + path);
//				caches.remove(path);
//				final Task task = new Task();
//				task.path = path;
//				task.num = num;
//				task.callback = getImageCallback((ImageView) view,
//						R.drawable.image_type);
//				
//				new Thread(){
//					public void run(){
//						assign(task);
//					}
//				}.start();
//
//				((ImageView) view).setImageResource(R.drawable.image_type);
//			} else {
//				// ���ͼƬδ���ͷţ�ֱ�ӷ��ظ�ͼƬ
//				((ImageView) view).setImageBitmap(bitmap);
//			}
//		} else {
//			// ��������в����ڸ�ͼƬ���򴴽�ͼƬ��������
//			// LogUtil.debug(TAG, "bindView", "�����в����ڸ�ͼƬ���򴴽�ͼƬ��������" + path);
//			final Task task = new Task();
//			task.path = path;
//			task.num = num;
//			task.callback = getImageCallback((ImageView) view,
//					R.drawable.image_type);
//			new Thread(){
//				public void run(){
//					assign(task); 
//				}
//			}.start();
//			((ImageView) view).setImageResource(R.drawable.image_type);
//		}
//		return (ImageView) view;
//	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
		if(!isLoading){
			bigCurrentNum = 0;
			smallCurrentNum = 0;
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
			Log.d(TAG, "GetLocalBitMapThread ���� "+t.getNum());
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
//			Log.e(TAG, "����ͼƬ��localCurrentNum:"+localCurrentNum+" localMax:"+localMax);
			t.bitmap = getBitMap(new File(t.path));
			if (handler != null) {
				// ������Ϣ���󣬲�����ɵ��������ӵ���Ϣ������
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
			Log.d(TAG, "GetBigBitMapThread ���� "+t.getNum());
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
			Log.e(TAG, "����ͼƬ��bigCurrentNum:"+bigCurrentNum+" bigMax:"+bigMax);
			putImage2Cache(t);
			if (handler != null) {
				// ������Ϣ���󣬲�����ɵ��������ӵ���Ϣ������
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
			Log.d(TAG, "GetSmallBitMapThread ���� "+t.getNum());
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
				// ������Ϣ���󣬲�����ɵ��������ӵ���Ϣ������
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
		// �����ص�ͼƬ���ӵ�����
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
							Log.e(TAG,"���ؼ���ͼƬ��"+cacheFile.getName());
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
//					LogUtil.debug(TAG, "putImage2Cache", "--"+t.path);
//					caches.put(t.path, new SoftReference<Bitmap>(t.bitmap));
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
			System.gc();

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
	private ImageCallback getImageCallback(final ImageView imageView,
			final int resId) {
		return new ImageCallback() {

			@Override
			public void loadImage(String path, Bitmap bitmap) {
				Log.e(TAG, "path:"+path);
				Log.e(TAG, "imageView:"+imageView.getTag());
				if (path.equals(imageView.getTag().toString())
						&& bitmap != null) {
					imageView.setImageBitmap(bitmap);
				} else {
					imageView.setImageResource(resId);
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
										
//										 LogUtil.error(TAG, "��������ͼƬ�����̣߳�" + t.getTask().getNum());
									} else {
										t.interrupt();
//										 LogUtil.error(TAG, "�жϱ���ͼƬ�����̣߳�" + t.getTask().getNum());
									}
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
				return  BitmapFactory.decodeByteArray(icon, 0, icon.length,
						opt);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	class ListItemView{
		TextView time;
		TextView size;
		ImageView icon;
		TextView name;
	}

}