package com.ypiao.test;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.sunsw.http.HttpEntity;
import com.sunsw.http.HttpResponse;
import com.sunsw.http.HttpStatus;
import com.sunsw.http.NameValuePair;
import com.sunsw.http.client.HttpClient;
import com.sunsw.http.client.UrlEncodedFormEntity;
import com.sunsw.http.message.BasicNameValuePair;
import com.sunsw.http.methods.HttpPost;
import com.sunsw.http.protocol.HTTP;
import com.sunsw.http.util.EntityUtils;
import com.ypiao.service.PoolService;
import com.ypiao.sign.JSON;
import com.ypiao.util.Constant;

/**
 * 示远验证码发送测试类. 
 */
public class SendSms {
	
	private static final Logger LOGGER = Logger.getLogger(SendSms.class);

	public static void main (String args[]) {
		HttpClient hc = PoolService.getHttpClient();
		HttpPost post = new HttpPost( "http://send.18sms.com/msg/HttpBatchSendSM" );
		
		try {
			List<NameValuePair> ns = new ArrayList<NameValuePair>(5);

			ns.add(new BasicNameValuePair( "account", "账户" ));
			ns.add(new BasicNameValuePair( "pswd", "密码" ));
			ns.add(new BasicNameValuePair( "mobile", "接收验证码的手机号码" ));
			ns.add(new BasicNameValuePair( "msg", "您的验证码为XXXX，感谢您使用XXXX。"));
			ns.add(new BasicNameValuePair( "needstatus", "true" ));
			post.setEntity(new UrlEncodedFormEntity(ns, Constant.SYS_UTF8));
			post.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
			post.setHeader(HTTP.USER_AGENT, "sunsw-HttpClient");
			HttpResponse res = hc.execute(post);
			LOGGER.info( "发送短信验证码【res】："  + res);
			HttpEntity entity = res.getEntity();
			LOGGER.info( "发送短信验证码【res转entity】："  + entity);
			if (entity == null) {
				// Ignored
			} else if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String body = EntityUtils.toString(entity, "UTF-8");
				Logger.info(body);
				LOGGER.info( "短信验证码发送成功，接口返回数据：" + body );
				JSON j = new JSON(body);
				Logger.info((j.getInt("status") == 0));
			} else {
				EntityUtils.consume(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.info("false");
		} finally {
			post.abort();
		}
	}
}
