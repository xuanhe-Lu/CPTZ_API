package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.ypiao.bean.AdsInfo;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.TagIndex;
import com.ypiao.data.JPrepare;
import com.ypiao.service.AderInfoService;
import com.ypiao.service.UserOrderService;

public class AderInfoServiceImp extends AConfig implements AderInfoService {

	private static final String TBL_ADER_INFO = "ader_info";
	
	private TagIndex TAG_INDEX = new TagIndex();
	
	private TagIndex TAG_TYPE = new TagIndex();

	private TagIndex TAG_LOGIN = new TagIndex();

	private UserOrderService userOrderService;

	protected void checkSQL() {
	}

	public UserOrderService getUserOrderService() {
		return userOrderService;
	}

	public void setUserOrderService(UserOrderService userOrderService) {
		this.userOrderService = userOrderService;
	}

	private void save(Connection conn, AdsInfo a) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBL_ADER_INFO + " SET Tid=?,Sortid=?,Name=?,Tags=?,Type=?,Dist=?,URL=?,Ver=?,State=?,Time=? WHERE Sid=?");
		try {
			ps.setInt(1, a.getTid());
			ps.setInt(2, a.getSortid());
			ps.setString(3, a.getName());
			ps.setString(4, a.getTags());
			ps.setInt(5, a.getType());
			ps.setString(6, a.getDist());
			ps.setString(7, a.getUrl());
			ps.setInt(8, a.getVer());
			ps.setInt(9, a.getState());
			ps.setLong(10, a.getTime());
			ps.setString(11, a.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_ADER_INFO + " (Sid,Tid,Sortid,Name,Tags,Type,Dist,URL,Ver,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
				ps.setString(1, a.getSid());
				ps.setInt(2, a.getTid());
				ps.setInt(3, a.getSortid());
				ps.setString(4, a.getName());
				ps.setString(5, a.getTags());
				ps.setInt(6, a.getType());
				ps.setString(7, a.getDist());
				ps.setString(8, a.getUrl());
				ps.setInt(9, a.getVer());
				ps.setInt(10, a.getState());
				ps.setLong(11, a.getTime());
				ps.executeUpdate();
			}
			TAG_INDEX.expired();
			TAG_LOGIN.expired();
		} finally {
			ps.close();
		}
	}

	public void save(AdsInfo ads) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			this.save(conn, ads);
		} finally {
			JPrepare.close(conn);
		}
	}

	public void order(String ids, long time) throws SQLException {
		String[] ts = this.toSplit(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int index = 1;
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_ADER_INFO + " SET Sortid=?,Time=? WHERE Sid=?");
			for (String sid : ts) {
				ps.setInt(1, index++);
				ps.setLong(2, time);
				ps.setString(3, sid);
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			TAG_INDEX.expired();
			TAG_LOGIN.expired();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void state(String ids, int state, long time) throws SQLException {
		String[] ts = this.toSplit(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE " + TBL_ADER_INFO + " SET State=?,Time=? WHERE Sid=?");
			for (String sid : ts) {
				ps.setInt(1, state);
				ps.setLong(2, time);
				ps.setString(3, sid);
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

	public void remove(String ids) throws SQLException {
		String[] ts = this.toSplit(ids);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("DELETE FROM " + TBL_ADER_INFO + " WHERE Sid=?");
			for (String sid : ts) {
				ps.setString(1, sid);
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

	private void addInfo(AjaxInfo json, ResultSet rs, int tid) throws SQLException {
		json.append("sid", rs.getString(1));
		json.append("tid", tid);
		json.append("sortid", rs.getInt(3));
		json.append("name", rs.getString(4));
		json.append("tags", rs.getString(5));
		json.append("type", rs.getInt(6));
		json.append("dist", rs.getString(7));
		json.append("url", rs.getString(8));
		json.append("ver", rs.getInt(9));
		json.append("time", rs.getLong(11));
	}

	/*private void addInfo(AjaxInfo json, ResultSet rs) throws SQLException {
		json.append("sid", rs.getString(1));
		json.append("tid", rs.getInt(2));
		json.append("sortid", rs.getInt(3));
		json.append("name", rs.getString(4));
		json.append("tags", rs.getString(5));
		json.append("type", rs.getInt(6));
		json.append("dist", rs.getString(7));
		json.append("url", rs.getString(8));
		json.append("ver", rs.getInt(9));
		json.append("time", rs.getLong(11));
	}*/

	public void sendByTid(AjaxInfo json, int tid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			json.datas(API_OK);
			ps = conn.prepareStatement("SELECT Sid,Tid,Sortid,Name,Tags,Type,Dist,URL,Ver,State,Time FROM " + TBL_ADER_INFO + " WHERE Tid=? AND State=? ORDER BY Sortid ASC");
			ps.setInt(1, tid);
			ps.setInt(2, STATE_NORMAL);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				this.addInfo(json, rs, tid);
			}
			rs.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	private String findIndex(TagIndex tag) {
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			int dt = 0, tid = 0;
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Sid,Tid,Sortid,Name,Tags,Type,Dist,URL,Ver,State,Time FROM " + TBL_ADER_INFO + " WHERE Tid IN (?,?,?) AND State=? ORDER BY Tid ASC,Sortid ASC");
			if ("LOGIN".equals(tag.getKey())) {
				ps.setInt(1, 5);
				ps.setInt(2, 7);
				ps.setInt(3, 8);
			} else {
				ps.setInt(1, 5);
				ps.setInt(2, 6);
				ps.setInt(3, 7);
			}
			ps.setInt(4, STATE_NORMAL);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				tid = rs.getInt(2);
				if (dt == tid) {
					json.formater();
				} else {
					if (dt > 0) {
						json.close();
					}
					json.adds("ad" + tid);
					dt = tid;
				}
				this.addInfo(json, rs, tid);
			}
			rs.close();
		} catch (SQLException e) {
			json.setBody("{}");
		} finally {
			JPrepare.close(ps, conn);
		}
		tag.setBody(json.getString());
		return tag.getBody();
	}

	public void sendIndex(AjaxInfo json, long uid) throws SQLException {
		TagIndex tag; // 缓存对象
		if (uid >= USER_UID_BEG && this.getUserOrderService().isNewByUid(uid)) {
			tag = TAG_LOGIN;
			tag.setKey("LOGIN");
		} else {
			tag = TAG_INDEX;
			tag.setKey("INDEX");
		} // 加载数据处理
		if (tag.isExpired()) {
			synchronized (tag) {
				if (tag.isRefresh()) {
					this.execute(() -> this.findIndex(tag));
				} else {
					this.findIndex(tag);
				}
			}
		}
		json.addText("ader", tag.getBody());
	}
	
	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @param tid int 
	 * @throws SQLException 
	 */
	private String findByTid(int tid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		AjaxInfo json = AjaxInfo.getArray();
		try {
			ps = conn.prepareStatement( "SELECT Sid, Tid, Sortid, Name, Tags, Type, URL, Ver, State, Time FROM " + TBL_ADER_INFO + " WHERE Tid = ? AND State = ? ORDER BY Tid ASC, Sortid ASC" );
			ps.setInt( 1, tid );
			ps.setInt( 2, STATE_NORMAL );
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				this.addInfo( json, rs, tid );
			}
			rs.close();
		} catch (SQLException e) {
			json.setBody( "[]" );
		} finally {
			JPrepare.close( ps, conn );
		}
		
		return json.toString();
	}
	
	/**
	 * @author xk
	 * @param json is AjaxInfo
	 * @param tid int 
	 * @param num int 节点编号
	 * @throws SQLException 
	 */
	public void sendByTid(AjaxInfo json, int tid, int num) {
		if (TAG_TYPE.isExpired()) {
			synchronized (TAG_TYPE) {
				if (TAG_TYPE.isRefresh()) {
					this.execute(() -> {
						try {
							this.findByTid(tid);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					});
				} else {
					try {
						this.findByTid(tid);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} 
		// 加载数据处理
		json.addText( "data" + num, TAG_TYPE.getBody() );
		TAG_TYPE.expired();
	}
}
