package com.ypm.bean;

import java.io.Serializable;

/**
 * 推送管理 Bean.
 * 
 * Created by xk on 2018-06-01.
 */
public class Push implements Serializable {

	private static final long serialVersionUID = 1L;

	// 推送编号
	private long sid;

	// 推送标题
	private String title;

	// 推送内容
	private String content;
	
	// 推送系统
	private int system = 0;
	
	// 发送类型
	private int type = 0;
	
	// 定时发送时间
	private long timer = 0;

	// 发送对象
	private int target = 0;
	
	// 指定发送用户id
	private long uid;

	// 状态
	private int state = 0;

	// 创建时间
	private long time = 0;
	
	// Constructor
	public Push() {
	}

	public Push(long sid, String title, String content, int system, int type, long timer, int target, long uid, int state, long time) {
		super();
		this.sid = sid;
		this.title = title;
		this.content = content;
		this.system = system;
		this.type = type;
		this.timer = timer;
		this.target = target;
		this.uid = uid;
		this.state = state;
		this.time = time;
	}

	// Getter and Setter
	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getSystem() {
		return system;
	}

	public void setSystem(int system) {
		this.system = system;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getTimer() {
		return timer;
	}

	public void setTimer(long timer) {
		this.timer = timer;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
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
		return "Push [sid=" + sid + ", title=" + title + ", content=" + content + ", system=" + system + ", type="
				+ type + ", timer=" + timer + ", target=" + target + ", uid=" + uid + ", state=" + state + ", time="
				+ time + "]";
	}
	
}

