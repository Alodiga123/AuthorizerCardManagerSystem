package com.alodiga.authorizer.cms.responses;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CalculateBonusCardResponse extends Response {
    
    public Float bonusAmount;

    public CalculateBonusCardResponse() {
            super();
    }

    public CalculateBonusCardResponse(ResponseCode code) {
            super(new Date(), code.getCode(), code.name());
    }

    public CalculateBonusCardResponse(ResponseCode code, String mensaje) {
            super(new Date(), code.getCode(), mensaje);
    }

    public CalculateBonusCardResponse(ResponseCode code, String mensaje, Float bonusAmount) {
            super(new Date(), code.getCode(), mensaje);
            this.bonusAmount = bonusAmount;
    }

    public Float getBonusAmount() {
        return bonusAmount;
    }

    public void setBonusAmount(Float bonusAmount) {
        this.bonusAmount = bonusAmount;
    }
  
        
}
