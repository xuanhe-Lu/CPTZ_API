package com.ypiao.sign;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLContext;
import com.sunsw.http.Consts;
import com.sunsw.http.HttpEntity;
import com.sunsw.http.HttpStatus;
import com.sunsw.http.StatusLine;
import com.sunsw.http.conn.SSLConnectionSocketFactory;
import com.sunsw.http.core.client.CloseableHttpClient;
import com.sunsw.http.core.client.HttpClients;
import com.sunsw.http.entity.StringEntity;
import com.sunsw.http.methods.CloseableHttpResponse;
import com.sunsw.http.methods.HttpPost;
import com.sunsw.http.ssl.SSLContexts;
import com.sunsw.http.util.EntityUtils;

public final class SSL {

	private static final SSL SSL;
	static {
		SSL = new SSL();
		SSL.cache = new HashMap<String, CloseableHttpClient>(2);
	}

	private Map<String, CloseableHttpClient> cache;

	public CloseableHttpClient get(String key) throws IOException {
		return get(key, key.toCharArray());
	}

	private CloseableHttpClient get(String mch, char[] pwd) throws IOException {
		CloseableHttpClient hc = cache.get(mch);
		if (hc == null) {
			InputStream is = this.getClass().getResourceAsStream(mch + ".p12");
			if (is == null) {
				throw new IOException("Certificate Not Found!");
			}
			try {
				KeyStore keyStore = KeyStore.getInstance("PKCS12");
				keyStore.load(is, pwd);
				SSLContext sc = SSLContexts.custom().loadKeyMaterial(keyStore, pwd).build();
				SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sc, new String[] { "TLSv1" });
				hc = HttpClients.custom().setMaxConnTotal(10).setMaxConnPerRoute(50).setSSLSocketFactory(factory).build();
				cache.put(mch, hc);
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			} finally {
				is.close();
			}
		}
		return hc;
	}

	public static String post(String url, String key, String text) throws IOException {
		CloseableHttpClient hc = SSL.get(key);
		HttpPost post = new HttpPost(url);
		try {
			StringEntity se = new StringEntity(text, Consts.UTF8);
			se.setContentType("application/x-www-form-urlencoded");
			post.setEntity(se); // 上传数据
			CloseableHttpResponse res = hc.execute(post);
			try {
				HttpEntity entity = res.getEntity();
				StatusLine s = res.getStatusLine();
				if (s.getStatusCode() == HttpStatus.SC_OK) {
					if (entity == null) {
						// ignored
					} else {
						return EntityUtils.doString(entity, Consts.UTF8);
					}
				} else {
					EntityUtils.consumeQuietly(entity);
				}
				return null;
			} finally {
				res.close();
			}
		} finally {
			post.abort();
		}
	}

}
