package com.ypiao.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.framework.context.ApplicationContext;

public final class AUtils {

	private static Map<String, Map<String, String>> KEYS = new HashMap<String, Map<String, String>>();

	public static final String[] SEP = { ",\uFFFF", "\u0001", "\u0002", "\u0003", "\u0004", "\u0005", "\u0006" };

	public static final String REGEX = "([\\w]+)\\=(.*?)(" + SEP[1] + "|$)";

	/** 时间戳 */
	private static long UUID_TIME = 0;

	private static void set(Method m, Class<?> cls, Object obj, String v, int t) {
		try {
			if (cls.equals(String.class)) {
				if (v != null && v.equals("null")) {
					v = null;
				}
				m.invoke(obj, v);
			} else if (v == null) {
				// Ignored
			} else if (cls.equals(int.class) || cls.equals(Integer.class)) {
				m.invoke(obj, Integer.parseInt(v));
			} else if (cls.equals(long.class) || cls.equals(Long.class)) {
				m.invoke(obj, Long.parseLong(v));
			} else if (cls.equals(float.class) || cls.equals(Float.class)) {
				m.invoke(obj, Float.parseFloat(v));
			} else if (cls.equals(double.class) || cls.equals(Double.class)) {
				m.invoke(obj, Double.parseDouble(v));
			} else if (cls.equals(boolean.class) || cls.equals(Boolean.class)) {
				if (v.equals("0") || v.equalsIgnoreCase("false")) {
					m.invoke(obj, false);
				} else {
					m.invoke(obj, true);
				}
			} else if (cls.equals(String[].class)) {
				Object os = v.split(SEP[t + 1]);
				m.invoke(obj, os);
			} else if (cls.equals(BigDecimal.class)) {
				m.invoke(obj, new BigDecimal(v));
			}
		} catch (Exception e) {
			// do nothing, use the default value
		}
	}

	/** 兑换数据 */
	public static final Object convert(Object obj, Map<String, String> map) throws SecurityException {
		String n = null, key = null;
		for (Method m : obj.getClass().getMethods()) {
			if (m.getParameterTypes().length != 1) {
				continue;
			}
			n = m.getName().toLowerCase();
			if (n.startsWith("set")) {
				key = n.substring(3);
			} else {
				continue;
			} // set Parameter
			if (map.containsKey(key)) {
				set(m, m.getParameterTypes()[0], obj, map.get(key), 1);
			}
		}
		return obj;
	}

	/** 兑换数据 */
	private static final Object convert(Object obj, Map<String, String> map, int t) throws SecurityException {
		String n = null, key = null;
		for (Method m : obj.getClass().getMethods()) {
			if (m.getParameterTypes().length != 1)
				continue;
			n = m.getName().toLowerCase();
			if (n.startsWith("set")) {
				key = n.substring(3);
			} else {
				continue;
			} // set Parameter
			set(m, m.getParameterTypes()[0], obj, map.remove(key), t);
			if (map.size() <= 0)
				break;
		}
		return obj;
	}

	/** 关键字提取 */
	public static String getKey(String name) {
		String str = name.substring(name.lastIndexOf('.') + 1);
		name = str.substring(0, 3).toLowerCase();
		return name += str.substring(3).replaceAll("Imp$", "");
	}

	/** 页次对应记录 */
	public static final int getOffet(int page, int max) {
		if (page <= 1) {
			return 0;
		}
		return (page - 1) * max;
	}

	public static final String getSid(long uid) {
		StringBuilder sb = new StringBuilder();
		long time = System.currentTimeMillis();
		if (time <= UUID_TIME) {
			time = (UUID_TIME + 1);
		}
		UUID_TIME = time;
		String fix = Long.toString(time, 36);
		sb.append(Long.toString(uid, 36).toUpperCase());
		int a = sb.length() + fix.length();
		for (int i = 15; i > a; i--) {
			sb.append('0');
		}
		return sb.append(fix.toUpperCase()).toString();
	}

	public static Map<String, String> getObject(Class<?> cls) {
		String key = cls.getName();
		Map<String, String> m = KEYS.get(key);
		if (m == null) {
			m = new HashMap<String, String>();
			for (Method method : cls.getMethods()) {
				if (method.getParameterTypes().length == 0) {
					String name = method.getName();
					if (name.startsWith("get")) {
						m.put(name.substring(3).toLowerCase(), name);
					} else if (name.startsWith("is")) {
						m.put(name.substring(2).toLowerCase(), name);
					}
				}
			} // 放入缓存信息
			m.remove("class");
			KEYS.put(key, m);
		}
		return m;
	}

