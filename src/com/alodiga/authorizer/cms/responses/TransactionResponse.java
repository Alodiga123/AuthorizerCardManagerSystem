package com.alodiga.authorizer.cms.responses;
import com.cms.commons.models.TransactionsManagement;
import com.cms.commons.models.Card;
import com.cms.commons.models.TransactionsManagementHistory;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
        public TransactionsManagement transactionsManagement;
        private Float transactionFeesAmount;
        private Float currentBalance; 
        private Date startDate;
        private Date endingDate;
        private Integer totalMovements;
        public List<TransactionsManagementHistory> transactionsManagementHistory;
        
	public TransactionResponse() {
		super();
	}
	
	public TransactionResponse(ResponseCode code) {
		super(new Date(), code.getCode(), code.name());
	}
        
        public TransactionResponse(String code, String message) {
		super(new Date(), code, message);
	}   
        
        public TransactionResponse(String code, String mensaje, Float transactionFeesAmount, TransactionsManagement transactionsManagement) {
            super(new Date(), code, mensaje);
            this.transactionFeesAmount = transactionFeesAmount;
            this.transactionsManagement = transactionsManagement;
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
        
    public TransactionResponse(String code, String mensaje, String cardNumber, int cardStatusId,
                                   String descriptionStatusCard, Long messageMiddlewareId, String transactionNumberIssuer,Float currentBalance, Date startDate, Date endingDate,Integer totalMovements, List<TransactionsManagementHistory> transactionsManagementHistory) {
        super(new Date(), code, mensaje);
        this.cardNumber = cardNumber;
        this.cardStatusId = cardStatusId;
        this.descriptionStatusCard = descriptionStatusCard;
        this.messageMiddlewareId = messageMiddlewareId;
        this.transactionNumberIssuer = transactionNumberIssuer;
        this.currentBalance = currentBalance; 
        this.startDate = startDate;
        this.endingDate = endingDate;
        this.totalMovements = totalMovements;
        this.transactionsManagementHistory = transactionsManagementHistory;
        

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

    public TransactionsManagement getTransactionsManagement() {
        return transactionsManagement;
    }

    public void setTransactionsManagement(TransactionsManagement transactionsManagement) {
        this.transactionsManagement = transactionsManagement;
    }

    public Float getTransactionFeesAmount() {
        return transactionFeesAmount;
    }

    public void setTransactionFeesAmount(Float transactionFeesAmount) {
        this.transactionFeesAmount = transactionFeesAmount;
    }
     
}
