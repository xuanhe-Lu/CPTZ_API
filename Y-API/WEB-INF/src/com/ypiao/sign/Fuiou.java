package com.ypiao.sign;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import javax.xml.bind.JAXBException;

import com.alibaba.fastjson.JSONObject;
import com.fuiou.mpay.encrypt.DESCoderFUIOU;
import com.fuiou.mpay.encrypt.RSAUtils;
import com.fuiou.mpay.encrypt.RSAUtilsFUIOU;
import com.fuiou.util.MD5;
import com.ypiao.util.VeStr;
import org.apache.log4j.Logger;
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

	private static Logger logger = Logger.getLogger(Fuiou.class);

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
			logger.info(code + "\t" + url);
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
			logger.info(req.getUserId() + "==" + body);
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
		logger.info(req.getUserId() + "==" + body);*/
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
			logger.info("[请求信息：]" + body);
			body = post(TEST_PAY_URL_ORDER_ACTION_PAY, ms);
			ms.clear(); // 清除请求参数
			body = Suncoder.deCrypt(body, skey, Constant.SYS_UTF8);
			logger.info("[返回数据：]" + body);
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
			logger.info("[请求信息：]" + body);
			body = post(TEST_PAY_URL_PROTO_ORDER_ACTION_PAY, ms);
			ms.clear(); // 清除请求参数
			body = Suncoder.deCrypt(body, skey, Constant.SYS_UTF8);
			logger.info("[返回数据：]" + body);
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
			logger.info("[请求信息：]" + body);
			body = post(TEST_PAY_URL_PAY_ACTION, ms);
			ms.clear(); // 清除请求参数
			body = Suncoder.deCrypt(body, skey, Constant.SYS_UTF8);
			logger.info("[返回数据：]" + body);
			return XML.convert2Bean(body, PayResponse.class);
		} finally {
			sb.setLength(0);
			skey = null;
		}*/
		return null;
	}

	/** 使用协议号进行支付调用的接口 */
	public static PayResponse toPay(ProtoPayRequest req, String key) throws JAXBException, IOException {
		req.setType("03");
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
			logger.info("[请求信息：]" + body);
			body = post(TEST_PAY_URL_PROTO_PAY_ACTION, ms);
			ms.clear(); // 清除请求参数
			body = Suncoder.deCrypt(body, skey, Constant.SYS_UTF8);
			logger.info("[返回数据：]" + body);
			return XML.convert2Bean(body, PayResponse.class);
		} finally {
			sb.setLength(0);
			skey = null;
		}
