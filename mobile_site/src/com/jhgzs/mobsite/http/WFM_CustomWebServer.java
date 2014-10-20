package com.jhgzs.mobsite.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.jhgzs.mobsite.ServerApplication;
import com.jhgzs.mobsite.http.WFM_NanoHTTPD.Response;
import com.jhgzs.mobsite.obj.WFM_DirInforObj;
import com.jhgzs.mobsite.obj.WFM_FileItemObj;
import com.jhgzs.mobsite.obj.WFM_Result;
import com.jhgzs.mobsite.util.LogUtil;
import com.jhgzs.mobsite.util.WFM_FileUtil;
import com.jhgzs.mobsite.util.WFM_ZipCompressor;

public class WFM_CustomWebServer extends WFM_NanoHTTPD {

	private static final String TAG = "CustomWebServer";
	//url parms
	public static final String PARM_ACTION = "action";
	public static final String PARM_PATH = "path";
	public static final String PARM_FILENAME = "fileName";
	//url parm‘s values
	public static final String ACTION_VALUE_LIST = "list"; //获取指定目录
	public static final String ACTION_VALUE_CREATFOLDER = "createFolder"; //创建文件夹
	public static final String ACTION_VALUE_DEL = "delete";  //删除
	public static final String ACTION_VALUE_UPLOAD = "upload";  //上传
	public static final String ACTION_VALUE_DOWNLOADZIP = "downloadzip";
	public static final String ACTION_VALUE_ISWRITABLE= "isWritable";  //是否可写
	public static final String ACTION_VALUE_RENAME = "rename";   //重命名
	private Context context;
	
	public WFM_CustomWebServer(Context c,int port, File wwwroot) throws IOException {
		super(port, wwwroot);
		this.context = c;
		LogUtil.info(TAG, "CustomWebServer", "start");
	}

	@Override
	public Response serve(String uri, String method, Properties header,
			Properties parms, Properties files) {
		LogUtil.debug(TAG, "serve", "uri = "+uri);
		LogUtil.debug(TAG, "serve", "method = "+method);
		LogUtil.debug(TAG, "serve", "parms = "+parms.toString());
		return doAction(uri, method, header,parms, files);
	}
	
