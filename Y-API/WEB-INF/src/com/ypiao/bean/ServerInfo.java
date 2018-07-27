package com.ypiao.bean;

import java.io.Serializable;
import com.ypiao.util.AUtils;

public class ServerInfo implements Serializable {

	private static final long serialVersionUID = 1893461424779995487L;

	private String key;

	private int sid;

	private String mfk;

	private int mfs = 1;

	private String name;

	private String iper;

	private int port = 0;

	private int state = 0;

	private long stime = 0;

	private long time = 0;

	public ServerInfo() {
	}

	public void setId(String id) {
		if (id == null || id.length() < 2) {
			// Ignored
		} else {
			this.mfk = id.substring(0, 1);
			this.mfs = AUtils.toInt(id.substring(1, 2), 1);
		}
	}

	public String getKey() {
		if (key == null) {
			key = mfk + mfs;
		}
		return key;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public String getMfk() {
		return mfk;
	}

	public void setMfk(String mfk) {
		this.mfk = mfk;
	}

	public int getMfs() {
		return mfs;
	}

	public void setMfs(int mfs) {
		this.mfs = mfs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIper() {
		return iper;
	}

	public void setIper(String iper) {
		this.iper = iper;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getStime() {
		return stime;
	}

	public void setStime(long stime) {
		this.stime = stime;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int hashCode() {
		StringBuilder sb = new StringBuilder();
		sb.append(mfk).append(':').append(mfs);
		return sb.toString().hashCode();
	}

}
