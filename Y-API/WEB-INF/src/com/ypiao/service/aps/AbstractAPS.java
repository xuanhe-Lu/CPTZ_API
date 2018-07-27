package com.ypiao.service.aps;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.framework.context.ApplicationContext;
import com.ypiao.service.APInterService;
import com.ypiao.service.Abstracter;
import com.ypiao.util.APSKey;
import com.ypiao.util.AUtils;

public class AbstractAPS implements Abstracter, APSKey {

	private Map<Integer, Object> cache;

	public AbstractAPS(ApplicationContext context) {
		this.loading(context);
	}

	public void loading(ApplicationContext context) {
		this.cache = new ConcurrentHashMap<Integer, Object>();
		try {
			// 配置信息同步
			this.setBean(SYS_A112, APIAt112.class);
			// 用户相关
			this.setBean(SYS_A120, APIAt120.class);
			this.setBean(SYS_A123, APIAt123.class);
			this.setBean(SYS_A124, APIAt124.class);
			this.setBean(SYS_A125, APIAt125.class);
			this.setBean(SYS_A128, APIAt128.class);
			this.setBean(SYS_A129, APIAt129.class);
			// 标的相关
			this.setBean(SYS_A201, APIAt201.class);
			this.setBean(SYS_A202, APIAt202.class);
			this.setBean(SYS_A203, APIAt203.class);
			// 产品相关
			this.setBean(SYS_A211, APIAt211.class);
			// 运营相关
			this.setBean(SYS_A501, APIAt501.class);
			this.setBean(SYS_A502, APIAt502.class);
			// 通知管理
			this.setBean(SYS_A503, APiAt503.class);
			// 票友学堂
			this.setBean(SYS_A504, APiAt504.class);
			// 常见问题
			this.setBean(SYS_A505, APiAt505.class);
			// 新闻管理
			this.setBean(SYS_A506, APiAt506.class);
			// 渠道统计
			this.setBean(SYS_A507, APiAt507.class);
			// 推送管理
			this.setBean(SYS_A508, APiAt508.class);
			// app版本更新
			this.setBean(SYS_A509, APiAt509.class);
			// 福利专区
			this.setBean(SYS_A510, APiAt510.class);
			this.setBean(SYS_A511, APIAt511.class);
			this.setBean(SYS_A512, APIAt512.class);
			this.setBean(SYS_A513, APIAt513.class);
			// 财务相关
			this.setBean(SYS_A850, APIAt850.class);
			this.setBean(SYS_A851, APIAt851.class);
			this.setBean(SYS_A852, APIAt852.class);
			this.setBean(SYS_A880, APIAt880.class);
			// 用户相关
			this.setBean(SYS_A901, APIAt901.class);
			// 系统相关
			this.setBean(SYS_A990, APIAt990.class);
			this.setBean(SYS_A991, APIAt991.class);
			this.setBean(SYS_A995, APIAt995.class);
			this.setBean(SYS_A996, APIAt996.class);
		} catch (Exception e) {
			// Ignored
		} finally {
			this.setInvoke(context);
		}
	}

	protected Object setBean(Integer key, Class<?> cls) {
		return this.setBean(key, cls.getName());
	}

	protected Object setBean(Integer key, String className, Object... objs) {
		try {
			Object obj = this.cache.get(key);
			if (obj == null) {
				Class<?> cls = Class.forName(className);
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
