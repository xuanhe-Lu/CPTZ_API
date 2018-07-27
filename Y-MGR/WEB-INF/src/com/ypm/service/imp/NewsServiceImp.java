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
import com.ypm.bean.News;
import com.ypm.bean.SyncMap;
import com.ypm.data.JPrepare;
import com.ypm.service.NewsService;
import com.ypm.util.GMTime;

/**
 * 新闻管理业务层接口实现类.
 * 
 * Created by xk on 2018-05-15.
 */
public class NewsServiceImp extends AConfig implements NewsService {

	// 表名
	private static final String TBL_NEWS = "news";

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
		PreparedStatement preparedStatement = conn.prepareStatement( "UPDATE " + TBL_NEWS + " SET Total = ? WHERE Sid = ?" );
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
	public AjaxInfo findNewsByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace( 1, 4, "WHERE" );
		} 
		// get total
		fs.add( 0, sql.insert( 0, "FROM " + TBL_NEWS ).toString() );
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal( conn, TBL_NEWS, fs );
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
				json.append( "VER", resultSet.getInt(8) );
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
	public AjaxInfo findNewsByAll(StringBuilder sql) {
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
				json.append( "VER", resultSet.getInt(8) );
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
	 * @param news News
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	private void save(Connection conn, News news) throws SQLException {
		PreparedStatement ps = conn.prepareStatement( "UPDATE " + TBL_NEWS + " SET Title = ?, Subject = ?, Position = ?, Detail = ?, Dist = ?, Ver = ?, State = ?, Time = ? WHERE Sid = ?" );
		try {
			ps.setString( 1, news.getTitle() );
			ps.setString( 2, news.getSubject() );
			ps.setInt( 3, news.getPosition() );
			ps.setString( 4, news.getDetail() );
			ps.setString( 5, news.getDist() );
			ps.setInt( 6, news.getVer() );
			ps.setInt( 7, news.getState() );
			ps.setLong( 8, news.getTime() );
			ps.setString( 9, news.getSid() );
			
			// 若更新操作失败，则开始新增
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement( "INSERT INTO " + TBL_NEWS + " (Sid, Title, Subject, Position, Author, Detail, Dist, Ver, State, Time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" );
				
				ps.setString( 1, news.getSid() );
				ps.setString( 2, news.getTitle() );
				ps.setString( 3, news.getSubject() );
				ps.setInt( 4, news.getPosition() );
				ps.setString( 5, news.getAuthor() );
				ps.setString( 6, news.getDetail() );
				ps.setString( 7, news.getDist() );
				ps.setInt( 8, news.getVer() );
				ps.setInt( 9, news.getState() );
				ps.setLong( 10, news.getTime() );
				
				ps.executeUpdate();
			}
		} finally {
			ps.close();
		}
	}

	/**
	 * @param sid String
	 * @return News
	 * @throws SQLException
	 * 
	 * 根据id查询数据
	 */
	public News findNewsBySId(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			News news = null;
			preparedStatement = conn.prepareStatement( "SELECT Sid, Title, Subject, Position, Author, Detail, Dist, Ver, State, Time FROM " + TBL_NEWS + " WHERE Sid = ?" );
			preparedStatement.setString( 1, sid );
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				news = new News();
				news.setSid( resultSet.getString(1) );
				news.setTitle( resultSet.getString(2) );
				news.setSubject( resultSet.getString(3) );
				news.setPosition( resultSet.getInt(4) );
				news.setAuthor( resultSet.getString(5) );
				news.setDetail( resultSet.getString(6) );
				news.setDist( resultSet.getString(7) );
				news.setVer( resultSet.getInt(8) );
				news.setState( resultSet.getInt(9) );
				news.setTime( resultSet.getLong(10) );
			}
			resultSet.close();
			return news;
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
	}

	/**
	 * @param news is News
	 * @throws SQLException, IOException
	 * 
	 * 新增保存
	 */
	public void saveNews(News news, FileInfo fileInfo) throws IOException, SQLException {
		Connection conn = JPrepare.getConnection();
		
		try {
			if (fileInfo.getDist() != null) {
				news.setDist(fileInfo.getDist());
			}
			// 保存数据
			this.save( conn, news ); 
		} finally {
			JPrepare.close(conn);
		} 
		// 保存文件
		fileInfo.setTime( news.getSid(), news.getTime() );
		this.saveFile(fileInfo);
		// 同步数据信息
		SyncMap.getAll().sender( SYS_A506, "save", news );
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
			preparedStatement = conn.prepareStatement( "DELETE FROM " + TBL_NEWS + " WHERE Sid = ?" );
			preparedStatement.setString( 1, sid );
			if (preparedStatement.executeUpdate() > 0) {
				// 同步删除数据
				SyncMap.getAll().add( "sid", sid ).sender( SYS_A506, "remove" );
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
