package com.ypiao.fuiou;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "REQUEST")
public class QueryCardRequest implements Serializable {

	private static final long serialVersionUID = 5583471165033338920L;

	private String version;

	private String mchntcd;

	private String userId;

	private String sign;

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

	public String getUserId() {
		return userId;
	}

	@XmlElement(name = "USERID")
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSign() {
		return sign;
	}

	@XmlElement(name = "SIGN")
	public void setSign(String sign) {
		this.sign = sign;
	}
}
