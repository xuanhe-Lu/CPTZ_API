package com.ypm.office;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelBook {

	private static final String KEY_HEADER = "At_Header";

	private static final String KEY_TITLER = "At_Titler";

	private Map<String, Font> cache_fonts = new HashMap<String, Font>();

	private Map<String, CellStyle> cache_style = new HashMap<String, CellStyle>();

	private Map<String, DataFormat> cache_data = new HashMap<String, DataFormat>();

	protected Workbook book;

	protected Sheet sheet;

	protected int sheetIndex;

	protected String title;

	/** 当前行数指针 */
	protected int index_row = -1;

	/** 当前列数指针 */
	protected int index_cel = 0;

	private Row row;

	public int getSheetIndex() {
		return sheetIndex;
	}

	public String getSheetName() {
		return sheet.getSheetName();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Row getRow() {
		return getRow(-1);
	}

	public Row getRow(int height) {
		if (row == null) {
			this.index_row += 1; // 设置行信息
			this.index_cel = 0; // 设置列信息
			this.row = sheet.createRow(this.index_row);
			if (height < 15) {
				// Ignored
			} else {
				if (height > 700) {
					height = 700;
				}
				this.row.setHeight((short) (height * 15));
			}
		}
		return row;
	}

	/** 标题字体信息 */
	public Font getFontByTitle() {
		Font font = cache_fonts.get(KEY_TITLER);
		if (font == null) {
			font = book.createFont();
			cache_fonts.put(KEY_TITLER, font);
			font.setBold(true);
			font.setColor((short) 1);
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 12);
		}
		return font;
	}

	/** 2 红色 3 绿芭 4蓝色 5 大黄 6 粉红 7 水蓝 8 黑色 9 白色 10 红色 */
	public Font getFontByHeader() {
		Font font = cache_fonts.get(KEY_HEADER);
		if (font == null) {
			font = book.createFont();
			cache_fonts.put(KEY_HEADER, font);
			font.setColor((short) 8);
		}
		return font;
	}

	public CellStyle getCellStyle(String key) {
		CellStyle style = this.cache_style.get(key);
		if (style == null) {
			style = book.createCellStyle();
			this.cache_style.put(key, style);
		}
		return style;
	}

	@SuppressWarnings("deprecation")
	public CellStyle getStyleByTitle() {
		CellStyle s = cache_style.get(KEY_TITLER);
		if (s == null) {
			s = book.createCellStyle();
			cache_style.put(KEY_TITLER, s);
			s.setAlignment(CellStyle.ALIGN_CENTER);
			s.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			s.setFillBackgroundColor((short) 48);
			s.setFillForegroundColor((short) 24);
			s.setFillPattern(CellStyle.SOLID_FOREGROUND);
			s.setFont(this.getFontByTitle());
		}
		return s;
	}

	@SuppressWarnings("deprecation")
	public CellStyle getStyleByHeader() {
		CellStyle s = cache_style.get(KEY_HEADER);
		if (s == null) {
			s = book.createCellStyle();
			cache_style.put(KEY_HEADER, s);
			s.setAlignment(CellStyle.ALIGN_CENTER);
			s.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			s.setBorderLeft(CellStyle.BORDER_THIN);
			s.setFillBackgroundColor((short) 41);
			s.setFillForegroundColor((short) 41);
			s.setFillPattern(CellStyle.SOLID_FOREGROUND);
			s.setFont(this.getFontByHeader());
		}
		return s;
	}

	@SuppressWarnings("deprecation")
	public CellStyle getStyleByC() {
		CellStyle s = cache_style.get("C");
		if (s == null) {
			s = book.createCellStyle();
			cache_style.put("C", s);
			s.setAlignment(CellStyle.ALIGN_CENTER);
			s.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		}
		return s;
	}

	public DataFormat getDataFormat(String key) {
		DataFormat df = cache_data.get(key);
		if (df == null) {
			df = book.createDataFormat();
			cache_data.put(key, df);
		}
		return df;
	}

	/** 合并单元格信息 */
	public void mergedRegion(int sRow, int eRow, int sCel, int eCel) {
		if (sRow <= -1) {
			sRow = this.index_row;
		}
		if (eRow <= -1) {
			eRow = this.index_row;
		}
		sheet.addMergedRegion(new CellRangeAddress(sRow, eRow, sCel, eCel));
	}

	public void addNext() {
		this.row = null;
	}

	public void addTitle(String name, int c) {
		Row row = this.getRow(28);
		int s = this.index_cel;
		if (c > 1) {
			int e = (s + c - 1);
			this.mergedRegion(index_row, index_row, s, e);
		} else {
			c = 1;
		} // 加载标题行
		this.index_cel += c;
		Cell cell = row.createCell(s);
		cell.setCellStyle(getStyleByTitle());
		cell.setCellValue(name);
	}

	public void addHeader(String name, int c, int width) {
		this.addHeader(getStyleByHeader(), name, c, width);
	}

	public void addHeader(CellStyle style, String name, int c, int width) {
		Row row = this.getRow();
		int s = this.index_cel;
		if (c > 1) {
			int e = (s + c - 1);
			this.mergedRegion(index_row, index_row, s, e);
		} else {
			c = 1;
		} // 加载标题行
		this.index_cel += c;
		Cell cell = row.createCell(s);
		cell.setCellStyle(style);
		cell.setCellValue(name);
		sheet.setColumnWidth(s, ((width + 2) * 32));
	}
}
