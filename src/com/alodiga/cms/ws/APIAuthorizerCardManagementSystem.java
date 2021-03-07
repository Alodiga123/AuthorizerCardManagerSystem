package com.alodiga.cms.ws;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.apache.log4j.Logger;
import com.alodiga.authorizer.cms.bean.APIOperations;
import com.alodiga.authorizer.cms.responses.CalculateBonusCardResponse;
import com.alodiga.authorizer.cms.responses.CountryListResponse;
import com.alodiga.authorizer.cms.responses.CardResponse;
import com.alodiga.authorizer.cms.responses.OperationCardBalanceInquiryResponse;
import com.alodiga.authorizer.cms.responses.TransactionFeesResponse;
import com.alodiga.authorizer.cms.responses.TransactionPurchageResponse;
import com.alodiga.authorizer.cms.responses.TransactionResponse;
import com.alodiga.authorizer.cms.responses.ValidateLimitsResponse;
import com.cms.commons.models.AccountCard;
import com.cms.commons.models.Card;
import java.sql.Timestamp;
import java.util.Date;

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
    public AccountCard getAccountNumberByCard(
            @WebParam(name = "cardNumber") String cardNumber) {
        return operations.getAccountNumberByCard(cardNumber);
    }

    @WebMethod
    public CardResponse getValidateCardByLUNH(
            @WebParam(name = "cardNumber") String cardNumber) {
        return operations.getValidateCardByLUNH(cardNumber);
    }
    
    @WebMethod
    public TransactionResponse calculateCommisionCMS(
        @WebParam(name = "cardNumber") String cardNumber,
        @WebParam(name = "channelId") Integer channelId,
        @WebParam(name = "transactionTypeId") Integer transactionTypeId,
        @WebParam(name = "settlementTransactionAmount") Float settlementTransactionAmount,
        @WebParam(name = "transactionNumberAcquirer") String transactionNumberAcquirer) {
        return operations.calculateCommisionCMS(cardNumber,channelId,transactionTypeId,settlementTransactionAmount,transactionNumberAcquirer);
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
    public CardResponse validateCard(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "ARQC") String ARQC,
            @WebParam(name = "cardHolder") String cardHolder,
            @WebParam(name = "CVV") String CVV,
            @WebParam(name = "cardDueDate") String cardDueDate,
            @WebParam(name = "indValidateCardActive") int indValidateCardActive ) {
        return operations.validateCard(cardNumber,ARQC,cardHolder,CVV,cardDueDate,indValidateCardActive);
    }
    
    @WebMethod
    public CardResponse validateDocumentIdentificationCustomer(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "identificationNumber") String identificationNumber) {
        return operations.validateDocumentIdentificationCustomer(cardNumber,identificationNumber);
    }
    
    @WebMethod
    public TransactionResponse changeCardStatus(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "CVV") String CVV,
            @WebParam(name = "cardDueDate") String cardDueDate,
            @WebParam(name = "cardHolder") String cardHolder,
            @WebParam(name = "messageMiddlewareId") Long messageMiddlewareId,
            @WebParam(name = "newStatusCardId") Long newStatusCardId,
            @WebParam(name = "statusUpdateReasonId") Integer statusUpdateReasonId,
            @WebParam(name = "observations") String observations,
            @WebParam(name = "statusUpdateReasonDate") Date statusUpdateReasonDate,
            @WebParam(name = "userResponsabibleStatusUpdateId") Long userResponsabibleStatusUpdateId,
            @WebParam(name = "documentIdentificationNumber") String documentIdentificationNumber,
            @WebParam(name = "transactionTypeId") Integer transactionTypeId,
            @WebParam(name = "channelId") Integer channelId,
            @WebParam(name = "transactionDate") Date transactionDate,
            @WebParam(name = "localTimeTransaction") Timestamp localTimeTransaction,
            @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
            @WebParam(name = "acquirerCountryId") Integer acquirerCountryId) {
        return operations.changeCardStatus(cardNumber,CVV,cardDueDate,cardHolder,messageMiddlewareId,newStatusCardId,statusUpdateReasonId,observations,statusUpdateReasonDate,userResponsabibleStatusUpdateId,documentIdentificationNumber,transactionTypeId,channelId,transactionDate,localTimeTransaction,acquirerTerminalCodeId,acquirerCountryId);
    }
         
    @WebMethod
    public OperationCardBalanceInquiryResponse cardBalanceInquiry(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "CVV") String CVV,
            @WebParam(name = "ARQC") String ARQC,
            @WebParam(name = "documentIdentificationNumber") String documentIdentificationNumber,
            @WebParam(name = "transactionTypeId") Integer transactionTypeId,
            @WebParam(name = "channelId") Integer channelId,
            @WebParam(name = "transactionDate") Date transactionDate,
            @WebParam(name = "localTimeTransaction") Timestamp localTimeTransaction,
            @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
            @WebParam(name = "acquirerCountryId") Integer acquirerCountryId,
            @WebParam(name = "messageMiddlewareId") Long messageMiddlewareId,
            @WebParam(name = "transactionNumberAcquirer") String transactionNumberAcquirer,
            @WebParam(name = "cardDueDate") String cardDueDate,
            @WebParam(name = "cardHolder") String cardHolder,
            @WebParam(name = "PinOffset") String PinOffset) {
        return operations.cardBalanceInquiry(cardNumber, CVV, ARQC, documentIdentificationNumber, transactionTypeId, channelId, transactionDate, localTimeTransaction, acquirerTerminalCodeId, acquirerCountryId, messageMiddlewareId, transactionNumberAcquirer, cardDueDate, cardHolder, PinOffset);
    }
      
    @WebMethod    
    public CalculateBonusCardResponse calculateBonus(
        @WebParam(name = "cardNumber") String cardNumber,
        @WebParam(name = "transactionTypeId") Integer transactionTypeId,
        @WebParam(name = "channelId") Integer channelId,
        @WebParam(name = "countryCode") String countryCode,
        @WebParam(name = "amountTransaction") Float amountTransaction,
        @WebParam(name = "transactionNumber") String transactionNumber){
        return operations.calculateBonus(cardNumber, transactionTypeId, channelId,countryCode,amountTransaction,transactionNumber);
    }
    
    @WebMethod
    public TransactionResponse viewCardMovements(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "CVV") String CVV,
            @WebParam(name = "cardDueDate") String cardDueDate,
            @WebParam(name = "cardHolder") String cardHolder,
            @WebParam(name = "documentIdentificationNumber") String documentIdentificationNumber,
            @WebParam(name = "channelId") Integer channelId,
            @WebParam(name = "transactionTypeId") Integer transactionTypeId,
            @WebParam(name = "messageMiddlewareId") Long messageMiddlewareId,
            @WebParam(name = "transactionDate") Date transactionDate,
            @WebParam(name = "localTimeTransaction") Timestamp localTimeTransaction,
            @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
            @WebParam(name = "acquirerCountryId") Integer acquirerCountryId,
            @WebParam(name = "startDate") String startDate,
            @WebParam(name = "endingDate") String endingDate) {
        return operations.viewCardMovements(cardNumber,CVV,cardDueDate,cardHolder,documentIdentificationNumber,channelId,transactionTypeId,messageMiddlewareId,transactionDate,localTimeTransaction,acquirerTerminalCodeId,acquirerCountryId,startDate,endingDate);
    }
    
    @WebMethod
    public TransactionResponse activateCard(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "cardHolder") String cardHolder,
            @WebParam(name = "CVV") String CVV,
            @WebParam(name = "cardDueDate") String cardDueDate,
            @WebParam(name = "answerDocumentIdentificationNumber") String answerDocumentIdentificationNumber,
            @WebParam(name = "answerNumberPhoneCustomer") String answerNumberPhoneCustomer,
            @WebParam(name = "answerDateBirth") Date answerDateBirth,
            @WebParam(name = "answerEmailCustomer") String answerEmailCustomer,
            @WebParam(name = "messageMiddlewareId") Long messageMiddlewareId,
            @WebParam(name = "transactionTypeId") Integer transactionTypeId,
            @WebParam(name = "channelId") Integer channelId,
            @WebParam(name = "transactionDate") Date transactionDate,
            @WebParam(name = "localTimeTransaction") Timestamp localTimeTransaction,       
            @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
            @WebParam(name = "transactionNumberAcquirer") String transactionNumberAcquirer,
            @WebParam(name = "acquirerCountryId") Integer acquirerCountryId) {
        return operations.activateCard(cardNumber,cardHolder,CVV,cardDueDate,answerDocumentIdentificationNumber,answerNumberPhoneCustomer,answerDateBirth,answerEmailCustomer,messageMiddlewareId,transactionTypeId,channelId,transactionDate,localTimeTransaction,acquirerTerminalCodeId,transactionNumberAcquirer,acquirerCountryId);
    }
    @WebMethod    
    public TransactionResponse transferBetweenAccount(
        @WebParam(name = "cardNumberOrigin") String cardNumberOrigin,
        @WebParam(name = "cardNumberDestinate") String cardNumberDestinate,
        @WebParam(name = "CVVOrigin") String CVVOrigin,
        @WebParam(name = "cardDueDateOrigin") String cardDueDateOrigin,
        @WebParam(name = "cardHolderOrigin") String cardHolderOrigin,
        @WebParam(name = "ARQCOrigin") String ARQCOrigin,
        @WebParam(name = "CVVDestinate") String CVVDestinate,
        @WebParam(name = "cardDueDateDestinate") String cardDueDateDestinate,
        @WebParam(name = "cardHolderDestinate") String cardHolderDestinate,
        @WebParam(name = "ARQCDestinate") String ARQCDestinate,
        @WebParam(name = "channelId") Integer channelId,
        @WebParam(name = "transactionTypeId") Integer transactionTypeId,
        @WebParam(name = "messageMiddlewareId") Long messageMiddlewareId,
        @WebParam(name = "transactionDate") Date transactionDate,
        @WebParam(name = "localTimeTransaction") Timestamp localTimeTransaction,
        @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
        @WebParam(name = "acquirerCountryId") Integer acquirerCountryId,
        @WebParam(name = "amountTransfer") Float amountTransfer,
        @WebParam(name = "dateTimeTransmissionTerminal")Timestamp dateTimeTransmissionTerminal,
        @WebParam(name = "localDateTransaction")Date localDateTransaction){
        return operations.transferBetweenAccount(cardNumberOrigin, cardNumberDestinate, CVVOrigin,cardDueDateOrigin,cardHolderOrigin,ARQCOrigin,CVVDestinate,cardDueDateDestinate,cardHolderDestinate,ARQCDestinate,channelId,transactionTypeId,messageMiddlewareId,transactionDate,localTimeTransaction,acquirerTerminalCodeId,acquirerCountryId,amountTransfer,dateTimeTransmissionTerminal,localDateTransaction);
    }
    
 
    @WebMethod
    public CardResponse validatePinOffset(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "pinOffset") String pinOffset) {
        return operations.validatePinOffset(cardNumber,pinOffset);
    }
    
    @WebMethod
    public TransactionResponse cardWithdrawal(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "CVV") String CVV,
            @WebParam(name = "cardDueDate") String cardDueDate,
            @WebParam(name = "cardHolder") String cardHolder,
            @WebParam(name = "documentIdentificationNumber") String documentIdentificationNumber,
            @WebParam(name = "channelId") Integer channelId,
            @WebParam(name = "transactionTypeId") Integer transactionTypeId,
            @WebParam(name = "messageMiddlewareId") Long messageMiddlewareId,
            @WebParam(name = "transactionDate") Date transactionDate,
            @WebParam(name = "localTimeTransaction") Timestamp localTimeTransaction,
            @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
            @WebParam(name = "acquirerCountryId") Integer acquirerCountryId,
            @WebParam(name = "localDateTransaction")Date localDateTransaction,
            @WebParam(name = "withdrawalAmount") Float withdrawalAmount) {
        return operations.cardWithdrawal(cardNumber,CVV,cardDueDate,cardHolder,documentIdentificationNumber,channelId,transactionTypeId,messageMiddlewareId,transactionDate,localTimeTransaction,acquirerTerminalCodeId,acquirerCountryId,localDateTransaction,withdrawalAmount);
    }
    
    @WebMethod
    public TransactionResponse cardRecharge(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "cardHolder") String cardHolder,
            @WebParam(name = "CVV") String CVV,
            @WebParam(name = "cardDueDate") String cardDueDate,
            @WebParam(name = "messageMiddlewareId") Long messageMiddlewareId,
            @WebParam(name = "transactionTypeId") Integer transactionTypeId,
            @WebParam(name = "channelId") Integer channelId,                      
            @WebParam(name = "transactionDate") Date transactionDate,
            @WebParam(name = "localTimeTransaction") Timestamp localTimeTransaction,
            @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
            @WebParam(name = "transactionNumberAcquirer") String transactionNumberAcquirer,
            @WebParam(name = "acquirerCountryId") Integer acquirerCountryId,
            @WebParam(name = "amountRecharge") Float amountRecharge) {
        return operations.cardRecharge(cardNumber,cardHolder,CVV,cardDueDate,messageMiddlewareId,transactionTypeId,channelId,transactionDate,localTimeTransaction,acquirerTerminalCodeId,transactionNumberAcquirer,acquirerCountryId,amountRecharge);
    }
    
    @WebMethod    
    public TransactionResponse keyChange(
        @WebParam(name = "cardNumberO") String cardNumber,
        @WebParam(name = "CVV") String CVV,
        @WebParam(name = "cardDueDate") String cardDueDate,
        @WebParam(name = "cardHolde") String cardHolder,
        @WebParam(name = "ARQC") String ARQC,
        @WebParam(name = "channelId") Integer channelId,
        @WebParam(name = "transactionTypeId") Integer transactionTypeId,
        @WebParam(name = "messageMiddlewareId") Long messageMiddlewareId,
        @WebParam(name = "transactionDate") Date transactionDate,
        @WebParam(name = "localTimeTransaction") Timestamp localTimeTransaction,
        @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
        @WebParam(name = "acquirerCountryId") Integer acquirerCountryId,
        @WebParam(name = "newpinOffset") String newPinOffset,
        @WebParam(name = "currentPinBlock") String currentPinBlock,
        @WebParam(name = "newPinBlock") String newPinBlock,
        @WebParam(name = "terminalId") Long terminalId){
        return operations.keyChange(cardNumber, CVV,cardDueDate,cardHolder,ARQC,channelId,transactionTypeId,messageMiddlewareId,transactionDate,localTimeTransaction,acquirerTerminalCodeId,acquirerCountryId,newPinOffset, currentPinBlock, newPinBlock,terminalId);
    }
   
    
    @WebMethod
    public TransactionResponse validatePropertiesKey(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "pinOffset") String pinOffset,
            @WebParam(name = "channelId") Integer channelId) {
        return operations.validatePropertiesKey(cardNumber,pinOffset,channelId);
    }
    
     @WebMethod
    public TransactionPurchageResponse cardPurchage(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "cardHolder") String cardHolder,
            @WebParam(name = "CVV") String CVV,
            @WebParam(name = "cardDueDate") String cardDueDate,
            @WebParam(name = "messageMiddlewareId") Long messageMiddlewareId,
            @WebParam(name = "transactionTypeId") Integer transactionTypeId,
            @WebParam(name = "channelId") Integer channelId,                      
            @WebParam(name = "transactionDate") Date transactionDate,
            @WebParam(name = "localTimeTransaction") Timestamp localTimeTransaction,
            @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
            @WebParam(name = "transactionNumberAcquirer") String transactionNumberAcquirer,
            @WebParam(name = "acquirerCountryId") Integer acquirerCountryId,
            @WebParam(name = "purchaseAmount") Float purchaseAmount,
            @WebParam(name = "documentNumber") String documentNumber,
            @WebParam(name = "pinBlock") String pinBlock,
            @WebParam(name = "ARQC") String ARQC,
            @WebParam(name = "terminalId") String terminalId,
            @WebParam(name = "oPMode") String oPMode,
            @WebParam(name = "schemeEMV") String schemeEMV,
            @WebParam(name = "seqNumber") String seqNumber,
            @WebParam(name = "atc") String atc,
            @WebParam(name = "unpredictableNumber") String unpredictableNumber,
            @WebParam(name = "transactionData") String transactionData){
        return operations.cardPurchage(cardNumber,cardHolder,CVV,cardDueDate,messageMiddlewareId,transactionTypeId,channelId,transactionDate,localTimeTransaction,acquirerTerminalCodeId,transactionNumberAcquirer,acquirerCountryId,purchaseAmount,pinBlock, ARQC, terminalId,
                oPMode,schemeEMV,seqNumber,atc,unpredictableNumber,transactionData);
    }
    
}
