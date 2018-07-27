package com.ypiao.server;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.sunsw.net.DataOutputStream;
import com.sunsw.net.IoSession;
import com.ypiao.util.Constant;
import com.ypiao.util.VeImage;
import com.ypiao.util.VeStr;

public class SendAtFiler implements Runnable {

	private static final int NOT_FOUND = 404;

	private static final int OK = 200;

	private static final File DEF_FACER;

	static {
		StringBuilder sb = new StringBuilder();
		sb.append(Constant.FILEPATH).append("def_facer.png");
		DEF_FACER = new File(sb.toString());
	}

	private IoSession session;

	private NetFiler filer;

	private String fileName;

	private final int cls;

	public SendAtFiler(IoSession session, int cls) {
		this.session = session;
		this.cls = cls;
	}

	public void set(NetFiler filer, String fileName) {
		this.filer = filer;
		this.fileName = fileName;
	}

	@Override
	public void run() {
		try {
			File file = this.getFile();
			if (file == null || !file.isFile()) {
				this.send(NOT_FOUND);
			} else {
				this.send(filer, file);
			}
		} catch (Exception e) {
			session.getProcessor().remove(session);
		} finally {
			filer.destroy();
		}
	}

	private File getFile() {
		String fn = fileName.toLowerCase();
		StringBuilder sb = new StringBuilder(Constant.FILEPATH);
		Matcher m = Pattern.compile("^/?(auth|fder|face)/(\\d{5,12})(/(\\w+))?.(\\w+)$").matcher(fn);
		if (m.find()) {
			String t = m.group(1);
			long uid = Long.parseLong(m.group(2));
			VeStr.getUdir(sb.append("user").append(File.separator), uid).append(File.separator).append(uid).append(File.separator).append(t).append(File.separator);
			if (t.equals("face")) {
				File f = null;
				String w = m.group(4);
				String dist = '.' + m.group(5);
				if (w == null || w.equals("big")) {
					f = new File(sb.append("big").append(dist).toString());
				} else if (w.equals("120")) {
					f = getFace(sb, 120, dist);
				} else if (w.equals("160")) {
					f = getFace(sb, 160, dist);
				} else if (w.equals("240")) {
					f = getFace(sb, 240, dist);
				} // 检测头像，没有则默认
				if (f == null || !f.isFile()) {
					return DEF_FACER;
				} else {
					return f;
				}
			}
			sb.append(m.group(4)).append('.').append(m.group(5));
			return new File(sb.toString());
		} // 聊天图片
		m = Pattern.compile("^/?(chat|img|map)/(s)?(\\d{6})(\\d{2})(\\w+).(\\w+)$").matcher(fn);
		if (m.find()) {
			sb.append(m.group(1)).append(File.separator);
			if (m.group(2) != null)
				sb.append("S");
			sb.append(m.group(3)).append(File.separator).append(m.group(4)).append(File.separator).append(m.group(5)).append('.').append(m.group(6));
			return new File(sb.toString());
		} // 其它图片
		m = Pattern.compile("^/?(act|ads|ader)/(\\d{6})(\\w+).(\\w+)$").matcher(fn);
		if (m.find()) {
			sb.append(m.group(1)).append(File.separator).append(m.group(2)).append(File.separator).append(m.group(3)).append('.').append(m.group(4));
			return new File(sb.toString());
		}
		return new File(sb.append(fileName).toString());
	}

	private File getFace(StringBuilder sb, int w, String distName) {
		int x = sb.length();
		File file = new File(sb.append('f').append(w).append(distName).toString());
		if (file.isFile()) {
			return file;
		}
		sb.delete(x, sb.length()).append("big").append(distName);
		File src = new File(sb.toString());
		if (src.isFile()) {
			try {
				VeImage.waterJPG(src, file, w, 0);
			} catch (IOException e) {
				return null;
			}
		}
		return file;
	}

	private void send(int code) throws IOException {
		synchronized (session) {
			DataOutputStream dos = session.getDataOutputStream();
			try {
				dos.writeInt(cls);
				dos.writeInt(code);
				dos.flush();
			} finally {
				dos.close();
			}
		}
	}

	private void send(NetFiler filer, File file) throws IOException {
		long ret = filer.remaining();
		long size = file.length();
		long time = file.lastModified();
		synchronized (session) {
			DataOutputStream dos = session.getDataOutputStream();
			try {
				dos.writeInt(cls);
				dos.writeInt(OK);
				dos.writeLong(time);
				if (filer.getSize() == -1) {
					dos.writeLong(0);
					dos.writeLong(size);
					dos.writeFile(file);
				} else if (ret <= 0) {
					dos.writeLong(0);
					dos.writeLong(size);
				} else if (filer.getTime() >= time && size >= filer.getSize()) {
					dos.writeLong(filer.getPos());
					dos.writeLong(filer.getSize());
					dos.writeFile(file, filer.getPos(), ret);
				} else {
					dos.writeLong(0);
					dos.writeLong(size);
					dos.writeFile(file);
				}
				dos.flush();
			} finally {
				dos.close();
			}
		}
	}
}
