package com.ypiao.server;

import java.io.File;
import java.io.IOException;
import com.sunsw.net.IoSession;
import com.sunsw.net.core.ByteOutputStream;
import com.ypiao.bean.Message;

public interface SendFuture {

	public void sender(IoSession session, int cls, int ack, int rev, Message msg) throws IOException;

	public void sender(IoSession session, int cls, int ack, int rev, Message msg, ByteOutputStream bos) throws IOException;

	public void sender(IoSession session, int cls, int ack, int rev, Message msg, File file) throws IOException;

}
