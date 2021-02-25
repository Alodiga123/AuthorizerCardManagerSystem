package com.alodiga.authorizer.cms.responses;
import com.cms.commons.models.CardKeyHistory;
import com.cms.commons.models.Country;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CardKeyHistoryListResponse extends Response {

	public List<CardKeyHistory> cardKeyHistorys;
	
	public CardKeyHistoryListResponse() {
		super();
	}
	
	public CardKeyHistoryListResponse(ResponseCode code) {
		super(new Date(), code.getCode(), code.name());
		this.cardKeyHistorys = null;
	}
	
	public CardKeyHistoryListResponse(ResponseCode code, String mensaje) {
		super(new Date(), code.getCode(), mensaje);
		this.cardKeyHistorys = null;
	}

	public CardKeyHistoryListResponse(ResponseCode code, String mensaje, List<CardKeyHistory> cardKeyHistorys) {
		super(new Date(), code.getCode(), mensaje);
		this.cardKeyHistorys = cardKeyHistorys;
	}

    public List<CardKeyHistory> getCardKeyHistorys() {
        return cardKeyHistorys;
    }

    public void setCardKeyHistorys(List<CardKeyHistory> cardKeyHistorys) {
        this.cardKeyHistorys = cardKeyHistorys;
    }
        
        
        
}
