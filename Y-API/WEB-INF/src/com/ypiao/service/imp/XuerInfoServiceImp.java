package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.TagIndex;
import com.ypiao.bean.Xues;
import com.ypiao.data.JPrepare;
import com.ypiao.service.XuerInfoService;
import com.ypiao.util.GMTime;

/**
 * 票友学堂数据接口实现类. 
 */
public class XuerInfoServiceImp extends AConfig implements XuerInfoService {
	
	// 表名
	private static final String TBL_XUE_INFO = "xue_info";

	private TagIndex TAG_INDEX = new TagIndex();

	protected void checkSQL() {
	}

	private String findIndex() throws SQLException {
		AjaxInfo json = AjaxInfo.getArray();
		// json.formater();
		// json.append("sid", "20180403S3MDXCC");
		// json.append("img", "20180328S2N0WVY");
		// json.append("title", "学堂标题信息一");
		// json.append("subject", "美臆测中国在吉布提测试激光武器 渲染中国基地威胁");
		// json.formater();
		// json.append("sid", "20180403S3MDX6F");
		// json.append("img", "20180403S3MDX8F");
		// json.append("title", "学堂标题信息二");
		// json.append("subject", "美臆测中国在吉布提测试激光武器 渲染中国基地威胁");
		TAG_INDEX.setBody("[]");
		return json.getString();
	}

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
		json.addText("xues", TAG_INDEX.getBody());
	}

	/**
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 票友学堂数据列表获取
	 */
	public AjaxInfo sendByAll(AjaxInfo json) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			json.datas(API_OK);
			preparedStatement = conn.prepareStatement( "SELECT Sid, Title, Subject, Author, Detail, Dist, Ver, State, Time, Position FROM " + TBL_XUE_INFO + " WHERE State = ?" );
			preparedStatement.setInt( 1, ENABLE );
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				json.formater();
				json.append( "sid", resultSet.getString(1) );
				json.append( "title", resultSet.getString(2) ); 
				json.append( "subject", resultSet.getString(3) ); 
				json.append( "author", resultSet.getString(4) );
				json.append( "detail", resultSet.getString(5) );
				json.append( "dist", resultSet.getString(6) );
				json.append( "ver", resultSet.getInt(7) );
				json.append( "state", resultSet.getInt(8) );
				json.append( "time", GMTime.format( resultSet.getLong(9), GMTime.CHINA ) );
				json.append( "position", resultSet.getInt(10) );
			}
			resultSet.close();
			return json;
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
	}

	/**
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 票友学堂单条记录获取
	 */
	public AjaxInfo sendByOne(AjaxInfo json, String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			json.datas(API_OK);
			preparedStatement = conn.prepareStatement( "SELECT Sid, Title, Subject, Author, Detail, Dist, Ver, State, Time, Position FROM " + TBL_XUE_INFO + " WHERE Sid = ?" );
			preparedStatement.setString( 1, sid );
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				json.formater();
				json.append( "sid", resultSet.getString(1) );
				json.append( "title", resultSet.getString(2) ); 
				json.append( "subject", resultSet.getString(3) ); 
				json.append( "author", resultSet.getString(4) );
				json.append( "detail", resultSet.getString(5) );
				json.append( "dist", resultSet.getString(6) );
				json.append( "ver", resultSet.getInt(7) );
				json.append( "state", resultSet.getInt(8) );
				json.append( "time", GMTime.format( resultSet.getLong(9), GMTime.CHINA ) );
				json.append( "position", resultSet.getInt(10) );
			} else {
				return null;
			}
			resultSet.close();
			return json;
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
	}

	/**
	 * @author xk
	 * @param xues is Xues
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	public void save(Xues xues) throws SQLException {
		Connection conn = JPrepare.getConnection();
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
			}
			//TAG_INDEX.expired();
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
			preparedStatement = conn.prepareStatement( "DELETE FROM " + TBL_XUE_INFO + " WHERE Sid = ?" );
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
	
	/**
	 * @author xk
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
}
