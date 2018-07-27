package com.ypiao.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.sunsw.net.SocketConnector;
import com.ypiao.bean.Messager;
import com.ypiao.bean.ServerInfo;
import com.ypiao.bean.SyncMap;
import com.ypiao.service.NodeInfoService;
import com.ypiao.service.PoolService;
import com.ypiao.service.SysBeaner;
import com.ypiao.util.APSKey;
import com.ypiao.util.Constant;

public class SendNoder {

	public static final SocketAddress get(String ip, int port) {
		return new InetSocketAddress(ip, port);
	}

	/** 连接测速处理 */
	public static final long speed(String ip, int port) {
		return speed(get(ip, port));
	}

	public static final long speed(SocketAddress addr) {
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

	private NetConnect net_adm;

	private List<NetConnect> net_dfs;

	private Map<String, Map<Long, NetConnect>> net_map;

	public SendNoder(SocketConnector sc, Map<Integer, NetRunner> map) {
		this.net_dfs = new ArrayList<NetConnect>(1);
		this.net_map = new HashMap<String, Map<Long, NetConnect>>();
		this.neter = new ConcurrentHashMap<String, NetConnect>();
		this.cache = map;
		this.sc = sc;
		PoolService.getService().execute(() -> loader());
	}

	public void loader() {
		NodeInfoService noder = SysBeaner.get(NodeInfoService.class);
		try {
			if (noder == null) {
				noder = new com.ypiao.service.imp.NodeInfoServiceImp();
			} // 获取所有节点信息
			List<ServerInfo> ls = noder.findByAll();
			this.loader(ls); // 本地加载节点
			ServerInfo s = new ServerInfo();
			s.setId(Constant.SYS_SSID);
			s.setPort(Constant.SYS_SERVER_PORT);
			try {
				Thread.sleep(700); // 700这等后执行
			} catch (InterruptedException e) {
				e.printStackTrace();
			} // 同步服务请求信息
			Messager mgr = SyncMap.getAdm().send(APSKey.SYS_A999, "server", s);
			try {
				if (mgr.isList()) {
					ls = mgr.getList(ServerInfo.class);
					if (ls.size() > 0) {
						this.loader(ls);
						noder.saveAll(ls);
					}
				}
			} finally {
				mgr.destroy();
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		} finally {
			for (NetConnect c : net_dfs) {
				c.syncer();
			}
		}
	}

	private void loader(List<ServerInfo> ls) {
		for (ServerInfo s : ls) {
			if (Constant.SYS_SSID.contains(s.getKey())) {
				// Ignored
			} else {
				SocketAddress remote = get(s.getIper(), s.getPort());
				long ct = speed(remote) * 100 + s.getSid();
				s.setStime(ct); // 当前连接时间
				NetConnect c = neter.get(s.getKey());
				if (c == null) {
					c = new NetConnect(sc.connect(remote, 3), s, cache, s.getMfk());
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
						c = new NetConnect(sc.connect(remote, 3), s, cache, s.getMfk());
					}
				}
				neter.put(s.getKey(), c);
			}
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
			fs.add(0, getAdm());
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
			ds.clear();
		}
	}

	/** 获取指定节点 */
	public NetConnect get(String key) {
		return neter.get(key);
	}

	/** 获取管理节点 */
	public NetConnect getAdm() {
		if (net_adm == null) {
			ServerInfo s = new ServerInfo();
			s.setIper(Constant.SYS_ADMIN_HOST);
			s.setPort(Constant.SYS_ADMIN_PORT);
			s.setSid(999); // 管理编号
			SocketAddress remote = get(s.getIper(), s.getPort());
			net_adm = new NetConnect(sc.connect(remote, 3), s, cache, 0);
		}
		return net_adm;
	}

	/** 获取中心节点 */
	public List<NetConnect> getAll() {
		for (int i = 0; i < 3; i++) {
			try {
				if (net_dfs.size() > 0) {
					break;
				} else {
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return net_dfs;
	}
}
