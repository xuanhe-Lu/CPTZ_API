package com.ypiao.service;

import java.io.File;
import java.sql.SQLException;
import com.ypiao.bean.UserFacer;

public interface UserFaceService {

	public int findFaceByUid(long uid) throws SQLException;

	public void saveFace(UserFacer f, File file, boolean sync) throws SQLException;

}
