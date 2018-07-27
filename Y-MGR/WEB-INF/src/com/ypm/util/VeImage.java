package com.ypm.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.commons.io.FileUtils;

public class VeImage {

	private static String formatName(File file, String def) throws IOException {
		ImageInputStream iis = ImageIO.createImageInputStream(file);
		try {
			Iterator<ImageReader> it = ImageIO.getImageReaders(iis);
			if (it.hasNext()) {
				return it.next().getFormatName();
			} else {
				return def;
			}
		} finally {
			iis.close();
		}
	}
	/**
	 * 获取图片文件类型
	 * @param file	File
	 */
	public static final String getFileType(File file) {
		if (file == null || !file.isFile()) {
			return null;
		}
		try {
			return formatName(file, null);
		} catch (IOException e) {
			return null;
		}
	}

	public static final String getFileType(String file) {
		return getFileType(new File(file));
	}
	/**
	 * 根据参数以原图为准(等比缩放)获取新宽度和高度
	 * @param sw	原宽度
	 * @param sh	原高度
	 * @param w		水宽度
	 * @param h		水高度
	 */
	public static int[] getInSize(int sw, int sh, int w, int h) {
		int[] fs = { sw, sh };
		if (sw > w) {
			if (w < 1) w = sw;
			if (h < 1) h = sh;
			int aw = sw * h;
			int ah = sh * w;
			if (aw > ah) {
				fs[0] = w;
				fs[1] = (ah / sw);
			} else {
				fs[0] = (aw / sh);
				fs[1] = h;
			}
		} else if (sh > h && h > 1) {
			fs[0] = (sw * h / sh);
			fs[1] = h;
		}
		return fs;
	}

	/**
	 * 根据参数以水印为准(等比缩放)获取新宽度和高度
	 * @param sw	原宽度
	 * @param sh	原高度
	 * @param w		水宽度
	 * @param h		水高度
	 */
	public static int[] getFixSize(int sw, int sh, int w, int h) {
		int[] fs = { sw, sh };
		if (w <= 0) {
			if (h > 0) {
				fs[0] = (sw * h / sh);
				fs[1] = h;
			}
		} else if (h <= 0) {
			fs[0] = w;
			fs[1] = (sh * w / sw);
		} else {
			int aw = sw * h;
			int ah = sh * w;
			if (aw > ah) {
				fs[0] = w;
				fs[1] = (ah / sw);
			} else {
				fs[0] = (aw / sh);
				fs[1] = h;
			}
		}
		return fs;
	}

	private static void makeDir(File file) {
		if (file == null) return;
		File f = file.getParentFile();
		if (f.exists()) {
			// Ignored
		} else {
			f.mkdirs();
		}
	}

	public static int[] waterJPG(File src, File dest, int width, int height) throws IOException {
		return waterJPG(src, dest, width, height, true);
	}

	public static int[] waterJPG(File src, File dest, int width, int height, boolean zoom) throws IOException {
		BufferedImage bs = ImageIO.read(src);
		try {
			int[] fs = { 0, 0, 0 };
			int w = bs.getWidth(null);
			int h = bs.getHeight(null);
			if (w == width && h == height) {
				fs[0] = w;
				fs[1] = h;
				FileUtils.copyFile(src, dest);
			} else {
				makeDir(dest);
				if (zoom || width == 0 || height == 0) {
					int[] wh = getInSize(w, h, width, height);
					fs[0] = wh[0];
					fs[1] = wh[1];
				} else {
					fs[0] = width;
					fs[1] = height;
				}
				String format = formatName(src, "JPEG").toUpperCase();
				BufferedImage tag = new BufferedImage(fs[0], fs[1], bs.getType()); // SCALE_AREA_AVERAGING
				tag.getGraphics().drawImage(bs.getScaledInstance(fs[0], fs[1], Image.SCALE_AREA_AVERAGING), 0, 0, null);
				ImageIO.write(tag, format, dest);
				tag.flush();
			}
			fs[2] = (int) dest.length();
			return fs;
		} catch (IOException e) {
			throw e;
		} finally {
			bs.flush();
		}
	}

	public static int[] waterJPG(File file, String savePath, int width, int height) throws IOException {
		return waterJPG(file, new File(savePath), width, height, true);
	}

	public static int[] waterJPG(File file, String savePath, int width, int height, boolean zoom) throws IOException {
		return waterJPG(file, new File(savePath), width, height, zoom);
	}

}
