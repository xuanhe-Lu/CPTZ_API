package com.ypm.office;

import java.io.Serializable;
import org.apache.poi.ss.usermodel.CellStyle;

public class ExcelStyle implements Serializable {

	private static final long serialVersionUID = 8655826751713554983L;

	private int index = 0;

	private int width = 0;

	private CellStyle style;

	public static ExcelStyle get(ExcelSheet s, int index, int width, int type, String format) {
		ExcelStyle style = new ExcelStyle(s, index, width, type, format);
		return style;
	}

	public ExcelStyle(ExcelSheet s, int index, int width, int type, String format) {
		this.index = index;
		this.width = width;
		if (format == null) {
			// Ignored
		} else if (format.equalsIgnoreCase("yyyy-MM-dd HH:mm:ss")) {
			this.style = s.getStyleByC();
			this.width = 171;
		}
	}

	public int getIndex() {
		return index;
	}

	public int getWidth() {
		return width;
	}

	public CellStyle getStyle() {
		return style;
	}
}
