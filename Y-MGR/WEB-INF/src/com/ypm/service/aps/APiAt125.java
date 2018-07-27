package com.ypm.service.aps;

import java.sql.SQLException;
import com.ypm.bean.Manager;
import com.ypm.bean.UserBank;
import com.ypm.bean.UserBker;
import com.ypm.service.UserBankService;

public class APiAt125 extends Abstract {

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
