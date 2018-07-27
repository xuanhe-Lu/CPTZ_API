package com.ypm.service.aps;

import java.sql.SQLException;
import com.ypm.bean.Manager;
import com.ypm.bean.Message;
import com.ypm.bean.UserIder;
import com.ypm.service.UserIderService;
import com.ypm.util.VeStr;

public class APiAt121 extends Abstract {

	private UserIderService userIderService;

	public UserIderService getUserIderService() {
		return userIderService;
	}

	public void setUserIderService(UserIderService userIderService) {
		this.userIderService = userIderService;
	}

	/** 获取用户编号 */
	public void getUid(Manager mgr) {
		Message msg = mgr.getMessage();
		String mobile = VeStr.getMobile(mgr.getParameter("mobile"));
		try {
			if (mobile == null) {
				msg.addError("手机号码格式错误！");
			} else {
				UserIder ider = this.getUserIderService().findUserIder(mobile);
				if (ider == null) {
					msg.addError(SYSTEM_ERROR_PARS);
				} else {
					msg.addObject(ider);
				}
			}
		} catch (SQLException e) {
			msg.addError(SYSTEM_ERROR_INFO);
		} finally {
			mobile = null;
		}
	}
}
