package cn.hneao.notebook.bean;

import java.io.Serializable;

public class URLs implements Serializable {
	public final static String HOST = "172.18.27.48";
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	public final static String APP = "NoteBook";

	private final static String URL_SPLITTER = "/";
	private final static String URL_UNDERLINE = "_";

	private final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER + APP
			+ URL_SPLITTER;
	// �û���¼
	public final static String LOGIN_VALIDATE_HTTP = URL_API_HOST + "Main.aspx";
	// ������
	public final static String UPDATE_VERSION = URL_API_HOST
			+ "MobileAppVersion.xml";
	/**
	 * ��ӿ���
	 */
	public final static String URL_BASE = URL_API_HOST + "Main.aspx";
}
