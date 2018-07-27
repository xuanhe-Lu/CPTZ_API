package com.ypm.service.imp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ypm.bean.AderNote;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.SyncMap;
import com.ypm.data.JPrepare;
import com.ypm.service.AderNoteService;
import com.ypm.util.GMTime;

/**
 * 通知管理业务层接口实现类
 * Create by xk on 2018-04-26
 */
public class AderNoteServiceImp extends AConfig implements AderNoteService {

	// 表名
	private static final String TBL_ADER_NOTE = "ader_note";

	protected void checkSQL() {
	}
	
	/**
	 * @param conn Connection
	 * @param cid int  
	 * @param row int
	 * @throws SQLException
	 * 
	 * 更新数据
	 */
	public int update(Connection conn, int cid, int row) throws SQLException {
		PreparedStatement preparedStatement = conn.prepareStatement( "UPDATE " + TBL_ADER_NOTE + " SET Total=? WHERE Cid = ?" );
		try {
			preparedStatement.setInt( 1, row );
			preparedStatement.setInt( 2, cid );
			return preparedStatement.executeUpdate();
		} finally {
			preparedStatement.close();
		}
	}
	
	/**
	 * @param sql StringBuilder
	 * @param fs List<Object>
	 * @param order String
	 * @param offset int 
	 * @param max int
	 * @return AjaxInfo
	 * 
	 * 列表数据获取1
	 */
	public AjaxInfo findAderNoteByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace( 1, 4, "WHERE" );
		} 
		// get total
		fs.add( 0, sql.insert( 0, "FROM " + TBL_ADER_NOTE ).toString() );
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal( conn, TBL_ADER_NOTE, fs );
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} 
			// 加载后续信息
			Map<String, String> ms = this.getDictInfoBySSid(COO_ADS_STATE_TYPE);
			Map<String, String> mt = this.getDictInfoBySSid(COO_ADS_TPL_TYPE);
			Map<String, String> dp = this.getDictInfoBySSid(DISPLAY_POSITION);
			sql.insert( 0, "SELECT Sid, Tid, Position, Title, Author, Detail, Sday, State, Time " ).append( " ORDER BY " ).append(order);
			preparedStatement = conn.prepareStatement(JPrepare.getQuery( sql, offset, max ));
			for (int i = 1, j = fs.size(); i < j; i++) {
				preparedStatement.setObject( i, fs.get(i) );
			} 
			// 查询结果
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getString(1) );
				json.append( "TID", mt.get(resultSet.getString(2)) ); 
				json.append( "POSITION", dp.get(resultSet.getString(3)) ); 
				json.append( "TITLE", resultSet.getString(4) );
				json.append( "AUTHOR", resultSet.getString(5) );
				json.append( "DETAIL", resultSet.getString(6) );
				json.append( "SDAY", GMTime.format( resultSet.getLong(7), GMTime.CHINA ) );
				json.append( "STATE", ms.get(resultSet.getString(8)) );
				json.append( "TIME", GMTime.format( resultSet.getLong(9), GMTime.CHINA ) );
			}
			resultSet.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
		return json;
	}
	
	/**
	 * @param sql StringBuilder
	 * @return AjaxInfo
	 * 
	 * 列表数据获取2
	 */
	public AjaxInfo findAderNoteByAll(StringBuilder sql) {
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		
		try {
			conn = JPrepare.getConnection();
			preparedStatement = conn.prepareStatement(sql.toString());
			
			// 查询结果
			ResultSet resultSet = preparedStatement.executeQuery();
			// 加载后续信息
			Map<String, String> ms = this.getDictInfoBySSid(COO_ADS_STATE_TYPE);
			Map<String, String> mt = this.getDictInfoBySSid(COO_ADS_TPL_TYPE);
			Map<String, String> dp = this.getDictInfoBySSid(DISPLAY_POSITION);
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getString(1) );
				json.append( "TID", mt.get(resultSet.getString(2)) );
				json.append( "POSITION", dp.get(resultSet.getString(3)) );
				json.append( "TITLE", resultSet.getString(4) );
				json.append( "AUTHOR", resultSet.getString(5) );
				json.append( "DETAIL", resultSet.getString(6) );
				json.append( "SDAY", GMTime.format( resultSet.getLong(7), GMTime.CHINA ) );
				json.append( "STATE", ms.get(resultSet.getString(8)) );
				json.append( "TIME", GMTime.format( resultSet.getLong(9), GMTime.CHINA ) );
			}
			resultSet.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
		return json;
	}

	/**
	 * @param conn Connection
	 * @param aderNote AderNote
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	private static void save(Connection conn, AderNote aderNote) throws SQLException {
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
				
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close( ps, conn );
		}
	}

	/**
	 * @param cid int
	 * @throws SQLException
	 * 
	 * 根据id查询数据
	 */
	public AderNote findAderNoteBySId(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			AderNote aderNote = null;
			preparedStatement = conn.prepareStatement( "SELECT Sid, Tid, Position, Title, Author, Detail, Sday, State, Time FROM " + TBL_ADER_NOTE + " WHERE Sid = ?" );
			preparedStatement.setString( 1, sid );
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				aderNote = new AderNote();
				aderNote.setSid( resultSet.getString(1) );
				aderNote.setTid( resultSet.getInt(2) );
				aderNote.setPosition( resultSet.getInt(3) );
				aderNote.setTitle( resultSet.getString(4) );
				aderNote.setAuthor( resultSet.getString(5) );
				aderNote.setDetail( resultSet.getString(6) );
				aderNote.setSday( resultSet.getLong(7) );
				aderNote.setState( resultSet.getInt(8) );
				aderNote.setTime( resultSet.getLong(9) );
			}
			resultSet.close();
			return aderNote;
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
	}

	/**
	 * @param sid String
	 * @return boolean 
	 * 
	 * 根据主键id判断数据是否存在
	 */
	public boolean isAderNoteBySNo(String sid) {
		return JPrepare.isExists( "SELECT * FROM " + TBL_ADER_NOTE + " WHERE Sid = ?", sid );
	}

	/**
	 * @param aderNote is AderNote
	 * @throws SQLException, IOException
	 * 
	 * 新增保存
	 */
	public void saveAderNote(AderNote aderNote) throws SQLException, IOException {
		Connection conn = JPrepare.getConnection();
		try {
			// 保存数据
			save( conn, aderNote ); 
		} finally {
			JPrepare.close(conn);
		} 
		// 同步数据信息
		SyncMap.getAll().sender( SYS_A503, "save", aderNote );
	}

	/**
	 * @param cid int
	 * @throws SQLException
	 * 
	 * 删除通知
	 */
	public boolean remove(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			boolean result = false;
			conn.setAutoCommit(false);
			preparedStatement = conn.prepareStatement( "DELETE FROM " + TBL_ADER_NOTE + " WHERE Sid = ?" );
			preparedStatement.setString( 1, sid );
			if (preparedStatement.executeUpdate() > 0) {
				// 同步删除数据
				SyncMap.getAll().add( "sid", sid ).sender( SYS_A503, "remove" );
				result = true;
			}
			conn.commit();
			return result;
		} catch (SQLException e) {
			// 异常回滚
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
	}
	
}
