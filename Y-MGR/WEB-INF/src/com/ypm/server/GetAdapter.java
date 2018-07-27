package com.ypm.server;

import java.nio.ByteBuffer;
import com.sunsw.net.IoSession;
import com.sunsw.net.core.IoFilterAdapter;

public class GetAdapter extends IoFilterAdapter {

	private GetHandler handler = new GetHandler();

	public void messageReceived(NextFilter filter, IoSession session, Object obj) throws Exception {
		try {
			ByteBuffer dst = (ByteBuffer) obj;
			dst.flip(); // Ready to read!
			long time = dst.getLong();
			session.setSecretKey(APIServer.get(dst.get()), time);
			session.getFilterChain().replace(APIServer.CODEC_KEY, handler);
			handler.received(filter, session, dst);
		} catch (Exception e) {
			session.getProcessor().remove(session);
		}
	}

}
