package com.alodiga.authorizer.cms.responses;
import com.cms.commons.models.Country;
import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "CountryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CountryResponse extends Response implements Serializable {

	private static final long serialVersionUID = -5826822375335798732L;

	public Country response;

	public CountryResponse() {
		super();
	}

	public CountryResponse(ResponseCode codigo) {
		super(new Date(), codigo.getCode(), codigo.name());
		this.response = null;
	}

	public CountryResponse(ResponseCode codigo,String mensajeRespuesta) {
		super(new Date(), codigo.getCode(), mensajeRespuesta);
		this.response = null;
	}

	public CountryResponse(ResponseCode codigo,String mensajeRespuesta, Country countryId) {
		super(new Date(), codigo.getCode(), mensajeRespuesta);
		this.response = countryId;
	}

}
