package com.ypm.service.imp;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.*;
import com.ypm.util.*;
import org.apache.log4j.Logger;

public class UserOrderServiceImp extends AConfig implements UserOrderService {
	private static Logger logger = Logger.getLogger(UserOrderServiceImp.class);
	private UserInfoService userInfoService;

	private UserMoneyService userMoneyService;

	protected void checkSQL() {
	}

	public UserInfoService getUserInfoService() {
		return userInfoService;
	}

	public void setUserInfoService(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	public UserMoneyService getUserMoneyService() {
		return userMoneyService;
	}

	public void setUserMoneyService(UserMoneyService userMoneyService) {
		this.userMoneyService = userMoneyService;
	}

	public int adds(Connection conn, LogOrder log) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_LOG_ORDER + " SET Pid=?,Cid=?,Name=?,GmtA=?,GmtB=?,GmtC=?,GmtD=?,GURL=?,State=?,Time=? WHERE Sid=?");
		try {
			ps.setLong(1, log.getPid());
			ps.setLong(2, log.getCid());
			ps.setString(3, log.getName());
			ps.setLong(4, log.getGmtA());
			ps.setLong(5, log.getGmtB());
			ps.setLong(6, log.getGmtC());
			ps.setLong(7, log.getGmtD());
			ps.setString(8, log.getUrl());
			ps.setInt(9, log.getState());
			ps.setLong(10, log.getTime());
			ps.setLong(11, log.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close(); // Add OK
				ps = conn.prepareStatement("INSERT INTO " + Table.TBL_LOG_ORDER + " (Sid,Uid,Pid,Cid,Tid,Name,Rate,Rday,Any,Day,Way,Ads,TMA,TMB,TMC,TMD,TME,TMF,TMG,YMA,YMB,GmtA,GmtB,GmtC,GmtD,GURL,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, log.getSid());
				ps.setLong(2, log.getUid());
				ps.setLong(3, log.getPid());
				ps.setLong(4, log.getCid());
				ps.setInt(5, log.getTid());
				ps.setString(6, log.getName());
				ps.setBigDecimal(7, log.getRate());
				ps.setInt(8, log.getRday());
				ps.setInt(9, log.getAny());
				ps.setInt(10, log.getDay());
				ps.setString(11, log.getWay());
				ps.setBigDecimal(12, log.getAds());
				ps.setBigDecimal(13, log.getTma());
				ps.setBigDecimal(14, log.getTmb());
				ps.setBigDecimal(15, log.getTmc());
				ps.setBigDecimal(16, log.getTmd());
				ps.setBigDecimal(17, log.getTme());
				ps.setBigDecimal(18, log.getTmf());
				ps.setBigDecimal(19, log.getTmg());
				ps.setBigDecimal(20, log.getYma());
				ps.setBigDecimal(21, log.getYmb());
				ps.setLong(22, log.getGmtA());
				ps.setLong(23, log.getGmtB());
				ps.setLong(24, log.getGmtC());
				ps.setLong(25, log.getGmtD());
				ps.setString(26, log.getUrl());
				ps.setInt(27, log.getState());
				ps.setLong(28, log.getTime());
				return ps.executeUpdate();
			} // Not Add
			return 0;
		} finally {
			ps.close();
		}
	}

