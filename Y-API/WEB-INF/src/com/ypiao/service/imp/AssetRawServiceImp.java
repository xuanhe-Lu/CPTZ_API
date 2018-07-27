package com.ypiao.service.imp;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.ypiao.bean.*;
import com.ypiao.data.JPrepare;
import com.ypiao.service.AssetComService;
import com.ypiao.service.AssetRawService;

public class AssetRawServiceImp extends AConfig implements AssetRawService {

	private static final String TBL_ASSET_RAWG = "asset_rawg", TBL_ASSET_RAWS = "asset_raws";

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
		return JPrepare.getRows("SELECT COUNT(1) FROM " + TBL_ASSET_RAWS + " WHERE Cid=?", cid);
	}

	private boolean save(Connection conn, FileInfo f) throws SQLException {
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
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_ASSET_RAWS + " SET Cid=?,Tid=?,Code=?,BA=?,BB=?,BC=?,BD=?,BE=?,BF=?,BG=?,BH=?,BI=?,BJ=?,BM=?,BN=?,BO=?,BU=?,BV=?,CA=?,CB=?,CC=?,CD=?,CE=?,CF=?,CG=?,CH=?,CK=?,MA=?,State=?,Time=? WHERE Rid=?");
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
				ps = conn.prepareStatement("INSERT INTO " + TBL_ASSET_RAWS + " (Rid,Cid,Tid,Code,BA,BB,BC,BD,BE,BF,BG,BH,BI,BJ,BM,BN,BO,BU,BV,CA,CB,CC,CD,CE,CF,CG,CH,CK,MA,Total,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
					ps = null;
					int row = getRow(r.getCid());
					this.getAssetComService().update(conn, r.getCid(), row);
				}
			}
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
	}

	public void save(long rid, String ids, long time) throws SQLException {
		String[] ts = this.toSplit(ids);
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
		}
	}

	public void save(FileInfo f) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			if (save(conn, f)) {
				ps = JPrepare.prepareStatement(conn, "SELECT Rid,Cid,Tid,Code,BA,BB,BV,State,Time FROM " + TBL_ASSET_RAWS + " WHERE Rid=?");
				ps.setLong(1, Long.parseLong(f.getSid()));
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					int bv = rs.getInt(7) + 1;
					int state = rs.getInt(8);
					if (state == STATE_CHECK && rs.getInt(6) > 0) {
						state = STATE_NORMAL;
					}
					rs.updateInt(7, bv);
					rs.updateInt(8, state);
					rs.updateRow();
				}
				rs.close();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void save(RawInfo r) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, r); // 保存数据
		} finally {
			JPrepare.close(conn);
		}
	}

	public void save(List<AutoRaws> fs) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_ASSET_RAWS + " SET MB=?,MC=?,MD=?,ME=?,MF=?,MG=?,YMA=?,State=?,Time=? WHERE Rid=?");
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
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_ASSET_RAWS + " SET Total=?,State=?,Time=? WHERE Rid=?");
		try {
			if (total <= 0) {
				ps.setInt(1, 0);
				ps.setInt(2, STATE_NORMAL);
			} else {
				ps.setInt(1, total);
				ps.setInt(2, STATE_READER);
			}
			ps.setLong(3, time);
			ps.setLong(4, rid);
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

	public void remove(long rid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int cid = 0;
			ps = JPrepare.prepareStatement(conn, "SELECT Rid,Cid,State,Time FROM " + TBL_ASSET_RAWS + " WHERE Rid=?");
			ps.setLong(1, rid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt(3) >= STATE_READER) {
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
				ps = conn.prepareStatement("DELETE FROM " + TBL_ASSET_RAWG + " WHERE Rid=?");
				ps.setLong(1, rid);
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void remove(long rid, String ids, long time) throws SQLException {
		String[] ts = this.toSplit(ids.toUpperCase());
		Set<String> set = new HashSet<String>();
		for (String Pid : ts) {
			set.add(Pid);
		} // 检测数据信息
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 0;
			ps = JPrepare.prepareStatement(conn, "SELECT Pid,Rid,Sortid FROM " + TBL_ASSET_RAWG + " WHERE Rid=? ORDER BY Sortid ASC");
			ps.setLong(1, rid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String Pid = rs.getString(1);
				if (set.remove(Pid)) {
					rs.deleteRow();
				} else {
					index += 1;
					rs.updateInt(3, index);
					rs.updateRow();
				}
			}
			rs.close();
			ps.close(); // 更新统计
			ps = conn.prepareStatement("UPDATE " + TBL_ASSET_RAWS + " SET BV=? WHERE Rid=?");
			ps.setInt(1, index);
			ps.setLong(2, rid);
			ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public RawInfo findRawByRid(long rid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			RawInfo r = null;
			ps = conn.prepareStatement("SELECT Rid,Cid,Tid,Code,BA,BB,BC,BD,BE,BF,BG,BH,BI,BJ,BM,BN,BO,BU,BV,CA,CB,CC,CD,CE,CF,CG,CH,CK,MA,Total,State,Time FROM " + TBL_ASSET_RAWS + " WHERE Rid=?");
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

	public void loadRawByRid(AjaxInfo json, long rid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT Rid,Pid,Name,State,Time FROM " + TBL_ASSET_RAWG + " WHERE Rid=? AND State=? ORDER BY Sortid ASC");
			ps.setLong(1, rid);
			ps.setInt(2, STATE_NORMAL);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("pid", rs.getString(2));
				json.append("name", rs.getString(3));
			}
			rs.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
