package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.Welfare;
import com.ypiao.data.JPrepare;
import com.ypiao.service.WelfareService;
import com.ypiao.util.GMTime;

/**
 * Created by xk on 2018-06-12.
 * 
 * 福利专区数据接口实现类. 
 */
public class WelfareServiceImp extends AConfig implements WelfareService {
	
	// 表名
	private static final String TBL_WELFARE = "welfare";
	
	protected void checkSQL() {
	}
	
	/**
	 * @param rs is ResultSet
	 * @param json is AjaxInfo
	 * @param count Integer 返回结果集中几条记录
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 装载json数据 
	 * @throws SQLException 
	 */
	public AjaxInfo pushJson(ResultSet rs, AjaxInfo json, Integer count) throws SQLException {
		Integer num = 0;
		
		if (count == null) {
			while (rs.next()) {
				json.formater();
				json.append( "sid", rs.getString(1) );
				json.append( "type", rs.getInt(2) ); 
				json.append( "state", rs.getInt(3) ); 
				json.append( "title", rs.getString(4) );
				json.append( "url", rs.getString(5) );
				json.append( "dist", rs.getString(6) );
				json.append( "ver", rs.getInt(7) );
				json.append( "time", GMTime.format( rs.getLong(8), GMTime.CHINA ) );
			}
		} else {
			while (rs.next()) {
				num++;
				if (num <= count) {
					json.formater();
					json.append( "sid", rs.getString(1) );
					json.append( "type", rs.getInt(2) ); 
					json.append( "state", rs.getInt(3) ); 
					json.append( "title", rs.getString(4) );
					json.append( "url", rs.getString(5) );
					json.append( "dist", rs.getString(6) );
					json.append( "ver", rs.getInt(7) );
					json.append( "time", GMTime.format( rs.getLong(8), GMTime.CHINA ) );
				} else {
					break;
				}
			}
		}
		
		return json;
	}

	/**
	 * @param json is AjaxInfo
	 * @return AjaxInfo
	 * @throws SQLException
	 * 
	 * 福利专区数据列表获取
	 */
	public AjaxInfo sendByAll(AjaxInfo json) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		
		AjaxInfo json1 = AjaxInfo.getArray();
		AjaxInfo json2 = AjaxInfo.getArray();
		try {
			json.success(API_OK);
			String dataSQL = "SELECT Sid, Type, State, Title, Url, Dist, Ver, Time FROM " + TBL_WELFARE + " WHERE Type = ? AND State = ? ORDER BY Time DESC";
			
			ps = conn.prepareStatement(dataSQL);
			ps.setInt( 1, 1 );
			ps.setInt( 2, STATE_ENABLE );
			ResultSet rs = ps.executeQuery();
			// 精选活动,后台最多设置2个,少于2个则不显示
			json.addText( "data1", this.pushJson( rs, json1, null ).toString() );
			
			ps.setInt( 1, 2 );
			ps.setInt( 2, STATE_ENABLE );
			rs = ps.executeQuery();
			// 近期活动,后台设置3~5个,少于3个则不显示,最多返回按时间倒叙前3个
			json.addText( "data2", this.pushJson( rs, json2, 3 ).toString() );
			
			rs.close();
			return json;
		} finally {
			JPrepare.close( ps, conn );
		}
	}
	
	/**
	 * @author xk
	 * @param welfare is Welfare
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	public void save(Welfare welfare) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = conn.prepareStatement( "UPDATE " + TBL_WELFARE + " SET Type = ?, State = ?, Title = ?, Url = ?, Dist = ?, Ver = ?, Time = ? WHERE Sid = ?" );
		
		try {
			ps.setInt( 1, welfare.getType() );
			ps.setInt( 2, welfare.getState() );
			ps.setString( 3, welfare.getTitle() );
			ps.setString( 4, welfare.getUrl() );
			ps.setString( 5, welfare.getDist() );
			ps.setInt( 6, welfare.getVer() );
			ps.setLong( 7, welfare.getTime() );
			ps.setString( 8, welfare.getSid() );
			
			// 若更新操作失败，则开始新增
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement( "INSERT INTO " + TBL_WELFARE + " (Sid, Type, State, Title, Url, Dist, Ver, Time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)" );
				
				ps.setString( 1, welfare.getSid() );
				ps.setInt( 2, welfare.getType() );
				ps.setInt( 3, welfare.getState() );
				ps.setString( 4, welfare.getTitle() );
				ps.setString( 5, welfare.getUrl() );
				ps.setString( 6, welfare.getDist() );
				ps.setInt( 7, welfare.getVer() );
				ps.setLong( 8, welfare.getTime() );
				
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
		PreparedStatement ps = null;
		
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement( "DELETE FROM " + TBL_WELFARE + " WHERE Sid = ?" );
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
