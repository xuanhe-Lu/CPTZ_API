package com.ypiao.json;

import org.apache.log4j.Logger;

import com.ypiao.bean.AjaxInfo;
import com.ypiao.service.SysConfig;
import com.ypiao.util.ConfigKey;

/**
 * Update by xk on 2018-07-13.
 * 
 * 关于信息接口.
 */
public class AtAbout extends Action implements ConfigKey {

	private static final long serialVersionUID = 9180417072425815832L;
	
	private static final Logger LOGGER = Logger.getLogger(AtAbout.class);

	private SysConfig sysConfig;

	public AtAbout() {
		super(true);
	}

	public SysConfig getSysConfig() {
		return sysConfig;
	}

	public void setSysConfig(SysConfig sysConfig) {
		this.sysConfig = sysConfig;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			SysConfig sys = this.getSysConfig();
			json.addObject();
			json.append( "company", sys.getString(COMPANY) );
			json.append( "version", sys.getString(VERSION) );
			json.append( "siteurl", sys.getString(SITE_URL) );
			json.append( "kefu_tel", sys.getString(KEFU_TEL) );
			json.append( "kefu_email", sys.getString(KEFU_EMAIL) );
			json.append( "kefu_weixi", sys.getString(KEFU_WEIXI) );
			json.append( "kefu_wxgzh", sys.getString(KEFU_WXGZH) );
		} catch (Exception e) {
			LOGGER.info( "获取关于信息失败，异常信息：" + e.getMessage() );
			e.printStackTrace();
			json.addError(this.getText( "system.error.get" ));
		}

		LOGGER.info("json:"+json.toString());
 return JSON;
	}
}
