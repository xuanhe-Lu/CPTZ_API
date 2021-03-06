package com.ypm.service.imp;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Map;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.*;
import com.ypm.util.GMTime;
import com.ypm.util.GState;

public class UserInfoServiceImp extends AConfig implements UserInfoService {

	private static final String TBL_USER_INFO = "user_info", TBL_USER_STATUS = "user_status";

	private TriggerService triggerService;

	private UserIderService userIderService;

	private UserRoleService userRoleService;

	protected void checkSQL() {
	}

	public TriggerService getTriggerService() {
		return triggerService;
	}

	public void setTriggerService(TriggerService triggerService) {
		this.triggerService = triggerService;
	}

	public UserIderService getUserIderService() {
		return userIderService;
	}

	public void setUserIderService(UserIderService userIderService) {
		this.userIderService = userIderService;
	}

	public UserRoleService getUserRoleService() {
		return userRoleService;
	}

	public void setUserRoleService(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	private boolean add(Connection conn, UserInfo info) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_INFO + " SET UPS=?,VIP=?,IOS=?,Account=?,UserName=?,Fta=?,Ftb=?,Facer=?,Gender=?,Gider=?,Nicer=?,Binds=?,Reals=?,Rtime=?,State=?,Login=?,Time=? WHERE Uid=?");
		try {
			ps.setLong(1, info.getUPS());
			ps.setInt(2, info.getVIP());
			ps.setInt(3, info.getIOS());
			ps.setString(4, info.getAccount());
			ps.setString(5, info.getUsername());
			ps.setString(6, info.getFta());
			ps.setString(7, info.getFtb());
			ps.setInt(8, info.getFacer());
			ps.setInt(9, info.getGender());
			ps.setString(10, info.getGider());
			ps.setString(11, info.getNicer());
			ps.setInt(12, info.getBinds());
			ps.setInt(13, info.getReals());
			ps.setLong(14, info.getRtime());
			ps.setInt(15, info.getState());
			ps.setLong(16, info.getLogin());
			ps.setLong(17, info.getTime());
			ps.setLong(18, info.getUid());
			if (ps.executeUpdate() <= 0) {
				ps.close(); // Add UserInfo
				this.getUserIderService().update(conn, info.getUid(), info.getAccount(), info.getTime());
				ps = conn.prepareStatement("INSERT INTO " + TBL_USER_INFO + " (Sid,Uid,UPS,VIP,IOS,Account,UserName,PassWord,Fta,Ftb,Facer,Gender,Gider,Nicer,Binds,Reals,Rtime,State,Login,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setString(1, info.getSid());
				ps.setLong(2, info.getUid());
				ps.setLong(3, info.getUPS());
				ps.setInt(4, info.getVIP());
				ps.setInt(5, info.getIOS());
				ps.setString(6, info.getAccount());
				ps.setString(7, info.getUsername());
				ps.setString(8, info.getPassword());
				ps.setString(9, info.getFta());
				ps.setString(10, info.getFtb());
				ps.setInt(11, info.getFacer());
				ps.setInt(12, info.getGender());
				ps.setString(13, info.getGider());
				ps.setString(14, info.getNicer());
				ps.setInt(15, info.getBinds());
				ps.setInt(16, info.getReals());
				ps.setLong(17, info.getRtime());
				ps.setInt(18, info.getState());
				ps.setLong(19, info.getLogin());
				ps.setLong(20, info.getTime());
				ps.executeUpdate();
				return true;
			}
			return false;
		} finally {
			ps.close();
		}
	}

