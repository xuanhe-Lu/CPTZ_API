package com.ypiao.fuiou;

import java.io.Serializable;

public class CardBinResponse implements Serializable {

	private static final long serialVersionUID = -8871975444402773082L;

	private String rcd;

	private String rdesc;

	private String ctp;

	private String cnm;

	private String inscd;

	private String sign;

	public String getRcd() {
		return rcd;
	}

	public void setRcd(String rcd) {
		this.rcd = rcd;
	}

	public String getRdesc() {
		return rdesc;
	}

	public void setRdesc(String rdesc) {
		this.rdesc = rdesc;
	}

	public String getCtp() {
		return ctp;
	}

	public void setCtp(String ctp) {
		this.ctp = ctp;
	}

	public String getCnm() {
		return cnm;
	}

	public void setCnm(String cnm) {
		this.cnm = cnm;
	}

	public String getInscd() {
		return inscd;
	}

	public void setInscd(String inscd) {
		this.inscd = inscd;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
}
