package com.ypiao.bean;

import java.io.Serializable;
import com.ypiao.util.GMTime;

public class Config implements Serializable {

	private static final long serialVersionUID = -1954282812396141336L;

	private String id, sid;

	private int losk = 0;

	private int type = 0;

	private int sortid = 0;

	private String sindex;

	private String content;

	private String remark;

	private long timeout;

	private long time;

	public boolean isExpired() {
		if (timeout <= 99) {
			return false;
		} else if (content == null) {
			return true;
		} else {
			return (GMTime.currentTimeMillis() > timeout);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public int getLosk() {
		return losk;
	}

	public void setLosk(int losk) {
		this.losk = losk;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSortid() {
		return sortid;
	}

	public void setSortid(int sortid) {
		this.sortid = sortid;
	}

	public String getSindex() {
		return sindex;
	}

	public void setSindex(String sindex) {
		this.sindex = sindex;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "Config [id=" + id + ", sid=" + sid + ", losk=" + losk + ", type=" + type + ", sortid=" + sortid
				+ ", sindex=" + sindex + ", content=" + content + ", remark=" + remark + ", timeout=" + timeout
				+ ", time=" + time + "]";
	}
}
