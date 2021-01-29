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
import com.alodiga.authorizer.cms.utils.Constants;
import com.alodiga.authorizer.cms.utils.Encryptor;
import com.alodiga.authorizer.cms.utils.EnvioCorreo;
import com.alodiga.authorizer.cms.utils.Mail;
import com.alodiga.authorizer.cms.utils.SendCallRegister;
import com.alodiga.authorizer.cms.utils.SendMailTherad;
import com.cms.commons.enumeraciones.StatusCardE;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import com.ericsson.alodiga.ws.APIRegistroUnificadoProxy;
import com.ericsson.alodiga.ws.Usuario;
import com.ericsson.alodiga.ws.RespuestaUsuario;
import java.sql.Timestamp;
import com.alodiga.authorizer.cms.utils.Utils;
import com.cms.commons.models.AccountCard;
import com.cms.commons.models.RateByCard;
import com.cms.commons.models.RateByProduct;
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
    
    public TransactionFeesResponse calculateTransactionFees(String cardNumber, Integer channelId, Integer transactionTypeId, Float settlementTransactionAmount) {
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
                transactionFeesAmount = (transactionFeesAmount * percentRate)/100;
            }
        }
        
        //2. Transacciones mensuales excentas
        if (transactionFeesAmount == 0) {
            totalTransactionsPerMonthByCard = getTotalTransactionsByCardByDate(card.getCardNumber(),EjbUtils.getBeginningDateMonth(new Date()), EjbUtils.getEndingDateMonth(new Date()),channelId,transactionTypeId);
        }
        
        
        return new TransactionFeesResponse(ResponseCode.SUCCESS.getCode(), "SUCCESS"); 
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
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(t.id) FROM transactionsManagementHistory t WHERE t.cardNumber = ?1 AND t.channelId = ?2 AND t.transactionTypeId = ?3");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("1", cardNumber);
        query.setParameter("2", channelId);
        query.setParameter("3", transactionTypeId);
        List result = (List) query.setHint("toplink.refresh", "true").getResultList();
        return result.get(0) != null ? (Long) result.get(0) : 0l;
    }
    
    public Long getTotalTransactionsByCardByDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer channelId, Integer transactionTypeId) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(t.id) transactionsManagementHistory t WHERE t.cardNumber = ?1 AND t.createDate between ?2 AND ?3 AND t.channelId = ?4 AND t.transactionTypeId = ?5");
        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter("1", cardNumber);
        query.setParameter("2", begginingDateTime);
        query.setParameter("3", endingDateTime);
        query.setParameter("4", channelId);
        query.setParameter("5", transactionTypeId);
        List result = (List) query.setHint("toplink.refresh", "true").getResultList();
        return result.get(0) != null ? (Long) result.get(0) : 0l;
    }
    
    public CardResponse verifyActiveCard(String cardNumber) {
        Card cards = new Card();
        try {
            cards = getCardByCardNumber(cardNumber);
            if(cards == null){
              return new CardResponse(ResponseCode.CARD_NOT_EXISTS.getCode(), ResponseCode.CARD_NOT_EXISTS.getMessage());  
            } else {
                int statusCard = cards.getCardStatusId().getId();
                switch(statusCard){
                    case 1:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: "+StatusCardE.SOLICI.statusCardDescription()+"");
                    case 2:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: "+StatusCardE.PERSON.statusCardDescription()+"");
                    case 3:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: "+StatusCardE.PENPER.statusCardDescription()+"");
                    case 4:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: "+StatusCardE.INVOK.statusCardDescription()+"");
                    case 5:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: "+StatusCardE.ERRPER.statusCardDescription()+"");
                    case 6:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: "+StatusCardE.PENDENTR.statusCardDescription()+"");
                    case 7:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: "+StatusCardE.ENTREG.statusCardDescription()+"");
                    case 8:
                        return new CardResponse(ResponseCode.SUCCESS.getCode(), "The card has the status: "+StatusCardE.ACTIVA.statusCardDescription()+"");
                    case 9:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: "+StatusCardE.BLOQUE.statusCardDescription()+"");
                    default:
                        return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error loading status card"); 
                }
            } 
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error loading card");
        }
    }
}
