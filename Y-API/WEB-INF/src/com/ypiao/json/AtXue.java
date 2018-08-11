package com.ypiao.json;

import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.Xues;
import com.ypiao.service.XuerInfoService;
import com.ypiao.util.GMTime;

/**
 * Created by xk on 2018-05-15.
 * 
 * 票友学堂数据接口. 
 */
public class AtXue extends Action {

	private static final long serialVersionUID = 8598185592101882657L;

	// 接收学堂参数sid
	private String sid;
	
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	// 返回的学堂信息对象
	private Xues xues;
	
	public Xues getXues() {
		return xues;
	}

	public void setXues(Xues xues) {
		this.xues = xues;
	}
	
	// 返回的处理后的学堂更新时间String
	private String time;
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	// Constructor
	public AtXue() {
		super(true);
	}
	
	// 注入 XuerInfoService
	private XuerInfoService xuerInfoService;
	
	public XuerInfoService getXuerInfoService() {
		return xuerInfoService;
	}

	public void setXuerInfoService(XuerInfoService xuerInfoService) {
		this.xuerInfoService = xuerInfoService;
	}

	/**
	 * @return String
	 * 
	 * 票友学堂数据列表
	 */
	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			this.getXuerInfoService().sendByAll(json);
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.get" ));
		}
		
		logger.info("json:"+json.toString());
 return JSON;
	}
	
	/**
	 * @return String
	 * 
	 * 票友学堂单条记录返回
	 */
	public String one() {
		AjaxInfo json = this.getAjaxInfo();
		String sid = this.sid;
		
		try {
			if (sid != null && !sid.equals("")) {
				AjaxInfo ajaxInfo = this.getXuerInfoService().sendByOne( json, sid );
				if (ajaxInfo == null) {
					json.datas(API_ERROR);
					json.addError(this.getText( "xue.none" ));
				}
			} else {
				json.addError(this.getText( "xue.none" ));
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "system.error.get" ));
		}
		
		logger.info("json:"+json.toString());
 return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 票友学堂列表项H5页面
	 */
	public String h5 () {
		String sid = this.sid;
		try {
			this.xues = this.getXuerInfoService().findXuesBySId(sid);
			this.time = this.xues == null ? null : GMTime.format( this.xues.getTime(), GMTime.CHINA );
			if (this.xues == null) {
				return ERROR;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
		
		return SUCCESS;
	}
	
}
