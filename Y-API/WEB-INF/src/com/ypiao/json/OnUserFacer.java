package com.ypiao.json;

import java.io.File;
import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.UserFacer;
import com.ypiao.bean.UserSession;
import com.ypiao.service.UserFaceService;
import com.ypiao.service.UserLogerService;
import com.ypiao.util.GMTime;
import com.ypiao.util.VeRule;

public class OnUserFacer extends Action {

	private static final long serialVersionUID = -5226644614194276685L;

	private UserFaceService userFaceService;

	private UserLogerService userLogerService;

	private File file;

	public OnUserFacer() {
		super(false);
	}

	public UserFaceService getUserFaceService() {
		return userFaceService;
	}

	public void setUserFaceService(UserFaceService userFaceService) {
		this.userFaceService = userFaceService;
	}

	public UserLogerService getUserLogerService() {
		return userLogerService;
	}

	public void setUserLogerService(UserLogerService userLogerService) {
		this.userLogerService = userLogerService;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			File file = this.getFile();
			if (file == null) {
				json.addError(this.getText("user.error.141", new String[] { "0K" }));
			} else if (file.isFile()) {
				UserSession us = this.getUserSession();
				int ver = this.getUserFaceService().findFaceByUid(us.getUid());
				UserFacer f = new UserFacer();
				f.setSid(VeRule.getSid(us.getUid(), ver));
				f.setUid(us.getUid());
				f.setVer(ver);
				f.setState(STATE_NORMAL);
				f.setTime(System.currentTimeMillis());
				this.getUserFaceService().saveFace(f, file, true);
				us.setFacer(f.getVer());
				this.getUserLogerService().update(us);
				json.addObject();
				json.append("uid", us.getUid());
				json.append("facer", us.getFacer());
			}
		} catch (SQLException e) {
			json.addError(this.getText("user.error.140"));
		} finally {
		}
		logger.info("json:"+json.toString());
 return JSON;
	}
}
