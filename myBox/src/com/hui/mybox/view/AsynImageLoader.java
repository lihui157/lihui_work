package com.hui.mybox.view;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;




import com.hui.mybox.sys.Config;
import com.hui.mybox.utils.PicUtil;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class AsynImageLoader {  
    private static final String TAG = "AsynImageLoader";  
    public static final String CACHE_DIR = Config.Sys.IMG_CACHE_PATH;
    // �������ع���ͼƬ��Map  
    private Map<String, SoftReference<Bitmap>> caches;  
    // �������  
    private List<Task> taskQueue;  
    private HashMap<String,GetBitMapThread> threadMap;
    private int start;
    private int end;
    private boolean isRunning = false;  
      
    public AsynImageLoader(){  
        // ��ʼ������  
        caches = new HashMap<String, SoftReference<Bitmap>>();  
        taskQueue = new ArrayList<AsynImageLoader.Task>();  
        // ����ͼƬ�����߳�  
        isRunning = true;  
        new Thread(runnable).start();  
    }  
      
    /** 
     *  
     * @param imageView ��Ҫ�ӳټ���ͼƬ�Ķ��� 
     * @param url ͼƬ��URL��ַ 
     * @param resId ͼƬ���ع�������ʾ��ͼƬ��Դ 
     */  
    public void showImageAsyn(ImageView imageView, String url, int resId){  
        imageView.setTag(url);  
        Bitmap bitmap = loadImageAsyn(url, getImageCallback(imageView, resId));  
        if(bitmap == null){  
            imageView.setImageResource(resId);  
        }else{  
            imageView.setImageBitmap(bitmap);  
        }  
       
    }  
      
    public Bitmap loadImageAsyn(String path, ImageCallback callback){  
        // �жϻ������Ƿ��Ѿ����ڸ�ͼƬ  
        if(caches.containsKey(path)){ 
            // ȡ��������  
            SoftReference<Bitmap> rf = caches.get(path);  
            // ͨ�������ã���ȡͼƬ  
            Bitmap bitmap = rf.get();  
            // �����ͼƬ�Ѿ����ͷţ��򽫸�path��Ӧ�ļ���Map���Ƴ���  
            if(bitmap == null){  
//            	LogUtil.debug(TAG, "loadImageAsyn", "caches.remove(path)"+path);
                caches.remove(path);  
                Task task = new Task();  
                task.path = path;  
                task.callback = callback;  
//                LogUtil.info(TAG, "new Task ," + path);  
                if(!taskQueue.contains(task)){  
                    taskQueue.add(task);  
                    // �����������ض���  
                    synchronized (runnable) {  
                        runnable.notify();  
                    }  
                }  
                
            }else{  
                // ���ͼƬδ���ͷţ�ֱ�ӷ��ظ�ͼƬ  
//                LogUtil.debug(TAG, "loadImageAsyn","bitmap="+bitmap+"    return image in cache" + path);  
                return bitmap;  
            }  
        }else{  
            // ��������в����ڸ�ͼƬ���򴴽�ͼƬ��������  
//        	 LogUtil.debug(TAG, "loadImageAsyn","�����в����ڸ�ͼƬ���򴴽�ͼƬ��������" + path);  
            Task task = new Task();  
            task.path = path;  
            task.callback = callback;  
//            LogUtil.info(TAG, "new Task ," + path);  
            if(!taskQueue.contains(task)){  
                taskQueue.add(task);  
                // �����������ض���  
                synchronized (runnable) {  
                    runnable.notify();  
                }  
            }  
        }  
          
        // ������û��ͼƬ�򷵻�null  
        return null;  
    }  
      
    /** 
     *  
     * @param imageView  
     * @param resId ͼƬ�������ǰ��ʾ��ͼƬ��ԴID 
     * @return 
     */  
    private ImageCallback getImageCallback(final ImageView imageView, final int resId){  
        return new ImageCallback() {  
              
            @Override  
            public void loadImage(String path, Bitmap bitmap) {  
                if(path.equals(imageView.getTag().toString())){  
                    imageView.setImageBitmap(bitmap);  
                }else{  
                    imageView.setImageResource(resId);  
                }  
            }  
        };  
    }  
      
    private Handler handler = new Handler(){  
  
        @Override  
        public void handleMessage(Message msg) {  
            // ���߳��з��ص�������ɵ�����  
            Task task = (Task)msg.obj;  
            // ����callback�����loadImage����������ͼƬ·����ͼƬ�ش���adapter  
            task.callback.loadImage(task.path, task.bitmap);  
            
        }  
          
    };  
      
    private Runnable runnable = new Runnable() {  
          
        @Override  
        public void run() {  
            while(isRunning){  
                // �������л���δ���������ʱ��ִ����������  
                while(taskQueue.size() > 0){  
                	new GetBitMapThread(taskQueue.remove(0)).start();
                	
                	
                	 // �����ص�ͼƬ��ӵ�����  
//                	Task t = taskQueue.remove(0);
//                    t.bitmap = PicUtil.getbitmap(t.path);  
//                    caches.put(t.path, new SoftReference<Bitmap>(t.bitmap));  
//                    if(handler != null){  
//                        // ������Ϣ���󣬲�����ɵ�������ӵ���Ϣ������  
//                        Message msg = handler.obtainMessage();  
//                        msg.obj = t;  
//                        // ������Ϣ�����߳�  
//                        handler.sendMessage(msg);  
//                    }  
                }  
                  
                //�������Ϊ��,�����̵߳ȴ�  
                synchronized (this) {  
                    try {  
                        this.wait();  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }  
    };  
    
    /**
     * ��ȡBitmap�߳�
     */
    private class GetBitMapThread extends Thread{
    	Task t;
    	
    	public GetBitMapThread(Task t){
    		this.t = t;
    	}
    	
    	@Override  
        public void run() { 
            // �����ص�ͼƬ��ӵ�����  
            t.bitmap = PicUtil.getbitmap(t.path);  
            caches.put(t.path, new SoftReference<Bitmap>(t.bitmap));  
            if(handler != null){  
                // ������Ϣ���󣬲�����ɵ�������ӵ���Ϣ������  
                Message msg = handler.obtainMessage();  
                msg.obj = t;  
                // ������Ϣ�����߳�  
                handler.sendMessage(msg);  
            }  
    	}
    }
      
    //�ص��ӿ�  
    public interface ImageCallback{  
        void loadImage(String path, Bitmap bitmap);  
    }  
      
    class Task{  
        // �������������·��  
        String path;  
        // ���ص�ͼƬ  
        Bitmap bitmap;  
        // �ص�����  
        ImageCallback callback;  
          
        @Override  
        public boolean equals(Object o) {  
            Task task = (Task)o;  
            return task.path.equals(path);  
        }  
    }

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}  
    
}  
