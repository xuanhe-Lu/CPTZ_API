package com.ypm.service.aps;

import java.io.IOException;
import com.ypm.bean.Manager;
import com.ypm.bean.Message;
import com.ypm.service.APInterService;
import com.ypm.util.AState;

public abstract class Abstract implements APInterService, AState {

	public void execute(Manager mgr) throws IOException {
		String name = mgr.getAction();
		try {
			if (name == null) {
				name = "index";
			} // add Method
			this.getClass().getMethod(name, Manager.class).invoke(this, mgr);
		} catch (Throwable e) {
			e.printStackTrace();
			mgr.getMessage().addError(-9, e.getMessage());
		} finally {
			name = null;
		}
	}

	public Message index(Manager mgr) {
		return mgr.getMessage();
	}
}
