package com.ypm.service.imp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.FileInfo;
import com.ypm.bean.SyncMap;
import com.ypm.bean.Xues;
import com.ypm.data.JPrepare;
import com.ypm.service.XuesService;
import com.ypm.util.GMTime;

/**
 * 票友学堂业务层接口实现类.
 * 
 * Created by xk on 2018-05-02.
 */
public class XuesServiceImp extends AConfig implements XuesService {

	// 表名
	private static final String TBL_XUE_INFO = "xue_info";

	protected void checkSQL() {
	}
	
	/**
	 * @param conn Connection
	 * @param sid int  
	 * @param row int
	 * @return int
	 * @throws SQLException
	 * 
	 * 更新数据
	 */
	public int update(Connection conn, int sid, int row) throws SQLException {
		PreparedStatement preparedStatement = conn.prepareStatement( "UPDATE " + TBL_XUE_INFO + " SET Total = ? WHERE Sid = ?" );
		try {
			preparedStatement.setInt( 1, row );
			preparedStatement.setInt( 2, sid );
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
	public AjaxInfo findXuesByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace( 1, 4, "WHERE" );
		} 
		// get total
		fs.add( 0, sql.insert( 0, "FROM " + TBL_XUE_INFO ).toString() );
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal( conn, TBL_XUE_INFO, fs );
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} 
			// 加载后续信息
			Map<String, String> ms = this.getDictInfoBySSid(COO_ADS_STATE_TYPE);
			Map<String, String> dp = this.getDictInfoBySSid(DISPLAY_POSITION);
			sql.insert( 0, "SELECT Sid, Title, Subject, Position, Author, Detail, Dist, Ver, State, Time " ).append( " ORDER BY " ).append(order);
			preparedStatement = conn.prepareStatement(JPrepare.getQuery( sql, offset, max ));
			for (int i = 1, j = fs.size(); i < j; i++) {
				preparedStatement.setObject( i, fs.get(i) );
			} 
			// 查询结果
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getString(1) );
				json.append( "TITLE", resultSet.getString(2) );
				json.append( "SUBJECT", resultSet.getString(3) );
				json.append( "POSITION", dp.get(resultSet.getString(4)) );
				json.append( "AUTHOR", resultSet.getString(5) );
				json.append( "DETAIL", resultSet.getString(6) );
				json.append( "DIST", resultSet.getString(7) );
				json.append( "VER", resultSet.getString(8) );
				json.append( "STATE", ms.get(resultSet.getString(9)) );
				json.append( "TIME", GMTime.format( resultSet.getLong(10), GMTime.CHINA ) );
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
	public AjaxInfo findXuesByAll(StringBuilder sql) {
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
			Map<String, String> dp = this.getDictInfoBySSid(DISPLAY_POSITION);
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getString(1) );
				json.append( "TITLE", resultSet.getString(2) );
				json.append( "SUBJECT", resultSet.getString(3) );
				json.append( "POSITION", dp.get(resultSet.getString(4)) );
				json.append( "AUTHOR", resultSet.getString(5) );
				json.append( "DETAIL", resultSet.getString(6) );
				json.append( "DIST", resultSet.getString(7) );
				json.append( "VER", resultSet.getString(8) );
				json.append( "STATE", ms.get(resultSet.getString(9)) );
				json.append( "TIME", GMTime.format( resultSet.getLong(10), GMTime.CHINA ) );
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
	 * @param xues Xues
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	private void save(Connection conn, Xues xues) throws SQLException {
		System.out.println(xues.toString());
		PreparedStatement ps = conn.prepareStatement( "UPDATE " + TBL_XUE_INFO + " SET Title = ?, Subject = ?, Position = ?, Detail = ?, Dist = ?, Ver = ?, State = ?, Time = ? WHERE Sid = ?" );
		try {
			ps.setString( 1, xues.getTitle() );
			ps.setString( 2, xues.getSubject() );
			ps.setInt( 3, xues.getPosition() );
			ps.setString( 4, xues.getDetail() );
			ps.setString( 5, xues.getDist() );
			ps.setInt( 6, xues.getVer() );
			ps.setInt( 7, xues.getState() );
			ps.setLong( 8, xues.getTime() );
			ps.setString( 9, xues.getSid() );
			
			// 若更新操作失败，则开始新增
			if (ps.executeUpdate() <= 0) {
				System.out.println("save add...");
				ps.close();
				ps = conn.prepareStatement( "INSERT INTO " + TBL_XUE_INFO + " (Sid, Title, Subject, Position, Author, Detail, Dist, Ver, State, Time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" );
				
				ps.setString( 1, xues.getSid() );
				ps.setString( 2, xues.getTitle() );
				ps.setString( 3, xues.getSubject() );
				ps.setInt( 4, xues.getPosition() );
				ps.setString( 5, xues.getAuthor() );
				ps.setString( 6, xues.getDetail() );
				ps.setString( 7, xues.getDist() );
				ps.setInt( 8, xues.getVer() );
				ps.setInt( 9, xues.getState() );
				ps.setLong( 10, xues.getTime() );
				
				ps.execute();
			} else {
				System.out.println("save update...");
			}
		} finally {
			ps.close();
		}
	}

	/**
	 * @param sid String
	 * @return Xues
	 * @throws SQLException
	 * 
	 * 根据id查询数据
	 */
	public Xues findXuesBySId(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			Xues xues = null;
			preparedStatement = conn.prepareStatement( "SELECT Sid, Title, Subject, Position, Author, Detail, Dist, Ver, State, Time FROM " + TBL_XUE_INFO + " WHERE Sid = ?" );
			preparedStatement.setString( 1, sid );
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				xues = new Xues();
				xues.setSid( resultSet.getString(1) );
				xues.setTitle( resultSet.getString(2) );
				xues.setSubject( resultSet.getString(3) );
				xues.setPosition( resultSet.getInt(4) );
				xues.setAuthor( resultSet.getString(5) );
				xues.setDetail( resultSet.getString(6) );
				xues.setDist( resultSet.getString(7) );
				xues.setVer( resultSet.getInt(8) );
				xues.setState( resultSet.getInt(9) );
				xues.setTime( resultSet.getLong(10) );
			}
			resultSet.close();
			return xues;
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
	}

	/**
	 * @param xues is Xues
	 * @throws SQLException, IOException
	 * 
	 * 新增保存
	 */
	public void saveXues(Xues xues, FileInfo fileInfo) throws IOException, SQLException {
		Connection conn = JPrepare.getConnection();
		
		try {
			if (fileInfo.getDist() != null) {
				xues.setDist(fileInfo.getDist());
			}
			// 保存数据
			this.save( conn, xues ); 
		} finally {
			JPrepare.close(conn);
		} 
		// 保存文件
		fileInfo.setTime( xues.getSid(), xues.getTime() );
		this.saveFile(fileInfo);
		// 同步数据信息
		SyncMap.getAll().sender( SYS_A504, "save", xues );
	}
	
	/**
	 * @param sid String
	 * @return boolean
	 * @throws SQLException
	 * 
	 * 删除
	 */
	public boolean remove(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			boolean result = false;
			conn.setAutoCommit(false);
			preparedStatement = conn.prepareStatement( "DELETE FROM " + TBL_XUE_INFO + " WHERE Sid = ?" );
			preparedStatement.setString( 1, sid );
			if (preparedStatement.executeUpdate() > 0) {
				// 同步删除数据
				SyncMap.getAll().add( "sid", sid ).sender( SYS_A504, "remove" );
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
