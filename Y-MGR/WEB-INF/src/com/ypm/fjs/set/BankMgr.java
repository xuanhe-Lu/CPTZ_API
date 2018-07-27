package com.ypm.fjs.set;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.ypm.Action;
import com.ypm.bean.AjaxInfo;
import com.ypm.bean.BankInfo;
import com.ypm.bean.FileInfo;
import com.ypm.service.BankInfoService;

public class BankMgr extends Action {

	private static final long serialVersionUID = -8892996458235910250L;

	private BankInfoService bankInfoService;

	private File fxt, fdt;

	public BankInfoService getBankInfoService() {
		return bankInfoService;
	}

	public void setBankInfoService(BankInfoService bankInfoService) {
		this.bankInfoService = bankInfoService;
	}

	public File getFxt() {
		return fxt;
	}

	public void setFxt(File fxt) {
		this.fxt = fxt;
	}

	public File getFdt() {
		return fdt;
	}

	public void setFdt(File fdt) {
		this.fdt = fdt;
	}

	public String tree() {
		this.setAjaxInfo(this.getBankInfoService().findBankByAll());
		return JSON;
	}

	public String getInfo() {
		AjaxInfo json = this.getAjaxInfo();
		try {
			int bid = this.getInt("Bid");
			BankInfo b = this.getBankInfoService().findBankByBid(bid);
			if (b == null) {
				json.addError(this.getText("system.error.none"));
			} else {
				json.data();
				json.append("BID", b.getBid());
				json.append("NAME", b.getName());
				json.append("NICE", b.getNice());
				json.append("TYPE", b.getType());
				json.append("MONTH", b.getMonth());
				json.append("TOALL", b.getToall());
				json.append("TODAY", b.getToday());
				json.append("TOTAL", b.getTotal());
				json.append("STATE", b.getState());
				json.append("TIME", b.getTime());
			}
		} catch (SQLException e) {
			json.addError(this.getText("system.error.info"));
		}
		return JSON;
	}

	public String list() {
		StringBuilder sql = new StringBuilder();
		List<Object> fs = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();
		int state = this.getInt("State", -1);
		if (state <= 1 && state >= 0) {
			sql.append(" AND State=?");
			fs.add(state); // 类型
		} // 检测关键字
		String key = this.getString("KEY");
		if (key == null) {
			// Ignored
		} else {
			sql.append(" AND Name LIKE ?");
			fs.add(sb.append('%').append(key).append('%').toString());
		}
		sb.setLength(0);
		String dir = this.getDir(), sort = this.getSort();
		if (sort == null) {
			sb.append("Sortid");
			sort = "Sortid";
		} else {
			sb.append(sort);
		} // 加载二次排序
		if (sort.equalsIgnoreCase("Bid")) {
			if (dir == null || dir.equalsIgnoreCase("ASC")) {
				sb.append(" ASC");
			} else {
				sb.append(" DESC");
			}
		} else if (dir == null || dir.equalsIgnoreCase("ASC")) {
			sb.append(" ASC,Bid ASC");
		} else {
			sb.append(" DESC,Bid DESC");
		} // 加载数据信息
		this.setAjaxInfo(this.getBankInfoService().findBankByAll(sql, fs, sb.toString(), getStart(), getLimit()));
		return JSON;
	}

	public String save() {
		AjaxInfo json = this.getAjaxInfo();
		FileInfo fxt = FileInfo.getImg(this.getFxt(), 0);
		FileInfo fdt = FileInfo.getImg(this.getFdt(), 0);
		try {
			BankInfo b = null;
			int bid = this.getInt("Bid");
			if (bid >= 1) {
				b = this.getBankInfoService().findBankByBid(bid);
				if (b == null) {
					json.addError(this.getText("system.error.none"));
				} else if (!fxt.image(true)) {
					json.addError(this.getText("fjs.error.101"));
					return JSON;
				} else if (!fdt.image(true)) {
					json.addError(this.getText("fjs.error.102"));
					return JSON;
				}
			} else if (!fxt.image()) {
				json.addError(this.getText("fjs.error.101"));
			} else if (!fdt.image()) {
				json.addError(this.getText("fjs.error.102"));
			} else {
				b = new BankInfo();
			}
			if (b != null) {
				b.setType(getInt("Type"));
				b.setName(getString("Name"));
				b.setNice(getString("Nice"));
				b.setMonth(getBigDecimal("Month"));
				b.setToall(getBigDecimal("Toall"));
				b.setToday(getBigDecimal("Today"));
				b.setState(getInt("State"));
				this.getBankInfoService().saveInfo(b);
				fxt.saveFile("bank/icon", b.getBid());
				fdt.saveFile("bank/logo", b.getBid());
				json.addMessage(this.getText("data.save.succeed"));
			}
		} catch (SQLException | IOException e) {
			json.addError(this.getText("data.save.failed"));
		} finally {
			fxt.destroy();
			fdt.destroy();
		}
		return JSON;
	}

	public String order() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (this.checkValid(ids, 1)) {
				this.getBankInfoService().saveOrder(ids);
				json.addMessage(this.getText("data.order.succeed"));
			} else {
				json.addError(this.getText("system.error.pars"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.order.failed"));
		}
		return JSON;
	}

	public String state() {
		AjaxInfo json = this.getAjaxInfo();
		String ids = this.getString("Ids");
		try {
			if (this.checkValid(ids, 1)) {
				int state = this.getInt("State");
				if (state != STATE_DISABLE) {
					state = STATE_ENABLE;
				}
				this.getBankInfoService().saveState(ids, state);
				json.addMessage(this.getText("data.update.succeed"));
			} else {
				json.addError(this.getText("system.error.pars"));
			}
		} catch (SQLException e) {
			json.addError(this.getText("data.update.failed"));
		} finally {
			ids = null;
		}
		return JSON;
	}

	public String export() {
		this.getAjaxInfo().addError(getText("system.error.export"));
		return JSON;
	}
}
