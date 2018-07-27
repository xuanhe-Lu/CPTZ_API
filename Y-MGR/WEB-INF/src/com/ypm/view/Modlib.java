package com.ypm.view;

import com.ypm.util.VeFile;
import com.ypm.util.VeStr;

public class Modlib extends Action {

	private static final long serialVersionUID = 3567034359209191378L;

	private String libs;

	public String getLibs() {
		return libs;
	}

	public void setLibs(String libs) {
		this.libs = libs;
	}

	public String index() {
		String libs = VeStr.toStr(this.getLibs());
		if (libs == null) {
			this.getAjaxInfo().setBody("");
			return JSON;
		} // other
		StringBuilder sb = new StringBuilder();
		try {
			String[] ts = libs.split(",");
			for (int i = 0, j = ts.length; i < j; i++) {
				VeFile.readFile(sb, "fjs/lib", ts[i], ".js");
			}
			this.getAjaxInfo().setBody(sb.toString());
			return JSON;
		} finally {
			sb.setLength(0);
			libs = null;
		}
	}

}
