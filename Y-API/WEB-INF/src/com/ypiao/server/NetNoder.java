package com.ypiao.server;

import java.io.File;
import java.io.IOException;
import com.sunsw.net.IoSession;
import com.ypiao.bean.Message;
import com.ypiao.bean.Messager;
import com.ypiao.bean.NetService;

public class NetNoder implements NetService {

	private IoSession session;

	private SendFuture future;

	public NetNoder(IoSession session, SendFuture future) {
		this.session = session;
		this.future = future;
	}

	public void destroy() {
		this.future = null;
		this.session = null;
	}

	public long getId() {
		return session.getId();
	}

	public Messager send(int cls, int rev, Message msg) throws IOException {
		future.sender(session, cls, 0, rev, msg);
		return null;
	}

	public Messager send(int cls, int rev, Message msg, File file) throws IOException {
		future.sender(session, cls, 0, rev, msg, file);
		return null;
	}

	@Override
	public void sender(int cls, int rev, Message msg) throws IOException {
		future.sender(session, cls, 0, rev, msg);
	}

	public void sender(int cls, int ack, int rev, Message msg) throws IOException {
		future.sender(session, cls, ack, rev, msg);
	}

	@Override
	public void sender(int cls, int rev, Message msg, File file) throws IOException {
		future.sender(session, cls, 0, rev, msg, file);
	}

	public void sender(int cls, int ack, int rev, Message msg, File file) throws IOException {
		future.sender(session, cls, ack, rev, msg, file);
	}

}
