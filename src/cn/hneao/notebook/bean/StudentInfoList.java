package cn.hneao.notebook.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import cn.hneao.notebook.AppException;
import cn.hneao.notebook.common.StringUtils;
import android.util.Xml;

/**
 * 考生信息列表类
 * 
 * @author Administrator
 * 
 */
public class StudentInfoList extends Entity {
	/**
	 * 未投档
	 */
	public final static int CATALOG_WTD = 1;
	/**
	 * 已投档
	 */
	public final static int CATALOG_YTD = 2;
	/**
	 * 已关注
	 */
	public final static int CATALOG_YGZ = 3;

	private int catalog;
	private int pageSize;
	private int studentCount;
	private List<StudentInfo> studentInfoList = new ArrayList<StudentInfo>();

	public int getCatalog() {
		return catalog;
	}

	public void setCatalog(int catalog) {
		this.catalog = catalog;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(int studentCount) {
		this.studentCount = studentCount;
	}

	public List<StudentInfo> getStudentInfoList() {
		return studentInfoList;
	}

	public void setStudentInfoList(List<StudentInfo> studentInfoList) {
		this.studentInfoList = studentInfoList;
	}

	public static StudentInfoList Parse(InputStream inputStream)
			throws IOException, AppException {
		StudentInfo studentInfo = null;
		StudentInfoList infoList = new StudentInfoList();
		XmlPullParser xmlParser = Xml.newPullParser();

		try {
			xmlParser.setInput(inputStream, UTF8);
			int evType = xmlParser.getEventType();
			while (evType != XmlPullParser.END_DOCUMENT) {
				String tag = xmlParser.getName();
				switch (evType) {
				case XmlPullParser.START_TAG:
					if (tag.equalsIgnoreCase("catalog")) {
						infoList.setCatalog(StringUtils.toInt(xmlParser
								.nextText()));
					} else if (tag.equalsIgnoreCase("pageSize")) {
						infoList.setPageSize(StringUtils.toInt(xmlParser
								.nextText()));
					} else if (tag.equalsIgnoreCase("studentCount")) {
						infoList.setStudentCount(StringUtils.toInt(xmlParser
								.nextText()));
					} else if (tag.equalsIgnoreCase(StudentInfo.NODE_START)) {
						studentInfo = new StudentInfo();
					} else if (studentInfo != null) {
						// 考生基本信息
						if (tag.equalsIgnoreCase(StudentInfo.NODE_LSH)) {
							studentInfo.setLsh(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_YHDM)) {
							studentInfo.setYhdm(xmlParser.nextText());
						} else if (tag
								.equalsIgnoreCase(StudentInfo.NODE_ZDFSXX)) {
							studentInfo.setZdfsxx(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_SFGZ)) {
							studentInfo.setSfgz(xmlParser.nextText());
						} else if (tag
								.equalsIgnoreCase(StudentInfo.NODE_XXFSZT)) {
							studentInfo.setXxfszt(xmlParser.nextText());
						} else if (tag
								.equalsIgnoreCase(StudentInfo.NODE_DXLXDH)) {
							studentInfo.setDxlxdh(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_BZ)) {
							studentInfo.setBz(xmlParser.nextText());
						} else if (tag
								.equalsIgnoreCase(StudentInfo.NODE_ADDTIME)) {
							studentInfo.setAddTime(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_KSZT)) {
							studentInfo.setKszt(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_KSH)) {
							studentInfo.setKsh(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_XM)) {
							studentInfo.setXm(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_XB)) {
							studentInfo.setXb(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_CSNY)) {
							studentInfo.setCsny(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_KH)) {
							studentInfo.setKh(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_KSLB)) {
							studentInfo.setKslb(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_ZZMM)) {
							studentInfo.setZzmm(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_DKLB)) {
							studentInfo.setDklb(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_LXDH)) {
							studentInfo.setLxdh(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_TXDZ)) {
							studentInfo.setTxdz(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_YZBM)) {
							studentInfo.setYzbm(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_BMD)) {
							studentInfo.setBmd(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(StudentInfo.NODE_DAH)) {
							studentInfo.setDah(xmlParser.nextText());
						}
					} else if (tag.equalsIgnoreCase("notice")) {
						infoList.setNotice(new Notice());
					} else if (infoList.getNotice() != null) {
						if (tag.equalsIgnoreCase("newlqCount")) {
							infoList.getNotice().setNewlqCount(
									StringUtils.toInt(xmlParser.nextText()));
						}
					}
					break;
				case XmlPullParser.END_TAG:
					// 添加对象当List列表中
					if (tag.equalsIgnoreCase("student") && studentInfo != null) {
						infoList.getStudentInfoList().add(studentInfo);
						studentInfo = null;
					}
					break;
				}
				// 如果xml没有结束，则导航到下一个节点
				evType = xmlParser.next();
			}
		} catch (XmlPullParserException e) {
			throw AppException.xml(e);
		} finally {
			inputStream.close();
		}
		return infoList;
	}

}
