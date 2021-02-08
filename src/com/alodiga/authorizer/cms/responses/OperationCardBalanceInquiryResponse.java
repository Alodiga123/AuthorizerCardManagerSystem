package com.alodiga.authorizer.cms.responses;

import com.cms.commons.models.Card;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationCardBalanceInquiryResponse extends Response {

    private String cardNumber;
    private Float cardCurrentBalance;
    private String transactionNumberIssuer;
    private Date transactionDateIssuer;
    private Long messageMiddlewareId;

    public OperationCardBalanceInquiryResponse() {
        super();
    }

    public OperationCardBalanceInquiryResponse(ResponseCode code, String message) {
        super(new Date(), code.getCode(), message);

    }

    public OperationCardBalanceInquiryResponse(ResponseCode code, String message, String cardNumber, Float cardCurrentBalance, String transactionNumberIssuer, Date transactionDateIssuer, Long messageMiddlewareId) {
        super(new Date(), code.getCode(), message);
        this.cardNumber = cardNumber;
        this.cardCurrentBalance = cardCurrentBalance;
        this.transactionNumberIssuer = transactionNumberIssuer;
        this.transactionDateIssuer = transactionDateIssuer;
        this.messageMiddlewareId = messageMiddlewareId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Float getCardCurrentBalance() {
        return cardCurrentBalance;
    }

    public void setCardCurrentBalance(Float cardCurrentBalance) {
        this.cardCurrentBalance = cardCurrentBalance;
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

    public Long getMessageMiddlewareId() {
        return messageMiddlewareId;
    }

    public void setMessageMiddlewareId(Long messageMiddlewareId) {
        this.messageMiddlewareId = messageMiddlewareId;
    }

    @Override
    public String toString() {
        return "CardBalanceInquiryResponse{" + "cardNumber=" + cardNumber + ", cardCurrentBalance=" + cardCurrentBalance + ", transactionNumberIssuer=" + transactionNumberIssuer + ", transactionDateIssuer=" + transactionDateIssuer + ", messageMiddlewareId=" + messageMiddlewareId + '}';
    }

}
