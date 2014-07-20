package cn.hneao.notebook.bean;

import java.io.Serializable;

public class Base implements Serializable {
	public final static String UTF8 = "UTF-8";
	public final static String NODE_ROOT = "hneao";

	protected Notice notice;

	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notice) {
		this.notice = notice;
	}
}
