package com.ypm.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import com.sunsw.net.DataInputStream;
import com.sunsw.net.IoSession;
import com.sunsw.net.IoFilter.NextFilter;

public interface NetFuture {

	static final int CLS_SIZE = 3;
	/** 整型数字长度 */
	static final int INT_SIZE = 4;
	/** 长整型数字长度 */
	static final int LONG_SIZE = 8;
	/** 服务端常规协议处理 */
	boolean doReader(NextFilter filter, NetManager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException;
	/** 客户端常规协议处理 */
	boolean doReader(NextFilter filter, NetMessager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException;

}
