/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cstong.android.api;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLHandshakeException;

import net.cstong.android.util.Constant;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ab.global.AbAppException;
import com.ab.global.AbConstant;
import com.ab.http.AbBinaryHttpResponseListener;
import com.ab.http.AbFileHttpResponseListener;
import com.ab.http.AbGzipDecompressingEntity;
import com.ab.http.AbHttpResponseListener;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.http.EasySSLProtocolSocketFactory;
import com.ab.task.AbThreadFactory;
import com.ab.util.AbAppUtil;
import com.ab.util.AbFileUtil;

// TODO: Auto-generated Javadoc
/**
 * © 2012 amsoft.cn
 * 名称：MyHttpClient.java 
 * 描述：Http客户端
 *
 * @author 还如一梦中
 * @version v1.0
 * @date：2013-11-13 上午9:00:52
 */
public class MyHttpClient {
	/** 日志标记. */
	private static final String TAG = "MyHttpClient";

	/** 上下文. */
	private Context mContext;

	/** 线程执行器. */
	public static Executor mExecutorService = null;

	/** 编码. */
	private String encode = HTTP.UTF_8;

	/** 用户代理. */
	private String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 BIDUBrowser/6.x Safari/537.31";

	private static final String HTTP_GET = "GET";
	private static final String HTTP_POST = "POST";
	private static final String USER_AGENT = "User-Agent";
	private static final String ACCEPT_ENCODING = "Accept-Encoding";
	private static final String COOKIE = "Cookie";
	private static final String SET_COOKIE = "Set-Cookie";
	/** 最大连接数. */
	private static final int DEFAULT_MAX_CONNECTIONS = 10;

	/** 超时时间. */
	public static final int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;

	/** 重试. */
	private static final int DEFAULT_MAX_RETRIES = 5;

	/** 缓冲大小. */
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;

	/** 缓冲大小. */
	private static final int BUFFER_SIZE = 4096;

	/** 成功. */
	protected static final int SUCCESS_MESSAGE = 0;

	/** 失败. */
	protected static final int FAILURE_MESSAGE = 1;

	/** 开始. */
	protected static final int START_MESSAGE = 2;

	/** 完成. */
	protected static final int FINISH_MESSAGE = 3;

	/** 进行中. */
	protected static final int PROGRESS_MESSAGE = 4;

	/** 重试. */
	protected static final int RETRY_MESSAGE = 5;

	/** 超时时间. */
	private int timeout = DEFAULT_SOCKET_TIMEOUT;

	/** debug true表示是局域网. */
	private boolean debug = false;

	/** 通用证书. 如果要求HTTPS连接，则使用SSL打开连接*/
	private boolean isOpenEasySSL = true;

	/** HTTP Client*/
	private DefaultHttpClient mHttpClient = null;

	/** HTTP 上下文*/
	private HttpContext mHttpContext = null;

	protected HashMap<String, String> cookieContainer = new HashMap<String, String>();

	/**
	 * 使用ResponseHandler接口处理响应,支持重定向
	 */
	private class RedirectionResponseHandler implements ResponseHandler<String> {

		private AbHttpResponseListener mResponseListener = null;
		private String mUrl = null;

		public RedirectionResponseHandler(final String url,
				final AbHttpResponseListener responseListener) {
			super();
			mUrl = url;
			mResponseListener = responseListener;
		}

