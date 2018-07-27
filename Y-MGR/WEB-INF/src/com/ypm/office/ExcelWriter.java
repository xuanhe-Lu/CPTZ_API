package com.ypm.office;

import java.io.File;
import java.io.IOException;

public interface ExcelWriter {

	public static ExcelWriter get() {
		return get("xlsx");
	}

	public static ExcelWriter get(String fix) {
		if ("xlsx".equalsIgnoreCase(fix)) {
			return new XSSFWriter();
		} else {
			return new HSSFWriter();
		}
	}

	public File getFile();

	public ExcelSheet getSheet(int index);

	public ExcelSheet getSheet(String name);

	public void setTitle(String title);

	public boolean output(File file) throws IOException;

	public boolean output(String filePath) throws IOException;
}
