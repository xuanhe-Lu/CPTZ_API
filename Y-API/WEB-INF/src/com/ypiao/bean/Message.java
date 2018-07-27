package com.ypiao.bean;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.ypiao.util.AState;
import com.ypiao.util.AUtils;

public class Message implements AState, Serializable {

	private static final long serialVersionUID = -6932702044754706862L;

	private static BlockingQueue<Message> SYSM = new LinkedBlockingQueue<Message>(12);

	public static final Message get() {
		Message msg = SYSM.poll();
		if (msg == null) {
			msg = new Message(API_OK);
		} else {
			msg.clear(API_OK);
		}
		return msg;
	}

	private StringBuilder sb = new StringBuilder();

	private int[] arr_First = new int[10];

	private BufferedImage image;

	private File file;

	private String msg;

	private String act;

	private int code = API_OK;

	private int length = 0;

	private int pos = 0;

	public Message() {
		this(API_OK);
	}

	public Message(int code) {
		this.code = code;
	}

	public void clear() {
		this.clear(API_OK);
	}

	public void clear(int code) {
		this.sb.setLength(0);
		if (length > 2048) {
			sb.trimToSize();
		}
		for (int i = 0; i <= pos; i++) {
			this.arr_First[i] = 0;
		}
		this.code = code;
		this.pos = 0;
		this.msg = null;
	}

	/** 返回顶级 */
	public Message backer() {
		while (pos > 0) {
			this.arr_First[pos] = 0;
			this.pos -= 1;
		}
		return this;
	}

	/** 关闭层级 */
	public Message close() {
		if (pos > 0) {
			this.arr_First[pos] = 0;
			this.pos -= 1;
		}
		return this;
	}

	/** 关闭2次 */
	public Message closer() {
		return this.close().close();
	}

	/** 结束，释放资源 */
	public void destroy() {
		this.clear();
		this.image = null;
	}

	/** 增加数组分隔 */
	public void formater() {
		int j = (this.pos - 1);
		if (j == 0) {
			sb.append(AUtils.SEP[1]);
			this.pos += 1;
		} else if (this.arr_First[j] > 0) {
			sb.append(AUtils.SEP[j]);
			this.arr_First[pos] = 0;
		}
	}

	/** 放入缓存 */
	public void offer() {
		this.destroy();
		SYSM.offer(this);
	}

	public void reset(int code) {
		this.clear(code);
	}

	private StringBuilder getBuilder() {
		if (this.arr_First[pos]++ > 0) {
			return sb.append(AUtils.SEP[pos]);
		} else {
			return sb;
		}
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
	}

	public int getCode() {
		return code;
	}

	public Message setCode(int code) {
		this.code = code;
		return this;
	}

	public Message add(String key, boolean value) {
		this.getBuilder().append(key).append(':').append(value ? 1 : 0);
		return this;
	}

	public Message add(String key, Object value) {
		this.getBuilder().append(key).append(':').append(value);
		return this;
	}

	public Message addError(String error) {
		return this.addError(API_ERROR, error);
	}

	public Message addError(int code, String error) {
		this.reset(code); // 重置信息
		return this.add("message", error);
	}

	public Message addMessage(String message) {
		this.reset(API_OK); // 重置信息
		return this.add("message", message);
	}

	public Message addList(String key) {
		return this.addObject("list", key);
	}

	public Message addObject() {
		return this.addObject("obj", null);
	}

	public Message addObject(Object obj) {
		this.addObject();
		Class<?> cls = obj.getClass();
		Iterator<Entry<String, String>> it = AUtils.getObject(cls).entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> e = it.next();
			try {
				Object a = cls.getMethod(e.getValue()).invoke(obj);
				if (a != null) {
					this.getBuilder().append(e.getKey()).append('=').append(a);
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		return this;
	}

	public Message addObject(String key, Object value) {
		while (this.pos > 0) {
			this.arr_First[this.pos--] = 0;
		} // 构建输出信息
		this.getBuilder().append(key).append(':');
		this.pos = 1; // 索引指针
		if (value == null) {
			this.arr_First[pos] = 0;
		} else {
			this.arr_First[pos] = 1;
			this.sb.append(value);
		}
		return this;
	}

	public Message addOver(long time) {
		return this.closer().add("time", time).add("over", true);
	}
	/** 新增标签 */
	public Message addTag() {
		this.getBuilder();
		this.pos += 1;
		return this;
	}

	/** 点位分隔 */
	public Message append() {
		this.getBuilder();
		return this;
	}

	public Message append(String str) {
		if (str == null) {
			str = "";
		}
		return appends(str);
	}

	public Message appends(String str) {
		this.getBuilder().append(str);
		return this;
	}

	public Message append(boolean v) {
		this.getBuilder().append(v);
		return this;
	}

	public Message append(double v) {
		this.getBuilder().append(v);
		return this;
	}

	public Message append(float v) {
		this.getBuilder().append(v);
		return this;
	}

	public Message append(int v) {
		this.getBuilder().append(v);
		return this;
	}

	public Message append(long v) {
		this.getBuilder().append(v);
		return this;
	}

	public Message append(Object v) {
		this.getBuilder().append(v);
		return this;
	}

	public Message append(String key, int value) {
		this.getBuilder().append(key).append('=').append(value);
		return this;
	}

	public Message append(String key, Object value) {
		this.getBuilder().append(key).append('=').append(value);
		return this;
	}

	public Message append(String key, String value) {
		StringBuilder sb = this.getBuilder().append(key).append('=');
		if (value != null)
			sb.append(value);
		return this;
	}

	/** 缓存信息添加 */
	public void addMsg(String msg) {
		sb.setLength(0);
		sb.append(msg);
	}

	/** 缓存信息复制 */
	public void setMsg(String msg) {
		this.msg = msg;
		sb.setLength(0);
	}

	/** 获取当前封闭信息 */
	public String getString() {
		return msg;
	}

	/** 缓存信息长度 */
	public int size() {
		return sb.length();
	}

	/** 分批构建值 */
	public String doString() {
		return this.doString(this.getAct());
	}

	public String doString(String act) {
		if (msg == null) {
			if (act == null) {
				// Ignored
			} else if (sb.length() > 0) {
				sb.insert(0, AUtils.SEP[0]).insert(0, act).insert(0, "act:");
			} else {
				sb.append("act:").append(act);
			}
			this.length = sb.length();
			this.msg = sb.toString();
			sb.delete(0, length);
		}
		return msg;
	}

	public String toString() {
		return this.toString(code, 0, act);
	}

	public String toString(int rev) {
		return this.toString(code, rev, act);
	}

	public String toString(int code, int ref, String act) {
		if (msg == null) {
			if (sb.length() > 0) {
				sb.insert(0, AUtils.SEP[0]).insert(0, act).insert(0, "act:");
				if (ref > 100) {
					sb.insert(0, AUtils.SEP[0]).insert(0, ref).insert(0, "ref:");
				}
				sb.insert(0, AUtils.SEP[0]).insert(0, code).insert(0, "code:");
			} else {
				sb.append("code:").append(code);
				if (ref > 100) {
					sb.append(AUtils.SEP[0]).append("ref:").append(ref);
				}
				sb.append(AUtils.SEP[0]).append("act:").append(act);
			}
			this.length = sb.length();
			this.msg = sb.toString();
			sb.delete(0, length);
		}
		return msg;
	}
}
