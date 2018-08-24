package com.ypiao.service.imp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.ypiao.bean.ComRaws;
import com.ypiao.data.JPrepare;
import com.ypiao.service.CfoCompanyService;
import com.ypiao.util.Table;

public class CfoCompanyServiceImp extends AConfig implements CfoCompanyService {

	protected void checkSQL() {
	}

	public int update(ComRaws r) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			Logger.info(r.getRid() + "==" + r.getState());
			ps = conn.prepareStatement("UPDATE " + Table.TBL_ASSET_RAWS + " SET State=?,Time=?,adM=?,adN=? WHERE Rid=?");
			ps.setInt(1, r.getState());
			ps.setLong(2, r.getTime());
			ps.setLong(3, r.getAdm());
			ps.setString(4, r.getAdn());
			ps.setLong(5, r.getRid());
			int num = ps.executeUpdate();
			if (num >= 1) {
				if (SALE_A5 == r.getState()) {
					ps.close(); // 下级更新
					ps = conn.prepareStatement("UPDATE " + Table.TBL_PROD_INFO + " SET AU=?,Time=? WHERE Rid=?");
					ps.setInt(1, r.getState());
					ps.setLong(2, r.getTime());
					ps.setLong(3, r.getRid());
					ps.executeUpdate();
				}
			}
			return num;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
