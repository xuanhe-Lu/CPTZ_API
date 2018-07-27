package com.ypiao.service.aps;

import java.sql.SQLException;
import com.ypiao.bean.FeedInfo;
import com.ypiao.bean.Manager;
import com.ypiao.service.FeedBackService;

public class APIAt995 extends Abstract {

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
