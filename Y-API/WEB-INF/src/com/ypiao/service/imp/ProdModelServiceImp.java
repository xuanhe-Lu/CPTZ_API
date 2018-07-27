package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.ypiao.bean.ProdModel;
import com.ypiao.data.JPrepare;
import com.ypiao.service.ProdModelService;

public class ProdModelServiceImp extends AConfig implements ProdModelService {

	private static final String TBL_PROD_MODEL = "prod_model";

	private Map<Integer, ProdModel> cache;

	protected void checkSQL() {
		cache = new HashMap<Integer, ProdModel>();
	}

	private void save(Connection conn, ProdModel m) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_PROD_MODEL + " SET Name=?,Total=?,Toall=?,Tofee=?,MA=?,MB=?,MC=?,Remark=?,State=?,Time=? WHERE Tid=?");
		try {
			ps.setString(1, m.getName());
			ps.setInt(2, m.getTotal());
			ps.setInt(3, m.getToall());
			ps.setInt(4, m.getTofee());
			ps.setBigDecimal(5, m.getMa());
			ps.setBigDecimal(6, m.getMb());
			ps.setBigDecimal(7, m.getMc());
			ps.setString(8, m.getRemark());
			ps.setInt(9, m.getState());
			ps.setLong(10, m.getTime());
			ps.setInt(11, m.getTid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_PROD_MODEL + " (Tid,Name,Total,Toall,Tofee,MA,MB,MC,Remark,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
				ps.setInt(1, m.getTid());
				ps.setString(2, m.getName());
				ps.setInt(3, m.getTotal());
				ps.setInt(4, m.getToall());
				ps.setInt(5, m.getTofee());
				ps.setBigDecimal(6, m.getMa());
				ps.setBigDecimal(7, m.getMb());
				ps.setBigDecimal(8, m.getMc());
				ps.setString(9, m.getRemark());
				ps.setInt(10, m.getState());
				ps.setLong(11, m.getTime());
				ps.executeUpdate();
			} // 更新数据
			this.cache.put(m.getTid(), m);
		} finally {
			ps.close();
		}
	}

	public void save(ProdModel m) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, m);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void update(String ids, int state, long time) throws SQLException {
		Set<Integer> set = this.toInt(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_PROD_MODEL + " SET State=?,Time=? WHERE Tid=?");
			for (Integer t : set) {
				ps.setInt(1, state);
				ps.setLong(2, time);
				ps.setInt(3, t.intValue());
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

	public ProdModel findProdModelByTid(int tid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ProdModel m = null;
			ps = conn.prepareStatement("SELECT Tid,Name,Total,Toall,Tofee,MA,MB,MC,Remark,State,Time FROM " + TBL_PROD_MODEL + " WHERE Tid=?");
			ps.setInt(1, tid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				m = new ProdModel();
				m.setTid(rs.getInt(1));
				m.setName(rs.getString(2));
				m.setTotal(rs.getInt(3));
				m.setToall(rs.getInt(4));
				m.setTofee(rs.getInt(5));
				m.setMa(rs.getBigDecimal(6));
				m.setMb(rs.getBigDecimal(7));
				m.setMc(rs.getBigDecimal(8));
				m.setRemark(rs.getString(9));
				m.setState(rs.getInt(10));
				m.setTime(rs.getLong(11));
				this.cache.put(m.getTid(), m);
			}
			rs.close();
			return m;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public ProdModel getProdModelByTid(int tid) throws SQLException {
		ProdModel m = this.cache.get(tid);
		if (m == null) {
			return findProdModelByTid(tid);
		}
		return m;
	}
}
