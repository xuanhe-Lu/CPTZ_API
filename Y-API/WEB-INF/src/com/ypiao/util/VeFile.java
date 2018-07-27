package com.ypiao.util;

import java.io.File;

public class VeFile {

	private static final String DIR_NOTE = "Note";

	private static final String DIR_PROD = "Prod";

	public static void delNote(String sid) {
		File f = getNote(sid);
		if (f == null) {
			// Ignroed
		} else if (f.isFile()) {
			f.delete();
		}
	}

	private static final StringBuilder get() {
		StringBuilder sb = new StringBuilder();
		sb.append(Constant.ROOTPATH).append(Constant.OSCACHE).append(File.separator);
		return sb;
	}

	public static File getNote(String sid) {
		StringBuilder sb = get();
		sb.append(DIR_NOTE).append(File.separator).append(sid).append(".txt");
		return new File(sb.toString());
	}

	public static File getProd(String dir, long Pid) {
		StringBuilder sb = get();
		sb.append(DIR_PROD).append(File.separator).append(dir).append(File.separator).append(Pid).append(".txt");
		return new File(sb.toString());
	}

}
