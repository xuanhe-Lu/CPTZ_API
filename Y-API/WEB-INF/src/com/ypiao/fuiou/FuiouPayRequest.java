package com.ypiao.fuiou;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @NAME:FuiouPayRequest
 * @DESCRIPTION:协议支付请求参数
 * @AUTHOR:luxh
 * @DATE:2018/8/15
 * @VERSION:1.0
 */
@XmlRootElement(name = "REQUEST")
public class FuiouPayRequest implements Serializable {
    private static final long serialVersionUID = -2603658202312438600L;
    private String version; //版本号
    private String mchntcd;//分配给各合作商户的唯一识别码
    private long userId;//商户端用户的唯一编号，即用户 ID
    private String tradeDate;//交易请求日期  20180417
    private long mchntssn; //商户流水号，保持唯一
    private String account;//银行卡账户名称
    private String cardNo;//银行卡号
    private String idType;//证件类型 0：身份证
    private String idCard;//证件号码
    private String mobileNo;//银行卡预留手机号码
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

    public long getUserId() {
        return userId;
    }
    @XmlElement(name = "USERID")
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTradeDate() {
        return tradeDate;
    }
    @XmlElement(name = "TRADEDATE")
    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public long getMchntssn() {
        return mchntssn;
    }
    @XmlElement(name = "MCHNTSSN")
    public void setMchntssn(long mchntssn) {
        this.mchntssn = mchntssn;
    }

    public String getAccount() {
        return account;
    }
    @XmlElement(name = "ACCOUNT")
    public void setAccount(String account) {
        this.account = account;
    }

    public String getCardNo() {
        return cardNo;
    }
    @XmlElement(name = "CARDNO")
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getIdType() {
        return idType;
    }
    @XmlElement(name = "IDTYPE")
    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdCard() {
        return idCard;
    }
    @XmlElement(name = "IDCARD")
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getMobileNo() {
        return mobileNo;
    }
    @XmlElement(name = "MOBILENO")
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getSign() {
        return sign;
    }
    @XmlElement(name = "SIGN")
    public void setSign(String sign) {
        this.sign = sign;
    }
}
