/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alodiga.authorizer.cms.operationsBDImp;
import com.alodiga.authorizer.cms.operationsBD.operationsBD;
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
import com.cms.commons.models.RateByCard;
import com.cms.commons.models.RateByProduct;
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
                                  Integer transactionTypeId, Integer channelId, Timestamp dateTimeTransmissionTerminal, Timestamp localTimeTransmission, Date localDateTransaction, Integer localCurrencyTransactionId, Float localCurrencyTransactionAmount, 
                                  Integer settlementCurrencyTransactionId, Float settlementTransactionAmount, Float rateConvertionSettlement, Float acquirerCommisionAmount, Float acquirerSettlementCommisionAmount, Float transactionRateAmount, 
                                  Integer transactionCityId, Integer statusTransactionManagementId, String cardNumber, String cardHolder, String CVV, String expirationCardDate, Integer pinLenght, String transferDestinationCardNumber, Integer issuerId, 
                                  String mccCodeTrade, String tradeName, String systemTraceAuditNumber, Integer numberMovementsCheckBalance, String responseCode, Long messageMiddlewareId, Integer DocumentTypeId, EntityManager entityManager) {
    
       TransactionsManagement transactionsManagement = new TransactionsManagement();
       Sequences sequence = getSequencesByDocumentTypeByOriginApplication(DocumentTypeId, Constants.ORIGIN_APPLICATION_CMS_ID, entityManager);
       String transactionNumberIssuer = generateNumberSequence(sequence,entityManager);
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
       transactionsManagement.setCreateDate(new Date()); 
       
       return transactionsManagement;
    }

    @Override
    public TransactionsManagementHistory createTransactionsManagementHistory(TransactionsManagement transactionManagement, Integer acquirerId, String acquirerTerminalCode, Integer acquirerCountryId, String transactionNumberAcquirer, Date dateTransaction, 
                                  String transactionSequence, Integer transactionTypeId, Integer channelId, Timestamp dateTimeTransmissionTerminal, Timestamp localTimeTransmission, Date localDateTransaction, 
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
       transactionsManagementHistory.setCreateDate(new Date()); 
        
        return transactionsManagementHistory;
    }

    @Override
    public TransactionsManagement saveTransactionsManagement(TransactionsManagement transactionsManagement, EntityManager entityManager) throws Exception {
        try {
            if (transactionsManagement.getId()==null)
                entityManager.persist(transactionsManagement);
            else
                entityManager.merge(transactionsManagement);
        } catch (Exception e) {
            e.printStackTrace();
           throw new Exception();
        }
        return transactionsManagement;
    }

    @Override
    public TransactionsManagementHistory saveTransactionsManagementHistory(TransactionsManagementHistory transactionsManagementHistory, EntityManager entityManager) throws Exception {
        try {
            if (transactionsManagementHistory.getId()==null)
                entityManager.persist(transactionsManagementHistory);
            else
                entityManager.merge(transactionsManagementHistory);
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
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(t.id) FROM transactionsManagementHistory t WHERE t.cardNumber = ?1 AND t.channelId = ?2 AND t.transactionTypeId = ?3 AND t.responseCode = '00'");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("1", cardNumber);
        query.setParameter("2", channelId);
        query.setParameter("3", transactionTypeId);
        List result = (List) query.setHint("toplink.refresh", "true").getResultList();
        return result.get(0) != null ? (Long) result.get(0) : 0l;
    }

    @Override
    public Long getTotalTransactionsByCardByDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer channelId, Integer transactionTypeId, EntityManager entityManager) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(t.id) FROM transactionsManagementHistory t WHERE t.createDate between ?1 AND ?2 AND t.cardNumber = ?3 AND t.channelId = ?4 AND t.transactionTypeId = ?5 AND t.responseCode = '00'");
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
        String sql = "SELECT * FROM transactionsManagementHistory t WHERE t.dateTransaction between ?1 AND ?2 AND t.cardNumber = ?3 AND t.transactionTypeId = ?4 AND t.channelId = ?5 AND t.responseCode =?6";
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
        String sql = "SELECT SUM(t.settlementTransactionAmount) FROM transactionsManagementHistory t WHERE t.dateTransaction between ?1 AND ?2 AND t.cardNumber = ?3 AND t.transactionTypeId = ?4 AND t.channelId = ?5 AND t.responseCode =?6";
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
    
}
