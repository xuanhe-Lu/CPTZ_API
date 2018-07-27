package com.ypm.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.commons.logs.Logger;
import com.sunsw.net.SocketSessionConfig;
import com.sunsw.net.socket.NioSocketAcceptor;
import com.sunsw.net.socket.NioSocketConnector;
import com.ypm.service.APIBaseService;
import com.ypm.service.SysConfig;

public class APIServer {

	protected static final Logger logger = SysConfig.logger;

	protected static final String CODEC_KEY = "codec";

	protected static final String KEY_CODER = "S20180312";

	protected static final String KEY_SHZYP = "YiPiao";

	private static APIServer server;

	/** 获取公钥 */
	public static String get(int kv) {
		if (kv == 9) {
			return KEY_CODER;
		} else {
			return KEY_SHZYP;
		}
	}

	/** 区域语言 */
	public static Locale getLocale(byte kv) {
		switch (kv) {
		case 1: // zh_CN
			return Locale.CHINA;
		case 2: // zh_TW
			return Locale.TAIWAN;
		case 3: // zh_HK
			return Locale.TAIWAN;
		case 10: // en
			return Locale.US;
		case 11: // en_US
			return Locale.US;
		case 12: // en_CA
			return Locale.CANADA;
		default:
			return Locale.CHINA;
		}
	}

	/** 获取发送节点 */
	public static SendNoder getNoder() {
		if (server == null) {
			server = new APIServer(false);
		}
		return server.noder;
	}

	public static void initServer(APIBaseService service) throws IOException {
		NioSocketAcceptor server = new NioSocketAcceptor();
		server.getFilterBuilder().addLast(CODEC_KEY, new CodecAdapter(service));
		server.setHandler(new NetHandler(service));
		SocketSessionConfig cfg = (SocketSessionConfig) server.getSessionConfig();
		cfg.setSoLinger(0);
		cfg.setSoTimeout(45000);
		server.bind(new InetSocketAddress(8603)); // 启动监听服务
		logger.info("Starting TCP socket monitor on address " + server.getLocalAddress());
		APIServer.server = new APIServer(true); // 启动同步客户端
	}

	private SendNoder noder;

	public APIServer(boolean async) {
		APIServer.server = this;
		GetAdapter adapter = new GetAdapter();
		Map<Integer, NetRunner> map = new ConcurrentHashMap<Integer, NetRunner>();
		NioSocketConnector sc = new NioSocketConnector(1);
		sc.getFilterBuilder().addLast(CODEC_KEY, adapter);
		sc.setHandler(new ReadHandler(map));
		this.noder = new SendNoder(sc, map, async);
	}
}
