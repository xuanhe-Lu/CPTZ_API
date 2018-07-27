package com.ypiao.service.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.ypiao.bean.SMSLogs;
import com.ypiao.service.PoolService;
import com.ypiao.service.SenderService;
import com.ypiao.sign.JSON;
import com.ypiao.util.Constant;
import com.ypiao.util.SmsUtils;

/**
 * 短信发送接口实现类. 
 */
public class SenderServiceImp extends AConfig implements SenderService {
	
	private static final Logger LOGGER = Logger.getLogger(SenderServiceImp.class);

	protected void checkSQL() {
	}

	/**
	 * 调用示远短信验证码接口. 
	 */
	@Override
	public boolean sendByCode(SMSLogs log) {
		/*HttpClient hc = PoolService.getHttpClient();
		HttpPost post = new HttpPost( "http://send.18sms.com/msg/HttpBatchSendSM" );
		
		String account = null, pswd = null, signName = null;
		try {
			List<NameValuePair> ns = new ArrayList<NameValuePair>(5);
			
			Map<String, String> map = SmsUtils.getSmsConfig();
			if (map != null) {
				signName = map.get( "signName" );
				account = map.get( "account" );
				pswd = map.get( "pswd" );
			} else {
				LOGGER.info( "获取短信配置信息失败。" );
				return false;
			}
			
			ns.add(new BasicNameValuePair( "account", account ));
			ns.add(new BasicNameValuePair( "pswd", pswd ));
			ns.add(new BasicNameValuePair( "mobile", log.getMobile().replace("+86-", "")) );
			ns.add(new BasicNameValuePair( "msg", signName + log.getContent()) );
			ns.add(new BasicNameValuePair( "needstatus", "true" ));
			post.setEntity(new UrlEncodedFormEntity( ns, Constant.SYS_UTF8 ));
			post.setHeader( HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded" );
			post.setHeader( HTTP.USER_AGENT, "sunsw-HttpClient" );
			HttpResponse res = hc.execute(post);
			LOGGER.info( "发送短信验证码【res】："  + res);
			HttpEntity entity = res.getEntity();
			LOGGER.info( "发送短信验证码【res转entity】："  + entity);
			if (entity == null) {
				// Ignored
			} else if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String body = EntityUtils.toString( entity, "UTF-8" );
				System.out.println(body);
				LOGGER.info( "短信验证码发送成功，接口返回数据：" + body );
				JSON j = new JSON(body);
				return (j.getInt( "status" ) == 0);
			} else {
				EntityUtils.consume(entity);
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			post.abort();
		}*/
		return true;
	}

	/**
	 * Update by xk on 2018-07-04.
	 * 绑卡验证码发送. 
	 * 
	 * @param mobile string 目标手机号码
	 * @param code string 验证码
	 * @return boolean
	 */
	public boolean sendByBank(String mobile, String code) {
		try {
			SMSLogs log = new SMSLogs();
			log.setMobile(mobile);
			log.setContent( "您的绑定银行卡验证码为#0。请在页面中提交验证码完成验证。".replace( "#0", code ) );
			return this.sendByCode(log);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 发送短信,示远营销接口.
	 * Update by xk on 2018-07-04. 
	 * 
	 * @param mobile string 目标手机号码
	 * @param content string 发送内容
	 * @return boolean
	 */
	public boolean sendByText(String mobile, String content) {
		HttpClient hc = PoolService.getHttpClient();
		HttpPost post = new HttpPost( "http://121.41.24.86:8080/sms.aspx" );
		
		try {
			// 获取短信签名
			String signName = SmsUtils.getSmsSignName();
			List<NameValuePair> ns = new ArrayList<NameValuePair>(5);
			ns.add(new BasicNameValuePair( "userid", "149" ));
			ns.add(new BasicNameValuePair( "account", "63GZFC" ));
			ns.add(new BasicNameValuePair( "password", "7ay3Y1Wj" ));
			ns.add(new BasicNameValuePair( "mobile", mobile.replace("+86-", "") ));
			ns.add(new BasicNameValuePair( "content", signName + content ));
			ns.add(new BasicNameValuePair( "action", "send" ));
			post.setEntity(new UrlEncodedFormEntity( ns, Constant.SYS_UTF8 ));
			post.setHeader( HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded" );
			post.setHeader( HTTP.USER_AGENT, "sunsw-HttpClient" );
			HttpResponse res = hc.execute(post);
			HttpEntity entity = res.getEntity();
			if (entity == null) {
				// Ignored
			} else if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String body = EntityUtils.toString(entity, "UTF-8");
				System.out.println(body);
				LOGGER.info( "短信发送成功，接口返回数据：" + body );
				JSON j = new JSON(body);
				return (j.getInt("status") == 0);
			} else {
				EntityUtils.consume(entity);
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			post.abort();
		}
	}
}
