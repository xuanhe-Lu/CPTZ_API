package com.ypiao.sign;

import java.io.IOException;
import com.ypiao.fuiou.BankResponse;
import com.ypiao.server.SendAtHttp;

public class Checker {

	private static final String APPKEY = "65d4b4096b618e60ed3466030a88fc32";

	private static final String URL = "https://way.jd.com/DetectionScience/bank4?";

	public static final BankResponse bankcard4(String name, String idCard, String cardNo, String mobile) throws IOException {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("bankCardNo=").append(cardNo);
			sb.append("&name=").append(name);
			sb.append("&identityNo=").append(idCard);
			sb.append("&mobileNo=").append(mobile.replace("+86-", ""));
			sb.append("&appkey=").append(APPKEY);
			Logger.info(URL + "\t=\t" + sb.toString());
			String body = SendAtHttp.post(URL, sb.toString());
			Logger.info(body);
			JSON json = new JSON(body);
			BankResponse res = new BankResponse();
			res.setCode(json.get("code"));
			res.setMsg(json.get("msg"));
			res.setResult(json.getJSON("result"));
			return res;
		} finally {
			sb.setLength(0);
		}
	}
}
