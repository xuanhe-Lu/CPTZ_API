package com.ypm.view;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.commons.code.Suncoder;
import org.commons.io.FileUtils;
import com.ypm.bean.AjaxFile;
import com.ypm.service.SystemService;
import com.ypm.util.Constant;
import com.ypm.util.VeFile;
import com.ypm.util.VeStr;

public class Module extends Action {

	private static final long serialVersionUID = 687165018833894151L;

	private static final String DIRS = "fjs";

	private SystemService systemService;

	private String id;

	private String libs;

	private String depend;

	public SystemService getSystemService() {
		return systemService;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLibs() {
		return libs;
	}

	public void setLibs(String libs) {
		this.libs = libs;
	}

	public String getDepend() {
		return depend;
	}

	public void setDepend(String depend) {
		this.depend = depend;
	}

	@Override
	public String index() {
		StringBuilder sb = new StringBuilder();
		String libs = VeStr.toStr(this.getLibs());
		try {
			if (libs == null) {
				// Ignored
			} else {
				String[] ts = libs.split(",");
				for (int i = 0; i < ts.length; i++) {
					VeFile.readFile(sb, "fjs/lib", ts[i], ".js");
				}
			} // Add Depend module
			libs = VeStr.toStr(this.getDepend());
			if (libs == null) {
				// Ignored
			} else {
				String[] ts = libs.split(",");
				for (int i = 0; i < ts.length; i++) {
					VeFile.readFile(sb, "fjs", ts[i], ".txt");
				}
			} // Add module
			this.addModule(sb, this.getId());
			this.getAjaxInfo().setBody(sb.toString());
		} finally {
			sb.setLength(0);
			libs = null;
		}
		return JSON;
	}

	/** Add Module */
	private void addModule(StringBuilder sb, String id) {
		AjaxFile file = AjaxFile.get(DIRS, id);
		try {
			if (Constant.USE_DEBUG || file.isExpired()) {
				StringBuffer buf = new StringBuffer(); // 模板路径信息
				buf.append(Constant.ROOTPATH).append(DIRS).append(File.separatorChar);
				File f = new File(buf.append(id).append(".txt").toString());
				if (f.isFile()) {
					file.write(this.readFile(buf, f));
				}
			} // 加载转换输出参数
			sb.append(this.getSystemService().getFields(file.getBody(), id));
		} finally {
			file.close();
		}
	}

	/** 加载文件并替换标签 */
	private String readFile(StringBuffer sb, File file) {
		String str = null;
		try {
			sb.setLength(0);
			str = FileUtils.readFile(file, Constant.CHARSET);
			if (str.charAt(0) == 65279) {
				str = str.substring(1);
			} // 正则替换标签
			Matcher m = Pattern.compile("\\$!?\\{([\\w.]{3,30})\\}").matcher(str);
			while (m.find()) {
				m.appendReplacement(sb, this.getText(m.group(1)));
			}
			return Suncoder.enJS(m.appendTail(sb).toString());
		} catch (Exception e) {
			return "";
		} finally {
			str = null;
		}
	}

}
