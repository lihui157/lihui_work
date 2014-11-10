package com.jhgzs.mobsite.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.os.Environment;
import android.util.Log;

public class FileUtil {
	private static final String TAG = "FileUtil";  
//	 /**
//	  * 获取图片缓存
//	  * @param imageUri
//	  * @return
//	  */
//    public static File getCacheFile(String imageUri){  
//        File cacheFile = null;  
//        try {  
//            if (Environment.getExternalStorageState().equals(  
//                    Environment.MEDIA_MOUNTED)) {  
//                File sdCardDir = Environment.getExternalStorageDirectory();  
//                String fileName = getFileName(imageUri);  
//                File dir = new File(sdCardDir.getCanonicalPath()  
//                        + AsynImageLoader.CACHE_DIR);  
//                if (!dir.exists()) {  
//                    dir.mkdirs();  
//                }  
//                cacheFile = new File(dir, fileName);  
////                LogUtil.info(TAG, "exists:" + cacheFile.exists() + ",dir:" + dir + ",file:" + fileName);  
//            }    
//        } catch (IOException e) {  
//            e.printStackTrace();  
//            LogUtil.debug(TAG, "getCacheFileError:" + e.getMessage());  
//        }  
//          
//        return cacheFile;  
//    }  
      
    public static String getFileName(String path) {  
        int index = path.lastIndexOf("/");  
        return path.substring(index + 1);  
    }  
    
    /** 
     *  根据路径删除指定的目录或文件，无论存在与否 
     *@param sPath  要删除的目录或文件 
     *@return 删除成功返回 true，否则返回 false。 
     */  
    public static boolean DeleteFolder(String sPath) {  
        boolean flag = false;  
        File file = new File(sPath);  
        // 判断目录或文件是否存在  
        if (!file.exists()) {  // 不存在返回 false  
            return flag;  
        } else {  
            // 判断是否为文件  
            if (file.isFile()) {  // 为文件时调用删除文件方法  
                return deleteFile(sPath);  
            } else {  // 为目录时调用删除目录方法  
                return deleteDirectory(sPath);  
            }  
        }  
    } 
    
    /** 
     * 删除单个文件 
     * @param   sPath    被删除文件的文件名 
     * @return 单个文件删除成功返回true，否则返回false 
     */  
    public static boolean deleteFile(String sPath) {  
    	boolean flag = false;  
        File file = new File(sPath);  
        // 路径为文件且不为空则进行删除  
        if (file.isFile() && file.exists()) {  
            file.delete();  
            flag = true;  
        }  
        return flag;  
    } 
    
    /** 
     * 删除目录（文件夹）以及目录下的文件 
     * @param   sPath 被删除目录的文件路径 
     * @return  目录删除成功返回true，否则返回false 
     */  
    public static boolean deleteDirectory(String sPath) {  
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
        if (!sPath.endsWith(File.separator)) {  
            sPath = sPath + File.separator;  
        }  
        File dirFile = new File(sPath);  
        //如果dir对应的文件不存在，或者不是一个目录，则退出  
        if (!dirFile.exists() || !dirFile.isDirectory()) {  
            return false;  
        }  
       boolean  flag = true;  
        //删除文件夹下的所有文件(包括子目录)  
        File[] files = dirFile.listFiles();  
        for (int i = 0; i < files.length; i++) {  
            //删除子文件  
            if (files[i].isFile()) {  
                flag = deleteFile(files[i].getAbsolutePath());  
                if (!flag) break;  
            } //删除子目录  
            else {  
                flag = deleteDirectory(files[i].getAbsolutePath());  
                if (!flag) break;  
            }  
        }  
        if (!flag) return false;  
        //删除当前目录  
        if (dirFile.delete()) {  
            return true;  
        } else {  
            return false;  
        }  
    } 
    
    /**
     * 判断目录是否重复
     * @return
     */
    public static boolean isRepeat(String[] pathArr,String path){
    	boolean tag = false;
    	if(pathArr==null||pathArr.length==0) return false;
    	for(int i = 0;i<pathArr.length;i++){
    		if(pathArr[i].equals(path)){
    			tag = true;
    		}
    	}
    	return tag;
    }
    
    /**
     * 获取文件后缀名
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename) {   
        if ((filename != null) && (filename.length() > 0)) {   
            int dot = filename.lastIndexOf('.');   
            if ((dot >-1) && (dot < (filename.length() - 1))) {   
                return filename.substring(dot + 1);   
            }   
        }   
        return "";   
    }
    
    /**
     * 写入txt文档
     * @param path
     * @param str
     */
    public static void writeText(String path,String str){
    	BufferedWriter bw = null;
		try {
			if(str!=null&&!str.equals("")){
				File file = new File(path);
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
				if(!file.exists()){
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file, false);
				bw = new BufferedWriter(fw);
				bw.write(str);
			}else{
				Log.e(TAG,"error : str=" + str);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bw!=null)
					bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    /**
     * 返回文件内容
     * @param path
     * @return
     */
    public static String readText(String path){
    	File file = new File(path);
    	StringBuffer sb = new StringBuffer();
		  try {
		   BufferedReader bw = new BufferedReader(new FileReader(file));
		   String line = null;
		   //因为不知道有几行数据，所以先存入list集合中
		   while((line = bw.readLine()) != null){
			   sb.append(line);
		   }
		   bw.close();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }catch (Error e) {
			e.printStackTrace();
			System.gc();
		}
		  return sb.toString();
    }
    
    /** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */ 
   public static boolean copyFile(String oldPath, String newPath) { 
	   boolean b= false;
       try { 
           int bytesum = 0; 
           int byteread = 0; 
           File oldfile = new File(oldPath); 
           if (oldfile.exists()) { //文件存在时 
               InputStream inStream = new FileInputStream(oldPath); //读入原文件 
               FileOutputStream fs = new FileOutputStream(newPath); 
               byte[] buffer = new byte[1444]; 
               int length; 
               while ( (byteread = inStream.read(buffer)) != -1) { 
                   bytesum += byteread; //字节数 文件大小 
                   fs.write(buffer, 0, byteread); 
               } 
               inStream.close(); 
               b = true;
           } 
       } 
       catch (Exception e) { 
           System.out.println("复制单个文件操作出错"); 
           e.printStackTrace(); 

       } 
       return b;

   } 
   
// 复制文件夹
   public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
       // 新建目标目录
       (new File(targetDir)).mkdirs();
       // 获取源文件夹当前下的文件或目录
       File[] file = (new File(sourceDir)).listFiles();
       for (int i = 0; i < file.length; i++) {
           if (file[i].isFile()) {
               // 源文件
               File sourceFile = file[i];
               // 目标文件
               File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
               copyFile(sourceFile.getCanonicalPath(), targetFile.getCanonicalPath());
           }
           if (file[i].isDirectory()) {
               // 准备复制的源文件夹
               String dir1 = sourceDir + "/" + file[i].getName();
               // 准备复制的目标文件夹
               String dir2 = targetDir + "/" + file[i].getName();
               copyDirectiory(dir1, dir2);
           }
       }
   }
    
}
