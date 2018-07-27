package com.ypiao.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ypiao.server.APIServer;
import com.ypiao.server.NetConnect;
import com.ypiao.util.AUtils;

public class SyncMap implements Serializable {

	private static final long serialVersionUID = 7416996500012805633L;

	private static final int SIZE = 12;

	private static final int TYPE_ABC = 0;

	private static final int TYPE_ADM = 1;

	private static final int TYPE_ALL = 9;

	private static List<SyncMap> CACHE = new ArrayList<SyncMap>(SIZE);

	private Map<String, Object> kv = new HashMap<String, Object>();

	private File file;

	private int type = 0;

	private NetConnect neter;

	private synchronized static SyncMap find(NetConnect net, int type) {
		SyncMap sm = null;
		if (CACHE.size() > 0) {
			sm = CACHE.remove(0);
			sm.clear();
		} else {
			sm = new SyncMap();
		} // 数据信息
		sm.neter = net;
		sm.type = type;
		return sm;
	}

	/** 同步数据到节点 */
	public static SyncMap get(String key) {
		return find(APIServer.getNoder().get(key), TYPE_ABC);
	}

	/** 同步数据到后台 */
	public static SyncMap getAdm() {
		return find(null, TYPE_ADM);
	}

	/** 同步数据到全服 */
	public static SyncMap getAll() {
		return find(null, TYPE_ALL);
	}

	public SyncMap add(File file) {
		this.file = file;
		return this;
	}

	public SyncMap add(Object obj) {
		AUtils.toMap(kv, obj);
		return this;
	}

	public SyncMap addAll(Map<String, String> map) {
		this.kv.putAll(map);
		return this;
	}

	public SyncMap add(String key, Object value) {
		if (value == null) {
			// Ignored
		} else {
			this.kv.put(key, value);
		}
		return this;
	}

	public SyncMap addTable(String tbl) {
		return this.add("_tbl", tbl);
	}

	private void clear() {
		this.kv.clear();
		this.file = null;
		this.type = 0;
	}

	public void destroy() {
		this.clear();
		if (CACHE.size() < SIZE) {
			CACHE.add(this);
		}
	}

	public Messager send(int cls, String act) throws IOException {
		return send(cls, act, file);
	}

	public Messager send(int cls, String act, File file) throws IOException {
		kv.put("act", act); // action
		String msg = AUtils.toString(kv);
		try {
			Messager mgr = null;
			if (TYPE_ABC == type) {
				if (neter != null) {
					mgr = neter.send(cls, cls, msg, file);
				}
			} else if (TYPE_ADM == type) {
				mgr = APIServer.getNoder().getAdm().send(cls, cls, msg, file);
			} else if (TYPE_ALL == type) {
				List<NetConnect> ls = APIServer.getNoder().getAll();
				for (NetConnect net : ls) {
					if (mgr == null) {
						mgr = net.send(cls, cls, msg, file);
						if (mgr == null || mgr.getCode() != 0) {
							return mgr;
						}
					} else {
						net.sender(cls, cls, msg, file);
					}
				}
			}
			return mgr;
		} finally {
			this.destroy();
			msg = null;
		}
	}

	public Messager send(int cls, String act, Object obj) throws IOException {
		return add(obj).send(cls, act, file);
	}

	public Messager send(int cls, String act, Object obj, File file) throws IOException {
		return add(obj).send(cls, act, file);
	}

	public void sender(int cls, String act) {
		this.sender(cls, act, file);
	}

	public void sender(int cls, String act, File file) {
		kv.put("act", act); // action
		String msg = AUtils.toString(kv);
		try {
			if (TYPE_ABC == type) {
				if (neter != null) {
					neter.sender(cls, cls, msg, file);
				}
			} else if (TYPE_ADM == type) {
				APIServer.getNoder().getAdm().sender(cls, cls, msg, file);
			} else if (TYPE_ALL == type) {
				List<NetConnect> ls = APIServer.getNoder().getAll();
				for (NetConnect net : ls) {
					net.sender(cls, cls, msg, file);
				}
			}
		} catch (Exception e) {
			// Ignored
		} finally {
			this.destroy();
			msg = null;
		}
	}

	public void sender(int cls, String act, Object obj) {
		this.add(obj).sender(cls, act, file);
	}

	public void sender(int cls, String act, Object obj, File file) {
		this.add(obj).sender(cls, act, file);
	}
}
