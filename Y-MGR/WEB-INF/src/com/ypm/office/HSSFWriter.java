package com.ypm.office;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.commons.io.FileUtils;

public class HSSFWriter implements ExcelWriter {

	protected HSSFWorkbook book;

	private List<HSSFBook> bs = new ArrayList<HSSFBook>(3);

	private Map<String, Integer> ks = new HashMap<String, Integer>();

	private File file;

	private String title = "None";

	private String company = "Grandroutes";

	public HSSFWriter() {
		this.book = new HSSFWorkbook();
	}

	public File getFile() {
		return file;
	}

	public HSSFBook getSheet(int index) {
		if (bs.size() > index) {
			return bs.get(index);
		} else {
			HSSFBook s = new HSSFBook(this);
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
			book.createInformationProperties();
			DocumentSummaryInformation ds = book.getDocumentSummaryInformation();
			SummaryInformation info = book.getSummaryInformation();
			info.setTitle(title);
			info.setSubject("主题 信息");
			ds.setCategory("类型");
			info.setAuthor("作者");
			// info.setComments("备注信息");
			// info.setRevNumber("修定号");
			// ds.setDocumentVersion("版本号");
			info.setApplicationName("Sunsw PIO");
			ds.setCompany(company);
			ds.setManager("99998");
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
