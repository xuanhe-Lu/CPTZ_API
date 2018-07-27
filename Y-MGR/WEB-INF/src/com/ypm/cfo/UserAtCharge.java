package com.ypm.cfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.commons.lang.StringUtils;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.service.UserChargeService;
import com.ypm.util.GMTime;
import com.ypm.util.VeKey;

public class UserAtCharge extends Action {

	private static final long serialVersionUID = 7027976920575512548L;

	private UserChargeService userChargeService;

	public UserChargeService getUserChargeService() {
		return userChargeService;
	}

	public void setUserChargeService(UserChargeService userChargeService) {
		this.userChargeService = userChargeService;
	}

	public String gather() {
		AjaxInfo json = this.getAjaxInfo();
		StringBuilder sb = new StringBuilder();
		try {
			StringBuilder sql = new StringBuilder();
			List<Object> fs = new ArrayList<Object>();
			int state = this.getInt("State", -1);
			if (state >= 0) {
				Map<String, String> ms = this.getDictBySid(VeKey.CFO_CHARGE_STATE);
				sb.append('【').append(ms.get(String.valueOf(state))).append('】');
				sql.append(" AND State=?");
				fs.add(state);
			} // 查询条件信息
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
					fs.add('%' + key + '%');
				}
			} else if (sb.length() <= 0) {
				sb.append("ALL");
			}
			json.data(API_OK);
			json.append("SKEY", sb.toString());
			this.getUserChargeService().loadOrderBySum(json, sql, fs);
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		} finally {
			sb.setLength(0);
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
		this.setAjaxInfo(this.getUserChargeService().findOrderByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}
}
