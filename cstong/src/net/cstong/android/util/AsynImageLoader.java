package net.cstong.android.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.cstong.android.MyApplication;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class AsynImageLoader {  
    private static final String TAG = "AsynImageLoader"; 
    // 任务队列  
    private List<Task> taskQueue;  
    private HashMap<String,GetBitMapThread> threadMap;
    private int start;
    private int end;
    private boolean isRunning = false;  
      
    public AsynImageLoader(){  
        // 初始化变量  
//        caches = new HashMap<String, SoftReference<Bitmap>>();  
     
        taskQueue = new ArrayList<AsynImageLoader.Task>();  
        // 启动图片下载线程  
        isRunning = true;  
        new Thread(runnable).start();  
    }  
      
    /** 
     *  
     * @param imageView 需要延迟加载图片的对象 
     * @param url 图片的URL地址 
     * @param resId 图片加载过程中显示的图片资源 
     */  
    public void showImageAsyn(final ImageView imageView, final String url, final int resId){
    	handler.post(new Runnable() {
			
			@Override
			public void run() {
				imageView.setTag(url);  
		        Bitmap bitmap = loadImageAsyn(url, getImageCallback(imageView, resId));  
		        if(bitmap == null){  
		            imageView.setImageResource(resId);  
		        }else{  
		            imageView.setImageBitmap(bitmap);  
		        } 
				
			}
		});
         
       
    }  
      
    private Bitmap loadImageAsyn(String path, ImageCallback callback){ 
    	
    	if(path==null){
    		return null;
    	}
        // 判断缓存中是否已经存在该图片  
        if(MyApplication.caches.get(path) != null){  
            // 通过软引用，获取图片  
            Bitmap bitmap = MyApplication.caches.get(path); 
            // 如果该图片已经被释放，则将该path对应的键从Map中移除掉  
            if(bitmap == null){  
            	MyApplication.caches.remove(path);  
                Task task = new Task();  
                task.path = path;  
                task.callback = callback;  
                if(!taskQueue.contains(task)){  
                    taskQueue.add(task);  
                    // 唤醒任务下载队列  
                    synchronized (runnable) {  
                        runnable.notify();  
                    }  
                }  
                
            }else{  
                // 如果图片未被释放，直接返回该图片  
                return bitmap;  
            }  
        }else{  
            // 如果缓存中不常在该图片，则创建图片下载任务   
            Task task = new Task();  
            task.path = path;  
            task.callback = callback;   
            if(!taskQueue.contains(task)){  
                taskQueue.add(task);  
                // 唤醒任务下载队列  
                synchronized (runnable) {  
                    runnable.notify();  
                }  
            }  
        }  
          
        // 缓存中没有图片则返回null  
        return null;  
    }  
      
    /** 
     *  
     * @param imageView  
     * @param resId 图片加载完成前显示的图片资源ID 
     * @return 
     */  
    private ImageCallback getImageCallback(final ImageView imageView, final int resId){  
        return new ImageCallback() {  
              
            @Override  
            public void loadImage(String path, Bitmap bitmap) {  
                if(path.equals(imageView.getTag().toString())){  
                	if(bitmap==null){
                		imageView.setImageResource(resId); 
                	}else{
                		imageView.setImageBitmap(bitmap);  
                	}
                    
                }else{  
                    imageView.setImageResource(resId);  
                }  
            }  
        };  
    }  
      
    private Handler handler = new Handler(){  
  
        @Override  
        public void handleMessage(Message msg) {  
            // 子线程中返回的下载完成的任务  
            Task task = (Task)msg.obj;  
            // 调用callback对象的loadImage方法，并将图片路径和图片回传给adapter  
            task.callback.loadImage(task.path, task.bitmap);  
            
        }  
          
    };  
      
    private Runnable runnable = new Runnable() {  
          
        @Override  
        public void run() {  
            while(isRunning){  
                // 当队列中还有未处理的任务时，执行下载任务  
                while(taskQueue.size() > 0){  
//                	new GetBitMapThread(taskQueue.remove(0)).start();
                	Task t = taskQueue.remove(0);
                	if(t!=null){
                    	if(t.path!=null){
                    		t.bitmap = getbitmap(t.path);
                    		if(t.bitmap!=null){
                    			MyApplication.caches.put(t.path, t.bitmap);
                    		}
                    		
                    	}
                    }
                    
                    if(handler != null){  
                        // 创建消息对象，并将完成的任务添加到消息对象中  
                        Message msg = handler.obtainMessage();  
                        msg.obj = t;  
                        // 发送消息回主线程  
                        handler.sendMessage(msg);  
                    } 
                }  
                  
                //如果队列为空,则令线程等待  
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
     * 获取Bitmap线程
     */
    private class GetBitMapThread extends Thread{
    	Task t;
    	
    	public GetBitMapThread(Task t){
    		this.t = t;
    	}
    	
    	@Override  
        public void run() { 
            // 将下载的图片添加到缓存  
            
            if(t!=null){
            	if(t.path!=null){
            		t.bitmap = getbitmap(t.path);
            		if(t.bitmap!=null){
            			MyApplication.caches.put(t.path, t.bitmap);
            		}
            		
            	}
            }
            
            if(handler != null){  
                // 创建消息对象，并将完成的任务添加到消息对象中  
                Message msg = handler.obtainMessage();  
                msg.obj = t;  
                // 发送消息回主线程  
                handler.sendMessage(msg);  
            }  
    	}
    }
      
    //回调接口  
    public interface ImageCallback{  
        void loadImage(String path, Bitmap bitmap);  
    }  
      
    class Task{  
        // 下载任务的下载路径  
        String path;  
        // 下载的图片  
        Bitmap bitmap;  
        // 回调对象  
        ImageCallback callback;  
          
        @Override  
        public boolean equals(Object o) {  
            Task task = (Task)o; 
            if(task!=null&&task.path!=null&&path==null){
            	return task.path.equals(path); 
            }else{
            	return false;
            }
             
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
    
	
	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	private static Bitmap getbitmap(String imageUri) {
		// 显示网络上的图片
		Bitmap bitmap = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		byte[] icon = null;
		BitmapFactory.Options opt = null;
		try {
			URL myFileUrl = new URL(imageUri);
			conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			icon = getBytes(is);
			// bitmap = BitmapFactory.decodeStream(is);
			opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(icon, 0, icon.length, opt);
			if (opt.outWidth < opt.outHeight) {
				opt.inSampleSize = opt.outWidth / 80;
				;
			} else {
				opt.inSampleSize = opt.outHeight / 80;
			}
			if (opt.inSampleSize < 0) {
				opt.inSampleSize = 1;
			}
			opt.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeByteArray(icon, 0, icon.length, opt);
			// is.close();
			// icon = null;
			// opt = null;
			// conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				if (is != null)
					is.close();
			} catch (IOException e1) {
				
				e1.printStackTrace();
				return null;
			}
			icon = null;
			opt = null;
			conn.disconnect();
			return null;
		}
		return bitmap;
	}
	
	private static byte[] getBytes(InputStream is) throws IOException {

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
}  
