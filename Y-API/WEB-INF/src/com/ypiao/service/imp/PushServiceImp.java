package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ypiao.bean.Push;
import com.ypiao.data.JPrepare;
import com.ypiao.service.PushService;

/**
 * 推送管理APS接口实现类.
 * 
 * Created by xk on 2018-06-06.
 */
public class PushServiceImp extends AConfig implements PushService {

	// 表名
	private static final String TBL_PUSH = "push";

	protected void checkSQL() {
	}

	/**
	 * @param push is Push
	 * @throws SQLException
	 * 
	 * 新增保存
	 */
	public void save(Push push) throws SQLException {
		// 先执行更新操作
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = conn.prepareStatement( "UPDATE " + TBL_PUSH + " SET Title = ?, Content = ?, Size = ?, Filename = ?, Description = ?, Time = ? WHERE Sid = ?" );
		
		try {
			/*ps.setString( 1, version.getNum() );
			ps.setInt( 2, version.getMid() );
			ps.setDouble( 3, version.getSize() );
			ps.setString( 4, version.getFilename() );
			ps.setString( 5, version.getDescription() );
			ps.setLong( 6, version.getTime() );
			ps.setString( 7, version.getSid() );*/
			
			// 若更新操作失败，则开始新增
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement( "INSERT INTO " + TBL_PUSH + " (Sid, Num, Mid, Size, Filename, Description, Time) VALUES (?, ?, ?, ?, ?, ?, ?)" );
				
				/*ps.setString( 1, version.getSid() );
				ps.setString( 2, version.getNum() );
				ps.setInt( 3, version.getMid() );
				ps.setDouble( 4, version.getSize() );
				ps.setString( 5, version.getFilename() );
				ps.setString( 6, version.getDescription() );
				ps.setLong( 7, version.getTime() );*/
				
				ps.execute();
			}
		} finally {
			JPrepare.close( ps, conn );
		}
	}
	
	/**
	 * @param sid long
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(long sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			conn.setAutoCommit(false);
			preparedStatement = conn.prepareStatement( "DELETE FROM " + TBL_PUSH + " WHERE Sid = ?" );
			preparedStatement.setLong( 1, sid );
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
