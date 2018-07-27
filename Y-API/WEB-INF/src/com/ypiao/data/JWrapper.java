package com.ypiao.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

public interface JWrapper {

	void setDataSource(DataSource ds);

	public Connection getConnection() throws SQLException;
	/** 检查优化索引 */
	public void checkIndex() throws SQLException;
	/** 检测优化索引 */
	public void checkIndex(String name) throws SQLException;

	public void checkTable(String file, String... args) throws SQLException;

	public void close(Connection conn);

	public void close(CallableStatement cs, Connection conn);

	public void close(PreparedStatement ps, Connection conn);

	public void close(Statement stmt, Connection conn);

	public boolean execute(String sql) throws SQLException;

	public boolean execute(String table, String[] ts) throws Exception;

	public String getQuery(String sql, int max);

	public String getQuery(String sql, int offset, int max);

	public String getQuery(String sqlA, int max, String type, String sqlB, String info);

	public String getQuery(String sqlA, int offset, int max, String type, String sqlB, String info);

	public String getQuery(StringBuilder sql, int max);

	public String getQuery(StringBuilder sql, int offset, int max);

	public String getUpdate(String tblA, String tblB, String info, String join);

	public long getTotal(String table) throws SQLException;

	public boolean isExists(String sql, int num) throws SQLException;

	public boolean isExists(String sql, long num) throws SQLException;

	public boolean isExists(String sql, String str) throws SQLException;

	public boolean isExists(String sql, Object... args) throws SQLException;

}
