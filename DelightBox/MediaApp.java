package com.dragonflow.sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.videolan.vlc.AudioUtil;
import org.videolan.vlc.BitmapCache;
import org.videolan.vlc.MediaDatabase;

import com.dragonflow.mymedia.activity.AudioListAct;
import com.dragonflow.mymedia.activity.FolderListAct;
import com.dragonflow.mymedia.activity.ImgListAct;
import com.dragonflow.mymedia.activity.VideoListAct;
import com.dragonflow.mymedia.obj.LocalBrowseList;
import com.dragonflow.mymedia.obj.MediaFileInfo;
import com.dragonflow.mymedia.obj.ShareFile;
import com.dragonflow.mymedia.obj.DLNABrowseList;
import com.dragonflow.mymedia.obj.DlnaDeviceInfo;
import com.dragonflow.mymedia.obj.ImageList;
import com.dragonflow.uitl.DlnaUtil;
import com.dragonflow.uitl.FileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netgear.genie.media.dlna.DLNAConfig;
import com.netgear.genie.media.dlna.DLNAItem;
import com.netgear.genie.media.dlna.DLNAObject;
import com.netgear.genie.media.dlna.DLNARenderStatus;
import com.netgear.genie.media.dlna.DeviceDesc;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
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
	
	//本地图片类型文件索引列表
	public static ArrayList<MediaFileInfo> imgList;
	//本地视频类型文件索引列表
	public static ArrayList<MediaFileInfo> videoList;
	//本地音频类型文件索引列表
	public static ArrayList<MediaFileInfo> audioList;
	//本地文件目录索引列表
	public static HashMap<String,LocalBrowseList> folderMap;
	// 本地图片类型文件索引列表
	public static ArrayList<MediaFileInfo> imgListTemp;
	// 本地视频类型文件索引列表
	public static ArrayList<MediaFileInfo> videoListTemp;
	// 本地音频类型文件索引列表
	public static ArrayList<MediaFileInfo> audioListTemp;
	// 本地文件目录索引列表
	public static HashMap<String, LocalBrowseList> folderMapTemp;
	//系统共享目录
	public static List<ShareFile> shareFileList;
	//是否弹出播放器
	public static boolean isOpenPlay = false;
	//是否弹出幻灯片播放
	public static boolean isImagePlay = false;
//	//是否弹出控制界面
//	public static boolean isOpenControl = false;
	
	// 图片缓存集合
	public static Map<String, DlnaDeviceInfo> bitmap_Cache = new HashMap<String, DlnaDeviceInfo>();
	//幻灯片图片list
	public static ArrayList<DlnaDeviceInfo> m_listdata = new ArrayList<DlnaDeviceInfo>();
	/*幻灯片图片list,因有多个activity界面都可能存在图片list，
	 * 为了避免tabhost的内嵌activity切换时导致缓存错位，因此
	 * 这里使用map来对不同的activity中的图片list进行存放。*/
	public static HashMap<String,ImageList> m_map_listdata = null;
	/**
	 * 服务设备列表
	 */
	public static List<DeviceDesc> m_Serverlist ;
	/**
	 * 渲染器列表（播放器列表）
	 */
	public static List<DeviceDesc> m_Rendererlist ;
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
	
	public static HashMap<String ,Stack<DLNABrowseList> > stackMap = null;
	public static  Stack<DLNABrowseList> m_datastack = null;
