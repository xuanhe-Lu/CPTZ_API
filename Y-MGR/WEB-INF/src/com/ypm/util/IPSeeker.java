package com.ypm.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.commons.logs.Logger;
import org.commons.logs.LoggerFactory;

public class IPSeeker {

	private static Logger loger = LoggerFactory.getLogger(IPSeeker.class);

	private class IPLocation {

		public String country;

		public String area;

		public IPLocation() {
			country = area = "";
		}

		public IPLocation getCopy() {
			IPLocation ret = new IPLocation();
			String TCountry = country.replaceAll("CZ88.NET","");
			ret.country = TCountry.replaceAll("\u7EAF\u771F\u7F51\u7EDC","\u6E05\u65B0\u7F51\u7EDC");
			ret.area = area.replaceAll("CZ88.NET","");
			return ret;
		}
	}

	private static final int IP_RECORD_LENGTH = 7;

	private static final byte REDIRECT_MODE_1 = 0x01;

	private static final byte REDIRECT_MODE_2 = 0x02;

	private Hashtable<String, IPLocation> ipCache;

	private RandomAccessFile ipFile;

	private long ipBegin, ipEnd;

	private IPLocation loc;

	private byte[] bs;

	private byte[] b4;

	private byte[] b3;

	private static final String IPDATE_FILE_PATH = Constant.ROOTPATH + "WEB-INF/IPdata/";

	private static final String IPDATE_FILE = "qqwry.dat";

	public IPSeeker() {
		ipCache = new Hashtable<String, IPLocation>();
		loc = new IPLocation();
		bs = new byte[100];
		b4 = new byte[4];
		b3 = new byte[3];
		File file = new File(IPDATE_FILE_PATH + IPDATE_FILE);
		try {
			ipFile = new RandomAccessFile(file, "r");
		} catch (FileNotFoundException e) {
			File fP = new File(IPDATE_FILE_PATH);
			if (fP.exists()) {
				String fn = file.getName();
				File[] fs = fP.listFiles();
				for (File f : fs) {
					if (f.isFile() && f.getName().equalsIgnoreCase(fn)) {
						try {
							ipFile = new RandomAccessFile(f, "r");
						} catch (FileNotFoundException e1) {
							loger.error("No found he address information document of IP!");
							ipFile = null;
						}
						break;
					}
				}
			}
		}
		if (ipFile != null) {
			try {
				ipBegin = readLong4(0);
				ipEnd = readLong4(4);
				if (ipBegin == -1 || ipEnd == -1) {
					ipFile.close();
					ipFile = null;
				}
			} catch (IOException e) {
				loger.error("Address data format mistake of IP!");
				ipFile = null;
			}
		}
	}

	public String getCountry(byte[] ip) {
		if (ipFile == null) {
			return "bad.ip.file";
		}
		String ipStr = getIpStringFromBytes(ip);
		if (ipCache.containsKey(ipStr)) {
			IPLocation loc = (IPLocation) ipCache.get(ipStr);
			return loc.country;
		} else {
			IPLocation loc = getIPLocation(ip);
			ipCache.put(ipStr, loc.getCopy());
			return loc.country;
		}
	}

	public String getCountry(String ip) {
		return getCountry(getIpByteArrayFromString(ip));
	}

	public String getArea(byte[] ip) {
		if (ipFile == null) {
			return "bad.ip.file";
		}
		String area = "";
		String ipStr = getIpStringFromBytes(ip);
		if (ipCache.containsKey(ipStr)) {
			IPLocation loc = (IPLocation) ipCache.get(ipStr);
			area = loc.area;
		} else {
			IPLocation loc = getIPLocation(ip);
			ipCache.put(ipStr, loc.getCopy());
			area = loc.area;
		}
		return area;
	}

	public String getArea(String ip) {
		return getArea(getIpByteArrayFromString(ip));
	}

	private IPLocation getIPLocation(byte[] ip) {
		IPLocation info = null;
		long offset = locateIP(ip);
		if (offset != -1) {
			info = getIPLocation(offset);
		}
		if (info == null) {
			info = new IPLocation();
			info.country = "";
			info.area = "";
		}
		return info;
	}

	private long readLong4(long offset) {
		long ret = 0;
		try {
			ipFile.seek(offset);
			ret |= (ipFile.readByte() & 0xFF);
			ret |= ((ipFile.readByte() << 8) & 0xFF00);
			ret |= ((ipFile.readByte() << 16) & 0xFF0000);
			ret |= ((ipFile.readByte() << 24) & 0xFF000000);
			return ret;
		} catch (IOException e) {
			return -1;
		}
	}

	private long readLong3(long offset) {
		long ret = 0;
		try {
			ipFile.seek(offset);
			ipFile.readFully(b3);
			ret |= (b3[0] & 0xFF);
			ret |= ((b3[1] << 8) & 0xFF00);
			ret |= ((b3[2] << 16) & 0xFF0000);
			return ret;
		} catch (IOException e) {
			return -1;
		}
	}

