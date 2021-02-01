package com.alodiga.authorizer.cms.responses;
import com.cms.commons.models.Card;
import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidateLimitsResponse extends Response {

	        
	public ValidateLimitsResponse() {
		super();
	}
	
	public ValidateLimitsResponse(ResponseCode code) {
		super(new Date(), code.getCode(), code.name());
	}
	
	public ValidateLimitsResponse(ResponseCode code, String mensaje) {
		super(new Date(), code.getCode(), mensaje);
	}
  
        
}
