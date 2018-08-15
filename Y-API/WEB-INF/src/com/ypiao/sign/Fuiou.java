package com.ypiao.sign;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import javax.xml.bind.JAXBException;
import org.commons.code.DigestUtils;
import org.commons.code.Suncoder;
import com.sunsw.http.*;
import com.sunsw.http.client.HttpClient;
import com.sunsw.http.client.UrlEncodedFormEntity;
import com.sunsw.http.message.BasicNameValuePair;
import com.sunsw.http.methods.HttpPost;
import com.sunsw.http.protocol.HTTP;
import com.sunsw.http.util.EntityUtils;
import com.ypiao.fuiou.*;
import com.ypiao.service.PoolService;
import com.ypiao.util.AUtils;
import com.ypiao.util.Constant;

public class Fuiou {

	private static final String TEST_PAY_URL_QUERY_CARDBIN = "https://mpay.fuiou.com:16128/findPay/cardBinQuery.pay"; // 查询卡号是否支持

	private static final String TEST_PAY_URL_ORDER_ACTION_PAY = "https://mpay.fuiou.com:16128/apinewpay/orderAction.pay"; // 商户首次协议下单、验证码获取接口

	private static final String TEST_PAY_URL_PROTO_ORDER_ACTION_PAY = "https://mpay.fuiou.com:16128/apipropay/orderAction.pay"; // 使用协议号进行下单、验证码获取接口测试地址

	private static final String TEST_PAY_URL_PAY_ACTION = "https://mpay.fuiou.com:16128/apinewpay/payAction.pay"; // 首次协议支付接口测试URL。

	private static final String TEST_PAY_URL_PROTO_PAY_ACTION = "https://mpay.fuiou.com:16128/apipropay/payAction.pay"; // 使用协议号进行支付的测试接口地址

	private static final String TEST_PAY_URL_PROTO_QUERY_CARDBIND = "https://mpay.fuiou.com:16128/cardPro/queryAction.pay"; // 协议卡绑定查询

	private static final String TEST_PAY_URL_PROTO_UNBIND_CARD = "https://mpay.fuiou.com:16128/cardPro/unbindAction.pay"; // 协议卡解绑

	private static String getKey(String key) {
		key = (key == null) ? "" : key.trim();
		int len = key.length();
		int j = (len % 64);
		StringBuilder sb = new StringBuilder(len + j);
		sb.append(key);
		for (; j < 64; j++) {
			sb.append('D');
		}
		return sb.toString();
	}

	private static String post(String url, Map<String, String> ms) throws IOException {
		/*HttpClient hc = PoolService.getHttpClient();
		HttpPost post = new HttpPost(url);
		try {
			List<NameValuePair> ns = new ArrayList<NameValuePair>(ms.size());
			Iterator<Entry<String, String>> it = ms.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				ns.add(new BasicNameValuePair(e.getKey(), e.getValue()));
			}
			post.setEntity(new UrlEncodedFormEntity(ns, Constant.SYS_UTF8));
			post.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
			post.setHeader(HTTP.USER_AGENT, "sunsw-HttpClient");
			HttpResponse res = hc.execute(post);
			int code = res.getStatusLine().getStatusCode();
			System.out.println(code + "\t" + url);
			HttpEntity entity = res.getEntity();
			if (entity == null) {
				// Ignored
			} else if (code == HttpStatus.SC_OK) {
				return EntityUtils.toString(entity, "UTF-8");
			} else {
				EntityUtils.consume(entity);
			}
			return null;
		} finally {
			post.abort();
		}*/
		return null;
	}

	public static CardBinResponse cardBinQry(String sellId, String key, String cardNo) throws IOException {
		/*StringBuffer sb = new StringBuffer();
		try {
			sb.append(sellId).append("|").append(cardNo).append("|").append(key);
			String sign = DigestUtils.md5Hex(sb.toString());
			sb.setLength(0);
			sb.append("<FM>").append("<MchntCd>").append(sellId).append("</MchntCd>").append("<Ono>").append(cardNo).append("</Ono>").append("<Sign>").append(sign).append("</Sign>").append("</FM>");
			Map<String, String> ms = new HashMap<String, String>(1);
			ms.put("FM", sb.toString());
			String body = post(TEST_PAY_URL_QUERY_CARDBIN, ms);
			ms.clear();
			XML.decode(ms, body); // 格式化数据
			return AUtils.toObject(CardBinResponse.class, ms);
		} finally {
			sb.setLength(0);
		}*/
		return null;
	}

