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
import com.alodiga.authorizer.cms.topup.TopUpInfo;
import com.alodiga.authorizer.cms.utils.Constante;
import com.alodiga.authorizer.cms.utils.Constants;
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
import com.cms.commons.models.AccountCard;
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
              return new CardResponse(ResponseCode.CARD_NOT_EXISTS, "The card does not exist in the Card Manager System database");  
            } 
            
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR, "Error loading card");
        }
        return new CardResponse(ResponseCode.CARD_EXISTS, "The Card exists in the Card Manager System database");
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
                       return new CardResponse(ResponseCode.THE_CARDHOLDER_IS_VERIFIED, "Cardholder data has been successfully verified");
                   } else {
                       return new CardResponse(ResponseCode.THE_CARDHOLDER_NOT_MATCH, "Cardholder details do not match"); 
                   }
               } else {
                  return new CardResponse(ResponseCode.CARD_OWNER_NOT_FOUND, "Error finding card owner"); 
               }
            } else {
               return new CardResponse(ResponseCode.CARD_NOT_FOUND, "Error finding the card to verify cardholder data"); 
            } 
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR, "Error loading card");
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
                return new CardResponse(ResponseCode.INTERNAL_ERROR, "The card does not exist in the CMS");
            }            
            if (!cards.getSecurityCodeCard().equals(cvv)) {
                return new CardResponse(ResponseCode.INTERNAL_ERROR, "The CVV is Different");
            }
            Date cardExpiration = cards.getExpirationDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            if (!sdf.format(cardExpiration).equals(cardDate)) {
                return new CardResponse(ResponseCode.INTERNAL_ERROR, "Expiration Date is Different");
            }
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR, "Error loading card");
        }
        cardResponse.setCard(cards);
        return new CardResponse(ResponseCode.SUCCESS, "The Card exists in the CMS");
    }

    public CardResponse getAccountNumberByCard(String cardNumber) {
        Card cards = new Card();
        AccountCard accountCard = new AccountCard();
        String accountNumber = "";
        try {
            cards = getCardByCardNumber(cardNumber);
            if (cards == null) {
                return new CardResponse(ResponseCode.INTERNAL_ERROR, "The card does not exist in the CMS");
            }
            accountCard = (AccountCard) entityManager.createNamedQuery("AccountCard.findByCardId", AccountCard.class).setParameter("cardId", cards.getId()).getSingleResult();
            accountNumber = accountCard.getAccountNumber();
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR, "There is no Account Associated with the Card");
        }
        return new CardResponse(ResponseCode.SUCCESS, "SUCCESS", accountNumber);
    }

}
