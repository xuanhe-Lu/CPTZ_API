package com.ypiao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.ypiao.util.AState;

public class AjaxInfo implements AState, Serializable {

	private static final long serialVersionUID = 6159417900851757160L;

	private static final int SIZE = 10;

	private static List<AjaxInfo> CACHE = new ArrayList<AjaxInfo>(SIZE);

	private StringBuilder sb = new StringBuilder();

	private int[] arr_Array = new int[10];

	private int[] arr_First = new int[10];

	private String regex, body = null;

	private int index = 0;

	private boolean json = true;

	public static AjaxInfo getBean() {
		return getBean(true, false);
	}

	public static AjaxInfo getArray() {
		return getBean(true, true);
	}

	public static AjaxInfo getBean(boolean json) {
		return getBean(json, false);
	}

	public synchronized static AjaxInfo getBean(boolean json, boolean arr) {
		AjaxInfo ajax = null;
		if (CACHE.size() > 0) {
			ajax = CACHE.remove(0);
		} else {
			ajax = new AjaxInfo();
		} // 数据信息
		ajax.json = json;
		ajax.regex = null;
		ajax.setArray(arr);
		return ajax;
	}

	private static StringBuilder quote(StringBuilder sb, String str) {
		if (str == null) {
			return sb.append("\"\"");
		} // 转换特殊字符
		sb.append('"');
		char b = 0, cs[] = str.toCharArray();
		for (char c : cs) {
			switch (c) {
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '"':
				sb.append('\\').append(c);
				break;
			case '/':
				if (b == '<')
					sb.append('\\');
				sb.append(c);
				break;
			case '\\':
				sb.append('\\').append(c);
				break;
			case '\u2026': // 省略号
				sb.append(c);
				break;
			default:
				if (c < 32) {
					if (c < 16) {
						sb.append("\\u000");
					} else {
						sb.append("\\u00");
					}
					sb.append(Integer.toHexString(c));
				} else if (c < '\u00a0' && c >= '\u0080') {
					sb.append("\\u00").append(Integer.toHexString(c));
				} else if (c < '\u2100' && c >= '\u2000') {
					sb.append("\\u").append(Integer.toHexString(c));
				} else {
					sb.append(c);
				}
			}
			b = c;
		}
		return sb.append('"');
	}

	public AjaxInfo act(String act) {
		this.success(API_OK).append("act", act);
		return this;
	}

	/** 添加子数据 */
	public AjaxInfo add(String key) {
		this.getBuilder(key);
		this.index += 1;
		this.arr_Array[this.index] = 2;
		return this;
	}

	/** 添加子数组 */
	public AjaxInfo adds(String key) {
		this.getBuilder(key).append('[');
		this.index += 1;
		this.arr_Array[this.index] = 1; // 数组状态
		this.index += 1;
		return this;
	}

	public AjaxInfo addObject() {
		return this.success(API_OK).add("obj");
	}

	public AjaxInfo addList() {
		return this.adds("list");
	}

	/** 添加文本数组 */
	public AjaxInfo addText() {
		return this.adds("text");
	}

	/** 增加缓存项目 */
	public void addText(String key, String body) {
		this.getBuilder(key).append(body);
	}

	public AjaxInfo addKey(String key) {
		this.getBuilder(key);
		this.index += 1;
		this.arr_Array[this.index] = 3; // 数组状态
		return this;
	}

	public void addFailure() {
		this.body = "failure";
		this.json = false;
	}

	public void addFailure(String msg) {
		sb.setLength(0);
		sb.append("<xml>");
		sb.append("<return_code><![CDATA[FAIL]]></return_code>");
		sb.append("<return_msg><![CDATA[").append(msg).append("]]></return_msg>");
		sb.append("</xml>");
		this.body = sb.toString();
		this.json = false;
	}

	public void addSuccess() {
		this.body = "success";
		this.json = false;
	}

	public void addSuccess(String msg) {
		sb.setLength(0);
		sb.append("<xml>");
		sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
		sb.append("<return_msg><![CDATA[").append(msg).append("]]></return_msg>");
		sb.append("</xml>");
		this.body = sb.toString();
		this.json = false;
	}

	public void addError(String error) {
		this.addError(API_ERROR, error);
	}

	public void addError(int code, String error) {
		this.failure(code).append("message", error);
	}

	public void addMessage(String message) {
		this.addMessage(API_OK, message);
	}

	public void addMessage(int code, String message) {
		this.success(code).append("message", message);
	}

