package com.ypm.service.imp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.framework.context.ApplicationContext;
import com.ypm.service.SysConfig;
import com.ypm.util.AUtils;

public class ABeaner {

	private Map<String, Object> cache = new HashMap<String, Object>();

	public ABeaner() {
		this(null);
	}

	public ABeaner(ApplicationContext ac, Class<?>... cls) {
		try {
			this.setBean(SysConfig.class);
			Class<?> c = this.getClass();
			URL a = c.getResource(c.getSimpleName() + ".class");
			if ("jar".equals(a.getProtocol())) {
				StringBuilder sb = new StringBuilder();
				sb.append("^((").append(c.getName().replaceAll("(\\w+)$", ""));
				for (Class<?> d : cls) {
					sb.append("|").append(d.getName().replaceAll("(\\w+)$", ""));
				}
				this.loadJar(a, sb.append(")(\\w+)Imp).class$").toString());
			} else if ("file".equals(a.getProtocol())) {
				this.loadFile(a, c.getName());
				for (Class<?> d : cls) {
					a = d.getResource(d.getSimpleName() + ".class");
					this.loadFile(a, d.getName());
				}
			}
		} catch (Exception e) {
			// Ignored
		} finally {
			this.setInvoke(ac);
			this.cache.clear();
		}
	}

	/** .Class loading... */
	private void loadFile(URL url, String name) throws java.io.UnsupportedEncodingException {
		String fix = name.replaceAll("(\\w+)$", "");
		File file = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
		if (file.isFile()) file = file.getParentFile();
		for (String str : file.list()) {
			Matcher m = Pattern.compile("((\\w+)Imp).class$").matcher(str);
			if (m.find()) {
				str = m.group(2);
				setBean((str.substring(0, 3).toLowerCase() + str.substring(3)), (fix + m.group(1)));
			}
		}
	}

	private void loadJar(URL url, String regex) throws IOException {
		JarURLConnection jc = (JarURLConnection) url.openConnection();
		Enumeration<JarEntry> es = jc.getJarFile().entries();
		while (es.hasMoreElements()) {
			String str = es.nextElement().getName().replace("/", ".");
			Matcher m = Pattern.compile(regex).matcher(str);
			if (m.find()) {
				str = m.group(3); // key
				setBean((str.substring(0, 3).toLowerCase() + str.substring(3)), m.group(1));
			}
		}
	}

	protected Object setBean(Class<?> cls) {
		return this.setBean(null, cls.getName());
	}

	protected Object setBean(String key, Class<?> cls) {
		return this.setBean(key, cls.getName());
	}

	protected Object setBean(String key, String clsName, Object... objs) {
		if (key == null) key = AUtils.getKey(clsName);
		try {
			Object obj = this.cache.get(key);
			if (obj == null) {
				Class<?> cls = Class.forName(clsName);
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
		Iterator<Map.Entry<String, Object>> it = cache.entrySet().iterator();
		if (ac == null) {
			while (it.hasNext()) {
				AUtils.setObject(it.next().getValue(), cache);
			}
		} else {
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				Object obj = entry.getValue();
				ac.setBean(entry.getKey(), obj);
				for (Method m : obj.getClass().getMethods()) {
					String name = m.getName();
					if (m.getParameterTypes().length != 1 || !name.startsWith("set")) continue;
					if (name.length() > 6) {
						name = name.substring(3);
					} else {
						name = m.getParameterTypes()[0].getName();
					} // 键值
					String key = AUtils.getKey(name);
					try {
						Object args = this.cache.get(key);
						if (args != null) {
							m.invoke(obj, args);
						} else if (ac.containsBean(key)) {
							m.invoke(obj, ac.getBean(key));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
