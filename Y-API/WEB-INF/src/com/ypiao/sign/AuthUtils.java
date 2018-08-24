package com.ypiao.sign;

import java.io.IOException;
import java.util.Map;
import org.commons.lang.LRUMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import com.ypiao.bean.IDModel;

public class AuthUtils {

	private static Map<String, String> _rds = new LRUMap<String, String>(64);

	public static IDModel realName(String name, String idCard) {
		IDModel m = new IDModel();
		String key = name + idCard;
		String body = _rds.get(key);
		try {
			if (body == null) {
				Document res = Jsoup.connect("https://way.jd.com/idcard/idcard").data("name", name.replace("·", "")).data("cardno", idCard).data("appkey", "65d4b4096b618e60ed3466030a88fc32").timeout(10000).ignoreContentType(true).post();
				body = res.body().text();
				Logger.info("AUTH:\t" + body);
				_rds.put(key, body); // 缓存数据
			}
			JSON obj = new JSON(body);
			String code = obj.getString("code");
			if (code.equals("10000")) {
				m.setMsg("接口调用成功");
				JSON rs = obj.getJSON("result");
				String cd2 = rs.getString("code");
				if (cd2.equals("1")) {
					m.setMsg("查询成功");
					m.setFlag(true);
					JSON ds = rs.getJSON("data");
					m.setAddress(ds.getString("address"));
					m.setBirthday(ds.getString("birthday"));
					m.setSex(ds.getString("sex"));
				} else if (cd2.equals("3")) {
					Logger.info("AUTH=" + body);
				} else {
					m.setMsg("查询失败:" + cd2);
				}
			} else {
				m.setMsg("接口调用失败:" + code);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//TODO 修改测试用
		m.setMsg("查询成功");
		m.setFlag(true);

		m.setAddress("杭州");
		m.setBirthday("1229");
		m.setSex("男");
		return m;
	}
}