	/** 绑定的协议卡进行解绑 */
	public static void unBindCard(String sellId, String key, String userId, String SNo) throws JAXBException, IOException {
		/*ProtoUnbindRequest req = new ProtoUnbindRequest();
		req.setVersion("3.0");
		req.setMchntcd(sellId);
		req.setUserId(userId);
		req.setProtocolno(SNo);
		StringBuffer sb = new StringBuffer();
		try {
			sb.append(req.getVersion()).append('|');
			sb.append(req.getMchntcd()).append('|');
			sb.append(req.getUserId()).append('|');
			sb.append(req.getProtocolno()).append('|');
			sb.append(key);
			req.setSign(DigestUtils.md5Hex(sb.toString().getBytes(Constant.SYS_UTF8))); // 签名
			sb.setLength(0);
			Map<String, String> ms = new HashMap<String, String>(1);
			ms.put("FMS", XML.convert2Xml(req));
			String body = post(TEST_PAY_URL_PROTO_UNBIND_CARD, ms);
			System.out.println(req.getUserId() + "==" + body);
		} finally {
			sb.setLength(0);
		}*/
	}

	public static void query(QueryCardRequest req, String key) throws JAXBException, IOException {
		/*req.setVersion("3.0");
		StringBuffer sb = new StringBuffer();
		sb.append(req.getVersion()).append('|');
		sb.append(req.getMchntcd()).append('|');
		sb.append(req.getUserId()).append('|');
		sb.append(key);
		req.setSign(DigestUtils.md5Hex(sb.toString().getBytes(Constant.SYS_UTF8))); // 签名
		sb.setLength(0);
		Map<String, String> ms = new HashMap<String, String>(1);
		ms.put("FMS", XML.convert2Xml(req));
		String body = post(TEST_PAY_URL_PROTO_QUERY_CARDBIND, ms);
		System.out.println(req.getUserId() + "==" + body);*/
	}

	/** 首次支付 */
	// 商户首次协议下单、验证码获取接口
	public static OrderResponse order(OrderRequest req, String key) throws JAXBException, IOException {
		/*req.setType("03");
		req.setVersion("3.0");
		req.setIdtype("0");
		req.setCvn("");
		req.setSigntp("MD5");
		StringBuffer sb = new StringBuffer();
		sb.append(req.getType()).append('|');
		sb.append(req.getVersion()).append('|');
		sb.append(req.getMchntcd()).append('|');
		sb.append(req.getMchntorderid()).append('|');
		sb.append(req.getUserId()).append('|');
		sb.append(req.getAmt()).append('|');
		sb.append(req.getBankcard()).append('|');
		sb.append(req.getBackurl()).append('|');
		sb.append(req.getName()).append('|');
		sb.append(req.getIdno()).append('|');
		sb.append(req.getIdtype()).append('|');
		sb.append(req.getMobile()).append('|');
		sb.append(req.getUserIP()).append('|');
		sb.append(key);
		req.setSign(DigestUtils.md5Hex(sb.toString().getBytes(Constant.SYS_UTF8))); // 签名
		String skey = getKey(key);
		try {
			String body = XML.convert2Xml(req);
			Map<String, String> ms = new HashMap<String, String>(2);
			ms.put("MCHNTCD", req.getMchntcd());
			ms.put("APIFMS", Suncoder.enCrypt(body, skey, Constant.SYS_UTF8));
			System.out.println("[请求信息：]" + body);
			body = post(TEST_PAY_URL_ORDER_ACTION_PAY, ms);
			ms.clear(); // 清除请求参数
			body = Suncoder.deCrypt(body, skey, Constant.SYS_UTF8);
			System.out.println("[返回数据：]" + body);
			return XML.convert2Bean(body, OrderResponse.class);
		} finally {
			sb.setLength(0);
			skey = null;
		}*/
		return null;
	}

	/** 协议支付 */
	// 用户使用协议号下单、短信发送接口，注意使用协议号是关键，不用传银行卡四要素了
	public static OrderResponse order(ProtoRequest req, String key) throws JAXBException, IOException {
		/*req.setType("03");
		req.setVersion("3.0");
		req.setRem2("");
		req.setRem3("");
		req.setSigntp("MD5");
		StringBuffer sb = new StringBuffer();
		sb.append(req.getType()).append('|');
		sb.append(req.getVersion()).append('|');
		sb.append(req.getMchntcd()).append('|');
		sb.append(req.getMchntorderid()).append('|');
		sb.append(req.getUserId()).append('|');
		sb.append(req.getProtocolno()).append('|');
		sb.append(req.getAmt()).append('|');
		sb.append(req.getBackurl()).append('|');
		sb.append(req.getUserIP()).append('|');
		sb.append(key);
		req.setSign(DigestUtils.md5Hex(sb.toString().getBytes(Constant.SYS_UTF8))); // 签名
		String skey = getKey(key);
		try {
			String body = XML.convert2Xml(req);
			Map<String, String> ms = new HashMap<String, String>(2);
			ms.put("MCHNTCD", req.getMchntcd());
			ms.put("APIFMS", Suncoder.enCrypt(body, skey, Constant.SYS_UTF8));
			System.out.println("[请求信息：]" + body);
			body = post(TEST_PAY_URL_PROTO_ORDER_ACTION_PAY, ms);
			ms.clear(); // 清除请求参数
			body = Suncoder.deCrypt(body, skey, Constant.SYS_UTF8);
			System.out.println("[返回数据：]" + body);
			return XML.convert2Bean(body, OrderResponse.class);
		} finally {
			sb.setLength(0);
		}*/
		return null;
	}

