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
import com.ypm.bean.Welfare;
import com.ypm.data.JPrepare;
import com.ypm.service.WelfareService;
import com.ypm.util.GMTime;

/**
 * 福利专区业务层接口实现类.
 * 
 * Created by xk on 2018-06-12.
 */
public class WelfareServiceImp extends AConfig implements WelfareService {

	// 表名
	private static final String TBL_WELFARE = "welfare";

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
		PreparedStatement preparedStatement = conn.prepareStatement( "UPDATE " + TBL_WELFARE + " SET Total = ? WHERE Sid = ?" );
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
	 * 列表数据获取
	 */
	public AjaxInfo list(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace( 1, 4, "WHERE" );
		} 
		// get total
		fs.add( 0, sql.insert( 0, "FROM " + TBL_WELFARE ).toString() );
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal( conn, TBL_WELFARE, fs );
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} 
			// 加载字典数据
			Map<String, String> typeDic = this.getDictInfoBySSid(WELFARE_TYPE);
			Map<String, String> stateDic = this.getDictInfoBySSid(SYSTEM_STATE);
			
			sql.insert( 0, "SELECT Sid, Type, State, Title, Url, Dist, Ver, Time " ).append( " ORDER BY " ).append(order);
			preparedStatement = conn.prepareStatement(JPrepare.getQuery( sql, offset, max ));
			for (int i = 1, j = fs.size(); i < j; i++) {
				preparedStatement.setObject( i, fs.get(i) );
			} 
			// 查询结果
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getString(1) );
				json.append( "TYPE", typeDic.get(resultSet.getString(2)) );
				json.append( "STATE", stateDic.get(resultSet.getString(3)) );
				json.append( "TITLE", resultSet.getString(4));
				json.append( "URL", resultSet.getString(5) );
				json.append( "DIST", resultSet.getString(6) );
				json.append( "VER", resultSet.getInt(7) );
				json.append( "TIME", GMTime.format( resultSet.getLong(8), GMTime.CHINA ) );
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
	 * @param welfare Welfare
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	private void save(Connection conn, Welfare welfare) throws SQLException {
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
			ps.close();
		}
	}

	/**
	 * @param sid String
	 * @return Welfare
	 * @throws SQLException
	 * 
	 * 根据id查询数据
	 */
	public Welfare bySid(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		
		try {
			Welfare welfare = null;
			ps = conn.prepareStatement( "SELECT Type, State, Title, Url, Dist, Ver, Time FROM " + TBL_WELFARE + " WHERE Sid = ?" );
			ps.setString( 1, sid );
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				welfare = new Welfare();
				welfare.setSid( sid );
				welfare.setType( rs.getInt(1) );
				welfare.setState( rs.getInt(2) );
				welfare.setTitle( rs.getString(3) );
				welfare.setUrl( rs.getString(4) );
				welfare.setDist( rs.getString(5) );
				welfare.setVer( rs.getInt(6) );
				welfare.setTime( rs.getLong(7) );
			}
			rs.close();
			return welfare;
		} finally {
			JPrepare.close( ps, conn );
		}
	}

	/**
	 * @param welfare is Welfare
	 * @throws SQLException, IOException
	 * 
	 * 新增保存
	 */
	public void saveWelfare(Welfare welfare, FileInfo fileInfo) throws IOException, SQLException {
		Connection conn = JPrepare.getConnection();
		
		try {
			if (fileInfo.getDist() != null) {
				welfare.setDist(fileInfo.getDist());
			}
			// 保存数据
			this.save( conn, welfare ); 
		} finally {
			JPrepare.close(conn);
		} 
		
		// 保存文件
		fileInfo.setTime( welfare.getSid(), welfare.getTime() );
		this.saveFile(fileInfo);
		// 同步数据信息
		SyncMap.getAll().sender( SYS_A510, "save", welfare );
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
		PreparedStatement ps = null;
		
		try {
			boolean result = false;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement( "DELETE FROM " + TBL_WELFARE + " WHERE Sid = ?" );
			ps.setString( 1, sid );
			if (ps.executeUpdate() > 0) {
				// 同步删除数据
				SyncMap.getAll().add( "sid", sid ).sender( SYS_A510, "remove" );
				result = true;
			}
			conn.commit();
			return result;
		} catch (SQLException e) {
			// 异常回滚
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close( ps, conn );
		}
	}
}
