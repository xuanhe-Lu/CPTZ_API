package com.ypiao.json;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.SetClient;
import com.ypiao.service.ConfigService;

public class AtConfig extends Action {

	private static final long serialVersionUID = 312039428441730181L;

	private ConfigService configService;

	public AtConfig() {
		super(true);
	}

	public ConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public String android() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int ver = this.getInt("code"); // 当前版本号
			SetClient c = this.getConfigService().findClientByAndroid(ver);
			if (c == null || c.getCode() == ver) {
				json.success(API_OK);
			} else {
				json.addObject();
				json.append("code", c.getCode());
				json.append("codever", c.getCodever());
				json.append("content", c.getContent());
				if (ver > c.getForce()) {
					json.append("force", 0);
				} else {
					json.append("force", 1);
				}
				json.append("filename", "yinpiao.apk");
				json.append("tday", c.getTday());
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.get"));
		}
		return JSON;
	}

	public String ios() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int ver = this.getInt("code"); // 当前版本号
			SetClient c = this.getConfigService().findClientByIOS(ver);
			if (c == null) {
				json.success(API_OK);
			} else if (c.getCode() == ver) {
				json.success(c.getCode());
			} else {
				json.addObject();
				json.append("code", c.getCode());
				json.append("codever", c.getCodever());
				json.append("content", c.getContent());
				if (ver > c.getForce()) {
					json.append("force", 0);
				} else {
					json.append("force", 1);
				}
				json.append("tday", c.getTday());
				json.append("url", "");
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.get"));
		}
		return JSON;
	}

	public String index() {
		return JSON;
	}

	public String binds() {
		AjaxInfo json = this.getAjaxInfo();
		json.addError(this.toString());
		return JSON;
	}
}
