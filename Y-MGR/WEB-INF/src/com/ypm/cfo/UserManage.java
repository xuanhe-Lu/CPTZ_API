package com.ypm.cfo;

import java.util.ArrayList;
import java.util.List;
import org.commons.lang.StringUtils;
import com.ypm.Action;
import com.ypm.service.CfoUserService;

public class UserManage extends Action {

	private static final long serialVersionUID = 9028517428746622722L;

	private CfoUserService cfoUserService;

	public CfoUserService getCfoUserService() {
		return cfoUserService;
	}

	public void setCfoUserService(CfoUserService cfoUserService) {
		this.cfoUserService = cfoUserService;
	}

	public String list() {
		StringBuilder sb = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		String key = this.getString("key");
		if (key == null) {
			// Ignored
		} else {
			int sel = this.getInt("Sel", -1);
			switch (sel) {
			case 1: // UID
				sql.append(" AND Uid=?");
				fs.add(Long.parseLong(key));
				break;
			case 2: // 真实姓名
				sql.append(" AND NAME LIKE ?");
				fs.add(sb.append('%').append(key).append('%').toString());
				break;
			case 3: // 手机号码
				sql.append(" AND Mobile LIKE ?");
				fs.add(sb.append('%').append(key.substring(1)).append('%').toString());
				break;
			default:
				if (key.charAt(0) == '-') {
					sql.append(" AND Mobile LIKE ?");
					fs.add(sb.append('%').append(key.substring(1)).append('%').toString());
				} else if (StringUtils.isNumber(key)) {
					sql.append(" AND Uid=?");
					fs.add(Long.parseLong(key));
				} else {
					sql.append(" AND NAME LIKE ?");
					fs.add(sb.append('%').append(key).append('%').toString());
				}
			}
		} // 排序信息
		sb.setLength(0);
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
			sb.append(" ASC,Uid ASC");
		} // 加载数据信息
		this.setAjaxInfo(this.getCfoUserService().findUserByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	public String export() {
		this.getAjaxInfo().addError(getText("system.error.export"));
		return JSON;
	}
}
