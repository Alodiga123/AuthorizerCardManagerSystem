package com.alodiga.authorizer.cms.responses;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionPurchageResponse extends Response {

    public String arpc;

    public TransactionPurchageResponse() {
        super();
    }

    public TransactionPurchageResponse(ResponseCode code) {
        super(new Date(), code.getCode(), code.name());
    }

    public TransactionPurchageResponse(String code, String message) {
        super(new Date(), code, message);
    }

    public TransactionPurchageResponse(String code, String mensaje, String arpc) {
        super(new Date(), code, mensaje);
        this.arpc = arpc;
    }

    public String getArpc() {
        return arpc;
    }

    public void setArpc(String arpc) {
        this.arpc = arpc;
    }


     
}
