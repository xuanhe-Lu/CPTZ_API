package com.ypm.taglib;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.PageContext;
import org.commons.lang.NumberUtils;
import org.commons.lang.TimeUtils;
import com.sunsw.xwork2.util.ValueStack;
import com.ypm.service.SysBeaner;
import com.ypm.service.SysConfig;

public class Site extends TagSupport {

	private String format;

	public Site(ValueStack stack, PageContext pageContext) {
		super(stack);
		this.setPageContext(pageContext);
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean start(Writer writer) {
		boolean result = super.start(writer);
		StringBuilder sb = new StringBuilder();
		try {
			String type = this.getType();
			if (type.equalsIgnoreCase("timestamp")) {
				String v = this.getValue();
				String f = this.getFormat();
				if (v == null) {
					if (f == null) {
						sb.append(TimeUtils.getStrTime());
					} else {
						sb.append(TimeUtils.getUserDate(f));
					}
				} else {
					long t = NumberUtils.toLong(v);
					if (f == null) {
						f = "yyyy-MM-dd HH:mm:ss";
					}
					sb.append(TimeUtils.format(t, f));
				}
			} else if (type.equalsIgnoreCase("sitename")) {
				String name = this.getName();
				if (name == null) {
					name = "system.title";
				} // 获取标题信息
				SysConfig sys = SysBeaner.get(SysConfig.class);
				if (sys == null) {
					sb.append(getMessage(name, new String[] { "" }));
				} else {
					sb.append(getMessage(name, new String[] { sys.getSiteName() }));
				}
			}
			writer.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sb.setLength(0);
		}
		return result;
	}
}
