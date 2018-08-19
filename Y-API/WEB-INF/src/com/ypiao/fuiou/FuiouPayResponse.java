package com.ypiao.fuiou;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @NAME:FuiouPayResponse
 * @DESCRIPTION:富有协议支付返回参数
 * @AUTHOR:luxh
 * @DATE:2018/8/15
 * @VERSION:1.0
 */

@XmlRootElement(name = "RESPONSE")
public class FuiouPayResponse implements Serializable {
    private static final long serialVersionUID = -293946215454155839L;
    private String version; //版本号
    private String responseCode; //响应代码 0000 成功
    private String responseMsg; //响应中文描述
    private String mchntssn; //商户流水号 商户请求发送的流水号
    private String mchntcd; //商户代码
    private String protocolNo ;//协议号
    private String userId ;//用户编号
    private String cardNo ;//银行卡号
    private String type ;//类型 03
    private String orderId ;//富友订单号
    private String bankCard  ;//银行卡号
    private String amt  ;//交易金额
    private String rem1   ;//保留字段 1
    private String rem2   ;//保留字段 2
    private String rem3  ;//保留字段 3
    private String signTp  ;//签名类型
    private String sign  ;//摘要数据
    private String rcd  ;//响应代码
    private String rdesc  ;//中文描述
    private String ctp  ;//中文描述
    private String cnm   ;//银行名称
    private String insCd    ;//银行机构号


    public String getMchntcd() {
        return mchntcd;
    }

    @Override
    public String toString() {
        return "FuiouPayResponse{" +
                "version='" + version + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", responseMsg='" + responseMsg + '\'' +
                ", mchntssn='" + mchntssn + '\'' +
                ", mchntcd='" + mchntcd + '\'' +
                ", protocolNo='" + protocolNo + '\'' +
                ", userId='" + userId + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", type='" + type + '\'' +
                ", orderId='" + orderId + '\'' +
                ", bankCard='" + bankCard + '\'' +
                ", amt='" + amt + '\'' +
                ", rem1='" + rem1 + '\'' +
                ", rem2='" + rem2 + '\'' +
                ", rem3='" + rem3 + '\'' +
                ", signTp='" + signTp + '\'' +
                ", sign='" + sign + '\'' +
                ", rcd='" + rcd + '\'' +
                ", rdesc='" + rdesc + '\'' +
                ", ctp='" + ctp + '\'' +
                ", cnm='" + cnm + '\'' +
                ", insCd='" + insCd + '\'' +
                '}';
    }

    @XmlElement(name = "MCHNTCD")
    public void setMchntcd(String mchntcd) {this.mchntcd = mchntcd;
    }

    public String getProtocolNo() {
        return protocolNo;
    }
    @XmlElement(name = "PROTOCOLNO")
    public void setProtocolNo(String protocolNo) {
        this.protocolNo = protocolNo;
    }

    public String getUserId() {
        return userId;
    }
    @XmlElement(name = "USERID")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCardNo() {
        return cardNo;
    }
    @XmlElement(name = "CARDNO")
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getType() {
        return type;
    }
    @XmlElement(name = "TYPE")
    public void setType(String type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }
    @XmlElement(name = "ORDERID")
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBankCard() {
        return bankCard;
    }
    @XmlElement(name = "BANKCARD")
    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getAmt() {
        return amt;
    }
    @XmlElement(name = "AMT")
    public void setAmt(String amt) {
        this.amt = amt;
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

    public String getSignTp() {
        return signTp;
    }
    @XmlElement(name = "SIGNTP")
    public void setSignTp(String signTp) {
        this.signTp = signTp;
    }

    public String getSign() {
        return sign;
    }
    @XmlElement(name = "Sign")
    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getRcd() {
        return rcd;
    }
    @XmlElement(name = "Rcd")
    public void setRcd(String rcd) {
        this.rcd = rcd;
    }

    public String getRdesc() {
        return rdesc;
    }
    @XmlElement(name = "RDesc")
    public void setRdesc(String rdesc) {
        this.rdesc = rdesc;
    }

    public String getCtp() {
        return ctp;
    }
    @XmlElement(name = "Ctp")
    public void setCtp(String ctp) {
        this.ctp = ctp;
    }

    public String getCnm() {
        return cnm;
    }
    @XmlElement(name = "Cnm")
    public void setCnm(String cnm) {
        this.cnm = cnm;
    }

    public String getInsCd() {
        return insCd;
    }
    @XmlElement(name = "InsCd")
    public void setInsCd(String insCd) {
        this.insCd = insCd;
    }

    public String getVersion() {
        return version;
    }

    @XmlElement(name = "VERSION")
    public void setVersion(String version) {
        this.version = version;
    }

    public String getResponseCode() {
        return responseCode;
    }

    @XmlElement(name = "RESPONSECODE")
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    @XmlElement(name = "RESPONSEMSG")
    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getMchntssn() {
        return mchntssn;
    }

    @XmlElement(name = "MCHNTSSN")
    public void setMchntssn(String mchntssn) {
        this.mchntssn = mchntssn;
    }

}
