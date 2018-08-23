package com.ypm.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.SyncMap;
import com.ypm.bean.UserBank;
import com.ypm.bean.UserBker;
import com.ypm.data.JPrepare;
import com.ypm.service.UserBankService;
import com.ypm.service.UserInfoService;
import com.ypm.util.GMTime;
import com.ypm.util.Table;

public class UserBankServiceImp extends AConfig implements UserBankService {

	private static final String TBL_USER_BANK = "user_bank", TBL_USER_BKER = "user_bker";

	private UserInfoService userInfoService;

	protected void checkSQL() {
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	private void save(Connection conn, UserBank b) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_BANK + " SET Uid=?,Bid=?,Code=?,BankId=?,BankName=?,BinId=?,BinStat=?,CardName=?,CardTy=?,Channel=?,Mobile=?,Name=?,GmtA=?,GmtB=?,State=?,Time=? WHERE CNo=?");
		try {
			ps.setLong(1, b.getUid());
			ps.setInt(2, b.getBid());
			ps.setString(3, b.getCode());
			ps.setString(4, b.getBankId());
			ps.setString(5, b.getBankName());
			ps.setInt(6, b.getBinId());
			ps.setInt(7, b.getBinStat());
			ps.setString(8, b.getCardName());
			ps.setString(9, b.getCardTy());
			ps.setString(10, b.getChannel());
			ps.setString(11, b.getMobile());
			ps.setString(12, b.getName());
			ps.setLong(13, b.getGmtA());
			ps.setLong(14, b.getGmtB());
			ps.setInt(15, b.getState());
			ps.setLong(16, b.getTime());
			ps.setString(17, b.getCardNo());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_USER_BANK + " (Uid,Bid,CNo,Code,BankId,BankName,BinId,BinStat,CardName,CardTy,Channel,Mobile,Name,GmtA,GmtB,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, b.getUid());
				ps.setInt(2, b.getBid());
				ps.setString(3, b.getCardNo());
				ps.setString(4, b.getCode());
				ps.setString(5, b.getBankId());
				ps.setString(6, b.getBankName());
				ps.setInt(7, b.getBinId());
				ps.setInt(8, b.getBinStat());
				ps.setString(9, b.getCardName());
				ps.setString(10, b.getCardTy());
				ps.setString(11, b.getChannel());
				ps.setString(12, b.getMobile());
				ps.setString(13, b.getName());
				ps.setLong(14, b.getGmtA());
				ps.setLong(15, b.getGmtB());
				ps.setInt(16, b.getState());
				ps.setLong(17, b.getTime());
				ps.executeUpdate();
			}
		} finally {
			ps.close();
		}
	}

	private void save(Connection conn, UserBker b) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_BKER + " SET Bid=?,CNo=?,Code=?,Mobile=?,Name=?,BA=?,BB=?,BC=?,BD=?,State=?,Time=?,adM=?,adN=? WHERE Sid=?");
		try {
			ps.setInt(1, b.getBid());
			ps.setString(2, b.getCNo());
			ps.setString(3, b.getCode());
			ps.setString(4, b.getMobile());
			ps.setString(5, b.getName());
			ps.setString(6, b.getBa());
			ps.setString(7, b.getBb());
			ps.setString(8, b.getBc());
			ps.setString(9, b.getBd());
			ps.setInt(10, b.getState());
			ps.setLong(11, b.getTime());
			ps.setLong(12, b.getAdM());
			ps.setString(13, b.getAdN());
			ps.setLong(14, b.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close(); // Add
				ps = conn.prepareStatement("INSERT INTO " + TBL_USER_BKER + " (Sid,Uid,Bid,CNo,Code,Mobile,Name,BA,BB,BC,BD,State,Time,adM,adN) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, b.getSid());
				ps.setLong(2, b.getUid());
				ps.setInt(3, b.getBid());
				ps.setString(4, b.getCNo());
				ps.setString(5, b.getCode());
				ps.setString(6, b.getMobile());
				ps.setString(7, b.getName());
				ps.setString(8, b.getBa());
				ps.setString(9, b.getBb());
				ps.setString(10, b.getBc());
				ps.setString(11, b.getBd());
				ps.setInt(12, b.getState());
				ps.setLong(13, b.getTime());
				ps.setLong(14, b.getAdM());
				ps.setString(15, b.getAdN());
				ps.executeUpdate();
			}
		} finally {
			ps.close();
		}
	}

	public void save(UserBank b) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, b);
			if (STATE_READER == b.getState()) {
				this.getUserInfoService().update(conn, b);
			}
		} finally {
			JPrepare.close(conn);
		}
	}

	public void save(UserBker b) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, b);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void used(UserBker b) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			this.save(conn, b);
			String bkName = null;
			ps = JPrepare.prepareStatement(conn, "SELECT Uid,Bid,CNo,BankName,GmtA,GmtB,Gdef,State,Time FROM " + TBL_USER_BANK + " WHERE CNo=?");
			ps.setString(1, b.getCNo());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int def = 0;
				if (STATE_READER == b.getState()) {
					bkName = rs.getString(4);
					def = 1;
				} // 更新信息
				rs.updateLong(6, b.getTime());
				rs.updateInt(7, def);
				rs.updateInt(8, b.getState());
				rs.updateLong(9, b.getTime());
				rs.updateRow();
			}
			rs.close();
			if (bkName != null) {
				ps.close();
				ps = conn.prepareStatement("UPDATE " + TBL_USER_BANK + " SET Gdef=? WHERE Uid=? AND CNo<>?");
				ps.setInt(1, 0);
				ps.setLong(2, b.getUid());
				ps.setString(3, b.getCNo());
				ps.executeUpdate();
				ps.close();
				ps = conn.prepareStatement("UPDATE " + Table.TBL_USER_STATUS + " SET BkId=?,BkName=?,BkInfo=?,Time=? WHERE Uid=?");
				ps.setInt(1, b.getBid());
				ps.setString(2, bkName);
				ps.setString(3, b.getCNo());
				ps.setLong(4, b.getTime());
				ps.setLong(5, b.getUid());
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(conn);
		}
	}

	public AjaxInfo findBankByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (fs.size() > 0) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + TBL_USER_BKER).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, TBL_USER_BKER, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			}
			long adm = 0; // 管理UID
			Map<String, String> ms = this.getDictInfoBySSid(CFO_CASH_STATE);
			sql.insert(0, "SELECT Sid,Uid,Bid,CNo,Code,Mobile,Name,State,Time,adM,adN ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql.toString(), offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.remove(1));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("SID", rs.getLong(1));
				json.append("UID", rs.getLong(2));
				if ((adm = rs.getInt(3)) > 0) {
					json.append("BID", adm);
				} else {
					json.append("BID", "-");
				}
				json.append("CNO", rs.getString(4));
				json.append("CODE", rs.getString(5));
				json.append("MOBILE", rs.getString(6));
				json.append("NAME", rs.getString(7));
				json.append("STATE", ms.get(rs.getString(8)));
				json.append("TIME", GMTime.format(rs.getLong(9), GMTime.CHINA));
				if ((adm = rs.getLong(10)) >= USER_IDS) {
					json.append("ADM", adm);
					json.append("ADN", rs.getString(11));
				} else {
					json.append("ADM", "");
					json.appends("ADN", "-");
				}
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public UserBker findBkerBySid(long sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			UserBker b = null;
			ps = conn.prepareStatement("SELECT Sid,Uid,Bid,CNo,Code,Mobile,Name,BA,BB,BC,BD,State,Time,adM,adN FROM " + TBL_USER_BKER + " WHERE Sid=?");
			ps.setLong(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				b = new UserBker();
				b.setSid(rs.getLong(1));
				b.setUid(rs.getLong(2));
				b.setBid(rs.getInt(3));
				b.setCNo(rs.getString(4));
				b.setCode(rs.getString(5));
				b.setMobile(rs.getString(6));
				b.setName(rs.getString(7));
				b.setBa(rs.getString(8));
				b.setBb(rs.getString(9));
				b.setBc(rs.getString(10));
				b.setBd(rs.getString(11));
				b.setState(rs.getInt(12));
				b.setTime(rs.getLong(13));
				b.setAdM(rs.getLong(14));
				b.setAdN(rs.getString(15));
			}
			rs.close();
			return b;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void update(UserBker b) throws SQLException {
		b.setTime(System.currentTimeMillis());
		this.used(b); // 保存数据信息
		SyncMap.getAll().sender(SYS_A125, "saveUsed", b);
	}
}
