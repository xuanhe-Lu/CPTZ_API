package com.ypm.coo;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.FileInfo;
import com.ypm.bean.Version;
import com.ypm.service.VersionService;
import com.ypm.util.GMTime;
import com.ypm.util.VeStr;

/**
 * app版本更新 Action.
 * 
 * Created by xk on 2018-06-06.
 */
public class VersionAction extends Action {

	private static final long serialVersionUID = 1L;
	
	// 接收文件域
	private File files;
	
	// 接收文件名
	private String filesFileName;
	
	public String getFilesFileName() {
		return filesFileName;
	}

	public void setFilesFileName(String filesFileName) {
		this.filesFileName = filesFileName;
	}

	public File getFiles() {
		return files;
	}

	public void setFiles(File files) {
		this.files = files;
	}
	
	// 注入 VersionService
	private VersionService versionService;
	
	public VersionService getVersionService() {
		return versionService;
	}

	public void setVersionService(VersionService versionService) {
		this.versionService = versionService;
	}

	/**
	 * @author xk
	 * @return String
	 * 
	 * 数据列表
	 */
	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();
		
		// 平台类型过滤
		int tid = this.getInt( "Tid" );
		if (tid >= 1) {
			sql.append( " AND Tid = ?" );
			fs.add(tid);
		}
		
		// 版本号过滤
		String num = this.getString( "Num" );
		if (num != null && !"".equals(num)) {
			sql.append( " AND Num = ?" );
			fs.add(num);
		} 
		
		// 升级方式过滤
		int mid = this.getInt( "Mid" );
		if (mid > 0) {
			sql.append( " AND Mid = ?" );
			fs.add(mid);
		}
		
		sb.setLength(0);
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase( "Sid" )) {
			sb.append( "Sid" );
			sort = "Sid";
		} else {
			sb.append(sort);
		} 
		
		// 加载二次排序
		if (sort.equalsIgnoreCase( "Sid" )) {
			if (dir == null || dir.equalsIgnoreCase( "DESC" )) {
				sb.append( " DESC" );
			} else {
				sb.append( " ASC" );
			}
		} else if (dir == null || dir.equalsIgnoreCase( "DESC" )) {
			sb.append( " DESC, Sid DESC" );
		} else {
			sb.append( " ASC, Sid ASC" );
		}
		
		// 加载数据信息
		AjaxInfo ajaxInfo = this.getVersionService().list( sql, fs, sb.toString(), getStart(), getLimit() );
		this.setAjaxInfo(ajaxInfo);
		
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 新增/编辑保存
	 */
	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		FileInfo fileInfo = FileInfo.get( files, 1, false );
		String sid = this.getString( "Sid" );
		double size = files != null && files.length() != 0 ? new BigDecimal((float)files.length()/1048576).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() : 0;
		
		try {
			Version version = null;
			// 编辑保存
			if (this.checkValid( sid, 5 )) {
				version = this.getVersionService().findVersionBySId(sid);
				if (version == null) {
					json.addError(this.getText( "system.error.none" ));
				} else if (version.getFilename() == null) {
					json.addError(this.getText( "coo.error.033" ));
					version = null; // apk文件不能为空
				} else {
					String num = this.getString( "Num" );
					int mid = this.getInt( "Mid" );
					String description = this.getString( "Description" );
					
					version.setNum(num);
					version.setMid(mid);
					if (filesFileName != null) {
						version.setSize(size);
						version.setFilename(filesFileName);
					}
					version.setDescription(description);
					version.setTime(System.currentTimeMillis());
				}
			} else if (!fileInfo.isFile()) {// 非文件
				json.addError(this.getText( "coo.error.033" ));
			} else if (fileInfo.getSize() > APK_FILE_MAX_SIZE) { // 文件大小超出限制
				json.addError(this.getText( "upload.error.smax", new String[] { String.valueOf(fileInfo.getSize()), String.valueOf(FILE_MAX_SIZE) } ));
			} else { // 新增保存
				version = new Version();
				version.setSid(VeStr.getUSid(true));
				version.setTid(1);
				version.setNum(this.getString( "Num" ));
				version.setMid(this.getInt( "Mid" ));;
				version.setSize(size);
				version.setFilename(filesFileName);
				version.setDescription(this.getString( "Description" ));
				version.setTime(System.currentTimeMillis());
			}
			
			if (version != null) {
				if (fileInfo.getSize() > 0) {
					fileInfo.setPid(version.getSid());
					fileInfo.setName( filesFileName );
				}
				fileInfo.setRule( 1080, 1920, true );
				this.getVersionService().saveVersion( version, fileInfo );
				json.addMessage(this.getText( "data.save.succeed" ));
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "data.save.failed" ));
		}
		
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 新增/编辑保存iOS
	 */
	public String saveIOS() {
		AjaxInfo json = this.getAjaxInfo();
		String sid = this.getString( "Sid" );
		
		try {
			Version version = null;
			// 编辑保存
			if (this.checkValid( sid, 5 )) {
				version = this.getVersionService().findVersionBySId(sid);
				if (version == null) {
					json.addError(this.getText( "system.error.none" ));
				} else {
					String num = this.getString( "Num" );
					int mid = this.getInt( "Mid" );
					String description = this.getString( "Description" );
					
					version.setNum(num);
					version.setMid(mid);
					version.setDescription(description);
					version.setTime(System.currentTimeMillis());
				}
			} else { // 新增保存
				version = new Version();
				version.setSid(VeStr.getUSid(true));
				version.setTid(2);
				version.setNum(this.getString( "Num" ));
				version.setMid(this.getInt( "Mid" ));
				version.setDescription(this.getString( "Description" ));
				version.setTime(System.currentTimeMillis());
			}
			
			if (version != null) {
				this.getVersionService().saveVersion(version);
				json.addMessage(this.getText( "data.save.succeed" ));
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "data.save.failed" ));
		}
		
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 删除
	 */
	public String delete () {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString( "Ids" );
		
		try {
			if (this.checkValid( ids, 5 )) {
				this.getVersionService().remove(ids);
				json.addMessage(this.getText( "data.delete.succeed" ));
			} else {
				json.addError(this.getText( "system.error.pars" ));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			json.addError(this.getText( "data.delete.failed" ));
		}
		
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String
	 * 
	 * 查看预览
	 */
	public String view() {
		AjaxInfo json = this.getAjaxInfo();
		String sid = this.getString( "sid" );
		
		try {
			Version version = this.getVersionService().findVersionBySId(sid);
			if (version == null) {
				json.addError(this.getText( "system.error.none" ));
			} else {
				json.data();
				json.append( "SID", version.getSid() );
				json.append( "NUM", version.getNum() );
				json.append( "MID", version.getMid() );
				json.append( "SIZE", version.getSize() );
				json.append( "FILENAME", version.getFilename() );
				json.append( "DIST", version.getDist() );
				json.append( "DESCRIPTION", version.getDescription() );
				json.append( "TIME", GMTime.format( version.getTime(), GMTime.CHINA ) );
			}
		} catch (SQLException e) {
			json.addError(this.getText( "system.error.info" ));
		}
		
		return JSON;
	}
}

