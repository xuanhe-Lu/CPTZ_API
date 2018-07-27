package com.ypm.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.commons.io.FileUtils;

public class VeFile {

	private static final String NR = System.getProperty("line.separator");

	private static final String DIR_IMG = "img";

	private static final String IMG_JPG = "jpg";

	private static final String SUFFIX = ".txt";

	public static final void delete(File file) {
		if (file == null) {
			// Ignored
		} else if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				delete(f);
			}
			if (file.listFiles().length <= 0) {
				file.delete();
			}
		}
	}

	public static final void delete(String filePath) {
		delete(new File(filePath));
	}

	public static final void deletes(String filePath) {
		StringBuilder sb = new StringBuilder();
		sb.append(Constant.ROOTPATH).append(Constant.OSCACHE).append(File.separator);
		delete(sb.append(filePath).toString());
	}

	public static final void deletes(String fns, String... dirs) {
		StringBuilder sb = new StringBuilder();
		sb.append(Constant.ROOTPATH).append(Constant.OSCACHE).append(File.separator);
		for (String dir : dirs) {
			sb.append(dir).append(File.separator);
		}
		delete(sb.append(fns).append(SUFFIX).toString());
	}

	public static final void deletes(String dir, String[] ts, String id) {
		StringBuilder sb = new StringBuilder();
		for (String fix : ts) {
			sb.append(Constant.ROOTPATH).append(Constant.OSCACHE).append(File.separator);
			sb.append(dir).append(File.separator).append(fix);
			delete(sb.append(id).append(SUFFIX).toString());
			sb.setLength(0);
		}
	}

	public static final void delFile(String dir, String[] ids) {
		StringBuilder sb = new StringBuilder();
		for (String sid : ids) {
			delete(getPath(sb, sid.substring(6), IMG_JPG, dir, sid.substring(0, 6)));
		}
	}

	public static File getFile(String dir, String sid) {
		return getFile(new StringBuilder(), dir, sid);
	}

	public static File getFile(StringBuilder sb, String dir, String sid) {
		return new File(getPath(sb, dir, sid));
	}

	public static final String getPath(StringBuilder sb, String dir, String sid) {
		return getPath(sb, sid.substring(6), IMG_JPG, dir, sid.substring(0, 6));
	}

	private static final String getPath(StringBuilder sb, String name, String ext, String... args) {
		if (sb.length() > 0) {
			sb.setLength(0);
		}
		sb.append(Constant.ROOTPATH);
		for (String arg : args) {
			sb.append(arg).append(File.separator);
		} // 加载文件信息
		return sb.append(name.toLowerCase()).append('.').append(ext).toString();
	}

	public static final String getPaths(StringBuilder sb, String dir, String sid) {
		return getPath(sb, sid.substring(8), IMG_JPG, dir, sid.substring(0, 6), sid.substring(6, 8));
	}

	public static final File getPics(String Pid) {
		StringBuilder sb = new StringBuilder();
		try {
			return new File(getPaths(sb, DIR_IMG, Pid));
		} finally {
			sb.setLength(0);
		}
	}

	public static final boolean isImage(File file) {
		String type = VeImage.getFileType(file);
		if (type == null) {
			return false;
		} else {
			return (type.equalsIgnoreCase("JPEG") || type.equalsIgnoreCase("JPG"));
		}
	}

	public static final boolean isJPEG(File file) {
		String type = VeImage.getFileType(file);
		if (type == null) {
			return false;
		} else {
			return (type.equalsIgnoreCase("JPEG") || type.equalsIgnoreCase("JPG"));
		}
	}

	public static final StringBuilder readFile(StringBuilder sb, String dir, String fileName, String ext) {
		StringBuilder s = new StringBuilder();
		try {
			s.append(Constant.ROOTPATH);
			if (dir != null) {
				s.append(dir.replace("/", File.separator)).append(File.separator);
			}
			File file = new File(s.append(fileName).append(ext).toString());
			if (file.isFile()) {
				sb.append(FileUtils.readFile(file, Constant.CHARSET).trim()).append(NR);
			} else {
				System.out.println(file.getPath());
			}
		} catch (IOException e) {
			// Ignored
		}
		return sb;
	}

	public static final void saveJPEG(String filePath, File file) throws IOException {
		File dest = new File(filePath);
		if (isJPEG(file)) {
			FileUtils.copyFile(file, dest);
		} else {
			ImageIO.write(ImageIO.read(file), "JPEG", dest);
		}
	}

	/** 保存本地文件->dir */
	public static final File toFile(File file, String dir, String sid) throws IOException {
		File dest = null;
		if (file != null && file.isFile()) {
			dest = getFile(dir, sid);
			FileUtils.copyFile(file, dest);
		}
		return dest;
	}

	/** 非JPEG->JPEG */
	public static final File toJPEG(File file) throws IOException {
		File out = new File(file.getPath() + ".jpg");
		RenderedImage im = ImageIO.read(file);
		BufferedImage c = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = c.createGraphics();
		try {
			g.drawImage((Image) im, 0, 0, Color.WHITE, null);
			ImageIO.write(c, "JPEG", out);
		} finally {
			g.dispose();
		}
		return out;
	}

}
