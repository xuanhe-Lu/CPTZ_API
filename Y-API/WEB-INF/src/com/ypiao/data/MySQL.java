package com.ypiao.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import com.ypiao.data.sql.ReadUtils;

public class MySQL extends AbstractSQL {

	private static final String SQL_TYPE = "mysql";

	public MySQL(DataSource ds) {
		super(ds);
	}

	protected MySQL(final String driver, final String url, final String user, final String pass) throws Exception {
		super(driver, url, user, pass);
	}

	protected MySQL(final boolean create, final String driver, final String url, final String user, final String pass) throws Exception {
		super(create, driver, url, user, pass);
	}

	public void checkIndex() throws SQLException {
		StringBuilder sql = new StringBuilder();
		Connection conn = this.getConnection();
		try {
			ResultSet rs = conn.getMetaData().getTables(null, null, null, new String[] { "TABLE" });
			while (rs.next()) {
				sql.append(",`").append(rs.getString(3)).append("`");
			}
			rs.close();
			if (sql.length() > 2) {
				String info = sql.substring(1);
				sql.setLength(0); // 检查表
				this.executeUpdate(conn, sql.append("CHECK TABLE ").append(info).toString());
				sql.setLength(0); // 分析表
				this.executeUpdate(conn, sql.append("ANALYZE TABLE ").append(info).toString());
				sql.setLength(0); // 修复表
				this.executeUpdate(conn, sql.append("REPAIR TABLE ").append(info).toString());
				sql.setLength(0); // 优化表
				this.executeUpdate(conn, sql.append("OPTIMIZE TABLE ").append(info).toString());
			}
		} finally {
			this.close(conn);
			sql.setLength(0);
		}
	}

	public void checkIndex(String name) throws SQLException {
		StringBuilder sql = new StringBuilder();
		Connection conn = this.getConnection();
		try {
			this.executeUpdate(conn, sql.append("CHECK TABLE `").append(name).append("`").toString());
			sql.setLength(0); // 分析表
			this.executeUpdate(conn, sql.append("ANALYZE TABLE `").append(name).append("`").toString());
			sql.setLength(0); // 修复表
			this.executeUpdate(conn, sql.append("REPAIR TABLE `").append(name).append("`").toString());
			sql.setLength(0); // 优化表
			this.executeUpdate(conn, sql.append("OPTIMIZE TABLE `").append(name).append("`").toString());
		} finally {
			this.close(conn);
			sql.setLength(0);
		}
	}

