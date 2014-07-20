package cn.hneao.notebook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

import cn.hneao.notebook.AppConfig;
import cn.hneao.notebook.AppException;
import cn.hneao.notebook.api.ApiClient;
import cn.hneao.notebook.bean.Notice;
import cn.hneao.notebook.bean.Result;
import cn.hneao.notebook.bean.StudentInfo;
import cn.hneao.notebook.bean.StudentInfoList;
import cn.hneao.notebook.bean.User;
import cn.hneao.notebook.common.StringUtils;
import cn.hneao.notebook.common.UIHelper;
import cn.hneao.notebook.ui.LoginActivity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AppContext extends Application {

	private static final String TAG = "AppContext";

	// 网络类型
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	public static final int PAGE_SIZE = 20;// 默认分页大小
	private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

	private boolean login = false; // 登录状态
	private String yhdm = ""; // 登录的用户代码

	public String getYhdm() {
		return yhdm;
	}

	public void setYhdm(String yhdm) {
		this.yhdm = yhdm;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();

	private String saveImagePath;// 保存图片路径
	// 未登录或修改密码处理
	private Handler unLoginHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				UIHelper.ToastMessage(AppContext.this, R.string.msg_login_error);
				// 跳转到登录页面
				Intent intent = new Intent(AppContext.this, LoginActivity.class);
				startActivity(intent);
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		// 注册应用程序异常崩溃处理器
		Thread.setDefaultUncaughtExceptionHandler(AppException
				.getAppExceptionHandler());
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 设置保存图片的路径
		saveImagePath = getProperty(AppConfig.SAVE_IMAGE_PATH);
		if (StringUtils.isEmpty(saveImagePath)) {
			setProperty(AppConfig.SAVE_IMAGE_PATH,
					AppConfig.DEFAULT_SAVE_IMAGE_PATH);
			saveImagePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH;
		}
	}

	/**
	 * 检测当前系统声音是否为正常模式
	 * 
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}

	/**
	 * 应用程序是否发出提示音
	 * 
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/**
	 * 获取一个APP的唯一标识字符串
	 * 
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if (StringUtils.isEmpty(uniqueID)) {
			// 如果不存在则初始化一个,并存储在配置文件中
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

	/**
	 * 是否发出提示音
	 * 
	 * @return
	 */
	public boolean isVoice() {
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		// 默认是开启提示声音
		if (StringUtils.isEmpty(perf_voice))
			return true;
		else
			return StringUtils.toBool(perf_voice);
	}

	/**
	 * 设置是否发出提示音
	 * 
	 * @param b
	 */
	public void setConfigVoice(boolean b) {
		setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
	}

	/**
	 * 是否启动检查更新
	 * 
	 * @return
	 */
	public boolean isCheckUp() {
		String perf_checkup = getProperty(AppConfig.CONF_CHECKUP);
		// 默认是开启
		if (StringUtils.isEmpty(perf_checkup))
			return true;
		else
			return StringUtils.toBool(perf_checkup);
	}

	/**
	 * 设置启动检查更新
	 * 
	 * @param b
	 */
	public void setConfigCheckUp(boolean b) {
		setProperty(AppConfig.CONF_CHECKUP, String.valueOf(b));
	}

	/**
	 * 是否自动发送短信息
	 * 
	 * @return
	 */
	public boolean isAutoSendMessage() {
		String auto_send_message = getProperty(AppConfig.AUTO_SEND_MESSAGE);
		if (StringUtils.isEmpty(auto_send_message)) {
			// 默认不自动发送
			return false;
		} else {
			return StringUtils.toBool(auto_send_message);
		}
	}

	/**
	 * 设置是否自动发送短信息
	 */
	public void setAutoSendMessage(boolean auto_send_message) {
		setProperty(AppConfig.AUTO_SEND_MESSAGE,
				String.valueOf(auto_send_message));
	}

	/**
	 * 注销登录
	 */
	public void loginOut() {
		ApiClient.cleanCookie();
		this.cleanCookie();
		this.login = false;
		this.yhdm = "";
	}

	/**
	 * 返回用户未登录或修改密码处理Handler
	 * 
	 * @return
	 */
	public Handler getUnLoginHandler() {
		return this.unLoginHandler;
	}

	/**
	 * 保存用户登录信息 将用户登录信息保存在Properties对象中
	 * 
	 * @param user
	 */
	public void saveLoginInfo(final User user) {
		this.yhdm = user.getYhdm();
		this.login = true;// 设置为已登录状态
		setProperties(new Properties() {
			{
				setProperty("user.yhdm", user.getYhdm());
				setProperty("user.yhmc", user.getYhmc());
				setProperty("user.yhmm", user.getYhmm());
				setProperty("user.lxdh", user.getLxdh());
				setProperty("user.qyzt", user.getQyzt());
				setProperty("user.addTime", user.getAddTime());
				setProperty("user.isRememberMe",
						String.valueOf(user.isRememberMe()));
			}
		});
	}

	/**
	 * 清除配置文件中保存的Cookie信息
	 */
	public void cleanCookie() {
		removeProperty(AppConfig.CONF_COOKIE);
	}

	/**
	 * 清除登录信息
	 */
	public void cleanLoginInfo() {
		this.yhdm = "";
		this.login = false;
		removeProperty("user.yhdm", "user.yhmc", "user.yhmm", "user.lxdh",
				"user.qyzt", "user.addTime", "user.isRememberMe");
	}

	/**
	 * 获取登录用户信息
	 * 
	 * @return
	 */
	public User getLoginInfo() {
		User user = new User();
		user.setYhdm(getProperty("user.yhdm"));
		user.setYhmm(getProperty("user.yhmm"));
		user.setYhmc(getProperty("user.yhmc"));
		user.setLxdh(getProperty("user.lxdh"));
		user.setAddTime(getProperty("user.addTime"));
		user.setQyzt(getProperty("user.qyzt"));
		user.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
		return user;
	}

	/**
	 * 验证用户登录信息
	 * 
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public User loginVerfy(String account, String pwd) throws AppException {
		return ApiClient.login(this, account, pwd);
	}

	/**
	 * 添加考生
	 * 
	 * @param info
	 * @return
	 * @throws AppException
	 */
	public Result addStudent(StudentInfo info) throws AppException {
		return ApiClient.addStudent(this, info);
	}

	/**
	 * 获取考生信息列表
	 * 
	 * @param catalog
	 * @param pageIndex
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	public StudentInfoList getStudentInfoList(int catalog, int pageIndex,
			boolean isRefresh) throws AppException {
		StudentInfoList infoList = null;
		// 缓存键
		String key = "studentinfolist_" + catalog + "_" + pageIndex + "_"
				+ PAGE_SIZE;
		Log.i(TAG,"---isNetworkConnected--->"+String.valueOf(isNetworkConnected()));
		
		if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
			// 从服务端获取数据
			Log.i(TAG,"从服务端获取数据");
			try {
				infoList = ApiClient.getInfoList(this, catalog, PAGE_SIZE,
						pageIndex);
				
				Log.i(TAG, "---infoList.ksh---->"
						+ infoList.getStudentInfoList().get(0).getKsh());
				if (infoList != null && pageIndex == 0) {
					Notice notice = infoList.getNotice();
					infoList.setNotice(null);
					// 将对象保存在缓存中
					infoList.setCacheKey(key);
					saveObject(infoList, key);
					infoList.setNotice(notice);
				}
			} catch (AppException e) {
				infoList = (StudentInfoList) readObject(key);
				if (infoList == null) {
					// 缓存中不存在
					throw e;
				}
			}
		} else {
			Log.i(TAG,"从本机文件缓存中获取数据");
			infoList = (StudentInfoList) readObject(key);
			if (infoList == null) {
				infoList = new StudentInfoList();
			}
		}
		return infoList;
	}

	/*********** 缓存相关 **********/
	/**
	 * 判断缓存数据是否可读
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * 判断缓存文件是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists()) {
			exist = true;
		}
		return exist;
	}

	/**
	 * 判断缓存是否失效
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME) {
			failure = true;// 失效
		} else if (!data.exists()) {
			failure = true;// 不存在缓存被判断为失效
		}
		return failure;
	}

	/**
	 * 将对象保存到内存缓存中
	 * 
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}

	/**
	 * 从内存缓存中获取对象
	 * 
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key) {
		return memCacheRegion.get(key);
	}

	/**
	 * 保存磁盘缓存
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput("cache_" + key + ".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获取磁盘缓存数据
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try {
			fis = openFileInput("cache_" + key + ".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 从指定的缓存文件中读取对象 如果不存在指定的缓存文件返回null 如果反序列化失败则删除指定文件 返回null
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	/****
	 * 读写配置辅助方法，调用AppConfig中的方法， 操作SharedPreferences对象
	 *********/
	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(this).get(key);
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}
}
