package com.ypm.service.aps;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.framework.context.ApplicationContext;
import com.ypm.service.APInterService;
import com.ypm.service.Abstracter;
import com.ypm.util.APSKey;
import com.ypm.util.AUtils;

public class AbstractAPS implements Abstracter, APSKey {

	private Map<Integer, Object> cache;

	public AbstractAPS(ApplicationContext ac) {
		this.loading(ac);
	}

	public void loading(ApplicationContext ac) {
		try {
			this.cache = new ConcurrentHashMap<Integer, Object>();
			// this.setBean(SYS_A112, APiAt112.class);
			this.setBean(SYS_A120, APiAt120.class);
			this.setBean(SYS_A121, APiAt121.class);
			// this.setBean(SYS_A122, APiAt122.class);
			this.setBean(SYS_A123, APiAt123.class);
			this.setBean(SYS_A124, APiAt124.class);
			this.setBean(SYS_A125, APiAt125.class);
			this.setBean(SYS_A128, APiAt128.class);
			this.setBean(SYS_A129, APiAt129.class);
			// this.setBean(SYS_A168, APiAt168.class);
			this.setBean(SYS_A203, APiAt203.class);
			//
			// this.setBean(SYS_A202, APiAt202.class);
			this.setBean(SYS_A512, APiAt512.class);
			this.setBean(SYS_A850, APiAt850.class);
			this.setBean(SYS_A851, APiAt851.class);
			this.setBean(SYS_A852, APiAt852.class);
			this.setBean(SYS_A853, APiAt853.class);
			this.setBean(SYS_A880, APiAt880.class);
			this.setBean(SYS_A995, APiAt995.class);
			this.setBean(SYS_A996, APiAt996.class);
			this.setBean(SYS_A999, APiAt999.class);
		} catch (Exception e) {
			// Ignored
		} finally {
			this.setInvoke(ac);
		}
	}

	protected Object setBean(Integer key, Class<?> cls) {
		return this.setBean(key, cls.getName());
	}

	protected Object setBean(Integer key, String name, Object... objs) {
		try {
			Object obj = this.cache.get(key);
			if (obj == null) {
				Class<?> cls = Class.forName(name);
				try {
					Class<?>[] args = new Class[objs.length];
					for (int i = 0; i < objs.length; i++) {
						args[i] = objs[i].getClass();
					}
					obj = cls.getConstructor(args).newInstance(objs);
				} catch (Exception e) {
					obj = cls.newInstance();
				}
				this.cache.put(key, obj);
			}
			return obj;
		} catch (Exception e) {
			return null;
		}
	}

	private void setInvoke(ApplicationContext ac) {
		Iterator<Object> it = this.cache.values().iterator();
		while (it.hasNext()) {
			AUtils.setObject(it.next(), ac);
		}
	}

	public APInterService getInterface(Integer key) {
		return (APInterService) this.cache.get(key);
	}
}
