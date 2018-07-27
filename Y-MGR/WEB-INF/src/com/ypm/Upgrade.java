package com.ypm;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.ypm.data.JPrepare;

public class Upgrade {
	/** 更新编号，连续处理 */
	public static void updateNumber(String table, String name, String order) throws SQLException {
		StringBuilder sql = new StringBuilder();
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql.append("SELECT MAX(").append(name).append(") FROM ").append(table).toString());
			ResultSet rs = ps.executeQuery();
			sql.setLength(0);
			if (rs.next()) {
				sql.append("UPDATE ").append(table).append(" SET ").append(name).append("=").append(name).append("+").append(rs.getInt(1));
			}
			rs.close();
			if (sql.length() <= 0) {
				System.out.println("无需处理");
				return; // 结束
			}
			ps.close();
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			ps.close(); sql.setLength(0);
			ps = JPrepare.prepareStatement(conn, sql.append("SELECT ").append(name).append(" FROM ").append(table).append(" ORDER BY ").append(order).toString());
			rs = ps.executeQuery();
			int index = 1;
			while (rs.next()) {
				rs.updateInt(1, index++);
				rs.updateRow();
			}
			rs.close();
			conn.commit();
			System.out.println("处理完成，共计：" + index);
		} catch (SQLException e) {
			conn.rollback(); throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
	/** 根据表名加载新报表字典 */
	public static void setField(List<String> ls, String sno, String PK) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			int sid = 0; // 加载分类编号
			ps = conn.prepareStatement("SELECT Sid FROM field_menu WHERE SNo=?");
			ps.setString(1, sno);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				sid = rs.getInt(1);
			}
			rs.close();
			if (sid <= 1) {
				System.out.println("标识信息不存在！");
				return;
			}
			ps.close();
			int id = 1; // 起始编号
			ps = conn.prepareStatement("SELECT MAX(Id) FROM field_info");
			rs = ps.executeQuery();
			if (rs.next()) {
				id += rs.getInt(1);
			}
			rs.close();
			ps.close(); // 删除已存在信息
			ps = conn.prepareStatement("DELETE FROM field_info WHERE Sid=?");
			ps.setInt(1, sid);
			ps.executeUpdate();
			ps.close(); // 写入新信息
			ps = conn.prepareStatement("INSERT INTO field_info (Id,Sid,Sdef,Sortid,Name,Nice,Width,Pkey,Display,Sortab,Export,Type,Format) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
			int index = 1, len = ls.size();
			String name = null; // 名称
			for (int i = 0; i < len; i++) {
				name = ls.get(i);
				ps.setInt(1, id++);
				ps.setInt(2, sid);
				ps.setInt(3, index);
				ps.setInt(4, index++);
				ps.setString(5, name);
				ps.setString(6, name);
				ps.setInt(7, 60);
				if (name.equalsIgnoreCase(PK)) {
					ps.setInt(8, 1);
				} else {
					ps.setInt(8, 0);
				}
				ps.setInt(9, 1);
				ps.setInt(10, 1);
				ps.setInt(11, 1);
				ps.setInt(12, 0);
				ps.setString(13, null);
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback(); throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public static void setField(String table, String sno, String PK) throws SQLException {
		Connection conn = JPrepare.getConnection();
		List<String> ls = new ArrayList<String>();
		try {
			conn.setAutoCommit(false);
			DatabaseMetaData dm = conn.getMetaData();
			ResultSet rs = dm.getColumns(null, null, table, null);
			while (rs.next()) {
				ls.add(rs.getString(4));
			}
			rs.close();
		} finally {
			JPrepare.close(conn);
		}
		if (ls.size() > 0) {
			setField(ls, sno, PK);
		} else {
			System.out.println("数据结构不存在！");
		}
	}

	public static void main(String[] args) throws SQLException {
//		Upgrade.updateNumber("field_info", "Id", "Sid ASC,Sdef ASC");
		
		// 票友学堂
		/*List<String> ls = new ArrayList<String>();
		ls.add( "Sid" );
		ls.add( "Title" );
		ls.add( "Subject" );
		ls.add( "Position" );
		ls.add( "Author" );
		ls.add( "Detail" );
		ls.add( "Facer" );
		ls.add( "State" );
		ls.add( "Time" );
		Upgrade.setField( ls, "coo.xue.info", "Sid" );*/
		
		// 通知管理
		/*List<String> ls = new ArrayList<String>();
		ls.add( "Sid" );
		ls.add( "Tid" );
		ls.add( "Position" );
		ls.add( "Title");
		ls.add( "Author" );
		ls.add( "Detail" );
		ls.add( "Sday" );
		ls.add( "State" );
		ls.add( "Time" );
		Upgrade.setField( ls, "coo.ader.note", "Sid" );*/
		
		// 常见问题
		/*List<String> ls = new ArrayList<String>();
		ls.add( "Sid" );
		ls.add( "Tid" );
		ls.add( "Question" );
		ls.add( "Answer" );
		ls.add( "Sortid" );
		ls.add( "State" );
		ls.add( "Time" );
		Upgrade.setField( ls, "coo.help", "Sid" );*/
		
		// 新闻管理
		/*List<String> ls = new ArrayList<String>();
		ls.add( "Sid" );
		ls.add( "Title" );
		ls.add( "Subject" );
		ls.add( "Position" );
		ls.add( "Author" );
		ls.add( "Detail" );
		ls.add( "Facer" );
		ls.add( "State" );
		ls.add( "Time" );
		Upgrade.setField( ls, "coo.news", "Sid" );*/
		
		// 渠道统计
		/*List<String> ls = new ArrayList<String>();
		ls.add( "Sid" );
		ls.add( "Tid" );
		ls.add( "Raddr" );
		ls.add( "Daddr" );
		ls.add( "Name" );
		ls.add( "Rcount" );
		ls.add( "Rname" );
		ls.add( "Bcount" );
		ls.add( "Ncount" );
		ls.add( "Nmoney" );
		ls.add( "Fcount" );
		ls.add( "Scount" );
		ls.add( "Fmoney" );
		ls.add( "Smoney" );
		ls.add( "Tzsum" );
		ls.add( "Txsum" );
		ls.add( "Nastock" );
		ls.add( "Sstock" );
		ls.add( "Time" );
		Upgrade.setField( ls, "coo.popular.channel", "Sid" );*/
		
		// 推送管理
		/*List<String> ls = new ArrayList<String>();
		ls.add( "Sid" );
		ls.add( "Title" );
		ls.add( "Content" );
		ls.add( "System" );
		ls.add( "Type" );
		ls.add( "Target" );
		ls.add( "State" );
		ls.add( "Time" );
		Upgrade.setField( ls, "coo.push", "Sid" );*/
		
		// 版本更新
		/*List<String> ls = new ArrayList<String>();
		ls.add( "Sid" );
		ls.add( "Tid" );
		ls.add( "Num" );
		ls.add( "Mid" );
		ls.add( "Size" );
		ls.add( "Filename" );
		ls.add( "Dist" );
		ls.add( "Description" );
		ls.add( "Time" );
		Upgrade.setField( ls, "coo.version.update", "Sid" );*/
		
		// 渠道汇总
		/*List<String> ls = new ArrayList<String>();
		ls.add( "Sum" );
		ls.add( "Stock" );
		ls.add( "Noexpire" );
		ls.add( "Income" );
		ls.add( "Rcount" );
		ls.add( "Acount" );
		ls.add( "Nacount" );
		ls.add( "Bcount" );
		ls.add( "Nbcount" );
		ls.add( "Tzcount" );
		ls.add( "Ntzcount" );
		ls.add( "Txsum" );
		ls.add( "Tzzs" );
		ls.add( "Czsxf" );
		ls.add( "Txsxf" );
		ls.add( "Time" );
		Upgrade.setField( ls, "coo.channel.sum", null );*/
		
		// 福利专区
		/*List<String> ls = new ArrayList<String>();
		ls.add( "Sid" );
		ls.add( "Type" );
		ls.add( "State" );
		ls.add( "Title" );
		ls.add( "Url" );
		ls.add( "Dist" );
		ls.add( "Ver" );
		ls.add( "Time" );
		Upgrade.setField( ls, "coo.activity.welfare", "Sid" );*/
		
		// 系统-账号管理
		/*List<String> ls = new ArrayList<String>();
		ls.add( "UserId" );
		ls.add( "UserNo" );
		ls.add( "UserName" );
		ls.add( "PassWord" );
		ls.add( "RealName" );
		ls.add( "Menu" );
		ls.add( "Dept" );
		ls.add( "Job" );
		ls.add( "Org" );
		ls.add( "State" );
		ls.add( "Soger" );
		ls.add( "FailCt" );
		ls.add( "Super" );
		ls.add( "Time" );
		Upgrade.setField( ls, "system.um.manage", "UserId" );*/
		
		// 更换银行卡
		/*List<String> ls = new ArrayList<String>();
		ls.add( "Sid" );
		ls.add( "Uid" );
		ls.add( "Bid" );
		ls.add( "CNo" );
		ls.add( "Code" );
		ls.add( "Mobile" );
		ls.add( "Name" );
		//ls.add( "BA" );
		//ls.add( "BB" );
		//ls.add( "BC" );
		//ls.add( "BD" );
		ls.add( "State" );
		ls.add( "Time" );
		ls.add( "adM" );
		ls.add( "adN" );
		Upgrade.setField( ls, "kefu.user.bank", "Sid" );*/
		
		// 预购订单
		List<String> ls = new ArrayList<String>();
		ls.add( "Sid" );
		ls.add( "Uid" );
		ls.add( "Cid" );
		ls.add( "Tid" );
		ls.add( "Name" );
		ls.add( "Rate" );
		ls.add( "Rday" );
		ls.add( "Day" );
		ls.add( "Way" );
		ls.add( "Ads" );
		ls.add( "TMA" );
		ls.add( "TMB" );
		ls.add( "TMC" );
		ls.add( "TMD" );
		ls.add( "TME" );
		ls.add( "TMF" );
		ls.add( "TMG" );
		ls.add( "YMA" );
		ls.add( "YMB" );
		ls.add( "GmtA" );
		ls.add( "GmtB" );
		ls.add( "GmtC" );
		ls.add( "Mobile" );
		ls.add( "Nicer" );
		ls.add( "State" );
		ls.add( "Stext" );
		ls.add( "Time" );
		Upgrade.setField( ls, "cfo.user.book", "Sid" );
	}

}
