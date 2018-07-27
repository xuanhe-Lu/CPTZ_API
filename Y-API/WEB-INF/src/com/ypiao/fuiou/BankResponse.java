package com.ypiao.fuiou;

import java.io.Serializable;
import com.ypiao.sign.JSON;

public class BankResponse implements Serializable {

	private static final long serialVersionUID = 7586479063007211727L;

	public String code = "ERR";

	private boolean flag = false;

	private String msg;

	private Result result;

	public class Data {

		private String name;

		private String channel;

		private String remark;

		private String identityNo;

		private String mobile;

		private String resultCode;

		private String resultMsg;

		private String bankCardBin;

		private String bankCardNo;

		public String getName() {
			return name;
		}

		public String getChannel() {
			return channel;
		}

		public String getRemark() {
			return remark;
		}

		public String getIdentityNo() {
			return identityNo;
		}

		public String getMobile() {
			return mobile;
		}

		public String getResultCode() {
			return resultCode;
		}

		public String getResultMsg() {
			return resultMsg;
		}

		public String getBankCardBin() {
			return bankCardBin;
		}

		public String getBankCardNo() {
			return bankCardNo;
		}
	}

	public class Result {

		private String code;

		private String msg;

		private String token;

		private Data data;

		public String getCode() {
			return code;
		}

		public String getMsg() {
			return msg;
		}

		public String getToken() {
			return token;
		}

		public Data getData() {
			return data;
		}

		public void setData(JSON j) {
			if (j == null) {
				this.data = null;
				setFlag(false);
			} else {
				Data d = new Data();
				d.bankCardBin = j.get("bankcardbin");
				d.bankCardNo = j.get("bankcardno");
				d.channel = j.get("channel");
				d.identityNo = j.get("identityno");
				d.mobile = j.get("mobile");
				d.name = j.get("name");
				d.remark = j.get("remark");
				d.resultCode = j.get("resultcode");
				d.resultMsg = j.get("resultmsg");
				this.data = d;
				if (d.resultCode.equals("R001")) {
					setFlag(true);
				}
			}
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(JSON j) {
		if (j == null) {
			this.result = null;
			setFlag(false);
		} else {
			Result r = new Result();
			r.code = j.get("code");
			r.msg = j.get("msg");
			r.token = j.get("token");
			r.setData(j.getJSON("data"));
			this.result = r;
		}
	}

	@Override
	public String toString() {
		return "BankResponse [code=" + code + ", flag=" + flag + ", msg=" + msg + ", result=" + result + "]";
	}
}
