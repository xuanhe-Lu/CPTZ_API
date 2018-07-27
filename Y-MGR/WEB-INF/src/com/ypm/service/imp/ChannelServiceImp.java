package com.ypm.service.imp;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.Channel;
import com.ypm.bean.SyncMap;
import com.ypm.data.JPrepare;
import com.ypm.service.ChannelService;
import com.ypm.util.GMTime;

/**
 * 渠道统计业务层接口实现类.
 * 
 * Created by xk on 2018-05-18.
 */
public class ChannelServiceImp extends AConfig implements ChannelService {

	// 渠道表名
	private static final String TBL_CHANNEL_COUNT = "channel_count";
	
	// 用户明细表名
	private static final String TBL_USER_STATUS = "user_status";

	protected void checkSQL() {
	}
	
	/**
	 * @param conn Connection
	 * @param sid int  
	 * @param row int
	 * @return int
	 * @throws SQLException
	 * 
	 * 更新动态数据
	 */
	public int update(Channel channel) throws SQLException {
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
			
			return ps.executeUpdate();
		} finally {
			JPrepare.close( ps, conn );
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
	public AjaxInfo findChannelByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace( 1, 4, "WHERE" );
		} 
		// get total
		fs.add( 0, sql.insert( 0, "FROM " + TBL_CHANNEL_COUNT ).toString() );
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal( conn, TBL_CHANNEL_COUNT, fs );
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} 
			sql.insert( 0, "SELECT Sid, Tid, RADDR, DADDR, Name, Time " ).append( " ORDER BY " ).append(order);
			preparedStatement = conn.prepareStatement(JPrepare.getQuery( sql, offset, max ));
			for (int i = 1, j = fs.size(); i < j; i++) {
				preparedStatement.setObject( i, fs.get(i) );
			} 
			// 查询结果
			ResultSet resultSet = preparedStatement.executeQuery();
			// 加载渠道分类数据字典
			Map<String, String> channelTypeDic = this.getDictInfoBySSid(CHANNEL_TYPE);
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getInt(1) );
				json.append( "TID", channelTypeDic.get(resultSet.getString(2)) );
				json.append( "RADDR", resultSet.getString(3) );
				json.append( "DADDR", resultSet.getString(4) );
				json.append( "NAME", resultSet.getString(5) );
				
				// 获取渠道的动态数据字段
				Map<String, Object> dynamicCol = this.getDynamicField( resultSet.getInt(1), preparedStatement, conn );
				if (!dynamicCol.isEmpty()) {
					json.append( "RCOUNT", (Long) dynamicCol.get("key_1") );
					json.append( "RNAME", (Long) dynamicCol.get("key_2") );
					json.append( "BCOUNT", (Long) dynamicCol.get("key_3") );
					json.append( "NCOUNT", (Long) dynamicCol.get("key_4") );
					json.append( "NMONEY", (BigDecimal) dynamicCol.get("key_5") );
					json.append( "FCOUNT", (Long) dynamicCol.get("key_6") );
					json.append( "SCOUNT", (Long) dynamicCol.get("key_7") );
					json.append( "FMONEY", (BigDecimal) dynamicCol.get("key_8") );
					json.append( "SMONEY", (BigDecimal) dynamicCol.get("key_9") );
					json.append( "TZSUM", (BigDecimal) dynamicCol.get("key_10") );
					json.append( "TXSUM", (BigDecimal) dynamicCol.get("key_11") );
					json.append( "NASTOCK", (BigDecimal) dynamicCol.get("key_12") );
					json.append( "SSTOCK", (BigDecimal) dynamicCol.get("key_13") );
				}
				json.append( "TIME", GMTime.format( resultSet.getLong(6), GMTime.CHINA ) );
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
	public AjaxInfo findChannelByAll(StringBuilder sql) {
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		
		try {
			conn = JPrepare.getConnection();
			preparedStatement = conn.prepareStatement(sql.toString());
			
			// 查询结果
			ResultSet resultSet = preparedStatement.executeQuery();
			// 加载渠道分类数据字典
			Map<String, String> channelTypeDic = this.getDictInfoBySSid(CHANNEL_TYPE);
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getInt(1) );
				json.append( "TID", channelTypeDic.get(resultSet.getString(2)) );
				json.append( "NAME", resultSet.getString(3) );
				json.append( "RCOUNT", resultSet.getInt(4) );
				json.append( "RNAME", resultSet.getInt(5) );
				json.append( "BCOUNT", resultSet.getInt(6) );
				json.append( "NCOUNT", resultSet.getInt(7) );
				json.append( "NMONEY", resultSet.getBigDecimal(8) );
				json.append( "FCOUNT", resultSet.getInt(9) );
				json.append( "SCOUNT", resultSet.getInt(10) );
				json.append( "FMONEY", resultSet.getBigDecimal(11) );
				json.append( "SMONEY", resultSet.getBigDecimal(12) );
				json.append( "TZSUM", resultSet.getBigDecimal(13) );
				json.append( "TXSUM", resultSet.getBigDecimal(14) );
				json.append( "NASTOCK", resultSet.getBigDecimal(15) );
				json.append( "SSTOCK", resultSet.getBigDecimal(16) );
				json.append( "TIME", GMTime.format( resultSet.getLong(17), GMTime.CHINA ) );
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
	 * @param channel Channel
	 * @throws SQLException
	 * 
	 * 新增/保存数据
	 */
	private void save(Connection conn, Channel channel) throws SQLException {
		// 先执行更新操作
		PreparedStatement ps = conn.prepareStatement( "UPDATE " + TBL_CHANNEL_COUNT + " SET Tid = ?, Name = ?, Raddr = ?, Daddr = ?, Rcount = ?, Rname = ?, Bcount = ?, Ncount = ?, Nmoney = ?, Fcount = ?, Scount = ?, Fmoney = ?, Smoney = ?, Tzsum = ?, Txsum = ?, Nastock = ?, Sstock = ?, Time = ? WHERE Sid = ?" );
		try {
			ps.setInt( 1, channel.getTid() );
			ps.setString( 2, channel.getName() );
			ps.setString( 3, channel.getRaddr() );
			ps.setString( 4, channel.getDaddr() );
			
			// 获取渠道的动态数据字段
			Map<String, Object> dynamicCol = this.getDynamicField( channel.getSid(), ps, conn );
			if (!dynamicCol.isEmpty()) {
				ps.setLong( 5, (Long) dynamicCol.get("key_1") );
				ps.setLong( 6, (Long) dynamicCol.get("key_2") );
				ps.setLong( 7, (Long) dynamicCol.get("key_3") );
				ps.setLong( 8, (Long) dynamicCol.get("key_4") );
				ps.setBigDecimal( 9, (BigDecimal) dynamicCol.get("key_5") );
				ps.setLong( 10, (Long) dynamicCol.get("key_6") );
				ps.setLong( 11, (Long) dynamicCol.get("key_7") );
				ps.setBigDecimal( 12, (BigDecimal) dynamicCol.get("key_8") );
				ps.setBigDecimal( 13, (BigDecimal) dynamicCol.get("key_9") );
				ps.setBigDecimal( 14, (BigDecimal) dynamicCol.get("key_10") );
				ps.setBigDecimal( 15, (BigDecimal) dynamicCol.get("key_11") );
				ps.setBigDecimal( 16, (BigDecimal) dynamicCol.get("key_12") );
				ps.setBigDecimal( 17, (BigDecimal) dynamicCol.get("key_13") );
			}
			ps.setLong( 18, channel.getTime() );
			ps.setInt( 19, channel.getSid() );
			
			// 若更新操作失败，则开始新增
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement( "INSERT INTO " + TBL_CHANNEL_COUNT + " (Sid, Tid, Name, Raddr, Daddr, Rcount, Rname, Bcount, Ncount, Nmoney, Fcount, Scount, Fmoney, Smoney, Tzsum, Txsum, Nastock, Sstock, Time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS );
				
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
				
				// 设置渠道动态数据字段
				ps = conn.prepareStatement( "UPDATE " + TBL_CHANNEL_COUNT + " SET Rcount = ?, Rname = ?, Bcount = ?, Ncount = ?, Nmoney = ?, Fcount = ?, Scount = ?, Fmoney = ?, Smoney = ?, Tzsum = ?, Txsum = ?, Nastock = ?, Sstock = ? WHERE Sid = ?" );
				// 获取渠道的动态数据字段
				Map<String, Object> dynamicColAdd = this.getDynamicField( channel.getSid(), ps, conn );
				if (!dynamicCol.isEmpty()) {
					ps.setLong( 1, (Long) dynamicColAdd.get("key_1") );
					ps.setLong( 2, (Long) dynamicColAdd.get("key_2") );
					ps.setLong( 3, (Long) dynamicColAdd.get("key_3") );
					ps.setLong( 4, (Long) dynamicColAdd.get("key_4") );
					ps.setBigDecimal( 5, (BigDecimal) dynamicColAdd.get("key_5") );
					ps.setLong( 6, (Long) dynamicColAdd.get("key_6") );
					ps.setLong( 7, (Long) dynamicColAdd.get("key_7") );
					ps.setBigDecimal( 8, (BigDecimal) dynamicColAdd.get("key_8") );
					ps.setBigDecimal( 9, (BigDecimal) dynamicColAdd.get("key_9") );
					ps.setBigDecimal( 10, (BigDecimal) dynamicColAdd.get("key_10") );
					ps.setBigDecimal( 11, (BigDecimal) dynamicColAdd.get("key_11") );
					ps.setBigDecimal( 12, (BigDecimal) dynamicColAdd.get("key_12") );
					ps.setBigDecimal( 13, (BigDecimal) dynamicColAdd.get("key_13") );
				}
				ps.setInt( 14, channel.getSid() );
				ps.executeUpdate();
			} else {// 更新成功，关联更新用户明细表的Cid字段值
				ps.close();
				String sql = "UPDATE user_status SET Cid = " + channel.getSid() + " WHERE Rinfo = '" + channel.getName() + "'";
				ps = conn.prepareStatement(sql);
				ps.executeUpdate();
			}
		} finally {
			ps.close();
		}
	}

	/**
	 * @param sid String
	 * @return Channel
	 * @throws SQLException
	 * 
	 * 根据id查询数据
	 */
	public Channel findChannelBySId(int sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			Channel channel = null;
			preparedStatement = conn.prepareStatement( "SELECT Sid, Tid, Name, Raddr, Daddr, Rcount, Rname, Bcount, Ncount, Nmoney, Fcount, Scount, Fmoney, Smoney, Tzsum, Txsum, Nastock, Sstock, Time FROM " + TBL_CHANNEL_COUNT + " WHERE Sid = ?" );
			preparedStatement.setInt( 1, sid );
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				channel = new Channel();
				channel.setSid(resultSet.getInt(1));
				channel.setTid(resultSet.getInt(2));
				channel.setName(resultSet.getString(3));
				channel.setRaddr(resultSet.getString(4));
				channel.setDaddr(resultSet.getString(5));
				
				// 获取渠道的动态数据字段
				Map<String, Object> dynamicCol = this.getDynamicField( resultSet.getInt(1), preparedStatement, conn );
				if (!dynamicCol.isEmpty()) {
					channel.setRcount((Long) dynamicCol.get("key_1"));
					channel.setRname((Long) dynamicCol.get("key_2"));
					channel.setBcount((Long) dynamicCol.get("key_3"));
					channel.setNcount((Long) dynamicCol.get("key_4"));
					channel.setNmoney((BigDecimal) dynamicCol.get("key_5"));
					channel.setFcount((Long) dynamicCol.get("key_6"));
					channel.setScount((Long) dynamicCol.get("key_7"));
					channel.setFmoney((BigDecimal) dynamicCol.get("key_8"));
					channel.setSmoney((BigDecimal) dynamicCol.get("key_9"));
					channel.setTzsum((BigDecimal) dynamicCol.get("key_10"));
					channel.setTxsum((BigDecimal) dynamicCol.get("key_11"));
					channel.setNastock((BigDecimal) dynamicCol.get("key_12"));
					channel.setSstock((BigDecimal) dynamicCol.get("key_13"));
				}
				channel.setTime(resultSet.getLong(19));
			}
			resultSet.close();
			return channel;
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
	}

	/**
	 * @param channel is Channel
	 * @throws SQLException, IOException
	 * 
	 * 新增保存
	 */
	public void saveChannel(Channel channel) throws IOException, SQLException {
		Connection conn = JPrepare.getConnection();
		
		try {
			// 保存数据
			save( conn, channel ); 
		} finally {
			JPrepare.close(conn);
		} 
		// 同步数据信息
		SyncMap.getAll().sender( SYS_A507, "save", channel );
	}
	
	/**
	 * @param sid int
	 * @return boolean
	 * @throws SQLException
	 * 
	 * 删除一条数据
	 */
	public boolean remove(int sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			boolean result = false;
			conn.setAutoCommit(false);
			preparedStatement = conn.prepareStatement( "DELETE FROM " + TBL_CHANNEL_COUNT + " WHERE Sid = ?" );
			preparedStatement.setInt( 1, sid );
			if (preparedStatement.executeUpdate() > 0) {
				// 同步删除数据
				SyncMap.getAll().add( "sid", sid ).sender( SYS_A507, "remove" );
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

	/**
	 * @param cid int 渠道编号
	 * @param ps is PrepareStatement
	 * @param conn is Connection
	 * @return Map<String, Object>
	 * @throws SQLException 
	 * 
	 * 根据渠道编号获取渠道的其他字段数据，即渠道的动态数据字段
	 * 
	 * 1 Rcount(注册人数):        				SELECT COUNT(*) FROM user_status WHERE Cid = 10;
	 * 2 Rname(实名人数):              		SELECT COUNT(*) FROM user_status WHERE Cid = ? AND Reals = 1; 
	 * 3 Bcount(绑卡人数):        				SELECT COUNT(*) FROM user_status WHERE Cid = 10 AND Binds = 1;
	 * 4 Ncount(新手标投资人数):    				SELECT COUNT(*) FROM user_status s, log_order l WHERE s.Cid = 10 AND s.Uid = l.Uid and l.Tid = 1;
	 * 5 Nmoney(新手标投资金额):    				SELECT SUM(TMA) FROM log_order l, user_status s WHERE s.Cid = 10 AND l.Tid = 1;
	 * 6 Fcount(首投人数):        				SELECT COUNT(*) FROM user_status us, log_order lo WHERE us.Cid = 10 AND us.Uid = lo.Uid;
	 * 7 Scount(复投人数):        				SELECT COUNT(*) FROM user_status us ,log_order lo WHERE us.Cid = 10 AND us.Uid = lo.Uid HAVING COUNT(lo.Uid) >= 2;
	 * 8 Fmoney(首投金额):        				SELECT SUM(lo.TMA) FROM log_order lo, user_status us WHERE us.Cid = 10 AND us.Uid = lo.Uid ORDER BY lo.Time ASC LIMIT 0,1;
	 * 9 Smoney(复投金额):        				SELECT SUM(lo.TMA) FROM log_order lo, user_status us WHERE us.Cid = 10 AND us.Uid = lo.Uid;
	 * Tzsum(投资总额):         				Fmoney + Smoney
	 * 10 Txsum(提现总额):         			SELECT SUM(uc.TMA) FROM user_cash uc, user_status us WHERE us.Cid = 10 AND us.Uid = uc.Uid;
	 * Nastock(新增存量[投资总额-提现总额]):    	(Fmoney + Smoney) - Txsum
	 * 11 Sstock(平台总存量[充值总额-提现总额]):    	(SELECT SUM(MA) FROM user_status WHERE Cid = 10) - Txsum
	 */
	public Map<String, Object> getDynamicField(int cid, PreparedStatement ps, Connection conn) throws SQLException {
		Map<String, Object> result = new HashMap<>();
		ResultSet rs = null;
	
		// 注册人数
		String sql_1 = "SELECT COUNT(*) FROM user_status WHERE Cid = " + cid;
		// 实名人数
		String sql_2 = "SELECT COUNT(*) FROM user_status WHERE Cid = " + cid + " AND Reals = 1";
		// 绑卡人数
		String sql_3 = "SELECT COUNT(*) FROM user_status WHERE Cid = " + cid + " AND Binds = 1";
		// 新手标投资人数
		String sql_4 = "SELECT COUNT(*) FROM user_status s, log_order l WHERE s.Cid = " + cid + " AND s.Uid = l.Uid AND l.Tid = 1";
		// 新手标投资金额
		String sql_5 = "SELECT SUM(TMA) FROM log_order l, user_status s WHERE s.Cid = " + cid + " AND s.Uid = l.Uid AND l.Tid = 1";
		// 首投人数(已发生过购买行为的用户数量)
		String sql_6 = "SELECT COUNT(DISTINCT s.Uid) FROM user_status s, log_order l WHERE s.Cid = " + cid + " AND s.Uid = l.Uid";
		// 复投人数
		String sql_7 = "SELECT COUNT(*) FROM(SELECT COUNT(*) AS num FROM user_status s ,log_order l WHERE s.Cid = " + cid + " AND s.Uid = l.Uid GROUP BY l.Uid HAVING num >= 2) a";
		// 首投金额(已发生过购买行为的用户第一次购买金额总额,包含抵扣券金额)
		String sql_8 = "SELECT SUM(TMA) FROM log_order a INNER JOIN (SELECT Uid, MIN(GmtA) buyTime FROM log_order GROUP BY Uid) b ON b.buyTime = a.GmtA AND b.Uid = a.Uid inner join user_status c on a.Uid = c.Uid AND c.Cid = " + cid;
		// 复投金额(已发生过购买行为的用户从二次起购买金额总额,包含抵扣券金额)
		//String sql_9 = "SELECT SUM(value) FROM(SELECT SUM(l.TMA) as value FROM log_order l, user_status s WHERE s.Cid = " + cid + " AND s.Uid = l.Uid GROUP BY l.Uid HAVING COUNT(*) >= 2) a";
		String sql_9 = "SELECT SUM(TMA) FROM log_order a INNER JOIN (SELECT Uid, MIN(GmtA) buyTime FROM log_order GROUP BY Uid) b ON a.GmtA > b.buyTime AND b.Uid = a.Uid INNER JOIN user_status c ON c.Uid = a.Uid AND c.Cid = " + cid;
		// 提现总额
		String sql_10 = "SELECT SUM(uc.TMA) FROM user_cash uc, user_status us WHERE us.Cid = " + cid + " AND uc.Uid = us.Uid";
		// 充值总额
		String sql_11 = "SELECT SUM(Amount) FROM log_charge l, user_status u WHERE u.Cid = " + cid + " AND u.Uid = l.Uid";
		
		try {
			Map<String, Object> tmp = new HashMap<>();
			
			// key_1 ~ key_11
			for (int i = 1; i <= 11; i++) {
				switch (i) {
				case 1:
					ps = conn.prepareStatement( sql_1 );
					rs = ps.executeQuery();
					tmp.put( "key_" + i, rs.next() ? rs.getObject(1) : 0L );
					break;
					
				case 2:
					ps = conn.prepareStatement( sql_2 );
					rs = ps.executeQuery();
					tmp.put( "key_" + i, rs.next() ? rs.getObject(1) : 0L );
					break;
					
				case 3:
					ps = conn.prepareStatement( sql_3 );
					rs = ps.executeQuery();
					tmp.put( "key_" + i, rs.next() ? rs.getObject(1) : 0L );
					break;
					
				case 4:
					ps = conn.prepareStatement( sql_4 );
					rs = ps.executeQuery();
					tmp.put( "key_" + i, rs.next() ? rs.getObject(1) : 0L );
					break;
					
				case 5:
					ps = conn.prepareStatement( sql_5 );
					rs = ps.executeQuery();
					tmp.put( "key_" + i, rs.next() ? rs.getObject(1) : BigDecimal.ZERO );
					break;
					
				case 6:
					ps = conn.prepareStatement( sql_6 );
					rs = ps.executeQuery();
					tmp.put( "key_" + i, rs.next() ? rs.getObject(1) : 0L );
					break;
					
				case 7:
					ps = conn.prepareStatement( sql_7 );
					rs = ps.executeQuery();
					tmp.put( "key_" + i, rs.next() ? rs.getObject(1) : 0L );
					break;
					
				case 8:
					ps = conn.prepareStatement( sql_8 );
					rs = ps.executeQuery();
					tmp.put( "key_" + i, rs.next() ? rs.getObject(1) : BigDecimal.ZERO );
					break;
					
				case 9:
					ps = conn.prepareStatement( sql_9 );
					rs = ps.executeQuery();
					tmp.put( "key_" + i, rs.next() ? rs.getObject(1) : BigDecimal.ZERO );
					break;
					
				case 10: // 提现总额
					ps = conn.prepareStatement( sql_10 );
					rs = ps.executeQuery();
					tmp.put( "key_" + i, rs.next() ? rs.getObject(1) : BigDecimal.ZERO );
					break;
					
				case 11: // 充值总额
					ps = conn.prepareStatement( sql_11 );
					rs = ps.executeQuery();
					tmp.put( "key_" + i, rs.next() ? rs.getObject(1) : BigDecimal.ZERO );
					break;
				}
			}
			
			// key1 ~ key9
			for (int j = 1; j <= 9; j++) {
				if (j == 1 || j == 2 || j == 3 || j == 5 || j == 6) {
					result.put( "key_" + j, tmp.get( "key_" + j ) != null ? tmp.get( "key_" + j ) : 0L );
				}
				result.put( "key_" + j, tmp.get( "key_" + j ) != null ? tmp.get( "key_" + j ) : BigDecimal.ZERO );
			}
			
			// key10(投资总额)
			BigDecimal key_10_val = BigDecimal.ZERO;
			if (((BigDecimal) tmp.get( "key_8" )) != null) {
				key_10_val = key_10_val.add((BigDecimal) tmp.get( "key_8" ));
			}
			if (((BigDecimal) tmp.get( "key_9" )) != null) {
				key_10_val = key_10_val.add((BigDecimal) tmp.get( "key_9" ));
			}
			result.put( "key_10", key_10_val );
			
			// key11(提现总额)
			result.put( "key_11", tmp.get( "key_10" ) != null ? tmp.get( "key_10" ) : BigDecimal.ZERO );
			
			// key12(新增存量[投资总额 - 提现总额])
			BigDecimal key_12_val = BigDecimal.ZERO;
			if (key_10_val != null) {
				if ((BigDecimal) tmp.get( "key_11" ) != null) {
					key_12_val = ((BigDecimal) result.get( "key_10" )).subtract((BigDecimal) result.get( "key_11" ));
				}
			}
			result.put( "key_12", key_12_val );

			// key13(平台存量[充值总额 - 提现总额])
			BigDecimal key_13_val = BigDecimal.ZERO;
			if (((BigDecimal) tmp.get( "key_11" )) != null) {
				if ((BigDecimal) tmp.get( "key_10" ) != null) {
					key_13_val = ((BigDecimal) tmp.get( "key_11" )).subtract((BigDecimal) tmp.get( "key_10" ));
				}
			}
			result.put( "key_13", key_13_val );
			
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * @return Map<String, Object> <总统计字段名key, 总统计字段值val>
	 * @throws SQLException 
	 * 
	 * 汇总渠道总统计数据
	 */
	public Map<String, Object> sum() throws SQLException {
		Map<String, Object> res = new HashMap<>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		// 注册人数
		String sql_rcount = "SELECT COUNT(*) FROM user_status";
		// 实名人数
		String sql_rname = "SELECT COUNT(*) FROM user_status WHERE Reals = 1";
		// 绑卡人数
		String sql_bcount = "SELECT COUNT(*) FROM user_status WHERE Binds = 1";
		// 新手标投资人数
		String sql_ncount = "SELECT COUNT(*) FROM user_status s, log_order l WHERE s.Uid = l.Uid and l.Tid = 1";
		// 新手标投资金额
		String sql_nmoney = "SELECT SUM(TMA) FROM log_order l, user_status s WHERE s.Uid = l.Uid AND l.Tid = 1";
		// 首投人数(已发生购买行为的用户)
		String sql_fcount = "SELECT COUNT(DISTINCT s.Uid) FROM user_status s, log_order l WHERE s.Uid = l.Uid";
		// 复投人数(投资2次及以上的用户)
		String sql_scount = "select count(*) from(SELECT COUNT(*) AS num FROM user_status s ,log_order l WHERE s.Uid = l.Uid GROUP BY l.Uid HAVING num >= 2) a;";
		// 首投金额,已发生过购买行为的用户第一次购买金额总额（包含抵扣券金额）
		String sql_fmoney = "SELECT SUM(TMA) FROM log_order a INNER JOIN (SELECT Uid, MIN(GmtA) buyTime FROM log_order GROUP BY Uid) b ON b.buyTime = a.GmtA AND b.Uid = a.Uid";
		// 复投金额,已发生过购买行为的用户从二次起购买金额总额（包含抵扣券金额）
		//String sql_smoney = "SELECT SUM(value) FROM(SELECT SUM(l.TMA) as value FROM log_order l, user_status s WHERE s.Uid = l.Uid GROUP BY l.Uid HAVING COUNT(*) >= 2) a";
		String sql_smoney = "SELECT SUM(TMA) FROM log_order a INNER JOIN (SELECT Uid, MIN(GmtA) buyTime FROM log_order GROUP BY Uid) b ON a.GmtA > b.buyTime AND b.Uid = a.Uid;";
		// 提现总额
		String sql_txsum = "SELECT SUM(TMA) FROM user_cash";
		// 充值总额,基于计算平台总存量
		String sql_sstock_tmp = "SELECT SUM(Amount) FROM log_charge";
		
		// 投资总额
		//String sql_tzsum = sql_fmoney + sql_smoney;
		// 新增存量
		//String sql_nastock = (sql_fmoney + sql_smoney) - sql_txsum;
		// 平台存量: 充值总额-提现总额
		//String sql_sstock = sql_sstock_tmp - sql_txsum; 
		
		try {
			conn = JPrepare.getConnection();
			
			// 注册人数
			ps = conn.prepareStatement(sql_rcount);
			rs = ps.executeQuery();
			res.put( "rcount", rs.next() ? rs.getLong(1) : 0L );
			ps.close();
			
			// 实名人数
			ps = conn.prepareStatement(sql_rname);
			rs = ps.executeQuery();
			res.put( "rname", rs.next() ? rs.getLong(1) : 0L );
			ps.close();
			
			// 绑卡人数
			ps = conn.prepareStatement(sql_bcount);
			rs = ps.executeQuery();
			res.put( "bcount", rs.next() ? rs.getLong(1) : 0L );
			
			// 新手标投资人数
			ps = conn.prepareStatement(sql_ncount);
			rs = ps.executeQuery();
			res.put( "ncount", rs.next() ? rs.getLong(1) : 0L );
			
			// 新手标投资金额
			ps = conn.prepareStatement(sql_nmoney);
			rs = ps.executeQuery();
			res.put( "nmoney", rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO );
			
			// 首投人数
			ps = conn.prepareStatement(sql_fcount);
			rs = ps.executeQuery();
			res.put( "fcount", rs.next() ? rs.getLong(1) : 0L );
			
			// 复投人数
			ps = conn.prepareStatement(sql_scount);
			rs = ps.executeQuery();
			res.put( "scount", rs.next() ? rs.getLong(1) : 0L );
			
			// 首投金额
			ps = conn.prepareStatement(sql_fmoney);
			rs = ps.executeQuery();
			res.put( "fmoney", rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO );
			
			// 复投金额
			ps = conn.prepareStatement(sql_smoney);
			rs = ps.executeQuery();
			res.put( "smoney", rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO );
			
			// 提现总额
			ps = conn.prepareStatement(sql_txsum);
			rs = ps.executeQuery();
			res.put( "txsum", rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO );
			
			// 投资总额
			BigDecimal tzsum = ((BigDecimal) res.get("fmoney")).add((BigDecimal) res.get("smoney"));
			res.put( "tzsum", tzsum );
			
			// 新增存量：(首投金额 + 复投金额) - 提现总额
			BigDecimal nastock = (((BigDecimal) res.get("fmoney")).add((BigDecimal) res.get("smoney"))).subtract((BigDecimal) res.get("txsum"));
			res.put( "nastock", nastock );
			
			// 平台总存量：充值总额 - 提现总额
			ps = conn.prepareStatement(sql_sstock_tmp);
			rs = ps.executeQuery();
			BigDecimal sstock_tmp = rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
			res.put( "sstock", sstock_tmp.subtract((BigDecimal) res.get("txsum")));
			
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JPrepare.close( ps, conn );
		}
		
		return res;
	}

	/**
	 * @return String 
	 * @throws SQLException
	 * 
	 * 新增渠道，获取渠道编号
	 */
	public int getNextSid() throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int sid = 0;
		
		try {
			ps = conn.prepareStatement( "SELECT MAX(Sid)+1 FROM channel_count" );
			rs = ps.executeQuery();
			if (rs.next()) {
				sid = rs.getInt(1);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JPrepare.close( ps, conn );
		}
		
		return sid;
	}

	/**
	 * @return AjaxInfo
	 * 
	 * 渠道名称下拉数据
	 */
	public AjaxInfo getRinfos() {
		AjaxInfo json = AjaxInfo.getArray();
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement( "SELECT DISTINCT Rinfo FROM " + TBL_USER_STATUS + " WHERE Rinfo IS NOT NULL" );
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				json.formater();
				json.append( "id", rs.getString(1) );
				json.append( "text", rs.getString(1) );
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JPrepare.close(ps, conn);
		}
		
		return json;
	}
}
