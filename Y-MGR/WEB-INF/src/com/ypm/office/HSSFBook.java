package com.ypm.office;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

public class HSSFBook extends ExcelBook implements ExcelSheet {

	private final HSSFWriter writer;

	public HSSFBook(HSSFWriter writer) {
		this.writer = writer;
		this.book = writer.book;
		this.sheet = book.createSheet();
		this.sheetIndex = (book.getNumberOfSheets() - 1);
	}

	public void setSheetName(String name) {
		writer.setSheetName(sheetIndex, name);
	}

	private Cell getCell() {
		Row row = this.getRow();
		try {
			return row.createCell(this.index_cel);
		} finally {
			this.index_cel += 1;
		}
	}

	public void addCell(int cell) {
		Row row = this.getRow();
		for (int i = 0; i < cell; i++) {
			row.createCell(index_cel);
			this.index_cel += 1;
		}
	}

	public void addCell(CellStyle style, double v) {
		Cell c = getCell();
		if (style != null) {
			c.setCellStyle(style);
		}
		c.setCellValue(v);
	}

	public void addCell(CellStyle style, String v) {
		Cell c = getCell();
		if (style != null) {
			c.setCellStyle(style);
		}
		c.setCellValue(v);
	}
}
