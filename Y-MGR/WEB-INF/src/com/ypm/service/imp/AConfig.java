package com.ypm.service.imp;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import org.commons.collect.MapMaker;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.util.*;

public abstract class AConfig implements APSKey, AState, VeKey {

	protected static final DecimalFormat DF1 = new DecimalFormat("0.0");

	protected static final DecimalFormat DF2 = new DecimalFormat("0.00");

	protected static final DecimalFormat DF3 = new DecimalFormat("#,##0.00");

	protected static final String KEY_DICT = "dict";

	private static Map<String, ArrayList<FieldInfo>> CACHE_FIELD;

	private static Map<String, Object> CACHE_INFOS;

	private static Map<String, DictMenu> CACHE_MENU;

	private static Map<String, TagCache> CACHE_TAGS;

	private static Map<Integer, Long> CACHE_TOTAL;

	public AConfig() {
		if (CACHE_FIELD == null) {
			CACHE_FIELD = new MapMaker().concurrencyLevel(32).expiration(600000).makeMap(); // 10
			CACHE_MENU = new MapMaker().concurrencyLevel(32).expiration(600000).makeMap(); // 10
			CACHE_TAGS = new MapMaker().concurrencyLevel(32).expiration(600000).makeMap(); // 10
			CACHE_INFOS = new MapMaker().concurrencyLevel(32).expiration(20000).makeMap(); // 20'
			CACHE_TOTAL = new MapMaker().concurrencyLevel(32).expiration(15000).makeMap(); // 15'
		}
		this.checkSQL();
	}

	/** 检测执行SQL */
	protected abstract void checkSQL();

	/** 清除报表结构缓存 */
	protected void clearField() {
		CACHE_FIELD.clear();
	}

	protected void close(PreparedStatement ps) throws SQLException {
		if (ps == null) {
			// Ignored
		} else if (ps.isClosed()) {
			// is Closed
		} else {
			ps.close();
		}
	}

	protected void deletes(String filePath) {
		this.execute(() -> VeFile.deletes(filePath));
	}

	protected void deletes(String fn, String... dirs) {
		this.execute(() -> VeFile.deletes(fn, dirs));
	}

	protected void deletes(String dir, String[] ts, String id) {
		this.execute(() -> VeFile.deletes(dir, ts, id));
	}

	protected void execute(Runnable run) {
		com.ypm.service.PoolService.getService().execute(run);
	}

