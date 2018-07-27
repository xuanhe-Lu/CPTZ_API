package com.ypiao.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.sunsw.net.DataInputStream;
import com.sunsw.net.IoFilter.NextFilter;
import com.sunsw.net.IoSession;

public class ReadFiler implements NetFuture {

	private static final int SIZE = (INT_SIZE + LONG_SIZE * 3);

	@Override
	public boolean doReader(NextFilter filter, NetManager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		NetFiler filer = mgr.makeFiler();
		if (dis.isReady()) {
			if (dst.remaining() >= SIZE) {
				int cls = dst.getInt();
				filer.setPos(dst.getLong());
				filer.setSize(dst.getLong());
				filer.setTime(dst.getLong());
				return ReadUtils.invoke(session, mgr, cls);
			}
		}
		if (dis.read(dst, SIZE)) {
			int cls = dis.getInt();
			filer.setPos(dis.getLong());
			filer.setSize(dis.getLong());
			filer.setTime(dis.getLong());
			return ReadUtils.invoke(session, mgr, cls);
		}
		mgr.setFuture(this);
		return false;
	}

	@Override
	public boolean doReader(NextFilter filter, NetMessager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		return false;
	}

}