	private void save(Connection conn, UserStatus s) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_STATUS + " SET UPS=?,VIP=?,Pay=?,Cid=?,Name=?,Mobile=?,IdCard=?,BkId=?,BkName=?,BkInfo=?,Binds=?,Reals=?,Rinfo=?,Rtels=?,Rtime=?,MA=?,MB=?,MC=?,MD=?,ME=?,MF=?,MG=?,NA=?,NB=?,NC=?,NM=?,NP=?,State=?,Time=? WHERE Uid=?");
		try {
			ps.setLong(1, s.getUPS());
			ps.setInt(2, s.getVIP());
			ps.setInt(3, s.getPay());
			ps.setInt(4, s.getCid());
			ps.setString(5, s.getName());
			ps.setString(6, s.getMobile());
			ps.setString(7, s.getIdCard());
			ps.setInt(8, s.getBkId());
			ps.setString(9, s.getBkName());
			ps.setString(10, s.getBkInfo());
			ps.setInt(11, s.getBinds());
			ps.setInt(12, s.getReals());
			ps.setString(13, s.getRinfo());
			ps.setString(14, s.getRtels());
			ps.setLong(15, s.getRtime());
			ps.setBigDecimal(16, s.getMa());
			ps.setBigDecimal(17, s.getMb());
			ps.setBigDecimal(18, s.getMc());
			ps.setBigDecimal(19, s.getMd());
			ps.setBigDecimal(20, s.getMe());
			ps.setBigDecimal(21, s.getMf());
			ps.setBigDecimal(22, s.getMg());
			ps.setInt(23, s.getNa());
			ps.setInt(24, s.getNb());
			ps.setInt(25, s.getNc());
			ps.setInt(26, s.getNm());
			ps.setInt(27, s.getNp());
			ps.setInt(28, s.getState());
			ps.setLong(29, s.getTime());
			ps.setLong(30, s.getUid());
			if (ps.executeUpdate() <= 0) {
				ps.close(); // Add UserStatus
				ps = conn.prepareStatement("INSERT INTO " + TBL_USER_STATUS + " (Uid,UPS,VIP,Pay,Cid,Name,Mobile,IdCard,BkId,BkName,BkInfo,Binds,Reals,Rinfo,Rtels,Rtime,MA,MB,MC,MD,ME,MF,MG,NA,NB,NC,NM,NP,GmtA,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, s.getUid());
				ps.setLong(2, s.getUPS());
				ps.setInt(3, s.getVIP());
				ps.setInt(4, s.getPay());
				ps.setInt(5, s.getCid());
				ps.setString(6, s.getName());
				ps.setString(7, s.getMobile());
				ps.setString(8, s.getIdCard());
				ps.setInt(9, s.getBkId());
				ps.setString(10, s.getBkName());
				ps.setString(11, s.getBkInfo());
				ps.setInt(12, s.getBinds());
				ps.setInt(13, s.getReals());
				ps.setString(14, s.getRinfo());
				ps.setString(15, s.getRtels());
				ps.setLong(16, s.getRtime());
				ps.setBigDecimal(17, s.getMa());
				ps.setBigDecimal(18, s.getMb());
				ps.setBigDecimal(19, s.getMc());
				ps.setBigDecimal(20, s.getMd());
				ps.setBigDecimal(21, s.getMe());
				ps.setBigDecimal(22, s.getMf());
				ps.setBigDecimal(23, s.getMg());
				ps.setInt(24, s.getNa());
				ps.setInt(25, s.getNb());
				ps.setInt(26, s.getNc());
				ps.setInt(27, s.getNm());
				ps.setInt(28, s.getNp());
				ps.setLong(29, s.getTime());
				ps.setInt(30, s.getState());
				ps.setLong(31, s.getTime());
				ps.executeUpdate();
			}
		} finally {
			ps.close();
		}
	}

	public void save(UserInfo info) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.add(conn, info);
		} finally {
			JPrepare.close(conn);
		}
	}

	private void init(UserReger reg, boolean sync) {
		try {
			UserStatus s = new UserStatus();
			s.setCid(reg.getCid());
			s.setUid(reg.getUid());
			s.setUPS(reg.getUPS());
			s.setVIP(reg.getVIP());
			s.setMobile(reg.getAccount());
			s.setBinds(reg.getBinds());
			s.setReals(reg.getReals());
			s.setRinfo(reg.getChannel());
			s.setRtels(reg.getRemark());
			s.setRtime(reg.getRtime());
			s.setState(reg.getState());
			s.setTime(reg.getTime());
			UserProfile a = new UserProfile();
			a.setUid(reg.getUid());
			a.setSid(reg.getSid());
			a.setCid(reg.getCid());
			a.setDevice(reg.getDevice());
			a.setMobile(reg.getAccount());
			a.setModel(reg.getModel());
			a.setRelease(reg.getRelease());
			a.setSdk(reg.getSdk());
			a.setToken(reg.getToken());
			a.setRaddr(reg.getRemote());
			a.setRtime(reg.getRtime());
			a.setTime(reg.getRtime());
			this.initProfile(s, a); // 扩展信息
			if (sync) {
				SyncMap.getAll().sender(SYS_A120, "reg", reg);
				this.getTriggerService().register(reg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initProfile(UserStatus s, UserProfile a) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			this.save(conn, s); // 扩展信息
			ps = conn.prepareStatement("UPDATE user_profile SET Sid=?,Cid=?,Device=?,Mobile=?,Model=?,Rels=?,Sdk=?,Token=?,RAddr=?,RTime=?,Time=? WHERE Uid=?");
			ps.setString(1, a.getSid());
			ps.setLong(2, a.getCid());
			ps.setString(3, a.getDevice());
			ps.setString(4, a.getMobile());
			ps.setString(5, a.getModel());
			ps.setString(6, a.getRelease());
			ps.setInt(7, a.getSdk());
			ps.setString(8, a.getToken());
			ps.setString(9, a.getRaddr());
			ps.setLong(10, a.getRtime());
			ps.setLong(11, a.getTime());
			ps.setLong(12, a.getUid());
			if (ps.executeUpdate() <= 0) {
				ps.close(); // add Profile
				ps = conn.prepareStatement("INSERT INTO user_profile (Uid,Sid,Cid,Device,Mobile,Model,Rels,Sdk,Token,RAddr,RTime,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, a.getUid());
				ps.setString(2, a.getSid());
				ps.setLong(3, a.getCid());
				ps.setString(4, a.getDevice());
				ps.setString(5, a.getMobile());
				ps.setString(6, a.getModel());
				ps.setString(7, a.getRelease());
				ps.setInt(8, a.getSdk());
				ps.setString(9, a.getToken());
				ps.setString(10, a.getRaddr());
				ps.setLong(11, a.getRtime());
				ps.setLong(12, a.getTime());
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void reg(UserReger reg) throws SQLException {
		boolean result = false;
		Connection conn = JPrepare.getConnection();
		try {
			result = add(conn, reg);
		} finally {
			JPrepare.close(conn);
		}
		if (result) {
			this.init(reg, false);
		}
	}

	public int update(Connection conn, UserAuth a) throws SQLException {
		int binds = 0, reals = 0, pay = 1;
		if (a.getBtime() > USER_TIME) {
			binds = 1;
		}
		if (a.getRtime() > USER_TIME) {
			reals = 1;
		}
		if (a.getPays() == null || a.getPays().length() != 32) {
			pay = 0; // 支付密码
		} // 更新用户数据信息
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_INFO + " SET Gender=?,Binds=?,Reals=?,Time=? WHERE Uid=?");
		try {
			ps.setInt(1, a.getGender());
			ps.setInt(2, binds);
			ps.setInt(3, reals);
			ps.setLong(4, a.getTime());
			ps.setLong(5, a.getUid());
			if (ps.executeUpdate() >= 1) {
				ps.close();
				ps = conn.prepareStatement("UPDATE " + TBL_USER_STATUS + " SET Pay=?,Name=?,IdCard=?,Binds=?,Reals=?,Time=? WHERE Uid=?");
				ps.setInt(1, pay);
				ps.setString(2, a.getName());
				ps.setString(3, a.getIdCard());
				ps.setInt(4, binds);
				ps.setInt(5, reals);
				ps.setLong(6, a.getTime());
				ps.setLong(7, a.getUid());
				return ps.executeUpdate();
			} else {
				return 0;
			}
		} finally {
			ps.close();
		}
	}

	public int update(Connection conn, UserBank b) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_STATUS + " SET BkId=?,BkName=?,BkInfo=?,Time=? WHERE Uid=?");
		try {
			ps.setInt(1, b.getBid());
			ps.setString(2, b.getBankName());
			ps.setString(3, b.getCardNo());
			ps.setLong(4, b.getTime());
			ps.setLong(5, b.getUid());
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

	public int update(Connection conn, long uid, int coupon, long time) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_STATUS + " SET NA=?,Time=? WHERE Uid=?");
		try {
			ps.setInt(1, coupon);
			ps.setLong(2, time);
			ps.setLong(3, uid);
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

	public int updateAddTZ(Connection conn, long uid, BigDecimal rmb, BigDecimal sy, long time) throws SQLException {
		PreparedStatement ps = JPrepare.prepareStatement(conn, "SELECT Uid,MA,MB,MC,MG,NP,Time FROM " + TBL_USER_STATUS + " WHERE Uid=? AND MC>=?");
		try {
			int num = 0;
			ps.setLong(1, uid);
			ps.setBigDecimal(2, rmb);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				BigDecimal mb = rs.getBigDecimal(3).add(rmb);
				BigDecimal mc = rs.getBigDecimal(4).subtract(rmb);
				BigDecimal ma = mb.add(mc); // 总资产
				BigDecimal mg = rs.getBigDecimal(5).add(sy);
				rs.updateBigDecimal(2, ma);
				rs.updateBigDecimal(3, mb);
				rs.updateBigDecimal(4, mc);
				rs.updateBigDecimal(5, mg);
				rs.updateLong(7, time);
				rs.updateRow();
			}
			rs.close();
			return num;
		} finally {
			ps.close();
		}
	}

	public int updateYpiaoAddTZ(Connection conn, long uid, BigDecimal rmb, BigDecimal sy, long time) throws SQLException {
		PreparedStatement ps = JPrepare.prepareStatement(conn, "SELECT Uid,MA,MB,MC,MG,NP,Time FROM ypiao.user_status WHERE Uid=? AND MC>=?");
		try {
			int num = 0;
			ps.setLong(1, uid);
			ps.setBigDecimal(2, rmb);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				BigDecimal mb = rs.getBigDecimal(3).add(rmb);
				BigDecimal mc = rs.getBigDecimal(4).subtract(rmb);
				BigDecimal ma = mb.add(mc); // 总资产
				BigDecimal mg = rs.getBigDecimal(5).add(sy);
				rs.updateBigDecimal(2, ma);
				rs.updateBigDecimal(3, mb);
				rs.updateBigDecimal(4, mc);
				rs.updateBigDecimal(5, mg);
				rs.updateLong(7, time);
				rs.updateRow();
			}
			rs.close();
			return num;
		} finally {
			ps.close();
		}
	}


	public int updateSubTX(Connection conn, long uid, BigDecimal rmb, long time) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_USER_STATUS + " SET MA=(MA-?),MB=(MB-?),NB=(NB+1),NM=(NM+1),Time=? WHERE Uid=? AND MB>=?");
		try {
			ps.setBigDecimal(1, rmb);
			ps.setBigDecimal(2, rmb);
			ps.setLong(3, time);
			ps.setLong(4, uid);
			ps.setBigDecimal(5, rmb);
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

	public int updateSubTZ(Connection conn, long uid, BigDecimal rmb, BigDecimal sy, long time) throws SQLException {
		PreparedStatement ps = JPrepare.prepareStatement(conn, "SELECT Uid,MA,MB,MC,MG,NP,Time FROM " + TBL_USER_STATUS + " WHERE Uid=? AND MB>=?");
		try {
			int num = 0;
			ps.setLong(1, uid);
			ps.setBigDecimal(2, rmb);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				BigDecimal mb = rs.getBigDecimal(3).subtract(rmb);
				BigDecimal mc = rs.getBigDecimal(4).add(sy);
				BigDecimal ma = mb.add(mc); // 总资产
				num = (rs.getInt(6) + 1);
				rs.updateBigDecimal(2, ma);
				rs.updateBigDecimal(3, mb);
				rs.updateBigDecimal(4, mc);
				rs.updateInt(6, num);
				rs.updateLong(7, time);
				rs.updateRow();
			}
			rs.close();
			return num;
		} finally {
			ps.close();
		}
	}

	public int updatePwd(long uid, String Pwd, long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_USER_INFO + " SET PassWord=?,Time=? WHERE Uid=?");
			ps.setString(1, Pwd);
			ps.setLong(2, time);
			ps.setLong(3, uid);
			return ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int updateMonth() throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			long time = (GState.USER_MONTH_BEG + 1000);
			ps = conn.prepareStatement("UPDATE " + TBL_USER_STATUS + " SET NM=?,GmtA=? WHERE GmtA<=?");
			ps.setInt(1, 0);
			ps.setLong(2, time);
			ps.setLong(3, GState.USER_MONTH_BEG);
			return ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo findUserByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (fs.size() > 0) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + TBL_USER_INFO).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, TBL_USER_INFO, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			}
			Map<String, String> ds = this.getDefault();
			Map<String, String> mg = this.getDictInfoBySSid(SYSTEM_USER_GENDER);
			Map<String, String> ms = this.getDictInfoBySSid(USER_INFO_STATE);
			Map<Integer, String> mv = this.getUserRoleService().getVIPByAll();
			sql.insert(0, "SELECT Uid,UPS,VIP,Account,UserName,Fta,Ftb,Facer,Gender,Gider,Nicer,Binds,Reals,Rtime,State,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql.toString(), offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.remove(1));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("UID", rs.getLong(1));
				json.append("UPS", rs.getLong(2));
				json.append("VIP", mv.get(rs.getInt(3)));
				json.append("ACCOUNT", rs.getString(4));
				json.append("USERNAME", rs.getString(5));
				json.append("FTA", rs.getString(6));
				json.append("FTB", rs.getString(7));
				json.append("FACER", rs.getInt(8));
				json.append("GENDER", mg.get(rs.getString(9)));
				json.append("NICER", rs.getString(11));
				json.append("BINDS", ds.get(rs.getString(12)));
				json.append("REALS", ds.get(rs.getString(13)));
				json.append("RTIME", GMTime.format(rs.getLong(14), GMTime.CHINA));
				json.append("STATE", ms.get(rs.getString(15)));
				json.append("TIME", GMTime.format(rs.getLong(16), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	private UserInfo findInfoByUid(Connection conn, long uid) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("SELECT Sid,Uid,UPS,VIP,Account,UserName,PassWord,Fta,Ftb,Facer,Gender,Gider,Nicer,Binds,Reals,Rtime,State,Time FROM " + TBL_USER_INFO + " WHERE Uid=?");
		try {
			UserInfo info = null;
			ps.setLong(1, uid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				info = new UserInfo();
				info.setSid(rs.getString(1));
				info.setUid(rs.getLong(2));
				info.setUPS(rs.getLong(3));
				info.setVIP(rs.getInt(4));
				info.setAccount(rs.getString(5));
				info.setUsername(rs.getString(6));
				info.setPassword(rs.getString(7));
				info.setFta(rs.getString(8));
				info.setFtb(rs.getString(9));
				info.setFacer(rs.getInt(10));
				info.setGender(rs.getInt(11));
				info.setGider(rs.getString(12));
				info.setNicer(rs.getString(13));
				info.setBinds(rs.getInt(14));
				info.setReals(rs.getInt(15));
				info.setRtime(rs.getLong(16));
				info.setState(rs.getInt(17));
				info.setTime(rs.getLong(18));
			}
			rs.close();
			return info;
		} finally {
			ps.close();
		}
	}

	public UserInfo findUserByUid(long uid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			return findInfoByUid(conn, uid);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void updatePwd(long uid, String Pwd) throws SQLException {
		long time = System.currentTimeMillis();
		if (this.updatePwd(uid, Pwd, time) >= 1) {
			SyncMap.getAll().add("uid", uid).add("pwd", Pwd).add("time", time).sender(SYS_A123, "modPwd");
		}
	}
}
