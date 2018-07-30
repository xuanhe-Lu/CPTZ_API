package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.ypiao.bean.*;
import com.ypiao.data.JPrepare;
import com.ypiao.service.*;
import com.ypiao.util.GMTime;
import com.ypiao.util.Table;
import com.ypiao.util.VeStr;

public class UserBankServiceImp extends AConfig implements UserBankService {

	private static final String TBL_USER_BANK = "user_bank", TBL_USER_BKER = "user_bker";

	private static String SQL_BY_UID;

	private BankInfoService bankInfoService;

	private TriggerService triggerService;

	private UserAuthService userAuthService;

	private UserInfoService userInfoService;

	protected void checkSQL() {
		SQL_BY_UID = JPrepare.getQuery("SELECT Uid,Bid,CNo,Code,BankId,BankName,BinId,BinStat,CardName,CardTy,Channel,Mobile,Name,GmtA,GmtB,State,Time FROM " + TBL_USER_BANK + " WHERE Uid=?", 0, "INNER", "SELECT Uid,IdCard FROM user_auth", "A.Uid=B.Uid ORDER BY A.Time DESC");
	}

	public BankInfoService getBankInfoService() {
		return bankInfoService;
	}

	public void setBankInfoService(BankInfoService bankInfoService) {
		this.bankInfoService = bankInfoService;
	}

	public TriggerService getTriggerService() {
		return triggerService;
	}

	public void setTriggerService(TriggerService triggerService) {
		this.triggerService = triggerService;
	}

	public UserAuthService getUserAuthService() {
		return userAuthService;
	}