	public AjaxInfo append(BigDecimal value) {
		this.getBuilder().append('"').append(value).append('"');
		return this;
	}

	/** 单项加载 */
	public AjaxInfo append(boolean value) {
		this.getBuilder().append(value);
		return this;
	}

	public AjaxInfo append(float value) {
		this.getBuilder().append(value);
		return this;
	}

	public AjaxInfo append(int value) {
		this.getBuilder().append(value);
		return this;
	}

	public AjaxInfo append(long value) {
		this.getBuilder().append(value);
		return this;
	}

	public AjaxInfo append(String value) {
		quote(this.getBuilder(), value);
		return this;
	}

	public AjaxInfo append(String key, BigDecimal value) {
		this.getBuilder(key).append('"').append(value).append('"');
		return this;
	}

	public AjaxInfo append(String key, BigDecimal str, String value) {
		this.getBuilder(key).append('"').append(str).append(value).append('"');
		return this;
	}

	public AjaxInfo append(String key, long day, String unit) {
		this.getBuilder(key).append('"').append(day).append(unit).append('"');
		return this;
	}

	public AjaxInfo append(String key, boolean value) {
		this.getBuilder(key).append(value);
		return this;
	}

	public AjaxInfo append(String key, float value) {
		this.getBuilder(key).append(value);
		return this;
	}

	public AjaxInfo append(String key, double value) {
		this.getBuilder(key).append(value);
		return this;
	}

	public AjaxInfo append(String key, int value) {
		this.getBuilder(key).append(value);
		return this;
	}

	public AjaxInfo append(String key, long value) {
		this.getBuilder(key).append(value);
		return this;
	}

	public AjaxInfo append(String key, String value) {
		quote(this.getBuilder(key), value);
		return this;
	}

	public AjaxInfo append(String key, String str, int value) {
		this.getBuilder(key).append('"').append(str).append('-').append(value).append('"');
		return this;
	}

	public AjaxInfo append(String key, String str, String value) {
		this.getBuilder(key).append('"').append(str).append(value).append('"');
		return this;
	}

	public AjaxInfo append(String key, float xs, float ys) {
		this.getBuilder(key).append('[').append(xs).append(',').append(ys).append(']');
		return this;
	}

	public AjaxInfo append(String key, double xs, double ys) {
		this.getBuilder(key).append('[').append(xs).append(',').append(ys).append(']');
		return this;
	}

	/** 不检测加载 */
	public AjaxInfo appends(String value) {
		if (value == null) {
			this.getBuilder().append('"').append('"');
		} else {
			this.getBuilder().append('"').append(value).append('"');
		}
		return this;
	}

	/** 不检测加载 */
	public AjaxInfo appends(String key, String value) {
		if (value == null) {
			this.getBuilder(key).append('"').append('"');
		} else {
			this.getBuilder(key).append('"').append(value).append('"');
		}
		return this;
	}

	public AjaxInfo data() {
		return this.success().add("data");
	}

	public AjaxInfo data(int code) {
		return this.setCode(code).data();
	}

	public void data(String key, boolean value) {
		this.data().append(key, value);
	}

	public void data(String key, long value) {
		this.data().append(key, value);
	}

	public void data(String key, String value) {
		this.data().append(key, value);
	}

	/** 当前级别数组 */
	public AjaxInfo datas(int code) {
		return this.success(code).adds("data");
	}

	public AjaxInfo datas(String key) {
		return this.success().adds(key);
	}

	/** 数据是否存在 */
	public void exist(boolean value) {
		this.success().append("isExist", value);
	}

	public void exist(boolean value, String message) {
		this.success().append("isExist", value).append("message", message);
	}

	public void clear() {
		for (int i = index; i >= 0; i--) {
			this.arr_Array[i] = 0;
			this.arr_First[i] = 0;
		}
		this.index = 0;
		this.body = null;
		this.regex = null;
		this.sb.setLength(0);
	}

	public AjaxInfo close() {
		int a = this.arr_Array[index];
		if (this.arr_First[index] > 0) {
			if (a == 1 || a == 3) {
				this.sb.append(']');
			} else {
				this.sb.append('}');
			}
		} else if (a == 3) {
			this.sb.append("[]");
		} // 是否为
		this.arr_Array[index] = 0;
		this.arr_First[index] = 0;
		this.index -= 1;
		if (this.arr_Array[index] == 1) {
			this.arr_Array[index] = 0;
			this.index -= 1;
			this.sb.append(']');
		} else {
			this.arr_Array[index] = 0;
		}
		return this;
	}