//	public static  Stack<DLNABrowseList> m_folderstack = null;
//	public static  Stack<DLNABrowseList> m_audiostack = null;
//	public static  Stack<DLNABrowseList> m_videostack = null;
//	public static  Stack<DLNABrowseList> m_imagestack = null;
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
	public static DLNAItem m_playItem = null;
	public static String m_playurl = null;
	public static int m_playStyle = -1;
	
	public static String m_playTitle=null; //媒体文件标题
	public static String m_albumArtURI=null; //媒体封面图片地址
	public static DLNAConfig m_DLNAConfig = null;
	
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
	
	public static DLNAObject[]  m_ListLoading = null;
	public static DLNARenderStatus m_DLNARenderStatus  = null;
	public static ArrayList<DlnaDeviceInfo> m_listdatabackup = null;

	static public void InitList()
	{
		m_Serverlist =  new ArrayList<DeviceDesc>();
		m_Rendererlist =  new ArrayList<DeviceDesc>();
		m_BrowsePathArr = new ArrayList<String>();
		
		stackMap = new HashMap<String,Stack<DLNABrowseList>>();
		stackMap.put(FolderListAct.class.getName(), new Stack<DLNABrowseList>());
		stackMap.put(AudioListAct.class.getName(), new Stack<DLNABrowseList>());
		stackMap.put(VideoListAct.class.getName(), new Stack<DLNABrowseList>());
		stackMap.put(ImgListAct.class.getName(), new Stack<DLNABrowseList>());
		
		m_map_listdata = new HashMap<String,ImageList>();
		m_map_listdata.put(FolderListAct.class.getName(), new ImageList());
		m_map_listdata.put(AudioListAct.class.getName(), new ImageList());
		m_map_listdata.put(VideoListAct.class.getName(), new ImageList());
		m_map_listdata.put(ImgListAct.class.getName(), new ImageList());
		
		m_datastack = new Stack<DLNABrowseList>();
		
		m_listdatabackup = new  ArrayList<DlnaDeviceInfo>();
		
		m_BrowseItemId = -1;
		m_ListToalItem = 0;
		m_ListLoadingItem = 0;
		m_ListLoadingAddNum = 0;
		m_DLNARenderStatus  = new DLNARenderStatus();
		m_DLNAConfig = new DLNAConfig();
		m_PlayViewCurrentMillis = -1;
		m_PlayViewTotalMillis = -1;
		m_PlayViewVolume = -1;
		m_PlayViewMuted = false;
		m_playStyle = -1;
		m_PlayViewSeekTimeInMillis = -1;
		m_PlayViewSetVolume = -1;
		m_PlayViewSetMuted = false;
		m_PlayViewLoading = false;
		//m_ShareFilePath = null;
		
		m_ListLoading = null;
		
		//索引缓存从索引文件中读取
		try{
			imgList = new Gson().fromJson(FileUtil.readText(Environment.getExternalStorageDirectory().getCanonicalPath()+Config.System.LOCAL_IMAGE_LIST_PATH), new TypeToken<ArrayList<MediaFileInfo>>(){}.getType());
			videoList = new Gson().fromJson(FileUtil.readText(Environment.getExternalStorageDirectory().getCanonicalPath()+Config.System.LOCAL_VIDEO_LIST_PATH), new TypeToken<ArrayList<MediaFileInfo>>(){}.getType());
			audioList = new Gson().fromJson(FileUtil.readText(Environment.getExternalStorageDirectory().getCanonicalPath()+Config.System.LOCAL_AUDIO_LIST_PATH), new TypeToken<ArrayList<MediaFileInfo>>(){}.getType());
			folderMap = new Gson().fromJson(FileUtil.readText(Environment.getExternalStorageDirectory().getCanonicalPath()+Config.System.LOCAL_FOLDER_LIST_PATH), new TypeToken<HashMap<String,LocalBrowseList>>(){}.getType());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	static public void Destroy()
	{
		m_Serverlist = null;
		m_Rendererlist =  null;
		m_datastack = null;
		m_DLNARenderStatus  = null;
		m_currentDeviceUUID = null;
		m_WorkRenderUUID = null;
		m_playItem = null;
		m_DLNAConfig = null;
		
		m_Mute = false;
		m_Volume = -1;
		m_SeekTimeInMillis = -1;
		
		m_PlayPosition = -1;
		m_ListToalItem = 0;
		m_ListLoadingItem = 0;
		m_ListLoadingAddNum = 0;
		
		m_BrowseItemId = -1;
		
		m_parentobjid = null;
		m_playurl = null;
		
		m_isImagePlayView = false;
		m_isVideoPlayVIew = false;
		m_PlayViewCurrentMillis = -1;
		m_PlayViewTotalMillis = -1;
		m_PlayViewVolume = -1;
		m_PlayViewMuted = false;
		m_playStyle = -1;
		m_PlayViewSeekTimeInMillis = -1;
		m_PlayViewSetVolume = -1;
		m_PlayViewSetMuted = false;
		m_PlayViewLoading = false;
		m_ShareFilePath = null;
		
		m_ListLoading = null;
		
		clearbackuplistdata();
//		m_listdatabackup = null;
		
	}
	
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
	
	private static MediaApp instance;

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

        // Initialize the database soon enough to avoid any race condition and crash
        MediaDatabase.getInstance(this);
        // Prepare cache folder constants
        AudioUtil.prepareCacheFolder(this);
    }

    /**
     * Called when the overall system is running low on memory
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "System is running low on memory");

        BitmapCache.getInstance().clear();
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
