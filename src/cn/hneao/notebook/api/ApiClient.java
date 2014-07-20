package cn.hneao.notebook.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import cn.hneao.notebook.AppContext;
import cn.hneao.notebook.bean.Result;
import cn.hneao.notebook.bean.StudentInfo;
import cn.hneao.notebook.bean.StudentInfoList;
import cn.hneao.notebook.bean.URLs;
import cn.hneao.notebook.bean.Update;
import cn.hneao.notebook.bean.User;
import cn.hneao.notebook.AppException;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * ���ڷ�����������
 * 
 * @author Administrator
 * 
 */
public class ApiClient {
	private final static String TAG = "myTest";
	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";

	private final static int TIMEOUT_CONNECTION = 20000;// ���ӳ�ʱʱ��
	private final static int TIMEOUT_SOCKET = 20000;// socket��ʱʱ��
	private final static int RETRY_TIME = 3;// ���Դ���

	private static String appCookie;
	private static String appUserAgent;

	public static void cleanCookie() {
		appCookie = "";
	}

	/**
	 * �����û����л�ȡcookie��Ϣ
	 * 
	 * @param appContext
	 * @return
	 */
	private static String getCookie(AppContext appContext) {
		if (appCookie == null || appCookie == "") {
			appCookie = appContext.getProperty("cookie");
		}
		return appCookie;
	}

	/**
	 * ��ȡ�û��ֻ���Ϣ
	 * 
	 * @param appContext
	 * @return
	 */
	private static String getUserAgent(AppContext appContext) {
		// if(appUserAgent == null || appUserAgent == "") {
		// StringBuilder ua = new StringBuilder("hneao.cn");
		// ua.append('/'+appContext.getPackageInfo().versionName+'_'+appContext.getPackageInfo().versionCode);//App�汾
		// ua.append("/Android");//�ֻ�ϵͳƽ̨
		// ua.append("/"+android.os.Build.VERSION.RELEASE);//�ֻ�ϵͳ�汾
		// ua.append("/"+android.os.Build.MODEL); //�ֻ��ͺ�
		// ua.append("/"+appContext.getAppId());//�ͻ���Ψһ��ʶ
		// appUserAgent = ua.toString();
		// }
		appUserAgent = "hneao.cn/notebook/Android/1.0/meizu/011";
		return appUserAgent;
	}

	/**
	 * ��ȡһ��HttpClient����
	 * 
	 * @return
	 */
	private static HttpClient getHttpClient() {
		HttpClient httpClient = new HttpClient();
		// ���� HttpClient ���� Cookie,���������һ���Ĳ���
		httpClient.getParams().setCookiePolicy(
				CookiePolicy.BROWSER_COMPATIBILITY);
		// ���� Ĭ�ϵĳ�ʱ���Դ������
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		// ���� ���ӳ�ʱʱ��
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(TIMEOUT_CONNECTION);
		// ���� �����ݳ�ʱʱ��
		httpClient.getHttpConnectionManager().getParams()
				.setSoTimeout(TIMEOUT_SOCKET);
		// ���� �ַ���
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}

	/**
	 * ��ȡһ��get�������
	 * 
	 * @param url
	 * @param cookie
	 * @param userAgent
	 * @return
	 */
	private static GetMethod getHttpGet(String url, String cookie,
			String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// ���� ����ʱʱ��
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", URLs.HOST);
		httpGet.setRequestHeader("Connection", "Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}

	/**
	 * ��ȡһ��POST �������
	 * 
	 * @param url
	 * @param cookie
	 * @param userAgent
	 * @return
	 */
	private static PostMethod getHttpPost(String url, String cookie,
			String userAgent) {
		PostMethod httpPost = new PostMethod(url);
		// ���� ����ʱʱ��
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", URLs.HOST);
		httpPost.setRequestHeader("Connection", "Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);
		return httpPost;
	}

	/**
	 * ���URL
	 * 
	 * @param p_url
	 * @param params
	 * @return
	 */
	private static String _MakeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if (url.indexOf("?") < 0)
			url.append('?');

		for (String name : params.keySet()) {
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
			// ����URLEncoder����
			// url.append(URLEncoder.encode(String.valueOf(params.get(name)),
			// UTF_8));
		}

		return url.toString().replace("?&", "?");
	}

	/**
	 * get����URL
	 * 
	 * @param url
	 * @throws AppException
	 */
	private static InputStream http_get(AppContext appContext, String url)
			throws AppException {
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		GetMethod httpGet = null;

		String responseBody = "";
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, cookie, userAgent);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				responseBody = httpGet.getResponseBodyAsString();
				
				Log.i(TAG,"---responseBody--->"+responseBody);
				
				break;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);// ��1s����һ��
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// �����������쳣��������Э�鲻�Ի��߷��ص�����������
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// ���������쳣
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// �ͷ�����
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);

		// �˴������ÿ�η���GET����ʱִ�еĲ�����oschina��������֤�û��Ƿ��¼

