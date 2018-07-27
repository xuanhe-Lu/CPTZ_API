package com.ypm.service.imp;

import java.sql.*;
import java.util.List;
import java.util.Map;
import com.ypm.bean.AjaxInfo;
import com.ypm.data.JPrepare;
import com.ypm.service.*;
import com.ypm.util.GMTime;
import com.ypm.util.VeRule;

public class CfoUserServiceImp extends AConfig implements CfoUserService {

	private static final String TBL_USER_STATUS = "user_status";

	private UserRoleService userRoleService;

	protected void checkSQL() {
	}

	public UserRoleService getUserRoleService() {
		return userRoleService;
	}

	public void setUserRoleService(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	public AjaxInfo findUserByAll(StringBuilder sql, List<Object> fs, String order, int offset, int max) {
		if (fs.size() > 0) {
			sql.replace(1, 4, "WHERE");
		} // get total
		fs.add(0, sql.insert(0, "FROM " + TBL_USER_STATUS).toString());
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			long total = this.getTotal(conn, TBL_USER_STATUS, fs);
			json.setTotal(total);
			if (total <= offset) {
				return json.close();
			}
			Map<String, String> ds = this.getDefault();
			Map<String, String> ms = this.getDictInfoBySSid(USER_INFO_STATE);
			Map<Integer, String> mv = this.getUserRoleService().getVIPByAll();
			sql.insert(0, "SELECT Uid,UPS,VIP,Cid,Name,Mobile,IdCard,BkInfo,BkId,Binds,Reals,Rinfo,Rtels,Rtime,MA,MB,MC,MD,ME,MF,MG,NA,NP,State,Time ").append(" ORDER BY ").append(order);
			ps = conn.prepareStatement(JPrepare.getQuery(sql.toString(), offset, max));
			for (int i = 1, j = fs.size(); i < j; i++) {
				ps.setObject(i, fs.remove(1));
			} // 查询结果
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				json.formater();
				json.append("UID", rs.getLong(1));
				json.append("UPS", rs.getLong(2));
				json.append("VIP", mv.get(rs.getInt(3)));
				json.append("CID", rs.getLong(4));
				json.append("NAME", rs.getString(5));
				json.append("MOBILE", rs.getString(6));
				json.append("IDCARD", VeRule.toStar(rs.getString(7), 6, 3, 3, "-"));
				json.append("BKINFO", VeRule.toStar(rs.getString(8), 4, 4, 3, "-"));
				json.append("BKID", rs.getInt(9));
				json.append("BINDS", ds.get(rs.getString(10)));
				json.append("REALS", ds.get(rs.getString(11)));
				json.append("RINFO", rs.getString(12));
				json.append("RTELS", rs.getString(13));
				json.append("RTIME", GMTime.format(rs.getLong(14), GMTime.CHINA));
				json.append("MA", DF3.format(rs.getDouble(15)));
				json.append("MB", DF3.format(rs.getDouble(16)));
				json.append("MG", DF3.format(rs.getDouble(21)));
				json.append("NA", rs.getInt(22));
				json.append("NP", rs.getInt(23));
				json.append("STATE", ms.get(rs.getString(24)));
				json.append("TIME", GMTime.format(rs.getLong(25), GMTime.CHINA));
			}
			rs.close();
		} catch (SQLException e) {
			// Ignored
		} finally {
			JPrepare.close(ps, conn);
		}
		return json;
	}
}
