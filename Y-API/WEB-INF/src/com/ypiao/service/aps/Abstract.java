package com.ypiao.service.aps;

import java.io.IOException;
import com.ypiao.bean.Manager;
import com.ypiao.bean.Message;
import com.ypiao.bean.Messager;
import com.ypiao.service.APInterService;
import com.ypiao.util.AState;

public abstract class Abstract implements APInterService, AState {

	protected void destroy(Messager mgr) {
		if (mgr == null) {
			// Ignored
		} else {
			mgr.destroy();
		}
	}

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
