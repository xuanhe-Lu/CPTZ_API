package com.ypm.service.imp;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ypm.bean.AjaxInfo;
import com.ypm.data.JPrepare;
import com.ypm.service.ChannelSumService;
import com.ypm.util.GMTime;

/**
 * 渠道统计业务层接口实现类.
 * 
 * Created by xk on 2018-05-18.
 */
public class ChannelSumServiceImp extends AConfig implements ChannelSumService {

	// 表名
	private static final String TBL_CHANNEL_SUM = "channel_sum";

	protected void checkSQL() {
	}
	
	/**
	 * @param sql is StringBuilder
	 * @return AjaxInfo
	 * 
	 * 渠道汇总数据
	 */
	public AjaxInfo list() {
		AjaxInfo json = AjaxInfo.getBean();
		Connection conn = null;
		PreparedStatement ps = null;
		String sql_counts = "SELECT COUNT(DISTINCT us.Uid) as zcrs,\r\n" + //注册人数
				"       (SELECT COUNT(sm.uid) FROM user_status sm WHERE Reals = 1) as smrs,\r\n" + //实名认证人数
				"		(SELECT SUM(tma) FROM log_order WHERE TMA NOT IN (0,5188) AND GmtC >= UNIX_TIMESTAMP(NOW()) * 1000) as tzwdqzje,\r\n" +//投资未到期总金额
				"		(SELECT COUNT(sm.uid) FROM user_status sm WHERE Reals = 0) as wsmrs,\r\n" + //未实名认证
				"		(SELECT COUNT(bk.Uid) FROM user_status bk WHERE Binds = 1) as bkrs,\r\n" + //绑卡人数
				"		(SELECT COUNT(bk.Uid) FROM user_status bk WHERE Binds = 0) as wbkrs,\r\n" + //未绑卡人数
				"		(SELECT COUNT(s.Uid) FROM user_status s,log_order l WHERE s.Uid = l.Uid and l.Tid = 1) as sjtzrs,\r\n" +//实际投资人数 
				"		(SELECT COUNT(s.Uid) FROM user_status s,log_order l WHERE s.Uid = l.Uid and l.Tid = 0) as wsjtzrs,\r\n" + //未实际投资人数
				"		(SELECT SUM(Amount) FROM log_charge) as txcl,\r\n" + //充值总额,基于计算平台总存量
				"		(SELECT COUNT(DISTINCT s.Uid) FROM user_status s, log_order l WHERE s.Uid = l.Uid) as strs,\r\n" + //首投人数
				"		(select count(*) from(SELECT COUNT(*) AS num FROM user_status s ,log_order l WHERE s.Uid = l.Uid GROUP BY l.Uid HAVING num >= 2) a) as ftrs,\r\n" + //复投人数
				"		(SELECT SUM(TMA) FROM log_order a INNER JOIN (SELECT Uid, MIN(GmtA) buyTime FROM log_order GROUP BY Uid) b ON b.buyTime = a.GmtA AND b.Uid = a.Uid) as stje,\r\n" + //首投金额
				"		(SELECT SUM(TMA) FROM log_order a INNER JOIN (SELECT Uid, MIN(GmtA) buyTime FROM log_order GROUP BY Uid) b ON a.GmtA > b.buyTime AND b.Uid = a.Uid) as ftje,\r\n" + //复投金额
				"		(SELECT SUM(TMA) FROM user_cash) as txze,\r\n" + //提现总额
				"		(SELECT SUM(u.MA-l.Amount) FROM user_status u,log_charge l WHERE u.Uid = l.Uid) as czze, \r\n" + //平台存量
				"		(SELECT SUM(YMA) FROM asset_raws) as shzje,\r\n" + //商户总金额
				"		(SELECT SUM(p.MA) FROM prod_info p WHERE p.Ads != 0) as bdtxze,\r\n" + //标的贴息总金额
				"		(SELECT SUM(Amount) FROM log_charge) as czsxfze,\r\n" + //用户充值手续费总额
				"		(SELECT SUM(u.tmb) FROM user_cash u) as txsxfze,\r\n" + //用户提现手续费总额
				"		(SELECT SUM(YMA) FROM prod_info p ,log_order l WHERE p.Pid = l.Pid AND l.Uid not in (101788,101114,100193)) as yhddzje, \r\n" + //用户总金额
				"		(SELECT SUM(TMC) FROM log_order WHERE TMC NOT IN (0,5188)) as dked, \r\n" + //标的贴息总金额  抵扣额度
				"		(SELECT SUM(TMF) FROM log_order) as jxsy \r\n" + //标的贴息总金额  加息收益
				"FROM user_status us LEFT JOIN log_order lo ON us.uid = lo.uid";
		
		
		String s = "SELECT Sum, Stock, Noexpire, Income, Rcount, Acount, Nacount, Bcount, Nbcount, Tzcount, Ntzcount, Txsum, Czsxf, Txsxf, Time FROM " + TBL_CHANNEL_SUM + " order by Time desc limit 1 ";
		ResultSet rs = null;
		try {
			conn = JPrepare.getConnection();
			json.setTotal(1);
			ps = conn.prepareStatement(sql_counts);
			rs = ps.executeQuery();
			if (rs.next()) {
				ps = conn.prepareStatement("INSERT INTO " + TBL_CHANNEL_SUM + " (Sum, Stock, Noexpire, Income, Rcount, Acount, Nacount, Bcount, Nbcount, Tzcount, Ntzcount, Txsum, Czsxf, Txsxf, Time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				BigDecimal st = rs.getBigDecimal(12);
				BigDecimal ft = rs.getBigDecimal(13);
				BigDecimal tx = rs.getBigDecimal(14);
				BigDecimal sh = rs.getBigDecimal(16);
				BigDecimal cz = rs.getBigDecimal(18);//充值手续费1.8%，不足2元按2元收；现在是汇总后的手续费
				BigDecimal yh = rs.getBigDecimal(20);
				BigDecimal jx = rs.getBigDecimal(21);//加息劵   -加息收益
				BigDecimal dk = rs.getBigDecimal(22);//抵扣劵  -抵扣额度
				Long zje = 0L;
				if (st == null) {
					st = BigDecimal.ZERO;
				}
				if (ft == null) {
					ft = BigDecimal.ZERO;
				}
				if (cz == null) {
					cz = BigDecimal.ZERO;
				}
				if (jx == null) {
					jx = BigDecimal.ZERO;
				}
				if (dk == null) {
					dk = BigDecimal.ZERO;
				}
				Double czsxf = ((cz.doubleValue()*1.8)/1000);//充值手续费
				zje = st.longValue()+ft.longValue();//首投+复投
				Long pt = sh.longValue() - yh.longValue();
				ps.setBigDecimal(1, new BigDecimal(zje));
				if (tx == null) {
					tx = BigDecimal.ZERO;
				}
				ps.setBigDecimal(2, new BigDecimal(zje-tx.longValue()));//总金额-提现      -还差一个运营成本
				ps.setBigDecimal(3, rs.getBigDecimal(3));//投资未到期总金额
				ps.setBigDecimal(4, new BigDecimal(pt));//平台收益总金额，未统计
				ps.setLong(5, rs.getLong(1));
				ps.setLong(6, rs.getLong(2));
				ps.setLong(7, rs.getLong(4));
				ps.setLong(8, rs.getLong(5));
				ps.setLong(9, rs.getLong(6));
				ps.setLong(10, rs.getLong(7));
				ps.setLong(11, rs.getLong(8));
				ps.setBigDecimal(12, new BigDecimal(jx.doubleValue()+dk.doubleValue()));
//				ps.setBigDecimal(13, rs.getBigDecimal(13));//新手投资赠送...已去掉
				ps.setBigDecimal(13, new BigDecimal(czsxf));//用户充值手续费...
				ps.setBigDecimal(14, rs.getBigDecimal(19));//用户提现手续费
				ps.setLong(15,GMTime.currentTimeMillis());
				ps.execute();
				ps.close();
			}
			
			ps = conn.prepareStatement(s);
			
			// 查询结果
			rs = ps.executeQuery();
			if (rs.next()) {
				json.formater();
				json.append( "SUM", rs.getBigDecimal(1) == null ? BigDecimal.ZERO : rs.getBigDecimal(1) );
				json.append( "STOCK", rs.getBigDecimal(2) == null ? BigDecimal.ZERO : rs.getBigDecimal(2) );
				json.append( "NOEXPIRE", rs.getBigDecimal(3) == null ? BigDecimal.ZERO : rs.getBigDecimal(3) );
				json.append( "INCOME", rs.getBigDecimal(4) == null ? BigDecimal.ZERO : rs.getBigDecimal(4) );
				json.append( "RCOUNT", rs.getLong(5) );
				json.append( "ACOUNT", rs.getLong(6) );
				json.append( "NACOUNT", rs.getLong(7) );
				json.append( "BCOUNT", rs.getLong(8) );
				json.append( "NBCOUNT", rs.getLong(9) );
				json.append( "TZCOUNT", rs.getLong(10) );
				json.append( "NTZCOUNT", rs.getLong(11) );
				json.append( "TXSUM", rs.getBigDecimal(12) == null ? BigDecimal.ZERO : rs.getBigDecimal(12) );
//				json.append( "TZZS", rs.getBigDecimal(13) );
				json.append( "CZSXF", rs.getBigDecimal(13) == null ? BigDecimal.ZERO : rs.getBigDecimal(13) );
				json.append( "TXSXF", rs.getBigDecimal(14) == null ? BigDecimal.ZERO : rs.getBigDecimal(14) );
				
				// 获取渠道的动态数据字段
				//Map<String, Object> dynamicCol = this.getDynamicField( resultSet.getInt(1), preparedStatement, conn );
				/*if (!dynamicCol.isEmpty()) {
					json.append( "RCOUNT", (Long) dynamicCol.get("key_1") );
					json.append( "RNAME", (Long) dynamicCol.get("key_2") );
					json.append( "BCOUNT", (Long) dynamicCol.get("key_3") );
					json.append( "NCOUNT", (Long) dynamicCol.get("key_4") );
					json.append( "NMONEY", (BigDecimal) dynamicCol.get("key_5") );
					json.append( "FCOUNT", (Long) dynamicCol.get("key_6") );
					json.append( "SCOUNT", (Long) dynamicCol.get("key_7") );
					json.append( "FMONEY", (BigDecimal) dynamicCol.get("key_8") );
					json.append( "SMONEY", (BigDecimal) dynamicCol.get("key_9") );
					json.append( "TZSUM", (BigDecimal) dynamicCol.get("key_10") );
					json.append( "TXSUM", (BigDecimal) dynamicCol.get("key_11") );
					json.append( "NASTOCK", (BigDecimal) dynamicCol.get("key_12") );
					json.append( "SSTOCK", (BigDecimal) dynamicCol.get("key_13") );
				}*/
				json.append( "TIME", GMTime.format( rs.getLong(15), GMTime.CHINA ) );
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JPrepare.close( ps, conn );
		}
		
		return json;
	}
	public static void main(String[] args) {
		long timeStamp = 1530656610984L;
		long cs = 1530656610984L * 1000;
		System.out.println(cs);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sd = sdf.format(new Date(timeStamp));
		System.out.println(sd);
	}
}
