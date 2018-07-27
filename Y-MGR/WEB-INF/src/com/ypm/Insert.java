package com.ypm;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import com.ypm.data.JPrepare;

public class Insert {

	private static void add(StringBuilder sb, ResultSetMetaData r, int i) throws SQLException {
		int type = r.getColumnType(i);
		switch (type) {
		case -7: // TINYINT(1)
		case -6: // TINYINT(2)
			sb.append("Int(").append(i);
			break;
		case -5: // BIGINT
			sb.append("Long(").append(i);
			break;
		case 3: // DECIMAL
			sb.append("BigDecimal(").append(i);
			break;
		case 4: // INT
			sb.append("Int(").append(i);
			break;
		case 5: // SMALLINT
			sb.append("Int(").append(i);
			break;
		case 12: // VARCHAR
			sb.append("String(").append(i);
			break;
		default:
			System.out.println(i + "\t=" + type + "\t" + r.getColumnTypeName(i));
		}
	}

	public static void insert(String table, String obj) throws SQLException {
		StringBuilder sb = new StringBuilder();
		StringBuilder buf = new StringBuilder();
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT * FROM " + table);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData r = rs.getMetaData();
			int j = r.getColumnCount(); // 获取列数
			sb.append("ps = conn.prepareStatement(\"INSERT INTO ").append(table).append(" (").append(r.getColumnName(1));
			for (int i = 2; i <= j; i++) {
				buf.append(",?");
				sb.append(',').append(r.getColumnName(i));
			}
			sb.append(") VALUES (?").append(buf.toString()).append(")\");\r");
			for (int i = 1; i <= j; i++) {
				add(sb.append("ps.set"), r, i);
				String n = r.getColumnName(i); // 例名
				sb.append(", ").append(obj).append(".get").append(n.substring(0, 1).toUpperCase()).append(n.substring(1).toLowerCase()).append("());\r");
			}
			rs.close();
			sb.append("ps.executeUpdate();\r");
			System.out.println(j);
			System.out.println(sb.toString());
		} finally {
			JPrepare.close(ps, conn);
			buf.setLength(0);
			sb.setLength(0);
		}
	}

	public static void query(String table, String obj) throws SQLException {
		StringBuilder sb = new StringBuilder();
		StringBuilder buf = new StringBuilder();
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			Map<Integer, String> m = new TreeMap<Integer, String>();
			DatabaseMetaData dm = conn.getMetaData();
			ResultSet rs = dm.getPrimaryKeys(null, null, table);
			while (rs.next()) {
				m.put(rs.getInt(5), rs.getString(4));
			}
			rs.close();
			ps = conn.prepareStatement("SELECT * FROM " + table);
			rs = ps.executeQuery();
			ResultSetMetaData r = rs.getMetaData();
			int j = r.getColumnCount(); // 获取列数
			sb.append("Connection conn = JPrepare.getConnection();\rPreparedStatement ps = null;\rtry {\r");
			sb.append("\tps = conn.prepareStatement(\"SELECT ").append(r.getColumnName(1));
			for (int i = 2; i <= j; i++) {
				sb.append(',').append(r.getColumnName(i));
			}
			sb.append(" FROM ").append(table);
			int index = 1;
			for (String name : m.values()) {
				if (index == 1) {
					sb.append(" WHERE ");
				} else {
					sb.append(" AND ");
				}
				sb.append(name).append("=?");
				buf.append("\tps.setLong(").append(index).append(", #);\r");
				index += 1;
			}
			sb.append("\");\r").append(buf.toString()).append("\tResultSet rs = ps.executeQuery();\r\tif (rs.next()) {\r\t\t").append(obj).append("= new #();\r");
			for (int i = 1; i <= j; i++) {
				String n = r.getColumnName(i); // 例名
				sb.append("\t\t").append(obj).append(".set").append(n.substring(0, 1).toUpperCase()).append(n.substring(1).toLowerCase()).append("(rs.get");
				add(sb, r, i);
				sb.append("));\r");
			}
			rs.close();
			sb.append("\t}\r\trs.close();\r\treturn ").append(obj).append(";");
			sb.append("\r} finally {\r\tJPrepare.close(ps, conn);\r}");
			System.out.println(j);
			System.out.println(sb.toString());
		} finally {
			JPrepare.close(ps, conn);
			buf.setLength(0);
			sb.setLength(0);
		}
	}

	public static void main(String[] args) throws Exception {
		//insert("prod_info", "info");
		query( "channel_count", "c" );
		insert( "channel_count", "c" );
	}
}
