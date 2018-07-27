package com.ypm.bean;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.commons.io.FileUtils;
import com.ypm.util.*;

public class FileInfo extends FileBase {

	private static final long serialVersionUID = 7511490656237671176L;

	private static final int JPEG = 1;

	private static final int JPG = 2;

	private static final int PNG = 3;

	private static final int SIZE = 12;

	private static List<FileInfo> CACHE = new ArrayList<FileInfo>(SIZE);

	public synchronized static FileInfo get(File file, int tid, boolean img) {
		FileInfo f = null;
		if (CACHE.size() > 0) {
			f = CACHE.remove(0);
		} else {
			f = new FileInfo();
		} // 文件及格式
		f.file = file;
		f.fix = 0;
		f.img = img;
		f.tid = tid;
		f.setPid(null);
		f.setDist(null);
		if (file == null) {
			f.setSize(0);
		} else {
			f.setSize(file.length());
		}
		return f;
	}

	private static File getFile(boolean img, String Pid, String dist) {
		StringBuilder sb = new StringBuilder(Constant.ROOTPATH);
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

	public static FileInfo getImg(File file, int tid) {
		return get(file, tid, true);
	}

	private boolean _zoom = false;

	private int _width = 0;

	private int _height = 0;

	private int destroy = 0;

	private File dest, file;

	private int fix = 0;

	private boolean img = false;

	private boolean oks = false;

	private String sid;

	private int tid = 0;

	private int sortid = 0;

	private int state = 0;

	private long time = 0;

	public FileInfo() {
	}

	public FileInfo add() {
		this.destroy++;
		return this;
	}

	public void destroy() {
		if (this.destroy-- > 0) {
			// Ignored
		} else if (CACHE.size() < SIZE) {
			this.setSortid(0);
			this.setPdw(0);
			this.setPdh(0);
			this.setSize(0);
			if (oks || dest == null) {
				// Ignored
			} else if (dest.isFile()) {
				dest.delete();
			}
			this.dest = null;
			CACHE.add(this);
		}
	}

	private void saveFile(BufferedImage im, int type) throws IOException {
		if (_zoom || _width <= 0 || _height <= 0) {
			int[] wh = VeImage.getInSize(Pdw, Pdh, _width, _height);
			this.setPdw(wh[0]);
			this.setPdh(wh[1]);
		} else {
			this.setPdw(_width);
			this.setPdh(_height);
		} // 保存图片文件
		BufferedImage c = new BufferedImage(getPdw(), getPdh(), im.getType()); // SCALE_AREA_AVERAGING
		try {
			c.getGraphics().drawImage(im.getScaledInstance(getPdw(), getPdh(), Image.SCALE_AREA_AVERAGING), 0, 0, null);
			if (type == PNG) {
				ImageIO.write(c, "PNG", dest);
			} else {
				ImageIO.write(c, "JPEG", dest);
			}
		} finally {
			c.flush();
		}
	}

	public boolean saveFile() throws IOException {
		if (file == null || !file.isFile()) {
			return false;
		} // 构建保存地址
		if (img) {
			dest = getFile(isImg(), getPid(), getDist());
			BufferedImage im = ImageIO.read(file);
			this.setPdw(im.getWidth());
			this.setPdh(im.getHeight());
			if (fix == PNG) {
				FileUtils.copyFile(file, dest);
			} else if (fix == JPG) {
				if (_width <= 0 && _height <= 0) {
					FileUtils.copyFile(file, dest);
				} else if (Pdw == _width && Pdh == _height) {
					FileUtils.copyFile(file, dest);
				} else {
					this.saveFile(im, fix);
				}
			} else {
				this.saveFile(im, fix);
			}
		} else {
			dest = getFile(isImg(), getPid(), getDist());
			this.setPdw(0);
			this.setPdh(0);
			FileUtils.copyFile(file, dest);
		} // 文件大小
		this.setSize(dest.length());
		return true;
	}
	
	/**
	 * @author xk
	 * @return boolean 是否保存成功
	 * @throws IOException
	 * 
	 * 保存apk文件
	 */
	public boolean saveApkFile() throws IOException {
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

	public void saveFile(File file) throws IOException {
		if (file == null) {
			// Ignored
		} else if (file.isFile()) {
			dest = getFile(isImg(), getPid(), getDist());
			FileUtils.copyFile(file, dest);
		}
	}

	public boolean saveFile(String fix, int id) throws IOException {
		if (file == null || !file.isFile()) {
			return false;
		}
		if (img) {
			String dist = this.getDist();
			this.setDist("png");
			StringBuilder sb = new StringBuilder();
			sb.append("img/").append(fix).append('_').append(id).append(".png");
			String fPath = sb.toString();
			sb.setLength(0);
			sb.append(Constant.ROOTPATH).append(fPath.replace("/", File.separator));
			dest = new File(sb.toString());
			File fP = dest.getParentFile();
			if (fP.exists()) {
				// Ignroed
			} else {
				fP.mkdirs();
			}
			RenderedImage im = ImageIO.read(file);
			this.setPdw(im.getWidth());
			this.setPdh(im.getHeight());
			if (dist.equalsIgnoreCase("png")) {
				FileUtils.copyFile(file, dest);
			} else {
				BufferedImage c = new BufferedImage(getPdw(), getPdh(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g = c.createGraphics();
				try {
					g.drawImage((Image) im, 0, 0, Color.WHITE, null);
					ImageIO.write(c, "PNG", dest);
				} finally {
					g.dispose();
				}
			} // 同步相关图片信息
			SyncMap.getAll().add("fPath", fPath).sender(APSKey.SYS_A996, "saveFile", this.doFile());
			return true;
		} else {
			return false;
		}
	}

	/** 同步数据图片 */
	public File doFile() {
		this.oks = true;
		return dest;
	}

	public boolean isFile() {
		return (file != null && file.isFile());
	}

	public boolean isImg() {
		return img;
	}

	public void setImg(boolean img) {
		this.img = img;
	}

	public boolean image() {
		return image(false);
	}

	public void setRule(int width, int height, boolean zoom) {
		this._zoom = zoom;
		this._width = width;
		this._height = height;
	}

	public boolean image(boolean def) {
		if (file == null || !file.isFile()) {
			return def;
		} // 检测处理文件
		String dist = VeImage.getFileType(file);
		if (dist == null) {
			this.setSize(0);
			return false;
		} // 检测转换为jpeg
		this.setDist(dist.toLowerCase());
		if (VeRule.isYes("(JPEG|JPG|PNG|BMP)", dist)) {
			if (syncs()) {
				// Ignored
			} else {
				this.setPid(VeStr.getUSid(true));
			} // 类型转换处理
			if (dist.equalsIgnoreCase("PNG")) {
				this.fix = PNG;
			} else if (dist.equalsIgnoreCase("JPEG") || dist.equalsIgnoreCase("JPG")) {
				this.setDist("jpg");
				this.fix = JPG;
			} else {
				this.setDist("jpg");
				this.fix = JPEG;
			}
			return true;
		} else {
			return false;
		}
	}

	/** 需要同步处理 */
	public boolean syncs() {
		String Pid = this.getPid();
		if (Pid == null) {
			return false;
		} else if (Pid.length() < 10) {
			this.setPid(null);
			return false;
		} else {
			return true;
		}
	}

	public String getPid(String Pid) {
		if (syncs()) {
			return getPid();
		} else {
			return Pid;
		}
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

	public void setTime(long sid, long time) {
		this.setSid(String.valueOf(sid));
		this.setTime(time);
	}

	public void setTime(String sid, long time) {
		this.setSid(sid);
		this.setTime(time);
	}
}
