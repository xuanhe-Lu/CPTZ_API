package com.ypm.service;

import java.sql.*;
import java.util.Map;
import org.commons.collect.MapMaker;
import org.commons.lang.NumberUtils;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;
import com.ypm.bean.Config;
import com.ypm.data.JPrepare;
import com.ypm.util.ConfigKey;
import com.ypm.util.Constant;

public class SysConfig implements ConfigKey {

	private static final long serialVersionUID = 8698492918734760417L;

	public static final Logger logger = LoggerFactory.getLogger(SysConfig.class);

	private Map<String, Config> cache = new MapMaker().concurrencyLevel(32).expiration(15000).makeMap();

	public SysConfig() {
		this.loadConfig(true);	// 系统参数
	}
	/** 系统参数 */
	private void loadConfig(boolean first) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Id,Losk,Type,Sortid,Sindex,Content,Remark,Timeout,Time FROM comm_config ORDER BY Id ASC");
			ResultSet rs = ps.executeQuery();
			if (first) logger.info("Load sys config...");
			while(rs.next()) {
				Config c = new Config();
				c.setId(rs.getString(1));
				c.setLosk(rs.getInt(2));
				c.setType(rs.getInt(3));
				c.setSortid(rs.getInt(4));
				c.setSindex(rs.getString(5));
				c.setContent(rs.getString(6));
				c.setRemark(rs.getString(7));
				c.setTimeout(rs.getLong(8));
				c.setTime(rs.getLong(9));
				this.cache.put(c.getId(), c);
			}
			rs.close();
			if (first) {
				logger.info("Load sys config complete, " + this.cache.size() + " items is loaded.");
			}
		} catch(SQLException e) {
			// ignored
		} finally {
			JPrepare.close(ps, conn);
			this.resetSystem();
		}
	}
	/** 刷新系统信息集 */
	public void resetSystem() {
		Constant.USE_DEBUG = isBoolean(USE_DEBUG, false);
		Constant.USE_REWRITE = isBoolean(USE_REWRITE, true);
	}
	// =================== Public System Configure ===================
	public Config findConfig(String key) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			Config c = null;
			ps = conn.prepareStatement("SELECT Id,Losk,Type,Sortid,Sindex,Content,Remark,Timeout,Time FROM comm_config WHERE Id=?");
			ps.setString(1, key);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				c = new Config();
				c.setId(rs.getString(1));
				c.setLosk(rs.getInt(2));
				c.setType(rs.getInt(3));
				c.setSortid(rs.getInt(4));
				c.setSindex(rs.getString(5));
				c.setContent(rs.getString(6));
				c.setRemark(rs.getString(7));
				c.setTimeout(rs.getLong(8));
				c.setTime(rs.getLong(9));
				this.cache.put(key, c);
			}
			rs.close();
			return c;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public Config getConfig(String key) {
		Config c = this.cache.get(key);
		if (c == null) {
			try {
				return findConfig(key);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return c;
	}

	public void saveConfig(Config c) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = JPrepare.prepareStatement(conn, "SELECT Id,Losk,Type,Sortid,Sindex,Content,Remark,Timeout,Time FROM comm_config WHERE Id=?");
			ps.setString(1, c.getId());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (c.getSid() == null) {
					// 无操作
				} else if (c.getSid().length() > 3) {
					rs.updateString(1, c.getSid());
				}
				rs.updateInt(3, c.getType());
				rs.updateString(5, c.getSindex());
				rs.updateString(6, c.getContent());
				rs.updateString(7, c.getRemark());
				rs.updateLong(8, c.getTimeout());
				rs.updateLong(9, c.getTime());
				rs.updateRow();
			} else {
				rs.moveToInsertRow();
				rs.updateString(1, c.getId());
				rs.updateInt(2, c.getLosk());
				rs.updateInt(3, c.getType());
				rs.updateInt(4, c.getSortid());
				rs.updateString(5, c.getSindex());
				rs.updateString(6, c.getContent());
				rs.updateString(7, c.getRemark());
				rs.updateLong(8, c.getTimeout());
				rs.updateLong(9, c.getTime());
				rs.insertRow();
			}
			rs.close();
			this.setConfig(c);
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	/** 设置键值信息 */
	public void setConfig(Config c) {
		this.cache.put(c.getId(), c);
	}

	public void setConfig(String key, String content) {
		Config c = this.getConfig(key);
		if (c == null) {
			c = new Config();
			c.setId(key);
			c.setContent(content);
		}
		this.setConfig(c);
	}
	/** Number */
	public int getIntValue(String key, int defValue) {
		Config c = this.getConfig(key);
		if (c == null) {
			return defValue;
		} else {
			return NumberUtils.toInt(c.getContent(), defValue);
		}
	}
	/** String */
	public String getString(String key) {
		Config c = this.getConfig(key);
		if (c == null) {
			return null;
		} else {
			return c.getContent().trim();
		}
	}

	private String getString(String key, String defValue) {
		Config c = this.getConfig(key);
		if (c == null || c.getContent() == null) {
			return defValue;
		} else {
			return c.getContent().trim();
		}
	}
	/** Yes or No */
	public boolean isBoolean(String key, boolean defValue) {
		Config c = this.getConfig(key);
		if (c == null) {
			return defValue;
		} // 检测相关信息
		int num = NumberUtils.toInt(c.getContent(), -1);
		if (num == -1) {
			return defValue;
		} else if (num == 1) {
			return true;
		}
		return false;
	}
	/** 网站名称 */
	public String getSiteName() {
		return this.getString(SITE_NAME);
	}
	/** 销售电话 */
	public String getSiteTel() {
		return this.getString(SITE_TEL, "0571-88888888");
	}

	public boolean isUseAuthCode() {
		return this.isBoolean("UseAuthCode", true);
	}

	public boolean isUseCodeLogin() {
		return this.isBoolean("UseCodeLogin", true);
	}

	public boolean isUseOnlyLogin() {
		return this.isBoolean("UseOnlyLogin", true);
	}

	public boolean isUseSafeLogin() {
		return this.isBoolean("UseSafeLogin", true);
	}

	public boolean isUserRegister() {
		return this.isBoolean("UserRegister", true);
	}

}
