package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ypiao.bean.Channel;
import com.ypiao.data.JPrepare;
import com.ypiao.service.ChannelService;

/**
 * 渠道统计APS接口实现类.
 * 
 * Created by xk on 2018-05-18.
 */
public class ChannelServiceImp extends AConfig implements ChannelService {

	// 表名
	private static final String TBL_CHANNEL_COUNT = "channel_count";

	protected void checkSQL() {
	}

	/**
	 * @param channel is Channel
	 * @throws SQLException
	 * 
	 * 新增保存
	 */
	public void save(Channel channel) throws SQLException {
		// 先执行更新操作
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = conn.prepareStatement( "UPDATE " + TBL_CHANNEL_COUNT + " SET Tid = ?, Name = ?, Raddr = ?, Daddr = ?, Rcount = ?, Rname = ?, Bcount = ?, Ncount = ?, Nmoney = ?, Fcount = ?, Scount = ?, Fmoney = ?, Smoney = ?, Tzsum = ?, Txsum = ?, Nastock = ?, Sstock = ?, Time = ? WHERE Sid = ?" );
		
		try {
			ps.setInt( 1, channel.getTid() );
			ps.setString( 2, channel.getName() );
			ps.setString( 3, channel.getRaddr() );
			ps.setString( 4, channel.getDaddr() );
			ps.setLong( 5, channel.getRcount() );
			ps.setLong( 6, channel.getRname() );
			ps.setLong( 7, channel.getBcount() );
			ps.setLong( 8, channel.getNcount() );
			ps.setBigDecimal( 9, channel.getNmoney() );
			ps.setLong( 10, channel.getFcount() );
			ps.setLong( 11, channel.getScount() );
			ps.setBigDecimal( 12, channel.getFmoney() );
			ps.setBigDecimal( 13, channel.getSmoney() );
			ps.setBigDecimal( 14, channel.getTzsum() );
			ps.setBigDecimal( 15, channel.getTxsum() );
			ps.setBigDecimal( 16, channel.getNastock() );
			ps.setBigDecimal( 17, channel.getSstock() );
			ps.setLong( 18, channel.getTime() );
			ps.setInt( 19, channel.getSid() );
			
			// 若更新操作失败，则开始新增
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement( "INSERT INTO " + TBL_CHANNEL_COUNT + " (Sid, Tid, Name, Raddr, Daddr, Rcount, Rname, Bcount, Ncount, Nmoney, Fcount, Scount, Fmoney, Smoney, Tzsum, Txsum, Nastock, Sstock, Time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" );
				
				ps.setInt( 1, channel.getSid() );
				ps.setInt( 2, channel.getTid() );
				ps.setString( 3, channel.getName() );
				ps.setString( 4, channel.getRaddr() );
				ps.setString( 5, channel.getDaddr() );
				ps.setLong( 6, channel.getRcount() );
				ps.setLong( 7, channel.getRname() );
				ps.setLong( 8, channel.getBcount() );
				ps.setLong( 9, channel.getNcount() );
				ps.setBigDecimal( 10, channel.getNmoney() );
				ps.setLong( 11, channel.getFcount() );
				ps.setLong( 12, channel.getScount() );
				ps.setBigDecimal( 13, channel.getFmoney() );
				ps.setBigDecimal( 14, channel.getSmoney() );
				ps.setBigDecimal( 15, channel.getTzsum() );
				ps.setBigDecimal( 16, channel.getTxsum() );
				ps.setBigDecimal( 17, channel.getNastock() );
				ps.setBigDecimal( 18, channel.getSstock() );
				ps.setLong( 19, channel.getTime() );
				ps.execute();
				// 获取新增后的自增主键id
				//ResultSet rs = ps.getGeneratedKeys();
				//rs.next();
				//int primaryKey = rs.getInt(1);
				
				// 根据渠道名称，设置用户明细表的Cid字段值
				ps = conn.prepareStatement( "UPDATE user_status SET Cid = " + channel.getSid() + " WHERE Rinfo = '" + channel.getName() + "'" );
				ps.executeUpdate();
			} else {// 更新成功，设置用户明细表的Cid字段值
				ps.close();
				ps = conn.prepareStatement( "UPDATE user_status SET Cid = " + channel.getSid() + " WHERE Rinfo = '" + channel.getName() + "'" );
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close( ps, conn );
		}
	}
	
	/**
	 * @param sid int
	 * @throws SQLException
	 * 
	 * 删除数据
	 */
	public void remove(int sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			conn.setAutoCommit(false);
			preparedStatement = conn.prepareStatement( "DELETE FROM " + TBL_CHANNEL_COUNT + " WHERE Sid = ?" );
			preparedStatement.setInt( 1, sid );
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
