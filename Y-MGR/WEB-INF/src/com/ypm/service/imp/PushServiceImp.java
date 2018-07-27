package com.ypm.service.imp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.dto.GtReq.Target;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.Push;
import com.ypm.bean.SyncMap;
import com.ypm.data.JPrepare;
import com.ypm.service.PushService;
import com.ypm.util.GMTime;

/**	
 * 推送管理业务层接口实现类
 * 
 * Created by xk on 2018-06-04.
 */
public class PushServiceImp extends AConfig implements PushService {

	// 表名
	private static final String TBL_PUSH = "push";

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
	public int update(Connection conn, long sid, int row) throws SQLException {
		PreparedStatement preparedStatement = conn.prepareStatement( "UPDATE " + TBL_PUSH + " SET Total = ? WHERE Sid = ?" );
		
		try {
			preparedStatement.setInt( 1, row );
			preparedStatement.setLong( 2, sid );
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
		fs.add( 0, sql.insert( 0, "FROM " + TBL_PUSH ).toString() );
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal( conn, TBL_PUSH, fs );
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} 
			// 加载数据字典信息
			Map<String, String> systemDic = this.getDictInfoBySSid(PUSH_SYSTEM);
			Map<String, String> typeDic = this.getDictInfoBySSid(PUSH_TYPE);
			Map<String, String> targetDic = this.getDictInfoBySSid(PUSH_TARGET);
			Map<String, String> stateDic = this.getDictInfoBySSid(PUSH_STATE);
			sql.insert( 0, "SELECT Sid, Title, System, Type, Target, State " ).append( " ORDER BY " ).append(order);
			preparedStatement = conn.prepareStatement(JPrepare.getQuery( sql, offset, max ));
			for (int i = 1, j = fs.size(); i < j; i++) {
				preparedStatement.setObject( i, fs.get(i) );
			} 
			// 查询结果
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getLong(1) );
				json.append( "TITLE", resultSet.getString(2) );
				json.append( "SYSTEM", systemDic.get(resultSet.getString(3)) );
				json.append( "TYPE", typeDic.get(resultSet.getString(4)) );
				json.append( "TARGET", targetDic.get(resultSet.getString(5)) );
				json.append( "STATE", stateDic.get(resultSet.getString(6)) );
				json.append( "TIME", GMTime.format( resultSet.getLong(7), GMTime.CHINA ) );
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
			Map<String, String> systemDic = this.getDictInfoBySSid(PUSH_SYSTEM);
			Map<String, String> typeDic = this.getDictInfoBySSid(PUSH_TYPE);
			Map<String, String> targetDic = this.getDictInfoBySSid(PUSH_TARGET);
			Map<String, String> stateDic = this.getDictInfoBySSid(PUSH_STATE);
			
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getLong(1) );
				json.append( "TITLE", resultSet.getString(2) );
				json.append( "SYSTEM", systemDic.get(resultSet.getString(3)) );
				json.append( "TYPE", typeDic.get(resultSet.getString(4)) );
				json.append( "TARGET", targetDic.get(resultSet.getString(5)) );
				json.append( "STATE", stateDic.get(resultSet.getString(6)) );
				json.append( "TIME", GMTime.format( resultSet.getLong(7), GMTime.CHINA ) );
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
	 * @param push is Push
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	private static void save(Connection conn, Push push) throws SQLException {
		PreparedStatement ps = conn.prepareStatement( "UPDATE " + TBL_PUSH + " SET Title = ?, Content = ?, System = ?, Type = ?, Timer = ?, Target = ?, Uid = ?, State = ?, Time = ? WHERE Sid = ?" );
		
		try {
			ps.setString( 1, push.getTitle() );
			ps.setString( 2, push.getContent() );
			ps.setInt( 3, push.getSystem() );
			ps.setInt( 4, push.getType() );
			ps.setLong( 5, push.getTimer() );
			ps.setInt( 6, push.getTarget() );
			ps.setLong( 7, push.getUid() );
		    ps.setInt( 8, push.getState() );
		    ps.setLong( 9, push.getTime() );
		    ps.setLong( 10, push.getSid() );
			
			// 若更新操作失败，则开始新增
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement( "INSERT INTO " + TBL_PUSH + " (Title, Content, System, Type, Timer, Target, Uid, State, Time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)" );
				
				ps.setString( 1, push.getTitle() );
				ps.setString( 2, push.getContent() );
				ps.setInt( 3, push.getSystem() );
				ps.setInt( 4, push.getType() );
				ps.setLong( 5, push.getTimer() );
				ps.setInt( 6, push.getTarget() );
				ps.setLong( 7, push.getUid() );
			    ps.setInt( 8, push.getState() );
			    ps.setLong( 9, push.getTime() );
				
				ps.execute();
			}
		} finally {
			JPrepare.close( ps, conn );
		}
	}

	/**
	 * @param sid long
	 * @return Push
	 * @throws SQLException
	 * 
	 * 根据id查询数据
	 */
	public Push findPushBySId(long sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			Push push = null;
			preparedStatement = conn.prepareStatement( "SELECT Title, Content, System, Type, Timer, Target, Uid, State, Time FROM " + TBL_PUSH + " WHERE Sid = ?" );
			preparedStatement.setLong( 1, sid );
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				push = new Push();
				push.setSid( sid );
				push.setTitle(resultSet.getString(1));
				push.setContent(resultSet.getString(2));
				push.setSystem(resultSet.getInt(3));
				push.setType(resultSet.getInt(4));
				push.setTimer(resultSet.getLong(5));
				push.setTarget(resultSet.getInt(6));
				push.setUid(resultSet.getLong(7));
			    push.setState(resultSet.getInt(8));
			    push.setTime(resultSet.getLong(9));
			}
			resultSet.close();
			return push;
		} finally {
			JPrepare.close( preparedStatement, conn );
		}
	}

	/**
	 * @param push is Push
	 * @throws SQLException, IOException
	 * 
	 * 新增保存
	 */
	public void savePush(Push push) throws IOException, SQLException {
		Connection conn = JPrepare.getConnection();
		
		try {
			// 保存数据
			save( conn, push ); 
		} finally {
			JPrepare.close(conn);
		} 
		
		// 同步数据信息
		SyncMap.getAll().sender( SYS_A508, "save", push );
	}
	
	/**
	 * @param sid long
	 * @return boolean
	 * @throws SQLException
	 * 
	 * 删除
	 */
	public boolean remove(long sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement preparedStatement = null;
		
		try {
			boolean result = false;
			conn.setAutoCommit(false);
			preparedStatement = conn.prepareStatement( "DELETE FROM " + TBL_PUSH + " WHERE Sid = ?" );
			preparedStatement.setLong( 1, sid );
			if (preparedStatement.executeUpdate() > 0) {
				// 同步删除数据
				SyncMap.getAll().add( "sid", sid ).sender( SYS_A508, "remove" );
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
	 * 单个用户推送消息接口
	 * 
	 * @param message is SingleMessage
	 * @param target is Target
	 * @return IPushResult
	 */
	public IPushResult pushMessageToSingle(SingleMessage message, Target target) {
		return null;
	}

	/**
	 * 单个用户推送消息异常重试接口
	 * 
	 * @param message is SingleMessage
	 * @param target is Target
	 * @param requestId String
	 * @return IPushResult
	 */
	public IPushResult pushMessageToSingle(SingleMessage message, Target target, String requestId) {
		return null;
	}
}