	public DictInfo findDictInfoBySid(String sid, String id) {
		DictMenu menu = this.getDictMenuBySid(sid);
		if (menu == null) {
			return null;
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			DictInfo info = null;
			conn = JPrepare.getConnection();
			if (menu.getType() <= DICT_DEFS) {
				ps = conn.prepareStatement("SELECT Id,Sid,Sortid,Name,Value,Remark FROM dict_defs WHERE Sid=? AND Id=?");
				ps.setInt(1, menu.getSid());
				ps.setString(2, id);
			} else if (menu.getType() == DICT_INFO) {
				ps = conn.prepareStatement("SELECT Id,Sid,Sortid,Name,Value,Remark FROM dict_info WHERE Id=?");
				ps.setInt(1, Integer.parseInt(id));
			} else {
				ps = conn.prepareStatement("SELECT Id,Sid,Sortid,Name,Value,Remark FROM dict_user WHERE Id=?");
				ps.setInt(1, Integer.parseInt(id));
			}
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				info = new DictInfo();
				info.setId(rs.getString(1));
				info.setSid(rs.getInt(2));
				info.setSNo(menu.getSNo());
				info.setType(menu.getType());
				info.setSortid(rs.getInt(3));
				info.setName(rs.getString(4));
				info.setValue(rs.getString(5));
				info.setRemark(rs.getString(6));
			}
			rs.close();
			return info;
		} catch (Exception e) {
			return null;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public DictMenu findDictMenuBySid(String sid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			DictMenu menu = null;
			ps = conn.prepareStatement("SELECT Sid,Tid,Sortid,SNo,Name,Type,Leaf,Losk FROM dict_menu WHERE SNo=?");
			ps.setString(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				menu = new DictMenu();
				menu.setSid(rs.getInt(1));
				menu.setTid(rs.getInt(2));
				menu.setSortid(rs.getInt(3));
				menu.setSNo(rs.getString(4));
				menu.setName(rs.getString(5));
				menu.setType(rs.getInt(6));
				menu.setLosk(rs.getInt(8));
				CACHE_MENU.put(sid, menu);
			}
			rs.close();
			return menu;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public DictMenu getDictMenuBySid(String sid) {
		if (sid == null) {
			return null;
		}
		String key = sid.trim();
		DictMenu menu = CACHE_MENU.get(key);
		if (menu == null) {
			try {
				return this.findDictMenuBySid(key);
			} catch (SQLException e) {
				// Ignored
			}
		}
		return menu;
	}

	private Map<String, String> getDictInfo(String sid, String sql) {
		StringBuilder sb = new StringBuilder(); // 构建关键字
		String key = sb.append("At").append(sid).toString();
		TagCache tag = CACHE_TAGS.get(key);
		if (tag == null) {
			sb.setLength(0);
			sb.append(Constant.ROOTPATH).append(Constant.OSCACHE).append(File.separator).append(KEY_DICT).append(File.separator).append(key);
			tag = new TagCache(new File(sb.append(".txt").toString()));
			CACHE_TAGS.put(key, tag);
		} // 加载数据
		if (tag.isExpired()) {
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = JPrepare.getConnection();
				ps = conn.prepareStatement(sql);
				ps.setString(1, sid);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					tag.add(rs.getString(1), rs.getString(2));
				}
				rs.close();
			} catch (Exception e) {
				// Ignored
			} finally {
				JPrepare.close(ps, conn);
			}
		} // 输出结果
		return tag.getInfo();
	}

	/** 默认字典参数 */
	protected Map<String, String> getDictInfoByDSid(String sid) {
		return this.getDictInfo(sid, "SELECT B.Id,B.Name FROM dict_menu AS A INNER JOIN dict_defs AS B ON A.Sid=B.Sid WHERE A.SNo=? ORDER BY B.Sortid ASC");
	}

	/** 系统字典参数 */
	protected Map<String, String> getDictInfoBySSid(String sid) {
		return this.getDictInfo(sid, "SELECT B.Name,B.Value FROM dict_menu AS A INNER JOIN dict_info AS B ON A.Sid=B.Sid WHERE A.SNo=? ORDER BY B.Sortid ASC");
	}

	/** 用户字典参数 */
	protected Map<String, String> getDictInfoByUSid(String sid) {
		return this.getDictInfo(sid, "SELECT B.Name,B.Value FROM dict_menu AS A INNER JOIN dict_user AS B ON A.Sid=B.Sid WHERE A.SNo=? ORDER BY B.Sortid ASC");
	}

	/** 是、否 */
	protected Map<String, String> getDefault() {
		return getDictInfoBySSid(SYSTEM_DEFAULT);
	}

	/** 状态信息-2 */
	protected Map<String, String> getInfoState() {
		return getDictInfoBySSid(SYSTEM_STATE);
	}

	/** 状态列表-4 */
	protected Map<String, String> getInfoStates() {
		return getDictInfoBySSid(SYSTEM_STATUS);
	}

