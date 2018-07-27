package com.ypm.service.aps;

import java.io.File;
import java.io.IOException;
import org.commons.io.FileUtils;
import com.ypm.bean.Manager;
import com.ypm.util.Constant;

public class APiAt996 extends Abstract {

	public void saveUsed(Manager mgr) {
		long uid = mgr.getLong("uid");
		try {
			File f = mgr.getFile();
			if (f == null || USER_UID_BEG > uid) {
				// Ignored
			} else if (f.isFile()) {
				String str = String.valueOf(uid);
				int len = str.length();
				StringBuilder sb = new StringBuilder();
				sb.append(Constant.ROOTPATH).append("img").append(File.separator).append("user").append(File.separator).append(str.substring(len - 3)).append(File.separator).append(str).append(File.separator);
				sb.append(mgr.getString("sid").toLowerCase()).append(".jpg");
				FileUtils.copyFile(f, new File(sb.toString()));
			}
		} catch (IOException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
