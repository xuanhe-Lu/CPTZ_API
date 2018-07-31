package com.ypm.service.imp;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.*;
import com.ypm.util.AUtils;
import com.ypm.util.GMTime;
import com.ypm.util.Table;

public class ProdInfoServiceImp extends AConfig implements ProdInfoService {

	private AderInfoService aderInfoService;

	private AssetRawService assetRawService;

	private ProdModelService prodModelService;

	private UserOrderService userOrderService;

	protected void checkSQL() {
	}

	public AderInfoService getAderInfoService() {
		return aderInfoService;
	}

	public void setAderInfoService(AderInfoService aderInfoService) {
		this.aderInfoService = aderInfoService;
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

	public UserOrderService getUserOrderService() {
		return userOrderService;
	}

	public void setUserOrderService(UserOrderService userOrderService) {
		this.userOrderService = userOrderService;
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
			ps.close();
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

	public AjaxInfo findProdByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + Table.TBL_PROD_INFO).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, Table.TBL_PROD_INFO, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} // 加载后续信息
			Map<String, String> ms = this.getInfoStates();
			Map<String, String> ab = this.getDictInfoBySSid(ASSET_PROD_TAGS);
			Map<String, String> ac = this.getDictInfoBySSid(ASSET_PROD_TYPE);
			Map<String, String> ad = this.getDictInfoBySSid(ASSET_PROD_SALES);
			Map<String, String> au = this.getDictInfoBySSid(ASSET_PROD_SALER);
			Map<String, String> at = this.getDictInfoBySSid(COO_PROD_SHOW);
			Map<Integer, String> PM = this.getProdModelService().getModelByAll();
			sql.insert(0, "SELECT Pid,Rid,Cid,Tid,AA,AB,AC,AD,AG,AH,AI,AJ,AK,AL,AM,AN,AO,AT,AU,BA,CA,MA,MB,MC,MD,GmtB,State,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql, offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.get(i));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("PID", rs.getLong(1));
				json.append("RID", rs.getLong(2));
				json.append("CID", rs.getInt(3));
				String model = PM.get(rs.getInt(4));
				if (model == null) {
					json.append("TID", "--");
				} else {
					json.append("TID", model);
				} // 产品名称
				json.append("AA", rs.getString(5));
				json.append("AB", ab.get(rs.getString(6)));
				json.append("AC", ac.get(rs.getString(7)));
				json.append("AD", ad.get(rs.getString(8)));
				json.append("AG", GMTime.formatInt(rs.getInt(9)));
				json.append("AH", GMTime.formatInt(rs.getInt(10)));
				json.append("AI", GMTime.formatInt(rs.getInt(11)));
				json.append("AJ", rs.getInt(12), "天");
				json.append("AM", DF3.format(rs.getDouble(15)));
				json.append("AN", DF2.format(rs.getFloat(16)), "%");
				json.append("AO", rs.getInt(17));
				json.append("AT", at.get(rs.getString(18)));
				json.append("AU", au.get(rs.getString(19)));
				json.append("BA", rs.getString(20));
				json.append("CA", rs.getString(21));
				json.append("MB", DF3.format(rs.getDouble(23)));
				json.append("MC", DF3.format(rs.getDouble(24)));
				json.append("MD", DF3.format(rs.getDouble(25)));
				json.append("GMTB", GMTime.format(rs.getLong(26), GMTime.CHINA));
				json.append("STATE", ms.get(rs.getString(27)));
				json.append("STATS", rs.getInt(27)); // 信息状态
				json.append("TIME", GMTime.format(rs.getLong(28), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public ProdInfo findProdByPid(long Pid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ProdInfo info = null;
			ps = conn.prepareStatement("SELECT Pid,Rid,Cid,Tid,AA,AB,AC,AD,AE,AF,AG,AH,AI,AJ,AK,AL,AM,AN,AO,AP,AT,AU,BA,BG,CA,MA,MB,MC,MD,SA,SB,Way,Adj,Adk,Ads,GmtA,GmtB,GmtC,State,Stime,Time FROM " + Table.TBL_PROD_INFO + " WHERE Pid=?");
			ps.setLong(1, Pid);
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
			JPrepare.close(ps, conn);
		}
	}

	public List<AutoRaws> findRawByAll() throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			List<AutoRaws> ls = new ArrayList<AutoRaws>();
			ps = conn.prepareStatement("SELECT Pid,Rid,Cid,AU,MD,State,Time FROM " + Table.TBL_PROD_INFO + " WHERE AU=? AND State=?");
			ps.setInt(1, SALE_A2);
			ps.setInt(2, STATE_READER);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				AutoRaws r = new AutoRaws();
				r.setPid(rs.getLong(1));
				r.setRid(rs.getLong(2));
				r.setCid(rs.getInt(3));
				r.setMd(rs.getBigDecimal(5));
				r.setTime(rs.getLong(7));
				ls.add(r);
			}
			rs.close();
			return ls;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean isProdByName(String name) {
		return JPrepare.isExists("SELECT Pid FROM " + Table.TBL_PROD_INFO + " WHERE AA=?", name);
	}

	public void saveAds(String ids) throws SQLException {
		String[] ts = this.toSplit(ids);
		List<AdsInfo> ads = new ArrayList<AdsInfo>(ts.length);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			for (String Pid : ts) {
				if (ps != null) {
					ps.close();
				}
				ps = conn.prepareStatement("SELECT Pid,AA FROM " + Table.TBL_PROD_INFO + " WHERE Pid=?");
				ps.setLong(1, AUtils.toLong(Pid));
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					AdsInfo a = new AdsInfo();
					a.setTid(0); // 默认位置
					a.setSortid(1);
					a.setName(rs.getString(2));
					a.setType(12); // 产品信息
					a.setUrl(rs.getString(1));
					a.setState(STATE_DISABLE);
					ads.add(a);
				}
				rs.close();
			}
		} finally {
			JPrepare.close(ps, conn);
		} // 保存广告信息
		if (ads.size() >= 1) {
			this.getAderInfoService().saveAds(ads);
		}
	}

	public void saveProd(ProdInfo info) throws SQLException {
		info.setTime(GMTime.currentTimeMillis());
		this.save(info); // 保存数据信息
//		SyncMap.getAll().sender(SYS_A203, "save", info);
		//修改发标时数据同步的处理方式 add by  luxh
		this.saveAPI(info);
 	}

	public void saveOver() throws SQLException {
		List<AutoRaws> fs = this.findRawByAll();
		for (AutoRaws r : fs) {
			this.getUserOrderService().compute(r);
			this.getAssetRawService().saveAuto(r);
		} // 逐级更新信息
		SyncMap.getAll().sender(SYS_A203, "over", fs);
		this.save(fs);
	}

	public void saveOver(String ids) throws SQLException {
		Set<Long> set = this.toLong(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			long time = GMTime.currentTimeMillis();
			ps = conn.prepareStatement("UPDATE " + Table.TBL_PROD_INFO + " SET AU=?,Way=?,GmtC=?,State=?,Time=? WHERE Pid=? AND AU=?");
			for (Long Pid : set) {
				ps.setInt(1, SALE_A2);
				ps.setString(2, "USER");
				ps.setLong(3, time);
				ps.setInt(4, STATE_READER);
				ps.setLong(5, time);
				ps.setLong(6, Pid.longValue());
				ps.setInt(7, SALE_A1);
				ps.addBatch();
			}
			int[] ns = ps.executeBatch();
			conn.commit();
			int i = 0;
			StringBuilder sb = new StringBuilder();
			for (Long Pid : set) {
				if (ns[i] == 1) {
					sb.append(',').append(Pid.longValue());
				}
				i++;
			} // 处理数据同步
			if (sb.length() >= 1) {
				this.execute(() -> {
					try {
						this.saveOver();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				});
				SyncMap.getAll().add("ids", sb.substring(1)).add("time", time).sender(SYS_A203, "saveOver");
			}
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void saveState(String ids, int state) throws SQLException {
		boolean onSale = (state == 2); // 上架
		Set<Long> set = this.toLong(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			long time = GMTime.currentTimeMillis();
			StringBuilder sb = new StringBuilder();
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
							sb.append(',').append(Pid.longValue());
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
						sb.append(',').append(Pid.longValue());
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
			} // 处理数据同步
			if (sb.length() >= 1) {
				SyncMap.getAll().add("ids", sb.substring(1)).add("state", state).add("time", time).sender(SYS_A203, "saveState");
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void removeProd(ProdInfo info) throws SQLException {
		long time = GMTime.currentTimeMillis();
		this.remove(info.getPid(), info.getRid(), time);
		SyncMap.getAll().add("pid", info.getPid()).add("rid", info.getRid()).add("time", time).sender(SYS_A203, "remove");
	}
	public void saveAPI(ProdInfo info) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE ypiao.prod_info SET Tid=?,AA=?,AB=?,AC=?,AD=?,AE=?,AF=?,AG=?,AH=?,AI=?,AJ=?,AK=?,AL=?,AM=?,AN=?,AO=?,AP=?,AT=?,AU=?,BA=?,BG=?,CA=?,MA=?,MB=?,MC=?,SA=?,SB=?,Adj=?,Adk=?,Ads=?,GmtB=?,GmtC=?,State=?,Stime=?,Time=? WHERE Pid=?");
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
					ps = conn.prepareStatement("INSERT INTO ypiao.prod_info (Pid,Rid,Cid,Tid,AA,AB,AC,AD,AE,AF,AG,AH,AI,AJ,AK,AL,AM,AN,AO,AP,AT,AU,BA,BG,CA,MA,MB,MC,MD,SA,SB,Way,Adj,Adk,Ads,GmtA,GmtB,GmtC,State,Stime,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
}
