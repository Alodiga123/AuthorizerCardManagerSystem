/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alodiga.authorizer.cms.operationsBDImp;

import com.alodiga.authorizer.cms.operationsBD.operationsBD;
import com.alodiga.authorizer.cms.responses.CardKeyHistoryListResponse;
import com.alodiga.authorizer.cms.responses.CardResponse;
import com.alodiga.authorizer.cms.responses.ResponseCode;
import com.cms.commons.enumeraciones.TransactionE;
import com.cms.commons.models.AccountCard;
import com.cms.commons.models.BalanceHistoryCard;
import com.cms.commons.models.BonusCard;
import com.cms.commons.models.Card;
import com.cms.commons.models.CardKeyHistory;
import com.cms.commons.models.CardStatus;
import com.cms.commons.models.Channel;
import com.cms.commons.models.Country;
import com.cms.commons.models.DaysWeek;
import com.cms.commons.models.DaysWeekHasProgramLoyalty;
import com.cms.commons.models.HistoryCardStatusChanges;
import com.cms.commons.models.KeyProperties;
import com.cms.commons.models.Sequences;
import com.cms.commons.models.TransactionsManagement;
import com.cms.commons.models.TransactionsManagementHistory;
import com.cms.commons.util.Constants;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import com.cms.commons.models.ProductHasChannelHasTransaction;
import com.cms.commons.models.ProgramLoyalty;
import com.cms.commons.models.ProgramLoyaltyTransaction;
import com.cms.commons.models.RateByCard;
import com.cms.commons.models.RateByProduct;
import com.cms.commons.models.StatusUpdateReason;
import com.cms.commons.models.TransactionPoint;
import com.cms.commons.models.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jose
 */
public class operationsBDImp implements operationsBD {

    public Sequences getSequencesByDocumentTypeByOriginApplication(int documentTypeId, int originApplicationId, EntityManager entityManager) {
        try {
            Sequences sequences = (Sequences) entityManager.createNamedQuery("Sequences.findBydocumentTypeByOriginApplication", Sequences.class).setParameter("documentTypeId", documentTypeId).setParameter("originApplicationId", originApplicationId).getSingleResult();
            return sequences;
        } catch (NoResultException e) {
            return null;
        }
    }

