package com.ypm.service.imp;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.ConfigInfoService;
import com.ypm.service.SysConfig;
import com.ypm.util.Constant;
import com.ypm.util.GMTime;
import com.ypm.util.VeRule;

/**
 * 系统参数配置业务层接口实现类. 
 */
public class ConfigInfoServiceImp extends AConfig implements ConfigInfoService {
	
	private static final Logger LOGGER = Logger.getLogger(ConfigInfoServiceImp.class);

	private static String SQL_BY_MAX;

	private static final String TBL_COMM_CFG = "comm_config", TBL_COMM_VER = "comm_ver";

	private SysConfig sysConfig;

	protected void checkSQL() {
		SQL_BY_MAX = JPrepare.getQuery("SELECT Sid,Tid,Code,Codever,Content,Tday,State,Time FROM " + TBL_COMM_VER + " WHERE Tid=? ORDER BY Sid DESC", 1);
	}

	public SysConfig getSysConfig() {
		return sysConfig;
	}

	public void setSysConfig(SysConfig sysConfig) {
		this.sysConfig = sysConfig;
	}

	public void save(Config cfg) throws SQLException {
		this.getSysConfig().saveConfig(cfg); // 保存数据
		if (VeRule.isYes("(USE_REWRITE|USE_DEBUG)", cfg.getId())) {
			this.getSysConfig().resetSystem();
		}
	}

