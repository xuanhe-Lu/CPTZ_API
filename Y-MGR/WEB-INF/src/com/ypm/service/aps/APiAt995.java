package com.ypm.service.aps;

import java.sql.SQLException;
import com.ypm.bean.FeedInfo;
import com.ypm.bean.Manager;
import com.ypm.service.FeedBackService;

public class APiAt995 extends Abstract {

	private FeedBackService feedBackService;

	public FeedBackService getFeedBackService() {
		return feedBackService;
	}

	public void setFeedBackService(FeedBackService feedBackService) {
		this.feedBackService = feedBackService;
	}

	public void save(Manager mgr) {
		try {
			FeedInfo feed = mgr.getObject(FeedInfo.class);
			this.getFeedBackService().save(feed);
		} catch (SQLException e) {
			mgr.addError(DATA_SAVE_FAILED);
		}
	}
}
