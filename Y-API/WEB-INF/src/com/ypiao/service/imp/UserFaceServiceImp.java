package com.ypiao.service.imp;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import org.commons.io.FileUtils;
import com.ypiao.bean.UserFacer;
import com.ypiao.data.JPrepare;
import com.ypiao.service.UserFaceService;
import com.ypiao.util.Constant;
import com.ypiao.util.VeImage;
import com.ypiao.util.VeStr;

public class UserFaceServiceImp extends AConfig implements UserFaceService {

	private static final String TBL_USER_FACER = "user_face";

	private static final String destName = ".jpg";

	private static final int[] FSS = { 120, 160, 240 };

	protected void checkSQL() {
	}

	private void copy(StringBuilder sb, File dest, File fP, int ver) {
		try {
			if (dest.isFile()) {
				sb.setLength(0);
				sb.append(fP.getPath()).append(File.separator).append(ver).append(destName);
				FileUtils.copyFile(dest, new File(sb.toString()), true);
			} // 删除缓存信息
			for (int size : FSS) {
				sb.setLength(0);
				sb.append(fP.getPath()).append(File.separator).append('f').append(size).append(destName);
				FileUtils.delete(sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int findFaceByUid(long uid) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			int ver = 1; // 默认版本号
			ps = conn.prepareStatement("SELECT MAX(Ver) FROM " + TBL_USER_FACER + " WHERE Uid=?");
			ps.setLong(1, uid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ver += rs.getInt(1);
			}
			rs.close();
			return ver;
		} finally {
			JPrepare.close(ps, conn);
		}
	}

	public void saveFile(UserFacer f, File file, boolean sync) {
		StringBuilder sb = new StringBuilder(Constant.FILEPATH);
		try {
			VeStr.getUdir(sb.append("user").append(File.separator), f.getUid());
			File fP = new File(sb.append(File.separator).append(f.getUid()).append(File.separator).append("face").toString());
			File dest = new File(sb.append(File.separator).append("big").append(destName).toString());
			if (fP.exists()) {
				this.copy(sb, dest, fP, (f.getVer() - 1));
			} else {
				fP.mkdirs(); // 生成目录
			} // 复制原图信息
			FileUtils.copyFile(file, dest);
			sb.setLength(0); // 生成迷你小图
			sb.append(fP.getPath()).append(File.separator).append('f').append(FSS[0]).append(destName);
			VeImage.waterJPG(file, sb.toString(), FSS[0], 0);
			if (sync) {
				// SyncMap.getAll().sender(SYS_A122, "facer", f, dest);
			}
		} catch (IOException e) {
			// Ignored
		} finally {
			sb.setLength(0);
			if (file.isFile()) {
				file.delete();
			}
		}
	}

	@Override
	public void saveFace(UserFacer f, File file, boolean sync) throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE " + TBL_USER_FACER + " SET Ver=?,Num=?,State=?,Time=? WHERE Sid=?");
			ps.setInt(1, f.getVer());
			ps.setInt(2, f.getNum());
			ps.setInt(3, f.getState());
			ps.setLong(4, f.getTime());
			ps.setLong(5, f.getSid());
			if (ps.executeUpdate() <= 0) {
				ps.close();
				ps = conn.prepareStatement("INSERT INTO " + TBL_USER_FACER + " (Sid,Uid,Ver,Num,State,Time) VALUES (?,?,?,?,?,?)");
				ps.setLong(1, f.getSid());
				ps.setLong(2, f.getUid());
				ps.setInt(3, f.getVer());
				ps.setInt(4, f.getNum());
				ps.setInt(5, f.getState());
				ps.setLong(6, f.getTime());
				ps.executeUpdate();
			}
			ps.close();
			ps = conn.prepareStatement("UPDATE user_info SET Facer=? WHERE Uid=?");
			ps.setInt(1, f.getVer());
			ps.setLong(2, f.getUid());
			ps.executeUpdate();
			this.execute(() -> this.saveFile(f, file, sync));
		} finally {
			JPrepare.close(ps, conn);
		}
	}
}
