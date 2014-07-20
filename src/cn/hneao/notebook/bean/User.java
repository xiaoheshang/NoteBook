package cn.hneao.notebook.bean;

import java.io.IOException;
import java.io.InputStream;

import cn.hneao.notebook.bean.Notice;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import cn.hneao.notebook.AppException;
import cn.hneao.notebook.common.StringUtils;

/**
 * �û���¼ʵ����
 * @author Administrator
 *
 */
public class User extends Base {
	private String yhdm;
	private String yhmc;
	private String yhmm;
	private String lxdh;
	private String qyzt;
	private String addTime;
	private boolean isRememberMe;
	
	public boolean isRememberMe() {
		return isRememberMe;
	}
	public void setRememberMe(boolean isRememberMe) {
		this.isRememberMe = isRememberMe;
	}

	private Result validate;

	public Result getValidate() {
		return validate;
	}
	public void setValidate(Result validate) {
		this.validate = validate;
	}
	public String getYhdm() {
		return yhdm;
	}
	public void setYhdm(String yhdm) {
		this.yhdm = yhdm;
	}
	public String getYhmc() {
		return yhmc;
	}
	public void setYhmc(String yhmc) {
		this.yhmc = yhmc;
	}
	public String getYhmm() {
		return yhmm;
	}
	public void setYhmm(String yhmm) {
		this.yhmm = yhmm;
	}
	public String getLxdh() {
		return lxdh;
	}
	public void setLxdh(String lxdh) {
		this.lxdh = lxdh;
	}
	public String getQyzt() {
		return qyzt;
	}
	public void setQyzt(String qyzt) {
		this.qyzt = qyzt;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	
	public static User parse(InputStream stream) throws IOException, AppException{
		User user=new User();
		Result result=null;
		// ���XmlPullParser������
		XmlPullParser xmlParser = Xml.newPullParser();
		try {
			xmlParser.setInput(stream,Base.UTF8);
			int evtType=xmlParser.getEventType();
			while(evtType!=XmlPullParser.END_DOCUMENT){
				String tag=xmlParser.getName();
				switch(evtType){
				case XmlPullParser.START_TAG:
					if(tag.equalsIgnoreCase("result")){
						result=new Result();
					}else if(tag.equalsIgnoreCase("errorcode")){
							result.setErrorCode(StringUtils.toInt(xmlParser.nextText(),0));
					}else if(tag.equalsIgnoreCase("errormessage")){
							result.setErrorMessage(xmlParser.nextText().trim());
					}else if(result!=null && result.OK()){
						//��ȡ���õ���Ϣ����
						if(tag.equalsIgnoreCase("yhdm")){
							user.setYhdm(xmlParser.nextText());
						}else if(tag.equalsIgnoreCase("yhmc")){
							user.setYhmc(xmlParser.nextText());
						}else if(tag.equalsIgnoreCase("lxdh")){
							user.setLxdh(xmlParser.nextText());
						}else if(tag.equalsIgnoreCase("addTime")){
							user.setAddTime(xmlParser.nextText());
						}else if(tag.equalsIgnoreCase("qyzt")){
							user.setQyzt(xmlParser.nextText());
						}
						//֪ͨ��Ϣ
						else if(tag.equalsIgnoreCase("notice")){
							user.setNotice(new Notice());
						}else if(user.getNotice()!=null){
							if(tag.equalsIgnoreCase("newLqCount")){
								result.getNotice().setNewlqCount(StringUtils.toInt(xmlParser.nextText(), 0));
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					//�������Ϣ�洢��validateʵ����
					if(tag.equalsIgnoreCase("result") && result!=null){
						user.setValidate(result);
					}
					break;
				}
				evtType = xmlParser.next();
			}
		} catch (XmlPullParserException e) {
			throw AppException.xml(e);
		}finally{
			stream.close();
		}
		return user;
	}
}
