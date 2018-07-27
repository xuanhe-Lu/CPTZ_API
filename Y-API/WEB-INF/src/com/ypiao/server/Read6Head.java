package com.ypiao.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.sunsw.net.DataInputStream;
import com.sunsw.net.IoFilter.NextFilter;
import com.sunsw.net.IoSession;

public class Read6Head implements NetFuture {

	private NetFuture future;

	public Read6Head(NetFuture future) {
		this.future = future;
	}

	public boolean doReader(NextFilter filter, NetManager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		if (dis.readUTF(dst)) {
			mgr.setHeader(dis.getUTF());
			dis.reset(); // reset
			return future.doReader(filter, mgr, session, dis, dst);
		} else {
			mgr.setFuture(this);
			return false;
		}
	}

	public boolean doReader(NextFilter filter, NetMessager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		return future.doReader(filter, mgr, session, dis, dst);
	}

}