	public AjaxInfo findConfigByAll() {
		AjaxInfo json = AjaxInfo.getArray();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Id,Remark FROM " + TBL_COMM_CFG + " ORDER BY Sortid ASC");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(1));
				json.append("text", rs.getString(2));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo findConfigByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + TBL_COMM_CFG).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, TBL_COMM_CFG, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} // 加载后续信息
			Map<String, String> ms = this.getDefault();
			Map<String, String> cs = this.getDictInfoBySSid(SYSTEM_CONFIG);
			sql.insert(0, "SELECT Id,Losk,Type,Sortid,Sindex,Content,Remark,Timeout,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql, offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.get(i));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				int c = rs.getInt(3);
				String dic = rs.getString(5);
				json.append("CLS", c); // 类型
				json.append("ID", rs.getString(1));
				json.append("LOSK", ms.get(rs.getString(2)));
				json.append("TYPE", cs.get(rs.getString(3)));
				json.append("SORTID", rs.getInt(4));
				json.append("SINDEX", dic);
				Map<String, String> m = null;
				switch (c) {
				case 3: // 货币
					json.append("CONTENT", DF2.format(rs.getDouble(6)));
					break;
				case 5: // 单选
					if (dic != null && dic.indexOf(".") > 0) {
						m = this.getDictInfoBySSid(dic);
					} else {
						m = ms;
					}
					json.append("CONTENT", m.get(rs.getString(6)));
					break;
				case 8: // 下拉
					if (dic != null && dic.indexOf(".") > 0) {
						m = this.getDictInfoBySSid(dic);
					} else {
						m = this.getInfoState();
					}
					json.append("CONTENT", m.get(rs.getString(6)));
					break;
				default:
					json.append("CONTENT", rs.getString(6));
				}
				json.append("REMARK", rs.getString(7));
				long out = rs.getLong(8);
				if (out == 0) {
					json.appends("TIMEOUT", "-");
				} else if (out >= Constant.USE_START) {
					json.appends("TIMEOUT", GMTime.format(out, GMTime.CHINA));
				} else {
					json.append("TIMEOUT", out);
				}
				json.appends("TIME", GMTime.format(rs.getLong(9), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo findClientByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + TBL_COMM_VER).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, TBL_COMM_VER, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} // 加载后续信息
			Map<String, String> ms = this.getInfoState();
			sql.insert(0, "SELECT Sid,Tid,Code,Codever,Tday,State,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql, offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.get(i));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("SID", rs.getString(1));
				json.append("TID", rs.getInt(2));
				json.appends("CODE", rs.getString(3));
				json.appends("CODEVER", rs.getString(4));
				json.appends("TDAY", rs.getString(5));
				json.append("STATE", ms.get(rs.getString(6)));
				json.append("TIME", GMTime.format(rs.getLong(7), GMTime.CHINA));
			}
			rs.close();
		} catch (Exception e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public SetClient findClientBySid(int sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			SetClient sc = null;
			ps = conn.prepareStatement("SELECT Sid,Tid,Code,Codever,Filename,Content,Tday,State,Time FROM comm_ver WHERE Sid=?");
			ps.setInt(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				sc = new SetClient();
				sc.setSid(rs.getInt(1));
				sc.setTid(rs.getInt(2));
				sc.setCode(rs.getInt(3));
				sc.setCodever(rs.getString(4));
				sc.setFilename(rs.getString(5));
				sc.setContent(rs.getString(6));
				sc.setTday(rs.getString(7));
				sc.setState(rs.getInt(8));
				sc.setTime(rs.getLong(9));
			}
			rs.close();
			return sc;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public SetClient findClientByTid(int tid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			SetClient sc = new SetClient();
			ps = conn.prepareStatement(SQL_BY_MAX);
			ps.setInt(1, tid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				sc.setSid(rs.getInt(1));
				sc.setTid(rs.getInt(2));
				sc.setCode(rs.getInt(3));
				sc.setCodever(rs.getString(4));
				sc.setContent(rs.getString(5));
				sc.setTday(rs.getString(6));
				sc.setState(rs.getInt(7));
				sc.setTime(rs.getLong(8));
			}
			rs.close();
			return sc;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	/** 同步选择器 */
	private SyncMap getSyncer(int adt) {
		return SyncMap.getAll();
	}

	public void saveClient(SetClient sc) throws IOException, SQLException {
		sc.setTime(System.currentTimeMillis());
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			if (sc.getSid() <= 0) {
				sc.setCode(getId(conn, TBL_COMM_VER, "Code", "Tid=?", sc.getTid()));
				ps = conn.prepareStatement("SELECT MAX(Sid) FROM " + TBL_COMM_VER + " WHERE Tid=?");
				ps.setInt(1, sc.getTid());
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					sc.setSid(rs.getInt(1));
				}
				rs.close();
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_COMM_VER + " (Sid,Tid,Code,Codever,Filename,Size,Content,Tday,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?)");
				ps.setInt(1, sc.newSid());
				ps.setInt(2, sc.getTid());
				ps.setInt(3, sc.getCode());
				ps.setString(4, sc.getCodever());
				ps.setString(5, sc.getFilename());
				ps.setDouble(6, sc.getSize());
				ps.setString(7, sc.getContent());
				ps.setString(8, sc.getTday());
				ps.setInt(9, sc.getState());
				ps.setLong(10, sc.getTime());
			} else {
				ps = conn.prepareStatement("UPDATE " + TBL_COMM_VER + " SET Codever=?,Filename=?,Size=?,Content=?,Tday=?,State=?,Time=? WHERE Sid=?");
				ps.setString(1, sc.getCodever());
				ps.setString(2, sc.getFilename());
				ps.setDouble(3, sc.getSize());
				ps.setString(4, sc.getContent());
				ps.setString(5, sc.getTday());
				ps.setInt(6, sc.getState());
				ps.setLong(7, sc.getTime());
				ps.setInt(8, sc.getSid());
			}
			ps.executeUpdate();
		} finally {
			JPrepare.close(ps, conn);
		} // 同步操作
		getSyncer(sc.getTid()).send(SYS_A112, "syncClient", sc).successfully();
	}
	
	public void saveAndroidClient(SetClient sc) throws IOException, SQLException {
		sc.setTime(System.currentTimeMillis());
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		
		try {
			if (sc.getSid() <= 0) {
				sc.setCode(getId( conn, TBL_COMM_VER, "Code", "Tid = ?", sc.getTid()) );
				ps = conn.prepareStatement( "SELECT MAX(Sid) FROM " + TBL_COMM_VER + " WHERE Tid = ?" );
				ps.setInt( 1, sc.getTid() );
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					sc.setSid(rs.getInt(1));
				}
				rs.close();
				ps.close();
				ps = conn.prepareStatement( "INSERT INTO " + TBL_COMM_VER + " (Sid, Tid, Code, Codever, Filename, Size, Content, Tday, State, Time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" );
				ps.setInt( 1, sc.newSid() );
				ps.setInt( 2, sc.getTid() );
				ps.setInt( 3, sc.getCode() );
				ps.setString( 4, sc.getCodever() );
				ps.setString( 5, sc.getFilename() );
				ps.setDouble( 6, sc.getSize() );
				ps.setString( 7, sc.getContent() );
				ps.setString( 8, sc.getTday() );
				ps.setInt( 9, sc.getState() );
				ps.setLong( 10, sc.getTime() );
			} else {
				ps = conn.prepareStatement( "UPDATE " + TBL_COMM_VER + " SET Codever = ?, Filename = ?, Size = ?, Content = ?, Tday = ?, State = ?, Time = ? WHERE Sid = ?" );
				ps.setString( 1, sc.getCodever() );
				ps.setString( 2, sc.getFilename() );
				ps.setDouble( 3, sc.getSize() );
				ps.setString( 4, sc.getContent() );
				ps.setString( 5, sc.getTday() );
				ps.setInt( 6, sc.getState() );
				ps.setLong( 7, sc.getTime() );
				ps.setInt( 8, sc.getSid() );
			}
			ps.executeUpdate();
		} finally {
			JPrepare.close( ps, conn );
		} 
	}

	public void updateClient(int type, String ids, int state) throws IOException, SQLException {
		long time = System.currentTimeMillis();
		SyncMap sm = getSyncer(type);
		sm.add("ids", ids).add("state", state).add("time", time);
		sm.send(SYS_A112, "stateClient").successfully();
		Set<Integer> set = this.toInt(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_COMM_VER + " SET State=?,Time=? WHERE Sid=?");
			for (Integer s : set) {
				ps.setInt(1, state);
				ps.setLong(2, time);
				ps.setInt(3, s.intValue());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void removeClient(int type, String ids) throws IOException, SQLException {
		getSyncer(type).add("ids", ids).send(SYS_A112, "delClient").successfully();
		Set<Integer> set = this.toInt(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("DELETE FROM " + TBL_COMM_VER + " WHERE Sid=?");
			for (Integer s : set) {
				ps.setInt(1, s.intValue());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public Config findConfigById(String id) {
		return this.getSysConfig().getConfig(id);
	}

	public void saveConfig(Config cfg) throws SQLException {
		if (cfg.getSortid() <= 0) {
			cfg.setSortid((int) (JPrepare.getTotal(TBL_COMM_CFG) + 1));
		}
		cfg.setTime(System.currentTimeMillis());
		this.save(cfg); 
		// 同步保存数据信息
		LOGGER.info( "同步保存系统参数配置信息前的对象为：" + cfg.toString() );
		SyncMap.getAll().sender( SYS_A112, "saveConfig", cfg );
	}

	public boolean isConfigById(String id) {
		return JPrepare.isExists("SELECT Id FROM " + TBL_COMM_CFG + " WHERE Id=?", id);
	}

	public void orderConfig(String ids) throws SQLException {
		String ts[] = this.toSplit(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_COMM_CFG + " SET Sortid=? WHERE Id=?");
			for (String id : ts) {
				ps.setInt(1, index++);
				ps.setString(2, id);
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			SyncMap.getAll().add("ids", ids).sender(SYS_A112, "orderConfig");
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public boolean removeConfig(String ids) throws IOException, SQLException {
		String ts[] = this.toSplit(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			boolean result = true;
			conn.setAutoCommit(false);
			StringBuilder sb = new StringBuilder();
			for (String id : ts) {
				if (ps != null) {
					ps.close();
				} // delete Config
				ps = JPrepare.prepareStatement(conn, "SELECT Id,Losk FROM " + TBL_COMM_CFG + " WHERE Id=?");
				ps.setString(1, id);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					if (rs.getInt(2) == STATE_DISABLE) {
						result = false;
					} else {
						sb.append(',').append(rs.getString(1));
						rs.deleteRow();
					}
				}
				rs.close();
			}
			conn.commit();
			if (sb.length() > 1) {
				SyncMap.getAll().add("ids", sb.substring(1)).sender(SYS_A112, "delConfig");
			}
			return result;
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	/**
	 * @author xk
	 * @param sc is SetClient
	 * @param fileInfo is FileInfo
	 * @throws  IOException, SQLException
	 * 
	 * 保存安卓版本信息
	 */
	public void saveClient(SetClient sc, FileInfo fileInfo) throws IOException, SQLException {
		Connection conn = JPrepare.getConnection();
		
		try {
			// 保存数据
			this.saveAndroidClient(sc); 
		} finally {
			JPrepare.close(conn);
		} 
		// 保存文件
		fileInfo.setTime( sc.getSid(), sc.getTime() );
		fileInfo.setDist( "apk" );
		this.saveApkFile(fileInfo);
		// 同步数据信息
		SyncMap.getAll().sender( SYS_A112, "syncClient", sc );
	}
}
