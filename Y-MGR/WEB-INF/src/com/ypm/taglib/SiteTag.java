package com.ypm.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.sunsw.struts.components.Component;
import com.sunsw.struts.views.jsp.ComponentTagSupport;
import com.sunsw.xwork2.util.ValueStack;

public class SiteTag extends ComponentTagSupport {

	private static final long serialVersionUID = 3783182273211980138L;

	private String type;

	private String name;

	private String value;

	private String format = "yyyy-MM-dd";

	public SiteTag() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
		return new Site(stack, pageContext);
	}

	protected void populateParams() {
		super.populateParams();
		Site tag = (Site) component;
		tag.setType(type);
		tag.setName(name);
		tag.setValue(value);
		tag.setFormat(format);
	}
}
