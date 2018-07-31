package com.ypiao.json;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.data.JPrepare;

public class AtIOS extends Action {

	private static final long serialVersionUID = 6915233726669317724L;

	private int sid;

	public AtIOS() {
		super(false);
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			int sid = this.getInt("sid");
			if (sid <= 0) {
				sid = this.getSid();
				if (sid <= 0) {
					sid = 1;
				}
			} // 加载数据信息
			json.addObject();
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("SELECT Sid,Ver,OKS,Com,Ext,Time FROM comm_toios WHERE Sid=?");
			ps.setInt(1, sid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				json.append("sid", rs.getInt(1));
				json.append("ver", rs.getString(2));
				json.append("oks", rs.getInt(3));
				json.append("com", rs.getString(4));
				json.append("ext", rs.getString(5));
			}
			rs.close();
		} catch (SQLException e) {
			json.addError(e.getMessage());
		} finally {
			JPrepare.close(ps, conn);
		}
		System.out.println("json:"+json.toString());
 return JSON;
	}
}
