package com.ypiao.service.imp;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Set;
import com.ypiao.bean.*;
import com.ypiao.data.JPrepare;
import com.ypiao.service.*;
import com.ypiao.util.Table;
import com.ypiao.util.VeRule;

public class ProdInfoServiceImp extends AConfig implements ProdInfoService {

	private AssetRawService assetRawService;

	private ProdModelService prodModelService;

	protected void checkSQL() {
	}

	public AssetRawService getAssetRawService() {
		return assetRawService;
	}

	public void setAssetRawService(AssetRawService assetRawService) {
		this.assetRawService = assetRawService;
	}

	public ProdModelService getProdModelService() {
		return prodModelService;
	}

	public void setProdModelService(ProdModelService prodModelService) {
		this.prodModelService = prodModelService;
	}

	public void save(ProdInfo info) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + Table.TBL_PROD_INFO + " SET Tid=?,AA=?,AB=?,AC=?,AD=?,AE=?,AF=?,AG=?,AH=?,AI=?,AJ=?,AK=?,AL=?,AM=?,AN=?,AO=?,AP=?,AT=?,AU=?,BA=?,BG=?,CA=?,MA=?,MB=?,MC=?,SA=?,SB=?,Adj=?,Adk=?,Ads=?,GmtB=?,GmtC=?,State=?,Stime=?,Time=? WHERE Pid=?");
			ps.setInt(1, info.getTid());
			ps.setString(2, info.getAa());
			ps.setInt(3, info.getAb());
			ps.setInt(4, info.getAc());
			ps.setInt(5, info.getAd());
			ps.setString(6, info.getAe());
			ps.setString(7, info.getAf());
			ps.setInt(8, info.getAg());
			ps.setInt(9, info.getAh());
			ps.setInt(10, info.getAi());
			ps.setInt(11, info.getAj());
			ps.setInt(12, info.getAk());
			ps.setInt(13, info.getAl());
			ps.setBigDecimal(14, info.getAm());
			ps.setBigDecimal(15, info.getAn());
			ps.setInt(16, info.getAo());
			ps.setInt(17, info.getAp());
			ps.setInt(18, info.getAt());
			ps.setInt(19, info.getAu());
			ps.setString(20, info.getBa());
			ps.setString(21, info.getBg());
			ps.setString(22, info.getCa());
			ps.setBigDecimal(23, info.getMa());
			ps.setBigDecimal(24, info.getMb());
			ps.setBigDecimal(25, info.getMc());
			ps.setInt(26, info.getSa());
			ps.setInt(27, info.getSb());
			ps.setInt(28, info.getAdj());
			ps.setBigDecimal(29, info.getAdk());
			ps.setBigDecimal(30, info.getAds());
			ps.setLong(31, info.getGmtB());
			ps.setLong(32, info.getGmtC());
			ps.setInt(33, info.getState());
			ps.setLong(34, info.getStime());
			ps.setLong(35, info.getTime());
			ps.setLong(36, info.getPid());
			if (ps.executeUpdate() <= 0) {
				ps.close(); // 检测数据
				if (this.getAssetRawService().update(conn, info.getRid(), 1, info.getTime()) >= 1) {
					ps = conn.prepareStatement("INSERT INTO " + Table.TBL_PROD_INFO + " (Pid,Rid,Cid,Tid,AA,AB,AC,AD,AE,AF,AG,AH,AI,AJ,AK,AL,AM,AN,AO,AP,AT,AU,BA,BG,CA,MA,MB,MC,MD,SA,SB,Way,Adj,Adk,Ads,GmtA,GmtB,GmtC,State,Stime,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					ps.setLong(1, info.getPid());
					ps.setLong(2, info.getRid());
					ps.setInt(3, info.getCid());
					ps.setInt(4, info.getTid());
					ps.setString(5, info.getAa());
					ps.setInt(6, info.getAb());
					ps.setInt(7, info.getAc());
					ps.setInt(8, info.getAd());
					ps.setString(9, info.getAe());
					ps.setString(10, info.getAf());
					ps.setInt(11, info.getAg());
					ps.setInt(12, info.getAh());
					ps.setInt(13, info.getAi());
					ps.setInt(14, info.getAj());
					ps.setInt(15, info.getAk());
					ps.setInt(16, info.getAl());
					ps.setBigDecimal(17, info.getAm());
					ps.setBigDecimal(18, info.getAn());
					ps.setInt(19, info.getAo());
					ps.setInt(20, info.getAp());
					ps.setInt(21, info.getAt());
					ps.setInt(22, info.getAu());
					ps.setString(23, info.getBa());
					ps.setString(24, info.getBg());
					ps.setString(25, info.getCa());
					ps.setBigDecimal(26, info.getMa());
					ps.setBigDecimal(27, info.getMb());
					ps.setBigDecimal(28, info.getMc());
					ps.setBigDecimal(29, info.getMd());
					ps.setInt(30, info.getSa());
					ps.setInt(31, info.getSb());
					ps.setString(32, info.getWay());
					ps.setInt(33, info.getAdj());
					ps.setBigDecimal(34, info.getAdk());
					ps.setBigDecimal(35, info.getAds());
					ps.setLong(36, info.getGmtA());
					ps.setLong(37, info.getGmtB());
					ps.setLong(38, info.getGmtC());
					ps.setInt(39, info.getState());
					ps.setLong(40, info.getStime());
					ps.setLong(41, info.getTime());
					ps.executeUpdate();
				}
			}
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void save(List<AutoRaws> fs) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + Table.TBL_PROD_INFO + " SET AU=?,Time=? WHERE Pid=?");
			for (AutoRaws r : fs) {
				ps.setInt(1, SALE_A3);
				ps.setLong(2, r.getTime());
				ps.setLong(3, r.getPid());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int updateAuto(long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + Table.TBL_PROD_INFO + " SET AU=?,Stime=GmtC,State=?,Time=GmtB WHERE AU=? AND State<=? AND Stime<=?");
			ps.setInt(1, SALE_A1);
			ps.setInt(2, STATE_READER);
			ps.setInt(3, SALE_A0);
			ps.setInt(4, STATE_READER);
			ps.setLong(5, time);
			ps.executeUpdate();
			ps.close(); // 系统自动结标
			ps = conn.prepareStatement("UPDATE " + Table.TBL_PROD_INFO + " SET AU=?,Way=?,Stime=GmtC,State=?,Time=GmtC WHERE AU=? AND State=? AND Stime<=? AND Stime>=GmtB");
			ps.setInt(1, SALE_A2);
			ps.setString(2, WAY_TSYS);
			ps.setInt(3, STATE_READER);
			ps.setInt(4, SALE_A1);
			ps.setInt(5, STATE_READER);
			ps.setLong(6, time);
			return ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int update(long Pid, long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + Table.TBL_PROD_INFO + " SET AU=?,Way=?,GmtC=?,State=?,Time=? WHERE Pid=? AND AU=?");
			ps.setInt(1, SALE_A2);
			ps.setString(2, WAY_AUTO);
			ps.setLong(3, time);
			ps.setInt(4, STATE_READER);
			ps.setLong(5, time);
			ps.setLong(6, Pid);
			ps.setInt(7, SALE_A1);
			return ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void update(String ids, long time) throws SQLException {
		Set<Long> set = this.toLong(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + Table.TBL_PROD_INFO + " SET AU=?,Way=?,GmtC=?,State=?,Time=? WHERE Pid=? AND AU=?");
			for (Long Pid : set) {
				ps.setInt(1, SALE_A2);
				ps.setString(2, WAY_USER);
				ps.setLong(3, time);
				ps.setInt(4, STATE_READER);
				ps.setLong(5, time);
				ps.setLong(6, Pid.longValue());
				ps.setInt(7, SALE_A1);
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void update(String ids, int state, long time) throws SQLException {
		boolean onSale = (state == 2); // 上架
		Set<Long> set = this.toLong(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			for (Long Pid : set) {
				if (ps != null) {
					ps.close();
				} // 分条逐步处理
				ps = JPrepare.prepareStatement(conn, "SELECT Pid,AU,MA,MD,GmtB,GmtC,State,Stime,Time FROM " + Table.TBL_PROD_INFO + " WHERE Pid=? AND AU<=?");
				ps.setLong(1, Pid.longValue());
				ps.setInt(2, SALE_A1);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					int au = rs.getInt(2);
					long stime = rs.getLong(6);
					if (onSale) {
						if (SALE_A1 == au) {
							// Ignored
						} else {
							rs.updateInt(2, SALE_A1);
							rs.updateLong(5, time);
							rs.updateInt(7, STATE_READER);
							rs.updateLong(8, stime);
							rs.updateLong(9, time);
							rs.updateRow();
						}
					} else if (SALE_A0 == au) {
						// Ignored
					} else {
						rs.updateInt(2, SALE_A0);
						if (rs.getInt(4) <= 0) {
							rs.updateInt(7, STATE_NORMAL);
						} else {
							rs.updateInt(7, STATE_ERRORS);
						}
						rs.updateLong(8, stime);
						rs.updateLong(9, time);
						rs.updateRow();
					}
				}
				rs.close();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void remove(long Pid, long Rid, long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("DELETE FROM " + Table.TBL_PROD_INFO + " WHERE Pid=? AND State<=? AND MD<=?");
			ps.setLong(1, Pid);
			ps.setInt(2, STATE_CHECK);
			ps.setBigDecimal(3, BigDecimal.ZERO);
			if (ps.executeUpdate() >= 0) {
				ps.close(); // 检测数据
				this.getAssetRawService().update(conn, Rid, 0, time);
			}
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int update(Connection conn, long Pid, BigDecimal amt) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_PROD_INFO + " SET AO=(AO+1),MD=(MD+?) WHERE Pid=? AND MA>=(MD+?)");
		try {
			ps.setBigDecimal(1, amt);
			ps.setLong(2, Pid);
			ps.setBigDecimal(3, amt);
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

	public ProdInfo findProdByPid(long Pid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ProdInfo info = null;
			ps = conn.prepareStatement("SELECT Pid,Rid,Cid,Tid,AA,AB,AC,AD,AE,AF,AG,AH,AI,AJ,AK,AL,AM,AN,AO,AP,AT,AU,BA,BG,CA,MA,MB,MC,MD,SA,SB,Way,Adj,Adk,Ads,GmtA,GmtB,GmtC,State,Stime,Time FROM " + Table.TBL_PROD_INFO + " WHERE Pid=?");
			ps.setLong( 1, Pid );
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				info = new ProdInfo();
				info.setPid(rs.getLong(1));
				info.setRid(rs.getLong(2));
				info.setCid(rs.getInt(3));
				info.setTid(rs.getInt(4));
				info.setAa(rs.getString(5));
				info.setAb(rs.getInt(6));
				info.setAc(rs.getInt(7));
				info.setAd(rs.getInt(8));
				info.setAe(rs.getString(9));
				info.setAf(rs.getString(10));
				info.setAg(rs.getInt(11));
				info.setAh(rs.getInt(12));
				info.setAi(rs.getInt(13));
				info.setAj(rs.getInt(14));
				info.setAk(rs.getInt(15));
				info.setAl(rs.getInt(16));
				info.setAm(rs.getBigDecimal(17));
				info.setAn(rs.getBigDecimal(18));
				info.setAo(rs.getInt(19));
				info.setAp(rs.getInt(20));
				info.setAt(rs.getInt(21));
				info.setAu(rs.getInt(22));
				info.setBa(rs.getString(23));
				info.setBg(rs.getString(24));
				info.setCa(rs.getString(25));
				info.setMa(rs.getBigDecimal(26));
				info.setMb(rs.getBigDecimal(27));
				info.setMc(rs.getBigDecimal(28));
				info.setMd(rs.getBigDecimal(29));
				info.setSa(rs.getInt(30));
				info.setSb(rs.getInt(31));
				info.setWay(rs.getString(32));
				info.setAdj(rs.getInt(33));
				info.setAdk(rs.getBigDecimal(34));
				info.setAds(rs.getBigDecimal(35));
				info.setGmtA(rs.getLong(36));
				info.setGmtB(rs.getLong(37));
				info.setGmtC(rs.getLong(38));
				info.setState(rs.getInt(39));
				info.setStime(rs.getLong(40));
				info.setTime(rs.getLong(41));
			}
			rs.close();
			return info;
		} finally {
			JPrepare.close( ps, conn );
		}
	}

	public void saveOver(long Pid, long time) throws SQLException {
		if (this.update(Pid, time) >= 1) {
			SyncMap.getAll().add("pid", Pid).add("time", time).sender(SYS_A203, "saveAuto");
		}
	}

	public void sendIndex(AjaxInfo json) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			json.add("prod");
			ps = conn.prepareStatement("SELECT Pid,Tid,AA,AE,AJ,AN,BG,MC FROM " + Table.TBL_PROD_INFO + " WHERE AU=? ORDER BY Tid ASC,SA ASC");
			ps.setInt(1, SALE_A1);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				json.append("pid", rs.getLong(1));
				json.append("tid", rs.getInt(2));
				json.append("aa", rs.getString(3));
				json.append("ae", rs.getString(4));
				json.append("aj", rs.getInt(5));
				json.append("an", DF2.format(rs.getFloat(6)));
				json.append("bg", rs.getString(7));
				json.append("mc", DF2.format(rs.getFloat(8)));
			} else {
				json.append("pid", 0);
			}
			rs.close();
			json.close();
		} catch (SQLException e) {
			json.setBody("{}");
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void sendList(AjaxInfo json) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			json.datas(API_OK);
			ps = conn.prepareStatement("SELECT Pid,Tid,AA,AE,AJ,AN,AU,BG,MA,MC,MD FROM " + Table.TBL_PROD_INFO + " WHERE AU=? ORDER BY Tid ASC,SA ASC");
			ps.setInt(1, SALE_A1);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("pid", rs.getLong(1));
				json.append("tid", rs.getInt(2));
				json.append("aa", rs.getString(3));
				json.append("ae", rs.getString(4));
				json.append("aj", rs.getInt(5));
				json.append("an", DF2.format(rs.getFloat(6)));
				json.append("au", rs.getInt(7));
				json.append("bg", rs.getString(8));
				json.append("mc", DF2.format(rs.getFloat(10)));
				BigDecimal ma = rs.getBigDecimal(9);
				BigDecimal md = rs.getBigDecimal(11);
				if (ma.compareTo(md) > 0) {
					json.append("rate", VeRule.toPers(ma, md).intValue());
				} else {
					json.append("rate", 100);
				}
			}
			rs.close();
			json.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void sendSale(AjaxInfo json) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			json.datas(API_OK);
			int f = 0, tid = -1;
			ps = conn.prepareStatement("SELECT Pid,Tid,AA,AE,AJ,AN,AU,BG,MA,MC,MD,Ads FROM " + Table.TBL_PROD_INFO + " WHERE AU=? ORDER BY Tid ASC,SA ASC");
			ps.setInt(1, SALE_A1);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				f = rs.getInt(2);
				if (f > tid) {
					if (tid > 0) {
						json.close();
					}
					json.formater();
					json.append("tid", f);
					ProdModel m = this.getProdModelService().getProdModelByTid(f);
					if (m == null) {
						json.append("name", "分类" + f);
					} else {
						json.append("name", m.getName());
					}
					json.adds("list");
					tid = f;
				}
				json.formater();
				json.append("pid", rs.getLong(1));
				json.append("aa", rs.getString(3));
				json.append("ae", rs.getString(4));
				json.append("aj", rs.getInt(5));
				json.append("an", DF2.format(rs.getFloat(6)));
				json.append("au", rs.getInt(7));
				json.append("bg", rs.getString(8));
				json.append("mc", DF2.format(rs.getFloat(10)));
				BigDecimal ma = rs.getBigDecimal(9);
				BigDecimal md = rs.getBigDecimal(11);
				if (ma.compareTo(md) > 0) {
					json.append("rate", VeRule.toPers(ma, md).intValue());
					//json.append("rate", VeRule.toPers(ma, md).doubleValue());
				} else {
					json.append("rate", 100);
				} // 加息幅度
				json.append("ads", DF2.format(rs.getFloat(12)));
			}
			rs.close();
			json.close();
			System.out.println("json:"+json.toString());
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void sendList(AjaxInfo json, int state) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int au = 0;
			json.datas(API_OK);
			if (SALE_A5 == state) {
				ps = conn.prepareStatement("SELECT Pid,Tid,AA,AE,AJ,AN,AU,BG,MA,MC,MD FROM " + Table.TBL_PROD_INFO + " WHERE AU>=? ORDER BY Pid ASC");
				ps.setInt(1, SALE_A5);
			} else {
				ps = conn.prepareStatement("SELECT Pid,Tid,AA,AE,AJ,AN,AU,BG,MA,MC,MD FROM " + Table.TBL_PROD_INFO + " WHERE AU<=? AND AU>=? ORDER BY Pid ASC");
				ps.setInt(1, SALE_A4);
				ps.setInt(2, SALE_A2);
			}
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("pid", rs.getLong(1));
				json.append("tid", rs.getInt(2));
				json.append("aa", rs.getString(3));
				json.append("ae", rs.getString(4));
				json.append("aj", rs.getInt(5));
				json.append("an", DF2.format(rs.getFloat(6)));
				au = rs.getInt(7); // 销售状态
				json.append("au", au);
				json.append("bg", rs.getString(8));
				json.append("mc", DF2.format(rs.getFloat(10)));
				json.append("rate", 100);
			}
			rs.close();
			json.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo sendByAdj(AjaxInfo json, int adj, int max) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			json.datas(API_OK);
			ps = conn.prepareStatement("SELECT Pid,Tid,AA,AE,AJ,AN,AU,MC FROM " + Table.TBL_PROD_INFO + " WHERE State=? AND AU=? AND Adj=? ORDER BY AJ ASC");
			ps.setInt(1, SALE_A2);
			ps.setInt(2, SALE_A1);
			ps.setInt(3, adj);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("pid", rs.getLong(1));
				json.append("tid", rs.getInt(2));
				json.append("aa", rs.getString(3));
				json.append("ae", rs.getString(4));
				json.append("aj", rs.getInt(5));
				json.append("an", DF2.format(rs.getFloat(6)));
				json.append("mc", DF2.format(rs.getFloat(8)));
				if (index++ >= max) {
					break;
				}
			}
			rs.close();
			return json.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
