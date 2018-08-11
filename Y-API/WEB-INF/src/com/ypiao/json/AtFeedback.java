package com.ypiao.json;

import java.sql.SQLException;
import com.ypiao.bean.AjaxInfo;
import com.ypiao.bean.FeedInfo;
import com.ypiao.bean.UserSession;
import com.ypiao.service.FeedBackService;
import com.ypiao.util.VeStr;

public class AtFeedback extends Action {

	private static final long serialVersionUID = 1780196783046051021L;

	private FeedBackService feedBackService;

	public AtFeedback() {
		super(true);
	}

	public FeedBackService getFeedBackService() {
		return feedBackService;
	}

	public void setFeedBackService(FeedBackService feedBackService) {
		this.feedBackService = feedBackService;
	}

	public String index() {
		AjaxInfo json = this.getAjaxInfo();
		String mobile = this.getString("mobile");
		String content = this.getString("content");
		try {
			UserSession us = this.getUserSession();
			FeedInfo f = new FeedInfo();
			f.setSid(VeStr.getUSid(true));
			f.setUid(us.getUid());
			f.setMobile(mobile);
			f.setContent(content);
			f.setState(STATE_NORMAL);
			this.getFeedBackService().saveFeed(f);
			json.addMessage(f.getSid());
		} catch (SQLException e) {
			e.printStackTrace();
			json.addError(this.getText("data.save.failed"));
		} finally {
			content = mobile = null;
		}
		logger.info("json:"+json.toString());
 return JSON;
	}
}
