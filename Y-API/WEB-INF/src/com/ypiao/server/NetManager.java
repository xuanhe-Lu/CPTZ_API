package com.ypiao.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import com.sunsw.net.IoSession;
import com.sunsw.net.core.ByteOutputStream;
import com.ypiao.bean.Manager;
import com.ypiao.bean.Message;
import com.ypiao.bean.UserSession;
import com.ypiao.util.AUtils;

public class NetManager extends Manager {

	private static final long serialVersionUID = -6981032640637824381L;

	private static final int MAX_SIZE = 50 * 1024;

	public static NetManager getBean(NetFuture future, IoSession session) {
		NetManager mgr = NetNode.getManager();
		mgr._hash = null;
		mgr.clazz = 0;
		mgr.action = "index";
		mgr.destroy = 0; // 销毁计数
		mgr.index = 0; // 发送次数索引
		mgr.transmit = true;
		mgr.future = future;
		mgr.session = session;
		mgr.clear();
		return mgr;
	}

	private int ack = 0;

	private int att = 0;

	private int cls = 0;

	private int destroy = 0;

	private int index = 0;

	private boolean transmit = true;

	private NetFiler filer;

	private NetFuture future;

	private Locale locale;

	private IoSession session;

	private String x_forwarded_for;

	/** 增加延时销毁次数 */
	public final NetManager add() {
		this.destroy++;
		return this;
	}

	/** 重置应答计数 */
	private final void clear() {
		this.ack = 0; // 默认无操作
		this.att = 0;
		this.cls = 0;
	}

	/** 结束，释放资源 */
	public final void destroy() {
		if (this.destroy-- <= 0) {
			this._hash = null;
			this.cache.clear();
			this.heads.clear();
			this.message.destroy();
			this.session = null;
			if (filer == null) {
				// Ignored
			} else {
				filer.destroy();
				filer = null;
			}
			NetNode.offer(this);
		}
	}

	public void formater() {
		this.message.formater();
		this.index += 1;
	}

	public final int getAck() {
		return ack;
	}

	public final int getAtt() {
		return att;
	}

	public final int getCls() {
		return cls;
	}

	public final long getSid() {
		return session.getId();
	}

	public final String getForwarded() {
		if (x_forwarded_for == null) {
			x_forwarded_for = cache.get("x-forwarded-for");
			if (x_forwarded_for == null) {
				x_forwarded_for = getRemoteAddress().getAddress().getHostAddress();
			}
		}
		return x_forwarded_for;
	}

	public final InetSocketAddress getRemoteAddress() {
		return (InetSocketAddress) session.getRemoteAddress();
	}

	public final Locale getLocale() {
		if (locale == null) {
			locale = (Locale) session.getAttribute("locale");
			if (locale == null) {
				locale = Locale.CHINA;
			}
		}
		return locale;
	}

	public final UserSession getSession() {
		return (UserSession) session.getUseSession();
	}

	public final void setSession(UserSession us) {
		session.setUseSession(us); // 缓存信息
	}

	public final File getFile() {
		if (filer == null) {
			return null;
		} else {
			return filer.getFile();
		}
	}

	public final boolean isFile() {
		if (filer == null) {
			return false;
		} else {
			return filer.isFile();
		}
	}

	public final void setFile(File file) {
		if (filer == null) {
			// Ignored
		} else {
			filer.setFile(null);
		}
	}

	public final void sendReply() throws IOException {
		if (this.isTransmit()) {
			this.sendReply(false);
		}
	}

	public final boolean sendReplys() throws IOException {
		return this.sendReply(MAX_SIZE);
	}

	public final boolean sendReply(int max) throws IOException {
		if (index == 0) {
			if (max > 1) {
				index += 1;
			} else if (max == -1) {
				this.sendReply(false);
			} else {
				this.setTransmit(max == 0);
				this.getMessage().clear();
			}
			return true;
		} else if (index >= max) {
			this.sendReply(max == 0);
			index = (max <= 1) ? 0 : 1;
			return true;
		} else {
			index++;
			return false;
		}
	}

	public final void sendReply(Message msg) throws IOException {
		SendFuture future = (SendFuture) session.getResponse();
		if (future == null) {
			session.getProcessor().remove(session);
		} else if (msg.getImage() == null) {
			future.sender(session, cls, ack, code, msg, msg.getFile());
		} else {
			ByteOutputStream bos = new ByteOutputStream(MAX_SIZE);
			ImageIO.write(msg.getImage(), "png", bos);
			future.sender(session, cls, ack, code, msg, bos);
		}
	}

	public final void sendReply(boolean transmit) throws IOException {
		Message msg = this.getMessage();
		try {
			this.setTransmit(transmit);
			this.sendReply(msg);
		} finally {
			msg.clear();
		}
	}

	public boolean isTransmit() {
		return transmit;
	}

	public void setTransmit(boolean transmit) {
		this.transmit = transmit;
	}

	public NetFiler getFiler() {
		return filer;
	}

	public NetFiler makeFiler() {
		if (filer == null) {
			filer = new NetFiler();
		}
		return filer;
	}

	public NetFuture getFuture() {
		return future;
	}

	public void setFuture(NetFuture future) {
		this.future = future;
	}

	public void setClass(int cls, int ack) {
		int c = Math.abs(cls);
		this.att = cls;
		this.ack = ack;
		this.cls = c;
		this.code = c;
		this.clazz = (c / 10);
	}

	public void setClass(int cls, int ack, int code) {
		int c = Math.abs(cls);
		this.att = cls;
		this.ack = ack;
		this.cls = c;
		this.code = code;
		this.clazz = c;
	}

	public void setRever() {
		String rev = this.getParameter("rev");
		if (rev == null) {
			// Ignored
		} else {
			int code = AUtils.toInt(rev);
			if (code > 100) {
				this.code = code;
			}
		}
	}

	private void setHeader(Map<String, String> m) {
		this.action = m.get("act");
		this.message.setAct(m.get("call"));
	}

	/** 解析头部信息 */
	public void setHeader(String head) {
		if (head == null) {
			// Ignored
		} else {
			Matcher m = Pattern.compile(AUtils.REGEX).matcher(head);
			while (m.find()) {
				this.heads.put(m.group(1).toLowerCase(), m.group(2));
			} // 处理动作信息
			this.setHeader(heads);
		}
	}

	/** 主体信息解码 */
	public void setBody(String body) {
		this.body = body;
		AUtils.toParameter(cache, body);
		System.out.println(this.getRemoteAddress() + "\t" + att + "\tA\t" + action + "=" + cache);
	}

	/** 带头部主体信息解码 */
	public void setBodys(String body) {
		this.setBody(body);
		this.setHeader(cache);
	}
}
