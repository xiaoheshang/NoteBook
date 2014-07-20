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

	// ��������
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	public static final int PAGE_SIZE = 20;// Ĭ�Ϸ�ҳ��С
	private static final int CACHE_TIME = 60 * 60000;// ����ʧЧʱ��

	private boolean login = false; // ��¼״̬
	private String yhdm = ""; // ��¼���û�����

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

	private String saveImagePath;// ����ͼƬ·��
	// δ��¼���޸����봦��
	private Handler unLoginHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				UIHelper.ToastMessage(AppContext.this, R.string.msg_login_error);
				// ��ת����¼ҳ��
				Intent intent = new Intent(AppContext.this, LoginActivity.class);
				startActivity(intent);
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		// ע��Ӧ�ó����쳣����������
		Thread.setDefaultUncaughtExceptionHandler(AppException
				.getAppExceptionHandler());
		init();
	}

	/**
	 * ��ʼ��
	 */
	private void init() {
		// ���ñ���ͼƬ��·��
		saveImagePath = getProperty(AppConfig.SAVE_IMAGE_PATH);
		if (StringUtils.isEmpty(saveImagePath)) {
			setProperty(AppConfig.SAVE_IMAGE_PATH,
					AppConfig.DEFAULT_SAVE_IMAGE_PATH);
			saveImagePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH;
		}
	}

	/**
	 * ��⵱ǰϵͳ�����Ƿ�Ϊ����ģʽ
	 * 
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}

	/**
	 * Ӧ�ó����Ƿ񷢳���ʾ��
	 * 
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}

	/**
	 * ��������Ƿ����
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * ��ȡ��ǰ��������
	 * 
	 * @return 0��û������ 1��WIFI���� 2��WAP���� 3��NET����
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
	 * �жϵ�ǰ�汾�Ƿ����Ŀ��汾�ķ���
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * ��ȡApp��װ����Ϣ
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
	 * ��ȡһ��APP��Ψһ��ʶ�ַ���
	 * 
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if (StringUtils.isEmpty(uniqueID)) {
			// ������������ʼ��һ��,���洢�������ļ���
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

	/**
	 * �Ƿ񷢳���ʾ��
	 * 
	 * @return
	 */
	public boolean isVoice() {
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		// Ĭ���ǿ�����ʾ����
		if (StringUtils.isEmpty(perf_voice))
			return true;
		else
			return StringUtils.toBool(perf_voice);
	}

	/**
	 * �����Ƿ񷢳���ʾ��
	 * 
	 * @param b
	 */
	public void setConfigVoice(boolean b) {
		setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
	}

	/**
	 * �Ƿ�����������
	 * 
	 * @return
	 */
	public boolean isCheckUp() {
		String perf_checkup = getProperty(AppConfig.CONF_CHECKUP);
		// Ĭ���ǿ���
		if (StringUtils.isEmpty(perf_checkup))
			return true;
		else
			return StringUtils.toBool(perf_checkup);
	}

	/**
	 * ��������������
	 * 
	 * @param b
	 */
	public void setConfigCheckUp(boolean b) {
		setProperty(AppConfig.CONF_CHECKUP, String.valueOf(b));
	}

	/**
	 * �Ƿ��Զ����Ͷ���Ϣ
	 * 
	 * @return
	 */
	public boolean isAutoSendMessage() {
		String auto_send_message = getProperty(AppConfig.AUTO_SEND_MESSAGE);
		if (StringUtils.isEmpty(auto_send_message)) {
			// Ĭ�ϲ��Զ�����
			return false;
		} else {
			return StringUtils.toBool(auto_send_message);
		}
	}

	/**
	 * �����Ƿ��Զ����Ͷ���Ϣ
	 */
	public void setAutoSendMessage(boolean auto_send_message) {
		setProperty(AppConfig.AUTO_SEND_MESSAGE,
				String.valueOf(auto_send_message));
	}

	/**
	 * ע����¼
	 */
	public void loginOut() {
		ApiClient.cleanCookie();
		this.cleanCookie();
		this.login = false;
		this.yhdm = "";
	}

	/**
	 * �����û�δ��¼���޸����봦��Handler
	 * 
	 * @return
	 */
	public Handler getUnLoginHandler() {
		return this.unLoginHandler;
	}

	/**
	 * �����û���¼��Ϣ ���û���¼��Ϣ������Properties������
	 * 
	 * @param user
	 */
	public void saveLoginInfo(final User user) {
		this.yhdm = user.getYhdm();
		this.login = true;// ����Ϊ�ѵ�¼״̬
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
	 * ��������ļ��б����Cookie��Ϣ
	 */
	public void cleanCookie() {
		removeProperty(AppConfig.CONF_COOKIE);
	}

	/**
	 * �����¼��Ϣ
	 */
	public void cleanLoginInfo() {
		this.yhdm = "";
		this.login = false;
		removeProperty("user.yhdm", "user.yhmc", "user.yhmm", "user.lxdh",
				"user.qyzt", "user.addTime", "user.isRememberMe");
	}

	/**
	 * ��ȡ��¼�û���Ϣ
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
	 * ��֤�û���¼��Ϣ
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
	 * ��ӿ���
	 * 
	 * @param info
	 * @return
	 * @throws AppException
	 */
	public Result addStudent(StudentInfo info) throws AppException {
		return ApiClient.addStudent(this, info);
	}

	/**
	 * ��ȡ������Ϣ�б�
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
		// �����
		String key = "studentinfolist_" + catalog + "_" + pageIndex + "_"
				+ PAGE_SIZE;
		Log.i(TAG,"---isNetworkConnected--->"+String.valueOf(isNetworkConnected()));
		
		if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
			// �ӷ���˻�ȡ����
			Log.i(TAG,"�ӷ���˻�ȡ����");
			try {
				infoList = ApiClient.getInfoList(this, catalog, PAGE_SIZE,
						pageIndex);
				
				Log.i(TAG, "---infoList.ksh---->"
						+ infoList.getStudentInfoList().get(0).getKsh());
				if (infoList != null && pageIndex == 0) {
					Notice notice = infoList.getNotice();
					infoList.setNotice(null);
					// �����󱣴��ڻ�����
					infoList.setCacheKey(key);
					saveObject(infoList, key);
					infoList.setNotice(notice);
				}
			} catch (AppException e) {
				infoList = (StudentInfoList) readObject(key);
				if (infoList == null) {
					// �����в�����
					throw e;
				}
			}
		} else {
			Log.i(TAG,"�ӱ����ļ������л�ȡ����");
			infoList = (StudentInfoList) readObject(key);
			if (infoList == null) {
				infoList = new StudentInfoList();
			}
		}
		return infoList;
	}

	/*********** ������� **********/
	/**
	 * �жϻ��������Ƿ�ɶ�
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * �жϻ����ļ��Ƿ����
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
	 * �жϻ����Ƿ�ʧЧ
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME) {
			failure = true;// ʧЧ
		} else if (!data.exists()) {
			failure = true;// �����ڻ��汻�ж�ΪʧЧ
		}
		return failure;
	}

	/**
	 * �����󱣴浽�ڴ滺����
	 * 
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}

	/**
	 * ���ڴ滺���л�ȡ����
	 * 
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key) {
		return memCacheRegion.get(key);
	}

	/**
	 * ������̻���
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
	 * ��ȡ���̻�������
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
	 * �������
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
	 * ��ָ���Ļ����ļ��ж�ȡ���� ���������ָ���Ļ����ļ�����null ��������л�ʧ����ɾ��ָ���ļ� ����null
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
			// �����л�ʧ�� - ɾ�������ļ�
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
	 * ��д���ø�������������AppConfig�еķ����� ����SharedPreferences����
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
