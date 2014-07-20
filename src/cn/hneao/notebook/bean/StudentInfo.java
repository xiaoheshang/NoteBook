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

public class StudentInfo extends Entity {

	public static final String NODE_START = "student";
	public static final String NODE_SCROE = "scroe";
	public static final String NODE_VOLUNTARY = "voluntary";
	public static final String NODE_ADMISSION = "admission";
	// 基本信息
	public static final String NODE_LSH = "lsh";
	public static final String NODE_YHDM = "yhdm";
	public static final String NODE_ZDFSXX = "zdfsxx";
	public static final String NODE_SFGZ = "sfgz";
	public static final String NODE_XXFSZT = "xxfszt";
	public static final String NODE_DXLXDH = "dxlxdh";
	public static final String NODE_BZ = "bz";
	public static final String NODE_KSZT = "kszt";
	public static final String NODE_ADDTIME="addtime";

	public String getKszt() {
		return kszt;
	}

	public void setKszt(String kszt) {
		this.kszt = kszt;
	}

	public static final String NODE_KSH = "ksh";
	public static final String NODE_XM = "xm";
	public static final String NODE_XB = "xb";
	public static final String NODE_CSNY = "csny";
	public static final String NODE_KH = "kh";
	public static final String NODE_KSLB = "kslb";
	public static final String NODE_ZZMM = "zzmm";
	public static final String NODE_DKLB = "dklb";
	public static final String NODE_LXDH = "lxdh";
	public static final String NODE_TXDZ = "txdz";
	public static final String NODE_YZBM = "yzbm";
	public static final String NODE_BMD = "bmd";
	public static final String NODE_DAH = "dah";

	// 成绩信息
	public static final String NODE_ZF = "zf";
	public static final String NODE_KM1 = "km1";
	public static final String NODE_KM2 = "km2";
	public static final String NODE_KM3 = "km3";
	public static final String NODE_KM4 = "km4";
	public static final String NODE_YHF = "yhf";
	public static final String NODE_ZYF2 = "zyf2";
	public static final String NODE_WYYKS = "wyyks";
	// 录取信息
	public static final String NODE_LQYXDM = "lqyxdm";
	public static final String NODE_LQYXMC = "lqyxmc";
	public static final String NODE_LQZYDM = "lqzydm";
	public static final String NODE_LQZYMC = "lqzymc";
	public static final String NODE_LQTIME = "lqtiem";
	// 志愿信息
	public static final String NODE_TBPCDM = "tbpcdm";
	public static final String NODE_TBPCMC = "tbpcmc";
	public static final String NODE_GBPCDM = "gbpcdm";
	public static final String NODE_GBPCMC = "gbpcmc";
	public static final String NODE_PCDM = "pcdm";
	public static final String NODE_PCMC = "pcmc";
	public static final String NODE_KLDM = "kldm";
	public static final String NODE_KLMC = "klmc";
	public static final String NODE_JHXZDM = "jhxzdm";
	public static final String NODE_JHXZMC = "jhxzmc";
	public static final String NODE_TDDW = "tddw";
	public static final String NODE_ZYH = "zyh";
	public static final String NODE_DF_JS = "df_js";
	public static final String NODE_YXDH = "yxdh";
	public static final String NODE_YXMC = "yxmc";
	public static final String NODE_ZYDH1 = "zydh1";
	public static final String NODE_ZYDH2 = "zydh2";
	public static final String NODE_ZYDH3 = "zydh3";
	public static final String NODE_ZYDH4 = "zydh4";
	public static final String NODE_ZYDH5 = "zydh5";
	public static final String NODE_ZYDH6 = "zydh6";
	public static final String NODE_ZYMC1 = "zymc1";
	public static final String NODE_ZYMC2 = "zymc2";
	public static final String NODE_ZYMC3 = "zymc3";
	public static final String NODE_ZYMC4 = "zymc4";
	public static final String NODE_ZYMC5 = "zymc5";
	public static final String NODE_ZYMC6 = "zymc6";
	public static final String NODE_ZYFC = "zyfc";
	public static final String NODE_YXFC = "yxfc";

	private String lsh;
	private String yhdm;
	private String zdfsxx;
	private String sfgz;
	private String xxfszt;
	private String dxlxdh;
	private String bz;
	private String kszt;
	private String addTime;
	
	private boolean checked;

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	private String ksh;
	private String xm;
	private String xb;
	private String csny;
	private String kh;
	private String kslb;
	private String zzmm;
	private String dklb;
	private String lxdh;
	private String txdz;
	private String yzbm;
	private String bmd;
	private String dah;

	private Scroe scroe;
	private List<Voluntary> voluntarys;
	private Admission admission;

	private Result result;

