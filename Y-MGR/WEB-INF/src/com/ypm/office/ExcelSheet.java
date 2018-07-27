package com.ypm.office;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;

public interface ExcelSheet {

	public int getSheetIndex();

	public String getSheetName();

	public void setSheetName(String name);

	public String getTitle();
	/** 设置标题 */
	public void setTitle(String title);

	/** 标题专用格式 */
	public CellStyle getStyleByTitle();

	public CellStyle getStyleByHeader();

	public CellStyle getStyleByC();

	public CellStyle getCellStyle(String key);

	public DataFormat getDataFormat(String key);

	public void addTitle(String name, int cell);

	public void addHeader(String name, int cell, int width);

	public void addHeader(CellStyle style, String name, int cell, int width);

	public void addCell(int cell);

	public void addCell(CellStyle style, double value);

	public void addCell(CellStyle style, String value);
	/** 下移一行 */
	public void addNext();

}
