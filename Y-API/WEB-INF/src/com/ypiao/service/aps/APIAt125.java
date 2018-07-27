package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.Manager;
import com.ypiao.bean.UserBank;
import com.ypiao.bean.UserBker;
import com.ypiao.service.UserBankService;

public class APIAt125 extends Abstract {

	private UserBankService userBankService;

	public UserBankService getUserBankService() {
		return userBankService;
	}

	public void setUserBankService(UserBankService userBankService) {
		this.userBankService = userBankService;
	}

	public void save(Manager mgr) {
		try {
			UserBank b = mgr.getObject(UserBank.class);
			if (b.getUid() >= USER_UID_BEG) {
				this.getUserBankService().save(b);
			}
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void saveBker(Manager mgr) {
		try {
			UserBker b = mgr.getObject(UserBker.class);
			if (b.getUid() >= USER_UID_BEG) {
				this.getUserBankService().save(b);
			}
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}

	public void saveUsed(Manager mgr) {
		try {
			UserBker b = mgr.getObject(UserBker.class);
			if (b.getUid() >= USER_UID_BEG) {
				this.getUserBankService().used(b);
			}
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
