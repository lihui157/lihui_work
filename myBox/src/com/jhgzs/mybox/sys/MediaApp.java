package com.jhgzs.mybox.sys;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;







import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jhgzs.mybox.model.bean.MediaFileInfo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

/* *
 * @author lihui
 * 2013.5.21
 * 全局application类，用于定义全局变量以及缓存变量
 *
 */
public class MediaApp extends Application {
	
	private static final String TAG = "MediaApp";
	
	public static SDCardFileListener listener;
	
	public static String hostName;
	
	//本地图片类型文件索引列表
	public static LinkedList<MediaFileInfo> imgList;
	//本地视频类型文件索引列表
	public static LinkedList<MediaFileInfo> videoList;
	//本地音频类型文件索引列表
	public static LinkedList<MediaFileInfo> audioList;
	//本地文件目录索引列表
//	public static HashMap<String,LocalBrowseList> folderMap;
	// 本地图片类型文件索引列表
	public static LinkedList<MediaFileInfo> imgListTemp;
	// 本地视频类型文件索引列表
	public static LinkedList<MediaFileInfo> videoListTemp;
	// 本地音频类型文件索引列表
	public static LinkedList<MediaFileInfo> audioListTemp;
	// 本地文件目录索引列表
//	public static HashMap<String, LocalBrowseList> folderMapTemp;
	//系统共享目录
//	public static List<ShareFile> shareFileList;
	//是否弹出播放器
	public static boolean isOpenPlay = false;
	//是否弹出幻灯片播放
	public static boolean isImagePlay = false;
//	//是否弹出控制界面
//	public static boolean isOpenControl = false;
	
	// 图片缓存集合
//	public static Map<String, DlnaDeviceInfo> bitmap_Cache = new HashMap<String, DlnaDeviceInfo>();
	//幻灯片图片list
//	public static ArrayList<DlnaDeviceInfo> m_listdata = new ArrayList<DlnaDeviceInfo>();
	/*幻灯片图片list,因有多个activity界面都可能存在图片list，
	 * 为了避免tabhost的内嵌activity切换时导致缓存错位，因此
	 * 这里使用map来对不同的activity中的图片list进行存放。*/
//	public static HashMap<String,ImageList> m_map_listdata = null;
	/**
	 * 当前所选设备索引（列表索引）
	 */
	public static int m_BrowseItemId = -1 ;
	/**
	 * 当前浏览路径
	 */
	public static String m_BrowsePath = null ;
	
	public static ArrayList<String> m_BrowsePathArr;
	/**
	 * 当前设别别名
	 */
	public static String m_DeviceName = "" ;
	
	public static String m_parentobjid = null;
	/**
	 * 当前设备id
	 */
	public static UUID m_currentDeviceUUID = null;
	/**
	 * 当前对象ID add by lihui 用于tabhost子界面数据查询的条件
	 */
	public static String m_currentObjectId = null;
	/**
	 * 当前UI add by lihui 用于tabhost子界面识别，方便以此为依据从相应堆栈中pop或push数据
	 */
	public static String m_currentTabUI = null;
	
	public static UUID m_WorkRenderUUID = null;
	/**
	 * 播放设备名
	 */
	public static String m_WorkRenderDeviceName = "";
	public static String m_playurl = null;
	public static int m_playStyle = -1;
	
	public static String m_playTitle=null; //媒体文件标题
	public static String m_albumArtURI=null; //媒体封面图片地址
	
	public static boolean m_Mute = false;
	public static int m_Volume = -1;
	public static int m_SeekTimeInMillis = -1;
	
	public static int m_PlayPosition = -1;
	public static boolean m_OnSlidePlay = false;
	public static int m_OnSlidePlaySpeedIndex = -1;
	
	public static boolean m_isImagePlayView = false;
	public static boolean m_isVideoPlayVIew = false;
	
	
	public static long m_PlayViewCurrentMillis = -1;
	public static long m_PlayViewTotalMillis = -1;
	public static int m_PlayViewVolume = -1;
	public static boolean m_PlayViewMuted = false;
	
	public static long m_PlayViewSeekTimeInMillis = -1;
	public static int m_PlayViewSetVolume = -1;
	public static boolean m_PlayViewSetMuted = false;
	
	public static boolean m_PlayViewLoading = false;
	
	public static String m_ShareFilePath = null;
	
	public static int m_ListToalItem = 0;
	public static int m_ListLoadingItem = 0;
	public static int m_ListLoadingAddNum = 0;
	
	//add by lihui 2013.5.24
	public static String m_FolderId = null;
	public static String m_AudioId = null;
	public static String m_VideoId = null;
	public static String m_ImageId = null;
	
	final public static int m_Speed[] = {
		30000,
		20000,
		10000,
	};
//	
//	public static DLNAObject[]  m_ListLoading = null;
//	public static DLNARenderStatus m_DLNARenderStatus  = null;
//	public static ArrayList<DlnaDeviceInfo> m_listdatabackup = null;

	static public void InitList()
	{}
	static public void Destroy()
	{}
	
	static public void clearbackuplistdata()
	{
//		if(null != m_listdatabackup)
//		{		
//	    	for(GenieDlnaDeviceInfo device : m_listdatabackup)
//	    	{
//	    		if(device.m_bitmap != null)
//	    			device.m_bitmap.recycle();
//	    		if(device.m_icon != null)
//	    			device.m_icon = null;
//	    	}
//	    	m_listdatabackup.clear();
//		}
	}
	
	//-------------------------------------------------------------------
	
	public static MediaApp instance;

    public final static String SLEEP_INTENT = "org.videolan.vlc.SleepIntent";

    @Override
    public void onCreate() {
        super.onCreate();

        // Are we using advanced debugging - locale?
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String p = pref.getString("set_locale", "");
        if (p != null && !p.equals("")) {
            Locale locale;
            // workaround due to region code
            if(p.equals("zh-TW")) {
                locale = Locale.TRADITIONAL_CHINESE;
            } else if(p.startsWith("zh")) {
                locale = Locale.CHINA;
            } else if(p.equals("pt-BR")) {
                locale = new Locale("pt", "BR");
            } else if(p.equals("bn-IN") || p.startsWith("bn")) {
                locale = new Locale("bn", "IN");
            } else {
                /**
                 * Avoid a crash of
                 * java.lang.AssertionError: couldn't initialize LocaleData for locale
                 * if the user enters nonsensical region codes.
                 */
                if(p.contains("-"))
                    p = p.substring(0, p.indexOf('-'));
                locale = new Locale(p);
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

		instance = this;

		new Thread() {
			public void run() {
				try {
					if (listener == null) {
						listener = new SDCardFileListener(Environment
								.getExternalStorageDirectory()
								.getCanonicalPath());
						listener.startWatching();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
        	
		new Thread(){
			public void run(){
				try {
					hostName = getLocalIpAddress().getHostName();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		
		

    }
    
    /**
	 * 获取ip
	 * @return
	 * @throws UnknownHostException
	 */
	private InetAddress getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) getSystemService(android.content.Context.WIFI_SERVICE );
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return InetAddress.getByName(String.format("%d.%d.%d.%d",
                        (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)));

    }   

    /**
     * Called when the overall system is running low on memory
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "System is running low on memory");

        System.gc();
//        BitmapCache.getInstance().clear();
    }

    /**
     * @return the main context of the Application
     */
    public static Context getAppContext()
    {
        return instance;
    }

    /**
     * @return the main resources from the Application
     */
    public static Resources getAppResources()
    {
        if(instance == null) return null;
        return instance.getResources();
    }
	
	
	


	

	
	
	
	
	

}
