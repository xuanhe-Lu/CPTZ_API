package com.ypiao.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.sunsw.net.DataInputStream;
import com.sunsw.net.IoSession;
import com.sunsw.net.IoFilter.NextFilter;

public class Read5Int implements NetFuture {

	private static final int CODE_MAX = 1000000;

	private static final int CODE_MIN = -1000000;

	private static final int SIZE = (INT_SIZE + INT_SIZE + LONG_SIZE);

	private NetFuture future;

	public Read5Int(NetFuture future) {
		this.future = future;
	}

	private boolean available(int code) {
		return (code < CODE_MAX && code > CODE_MIN);
	}

	private boolean doNexter(NextFilter filter, NetManager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		if (this.available(mgr.getCode())) {
			return future.doReader(filter, mgr, session, dis, dst);
		} else {
			throw new IOException("Unsupported protocol = " + mgr.getCode());
		}
	}
	@Override
	public boolean doReader(NextFilter filter, NetManager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		if (dis.isReady()) {
			if (dst.remaining() >= SIZE) {
				mgr.setClass(dst.getInt(), dst.getInt());
				mgr.setTime(dst.getLong());
				return this.doNexter(filter, mgr, session, dis, dst);
			}
		}
		if (dis.read(dst, SIZE)) {
			mgr.setClass(dis.getInt(), dis.getInt());
			mgr.setTime(dis.getLong());
			dis.reset();
			return this.doNexter(filter, mgr, session, dis, dst);
		} else {
			mgr.setFuture(this);
			return false;
		}
	}

	public boolean doReader(NextFilter filter, NetMessager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		if (dis.isReady()) {
			if (dst.remaining() >= SIZE) {
				mgr.setClass(dst.getInt(), dst.getInt());
				return future.doReader(filter, mgr, session, dis, dst);
			}
		}
		if (dis.read(dst, SIZE)) {
			mgr.setClass(dis.getInt(), dis.getInt());
			dis.reset();
			return future.doReader(filter, mgr, session, dis, dst);
		} else {
			mgr.setFuture(this);
			return false;
		}
	}

}
