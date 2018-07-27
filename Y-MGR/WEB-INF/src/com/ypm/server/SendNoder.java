package com.ypm.server;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.sunsw.net.SocketConnector;
import com.ypm.bean.ServerInfo;
import com.ypm.service.NodeInfoService;
import com.ypm.service.PoolService;
import com.ypm.service.SysBeaner;
import com.ypm.util.Constant;

public class SendNoder {

	protected static Map<Integer, Integer> KVS = new HashMap<Integer, Integer>();

	public static final SocketAddress get(String ip, int port) {
		return new InetSocketAddress(ip, port);
	}

	/** 连接测速处理 */
	public static final long speed(String ip, int port) {
		return speed(get(ip, port));
	}

	private static final long speed(SocketAddress addr) {
		try {
			long s = System.currentTimeMillis();
			Socket c = new Socket();
			try {
				c.connect(addr, 5000);
			} finally {
				c.close();
			}
			return (System.currentTimeMillis() - s);
		} catch (Exception e) {
			return Integer.MAX_VALUE;
		}
	}

	private SocketConnector sc;

	private Map<Integer, NetRunner> cache;

	private Map<String, NetConnect> neter;

	private List<NetConnect> net_dfs;

	private NetConnect net_adm;

	private Map<String, Map<Long, NetConnect>> net_map;

	public SendNoder(SocketConnector sc, Map<Integer, NetRunner> map, boolean async) {
		this.net_dfs = new ArrayList<NetConnect>(1);
		this.net_map = new HashMap<String, Map<Long, NetConnect>>();
		this.neter = new ConcurrentHashMap<String, NetConnect>();
		this.cache = map;
		this.sc = sc;
		if (async) {
			PoolService.getService().execute(() -> loader());
		} else {
			this.loader();
		}
	}

	public void loader() {
		NodeInfoService noder = SysBeaner.get(NodeInfoService.class);
		try {
			if (noder == null) {
				noder = new com.ypm.service.imp.NodeInfoServiceImp();
			} // 获取所有节点信息
			this.loader(noder.findByAll());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.reset();
		}
	}

	private void loader(List<ServerInfo> ls) {
		for (ServerInfo s : ls) {
			int type = Integer.valueOf(s.getMfk(), 36);
			SocketAddress remote = get(s.getIper(), s.getPort());
			KVS.put(remote.hashCode(), 9); // 后台发起
			long ct = speed(remote) * 100 + s.getSid();
			s.setStime(ct); // 当前连接时间
			NetConnect c = neter.get(s.getKey());
			if (c == null) {
				c = new NetConnect(sc.connect(remote, 3), s, cache, type);
			} else {
				ServerInfo info = c.getServer();
				info.setSid(s.getSid()); // 节点标识
				if (info.hashCode() == s.hashCode()) {
					// Ignored
				} else {
					Map<?, ?> ms = net_map.get(s.getMfk());
					if (ms != null) {
						ms.remove(info.getStime());
					}
					c = new NetConnect(sc.connect(remote, 3), s, cache, type);
				}
			}
			neter.put(s.getKey(), c);
		} // 检出各节点最快
		Iterator<NetConnect> it = neter.values().iterator();
		while (it.hasNext()) {
			NetConnect c = it.next();
			ServerInfo s = c.getServer();
			if (Constant.SYS_SSIA.equalsIgnoreCase(s.getMfk())) {
				// 同一中心
			} else {
				Map<Long, NetConnect> ns = net_map.get(s.getMfk());
				if (ns == null) {
					ns = new TreeMap<Long, NetConnect>();
					net_map.put(s.getMfk(), ns);
				}
				ns.put(s.getStime(), c);
			}
		}
		this.loader(net_map);
	}

	/** 重构节点数组 */
	private void loader(Map<String, Map<Long, NetConnect>> map) {
		List<NetConnect> ds = this.net_dfs;
		List<NetConnect> fs = new ArrayList<NetConnect>(map.size() + 1);
		try {
			Iterator<Map<Long, NetConnect>> it = map.values().iterator();
			while (it.hasNext()) {
				Iterator<NetConnect> cs = it.next().values().iterator();
				if (cs.hasNext()) {
					fs.add(cs.next());
				} else {
					it.remove();
				}
			}
			this.net_dfs = fs;
		} finally {
			this.findAdm();
			ds.clear();
		}
	}

	private void reset() {
		for (NetConnect net : net_dfs) {
			net.syncer();
		}
	}

	/** 获取最快节点 */
	private NetConnect findAdm() {
		long ct = Integer.MAX_VALUE;
		for (NetConnect net : net_dfs) {
			long a = net.getServer().getStime();
			if (ct > a) {
				this.net_adm = net;
				ct = a;
			}
		}
		return net_adm;
	}

	/** 获取指定节点 */
	public NetConnect get(String key) {
		return neter.get(key);
	}

	/** 获取最快节点 */
	public NetConnect getAdm() {
		if (net_adm == null) {
			return this.findAdm();
		} else {
			return net_adm;
		}
	}

	/** 获取中心节点 */
	public List<NetConnect> getAll() {
		return net_dfs;
	}

}
