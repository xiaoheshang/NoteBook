package cn.hneao.notebook.bean;

import java.io.IOException;
import java.io.InputStream;

import cn.hneao.notebook.AppException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import cn.hneao.notebook.common.StringUtils;
import android.util.Xml;

public class Result extends Base {
	private int errorCode;
	private String errorMessage;

	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean OK() {
		return errorCode == 1;
	}
	/**
	 * XML结果信息转换成Result对象
	 * @param stream
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
	public static Result parse(InputStream stream) throws IOException, AppException {
		Result result = null;
		XmlPullParser xmlParser = Xml.newPullParser();
		try {
			xmlParser.setInput(stream, UTF8);
			int evtType = xmlParser.getEventType();
			while (evtType != XmlPullParser.END_DOCUMENT) {
				String tag = xmlParser.getName();
				switch (evtType) {
				case XmlPullParser.START_TAG:
					if (tag.equalsIgnoreCase("result")) {
						result = new Result();
					} else if (result != null) {
						if(tag.equalsIgnoreCase("errorcode")){
							result.errorCode=StringUtils.toInt(xmlParser.nextText(), 0);
						}else if(tag.equalsIgnoreCase("errormessage")){
							result.errorMessage=xmlParser.nextText().trim();
						}else if(tag.equalsIgnoreCase("notice")){
							//通知消息
							result.setNotice(new Notice());
						}else if(result.getNotice()!=null){
							if(tag.equalsIgnoreCase("newLqCount")){
								result.getNotice().setNewlqCount(StringUtils.toInt(xmlParser.nextText(), 0));
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				evtType=xmlParser.next();
			}
		} catch (XmlPullParserException e) {
			throw AppException.xml(e);
		}finally{
			stream.close();
		}
		return result;
	}
}