	/** 列表显示列信息 */
	public FieldMenu findFieldMenuById(String sno) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			FieldMenu menu = null;
			ps = conn.prepareStatement("SELECT Sid,Tid,Sortid,SNo,Name,Title,Value,Losk FROM field_menu WHERE SNo=?");
			ps.setString(1, sno);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				menu = new FieldMenu();
				menu.setSid(rs.getInt(1));
				menu.setTid(rs.getInt(2));
				menu.setSortid(rs.getInt(3));
				menu.setSno(rs.getString(4));
				menu.setName(rs.getString(5));
				menu.setTitle(rs.getString(6));
				menu.setValue(rs.getString(7));
				menu.setLosk(rs.getInt(8));
			}
			rs.close();
			return menu;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	protected List<FieldInfo> getFieldInfoById(String fid) {
		String key = fid.toLowerCase();
		ArrayList<FieldInfo> ls = CACHE_FIELD.get(key);
		if (ls != null) {
			return ls;
		} // 缓存中加载
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT A.Id,A.Sortid,A.Name,A.Nice,A.Width,A.Pkey,A.Display,A.Sortab,A.Export,A.Type,A.Format FROM field_info AS A INNER JOIN field_menu AS B ON A.Sid=B.Sid WHERE B.SNo=? ORDER BY A.Sortid ASC");
			ps.setString(1, fid);
			ResultSet rs = ps.executeQuery();
			ls = new ArrayList<FieldInfo>();
			while (rs.next()) {
				FieldInfo info = new FieldInfo();
				info.setId(rs.getInt(1));
				info.setSortid(rs.getInt(2));
				info.setName(rs.getString(3));
				info.setNice(rs.getString(4));
				info.setWidth(rs.getInt(5));
				info.setPkid(rs.getBoolean(6));
				info.setShow(rs.getInt(7));
				info.setSortab(rs.getInt(8));
				info.setExport(rs.getInt(9));
				info.setType(rs.getInt(10));
				info.setFormat(rs.getString(11));
				ls.add(info);
			}
			rs.close();
			ls.trimToSize();
			CACHE_FIELD.put(key, ls);
			return ls; // 结果
		} catch (Exception e) {
			return null;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	/** 新记录编号 */
	protected int getId(Connection conn, String tbl, String name) throws SQLException {
		Statement stmt = conn.createStatement();
		try {
			int sid = 1;
			StringBuilder sql = new StringBuilder(32);
			ResultSet rs = stmt.executeQuery(sql.append("SELECT MAX(").append(name).append(") FROM ").append(tbl).toString());
			if (rs.next()) {
				sid += rs.getInt(1);
			}
			rs.close();
			return sid;
		} finally {
			stmt.close();
		}
	}

	protected int getId(Connection conn, String tbl, String name, String wstr, Object... objs) throws SQLException {
		if (wstr == null) {
			return getId(conn, tbl, name);
		} // id
		StringBuilder sql = new StringBuilder();
		PreparedStatement ps = conn.prepareStatement(sql.append("SELECT MAX(").append(name).append(") FROM ").append(tbl).append(" WHERE ").append(wstr).toString());
		try {
			int index = 1, sid = 1;
			for (Object obj : objs) {
				ps.setObject(index++, obj);
			} // search num
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				sid += rs.getInt(1);
			}
			rs.close();
			return sid;
		} finally {
			ps.close();
		}
	}

	/** 构建数据新编号 */
	protected String getSid(Connection conn, String fix, int s, String sql, Object... args) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(sql);
		try {
			int index = 1;
			String str = "1";
			for (Object obj : args) {
				ps.setObject(index++, obj);
			}
			StringBuilder sb = new StringBuilder();
			ResultSet rs = ps.executeQuery();
			if (rs.next() && rs.getString(1) != null) {
				char[] cs = (str = rs.getString(1)).toCharArray();
				int i = cs.length, j = 0;
				for (; i > 0; i--) {
					j = cs[i - 1];
					if (j < 48 || j > 57)
						break;
				}
				sb.append(str.substring(0, i));
				str = String.valueOf(Integer.parseInt(str.substring(i)) + 1);
			} else if (fix == null) {
				// Ignored
			} else {
				sb.append(fix);
			}
			rs.close(); // check
			for (int i = str.length(); i < s; i++) {
				sb.append("0");
			}
			return sb.append(str).toString();
		} finally {
			ps.close();
		}
	}

	/** 序号加载 */
	protected int getSortid(Connection conn, String sql, Object... args) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(sql);
		try {
			int index = 1, sortid = 1;
			for (Object obj : args) {
				ps.setObject(index++, obj);
			}
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				sortid += rs.getInt(1);
			}
			rs.close();
			return sortid;
		} finally {
			ps.close();
		}
	}
	/** 记录信息获取 */
	protected long getTotal(Connection conn, String table) throws SQLException {
		Integer key = table.hashCode();
		Long total = CACHE_TOTAL.get(key);
		if (total == null) {
			PreparedStatement ps = conn.prepareStatement("SELECT COUNT(1) FROM " + table);
			try {
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					total = rs.getLong(1);
				}
				rs.close();
			} finally {
				ps.close();
			}
			if (total == null) {
				return 0;
			} // 写入缓存
			CACHE_TOTAL.put(key, total);
		}
		return total.longValue();
	}

	protected long getTotal(Connection conn, String table, List<Object> fs) throws SQLException {
		int len = fs.size();
		if (len <= 1) {
			return this.getTotal(conn, table);
		}
		Integer key = fs.hashCode();
		Long total = CACHE_TOTAL.get(key);
		if (total == null) {
			PreparedStatement ps = conn.prepareStatement("SELECT COUNT(1) " + fs.get(0).toString());
			try {
				for (int i = 1; i < len; i++) {
					ps.setObject(i, fs.get(i));
				}
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					total = rs.getLong(1);
				}
				rs.close();
			} finally {
				ps.close();
			}
			if (total == null) {
				return 0;
			} // 写入缓存
			CACHE_TOTAL.put(key, total);
		}
		return total.longValue();
	}

	protected Map<Integer, String> getMapInt(String key, String sql) {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Map<Integer, String> map = (Map) this.getS(key);
		if (map == null) {
			map = new LinkedHashMap<Integer, String>();
			Connection conn = null;
			Statement stmt = null;
			try {
				conn = JPrepare.getConnection();
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					map.put(rs.getInt(1), rs.getString(2));
				}
				rs.close();
				this.putS(key, map);
			} catch (Exception e) {
				// Ignored
			} finally {
				JPrepare.close(stmt, conn);
			}
		}
		return map;
	}

	protected Map<String, String> getMapStr(String key, String sql) {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Map<String, String> map = (Map) this.getS(key);
		if (map == null) {
			map = new LinkedHashMap<String, String>();
			Connection conn = null;
			Statement stmt = null;
			try {
				conn = JPrepare.getConnection();
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					map.put(rs.getString(1), rs.getString(2));
				}
				rs.close();
				this.putS(key, map);
			} catch (Exception e) {
				// Ignored
			} finally {
				JPrepare.close(stmt, conn);
			}
		}
		return map;
	}

	protected boolean isNotOK(String text, int len) {
		return (text == null || text.length() < len);
	}

	protected boolean isOK(String text) {
		return isOK(text, 5);
	}

	protected boolean isOK(String text, int len) {
		if (text == null || text.length() < len) {
			return false;
		} else {
			return true;
		}
	}

	/** 获取缓存对象 */
	protected Object getS(String key) {
		return CACHE_INFOS.get(key);
	}

	/** 添加缓存对象 */
	protected Object putS(String key, Object object) {
		return CACHE_INFOS.put(key, object);
	}

	/** 移除缓存对象 */
	protected Object removeS(String key) {
		return CACHE_INFOS.remove(key);
	}

	protected void saveFile(FileInfo f) throws IOException, SQLException {
		if (f.syncs()) {
			if (f.saveFile()) {
				SyncMap.getAll().send(SYS_A996, "save", f, f.doFile());
			} else {
				SyncMap.getAll().send(SYS_A996, "save", f);
			} // 保存数据信息
			Connection conn = JPrepare.getConnection();
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement("UPDATE comm_imgs SET Sortid=?,Source=?,Dist=?,Name=?,Info=?,Pdw=?,Pdh=?,Size=?,State=?,Time=? WHERE Pid=?");
				ps.setInt(1, f.getSortid());
				ps.setString(2, f.getSource());
				ps.setString(3, f.getDist());
				ps.setString(4, f.getName());
				ps.setString(5, f.getInfo());
				ps.setInt(6, f.getPdw());
				ps.setInt(7, f.getPdh());
				ps.setLong(8, f.getSize());
				ps.setInt(9, f.getState());
				ps.setLong(10, f.getTime());
				ps.setString(11, f.getPid());
				if (ps.executeUpdate() <= 0) {
					ps.close();
					ps = conn.prepareStatement("INSERT INTO comm_imgs (Pid,Sid,Tid,Sortid,Source,Dist,Name,Info,Pdw,Pdh,Size,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
					ps.setString(1, f.getPid());
					ps.setString(2, f.getSid());
					ps.setInt(3, f.getTid());
					ps.setInt(4, f.getSortid());
					ps.setString(5, f.getSource());
					ps.setString(6, f.getDist());
					ps.setString(7, f.getName());
					ps.setString(8, f.getInfo());
					ps.setInt(9, f.getPdw());
					ps.setInt(10, f.getPdh());
					ps.setLong(11, f.getSize());
					ps.setInt(12, f.getState());
					ps.setLong(13, f.getTime());
					ps.executeUpdate();
				}
			} finally {
				JPrepare.close(ps, conn);
			}
		}
	}
	
	/**
	 * @author xk
	 * @param f is FileInfo
	 * @throws IOException, SQLException
	 * 
	 * 保存apk文件
	 */
	protected void saveApkFile(FileInfo f) throws IOException, SQLException {
		if (f.syncs()) {
			if (f.saveApkFile()) {
				SyncMap.getAll().send( SYS_A996, "saveApkFile", f, f.doFile() );
			} else {
				SyncMap.getAll().send( SYS_A996, "saveApkFile", f );
			} 
			
			// 保存数据信息
			Connection conn = JPrepare.getConnection();
			PreparedStatement ps = null;
			
			try {
				ps = conn.prepareStatement( "UPDATE comm_imgs SET Sortid = ?,Source = ?,Dist = ?,Name = ?,Info = ?,Pdw = ?,Pdh = ?,Size = ?,State = ?,Time = ? WHERE Pid = ?" );
				ps.setInt( 1, f.getSortid() );
				ps.setString( 2, f.getSource() );
				ps.setString( 3, f.getDist() );
				ps.setString( 4, f.getName() );
				ps.setString( 5, f.getInfo() );
				ps.setInt( 6, f.getPdw() );
				ps.setInt( 7, f.getPdh() );
				ps.setLong( 8, f.getSize() );
				ps.setInt( 9, f.getState() );
				ps.setLong( 10, f.getTime() );
				ps.setString( 11, f.getPid() );
				
				if (ps.executeUpdate() <= 0) {
					ps.close();
					ps = conn.prepareStatement( "INSERT INTO comm_imgs (Pid, Sid, Tid, Sortid, Source, Dist, Name, Info, Pdw, Pdh, Size, State, Time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
					ps.setString( 1, f.getPid() );
					ps.setString( 2, f.getSid() );
					ps.setInt( 3, f.getTid() );
					ps.setInt( 4, f.getSortid() );
					ps.setString( 5, f.getSource() );
					ps.setString( 6, f.getDist() );
					ps.setString( 7, f.getName() );
					ps.setString( 8, f.getInfo() );
					ps.setInt( 9, f.getPdw() );
					ps.setInt( 10, f.getPdh() );
					ps.setLong( 11, f.getSize() );
					ps.setInt( 12, f.getState() );
					ps.setLong( 13, f.getTime() );
					ps.executeUpdate();
				}
			} finally {
				JPrepare.close(ps, conn);
			}
		}
	}

	protected void saveOrder(String[] ts, long time) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE comm_imgs SET Sortid=?,Time=? WHERE Pid=?");
			for (String Pid : ts) {
				ps.setInt(1, index++);
				ps.setLong(2, time);
				ps.setString(3, Pid);
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
	// ==================== 数据类型转换 ====================
	/** 分离数组->Int */
	protected Set<Integer> toInt(String ids) {
		return BUtils.toInt(ids);
	}

	/** 分离数组->Long */
	protected Set<Long> toLong(String ids) {
		return BUtils.toLong(ids);
	}

	/** 分离数组->String */
	protected String[] toSplit(String ids) {
		return BUtils.toSplit(ids);
	}

	protected String[] toString(String info) {
		return BUtils.toString(info);
	}

	protected String toUser(String str) {
		return BUtils.toUser(str);
	}
}
