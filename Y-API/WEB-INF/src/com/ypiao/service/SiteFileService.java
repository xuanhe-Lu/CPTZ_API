package com.ypiao.service;

import java.sql.SQLException;
import com.ypiao.bean.FileInfo;

public interface SiteFileService {

	public void save(FileInfo f) throws SQLException;

}
