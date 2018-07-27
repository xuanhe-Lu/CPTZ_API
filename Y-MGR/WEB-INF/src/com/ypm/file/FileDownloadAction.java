package com.ypm.file;

import java.io.File;
import java.io.InputStream;

import org.commons.lang.StringUtils;

import com.sunsw.struts.ServletActionContext;
import com.sunsw.xwork2.ActionSupport;
import com.ypm.util.Constant;

/**
 * Created by xk on 2018-07-02.
 * 
 * 文件下载 Action.
 */
public class FileDownloadAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;

	// 文件名
	private String fileName;
	
	// 返回的文件流
    private InputStream inputStream;
    
    public String execute(){
    	if (StringUtils.isEmpty(fileName)) {
    		return ERROR;
    	}
        this.inputStream = ServletActionContext.getServletContext().getResourceAsStream( File.separator + Constant.EXPORTS + File.separator + fileName );
        return this.inputStream == null ? ERROR : SUCCESS;
    }
    
    public InputStream getInputStream() {
        return inputStream;
    }
    
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