	private long readLong3() {
		long ret = 0;
		try {
			ipFile.readFully(b3);
			ret |= (b3[0] & 0xFF);
			ret |= ((b3[1] << 8) & 0xFF00);
			ret |= ((b3[2] << 16) & 0xFF0000);
			return ret;
		} catch (IOException e) {
			return -1;
		}
	}

	private void readIP(long offset, byte[] ip) {
		try {
			ipFile.seek(offset);
			ipFile.readFully(ip);
			byte temp = ip[0];
			ip[0] = ip[3];
			ip[3] = temp;
			temp = ip[1];
			ip[1] = ip[2];
			ip[2] = temp;
		} catch (IOException e) {
			loger.error(e.getMessage());
		}
	}

	private int compareIP(byte[] ip, byte[] beginIp) {
		for (int i = 0; i < 4; i++) {
			int r = compareByte(ip[i], beginIp[i]);
			if (r != 0) {
				return r;
			}
		}
		return 0;
	}

	private int compareByte(byte b1, byte b2) {
		if ((b1 & 0xFF) > (b2 & 0xFF)) {
			return 1;
		} else if ((b1 ^ b2) == 0) {
			return 0;
		} else {
			return -1;
		}
	}

	private long locateIP(byte[] ip) {
		long m = 0;
		int r;
		readIP(ipBegin, b4);
		r = compareIP(ip, b4);
		if (r == 0) {
			return ipBegin;
		} else if (r < 0) {
			return -1;
		}
		for (long i = ipBegin, j = ipEnd; i < j;) {
			m = getMiddleOffset(i, j);
			readIP(m, b4);
			r = compareIP(ip, b4);
			if (r > 0) {
				i = m;
			} else if (r < 0) {
				if (m == j) {
					j -= IP_RECORD_LENGTH;
					m = j;
				} else {
					j = m;
				}
			} else {
				return readLong3(m + 4);
			}
		}
		m = readLong3(m + 4);
		readIP(m, b4);
		r = compareIP(ip, b4);
		if (r <= 0) {
			return m;
		} else {
			return -1;
		}
	}

	private long getMiddleOffset(long begin, long end) {
		long records = (end - begin) / IP_RECORD_LENGTH;
		records >>= 1;
		if (records == 0) {
			records = 1;
		}
		return begin + records * IP_RECORD_LENGTH;
	}

	private IPLocation getIPLocation(long offset) {
		try {
			ipFile.seek(offset + 4);
			byte b = ipFile.readByte();
			if (b == REDIRECT_MODE_1) {
				long countryOffset = readLong3();
				ipFile.seek(countryOffset);
				b = ipFile.readByte();
				if (b == REDIRECT_MODE_2) {
					loc.country = readString(readLong3());
					ipFile.seek(countryOffset + 4);
				} else {
					loc.country = readString(countryOffset);
				}
				loc.area = readArea(ipFile.getFilePointer());
			} else if (b == REDIRECT_MODE_2) {
				loc.country = readString(readLong3());
				loc.area = readArea(offset + 8);
			} else {
				loc.country = readString(ipFile.getFilePointer() - 1);
				loc.area = readArea(ipFile.getFilePointer());
			}
			return loc;
		} catch (IOException e) {
			return null;
		}
	}

	private String readArea(long offset) throws IOException {
		ipFile.seek(offset);
		byte b = ipFile.readByte();
		if (b == REDIRECT_MODE_1 || b == REDIRECT_MODE_2) {
			long areaOffset = readLong3(offset + 1);
			if (areaOffset == 0) {
				return "unknown.area";
			} else {
				return readString(areaOffset);
			}
		} else {
			return readString(offset);
		}
	}

	private String readString(long offset) {
		try {
			ipFile.seek(offset);
			int i;
			for (i = 0, bs[i] = ipFile.readByte(); bs[i] != 0; bs[++i] = ipFile.readByte()) {
				;
			}
			if (i != 0) {
				return getString(bs, 0, i, "GBK");
			}
		} catch (IOException e) {
			loger.error(e.getMessage());
		}
		return "";
	}

	public static String getIpStringFromBytes(byte[] ip) {
		StringBuffer sb = new StringBuffer();
		sb.append(ip[0] & 0xFF).append('.').append(ip[1] & 0xFF).append('.').append(ip[2] & 0xFF).append('.').append(ip[3] & 0xFF);
		return sb.toString();
	}

	public static String getString(byte[] b, int offset, int len, String encoding) {
		try {
			return new String(b, offset, len, encoding);
		} catch (UnsupportedEncodingException ex) {
			return new String(b, offset, len);
		}
	}

	public static byte[] getIpByteArrayFromString(String ip) {
		byte[] ret = new byte[4];
		StringTokenizer st = new StringTokenizer(ip, ".");
		try {
			ret[0] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[1] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[2] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[3] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
		} catch (Exception e) {
			loger.error(e.getMessage());
		}
		return ret;
	}
}
