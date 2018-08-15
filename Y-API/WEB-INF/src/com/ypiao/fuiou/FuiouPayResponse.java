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