	public int addsYpiao(Connection conn, LogOrder log) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE ypiao.log_order SET Pid=?,Cid=?,Name=?,GmtA=?,GmtB=?,GmtC=?,GmtD=?,GURL=?,State=?,Time=? WHERE Sid=?");
		try {
			ps.setLong(1, log.getPid());
			ps.setLong(2, log.getCid());
			ps.setString(3, log.getName());
			ps.setLong(4, log.getGmtA());
			ps.setLong(5, log.getGmtB());
			ps.setLong(6, log.getGmtC());
			ps.setLong(7, log.getGmtD());
			ps.setString(8, log.getUrl());
			ps.setInt(9, log.getState());
			ps.setLong(10, log.getTime());
			ps.setLong(11, log.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close(); // Add OK
				ps = conn.prepareStatement("INSERT INTO ypiao.log_order (Sid,Uid,Pid,Cid,Tid,Name,Rate,Rday,Any,Day,Way,Ads,TMA,TMB,TMC,TMD,TME,TMF,TMG,YMA,YMB,GmtA,GmtB,GmtC,GmtD,GURL,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, log.getSid());
				ps.setLong(2, log.getUid());
				ps.setLong(3, log.getPid());
				ps.setLong(4, log.getCid());
				ps.setInt(5, log.getTid());
				ps.setString(6, log.getName());
				ps.setBigDecimal(7, log.getRate());
				ps.setInt(8, log.getRday());
				ps.setInt(9, log.getAny());
				ps.setInt(10, log.getDay());
				ps.setString(11, log.getWay());
				ps.setBigDecimal(12, log.getAds());
				ps.setBigDecimal(13, log.getTma());
				ps.setBigDecimal(14, log.getTmb());
				ps.setBigDecimal(15, log.getTmc());
				ps.setBigDecimal(16, log.getTmd());
				ps.setBigDecimal(17, log.getTme());
				ps.setBigDecimal(18, log.getTmf());
				ps.setBigDecimal(19, log.getTmg());
				ps.setBigDecimal(20, log.getYma());
				ps.setBigDecimal(21, log.getYmb());
				ps.setLong(22, log.getGmtA());
				ps.setLong(23, log.getGmtB());
				ps.setLong(24, log.getGmtC());
				ps.setLong(25, log.getGmtD());
				ps.setString(26, log.getUrl());
				ps.setInt(27, log.getState());
				ps.setLong(28, log.getTime());
				return ps.executeUpdate();
			} // Not Add
			return 0;
		} finally {
			ps.close();
		}
	}

