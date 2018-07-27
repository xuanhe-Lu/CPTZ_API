package com.ypiao.bean;

import java.io.File;
import java.io.IOException;
import org.commons.io.FileUtils;
import com.ypiao.util.Constant;

public class FileInfo extends FileBase {

	private static final long serialVersionUID = 3787364330729267774L;

	private static File getFile(boolean img, String Pid, String dist) {
		StringBuilder sb = new StringBuilder(Constant.FILEPATH);
		if (img) {
			sb.append("img");
		} else {
			sb.append("file");
		}
		sb.append(File.separator).append(Pid.substring(0, 6)).append(File.separator).append(Pid.substring(6).toLowerCase()).append('.').append(dist);
		File f = new File(sb.toString());
		File fP = f.getParentFile();
		if (fP.exists()) {
			// Ignroed
		} else {
			fP.mkdirs();
		}
		return f;
	}
	
	/**
	 * @author xk
	 * @param fileName String [文件名]
	 * @param dist String [文件格式]
	 * 
	 * apk保存文件
	 */
	private static File getApkFile(String fileName, String dist) {
		StringBuilder sb = new StringBuilder(Constant.ROOTPATH);
		sb.append( "app" ).append(File.separator).append( "download" );
		sb.append(File.separator).append(fileName.substring( 0, fileName.lastIndexOf('.') )).append( '.' ).append(dist);
		
		File saveFile = new File(sb.toString());
		File parentFile = saveFile.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		
		return saveFile;
	}

	//private int destroy = 0;

	private File dest;

	private boolean img = false;

	private String sid;

	private int tid = 0;

	private int sortid = 0;

	private int state = 0;

	private long time = 0;

	public void saveFile(File file) throws IOException {
		if (file == null) {
			// Ignored
		} else if (file.isFile()) {
			dest = getFile(isImg(), getPid(), getDist());
			FileUtils.copyFile(file, dest);
		}
	}
	
	/**
	 * @author xk
	 * @return boolean 是否保存成功
	 * @throws IOException
	 * 
	 * 保存apk文件
	 */
	public boolean saveApkFile(File file) throws IOException {
		if (file == null || !file.isFile()) {
			return false;
		} 
		
		// 构建保存地址
		dest = getApkFile( getName(), getDist() );
		this.setPdw(0);
		this.setPdh(0);
		FileUtils.copyFile( file, dest );
		// 文件大小
		this.setSize(dest.length());
		
		return true;
	}

	public boolean isImg() {
		return img;
	}

	public void setImg(boolean img) {
		this.img = img;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getSortid() {
		return sortid;
	}

	public void setSortid(int sortid) {
		this.sortid = sortid;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
