package com.alodiga.authorizer.cms.bean;

import com.alodiga.transferto.integration.connection.RequestManager;
import com.alodiga.transferto.integration.model.MSIDN_INFOResponse;
import com.alodiga.transferto.integration.model.ReserveResponse;
import com.alodiga.transferto.integration.model.TopUpResponse;
import com.cms.commons.models.Country;
import com.cms.commons.models.Product;
import com.cms.commons.models.Card;
import com.cms.commons.models.NaturalCustomer;
import com.cms.commons.models.BalanceHistoryCard;
import com.cms.commons.models.TransactionsManagementHistory;
import com.cms.commons.util.Constants;
import com.alodiga.authorizer.cms.response.generic.BankGeneric;
import com.alodiga.authorizer.cms.responses.CardResponse;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.Iterator;
import java.math.BigInteger;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;
import com.alodiga.authorizer.cms.responses.ResponseCode;
import com.alodiga.authorizer.cms.responses.Response;
import com.alodiga.authorizer.cms.responses.ProductResponse;
import com.alodiga.authorizer.cms.responses.UserHasProductResponse;
import com.alodiga.authorizer.cms.responses.CountryListResponse;
import com.alodiga.authorizer.cms.responses.ProductListResponse;
import com.alodiga.authorizer.cms.responses.TopUpInfoListResponse;
import com.alodiga.authorizer.cms.responses.TransactionFeesResponse;
import com.alodiga.authorizer.cms.topup.TopUpInfo;
import com.alodiga.authorizer.cms.utils.Constante;
import com.alodiga.authorizer.cms.utils.Encryptor;
import com.alodiga.authorizer.cms.utils.EnvioCorreo;
import com.alodiga.authorizer.cms.utils.Mail;
import com.alodiga.authorizer.cms.utils.SendCallRegister;
import com.alodiga.authorizer.cms.utils.SendMailTherad;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import com.ericsson.alodiga.ws.APIRegistroUnificadoProxy;
import com.ericsson.alodiga.ws.Usuario;
import com.ericsson.alodiga.ws.RespuestaUsuario;
import java.sql.Timestamp;
import com.alodiga.authorizer.cms.utils.Utils;
import com.cms.commons.enumeraciones.ChannelE;
import com.cms.commons.enumeraciones.DocumentTypeE;
import com.cms.commons.enumeraciones.StatusTransactionManagementE;
import com.cms.commons.enumeraciones.TransactionE;
import com.cms.commons.models.AccountCard;
import com.cms.commons.models.RateByCard;
import com.cms.commons.models.RateByProduct;
import com.cms.commons.models.Sequences;
import com.cms.commons.models.TransactionsManagement;
import com.cms.commons.util.EjbUtils;
import com.ericsson.alodiga.ws.Cuenta;
import java.util.HashMap;
import java.util.Map;

