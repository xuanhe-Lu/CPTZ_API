package com.ypiao.service;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Map;
import org.commons.collect.MapMaker;
import org.commons.lang.NumberUtils;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;
import com.ypiao.bean.Config;
import com.ypiao.data.JPrepare;
import com.ypiao.util.ConfigKey;
import com.ypiao.util.Constant;

/**
 * Update by xk on 2018-07-13.
 * 
 * 系统配置参数信息获取工具类.
 */
public class SysConfig implements ConfigKey {

	private static final long serialVersionUID = 8714991916368009582L;

	public static final Logger logger = LoggerFactory.getLogger(SysConfig.class);

	private static final String TBL_COMM_CONFIG = "comm_config";

	private Map<String, Config> cache = new MapMaker().concurrencyLevel(32).expiration(30000).makeMap();

	public SysConfig() {
		this.loadConfig(true);
	}

	/** 加载基础公共配置 */
	public void loadConfig(boolean first) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Id,Losk,Type,Sortid,Sindex,Content,Remark,Timeout,Time FROM " + TBL_COMM_CONFIG + " ORDER BY Id ASC");
			ResultSet rs = ps.executeQuery();
			if (first) {
				logger.info("Load sys config...");
			}
			while (rs.next()) {
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
		} catch (SQLException e) {
			// ignored
		} finally {
			JPrepare.close(ps, conn);
			this.resetSystem();
		}
	}

	public void resetSystem() {
		Constant.SYS_ADMIN_HOST = this.getString("SYS_ADMIN_HOST", Constant.SYS_ADMIN_HOST);
		Constant.SYS_ADMIN_PORT = this.getIntValue("SYS_ADMIN_PORT", Constant.SYS_ADMIN_PORT);
		Constant.SYS_SERVER_PORT = this.getIntValue("SYS_SERVER_PORT", Constant.SYS_SERVER_PORT);
		Constant.USE_DEBUG = isBoolean(USE_DEBUG, false);
		Constant.USE_REWRITE = isBoolean(USE_REWRITE, true);
	}

	// =================== Public System Config ===================
	public Config getConfig(String key) {
		Config c = this.cache.get(key);
		if (c == null) {
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = JPrepare.getConnection();
				ps = conn.prepareStatement("SELECT Id,Losk,Type,Sortid,Sindex,Content,Remark,Timeout,Time FROM " + TBL_COMM_CONFIG + " WHERE Id=?");
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
			} catch (SQLException e) {
				// Ignored
			} finally {
				JPrepare.close(ps, conn);
			}
		}
		return c;
	}

	public void saveConfig(Config c) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = JPrepare.prepareStatement(conn, "SELECT Id,Losk,Type,Sortid,Sindex,Content,Remark,Timeout,Time FROM " + TBL_COMM_CONFIG + " WHERE Id=?");
			ps.setString(1, c.getId());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				logger.info( "根据配置参数ID找到了记录，开始执行更新操作..." );
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
				logger.info( "根据配置参数ID未找到记录，开始执行新增操作..." );
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

	public BigDecimal getBigDecimal(String key, BigDecimal defV) {
		Config c = this.getConfig(key);
		if (c == null || c.getContent() == null) {
			return defV;
		}
		try {
			return new BigDecimal(c.getContent());
		} catch (Throwable t) {
			return defV;
		}
	}

	/** Number */
	public int getIntValue(String key, int defV) {
		Config c = this.getConfig(key);
		if (c == null) {
			return defV;
		} else {
			return NumberUtils.toInt(c.getContent(), defV);
		}
	}

	/** String */
	public String getString(String key) {
		Config c = this.getConfig(key);
		if (c == null) {
			return null;
		} else {
			return c.getContent();
		}
	}

	private String getString(String key, String defV) {
		Config c = this.getConfig(key);
		if (c == null || c.getContent() == null) {
			return defV;
		} else {
			return c.getContent();
		}
	}

	/** Yes or No */
	public boolean isBoolean(String key, boolean defV) {
		Config c = this.getConfig(key);
		if (c == null) {
			return defV;
		} // 检测相关信息
		int num = NumberUtils.toInt(c.getContent(), -1);
		if (num == -1) {
			return defV;
		} else if (num == 1) {
			return true;
		}
		return false;
	}

	/** 是否启用短信发送功能 */
	public boolean sendSMS() {
		return this.isBoolean(USE_SENDSMS, false);
	}

	/** 提现手续（元/次） */
	public BigDecimal getSYSCashByFee() {
		return this.getBigDecimal(SYS_CASH_FEE, BigDecimal.ZERO);
	}

	/** 每月免费提现次数 */
	public int getSYSCashByMonth() {
		return getIntValue(SYS_CASH_MONTH, 3);
	}

	/** 最低提现金额 */
	public BigDecimal getSYSCashByMin() {
		return this.getBigDecimal(SYS_CASH_TOMIN, BigDecimal.ONE);
	}

	public String getShareUrl() {
		return this.getString(SYS_SHARE_URL, "http://www.yingpiaolicai.com/xinrenfuli_activity.html");
	}

	/** 复投活动结束时间 */
	public long getSYSActInfb() {
		Config c = this.getConfig(SYS_ACT_INFB);
		if (c == null) {
			return 0;
		} else {
			return c.getTimeout();
		}
	}
}
