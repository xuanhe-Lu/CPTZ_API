package com.ypiao.bean;

import java.io.Serializable;

/**
 * 新闻管理 Bean.
 * 
 * Created by xk on 2018-05-15.
 */
public class News implements Serializable {

	private static final long serialVersionUID = 1L;

	// 编号
	private String sid;

	// 标题
	private String title;

	// 标签
	private String subject;
	
	// 显示位置
	private int position = 0;
	
	// 作者
	private String author;

	// 内容
	private String detail;

	// 图片格式
	private String dist;
	
	// 图片版本
	private int ver = 0;

	// 状态
	private int state = 0;

	// 更新时间
	private long time = 0;
	
	// Constructor
	public News() {
	}
	
	public News(String sid, String title, String subject, int position, String author, String detail, String dist, int ver, int state, long time) {
		super();
		this.sid = sid;
		this.title = title;
		this.subject = subject;
		this.position = position;
		this.author = author;
		this.detail = detail;
		this.dist = dist;
		this.ver = ver;
		this.state = state;
		this.time = time;
	}

	// Getter and Setter
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
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
		return "News [sid=" + sid + ", title=" + title + ", subject=" + subject + ", position=" + position + ", author="
				+ author + ", detail=" + detail + ", dist=" + dist + ", ver=" + ver + ", state=" + state + ", time="
				+ time + "]";
	}
	
}

