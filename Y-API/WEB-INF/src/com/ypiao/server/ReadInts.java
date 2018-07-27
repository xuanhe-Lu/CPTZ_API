package com.ypiao.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import com.sunsw.net.DataInputStream;
import com.sunsw.net.IoSession;
import com.sunsw.net.IoFilter.NextFilter;

public class ReadInts implements NetFuture {

	private static final int CODE_MAX = 1000000;

	private static final int CODE_MIN = -1000000;

	private static final int SIZE = (INT_SIZE + INT_SIZE);

	private NetFuture future;

	public ReadInts(NetFuture future) {
		this.future = future;
	}

	private static boolean available(int code) {
		return (code < CODE_MAX && code > CODE_MIN);
	}

	private boolean changed(NextFilter filter, NetMessager mgr, IoSession session, DataInputStream dis, ByteBuffer dst, int cls, int key) throws IOException {
		if (cls == Integer.MIN_VALUE) {
			try {
				session.setSecretKey(key);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			mgr.setClass(cls, key);
			return future.doReader(filter, mgr, session, dis, dst);
		}
	}

	private boolean doNexter(NextFilter filter, NetManager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		if (available(mgr.getCode())) {
			return future.doReader(filter, mgr, session, dis, dst);
		} else {
			throw new IOException("Unsupported protocol!");
		}
	}

	@Override
	public boolean doReader(NextFilter filter, NetManager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		if (dis.isReady() && dst.remaining() >= SIZE) {
			mgr.setClass(dst.getInt(), dst.getInt());
			return this.doNexter(filter, mgr, session, dis, dst);
		} else if (dis.read(dst, SIZE)) {
			mgr.setClass(dis.getInt(), dis.getInt());
			dis.reset();
			return this.doNexter(filter, mgr, session, dis, dst);
		} else {
			mgr.setFuture(this);
			return false;
		}
	}

	public boolean doReader(NextFilter filter, NetMessager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		if (dis.isReady() && dst.remaining() >= SIZE) {
			int c = dst.getInt();
			int k = dst.getInt();
			return changed(filter, mgr, session, dis, dst, c, k);
		} else if (dis.read(dst, SIZE)) {
			int c = dis.getInt();
			int k = dis.getInt();
			dis.reset();
			return changed(filter, mgr, session, dis, dst, c, k);
		} else {
			mgr.setFuture(this);
			return false;
		}
	}

}