//		return null;
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
	public static String sendSMS (FuiouPayRequest fuiouPayRequest,String secret ) throws IOException{
		logger.info("come in sendSMS");
		try {


			String version = "1.0";
			String mchntSsn =fuiouPayRequest.getMchntssn();
			String tradeDate = fuiouPayRequest.getTradeDate();
			String mchntcd =fuiouPayRequest.getMchntcd();
			String key = secret;
			String userid = fuiouPayRequest.getUserId();
			String account = fuiouPayRequest.getAccount();
			String cardNo = fuiouPayRequest.getCardNo();
			String idType =fuiouPayRequest.getIdType();
			String idCard =fuiouPayRequest.getIdCard();
			String mobileNo = fuiouPayRequest.getMobileNo();

			NewProtocolBindXmlBeanReq beanReq = new NewProtocolBindXmlBeanReq();
			beanReq.setVersion(version);
			beanReq.setTradeDate(tradeDate);
			beanReq.setMchntCd(mchntcd);
			beanReq.setUserId(userid);
			beanReq.setAccount(account);
			beanReq.setCardNo(cardNo);
			beanReq.setIdType(idType);
			beanReq.setIdCard(idCard);
			beanReq.setMobileNo(mobileNo);
			beanReq.setMchntSsn(mchntSsn);
			beanReq.setSign(getSign(beanReq.sendMsgSignStr(key), "MD5", Constant.FUIOU_PRI_KEY));
			logger.info("sign:"+beanReq.getSign());
			logger.info("【调取富有协议支付首次发送短信绑定接口传递参数】"+beanReq.toString());
			Map<String,String> map = new HashMap<String, String>();
			//String url = "http://www-1.fuiou.com:18670/mobile_pay/newpropay/bindMsg.pay";
			String url =Constant.SEND_SMS;
			String APIFMS =XMapUtil.toXML(beanReq, "UTF-8");;
			APIFMS = DESCoderFUIOU.desEncrypt(APIFMS, DESCoderFUIOU.getKeyLength8(key));
			map.put("MCHNTCD",mchntcd);
			map.put("APIFMS", APIFMS);
			String result = new HttpPoster(url).postStr(map);
			result = DESCoderFUIOU.desDecrypt(result,DESCoderFUIOU.getKeyLength8(key));
			logger.info("【调取富有协议支付首次发送短信绑定接口返回参数】"+result);
			XML.convert2Bean(result,FuiouPayResponse.class);
			return  result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  null;
	}

	/*
	 * @NAME:protoBind
	 * @DESCRIPTION:协议卡绑定
	 * @AUTHOR:luxh
	 * @DATE:2018/8/17
	 * @VERSION:1.0
	 */
	public  static String  protoBind(Map<String,String> map ,String secret){
		String result = "";
		try {
			String version = "1.0";
			String mchntSsn = map.get("MCHNTSSN");
			String tradeDate = map.get("TRADEDATE");
			String mchntcd = map.get("MCHNTCD");
			String key = map.get("KEY");
			String userid = map.get("USERID");
			String account = map.get("ACCOUNT");
			String cardNo = map.get("CARDNO");
			String idType = map.get("IDTYPE");
			String idCard = map.get("IDCARD");
			String mobileNo = map.get("MOBILENO");
			String msgcode = map.get("MSGCODE");

			NewProtocolBindXmlBeanReq beanReq = new NewProtocolBindXmlBeanReq();
			beanReq.setVersion(version);
			beanReq.setTradeDate(tradeDate);
			beanReq.setMchntCd(mchntcd);
			beanReq.setUserId(userid);
			beanReq.setAccount(account);
			beanReq.setCardNo(cardNo);
			beanReq.setIdType(idType);
			beanReq.setIdCard(idCard);
			beanReq.setMobileNo(mobileNo);
			beanReq.setMchntSsn(mchntSsn);
			beanReq.setMsgCode(msgcode);
			beanReq.setSign(getSign(beanReq.proBindSignStr(key), "MD5", Constant.FUIOU_PRI_KEY));


			map.clear();
			//String url = "http://www-1.fuiou.com:18670/mobile_pay/newpropay/bindCommit.pay";
			String APIFMS =XMapUtil.toXML(beanReq, "UTF-8");;
			APIFMS = DESCoderFUIOU.desEncrypt(APIFMS, DESCoderFUIOU.getKeyLength8(key));
			map.put("MCHNTCD",mchntcd);
			map.put("APIFMS", APIFMS);
			result = new HttpPoster(Constant.PROTOCOL_BIND).postStr(map);
			result = DESCoderFUIOU.desDecrypt(result,DESCoderFUIOU.getKeyLength8(key));
			logger.info(result);
			result.getBytes("utf8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  result;
	}


//	public static String
	/**
	 * 获取签名
	 * @param signStr  签名串
	 * @param signtp   签名类型
	 * @param key      密钥
	 * @return
	 * @throws Exception
	 */
	public static String getSign(String signStr,String signtp,String key) throws  Exception{
		String sign = "";
		if ("md5".equalsIgnoreCase(signtp)) {
			sign = MD5.MD5Encode(signStr);
		} else {
			sign =	RSAUtils.sign(signStr.getBytes("utf-8"), key);
		}
		RSAUtilsFUIOU.getKeyInfo();
		return sign;
	}

	public static void main(String[] args) {
		RSAUtilsFUIOU.getKeyInfo();
		FuiouPayRequest fuiouPayRequest = new FuiouPayRequest();
		// UPDATE ypiao.user_bank SET Bid = 101, Code = '194448', BankId = '1020000', BankName = '工商银行', BinId = 621226, BinStat = 1, CardName = '牡丹卡普卡', CardTy = 'D', Channel = 'CUPS', Mobile = '+86-13074149273', Name = '芦炫赫', GmtA = 1534399793211, GmtB = 1534399800780, GmtC = 0, Gdef = 0, State = 2, Time = 1534399800780 WHERE Uid = 107918 AND CNo = '6212263400021079107';
		fuiouPayRequest.setMobileNo("13074149273");
		fuiouPayRequest.setCardNo("6212263400021079107");
		fuiouPayRequest.setVersion("3.0");
		fuiouPayRequest.setMchntcd("0003310F1078099");
		fuiouPayRequest.setIdType("0");
		fuiouPayRequest.setMchntssn(String.valueOf(VeStr.getUSid()));
		fuiouPayRequest.setIdCard("210504199010262112");
		fuiouPayRequest.setTradeDate("20180816");
		fuiouPayRequest.setUserId("107918");
		fuiouPayRequest.setAccount("芦炫赫");
		String s = Suncoder.decode("IlpHEVZFXTgWJnNZSQh2dzwLFBRhYAEFElkuBAs8RBU=");
		try {
			sendSMS(fuiouPayRequest,s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
