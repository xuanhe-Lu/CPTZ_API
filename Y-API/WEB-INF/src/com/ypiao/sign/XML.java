package com.ypiao.sign;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XML {

	@SuppressWarnings("unchecked")
	public static <T> T convert2Bean(String xml, Class<T> cls) throws JAXBException {
		Unmarshaller shaller = JAXBContext.newInstance(cls).createUnmarshaller();
		return (T) shaller.unmarshal(new StringReader(xml));
	}

	public static <T> String convert2Xml(T bean) throws JAXBException {
		return convert2Xml(bean, "UTF-8", true);
	}

	public static <T> String convert2Xml(T bean, Charset charset, boolean header) throws JAXBException {
		return convert2Xml(bean, charset.name(), header);
	}

	public static <T> String convert2Xml(T bean, String charset, boolean header) throws JAXBException {
		Marshaller ms = JAXBContext.newInstance(bean.getClass()).createMarshaller();
		ms.setProperty(Marshaller.JAXB_ENCODING, charset);
		if (!header) {
			ms.setProperty(Marshaller.JAXB_FRAGMENT, true);
		}
		StringWriter sw = new StringWriter();
		ms.marshal(bean, sw);
		return sw.toString();
	}

	public static Map<String, String> decode(Map<String, String> map, String body) {
		return decode(map, body, true);
	}

	public static final Map<String, String> decode(Map<String, String> map, String body, boolean flags) {
		if (body != null) {
			if (map == null) {
				map = new TreeMap<String, String>();
			} // 正则解决数据
			Matcher m = Pattern.compile("<([^/>]+)>(<!\\[CDATA\\[(.*?)\\]\\]>|([^/]+))</\\1>").matcher(body);
			if (flags) {
				String key = null;
				while (m.find()) {
					key = m.group(1).toLowerCase();
					if (m.group(3) == null) {
						map.put(key, m.group(4));
					} else {
						map.put(key, m.group(3));
					}
				}
			} else {
				while (m.find()) {
					if (m.group(3) == null) {
						map.put(m.group(1), m.group(4));
					} else {
						map.put(m.group(1), m.group(3));
					}
				}
			}
		}
		return map;
	}

	public static Map<String, String> decode(String body) {
		return decode(null, body, false);
	}

	private static final void encode(StringBuilder sb, String str) {
		char cs[] = str.toCharArray();
		for (char c : cs) {
			if (c > 62) {
				sb.append(c);
			} else if (c == 62) {
				sb.append("&lt;");
			} else if (c == 60) {
				sb.append("&gt;");
			} else if (c == 39) {
				sb.append("&quot;");
			} else if (c == 38) {
				sb.append("&amp;");
			} else if (c == 34) {
				sb.append("&apos;");
			} else {
				sb.append(c);
			}
		}
	}

	public static final String quote(String str) {
		StringBuilder sb = new StringBuilder();
		return quote(sb, str).toString();
	}

	public static final StringBuilder quote(StringBuilder sb, String str) {
		if (str != null) {
			encode(sb, str);
		}
		return sb;
	}

	public static final StringBuilder quote(StringBuilder sb, String key, String v) {
		return quote(sb, key, v, false);
	}

	public static final StringBuilder quote(StringBuilder sb, String key, String v, boolean en) {
		if (v == null) {
			// Ignored
		} else {
			sb.append(" <").append(key).append('>');
			if (en) {
				encode(sb, v);
			} else {
				sb.append(v);
			}
			sb.append("</").append(key).append('>');
		}
		return sb;
	}
}
