package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.AdsNote;
import com.ypiao.bean.Manager;
import com.ypiao.service.AderNoteService;

public class APIAt502 extends Abstract {

	private AderNoteService aderNoteService;

	public AderNoteService getAderNoteService() {
		return aderNoteService;
	}

	public void setAderNoteService(AderNoteService aderNoteService) {
		this.aderNoteService = aderNoteService;
	}

	public void save(Manager mgr) {
		try {
			AdsNote note = mgr.getObject(AdsNote.class);
			this.getAderNoteService().save(note);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
