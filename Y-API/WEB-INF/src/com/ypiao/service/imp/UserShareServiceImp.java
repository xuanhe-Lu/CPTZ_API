package com.ypiao.service.imp;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import org.commons.lang.TimeUtils;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.data.JPrepare;
import com.ypiao.service.UserShareService;
import com.ypiao.util.GMTime;

public class UserShareServiceImp extends AConfig implements UserShareService {

	private static String SQL_BY_RMBS;

	protected void checkSQL() {
		SQL_BY_RMBS =  JPrepare.getQuery("SELECT Sid,Uid,Fid,Adds,Time FROM user_rmbs WHERE Uid=? AND Tid=5", 0, "inner", "SELECT Uid,Account FROM user_info", "A.Fid=B.Uid ORDER BY A.Sid DESC");
	}

	public void  sendByAll(AjaxInfo json, long uid, int state) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int num = 0; // 邀请人数
			BigDecimal sy = BigDecimal.ZERO;
			ps = conn.prepareStatement("SELECT Uid,MF,NF FROM user_status WHERE Uid=?");
			ps.setLong(1, uid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				sy = rs.getBigDecimal(2);
				num = rs.getInt(3); // 人数
			}
			json.datas(API_OK);
			SimpleDateFormat sdf = TimeUtils.get("yyyy-MM-dd HH:mm");
			if (state == 1) {
				rs.close();
				ps.close();
				ps = conn.prepareStatement(SQL_BY_RMBS);
				ps.setLong(1, uid);
				rs = ps.executeQuery();
				while (rs.next()) {
					json.formater();
					json.appends("time", GMTime.format(rs.getLong(5), GMTime.CHINA, sdf));
					toStar(json.getBuilder("mobile"), rs.getString(7));
					json.append("money", DF2.format(rs.getFloat(4)));
				}
				rs.close();
			} else if (num >= 1) {
				rs.close();
				ps.close();
				num = 0; // 重新计算数量
				ps = conn.prepareStatement("SELECT Uid,Mobile,Rtime,NP FROM user_status WHERE UPS=? ORDER BY Rtime DESC");
				ps.setLong(1, uid);
				rs = ps.executeQuery();
				while (rs.next()) {
					json.formater();
					json.appends("time", GMTime.format(rs.getLong(3), GMTime.CHINA, sdf));
					toStar(json.getBuilder("mobile"), rs.getString(2));
					if (rs.getInt(4) >= 1) {
						json.append("state", 1);
					} else {
						json.append("state", 0);
					}
					num += 1;
				}
				rs.close();
			}
			json.close().add("obj");
			json.append("uid", uid);
			json.append("mf", DF2.format(sy));
			json.append("nf", num);
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
