package com.ypiao.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.commons.lang.StringUtils;
import com.ypiao.data.JPrepare;

/**
 * Created by xk on 2018-07-04.
 * 
 * 动态加载数据库中短信发送相关配置工具类.
 */
public final class SmsUtils {
	
	private static final Logger LOGGER = Logger.getLogger(SmsUtils.class);
	
	// 系统配置表名
	private static final String TBL_COMM_CONFIG = "comm_config";
	
	/**
	 * 获取系统配置的短信发送签名.
	 * 
	 * @return string
	 * @throws SQLException 
	 */
	public static final String getSmsSignName () {
		Connection conn = null;
		PreparedStatement ps = null;
		String signName = "";

		try {
			conn = JPrepare.getConnection();
			String sql = "SELECT Content FROM " + TBL_COMM_CONFIG + " WHERE Id = 'SMS_SIGN_NAME'";
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				signName = rs.getString(1);
				LOGGER.info( "动态加载数据库中配置的短信发送签名成功，签名为：" + signName );
			}
			rs.close();
			
			if (StringUtils.isEmpty(signName)) {
				LOGGER.info( "动态加载数据库中配置的短信发送签名失败，请登录后台系统进行正确配置。" );
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JPrepare.close( ps, conn );
		}
		
		return signName;
	}

	/**
	 * 获取系统配置的短信发送签名、短信账户、短信密码.
	 * 
	 * @return string
	 * @throws SQLException 
	 */
	public static final Map<String, String> getSmsConfig () {
		Map<String, String> res = new HashMap<>();

		Connection conn = null;
		PreparedStatement ps = null;
		String signName = null, account = null, pswd = null;

		try {
			conn = JPrepare.getConnection();
			String sql = "SELECT (SELECT Content FROM " + TBL_COMM_CONFIG + " WHERE Id = 'SMS_SIGN_NAME') AS a, (SELECT Content FROM " + TBL_COMM_CONFIG + " WHERE Id = 'IDENTIFY_CODE_ACCOUNT') AS b, (SELECT Content FROM " + TBL_COMM_CONFIG + " WHERE Id = 'IDENTIFY_CODE_PSWD') AS c";
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				signName = rs.getString(1);
				account = rs.getString(2);
				pswd = rs.getString(3);
				LOGGER.info( "动态加载数据库中配置的短信发送签名成功，签名为：" + signName );
				LOGGER.info( "动态加载数据库中配置的短信验证码账户及密码成功，账户为：" + account + ", 密码为: " + pswd );
			} else {
				LOGGER.info( "动态加载数据库中配置的短信配置信息失败，请登录后台系统进行正确配置。" );
				return null;
			}
			rs.close();
			if (!StringUtils.isEmpty(signName) && !StringUtils.isEmpty(account) && !StringUtils.isEmpty(pswd)) {
				res.put( "signName", signName );
				res.put( "account", account );
				res.put( "pswd", pswd );
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JPrepare.close( ps, conn );
		}
		
		return res;
	}
}
