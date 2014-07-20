package cn.hneao.notebook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import cn.hneao.notebook.common.StringUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

/**
 * 应用程序配置类：用于保存用户相关信息及设置
 * @author Administrator
 *
 */
@SuppressLint("NewApi")
public class AppConfig {
	private final static String APP_CONFIG = "config";
	
	/**
	 * 是否自动发送短信息
	 */
	public final static String AUTO_SEND_MESSAGE="auto_send_message";
	/**
	 * 是否开启提示声音
	 */
	public final static String CONF_VOICE = "perf_voice";
	/**
	 * 是否自动检查更新
	 */
	public final static String CONF_CHECKUP = "perf_checkup";
	
	
	public final static String TEMP_TWEET = "temp_tweet";
	public final static String TEMP_TWEET_IMAGE = "temp_tweet_image";
	public final static String TEMP_MESSAGE = "temp_message";
	public final static String TEMP_COMMENT = "temp_comment";
	public final static String TEMP_POST_TITLE = "temp_post_title";
	public final static String TEMP_POST_CATALOG = "temp_post_catalog";
	public final static String TEMP_POST_CONTENT = "temp_post_content";

	public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";
	public final static String CONF_COOKIE = "cookie";
	public final static String CONF_EXPIRESIN = "expiresIn";
	public final static String CONF_LOAD_IMAGE = "perf_loadimage";
	public final static String CONF_SCROLL = "perf_scroll";
	public final static String CONF_HTTPS_LOGIN = "perf_httpslogin";
	
	

	public final static String SAVE_IMAGE_PATH = "save_image_path";
	@SuppressLint("NewApi")
	public final static String DEFAULT_SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory()+ File.separator+ "OSChina"+ File.separator;
			
	private Context mContext;
	private static AppConfig appConfig;

	//返回一个用Context初始化后的自身的实例
	public static AppConfig getAppConfig(Context context) {
		if (appConfig == null) {
			appConfig = new AppConfig();
			appConfig.mContext = context;
		}
		return appConfig;
	}

	/**
	 * 获取Preference设置，相当于Android提供的一个公共对象，
	 * 一个应用的任何地方都可以访问与修改该对象的数据。
	 */
	public static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * 是否加载显示文章图片
	 * getSharedPreferences(context) 从 Preference中获取（配置）信息
	 */
	public static boolean isLoadImage(Context context) {
		return getSharedPreferences(context).getBoolean(CONF_LOAD_IMAGE, true);
	}

	public String getCookie() {
		return get(CONF_COOKIE);
	}
	//会话超时时间
	public void setExpiresIn(long expiresIn) {
		set(CONF_EXPIRESIN, String.valueOf(expiresIn));
	}

	public long getExpiresIn() {
		return StringUtils.toLong(get(CONF_EXPIRESIN));
	}

	/**
	 * 从Properties对象中获取指定key的value，
	 * 即获取特定的配置信息
	 * @param key
	 * @return
	 */
	public String get(String key) {
		Properties props = get();
		return (props != null) ? props.getProperty(key) : null;
	}
	/**
	 * Properties 为键值都为String类型的Map。Map<String,String>
	 * get方法的作用为从系统配置文件中读取配置信息存储到Properties对象中
	 * @return
	 */
	public Properties get() {
		FileInputStream fis = null;
		Properties props = new Properties();
		try {
			// 读取files目录下的config
			// fis = activity.openFileInput(APP_CONFIG);

			// 读取app_config目录下的config
			//getDir创建一个directory，这个directory只能够给当前应用访问到
			//并返回一个File类型对象，通过这个对象可以操作该directory（增、删、改、查）文件。
			//可以指定这个目的的属性mode
			//这里的directory为config目录
			File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
			fis = new FileInputStream(dirConf.getPath() + File.separator
					+ APP_CONFIG);
			//从文件中加载Properties对象的内容
			props.load(fis);
		} catch (Exception e) {
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return props;
	}

	/**
	 * 保存Properties对象到配置文件，相当于添加或者保存配置信息
	 * @param p
	 */
	private void setProps(Properties p) {
		FileOutputStream fos = null;
		try {
			// 把config建在files目录下
			// fos = activity.openFileOutput(APP_CONFIG, Context.MODE_PRIVATE);

			// 把config建在(自定义)app_config的目录下
			File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
			File conf = new File(dirConf, APP_CONFIG);
			fos = new FileOutputStream(conf);

			p.store(fos, null);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	public void set(Properties ps) {
		Properties props = get();
		props.putAll(ps);
		setProps(props);
	}
	/**
	 * 添加或修改配置信息
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		Properties props = get();
		props.setProperty(key, value);
		setProps(props);
	}
	/**
	 * 删除配置信息
	 * @param key
	 */
	public void remove(String... key) {
		Properties props = get();
		for (String k : key)
			props.remove(k);
		setProps(props);
	}
}
