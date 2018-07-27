package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ypiao.bean.Version;
import com.ypiao.data.JPrepare;
import com.ypiao.service.VersionService;

/**
 * app版本更新APS接口实现类.
 * 
 * Created by xk on 2018-06-06.
 */
public class VersionServiceImp extends AConfig implements VersionService {

	// 表名
	private static final String TBL_VERSION_UPDATE = "version_update";

	protected void checkSQL() {
	}

	/**
	 * @param version is Version
	 * @throws SQLException
	 * 
	 * 新增保存
	 */
	public void save(Version version) throws SQLException {
		// 先执行更新操作
		Connection conn = JPrepare.getConnection();
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
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			conn.setAutoCommit(false);
			preparedStatement = conn.prepareStatement( "DELETE FROM " + TBL_VERSION_UPDATE + " WHERE Sid = ?" );
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
