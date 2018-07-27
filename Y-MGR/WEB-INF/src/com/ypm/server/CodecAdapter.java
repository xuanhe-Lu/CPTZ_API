package com.ypm.server;

import java.nio.ByteBuffer;
import com.sunsw.net.DataOutputStream;
import com.sunsw.net.IoSession;
import com.sunsw.net.core.IoFilterAdapter;
import com.ypm.service.APIBaseService;
import com.ypm.util.GMTime;

public class CodecAdapter extends IoFilterAdapter {

	private static final int TIMEOUT = (5 * 60 * 1000);

	private final APIBaseService service;

	private final CodecHandler admins;

	public CodecAdapter(APIBaseService service) {
		this.admins = new CodecHandler(NetNode.getAdmin());
		this.service = service;
	}

	public void messageReceived(NextFilter filter, IoSession session, Object object) throws Exception {
		try {
			ByteBuffer dst = (ByteBuffer) object;
			dst.flip();
			int code = dst.get();
			if (code == 0x7F) { // 127
				this.received(filter, session, dst, admins, NetNode.sendAt6AES(), TIMEOUT, dst.get());
			} else {
				throw new Exception("forbid!");
			}
		} catch (Exception e) {
			session.getProcessor().remove(session);
		}
	}

	private void received(NextFilter filter, IoSession session, ByteBuffer dst, CodecHandler handler, SendFuture future, int timeout, byte bk) throws Exception {
		byte kv = dst.get(); // 语言
		long time = GMTime.currentTimeMillis();
		session.setAttribute("locale", APIServer.getLocale(kv));
		session.setSecretKey(APIServer.get(bk), time);
		DataOutputStream dos = session.getDataOutputStream();
		try {
			dos.writeLong(time);
			dos.write(bk);
			dos.flush();
		} finally {
			dos.close();
		}
		service.addNeter(new NetNoder(session, future));
		session.setResponse(future);
		session.getConfig().setSoTimeout(timeout);
		session.getFilterChain().replace(APIServer.CODEC_KEY, handler);
		handler.received(filter, session, dst);
	}
}
