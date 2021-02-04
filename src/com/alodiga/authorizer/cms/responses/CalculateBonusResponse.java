package com.alodiga.authorizer.cms.responses;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CalculateBonusResponse extends Response {

	        
	public CalculateBonusResponse() {
		super();
	}
	
	public CalculateBonusResponse(ResponseCode code) {
		super(new Date(), code.getCode(), code.name());
	}
	
	public CalculateBonusResponse(ResponseCode code, String mensaje) {
		super(new Date(), code.getCode(), mensaje);
	}
  
        
}
