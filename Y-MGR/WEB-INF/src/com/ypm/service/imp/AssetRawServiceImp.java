package com.ypm.service.imp;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.AssetComService;
import com.ypm.service.AssetRawService;
import com.ypm.util.GMTime;
import com.ypm.util.Table;
import com.ypm.util.VeRule;

public class AssetRawServiceImp extends AConfig implements AssetRawService {

	private static final String TBL_ASSET_RAWG = "asset_rawg";

	private AssetComService assetComService;

	protected void checkSQL() {
	}

	public AssetComService getAssetComService() {
		return assetComService;
	}

	public void setAssetComService(AssetComService assetComService) {
		this.assetComService = assetComService;
	}

	private int getRow(int cid) {
		return JPrepare.getRows("SELECT COUNT(1) FROM " + Table.TBL_ASSET_RAWS + " WHERE Cid=?", cid);
	}

	private static boolean save(Connection conn, FileInfo f) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_ASSET_RAWG + " SET Sortid=?,Source=?,Dist=?,Name=?,Info=?,Pdw=?,Pdh=?,Size=?,State=?,Time=? WHERE Pid=?");
		try {
			ps.setInt(1, f.getSortid());
			ps.setString(2, f.getSource());
			ps.setString(3, f.getDist());
			ps.setString(4, f.getName());
			ps.setString(5, f.getInfo());
			ps.setInt(6, f.getPdw());
			ps.setInt(7, f.getPdh());
			ps.setLong(8, f.getSize());
			ps.setInt(9, f.getState());
			ps.setLong(10, f.getTime());
			ps.setString(11, f.getPid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_ASSET_RAWG + " (Pid,Rid,Sortid,Source,Dist,Name,Info,Pdw,Pdh,Size,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setString(1, f.getPid());
				ps.setString(2, f.getSid());
				ps.setInt(3, f.getSortid());
				ps.setString(4, f.getSource());
				ps.setString(5, f.getDist());
				ps.setString(6, f.getName());
				ps.setString(7, f.getInfo());
				ps.setInt(8, f.getPdw());
				ps.setInt(9, f.getPdh());
				ps.setLong(10, f.getSize());
				ps.setInt(11, f.getState());
				ps.setLong(12, f.getTime());
				ps.executeUpdate();
				return true;
			} else {
				return false;
			}
		} finally {
			ps.close();
		}
	}

	private void save(Connection conn, RawInfo r) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_ASSET_RAWS + " SET Cid=?,Tid=?,Code=?,BA=?,BB=?,BC=?,BD=?,BE=?,BF=?,BG=?,BH=?,BI=?,BJ=?,BM=?,BN=?,BO=?,BU=?,BV=?,CA=?,CB=?,CC=?,CD=?,CE=?,CF=?,CG=?,CH=?,CK=?,MA=?,State=?,Time=? WHERE Rid=?");
		try {
			ps.setInt(1, r.getCid());
			ps.setInt(2, r.getTid());
			ps.setString(3, r.getCode());
			ps.setString(4, r.getBa());
			ps.setBigDecimal(5, r.getBb());
			ps.setInt(6, r.getBc());
			ps.setInt(7, r.getBd());
			ps.setInt(8, r.getBe());
			ps.setString(9, r.getBf());
			ps.setString(10, r.getBg());
			ps.setInt(11, r.getBh());
			ps.setInt(12, r.getBi());
			ps.setInt(13, r.getBj());
			ps.setBigDecimal(14, r.getBm());
			ps.setBigDecimal(15, r.getBn());
			ps.setBigDecimal(16, r.getBo());
			ps.setString(17, r.getBu());
			ps.setInt(18, r.getBv()); // 合同数
			ps.setString(19, r.getCa());
			ps.setString(20, r.getCb());
			ps.setString(21, r.getCc());
			ps.setString(22, r.getCd());
			ps.setString(23, r.getCe());
			ps.setString(24, r.getCf());
			ps.setString(25, r.getCg());
			ps.setString(26, r.getCh());
			ps.setInt(27, r.getCk());
			ps.setBigDecimal(28, r.getMa());
			ps.setInt(29, r.getState());
			ps.setLong(30, r.getTime());
			ps.setLong(31, r.getRid());
			if (ps.executeUpdate() <= 0) {
				ps.close(); // 新增记录
				ps = conn.prepareStatement("INSERT INTO " + Table.TBL_ASSET_RAWS + " (Rid,Cid,Tid,Code,BA,BB,BC,BD,BE,BF,BG,BH,BI,BJ,BM,BN,BO,BU,BV,CA,CB,CC,CD,CE,CF,CG,CH,CK,MA,Total,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, r.getRid());
				ps.setInt(2, r.getCid());
				ps.setInt(3, r.getTid());
				ps.setString(4, r.getCode());
				ps.setString(5, r.getBa());
				ps.setBigDecimal(6, r.getBb());
				ps.setInt(7, r.getBc());
				ps.setInt(8, r.getBd());
				ps.setInt(9, r.getBe());
				ps.setString(10, r.getBf());
				ps.setString(11, r.getBg());
				ps.setInt(12, r.getBh());
				ps.setInt(13, r.getBi());
				ps.setInt(14, r.getBj());
				ps.setBigDecimal(15, r.getBm());
				ps.setBigDecimal(16, r.getBn());
				ps.setBigDecimal(17, r.getBo());
				ps.setString(18, r.getBu());
				ps.setInt(19, r.getBv()); // 合同数
				ps.setString(20, r.getCa());
				ps.setString(21, r.getCb());
				ps.setString(22, r.getCc());
				ps.setString(23, r.getCd());
				ps.setString(24, r.getCe());
				ps.setString(25, r.getCf());
				ps.setString(26, r.getCg());
				ps.setString(27, r.getCh());
				ps.setInt(28, r.getCk());
				ps.setBigDecimal(29, r.getMa());
				ps.setInt(30, r.getTotal());
				ps.setInt(31, r.getState());
				ps.setLong(32, r.getTime());
				if (ps.executeUpdate() >= 1) {
					ps.close();
					ps = null; // 更新上级汇总数据
					int row = getRow(r.getCid());
					this.getAssetComService().update(conn, r.getCid(), row);
				}
			}
		} finally {
			ps.close();
		}
	}

	public void save(List<AutoRaws> fs) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + Table.TBL_ASSET_RAWS + " SET MB=?,MC=?,MD=?,ME=?,MF=?,MG=?,YMA=?,State=?,Time=? WHERE Rid=?");
			for (AutoRaws r : fs) {
				ps.setBigDecimal(1, r.getMb());
				ps.setBigDecimal(2, r.getMc());
				ps.setBigDecimal(3, r.getMd());
				ps.setBigDecimal(4, r.getMe());
				ps.setBigDecimal(5, r.getMf());
				ps.setBigDecimal(6, r.getMg());
				ps.setBigDecimal(7, r.getYma());
				ps.setInt(8, r.getState());
				ps.setLong(9, r.getTime());
				ps.setLong(10, r.getRid());
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

	public int update(Connection conn, long rid, int total, long time) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + Table.TBL_ASSET_RAWS + " SET Total=?,State=?,Time=? WHERE Rid=?");
		try {
			if (total <= 0) {
				ps.setInt(1, 0);
				ps.setInt(2, SALE_A0);
			} else {
				ps.setInt(1, total);
				ps.setInt(2, SALE_A2);
			}
			ps.setLong(3, time);
			ps.setLong(4, rid);
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

	public AjaxInfo findRawByAll() {
		AjaxInfo json = AjaxInfo.getArray();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			int day = GMTime.getTday(GMTime.CHINA, 1);
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Rid,BA,BH,BI,BJ,BM,BN FROM " + Table.TBL_ASSET_RAWS + " WHERE State=? AND BI>=? ORDER BY Rid ASC");
			ps.setInt(1, SALE_A0);
			ps.setInt(2, day); // 借款日期
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(1));
				String BM = DF2.format(rs.getDouble(6));
				String BN = DF2.format(rs.getFloat(7));
				StringBuilder sb = json.getBuilder("text");
				sb.append("\"【").append(BM).append("】").append(BN).append("%");
				sb.append("【").append(rs.getInt(4)).append('~').append(rs.getInt(3)).append('=').append(rs.getInt(5)).append("】\"");
				sb = json.getBuilder("cycle"); // 理财周期
				sb.append('"').append(GMTime.formatInt(rs.getString(4))).append('~').append(GMTime.formatInt(rs.getString(3))).append('"');
				json.append("BI", GMTime.formatInt(rs.getString(4))); // 借款日期
				json.append("BJ", rs.getInt(5));
				json.append("BM", BM);
				json.append("BN", BN);
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo findRawByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + Table.TBL_ASSET_RAWS).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, Table.TBL_ASSET_RAWS, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} // 加载后续信息
			double r = 0;
			Map<String, String> mt = this.getDictInfoBySSid(ASSET_RAW_OWNER);
			Map<String, String> be = this.getDictInfoBySSid(ASSET_RAW_SAFE);
			Map<String, String> ms = this.getDictInfoBySSid(ASSET_RAW_STATE);
			sql.insert(0, "SELECT Rid,Cid,Tid,Code,BA,BB,BC,BD,BE,BF,BG,BH,BI,BJ,BM,BN,BO,CA,CB,CD,CE,CF,ME,YMA,Total,State,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql, offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.get(i));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("RID", rs.getLong(1));
				json.append("TID", mt.get(rs.getString(3)));
				json.append("BA", rs.getString(5));
				json.append("BB", DF3.format(rs.getDouble(6)));
				json.append("BC", GMTime.formatInt(rs.getString(7)));
				json.append("BD", GMTime.formatInt(rs.getString(8)));
				json.append("BE", be.get(rs.getString(9)));
				json.append("BF", rs.getString(10));
				json.append("BG", rs.getString(11));
				json.append("BH", GMTime.formatInt(rs.getString(12)));
				json.append("BI", GMTime.formatInt(rs.getString(13)));
				json.append("BJ", rs.getInt(14));
				json.append("BM", DF3.format(rs.getDouble(15)));
				json.append("BN", DF2.format(rs.getFloat(16)), "%");
				if ((r = rs.getDouble(17)) > 0) {
					json.append("BO", DF2.format(r), "%");
				} else {
					json.append("BO", "-");
				}
				json.append("CA", rs.getString(18));
				json.append("CB", rs.getString(19));
				json.append("CE", rs.getString(21));
				if ((r = rs.getDouble(23)) > 0) {
					json.append("ME", DF3.format(r));
					json.append("YMA", DF3.format(rs.getDouble(24)));
				} else {
					json.append("ME", "-");
					json.append("YMA", "-");
				}
				json.append("TOTAL", rs.getInt(25));
				json.append("STATE", ms.get(rs.getString(26)));
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

	public AjaxInfo findImgsByRid(AjaxInfo json, long rid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			Map<String, String> ms = this.getInfoState();
			ps = conn.prepareStatement("SELECT Pid,Rid,Sortid,Name,Info,State,Time FROM " + TBL_ASSET_RAWG + " WHERE Rid=? AND State<=? ORDER BY Sortid ASC");
			ps.setLong(1, rid);
			ps.setInt(2, STATE_CHECK);
			ResultSet rs = ps.executeQuery();
			json.setArray(true);
			while (rs.next()) {
				json.formater();
				json.append("PID", rs.getString(1));
				json.append("RID", rs.getString(2));
				json.append("SORTID", rs.getInt(3));
				json.append("NAME", rs.getString(4));
				json.append("INFO", rs.getString(5));
				json.append("STATE", ms.get(rs.getString(6)));
				json.append("STATS", rs.getInt(6)); // 默认状态
				json.append("TIME", GMTime.format(rs.getLong(7), GMTime.CHINA));
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo findTreeByRid(AjaxInfo json, long rid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT Pid,Name FROM " + TBL_ASSET_RAWG + " WHERE Rid=? AND State=? ORDER BY Sortid ASC");
			ps.setLong(1, rid);
			ps.setInt(2, SALE_A0);
			ResultSet rs = ps.executeQuery();
			json.setArray(true);
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(1));
				json.append("text", rs.getString(2));
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public RawInfo findRawByRid(long rid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			RawInfo r = null;
			ps = conn.prepareStatement("SELECT Rid,Cid,Tid,Code,BA,BB,BC,BD,BE,BF,BG,BH,BI,BJ,BM,BN,BO,BU,BV,CA,CB,CC,CD,CE,CF,CG,CH,CK,MA,Total,State,Time FROM " + Table.TBL_ASSET_RAWS + " WHERE Rid=?");
			ps.setLong(1, rid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				r = new RawInfo();
				r.setRid(rs.getLong(1));
				r.setCid(rs.getInt(2));
				r.setTid(rs.getInt(3));
				r.setCode(rs.getString(4));
				r.setBa(rs.getString(5));
				r.setBb(rs.getBigDecimal(6));
				r.setBc(rs.getInt(7));
				r.setBd(rs.getInt(8));
				r.setBe(rs.getInt(9));
				r.setBf(rs.getString(10));
				r.setBg(rs.getString(11));
				r.setBh(rs.getInt(12));
				r.setBi(rs.getInt(13));
				r.setBj(rs.getInt(14));
				r.setBm(rs.getBigDecimal(15));
				r.setBn(rs.getBigDecimal(16));
				r.setBo(rs.getBigDecimal(17));
				r.setBu(rs.getString(18));
				r.setBv(rs.getInt(19));
				r.setCa(rs.getString(20));
				r.setCb(rs.getString(21));
				r.setCc(rs.getString(22));
				r.setCd(rs.getString(23));
				r.setCe(rs.getString(24));
				r.setCf(rs.getString(25));
				r.setCg(rs.getString(26));
				r.setCh(rs.getString(27));
				r.setCk(rs.getInt(28));
				r.setMa(rs.getBigDecimal(29));
				r.setTotal(rs.getInt(30));
				r.setState(rs.getInt(31));
				r.setTime(rs.getLong(32));
			}
			rs.close();
			return r;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public RawProd findProdByRid(long rid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			RawProd r = null;
			ps = conn.prepareStatement("SELECT Rid,Cid,BA,BG,BH,BI,BJ,BM,BN,CA,Total,State,Time FROM " + Table.TBL_ASSET_RAWS + " WHERE Rid=?");
			ps.setLong(1, rid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				r = new RawProd();
				r.setRid(rs.getLong(1));
				r.setCid(rs.getInt(2));
				r.setBa(rs.getString(3));
				r.setBg(rs.getString(4));
				r.setBh(rs.getInt(5));
				r.setBi(rs.getInt(6));
				r.setBj(rs.getInt(7));
				r.setBm(rs.getBigDecimal(8));
				r.setBn(rs.getBigDecimal(9));
				r.setCa(rs.getString(10));
				r.setState(rs.getInt(12));
			}
			rs.close();
			return r;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int findImgByRid(long rid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			return this.getSortid(conn, "SELECT MAX(Sortid) FROM " + TBL_ASSET_RAWG + " WHERE Rid=?", rid);
		} finally {
			JPrepare.close(conn);
		}
	}

	public boolean loadImg(FileInfo f) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			boolean result = false;
			ps = conn.prepareStatement("SELECT Pid,Rid,Sortid,Source,Dist,Name,Info,Pdw,Pdh,Size,State,Time FROM " + TBL_ASSET_RAWG + " WHERE Pid=?");
			ps.setString(1, f.getPid());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				f.setPid(rs.getString(1));
				f.setSid(rs.getString(2));
				f.setSortid(rs.getInt(3));
				f.setSource(rs.getString(4));
				f.setDist(rs.getString(5));
				f.setName(rs.getString(6));
				f.setInfo(rs.getString(7));
				f.setPdw(rs.getInt(8));
				f.setPdh(rs.getInt(9));
				f.setSize(rs.getLong(10));
				f.setState(rs.getInt(11));
				f.setTime(rs.getLong(12));
				result = true;
			}
			rs.close();
			return result;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void saveAuto(AutoRaws r) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = JPrepare.prepareStatement(conn, "SELECT Rid,BJ,BM,BN,BO,MA,MB,MC,MD,ME,MF,MG,YMA,State,Time FROM " + Table.TBL_ASSET_RAWS + " WHERE Rid=?");
			ps.setLong(1, r.getRid());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (r.getMe().compareTo(BigDecimal.ZERO) >= 1) {
					BigDecimal day = rs.getBigDecimal(2); // 天数
					BigDecimal R = rs.getBigDecimal(4); // 年化率
					BigDecimal f = rs.getBigDecimal(5); // 管理费
					r.setMf(VeRule.toPer(r.getMe(), f));
					r.setMg(VeRule.income(r.getMe(), R, day));
					r.setYma(r.getMe().add(r.getMf().add(r.getMg())));
				} // 检测状态信息
				r.setMa(rs.getBigDecimal(6));
				r.setMb(rs.getBigDecimal(7));
				if (SALE_A2 == rs.getInt(14)) {
					r.setState(SALE_A3);
					r.setTime(GMTime.currentTimeMillis());
				} else {
					r.setState(rs.getInt(14));
					r.setTime(rs.getLong(15));
				} // 更新数据信息
				rs.updateBigDecimal(8, r.getMc());
				rs.updateBigDecimal(9, r.getMd());
				rs.updateBigDecimal(10, r.getMe());
				rs.updateBigDecimal(11, r.getMf());
				rs.updateBigDecimal(12, r.getMg());
				rs.updateBigDecimal(13, r.getYma());
				rs.updateInt(14, r.getState());
				rs.updateLong(15, r.getTime());
				rs.updateRow();
			}
			rs.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void saveOrder(long rid, String ids) throws SQLException {
		String[] ts = this.toSplit(ids);
		long time = GMTime.currentTimeMillis();
		this.saveOrder(ts, time); // 基础数据更新
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_ASSET_RAWG + " SET Sortid=?,Time=? WHERE Rid=? AND Pid=?");
			for (String Pid : ts) {
				ps.setInt(1, index++);
				ps.setLong(2, time);
				ps.setLong(3, rid);
				ps.setString(4, Pid);
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		} // 同步更新操作
		SyncMap.getAll().add("rid", rid).add("ids", ids).add("time", time).sender(SYS_A202, "order");
	}

	public void saveRawImgs(FileInfo f, long rid) throws SQLException, IOException {
		f.setTime(GMTime.currentTimeMillis());
		this.saveFile(f); // 保存相关文件
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			if (save(conn, f)) {
				ps = JPrepare.prepareStatement(conn, "SELECT Rid,Cid,Tid,Code,BA,BB,BV,State,Time FROM " + Table.TBL_ASSET_RAWS + " WHERE Rid=?");
				ps.setLong(1, rid);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					int bv = rs.getInt(7) + 1;
					int state = rs.getInt(8);
					if (state == SALE_A1 && rs.getInt(6) > 0) {
						state = SALE_A0;
					}
					rs.updateInt(7, bv);
					rs.updateInt(8, state);
					rs.updateRow();
				}
				rs.close();
			}
		} finally {
			JPrepare.close(ps, conn);
		} // 同步数据信息
		SyncMap.getAll().sender(SYS_A202, "imgs", f);
	}

	public void saveRawInfo(RawInfo r, FileInfo f) throws SQLException, IOException {
		Connection conn = JPrepare.getConnection();
		try {
			r.setBu(f.getPid(r.getBu()));
			r.setTime(GMTime.currentTimeMillis());
			this.save(conn, r); // 保存数据
		} finally {
			JPrepare.close(conn);
		} // 同步数据信息
		f.setTime(r.getRid(), r.getTime());
		this.saveFile(f); // 保存相关文件
		SyncMap.getAll().sender(SYS_A202, "save", r);
	}

	public void removeInfo(long rid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int cid = 0;
			ps = JPrepare.prepareStatement(conn, "SELECT Rid,Cid,State,Time FROM " + Table.TBL_ASSET_RAWS + " WHERE Rid=?");
			ps.setLong(1, rid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt(3) >= SALE_A2) {
					// 锁定项目不允许删除
				} else {
					cid = rs.getInt(2);
					rs.deleteRow();
				}
			}
			rs.close();
			if (cid >= 1) {
				ps.close();
				int row = this.getRow(cid);
				this.getAssetComService().update(conn, cid, row);
				SyncMap.getAll().add("rid", rid).sender(SYS_A202, "remove");
				ps = conn.prepareStatement("DELETE FROM " + TBL_ASSET_RAWG + " WHERE Rid=?");
				ps.setLong(1, rid);
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean removePics(long rid, String ids) throws SQLException {
		String[] ts = this.toSplit(ids.toUpperCase());
		Set<String> set = new HashSet<String>();
		for (String Pid : ts) {
			set.add(Pid);
		} // 检测数据信息
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			boolean result = false;
			ps = conn.prepareStatement("SELECT Pid,Rid,Sortid FROM " + TBL_ASSET_RAWG + " WHERE Rid=? ORDER BY Sortid ASC");
			ps.setLong(1, rid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String Pid = rs.getString(1);
				if (!set.contains(Pid)) {
					result = true;
					break;
				}
			}
			rs.close();
			if (result) {
				ps.close();
				int index = 0;
				StringBuilder sb = new StringBuilder();
				ps = JPrepare.prepareStatement(conn, "SELECT Pid,Rid,Sortid FROM " + TBL_ASSET_RAWG + " WHERE Rid=? ORDER BY Sortid ASC");
				ps.setLong(1, rid);
				rs = ps.executeQuery();
				while (rs.next()) {
					String Pid = rs.getString(1);
					if (set.remove(Pid)) {
						sb.append(',').append(Pid);
						rs.deleteRow();
					} else {
						index += 1;
						rs.updateInt(3, index);
						rs.updateRow();
					}
				}
				rs.close();
				ps.close(); // 更新统计
				ps = conn.prepareStatement("UPDATE " + Table.TBL_ASSET_RAWS + " SET BV=? WHERE Rid=?");
				ps.setInt(1, index);
				ps.setLong(2, rid);
				ps.executeUpdate();
				SyncMap.getAll().add("rid", rid).add("ids", sb.substring(1)).sender(SYS_A202, "delete");
			}
			return result;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