    private String generateNumberSequence(Sequences s, EntityManager entityManager) {
        String secuence = "";
        try {
            Integer numberSequence = s.getCurrentValue() > 1 ? s.getCurrentValue() : s.getInitialValue();
            s.setCurrentValue(s.getCurrentValue() + 1);
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            secuence = ((s.getOriginApplicationId().getId().equals(Constants.ORIGIN_APPLICATION_CMS_ID)) ? "CMS-" : "APP-")
                    .concat(s.getDocumentTypeId().getAcronym()).concat("-")
                    .concat(String.valueOf(year)).concat("-")
                    .concat(numberSequence.toString());
            entityManager.persist(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return secuence;
    }

    @Override
    public TransactionsManagement createTransactionsManagement(TransactionsManagement management, Integer acquirerId, String acquirerTerminalCode, Integer acquirerCountryId, String transactionNumberAcquirer, Date dateTransaction,
            Integer transactionTypeId, Integer channelId, String dateTimeTransmissionTerminal, String localTimeTransmission, Date localDateTransaction, Integer localCurrencyTransactionId, Float localCurrencyTransactionAmount,
            Integer settlementCurrencyTransactionId, Float settlementTransactionAmount, Float rateConvertionSettlement, Float acquirerCommisionAmount, Float acquirerSettlementCommisionAmount, Float transactionRateAmount,
            Integer transactionCityId, Integer statusTransactionManagementId, String cardNumber, String cardHolder, String CVV, String expirationCardDate, Integer pinLenght, String transferDestinationCardNumber, Integer issuerId,
            String mccCodeTrade, String tradeName, String systemTraceAuditNumber, Integer numberMovementsCheckBalance, String responseCode, Long messageMiddlewareId, Integer DocumentTypeId, EntityManager entityManager) {

        TransactionsManagement transactionsManagement = new TransactionsManagement();
        Sequences sequence = getSequencesByDocumentTypeByOriginApplication(DocumentTypeId, Constants.ORIGIN_APPLICATION_CMS_AUTHORIZE, entityManager);
        String transactionNumberIssuer = generateNumberSequence(sequence, entityManager);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        String transactionSequenceNumber = transactionTypeId.toString().concat(String.valueOf(year)).concat(sequence.getCurrentValue().toString());
        if (management == null) {
            transactionsManagement.setAcquirerTerminalCode(acquirerTerminalCode);
            transactionsManagement.setAcquirerCountryId(acquirerCountryId);
            transactionsManagement.setDateTransaction(dateTransaction);
            transactionsManagement.setCardHolder(cardHolder);
            transactionsManagement.setCardNumber(cardNumber);
            transactionsManagement.setCvv(CVV);
            transactionsManagement.setExpirationCardDate(expirationCardDate);
            transactionsManagement.setIssuerId(issuerId);
            transactionsManagement.setMccCodeTrade(mccCodeTrade);
            transactionsManagement.setTradeName(tradeName);
        } else {
            transactionsManagement.setAcquirerTerminalCode(management.getAcquirerTerminalCode());
            transactionsManagement.setAcquirerCountryId(management.getAcquirerCountryId());
            transactionsManagement.setTransactionReference(management.getTransactionNumberAcquirer());
            transactionsManagement.setDateTransaction(management.getDateTransaction());
            transactionsManagement.setCardHolder(management.getCardHolder());
            transactionsManagement.setCardNumber(management.getCardNumber());
            transactionsManagement.setCvv(management.getCvv());
            transactionsManagement.setExpirationCardDate(management.getExpirationCardDate());
            transactionsManagement.setIssuerId(management.getIssuerId());
            transactionsManagement.setMccCodeTrade(management.getMccCodeTrade());
            transactionsManagement.setTradeName(management.getTradeName());
        }
        transactionsManagement.setTransactionNumberIssuer(transactionNumberIssuer);
        transactionsManagement.setTransactionDateIssuer(new Timestamp(new Date().getTime()));
        transactionsManagement.setTransactionSequence(transactionSequenceNumber);
        transactionsManagement.setTransactionTypeId(transactionTypeId);
        transactionsManagement.setChannelId(channelId);
        transactionsManagement.setSettlementTransactionAmount(settlementTransactionAmount);
        transactionsManagement.setSettlementCurrencyTransactionId(settlementCurrencyTransactionId);
        transactionsManagement.setStatusTransactionManagementId(statusTransactionManagementId);
        transactionsManagement.setResponseCode(responseCode);
        transactionsManagement.setCreateDate(new Timestamp(new Date().getTime()));

        return transactionsManagement;
    }

    @Override
    public TransactionsManagementHistory createTransactionsManagementHistory(TransactionsManagement transactionManagement, Integer acquirerId, String acquirerTerminalCode, Integer acquirerCountryId, String transactionNumberAcquirer, Date dateTransaction,
            String transactionSequence, Integer transactionTypeId, Integer channelId, String dateTimeTransmissionTerminal, String localTimeTransmission, Date localDateTransaction,
            Integer localCurrencyTransactionId, Float localCurrencyTransactionAmount, Integer settlementCurrencyTransactionId, Float settlementTransactionAmount, Float rateConvertionSettlement,
            Float acquirerCommisionAmount, Float acquirerSettlementCommisionAmount, Float transactionRateAmount, Integer transactionCityId, Integer statusTransactionManagementId, String cardNumber,
            String cardHolder, String CVV, String expirationCardDate, Integer pinLenght, String transferDestinationCardNumber, Integer issuerId, String mccCodeTrade, String tradeName, String systemTraceAuditNumber,
            Integer numberMovementsCheckBalance, String responseCode, Long messageMiddlewareId, String transactionNumberIssuer, EntityManager entityManager) {

        TransactionsManagementHistory transactionsManagementHistory = new TransactionsManagementHistory();
        if (transactionManagement == null) {
            transactionsManagementHistory.setAcquirerTerminalCode(acquirerTerminalCode);
            transactionsManagementHistory.setAcquirerCountryId(acquirerCountryId);
            transactionsManagementHistory.setDateTransaction(dateTransaction);
            transactionsManagementHistory.setCardHolder(cardHolder);
            transactionsManagementHistory.setCardNumber(cardNumber);
            transactionsManagementHistory.setCvv(CVV);
            transactionsManagementHistory.setExpirationCardDate(expirationCardDate);
            transactionsManagementHistory.setIssuerId(issuerId);
            transactionsManagementHistory.setMccCodeTrade(mccCodeTrade);
            transactionsManagementHistory.setTradeName(tradeName);
        } else {
            transactionsManagementHistory.setAcquirerTerminalCode(transactionManagement.getAcquirerTerminalCode());
            transactionsManagementHistory.setAcquirerCountryId(transactionManagement.getAcquirerCountryId());
            transactionsManagementHistory.setTransactionReference(transactionManagement.getTransactionNumberAcquirer());
            transactionsManagementHistory.setDateTransaction(transactionManagement.getDateTransaction());
            transactionsManagementHistory.setCardHolder(transactionManagement.getCardHolder());
            transactionsManagementHistory.setCardNumber(transactionManagement.getCardNumber());
            transactionsManagementHistory.setCvv(transactionManagement.getCvv());
            transactionsManagementHistory.setExpirationCardDate(transactionManagement.getExpirationCardDate());
            transactionsManagementHistory.setIssuerId(transactionManagement.getIssuerId());
            transactionsManagementHistory.setMccCodeTrade(transactionManagement.getMccCodeTrade());
            transactionsManagementHistory.setTradeName(transactionManagement.getTradeName());
        }
        transactionsManagementHistory.setTransactionNumberIssuer(transactionNumberIssuer);
        transactionsManagementHistory.setTransactionDateIssuer(new Timestamp(new Date().getTime()));
        transactionsManagementHistory.setTransactionSequence(transactionSequence);
        transactionsManagementHistory.setTransactionTypeId(transactionTypeId);
        transactionsManagementHistory.setChannelId(channelId);
        transactionsManagementHistory.setSettlementTransactionAmount(settlementTransactionAmount);
        transactionsManagementHistory.setSettlementCurrencyTransactionId(settlementCurrencyTransactionId);
        transactionsManagementHistory.setStatusTransactionManagementId(statusTransactionManagementId);
        transactionsManagementHistory.setResponseCode(responseCode);
        transactionsManagementHistory.setCreateDate(new Timestamp(new Date().getTime()));

        return transactionsManagementHistory;
    }

    @Override
    public TransactionsManagement saveTransactionsManagement(TransactionsManagement transactionsManagement, EntityManager entityManager) throws Exception {
        try {
            if (transactionsManagement.getId() == null) {
                entityManager.persist(transactionsManagement);
            } else {
                entityManager.merge(transactionsManagement);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        return transactionsManagement;
    }

    @Override
    public TransactionsManagementHistory saveTransactionsManagementHistory(TransactionsManagementHistory transactionsManagementHistory, EntityManager entityManager) throws Exception {
        try {
            if (transactionsManagementHistory.getId() == null) {
                entityManager.persist(transactionsManagementHistory);
            } else {
                entityManager.merge(transactionsManagementHistory);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        return transactionsManagementHistory;
    }

    @Override
    public RateByCard getRateByCard(Long cardId, Integer channelId, Integer transactionTypeId, EntityManager entityManager) {
        try {
            Query query = entityManager.createQuery("SELECT r FROM RateByCard r WHERE r.cardId.id = " + cardId + " AND r.channelId.id = " + channelId + " AND r.transactionId.id = " + transactionTypeId + "");
            query.setMaxResults(1);
            RateByCard result = (RateByCard) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public RateByProduct getRateByProduct(Long productId, Integer channelId, Integer transactionTypeId, EntityManager entityManager) {
        try {
            Query query = entityManager.createQuery("SELECT r FROM RateByProduct r WHERE r.productId.id = " + productId + " AND r.channelId.id = " + channelId + " AND r.transactionId.id = " + transactionTypeId + "");
            query.setMaxResults(1);
            RateByProduct result = (RateByProduct) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Long getTotalTransactionsByCard(String cardNumber, Integer channelId, Integer transactionTypeId, EntityManager entityManager) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(t.id) FROM transactionsManagement t WHERE t.cardNumber = ?1 AND t.channelId = ?2 AND t.transactionTypeId = ?3 AND t.responseCode = '00'");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("1", cardNumber);
        query.setParameter("2", channelId);
        query.setParameter("3", transactionTypeId);
        List result = (List) query.setHint("toplink.refresh", "true").getResultList();
        return result.get(0) != null ? (Long) result.get(0) : 0l;
    }

    @Override
    public Long getTotalTransactionsByCardByDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer channelId, Integer transactionTypeId, EntityManager entityManager) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(t.id) FROM transactionsManagement t WHERE t.createDate between ?1 AND ?2 AND t.cardNumber = ?3 AND t.channelId = ?4 AND t.transactionTypeId = ?5 AND t.responseCode = '00'");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("1", begginingDateTime);
        query.setParameter("2", endingDateTime);
        query.setParameter("3", cardNumber);
        query.setParameter("4", channelId);
        query.setParameter("5", transactionTypeId);
        List result = (List) query.setHint("toplink.refresh", "true").getResultList();
        return result.get(0) != null ? (Long) result.get(0) : 0l;
    }

    @Override
    public ProductHasChannelHasTransaction getSettingLimits(Integer transactionId, Integer channelId, Long productId, EntityManager entityManager) {
        try {
            Query query = entityManager.createQuery("SELECT P FROM ProductHasChannelHasTransaction p WHERE p.productId.id = " + productId + " AND p.channelId.id= " + channelId
                    + " AND p.transactionId.id= " + transactionId + "");
            query.setMaxResults(1);
            ProductHasChannelHasTransaction result = (ProductHasChannelHasTransaction) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Long getTransactionsByCardByTransactionByProductCurrentDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer transactionTypeId, Integer channelId, String code, boolean isTransactionLocal, Integer countryId, EntityManager entityManager) {
        String sql = "SELECT * FROM transactionsManagement t WHERE t.dateTransaction between ?1 AND ?2 AND t.cardNumber = ?3 AND t.transactionTypeId = ?4 AND t.channelId = ?5 AND t.responseCode =?6";
        if (isTransactionLocal) {
            sql += (" AND acquirerCountryId = ?7");
        } else {
            sql += (" AND acquirerCountryId <> ?7");
        }
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("1", begginingDateTime);
        query.setParameter("2", endingDateTime);
        query.setParameter("3", cardNumber);
        query.setParameter("4", transactionTypeId);
        query.setParameter("5", channelId);
        query.setParameter("6", code);
        query.setParameter("7", countryId);
        List result = (List) query.setHint("toplink.refresh", "true").getResultList();
        return !result.isEmpty() ? (Long) result.get(0) : 0l;
    }

    @Override
    public Double getAmountMaxByUserByUserByTransactionByProductCurrentDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer transactionTypeId, Integer channelId, String code, boolean isTransactionLocal, Integer countryId, EntityManager entityManager) {
        String sql = "SELECT SUM(t.settlementTransactionAmount) FROM transactionsManagement t WHERE t.dateTransaction between ?1 AND ?2 AND t.cardNumber = ?3 AND t.transactionTypeId = ?4 AND t.channelId = ?5 AND t.responseCode =?6";
        if (isTransactionLocal) {
            sql += (" AND acquirerCountryId = ?7");
        } else {
            sql += (" AND acquirerCountryId <> ?7");
        }
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("1", begginingDateTime);
        query.setParameter("2", endingDateTime);
        query.setParameter("3", cardNumber);
        query.setParameter("4", transactionTypeId);
        query.setParameter("5", channelId);
        query.setParameter("6", code);
        query.setParameter("7", countryId);
        List result = (List) query.setHint("toplink.refresh", "true").getResultList();
        return result.get(0) != null ? (double) result.get(0) : 0f;
    }

    @Override
    public List<TransactionsManagementHistory> getCardMovements(String cardNumber, Date startDate, Date endingDate, EntityManager entityManager) {
        List<TransactionsManagementHistory> transactionsManagementHistory = new ArrayList<TransactionsManagementHistory>();
        String sql = "SELECT t FROM TransactionsManagementHistory t WHERE t.cardNumber = '" + cardNumber + "' AND t.transactionDateIssuer BETWEEN ?1 AND ?2 AND t.transactionTypeId IN(?3,?4,?5,?6,?7,?8,?9,?10,?11)";
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("1", startDate);
        query.setParameter("2", endingDate);
        query.setParameter("3", TransactionE.CARD_RECHARGE.getId());
        query.setParameter("4", TransactionE.RECARGA_INICIAL.getId());
        query.setParameter("5", TransactionE.RETIRO_DOMESTICO.getId());
        query.setParameter("6", TransactionE.RETIRO_INTERNACIONAL.getId());
        query.setParameter("7", TransactionE.COMPRA_DOMESTICA_PIN.getId());
        query.setParameter("8", TransactionE.COMPRA_INTERNACIONAL_PIN.getId());
        query.setParameter("9", TransactionE.DEPOSITO.getId());
        query.setParameter("10", TransactionE.TRANSFERENCIAS_PROPIAS.getId());
        query.setParameter("11", TransactionE.RECARGA_MANUAL.getId());
        try {
            transactionsManagementHistory = query.setHint("toplink.refresh", "true").getResultList();
        } catch (NoResultException e) {
            return null;
        }
        return transactionsManagementHistory;
    }

    @Override
    public List<ProgramLoyalty> getProgramLoyaltybyProductId(Long productId, EntityManager entityManager) {
        try {
            Query query = entityManager.createQuery("SELECT p FROM ProgramLoyalty p WHERE p.productId.id = " + productId + " AND p.statusProgramLoyaltyId.id=" + Constants.STATUS_LOYALTY_PROGRAM_ACTIVE);
            List<ProgramLoyalty> result = query.setHint("toplink.refresh", "true").getResultList();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public ProgramLoyaltyTransaction getProgramLoyaltyTransactionbyParam(Long programLoyaltyId, Integer transactionId, Integer channelId, EntityManager entityManager) {
        try {
            Query query = entityManager.createQuery("SELECT P FROM ProgramLoyaltyTransaction p WHERE p.programLoyaltyId.id = " + programLoyaltyId + " AND p.channelId.id= " + channelId
                    + " AND p.transactionId.id= " + transactionId + "");
            query.setMaxResults(1);
            ProgramLoyaltyTransaction result = (ProgramLoyaltyTransaction) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean checkActiveProgramLoyalty(Long programLoyaltyId, int dayWeekId, EntityManager entityManager) {
        try {
            Query query = entityManager.createQuery("SELECT d FROM DaysWeekHasProgramLoyalty d WHERE d.programLoyaltyId.id = " + programLoyaltyId + " AND d.daysWeekId.id=" + dayWeekId);
            query.setMaxResults(1);
            DaysWeekHasProgramLoyalty result = (DaysWeekHasProgramLoyalty) query.setHint("toplink.refresh", "true").getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public DaysWeek getDaysWeekByDate(EntityManager entityManager) {
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_WEEK);
        try {
            Query query = entityManager.createQuery("SELECT d FROM DaysWeek d WHERE d.id = " + day);
            query.setMaxResults(1);
            DaysWeek result = (DaysWeek) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public TransactionsManagement getTransactionsManagementByNumber(String transactionNumber, EntityManager entityManager) {
        String sql = "SELECT t FROM TransactionsManagement t WHERE t.transactionNumberAcquirer = ?1";
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("1", transactionNumber);
        try {
            TransactionsManagement result = (TransactionsManagement) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public BonusCard getBonusCardByCardId(Long cardId, EntityManager entityManager) {
        String sql = "SELECT b FROM BonusCard b WHERE b.cardId.id = ?1";
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("1", cardId);
        try {
            BonusCard result = (BonusCard) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public BalanceHistoryCard loadLastBalanceHistoryByCard(Long cardId, EntityManager entityManager) {
        try {
            Query query = entityManager.createQuery("SELECT b FROM BalanceHistoryCard b WHERE b.cardUserId.id = '" + cardId + "'");
            query.setMaxResults(1);
            BalanceHistoryCard result = (BalanceHistoryCard) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public AccountCard getAccountCardbyCardId(Long cardId, EntityManager entityManager) {
        try {
            AccountCard result = (AccountCard) entityManager.createNamedQuery("AccountCard.findByCardId", AccountCard.class).setParameter("cardId", cardId).getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Channel getChannelById(Integer channelId, EntityManager entityManager) {
        try {
            Query query = entityManager.createQuery("SELECT c FROM Channel c WHERE c.id = " + channelId);
            query.setMaxResults(1);
            Channel result = (Channel) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Country getCountry(String countryCode, EntityManager entityManager) {
        try {
            Query query = entityManager.createQuery("SELECT c FROM Country c WHERE c.codeIso3 = '" + countryCode + "'");
            query.setMaxResults(1);
            Country result = (Country) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public String transformCardNumber(String cardNumber) {
        StringBuilder cadena = new StringBuilder(cardNumber);
        for (int i = 5; i < cadena.length(); i++) {
            if (i <= 11) {
                cadena.setCharAt(i, '*');
            }
        }
        return cadena.toString();
    }

    @Override
    public String maskCCNumber(String ccnum) {
        int total = ccnum.length();
        int startlen = 4, endlen = 4;
        int masklen = total - (startlen + endlen);
        StringBuffer maskedbuf = new StringBuffer(ccnum.substring(0, startlen));
        for (int i = 0; i < masklen; i++) {
            maskedbuf.append('X');
        }
        maskedbuf.append(ccnum.substring(startlen + masklen, total));
        String masked = maskedbuf.toString();
        return masked;
    }

    @Override
    public Card saveCard(Card card, EntityManager entityManager) throws Exception {
        try {
            if (card.getId() == null) {
                entityManager.persist(card);
            } else {
                entityManager.merge(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        return card;
    }

    @Override
    public CardStatus getStatusCard(int cardStatusId, EntityManager entityManager) {
        try {
            CardStatus result = (CardStatus) entityManager.createNamedQuery("CardStatus.findById", CardStatus.class).setParameter("id", cardStatusId).getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public HistoryCardStatusChanges createHistoryCardStatusChanges(Card cardId, CardStatus cardStatusId, User userResponsabileId, StatusUpdateReason statusUpdateReasonId, EntityManager entityManager) {
        HistoryCardStatusChanges historyCardStatusChanges = new HistoryCardStatusChanges();
        historyCardStatusChanges.setCardId(cardId);
        historyCardStatusChanges.setCardStatusId(cardStatusId);
        historyCardStatusChanges.setStatusUpdateReasonId(statusUpdateReasonId);
        historyCardStatusChanges.setUserResponsabileId(userResponsabileId);
        historyCardStatusChanges.setCreateDate(new Timestamp(new Date().getTime()));
        return historyCardStatusChanges;
    }

    @Override
    public HistoryCardStatusChanges saveHistoryCardStatusChanges(HistoryCardStatusChanges historyCardStatusChanges, EntityManager entityManager) throws Exception {
        try {
            if (historyCardStatusChanges.getId() == null) {
                entityManager.persist(historyCardStatusChanges);
            } else {
                entityManager.merge(historyCardStatusChanges);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        return historyCardStatusChanges;
    }

    @Override
    public BalanceHistoryCard createBalanceHistoryCard(Card cardUserId, Long transactionManagementId, Float previousBalance, Float currentBalance, EntityManager entityManager) {
        BalanceHistoryCard balanceHistoryCard = new BalanceHistoryCard();
        balanceHistoryCard.setCardUserId(cardUserId);
        balanceHistoryCard.setTransactionsManagementId(transactionManagementId);
        balanceHistoryCard.setPreviousBalance(previousBalance);
        balanceHistoryCard.setCurrentBalance(currentBalance);
        balanceHistoryCard.setCreateDate(new Timestamp(new Date().getTime()));
        return balanceHistoryCard;
    }

    @Override
    public BalanceHistoryCard saveBalanceHistoryCard(BalanceHistoryCard balanceHistoryCard, EntityManager entityManager) throws Exception {
        try {
            if (balanceHistoryCard.getId() == null) {
                entityManager.persist(balanceHistoryCard);
            } else {
                entityManager.merge(balanceHistoryCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        return balanceHistoryCard;
    }

    public boolean testConsecutive(String pinoffset) {
        if (pinoffset.length() == 1) {
            return true;
        } else if (pinoffset.isEmpty()) {
            return false;
        }

        for (int i = 0; i < pinoffset.length() - 1; i++) {
            int valor1 = (int) pinoffset.charAt(i);
            int valor2 = (int) pinoffset.charAt(i + 1);

            if (valor1 == valor2) {
                return true;
            }
        }
        return false;
    }

    public boolean testContinuous(String pinOffset) {
        if (pinOffset.length() == 1) {
            return true;
        } else if (pinOffset.isEmpty()) {
            return false;
        }

        for (int i = 0; i < pinOffset.length() - 1; i++) {
            int valor1 = (int) pinOffset.charAt(i);
            int valor2 = (int) pinOffset.charAt(i + 1);

            if (valor1 + 1 != valor2) {
                return false;
            }
        }
        return true;
    }

    public boolean isNumeric(String pinOffset) {

        boolean resultado;

        try {
            Integer.parseInt(pinOffset);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }

        return resultado;
    }
    
    @Override
    public TransactionsManagement getTransactionByNumberAndSequence(String transactionNumber, String transactionSequence, EntityManager entityManager) {
        String sql = "SELECT t FROM TransactionsManagement t WHERE t.transactionNumberIssuer = ?1 AND t.transactionSequence = ?2";
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("1", transactionNumber);
        query.setParameter("2", transactionSequence);
        try {
            TransactionsManagement result = (TransactionsManagement) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @Override
    public TransactionsManagement getTransactionsManagementByTransactionReference(String transactionNumber, Integer transactionTypeId, EntityManager entityManager) {
        String sql = "SELECT t FROM TransactionsManagement t WHERE t.transactionReference = ?1 AND t.transactionTypeId = ?2";
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("1", transactionNumber);
        query.setParameter("2", transactionTypeId);
        try {
            TransactionsManagement result = (TransactionsManagement) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @Override
    public TransactionPoint getTransactionPointByTransactionReference(String transactionNumber, EntityManager entityManager) {
        String sql = "SELECT t FROM TransactionPoint t WHERE t.transactionReference = ?1";
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("1", transactionNumber);
        try {
            TransactionPoint result = (TransactionPoint) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public KeyProperties getKeyPropertiesByProductIdByChanelId(Long productId, Integer channelId,EntityManager entityManager) {
        try {
            Query query = entityManager.createQuery("SELECT k FROM KeyProperties k WHERE k.productId.id = " + productId + " AND k.channelId.id = " + channelId + "");
            query.setMaxResults(1);
            KeyProperties result = (KeyProperties) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CardKeyHistoryListResponse getCardKeyHistoryByCardId(Long cardId, Integer limit,EntityManager entityManager) {
        List<CardKeyHistory> cardKeyHistorys = null;
        try {
            cardKeyHistorys = entityManager.createNamedQuery("CardKeyHistory.findByCardId", CardKeyHistory.class).setParameter("cardId", cardId).setMaxResults(limit).getResultList();
        } catch (Exception e) {
            return new CardKeyHistoryListResponse(ResponseCode.INTERNAL_ERROR, "Error loading countries");
        }
        return new CardKeyHistoryListResponse(ResponseCode.SUCCESS, "", cardKeyHistorys);
    }
}
