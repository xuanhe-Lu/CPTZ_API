package com.ypm.kefu;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.commons.lang.StringUtils;
import com.ypm.Action;
import com.ypm.bean.*;
import com.ypm.service.UserAuthService;
import com.ypm.service.UserBankService;
import com.ypm.util.GMTime;

public class UserAtBank extends Action {

	private static final long serialVersionUID = 2408356313129209686L;

	private UserAuthService userAuthService;

	private UserBankService userBankService;

	public UserAuthService getUserAuthService() {
		return userAuthService;
	}

	public void setUserAuthService(UserAuthService userAuthService) {
		this.userAuthService = userAuthService;
	}

	public UserBankService getUserBankService() {
		return userBankService;
	}

	public void setUserBankService(UserBankService userBankService) {
		this.userBankService = userBankService;
	}

	public String info() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long sid = this.getLong("sid");
			UserBker bk = this.getUserBankService().findBkerBySid(sid);
			if (bk == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				UserAuth a = this.getUserAuthService().findAuthByUid(bk.getUid());
				long k = bk.getUid() % 1000;
				StringBuilder sb = new StringBuilder("/img/user/");
				if (k < 10) {
					sb.append("00");
				} else if (k < 100) {
					sb.append("0");
				} // 格式信息
				sb.append(k).append('/').append(bk.getUid()).append('/');
				String fPath = sb.toString();
				json.data();
				json.append("SID", bk.getSid());
				json.append("NAME", bk.getName());
				if (a == null) {
					json.append("IDCARD", "NONE");
				} else {
					json.append("IDCARD", a.getIdCard());
				}
				json.append("MOBILE", bk.getMobile());
				json.append("CNO", bk.getCNo());
				if (this.checkValid(bk.getBa(), 10)) {
					sb.append(bk.getBa().toLowerCase()).append(".jpg");
					json.append("BA", sb.toString());
				} else {
					json.append("BA", "/images/s.gif");
				}
				sb.setLength(0);
				if (this.checkValid(bk.getBb(), 10)) {
					sb.append(fPath).append(bk.getBb().toLowerCase()).append(".jpg");
					json.append("BB", sb.toString());
				} else {
					json.append("BB", "/images/s.gif");
				}
				sb.setLength(0);
				if (this.checkValid(bk.getBc(), 10)) {
					sb.append(fPath).append(bk.getBc().toLowerCase()).append(".jpg");
					json.append("BC", sb.toString());
				} else {
					json.append("BC", "/images/s.gif");
				}
				sb.setLength(0);
				if (this.checkValid(bk.getBd(), 10)) {
					sb.append(fPath).append(bk.getBd().toLowerCase()).append(".jpg");
					json.append("BD", sb.toString());
				} else {
					json.append("BD", "/images/s.gif");
				}
				json.append("STATE", bk.getState());
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}

	public String list() {
		StringBuilder sb = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		int state = this.getInt("State", -1);
		if (state >= 0) {
			sql.append(" AND State=?");
			fs.add(state);
		}
		String key = this.getString("key");
		if (key != null) {
			int pos = key.indexOf('-');
			int end = (key.length() - 1);
			if (key.charAt(0) == '+') {
				sql.append(" AND Mobile LIKE ?");
				fs.add(sb.append('%').append(key.substring(1)).append('%').toString());
			} else if (key.charAt(0) == '=') {
				sql.append(" AND Time<=? AND Time>=?");
				fs.add(GMTime.getTime(key, 1, 0, 1));
				fs.add(GMTime.getTime(key, 1, 0));
			} else if (pos == 0) {
				sql.append(" AND Time<=?");
				fs.add(GMTime.getTime(key, 1, 0));
			} else if (end == pos) {
				sql.append(" AND Time>=?");
				fs.add(GMTime.getTime(key, 0, pos));
			} else if (pos >= 1) {
				sql.append(" AND Time<=? AND Time>=?");
				fs.add(GMTime.getTime(key, (pos + 1), 0));
				fs.add(GMTime.getTime(key, 0, pos));
			} else if (StringUtils.isNumber(key)) {
				sql.append(" AND Uid=?");
				fs.add(Long.parseLong(key));
			} else {
				sql.append(" AND NAME LIKE ?");
				fs.add(sb.append('%').append(key).append('%').toString());
			}
		} // 排序信息
		sb.setLength(0);
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Sid")) {
			sb.append("Sid");
			sort = "Sid";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Sid")) {
			if (dir == null || dir.equalsIgnoreCase("DESC")) {
				sb.append(" DESC");
			} else {
				sb.append(" ASC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("DESC")) {
			sb.append(" DESC,Sid DESC");
		} else {
			sb.append(" ASC,Sid ASC");
		} // 加载数据信息
		this.setAjaxInfo(this.getUserBankService().findBankByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			long sid = this.getLong("sid");
			UserBker bk = this.getUserBankService().findBkerBySid(sid);
			if (bk == null) {
				json.addError(this.getText("system.error.none"));
			} else if (STATE_CHECK == bk.getState()) {
				UserSession us = this.getUserSession();
				if (getInt("state") == 2) {
					bk.setState(STATE_READER);
				} else {
					bk.setState(STATE_ERRORS);
				}
				bk.setAdM(us.getUserId());
				bk.setAdN(us.getUserName());
				this.getUserBankService().update(bk);
				json.addMessage(this.getText("data.update.succeed"));
			} else {
				json.addError(this.getText("system.error.pars"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.update.failed"));
		}
		return JSON;
	}

	/** 导出信息 */
	public String export() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		String suffix = this.getString("Suffix");
		ExportInfo info = new ExportInfo(suffix);
		try {
			if (ids == null) {
				json.addError(getText("system.error.export"));
				// } else if (ids.equalsIgnoreCase("out")) {
				// this.getUserCashService().exportByOut(info);
				// json.addMessage(info.getFileName());
			} else {
				System.out.println(info.getSid());
				json.addError(getText("system.error.export"));
			}
			// } catch (SQLException | IOException e) {
			json.addError(this.getText("system.error.info"));
		} finally {
			ids = suffix = null;
		}
		return JSON;
	}
}
