package com.alodiga.cms.ws;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.log4j.Logger;
import com.alodiga.authorizer.cms.bean.APIOperations;
import com.alodiga.authorizer.cms.responses.CountryListResponse;
import com.alodiga.authorizer.cms.responses.ProductListResponse;
import com.alodiga.authorizer.cms.responses.ProductResponse;
import com.alodiga.authorizer.cms.responses.CardResponse;
import com.alodiga.authorizer.cms.responses.TopUpInfoListResponse;
import com.alodiga.authorizer.cms.responses.UserHasProductResponse;

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
    public CardResponse calculatesCheckDigitLunh(
            @WebParam(name = "cardNumber") String cardNumber) {
        return operations.calculatesCheckDigitLunh(cardNumber);
    }
    

}
