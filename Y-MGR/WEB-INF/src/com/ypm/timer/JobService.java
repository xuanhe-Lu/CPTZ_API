package com.ypm.timer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.commons.lang.SystemUtils;
import com.ypm.bean.SiteJobs;
import com.ypm.data.JPrepare;
import com.ypm.util.Constant;
import com.ypm.util.GMTime;

public class JobService {
	/** 初始化服务器信息 */
	public static boolean initServer() throws SQLException {
		Connection conn = JPrepare.getConnection();
		PreparedStatement ps = null;
		try {
			loadSystem(0); // 加载系统信息 time=下次操作时间,todo=运行时间
			//long time = GMTime.currentTimeMillis();
			ps = conn.prepareStatement("UPDATE comm_jobs SET State=? WHERE State=?");
			ps.setInt(1, 2);
			ps.setInt(2, 1);
			ps.executeUpdate();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
	private static void loadSystem(final int times) {
		List<MBeanServer> ls = javax.management.MBeanServerFactory.findMBeanServer(null);
		if (ls == null || ls.size() <= 0) return;
		MBeanServer server = ls.get(0);
		String line, regex = null;
		try {
			Integer port = null;
			Set<ObjectName> names = server.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
			Iterator<ObjectName> it = names.iterator();
			while (it.hasNext()) {
				ObjectName objs = it.next();
				String svalue = (String) server.getAttribute(objs, "scheme");
				if (!svalue.equalsIgnoreCase("http")) continue;
				String pvalue = (String) server.getAttribute(objs, "protocol");
				port = (Integer) server.getAttribute(objs, "port");
				if (pvalue.toLowerCase().indexOf("http") != -1) {
					Constant.SERVER_PORT = port.intValue();
				} else if (pvalue.toLowerCase().indexOf("ajp") != -1) {
					Constant.SERVER_AJP = port.intValue();
				}
			} // 读取服务进程
			if (SystemUtils.IS_OS_WINDOWS) {
				Process proc = Runtime.getRuntime().exec("cmd /c netstat -ano|findstr :"+ port);
				BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				StringBuilder sb = new StringBuilder();
				regex = sb.append(":").append(port).append("\\s(.*?)(\\d+)$").toString();
				while ((line = reader.readLine()) != null) {
					Matcher m = Pattern.compile(regex).matcher(line.trim());
					if (m.find()) {
						Constant.SERVER_PID = Integer.parseInt(m.group(2)); break;
					}
				}
				reader.close(); proc.destroy();
				if (Constant.SERVER_PID == 0) return;
				sb.setLength(0);
				regex = sb.append(Constant.SERVER_PID).append("(.*)$").toString();
				proc = Runtime.getRuntime().exec("cmd /c tasklist /svc|findstr "+ Constant.SERVER_PID);
				reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				while ((line = reader.readLine()) != null) {
					Matcher m = Pattern.compile(regex).matcher(line);
					if (m.find()) {
						Constant.SERVER_NAME = m.group(1).trim(); break;
					}
				}
				reader.close(); proc.destroy();
			} else if (SystemUtils.IS_OS_LINUX) {
				// Ignored
			} // 过滤异常
			if (times < 5 && Constant.SERVER_NAME.equals("暂缺")) {
				Thread.sleep(2000); // 2 秒
				loadSystem(times + 1);
			}
		} catch (Exception e) {
			// Ignored
		}
	}
	/** 获取操作对象 */
	public static List<SiteJobs> getSiteJobs(long time) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			ArrayList<SiteJobs> ls = new ArrayList<SiteJobs>();
			ps = JPrepare.prepareStatement(conn, "SELECT Id,Zone,Name,Sday,Eday,Outs,Times,Weeks,Stype,Script,Rlast,Rnext,Rtime,Runs,State FROM comm_jobs WHERE State=? AND Rnext<=?");
			ps.setInt(1, 2);
			ps.setLong(2, (time + 60000));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				SiteJobs job = new SiteJobs();
				job.setId(rs.getString(1));
				job.setZone(rs.getInt(2));
				job.setName(rs.getString(3));
				job.setSday(rs.getString(4));
				job.setEday(rs.getString(5));
				job.setOuts(rs.getString(6));
				job.setTimes(rs.getString(7));
				job.setWeeks(rs.getString(8));
				job.setType(rs.getInt(9));
				job.setScript(rs.getString(10));
				job.setNext(rs.getLong(12));
				job.setTime(rs.getLong(13));
				ls.add(job);
				rs.updateInt(15, 1);
				rs.updateRow(); // update state
			}
			rs.close();
			ls.trimToSize(); return ls;
		} catch (Exception e) {
			return null;
		} finally {
			JPrepare.close(ps, conn);
		}
	}
	/** 更新操作对象 */
	public static void saveSiteJobs(SiteJobs job) {
		//VeTime.getRNext(job.getSday(), job.getEday(), job.getTimes(), job.getOuts(), job.getWeeks());
		updaeSiteJobs(job);
	}
	/** 更新定时对象 */
	public static void updaeSiteJobs(SiteJobs job) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = JPrepare.getConnection();
			ps = conn.prepareStatement("UPDATE comm_jobs SET Rlast=?,Rnext=?,State=? WHERE Id=?");
			ps.setLong(1, GMTime.currentTimeMillis());
			ps.setLong(2, job.getNext());
			ps.setInt(3, 2); // 执行完毕
			ps.setString(4, job.getId());
			ps.executeUpdate();
		} catch (Exception e) {
			// ignored
		} finally {
			JPrepare.close(ps, conn);
		}
	}
	/** 系统数据处理 */
	public static void execsys(String script, long time) {
		String type = script.toLowerCase();
		try {
			if (type.equals("useronline")) {
				//SysBeaner.getOnlineService().removeOnlineByTimeout(time - 60 * 60000);
			} else if (type.equals("delerror")) {
				
			} else if (type.equals("delspider")) {
				//PoolService.getService().execute(new RunAtDelete(false));
			} else if (type.equals("delcache")) {
				
			} else if (type.equals("reindex")) {
				//PoolService.getService().execute(new RunAtIndexs());
			}
		} catch (Exception e) {
			// Ignored
		}
	}
	/** 系统SQL脚本 */
	public static boolean execute(String sql) {
		try {
			JPrepare.execute(sql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
