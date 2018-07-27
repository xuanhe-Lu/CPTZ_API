package com.ypm.service.imp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ypm.bean.AjaxInfo;
import com.ypm.bean.Help;
import com.ypm.bean.SyncMap;
import com.ypm.data.JPrepare;
import com.ypm.service.HelpService;
import com.ypm.util.GMTime;

/**	
 * 常见问题业务层接口实现类.
 * 
 * Created by xk on 2018-05-09.
 */
public class HelpServiceImp extends AConfig implements HelpService {

	// 表名
	private static final String TBL_COMM_HELP = "comm_help";

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
		PreparedStatement preparedStatement = conn.prepareStatement( "UPDATE " + TBL_COMM_HELP + " SET Total = ? WHERE Sid = ?" );
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
	 * 列表数据获取1
	 */
	public AjaxInfo findHelpByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace( 1, 4, "WHERE" );
		} 
		// get total
		fs.add( 0, sql.insert( 0, "FROM " + TBL_COMM_HELP ).toString() );
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal( conn, TBL_COMM_HELP, fs );
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} 
			// 加载数据字典信息
			Map<String, String> stateDic = this.getDictInfoBySSid(SYSTEM_STATE);
			Map<String, String> problemTypeDic = this.getDictInfoBySSid(PROBLEM_TYPE);
			sql.insert( 0, "SELECT Sid, Tid, Question, Answer, Sortid, State, Time " ).append( " ORDER BY " ).append(order);
			preparedStatement = conn.prepareStatement(JPrepare.getQuery( sql, offset, max ));
			for (int i = 1, j = fs.size(); i < j; i++) {
				preparedStatement.setObject( i, fs.get(i) );
			} 
			// 查询结果
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getString(1) );
				json.append( "TID", problemTypeDic.get(resultSet.getString(2)) );
				json.append( "QUESTION", resultSet.getString(3) );
				json.append( "ANSWER", resultSet.getString(4) );
				json.append( "SORTID", resultSet.getInt(5) );
				json.append( "STATE", stateDic.get(resultSet.getString(6)) );
				json.append( "TIME", GMTime.format( resultSet.getLong(7), GMTime.CHINA ) );
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
	public AjaxInfo findHelpByAll(StringBuilder sql) {
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement(sql.toString());
			
			// 查询结果
			ResultSet resultSet = ps.executeQuery();
			// 加载数据字典信息
			Map<String, String> stateDic = this.getDictInfoBySSid(SYSTEM_STATE);
			Map<String, String> problemTypeDic = this.getDictInfoBySSid(PROBLEM_TYPE);
			while (resultSet.next()) {
				json.formater();
				json.append( "SID", resultSet.getString(1) );
				json.append( "TID", problemTypeDic.get(resultSet.getString(2)) );
				json.append( "QUESTION", resultSet.getString(3) );
				json.append( "ANSWER", resultSet.getString(4) );
				json.append( "SORTID", resultSet.getInt(5) );
				json.append( "STATE", stateDic.get(resultSet.getString(6)) );
				json.append( "TIME", GMTime.format( resultSet.getLong(7), GMTime.CHINA ) );
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JPrepare.close( ps, conn );
		}
		
		return json;
	}

	/**
	 * @param conn is Connection
	 * @param help is Help
	 * @throws SQLException
	 * 
	 * 保存数据
	 */
	private static void save(Connection conn, Help help) throws SQLException {
		PreparedStatement ps = conn.prepareStatement( "UPDATE " + TBL_COMM_HELP + " SET Tid = ?, Question = ?, Answer = ?, Sortid = ?, State = ?, Time = ? WHERE Sid = ?" );
		try {
			ps.setInt( 1, help.getTid() );
			ps.setString( 2, help.getQuestion() );
			ps.setString( 3, help.getAnswer() );
			ps.setInt( 4, help.getSortid() );
			ps.setInt( 5, help.getState() );
			ps.setLong( 6, help.getTime() );
			ps.setString( 7, help.getSid() );
			
			// 若更新操作失败，则开始新增
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement( "INSERT INTO " + TBL_COMM_HELP + " (Sid, Tid, Question, Answer, Sortid, State, Time) VALUES (?, ?, ?, ?, ?, ?, ?)" );
				
				ps.setString( 1, help.getSid() );
				ps.setInt( 2, help.getTid() );
				ps.setString( 3, help.getQuestion() );
				ps.setString( 4, help.getAnswer() );
				ps.setInt( 5, help.getSortid() );
				ps.setInt( 6, help.getState() );
				ps.setLong( 7, help.getTime() );
				
				ps.executeUpdate();
			}
		} finally {
			JPrepare.close( ps, conn );
		}
	}

	/**
	 * @param sid String
	 * @return Help
	 * @throws SQLException
	 * 
	 * 根据id查询数据
	 */
	public Help findHelpBySId(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		
		try {
			Help help = null;
			ps = conn.prepareStatement( "SELECT Sid, Tid, Question, Answer, Sortid, State, Time FROM " + TBL_COMM_HELP + " WHERE Sid = ?" );
			ps.setString( 1, sid );
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				help = new Help();
				help.setSid( rs.getString(1) );
				help.setTid( rs.getInt(2) );
				help.setQuestion( rs.getString(3) );
				help.setAnswer( rs.getString(4) );
				help.setSortid( rs.getInt(5) );
				help.setState( rs.getInt(6) );
				help.setTime( rs.getLong(7) );
			}
			rs.close();
			return help;
		} finally {
			JPrepare.close( ps, conn );
		}
	}

	/**
	 * @param help is Help
	 * @throws SQLException, IOException
	 * 
	 * 新增保存
	 */
	public void saveHelp(Help help) throws IOException, SQLException {
		Connection conn = JPrepare.getConnection();
		
		try {
			// 保存数据
			save( conn, help ); 
		} finally {
			JPrepare.close(conn);
		} 
		// 同步数据信息
		SyncMap.getAll().sender( SYS_A505, "save", help );
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
			ps = conn.prepareStatement( "DELETE FROM " + TBL_COMM_HELP + " WHERE Sid = ?" );
			ps.setString( 1, sid );
			if (ps.executeUpdate() > 0) {
				// 同步删除数据
				SyncMap.getAll().add( "sid", sid ).sender( SYS_A505, "remove" );
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
