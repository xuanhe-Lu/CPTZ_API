package com.ypm.taglib;

import java.util.Locale;
import java.util.StringTokenizer;
import javax.servlet.jsp.PageContext;
import org.commons.lang.NumberUtils;
import org.commons.lang.StringUtils;
import org.framework.context.support.ResourceBundleMessageSource;
import org.framework.web.context.WebApplicationContext;
import org.framework.web.context.support.WebApplicationContextUtils;
import com.sunsw.struts.components.Component;
import com.sunsw.xwork2.util.ValueStack;

public class TagSupport extends Component {

	protected static final String SPLITTER = ",";

	protected static final String REP = "@ivalue";

	protected static final String NR = System.getProperty("line.separator");

	private Locale locale = null;

	private PageContext pageContext;

	private WebApplicationContext wc = null;

	private ResourceBundleMessageSource messageSource = null;

	protected String cssClass;

	protected String cssStyle;

	protected String onclick;

	protected String onchange;

	protected boolean disabled = false;

	private String type;

	private String id;

	private String key;

	private String name;

	private int index = 0;

	private int length = 0;

	private int size = 0;

	private int tabindex = 0;

	private String list;

	private String listKey = "key";

	private String listValue = "value";

	private String value;

	public PageContext getPageContext() {
		return pageContext;
	}

	public void setPageContext(PageContext context) {
		this.pageContext = context;
		this.wc = WebApplicationContextUtils.getWebApplicationContext(context.getServletContext());
	}

	public TagSupport(ValueStack arg) {
		super(arg);
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public String getCssStyle() {
		return cssStyle;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getOnchange() {
		return onchange;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
		if (key == null) {
			// ignored
		} else if (key.equalsIgnoreCase("key")) {
			this.key = this.stack.findString("key");
		} else if (key.startsWith("%{") && key.endsWith("}")) {
			this.key = this.stack.findString(key.substring(2, key.length() - 1));
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getTabindex() {
		return tabindex;
	}

	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public String getListKey() {
		return listKey;
	}

	public void setListKey(String listKey) {
		this.listKey = listKey;
	}

	public String getListValue() {
		return listValue;
	}

	public void setListValue(String listValue) {
		this.listValue = listValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		if (value == null) {
			// Ignored
		} else if (altSyntax()) {
			if (value.startsWith("%{") && value.endsWith("}")) {
				this.value = this.stack.findString(value.substring(2, value.length() - 1));
			}
		}
	}

	/** 获取资源对象 */
	public Object getBean(String name) {
		return this.wc.getBean(name);
	}

	/** 获取资源信息 */
	public String getMessage(String name) {
		return getMessage(name, null);
	}

	public String getMessage(String name, Object[] obj) {
		if (this.messageSource == null) {
			this.locale = this.pageContext.getRequest().getLocale();
			this.messageSource = (ResourceBundleMessageSource) this.getBean("messageSource");
		}
		return this.messageSource.getMessage(name, obj, this.locale);
	}

	/** 获取资源数组 */
	public StringTokenizer getStringTokkenize(String name) {
		return new StringTokenizer(getMessage(name), SPLITTER);
	}

	/** 获取资源数组 */
	public String[] getStrings(String name) {
		return getMessage(name).split(SPLITTER);
	}

	public Object getObject() {
		if (this.getName() == null) {
			return null;
		} else if (this.altSyntax()) {
			if (name.startsWith("%{") && name.endsWith("}")) {
				this.setName(name.substring(2, name.length() - 1));
			}
		}
		return this.getObject(name);
	}

	protected Object getObject(String key) {
		return this.stack.findValue(key);
	}

	public String getString() {
		if (StringUtils.isNotBlank(value)) {
			return value;
		} // 从Name中获取字符
		Object obj = this.getObject();
		if (obj == null) {
			return null;
		} // String
		return String.valueOf(obj);
	}

	public String getString(String name) {
		if (name == null)
			return null;
		return this.stack.findString(name);
	}

	public int getNumber(int num) {
		if (StringUtils.isNotBlank(value)) {
			return NumberUtils.toInt(value, num);
		} // 从Name中获取数值
		try {
			return Integer.parseInt(String.valueOf(this.getObject()));
		} catch (Exception e) {
			return num;
		}
	}
}
