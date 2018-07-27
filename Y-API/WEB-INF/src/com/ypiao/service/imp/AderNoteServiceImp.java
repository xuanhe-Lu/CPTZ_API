package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.ypiao.bean.AdsNote;
import com.ypiao.bean.AderNote;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.TagIndex;
import com.ypiao.data.JPrepare;
import com.ypiao.service.AderNoteService;
import com.ypiao.util.GMTime;
import com.ypiao.util.VeFile;

public class AderNoteServiceImp extends AConfig implements AderNoteService {

	private static final String TBL_ADER_NOTE = "ader_note";

	private TagIndex TAG_INDEX = new TagIndex();

	protected void checkSQL() {
	}

	public void save(AdsNote a) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_ADER_NOTE + " SET Tid=?,Title=?,Author=?,Detail=?,Sday=?,State=?,Time=? WHERE Sid=?");
			ps.setInt(1, a.getTid());
			ps.setString(2, a.getTitle());
			ps.setString(3, a.getAuthor());
			ps.setString(4, a.getDetail());
			ps.setLong(5, a.getSday());
			ps.setInt(6, a.getState());
			ps.setLong(7, a.getTime());
			ps.setString(8, a.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_ADER_NOTE + " (Sid,Tid,Title,Author,Detail,Sday,State,Time) VALUES (?,?,?,?,?,?,?,?)");
				ps.setString(1, a.getSid());
				ps.setInt(2, a.getTid());
				ps.setString(3, a.getTitle());
				ps.setString(4, a.getAuthor());
				ps.setString(5, a.getDetail());
				ps.setLong(6, a.getSday());
				ps.setInt(7, a.getState());
				ps.setLong(8, a.getTime());
				ps.executeUpdate();
			}
			TAG_INDEX.expired();
			VeFile.delNote(a.getSid());
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AdsNote findNoteBySid(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			AdsNote a = null;
			ps = conn.prepareStatement("SELECT Sid,Tid,Title,Author,Detail,Sday,State,Time FROM " + TBL_ADER_NOTE + " WHERE Sid=?");
			ps.setString(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				a = new AdsNote();
				a.setSid(rs.getString(1));
				a.setTid(rs.getInt(2));
				a.setTitle(rs.getString(3));
				a.setAuthor(rs.getString(4));
				a.setDetail(rs.getString(5));
				a.setSday(rs.getLong(6));
				a.setState(rs.getInt(7));
				a.setTime(rs.getLong(8));
			}
			rs.close();
			return a;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	private String findIndex() throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		AjaxInfo json = AjaxInfo.getArray();
		try {
			ps = conn.prepareStatement("SELECT Sid,Tid,Title,State,Time FROM " + TBL_ADER_NOTE + " WHERE Position<=? AND State=? AND Sday<=? ORDER BY Sid DESC");
			ps.setInt(1, 2);
			ps.setInt(2, STATE_CHECK);
			ps.setLong(3, GMTime.currentTimeMillis());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("sid", rs.getString(1));
				json.append("title", rs.getString(3));
			}
			rs.close();
		} catch (SQLException e) {
			json.setBody("[]");
		} finally {
			JPrepare.close(ps, conn);
		}
		TAG_INDEX.setBody(json.getString());
		return TAG_INDEX.getBody();
	}

	@Override
	public void sendIndex(AjaxInfo json) throws SQLException {
		if (TAG_INDEX.isExpired()) {
			synchronized (TAG_INDEX) {
				if (TAG_INDEX.isRefresh()) {
					this.execute(() -> {
						try {
							this.findIndex();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					});
				} else {
					this.findIndex();
				}
			}
		} // 加载数据处理
		json.addText("note", TAG_INDEX.getBody());
	}

	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 通知管理列表
	 */
	public AjaxInfo sendByAll(AjaxInfo json) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		StringBuilder sql = new StringBuilder();
		
		try {
			json.datas(API_OK);
			sql = sql.append( "SELECT Sid, Tid, Position, Title, Author, Detail, Sday, State, Time FROM " + TBL_ADER_NOTE + " WHERE State = ? AND Sday <= ? AND (Position = ? OR Position = ?) ORDER BY Time DESC" );
			preparedStatement = conn.prepareStatement(sql.toString());
			preparedStatement.setInt( 1, ENABLE );
			preparedStatement.setLong( 2, GMTime.currentTimeMillis() );
			preparedStatement.setInt( 3, ALL );
			preparedStatement.setInt( 4, SITE );
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getString(1) );
				json.append( "TID", resultSet.getInt(2) ); 
				json.append( "POSITION", resultSet.getInt(3) ); 
				json.append( "TITLE", resultSet.getString(4) );
				json.append( "AUTHOR", resultSet.getString(5) );
				json.append( "DETAIL", resultSet.getString(6) );
				json.append( "SDAY", GMTime.format( resultSet.getLong(7), GMTime.CHINA ) );
				json.append( "STATE", resultSet.getInt(8) );
				json.append( "TIME", GMTime.format( resultSet.getLong(9), GMTime.CHINA ) );
			}
			resultSet.close();
			return json;
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
	}

	/**
	 * @author xk
	 * @param note is AderNote
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	public void save(AderNote aderNote) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = conn.prepareStatement( "UPDATE " + TBL_ADER_NOTE + " SET Tid = ?, Position = ?, Title = ?, Detail = ?, Sday = ?, State = ?, Time = ? WHERE Sid = ?" );
		
		try {
			ps.setInt( 1, aderNote.getTid() );
			ps.setInt( 2, aderNote.getPosition() );
			ps.setString( 3, aderNote.getTitle() );
			ps.setString( 4, aderNote.getDetail() );
			ps.setLong( 5, aderNote.getSday() );
			ps.setInt( 6, aderNote.getState() );
			ps.setLong( 7, aderNote.getTime() );
			ps.setString( 8, aderNote.getSid() );
			
			// 若更新操作失败，则开始新增
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement( "INSERT INTO " + TBL_ADER_NOTE + " (Sid, Tid, Position, Title, Author, Detail, Sday, State, Time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)" );
				
				ps.setString( 1, aderNote.getSid() );
				ps.setInt( 2, aderNote.getTid() );
				ps.setInt( 3, aderNote.getPosition() );
				ps.setString( 4, aderNote.getTitle() );
				ps.setString( 5, aderNote.getAuthor() );
				ps.setString( 6, aderNote.getDetail() );
				ps.setLong( 7, aderNote.getSday() );
				ps.setInt( 8, aderNote.getState() );
				ps.setLong( 9, aderNote.getTime() );
				
				ps.execute();
			}
			//TAG_INDEX.expired();
		} finally {
			JPrepare.close( ps, conn );
		}
	}

	/**
	 * @author xk
	 * @param ids String
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement( "DELETE FROM " + TBL_ADER_NOTE + " WHERE Sid = ?" );
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
}