	private static boolean isObject(Map<String, String> map, String text, int k) {
		for (String str : text.split(SEP[k])) {
			int a = str.indexOf("=");
			if (a != -1) {
				map.put(str.substring(0, a), str.substring(a + 1));
			}
		} // 解析基本信息
		return (map.size() > 0);
	}

	public static void setObject(Object obj, ApplicationContext ac) {
		for (Method method : obj.getClass().getMethods()) {
			String name = method.getName();
			if (method.getParameterTypes().length != 1 || !name.startsWith("set")) {
				continue;
			}
			if (name.length() > 6) {
				name = name.substring(3);
			} else {
				name = method.getParameterTypes()[0].getName();
			} // 键值
			String key = AUtils.getKey(name);
			try {
				if (ac.containsBean(key)) {
					method.invoke(obj, ac.getBean(key));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void setObject(Object obj, Map<String, Object> map) {
		for (Method m : obj.getClass().getMethods()) {
			String name = m.getName();
			if (m.getParameterTypes().length != 1 || !name.startsWith("set")) {
				continue;
			}
			if (name.length() > 6) {
				name = name.substring(3);
			} else {
				name = m.getParameterTypes()[0].getName();
			} // 键值
			try {
				Object args = map.get(getKey(name));
				if (args != null) {
					m.invoke(obj, args);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** 头部信息转换 */
	public static final String toHeader(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		try {
			toString(sb, map, 0);
			return (sb.length() <= 0) ? "" : sb.substring(1);
		} finally {
			sb.setLength(0);
		}
	}

	public static final String toHeader(Map<String, String> map, String act, String call) {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("act=").append(act).append(SEP[1]).append("call=").append(call);
			return toString(sb, map, 1).toString();
		} finally {
			sb.setLength(0);
		}
	}

	/** 头部流向转换 */
	public static final String toHeader(Map<String, String> map, String act, String call, long fid, String ta, String tb) {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("act=").append(act).append(SEP[1]).append("call=").append(call);
			sb.append(SEP[1]).append("fid=").append(fid);
			sb.append(SEP[1]).append("fta=").append(ta);
			sb.append(SEP[1]).append("ftb=").append(tb);
			return toString(sb, map, 1).toString();
		} finally {
			sb.setLength(0);
		}
	}

	public static final Map<String, String> toMap(Map<String, String> map, String body, int k) {
		String key, ts[] = body.split(SEP[k]);
		for (String str : ts) {
			int beg = str.indexOf("=");
			if (beg != -1) {
				key = str.substring(0, beg).toLowerCase();
				map.put(key, str.substring(beg + 1));
			}
		}
		return map;
	}

	public static final Map<String, Object> toMap(Map<String, Object> map, Object obj) {
		if (obj instanceof List) {
			List<?> fs = (List<?>) obj;
			StringBuilder sb = toString(new StringBuilder(), fs, 2);
			if (sb.length() > 0) {
				map.put("list", sb.toString());
			}
		} else if (obj instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> m = (Map<String, Object>) obj;
			map.putAll(m);
		} else if (obj instanceof Set) {
			Set<?> set = (Set<?>) obj;
			StringBuilder sb = new StringBuilder();
			for (Object v : set) {
				sb.append(SEP[2]).append(v);
			}
			map.put("list", sb.substring(1));
		} else {
			Class<?> cls = obj.getClass();
			Iterator<Entry<String, String>> it = getObject(cls).entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				try {
					Object a = cls.getMethod(e.getValue()).invoke(obj);
					if (a != null)
						map.put(e.getKey(), a);
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		}
		return map;
	}

	public static final Map<String, String> toMap(String body, int k) {
		return toMap(new HashMap<String, String>(), body, k);
	}

	/** 主体参数转换 */
	public static final Map<String, String> toParameter(Map<String, String> map, String body) {
		return toMap(map, body, 1);
	}

	public static final String toString(Map<?, ?> map) {
		if (map == null || map.size() <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		try {
			return toString(sb, map, 1).substring(1);
		} finally {
			sb.setLength(0);
		}
	}

	public static final String[] toString(String body, int k) {
		if (body == null) {
			return null;
		} else {
			return body.split(SEP[k], -1);
		}
	}

	public static final StringBuilder toString(StringBuilder sb, List<?> ls, int j) {
		if (ls.size() >= 1) {
			Class<?> cls = ls.get(0).getClass();
			if (Pattern.matches("(?i)(Long|String|Integer)", cls.getSimpleName())) {
				sb.append(cls.getSimpleName());
				for (Object v : ls) {
					sb.append(SEP[j]).append(v);
				}
			} else {
				boolean add = false;
				Map<String, String> m = getObject(cls);
				for (String key : m.keySet()) {
					if (add) {
						sb.append(",").append(key);
					} else {
						sb.append(key);
						add = true;
					}
				} // 加载数据信息
				for (Object o : ls) {
					add = false;
					sb.append(SEP[j]);
					Iterator<String> st = m.values().iterator();
					while (st.hasNext()) {
						if (add) {
							sb.append(SEP[j + 1]);
						} else {
							add = true;
						} // add object
						try {
							sb.append(cls.getMethod(st.next()).invoke(o));
						} catch (Exception x) {
							x.printStackTrace();
						}
					}
				}
			}
		}
		return sb;
	}

	private static final StringBuilder toString(StringBuilder sb, Map<?, ?> map, int k) {
		Iterator<?> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<?, ?> e = (Map.Entry<?, ?>) it.next();
			sb.append(SEP[k]).append(e.getKey()).append('=');
			if (e.getValue() instanceof Map) {
				StringBuilder buf = toString(new StringBuilder(), (Map<?, ?>) e.getValue(), (k + 1));
				if (buf.length() >= 1) {
					sb.append(buf.substring(1));
				}
			} else if (e.getValue() instanceof List) {
				List<?> fs = (List<?>) e.getValue();
				toString(sb, fs, (k + 1));
			} else {
				sb.append(e.getValue());
			}
		}
		return sb;
	}

	public static BigDecimal toDeciml(String str) {
		return toDeciml(str, BigDecimal.ZERO);
	}

	public static BigDecimal toDeciml(String str, BigDecimal def) {
		if (str == null) return def;
		try {
			return new BigDecimal(str);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static float toFloat(String str) {
		return toFloat(str, 0f);
	}

	public static float toFloat(String str, float def) {
		if (str == null) return def;
		try {
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static double toDouble(String str) {
		return toDouble(str, 0f);
	}

	public static double toDouble(String str, double def) {
		if (str == null) return def;
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static int toInt(String str) {
		return toInt(str, 0);
	}

	public static int toInt(String str, int def) {
		if (str == null) return def;
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static long toLong(String str) {
		return toLong(str, 0);
	}

	public static long toLong(String str, long def) {
		if (str == null) return def;
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/** 单个对象转换 */
	@SuppressWarnings("unchecked")
	public static <E> E toObject(Class<E> cls, Map<String, String> map) {
		try {
			Object obj = Class.forName(cls.getName()).newInstance();
			return (E) convert(obj, map);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SecurityException e) {
			return null;
		}
	}

	/** 单个对象转换 */
	@SuppressWarnings("unchecked")
	public static <E> E toObject(Class<E> cls, String str) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			Object obj = Class.forName(cls.getName()).newInstance();
			if (str == null) {
				// Ignored
			} else if (isObject(map, str, 1)) {
				convert(obj, map, 1);
			}
			return (E) obj;
		} catch (Exception e) {
			return null;
		} finally {
			map.clear();
		}
	}

	/** 列表对象转换 */
	public static <E> List<E> toObjects(Class<E> cls, String str) {
		return toObjects(cls, str, 1);
	}

	@SuppressWarnings("unchecked")
	public static <E> List<E> toObjects(Class<E> cls, String str, int k) {
		List<E> ls = new ArrayList<E>();
		if (str == null) return ls;
		String ts[] = str.split(SEP[k]);
		if (ts.length <= 1) {
			return ls;
		}
		Map<String, String> map = null;
		try {
			if (cls.equals(String.class)) {
				for (int i = 1; i < ts.length; i++) {
					ls.add((E) ts[i]);
				}
			} else if (cls.equals(Long.class)) {
				for (int i = 1; i < ts.length; i++) {
					Object obj = toLong(ts[i]);
					ls.add((E) obj);
				}
			} else if (cls.equals(Integer.class)) {
				for (int i = 1; i < ts.length; i++) {
					Object obj = toInt(ts[i]);
					ls.add((E) obj);
				}
			} else {
				String ks[] = ts[0].toLowerCase().split(",");
				int t = (k + 1), max = ks.length;
				map = new HashMap<String, String>(max);
				for (int i = 1; i < ts.length; i++) {
					String[] s = ts[i].split(SEP[t], max);
					for (int j = 0; j < max; j++) {
						map.put(ks[j], s[j]);
					} // 创建新数据对象
					Object obj = Class.forName(cls.getName()).newInstance();
					ls.add((E) convert(obj, map, t));
					map.clear();
				}
			}
		} catch (Exception e) {
			// Ignored
		} finally {
			if (map != null) map.clear();
		}
		return ls;
	}
}
