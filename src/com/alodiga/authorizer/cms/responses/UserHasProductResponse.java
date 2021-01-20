package com.alodiga.authorizer.cms.responses;
import com.cms.commons.models.Product;
import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "UserHasProductResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserHasProductResponse extends Response implements Serializable {

	private static final long serialVersionUID = -5826822375335798732L;

	public Product response;

	public UserHasProductResponse() {
		super();
	}

	public UserHasProductResponse(ResponseCode codigo) {
		super(new Date(), codigo.getCode(), codigo.name());
		this.response = null;
	}

	public UserHasProductResponse(ResponseCode codigo,
			String mensajeRespuesta) {
		super(new Date(), codigo.getCode(), mensajeRespuesta);
		this.response = null;
	}

	public UserHasProductResponse(ResponseCode codigo,
			String mensajeRespuesta, Product productId) {
		super(new Date(), codigo.getCode(), mensajeRespuesta);
		this.response = productId;
	}

}
