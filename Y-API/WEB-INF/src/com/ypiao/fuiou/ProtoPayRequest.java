package com.ypiao.fuiou;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "REQUEST")
public class ProtoPayRequest implements Serializable {

	private static final long serialVersionUID = -7483661059519401805L;

	private String type;

	private String version;

	private String mchntcd;

	private String mchntorderid;

	private String orderId;

	private String protocolno;

	private String userId;

	private String userIP;

	private String rem1;

	private String rem2;

	private String rem3;

	private String sign;

	private String signtp;

	private String signpay;

	private String vercd;

	public String getType() {
		return type;
	}

	@XmlElement(name = "TYPE")
	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	@XmlElement(name = "VERSION")
	public void setVersion(String version) {
		this.version = version;
	}

	public String getMchntcd() {
		return mchntcd;
	}

	@XmlElement(name = "MCHNTCD")
	public void setMchntcd(String mchntcd) {
		this.mchntcd = mchntcd;
	}

	public String getMchntorderid() {
		return mchntorderid;
	}

	@XmlElement(name = "MCHNTORDERID")
	public void setMchntorderid(String mchntorderid) {
		this.mchntorderid = mchntorderid;
	}

	public String getOrderId() {
		return orderId;
	}

	@XmlElement(name = "ORDERID")
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getProtocolno() {
		return protocolno;
	}

	@XmlElement(name = "PROTOCOLNO")
	public void setProtocolno(String protocolno) {
		this.protocolno = protocolno;
	}

	public String getUserId() {
		return userId;
	}

	@XmlElement(name = "USERID")
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserIP() {
		return userIP;
	}

	@XmlElement(name = "USERIP")
	public void setUserIP(String userIP) {
		this.userIP = userIP;
	}

	public String getRem1() {
		return rem1;
	}

	@XmlElement(name = "REM1")
	public void setRem1(String rem1) {
		this.rem1 = rem1;
	}

	public String getRem2() {
		return rem2;
	}

	@XmlElement(name = "REM2")
	public void setRem2(String rem2) {
		this.rem2 = rem2;
	}

	public String getRem3() {
		return rem3;
	}

	@XmlElement(name = "REM3")
	public void setRem3(String rem3) {
		this.rem3 = rem3;
	}

	public String getSign() {
		return sign;
	}

	@XmlElement(name = "SIGN")
	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSigntp() {
		return signtp;
	}

	@XmlElement(name = "SIGNTP")
	public void setSigntp(String signtp) {
		this.signtp = signtp;
	}

	public String getSignpay() {
		return signpay;
	}

	@XmlElement(name = "SIGNPAY")
	public void setSignpay(String signpay) {
		this.signpay = signpay;
	}

	public String getVercd() {
		return vercd;
	}

	@XmlElement(name = "VERCD")
	public void setVercd(String vercd) {
		this.vercd = vercd;
	}
}
