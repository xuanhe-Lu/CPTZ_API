package com.ypm.fjs.set;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.commons.lang.TimeUtils;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.FileInfo;
import com.ypm.bean.SetClient;
import com.ypm.service.ConfigInfoService;

public class ClientMgr extends Action {

	private static final long serialVersionUID = 477622551907116743L;
	
	// 接收apk文件域
	private File files;
		
	// 接收apk文件名
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

	private static final int ADT = ConfigInfoService.ADT_YPLC;

	private ConfigInfoService configInfoService;

	public ConfigInfoService getConfigInfoService() {
		return configInfoService;
	}

	public void setConfigInfoService(ConfigInfoService configInfoService) {
		this.configInfoService = configInfoService;
	}

	private int getTid() {
		return (ADT + getInt("tid"));
	}

	public String getInfo() {
		int sid = this.getInt("Sid");
		AjaxInfo json = this.getAjaxInfo();
		try {
			if (sid == 0) {
				int tid = this.getTid(); // 类型信息
				SetClient s = this.getConfigInfoService().findClientByTid(tid);
				json.data();
				json.append("CODE", s.newCode());
				json.append("CODEVER", s.newVer());
				json.append("FILENAME", "");
				json.append("CONTENT", "");
				json.append("TDAY", TimeUtils.getToday());
				json.append("STATE", STATE_DISABLE);
			} else {
				SetClient s = this.getConfigInfoService().findClientBySid(sid);
				if (s == null) {
					json.addError(this.getText("system.error.none"));
				} else {
					json.data();
					json.append("CODE", s.getCode());
					json.append("CODEVER", s.getCodever());
					json.append("FILENAME", s.getFilename());
					json.append("CONTENT", s.getContent());
					json.append("TDAY", s.getTday());
					json.append("STATE", s.getState());
				}
			}
		} catch (Exception e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}

	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		sql.append(" AND Tid=?");
		fs.add(getTid());
		StringBuilder sb = new StringBuilder();
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null || sort.equalsIgnoreCase("Sid")) {
			sb.append("Sid");
			sort = "Sid";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Sid")) {
			if (dir == null || dir.equalsIgnoreCase("DESC")) {
				sb.append(" DESC");
			} else {
				sb.append(" ASC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("DESC")) {
			sb.append(" DESC,Sid DESC");
		} else {
			sb.append(" ASC,Sid DESC");
		} // 加载数据信息
		this.setAjaxInfo(this.getConfigInfoService().findClientByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}
	
	/**
	 * @author xk
	 * @return String 
	 * 
	 * 保存安卓版本信息
	 */
	public String saveAndroid() {
		AjaxInfo json = this.getAjaxInfo();
		FileInfo fileInfo = FileInfo.get( files, 1, false );
		double size = files != null && files.length() != 0 ? new BigDecimal((float)files.length()/1048576).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() : 0;
		SetClient s = null;
		
		try {
			int sid = getInt( "Sid" );
			
			if (sid <= 0) {// 新增保存
				if (!fileInfo.isFile()) {// 非文件
					json.addError(this.getText( "coo.error.033" ));
				} else if (fileInfo.getSize() > APK_FILE_MAX_SIZE) { // 文件大小超出限制
					json.addError(this.getText( "upload.error.smax", new String[] { String.valueOf(fileInfo.getSize()), String.valueOf(FILE_MAX_SIZE) } ));
				} else {
					s = new SetClient();
					s.setSid(0);
					s.setTid(getTid());
				}
			} else { // 修改保存
				s = this.getConfigInfoService().findClientBySid(sid);
				if (s == null) {
					json.addError(this.getText( "system.error.none" ));
					return JSON;
				}
			}
			
			if (filesFileName != null) {
				s.setSize(size);
				s.setFilename(filesFileName);
			}
			s.setCodever(getString( "Codever" ));
			s.setContent(getString( "Content" ));
			s.setState(getInt( "State" ));
			s.setTday(TimeUtils.getOKDay(getString( "Tday" )));
			
			if (s != null) {
				if (fileInfo.getSize() > 0) {
					fileInfo.setPid(String.valueOf(s.getSid()));
					fileInfo.setName( filesFileName );
				}
				fileInfo.setRule( 1080, 1920, true );
				this.getConfigInfoService().saveClient( s, fileInfo );
				json.addMessage(this.getText( "data.save.succeed" ));
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addError(this.getText( "data.save.failed" ));
		}
		
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		SetClient s = null;
		try {
			int sid = getInt("Sid");
			if (sid <= 0) {
				s = new SetClient();
				s.setSid(0);
				s.setTid(getTid());
			} else {
				s = this.getConfigInfoService().findClientBySid(sid);
				if (s == null) {
					json.addError(this.getText("system.error.none"));
					return JSON;
				}
			}
			s.setCodever(getString("Codever"));
			s.setContent(getString("Content"));
			s.setState(getInt("State"));
			s.setTday(TimeUtils.getOKDay(getString("Tday")));
			this.getConfigInfoService().saveClient(s);
			json.addMessage(this.getText("data.save.succeed"));
		} catch (IOException | SQLException e) {
			json.addError(this.getText("data.save.failed"));
		}
		return JSON;
	}

	public String state() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (ids == null) {
				json.addError(this.getText("system.error.pars"));
			} else {
				this.getConfigInfoService().updateClient(ADT, ids, getInt("State"));
				json.addMessage(this.getText("data.update.succeed"));
			}
		} catch (IOException | SQLException e) {
			json.addError(this.getText("data.update.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

	public String delete() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		
		try {
			if (ids == null) {
				json.addError(this.getText("system.error.pars"));
			} else {
				this.getConfigInfoService().removeClient(ADT, ids);
				json.addMessage(this.getText("data.delete.succeed"));
			}
		} catch (IOException | SQLException e) {
			json.addError(this.getText("data.delete.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

	public String export() {
		this.getAjaxInfo().addError(getText("system.error.export"));
		return JSON;
	}
}
