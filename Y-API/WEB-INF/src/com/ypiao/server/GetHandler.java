package com.ypiao.server;

import java.nio.ByteBuffer;
import com.sunsw.net.DataInputStream;
import com.sunsw.net.IoSession;
import com.sunsw.net.core.IoFilterAdapter;

public class GetHandler extends IoFilterAdapter {

	private NetFuture future = NetNode.getAdmin(null);

	public void messageReceived(NextFilter filter, IoSession session, Object obj) throws Exception {
		try {
			ByteBuffer dst = (ByteBuffer) obj;
			dst.flip(); // Ready to read!
			this.received(filter, session, dst);
		} catch (Exception e) {
			session.getProcessor().remove(session);
		}
	}

	public void received(NextFilter filter, IoSession session, ByteBuffer dst) throws Exception {
		if (dst.position() >= dst.limit()) {
			if (session.read(dst) <= 0) return;
		}
		NetMessager mgr = (NetMessager) session.getReceiveBuffer();
		if (mgr == null) {
			mgr = NetMessager.getBean(future);
			session.setReceiveBuffer(mgr);
		} // 进行数据读取操作
		DataInputStream dis = session.getDataInputStream();
		if (mgr.getFuture().doReader(filter, mgr, session, dis, dst)) {
			this.received(filter, session, dst);
		}
	}

}
