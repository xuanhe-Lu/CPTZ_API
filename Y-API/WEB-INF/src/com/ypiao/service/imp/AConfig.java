package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import org.commons.collect.MapMaker;
import com.ypiao.bean.Messager;
import com.ypiao.data.JPrepare;
import com.ypiao.util.*;

public abstract class AConfig implements APSKey, AState {

	protected static final String TBL_USER_STATUS = "user_status";

	protected static final DecimalFormat DF2 = new DecimalFormat("0.00");

	protected static final DecimalFormat DFY = new DecimalFormat("￥0.00");

	protected static final int MAX_100 = 100;

	protected static final int MAX_300 = 300;

	protected static final int MAX_500 = 500;

	protected static final int MAX_ALL = 1000;
	/** 缓冲区大小 */
	protected static final int SIZE_MAX = 4 * 1024;
	/** 统一超时时间5分钟 */
	protected static final int TIME_OUT = 5 * 60 * 1000;

	private static Map<String, Object> CACHE_INFOS;

	public AConfig() {
		if (CACHE_INFOS == null) {
			CACHE_INFOS = new MapMaker().concurrencyLevel(32).expiration(15000).makeMap(); // 15 秒缓存时长
		}
		this.checkSQL();
	}

	/** 检测执行SQL */
	protected abstract void checkSQL();

	protected void close(Messager mgr) {
		if (mgr == null) {
			// Ignored
		} else {
			mgr.destroy();
		}
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

	protected void execute(Runnable run) {
		com.ypiao.service.PoolService.getService().execute(run);
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

	// ==================== 可处理信息 ====================
	protected Object getObject(String key) {
		return CACHE_INFOS.get(key);
	}

	protected void setObject(String key, Object obj) {
		CACHE_INFOS.put(key, obj);
	}

	protected boolean isNot(String sql, long obj) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			boolean result = true;
			ps = conn.prepareStatement(sql);
			ps.setLong(1, obj);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = false;
			}
			rs.close();
			return result;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	protected boolean isOK(String text) {
		return isOK(text, 10);
	}

	protected boolean isOK(String text, int len) {
		if (text == null || text.length() < len) {
			return false;
		} else {
			return true;
		}
	}

	/** 手机星号 */
	protected static void toStar(StringBuilder sb, String sm) {
		sb.append('"').append(sm.substring(4, 7)).append("***").append(sm.substring(11)).append('"');
	}
	/** 分离数组 */
	protected String[] toSplit(String ids) {
		return BUtils.toSplit(ids);
	}

	protected Set<Integer> toInt(String ids) {
		return BUtils.toInt(ids);
	}

	protected Set<Long> toLong(String ids) {
		return BUtils.toLong(ids);
	}

}
