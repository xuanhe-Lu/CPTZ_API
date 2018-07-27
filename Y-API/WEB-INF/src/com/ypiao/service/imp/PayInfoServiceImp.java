package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.commons.code.Suncoder;
import com.ypiao.bean.PayInfo;
import com.ypiao.data.JPrepare;
import com.ypiao.service.PayInfoService;

public class PayInfoServiceImp implements PayInfoService {

	private final Map<String, PayInfo> cache;

	public PayInfoServiceImp() {
		cache = new ConcurrentHashMap<String, PayInfo>(10);
		try {
			this.loadAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public PayInfo getInfoByFuiou() {
		return getPayInfo("FUIOU");
	}

	public PayInfo getPayInfo(String key) {
		PayInfo pay = cache.get(key);
		if (pay == null && cache.isEmpty()) {
			synchronized (this) {
				try {
					this.loadAll();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pay = cache.get(key);
			}
		}
		return pay;
	}

	private void loadAll() throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SELECT Sid,Tid,Pid,Mark,Name,APPId,Secret,Sellid,Seller,NotifyUrl FROM comm_topay");
			ps.executeQuery();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				PayInfo pay = new PayInfo();
				pay.setSid(rs.getInt(1));
				pay.setTid(rs.getInt(2));
				pay.setPid(rs.getString(3));
				pay.setMark(rs.getString(4));
				pay.setName(rs.getString(5));
				pay.setApp_id(rs.getString(6));
				String str = rs.getString(7);
				if (str == null) {
					// Ignored
				} else {
					pay.setSecret(Suncoder.decode(str));
				}
				pay.setSellid(rs.getString(8));
				pay.setSeller(rs.getString(9));
				pay.setNotifyUrl(rs.getString(10));
				pay.execute(); // 重构数据信息
				cache.put(pay.getMark(), pay);
				cache.put(pay.getSellid(), pay);
			}
			rs.close();
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