	public void setUserAuthService(UserAuthService userAuthService) {
		this.userAuthService = userAuthService;
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

	public UserBank findBankByCNo(String cardNo) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			UserBank b = null;
			ps = conn.prepareStatement("SELECT Uid,Bid,CNo,Code,BankId,BankName,BinId,BinStat,CardName,CardTy,Channel,Mobile,Name,GmtA,GmtB,State,Time FROM " + TBL_USER_BANK + " WHERE CNo=?");
			ps.setString(1, cardNo);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				b = new UserBank();
				b.setUid(rs.getLong(1));
				b.setBid(rs.getInt(2));
				b.setCardNo(rs.getString(3));
				b.setCode(rs.getString(4));
				b.setBankId(rs.getString(5));
				b.setBankName(rs.getString(6));
				b.setBinId(rs.getInt(7));
				b.setBinStat(rs.getInt(8));
				b.setCardName(rs.getString(9));
				b.setCardTy(rs.getString(10));
				b.setChannel(rs.getString(11));
				b.setMobile(rs.getString(12));
				b.setName(rs.getString(13));
				b.setGmtA(rs.getLong(14));
				b.setGmtB(rs.getLong(15));
				b.setState(rs.getInt(16));
				b.setTime(rs.getLong(17));
			}
			rs.close();
			return b;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public UserBank findBankByUid(long uid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			UserBank b = null;
			ps = conn.prepareStatement(SQL_BY_UID);
			ps.setLong(1, uid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				b = new UserBank();
				b.setUid(rs.getLong(1));
				b.setBid(rs.getInt(2));
				b.setCardNo(rs.getString(3));
				b.setCode(rs.getString(4));
				b.setBankId(rs.getString(5));
				b.setBankName(rs.getString(6));
				b.setBinId(rs.getInt(7));
				b.setBinStat(rs.getInt(8));
				b.setCardName(rs.getString(9));
				b.setCardTy(rs.getString(10));
				b.setChannel(rs.getString(11));
				b.setMobile(rs.getString(12));
				b.setName(rs.getString(13));
				b.setGmtA(rs.getLong(14));
				b.setGmtB(rs.getLong(15));
				b.setGdef(1); // 默认卡
				b.setState(rs.getInt(16));
				b.setTime(rs.getLong(17));
				b.setIdCard(rs.getString(19));
			}
			rs.close();
			return b;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public UserBker findBkerByUid(long uid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			UserBker b = new UserBker();
			ps = conn.prepareStatement("SELECT Sid,Uid,Bid,CNo,Code,Mobile,Name,BA,BB,BC,BD,State,Time FROM " + TBL_USER_BKER + " WHERE Uid=? AND State<=?");
			ps.setLong(1, uid);
			ps.setInt(2, STATE_CHECK);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
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
			} else {
				b.setSid(VeStr.getUSid());
				b.setUid(uid);
			}
			rs.close();
			return b;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void saveBank(UserBank b) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			b.setTime(GMTime.currentTimeMillis());
			this.save(conn, b); // 保存数据信息
//			SyncMap.getAll().sender(SYS_A125, "save", b);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void saveBker(UserBker b) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			b.setTime(GMTime.currentTimeMillis());
			this.save(conn, b); // 保存数据信息
//			SyncMap.getAll().sender(SYS_A125, "saveBker", b);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void saveBind(UserBank b) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			long time = GMTime.currentTimeMillis();
			b.setGmtB(time); // 完成绑卡
			b.setState(STATE_READER);
			b.setTime(time);
			UserAuth a = this.getUserAuthService().findAuthByUid(conn, b.getUid());
			if (a.getBtime() <= USER_TIME) {
				a.setBtime(b.getGmtB());
				this.execute(() -> {
					try {
						this.getTriggerService().bindCard(b.getUid(), b.getTime());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});
			}
			this.getUserAuthService().save(conn, a); // 认证信息
			this.save(conn, b); // 实名信息
			this.getUserInfoService().update(conn, b);
//			SyncMap.getAll().sender(SYS_A124, "save", a);
//			SyncMap.getAll().sender(SYS_A125, "save", b);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void sendByNew(AjaxInfo json, long uid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			String card = null;
			json.datas(API_OK);
			ps = conn.prepareStatement("SELECT Uid,Bid,CNo,BankName,CardName,Mobile,State FROM " + TBL_USER_BANK + " WHERE Uid=? AND State>=? ORDER BY Time DESC");
			ps.setLong(1, uid);
			ps.setInt(2, STATE_CHECK);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				int bid = rs.getInt(2);
				card = rs.getString(3);
				int j = card.length() - 4;
				json.append("bid", bid);
				json.append("cno", card.substring(j));
				BankInfo b = this.getBankInfoService().getBankByBid(bid);
				if (b == null) {
					json.append("bname", rs.getString(4));
					json.append("cname", rs.getString(5));
					json.append("mobile", rs.getString(6));
					json.append("toall", 0);
					json.append("today", 0);
				} else {
					json.append("bname", b.getNice());
					json.append("cname", rs.getString(5));
					json.append("mobile", rs.getString(6));
					json.append("toall", DF2.format(b.getToall()));
					json.append("today", DF2.format(b.getToday()));
				}
				json.append("state", rs.getInt(7));
			}
			rs.close();
			json.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void sendByUid(AjaxInfo json, long uid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			String card = null;
			json.datas(API_OK);
			ps = conn.prepareStatement("SELECT Uid,Bid,CNo,BankName,CardName,Mobile,State FROM " + TBL_USER_BANK + " WHERE Uid=? AND State=? ORDER BY Time DESC");
			ps.setLong(1, uid);
			ps.setInt(2, STATE_READER);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				int bid = rs.getInt(2);
				card = rs.getString(3);
				int j = card.length() - 4;
				json.append("bid", bid);
				json.append("cno", card.substring(j));
				BankInfo b = this.getBankInfoService().getBankByBid(bid);
				if (b == null) {
					json.append("bname", rs.getString(4));
					json.append("cname", rs.getString(5));
					json.append("mobile", rs.getString(6));
					json.append("toall", 0);
					json.append("today", 0);
				} else {
					json.append("bname", b.getNice());
					json.append("cname", rs.getString(5));
					json.append("mobile", rs.getString(6));
					json.append("toall", DF2.format(b.getToall()));
					json.append("today", DF2.format(b.getToday()));
				}
				json.append("state", rs.getInt(7));
			}
			rs.close();
			json.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
