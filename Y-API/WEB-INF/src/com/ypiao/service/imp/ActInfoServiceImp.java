package com.ypiao.service.imp;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.commons.lang.TimeUtils;
import com.ypiao.bean.Act180618;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.data.JPrepare;
import com.ypiao.service.ActInfoService;
import com.ypiao.util.GMTime;
import com.ypiao.util.GState;
import com.ypiao.util.Table;

public class ActInfoServiceImp extends AConfig implements ActInfoService {

	private static String SQL_BY_TOP10;

	protected void checkSQL() {
		SQL_BY_TOP10 = JPrepare.getQuery("SELECT Uid,SUM(TMA) AS TA FROM " + Table.TBL_LOG_ORDER + " WHERE Sid<=? AND Sid>=? AND Tid>=2 GROUP BY Uid ORDER BY TA DESC", 5, "inner", "SELECT Uid,Account FROM " + Table.TBL_USER_INFO, "A.Uid=B.Uid ORDER BY A.TA DESC");
	}

	public AjaxInfo findInfoByUid(AjaxInfo json, long uid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int a = (GState.USER_TOADD - 20000000);
			long beg = a * 10000000000L;
			long end = (a + 1) * 10000000000L;
			int index = 1, mc = 0;
			json.datas(API_OK);
			ps = conn.prepareStatement(SQL_BY_TOP10);
			ps.setLong(1, end);
			ps.setLong(2, beg);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (mc >= 1) {
					// Ignored
				} else if (uid == rs.getLong(1)) {
					mc = index;
				} // add 数据
				json.formater();
				json.append("index", index);
				toStar(json.getBuilder("mobile"), rs.getString(4));
				json.append("money", DF2.format(rs.getDouble(2)));
				index++;
			}
			rs.close();
			json.close().append("index", mc);
			return json;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public List<Act180618> findListByUid(long uid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			BigDecimal all = BigDecimal.ZERO;
			SimpleDateFormat sdf = TimeUtils.get("M月d日");
			List<Act180618> ls = new ArrayList<Act180618>();
			ps = conn.prepareStatement("SELECT Sid,TMA,GmtA FROM " + Table.TBL_LOG_ORDER + " WHERE Uid=? AND Tid>=? AND Sid<=? AND Sid>=? ORDER BY Sid DESC");
			ps.setLong(1, uid);
			ps.setInt(2, 2);
			ps.setLong(3, 1806242360000000L);
			ps.setLong(4, 1806150000000000L);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Act180618 a = new Act180618();
				a.setSid(rs.getLong(1));
				a.setTma(rs.getBigDecimal(2));
				a.setTday(GMTime.format(rs.getLong(3), GMTime.CHINA, sdf));
				a.setAll(all.add(a.getTma()));
				all = a.getAll();
				ls.add(a);
			}
			rs.close();
			return ls;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public AjaxInfo findListByUid(AjaxInfo json, long uid) throws SQLException {
		json.datas(API_OK);
		List<Act180618> ls = this.findListByUid(uid);
		int len = (ls == null) ? 0 : ls.size();
		for (int i = (len - 1); i >= 0; i--) {
			Act180618 a = ls.get(i);
			json.formater();
			json.append("tday", a.getTday());
			json.append("money", DF2.format(a.getTma()));
			json.append("all", DF2.format(a.getAll()));
		}
		return json;
	}
}
