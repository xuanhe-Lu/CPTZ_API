package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.Help;
import com.ypiao.data.JPrepare;
import com.ypiao.service.HelpInfoService;
import com.ypiao.util.GMTime;

/**
 * Created by xk on 2018-05-10.
 * 
 * 常见问题信息同步APS接口实现类.
 */
public class HelpInfoServiceImp extends AConfig implements HelpInfoService {
	
	// 表名
	private static final String TBL_COMM_HELP = "comm_help";

	protected void checkSQL() {
	}

	public AjaxInfo sendByAll(AjaxInfo json) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			json.datas(API_OK);
			ps = conn.prepareStatement("SELECT Sid,Sortid,Question,Answer,State,Time FROM comm_help WHERE State=? ORDER BY Sortid ASC");
			ps.setInt(1, STATE_ENABLE);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("q", rs.getString(3));
				json.append("a", rs.getString(4));
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 常见问题数据列表获取
	 */
	public AjaxInfo sendByList(AjaxInfo json) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		
		try {
			json.datas(API_OK);
			ps = conn.prepareStatement( "SELECT Sid, Sortid, Question, Answer, State, Time, Tid FROM comm_help WHERE State = ? ORDER BY Sortid ASC" );
			ps.setInt( 1, STATE_ENABLE );
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append( "SID", rs.getString(1) );
				json.append( "SORTID", rs.getInt(2) ); 
				json.append( "QUESTION", rs.getString(3) ); 
				json.append( "ANSWER", rs.getString(4) );
				json.append( "STATE", rs.getInt(5) );
				json.append( "TIME", GMTime.format( rs.getLong(6), GMTime.CHINA ) );
				json.append( "TID", rs.getInt(7) );
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close( ps, conn );
		}
	}

	/**
	 * @author xk
	 * @param help is Help
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	public void save(Help help) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = conn.prepareStatement( "UPDATE " + TBL_COMM_HELP + " SET Tid = ?, Question = ?, Answer = ?, Sortid = ?, State = ?, Time = ? WHERE Sid = ?" );
		
		try {
			ps.setInt( 1, help.getTid() );
			ps.setString( 2, help.getQuestion() );
			ps.setString( 3, help.getAnswer() );
			ps.setInt( 4, help.getSortid() );
			ps.setInt( 5, help.getState() );
			ps.setLong( 6, help.getTime() );
			ps.setString( 7, help.getSid() );
			
			// 若更新操作失败，则开始新增
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement( "INSERT INTO " + TBL_COMM_HELP + " (Sid, Tid, Question, Answer, Sortid, State, Time) VALUES (?, ?, ?, ?, ?, ?, ?)" );
				
				ps.setString( 1, help.getSid() );
				ps.setInt( 2, help.getTid() );
				ps.setString( 3, help.getQuestion() );
				ps.setString( 4, help.getAnswer() );
				ps.setInt( 5, help.getSortid() );
				ps.setInt( 6, help.getState() );
				ps.setLong( 7, help.getTime() );
				
				ps.execute();
			}
		} finally {
			JPrepare.close( ps, conn );
		}
	}

	/**
	 * @author xk
	 * @param sid string
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement( "DELETE FROM " + TBL_COMM_HELP + " WHERE Sid = ?" );
			ps.setString( 1, sid );
			ps.execute();
			conn.commit();
		} catch (SQLException e) {
			// 异常回滚
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close( ps, conn );
		}
	}

	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 根据常见问题分类id获取数据
	 */
	public AjaxInfo listByTid(AjaxInfo json, int tid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		
		try {
			json.datas(API_OK);
			ps = conn.prepareStatement( "SELECT Sid, Sortid, Question, Answer, State, Time, Tid FROM comm_help WHERE State = ? AND Tid = ? ORDER BY Sortid ASC" );
			ps.setInt( 1, STATE_ENABLE );
			ps.setInt( 2, tid );
			
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append( "sid", rs.getString(1) );
				json.append( "sortid", rs.getInt(2) ); 
				json.append( "question", rs.getString(3) ); 
				json.append( "answer", rs.getString(4) );
				json.append( "state", rs.getInt(5) );
				json.append( "time", GMTime.format( rs.getLong(6), GMTime.CHINA ) );
				json.append( "tid", rs.getInt(7) );
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close( ps, conn );
		}
	}
	
	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @param type int
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 根据常见问题分类id获取数据
	 */
	public AjaxInfo listByType(AjaxInfo json, int type) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		
		try {
			json.datas(API_OK);
			String sql1 = "SELECT Sid, Sortid, Question, Answer, State, Time, Tid FROM comm_help WHERE State = ? AND Tid = ? ORDER BY Sortid ASC";
			String sql2 = "SELECT Sid, Sortid, Question, Answer, State, Time, Tid FROM comm_help WHERE State = ? AND (Tid = ? OR Tid = ?) ORDER BY Sortid ASC";
			switch (type) {
			case 0:
				ps = conn.prepareStatement( sql1 );
				ps.setInt( 1, STATE_ENABLE );
				ps.setInt( 2, 1 );
				break;
				
			case 1:
				ps = conn.prepareStatement( sql2 );
				ps.setInt( 1, STATE_ENABLE );
				ps.setInt( 2, 2 );
				ps.setInt( 3, 3 );
				break;
				
			case 2:
				ps = conn.prepareStatement( sql2 );
				ps.setInt( 1, STATE_ENABLE );
				ps.setInt( 2, 4 );
				ps.setInt( 3, 5 );
				break;
				
			case 3:
				ps = conn.prepareStatement( sql2 );
				ps.setInt( 1, STATE_ENABLE );
				ps.setInt( 2, 6 );
				ps.setInt( 3, 7 );
				break;
				
			case 4:
				ps = conn.prepareStatement( sql1 );
				ps.setInt( 1, STATE_ENABLE );
				ps.setInt( 2, 8 );
				break;
				
			case 5:
				ps = conn.prepareStatement( sql1 );
				ps.setInt( 1, STATE_ENABLE );
				ps.setInt( 2, 9 );
				break;
				
			default:
				return json;
			}
			
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append( "sid", rs.getString(1) );
				json.append( "sortid", rs.getInt(2) ); 
				json.append( "question", rs.getString(3) ); 
				json.append( "answer", rs.getString(4) );
				json.append( "state", rs.getInt(5) );
				json.append( "time", GMTime.format( rs.getLong(6), GMTime.CHINA ) );
				json.append( "tid", rs.getInt(7) );
			}
			rs.close();
			return json;
		} finally {
			JPrepare.close( ps, conn );
		}
	}
}
