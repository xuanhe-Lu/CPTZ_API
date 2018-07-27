package com.ypm.service.imp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.commons.io.FileUtils;
import com.ypm.service.DownloadService;
import com.ypm.util.Constant;
import com.ypm.util.VeStr;

public class DownloadServiceImp implements DownloadService {

	private static final int SIZE = 4096;

	/** 参数转换成小写 */
	private Map<String, String> convert(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<String> es = request.getParameterNames();
		String name = null; // 参数名称
		while (es.hasMoreElements()) {
			name = es.nextElement(); // 相关参数
			map.put(name.toLowerCase(), request.getParameter(name));
		}
		return map;
	}

	public boolean doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> pars = this.convert(request); // 转换成小写
		String fileName = pars.get("filename"); // 请求文件名
		String suffix = VeStr.getSuffixName(fileName);
		String contentType = null, headerType = null;
		try {
			if (suffix == null) {
				return this.invoke404(request, response);
			} // 处理文件请求
			StringBuilder sb = new StringBuilder();
			sb.append(Constant.ROOTPATH).append(Constant.EXPORTS);
			if (suffix.equalsIgnoreCase(".xls")) {
				contentType = "application/vnd.ms-excel; charset=utf-8";
				headerType = "application/vnd.ms-excel";
			} else if (suffix.equalsIgnoreCase(".xlsx")) {
				contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
				headerType = "application/vnd.ms-excel";
			} else {
				return this.invoke404(request, response);
			} // 检测下载文件
			File file = new File(sb.append(File.separator).append(fileName).toString());
			if (!file.isFile()) {
				return this.invoke404(request, response);
			} // 下载文件信息
			String text = FileUtils.readFile(sb.append(".txt").toString(), "UTF-8");
			long length = 0, len = file.length();
			long start = 0, end = (len - 1);
			boolean isSwitch = false;
			String range = pars.get("range");
			if (range == null || range.equals("null") || range.trim().length() <= 0) {
				length = len;
			} else {
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
				String rangBytes = range.replaceAll("bytes=", "");
				int pos = rangBytes.indexOf("-");
				start = Long.parseLong(rangBytes.substring(0, pos));
				if (rangBytes.endsWith("-")) {
					length = len - start;
				} else {
					isSwitch = true;
					end = Long.parseLong(rangBytes.substring(pos + 1));
					length = end - start + 1;
				}
			}
			sb.setLength(0);
			sb.append("bytes ").append(start).append("-").append(end).append("/").append(len);
			response.reset();
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("Content-Length", String.valueOf(length));
			response.setHeader("Content-Range", sb.toString());
			response.setHeader("Content-Type", headerType);
			sb.setLength(0);
			if (text != null && text.length() < 100) {
				String[] ts = text.split("\u002C\u0001");
				if (ts.length >= 2) {
					sb.append("attachment;filename=").append(ts[1]).append(suffix);
				}
			}
			if (sb.length() > 0) {
				response.addHeader("Content-Disposition", new String(sb.toString().getBytes(), "ISO_8859_1"));
			} // 加载下载文件
			response.setContentType(contentType);
			InputStream in = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(in);
			if (start > 0)
				bis.skip(start); // 忽略已处理
			ServletOutputStream out = response.getOutputStream();
			try {
				int n = 0;
				long readLength = 0; // 读取长度
				byte[] bytes = new byte[SIZE];
				len = (length - SIZE);
				if (isSwitch) {
					while (len >= readLength) {
						n = bis.read(bytes);
						readLength += n;
						out.write(bytes, 0, n);
					} // 加载结尾
					len = (length - readLength);
					if (len >= 0) {
						n = bis.read(bytes, 0, (int) len);
						out.write(bytes, 0, n);
					}
				} else {
					while ((n = bis.read(bytes)) != -1) {
						out.write(bytes, 0, n);
					}
				}
				out.flush();
			} finally {
				out.close();
				bis.close();
				in.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			fileName = suffix = null;
			pars.clear();
		}
	}

	/** 文件不存在 */
	private boolean invoke404(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		request.getRequestDispatcher("/404.html").forward(request, response);
		return true;
	}
}
