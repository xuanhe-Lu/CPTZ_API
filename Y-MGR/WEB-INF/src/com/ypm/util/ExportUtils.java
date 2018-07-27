package com.ypm.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ypm.bean.FieldInfo;
import com.ypm.data.JPrepare;

/**
 * Created by xk on 2018-07-16.
 * 
 * 系统中列表数据导出工具类.
 */
public final class ExportUtils implements Table {
	
	/**
	 * @param sno string 报表字典编号
	 * 
	 * 根据报表字典编号，获取要导出的字段信息集合
	 */
	public static final List<FieldInfo> getExportFields (String sno) throws SQLException {
		List<FieldInfo> list = new ArrayList<>();
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;

		String sql = "SELECT Name, Nice, Width FROM " + TBL_FIELD_INFO + " WHERE Sid = (SELECT Sid FROM " + TBL_FIELD_MENU + " WHERE SNo = '" + sno + "') AND Display = 1 AND Export = 1";
		ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		FieldInfo fieldInfo = null;
		while (rs.next()) {
			fieldInfo = new FieldInfo();
			fieldInfo.setName(rs.getString(1));
			fieldInfo.setNice(rs.getString(2));
			fieldInfo.setWidth(rs.getInt(3));
			list.add(fieldInfo);
			fieldInfo = null;
		}
		rs.close();
		JPrepare.close( ps, conn );

		return list;
	}
}