	private Response doAction(String uri, String method, Properties header,
			Properties parms, Properties files){
		String action = parms.getProperty(PARM_ACTION,"");
		//获取文件列表
			if(action.equals(ACTION_VALUE_LIST)){
				//获取其他参数
				String path = parms.getProperty(PARM_PATH,"");
				//设置操作返回数据对象
				WFM_Result result = new WFM_Result(WFM_Result.SUCCESS, "");
				result.setResultData(getDirInforObj(path));
				//返回结果
				return new Response(HTTP_OK, MIME_HTML, new Gson().toJson(result));
			}
			//创建文件夹
			if(action.equals(ACTION_VALUE_CREATFOLDER)){
				String path = parms.getProperty(PARM_PATH,"");
				String folder = parms.getProperty(PARM_FILENAME, "");
				if(folder.equals("")){
					return new Response(HTTP_OK, MIME_HTML, new Gson().toJson(new WFM_Result(WFM_Result.FAIL, "param fileName is ''")) );
				}else{
					return new Response(HTTP_OK, MIME_HTML, new Gson().toJson(createFolder(path,folder)) );
				}
			}
			//删除文件
			if(action.equals(ACTION_VALUE_DEL)){
				String path = parms.getProperty(PARM_PATH,"");
				if(path.equals("")){
					return new Response(HTTP_OK, MIME_HTML, new Gson().toJson(new WFM_Result(WFM_Result.FAIL, "param path is ''")) );
				}else{
					return new Response(HTTP_OK, MIME_HTML, new Gson().toJson(delete(path)) );
				}
			}
			//重命名
			if(action.equals(ACTION_VALUE_RENAME)){
				String path = parms.getProperty(PARM_PATH,"");
				String fileName = parms.getProperty(PARM_FILENAME, "");
				if(fileName.equals("")){
					return new Response(HTTP_OK, MIME_HTML, new Gson().toJson(new WFM_Result(WFM_Result.FAIL, "param fileName is ''")) );
				}else{
					return new Response(HTTP_OK, MIME_HTML, new Gson().toJson(rename(path, fileName)) );
				}
			}
			
			//上传
			if(action.equals(ACTION_VALUE_UPLOAD)){
				String path = parms.getProperty(PARM_PATH,"");
				String datafile = parms.getProperty("datafile","");
				if(datafile.equals("")){
					return new Response(HTTP_OK, MIME_HTML, new Gson().toJson(new WFM_Result(WFM_Result.FAIL, "param datafile is ''")) );
				}else{
					try {
//						boolean b = FileUtil.copyFile(files.getProperty("datafile", ""), Environment.getExternalStorageDirectory().getCanonicalPath()+path+"/"+datafile);
						boolean b = WFM_FileUtil.copyFile(files.getProperty("datafile", ""), path+"/"+datafile);
						//删除缓存
						WFM_FileUtil.deleteFile(files.getProperty("datafile", ""));
						if(b){
							return new Response(HTTP_OK, MIME_HTML, new Gson().toJson(new WFM_Result(WFM_Result.SUCCESS, "")) );
						}else{
							return new Response(HTTP_OK, MIME_HTML, new Gson().toJson(new WFM_Result(WFM_Result.FAIL, "")) );
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			if(action.equals(ACTION_VALUE_DOWNLOADZIP)){
				String path = parms.getProperty(PARM_PATH,"");
				if("".equals(path)){
					return new Response(HTTP_OK, MIME_HTML, new Gson().toJson(new WFM_Result(WFM_Result.FAIL, "param path is ''")) );
				}else{
					String zipPath;
					try {
//						zipPath = Environment.getExternalStorageDirectory().getCanonicalPath()+path;
						zipPath = path;
						String zipFile = zipPath+".temp.zip";
						WFM_ZipCompressor zipCompressor = new WFM_ZipCompressor(zipFile);
						boolean b = zipCompressor.compress(zipPath);
						if(b){
							WFM_Result result = new WFM_Result(WFM_Result.SUCCESS, "");
							result.setResultData(path+".temp.zip");
							return new Response(HTTP_OK, MIME_HTML, new Gson().toJson(result) );
						}else{
							return new Response(HTTP_OK, MIME_HTML, new Gson().toJson(new WFM_Result(WFM_Result.FAIL, "Compression failure")) );
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
			
		return super.serve(uri, method, header, parms, files);
	}
	
	/**
	 * 获取列表对象
	 * @param path
	 * @return
	 */
	private WFM_DirInforObj getDirInforObj(String path){
		
		WFM_DirInforObj dirInforObj = new WFM_DirInforObj();
		File file = null;
		ArrayList<WFM_FileItemObj> itemObjs = null;
		//定位目录
		try {
			if (path == null || "".equals(path)
					||(Environment.getExternalStorageDirectory().getCanonicalPath().startsWith(path)
							&&Environment.getExternalStorageDirectory().getCanonicalPath().length()>path.length())) {
				file = Environment.getExternalStorageDirectory();
//				dirInforObj.setCurrentPath(file.getCanonicalPath().replace(Environment.getExternalStorageDirectory().getCanonicalPath(), ""));
//				file = new File("/");
				dirInforObj.setCurrentPath(file.getCanonicalPath());
			} else {

//				file = new File(Environment.getExternalStorageDirectory().getCanonicalFile() + path);
//				dirInforObj.setCurrentPath(file.getCanonicalPath().replace(Environment.getExternalStorageDirectory().getCanonicalPath(), ""));
				file = new File(path);
				dirInforObj.setCurrentPath(file.getCanonicalPath());

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//遍历该目录下的文件列表
		if(file.isDirectory()){
			File[] list = file.listFiles();
			if(list!=null&&list.length>0){
				itemObjs = new ArrayList<WFM_FileItemObj>();
				WFM_FileItemObj itemObj = null;
				for(int i=0;i<list.length;i++){
					itemObj = new WFM_FileItemObj();
					itemObj.setFileName(list[i].getName());
					try {
//						itemObj.setUrl(list[i].getCanonicalPath().replace(Environment.getExternalStorageDirectory().getCanonicalPath(), ""));
						itemObj.setUrl(list[i].getCanonicalPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
					itemObj.setDirectory(list[i].isDirectory());
					 long len = list[i].length();
                     if ( len < 1024 )
                             itemObj.setFileLength(len+" bytes");
                     else if ( len < 1024 * 1024 )
                             itemObj.setFileLength(len/1024 + "." + (len%1024/10%100) + " KB");
                     else
                             itemObj.setFileLength(len/(1024*1024) + "." + len%(1024*1024)/10%100 + " MB");
                     itemObj.setLastTime((new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(list[i].lastModified()));
					itemObjs.add(itemObj);
					
				}
			}
		}
		dirInforObj.setFileList(itemObjs);
		
		return dirInforObj;
	}
	
	/**
	 * 删除文件
	 * @param path
	 * @return
	 */
	private WFM_Result delete(String path) {
		WFM_Result result = new WFM_Result();
		try {
//			String direct = Environment.getExternalStorageDirectory().getCanonicalPath()+path;
			String direct = path;
			LogUtil.debug(TAG, "delete", "direct = "+direct);
			File file = new File(direct);
			if(file.exists()){
				if(file.isDirectory()){
					if(WFM_FileUtil.deleteDirectory(direct)){
						result.setResultTag(WFM_Result.SUCCESS);
						result.setResultMsg("");
					}else{
						result.setResultTag(WFM_Result.FAIL);
						result.setResultMsg("");
					}
				}else{
					if(WFM_FileUtil.deleteFile(direct)){
						result.setResultTag(WFM_Result.SUCCESS);
						result.setResultMsg("");
					}else{
						result.setResultTag(WFM_Result.FAIL);
						result.setResultMsg("");
					}
				}
			}else{
				result.setResultTag(WFM_Result.FAIL);
				result.setResultMsg("no exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultTag(WFM_Result.FAIL);
			result.setResultMsg(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 重命名
	 */
	private WFM_Result rename(String path,String fileName){
		WFM_Result result = new WFM_Result();
		try {
//			String oldFilePath = Environment.getExternalStorageDirectory().getCanonicalPath()+path;
			String oldFilePath = path;
			LogUtil.debug(TAG, "rename", "direct = "+oldFilePath);
			File oldFile = new File(oldFilePath);
			if(oldFile.exists()){
				String newFilePath = oldFile.getParentFile().getCanonicalPath()+"/"+fileName;
				File newFile = new File(newFilePath);
				LogUtil.debug(TAG, "rename", "oldFile = "+oldFile.getCanonicalPath());
				LogUtil.debug(TAG, "rename", "newFile = "+newFile.getCanonicalPath());
				boolean b = oldFile.renameTo(newFile);
				if(b){
					result.setResultTag(WFM_Result.SUCCESS);
					result.setResultMsg("");
					result.setResultData(true);
				}else{
					result.setResultTag(WFM_Result.FAIL);
					result.setResultMsg("rename fail");
					result.setResultData(false);
				}
			}else{
				result.setResultTag(WFM_Result.FAIL);
				result.setResultMsg("no file");
				result.setResultData(false);
			}
		} catch (IOException e) {
			e.printStackTrace();
			result.setResultTag(WFM_Result.FAIL);
			result.setResultMsg(e.getMessage());
			result.setResultData(false);
		}
		
		return result;
	}
	
	//创建文件夹
	private WFM_Result createFolder(String dir,String folderName){
		WFM_Result result = new WFM_Result();
		try {
//			String direct = Environment.getExternalStorageDirectory().getCanonicalPath()+dir;
			String direct = dir;
			LogUtil.debug(TAG, "createFolder", "direct = "+direct);
			File prentFile = new File(direct);
			if(prentFile.isDirectory()){
				String newDirect = direct+"/"+folderName;
				File file = new File(newDirect);
				if(!file.exists()){//文件夹不存在，可创建
					file.mkdirs();
					if(file.exists()){
						result.setResultTag(WFM_Result.SUCCESS);
						result.setResultMsg("");
						result.setResultData(true);
					}else{
						result.setResultTag(WFM_Result.FAIL);
						result.setResultMsg("");
						result.setResultData(false);
					}
				}else{//文件夹存在，不可创建重复目录
					result.setResultTag(WFM_Result.FAIL);
					result.setResultMsg("folder already exists");
					result.setResultData(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultTag(WFM_Result.FAIL);
			result.setResultMsg(e.getMessage());
			result.setResultData(false);
		}
		
		return result;
	}

	@Override
	public void stop() {
		LogUtil.info(TAG, "stop", "stop");
		super.stop();
	}

	@Override
	public Response serveFile(String uri, Properties header, File homeDir,
			boolean allowDirectoryListing) {

        Response res = null;

        // Make sure we won't die of an exception later
        if ( !homeDir.isDirectory())
                res = new Response( HTTP_INTERNALERROR, MIME_PLAINTEXT,
                        "INTERNAL ERRROR: serveFile(): given homeDir is not a directory." );

        if ( res == null )
        {
                // Remove URL arguments
                uri = uri.trim().replace( File.separatorChar, '/' );
                if ( uri.indexOf( '?' ) >= 0 )
                        uri = uri.substring(0, uri.indexOf( '?' ));

                // Prohibit getting out of current directory
                if ( uri.startsWith( ".." ) || uri.endsWith( ".." ) || uri.indexOf( "../" ) >= 0 )
                        res = new Response( HTTP_FORBIDDEN, MIME_PLAINTEXT,
                                "FORBIDDEN: Won't serve ../ for security reasons." );
        }

        File f = new File( homeDir, uri );
        if ( res == null && !f.exists())
                res = new Response( HTTP_NOTFOUND, MIME_PLAINTEXT,
                        "Error 404, file not found." );

        // List the directory, if necessary
        if ( res == null && f.isDirectory())
        {
                // Browsers get confused without '/' after the
                // directory, send a redirect.
                if ( !uri.endsWith( "/" ))
                {
                        uri += "/";
                        res = new Response( HTTP_REDIRECT, MIME_HTML,
                                "<html><body>Redirected: <a href=\"" + uri + "\">" +
                                uri + "</a></body></html>");
                        res.addHeader( "Location", uri );
                }

                if ( res == null )
                {
//                        // First try index.html and index.htm  "/data/data/com.example.nanohttpd_demo/files
//                        if ( new File( f, ServerApplication.http_root+"/index.htm" ).exists())
//                                f = new File( homeDir, uri + ServerApplication.http_root+"/index.htm" );
//                        else if ( new File( f, ServerApplication.http_root+"/index.html" ).exists())
//                                f = new File( homeDir, uri + ServerApplication.http_root+"/index.html" );
                        try {
							if ( new File( f, context.getFilesDir().getCanonicalPath()+"/webroot"+"/index.htm" ).exists())
							    f = new File( homeDir, uri + context.getFilesDir().getCanonicalPath()+"/webroot"+"/index.htm" );
               else if ( new File( f, context.getFilesDir().getCanonicalPath()+"/webroot"+"/index.html" ).exists())
							    f = new File( homeDir, uri + context.getFilesDir().getCanonicalPath()+"/webroot"+"/index.html" );
//                        	f = new File( "/data/data/com.example.nanohttpd_demo/files/index.html" );
							// No index file, list the directory if it is readable
							else if ( allowDirectoryListing && f.canRead() )
							{
							        String[] files = f.list();
							        String msg = "<html><body><h1>Directory " + uri + "</h1><br/>";

							        if ( uri.length() > 1 )
							        {
							                String u = uri.substring( 0, uri.length()-1 );
							                int slash = u.lastIndexOf( '/' );
							                if ( slash >= 0 && slash  < u.length())
							                        msg += "<b><a href=\"" + uri.substring(0, slash+1) + "\">..</a></b><br/>";
							        }

							        if (files!=null)
							        {
							                for ( int i=0; i<files.length; ++i )
							                {
							                        File curFile = new File( f, files[i] );
							                        boolean dir = curFile.isDirectory();
							                        if ( dir )
							                        {
							                                msg += "<b>";
							                                files[i] += "/";
							                        }

							                        msg += "<a href=\"" + encodeUri( uri + files[i] ) + "\">" +
							                                  files[i] + "</a>";

							                        // Show file size
							                        if ( curFile.isFile())
							                        {
							                                long len = curFile.length();
							                                msg += " &nbsp;<font size=2>(";
							                                if ( len < 1024 )
							                                        msg += len + " bytes";
							                                else if ( len < 1024 * 1024 )
							                                        msg += len/1024 + "." + (len%1024/10%100) + " KB";
							                                else
							                                        msg += len/(1024*1024) + "." + len%(1024*1024)/10%100 + " MB";

							                                msg += ")</font>";
							                        }
							                        msg += "<br/>";
							                        if ( dir ) msg += "</b>";
							                }
							        }
							        msg += "</body></html>";
							        res = new Response( HTTP_OK, MIME_HTML, msg );
							}
							else
							{
							        res = new Response( HTTP_FORBIDDEN, MIME_PLAINTEXT,
							                "FORBIDDEN: No directory listing." );
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                }
        }

        try
        {
                if ( res == null )
                {
                        // Get MIME type from file name extension, if possible
                        String mime = null;
                        int dot = f.getCanonicalPath().lastIndexOf( '.' );
                        if ( dot >= 0 )
                                mime = (String)theMimeTypes.get( f.getCanonicalPath().substring( dot + 1 ).toLowerCase());
                        if ( mime == null )
                                mime = MIME_DEFAULT_BINARY;

                        // Calculate etag
                        String etag = Integer.toHexString((f.getAbsolutePath() + f.lastModified() + "" + f.length()).hashCode());

                        // Support (simple) skipping:
                        long startFrom = 0;
                        long endAt = -1;
                        String range = header.getProperty( "range" );
                        if ( range != null )
                        {
                                if ( range.startsWith( "bytes=" ))
                                {
                                        range = range.substring( "bytes=".length());
                                        int minus = range.indexOf( '-' );
                                        try {
                                                if ( minus > 0 )
                                                {
                                                        startFrom = Long.parseLong( range.substring( 0, minus ));
                                                        endAt = Long.parseLong( range.substring( minus+1 ));
                                                }
                                        }
                                        catch ( NumberFormatException nfe ) {}
                                }
                        }

                        // Change return code and add Content-Range header when skipping is requested
                        long fileLen = f.length();
                        if (range != null && startFrom >= 0)
                        {
                                if ( startFrom >= fileLen)
                                {
                                        res = new Response( HTTP_RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, "" );
                                        res.addHeader( "Content-Range", "bytes 0-0/" + fileLen);
                                        if ( mime.startsWith( "application/" ))
                                          res.addHeader( "Content-Disposition", "attachment; filename=\"" + f.getName() + "\"");
                                        res.addHeader( "ETag", etag);
                                }
                                else
                                {
                                        if ( endAt < 0 )
                                                endAt = fileLen-1;
                                        long newLen = endAt - startFrom + 1;
                                        if ( newLen < 0 ) newLen = 0;

                                        final long dataLen = newLen;
                                        FileInputStream fis = new FileInputStream( f ) {
                                                public int available() throws IOException { return (int)dataLen; }
                                        };
                                        fis.skip( startFrom );

                                        res = new Response( HTTP_PARTIALCONTENT, mime, fis );
                                        res.addHeader( "Content-Length", "" + dataLen);
                                        res.addHeader( "Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                                        if ( mime.startsWith( "application/" ))
                                                res.addHeader( "Content-Disposition", "attachment; filename=\"" + f.getName() + "\"");
                                        res.addHeader( "ETag", etag);
                                }
                        }
                        else
                        {
                                if (etag.equals(header.getProperty("if-none-match")))
                                        res = new Response( HTTP_NOTMODIFIED, mime, "");
                                else
                                {
                                	if(f.getName().endsWith(".htm")||f.getName().endsWith(".html")){
                                		res = new Response( HTTP_OK, mime, replaceTag(f));
                                		res.addHeader( "Content-Length", "" + res.data.available());
                                	}else{
                                		res = new Response( HTTP_OK, mime, new FileInputStream( f ));
                                		res.addHeader( "Content-Length", "" + fileLen);
                                	}
                                	
                                        
                                        if ( mime.startsWith( "application/" ))
                                                res.addHeader( "Content-Disposition", "attachment; filename=\"" + f.getName() + "\"");
                                        res.addHeader( "ETag", etag);
                                }
                        }
                }
        }
        catch( IOException ioe )
        {
                res = new Response( HTTP_FORBIDDEN, MIME_PLAINTEXT, "FORBIDDEN: Reading file failed." );
        }

        res.addHeader( "Accept-Ranges", "bytes"); // Announce that the file server accepts partial content requestes
        return res;

	}
	
	/**
	 * 替换html里的path标签，用于定位html的实际路径,本方法只能用于
	 * html页面，对js或其他文件进行替换，会导致文件失效
	 * @param f
	 * @return
	 */
	private String replaceTag(File f){
		try {
			BufferedReader br=new BufferedReader(new FileReader(f));
			StringBuffer sb = new StringBuffer();
			String r=br.readLine();
			while(r!=null){
				sb.append(r).append("\r\n");
				r=br.readLine();
			}
			return sb.toString().replace("{#path}", context.getFilesDir().getCanonicalPath()+"/webroot");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
		
	}


}
