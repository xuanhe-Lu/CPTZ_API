package com.ypiao.bean;

import java.io.File;
import java.io.Serializable;

public class SyncInfo implements Serializable {

	private static final long serialVersionUID = -8027620395731577660L;

	private long sid = 0;

	private int cls = 0;

	private int rev = 0;

	private String content;

	private File file;

	private long time = 0;

	public SyncInfo() {
	}

	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public int getCls() {
		return cls;
	}

	public void setCls(int cls) {
		this.cls = cls;
	}

	public int getRev() {
		return rev;
	}

	public void setRev(int rev) {
		this.rev = rev;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFPath() {
		if (file == null) {
			// Ignored
		} else if (file.isFile()) {
			return file.getPath();
		}
		return null;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
