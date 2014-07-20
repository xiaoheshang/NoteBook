package cn.hneao.notebook.bean;

import java.util.Date;

public class Student {
	private Integer id;
	private String ksh;
	private String name;
	private Integer kszt;
	private boolean checked;
	private Date addTime;
	private Integer dxfscs;
	private String bz;
	
	public String getBz() {
		return bz;
	}
	public void setBz(String bz) {
		this.bz = bz;
	}
	public Integer getDxfscs() {
		return dxfscs;
	}
	public void setDxfscs(Integer dxfscs) {
		this.dxfscs = dxfscs;
	}
	public String getKsh() {
		return ksh;
	}
	public void setKsh(String ksh) {
		this.ksh = ksh;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public Integer getKszt() {
		return kszt;
	}
	public void setKszt(Integer kszt) {
		this.kszt = kszt;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public String getTitle()
	{
		return this.ksh+"-"+this.name;
	}
	public String getAddTiemStr()
	{
		return "Ìí¼ÓÓÚ£º"+this.addTime.toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
