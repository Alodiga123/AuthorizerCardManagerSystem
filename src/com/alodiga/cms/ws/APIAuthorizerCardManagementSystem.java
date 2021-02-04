package com.alodiga.cms.ws;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.log4j.Logger;
import com.alodiga.authorizer.cms.bean.APIOperations;
import com.alodiga.authorizer.cms.responses.CalculateBonusResponse;
import com.alodiga.authorizer.cms.responses.CountryListResponse;
import com.alodiga.authorizer.cms.responses.CardResponse;
import com.alodiga.authorizer.cms.responses.TransactionFeesResponse;
import com.alodiga.authorizer.cms.responses.ValidateLimitsResponse;

@WebService
public class APIAuthorizerCardManagementSystem {

    private static final Logger logger = Logger.getLogger(APIAuthorizerCardManagementSystem.class);

    @EJB
    private APIOperations operations;

    @WebMethod
    public CountryListResponse getCountryList() {
        return operations.getCountryList();
    }

    @WebMethod
    public CardResponse getValidateCard(
            @WebParam(name = "cardNumber") String cardNumber) {
        return operations.getValidateCard(cardNumber);
    }

    @WebMethod
    public CardResponse validateCardByCardHolder(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "cardHolder") String cardHolder) {
        return operations.validateCardByCardHolder(cardNumber, cardHolder);
    }

    @WebMethod
    public CardResponse getValidateCVVAndDueDateCard(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "cvv") String cvv,
            @WebParam(name = "cardDate") String cardDate) {
        return operations.getValidateCVVAndDueDateCard(cardNumber, cvv, cardDate);
    }

    @WebMethod
    public CardResponse getAccountNumberByCard(
            @WebParam(name = "cardNumber") String cardNumber) {
        return operations.getAccountNumberByCard(cardNumber);
    }

    @WebMethod
    public CardResponse getValidateCardByLUNH(
            @WebParam(name = "cardNumber") String cardNumber) {
        return operations.getValidateCardByLUNH(cardNumber);
    }
    
    @WebMethod
    public TransactionFeesResponse calculateTransactionFees(
        @WebParam(name = "cardNumber") String cardNumber,
        @WebParam(name = "channelId") Integer channelId,
        @WebParam(name = "transactionTypeId") Integer transactionTypeId,
        @WebParam(name = "settlementTransactionAmount") Float settlementTransactionAmount,
        @WebParam(name = "transactionNumberAcquirer") String transactionNumberAcquirer) {
        return operations.calculateTransactionFees(cardNumber,channelId,transactionTypeId,settlementTransactionAmount,transactionNumberAcquirer);
    }
    @WebMethod
    public CardResponse calculatesCheckDigitLunh(
            @WebParam(name = "cardNumber") String cardNumber) {
        return operations.calculatesCheckDigitLunh(cardNumber);
    }
    
    @WebMethod
    public CardResponse verifyActiveCard(
        @WebParam(name = "cardNumber") String cardNumber) {
        return operations.verifyActiveCard(cardNumber);
    }
    
    @WebMethod    
    public ValidateLimitsResponse getValidateLimitsTransaccionals(
        @WebParam(name = "cardNumber") String cardNumber,
        @WebParam(name = "transactionTypeId") Integer transactionTypeId,
        @WebParam(name = "channelId") Integer channelId,
        @WebParam(name = "countryCode") String countryCode,
        @WebParam(name = "amountTransaction") Float amountTransaction)   {
        return operations.getValidateLimits(cardNumber, transactionTypeId, channelId,countryCode,amountTransaction);
    }
    
    @WebMethod    
    public CalculateBonusResponse calculateBonus(
        @WebParam(name = "cardNumber") String cardNumber,
        @WebParam(name = "transactionTypeId") Integer transactionTypeId,
        @WebParam(name = "channelId") Integer channelId,
        @WebParam(name = "commerceId") Long commerceId,
        @WebParam(name = "countryCode") String countryCode,
        @WebParam(name = "amountTransaction") Float amountTransaction)   {
        return operations.calculateBonus(cardNumber, transactionTypeId, channelId,commerceId,countryCode,amountTransaction);
    }
    
}
