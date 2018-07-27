package com.ypm.service.imp;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Map;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.AssetComService;
import com.ypm.service.AssetRawService;
import com.ypm.util.GMTime;
import com.ypm.util.VeStr;

public class AssetComServiceImp extends AConfig implements AssetComService {

	private static final String TBL_ASSET_COMS = "asset_coms";

	private AssetRawService assetRawService;

	protected void checkSQL() {
	}

	public AssetRawService getAssetRawService() {
		return assetRawService;
	}

	public void setAssetRawService(AssetRawService assetRawService) {
		this.assetRawService = assetRawService;
	}

	private static void save(Connection conn, Company c) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_ASSET_COMS + " SET Tid=?,CA=?,CB=?,CC=?,CD=?,CE=?,CF=?,CG=?,CH=?,CK=?,CM=?,CN=?,State=?,Time=? WHERE Cid=?");
		try {
			ps.setInt(1, c.getTid());
			ps.setString(2, c.getCa());
			ps.setString(3, c.getCb());
			ps.setString(4, c.getCc());
			ps.setString(5, c.getCd());
			ps.setString(6, c.getCe());
			ps.setString(7, c.getCf());
			ps.setString(8, c.getCg());
			ps.setString(9, c.getCh());
			ps.setInt(10, c.getCk());
			ps.setBigDecimal(11, c.getCm());
			ps.setBigDecimal(12, c.getCn());
			ps.setInt(13, c.getState());
			ps.setLong(14, c.getTime());
			ps.setInt(15, c.getCid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_ASSET_COMS + " (Cid,Tid,CA,CB,CC,CD,CE,CF,CG,CH,CK,CM,CN,Total,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setInt(1, c.getCid());
				ps.setInt(2, c.getTid());
				ps.setString(3, c.getCa());
				ps.setString(4, c.getCb());
				ps.setString(5, c.getCc());
				ps.setString(6, c.getCd());
				ps.setString(7, c.getCe());
				ps.setString(8, c.getCf());
				ps.setString(9, c.getCg());
				ps.setString(10, c.getCh());
				ps.setInt(11, c.getCk());
				ps.setBigDecimal(12, c.getCm());
				ps.setBigDecimal(13, c.getCn());
				ps.setInt(14, c.getTotal());
				ps.setInt(15, c.getState());
				ps.setLong(16, c.getTime());
				ps.executeUpdate();
			} // 更新统计数据
			if (c.getBank() == c.getCk()) {
				// Ignored
			} else {
				if (c.getBank() > 0) {
					ps.close();
					ps = conn.prepareStatement("UPDATE comm_bank SET Total=(Total-1) WHERE Bid=? AND Bid>=?");
					ps.setInt(1, c.getBank());
					ps.setInt(2, 1);
					ps.executeUpdate();
				}
				ps.close();
				ps = conn.prepareStatement("UPDATE comm_bank SET Total=(Total+1) WHERE Bid=?");
				ps.setInt(1, c.getCk());
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public int update(Connection conn, int cid, int row) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_ASSET_COMS + " SET Total=? WHERE Cid=?");
		try {
			ps.setInt(1, row);
			ps.setInt(2, cid);
			return ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

	public AjaxInfo findCompanyByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + TBL_ASSET_COMS).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, TBL_ASSET_COMS, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} // 加载后续信息
			Map<String, String> ms = this.getInfoState();
			Map<String, String> mt = this.getDictInfoBySSid(ASSET_RAW_OWNER);
			sql.insert(0, "SELECT Cid,Tid,CA,CB,CC,CD,CE,CF,CG,CH,CM,CN,Total,State,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql, offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.get(i));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("CID", rs.getInt(1));
				json.append("TID", mt.get(rs.getString(2)));
				json.append("CA", rs.getString(3));
				json.append("CB", rs.getString(4));
				json.append("CC", rs.getString(5));
				json.append("CD", rs.getString(6));
				json.append("CE", rs.getString(7));
				json.append("CF", rs.getString(8));
				json.append("CG", rs.getString(9));
				json.append("CH", rs.getString(10));
				json.append("CM", DF3.format(rs.getDouble(11)));
				json.append("CN", DF3.format(rs.getDouble(12)));
				json.append("TOTAL", rs.getInt(13));
				json.append("STATE", ms.get(rs.getString(14)));
				json.append("TIME", GMTime.format(rs.getLong(15), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo findYPiaoByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		return null;
	}

	public Company findCompanyById(int cid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			Company c = null;
			ps = conn.prepareStatement("SELECT Cid,Tid,CA,CB,CC,CD,CE,CF,CG,CH,CK,CM,CN,Total,State,Time FROM " + TBL_ASSET_COMS + " WHERE Cid=?");
			ps.setInt(1, cid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				c = new Company();
				c.setCid(rs.getInt(1));
				c.setTid(rs.getInt(2));
				c.setCa(rs.getString(3));
				c.setCb(rs.getString(4));
				c.setCc(rs.getString(5));
				c.setCd(rs.getString(6));
				c.setCe(rs.getString(7));
				c.setCf(rs.getString(8));
				c.setCg(rs.getString(9));
				c.setCh(rs.getString(10));
				c.setCk(rs.getInt(11));
				c.setCm(rs.getBigDecimal(12));
				c.setCn(rs.getBigDecimal(13));
				c.setTotal(rs.getInt(14));
				c.setState(rs.getInt(15));
				c.setTime(rs.getLong(16));
				c.setBank(c.getCk());
			}
			rs.close();
			return c;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean isCompanyBySNo(String sno) {
		return JPrepare.isExists("SELECT Cid FROM " + TBL_ASSET_COMS + " WHERE CB=?", sno);
	}

	public void saveCompany(Company c, FileInfo f) throws SQLException, IOException {
		Connection conn = JPrepare.getConnection();
		try {
			if (c.getCid() <= 0) {
				c.setCid(this.getId(conn, TBL_ASSET_COMS, "Cid"));
			}
			c.setCc(f.getPid(c.getCc()));
			c.setTime(GMTime.currentTimeMillis());
			save(conn, c); // 保存数据
		} finally {
			JPrepare.close(conn);
		} // 同步数据信息
		f.setTime(c.getCid(), c.getTime());
		this.saveFile(f); // 保存相关文件
		SyncMap.getAll().sender(SYS_A201, "save", c);
	}

	public boolean sendYPiao(Company c) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			boolean result = true;
			ps = conn.prepareStatement("SELECT Rid FROM asset_raws WHERE Cid=? AND State=?");
			ps.setInt(1, c.getCid());
			ps.setInt(2, STATE_CHECK);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = false;
			}
			rs.close();
			if (result) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO asset_raws (Rid,Cid,Tid,CA,CB,CC,CD,CE,CF,CG,CH,CK,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setLong(1, VeStr.getUSid());
				ps.setInt(2, c.getCid());
				ps.setInt(3, c.getTid());
				ps.setString(4, c.getCa());
				ps.setString(5, c.getCb());
				ps.setString(6, c.getCc());
				ps.setString(7, c.getCd());
				ps.setString(8, c.getCe());
				ps.setString(9, c.getCf());
				ps.setString(10, c.getCg());
				ps.setString(11, c.getCh());
				ps.setInt(12, c.getCk());
				ps.setInt(13, STATE_CHECK);
				ps.setLong(14, GMTime.currentTimeMillis());
				if (ps.executeUpdate() >= 1) {
					ps.close();
					ps = null; // 更新上级汇总数据
					int row = JPrepare.getRows("SELECT COUNT(1) FROM asset_raws WHERE Cid=?", c.getCid());
					this.update(conn, c.getCid(), row);
				}
			}
			return result;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean remove(int cid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			boolean result = false;
			ps = conn.prepareStatement("DELETE FROM " + TBL_ASSET_COMS + " WHERE Cid=? AND State=? AND Total<=?");
			ps.setInt(1, cid);
			ps.setInt(2, STATE_NORMAL);
			ps.setInt(3, 0); // Total 票据
			if (ps.executeUpdate() >= 1) {
				SyncMap.getAll().sender(SYS_A201, "remove", cid);
				result = true;
			}
			return result;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
