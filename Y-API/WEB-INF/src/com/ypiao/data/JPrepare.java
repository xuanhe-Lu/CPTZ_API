package com.ypiao.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JPrepare {

	protected static JWrapper jw = null;

	public static Connection getConnection() throws SQLException {
		if (jw == null) {
			DataFactory.setJWrapper();
		}
		return jw.getConnection();
	}
	/** 检查索引 */
	public static final void checkIndex() throws SQLException {
		jw.checkIndex();
	}

	public static void checkSyncs(String name, String tbl) {
		try {
			jw.checkTable(name, tbl);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static final void close(Connection conn) {
		jw.close(conn);
	}

	public static final void close(CallableStatement cs, Connection conn) {
		jw.close(cs, conn);
	}

	public static final void close(PreparedStatement ps, Connection conn) {
		jw.close(ps, conn);
	}

	public static final void close(Statement stmt, Connection conn) {
		jw.close(stmt, conn);
	}
	/** 只读双向滚动 */
	public static final Statement createStatement(Connection conn) throws SQLException {
		return conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}
	/** 可更新预处理 */
	public static final PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {
		return conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	}
	/** 执行SQL语句 */
	public static final boolean execute(String sql) throws SQLException {
		return jw.execute(sql);
	}
	/** 同步更新数据 */
	public static final boolean execute(String table, String[] ts) throws Exception {
		return jw.execute(table, ts);
	}
	/** 执行SQL字符串 */
	public static int executeUpdate(String sql) throws SQLException {
		Connection conn = getConnection();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			return stmt.executeUpdate(sql);
		} catch (Exception e) {
			return 0;
		} finally {
			close(stmt, conn);
		}
	}
	/** 执行SQL字符串 */
	public static int executeUpdate(String sql, int num) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, num);
			return ps.executeUpdate();
		} catch (Exception e) {
			return 0;
		} finally {
			close(ps, conn);
		}
	}
	/** 执行SQL字符串 */
	public static int executeUpdate(String sql, long num) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setLong(1, num);
			return ps.executeUpdate();
		} catch (Exception e) {
			return 0;
		} finally {
			close(ps, conn);
		}
	}
	/** 执行SQL字符串 */
	public static int executeUpdate(String sql, String sid) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, sid);
			return ps.executeUpdate();
		} catch (Exception e) {
			return 0;
		} finally {
			close(ps, conn);
		}
	}
	/** 执行SQL字符串 */
	public static int executeUpdate(String sql, Object... args) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			int len = args.length;
			for (int i = 1; i <= len; i++) {
				ps.setObject(i, args[i-1]);
			}
			return ps.executeUpdate();
		} catch (Exception e) {
			return 0;
		} finally {
			close(ps, conn);
		}
	}

	public static final String getQuery(String sql, int max) {
		return jw.getQuery(sql, max);
	}

	public static final String getQuery(String sql, int offset, int max) {
		return jw.getQuery(sql, offset, max);
	}
	/** 关联分页查询 */
	public static final String getQuery(String sqlA, int max, String type, String sqlB, String info) {
		return jw.getQuery(sqlA, max, type, sqlB, info);
	}
	/** 关联分页查询 */
	public static final String getQuery(String sqlA, int offset, int max, String type, String sqlB, String info) {
		return jw.getQuery(sqlA, offset, max, type, sqlB, info);
	}

	public static final String getQuery(StringBuilder sql, int max) {
		return jw.getQuery(sql, max);
	}

	public static final String getQuery(StringBuilder sql, int offset, int max) {
		return jw.getQuery(sql, offset, max);
	}

	public static final String getUpdate(String tblA, String tblB, String info, String join) {
		return jw.getUpdate(tblA, tblB, info, join);
	}
	/** 获取记录数 */
	public static int getRows(Connection conn, String sql, Object... args) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(sql);
		try {
			for (int i = 1; i <= args.length; i++) {
				ps.setObject(i, args[i - 1]);
			}
			ResultSet rs = ps.executeQuery();
			int def = 0;
			if (rs.next()) {
				def = rs.getInt(1);
			}
			rs.close();
			return def;
		} finally {
			ps.close();
		}
	}
	/** 获取记录数 */
	public static int getRows(String sql, Object... args) {
		Connection conn = null;
		try {
			conn = getConnection();
			return getRows(conn, sql, args);
		} catch (SQLException e) {
			return 0;
		} finally {
			close(conn);
		}
	}
	/** 表记录总数 */
	public static final long getTotal(String table) {
		try {
			return jw.getTotal(table);
		} catch (SQLException e) {
			return 0;
		}
	}

	public static final long getMaxs(String table, String col) throws SQLException {
		Connection conn = jw.getConnection();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			StringBuilder sql = new StringBuilder();
			ResultSet rs = stmt.executeQuery(sql.append("SELECT MAX(").append(col).append(") FROM ").append(table).toString());
			long max = 0; // 最大值 默认为0
			if (rs.next()) {
				max = rs.getLong(1);
			} // return result
			rs.close(); return max;
		} finally {
			jw.close(stmt, conn);
		}
	}

	public static final boolean isExists(Connection conn, String sql, Object... args) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			for (int i = 1; i <= args.length; i++) {
				ps.setObject(i, args[i-1]);
			}
			ResultSet rs = ps.executeQuery();
			boolean result = false;
			if (rs.next()) {
				result = true;
			}
			rs.close(); return result;
		} finally {
			if (ps != null) ps.close();
		}
	}

	public static final boolean isExists(String sql, int num) {
		try {
			return jw.isExists(sql, num);
		} catch (SQLException e) {
			return true;
		}
	}

	public static final boolean isExists(String sql, long num) {
		try {
			return jw.isExists(sql, num);
		} catch (SQLException e) {
			return true;
		}
	}

	public static final boolean isExists(String sql, String str) {
		try {
			return jw.isExists(sql, str);
		} catch (SQLException e) {
			return true;
		}
	}

	public static final boolean isExists(String sql, Object... args) {
		try {
			return jw.isExists(sql, args);
		} catch (SQLException e) {
			return true;
		}
	}

}
