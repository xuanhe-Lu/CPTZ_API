package com.ypiao.bean;

import java.io.Serializable;
/**
 * 排重接口
 * */
public class ComDuplicate implements Serializable {
	private static final long serialVersionUID = -3697234633236889833L;
	private Integer aId;
	private String appId;
	private String iphType;
	private String iphMd;
	private String iphVersion;
	private String iphName;
	private String serialNumber;
	private String iphSystem;
	private String idfa;
	private String source;
	// 更新时间
	private long time = 0;
	public Integer getaId() {
		return aId;
	}
	public void setaId(Integer aId) {
		this.aId = aId;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getIphType() {
		return iphType;
	}
	public void setIphType(String iphType) {
		this.iphType = iphType;
	}
	public String getIphMd() {
		return iphMd;
	}
	public void setIphMd(String iphMd) {
		this.iphMd = iphMd;
	}
	public String getIphVersion() {
		return iphVersion;
	}
	public void setIphVersion(String iphVersion) {
		this.iphVersion = iphVersion;
	}
	public String getIphName() {
		return iphName;
	}
	public void setIphName(String iphName) {
		this.iphName = iphName;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getIphSystem() {
		return iphSystem;
	}
	public void setIphSystem(String iphSystem) {
		this.iphSystem = iphSystem;
	}
	public String getIdfa() {
		return idfa;
	}
	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}
