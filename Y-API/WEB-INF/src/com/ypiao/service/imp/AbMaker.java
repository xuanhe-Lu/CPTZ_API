package com.ypiao.service.imp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.commons.io.FileUtils;
import org.commons.io.output.StringBuilderWriter;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;
import com.sunsw.velocity.Template;
import com.sunsw.velocity.Velocity;
import com.sunsw.velocity.VelocityContext;
import com.sunsw.velocity.VelocityEngine;
import com.sunsw.velocity.exception.ParseErrorException;
import com.sunsw.velocity.exception.ResourceNotFoundException;
import com.ypiao.util.AState;
import com.ypiao.util.Constant;
import com.ypiao.util.VeStr;

public abstract class AbMaker implements AState {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected static final String NR = System.getProperty("line.separator");

	protected static SimpleDateFormat SDF = new SimpleDateFormat(Constant.SYS_FORMAT);

	protected static VelocityEngine VE;

	public AbMaker() {
		if (VE == null) {
			VE = new VelocityEngine();
			VE.setProperty(Velocity.FILE_RESOURCE_LOADER_CACHE, true);
			VE.setProperty(Velocity.FILE_RESOURCE_LOADER_CHECK, "30");
			VE.setProperty(Velocity.FILE_RESOURCE_LOADER_EXPIRED, "3600"); // 过期检测
			VE.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, Constant.ROOTPATH);
			VE.setProperty(Velocity.PARSER_POOL_SIZE, 64); // 初始池大小
			VE.setProperty(Velocity.INPUT_ENCODING, Constant.CHARSET.displayName());
			VE.setProperty(Velocity.OUTPUT_ENCODING, Constant.CHARSET.displayName());
			try {
				VE.init();
			} catch (Exception e) {
				logger.error("Template init Error: " + e);
			}
		}
	}

	/** 异步执行相关操作 */
	protected void execute(Runnable run) {
		com.ypiao.service.PoolService.getService().execute(run);
	}

	/** 根据模板名称获取模板 */
	protected Template getTemplate(String fileName) {
		try {
			return VE.getTemplate(fileName);
		} catch (ResourceNotFoundException | ParseErrorException e) {
			return null;
		}
	}

	protected Template getTemplate(StringBuilder sb, String dir, String fileName) throws ResourceNotFoundException, ParseErrorException {
		return this.getTemplate(sb, dir, fileName, -1);
	}

	protected Template getTemplate(StringBuilder sb, String dir, String fileName, int tid) throws ResourceNotFoundException, ParseErrorException {
		sb.setLength(0); // 处理缓存信息
		sb.append("WEB-INF").append(File.separator).append("Template").append(File.separator);
		if (dir != null) {
			sb.append(dir.replace('/', File.separatorChar)).append(File.separator);
		}
		if (tid >= 0) {
			sb.append(fileName).append(tid);
		} else {
			sb.append(fileName);
		}
		return VE.getTemplate(sb.append(".vm").toString());
	}

	/** 预先解析模板 */
	protected String parse(VelocityContext context, Template template, File file) throws IOException {
		String str = null, body = this.writer(context, template);
		try {
			if (body.charAt(0) == 65279) {
				str = body.substring(1);
			} else {
				str = body;
			} // 替换二次标签
			body = str.replaceAll("(?i)#2(if|end|for|else)", "#$1").replaceAll("\\$2!?([\\w{])", "\\$!$1");
			FileUtils.write(file, body, Constant.CHARSET);
			return body;
		} finally {
			body = str = null;
		}
	}

	/** 转换成HTML */
	protected String toHtml(String text) {
		return VeStr.toHtml(text);
	}

	/** 数据缓存构建 */
	protected String writer(VelocityContext vc, Template template) {
		StringBuilderWriter sw = new StringBuilderWriter();
		try {
			Calendar c = Calendar.getInstance();
			vc.put("Charset", Constant.CHARSET);
			vc.put("UseTime", SDF.format(c.getTime()));
			template.merge(vc, sw);
			return sw.toString();
		} catch (Exception e) {
			return sw.toString();
		} finally {
			sw.flush();
			sw.close();
		}
	}
}
