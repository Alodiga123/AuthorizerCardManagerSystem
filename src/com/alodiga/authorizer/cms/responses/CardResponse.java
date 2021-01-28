package com.alodiga.authorizer.cms.responses;
import com.cms.commons.models.Card;
import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CardResponse extends Response {

	public Card card;
        private String accountNumber;
        
	public CardResponse() {
		super();
	}
	
	public CardResponse(ResponseCode code) {
		super(new Date(), code.getCode(), code.name());
		this.card = null;
	}
        
        public CardResponse(String code, String message) {
		super(new Date(), code, message);
		this.card = null;
	}        

    public CardResponse(String code, String mensaje, String accountNumber) {
        super(new Date(), code, mensaje);
        this.accountNumber = accountNumber;
    }
    

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    

	
        
}
