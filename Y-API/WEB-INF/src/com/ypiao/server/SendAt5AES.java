package com.ypiao.server;

import java.io.File;
import java.io.IOException;
import org.commons.lang.RandomUtils;
import com.sunsw.net.DataOutputStream;
import com.sunsw.net.IoSession;
import com.sunsw.net.core.ByteOutputStream;
import com.ypiao.bean.Message;

public class SendAt5AES implements SendFuture {

	private static final int MIN = 1 << 6; // 64

	private static final int MAX = 1 << 7; // 128

	private static final void setKey(IoSession session, int key) {
		try {
			session.setSecretKey(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void send(IoSession session, int cls, int ack, String msg) throws IOException {
		synchronized (session) {
			DataOutputStream dos = session.getDataOutputStream();
			try {
				if (session.changedKey(MIN, MAX)) {
					int key = RandomUtils.randomNumeric(MAX, Integer.MAX_VALUE);
					dos.writeInt(Integer.MIN_VALUE);
					dos.writeInt(key);
					dos.flush(); // 下发密钥
					setKey(session, key);
				}
				dos.writeInt(cls);
				dos.writeInt(ack);
				dos.writeAES(msg);
				dos.flush();
			} finally {
				dos.close();
			}
		}
	}

	private void send(IoSession session, int cls, int ack, String msg, ByteOutputStream bos) throws IOException {
		synchronized (session) {
			DataOutputStream dos = session.getDataOutputStream();
			try {
				dos.writeInt(-cls);
				dos.writeInt(ack);
				dos.writeAES(msg);
				dos.writeFile(bos);
				dos.flush();
			} finally {
				dos.close();
			}
		}
	}

	private void send(IoSession session, int cls, int ack, String msg, File file) throws IOException {
		synchronized (session) {
			DataOutputStream dos = session.getDataOutputStream();
			try {
				dos.writeInt(-cls);
				dos.writeInt(ack);
				dos.writeAES(msg);
				dos.writeFile(file);
				dos.flush();
			} finally {
				dos.close();
			}
		}
	}

	public void sender(IoSession session, int cls, int ack, int rev, Message msg) throws IOException {
		int c = (cls == rev) ? 0 : cls;
		this.send(session, rev, ack, msg.toString(c));
	}

	public void sender(IoSession session, int cls, int ack, int rev, Message msg, ByteOutputStream bos) throws IOException {
		int c = (cls == rev) ? 0 : cls;
		if (bos == null) {
			this.send(session, rev, ack, msg.toString(c));
		} else {
			this.send(session, rev, ack, msg.toString(c), bos);
		}
	}

	public void sender(IoSession session, int cls, int ack, int rev, Message msg, File file) throws IOException {
		int c = (cls == rev) ? 0 : cls;
		if (file == null) {
			this.send(session, rev, ack, msg.toString(c));
		} else if (file.isFile()) {
			this.send(session, rev, ack, msg.toString(c), file);
		} else {
			this.send(session, rev, ack, msg.toString(c));
		}
	}
}