	public StudentInfo() {
		this.voluntarys = new ArrayList<Voluntary>();
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public String getTitle(){
		return this.ksh+"-"+this.xm;
	}
	
	public String getAddTimeStr(){
		return "添加于："+this.addTime;
	}
	
	public String getLsh() {
		return lsh;
	}

	public void setLsh(String lsh) {
		this.lsh = lsh;
	}

	public String getYhdm() {
		return yhdm;
	}

	public void setYhdm(String yhdm) {
		this.yhdm = yhdm;
	}

	public String getZdfsxx() {
		return zdfsxx;
	}

	public void setZdfsxx(String zdfsxx) {
		this.zdfsxx = zdfsxx;
	}

	public String getSfgz() {
		return sfgz;
	}

	public void setSfgz(String sfgz) {
		this.sfgz = sfgz;
	}

	public String getXxfszt() {
		return xxfszt;
	}

	public void setXxfszt(String xxfszt) {
		this.xxfszt = xxfszt;
	}

	public String getDxlxdh() {
		return dxlxdh;
	}

	public void setDxlxdh(String dxlxdh) {
		this.dxlxdh = dxlxdh;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

	public String getKsh() {
		return ksh;
	}

	public void setKsh(String ksh) {
		this.ksh = ksh;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getXb() {
		return xb;
	}

	public void setXb(String xb) {
		this.xb = xb;
	}

	public String getCsny() {
		return csny;
	}

	public void setCsny(String csny) {
		this.csny = csny;
	}

	public String getKh() {
		return kh;
	}

	public void setKh(String kh) {
		this.kh = kh;
	}

	public String getKslb() {
		return kslb;
	}

	public void setKslb(String kslb) {
		this.kslb = kslb;
	}

	public String getZzmm() {
		return zzmm;
	}

	public void setZzmm(String zzmm) {
		this.zzmm = zzmm;
	}

	public String getDklb() {
		return dklb;
	}

	public void setDklb(String dklb) {
		this.dklb = dklb;
	}

	public String getLxdh() {
		return lxdh;
	}

	public void setLxdh(String lxdh) {
		this.lxdh = lxdh;
	}

	public String getTxdz() {
		return txdz;
	}

	public void setTxdz(String txdz) {
		this.txdz = txdz;
	}

	public String getYzbm() {
		return yzbm;
	}

	public void setYzbm(String yzbm) {
		this.yzbm = yzbm;
	}

	public String getBmd() {
		return bmd;
	}

	public void setBmd(String bmd) {
		this.bmd = bmd;
	}

	public String getDah() {
		return dah;
	}

	public void setDah(String dah) {
		this.dah = dah;
	}

	public Scroe getScroe() {
		return scroe;
	}

	public void setScroe(Scroe scroe) {
		this.scroe = scroe;
	}

	public List<Voluntary> getVoluntarys() {
		return voluntarys;
	}

	public void setVoluntarys(List<Voluntary> voluntarys) {
		this.voluntarys = voluntarys;
	}

	public Admission getAdmission() {
		return admission;
	}

	public void setAdmission(Admission admission) {
		this.admission = admission;
	}

	public static StudentInfo Parse(InputStream inputStream)
			throws IOException, AppException {
		StudentInfo studentInfo = null;
		Scroe scroe = null;
		Admission admission = null;
		Voluntary voluntary = null;
		// 获得XmlPullParser解析器
		XmlPullParser xmlParser = Xml.newPullParser();
		try {
			xmlParser.setInput(inputStream, UTF8);
			// 获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
			int evtType = xmlParser.getEventType();
			// 一直循环，直到文档结束
			while (evtType != XmlPullParser.END_DOCUMENT) {
				String tag = xmlParser.getName();
				switch (evtType) {
				case XmlPullParser.START_TAG:
					if (tag.equalsIgnoreCase(NODE_START)) {
						studentInfo = new StudentInfo();
					} else if (studentInfo != null) {
						if (tag.equalsIgnoreCase(NODE_LSH)) {
							studentInfo.setLsh(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_YHDM)) {
							studentInfo.setYhdm(xmlParser.nextText());
						}else if (tag.equalsIgnoreCase(NODE_ZDFSXX)) {
							studentInfo.setZdfsxx(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_SFGZ)) {
							studentInfo.setSfgz(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_XXFSZT)) {
							studentInfo.setXxfszt(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_DXLXDH)) {
							studentInfo.setDxlxdh(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_BZ)) {
							studentInfo.setBz(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_KSZT)) {
							studentInfo.setKszt(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_KSH)) {
							studentInfo.setKsh(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_XM)) {
							studentInfo.setXm(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_XB)) {
							studentInfo.setXb(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_CSNY)) {
							studentInfo.setCsny(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_KH)) {
							studentInfo.setKh(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_KSLB)) {
							studentInfo.setKslb(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_ZZMM)) {
							studentInfo.setZzmm(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_DKLB)) {
							studentInfo.setDklb(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_LXDH)) {
							studentInfo.setLxdh(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_TXDZ)) {
							studentInfo.setTxdz(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_YZBM)) {
							studentInfo.setYzbm(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_BMD)) {
							studentInfo.setBmd(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_DAH)) {
							studentInfo.setDah(xmlParser.nextText());
						} else if (tag.equalsIgnoreCase(NODE_SCROE)) {
							scroe = new Scroe();
						} else if (scroe != null) {
							// 考生成绩信息
							if (tag.equalsIgnoreCase(NODE_ZF)) {
								scroe.setZf(StringUtils.toInt(xmlParser
										.nextText()));
							} else if (tag.equalsIgnoreCase(NODE_KM1)) {
								scroe.setKm1(StringUtils.toInt(xmlParser
										.nextText()));
							} else if (tag.equalsIgnoreCase(NODE_KM2)) {
								scroe.setKm2(StringUtils.toInt(xmlParser
										.nextText()));
							} else if (tag.equalsIgnoreCase(NODE_KM3)) {
								scroe.setKm3(StringUtils.toInt(xmlParser
										.nextText()));
							} else if (tag.equalsIgnoreCase(NODE_KM4)) {
								scroe.setKm4(StringUtils.toInt(xmlParser
										.nextText()));
							} else if (tag.equalsIgnoreCase(NODE_YHF)) {
								scroe.setYhf(StringUtils.toInt(xmlParser
										.nextText()));
							} else if (tag.equalsIgnoreCase(NODE_ZYF2)) {
								scroe.setZyf2(StringUtils.toInt(xmlParser
										.nextText()));
							} else if (tag.equalsIgnoreCase(NODE_WYYKS)) {
								scroe.setWyyks(xmlParser.nextText());
							}
						} else if (tag.equalsIgnoreCase(NODE_ADMISSION)) {
							admission = new Admission();
						} else if (admission != null) {
							// 录取信息
							if (tag.equalsIgnoreCase(NODE_LQYXDM)) {
								admission.setLqyxdm(xmlParser.nextText());
							}
							if (tag.equalsIgnoreCase(NODE_LQYXMC)) {
								admission.setLqyxmc(xmlParser.nextText());
							}
							if (tag.equalsIgnoreCase(NODE_LQZYDM)) {
								admission.setLqzydm(xmlParser.nextText());
							}
							if (tag.equalsIgnoreCase(NODE_LQZYMC)) {
								admission.setLqzymc(xmlParser.nextText());
							}
							if (tag.equalsIgnoreCase(NODE_LQTIME)) {
								admission.setLqtime(xmlParser.nextText());
							}
						} else if (tag.equalsIgnoreCase(NODE_VOLUNTARY)) {
							voluntary = new Voluntary();
						} else if (voluntary != null) {
							// 志愿信息
							if (tag.equalsIgnoreCase(NODE_TBPCDM)) {
								voluntary.setTbpcdm(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_TBPCMC)) {
								voluntary.setTbpcmc(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_GBPCDM)) {
								voluntary.setGbpcdm(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_GBPCMC)) {
								voluntary.setGbpcmc(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_PCDM)) {
								voluntary.setPcdm(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_PCMC)) {
								voluntary.setPcmc(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_KLDM)) {
								voluntary.setKldm(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_KLMC)) {
								voluntary.setKlmc(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_JHXZDM)) {
								voluntary.setJhxzdm(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_JHXZMC)) {
								voluntary.setJhxzmc(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_TDDW)) {
								voluntary.setTddw(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYH)) {
								voluntary.setZyh(StringUtils.toInt(xmlParser
										.nextText()));
							} else if (tag.equalsIgnoreCase(NODE_DF_JS)) {
								voluntary.setDf_js(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_YXDH)) {
								voluntary.setYxdh(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_YXMC)) {
								voluntary.setYxmc(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYDH1)) {
								voluntary.setZydh1(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYDH2)) {
								voluntary.setZydh2(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYDH3)) {
								voluntary.setZydh3(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYDH4)) {
								voluntary.setZydh4(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYDH5)) {
								voluntary.setZydh5(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYDH6)) {
								voluntary.setZydh6(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYMC1)) {
								voluntary.setZymc1(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYMC2)) {
								voluntary.setZymc2(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYMC3)) {
								voluntary.setZymc3(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYMC4)) {
								voluntary.setZymc4(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYMC5)) {
								voluntary.setZymc5(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYMC6)) {
								voluntary.setZymc6(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_ZYFC)) {
								voluntary.setZyfc(xmlParser.nextText());
							} else if (tag.equalsIgnoreCase(NODE_YXFC)) {
								voluntary.setYxfc(xmlParser.nextText());
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					// 如果遇到标签结束，则把对象添加进集合中
					if (tag.equalsIgnoreCase(NODE_VOLUNTARY)
							&& studentInfo != null && voluntary != null) {
						studentInfo.getVoluntarys().add(voluntary);
						voluntary = null;
					}
					break;
				}
				// 如果xml没有结束，则导航到下一个节点
				evtType = xmlParser.next();
			}
		} catch (XmlPullParserException e) {
			throw AppException.xml(e);
		} finally {
			inputStream.close();
		}

		return studentInfo;
	}

}
