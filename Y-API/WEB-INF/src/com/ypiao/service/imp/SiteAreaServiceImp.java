package com.ypiao.service.imp;

import java.sql.*;
import com.ypiao.bean.RegionInfo;
import com.ypiao.data.JPrepare;
import com.ypiao.service.SiteAreaService;

/**
 * 区域信息接口实现类. 
 */
public class SiteAreaServiceImp implements SiteAreaService {

	@Override
	public void saveRegion(RegionInfo info) throws SQLException {
		String code = info.getCode();
		int len = code.length(); // 编码长度
		int end = (len - 2), tj = (len / 2);
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE comm_region SET Code=?,CNA=?,CNB=?,ENA=?,ENB=?,ZipCode=?,TelCode=?,TelNum=?,State=?,Time=? WHERE Code=?");
			ps.setString(1, code);
			ps.setString(2, info.getCna());
			ps.setString(3, info.getCnb());
			ps.setString(4, info.getEna());
			ps.setString(5, info.getEnb());
			ps.setString(6, info.getZipCode());
			ps.setString(7, info.getTelCode());
			ps.setInt(8, info.getTelNum());
			ps.setInt(9, info.getState());
			ps.setLong(10, info.getTime());
			if (info.getSid() == null) {
				ps.setString(11, info.getCode());
				if (ps.executeUpdate() <= 0) {
					ps.close(); // add new
					ps = conn.prepareStatement("INSERT INTO comm_region (Code,CNA,CNB,ENA,ENB,ZipCode,TelCode,TelNum,Tj,Leaf,State,Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
					ps.setString(1, info.getCode());
					ps.setString(2, info.getCna());
					ps.setString(3, info.getCnb());
					ps.setString(4, info.getEna());
					ps.setString(5, info.getEnb());
					ps.setString(6, info.getZipCode());
					ps.setString(7, info.getTelCode());
					ps.setInt(8, info.getTelNum());
					ps.setInt(9, tj); // 层次
					ps.setInt(10, 1); // 页
					ps.setInt(11, info.getState());
					ps.setLong(12, info.getTime());
					ps.executeUpdate();
				}
				if (tj > 1) {
					ps.close(); // 更新节点状态
					ps = conn.prepareStatement("UPDATE comm_region SET Leaf=?,Time=? WHERE Code=?");
					ps.setInt(1, 0);
					ps.setLong(2, info.getTime());
					ps.setString(3, code.substring(0, end));
					ps.executeUpdate();
				}
			} else {
				ps.setString(11, info.getSid());
				if (ps.executeUpdate() <= 0) {
					return;
				} else if (info.getSid().equalsIgnoreCase(code)) {
					// Ignored
				} else {
					ps.close(); // 修改子编号
					StringBuilder sb = new StringBuilder();
					ps = JPrepare.prepareStatement(conn, "SELECT Code,Time FROM comm_region WHERE Code LIKE ? AND Tj>?");
					ps.setString(1, info.getSid() + '%');
					ps.setInt(2, tj);
					ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						sb.setLength(0);
						sb.append(code).append(rs.getString(1).substring(len));
						rs.updateString(1, sb.toString());
						rs.updateLong(2, info.getTime());
						rs.updateRow();
					}
					rs.close();
					ps.close(); // 修改新状态
					ps = conn.prepareStatement("UPDATE comm_region SET Leaf=?,Time=? WHERE Code=?");
					ps.setInt(1, 0);
					ps.setLong(2, info.getTime());
					ps.setString(3, code.substring(0, end));
					ps.executeUpdate();
				}
			} // 提交事务
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
