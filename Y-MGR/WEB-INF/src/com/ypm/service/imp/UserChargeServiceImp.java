package com.ypm.service.imp;

import java.sql.*;
import java.util.List;
import java.util.Map;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.UserChargeService;
import com.ypm.util.APState;
import com.ypm.util.GMTime;

public class UserChargeServiceImp extends AConfig implements APState, UserChargeService {

	private static final String TBL_LOG_CHARGE = "log_charge", TBL_USER_PROTO = "user_proto";

	protected void checkSQL() {
	}

	private void save(Connection conn, LogCharge c) throws SQLException {
		PreparedStatement ps = JPrepare.prepareStatement(conn, "SELECT Sid,Uid,Name,Mobile,IdCard,BankName,BankCard,BackUrl,Amount,Signtp,Signpay,OrderId,ResCode,ResMsg,Vercd,HSIP,State,Time FROM " + TBL_LOG_CHARGE + " WHERE Sid=?");
		try {
			ps.setLong(1, c.getSid());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (c.getVercd() == null) {
					c.setVercd(rs.getString(15));
				} // 根据状态处理
				if (ORDER_SUCCESS == rs.getInt(17)) {
					rs.updateString(15, c.getVercd());
				} else {
					rs.updateLong(2, c.getUid());
					rs.updateString(3, c.getName());
					rs.updateString(4, c.getMobile());
					rs.updateString(5, c.getIdCard());
					rs.updateString(6, c.getBankName());
					rs.updateString(7, c.getBankCard());
					rs.updateString(8, c.getBackUrl());
					rs.updateBigDecimal(9, c.getAmount());
					rs.updateString(10, c.getSigntp());
					rs.updateString(11, c.getSignpay());
					rs.updateString(12, c.getOrderId());
					rs.updateString(13, c.getRes_code());
					rs.updateString(14, c.getRes_msg());
					rs.updateString(15, c.getVercd());
					rs.updateString(16, c.getHSIP());
					rs.updateInt(17, c.getState());
					rs.updateLong(18, c.getTime());
				}
				rs.updateRow();
			} else {
				rs.moveToInsertRow();
				rs.updateLong(1, c.getSid());
				rs.updateLong(2, c.getUid());
				rs.updateString(3, c.getName());
				rs.updateString(4, c.getMobile());
				rs.updateString(5, c.getIdCard());
				rs.updateString(6, c.getBankName());
				rs.updateString(7, c.getBankCard());
				rs.updateString(8, c.getBackUrl());
				rs.updateBigDecimal(9, c.getAmount());
				rs.updateString(10, c.getSigntp());
				rs.updateString(11, c.getSignpay());
				rs.updateString(12, c.getOrderId());
				rs.updateString(13, c.getRes_code());
				rs.updateString(14, c.getRes_msg());
				rs.updateString(15, c.getVercd());
				rs.updateString(16, c.getHSIP());
				rs.updateInt(17, c.getState());
				rs.updateLong(18, c.getTime());
				rs.insertRow();
			}
			rs.close();
		} finally {
			ps.close();
		}
	}

	public void save(LogCharge c) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, c);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void save(UserProto p) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_USER_PROTO + " SET Uid=?,SNO=?,UserId=?,OrderId=?,Merorder=?,Total=?,State=?,Time=? WHERE CNo=?");
			ps.setLong(1, p.getUid());
			ps.setString(2, p.getSNo());
			ps.setString(3, p.getUserId());
			ps.setString(4, p.getOrderId());
			ps.setString(5, p.getMerorder());
			ps.setInt(6, p.getTotal());
			ps.setInt(7, p.getState());
			ps.setLong(8, p.getTime());
			ps.setString(9, p.getCNo());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_USER_PROTO + " (Uid,CNo,SNO,UserId,OrderId,Merorder,Total,State,Time) VALUES (?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, p.getUid());
				ps.setString(2, p.getCNo());
				ps.setString(3, p.getSNo());
				ps.setString(4, p.getUserId());
				ps.setString(5, p.getOrderId());
				ps.setString(6, p.getMerorder());
				ps.setInt(7, p.getTotal());
				ps.setInt(8, p.getState());
				ps.setLong(9, p.getTime());
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int update(String CNo, int state, long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_USER_PROTO + " SET State=?,Time=? WHERE CNo=?");
			ps.setInt(1, state);
			ps.setLong(2, time);
			ps.setString(3, CNo);
			return ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo findOrderByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (fs.size() > 0) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + TBL_LOG_CHARGE).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, TBL_LOG_CHARGE, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			}
			Map<String, String> ms = this.getDictInfoBySSid(CFO_CHARGE_STATE);
			sql.insert(0, "SELECT Sid,Uid,Name,Mobile,IdCard,BankName,BankCard,Amount,Signtp,OrderId,ResCode,ResMsg,Vercd,HSIP,State,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql.toString(), offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.remove(1));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("SID", rs.getLong(1));
				json.append("UID", rs.getLong(2));
				json.append("NAME", rs.getString(3));
				json.append("MOBILE", rs.getString(4));
				json.append("IDCARD", rs.getString(5));
				json.append("BANKNAME", rs.getString(6));
				json.append("BANKCARD", rs.getString(7));
				json.append("AMOUNT", DF3.format(rs.getDouble(8)));
				json.append("SIGNTP", rs.getString(9));
				json.append("ORDERID", rs.getString(10));
				json.append("RESCODE", rs.getString(11));
				json.append("RESMSG", rs.getString(12));
				json.append("VERCD", rs.getString(13));
				json.append("HSIP", rs.getString(14));
				json.append("STATE", ms.get(rs.getString(15)));
				json.append("TIME", GMTime.format(rs.getLong(16), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo loadOrderBySum(AjaxInfo json, StringBuilder sql, List<Object> fs) throws SQLException {
		if (fs.size() > 0) {
			sql.replace(1, 4, "WHERE");
		} // get total
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			sql.insert(0, "SELECT SUM(Amount) FROM " + TBL_LOG_CHARGE);
			ps = conn.prepareStatement(sql.toString());
			for (Object obj : fs) {
				ps.setObject(index++, obj);
			}
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				json.append("INFO", DF3.format(rs.getDouble(1)), "元");
			} else {
				json.append("INFO", "-");
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

}
