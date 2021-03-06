package com.ypiao.test;

import java.sql.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import com.ypiao.bean.AjaxInfo;
import com.ypiao.data.JPrepare;
import com.ypiao.sign.JSON;
import com.ypiao.util.GMTime;
import com.ypiao.util.MonthFound;
import com.ypiao.util.VeStr;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TestMain {
private static Logger logger = Logger.getLogger(TestMain.class);

	public static void main (String args[]) throws Exception {
//		dateFormat();
//		Connection conn = JPrepare.getConnection();
//		testSql();

//		String data2 = "2018-07-12";
//		long ts = 1531324800002l;
//		dateFormat(data2,ts);
//		data2 = "2018-07-13";
//		dateFormat(data2,ts);
//		long i = 1531324800000l - 1531238400000l;
//		Logger.info(i/3600);
//		Logger.info(i);
//		String driverName = "com.mysql.jdbc.Driver";
//		String url = "jdbc:mysql://localhost:3306/ypiao?useUnicode=true&allowMultiQueries=true";
//		String user = "root";
//		String password = "123456";
//		Class.forName("com.mysql.jdbc.Driver");
//
//		Connection conn = DriverManager.getConnection(url, user, password);
//		PreparedStatement ps = conn.prepareStatement("SELECT time ,rate FROM act_info  order by time ASC ");
//		ResultSet rs = ps.executeQuery();
//		AjaxInfo ajaxInfo = new AjaxInfo();
//		ajaxInfo.formater();//map
//		ajaxInfo.add("time");
//		ajaxInfo.formates();//list
//		while (rs.next()) {
////			ajaxInfo.formater();
////			ajaxInfo.append("time",rs.getLong(1));
////			ajaxInfo.append("rate",String.valueOf(rs.getDouble(2)));
//			ajaxInfo.append(rs.getLong(1));
//
//		}
//		Logger.info(ajaxInfo);

//		dateFormat("2018-07-19",1531641600000l);

//		AjaxInfo ajaxInfo = new AjaxInfo() ;
//		ajaxInfo.append("tets",1);
//		ajaxInfo.formates();
//
//		for (int i1 = 0; i1 < 10; i1++) {
//			ajaxInfo.append(i1);
//		}
//		ajaxInfo.append("test1",2);
//		ajaxInfo.append("test2",3);

//		return ajaxInfo.toString();
//		int i = 100;
//		for (int i1 = 0; i1 < i; i1++) {
//			Logger.info((int)(Math.random()*1000+1));
//
//		}
//		"SELECT time,Monthcount  FROM USER_ATTEN  WHERE UID = ? and time > = ?

//		Logger.info(MonthFound.getDataFormat(1533668948385L,"yyyy-MM-dd HH:mm:ss"));
//		Document res = Jsoup.connect("https://way.jd.com/idcard/idcard").data("name", "池慧滢".replace("·", "")).data("cardno", "350426199605293528").data("appkey", "65d4b4096b618e60ed3466030a88fc32").timeout(10000).ignoreContentType(true).post();
//		String body = res.body().text();
//		Logger.info("AUTH:\t" + body);
	}

	private static void dateFormat(String data2,long ts1) throws ParseException {
		//时间转换为时间戳
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date date = simpleDateFormat.parse(data2);
		long ts = date.getTime();
		logger.info(ts);
		//时间戳转换为时间
		java.util.Date date1 = new java.util.Date(ts1);
		SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String da = simpleDateFormat1.format(date1);
		logger.info(da);
	}

	private static void testSql() throws ClassNotFoundException, SQLException {
		String driverName = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/ypiao?useUnicode=true&allowMultiQueries=true";
		String user = "root";
		String password = "123456";
		Class.forName("com.mysql.jdbc.Driver");

		Connection conn = DriverManager.getConnection(url, user, password);

		PreparedStatement ps = null;
		ps = conn.prepareStatement("INSERT INTO user_atten (time,remark )"+"values (?,?)");
//		ps.setLong(1,12l);
		// yyyy-mm-dd hh:mm:ss

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = df.format(Date.valueOf("2018-07-16"));
		logger.info(java.sql.Timestamp.valueOf(str));
		ps.setTimestamp(1, java.sql.Timestamp.valueOf(str));
		ps.setString(2,"test2");
		int b = ps.executeUpdate();
		logger.info(b);


		ps =conn.prepareStatement("select time  from user_atten where id =1");
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			Timestamp time = rs.getTimestamp(1);
			String str1 = time.toString();
			logger.info("!!!!!"+str1			);

		}
	}

	private static void dateFormat() {
		try {
			Date regDate = (Date) new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).parse(GMTime.format(8797468456456L, GMTime.CHINA));
			logger.info(regDate.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
