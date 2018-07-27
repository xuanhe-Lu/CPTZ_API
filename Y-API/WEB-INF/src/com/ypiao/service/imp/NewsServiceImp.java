package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.News;
import com.ypiao.data.JPrepare;
import com.ypiao.service.NewsService;
import com.ypiao.util.GMTime;

/**
 * 新闻管理数据接口实现类. 
 */
public class NewsServiceImp extends AConfig implements NewsService {
	
	// 表名
	private static final String TBL_NEWS = "news";
	
	protected void checkSQL() {
	}

	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 新闻数据列表获取
	 */
	public AjaxInfo sendByAll(AjaxInfo json) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			json.datas(API_OK);
			preparedStatement = conn.prepareStatement( "SELECT Sid, Title, Subject, Author, Detail, Dist, Ver, State, Time, Position FROM " + TBL_NEWS + " WHERE State = ? AND (Position = ? OR Position = ?) ORDER BY Time DESC" );
			preparedStatement.setInt( 1, ENABLE );
			preparedStatement.setInt( 2, ALL );
			preparedStatement.setInt( 3, SITE );
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getString(1) );
				json.append( "TITLE", resultSet.getString(2) ); 
				json.append( "SUBJECT", resultSet.getString(3) ); 
				json.append( "AUTHOR", resultSet.getString(4) );
				json.append( "DETAIL", resultSet.getString(5) );
				json.append( "DIST", resultSet.getString(6) );
				json.append( "VER", resultSet.getInt(7) );
				json.append( "STATE", resultSet.getInt(8) );
				json.append( "TIME", GMTime.format( resultSet.getLong(9), GMTime.CHINA ) );
				json.append( "POSITION", resultSet.getInt(10) );
			}
			resultSet.close();
			return json;
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
	}

	/**
	 * @author xk
	 * @param news is News
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	public void save(News news) throws SQLException {
		Connection conn = JPrepare.getConnection();
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
				
				ps.execute();
			}
		} finally {
			JPrepare.close( ps, conn );
		}
	}

	/**
	 * @author xk
	 * @param sid String
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			conn.setAutoCommit(false);
			preparedStatement = conn.prepareStatement( "DELETE FROM " + TBL_NEWS + " WHERE Sid = ?" );
			preparedStatement.setString( 1, sid );
			preparedStatement.execute();
			conn.commit();
		} catch (SQLException e) {
			// 异常回滚
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
	}
}
