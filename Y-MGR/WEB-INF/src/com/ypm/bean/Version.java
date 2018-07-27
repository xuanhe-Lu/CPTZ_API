package com.ypm.bean;

import java.io.Serializable;

/**
 * app版本更新 Bean.
 * 
 * Created by xk on 2018-06-06.
 */
public class Version implements Serializable {

	private static final long serialVersionUID = 1L;

	// 版本id
	private String sid;
	
	// 平台类型,1：Android,2：iOS
	private int tid = 0;

	// 版本号
	private String num;

	// 升级方式
	private int mid = 0;
	
	// 版本大小
	private double size = 0;
	
	// apk文件名
	private String filename;
	
	// 安装文件格式
	private String dist;
	
	// 更新内容
	private String description;

	// 更新时间
	private long time = 0;
	
	// Constructor
	public Version() {
	}

	public Version(String sid, int tid, String num, int mid, double size, String filename, String dist,
			String description, long time) {
		super();
		this.sid = sid;
		this.tid = tid;
		this.num = num;
		this.mid = mid;
		this.size = size;
		this.filename = filename;
		this.dist = dist;
		this.description = description;
		this.time = time;
	}

	// Getter and Setter
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

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getDist() {
		return dist;
	}

	public void setDist(String dist) {
		this.dist = dist;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		return "Version [sid=" + sid + ", tid=" + tid + ", num=" + num + ", mid=" + mid + ", size=" + size
				+ ", filename=" + filename + ", dist=" + dist + ", description=" + description + ", time=" + time + "]";
	}
	
}

