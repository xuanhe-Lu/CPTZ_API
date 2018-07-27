package com.ypm.bean;

import java.io.Serializable;

/**
 * 常见问题 Bean.
 * 
 * Created by xk on 2018-05-09.
 */
public class Help implements Serializable {

	private static final long serialVersionUID = 1L;

	// 问题编号,工具类生成15位
	private String sid;

	// 问题分类
	private int tid;

	// 问题
	private String question;
	
	// 答案
	private String answer;
	
	// 排序
	private int sortid;

	// 状态
	private int state = 0;

	// 更新时间
	private long time = 0;
	
	// Constructor
	public Help() {
	}

	public Help(String sid, int tid, String question, String answer, int sortid, int state, long time) {
		super();
		this.sid = sid;
		this.tid = tid;
		this.question = question;
		this.answer = answer;
		this.sortid = sortid;
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

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getSortid() {
		return sortid;
	}

	public void setSortid(int sortid) {
		this.sortid = sortid;
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
		return "Help [sid=" + sid + ", tid=" + tid + ", question=" + question + ", answer=" + answer + ", sortid="
				+ sortid + ", state=" + state + ", time=" + time + "]";
	}
	
}