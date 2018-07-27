package com.ypm.user;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.UserInfo;
import com.ypm.service.UserInfoService;
import com.ypm.util.VeStr;

public class InfoManage extends Action {

	private static final long serialVersionUID = 5426500922285137741L;

	private UserInfoService userInfoService;

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	public String getInfo() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long uid = this.getLong("Uid");
			UserInfo info = this.getUserInfoService().findUserByUid(uid);
			if (info == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("UID", info.getUid());
				json.append("VIP", info.getVIP());
				json.append("ACCOUNT", info.getAccount());
				json.append("GENDER", info.getGender());
				json.append("NICER", info.getNicer());
				json.append("REALS", info.getReals());
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.none"));
		}
		return JSON;
	}

	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		String key = this.getString("key");
		if (key == null) {
			// Ignored
		} else {
			switch (getInt("Sel")) {
			case 1:
				sql.append(" AND Uid LIKE ?");
				break;
			case 2:
				sql.append(" AND Nicer LIKE ?");
				break;
			default:
				sql.append(" AND Account LIKE ?");
			}
			fs.add('%' + key + '%');
		} // 排序信息
		StringBuilder sb = new StringBuilder();
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Uid")) {
			sb.append("Uid");
			sort = "Uid";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Uid")) {
			if (dir == null || dir.equalsIgnoreCase("DESC")) {
				sb.append(" DESC");
			} else {
				sb.append(" ASC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("DESC")) {
			sb.append(" DESC,Uid DESC");
		} else {
			sb.append(" ASC,Uid DESC");
		} // 加载数据信息
		this.setAjaxInfo(this.getUserInfoService().findUserByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	public String save() {
		return JSON;
	}

	/** 修改信息 */
	public String modInfo() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long uid = this.getLong("Uid");
			UserInfo info = this.getUserInfoService().findUserByUid(uid);
			if (info == null) {
				json.addError(this.getText("system.error.none"));
			} else {

			}
		} catch (Exception e) {
			json.addError(this.getText("data.update.failed"));
		}
		return JSON;
	}

	/** 修改密码 */
	public String modPwd() {
		AjaxInfo json = this.getAjaxInfo();
		String Pword = this.getString("PWORD");
		String rePwd = this.getString("REPWD");
		try {
			if (Pword == null) {
				json.addError(this.getText("user.error.011"));
			} else if (Pword.equalsIgnoreCase(rePwd)) {
				long uid = this.getLong("Uid");
				this.getUserInfoService().updatePwd(uid, VeStr.MD5(rePwd));
				json.addMessage(this.getText("data.update.succeed"));
			} else {
				json.addError(this.getText("user.error.012"));
			}
		} catch (Exception e) {
			json.addError(this.getText("data.update.failed"));
		} finally {
			rePwd = Pword = null;
		}
		return JSON;
	}

	public String export() {
		this.getAjaxInfo().addError(getText("system.error.export"));
		return JSON;
	}
}
