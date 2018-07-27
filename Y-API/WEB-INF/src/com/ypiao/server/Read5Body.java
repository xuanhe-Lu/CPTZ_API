package com.ypiao.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.sunsw.net.DataInputStream;
import com.sunsw.net.IoFilter.NextFilter;
import com.sunsw.net.IoSession;

public class Read5Body implements NetFuture {

	private NetFuture future;

	public Read5Body(NetFuture future) {
		this.future = future;
	}
	@Override
	public boolean doReader(NextFilter filter, NetManager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		if (dis.readUTFS(dst)) {
			mgr.setBody(dis.getAES());
			mgr.setRever(); // rev
			if (mgr.getAtt() >= 0) {
				dis.close();
				filter.messageReceived(session, mgr);
				return true;
			} else {
				dis.reset(); // reset
			} // Load file information
			return future.doReader(filter, mgr, session, dis, dst);
		} else {
			mgr.setFuture(this);
			return false;
		}
	}

	public final boolean doReader(NextFilter filter, NetMessager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		if (dis.readUTFS(dst)) {
			mgr.setBod5(dis.getAES());
			if (mgr.getCls() >= 0) {
				dis.close();
				filter.messageReceived(session, mgr);
				return true;
			} else {
				dis.reset(); // reset
			} // Load file information
			return future.doReader(filter, mgr, session, dis, dst);
		} else {
			mgr.setFuture(this);
			return false;
		}
	}

}