		// if(responseBody.contains("result") &&
		// responseBody.contains("errorCode") &&
		// appContext.containsProperty("user.uid")){
		// try {
		// Result res = Result.parse(new
		// ByteArrayInputStream(responseBody.getBytes()));
		// if(res.getErrorCode() == 0){
		// appContext.Logout();
		// appContext.getUnLoginHandler().sendEmptyMessage(1);//�����û�δ��½�����
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		return new ByteArrayInputStream(responseBody.getBytes());
	}

	/**
	 * ����post����
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	private static InputStream _post(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException {
		// System.out.println("post_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		PostMethod httpPost = null;

		// post����������
		int length = (params == null ? 0 : params.size())
				+ (files == null ? 0 : files.size());// ���ݳ���
		Part[] parts = new Part[length];
		int i = 0;
		if (params != null)
			for (String name : params.keySet()) {
				parts[i++] = new StringPart(name, String.valueOf(params
						.get(name)), UTF_8);
				// System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
			}
		if (files != null)
			for (String file : files.keySet()) {
				try {
					parts[i++] = new FilePart(file, files.get(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				// System.out.println("post_key_file==> "+file);
			}

		String responseBody = "";
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpPost = getHttpPost(url, cookie, userAgent);
				httpPost.setRequestEntity(new MultipartRequestEntity(parts,
						httpPost.getParams()));
				int statusCode = httpClient.executeMethod(httpPost);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				} else if (statusCode == HttpStatus.SC_OK) {
					Cookie[] cookies = httpClient.getState().getCookies();
					String tmpcookies = "";
					for (Cookie ck : cookies) {
						tmpcookies += ck.toString() + ";";
					}
					// ����cookie
					if (appContext != null && tmpcookies != "") {
						appContext.setProperty("cookie", tmpcookies);
						appCookie = tmpcookies;
					}
				}
				responseBody = httpPost.getResponseBodyAsString();
				// System.out.println("XMLDATA=====>"+responseBody);
				break;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// �����������쳣��������Э�鲻�Ի��߷��ص�����������
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// ���������쳣
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// �ͷ�����
				httpPost.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);

		// responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
		// if(responseBody.contains("result") &&
		// responseBody.contains("errorCode") &&
		// appContext.containsProperty("user.uid")){
		// try {
		// Result res = Result.parse(new
		// ByteArrayInputStream(responseBody.getBytes()));
		// if(res.getErrorCode() == 0){
		// appContext.Logout();
		// appContext.getUnLoginHandler().sendEmptyMessage(1);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		return new ByteArrayInputStream(responseBody.getBytes());
	}

	/**
	 * post����URL
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 * @throws IOException
	 * @throws
	 */
	private static Result http_post(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException, IOException {
		return Result.parse(_post(appContext, url, params, files));
	}

	/**
	 * ��ȡ����ͼƬ
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				InputStream inStream = httpGet.getResponseBodyAsStream();
				bitmap = BitmapFactory.decodeStream(inStream);
				inStream.close();
				break;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// �����������쳣��������Э�鲻�Ի��߷��ص�����������
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// ���������쳣
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// �ͷ�����
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);
		return bitmap;
	}

	/**
	 * �û���¼
	 * 
	 * @param appContext
	 * @param userName
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public static User login(AppContext appContext, String userName, String pwd)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("action", "login");// ��¼����
		params.put("yhdm", userName);
		params.put("yhmm", pwd);
		params.put("keep_login", 1);
		String loginString = URLs.LOGIN_VALIDATE_HTTP;

		Log.i(TAG, "--loginString-->>" + loginString);
		try {
			return User.parse(_post(appContext, loginString, params, null));
		} catch (Exception e) {
			if (e instanceof AppException) {
				throw (AppException) e;
			}
			throw AppException.network(e);
		}
	}

	/**
	 * ������
	 * 
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static Update checkVersion(AppContext appContext)
			throws AppException {
		try {
			return Update.parse(http_get(appContext, URLs.UPDATE_VERSION));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * ��ӿ���
	 * 
	 * @param appContext
	 * @param info
	 * @return
	 * @throws AppException
	 */
	public static Result addStudent(AppContext appContext, StudentInfo info)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("action", "addStudent");
		params.put("yhdm", info.getYhdm());
		params.put("sfgz", info.getSfgz());
		params.put("lsh", info.getLsh());
		params.put("ksh", info.getKsh());
		params.put("dxlxdh", info.getDxlxdh());
		params.put("zdfsxx", info.getZdfsxx());
		params.put("bz", info.getBz());

		try {
			return http_post(appContext, URLs.URL_BASE, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
	/**
	 * ��ȡ������Ϣ�б�
	 * @param appContext
	 * @param catalog
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static StudentInfoList getInfoList(AppContext appContext,
			final int catalog, final int pageSize, final int pageIndex)
			throws AppException {
		final String yhdm = appContext.getYhdm();
		//final String yhdm="1002";
		String newUrl = _MakeURL(URLs.URL_BASE, new HashMap<String, Object>() {
			{
				put("action", "getStudentInfo");
				put("catalog", catalog);
				put("pageSize", pageSize);
				put("pageIndex", pageIndex);
				put("yhdm", yhdm);
			}
		});
		
		Log.i(TAG,"---newUrl-->"+newUrl);
		try {
			return StudentInfoList.Parse(http_get(appContext, newUrl));
		} catch (Exception e) {
			if (e instanceof AppException) {
				throw (AppException) e;
			}
			throw AppException.network(e);
		}
	}

}
