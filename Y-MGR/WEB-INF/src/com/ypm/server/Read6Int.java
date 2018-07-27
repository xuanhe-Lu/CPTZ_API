package com.ypm.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.sunsw.net.DataInputStream;
import com.sunsw.net.IoFilter.NextFilter;
import com.sunsw.net.IoSession;

public class Read6Int implements NetFuture {

	private static final int SIZE_3INT = (INT_SIZE << 1) + INT_SIZE;

	private static final int SIZE_4INT = (INT_SIZE << 2);

	private NetFuture future;

	public Read6Int(NetFuture future) {
		this.future = future;
	}

	public boolean doReader(NextFilter filter, NetManager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		if (dis.isReady()) {
			if (dst.remaining() >= SIZE_3INT) {
				mgr.setClass(dst.getInt(), dst.getInt(), dst.getInt());
				mgr.setTime(System.currentTimeMillis());
				return future.doReader(filter, mgr, session, dis, dst);
			}
		}
		if (dis.read(dst, SIZE_3INT)) {
			mgr.setClass(dis.getInt(), dis.getInt(), dis.getInt());
			mgr.setTime(System.currentTimeMillis());
			dis.reset();
			return future.doReader(filter, mgr, session, dis, dst);
		} else {
			mgr.setFuture(this);
			return false;
		}
	}

	public boolean doReader(NextFilter filter, NetMessager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		if (dis.isReady()) {
			if (dst.remaining() >= SIZE_4INT) {
				mgr.setClass(dst.getInt(), dst.getInt(), dst.getInt(), dst.getInt());
				return future.doReader(filter, mgr, session, dis, dst);
			}
		}
		if (dis.read(dst, SIZE_4INT)) {
			mgr.setClass(dis.getInt(), dis.getInt(), dis.getInt(), dis.getInt());
			dis.reset();
			return future.doReader(filter, mgr, session, dis, dst);
		} else {
			mgr.setFuture(this);
			return false;
		}
	}

}
