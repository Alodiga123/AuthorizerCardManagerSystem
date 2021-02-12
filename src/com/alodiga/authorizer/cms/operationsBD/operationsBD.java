/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alodiga.authorizer.cms.operationsBD;

import com.cms.commons.models.ProductHasChannelHasTransaction;
import com.cms.commons.models.RateByCard;
import com.cms.commons.models.RateByProduct;
import com.cms.commons.models.Sequences;
import com.cms.commons.models.TransactionsManagement;
import com.cms.commons.models.TransactionsManagementHistory;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author jose
 */
public interface operationsBD {    
    public TransactionsManagement createTransactionsManagement(TransactionsManagement management, Integer acquirerId, String acquirerTerminalCode, Integer acquirerCountryId, String transactionNumberAcquirer, Date dateTransaction, 
                                  Integer transactionTypeId, Integer channelId, Timestamp dateTimeTransmissionTerminal, Timestamp localTimeTransmission, Date localDateTransaction, Integer localCurrencyTransactionId,
                                  Float localCurrencyTransactionAmount, Integer settlementCurrencyTransactionId, Float settlementTransactionAmount, Float rateConvertionSettlement, Float acquirerCommisionAmount,
                                  Float acquirerSettlementCommisionAmount, Float transactionRateAmount, Integer transactionCityId, Integer statusTransactionManagementId, String cardNumber, String cardHolder,
                                  String CVV, String expirationCardDate, Integer pinLenght, String transferDestinationCardNumber, Integer issuerId, String mccCodeTrade, String tradeName, String systemTraceAuditNumber,
                                  Integer numberMovementsCheckBalance, String responseCode, Long messageMiddlewareId, Integer DocumentTypeId, EntityManager entityManager);
    
    public TransactionsManagementHistory createTransactionsManagementHistory(TransactionsManagement transactionManagement, Integer acquirerId, String acquirerTerminalCode, Integer acquirerCountryId, String transactionNumberAcquirer, Date dateTransaction, 
                                  String transactionSequence, Integer transactionTypeId, Integer channelId, Timestamp dateTimeTransmissionTerminal, Timestamp localTimeTransmission, Date localDateTransaction, 
                                  Integer localCurrencyTransactionId, Float localCurrencyTransactionAmount, Integer settlementCurrencyTransactionId, Float settlementTransactionAmount, Float rateConvertionSettlement, 
                                  Float acquirerCommisionAmount, Float acquirerSettlementCommisionAmount, Float transactionRateAmount, Integer transactionCityId, Integer statusTransactionManagementId, String cardNumber,
                                  String cardHolder, String CVV, String expirationCardDate, Integer pinLenght, String transferDestinationCardNumber, Integer issuerId, String mccCodeTrade, String tradeName, String systemTraceAuditNumber,
                                  Integer numberMovementsCheckBalance, String responseCode, Long messageMiddlewareId, String transactionNumberIssuer, EntityManager entityManager);
    
    public TransactionsManagement saveTransactionsManagement(TransactionsManagement transactionsManagement, EntityManager entityManager) throws Exception;
    
    public TransactionsManagementHistory saveTransactionsManagementHistory(TransactionsManagementHistory transactionsManagementHistory, EntityManager entityManager) throws Exception;
        
    //Consultas a BD
    public RateByCard getRateByCard(Long cardId, Integer channelId, Integer transactionTypeId, EntityManager entityManager);
    public RateByProduct getRateByProduct(Long productId, Integer channelId, Integer transactionTypeId, EntityManager entityManager);
    public Long getTotalTransactionsByCard(String cardNumber, Integer channelId, Integer transactionTypeId, EntityManager entityManager);
    public Long getTotalTransactionsByCardByDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer channelId, Integer transactionTypeId, EntityManager entityManager);
    public ProductHasChannelHasTransaction getSettingLimits(Integer transactionId, Integer channelId, Long productId, EntityManager entityManager);
    public Long getTransactionsByCardByTransactionByProductCurrentDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer transactionTypeId, Integer channelId, String code, boolean isTransactionLocal, Integer countryId, EntityManager entityManager);
    public Double getAmountMaxByUserByUserByTransactionByProductCurrentDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer transactionTypeId, Integer channelId, String code, boolean isTransactionLocal, Integer countryId, EntityManager entityManager);
    public List<TransactionsManagementHistory> getCardMovements(String cardNumber, Date startDate, Date endingDate, EntityManager entityManager);
}
