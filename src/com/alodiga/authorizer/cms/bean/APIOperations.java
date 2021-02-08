package com.alodiga.authorizer.cms.bean;

import com.cms.commons.models.Country;
import com.cms.commons.models.Card;
import com.cms.commons.models.NaturalCustomer;
import com.cms.commons.models.BalanceHistoryCard;
import com.cms.commons.models.TransactionsManagementHistory;
import com.cms.commons.util.Constants;
import com.alodiga.authorizer.cms.responses.CardResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import com.alodiga.authorizer.cms.responses.ResponseCode;
import com.alodiga.authorizer.cms.responses.CountryListResponse;
import com.alodiga.authorizer.cms.responses.ValidateLimitsResponse;
import com.alodiga.authorizer.cms.responses.TransactionFeesResponse;
import com.alodiga.authorizer.cms.responses.TransactionResponse;
import java.sql.Timestamp;
import com.cms.commons.enumeraciones.ChannelE;
import com.cms.commons.enumeraciones.DocumentTypeE;
import com.cms.commons.enumeraciones.StatusTransactionManagementE;
import com.cms.commons.enumeraciones.TransactionE;
import com.cms.commons.enumeraciones.StatusCardE;
import com.cms.commons.models.AccountCard;
import com.cms.commons.models.ProductHasChannelHasTransaction;
import com.cms.commons.models.RateByCard;
import com.cms.commons.models.RateByProduct;
import com.cms.commons.models.Sequences;
import com.cms.commons.models.TransactionsManagement;
import com.cms.commons.util.EjbUtils;
import java.util.Calendar;

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

    public NaturalCustomer getCardCustomer(Long personId) {
        try {
            Query query = entityManager.createQuery("SELECT n FROM NaturalCustomer n WHERE n.personId.id = '" + personId + "'");
            query.setMaxResults(1);
            NaturalCustomer result = (NaturalCustomer) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CardResponse validateCardByCardHolder(String cardNumber, String cardHolder) {
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


    public CardResponse getValidateCVVAndDueDateCard(String cardNumber, String cvv, String cardDueDate) {
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
            SimpleDateFormat sdf = new SimpleDateFormat("MMyy");
            if (!sdf.format(cardExpiration).equals(cardDueDate)) {
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

    public CardResponse getValidateCardByLUNH(String cardNumber) {
        try {
            if (checkLuhn(cardNumber)) {
                return new CardResponse(ResponseCode.SUCCESS.getCode(), "The verification digit on the card is valid");
            } else {
                return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "The verification digit on the card is invalid");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "INTERNAL_ERROR");
        }
    }

    static boolean checkLuhn(String cardNumber) {
        int nDigits = cardNumber.length();
        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--) {
            int d = cardNumber.charAt(i) - '0';
            if (isSecond == true) {
                d = d * 2;
            }

            // We add two digits to handle cases that make two digits after doubling
            nSum += d / 10;
            nSum += d % 10;
            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
    }

    public CardResponse calculatesCheckDigitLunh(String cardNumber) {

        try {
            if (cardNumber == null) {
                return null;
            }
            String digit;
            /* se convierte el número en un arreglo de digitos */
            int[] digits = new int[cardNumber.length()];
            for (int i = 0; i < cardNumber.length(); i++) {
                digits[i] = Character.getNumericValue(cardNumber.charAt(i));
            }

            /* se duplica cada dígito desde la derecha saltando de dos en dos*/
            for (int i = digits.length - 1; i >= 0; i -= 2) {
                digits[i] += digits[i];

                /* si la suma de los digitos es más de 10, se resta 9 */
                if (digits[i] >= 10) {
                    digits[i] = digits[i] - 9;
                }
            }
            int sum = 0;
            for (int i = 0; i < digits.length; i++) {
                sum += digits[i];
            }

            /* se multiplica por 9 */
            sum = sum * 9;

            /* se convierte a cadena para obtener facilmente el último dígito */
            digit = sum + "";
            Long checkdigit = Long.valueOf(digit.substring(digit.length() - 1));
            return new CardResponse(ResponseCode.SUCCESS, "SUCCESS", checkdigit);
        } catch (Exception e) {
            e.printStackTrace();
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "INTERNAL_ERROR");
        }
    }     
    
    public TransactionFeesResponse calculateCommisionCMS(String cardNumber, Integer channelId, Integer transactionTypeId, Float settlementTransactionAmount, String transactionNumberAcquirer) {
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
        TransactionsManagement transactionCommisionCMS = null;
        TransactionsManagementHistory transactionHistoryCommisionCMS = null;
        
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
            transactionNumberIssuer = generateNumberSequence(getSequencesByDocumentTypeByOriginApplication(DocumentTypeE.COMMISION_CMS.getId(), Constants.ORIGIN_APPLICATION_CMS_ID));

            //Se guarda la comisión de Alodiga en la BD
            transactionCommisionCMS = new TransactionsManagement();
            transactionCommisionCMS.setTransactionNumberIssuer(transactionNumberIssuer);
            transactionCommisionCMS.setDateTransaction(new Date());
            transactionCommisionCMS.setChannelId(ChannelE.INT.getId());
            transactionCommisionCMS.setTransactionTypeId(TransactionE.COMISION_CMS.getId());
            transactionCommisionCMS.setTransactionReference(transactionNumberAcquirer);
            transactionCommisionCMS.setCardHolder(card.getCardHolder());
            transactionCommisionCMS.setCardNumber(cardNumber);
            transactionCommisionCMS.setCvv(card.getSecurityCodeCard());
            String pattern = "MMyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String expirationCardDate = simpleDateFormat.format(card.getExpirationDate());
            transactionCommisionCMS.setExpirationCardDate(expirationCardDate);
            transactionCommisionCMS.setSettlementTransactionAmount(transactionFeesAmount);
            transactionCommisionCMS.setSettlementCurrencyTransactionId(card.getProductId().getDomesticCurrencyId().getId());
            transactionCommisionCMS.setStatusTransactionManagementId(StatusTransactionManagementE.APPROVED.getId());
            transactionCommisionCMS.setCreateDate(new Timestamp(new Date().getTime()));
            entityManager.persist(transactionCommisionCMS);

            transactionHistoryCommisionCMS = new TransactionsManagementHistory();
            transactionHistoryCommisionCMS.setTransactionNumberIssuer(transactionNumberIssuer);
            transactionHistoryCommisionCMS.setDateTransaction(new Date());
            transactionHistoryCommisionCMS.setChannelId(ChannelE.INT.getId());
            transactionHistoryCommisionCMS.setTransactionTypeId(TransactionE.COMISION_CMS.getId());
            transactionHistoryCommisionCMS.setTransactionReference(transactionNumberAcquirer);
            transactionHistoryCommisionCMS.setCardHolder(card.getCardHolder());
            transactionHistoryCommisionCMS.setCardNumber(cardNumber);
            transactionHistoryCommisionCMS.setCvv(card.getSecurityCodeCard());
            transactionHistoryCommisionCMS.setExpirationCardDate(expirationCardDate);
            transactionHistoryCommisionCMS.setSettlementTransactionAmount(transactionFeesAmount);
            transactionHistoryCommisionCMS.setSettlementCurrencyTransactionId(card.getProductId().getDomesticCurrencyId().getId());
            transactionHistoryCommisionCMS.setStatusTransactionManagementId(StatusTransactionManagementE.APPROVED.getId());
            transactionHistoryCommisionCMS.setCreateDate(new Timestamp(new Date().getTime()));
            entityManager.persist(transactionHistoryCommisionCMS);
        } else {
            return new TransactionFeesResponse(ResponseCode.SUCCESS.getCode(),"The transaction received did not generate commission to be charged");
        }     
        return new TransactionFeesResponse(ResponseCode.SUCCESS.getCode(),"The transaction to record the Alodiga commission corresponding to the received transaction was successfully saved in the database.",transactionFeesAmount,transactionCommisionCMS); 
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


     public ValidateLimitsResponse getValidateLimits(String cardNumber, Integer transactionTypeId, Integer channelId, String countryCode, Float amountTransaction){
        Long totalTransactionsByCardDaily = 0L;
        Double totalAmountByCardDaily = 0.00D;
        Long totalTransactionsByCardMonthly = 0L;
        Double totalAmountByUserMonthly = 0.00D;
        boolean isTransactionLocal = false;
        
        if (cardNumber == null || countryCode ==null )
            return new ValidateLimitsResponse(ResponseCode.INVALID_DATA, "The invalid data");
        
        Card card = getCardByCardNumber(cardNumber);
        if (card==null)
             return new ValidateLimitsResponse(ResponseCode.CARD_NOT_EXISTS, ResponseCode.CARD_NOT_FOUND.getMessage());
        
        Country country = getCountry(countryCode);
        if (country==null)
            return new ValidateLimitsResponse(ResponseCode.COUNTRY_NOT_FOUND, ResponseCode.COUNTRY_NOT_FOUND.getMessage());
        
        if (country.getId().equals(card.getProductId().getCountryId().getId()))
            isTransactionLocal = true;
        
        ProductHasChannelHasTransaction productHasChannelHasTransaction = getSettingLimits(transactionTypeId,channelId, card.getProductId().getId());
       
        if (productHasChannelHasTransaction != null) {
            
           
            if (amountTransaction < Double.parseDouble(isTransactionLocal ? productHasChannelHasTransaction.getAmountMinimumTransactionDomestic().toString():productHasChannelHasTransaction.getAmountMinimumTransactionInternational().toString())) {
                return new ValidateLimitsResponse(ResponseCode.MIN_TRANSACTION_AMOUNT, ResponseCode.MIN_TRANSACTION_AMOUNT.getMessage());
            }
            if (amountTransaction > Double.parseDouble(isTransactionLocal ? productHasChannelHasTransaction.getAmountMaximumTransactionDomestic().toString():productHasChannelHasTransaction.getAmountMaximumTransactionInternational().toString())) {
                return new ValidateLimitsResponse(ResponseCode.MIN_TRANSACTION_AMOUNT, ResponseCode.MIN_TRANSACTION_AMOUNT.getMessage());
            }
            totalTransactionsByCardDaily = getTransactionsByCardByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDate(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(),isTransactionLocal, country.getId());
            if ((totalTransactionsByCardDaily + 1) > productHasChannelHasTransaction.getMaximumNumberTransactionsDaily()) {
                return new ValidateLimitsResponse(ResponseCode.TRANSACTION_QUANTITY_LIMIT_DIALY, ResponseCode.TRANSACTION_QUANTITY_LIMIT_DIALY.getMessage());
            }
            totalAmountByCardDaily = getAmountMaxByUserByUserByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDate(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(),isTransactionLocal, country.getId());
            if ((totalAmountByCardDaily + amountTransaction) > Double.parseDouble(isTransactionLocal ? productHasChannelHasTransaction.getDailyAmountLimitDomestic().toString():productHasChannelHasTransaction.getDailyAmountLimitInternational().toString())) {
                return new ValidateLimitsResponse(ResponseCode.TRANSACTION_AMOUNT_LIMIT_DIALY, ResponseCode.TRANSACTION_AMOUNT_LIMIT_DIALY.getMessage());
            }
            
            totalTransactionsByCardMonthly = getTransactionsByCardByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDateMonth(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(),isTransactionLocal, country.getId());
            if ((totalTransactionsByCardMonthly + 1) > productHasChannelHasTransaction.getMaximumNumberTransactionsMonthly()) {
                return new ValidateLimitsResponse(ResponseCode.TRANSACTION_QUANTITY_LIMIT_MONTHLY, ResponseCode.TRANSACTION_QUANTITY_LIMIT_MONTHLY.getMessage());
            }
            
            totalAmountByUserMonthly = getAmountMaxByUserByUserByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDateMonth(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(),isTransactionLocal, country.getId());
            if ((totalAmountByUserMonthly + amountTransaction) > Double.parseDouble(isTransactionLocal ? productHasChannelHasTransaction.getMonthlyAmountLimitDomestic().toString():productHasChannelHasTransaction.getMonthlyAmountLimitInternational().toString())) {
                return new ValidateLimitsResponse(ResponseCode.TRANSACTION_AMOUNT_LIMIT_MONTHLY, ResponseCode.TRANSACTION_AMOUNT_LIMIT_MONTHLY.getMessage());
            }

        }
        return new ValidateLimitsResponse(ResponseCode.SUCCESS, "SUCCESS");
    }
    
    private ProductHasChannelHasTransaction getSettingLimits(Integer transactionId, Integer channelId, Long productId) {
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

    public Long getTransactionsByCardByTransactionByProductCurrentDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer transactionTypeId, Integer channelId, String code, boolean isTransactionLocal, Integer countryId) {
        String sql = "SELECT * FROM transactionsManagementHistory t WHERE t.dateTransaction between ?1 AND ?2 AND t.cardNumber = ?3 AND t.transactionTypeId = ?4 AND t.channelId = ?5 AND t.responseCode =?6";
        if (isTransactionLocal)
            sql += (" AND acquirerCountryId = ?7");
        else
            sql += (" AND acquirerCountryId <> ?7");
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


    public Double getAmountMaxByUserByUserByTransactionByProductCurrentDate(String cardNumber, Date begginingDateTime, Date endingDateTime, Integer transactionTypeId, Integer channelId, String code, boolean isTransactionLocal, Integer countryId) {
        String sql = "SELECT SUM(t.settlementTransactionAmount) FROM transactionsManagementHistory t WHERE t.dateTransaction between ?1 AND ?2 AND t.cardNumber = ?3 AND t.transactionTypeId = ?4 AND t.channelId = ?5 AND t.responseCode =?6";
         if (isTransactionLocal)
            sql += (" AND acquirerCountryId = ?7");
        else
            sql += (" AND acquirerCountryId <> ?7");
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
        return result.get(0)!=null ? (double) result.get(0) : 0f;
    }
    
     private Country getCountry(String countryCode) {
        try {
            Query query = entityManager.createQuery("SELECT c FROM Country c WHERE c.code = '" + countryCode + "'");
            query.setMaxResults(1);
            Country result = (Country) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }
     
    public CardResponse validateCard(String cardNumber, String ARQC, String cardHolder, String CVV, String cardDueDate) {
        try {
            CardResponse validateCard = getValidateCard(cardNumber);
            //Se valida que la tarjeta exista en la BD del CMS
            if (validateCard.getCodigoRespuesta().equals(ResponseCode.CARD_EXISTS.getCode())) {
                CardResponse verifyActiveCard = verifyActiveCard(cardNumber);
                //Se valida que la tarjeta este en estatus ACTIVA
                if (verifyActiveCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                    CardResponse validateCardByLUNH = getValidateCardByLUNH(cardNumber);
                    //Se valida el dígito verificador de la tarjeta a través de algoritmo de LUHN
                    if (getValidateCardByLUNH(cardNumber).getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                        CardResponse validateCVVAndDueDate = getValidateCVVAndDueDateCard(cardNumber, CVV, cardDueDate);
                        //Se valida el CVV y la fecha de vencimiento de la tarjeta
                        if (validateCVVAndDueDate.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                            //Se valida el nombre del cliente en la tarjeta (CardHolder)
                            CardResponse validateCardHolder = validateCardByCardHolder(cardNumber, cardHolder);
                            if (validateCardHolder.getCodigoRespuesta().equals(ResponseCode.THE_CARDHOLDER_IS_VERIFIED.getCode())) {
                                return new CardResponse(ResponseCode.SUCCESS.getCode(), "The Card is Valid");
                            } else {
                                return validateCardHolder;
                            }
                        } else {
                            return validateCVVAndDueDate;
                        }
                    } else {
                        return validateCardByLUNH;
                    }
                } else {
                    return verifyActiveCard;
                }
            } else {
                return validateCard;
            }
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an unexpected error occurred");
        }
        
    }
    
    public TransactionResponse activateCard(String cardNumber, String cardHolder, String CVV, String cardDueDate, String documentIdentificationNumber, 
                                            String numberPhoneCustomer, Date dateBirth, String emailCustomer, Long messageMiddlewareId,
                                            Integer transactionTypeId, Integer channelId, Date transactionDate, Timestamp localTimeTransaction,
                                            String acquirerTerminalCodeId, Integer acquirerCountryId) {
        
        String ARQC = null;
        
        try {
        //Se valida la tarjeta
        CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate);
        if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
            //Se registra la transacción en el CMS
            
            
            
            
        } else {
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), validateCard.getMensajeRespuesta());
        }
         return new TransactionResponse();    
        
        
        } catch (Exception e) {
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an unexpected error occurred");
        }
    }

}
