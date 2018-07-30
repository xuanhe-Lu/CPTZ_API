package com.ypiao.json;

import java.io.File;
import org.apache.log4j.Logger;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.SyncMap;
import com.ypiao.bean.UserBker;
import com.ypiao.bean.UserSession;
import com.ypiao.service.UserBankService;
import com.ypiao.util.APSKey;
import com.ypiao.util.Constant;
import com.ypiao.util.VeImage;
import com.ypiao.util.VeStr;

/**
 * 更换银行卡，上传身份证照片接口.
 */
public class OnUserCard extends Action {

	private static final long serialVersionUID = -5226644614194276685L;
	
	private static final Logger LOGGER = Logger.getLogger(OnUserCard.class);

	private UserBankService userBankService;

	private File file;

	private int type = 0;

	public OnUserCard() {
		super(false);
	}

	public UserBankService getUserBankService() {
		return userBankService;
	}

	public void setUserBankService(UserBankService userBankService) {
		this.userBankService = userBankService;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		
		try {
			File file = this.getFile();
			if (file == null) {
				json.addError(this.getText("user.error.080"));
			} else if (file.isFile()) {
				String sid = null;
				int type = this.getType();
				UserSession us = this.getUserSession();
				UserBker b = this.getUserBankService().findBkerByUid(us.getUid());
				switch(type) {
				case 2: // 身份证背面
					if (b.getBb() == null) {
						b.setBb(VeStr.getUSid(true));
					}
					sid = b.getBb();
					break;
				case 3: // 新银行卡照片
					if (b.getBc() == null) {
						b.setBc(VeStr.getUSid(true));
					}
					sid = b.getBc();
					break;
				case 4: // 合照
					if (b.getBd() == null) {
						b.setBd(VeStr.getUSid(true));
					}
					sid = b.getBd();
					break;
				default: // 身份证正面
					if (b.getBa() == null) {
						b.setBa(VeStr.getUSid(true));
					}
					sid = b.getBa();
				} // 保存图片信息
				String str = String.valueOf(b.getUid());
				int len = str.length();
				StringBuilder sb = new StringBuilder();
				sb.append(Constant.FILEPATH).append("user").append(File.separator).append(str.substring(len - 3)).append(File.separator).append(str).append(File.separator);
				sb.append(sid.toLowerCase()).append(".jpg");
				File dest = new File(sb.toString());
				VeImage.waterJPG(file, dest, 800, 800, true);
				this.getUserBankService().saveBker(b); // 保存数据信息
//				SyncMap.getAll().add("uid", str).add("sid", sid).sender(APSKey.SYS_A996, "saveUsed", dest);
				json.addObject();
				json.append("uid", us.getUid());
				json.append("sid", sid); // 图片编号
			}
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
			json.addError(this.getText("system.error.info"));
		}
		
		return JSON;
	}
}
