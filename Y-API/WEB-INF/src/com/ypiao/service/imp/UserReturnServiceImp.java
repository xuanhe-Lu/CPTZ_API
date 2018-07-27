package com.ypiao.service.imp;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.data.JPrepare;
import com.ypiao.service.UserReturnService;
import com.ypiao.util.GMTime;

public class UserReturnServiceImp extends AConfig implements UserReturnService {

	private static final String TBL_USER_RETURN = "user_return";

	protected void checkSQL() {
	}

	public AjaxInfo sendByAll(AjaxInfo json, long uid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			BigDecimal all = null;
			ps = conn.prepareStatement("SELECT Uid,SUM(YMC) FROM " + TBL_USER_RETURN + " WHERE Uid=?");
			ps.setLong(1, uid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				all = rs.getBigDecimal(2);
			}
			rs.close();
			json.addObject();
			if (all == null) {
				json.append("all", "0.00");
			} else {
				json.append("all", DF2.format(all));
			}
			ps.close();
			ps = conn.prepareStatement("SELECT Pid FROM prod_info WHERE State=? AND AU=? AND Adj=? ORDER BY AJ ASC");
			ps.setInt(1, 2);
			ps.setInt(2, 1);
			ps.setInt(3, 1); // 518活动
			rs = ps.executeQuery();
			if (rs.next()) {
				json.append("ida", rs.getLong(1));
				if (rs.next()) {
					json.append("idb", rs.getLong(1));
				} else {
					json.append("idb", 0);
				}
			} else {
				json.append("ida", 0);
				json.append("idb", 0);
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo sendByUid(AjaxInfo json, long uid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			json.datas(API_OK);
			BigDecimal all = BigDecimal.ZERO;
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
			ps = conn.prepareStatement("SELECT Sid,Uid,Pid,TMA,YMC,State,Time FROM " + TBL_USER_RETURN + " WHERE Uid=? ORDER BY Time DESC");
			ps.setLong(1, uid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				BigDecimal c = rs.getBigDecimal(5);
				json.formater();
				json.append("tma", DF2.format(rs.getDouble(4)));
				json.append("ymc", DF2.format(c));
				json.append("time", GMTime.format(rs.getLong(7), GMTime.CHINA, sdf));
				all = all.add(c); // 增加
			}
			rs.close();
			json.close().add("obj");
			json.append("all", DF2.format(all));
			ps.close();
			ps = conn.prepareStatement("SELECT Pid FROM prod_info WHERE State=? AND AU=? AND Adj=? ORDER BY AJ ASC");
			ps.setInt(1, 2);
			ps.setInt(2, 1);
			ps.setInt(3, 1); // 518活动
			rs = ps.executeQuery();
			if (rs.next()) {
				json.append("ida", rs.getLong(1));
				if (rs.next()) {
					json.append("idb", rs.getLong(1));
				} else {
					json.append("idb", 0);
				}
			} else {
				json.append("ida", 0);
				json.append("idb", 0);
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