@Stateless(name = "FsProcessorWallet", mappedName = "ejb/FsProcessorWallet")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class APIOperations {

    @PersistenceContext(unitName = "cmsPu")
    private EntityManager entityManager;
    private static final Logger logger = Logger.getLogger(APIOperations.class);

    public CountryListResponse getCountryList() {
        List<Country> countries = null;
        try {
            countries = entityManager.createNamedQuery("Country.findAll", Country.class).getResultList();

        } catch (Exception e) {
            return new CountryListResponse(ResponseCode.INTERNAL_ERROR, "Error loading countries");
        }
        return new CountryListResponse(ResponseCode.SUCCESS, "", countries);
    }

    public Card getCardByCardNumber(String cardNumber) {
        try {
            Query query = entityManager.createQuery("SELECT c FROM Card c WHERE c.cardNumber = " + cardNumber + "");
            query.setMaxResults(1);
            Card result = (Card) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CardResponse getValidateCard(String cardNumber) {
        Card cards = new Card();
        try {
            cards = getCardByCardNumber(cardNumber);
            if(cards == null){
              return new CardResponse(ResponseCode.CARD_NOT_EXISTS.getCode(), ResponseCode.CARD_NOT_EXISTS.getMessage());  
            } 
            
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error loading card");
        }
        return new CardResponse(ResponseCode.CARD_EXISTS.getCode(), ResponseCode.CARD_EXISTS.getMessage());
    }
    
    public NaturalCustomer getCardCustomer(Long personId){
        try{
            Query query = entityManager.createQuery("SELECT n FROM NaturalCustomer n WHERE n.personId.id = '" + personId + "'");
            query.setMaxResults(1);
            NaturalCustomer result = (NaturalCustomer) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public CardResponse validateCardByCardHolder(String cardNumber, String  cardHolder) {
        Card cards = new Card();
        try {
            cards = getCardByCardNumber(cardNumber);
            if(cards != null){
               NaturalCustomer naturalCustomer = new NaturalCustomer();
               naturalCustomer = getCardCustomer(cards.getPersonCustomerId().getId());
               if(naturalCustomer != null){
                   StringBuilder customerName = new StringBuilder(naturalCustomer.getFirstNames());
                   customerName.append(" ");
                   customerName.append(naturalCustomer.getLastNames());
                   if(cardHolder.equals(customerName.toString())){ 
                       return new CardResponse(ResponseCode.THE_CARDHOLDER_IS_VERIFIED.getCode(), ResponseCode.THE_CARDHOLDER_IS_VERIFIED.getMessage());
                   } else {
                       return new CardResponse(ResponseCode.THE_CARDHOLDER_NOT_MATCH.getCode(),ResponseCode.THE_CARDHOLDER_NOT_MATCH.getMessage()); 
                   }
               } else {
                  return new CardResponse(ResponseCode.CARD_OWNER_NOT_FOUND.getCode(), ResponseCode.CARD_OWNER_NOT_FOUND.getMessage()); 
               }
            } else {
               return new CardResponse(ResponseCode.CARD_NOT_FOUND.getCode(),ResponseCode.CARD_NOT_FOUND.getMessage()); 
            } 
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error loading card");
        }
    }        
    
    public Float getCurrentBalanceCard(Long cardId){
        try{
            Query query = entityManager.createQuery("SELECT b FROM BalanceHistoryCard b WHERE b.cardUserId.id = '" + cardId + "'");
            query.setMaxResults(1);
            BalanceHistoryCard result = (BalanceHistoryCard) query.setHint("toplink.refresh", "true").getSingleResult();
            return result.getCurrentBalance();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CardResponse getValidateCVVAndDueDateCard(String cardNumber, String cvv, String cardDate) {
        Card cards = new Card();        
        CardResponse cardResponse = new CardResponse();
        try {
            cards = getCardByCardNumber(cardNumber);
            if (cards == null) {
                return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "The card does not exist in the CMS");
            }            
            if (!cards.getSecurityCodeCard().equals(cvv)) {
                return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "The CVV is Different");
            }
            Date cardExpiration = cards.getExpirationDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            if (!sdf.format(cardExpiration).equals(cardDate)) {
                return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Expiration Date is Different");
            }
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error loading card");
        }
        cardResponse.setCard(cards);
        return new CardResponse(ResponseCode.SUCCESS.getCode(), "The Card exists in the CMS");
    }

    public CardResponse getAccountNumberByCard(String cardNumber) {
        Card cards = new Card();
        AccountCard accountCard = new AccountCard();
        String accountNumber = "";
        try {
            cards = getCardByCardNumber(cardNumber);
            if (cards == null) {
                return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "The card does not exist in the CMS");
            }
            accountCard = (AccountCard) entityManager.createNamedQuery("AccountCard.findByCardId", AccountCard.class).setParameter("cardId", cards.getId()).getSingleResult();
            accountNumber = accountCard.getAccountNumber();
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "There is no Account Associated with the Card");
        }
        return new CardResponse(ResponseCode.SUCCESS.getCode(), "SUCCESS", accountNumber);
    }
    
    public TransactionFeesResponse calculateTransactionFees(String cardNumber, Integer channelId, Integer transactionTypeId, Float settlementTransactionAmount, String transactionNumberAcquirer) {
        Card card = null;
        RateByCard rateByCard = null;
        RateByProduct rateByProduct = null;
        Long totalTransactionsByCard = 0L;
        Long totalTransactionsPerMonthByCard = 0L;
        int transactionsInitialExempt = 0;
        int transactionExemptPerMonth = 0;
        Float transactionFeesAmount = 0.00F;
        Float fixedRate = 0.00F;
        Float percentRate = 0.00F; 
        String transactionNumberIssuer;
        TransactionsManagement transactionFeeCharge = null;
        TransactionsManagementHistory transactionHistoryFeeCharge = null;
        
        //Se obtiene la tarjeta asociada a la transacción
        card = getCardByCardNumber(cardNumber);
        
        //Se revisa si el tarjetahabiente tiene tarifas definidas
        rateByCard = getRateByCard(card.getId(),channelId,transactionTypeId);
        if (rateByCard == null) {
            //Se revisa si el producto tiene tarifas definidas
            rateByProduct = getRateByProduct(card.getProductId().getId(),channelId,transactionTypeId);
            if (rateByProduct == null) {
                return new TransactionFeesResponse(ResponseCode.RATE_BY_PRODUCT_NOT_FOUND.getCode(),ResponseCode.RATE_BY_PRODUCT_NOT_FOUND.getMessage());
            } else {
                transactionsInitialExempt = rateByProduct.getTotalInitialTransactionsExempt();
                transactionExemptPerMonth = rateByProduct.getTotalTransactionsExemptPerMonth();
                if (rateByProduct.getFixedRate() != null) {
                    fixedRate = rateByProduct.getFixedRate();
                } else {
                    percentRate = rateByProduct.getPercentageRate();
                }
            }
        } else {
            transactionsInitialExempt = rateByCard.getTotalInitialTransactionsExempt();
            transactionExemptPerMonth = rateByCard.getTotalTransactionsExemptPerMonth();
            if (rateByCard.getFixedRate() != null) {
                fixedRate = rateByCard.getFixedRate();
            } else {
                percentRate = rateByCard.getPercentageRate();
            }
        }
        
        //Validar si aplica el cobro de la tarifa
        //1. Transacciones iniciales excentas
        totalTransactionsByCard = getTotalTransactionsByCard(card.getCardNumber(),channelId,transactionTypeId);
        if (totalTransactionsByCard > transactionsInitialExempt) {
            if (fixedRate != null) {
                transactionFeesAmount = fixedRate;
            } else {
                transactionFeesAmount = (settlementTransactionAmount * percentRate)/100;
            }
        }
        
        //2. Transacciones mensuales excentas
        if (transactionFeesAmount == 0) {
            totalTransactionsPerMonthByCard = getTotalTransactionsByCardByDate(card.getCardNumber(),EjbUtils.getBeginningDateMonth(new Date()), EjbUtils.getEndingDateMonth(new Date()),channelId,transactionTypeId);
            if (totalTransactionsPerMonthByCard > transactionExemptPerMonth) {
                if (fixedRate != null) {
                    transactionFeesAmount = fixedRate;
                } else {
                    transactionFeesAmount = (settlementTransactionAmount * percentRate)/100;
                }
            }
        }
        
        //Si aplica la tarifa a la transacción se registra la transacción para guardar la comisión de Alodiga en la BD
        if (transactionFeesAmount > 0) {
            //Se obtiene el número de la transacción
            transactionNumberIssuer = generateNumberSequence(getSequencesByDocumentTypeByOriginApplication(DocumentTypeE.TRANSACTION_FEE_CMS.getId(), Constants.ORIGIN_APPLICATION_CMS_ID));

            //Se guarda la comisión de Alodiga en la BD
            transactionFeeCharge = new TransactionsManagement();
            transactionFeeCharge.setTransactionNumberIssuer(transactionNumberIssuer);
            transactionFeeCharge.setDateTransaction(new Date());
            transactionFeeCharge.setChannelId(ChannelE.INT.getId());
            transactionFeeCharge.setTransactionTypeId(TransactionE.TARIFA_TRANSACCION_CMS.getId());
            transactionFeeCharge.setTransactionReference(transactionNumberAcquirer);
            transactionFeeCharge.setCardHolder(card.getCardHolder());
            transactionFeeCharge.setCardNumber(cardNumber);
            transactionFeeCharge.setCvv(card.getSecurityCodeCard());
            String pattern = "MMyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String expirationCardDate = simpleDateFormat.format(card.getExpirationDate());
            transactionFeeCharge.setExpirationCardDate(expirationCardDate);
            transactionFeeCharge.setSettlementTransactionAmount(transactionFeesAmount);
            transactionFeeCharge.setSettlementCurrencyTransactionId(card.getProductId().getDomesticCurrencyId().getId());
            transactionFeeCharge.setStatusTransactionManagementId(StatusTransactionManagementE.APPROVED.getId());
            transactionFeeCharge.setCreateDate(new Timestamp(new Date().getTime()));
            entityManager.persist(transactionFeeCharge);

            transactionHistoryFeeCharge = new TransactionsManagementHistory();
            transactionHistoryFeeCharge.setTransactionNumberIssuer(transactionNumberIssuer);
            transactionHistoryFeeCharge.setDateTransaction(new Date());
            transactionHistoryFeeCharge.setChannelId(ChannelE.INT.getId());
            transactionHistoryFeeCharge.setTransactionTypeId(TransactionE.TARIFA_TRANSACCION_CMS.getId());
            transactionHistoryFeeCharge.setTransactionReference(transactionNumberAcquirer);
            transactionHistoryFeeCharge.setCardHolder(card.getCardHolder());
            transactionHistoryFeeCharge.setCardNumber(cardNumber);
            transactionHistoryFeeCharge.setCvv(card.getSecurityCodeCard());
            transactionHistoryFeeCharge.setExpirationCardDate(expirationCardDate);
            transactionHistoryFeeCharge.setSettlementTransactionAmount(transactionFeesAmount);
            transactionHistoryFeeCharge.setSettlementCurrencyTransactionId(card.getProductId().getDomesticCurrencyId().getId());
            transactionHistoryFeeCharge.setStatusTransactionManagementId(StatusTransactionManagementE.APPROVED.getId());
            transactionHistoryFeeCharge.setCreateDate(new Timestamp(new Date().getTime()));
            entityManager.persist(transactionHistoryFeeCharge);
        } else {
            return new TransactionFeesResponse(ResponseCode.SUCCESS.getCode(),"The transaction received did not generate commission to be charged");
        }     
        return new TransactionFeesResponse(ResponseCode.SUCCESS.getCode(),"The transaction to record the Alodiga commission corresponding to the received transaction was successfully saved in the database.",transactionFeesAmount,transactionFeeCharge); 
    }
    
    private RateByCard getRateByCard(Long cardId, Integer channelId, Integer transactionTypeId) {
        try {
            Query query = entityManager.createQuery("SELECT r FROM RateByCard r WHERE r.cardId.id = " + cardId + " AND r.channelId.id = " + channelId + " AND r.transactionId.id = " + transactionTypeId + "");
            query.setMaxResults(1);
            RateByCard result = (RateByCard) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    private RateByProduct getRateByProduct(Long productId, Integer channelId, Integer transactionTypeId) {
        try {
            Query query = entityManager.createQuery("SELECT r FROM RateByProduct r WHERE r.productId.id = " + productId + " AND r.channelId.id = " + channelId + " AND r.transactionId.id = " + transactionTypeId + "");
            query.setMaxResults(1);
            RateByProduct result = (RateByProduct) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    private Long getTotalTransactionsByCard(String cardNumber, Integer channelId, Integer transactionTypeId) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(t.id) FROM transactionsManagementHistory t WHERE t.cardNumber = ?1 AND t.channelId = ?2 AND t.transactionTypeId = ?3 AND t.responseCode = '00'");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("1", cardNumber);
        query.setParameter("2", channelId);
        query.setParameter("3", transactionTypeId);
        List result = (List) query.setHint("toplink.refresh", "true").getResultList();
        return result.get(0) != null ? (Long) result.get(0) : 0l;
    }
    
    public Long getTotalTransactionsByCardByDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer channelId, Integer transactionTypeId) {
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
    
    public Sequences getSequencesByDocumentTypeByOriginApplication(int documentTypeId, int originApplicationId) {
        try {
            Sequences sequences = (Sequences) entityManager.createNamedQuery("Sequences.findBydocumentTypeByOriginApplication", Sequences.class).setParameter("documentTypeId", documentTypeId).setParameter("originApplicationId", originApplicationId).getSingleResult();
            return sequences;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    private String generateNumberSequence(Sequences s) {
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
}
