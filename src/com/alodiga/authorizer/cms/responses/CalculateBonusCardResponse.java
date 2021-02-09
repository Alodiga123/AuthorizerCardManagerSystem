package com.alodiga.authorizer.cms.responses;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CalculateBonusCardResponse extends Response {

	        
	public CalculateBonusCardResponse() {
		super();
	}
	
	public CalculateBonusCardResponse(ResponseCode code) {
		super(new Date(), code.getCode(), code.name());
	}
	
	public CalculateBonusCardResponse(ResponseCode code, String mensaje) {
		super(new Date(), code.getCode(), mensaje);
	}
  
        
}