	public void save(LogOrder log) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.adds(conn, log);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void saveAuto(LogOrder log) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			conn.setAutoCommit(false);
			UserRmbs r = this.getUserMoneyService().findMoneyByUid(conn, log.getUid());
			r.setSid(log.getSid() + 100); // 回款
			r.setTid(APState.TRADE_ADD5);
			r.setWay("理财回款");
			r.setEvent("回款：" + log.getName());
			r.add(log.getYma());
			r.setTime(log.getTime());
			if (this.getUserMoneyService().update(conn, r) >= 1) {
				// Ignored
			} else if (this.getUserMoneyService().insert(conn, r) >= 1) {
				BigDecimal sy = log.getYma().subtract(log.getTmb());
				this.getUserInfoService().updateAddTZ(conn, r.getUid(), log.getYma(), sy, log.getTime());
			}
			this.adds(conn, log);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(conn);
		}
	}

	public void updateAny() throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			long time = (GState.USER_TODAX - 1);
			ps = JPrepare.prepareStatement(conn, "SELECT Sid,Uid,Any,Day,GmtC,GmtD,State,Time FROM " + Table.TBL_LOG_ORDER + " WHERE State<=? AND GmtD<=?");
			ps.setInt(1, SALE_A3); // 回款中
			ps.setLong(2, time);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				long gmtC = rs.getLong(5);
				if (GState.USER_TODAX >= gmtC) {
					rs.updateInt(3, 0); // 今日回款
					rs.updateLong(6, gmtC);
					rs.updateInt(7, SALE_A3);
				} else {
					rs.updateInt(3, VeRule.toAny(gmtC));
					rs.updateLong(6, GState.USER_TODAX);
					rs.updateInt(7, SALE_A2);
				}
				rs.updateRow();
			}
			rs.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	/** 标的结算处理 */
	public void doReceived() throws SQLException {
		List<LogOrder> ls = new ArrayList<LogOrder>();
		do {
			for (LogOrder log : ls) {
				if (log.getGmtC() > log.getGmtD()) {
					log.setState(SALE_A2);
					this.save(log);
				} else {
					log.setState(SALE_A4); // 已回款
					log.setTime(System.currentTimeMillis());
					this.saveAuto(log); // 自动回款操作
//					SyncMap.getAll().sender(SYS_A851, "saveAuto", log);

					// TODO 修改数据同步方式，改为直接写入，
					logger.info("开始执行数据传输到YPIAO操作");
					this.saveYpiao(log);




				}
			}
			ls.clear();
			this.loadOrder(ls, SALE_A3);
		} while (ls.size() >= 1);
	}

	public void loadOrder(List<LogOrder> ls, int state) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT Sid,Uid,Pid,Cid,Tid,Name,Rate,Rday,Any,Day,Way,ADS,TMA,TMB,TMC,TMD,TME,TMF,TMG,YMA,YMB,GmtA,GmtB,GmtC,GmtD,GURL,State,Time FROM " + Table.TBL_LOG_ORDER + " WHERE state=?");
			ps.setInt(1, state);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				LogOrder log = new LogOrder();
				log.setSid(rs.getLong(1));
				log.setUid(rs.getLong(2));
				log.setPid(rs.getLong(3));
				log.setCid(rs.getLong(4));
				log.setTid(rs.getInt(5));
				log.setName(rs.getString(6));
				log.setRate(rs.getBigDecimal(7));
				log.setRday(rs.getInt(8));
				log.setAny(rs.getInt(9));
				log.setDay(rs.getInt(10));
				log.setWay(rs.getString(11));
				log.setAds(rs.getBigDecimal(12));
				log.setTma(rs.getBigDecimal(13));
				log.setTmb(rs.getBigDecimal(14));
				log.setTmc(rs.getBigDecimal(15));
				log.setTmd(rs.getBigDecimal(16));
				log.setTme(rs.getBigDecimal(17));
				log.setTmf(rs.getBigDecimal(18));
				log.setTmg(rs.getBigDecimal(19));
				log.setYma(rs.getBigDecimal(20));
				log.setYmb(rs.getBigDecimal(21));
				log.setGmtA(rs.getLong(22));
				log.setGmtB(rs.getLong(23));
				log.setGmtC(rs.getLong(24));
				log.setGmtD(rs.getLong(25));
				log.setUrl(rs.getString(26));
				log.setState(rs.getInt(27));
				log.setTime(rs.getLong(28));
				ls.add(log);
			}
			rs.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AutoRaws compute(AutoRaws r) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT SUM(TMA) FROM " + Table.TBL_LOG_ORDER + " WHERE Pid IN (SELECT Pid FROM " + Table.TBL_PROD_INFO + " WHERE Rid=?)");
			ps.setLong(1, r.getRid());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				r.setMd(rs.getBigDecimal(1));
			}
			rs.close();
			if (r.getMd() == null) {
				r.setMd(BigDecimal.ZERO);
				return r; // 无需计算后续
			}
			ps.close(); // 计算占用金额
			ps = conn.prepareStatement("SELECT SUM(TMA) FROM (SELECT Uid,TMA FROM " + Table.TBL_LOG_ORDER + " WHERE Pid IN (SELECT Pid FROM " + Table.TBL_PROD_INFO + " WHERE Rid=?)) A WHERE EXISTS(SELECT Uid FROM " + Table.TBL_USER_INFO + " WHERE Uid=A.Uid AND VIP=?)");
			ps.setLong(1, r.getRid());
			ps.setInt(2, 1); // 内部号
			rs = ps.executeQuery();
			if (rs.next()) {
				r.setMc(rs.getBigDecimal(1));
			}
			rs.close();
			BigDecimal all = r.getMd();
			if (r.getMc() == null) {
				r.setMc(BigDecimal.ZERO);
			} else {
				all = r.getMd().subtract(r.getMc());
			} // 合并计算结果
			r.setMe(all); // 实际打款
			return r;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo findBookByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (fs.size() > 0) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + Table.TBL_SYS_ORDER).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, Table.TBL_SYS_ORDER, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			}
			float f = 0f;
			long out = (System.currentTimeMillis() - 180000); // 3分钟超时
			Map<String, String> ms = this.getDictInfoBySSid(CFO_ORDER_BOOK);
			sql.insert(0, "SELECT Sid,Uid,Cid,Tid,Name,Rate,Rday,Day,Way,Ads,TMA,TMB,TMC,TMD,TME,TMF,TMG,YMA,YMB,GmtA,GmtB,GmtC,Mobile,Nicer,State,Stext,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql.toString(), offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.remove(1));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.formater();
				json.append("SID", rs.getLong(1));
				json.append("UID", rs.getLong(2));
				json.append("CID", rs.getLong(3));
				json.append("TID", rs.getInt(4));
				json.append("NAME", rs.getString(5));
				json.append("RATE", DF2.format(rs.getFloat(6)), "%");
				json.append("RDAY", rs.getInt(7));
				json.append("DAY", rs.getInt(8));
				json.append("WAY", rs.getString(9));
				if ((f = rs.getFloat(10)) > 0) {
					json.append("ADS", DF2.format(f), "%");
				} else {
					json.append("ADS", "-");
				}
				json.append("TMA", DF3.format(rs.getFloat(11)));
				json.append("TMB", DF3.format(rs.getFloat(12)));
				if ((f = rs.getFloat(13)) > 0) {
					json.append("TMC", DF3.format(f));
				} else {
					json.appends("TMC", "-");
				}
				json.append("TMD", DF3.format(rs.getFloat(14)));
				if ((f = rs.getFloat(15)) > 0) {
					json.append("TME", DF2.format(f), "%");
					json.append("TMF", DF3.format(rs.getFloat(16)));
				} else {
					json.appends("TME", "-");
					json.appends("TMF", "-");
				}
				json.append("TMG", DF3.format(rs.getFloat(17)));
				json.append("YMA", DF3.format(rs.getFloat(18)));
				if ((f = rs.getFloat(19)) > 0) {
					json.append("YMB", DF3.format(f));
				} else {
					json.appends("YMB", "-");
				}
				json.append("GMTA", GMTime.format(rs.getLong(20), GMTime.CHINA));
				json.append("GMTB", GMTime.format(rs.getLong(21), GMTime.CHINA, GMTime.OUT_SHORT));
				json.append("GMTC", GMTime.format(rs.getLong(22), GMTime.CHINA, GMTime.OUT_SHORT));
				json.append("MOBILE", rs.getString(23));
				json.append("NICER", rs.getString(24));
				if (rs.getInt(25) == 0) {
					if (rs.getLong(20) > out) {
						json.append("STATE", ms.get("0"));
						json.append("STEXT", rs.getString(26));
					} else {
						json.append("STATE", ms.get("6"));
						json.append("STEXT", "支付超时");
					}
				} else {
					json.append("STATE", ms.get(rs.getString(25)));
					json.append("STEXT", rs.getString(26));
				}
				json.append("TIME", GMTime.format(rs.getLong(27), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo findOrderByAll(StringBuilder sql, List<Object> fs, boolean join, String order, int offset, int max) {
		if (fs.size() > 0) {
			sql.replace(1, 4, "WHERE");
		} // add table
		if (join) {
			sql.insert(0, "FROM " + Table.TBL_LOG_ORDER + " A INNER JOIN " + Table.TBL_USER_STATUS + " B ON A.Uid=B.Uid");
			fs.add(0, sql.toString());
		} else {
			fs.add(0, sql.insert(0, "FROM " + Table.TBL_LOG_ORDER).toString().replace("A.", ""));
		} // get total
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, Table.TBL_LOG_ORDER, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			}
			float f = 0f;
			Map<String, String> ms = this.getDictInfoBySSid(CFO_ORDER_STATE);
			if (join) {
				sql.insert(0, "SELECT A.Sid,A.Uid,A.Cid,A.Tid,A.Name,A.Rate,A.Rday,A.Any,A.Day,A.Way,A.TMA,A.TMB,A.TMC,A.TMD,A.TME,A.TMF,A.TMG,A.YMA,A.YMB,A.GmtA,A.GmtB,A.GmtC,A.State,A.Time,B.Uid,B.Mobile,B.Name ").append(" ORDER BY ").append(order);
				ps = conn.prepareStatement(JPrepare.getQuery(sql.toString(), offset, max));
			} else {
				sql.insert(0, "SELECT Sid,Uid,Cid,Tid,Name,Rate,Rday,Any,Day,Way,TMA,TMB,TMC,TMD,TME,TMF,TMG,YMA,YMB,GmtA,GmtB,GmtC,State,Time ").append(" ORDER BY ").append(order);
				ps = conn.prepareStatement(JPrepare.getQuery(sql.toString().replace("A.", ""), offset, max, "INNER", "SELECT Uid,Mobile,Name FROM " + Table.TBL_USER_STATUS, "A.Uid=B.Uid"));
			} // object
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.remove(1));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("SID", rs.getLong(1));
				json.append("UID", rs.getLong(2));
				json.append("CID", rs.getLong(3));
				json.append("TID", rs.getInt(4));
				json.append("MOBILE", rs.getString(26));
				json.append("NICER", rs.getString(27));
				json.append("NAME", rs.getString(5));
				json.append("RATE", DF2.format(rs.getFloat(6)), "%");
				json.append("RDAY", rs.getInt(7));
				json.append("ANY", rs.getInt(8));
				json.append("DAY", rs.getInt(9));
				json.append("WAY", rs.getString(10));
				json.append("TMA", DF3.format(rs.getFloat(11)));
				json.append("TMB", DF3.format(rs.getFloat(12)));
				if ((f = rs.getFloat(13)) > 0) {
					json.append("TMC", DF3.format(f));
				} else {
					json.appends("TMC", "-");
				}
				json.append("TMD", DF3.format(rs.getFloat(14)));
				if ((f = rs.getFloat(15)) > 0) {
					json.append("TME", DF2.format(f), "%");
					json.append("TMF", DF3.format(rs.getFloat(16)));
				} else {
					json.appends("TME", "-");
					json.appends("TMF", "-");
				}
				json.append("TMG", DF3.format(rs.getFloat(17)));
				json.append("YMA", DF3.format(rs.getFloat(18)));
				if ((f = rs.getFloat(19)) > 0) {
					json.append("YMB", DF3.format(f));
				} else {
					json.appends("YMB", "-");
				}
				json.append("GMTA", GMTime.format(rs.getLong(20), GMTime.CHINA));
				json.append("GMTB", GMTime.format(rs.getLong(21), GMTime.CHINA, GMTime.OUT_SHORT));
				json.append("GMTC", GMTime.format(rs.getLong(22), GMTime.CHINA, GMTime.OUT_SHORT));
				json.append("STATE", ms.get(rs.getString(23)));
				json.append("TIME", GMTime.format(rs.getLong(24), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	@Override
	public void saveYpiao(LogOrder log) throws SQLException {
		logger.info("come in saveYpiao");
		Connection conn = JPrepare.getConnection();
		try {
			conn.setAutoCommit(false);
			logger.info("come in saveYpiao");
			UserRmbs r = this.getUserMoneyService().findYpiaoMoneyByUid(conn, log.getUid());
			r.setSid(log.getSid() + 100); // 回款
			r.setFid(0);
			r.setTid(APState.TRADE_ADD5);
			r.setWay("理财回款");
			r.setEvent("回款：" + log.getName());
			r.add(log.getYma());
			r.setTime(log.getTime());
			logger.info("come in updateYpiao");
			if (this.getUserMoneyService().updateYpiao(conn, r) >= 1) {
				// Ignored
			} else if (this.getUserMoneyService().insertYpiao(conn, r) >= 1) {
				BigDecimal sy = log.getYma().subtract(log.getTmb());
				this.getUserInfoService().updateYpiaoAddTZ(conn, r.getUid(), log.getYma(), sy, log.getTime());
			}
			logger.info("come in addsYpiao");
			this.addsYpiao(conn, log);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(conn);
		}


		//END LUXH
	}
}
