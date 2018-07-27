package com.ypiao.bean;

import java.io.Serializable;

/**
 * 通知管理 Bean.
 * 
 * Create by xk on 2018-04-27.
 */
public class AderNote implements Serializable {

	private static final long serialVersionUID = 1L;

	// 公告编号
	private String sid;

	// 模板类型
	private int tid = 0;
	
	// 显示位置
	private int position = 0;

	// 公告标题
	private String title;

	// 发布人员
	private String author;

	// 公告内容
	private String detail;

	// 上线时间
	private long sday = 0;

	// 状态
	private int state = 0;

	// 更新时间
	private long time = 0;
	
	// constructor
	public AderNote() {
	}
	
	public AderNote(String sid, int tid, int position, String title, String author, String detail, long sday, int state, long time) {
		super();
		this.sid = sid;
		this.tid = tid;
		this.position = position;
		this.title = title;
		this.author = author;
		this.detail = detail;
		this.sday = sday;
		this.state = state;
		this.time = time;
	}

	// getter and setter
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public long getSday() {
		return sday;
	}

	public void setSday(long sday) {
		this.sday = sday;
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
		return "AderNote [sid=" + sid + ", tid=" + tid + ", position=" + position + ", title=" + title + ", author="
				+ author + ", detail=" + detail + ", sday=" + sday + ", state=" + state + ", time=" + time + "]";
	}

}
