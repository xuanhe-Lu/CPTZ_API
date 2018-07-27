package com.ypm.office;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.commons.io.FileUtils;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTProperties;

public class XSSFWriter implements ExcelWriter {

	protected XSSFWorkbook book;

	private List<XSSFBook> bs = new ArrayList<XSSFBook>(3);

	private Map<String, Integer> ks = new HashMap<String, Integer>();

	private File file;

	private String title = "None";

	private String company = "Hangzhou";

	public XSSFWriter() {
		this.book = new XSSFWorkbook();
	}

	public File getFile() {
		return file;
	}

	public XSSFBook getSheet(int index) {
		if (bs.size() > index) {
			return bs.get(index);
		} else {
			XSSFBook s = new XSSFBook(this);
			index = s.getSheetIndex();
			bs.add(s); // 缓存Sheet
			ks.put(String.valueOf(index), index);
			return s;
		}
	}

	public ExcelSheet getSheet(String name) {
		Integer k = ks.get(name);
		if (k == null) {
			return null;
		} else {
			return getSheet(k.intValue());
		}
	}

	public void setSheetName(int index, String name) {
		book.setSheetName(index, name);
		Integer k = ks.get(name);
		if (k == null) {
			ks.put(name, index);
		} else if (k.intValue() > index) {
			ks.put(name, index);
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean output(File file) throws IOException {
		this.file = file; // 文件信息
		FileOutputStream fos = new FileOutputStream(file);
		try {
			for (XSSFBook b : bs) {
				b.makeWorking();
			}
			CoreProperties cp = book.getProperties().getCoreProperties();
			CTProperties ctp = book.getProperties().getExtendedProperties().getUnderlyingProperties();
			cp.setTitle(title);
			cp.setSubjectProperty("主题信息");
			cp.setCategory("类别");
			cp.setCreator("作者");
			// cp.setDescription("备注信息");
			// cp.setContentStatus("内容状态");
			// cp.setContentType("内容类型");
			ctp.setApplication("Sunsw PIO");
			ctp.setCompany(company);
			ctp.setManager("99999");
			book.write(fos);
			fos.flush();
			StringBuilder sb = new StringBuilder();
			sb.append(this.title).append("\u002C\u0001").append(this.getTitle());
			FileUtils.write(file.getPath() + ".txt", sb.toString(), "UTF-8");
			return true;
		} finally {
			fos.close();
		}
	}

	public boolean output(String filePath) throws IOException {
		return output(new File(filePath));
	}
}