	@Override
	protected Connection checkSource(String driver, String url, String user, String pass) throws Exception {
		StringBuilder sb = new StringBuilder();
		Matcher m = Pattern.compile("(jdbc:(.*))/(\\w+)(\\?|\\s+)", Pattern.CASE_INSENSITIVE).matcher(url);
		if (m.find()) {
			sb.append(m.group(1));
			this.database = m.group(3).toLowerCase();
		} else {
			throw new SQLException("Unknown database for MySQL Server!");
		}
		sb.append("?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true");
		Class.forName(driver); // 注册驱动
		int seconds = DriverManager.getLoginTimeout();
		try {
			DriverManager.setLoginTimeout(5);
			return DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			if (e.getMessage().indexOf(this.database) == -1)
				throw e;
			Connection conn = DriverManager.getConnection(sb.toString(), user, pass);
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement("SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME=?");
				ps.setString(1, this.database);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					// Ignored
				} else {
					sb.setLength(0);
					sb.append("CREATE DATABASE IF NOT EXISTS `").append(this.database).append("` DEFAULT CHARACTER SET UTF8 COLLATE 'utf8_general_ci';");
					this.executeUpdate(conn, sb.toString());
				}
				rs.close();
			} finally {
				this.close(ps, conn);
			} // 创建检测链接
			return DriverManager.getConnection(url, user, pass);
		} finally {
			DriverManager.setLoginTimeout(seconds);
		}
	}

	public void checkTable(String file, String... arr) throws SQLException {
		String sql = ReadUtils.readSQL(SQL_TYPE, file);
		if (sql == null)
			return;
		try {
			for (int i = 0, j = arr.length; i < j; i++) {
				sql = sql.replace("{" + i + "}", arr[i]);
			}
			this.execute(sql);
		} finally {
			sql = null;
		}
	}

	@Override
	protected void checkTables(Connection conn, boolean isCreate) throws SQLException {
		StringBuilder sql = new StringBuilder();
		ResultSet rs = conn.getMetaData().getTables(null, null, null, new String[] { "TABLE" });
		while (rs.next()) {
			sql.append(",`").append(rs.getString(3)).append("`");
		}
		rs.close(); // 检测完整性
		if (sql.length() > 2) {
			// String info = sql.substring(1);
			// sql.setLength(0); // 检查表
			// this.executeUpdate(conn, sql.append("CHECK TABLE
			// ").append(info).toString());
			// sql.setLength(0); // 分析表
			// this.executeUpdate(conn, sql.append("ANALYZE TABLE
			// ").append(info).toString());
			// sql.setLength(0); // 修复表
			// this.executeUpdate(conn, sql.append("REPAIR TABLE
			// ").append(info).toString());
			// sql.setLength(0); // 优化表
			// this.executeUpdate(conn, sql.append("OPTIMIZE TABLE
			// ").append(info).toString());
		} // 创建表信息
		if (isCreate) {
			String str = ReadUtils.readSQL(SQL_TYPE, "sys");
			if (str != null) {
				this.executeUpdate(conn, str);
			}
		}
	}

	private boolean execute(Connection conn, String sql) throws SQLException {
		Statement stmt = conn.createStatement();
		try {
			return stmt.execute(sql);
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	public boolean execute(String sql) throws SQLException {
		Connection conn = getConnection();
		try {
			return execute(conn, sql);
		} finally {
			close(conn);
		}
	}

	public boolean execute(String table, String[] ts) throws Exception {
		return false;
	}

	public int executeUpdate(Connection conn, String sql) throws SQLException {
		Statement stmt = conn.createStatement();
		try {
			return stmt.executeUpdate(sql);
		} finally {
			stmt.close();
		}
	}

	public String generate(String type) throws SQLException {
		return null;
	}

	public String getExists(String sql) {
		return this.getQuery(new StringBuilder(sql), 1);
	}

	public String getQuery(String sql, int max) {
		return this.getQuery(new StringBuilder(sql), max);
	}

	public String getQuery(String sqlA, int max, String type, String sqlB, String info) {
		return this.getQuery(sqlA, 0, max, type, sqlB, info);
	}

	public String getQuery(String sqlA, int offset, int max, String type, String sqlB, String info) {
		StringBuilder sql = new StringBuilder();
		if (max <= 0) {
			sql.append("SELECT * FROM (").append(sqlA.trim()).append(") A ");
		} else {
			sql.append("SELECT * FROM (").append(sqlA.trim()).append(" LIMIT ").append(offset).append(",").append(max).append(") A ");
		}
		if (type == null || type.equalsIgnoreCase("inner")) {
			sql.append("INNER JOIN");
		} else if (type.equalsIgnoreCase("left")) {
			sql.append("LEFT JOIN");
		} else {
			sql.append("RIGHT JOIN");
		} // 附加信息
		Matcher m = Pattern.compile("select(.*?)from(\\s+)\\[?([\\w]+)$", Pattern.CASE_INSENSITIVE).matcher(sqlB);
		if (m.find()) {
			String str = m.group(1).trim();
			if (str.indexOf(",") == -1) {
				// Ignored
			} else {
				sql.replace(7, 8, "A.*,B.").insert(13, str.replace(",", ",B."));
			}
			sql.append(" ").append(m.group(3)).append(" AS B");
		} else {
			sql.append(" (").append(sqlB).append(") B");
		}
		return sql.append(" ON ").append(info).toString();
	}

	public String getQuery(String sql, int offset, int max) {
		StringBuilder sb = new StringBuilder();
		if (offset > 5000) {
			Matcher m = Pattern.compile("ORDER(\\s+)BY(\\s+)([\\w.]+)", Pattern.CASE_INSENSITIVE).matcher(sql);
			if (m.find()) {
				String str = sql.toLowerCase();
				int index = str.indexOf("order");
				sb.append(sql.substring(0, index));
				if (str.indexOf("where") == -1) {
					sb.append("WHERE ").append(m.group(3));
				} else {
					sb.append("AND ").append(m.group(3));
				}
				if (str.indexOf("desc") == -1) {
					sb.append(">=");
				} else {
					sb.append("<=");
				}
				sb.append("(SELECT ").append(m.group(3)).append(sql.substring(str.indexOf(" from"))).append(" LIMIT ").append(offset);
				return sb.append(",1) ").append(sql.substring(index)).append(" LIMIT 0,").append(max).toString();
			}
		} else if (offset < 0) {
			offset = 0;
		} // return sql info
		return sb.append(sql).append(" LIMIT ").append(offset).append(",").append(max).toString();
	}

	public String getQuery(StringBuilder sql, int max) {
		return sql.append(" LIMIT 0,").append(max).toString();
	}

	public String getQuery(StringBuilder sql, int offset, int max) {
		if (offset > 5000) {
			String text = sql.toString();
			Matcher m = Pattern.compile("ORDER(\\s+)BY(\\s+)([\\w.]+)", Pattern.CASE_INSENSITIVE).matcher(text);
			if (m.find()) {
				sql.setLength(0);
				String str = text.toLowerCase();
				int index = str.indexOf("order");
				sql.append(text.substring(0, index));
				if (str.indexOf("where") == -1) {
					sql.append("WHERE ").append(m.group(3));
				} else {
					sql.append("AND ").append(m.group(3));
				}
				if (str.indexOf("desc") == -1) {
					sql.append(">=");
				} else {
					sql.append("<=");
				}
				sql.append("(SELECT ").append(m.group(3)).append(text.substring(str.indexOf(" from"))).append(" LIMIT ").append(offset);
				return sql.append(",1) ").append(text.substring(index)).append(" LIMIT 0,").append(max).toString();
			}
		} else if (offset < 0) {
			offset = 0;
		} // return sql info
		return sql.append(" LIMIT ").append(offset).append(",").append(max).toString();
	}

	public String getUpdate(String tblA, String tblB, String info, String join) {
		StringBuilder sql = new StringBuilder(200); // 构建更新语句
		sql.append("UPDATE ").append(tblA).append(" AS A,");
		if (tblB.indexOf(' ') == -1) {
			sql.append(tblB);
		} else {
			sql.append('(').append(tblB).append(')');
		} // return sql info
		sql.append(" AS B SET A.").append(info.replaceAll(",(\\w+)=", ",A.$1=")).append(" WHERE ").append(join.replaceAll("(?i)WHERE", "AND"));
		return sql.toString();
	}

	public int getRows(String sql, Object... args) throws SQLException {
		Connection conn = this.getConnection();
		PreparedStatement ps = null;
		try {
			int defVal = 0;
			ps = conn.prepareStatement(sql);
			for (int i = 1, j = args.length; i <= j; i++) {
				ps.setObject(i, args[i - 1]);
			}
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				defVal = rs.getInt(1);
			} // return result
			rs.close();
			return defVal;
		} finally {
			this.close(ps, conn);
		}
	}

	public long getTotal(String table) throws SQLException {
		Connection conn = this.getConnection();
		Statement stmt = null;
		try {
			long defVal = 0;
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
			if (rs.next()) {
				defVal = rs.getLong(1);
			}
			rs.close();
			return defVal;
		} finally {
			this.close(stmt, conn);
		}
	}

	public boolean isExists(String sql, int num) throws SQLException {
		Connection conn = this.getConnection();
		PreparedStatement ps = null;
		try {
			boolean result = false;
			ps = conn.prepareStatement(this.getExists(sql));
			ps.setInt(1, num);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = true;
			} // 释放内存
			rs.close();
			return result;
		} finally {
			this.close(ps, conn);
		}
	}

	public boolean isExists(String sql, long num) throws SQLException {
		Connection conn = this.getConnection();
		PreparedStatement ps = null;
		try {
			boolean result = false;
			ps = conn.prepareStatement(this.getExists(sql));
			ps.setLong(1, num);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = true;
			} // 释放内存
			rs.close();
			return result;
		} finally {
			this.close(ps, conn);
		}
	}

	public boolean isExists(String sql, String str) throws SQLException {
		Connection conn = this.getConnection();
		PreparedStatement ps = null;
		try {
			boolean result = false;
			ps = conn.prepareStatement(this.getExists(sql));
			ps.setString(1, str);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = true;
			} // 释放内存
			rs.close();
			return result;
		} finally {
			this.close(ps, conn);
		}
	}

	public boolean isExists(String sql, Object... args) throws SQLException {
		Connection conn = this.getConnection();
		PreparedStatement ps = null;
		try {
			boolean result = false;
			ps = conn.prepareStatement(this.getExists(sql));
			for (int i = 1, j = args.length; i <= j; i++) {
				ps.setObject(i, args[i - 1]);
			}
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = true;
			} // 释放内存
			rs.close();
			return result;
		} finally {
			this.close(ps, conn);
		}
	}
}