	public static PayResponse toPay(PayRequest req, String key) throws JAXBException, IOException {
		/*req.setType("03");
		req.setVersion("3.0");
		req.setRem2("");
		req.setRem3("");
		req.setSigntp("MD5");
		StringBuffer sb = new StringBuffer();
		sb.append(req.getType()).append('|');
		sb.append(req.getVersion()).append('|');
		sb.append(req.getMchntcd()).append('|');
		sb.append(req.getOrderId()).append('|');
		sb.append(req.getMchntorderid()).append('|');
		sb.append(req.getUserId()).append('|');
		sb.append(req.getBankcard()).append('|');
		sb.append(req.getVercd()).append('|');
		sb.append(req.getMobile()).append('|');
		sb.append(req.getUserIP()).append('|');
		sb.append(key);
		req.setSign(DigestUtils.md5Hex(sb.toString().getBytes(Constant.SYS_UTF8))); // 签名
		String skey = getKey(key);
		try {
			String body = XML.convert2Xml(req);
			Map<String, String> ms = new HashMap<String, String>(2);
			ms.put("MCHNTCD", req.getMchntcd());
			ms.put("APIFMS", Suncoder.enCrypt(body, skey, Constant.SYS_UTF8));
			System.out.println("[请求信息：]" + body);
			body = post(TEST_PAY_URL_PAY_ACTION, ms);
			ms.clear(); // 清除请求参数
			body = Suncoder.deCrypt(body, skey, Constant.SYS_UTF8);
			System.out.println("[返回数据：]" + body);
			return XML.convert2Bean(body, PayResponse.class);
		} finally {
			sb.setLength(0);
			skey = null;
		}*/
		return null;
	}

	/** 使用协议号进行支付调用的接口 */
	public static PayResponse toPay(ProtoPayRequest req, String key) throws JAXBException, IOException {
		/*req.setType("03");
		req.setVersion("3.0");
		req.setRem2("");
		req.setRem3("");
		req.setSigntp("MD5");
		StringBuffer sb = new StringBuffer();
		sb.append(req.getType()).append('|');
		sb.append(req.getVersion()).append('|');
		sb.append(req.getMchntcd()).append('|');
		sb.append(req.getOrderId()).append('|');
		sb.append(req.getMchntorderid()).append('|');
		sb.append(req.getProtocolno()).append('|');
		sb.append(req.getUserId()).append('|');
		sb.append(req.getVercd()).append('|');
		sb.append(key);
		req.setSign(DigestUtils.md5Hex(sb.toString().getBytes(Constant.SYS_UTF8))); // 签名
		String skey = getKey(key);
		try {
			String body = XML.convert2Xml(req);
			Map<String, String> ms = new HashMap<String, String>(2);
			ms.put("MCHNTCD", req.getMchntcd());
			ms.put("APIFMS", Suncoder.enCrypt(body, skey, Constant.SYS_UTF8));
			System.out.println("[请求信息：]" + body);
			body = post(TEST_PAY_URL_PROTO_PAY_ACTION, ms);
			ms.clear(); // 清除请求参数
			body = Suncoder.deCrypt(body, skey, Constant.SYS_UTF8);
			System.out.println("[返回数据：]" + body);
			return XML.convert2Bean(body, PayResponse.class);
		} finally {
			sb.setLength(0);
			skey = null;
		}*/
		return null;
	}

	/** 验签操作 */
	public static boolean verify(Map<String, String> map, String key) {
		/*StringBuilder sb = new StringBuilder(128);
		sb.append(map.get("TYPE")).append('|');
		sb.append(map.get("VERSION")).append('|');
		sb.append(map.get("RESPONSECODE")).append('|');
		sb.append(map.get("MCHNTCD")).append('|');
		sb.append(map.get("MCHNTORDERID")).append('|');
		sb.append(map.get("ORDERID")).append('|');
		sb.append(map.get("AMT")).append('|');
		sb.append(map.get("BANKCARD")).append('|');
		sb.append(key);
		String sign = DigestUtils.md5Hex(sb.toString().getBytes(Constant.SYS_UTF8));
		return sign.equals(map.get("SIGN"));*/
		return false;
	}


	/*
	 * @NAME:sendSMS
	 * @DESCRIPTION:富有协议支付首次发送短信绑定
	 * @AUTHOR:luxh
	 * @DATE:2018/8/15
	 * @VERSION:1.0
	 */
	public static PayResponse sendSMS (Map<String,Object> map ){

return null;
	}
}
