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
        private String aliasCard;
        private String name;
        private String email;
        private String numberPhone;
        
	public CardResponse() {
		super();
	}
	
	public CardResponse(ResponseCode code) {
		super(new Date(), code.getCode(), code.name());
		this.card = null;
	}
	
	public CardResponse(ResponseCode code, String mensaje) {
		super(new Date(), code.getCode(), mensaje);
		this.card = null;
	}

    public CardResponse(ResponseCode code, String mensaje, String aliasCard) {
        super(new Date(), code.getCode(), mensaje);
        this.aliasCard = aliasCard;
    }
    
    public CardResponse(ResponseCode code, String mensaje, String aliasCard, String name, String email, String numberPhone) {
        super(new Date(), code.getCode(), mensaje);
        this.aliasCard = aliasCard;
        this.name = name;
        this.email = email;
        this.numberPhone = numberPhone;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public String getaliasCard() {
        return aliasCard;
    }

    public void setaliasCard(String aliasCard) {
        this.aliasCard = aliasCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }
    
    

	
        
}
