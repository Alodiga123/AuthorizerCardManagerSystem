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
        public Date transactionDateIssuer;
        public String transactionSequence;
        public TransactionsManagement transactionsManagement;
        private Float transactionCommissionAmount;
        private Float currentBalanceAmount; 
        private Date startDate;
        private Date endingDate;
        private Integer totalMovements;
        public List<TransactionsManagement> transactionsManagementList;
        private Float transactionAmount;
        private String ARPC;
        private Float transanctionBonusAmount;
        
	public TransactionResponse() {
		super();
	}
	
	public TransactionResponse(ResponseCode code) {
		super(new Date(), code.getCode(), code.name());
	}
        
        public TransactionResponse(String code, String message) {
		super(new Date(), code, message);
	}   
        
        public TransactionResponse(String code, String mensaje, Float transactionCommissionAmount, TransactionsManagement transactionsManagement) {
            super(new Date(), code, mensaje);
            this.transactionCommissionAmount = transactionCommissionAmount;
            this.transactionsManagement = transactionsManagement;
        }

        public TransactionResponse(String code, String mensaje, String cardNumber, int cardStatusId,
                                   String descriptionStatusCard, Long messageMiddlewareId, String transactionNumberIssuer,
                                   Date transactionDateIssuer) {
        super(new Date(), code, mensaje);
        this.cardNumber = cardNumber;
        this.cardStatusId = cardStatusId;
        this.descriptionStatusCard = descriptionStatusCard;
        this.messageMiddlewareId = messageMiddlewareId;
        this.transactionNumberIssuer = transactionNumberIssuer;
        this.transactionDateIssuer = transactionDateIssuer;
        
    }
        
    public TransactionResponse(String code, String mensaje, String cardNumber, int cardStatusId,
                                   String descriptionStatusCard, Long messageMiddlewareId, String transactionNumberIssuer,Float currentBalance, Date startDate, Date endingDate,Integer totalMovements, List<TransactionsManagement> transactionsManagementList) {
        super(new Date(), code, mensaje);
        this.cardNumber = cardNumber;
        this.cardStatusId = cardStatusId;
        this.descriptionStatusCard = descriptionStatusCard;
        this.messageMiddlewareId = messageMiddlewareId;
        this.transactionNumberIssuer = transactionNumberIssuer;
        this.currentBalanceAmount = currentBalance; 
        this.startDate = startDate;
        this.endingDate = endingDate;
        this.totalMovements = totalMovements;
        this.transactionsManagementList = transactionsManagementList;
    }  
    
    public TransactionResponse(String code, String mensaje, String cardNumber, int cardStatusId,
                               String descriptionStatusCard, Long messageMiddlewareId, String transactionNumberIssuer, Date transactionDateIssuer, String transactionSequence,
                               Float currentBalanceAmount, Float transactionAmount, Float transactionCommissionAmount, Float transanctionBonusAmount) {
        super(new Date(), code, mensaje);
        this.cardNumber = cardNumber;
        this.cardStatusId = cardStatusId;
        this.descriptionStatusCard = descriptionStatusCard;
        this.messageMiddlewareId = messageMiddlewareId;
        this.transactionNumberIssuer = transactionNumberIssuer;
        this.transactionDateIssuer = transactionDateIssuer;
        this.transactionSequence = transactionSequence;
        this.currentBalanceAmount= currentBalanceAmount; 
        this.transactionAmount = transactionAmount;
        this.transactionCommissionAmount = transactionCommissionAmount;
        this.transanctionBonusAmount = transanctionBonusAmount;
    }  
    
    public TransactionResponse(String code, String mensaje, String ARPC) {
            super(new Date(), code, mensaje);
            this.ARPC = ARPC;
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

    public Date getTransactionDateIssuer() {
        return transactionDateIssuer;
    }

    public void setTransactionDateIssuer(Date transactionDateIssuer) {
        this.transactionDateIssuer = transactionDateIssuer;
    }

    public TransactionsManagement getTransactionsManagement() {
        return transactionsManagement;
    }

    public void setTransactionsManagement(TransactionsManagement transactionsManagement) {
        this.transactionsManagement = transactionsManagement;
    }

    public Float getTransactionCommissionAmount() {
        return transactionCommissionAmount;
    }

    public void setTransactionCommissionAmount(Float transactionCommissionAmount) {
        this.transactionCommissionAmount = transactionCommissionAmount;
    }

    public Float getTransanctionBonusAmount() {
        return transanctionBonusAmount;
    }

    public void setTransanctionBonusAmount(Float transanctionBonusAmount) {
        this.transanctionBonusAmount = transanctionBonusAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }

    public Integer getTotalMovements() {
        return totalMovements;
    }

    public void setTotalMovements(Integer totalMovements) {
        this.totalMovements = totalMovements;
    }

    public Float getCurrentBalanceAmount() {
        return currentBalanceAmount;
    }

    public void setCurrentBalanceAmount(Float currentBalanceAmount) {
        this.currentBalanceAmount = currentBalanceAmount;
    }

    public List<TransactionsManagement> getTransactionsManagementList() {
        return transactionsManagementList;
    }

    public void setTransactionsManagementList(List<TransactionsManagement> transactionsManagementList) {
        this.transactionsManagementList = transactionsManagementList;
    }

    public Float getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Float transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getARPC() {
        return ARPC;
    }

    public void setARPC(String ARPC) {
        this.ARPC = ARPC;
    }
    
    public String getTransactionSequence() {
        return transactionSequence;
    }

    public void setTransactionSequence(String transactionSequence) {
        this.transactionSequence = transactionSequence;
    }
}
