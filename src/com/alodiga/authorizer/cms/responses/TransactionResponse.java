package com.alodiga.authorizer.cms.responses;
import com.cms.commons.models.TransactionsManagement;
import com.cms.commons.models.Card;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionResponse extends Response {

	public String cardNumber;
        public int cardStatusId;
        public String descriptionStatusCard;
        public Long messageMiddlewareId;
        public String transactionNumberIssuer;
        public Timestamp transactionDateIssuer;
        
	public TransactionResponse() {
		super();
	}
	
	public TransactionResponse(ResponseCode code) {
		super(new Date(), code.getCode(), code.name());
	}
        
        public TransactionResponse(String code, String message) {
		super(new Date(), code, message);
	}        

        public TransactionResponse(String code, String mensaje, String cardNumber, int cardStatusId,
                                   String descriptionStatusCard, Long messageMiddlewareId, String transactionNumberIssuer,
                                   Timestamp transactionDateIssuer) {
        super(new Date(), code, mensaje);
        this.cardNumber = cardNumber;
        this.cardStatusId = cardStatusId;
        this.descriptionStatusCard = descriptionStatusCard;
        this.messageMiddlewareId = messageMiddlewareId;
        this.transactionNumberIssuer = transactionNumberIssuer;
        this.transactionDateIssuer = transactionDateIssuer;
        
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getCardStatusId() {
        return cardStatusId;
    }

    public void setCardStatusId(int cardStatusId) {
        this.cardStatusId = cardStatusId;
    }

    public String getDescriptionStatusCard() {
        return descriptionStatusCard;
    }

    public void setDescriptionStatusCard(String descriptionStatusCard) {
        this.descriptionStatusCard = descriptionStatusCard;
    }

    public Long getMessageMiddlewareId() {
        return messageMiddlewareId;
    }

    public void setMessageMiddlewareId(Long messageMiddlewareId) {
        this.messageMiddlewareId = messageMiddlewareId;
    }

    public String getTransactionNumberIssuer() {
        return transactionNumberIssuer;
    }

    public void setTransactionNumberIssuer(String transactionNumberIssuer) {
        this.transactionNumberIssuer = transactionNumberIssuer;
    }

    public Timestamp getTransactionDateIssuer() {
        return transactionDateIssuer;
    }

    public void setTransactionDateIssuer(Timestamp transactionDateIssuer) {
        this.transactionDateIssuer = transactionDateIssuer;
    }
     
}
