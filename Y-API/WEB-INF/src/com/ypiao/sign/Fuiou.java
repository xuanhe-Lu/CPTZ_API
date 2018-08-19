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
import com.ypiao.util.HttpPostUtil;
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
        StringBuffer sb = new StringBuffer();
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
        }
    }

    /**
     * 绑定的协议卡进行解绑
     */
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

    /**
     * 首次支付
     */
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

    /**
     * 协议支付
     */
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

    /**
     * 使用协议号进行支付调用的接口
     */
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

    /**
     * 验签操作
     */
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
    public static FuiouPayResponse sendSMS(FuiouPayRequest fuiouPayRequest, String secret) throws IOException {
        try {
            logger.info("come in sendSMS");
            String version = "1.0";
            String mchntSsn = fuiouPayRequest.getMchntssn();
            String tradeDate = fuiouPayRequest.getTradeDate();
            String mchntcd = fuiouPayRequest.getMchntcd();
            String key = secret;
            String userid = fuiouPayRequest.getUserId();
            String account = fuiouPayRequest.getAccount();
            String cardNo = fuiouPayRequest.getCardNo();
            String idType = fuiouPayRequest.getIdType();
            String idCard = fuiouPayRequest.getIdCard();
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
            logger.info("sign:" + beanReq.getSign());
            logger.info("【调取富有协议支付首次发送短信绑定接口传递参数】" + beanReq.toString());
            Map<String, String> map = new HashMap<String, String>();
            //String url = "http://www-1.fuiou.com:18670/mobile_pay/newpropay/bindMsg.pay";
            String url = Constant.SEND_SMS;
            String APIFMS = XMapUtil.toXML(beanReq, "UTF-8");
            ;
            APIFMS = DESCoderFUIOU.desEncrypt(APIFMS, DESCoderFUIOU.getKeyLength8(key));
            map.put("MCHNTCD", mchntcd);
            map.put("APIFMS", APIFMS);
            String result = new HttpPoster(url).postStr(map);
            result = DESCoderFUIOU.desDecrypt(result, DESCoderFUIOU.getKeyLength8(key));
            logger.info("【调取富有协议支付首次发送短信绑定接口返回参数】" + result);

            return XML.convert2Bean(result, FuiouPayResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * @NAME:protoBind
     * @DESCRIPTION:协议卡绑定
     * @AUTHOR:luxh
     * @DATE:2018/8/17
     * @VERSION:1.0
     */
    public static FuiouPayResponse protoBind(Map<String, String> map, String secret) {
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

            logger.info("【调取富有协议卡绑定接口参数】" + beanReq.toString());
            map.clear();
            //String url = "http://www-1.fuiou.com:18670/mobile_pay/newpropay/bindCommit.pay";
            String APIFMS = XMapUtil.toXML(beanReq, "UTF-8");
            logger.info("APIFMS:"+APIFMS);
            APIFMS = DESCoderFUIOU.desEncrypt(APIFMS, DESCoderFUIOU.getKeyLength8(key));
            map.put("MCHNTCD", mchntcd);
            map.put("APIFMS", APIFMS);
            String result = new HttpPoster(Constant.PROTOCOL_BIND).postStr(map);
            result = DESCoderFUIOU.desDecrypt(result, DESCoderFUIOU.getKeyLength8(key));
            logger.info("【调取富有协议卡绑定接口返回参数】" + result);
            result.getBytes("utf8");
            return XML.convert2Bean(result, FuiouPayResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /* @NAME:order
     * @DESCRIPTION:协议支付接口
     * @AUTHOR:luxh
     * @DATE:2018/8/19  20:02
     * @VERSION:1.0
     */
    public static FuiouPayResponse order(Map<String, String> mapIn) {
        try {

            String version = "1.0"; // 版本号
            String type = "03";//交易类型
            String userid = mapIn.get("USERID");//用户编号
            String mchntorderid = mapIn.get("MCHNTORDERID");//商户订单号
            String protocolno = mapIn.get("PROTOCOLNO");//协议号
            String amt = mapIn.get("AMT");//交易金额
            String userip = mapIn.get("USERIP");//客户 IP
            String needsendmsg = "0";//是否需要发送短信 0
            String mchntcd = mapIn.get("MCHNTCD");//商户代码
            String backurl = mapIn.get("BACKURL");//回调地址
            String rem1 = "";
            String rem2 = "";
            String rem3 = "";
            String key = mapIn.get("KEY");
            String orderalivetime = mapIn.get("ORDERALIVETIME");


            NewProtocolOrderXmlBeanReq beanReq = new NewProtocolOrderXmlBeanReq();
            beanReq.setVersion(version);
            beanReq.setMchntCd(mchntcd);
            beanReq.setType(type);
            beanReq.setMchntOrderId(mchntorderid);
            beanReq.setUserId(userid);
            beanReq.setAmt(amt);
            beanReq.setProtocolNo(protocolno);
            beanReq.setBackUrl(backurl);
            beanReq.setRem1(rem1);
            beanReq.setRem2(rem2);
            beanReq.setRem3(rem3);
            beanReq.setOrderAliveTime(orderalivetime);
            beanReq.setUserIp(userip);
            beanReq.setNeedSendMsg(needsendmsg);
            beanReq.setSignTp(Constant.SIGN_TP);
            beanReq.setSign(getSign(beanReq.signStr(key), Constant.SIGN_TP, Constant.FUIOU_PRI_KEY));

            Map<String, String> map = new HashMap<String, String>();
            //String url = "http://www-1.fuiou.com:18670/mobile_pay/newpropay/order.pay";

            String APIFMS = XMapUtil.toXML(beanReq, "UTF-8");
            APIFMS = DESCoderFUIOU.desEncrypt(APIFMS, DESCoderFUIOU.getKeyLength8(key));
            map.put("MCHNTCD", mchntcd);
            map.put("APIFMS", APIFMS);
            String result = new HttpPoster(Constant.ORDER).postStr(map);
            result = DESCoderFUIOU.desDecrypt(result, DESCoderFUIOU.getKeyLength8(key));
            System.out.println(result);
            logger.info(result.getBytes("utf8"));
            return XML.convert2Bean(result, FuiouPayResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return null;
    }

    /* @NAME:order
     * @DESCRIPTION:商户支持卡 Bin 查询接口
     * @AUTHOR:luxh
     * @DATE:2018/8/20  0:33
     * @VERSION:1.0
     */
    public static  FuiouPayResponse queryBin(Map<String, String> mapIn) {
        logger.info("come in queryBin");
        String cardNo = mapIn.get("CARDNO");
        String mchntCd = mapIn.get("MCHNTCD");
        String key = mapIn.get("KEY");
        String sign = MD5.MD5Encode(new StringBuffer().append(mchntCd).append("|").append(cardNo).append("|").append(key).toString());
        String fm = new StringBuffer().append("<FM>").append("<MchntCd>").append(mchntCd)
                .append("</MchntCd>").append("<Ono>").append(cardNo).append("</Ono>").append("<Sign>").append(sign)
                .append("</Sign>").append("</FM>").toString();
        Map <String, String> params = new HashMap <String, String>();
        params.put("FM", fm);
        logger.info("[调用富有商户支持卡 Bin 查询接口]" + params);
        try {
            String respStr = HttpPostUtil.postForward(Constant.CARD_BIN_QUERY, params);
            logger.info("[调用富有商户支持卡 Bin 查询接口]" + respStr);
            String rcd = respStr.substring(respStr.indexOf("<Rcd>") + 5, respStr.indexOf("</Rcd>"));
            String rDesc = respStr.substring(respStr.indexOf("<RDesc>") + 7, respStr.indexOf("</RDesc>"));
            String cTp = respStr.substring(respStr.indexOf("<Ctp>") + 5, respStr.indexOf("</Ctp>"));
            String cNm = respStr.substring(respStr.indexOf("<Cnm>") + 5, respStr.indexOf("</Cnm>"));
            String insCd = respStr.substring(respStr.indexOf("<InsCd>") + 7, respStr.indexOf("</InsCd>"));
            String rSign = respStr.substring(respStr.indexOf("<Sign>") + 6, respStr.indexOf("</Sign>"));
            FuiouPayResponse fuiouPayResponse = new FuiouPayResponse();
            fuiouPayResponse.setRcd(rcd);
            fuiouPayResponse.setRdesc(rDesc);
            fuiouPayResponse.setCtp(cTp);
            fuiouPayResponse.setCnm(cNm);
            fuiouPayResponse.setInsCd(insCd);
            fuiouPayResponse.setSign(rSign);
            return fuiouPayResponse;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取签名
     *
     * @param signStr 签名串
     * @param signtp  签名类型
     * @param key     密钥
     * @return
     * @throws Exception
     */
    public static String getSign(String signStr, String signtp, String key) throws Exception {
        String sign = "";
        if ("md5".equalsIgnoreCase(signtp)) {
            sign = MD5.MD5Encode(signStr);
        } else {
            sign = RSAUtils.sign(signStr.getBytes("utf-8"), key);
        }
        RSAUtilsFUIOU.getKeyInfo();
        return sign;
    }


    /* @NAME:unbind
     * @DESCRIPTION:协议解绑接口
     * @AUTHOR:luxh
     * @DATE:2018/8/19  15:08
     * @VERSION:1.0
     */
    public static FuiouPayResponse unbind(Map<String, String> map) {
        try {
            String version = "1.0";
            String mchntcd = map.get("MCHNTCD");//商户代码
            String userId = map.get("USERID");// 用户编号
            String protocolNo = map.get("PROTOCOLNO"); //协议号
            String key = map.get("KEY");//密钥

            NewProtocolBindXmlBeanReq beanReq = new NewProtocolBindXmlBeanReq();
            beanReq.setVersion(version);
            beanReq.setMchntCd(mchntcd);
            beanReq.setUserId(userId);
            beanReq.setProtocolNo(protocolNo);

            beanReq.setSign(getSign(beanReq.proUnBindSignStr(key), "MD5", Constant.FUIOU_PRI_KEY));
            logger.info("[调用富有协议解绑接口参数]" + beanReq);
            Map<String, String> map1 = new HashMap<String, String>();
            String APIFMS = XMapUtil.toXML(beanReq, "UTF-8");
            ;
            APIFMS = DESCoderFUIOU.desEncrypt(APIFMS, DESCoderFUIOU.getKeyLength8(key));
            map1.put("MCHNTCD", mchntcd);
            map1.put("APIFMS", APIFMS);
            String result = new HttpPoster(Constant.UNBIND).postStr(map1);
            result = DESCoderFUIOU.desDecrypt(result, DESCoderFUIOU.getKeyLength8(key));
            logger.info("[调用富有协议解绑接口返回参数]" + result);
            return XML.convert2Bean(result, FuiouPayResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void main(String[] args) {
        HashMap<String,String>map = new HashMap();
        /*RSAUtilsFUIOU.getKeyInfo();
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
            FuiouPayResponse fuiouPayResponse = sendSMS(fuiouPayRequest, s);
            System.out.println("fuiouPayResponse:" + fuiouPayResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        // 协议卡绑定
     /*   map.put("MCHNTSSN",*//*String.valueOf(VeStr.getUSid())*//*"20170630009"); //20170630009
        map.put("TRADEDATE","20180816");
        map.put("MCHNTCD","0002900F0096235");
        map.put("KEY",Suncoder.decode("IlpHEVZFXTgWJnNZSQh2dzwLFBRhYAEFElkuBAs8RBU="));
        map.put("USERID","107918");
        map.put("ACCOUNT","芦炫赫");
        map.put("CARDNO","6212263400021079107");
        map.put("IDTYPE","0");
        map.put("IDCARD","210504199010262112");
        map.put("MOBILENO","13074149273");
        map.put("MSGCODE","022798");
//        FuiouPayResponse fuiouPayResponse = protoBind(map,Suncoder.decode("5old71wihg2tqjug9kkpxnhx9hiujoqj"));
        FuiouPayResponse fuiouPayResponse = protoBind(map,"5old71wihg2tqjug9kkpxnhx9hiujoqj");
        System.out.println("fuiouPayResponse:"+fuiouPayResponse);
*/
        // 协议解绑
        FuiouPayResponse fuiouPayResponse = new FuiouPayResponse();
        map.clear();
        map.put("MCHNTCD","0002900F0096235");//商户代码
        map.put("USERID","1236985478");// 用户编号
        map.put("PROTOCOLNO","14908601655875030001"); //协议号
        map.put("KEY","00b16e8292ff75851ef8e8b02645fa50");//密钥
         fuiouPayResponse = unbind(map);



        // 协议支付
      /*   map.put("USERID","1236985478");//用户编号
        map.put("MCHNTORDERID","14909408631788350725");//商户订单号
        map.put("PROTOCOLNO","1490776393898663163");//协议号
        map.put("AMT","100");//交易金额
        map.put("USERIP","116.239.4.19");//客户 IP
        map.put("MCHNTCD","0002900F0096235");//商户代码
        map.put("BACKURL","http://www.baidu.com");//回调地址
        map.put("KEY",Suncoder.decode("5old71wihg2tqjug9kkpxnhx9hiujoqj"));
        order(map);*/
    }

}
