package com.ypiao.json;

import java.util.List;

import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.ComDuplicate;
import com.ypiao.service.ComDuplicateService;

public class ComDuplicateAt extends Action{
	
	public ComDuplicateAt() {
		super(true);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 6058010698613509150L;
	private ComDuplicateService comDuplicateService;
	
	public ComDuplicateService getComDuplicateService() {
		return comDuplicateService;
	}

	public void setComDuplicateService(ComDuplicateService comDuplicateService) {
		this.comDuplicateService = comDuplicateService;
	}
	private String type;
	private String idfa;
	private String source;
	private String appId;
	private String iphMd;
	private String iphName;
	private String iphSystem;
	private String iphVersion;
	private String serialNumber;
	
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getIphMd() {
		return iphMd;
	}

	public void setIphMd(String iphMd) {
		this.iphMd = iphMd;
	}

	public String getIphName() {
		return iphName;
	}

	public void setIphName(String iphName) {
		this.iphName = iphName;
	}

	public String getIphSystem() {
		return iphSystem;
	}

	public void setIphSystem(String iphSystem) {
		this.iphSystem = iphSystem;
	}

	public String getIphVersion() {
		return iphVersion;
	}

	public void setIphVersion(String iphVersion) {
		this.iphVersion = iphVersion;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getType() {
		return type;
	}
	public String getSource() {
		return source;
	}
	public String getIdfa() {
		return idfa;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}
	public String index() {
		AjaxInfo json = this.getAjaxInfo();
//		String type = type;//类型：ios、Android
		//String idfa = this.idfa;//苹果设备的 IDFA
		//String source = this.source;//渠道标识
		logger.info("type:"+type);
		logger.info("idfa:"+idfa);
		try {
			ComDuplicate cd = new ComDuplicate();
			cd.setAppId(appId);//app的appleid
			cd.setIdfa(idfa);
			cd.setIphMd(iphMd);//手机型号
			cd.setIphName(iphName);//手机名称
			cd.setIphSystem(iphSystem);//手机操作系统
			cd.setIphType(type);
			cd.setIphVersion(iphVersion);//版本号
			cd.setSerialNumber(serialNumber);//手机序列号
			cd.setSource(source);
//			if (type.equals("") || idfa.equals("") || source.equals("")) {
			if (type.equals("") || type == null) {
				json.addError(this.getText("未获取到相关信息！"));
			}else {
				if (type.equals("iOS") || type.equals("Android")) {
					ComDuplicate cdList = this.getComDuplicateService().findDuplicates(idfa, type);
					if (cdList == null) {
						cdList = new ComDuplicate();
					}
					logger.info("查询是否存在的数据有多少条："+cdList.getIdfa());
					if (cdList.getIdfa() != null) {//不是首次下载
						
						System.out.println("已存在的数据！");
					}else {//首次下载
						logger.info("44444444444444"+cd.getIdfa());
						boolean ig = this.getComDuplicateService().saveDuplicate(cd);
						System.out.println("ig="+ig);
						logger.info("ig:"+ig);
					}
					logger.info("aaaaaaaaaaaaaaaaaaaaaa");
				}else {
					logger.info("其它系统...");
				}
				
			}
		} catch (Exception e) {
			logger.info("e:"+e.getMessage());
			json.addError(this.getText("system.error.get"));
		}
		logger.info("json:"+json.toString());
 return JSON;
	}
}
