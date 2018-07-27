package com.ypm.cfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.commons.lang.StringUtils;
import com.ypm.Action;
import com.ypm.bean.*;
import com.ypm.service.UserCashService;
import com.ypm.util.GMTime;
import com.ypm.util.VeKey;

public class UserAtCash extends Action {

	private static final long serialVersionUID = 3126856825834550147L;
	
	private static final Logger LOGGER = Logger.getLogger(UserAtCash.class);

	private UserCashService userCashService;

	public UserCashService getUserCashService() {
		return userCashService;
	}

	public void setUserCashService(UserCashService userCashService) {
		this.userCashService = userCashService;
	}

	public String gather() {
		AjaxInfo json = this.getAjaxInfo();
		StringBuilder sb = new StringBuilder();
		try {
			StringBuilder sql = new StringBuilder();
			List<Object> fs = new ArrayList<Object>();
			int state = this.getInt("State", -1);
			if (state >= 0) {
				Map<String, String> ms = this.getDictBySid(VeKey.CFO_CASH_STATE);
				sb.append('【').append(ms.get(String.valueOf(state))).append('】');
				sql.append(" AND State=?");
				fs.add(state);
			} 
			// 查询条件信息
			String key = this.getString("key");
			if (key != null) {
				int pos = key.indexOf('-');
				int end = (key.length() - 1);
				if (key.charAt(0) == '+') {
					sql.append(" AND Mobile LIKE ?");
					fs.add(sb.append('%').append(key.substring(1)).append('%').toString());
					sb.append('【').append(key.substring(1)).append('】');
				} else if (key.charAt(0) == '=') {
					sql.append(" AND Time<=? AND Time>=?");
					fs.add(GMTime.getTime(key, 1, 0, 1));
					fs.add(GMTime.getTime(key, 1, 0));
					sb.append('【').append(key.substring(1)).append('】');
				} else if (pos == 0) {
					sql.append(" AND Time<=?");
					fs.add(GMTime.getTime(key, 1, 0));
					sb.append('【').append("截止").append(key.substring(1)).append('】');
				} else if (end == pos) {
					sql.append(" AND Time>=?");
					fs.add(GMTime.getTime(key, 0, pos));
					sb.append('【').append(key).append("至今").append('】');
				} else if (pos >= 1) {
					sql.append(" AND Time<=? AND Time>=?");
					fs.add(GMTime.getTime(key, (pos + 1), 0));
					fs.add(GMTime.getTime(key, 0, pos));
					sb.append('【').append(key).append('】');
				} else if (StringUtils.isNumber(key)) {
					sql.append(" AND Uid=?");
					fs.add(Long.parseLong(key));
					sb.append("UID=").append('【').append(key).append('】');
				} else {
					sql.append(" AND NAME LIKE ?");
					sb.append("用户名").append('【').append(key).append('】');
					fs.add('%' + key+'%');
				}
			} else if (sb.length() <= 0) {
				sb.append("ALL");
			}
			json.data(API_OK);
			json.append("SKEY", sb.toString());
			this.getUserCashService().loadCashBySum(json, sql, fs);
		} catch (SQLException e) {
			LOGGER.info( "enter gather exception..." );
			json.addError(this.getText("system.error.info"));
		} finally {
			sb.setLength(0);
		}
		return JSON;
	}

	/**
	 * @return string 
	 * 
	 * 用户提现数据列表
	 */
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
		} 
		// 排序信息
		sb.setLength(0);
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Sid")) {
			sb.append("Sid");
			sort = "Sid";
		} else {
			sb.append(sort);
		} 
		// 加载二次排序
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
		} 
		// 加载数据信息
		this.setAjaxInfo(this.getUserCashService().findCashByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	/**
	 * @return string
	 * 
	 * 用户提现-同意
	 */
	public String agree() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString( "Ids" );

		try {
			UserSession us = this.getUserSession();
			this.getUserCashService().agree( us, ids );
			json.addMessage(this.getText( "data.update.succeed" ));
		} catch (Exception e) {
			LOGGER.info( "同意用户提现失败，异常信息：" + e.getMessage() );
			json.addError(this.getText( "data.update.failed" ));
		} finally {
			ids = null;
		}
		
		return JSON;
	}
	
	/**
	 * @return string 
	 * 
	 * 用户提现-拒绝
	 */
	public String refuse() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			long sid = this.getLong( "sid" );
			UserCash c = this.getUserCashService().findCashBySid(sid);
			if (c == null) {
				json.addError(this.getText( "system.error.none" ));
			} else if (c.getState() >= STATE_ERRORS) {
				json.addError(this.getText( "cfo.error.201" ));
			} else if (c.getState() == STATE_READER) {
				json.addError(this.getText( "cfo.error.202" ));
			} else {
				UserSession us = this.getUserSession();
				this.getUserCashService().refuse(us, c);
				json.addMessage(this.getText( "data.update.succeed" ));
			}
		} catch (Exception e) {
			LOGGER.info( "拒绝用户提现失败，异常信息：" + e.getMessage() );
			json.addError(this.getText( "data.update.failed" ));
		}
		
		return JSON;
	}

	/**
	 * @return string 
	 * 
	 * 用户提现记录导出
	 */
	public String export() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		String suffix = this.getString("Suffix");
		ExportInfo info = new ExportInfo(suffix);
		try {
			if (ids == null) {
				json.addError(getText("system.error.export"));
			} else if (ids.equalsIgnoreCase("out")) {
				this.getUserCashService().exportByOut(info);
				json.addMessage(info.getFileName());
			} else {
				json.addError(getText("system.error.export"));
			}
		} catch (SQLException | IOException e) {
			LOGGER.info( "enter export exp..." );
			json.addError(this.getText("system.error.info"));
		} finally {
			ids = suffix = null;
		}
		return JSON;
	}
}
