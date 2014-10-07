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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.XMLReader;

import com.ab.util.AbFileUtil;

import net.cstong.android.MyApplication;
import net.cstong.android.R;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class AsynHtmlContentLoader {  
    private static final String TAG = "AsynHtmlContentLoader"; 
    // 任务队列  
    private List<Task> taskQueue;  
    private HashMap<String,GetHtmlContentThread> threadMap;
    private int start;
    private int end;
    private boolean isRunning = false; 
    public static Context mContext;
    private LruCache<String, Spanned> caches;
      
    public AsynHtmlContentLoader(Context context){  
    	
    	this.mContext = context;
        // 初始化变量  
//        caches = new HashMap<String, SoftReference<Bitmap>>();  
     
    	initLruCache();
    	
        taskQueue = new ArrayList<AsynHtmlContentLoader.Task>();  
        // 启动图片下载线程  
        isRunning = true;  
        new Thread(runnable).start();  
    }  
    
    private void initLruCache(){
		// LruCache通过构造函数传入缓存值，以KB为单位。 
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024); 
        // 使用最大可用内存值的1/8作为缓存的大小。 
        int cacheSize = maxMemory / 8; 
        caches = new LruCache<String, Spanned>(cacheSize) { 
            @SuppressLint("NewApi")
			@Override
            protected int sizeOf(String key, Spanned spanned) { 
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。 
                return spanned.length() / 1024; 
            } 
        };
	}
      
    /** 
     *  
     * @param imageView 需要延迟加载图片的对象 
     * @param url 图片的URL地址 
     * @param resId 图片加载过程中显示的图片资源 
     */  
    public void showHtmlContentAsyn(final TextView textView, final String resource, final int resId){
    	handler.post(new Runnable() {
			
			@Override
			public void run() {
				String key = getKey(resource);
				textView.setTag(key);  
				Spanned content = loadHtmlContentAsyn(resource,textView, getImageCallback(textView, resId));  
		        if(content == null){  
		        	textView.setText("加载中...");  
		        }else{  
		        	textView.setText(content); 
		        	Log.e(TAG, "textview:"+textView.getHeight());
		        } 
				
			}
		});
         
       
    }  
    
    private String getKey(String str){
    	return String.valueOf(str.hashCode());
//    	if(str.length()>50){
//    		return str.substring(0, 50);
//    		
//    	}else{
//    		return str.substring(0,str.length());
//    	}
    	
    }
      
    private Spanned loadHtmlContentAsyn(String resource,TextView textView, HtmlContentCallback callback){ 
    	
    	if(resource==null){
    		return null;
    	}
    	String key = getKey(resource);
    	Spanned content= caches.get(key);
    	if(caches.get(key)!=null){
    		return content;
    	}else{
    		Task task = new Task(); 
        	task.key = key;
            task.resource = resource;  
            task.textView = textView;
            task.callback = callback;  
//            LogUtil.info(TAG, "new Task ," + path);  
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
    private HtmlContentCallback getImageCallback(final TextView textView, final int resId){  
        return new HtmlContentCallback() {  
              
            @Override  
            public void loadHtmlContent(String key, Spanned content) {  
                if(key.equals(textView.getTag().toString())){  
                	textView.setText(content);
                	Log.e(TAG, "textview:"+textView.getHeight());
//                	textView.setText(content);
                }else{  
                	textView.setText("加载中...");  
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
            task.callback.loadHtmlContent(task.key, task.content);  
            
        }  
          
    };  
      
    private Runnable runnable = new Runnable() {  
          
        @Override  
        public void run() {  
            while(isRunning){  
                // 当队列中还有未处理的任务时，执行下载任务  
                while(taskQueue.size() > 0){  
//                	new GetHtmlContentThread(taskQueue.remove(0)).start();
                	Task t = taskQueue.remove(0);
                	if(t!=null){
                    	if(t.resource!=null&&t.textView!=null){
                    		t.content = getHtmlContent(t.resource,t.textView); 
                    		if(t.content!=null){
                    			caches.put(t.key, t.content);
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
    private class GetHtmlContentThread extends Thread{
    	Task t;
    	
    	public GetHtmlContentThread(Task t){
    		this.t = t;
    	}
    	
    	@Override  
        public void run() { 
            // 将下载的图片添加到缓存  
    		if(t!=null){
            	if(t.resource!=null&&t.textView!=null){
            		t.content = getHtmlContent(t.resource,t.textView); 
            		if(t.content!=null){
            			caches.put(t.key, t.content);
            		}
            		
            	}
            }
    		
//    		if(t==null)return;
//    		if(t.resource==null) return;
//    		
//            t.content = getHtmlContent(t.resource,t.textView);  
//            if(t.content==null) return; 
//            caches.put(t.key, t.content);
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
    public interface HtmlContentCallback{  
        void loadHtmlContent(String path, Spanned content);  
    }  
      
    class Task{  
    	TextView textView;
    	
    	String key;
        // 下载任务的下载路径  
        String resource;  
        // 下载的图片  
        Spanned content;  
        // 回调对象  
        HtmlContentCallback callback;  
          
        @Override  
        public boolean equals(Object o) {  
            Task task = (Task)o; 
            if(task!=null&&task.key!=null&&key==null){
            	return task.key.equals(key); 
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
	private static Spanned getHtmlContent(String resource,TextView textView) {
		resource = resource
				.replace("\" </img>", "</img>")
				.replace("[img]", "")
				.replace("[/img]", "")
				.replace("\"/>\"/>", "\"/>")
				.replace("src=\"<img", "")
				.replace("<img", "<br/><img")
//		Pattern pattern = Pattern.compile("<.+?>", Pattern.DOTALL);
//		Matcher matcher = pattern.matcher(resource);
//		String string = matcher.replaceAll("");
//		System.out.println(string);
//		resource = string;
				;
		Log.e(TAG, "-----------"+resource);
		try {
			Spanned spanned = Html.fromHtml(resource
					,new HtmlImageGetter(
							textView
							, AbFileUtil.getImageDownFullDir()
							,mContext.getResources().getDrawable(R.drawable.progress_circular))
					,null);
			
//			Spanned spanned = Html.fromHtml(resource
//			,new URLImageGetter(mContext, textView)
////			,new HtmlImageGetter(textView, AbFileUtil.getImageDownFullDir(),mContext.getResources().getDrawable(R.drawable.progress_circular))
//			,null);
//	Spanned spanned = Html.fromHtml(resource,new Html.ImageGetter(){
//
//          @Override
//          public Drawable getDrawable(String source) {
//        	  source = source.replace("[img]", "");
//             Drawable myDrawable = null;
//             try {
//                myDrawable = Drawable.createFromStream(new URL(source).openStream(), "baidu_sylogo1.gif"); 
//             } catch (MalformedURLException e) {
//                e.printStackTrace();
//             } catch (IOException e) {
//                e.printStackTrace();
//             }
//
//             if(myDrawable==null){
//            	 return null;
//             }
////            myDrawable=zoomDrawable(myDrawable,600,600);
//             
//            myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
//
//             return myDrawable;
//          }},null);
			
			return spanned;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		

		
	}
	
	private static Bitmap drawableToBitmap(Drawable drawable)// drawable 转换成bitmap 
	{ 
		int width = drawable.getIntrinsicWidth(); // 取drawable的长宽 
		int height = drawable.getIntrinsicHeight(); 
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 
		: Bitmap.Config.RGB_565; // 取drawable的颜色格式 
		Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应bitmap 
		Canvas canvas = new Canvas(bitmap); // 建立对应bitmap的画布 
		drawable.setBounds(0, 0, width, height); 
		drawable.draw(canvas); // 把drawable内容画到画布中 
		return bitmap; 
	} 
	
	// 缩放图片
		public static Bitmap zoomImg(Bitmap bitmap, int newWidth ,int newHeight){
			 int width = bitmap.getWidth();
			  int height = bitmap.getHeight();
			  float scaleWidth = ((float) newWidth) / width;
			  float scaleHeight = ((float) newHeight) / height;
			  Matrix matrix = new Matrix();
			  matrix.postScale(scaleWidth, scaleHeight);
			  // create the new Bitmap object
			  Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
			    height, matrix, true);
//			  if (!bitmap.isRecycled()) {
//					bitmap.recycle(); // 回收图片所占的内存
//					// System.gc(); // 提醒系统及时回收
//				}
		    return resizedBitmap;
		}
	
	private static Drawable zoomDrawable(Drawable drawable, int w, int h) 
    { 
              int width = drawable.getIntrinsicWidth(); 
              int height= drawable.getIntrinsicHeight(); 
              Bitmap oldbmp = drawableToBitmap(drawable);// drawable转换成bitmap 
              Matrix matrix = new Matrix();   // 创建操作图片用的Matrix对象 
              float scaleWidth = ((float)w / width);   // 计算缩放比例 
              float scaleHeight = ((float)h / height); 
              matrix.postScale(scaleWidth, scaleHeight);         // 设置缩放比例 
              Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);       // 建立新的bitmap，其内容是对原bitmap的缩放后的图 
              return new BitmapDrawable(mContext.getResources() ,newbmp);       // 把bitmap转换成drawable并返回 
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
		} catch(OutOfMemoryError e){
			e.printStackTrace();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}  
