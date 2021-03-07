/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alodiga.authorizer.cms.operationsBD;

import com.alodiga.authorizer.cms.responses.CardResponse;
import com.cms.commons.models.AccountCard;
import com.cms.commons.models.BalanceHistoryCard;
import com.cms.commons.models.BonusCard;
import com.cms.commons.models.Card;
import com.cms.commons.models.CardStatus;
import com.cms.commons.models.Channel;
import com.cms.commons.models.Country;
import com.cms.commons.models.DaysWeek;
import com.cms.commons.models.HistoryCardStatusChanges;
import com.cms.commons.models.ProductHasChannelHasTransaction;
import com.cms.commons.models.ProgramLoyalty;
import com.cms.commons.models.ProgramLoyaltyTransaction;
import com.cms.commons.models.RateByCard;
import com.cms.commons.models.RateByProduct;
import com.cms.commons.models.Sequences;
import com.cms.commons.models.StatusUpdateReason;
import com.cms.commons.models.TransactionsManagement;
import com.cms.commons.models.TransactionsManagementHistory;
import com.cms.commons.models.User;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author jose
 */
public interface operationsBD {
    //Builders
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
    
    public HistoryCardStatusChanges createHistoryCardStatusChanges(Card cardId, CardStatus cardStatusId, User userResponsabileId, StatusUpdateReason statusUpdateReasonId, EntityManager entityManager);
    public BalanceHistoryCard createBalanceHistoryCard(Card cardUserId, Long transactionManagementId, Float previousBalance, Float currentBalance, EntityManager entityManager);
    
    //Operaciones en BD
    public TransactionsManagement saveTransactionsManagement(TransactionsManagement transactionsManagement, EntityManager entityManager) throws Exception;    
    public TransactionsManagementHistory saveTransactionsManagementHistory(TransactionsManagementHistory transactionsManagementHistory, EntityManager entityManager) throws Exception;
    public Card saveCard(Card card, EntityManager entityManager) throws Exception;
    public HistoryCardStatusChanges saveHistoryCardStatusChanges(HistoryCardStatusChanges historyCardStatusChanges, EntityManager entityManager) throws Exception; 
    public BalanceHistoryCard saveBalanceHistoryCard(BalanceHistoryCard balanceHistoryCard, EntityManager entityManager) throws Exception;
        
    //Consultas a BD
    public RateByCard getRateByCard(Long cardId, Integer channelId, Integer transactionTypeId, EntityManager entityManager);
    public RateByProduct getRateByProduct(Long productId, Integer channelId, Integer transactionTypeId, EntityManager entityManager);
    public Long getTotalTransactionsByCard(String cardNumber, Integer channelId, Integer transactionTypeId, EntityManager entityManager);
    public Long getTotalTransactionsByCardByDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer channelId, Integer transactionTypeId, EntityManager entityManager);
    public ProductHasChannelHasTransaction getSettingLimits(Integer transactionId, Integer channelId, Long productId, EntityManager entityManager);
    public Long getTransactionsByCardByTransactionByProductCurrentDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer transactionTypeId, Integer channelId, String code, boolean isTransactionLocal, Integer countryId, EntityManager entityManager);
    public Double getAmountMaxByUserByUserByTransactionByProductCurrentDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer transactionTypeId, Integer channelId, String code, boolean isTransactionLocal, Integer countryId, EntityManager entityManager);
    public List<TransactionsManagementHistory> getCardMovements(String cardNumber, Date startDate, Date endingDate, EntityManager entityManager);
    public List<ProgramLoyalty> getProgramLoyaltybyProductId(Long productId, EntityManager entityManager);
    public ProgramLoyaltyTransaction getProgramLoyaltyTransactionbyParam(Long programLoyaltyId, Integer transactionId, Integer channelId, EntityManager entityManager);
    public boolean checkActiveProgramLoyalty(Long programLoyaltyId, int dayWeekId, EntityManager entityManager);
    public DaysWeek getDaysWeekByDate(EntityManager entityManager);
    public TransactionsManagement getTransactionsManagementByNumber(String transactionNumber, EntityManager entityManager);
    public BonusCard getBonusCardByCardId(Long cardId, EntityManager entityManager);
    public BalanceHistoryCard loadLastBalanceHistoryByCard(Long cardId, EntityManager entityManager);
    public AccountCard getAccountCardbyCardId(Long cardId, EntityManager entityManager);
    public Channel getChannelById(Integer channelId, EntityManager entityManager);
    public Country getCountry(String countryCode, EntityManager entityManager);
    public CardStatus getStatusCard(int cardStatusId, EntityManager entityManager);
    
    //Métodos reutilizados por la API
    public String transformCardNumber(String cardNumber);
    public String maskCCNumber(String ccnum);
    public boolean testConsecutive(String pinoffset);
    public boolean testContinuous(String pinOffset);
    public boolean isNumeric(String pinOffset);
}