		@Override
		public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
			HttpUriRequest request = (HttpUriRequest) mHttpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
			//请求成功  
			int statusCode = response.getStatusLine().getStatusCode();
			//200直接返回结果
			if (statusCode == HttpStatus.SC_OK) {
				saveCookies(response);
				HttpEntity entity = response.getEntity();
				String responseBody = null;
				// 不打算读取response body   
				// 调用request的abort方法  
				// request.abort();  

				if (entity != null) {
					if (statusCode == HttpStatus.SC_OK) {

						if (mResponseListener instanceof AbStringHttpResponseListener) {
							//entity中的内容只能读取一次,否则Content has been consumed
							//如果压缩要解压
							Header header = entity.getContentEncoding();
							if (header != null) {
								String contentEncoding = header.getValue();
								if (contentEncoding != null) {
									if (contentEncoding.contains("gzip")) {
										entity = new AbGzipDecompressingEntity(entity);
									}
								}
							}
							String charset = EntityUtils.getContentCharSet(entity) == null ? encode : EntityUtils.getContentCharSet(entity);
							responseBody = new String(EntityUtils.toByteArray(entity), charset);

							((AbStringHttpResponseListener) mResponseListener).sendSuccessMessage(statusCode, responseBody);
						} else if (mResponseListener instanceof AbBinaryHttpResponseListener) {
							responseBody = "这是二进制数据";
							readResponseData(entity, ((AbBinaryHttpResponseListener) mResponseListener));
						} else if (mResponseListener instanceof AbFileHttpResponseListener) {
							//获取文件名
							String fileName = AbFileUtil.getCacheFileNameFromUrl(mUrl, response);
							writeResponseData(entity, fileName, ((AbFileHttpResponseListener) mResponseListener));
						}
					} else {
						String charset = EntityUtils.getContentCharSet(entity) == null ? encode : EntityUtils.getContentCharSet(entity);
						responseBody = new String(EntityUtils.toByteArray(entity), charset);
						mResponseListener.sendFailureMessage(statusCode, responseBody, new AbAppException(AbConstant.UNKNOWNHOSTEXCEPTION));

					}
					//资源释放!!!
					try {
						entity.consumeContent();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return responseBody;
				}

			}
			//301 302进行重定向请求
			else if ((statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
					|| (statusCode == HttpStatus.SC_MOVED_PERMANENTLY)) {
				// 从头中取出转向的地址
				Header locationHeader = response.getLastHeader("location");
				String location = locationHeader.getValue();
				if (request.getMethod().equalsIgnoreCase(HTTP_POST)) {
					doPost(location, null, mResponseListener);
				}
				else if (request.getMethod().equalsIgnoreCase(HTTP_GET)) {
					doGet(location, null, mResponseListener);
				}
			}
			return null;
		}
	}

	/**
	 * 自动重试处理
	 */
	private HttpRequestRetryHandler mRequestRetryHandler = new HttpRequestRetryHandler() {

		// 自定义的恢复策略
		@Override
		public boolean retryRequest(final IOException exception, final int executionCount,
				final HttpContext context) {
			// 设置恢复策略，在发生异常时候将自动重试3次
			if (executionCount >= 3) {
				// 如果超过最大重试次数，那么就不要继续了
				Log.d(TAG, "超过最大重试次数，不重试");
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				// 如果服务器丢掉了连接，那么就重试
				Log.d(TAG, "服务器丢掉了连接，重试");
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				// SSL握手异常，不重试
				Log.d(TAG, "ssl 异常 不重试");
				return false;
			}
			HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// 如果请求被认为是幂等的，那么就重试
				Log.d(TAG, "请求被认为是幂等的，重试");
				return true;
			}
			if (exception != null) {
				return true;
			}
			return false;
		}
	};

	/**
	 * 初始化.
	 *
	 * @param context the context
	 */
	public MyHttpClient(final Context context) {
		mContext = context;
		mExecutorService = AbThreadFactory.getExecutorService();
		mHttpContext = new BasicHttpContext();
		initCookie();
	}

	/**
	 * 描述：无参数的get请求.
	 *
	 * @param url the url
	 * @param responseListener the response listener
	 */
	public void get(final String url, final AbHttpResponseListener responseListener) {
		get(url, null, responseListener);
	}

