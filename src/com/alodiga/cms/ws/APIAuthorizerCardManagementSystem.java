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
    public AccountCard getAccountNumberByCard(
            @WebParam(name = "cardNumber") String cardNumber) {
        return operations.getAccountNumberByCard(cardNumber);
    }
    
    @WebMethod
    public CardResponse calculatesCheckDigitLunh(
            @WebParam(name = "cardNumber") String cardNumber) {
        return operations.calculatesCheckDigitLunh(cardNumber);
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
            @WebParam(name = "localTimeTransaction") String localTimeTransaction,
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
            @WebParam(name = "localTimeTransaction") String localTimeTransaction,
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
            @WebParam(name = "localTimeTransaction") String localTimeTransaction,
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
            @WebParam(name = "localTimeTransaction") String localTimeTransaction,       
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
        @WebParam(name = "localTimeTransaction") String localTimeTransaction,
        @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
        @WebParam(name = "acquirerCountryId") Integer acquirerCountryId,
        @WebParam(name = "amountTransfer") Float amountTransfer,
        @WebParam(name = "dateTimeTransmissionTerminal")String dateTimeTransmissionTerminal,
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
            @WebParam(name = "localTimeTransaction") String localTimeTransaction,
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
            @WebParam(name = "localTimeTransaction") String localTimeTransaction,
            @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
            @WebParam(name = "transactionNumberAcquirer") String transactionNumberAcquirer,
            @WebParam(name = "acquirerCountryId") Integer acquirerCountryId,
            @WebParam(name = "amountRecharge") Float amountRecharge) {
        return operations.cardRecharge(cardNumber,cardHolder,CVV,cardDueDate,messageMiddlewareId,transactionTypeId,channelId,transactionDate,localTimeTransaction,acquirerTerminalCodeId,transactionNumberAcquirer,acquirerCountryId,amountRecharge);
    }
    
    @WebMethod    
    public TransactionResponse keyChange(
        @WebParam(name = "cardNumber") String cardNumber,
        @WebParam(name = "CVV") String CVV,
        @WebParam(name = "cardDueDate") String cardDueDate,
        @WebParam(name = "cardHolde") String cardHolder,
        @WebParam(name = "ARQC") String ARQC,
        @WebParam(name = "channelId") Integer channelId,
        @WebParam(name = "transactionTypeId") Integer transactionTypeId,
        @WebParam(name = "messageMiddlewareId") Long messageMiddlewareId,
        @WebParam(name = "transactionDate") Date transactionDate,
        @WebParam(name = "localTimeTransaction") String localTimeTransaction,
        @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
        @WebParam(name = "acquirerCountryId") Integer acquirerCountryId,
        @WebParam(name = "newpinOffset") String newPinClear,        
        @WebParam(name = "terminalId") String terminalId){
        return operations.keyChange(cardNumber, CVV,cardDueDate,cardHolder,ARQC,channelId,transactionTypeId,messageMiddlewareId,transactionDate,localTimeTransaction,acquirerTerminalCodeId,acquirerCountryId,newPinClear,terminalId);
    }
   
    
    @WebMethod
    public TransactionResponse validatePropertiesKey(
            @WebParam(name = "card") Card card,
            @WebParam(name = "pinOffset") String pinOffset,
            @WebParam(name = "channelId") Integer channelId,
            @WebParam(name = "typeTransaction") boolean typeTransaction) {
        return operations.validatePropertiesKey(card,pinOffset,channelId,typeTransaction);
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
            @WebParam(name = "localTimeTransaction") String localTimeTransaction,
            @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
            @WebParam(name = "transactionNumberAcquirer") String transactionNumberAcquirer,
            @WebParam(name = "acquirerCountryId") String acquirerCountryId,
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
    @WebMethod    
    public TransactionResponse reverseWalletWithdrawal(
        @WebParam(name = "cardNumber") String cardNumber,
        @WebParam(name = "CVV") String CVV,
        @WebParam(name = "cardDueDate") String cardDueDate,
        @WebParam(name = "cardHolde") String cardHolder,
        @WebParam(name = "ARQC") String ARQC,
        @WebParam(name = "channelId") Integer channelId,
        @WebParam(name = "transactionTypeId") Integer transactionTypeId,
        @WebParam(name = "messageMiddlewareId") Long messageMiddlewareId,
        @WebParam(name = "transactionDate") Date transactionDate,
        @WebParam(name = "localTimeTransaction") String localTimeTransaction,
        @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
        @WebParam(name = "acquirerCountryId") Integer acquirerCountryId,
        @WebParam(name = "transactionNumber") String transactionNumber,
        @WebParam(name = "transactionSequence") String transactionSequence){
        return operations.reverseWalletWithdrawal(cardNumber, CVV,cardDueDate,cardHolder,ARQC,channelId,transactionTypeId,messageMiddlewareId,transactionDate,localTimeTransaction,acquirerTerminalCodeId,acquirerCountryId,transactionNumber,transactionSequence);
    }
    
    @WebMethod
    public TransactionResponse saveRegisterPin(
            @WebParam(name = "cardNumber") String cardNumber,
            @WebParam(name = "CVV") String CVV,
            @WebParam(name = "ARQC") String ARQC,
            @WebParam(name = "transactionTypeId") Integer transactionTypeId,
            @WebParam(name = "channelId") Integer channelId,
            @WebParam(name = "transactionDate") Date transactionDate,
            @WebParam(name = "localTimeTransaction") String localTimeTransaction,
            @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
            @WebParam(name = "acquirerCountryId") Integer acquirerCountryId,
            @WebParam(name = "messageMiddlewareId") Long messageMiddlewareId,
            @WebParam(name = "cardDueDate") String cardDueDate,
            @WebParam(name = "cardHolder") String cardHolder,
            @WebParam(name = "pinClear") String pinClear,
            @WebParam(name = "terminalId") String terminalId){
        return operations.saveRegisterPin(cardNumber, CVV, ARQC,transactionTypeId, channelId, transactionDate, localTimeTransaction, acquirerTerminalCodeId, acquirerCountryId, messageMiddlewareId, cardDueDate, cardHolder, pinClear, terminalId);
    }
    
   @WebMethod    
    public TransactionResponse reverseCardPurchase(
        @WebParam(name = "cardNumber") String cardNumber,
        @WebParam(name = "CVV") String CVV,
        @WebParam(name = "cardDueDate") String cardDueDate,
        @WebParam(name = "cardHolde") String cardHolder,
        @WebParam(name = "ARQC") String ARQC,
        @WebParam(name = "channelId") Integer channelId,
        @WebParam(name = "transactionTypeId") Integer transactionTypeId,
        @WebParam(name = "messageMiddlewareId") Long messageMiddlewareId,
        @WebParam(name = "transactionDate") Date transactionDate,
        @WebParam(name = "localTimeTransaction") String localTimeTransaction,
        @WebParam(name = "acquirerTerminalCodeId") String acquirerTerminalCodeId,
        @WebParam(name = "acquirerCountryId") Integer acquirerCountryId,
        @WebParam(name = "transactionNumber") String transactionNumber){
        return operations.reverseCardPurchage(cardNumber, CVV,cardDueDate,cardHolder,ARQC,channelId,transactionTypeId,messageMiddlewareId,transactionDate,localTimeTransaction,acquirerTerminalCodeId,acquirerCountryId,transactionNumber);
    }
    
}
