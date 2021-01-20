package com.alodiga.authorizer.cms.responses;
import com.alodiga.authorizer.cms.topup.TopUpInfo;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TopUpInfoListResponse extends Response {

	public List<TopUpInfo> topUpInfos;
	
	public TopUpInfoListResponse() {
		super();
	}
	
	public TopUpInfoListResponse(ResponseCode code) {
		super(new Date(), code.getCode(), code.name());
		this.topUpInfos = null;
	}
	
	public TopUpInfoListResponse(ResponseCode code, String mensaje) {
		super(new Date(), code.getCode(), mensaje);
		this.topUpInfos = null;
	}

	public TopUpInfoListResponse(ResponseCode code, String mensaje, List<TopUpInfo> topUpInfos) {
		super(new Date(), code.getCode(), mensaje);
		this.topUpInfos = topUpInfos;
	}
        
}
