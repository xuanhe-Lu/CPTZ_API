package com.ypm.cfo;

import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.service.UserOrderService;

public class UserAtBook extends Action {

	private static final long serialVersionUID = -1996999981207468435L;

	private UserOrderService userOrderService;

	public UserOrderService getUserOrderService() {
		return userOrderService;
	}

	public void setUserOrderService(UserOrderService userOrderService) {
		this.userOrderService = userOrderService;
	}

	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		int tid = this.getInt("State", -1);
		if (tid >= 0) {
			sql.append(" AND State=?");
			fs.add(tid);
		}
		String key = this.getString("key");
		if (key == null) {
			// Ignored
		} else {
			sql.append(" AND NAME LIKE ?");
			fs.add('%' + key + '%');
		} // 排序信息
		StringBuilder sb = new StringBuilder();
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
		this.setAjaxInfo(this.getUserOrderService().findBookByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}
}
