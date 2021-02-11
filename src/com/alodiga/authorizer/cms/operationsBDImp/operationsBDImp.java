/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alodiga.authorizer.cms.operationsBDImp;
import com.alodiga.authorizer.cms.operationsBD.operationsBD;
import com.cms.commons.enumeraciones.DocumentTypeE;
import com.cms.commons.enumeraciones.StatusTransactionManagementE;
import com.cms.commons.enumeraciones.TransactionE;
import com.cms.commons.models.Sequences;
import com.cms.commons.models.TransactionsManagement;
import com.cms.commons.models.TransactionsManagementHistory;
import com.cms.commons.util.Constants;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import com.alodiga.authorizer.cms.bean.APIOperations;
import org.apache.log4j.Logger;

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
    public TransactionsManagement createTransactionsManagement(TransactionsManagement management, Integer acquirerId, String acquirerTerminalCode, Integer acquirerCountryId, String transactionNumberAcquirer, Date dateTransaction, String transactionSequence, 
                                  Integer transactionTypeId, Integer channelId, Timestamp dateTimeTransmissionTerminal, Timestamp localTimeTransmission, Date localDateTransaction, Integer localCurrencyTransactionId, Float localCurrencyTransactionAmount, 
                                  Integer settlementCurrencyTransactionId, Float settlementTransactionAmount, Float rateConvertionSettlement, Float acquirerCommisionAmount, Float acquirerSettlementCommisionAmount, Float transactionRateAmount, 
                                  Integer transactionCityId, Integer statusTransactionManagementId, String cardNumber, String cardHolder, String CVV, String expirationCardDate, Integer pinLenght, String transferDestinationCardNumber, Integer issuerId, String mccCodeTrade, 
                                  String tradeName, String systemTraceAuditNumber, Integer numberMovementsCheckBalance, String responseCode, Integer messageMiddlewareId, Integer DocumentTypeId, EntityManager entityManager) {
    
       TransactionsManagement transactionsManagement = new TransactionsManagement();
       String transactionNumberIssuer = generateNumberSequence(getSequencesByDocumentTypeByOriginApplication(DocumentTypeId, Constants.ORIGIN_APPLICATION_CMS_ID, entityManager),entityManager);
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
                                  Integer numberMovementsCheckBalance, String responseCode, Integer messageMiddlewareId, String transactionNumberIssuer, EntityManager entityManager) {
        
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
    
}
