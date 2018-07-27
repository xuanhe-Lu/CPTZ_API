package com.ypm.bean;

import java.io.Serializable;

/**
 * Update by xk on 2018-07-16.
 * 
 * 报表规则实体类.
 */
public class FieldInfo implements Serializable {

	private static final long serialVersionUID = -1004135347384658430L;

	private int id = 0;

	private int sid = 0;

	private int sortid = 1;

	private String key;

	private String name;

	private String nice;

	private int width = 0;

	private boolean pkid = false;

	private int show = 0;

	private int sortab = 0;

	private int export = 0;

	private int type = 0;

	private String format;

	public FieldInfo() {
	}

	public boolean equals(String name) {
		return (name.equalsIgnoreCase(key));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public int getSortid() {
		return sortid;
	}

	public void setSortid(int sortid) {
		this.sortid = sortid;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			this.key = null;
		} else {
			this.key = name.toUpperCase();
		} // 名称值
		this.name = name;
	}

	public String getNice() {
		return nice;
	}

	public void setNice(String nice) {
		this.nice = nice;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isExport() {
		return (export == 1);
	}

	public boolean isPkid() {
		return pkid;
	}

	public void setPkid(boolean pkid) {
		this.pkid = pkid;
	}

	public int getShow() {
		return show;
	}

	public void setShow(int show) {
		this.show = show;
	}

	public int getSortab() {
		return sortab;
	}

	public void setSortab(int sortab) {
		this.sortab = sortab;
	}

	public int getExport() {
		return export;
	}

	public void setExport(int export) {
		this.export = export;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
