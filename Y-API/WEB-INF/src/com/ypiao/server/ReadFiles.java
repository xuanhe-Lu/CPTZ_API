package com.ypiao.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import com.sunsw.net.DataInputStream;
import com.sunsw.net.IoSession;
import com.sunsw.net.IoFilter.NextFilter;

public class ReadFiles implements NetFuture {

	private final Object service;

	public ReadFiles(Object service) {
		this.service = service;
	}
	@Override
	public boolean doReader(NextFilter filter, NetManager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		NetFiler filer = mgr.makeFiler();
		if (filer.getSize() <= 0) {
			if (dis.size() <= 0 && dst.remaining() >= LONG_SIZE) {
				filer.setSize(dst.getLong());
			} else if (dis.read(dst, LONG_SIZE)) {
				filer.setSize(dis.getLong());
			} else {
				mgr.setFuture(this);
				return false;
			}
		} // 读取文件信息
		dis.close(); // 关闭流
		FileChannel fc = null;
		File file = filer.makeFile();
		FileOutputStream fos = new FileOutputStream(file, true);
		try {
			fc = fos.getChannel();
			long ret = (filer.getSize() - file.length());
			if (ReadFile.write(session, fc, dst, ret)) {
				return ReadUtils.invoke(session, mgr, service);
			} else {
				mgr.setFuture(this);
				return false;
			}
		} finally {
			if (fc != null) fc.close();
			fos.close();
		}
	}

	public final boolean doReader(NextFilter filter, NetMessager mgr, IoSession session, DataInputStream dis, ByteBuffer dst) throws IOException {
		return false;
	}

}
