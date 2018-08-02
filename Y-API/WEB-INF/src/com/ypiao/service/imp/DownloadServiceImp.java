package com.ypiao.service.imp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ypiao.service.DownloadService;
import com.ypiao.util.Constant;
import com.ypiao.util.VeImage;

public class DownloadServiceImp implements DownloadService {

	private static File DEF_FACER;

	public DownloadServiceImp() {
		StringBuilder sb = new StringBuilder();
		sb.append(Constant.ROOTPATH).append("img").append(File.separator).append("def_facer.png");
		DEF_FACER = new File(sb.toString());
	}

	@Override
	public boolean doGet(HttpServletRequest req, HttpServletResponse res, String uri) throws ServletException, IOException {
		System.out.println("URI:"+uri);
		String URI = uri.toLowerCase();
		Matcher m = Pattern.compile("^/img/(\\d{6})(\\w{7,9}).(jpg|png)$").matcher(URI);
		if (m.find()) {
			String dist = m.group(3);
			StringBuilder sb = new StringBuilder(64);
			sb.append(Constant.FILEPATH).append("img").append(File.separator).append(m.group(1)).append(File.separator).append(m.group(2));
			System.out.println("sb:"+sb);
			File file = new File(sb.append('.').append(dist).toString());
			if (dist.equalsIgnoreCase("png")) {
				return this.toPNG(req, res, file);
			} else if (file.isFile()) {
				return this.toJPEG(req, res, file);
			} else {
				int end = sb.length();
				sb.replace((end - 3), end, "png");
				return this.toPNG(req, res, new File(sb.toString()));
			}
		} // 用户头像信息
		m = Pattern.compile("^/(facer)/(\\d+)(/(120|160|240|big))?").matcher(URI);
		if (m.find()) {
			String dir = m.group(1);
			String uid = m.group(2);
			int w = 0, beg = 0, len = uid.length();
			StringBuilder sb = new StringBuilder(64);
			sb.append(Constant.FILEPATH).append("user").append(File.separator).append(uid.substring(len - 3)).append(File.separator).append(uid).append(File.separator);
			if (dir.equalsIgnoreCase("facer")) {
				sb.append("face").append(File.separator);
			} else {
				sb.append(dir).append(File.separator);
			} // 检测大小信息
			if (m.group(3) == null) {
				sb.append("big.jpg");
			} else {
				String fix = m.group(4);
				if (fix.equalsIgnoreCase("big")) {
					sb.append("big.jpg");
				} else {
					beg = sb.length();
					w = Integer.parseInt(fix);
					sb.append("f").append(fix).append(".jpg");
				}
			}
			return this.facer(req, res, sb, beg, w);
		} // 银行图标信息
		m = Pattern.compile("^/img/(bank)/([^.]+).png$").matcher(URI);
		if (m.find()) {
			StringBuilder sb = new StringBuilder(64);
			sb.append(Constant.FILEPATH).append("img").append(File.separator).append(m.group(1)).append(File.separator).append(m.group(2).toLowerCase());
			File file = new File(sb.append(".png").toString());
			return this.toPNG(req, res, file);
		}
		return false;
	}

	private boolean facer(HttpServletRequest req, HttpServletResponse res, StringBuilder sb, int beg, int w) throws ServletException, IOException {
		File file = new File(sb.toString());
		if (file.isFile()) {
			// Ignored
		} else if (beg > 0 && w > 0) {
			int end = sb.length();
			File src = new File(sb.replace(beg, end, "big.jpg").toString());
			if (src.isFile()) {
				VeImage.waterJPG(src, file, w, 0);
			} else {
				file = DEF_FACER;
			}
		} else {
			file = DEF_FACER;
		}
		res.setContentType("image/jpeg");
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		ServletOutputStream out = res.getOutputStream();
		try {
			int n = 0, size = 8192;
			byte bs[] = new byte[size];
			while ((n = bis.read(bs)) > 0) {
				out.write(bs, 0, n);
			}
			return true;
		} finally {
			out.close();
			bis.close();
			fis.close();
		}
	}

	private boolean toImage(HttpServletRequest req, HttpServletResponse res, File file) throws ServletException, IOException {
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		ServletOutputStream out = res.getOutputStream();
		try {
			int n = 0, size = 8192;
			byte bs[] = new byte[size];
			while ((n = bis.read(bs)) > 0) {
				out.write(bs, 0, n);
			}
			return true;
		} finally {
			out.close();
			bis.close();
			fis.close();
		}
	}

	private boolean toJPEG(HttpServletRequest req, HttpServletResponse res, File file) throws ServletException, IOException {
		if (file.isFile()) {
			res.setContentType("image/jpeg");
			return toImage(req, res, file);
		}
		return false;
	}

	private boolean toPNG(HttpServletRequest req, HttpServletResponse res, File file) throws ServletException, IOException {
		if (file.isFile()) {
			res.setContentType("image/png");
			return toImage(req, res, file);
		}
		return false;
	}

	/** 文件不存在 */
	public boolean invoke404(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setStatus(HttpServletResponse.SC_NOT_FOUND);
		req.getRequestDispatcher("/404.html").forward(req, res);
		return true;
	}
}