	/**
	 * 描述：带参数的get请求.
	 *
	 * @param url the url
	 * @param params the params
	 * @param responseListener the response listener
	 */
	public void get(final String url, final AbRequestParams params, final AbHttpResponseListener responseListener) {

		responseListener.setHandler(new ResponderHandler(responseListener));
		mExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					doGet(url, params, responseListener);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 描述：无参数的post请求.
	 *
	 * @param url the url
	 * @param responseListener the response listener
	 */
	public void post(final String url, final AbHttpResponseListener responseListener) {
		post(url, null, responseListener);
	}

	/**
	 * 描述：带参数的post请求.
	 *
	 * @param url the url
	 * @param params the params
	 * @param responseListener the response listener
	 */
	public void post(final String url, final AbRequestParams params, final AbHttpResponseListener responseListener) {
		responseListener.setHandler(new ResponderHandler(responseListener));
		mExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					doPost(url, params, responseListener);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 描述：执行get请求.
	 *
	 * @param url the url
	 * @param params the params
	 * @param responseListener the response listener
	 */
	private void doGet(String url, final AbRequestParams params, final AbHttpResponseListener responseListener) {
		try {

			responseListener.sendStartMessage();

			if (!debug && !AbAppUtil.isNetworkAvailable(mContext)) {
				responseListener.sendFailureMessage(AbConstant.CONNECT_FAILURE_CODE, AbConstant.CONNECTEXCEPTION, new AbAppException(AbConstant.CONNECTEXCEPTION));
				return;
			}

			//HttpGet连接对象  
			if (params != null) {
				url += params.getParamString();
			}
			HttpGet httpRequest = new HttpGet(url);
			httpRequest.addHeader(USER_AGENT, userAgent);
			//压缩
			httpRequest.addHeader(ACCEPT_ENCODING, "gzip");
			if (Constant.myApp.mUser.cookie.length() > 0) {
				httpRequest.addHeader(COOKIE, Constant.myApp.mUser.cookie);
			}
			//取得默认的HttpClient
			HttpClient httpClient = getHttpClient();
			//取得HttpResponse
			String response = httpClient.execute(httpRequest, new RedirectionResponseHandler(url, responseListener), mHttpContext);
			Log.i(TAG, "[HTTP Request]：" + url + ",result：" + response);
		} catch (Exception e) {
			e.printStackTrace();
			//发送失败消息
			responseListener.sendFailureMessage(AbConstant.UNTREATED_CODE, e.getMessage(), new AbAppException(e));
		} finally {
			responseListener.sendFinishMessage();
		}
	}

	/**
	 * 描述：执行post请求.
	 *
	 * @param url the url
	 * @param params the params
	 * @param responseListener the response listener
	 */
	public void doPost(final String url, final AbRequestParams params, final AbHttpResponseListener responseListener) {
		try {
			responseListener.sendStartMessage();

			if (!debug && !AbAppUtil.isNetworkAvailable(mContext)) {
				responseListener.sendFailureMessage(AbConstant.CONNECT_FAILURE_CODE, AbConstant.CONNECTEXCEPTION, new AbAppException(AbConstant.CONNECTEXCEPTION));
				return;
			}

			//HttpPost连接对象  
			HttpPost httpRequest = new HttpPost(url);
			httpRequest.addHeader(USER_AGENT, userAgent);
			//压缩
			httpRequest.addHeader(ACCEPT_ENCODING, "gzip");
			if (Constant.myApp.mUser.cookie.length() > 0) {
				httpRequest.addHeader(COOKIE, Constant.myApp.mUser.cookie);
			}
			if (params != null) {
				//使用NameValuePair来保存要传递的Post参数设置字符集 
				HttpEntity httpentity = params.getEntity(responseListener);
				//请求httpRequest  
				httpRequest.setEntity(httpentity);
			}

			//取得默认的HttpClient
			HttpClient httpClient = getHttpClient();
			//取得HttpResponse

			String response = httpClient.execute(httpRequest, new RedirectionResponseHandler(url, responseListener), mHttpContext);
			Log.i(TAG, "request：" + url + ",result：" + response);

		} catch (Exception e) {
			e.printStackTrace();
			//发送失败消息
			responseListener.sendFailureMessage(AbConstant.UNTREATED_CODE, e.getMessage(), new AbAppException(e));
		} finally {
			responseListener.sendFinishMessage();
		}
	}

	/**
	 * 描述：写入文件并回调进度.
	 *
	 * @param entity the entity
	 * @param name the name
	 * @param responseListener the response listener
	 */
	public void writeResponseData(final HttpEntity entity, final String name, final AbFileHttpResponseListener responseListener) {

		if (entity == null) {
			return;
		}

		if (responseListener.getFile() == null) {
			//创建缓存文件
			responseListener.setFile(name);
		}

		InputStream inStream = null;
		FileOutputStream outStream = null;
		try {
			inStream = entity.getContent();
			long contentLength = entity.getContentLength();
			outStream = new FileOutputStream(responseListener.getFile());
			if (inStream != null) {

				byte[] tmp = new byte[BUFFER_SIZE];
				int l, count = 0;
				while (((l = inStream.read(tmp)) != -1) && !Thread.currentThread().isInterrupted()) {
					count += l;
					outStream.write(tmp, 0, l);
					responseListener.sendProgressMessage(count, (int) contentLength);
				}
			}
			responseListener.sendSuccessMessage(200);
		} catch (Exception e) {
			e.printStackTrace();
			//发送失败消息
			responseListener.sendFailureMessage(AbConstant.RESPONSE_TIMEOUT_CODE, AbConstant.SOCKETTIMEOUTEXCEPTION, e);
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.flush();
					outStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 描述：转换为二进制并回调进度.
	 *
	 * @param entity the entity
	 * @param responseListener the response listener
	 */
	public void readResponseData(final HttpEntity entity, final AbBinaryHttpResponseListener responseListener) {

		if (entity == null) {
			return;
		}

		InputStream inStream = null;
		ByteArrayOutputStream outSteam = null;

		try {
			inStream = entity.getContent();
			outSteam = new ByteArrayOutputStream();
			long contentLength = entity.getContentLength();
			if (inStream != null) {
				int l, count = 0;
				byte[] tmp = new byte[BUFFER_SIZE];
				while ((l = inStream.read(tmp)) != -1) {
					count += l;
					outSteam.write(tmp, 0, l);
					responseListener.sendProgressMessage(count, (int) contentLength);

				}
			}
			responseListener.sendSuccessMessage(HttpStatus.SC_OK, outSteam.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			//发送失败消息
			responseListener.sendFailureMessage(AbConstant.RESPONSE_TIMEOUT_CODE, AbConstant.SOCKETTIMEOUTEXCEPTION, e);
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
				if (outSteam != null) {
					outSteam.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 描述：设置连接超时时间.
	 *
	 * @param timeout 毫秒
	 */
	public void setTimeout(final int timeout) {
		this.timeout = timeout;
	}

	/**
	 * 描述：调试模式.
	 *
	 * @return true, if is debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * 描述：是否为调试模式.
	 *
	 * @param debug the new debug
	 */
	public void setDebug(final boolean debug) {
		this.debug = debug;
	}

	/**
	 * © 2012 amsoft.cn
	 * 名称：ResponderHandler.java 
	 * 描述：请求返回
	 *
	 * @author 还如一梦中
	 * @version v1.0
	 * @date：2013-11-13 下午3:22:30
	 */
	private static class ResponderHandler extends Handler {

		/** 响应数据. */
		private Object[] response;

		/** 响应消息监听. */
		private AbHttpResponseListener responseListener;

		/**
		 * 响应消息处理.
		 *
		 * @param responseListener the response listener
		 */
		public ResponderHandler(final AbHttpResponseListener responseListener) {
			this.responseListener = responseListener;
		}

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(final Message msg) {

			switch (msg.what) {

			case SUCCESS_MESSAGE:
				response = (Object[]) msg.obj;

				if (response != null) {
					if (responseListener instanceof AbStringHttpResponseListener) {
						if (response.length >= 2) {
							((AbStringHttpResponseListener) responseListener).onSuccess((Integer) response[0], (String) response[1]);
						} else {
							Log.e(TAG, "SUCCESS_MESSAGE " + AbConstant.MISSINGPARAMETERS);
						}

					} else if (responseListener instanceof AbBinaryHttpResponseListener) {
						if (response.length >= 2) {
							((AbBinaryHttpResponseListener) responseListener).onSuccess((Integer) response[0], (byte[]) response[1]);
						} else {
							Log.e(TAG, "SUCCESS_MESSAGE " + AbConstant.MISSINGPARAMETERS);
						}
					} else if (responseListener instanceof AbFileHttpResponseListener) {

						if (response.length >= 1) {
							AbFileHttpResponseListener mAbFileHttpResponseListener = ((AbFileHttpResponseListener) responseListener);
							mAbFileHttpResponseListener.onSuccess((Integer) response[0], mAbFileHttpResponseListener.getFile());
						} else {
							Log.e(TAG, "SUCCESS_MESSAGE " + AbConstant.MISSINGPARAMETERS);
						}

					}
				}
				break;
			case FAILURE_MESSAGE:
				response = (Object[]) msg.obj;
				if ((response != null) && (response.length >= 3)) {
					//异常转换为可提示的
					AbAppException exception = new AbAppException((Exception) response[2]);
					responseListener.onFailure((Integer) response[0], (String) response[1], exception);
				} else {
					Log.e(TAG, "FAILURE_MESSAGE " + AbConstant.MISSINGPARAMETERS);
				}
				break;
			case START_MESSAGE:
				responseListener.onStart();
				break;
			case FINISH_MESSAGE:
				responseListener.onFinish();
				break;
			case PROGRESS_MESSAGE:
				response = (Object[]) msg.obj;
				if ((response != null) && (response.length >= 2)) {
					responseListener.onProgress((Integer) response[0], (Integer) response[1]);
				} else {
					Log.e(TAG, "PROGRESS_MESSAGE " + AbConstant.MISSINGPARAMETERS);
				}
				break;
			case RETRY_MESSAGE:
				responseListener.onRetry();
				break;
			default:
				break;
			}
		}

	}

	/**
	 * HTTP参数配置
	 * @return
	 */
	public BasicHttpParams getHttpParams() {

		BasicHttpParams httpParams = new BasicHttpParams();

		// 设置每个路由最大连接数
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(30);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
		HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
		// 从连接池中取连接的超时时间，设置为1秒
		ConnManagerParams.setTimeout(httpParams, timeout);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(DEFAULT_MAX_CONNECTIONS));
		ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);
		// 读响应数据的超时时间
		HttpConnectionParams.setSoTimeout(httpParams, timeout);
		HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(httpParams, userAgent);
		//默认参数
		HttpClientParams.setRedirecting(httpParams, false);
		HttpClientParams.setCookiePolicy(httpParams, CookiePolicy.BROWSER_COMPATIBILITY);
		httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
		return httpParams;

	}

	/**
	 * 获取HttpClient，自签名的证书，如果想做签名参考AuthSSLProtocolSocketFactory类
	 * @return
	 */
	public HttpClient getHttpClient() {

		if (mHttpClient != null) {
			return mHttpClient;
		} else {
			return createHttpClient();
		}
	}

	/**
	 * 获取HttpClient，自签名的证书，如果想做签名参考AuthSSLProtocolSocketFactory类
	 * @param httpParams
	 * @return
	 */
	public HttpClient createHttpClient() {
		BasicHttpParams httpParams = getHttpParams();
		if (isOpenEasySSL) {
			// 支持https的   SSL自签名的实现类
			EasySSLProtocolSocketFactory easySSLProtocolSocketFactory = new EasySSLProtocolSocketFactory();
			SchemeRegistry supportedSchemes = new SchemeRegistry();
			SocketFactory socketFactory = PlainSocketFactory.getSocketFactory();
			supportedSchemes.register(new Scheme("http", socketFactory, 80));
			supportedSchemes.register(new Scheme("https", easySSLProtocolSocketFactory, 443));

			ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(
					httpParams, supportedSchemes);
			//取得HttpClient
			mHttpClient = new DefaultHttpClient(connectionManager, httpParams);
			//自动重试
			mHttpClient.setHttpRequestRetryHandler(mRequestRetryHandler);
		} else {
			//线程安全的HttpClient
			mHttpClient = new DefaultHttpClient(httpParams);
			//自动重试
			mHttpClient.setHttpRequestRetryHandler(mRequestRetryHandler);
		}

		return mHttpClient;
	}

	/**
	 * 是否打开ssl 自签名
	 */
	public boolean isOpenEasySSL() {
		return isOpenEasySSL;
	}

	/**
	 * 打开ssl 自签名
	 * @param isOpenEasySSL
	 */
	public void setOpenEasySSL(final boolean isOpenEasySSL) {
		this.isOpenEasySSL = isOpenEasySSL;
	}

	/**
	 * 获取用户代理
	 * @return
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * 设置用户代理
	 * @param userAgent
	 */
	public void setUserAgent(final String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * 获取编码
	 * @return
	 */
	public String getEncode() {
		return encode;
	}

	/**
	 * 设置编码
	 * @param encode
	 */
	public void setEncode(final String encode) {
		this.encode = encode;
	}

	/**
	 * 关闭HttpClient
	 */
	public void shutdown() {
		if ((mHttpClient != null) && (mHttpClient.getConnectionManager() != null)) {
			mHttpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * 保存Cookie
	 * @param resp
	 */
	protected void saveCookies(final HttpResponse httpResponse) {
		Header[] headers = httpResponse.getHeaders(SET_COOKIE);
		if (headers == null) {
			return;
		}

		for (Header header : headers) {
			String cookie = header.getValue();
			String[] cookievalues = cookie.split(";");
			for (String cookievalue : cookievalues) {
				Log.d(TAG, "cookievalue:" + cookievalue);
				String[] keyPair = cookievalue.split("=");
				String key = keyPair[0].trim();
				String value = keyPair.length > 1 ? keyPair[1].trim() : "";
				if (key.equals("EgG_winduser") || key.equals("EgG_visitor") || key.equals("EgG_lastvisit")) {
					cookieContainer.put(key, value);
				}
			}
		}
		if (cookieContainer.containsKey(Constant.KEY_USER_COOKIE)) {
			Constant.myApp.mUser.cookie = formatCookie();
		}
	}

	protected void initCookie() {
		if (Constant.myApp.mUser.cookie.length() > 0) {
			cookieContainer.clear();
			String[] cookieValues = Constant.myApp.mUser.cookie.split(";");
			for (String cookieValue : cookieValues) {
				String[] keyPair = cookieValue.split("=");
				String key = keyPair[0].trim();
				String value = keyPair.length > 1 ? keyPair[1].trim() : "";
				cookieContainer.put(key, value);
			}
		}
	}

	protected String formatCookie() {
		StringBuffer sb = new StringBuffer();
		Iterator<String> iter = cookieContainer.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String val = cookieContainer.get(key);
			sb.append(key);
			sb.append("=");
			sb.append(val);
			sb.append(";");
		}
		return sb.toString();
	}
}