	/** 关闭2次 */
	public AjaxInfo closer() {
		return this.close().close();
	}

	public void destroy() {
		this.clear();
		if (this.sb.capacity() > 1024) {
			this.sb.trimToSize();
		} // put cache
		if (CACHE.size() < SIZE) {
			CACHE.add(this);
		}
	}

	/** 键值分隔 Key-Value */
	public void formater() {
		if (this.arr_First[index] == 0) {
			this.arr_Array[index] = 2;
		} else {
			this.arr_First[index] = 0;
			this.sb.append("},");
		}
	}

	/** 数组分隔 Value */
	public void formates() {
		if (this.arr_First[index] == 0) {
			this.arr_Array[index] = 1;
		} else {
			this.arr_First[index] = 0;
			this.sb.append("],");
		}
	}

	private StringBuilder getBuilder() {
		if (this.arr_First[index]++ > 0) {
			return this.sb.append(',');
		} else if (this.arr_Array[index] == 1) {
			return this.sb.append('[');
		} else if (this.arr_Array[index] == 3) {
			return this.sb.append('[');
		} else {
			return this.sb.append('{');
		}
	}

	public StringBuilder getBuilder(String key) {
		return this.getBuilder().append('"').append(key).append('"').append(':');
	}

	/** 数组单项添加 */
	public AjaxInfo put(String key, String value) {
		this.formates();
		quote(this.getBuilder().append('"').append(key).append('"').append(','), value);
		return this;
	}

	public void setArray(boolean isArray) {
		if (isArray) {
			this.arr_Array[index] = 1;
			this.index += 1;
			this.sb.append('[');
		} else {
			this.arr_Array[index] = 0;
		}
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public AjaxInfo setCode(int code) {
		this.clear(); // code
		return this.append("code", code);
	}

	public AjaxInfo setData(int code, String data) {
		return this.setCode(code).append("data", data);
	}

	public AjaxInfo setError(String error) {
		return this.setError(API_ERROR, error);
	}

	public AjaxInfo setError(int code, String error) {
		return this.setCode(code).append("message", error);
	}

	public AjaxInfo setMessage(String message) {
		return this.setMessage(API_OK, message);
	}

	public AjaxInfo setMessage(int code, String message) {
		return this.setCode(code).append("message", message);
	}

	public AjaxInfo setText(int code, String text) {
		return this.setCode(code).append("text", text);
	}

	public AjaxInfo setText(int code, String message, String text) {
		return this.setCode(code).append("message", message).append("text", text);
	}

	public void setTotal(long total) {
		this.setTotal(total, "data");
	}

	public void setTotal(long total, String key) {
		this.getBuilder("totalCount").append(total);
		this.adds(key); // 添加子数组
	}

	/** 失败状态 */
	public AjaxInfo failure() {
		return this.append("success", false);
	}

	public AjaxInfo failure(int code) {
		return this.setCode(code).failure();
	}

	/** 成功状态 */
	public AjaxInfo success() {
		return this.success(true);
	}

	public AjaxInfo success(boolean value) {
		return this.append("success", value);
	}

	public AjaxInfo success(int code) {
		return this.setCode(code).success();
	}

	public String getContentType() {
		if (this.json) {
			return "application/json";
		} else {
			return "text/html";
		}
	}

	public String getString() {
		try {
			return toString();
		} finally {
			this.destroy();
		}
	}

	public String toString() {
		if (body == null) {
			if (this.json) {
				boolean result = true;
				for (int i = index; i >= 0; i--) {
					if (this.arr_Array[i] == 1) {
						if (this.arr_First[i] > 0) {
							this.sb.append(']');
						} else if (result) {
							int a = this.sb.length();
							if (this.sb.charAt(a - 1) == ':') {
								this.sb.append('[');
							}
							this.sb.append(']');
							result = false;
						}
					} else if (this.arr_Array[i] == 3) {
						if (this.arr_First[i] > 0) {
							this.sb.append(']');
						} else {
							this.sb.append('[').append(']');
						}
						result = true;
					} else if (this.arr_First[i] > 0) {
						this.sb.append('}');
						result = true;
					}
					this.arr_Array[i] = 0;
					this.arr_First[i] = 0;
				}
				this.index = 0;
				if (sb.length() <= 0) {
					sb.append('{').append('}');
				} // 替换替换
				if (regex == null) {
					this.body = sb.toString();
				} else {
					this.body = regex.replace("${json}", sb.toString());
				}
			} else {
				this.body = "failure";
			}
		}
		return body;
	}
}
