package com.ypm.bean;

import java.io.Serializable;

/**
 * 福利专区 Bean.
 * 
 * Created by xk on 2018-06-12.
 */
public class Welfare implements Serializable {

	private static final long serialVersionUID = 1L;

	// 编号,系统自动生成15位
	private String sid;

	// 类型,1：精选活动,2：近期活动
	private int type = 0;

	// 状态,0：启用,1：禁用
	private int state = 0;
	
	// 标题
	private String title;
	
	// 跳转链接
	private String url;

	// 图片格式,如：png
	private String dist;

	// 图片版本,数字
	private int ver = 0;
	
	// 更新时间
	private long time = 0;
	
	// Constructor
	public Welfare() {
	}

	public Welfare(String sid, int type, int state, String title, String url, String dist, int ver, long time) {
		super();
		this.sid = sid;
		this.type = type;
		this.state = state;
		this.title = title;
		this.url = url;
		this.dist = dist;
		this.ver = ver;
		this.time = time;
	}

	// Getter and Setter
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDist() {
		return dist;
	}

	public void setDist(String dist) {
		this.dist = dist;
	}

	public int getVer() {
		return ver;
	}

	public void setVer(int ver) {
		this.ver = ver;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	// toString method
	@Override
	public String toString() {
		return "Welfare [sid=" + sid + ", type=" + type + ", state=" + state + ", title=" + title + ", url=" + url
				+ ", dist=" + dist + ", ver=" + ver + ", time=" + time + "]";
	}
}

