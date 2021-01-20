package com.alodiga.authorizer.cms.responses;
import com.cms.commons.models.Product;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductListResponse extends Response {

	public List<Product> products;
	
	public ProductListResponse() {
		super();
	}
	
	public ProductListResponse(ResponseCode code) {
		super(new Date(), code.getCode(), code.name());
		this.products = null;
	}
	
	public ProductListResponse(ResponseCode code, String mensaje) {
		super(new Date(), code.getCode(), mensaje);
		this.products = null;
	}

	public ProductListResponse(ResponseCode code, String mensaje, List<Product> products) {
		super(new Date(), code.getCode(), mensaje);
		this.products = products;
	}
        
}
