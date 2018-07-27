package com.ypm.fjs.um;

import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AdminInfo;
import com.ypm.bean.AjaxInfo;
import com.ypm.service.AdminDeptService;
import com.ypm.service.AdminInfoService;
import com.ypm.util.VeStr;

public class AdminManage extends Action {

	private static final long serialVersionUID = 1103687213413536404L;

	private AdminDeptService adminDeptService;

	private AdminInfoService adminInfoService;

	public AdminDeptService getAdminDeptService() {
		return adminDeptService;
	}

	public void setAdminDeptService(AdminDeptService adminDeptService) {
		this.adminDeptService = adminDeptService;
	}

	public AdminInfoService getAdminInfoService() {
		return adminInfoService;
	}

	public void setAdminInfoService(AdminInfoService adminInfoService) {
		this.adminInfoService = adminInfoService;
	}

	public String getDept() {
		this.setAjaxInfo(this.getAdminDeptService().getDetpChildrens());
		return JSON;
	}

	public String getInfo() {
		AjaxInfo json = this.getAjaxInfo();
		AdminInfo a = this.getAdminInfoService().findAdminInfoByUid(getLong("Uid"));
		if (a == null) {
			json.addError(this.getText("system.error.none"));
		} else {
			json.data();
			json.append("UID", a.getUserId());
			json.append("USERNO", a.getUserNo());
			json.append("LOGINNAME", a.getUserName());
			json.append("USERNAME", a.getRealName());
			json.append("ISSUPER", a.getAdmin());
			json.append("JOBID", a.getJob());
			json.append("ORGID", a.getOrg());
			json.append("DEPTID", a.getDept());
			json.append("MENUID", a.getMenu());
		}
		return JSON;
	}

	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		String dept = this.getString("DEPT");
		String key = this.getString("KEY");
		try {
			sql.append(" AND State<=?");
			fs.add(2); // 正常账号
			if (dept != null) {
				sql.append(" AND Dept=?");
				fs.add(dept);
			} // key
			StringBuilder sb = new StringBuilder();
			if (key != null) {
				sql.append(" AND (UserName LIKE ? OR RealName LIKE ?)");
				sb.append('%').append(key).append('%');
				key = sb.toString();
				sb.setLength(0);
				fs.add(key);
				fs.add(key);
			} // 加载排序规则
			String dir = this.getDir();
			String sort = this.getSort();
			if (sort == null || sort.equalsIgnoreCase("UID")) {
				sb.append("UserId"); sort="Uid";
			} else if (sort.equalsIgnoreCase("USNO")) {
				sb.append("UserNo"); // 编号
			} else if (sort.equalsIgnoreCase("LOGINNAME")) {
				sb.append("UserName");
			} else if (sort.equalsIgnoreCase("USERNAME")) {
				sb.append("RealName");
			} else if (sort.equalsIgnoreCase("MENUID")) {
				sb.append("Menu");
			} else if (sort.equalsIgnoreCase("DEPTID")) {
				sb.append("Dept");
			} else if (sort.equalsIgnoreCase("JOBID")) {
				sb.append("Job");
			} else if (sort.equalsIgnoreCase("ORGID")) {
				sb.append("Org");
			} else if (sort.equalsIgnoreCase("STATUS")) {
				sb.append("State");
			} else {
				sb.append("UserId"); sort="Uid";
			} // 加载页次信息
			if (sort.equalsIgnoreCase("Uid")) {
				if (dir == null || dir.equalsIgnoreCase("ASC")) {
					sb.append(" ASC");
				} else {
					sb.append(" DESC");
				}
			} else if (dir == null || dir.equalsIgnoreCase("ASC")) {
				sb.append(" ASC,UserId ASC");
			} else {
				sb.append(" DESC,UserId ASC");
			} // 查询用户信息
			this.setAjaxInfo(this.getAdminInfoService().findAdminInfo(sql, fs, sb.toString(), getStart(), getLimit()));
			return JSON;
		} finally {
			dept = key = null;
		}
	}

	public String add() {
		AjaxInfo json = this.getAjaxInfo();
		String username = this.getString("LOGINNAME");
		try {
			if (username == null) {
				json.addError(this.getText("user.error.001"));
				return JSON;
			} // Checker PassWord
			String repwd = this.getString("REPWD");
			if (repwd == null) repwd = "none";
			String password = this.getString("PASSWORD");
			if (password == null) password = "none";
			if (!password.equals(repwd)) {
				json.addError(this.getText("user.error.012"));
				return JSON;
			} // Check userName
			if (this.getAdminInfoService().isAdminByUserName(username)) {
				json.addError(this.getText("user.error.002", new String[]{username}));
				return JSON;
			} // Check userId
			String no = this.getString("USERNO");
			if (no != null && this.getAdminInfoService().isAdminByUserNo(no)) {
				json.addError(this.getText("user.error.018", new String[]{no}));
			} else {
				AdminInfo info = new AdminInfo();
				info.setUserNo(no);
				info.setUserName(username);
				info.setPassWord(VeStr.MD5(password));
				info.setRealName(getString("USERNAME"));
				info.setMenu(getString("MENUID"));
				info.setDept(getString("DEPTID"));
				info.setJob(getString("JOBID"));
				info.setOrg(getString("ORGID"));
				info.setState(STATE_DISABLE);
				info.setAdmin(getInt("ISSUPER"));
				this.getAdminInfoService().saveInfo(info);
				json.addMessage(this.getText("data.save.succeed"));
			}
		} catch (Exception e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			username = null;
		}
		return JSON;
	}

	public String updateUser() {
		AjaxInfo json = this.getAjaxInfo();
		String no = this.getString("USERNO");
		String username = this.getString("LOGINNAME");
		try {
			if (username == null) {
				json.addError(this.getText("user.error.001"));
				return JSON;
			}
			long uid = this.getLong("Uid");
			AdminInfo info = this.getAdminInfoService().findAdminInfoByUid(uid);
			if (info == null) {
				json.addError(this.getText("system.error.none"));
			} else if (!info.getUserName().equalsIgnoreCase(username) && this.getAdminInfoService().isAdminByUserName(username)) {
				json.addError(this.getText("user.error.002", new String[]{username}));
			} else if (info.getUserNo().equalsIgnoreCase(no) || no == null || !this.getAdminInfoService().isAdminByUserNo(no)) {
				info.setUserName(username);
				if (no != null) info.setUserNo(no);
				info.setRealName(getString("USERNAME"));
				info.setMenu(getString("MENUID"));
				info.setDept(getString("DEPTID"));
				info.setJob(getString("JOBID"));
				info.setOrg(getString("ORGID"));
				info.setAdmin(getInt("ISSUPER"));
				this.getAdminInfoService().saveInfo(info);
				json.addMessage(this.getText("data.update.succeed"));
			} else {
				json.addError(this.getText("user.error.018", new String[]{no}));
			}
		} catch (Exception e) {
			json.addError(this.getText("data.update.failed"));
		} finally {
			no = username = null;
		}
		return JSON;
	}

	public String updatePwd() {
		AjaxInfo json = this.getAjaxInfo();
		String repwd = this.getString("REPWD");
		String password = this.getString("PWORD");
		try {
			if (password == null) {
				json.addError(this.getText("user.error.011"));
			} else if (!password.equals(repwd)) {
				json.addError(this.getText("user.error.012"));
			} else {
				this.getAdminInfoService().updatePwd(getLong("Uid"), VeStr.MD5(repwd));
				json.addMessage(this.getText("data.update.succeed"));
			}
		} catch (Exception e) {
			json.addError(this.getText("data.update.failed"));
		} finally {
			repwd = password = null;
		}
		return JSON;
	}

	public String state() {
		AjaxInfo json = this.getAjaxInfo();
		long uid = this.getLong("Uid");
		try {
			if (this.getUserSession().getUserId() == uid) {
				json.addError(this.getText("user.error.041"));
			} else {
				this.getAdminInfoService().update(uid, getInt("State"));
				json.addMessage(this.getText("data.update.succeed"));
			}
		} catch (Exception e) {
			json.addError(this.getText("data.update.failed"));
		}
		return JSON;
	}

	public String delete() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			long uid = this.getUserSession().getUserId();
			if (ids == null || ids.length() < 5) {
				json.addError(this.getText("system.error.pars"));
			} else if (ids.equalsIgnoreCase(String.valueOf(uid))) {
				json.addError(this.getText("user.error.043"));
			} else {
				this.getAdminInfoService().delete(uid, ids);
				json.addMessage(this.getText("data.delete.succeed"));
			}
		} catch (Exception e) {
			json.addError(this.getText("data.delete.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

}
