package com.alodiga.authorizer.cms.responses;
import com.cms.commons.models.Country;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CountryListResponse extends Response {

	public List<Country> countries;
	
	public CountryListResponse() {
		super();
	}
	
	public CountryListResponse(ResponseCode code) {
		super(new Date(), code.getCode(), code.name());
		this.countries = null;
	}
	
	public CountryListResponse(ResponseCode code, String mensaje) {
		super(new Date(), code.getCode(), mensaje);
		this.countries = null;
	}

	public CountryListResponse(ResponseCode code, String mensaje, List<Country> countries) {
		super(new Date(), code.getCode(), mensaje);
		this.countries = countries;
	}
        
}
