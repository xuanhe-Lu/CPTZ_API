package com.ypiao.service.aps;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import org.commons.io.FileUtils;
import com.ypiao.bean.FileInfo;
import com.ypiao.bean.Manager;
import com.ypiao.service.SiteFileService;
import com.ypiao.util.Constant;

public class APIAt996 extends Abstract {

	private SiteFileService siteFileService;

	public SiteFileService getSiteFileService() {
		return siteFileService;
	}

	public void setSiteFileService(SiteFileService siteFileService) {
		this.siteFileService = siteFileService;
	}

	public void save(Manager mgr) {
		try {
			FileInfo f = mgr.getObject(FileInfo.class);
			this.getSiteFileService().save(f);
			f.saveFile(mgr.getFile());
		} catch (SQLException | IOException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void saveFile(Manager mgr) {
		String fPath = mgr.getString("fpath");
		try {
			File f = mgr.getFile();
			if (f == null || fPath == null) {
				// Ignored
			} else if (f.isFile()) {
				StringBuilder sb = new StringBuilder();
				sb.append(Constant.FILEPATH).append(fPath.replace("/", File.separator));
				FileUtils.copyFile(f, new File(sb.toString()));
			}
		} catch (IOException e) {
			mgr.addError(DATA_SAVE_FAILED);
		} finally {
			fPath = null;
		}
	}

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
				sb.append(Constant.FILEPATH).append("user").append(File.separator).append(str.substring(len - 3)).append(File.separator).append(str).append(File.separator);
				sb.append(mgr.getString("sid").toLowerCase()).append(".jpg");
				FileUtils.copyFile(f, new File(sb.toString()));
			}
		} catch (IOException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	/**
	 * @author xk
	 * @param mgr is Manager
	 * 
	 * 保存apk文件
	 */
	public void saveApkFile(Manager mgr) {
		try {
			FileInfo fileInfo = mgr.getObject(FileInfo.class);
			this.getSiteFileService().save(fileInfo);
			fileInfo.saveApkFile(mgr.getFile());
		} catch (SQLException | IOException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
