package cn.hneao.notebook.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import cn.hneao.notebook.AppException;
import cn.hneao.notebook.common.StringUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * ��Ϣʵ��
 * 
 * @author Administrator
 * 
 */
public class Notice implements Serializable{
	public final static String UTF8 = "UTF-8";
	public final static String NODE_ROOT = "hneao";

	public final static int TYPE_NEWLQ = 1;

	/**
	 * ���µĿ�����¼ȡ
	 */
	private int newlqCount;

	public int getNewlqCount() {
		return newlqCount;
	}

	public void setNewlqCount(int newqlCount) {
		this.newlqCount = newqlCount;
	}

	/**
	 * ����XML
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
	public static Notice parse(InputStream inputStream) throws IOException,
			AppException {
		Notice notice = null;
		XmlPullParser xmlParser = Xml.newPullParser();
		try {
			xmlParser.setInput(inputStream, UTF8);
			int evtType = xmlParser.getEventType();
			while (evtType != XmlPullParser.END_DOCUMENT) {
				String tag = xmlParser.getName();
				switch (evtType) {
				case XmlPullParser.START_TAG:
					// ֪ͨ��Ϣ
					if (tag.equalsIgnoreCase("notice")) {
						notice = new Notice();
					} else if (notice != null) {
						if (tag.equalsIgnoreCase("newlqCount")) {
							notice.setNewlqCount(StringUtils.toInt(
									xmlParser.nextText(), 0));
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				//���xmlû�н������򵼺�����һ���ڵ�
			    evtType=xmlParser.next();
			}

		} catch (XmlPullParserException e) {
			throw AppException.xml(e);
		} finally {
			inputStream.close();
		}
		return notice;
	}
}
