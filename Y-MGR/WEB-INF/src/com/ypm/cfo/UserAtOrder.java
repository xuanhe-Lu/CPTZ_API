package com.ypm.cfo;

import java.util.ArrayList;
import java.util.List;
import org.commons.lang.StringUtils;
import com.ypm.Action;
import com.ypm.service.UserOrderService;
import com.ypm.util.AUtils;
import com.ypm.util.GMTime;

public class UserAtOrder extends Action {

	private static final long serialVersionUID = 2018418538125956190L;

	private UserOrderService userOrderService;

	public UserOrderService getUserOrderService() {
		return userOrderService;
	}

	public void setUserOrderService(UserOrderService userOrderService) {
		this.userOrderService = userOrderService;
	}

	public String list() {
		StringBuilder sb = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		int tid = this.getInt("State", -1);
		if (tid >= 0) {
			sql.append(" AND A.State=?");
			fs.add(tid);
		} // 根据内容解析查询条件
		boolean join = false;
		String key = this.getString("key");
		if (key != null) {
			int pos = key.indexOf('-');
			int end = (key.length() - 1);
			if (key.charAt(0) == '+') {
				join = true; // 关联查询
				String str = key.substring(1);
				if (StringUtils.isNumber(str)) {
					sql.append(" AND B.Mobile LIKE ?");
				} else {
					sql.append(" AND B.Name LIKE ?");
				}
				fs.add(sb.append('%').append(str).append('%').toString());
			} else if (key.charAt(0) == '=') {
				int a = AUtils.toInt(key.substring(1));
				if (a >= 2018) {
					sql.append(" AND A.GmtC<=? AND A.GmtC>=?");
					fs.add(GMTime.getTime(key, 1, 0, 1));
					fs.add(GMTime.getTime(key, 1, 0));
				} else {
					sql.append(" AND A.Day=?");
					fs.add(a); // 贴息天数
				}
			} else if (pos == 0) {
				sql.append(" AND A.GmtC<=?");
				fs.add(GMTime.getTime(key, 1, 0));
			} else if (end == pos) {
				sql.append(" AND A.GmtB>=?");
				fs.add(GMTime.getTime(key, 0, pos));
			} else if (pos >= 1) {
				int a = AUtils.toInt(key.substring(0, pos));
				if (a >= 2018) {
					sql.append(" AND A.GmtA<=? AND A.GmtA>=?");
					fs.add(GMTime.getTime(key, (pos + 1), 0));
					fs.add(GMTime.getTime(key, 0, pos));
				} else {
					sql.append(" AND A.Rday<=? AND A.Rday>=?");
					fs.add(AUtils.toInt(key.substring(pos + 1)));
					fs.add(a);
				}
			} else if (StringUtils.isNumber(key)) {
				long a = Long.parseLong(key);
				if (a >= USER_UID_BEG) {
					sql.append(" AND A.Uid=?");
				} else {
					sql.append(" AND A.Any=?");
				}
				fs.add(a);
			} else {
				sql.append(" AND A.NAME LIKE ?");
				fs.add(sb.append('%').append(key).append('%').toString());
			}
		} // 排序信息
		sb.setLength(0);
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Sid")) {
			sb.append("A.Sid");
			sort = "Sid";
		} else if (sort.equalsIgnoreCase("mobile")) {
			sb.append("B.Mobile");
			join = true;
		} else if (sort.equalsIgnoreCase("nicer")) {
			sb.append("B.Name");
			join = true;
		} else {
			sb.append("A.").append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Sid")) {
			if (dir == null || dir.equalsIgnoreCase("DESC")) {
				sb.append(" DESC");
			} else {
				sb.append(" ASC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("DESC")) {
			sb.append(" DESC,A.Sid DESC");
		} else {
			sb.append(" ASC,A.Sid ASC");
		} // 加载数据信息
		this.setAjaxInfo(this.getUserOrderService().findOrderByAll(sql, fs, join, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	public String export() {
		this.getAjaxInfo().addError(getText("system.error.export"));
		return JSON;
	}
}
