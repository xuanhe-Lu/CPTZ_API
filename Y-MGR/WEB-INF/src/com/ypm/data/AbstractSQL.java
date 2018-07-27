package com.ypm.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;

public abstract class AbstractSQL implements JWrapper {

	protected static final Logger logger = LoggerFactory.getLogger(JWrapper.class);

	protected static final String NR = System.getProperty("line.separator");
	/** 缓存表Auto */
	protected Map<String, String> AIs = new HashMap<String, String>();
	/** 缓存表主键 */
	protected Map<String, String> PKs = new HashMap<String, String>();
	/** 数据库名称 */
	protected String database = null;
	/** 链接数据源 */
	protected DataSource ds = null;
	/** 数据库版本 */
	protected int version = 2000;

	protected AbstractSQL(DataSource ds) {
		Connection conn = null;
		try {
			this.ds = ds;
			conn = ds.getConnection();
			this.checkTables(conn, false);
		} catch (SQLException e) {
			// Ignored
		} finally {
			this.close(conn);
		}
	}

	protected AbstractSQL(final String driver, final String url, final String user, final String pass) throws SQLException {
		this(true, driver, url, user, pass);
	}

	protected AbstractSQL(final boolean create, final String driver, final String url, final String user, final String pass) throws SQLException {
		if (driver == null || url == null) return; // 不检测表源
		Connection conn = this.checkSource(driver, url, user, pass);
		try {
			this.checkTables(conn, create);
		} finally {
			this.close(conn);
		}
	}
	/** 检测数据链接可用性 */
	protected abstract Connection checkSource(final String driver, final String url, final String user, final String pass) throws SQLException;
	/** 检测数据结构完整性 */
	protected abstract void checkTables(Connection conn, boolean isCreate) throws SQLException;

	public void setDataSource(DataSource ds) {
		this.ds = ds;
	}

	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	public void close(Connection conn) {
		if (conn != null) {
			try {
				if (!conn.isClosed()) conn.close();
			} catch (SQLException e) {
				// ignored
			} finally {
				conn = null;
			}
		}
	}

	public void close(CallableStatement cs, Connection conn) {
		if (cs == null) {
			this.close(conn);
		} else {
			try {
				try {
					if (!cs.isClosed()) cs.close();
				} catch (Exception e) {
					cs.close();
				} finally {
					if (conn != null && !conn.isClosed()) conn.close();
				}
			} catch (SQLException e) {
				// Ignored
			} finally {
				conn = null; cs = null;
			}
		}
	}

	public void close(PreparedStatement ps, Connection conn) {
		if (ps == null) {
			this.close(conn);
		} else {
			try {
				try {
					if (!ps.isClosed()) ps.close();
				} catch (Exception e) {
					ps.close();
				} finally {
					if (conn != null && !conn.isClosed()) conn.close();
				}
			} catch (SQLException e) {
				// Ignored
			} finally {
				conn = null; ps = null;
			}
		}
	}

	public void close(Statement stmt, Connection conn) {
		if (stmt == null) {
			this.close(conn);
		} else {
			try {
				try {
					if (!stmt.isClosed()) stmt.close();
				} catch (Exception e) {
					stmt.close();
				} finally {
					if (conn != null && !conn.isClosed()) conn.close();
				}
			} catch (SQLException e) {
				// Ignored
			} finally {
				conn = null; stmt = null;
			}
		}
	}

	public abstract boolean execute(String sql) throws SQLException;

	protected String getPrimaryKeys(String table) {
		String keys = PKs.get(table);
		if (keys != null) return keys;
		Connection conn = null;
		try {
			conn = this.getConnection();
			DatabaseMetaData dm = conn.getMetaData();
			ResultSet rs = dm.getPrimaryKeys(null, null, table);
			StringBuffer sb = new StringBuffer();
			while (rs.next()) {
				sb.append(",").append(rs.getString(4));
			}
			rs.close();
			if (sb.length() > 0) {
				PKs.put(table, sb.substring(1));
			}
			return PKs.get(table);
		} catch (Exception e) {
			return null;
		} finally {
			this.close(conn);
		}
	}

}
