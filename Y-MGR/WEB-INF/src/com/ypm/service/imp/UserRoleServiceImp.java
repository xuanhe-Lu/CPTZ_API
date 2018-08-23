package com.ypm.service.imp;

import java.sql.*;
import java.util.*;
import org.commons.lang.LRUMap;
import com.ypm.bean.*;
import com.ypm.data.JPrepare;
import com.ypm.service.UserRoleService;
import com.ypm.util.GMTime;

public class UserRoleServiceImp extends AConfig implements UserRoleService {

	private static final String KEY_VIPS = "user_vips";

	private static final String TBl_USER_VIPS = "user_vips";

	private Map<Integer, String> vips = new HashMap<Integer, String>();

	private Map<Integer, InfoVIPS> cache = new LRUMap<Integer, InfoVIPS>(12);

	protected void checkSQL() {
	}

	private void save(Connection conn, InfoVIPS v) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("UPDATE " + TBl_USER_VIPS + " SET Name=?,Rate=?,Rats=?,Sale=?,SNum=?,SRmb=?,STxt=?,Sok=?,GMa=?,GMb=?,GNa=?,GNb=?,Rday=?,Remark=?,State=?,Time=? WHERE VIP=?");
		try {
			ps.setString(1, v.getName());
			ps.setBigDecimal(2, v.getRate());
			ps.setBigDecimal(3, v.getRats());
			ps.setBigDecimal(4, v.getSale());
			ps.setInt(5, v.getSNum());
			ps.setBigDecimal(6, v.getSRmb());
			ps.setString(7, v.getSTxt());
			ps.setInt(8, v.getSok());
			ps.setBigDecimal(9, v.getGma());
			ps.setBigDecimal(10, v.getGmb());
			ps.setBigDecimal(11, v.getGna());
			ps.setBigDecimal(12, v.getGnb());
			ps.setInt(13, v.getRday());
			ps.setString(14, v.getRemark());
			ps.setInt(15, v.getState());
			ps.setLong(16, v.getTime());
			ps.setInt(17, v.getVip());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBl_USER_VIPS + " (VIP,Name,Rate,Rats,Sale,SNum,SRmb,STxt,Sok,GMa,GMb,GNa,GNb,Rday,Remark,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps.setInt(1, v.getVip());
				ps.setString(2, v.getName());
				ps.setBigDecimal(3, v.getRate());
				ps.setBigDecimal(4, v.getRats());
				ps.setBigDecimal(5, v.getSale());
				ps.setInt(6, v.getSNum());
				ps.setBigDecimal(7, v.getSRmb());
				ps.setString(8, v.getSTxt());
				ps.setInt(9, v.getSok());
				ps.setBigDecimal(10, v.getGma());
				ps.setBigDecimal(11, v.getGmb());
				ps.setBigDecimal(12, v.getGna());
				ps.setBigDecimal(13, v.getGnb());
				ps.setInt(14, v.getRday());
				ps.setString(15, v.getRemark());
				ps.setInt(16, v.getState());
				ps.setLong(17, v.getTime());
				ps.executeUpdate();
			}
			cache.put(v.getVip(), v);
			vips.put(v.getVip(), v.getName());
		} finally {
			ps.close();
		}
	}

	public void save(InfoVIPS v) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			if (v.getVip() >= 0) {
				this.save(conn, v);
			}
		} finally {
			JPrepare.close(conn);
		}
	}

	public AjaxInfo findVIPByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (sql.length() > 4) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + TBl_USER_VIPS).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			Map<String, String> sok = this.getInfoState();
			Map<String, String> ms = this.getInfoStates();
			Map<String, String> md = this.getDictInfoBySSid(USER_VIP_RDAY);
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, TBl_USER_VIPS, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			} // 加载后续信息
			sql.insert(0, "SELECT VIP,Name,Rate,Rats,Sale,SNum,SRmb,STxt,Sok,GMa,GMb,GNa,GNb,Rday,Remark,State,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql, offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.remove(1));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("VIP", rs.getInt(1));
				json.append("NAME", rs.getString(2));
				float n = rs.getFloat(3);
				if (n <= 0) {
					json.append("RATE", "-");
				} else {
					json.append("RATE", DF2.format(n), "%");
				} // 上线值
				if ((n = rs.getFloat(4)) <= 0) {
					json.append("RATS", "-");
				} else {
					json.append("RATS", DF2.format(n), "%");
				} // 销售价
				json.append("SALE", DF2.format(rs.getFloat(5)));
				json.append("SNUM", rs.getInt(6));
				if ((n = rs.getFloat(7)) <= 0) {
					json.append("SRMB", "0");
				} else {
					json.append("SRMB", DF2.format(n));
				} // 奖励文本
				json.append("STXT", rs.getString(8));
				json.append("SOK", sok.get(rs.getString(9)));
				if ((n = rs.getFloat(10)) <= 0) {
					json.append("GMA", "0");
				} else {
					json.append("GMA", DF2.format(n));
				} // 分享(2)
				if ((n = rs.getFloat(11)) <= 0) {
					json.append("GMB", "0");
				} else {
					json.append("GMB", DF2.format(n));
				} // 订单(1)
				if ((n = rs.getFloat(12)) <= 0) {
					json.append("GNA", "0");
				} else {
					json.append("GNA", DF2.format(n), "%");
				} // 订单(2)
				if ((n = rs.getFloat(13)) <= 0) {
					json.append("GNB", "0");
				} else {
					json.append("GNB", DF2.format(n), "%");
				}
				json.append("RDAY", md.get(rs.getString(14)));
				json.append("REMARK", rs.getString(15));
				json.append("STATE", ms.get(rs.getString(16)));
				json.append("TIME", GMTime.format(rs.getLong(17), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}

	public AjaxInfo getVIPChildrens() {
		AjaxInfo json = AjaxInfo.getArray();
		AjaxFile file = AjaxFile.get(KEY_VIPS);
		try {
			if (file.isFailed()) {
				this.setUserVIP(file, json);
			} else {
				json.setBody(file.getBody());
				if (file.isExpired()) {
					this.execute(() -> setUserVIP(file));
				}
			}
		} catch (SQLException e) {
			// Ignored
		} finally {
			file.close();
		}
		return json;
	}

	private void setUserVIP(AjaxFile file) {
		try {
			this.setUserVIP(file, null);
		} catch (Exception e) {
			// Ignored
		}
	}

	private void setUserVIP(AjaxFile file, AjaxInfo json) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			if (json == null) {
				json = AjaxInfo.getArray();
			}
			ps = conn.prepareStatement("SELECT VIP,Name FROM " + TBl_USER_VIPS + " WHERE State=? ORDER BY VIP ASC");
			ps.setInt(1, STATE_NORMAL);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("id", rs.getString(1));
				json.append("text", rs.getString(2));
			}
			rs.close();
			file.write(json);
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public Map<Integer, String> getVIPByAll() {
		if (vips.size() <= 0) {
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = JPrepare.getConnection();
				ps = conn.prepareStatement("SELECT VIP,Name FROM " + TBl_USER_VIPS + " ORDER BY VIP ASC");
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					vips.put(rs.getInt(1), rs.getString(2));
				}
			} catch (SQLException e) {
				// Ignored
			} finally {
				JPrepare.close(ps, conn);
			}
		}
		return vips;
	}

	public InfoVIPS findInfoByVIP(int vip) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			InfoVIPS v = null;
			ps = conn.prepareStatement("SELECT VIP,Name,Rate,Rats,Sale,SNum,SRmb,STxt,Sok,GMa,GMb,GNa,GNb,Rday,Remark,State,Time FROM " + TBl_USER_VIPS + " WHERE VIP=?");
			ps.setInt(1, vip);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				v = new InfoVIPS();
				v.setVip(rs.getInt(1));
				v.setName(rs.getString(2));
				v.setRate(rs.getBigDecimal(3));
				v.setRats(rs.getBigDecimal(4));
				v.setSale(rs.getBigDecimal(5));
				v.setSNum(rs.getInt(6));
				v.setSRmb(rs.getBigDecimal(7));
				v.setSTxt(rs.getString(8));
				v.setSok(rs.getInt(9));
				v.setGma(rs.getBigDecimal(10));
				v.setGmb(rs.getBigDecimal(11));
				v.setGna(rs.getBigDecimal(12));
				v.setGnb(rs.getBigDecimal(13));
				v.setRday(rs.getInt(14));
				v.setRemark(rs.getString(15));
				v.setState(rs.getInt(16));
				v.setTime(rs.getLong(17));
			}
			rs.close();
			return v;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public InfoVIPS getInfoByVIP(int vip) {
		InfoVIPS v = cache.get(vip);
		if (v == null) {
			try {
				v = this.findInfoByVIP(vip);
				if (v == null) {
					v = new InfoVIPS();
				}
			} catch (SQLException e) {
				v = new InfoVIPS();
			} finally {
				v.setVip(vip);
				this.cache.put(vip, v);
			}
		}
		return v;
	}

	public void saveVIP(InfoVIPS v) throws SQLException {
		Connection conn = JPrepare.getConnection();
		try {
			if (v.getVip() <= -1) {
				v.setVip(this.getId(conn, TBl_USER_VIPS, "VIP"));
			} // save vips
			if (STATE_ENABLE == v.getState()) {
				v.setState(STATE_ENABLE);
			} else {
				v.setState(STATE_DISABLE);
			} // time
			v.setTime(System.currentTimeMillis());
			this.save(conn, v);
		} finally {
			JPrepare.close(conn);
		} // 更新信息
		SyncMap.getAll().sender(SYS_A901, "saveVIP", v);
	}
}
