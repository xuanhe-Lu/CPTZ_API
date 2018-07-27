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
import com.ypm.bean.Version;
import com.ypm.data.JPrepare;
import com.ypm.service.VersionService;
import com.ypm.util.GMTime;

/**	
 * app版本更新业务层接口实现类
 * 
 * Created by xk on 2018-06-06.
 */
public class VersionServiceImp extends AConfig implements VersionService {

	// 表名
	private static final String TBL_VERSION_UPDATE = "version_update";

	protected void checkSQL() {
	}
	
	/**
	 * @param conn Connection
	 * @param sid String  
	 * @param row int
	 * @return int
	 * @throws SQLException
	 * 
	 * 更新数据
	 */
	public int update(Connection conn, String sid, int row) throws SQLException {
		PreparedStatement preparedStatement = conn.prepareStatement( "UPDATE " + TBL_VERSION_UPDATE + " SET Total = ? WHERE Sid = ?" );
		
		try {
			preparedStatement.setInt( 1, row );
			preparedStatement.setString( 2, sid );
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
	public AjaxInfo list(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace( 1, 4, "WHERE" );
		} 
		// get total
		fs.add( 0, sql.insert( 0, "FROM " + TBL_VERSION_UPDATE ).toString() );
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal( conn, TBL_VERSION_UPDATE, fs );
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} 
			// 加载数据字典信息
			Map<String, String> upgradeMethodDic = this.getDictInfoBySSid(UPGRADE_METHOD);
			Map<String, String> appClientDic = this.getDictInfoBySSid(APP_CLIENT);
			sql.insert( 0, "SELECT Sid, Tid, Num, Mid, Size, Filename, Dist, Description, Time " ).append( " ORDER BY " ).append(order);
			preparedStatement = conn.prepareStatement(JPrepare.getQuery( sql, offset, max ));
			for (int i = 1, j = fs.size(); i < j; i++) {
				preparedStatement.setObject( i, fs.get(i) );
			} 
			// 查询结果
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getString(1) );
				json.append( "TID", appClientDic.get(resultSet.getString(2)) );
				json.append( "NUM", resultSet.getString(3) );
				json.append( "MID", upgradeMethodDic.get(resultSet.getString(4)) );
				json.append( "SIZE", resultSet.getDouble(5) );
				json.append( "FILENAME", resultSet.getString(6) );
				json.append( "DIST", resultSet.getString(7) );
				json.append( "DESCRIPTION", resultSet.getString(8) );
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
	public AjaxInfo list(StringBuilder sql) {
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		
		try {
			conn = JPrepare.getConnection();
			preparedStatement = conn.prepareStatement(sql.toString());
			
			// 查询结果
			ResultSet resultSet = preparedStatement.executeQuery();
			// 加载数据字典信息
			Map<String, String> upgradeMethodDic = this.getDictInfoBySSid(UPGRADE_METHOD);
			Map<String, String> appClientDic = this.getDictInfoBySSid(APP_CLIENT);
			
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getString(1) );
				json.append( "TID", appClientDic.get(resultSet.getString(2)) );
				json.append( "NUM", resultSet.getString(3) );
				json.append( "MID", upgradeMethodDic.get(resultSet.getString(4)) );
				json.append( "SIZE", resultSet.getDouble(5) );
				json.append( "FILENAME", resultSet.getString(6) );
				json.append( "DIST", resultSet.getString(7) );
				json.append( "DESCRIPTION", resultSet.getString(8) );
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
	 * @param conn is Connection
	 * @param version is Version
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	private static void save(Connection conn, Version version) throws SQLException {
		PreparedStatement ps = conn.prepareStatement( "UPDATE " + TBL_VERSION_UPDATE + " SET Num = ?, Mid = ?, Size = ?, Filename = ?, Dist = ?, Description = ?, Time = ? WHERE Sid = ?" );
		
		try {
			ps.setString( 1, version.getNum() );
			ps.setInt( 2, version.getMid() );
			ps.setDouble( 3, version.getSize() );
			ps.setString( 4, version.getFilename() );
			ps.setString( 5, version.getDist() );
			ps.setString( 6, version.getDescription() );
			ps.setLong( 7, version.getTime() );
			ps.setString( 8, version.getSid() );
			
			// 若更新操作失败，则开始新增
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement( "INSERT INTO " + TBL_VERSION_UPDATE + " (Sid, Tid, Num, Mid, Size, Filename, Dist, Description, Time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)" );
				
				ps.setString( 1, version.getSid() );
				ps.setInt( 2, version.getTid() );
				ps.setString( 3, version.getNum() );
				ps.setInt( 4, version.getMid() );
				ps.setDouble( 5, version.getSize() );
				ps.setString( 6, version.getFilename() );
				ps.setString( 7, version.getDist() );
				ps.setString( 8, version.getDescription() );
				ps.setLong( 9, version.getTime() );
				
				ps.execute();
			}
		} finally {
			JPrepare.close( ps, conn );
		}
	}

	/**
	 * @param sid String
	 * @return Version
	 * @throws SQLException
	 * 
	 * 根据id查询数据
	 */
	public Version findVersionBySId(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			Version version = null;
			preparedStatement = conn.prepareStatement( "SELECT Num, Mid, Size, Filename, Dist, Description, Time FROM " + TBL_VERSION_UPDATE + " WHERE Sid = ?" );
			preparedStatement.setString( 1, sid );
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				version = new Version();
				version.setSid(sid);
				version.setNum(resultSet.getString(1));
				version.setMid(resultSet.getInt(2));
				version.setSize(resultSet.getDouble(3));
				version.setFilename(resultSet.getString(4));
				version.setDist(resultSet.getString(5));
				version.setDescription(resultSet.getString(6));
			    version.setTime(resultSet.getLong(7));
			}
			resultSet.close();
			return version;
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
	}

	/**
	 * @param version is Version
	 * @throws SQLException, IOException
	 * 
	 * 新增保存Android版本信息
	 */
	public void saveVersion(Version version, FileInfo fileInfo) throws IOException, SQLException {
		Connection conn = JPrepare.getConnection();
		
		try {
			version.setDist( "apk" );
			// 保存数据
			save( conn, version ); 
		} finally {
			JPrepare.close(conn);
		} 
		// 保存文件
		fileInfo.setTime( version.getSid(), version.getTime() );
		fileInfo.setDist( "apk" );
		this.saveApkFile(fileInfo);
		// 同步数据信息
		SyncMap.getAll().sender( SYS_A509, "save", version );
	}
	
	/**
	 * @param version is Version
	 * @throws SQLException, IOException
	 * 
	 * 新增保存iOS版本信息
	 */
	public void saveVersion(Version version) throws IOException, SQLException {
		Connection conn = JPrepare.getConnection();
		
		try {
			// 保存数据
			save( conn, version ); 
		} finally {
			JPrepare.close(conn);
		} 
		// 同步数据信息
		SyncMap.getAll().sender( SYS_A509, "save", version );
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
			preparedStatement = conn.prepareStatement( "DELETE FROM " + TBL_VERSION_UPDATE + " WHERE Sid = ?" );
			preparedStatement.setString( 1, sid );
			if (preparedStatement.executeUpdate() > 0) {
				// 同步删除数据
				SyncMap.getAll().add( "sid", sid ).sender( SYS_A509, "remove" );
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
