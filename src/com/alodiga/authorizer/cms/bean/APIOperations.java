package com.alodiga.authorizer.cms.bean;

import com.alodiga.authorizer.cms.responses.CalculateBonusCardResponse;
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
import com.alodiga.authorizer.cms.responses.OperationCardBalanceInquiryResponse;
import com.alodiga.authorizer.cms.responses.ValidateLimitsResponse;
import com.alodiga.authorizer.cms.responses.TransactionResponse;
import java.sql.Timestamp;
import com.cms.commons.enumeraciones.ChannelE;
import com.cms.commons.enumeraciones.DocumentTypeE;
import com.cms.commons.enumeraciones.ProgramLoyaltyTypeE;
import com.cms.commons.enumeraciones.StatusTransactionManagementE;
import com.cms.commons.enumeraciones.TransactionE;
import com.cms.commons.enumeraciones.StatusCardE;
import com.cms.commons.enumeraciones.SubTransactionE;
import com.cms.commons.enumeraciones.StatusUpdateReasonE;
import com.cms.commons.models.AccountCard;
import com.cms.commons.models.BonusCard;
import com.cms.commons.models.Channel;
import com.cms.commons.models.DaysWeek;
import com.cms.commons.models.CardStatus;
import com.cms.commons.models.DaysWeekHasProgramLoyalty;
import com.cms.commons.models.Product;
import com.cms.commons.models.ProductHasChannelHasTransaction;
import com.cms.commons.models.ProgramLoyalty;
import com.cms.commons.models.ProgramLoyaltyTransaction;
import com.cms.commons.models.RateByCard;
import com.cms.commons.models.RateByProduct;
import com.cms.commons.models.Sequences;
import com.cms.commons.models.TransactionPoint;
import com.cms.commons.models.StatusUpdateReason;
import com.cms.commons.models.TransactionsManagement;
import com.cms.commons.models.User;
import com.cms.commons.models.Transaction;
import com.cms.commons.util.EjbUtils;
import java.math.BigInteger;
import java.util.Calendar;
import com.alodiga.authorizer.cms.operationsBDImp.operationsBDImp;
import java.util.ArrayList;


@Stateless(name = "FsProcessorCMSAuthorizer", mappedName = "ejb/FsProcessorCMSAuthorizer")
@TransactionManagement(TransactionManagementType.CONTAINER)
public class APIOperations {

    @PersistenceContext(unitName = "cmsPu")
    private EntityManager entityManager;
    private static final Logger logger = Logger.getLogger(APIOperations.class);
    operationsBDImp operationsBD = new operationsBDImp();
    
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
            if (cards == null) {
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
            if (cards != null) {
                NaturalCustomer naturalCustomer = new NaturalCustomer();
                naturalCustomer = getCardCustomer(cards.getPersonCustomerId().getId());
                if (naturalCustomer != null) {
                    StringBuilder customerName = new StringBuilder(naturalCustomer.getFirstNames());
                    customerName.append(" ");
                    customerName.append(naturalCustomer.getLastNames());
                    if (cardHolder.equals(customerName.toString())) {
                        return new CardResponse(ResponseCode.THE_CARDHOLDER_IS_VERIFIED.getCode(), ResponseCode.THE_CARDHOLDER_IS_VERIFIED.getMessage());
                    } else {
                        return new CardResponse(ResponseCode.THE_CARDHOLDER_NOT_MATCH.getCode(), ResponseCode.THE_CARDHOLDER_NOT_MATCH.getMessage());
                    }
                } else {
                    return new CardResponse(ResponseCode.CARD_OWNER_NOT_FOUND.getCode(), ResponseCode.CARD_OWNER_NOT_FOUND.getMessage());
                }
            } else {
                return new CardResponse(ResponseCode.CARD_NOT_FOUND.getCode(), ResponseCode.CARD_NOT_FOUND.getMessage());
            }
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error loading card");
        }
    }

    public Float getCurrentBalanceCard(Long cardId) {
        try {
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
                if (!cards.getSecurityCodeCard().equals(cvv)) {
                    return new CardResponse(ResponseCode.CVV_DIFFERENT.getCode(), ResponseCode.CVV_DIFFERENT.getMessage());
                }
                Date cardExpiration = cards.getExpirationDate();
                SimpleDateFormat sdf = new SimpleDateFormat("MMyy");
                if (!sdf.format(cardExpiration).equals(cardDueDate)) {
                    return new CardResponse(ResponseCode.DATE_DIFFERENT.getCode(), ResponseCode.DATE_DIFFERENT.getMessage());
                }

            } else {
                return new CardResponse(ResponseCode.CARD_NOT_EXISTS.getCode(), ResponseCode.CARD_NOT_EXISTS.getMessage());
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
                accountCard = (AccountCard) entityManager.createNamedQuery("AccountCard.findByCardId", AccountCard.class).setParameter("cardId", cards.getId()).getSingleResult();

                if (accountCard != null) {
                    accountNumber = accountCard.getAccountNumber();
                } else {
                    return new CardResponse(ResponseCode.ACCOUNT_NOT_ASSOCIATED.getCode(), ResponseCode.ACCOUNT_NOT_ASSOCIATED.getMessage());
                }
            } else {
                return new CardResponse(ResponseCode.CARD_NOT_EXISTS.getCode(), ResponseCode.CARD_NOT_EXISTS.getMessage());
            }

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
                System.out.println("This is not a valid card");
                return new CardResponse(ResponseCode.INVALID_CARD.getCode(), "This is not a valid card");
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
    
    public TransactionResponse calculateCommisionCMS(String cardNumber, Integer channelId, Integer transactionTypeId, Float settlementTransactionAmount, String transactionNumberAcquirer) {
        Card card = null;
        RateByCard rateByCard = null;
        RateByProduct rateByProduct = null;
        Long totalTransactionsByCard = 0L;
        Long totalTransactionsPerMonthByCard = 0L;
        int transactionsInitialExempt = 0;
        int transactionExemptPerMonth = 0;
        Float transactionCommisionAmount = 0.00F;
        Float fixedRate = 0.00F;
        Float percentRate = 0.00F;
        String transactionNumberIssuer;
        TransactionsManagement transactionCommisionCMS = null;
        TransactionsManagementHistory transactionHistoryCommisionCMS = null;

        //Se obtiene la tarjeta asociada a la transacción
        card = getCardByCardNumber(cardNumber);

        //Se revisa si el tarjetahabiente tiene tarifas definidas
        rateByCard = operationsBD.getRateByCard(card.getId(), channelId, transactionTypeId, entityManager);
        if (rateByCard == null) {
            //Se revisa si el producto tiene tarifas definidas
            rateByProduct = operationsBD.getRateByProduct(card.getProductId().getId(), channelId, transactionTypeId, entityManager);
            if (rateByProduct == null) {
                return new TransactionResponse(ResponseCode.RATE_BY_PRODUCT_NOT_FOUND.getCode(), ResponseCode.RATE_BY_PRODUCT_NOT_FOUND.getMessage());
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
        totalTransactionsByCard = operationsBD.getTotalTransactionsByCard(card.getCardNumber(), channelId, transactionTypeId, entityManager);
        if (totalTransactionsByCard > transactionsInitialExempt) {
            if (fixedRate != null) {
                transactionCommisionAmount = fixedRate;
            } else {
                transactionCommisionAmount = (settlementTransactionAmount * percentRate) / 100;
            }
        }

        //2. Transacciones mensuales excentas
        if (transactionCommisionAmount == 0) {
            totalTransactionsPerMonthByCard = operationsBD.getTotalTransactionsByCardByDate(card.getCardNumber(), EjbUtils.getBeginningDateMonth(new Date()), EjbUtils.getEndingDateMonth(new Date()), channelId, transactionTypeId, entityManager);
            if (totalTransactionsPerMonthByCard > transactionExemptPerMonth) {
                if (fixedRate != null) {
                    transactionCommisionAmount = fixedRate;
                } else {
                    transactionCommisionAmount = (settlementTransactionAmount * percentRate) / 100;
                }
            }
        }

        //Si aplica la tarifa a la transacción, registrando la comisión del emisor en la BD
        if (transactionCommisionAmount > 0) {
            //Se obtiene la transacción que generó la comisión
            TransactionsManagement transactionsManagement = getTransactionsManagementByNumber(transactionNumberAcquirer);
            
            //Se crea el objeto TransactionManagement y se guarda en BD
            String pattern = "MMyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String expirationCardDate = simpleDateFormat.format(card.getExpirationDate());
            transactionCommisionCMS = operationsBD.createTransactionsManagement(transactionsManagement, null, null, null, null, null, 
                                  TransactionE.COMISION_CMS.getId(), ChannelE.INT.getId(), null, null, null, null, null, 
                                  card.getProductId().getDomesticCurrencyId().getId(), transactionCommisionAmount, null, null, null, null, 
                                  null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, card.getCardHolder(), card.getSecurityCodeCard(), expirationCardDate, null, null, null, null, 
                                  null, null, null, ResponseCode.SUCCESS.getCode(), null, DocumentTypeE.COMMISION_CMS.getId(), entityManager);
            try {
                transactionCommisionCMS = operationsBD.saveTransactionsManagement(transactionCommisionCMS, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }
            
            //Se crea el objeto TransactionManagementHistory y se guarda en BD
            transactionHistoryCommisionCMS = operationsBD.createTransactionsManagementHistory(transactionsManagement, null, null, null, null, null,
                                  transactionCommisionCMS.getTransactionSequence(), TransactionE.COMISION_CMS.getId(), ChannelE.INT.getId(), null, null, null, null, null, 
                                  card.getProductId().getDomesticCurrencyId().getId(), transactionCommisionAmount, null, null, null, null, 
                                  null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, card.getCardHolder(), card.getSecurityCodeCard(), expirationCardDate, null, null, null, null, 
                                  null, null, null, ResponseCode.SUCCESS.getCode(), null, transactionCommisionCMS.getTransactionNumberIssuer(), entityManager);
            
            try {
                transactionHistoryCommisionCMS = operationsBD.saveTransactionsManagementHistory(transactionHistoryCommisionCMS, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }
        } else {
            return new TransactionResponse(ResponseCode.SUCCESS.getCode(),"The transaction received did not generate commission to be charged");
        }     
        return new TransactionResponse(ResponseCode.SUCCESS.getCode(),"The transaction to record the Alodiga commission corresponding to the received transaction was successfully saved in the database.",transactionCommisionAmount,transactionCommisionCMS); 
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
            if (cards == null) {
                return new CardResponse(ResponseCode.CARD_NOT_EXISTS.getCode(), ResponseCode.CARD_NOT_EXISTS.getMessage());
            } else {
                int statusCard = cards.getCardStatusId().getId();
                switch (statusCard) {
                    case 1:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: " + StatusCardE.SOLICI.statusCardDescription() + "");
                    case 2:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: " + StatusCardE.PERSON.statusCardDescription() + "");
                    case 3:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: " + StatusCardE.PENPER.statusCardDescription() + "");
                    case 4:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: " + StatusCardE.INVOK.statusCardDescription() + "");
                    case 5:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: " + StatusCardE.ERRPER.statusCardDescription() + "");
                    case 6:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: " + StatusCardE.PENDENTR.statusCardDescription() + "");
                    case 7:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: " + StatusCardE.ENTREG.statusCardDescription() + "");
                    case 8:
                        return new CardResponse(ResponseCode.SUCCESS.getCode(), "The card has the status: " + StatusCardE.ACTIVA.statusCardDescription() + "");
                    case 9:
                        return new CardResponse(ResponseCode.THE_CARD_IS_NOT_ACTIVE.getCode(), "The card is not active, its status is: " + StatusCardE.BLOQUE.statusCardDescription() + "");
                    default:
                        return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error loading status card");
                }
            }
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error loading card");
        }
    }

    public ValidateLimitsResponse getValidateLimits(String cardNumber, Integer transactionTypeId, Integer channelId, String countryCode, Float amountTransaction) {
        Long totalTransactionsByCardDaily = 0L;
        Double totalAmountByCardDaily = 0.00D;
        Long totalTransactionsByCardMonthly = 0L;
        Double totalAmountByUserMonthly = 0.00D;
        boolean isTransactionLocal = false;
        
        if (cardNumber == null || countryCode == null) {
            return new ValidateLimitsResponse(ResponseCode.INVALID_DATA, "The invalid data");
        }

        Card card = getCardByCardNumber(cardNumber);
        if (card == null) {
            return new ValidateLimitsResponse(ResponseCode.CARD_NOT_EXISTS, ResponseCode.CARD_NOT_FOUND.getMessage());
        }

        Country country = getCountry(countryCode);
        if (country == null) {
            return new ValidateLimitsResponse(ResponseCode.COUNTRY_NOT_FOUND, ResponseCode.COUNTRY_NOT_FOUND.getMessage());
        }

        if (country.getId().equals(card.getProductId().getCountryId().getId())) {
            isTransactionLocal = true;

        }

        ProductHasChannelHasTransaction productHasChannelHasTransaction = operationsBD.getSettingLimits(transactionTypeId, channelId, card.getProductId().getId(), entityManager);

        if (productHasChannelHasTransaction != null) {

            if (amountTransaction < Double.parseDouble(isTransactionLocal ? productHasChannelHasTransaction.getAmountMinimumTransactionDomestic().toString() : productHasChannelHasTransaction.getAmountMinimumTransactionInternational().toString())) {
                return new ValidateLimitsResponse(ResponseCode.MIN_TRANSACTION_AMOUNT, ResponseCode.MIN_TRANSACTION_AMOUNT.getMessage());
            }
            if (amountTransaction > Double.parseDouble(isTransactionLocal ? productHasChannelHasTransaction.getAmountMaximumTransactionDomestic().toString() : productHasChannelHasTransaction.getAmountMaximumTransactionInternational().toString())) {
                return new ValidateLimitsResponse(ResponseCode.MIN_TRANSACTION_AMOUNT, ResponseCode.MIN_TRANSACTION_AMOUNT.getMessage());
            }
            totalTransactionsByCardDaily = operationsBD.getTransactionsByCardByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDate(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), isTransactionLocal, country.getId(), entityManager);
            if ((totalTransactionsByCardDaily + 1) > productHasChannelHasTransaction.getMaximumNumberTransactionsDaily()) {
                return new ValidateLimitsResponse(ResponseCode.TRANSACTION_QUANTITY_LIMIT_DIALY, ResponseCode.TRANSACTION_QUANTITY_LIMIT_DIALY.getMessage());
            }
            totalAmountByCardDaily = operationsBD.getAmountMaxByUserByUserByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDate(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), isTransactionLocal, country.getId(), entityManager);
            if ((totalAmountByCardDaily + amountTransaction) > Double.parseDouble(isTransactionLocal ? productHasChannelHasTransaction.getDailyAmountLimitDomestic().toString() : productHasChannelHasTransaction.getDailyAmountLimitInternational().toString())) {
                return new ValidateLimitsResponse(ResponseCode.TRANSACTION_AMOUNT_LIMIT_DIALY, ResponseCode.TRANSACTION_AMOUNT_LIMIT_DIALY.getMessage());
            }

            totalTransactionsByCardMonthly = operationsBD.getTransactionsByCardByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDateMonth(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), isTransactionLocal, country.getId(), entityManager);
            if ((totalTransactionsByCardMonthly + 1) > productHasChannelHasTransaction.getMaximumNumberTransactionsMonthly()) {
                return new ValidateLimitsResponse(ResponseCode.TRANSACTION_QUANTITY_LIMIT_MONTHLY, ResponseCode.TRANSACTION_QUANTITY_LIMIT_MONTHLY.getMessage());
            }

            totalAmountByUserMonthly = operationsBD.getAmountMaxByUserByUserByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDateMonth(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), isTransactionLocal, country.getId(), entityManager);
            if ((totalAmountByUserMonthly + amountTransaction) > Double.parseDouble(isTransactionLocal ? productHasChannelHasTransaction.getMonthlyAmountLimitDomestic().toString() : productHasChannelHasTransaction.getMonthlyAmountLimitInternational().toString())) {
                return new ValidateLimitsResponse(ResponseCode.TRANSACTION_AMOUNT_LIMIT_MONTHLY, ResponseCode.TRANSACTION_AMOUNT_LIMIT_MONTHLY.getMessage());
            }

        }
        return new ValidateLimitsResponse(ResponseCode.SUCCESS, "SUCCESS");
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
    
    public TransactionResponse activateCard(String cardNumber, String cardHolder, String CVV, String cardDueDate, String answerDocumentIdentificationNumber, 
                                            String answerNumberPhoneCustomer, Date answerDateBirth, String answerEmailCustomer, Long messageMiddlewareId,
                                            Integer transactionTypeId, Integer channelId, Date transactionDate, Timestamp localTimeTransaction,
                                            String acquirerTerminalCodeId, String transactionNumberAcquirer, Integer acquirerCountryId) {
        
        String ARQC = null;
        String transactionNumberIssuer;
        TransactionsManagement transactionActivateCard = null;
        TransactionsManagementHistory transactionHistoryActivateCard = null;
        Card card = null;
        String phoneNumberCustomer = "";
        String emailCustomer = "";
        Date dateBirthCustomer;
        
        try {
            //Se valida la tarjeta
            CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate);
            if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //Se obtiene la tarjeta asociada a la transacción
                card = getCardByCardNumber(cardNumber);

                //Se obtiene el número de la transacción
                transactionNumberIssuer = generateNumberSequence(getSequencesByDocumentTypeByOriginApplication(DocumentTypeE.ACTIVATE_CARD.getId(), Constants.ORIGIN_APPLICATION_CMS_ID));

                //Se validan si las respuestas del tarjetahabiente son correctas
                //1. Se valida respuesta del documento de identificación
                CardResponse validateDocumentIdentification = validateDocumentIdentificationCustomer(cardNumber, answerDocumentIdentificationNumber);
                if (validateDocumentIdentification.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                    //2. Se valida respuesta de número de teléfono del tarjehabiente
                        phoneNumberCustomer = card.getPersonCustomerId().getPhonePerson().getAreaCode().concat(card.getPersonCustomerId().getPhonePerson().getNumberPhone());
                        if (phoneNumberCustomer.equals(answerNumberPhoneCustomer)) {
                            //3. Se valida respuesta del correo del tarjetabiente
                            emailCustomer = card.getPersonCustomerId().getEmail();
                            if (emailCustomer.equals(answerEmailCustomer)) {
                                //4. Se valida respuesta de la fecha de nacimiento
                                dateBirthCustomer = card.getPersonCustomerId().getNaturalCustomer().getDateBirth();
                                if (dateBirthCustomer.compareTo(answerDateBirth) == 0) {
                                    //Se registra la transacción de Activación de Tarjeta en el CMS en la BD
                                    String pattern = "MMyy";
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                    String expirationCardDate = simpleDateFormat.format(card.getExpirationDate());
                                    transactionActivateCard = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, transactionNumberAcquirer, transactionDate, 
                                                          TransactionE.ACTIVACION_TARJETA.getId(), ChannelE.INT.getId(), null, localTimeTransaction, null, null, null, 
                                                          card.getProductId().getDomesticCurrencyId().getId(), null, null, null, null, null, 
                                                          null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, card.getCardHolder(), card.getSecurityCodeCard(), expirationCardDate, null, null, null, null, 
                                                          null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.ACTIVATE_CARD.getId(), entityManager);
                                    
                                    try {
                                        transactionActivateCard = operationsBD.saveTransactionsManagement(transactionActivateCard, entityManager);
                                    } catch (Exception e) {
                                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                                    }
                                    
                                    transactionHistoryActivateCard = operationsBD.createTransactionsManagementHistory(null, null, acquirerTerminalCodeId, acquirerCountryId, transactionNumberAcquirer, transactionDate, 
                                                          transactionHistoryActivateCard.getTransactionSequence(),TransactionE.ACTIVACION_TARJETA.getId(), ChannelE.INT.getId(), null, localTimeTransaction, null, null, null, 
                                                          card.getProductId().getDomesticCurrencyId().getId(), null, null, null, null, null, 
                                                          null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, card.getCardHolder(), card.getSecurityCodeCard(), expirationCardDate, null, null, null, null, 
                                                          null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, transactionActivateCard.getTransactionNumberIssuer(), entityManager);
                                    
                                    try {
                                        transactionHistoryActivateCard = operationsBD.saveTransactionsManagementHistory(transactionHistoryActivateCard, entityManager);
                                    } catch (Exception e) {
                                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                                    }
                                    
                                    //retorna que la tarjeta fué activada con éxito
                                    return new TransactionResponse(ResponseCode.ACTIVE_CARD_YES.getCode(), ResponseCode.ACTIVE_CARD_YES.getMessage());
                                    
                                } else {
                                    return new TransactionResponse(ResponseCode.ACTIVE_CARD_NO.getCode(), ResponseCode.ACTIVE_CARD_NO.getMessage());
                                }
                            } else {
                                return new TransactionResponse(ResponseCode.ACTIVE_CARD_NO.getCode(), ResponseCode.ACTIVE_CARD_NO.getMessage());
                            }
                        } else {
                            return new TransactionResponse(ResponseCode.ACTIVE_CARD_NO.getCode(), ResponseCode.ACTIVE_CARD_NO.getMessage());
                        }
                } else {
                    return new TransactionResponse(ResponseCode.ACTIVE_CARD_NO.getCode(), ResponseCode.ACTIVE_CARD_NO.getMessage());
                }
            } else {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), validateCard.getMensajeRespuesta());
            }
        } catch (Exception e) {
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an unexpected error occurred");
        }
    }

    public CardResponse validateDocumentIdentificationCustomer(String cardNumber, String identificationNumber) {
        Card cards = new Card();
        try {
            cards = getCardByCardNumber(cardNumber);
            if (cards == null) {
                return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "The card does not exist in the CMS");
            } else {
                NaturalCustomer naturalCustomer = new NaturalCustomer();
                naturalCustomer = getCardCustomer(cards.getPersonCustomerId().getId());
                if (naturalCustomer != null) {
                    String identificationCustomer = naturalCustomer.getIdentificationNumber();
                    if (identificationCustomer.equals(identificationNumber)) {
                        return new CardResponse(ResponseCode.THE_IDENTIFICATION_NUMBER_IS_VERIFIED.getCode(), ResponseCode.THE_IDENTIFICATION_NUMBER_IS_VERIFIED.getMessage());
                    } else {
                        return new CardResponse(ResponseCode.THE_IDENTIFICATION_NUMBER_NOT_MATCH.getCode(), ResponseCode.THE_IDENTIFICATION_NUMBER_NOT_MATCH.getMessage());
                    }
                } else {
                    return new CardResponse(ResponseCode.CARD_OWNER_NOT_FOUND.getCode(), ResponseCode.CARD_OWNER_NOT_FOUND.getMessage());
                }
            }
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Unexpected error has occurred");
        }

    }

    public TransactionResponse changeCardStatus(String cardNumber, String CVV, String cardDueDate, String cardHolder, Long messageMiddlewareId, Long newStatusCardId, Integer statusUpdateReasonId, String observations,
            Date statusUpdateReasonDate, Long userResponsabibleStatusUpdateId, String documentIdentificationNumber, Integer transactionTypeId, Integer channelId, Date transactionDate, Timestamp localTimeTransaction, String acquirerTerminalCodeId, Integer acquirerCountryId) {
        //Se valida que la tarjeta exista en la BD del CMS
        CardResponse validateCard = getValidateCard(cardNumber);
        if (validateCard.getCodigoRespuesta().equals(ResponseCode.CARD_EXISTS.getCode())) {
            String transactionNumberIssuer;
            //Update Status Reason
            Integer reasonLost = StatusUpdateReasonE.PERDID.getId();
            Integer reasonStole = StatusUpdateReasonE.ROBO.getId();
            Integer reasonDamaged = StatusUpdateReasonE.DAÑADA.getId();
            Integer reasonCloning = StatusUpdateReasonE.CLONAC.getId();
            Integer reasonNoInterested = StatusUpdateReasonE.NOINT.getId();
            Integer reasonFound = StatusUpdateReasonE.ENCONT.getId();
            //Se obtiene el número de secuencia la transacción
            transactionNumberIssuer = generateNumberSequence(getSequencesByDocumentTypeByOriginApplication(DocumentTypeE.CHANGE_CARD_STATUS.getId(), Constants.ORIGIN_APPLICATION_CMS_ID));
            //Colocar asteriscos al cardNumber
            String cardNumberEncript = transformCardNumber(cardNumber);

            if (statusUpdateReasonId == reasonLost || statusUpdateReasonId == reasonStole || statusUpdateReasonId == reasonDamaged) {
                if (newStatusCardId == StatusCardE.BLOQUE.getId() || newStatusCardId == StatusCardE.ANULAD.getId()) {
                    //Se guarda el transactionsManagement
                    TransactionsManagement transactionsManagement = new TransactionsManagement();
                    transactionsManagement.setCardNumber(cardNumber);
                    transactionsManagement.setCvv(CVV);
                    transactionsManagement.setCardHolder(cardHolder);
                    transactionsManagement.setMessageMiddlewareId(messageMiddlewareId);
                    transactionsManagement.setTransactionTypeId(transactionTypeId);
                    transactionsManagement.setChannelId(channelId);
                    transactionsManagement.setTransactionNumberIssuer(transactionNumberIssuer);
                    transactionsManagement.setLocalDateTransaction(localTimeTransaction);
                    transactionsManagement.setAcquirerTerminalCode(acquirerTerminalCodeId);
                    transactionsManagement.setAcquirerCountryId(acquirerCountryId);
                    transactionsManagement.setTransactionDateIssuer(new Timestamp(new Date().getTime()));
                    entityManager.persist(transactionsManagement);

                    //Se guarda el transactionsHistory
                    TransactionsManagementHistory transactionsHistory = new TransactionsManagementHistory();
                    transactionsHistory.setCardNumber(cardNumber);
                    transactionsHistory.setCvv(CVV);
                    transactionsHistory.setCardHolder(cardHolder);
                    transactionsHistory.setTransactionNumberIssuer(transactionNumberIssuer);
                    transactionsHistory.setMessageMiddlewareId(messageMiddlewareId);
                    transactionsHistory.setTransactionTypeId(transactionTypeId);
                    transactionsHistory.setChannelId(channelId);
                    transactionsHistory.setTransactionDateIssuer(new Timestamp(new Date().getTime()));
                    transactionsHistory.setAcquirerTerminalCode(acquirerTerminalCodeId);
                    transactionsHistory.setAcquirerCountryId(acquirerCountryId);
                    entityManager.persist(transactionsManagement);

                    //Se obtiene el nuevo status, el statusUpdateReason y el usuario responsable
                    CardStatus cardStatus = (CardStatus) entityManager.createNamedQuery("CardStatus.findById", CardStatus.class).setParameter("id", newStatusCardId).getSingleResult();
                    StatusUpdateReason statusUpdateReason = (StatusUpdateReason) entityManager.createNamedQuery("StatusUpdateReason.findById", StatusUpdateReason.class).setParameter("id", statusUpdateReasonId).getSingleResult();
                    User user = (User) entityManager.createNamedQuery("User.findById", User.class).setParameter("id", userResponsabibleStatusUpdateId).getSingleResult();

                    //Se obtiene la tarjeta y se actualiza el estado de la tarjeta
                    Card cards = getCardByCardNumber(cardNumber);
                    cards.setCardStatusId(cardStatus);
                    cards.setStatusUpdateReasonId(statusUpdateReason);
                    cards.setUserResponsibleStatusUpdateId(user);
                    cards.setObservations(observations);
                    cards.setUpdateDate(new Timestamp(new Date().getTime()));
                    entityManager.persist(cards);

                    return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "", cardNumberEncript, cardStatus.getId().intValue(), observations, messageMiddlewareId, transactionNumberIssuer, localTimeTransaction);

                } else {
                    // El status no se puede actualizar al nuevo tiene que ser bloqueada o anulada
                    return new TransactionResponse(ResponseCode.THE_CARD_STATUS_NOT_BE_CHANGED.getCode(), "Card status cannot be updated, it can only be updated to status " + StatusCardE.BLOQUE.statusCardDescription() + "or " + StatusCardE.ANULAD.statusCardDescription() + "");
                }
            } else if (statusUpdateReasonId == reasonCloning || statusUpdateReasonId == reasonNoInterested) {
                if (newStatusCardId == StatusCardE.ANULAD.getId()) {
                    //Se guarda el transactionsManagement
                    TransactionsManagement transactionsManagement = new TransactionsManagement();
                    transactionsManagement.setCardNumber(cardNumber);
                    transactionsManagement.setCvv(CVV);
                    transactionsManagement.setCardHolder(cardHolder);
                    transactionsManagement.setMessageMiddlewareId(messageMiddlewareId);
                    transactionsManagement.setTransactionTypeId(transactionTypeId);
                    transactionsManagement.setChannelId(channelId);
                    transactionsManagement.setTransactionNumberIssuer(transactionNumberIssuer);
                    transactionsManagement.setLocalDateTransaction(localTimeTransaction);
                    transactionsManagement.setAcquirerTerminalCode(acquirerTerminalCodeId);
                    transactionsManagement.setAcquirerCountryId(acquirerCountryId);
                    transactionsManagement.setTransactionDateIssuer(new Timestamp(new Date().getTime()));
                    entityManager.persist(transactionsManagement);

                    //Se guarda el transactionsHistory
                    TransactionsManagementHistory transactionsHistory = new TransactionsManagementHistory();
                    transactionsHistory.setCardNumber(cardNumber);
                    transactionsHistory.setCvv(CVV);
                    transactionsHistory.setCardHolder(cardHolder);
                    transactionsHistory.setTransactionNumberIssuer(transactionNumberIssuer);
                    transactionsHistory.setMessageMiddlewareId(messageMiddlewareId);
                    transactionsHistory.setTransactionTypeId(transactionTypeId);
                    transactionsHistory.setChannelId(channelId);
                    transactionsHistory.setTransactionDateIssuer(new Timestamp(new Date().getTime()));
                    transactionsHistory.setAcquirerTerminalCode(acquirerTerminalCodeId);
                    transactionsHistory.setAcquirerCountryId(acquirerCountryId);
                    entityManager.persist(transactionsManagement);

                    //Se obtiene el nuevo status, el statusUpdateReason y el usuario responsable
                    CardStatus cardStatus = (CardStatus) entityManager.createNamedQuery("CardStatus.findById", CardStatus.class).setParameter("id", newStatusCardId).getSingleResult();
                    StatusUpdateReason statusUpdateReason = (StatusUpdateReason) entityManager.createNamedQuery("StatusUpdateReason.findById", StatusUpdateReason.class).setParameter("id", statusUpdateReasonId).getSingleResult();
                    User user = (User) entityManager.createNamedQuery("User.findById", User.class).setParameter("id", userResponsabibleStatusUpdateId).getSingleResult();

                    //Se obtiene la tarjeta y se actualiza el estado de la tarjeta
                    Card cards = getCardByCardNumber(cardNumber);
                    cards.setCardStatusId(cardStatus);
                    cards.setStatusUpdateReasonId(statusUpdateReason);
                    cards.setUserResponsibleStatusUpdateId(user);
                    cards.setUpdateDate(new Timestamp(new Date().getTime()));
                    entityManager.persist(cards);

                    return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "", cardNumberEncript, cardStatus.getId().intValue(), observations, messageMiddlewareId, transactionNumberIssuer, localTimeTransaction);

                } else {
                    return new TransactionResponse(ResponseCode.THE_CARD_STATUS_NOT_BE_CHANGED.getCode(), "Card status cannot be updated, it can only be updated to status " + StatusCardE.ANULAD.statusCardDescription() + "");
                }
            } else if (statusUpdateReasonId == reasonFound) {
                if (newStatusCardId == StatusCardE.ACTIVA.getId()) {
                    //Se obtiene la tarjeta y el producto por el id
                    Card cards = getCardByCardNumber(cardNumber);
                    Product product = (Product) entityManager.createNamedQuery("Product.findById", Product.class).setParameter("id", cards.getProductId().getId()).getSingleResult();

                    //Validar cuantos dias han transcurrido con el estados actual de la tarjeta
                    Date currentDate = new Timestamp(new Date().getTime());
                    int days = (int) ((currentDate.getTime() - cards.getUpdateDate().getTime()) / 86400000);

                    //Si es menor o igual al tiempo permitido por el producto sigue con el proceso
                    if (days <= product.getMaximunDeactivationTimeBlocking()) {

                        //Se guarda el transactionsManagement
                        TransactionsManagement transactionsManagement = new TransactionsManagement();
                        transactionsManagement.setCardNumber(cardNumber);
                        transactionsManagement.setCvv(CVV);
                        transactionsManagement.setCardHolder(cardHolder);
                        transactionsManagement.setMessageMiddlewareId(messageMiddlewareId);
                        transactionsManagement.setTransactionTypeId(transactionTypeId);
                        transactionsManagement.setChannelId(channelId);
                        transactionsManagement.setTransactionNumberIssuer(transactionNumberIssuer);
                        transactionsManagement.setLocalDateTransaction(localTimeTransaction);
                        transactionsManagement.setAcquirerTerminalCode(acquirerTerminalCodeId);
                        transactionsManagement.setAcquirerCountryId(acquirerCountryId);
                        transactionsManagement.setTransactionDateIssuer(new Timestamp(new Date().getTime()));
                        entityManager.persist(transactionsManagement);

                        //Se guarda el transactionsHistory
                        TransactionsManagementHistory transactionsHistory = new TransactionsManagementHistory();
                        transactionsHistory.setCardNumber(cardNumber);
                        transactionsHistory.setCvv(CVV);
                        transactionsHistory.setCardHolder(cardHolder);
                        transactionsHistory.setTransactionNumberIssuer(transactionNumberIssuer);
                        transactionsHistory.setMessageMiddlewareId(messageMiddlewareId);
                        transactionsHistory.setTransactionTypeId(transactionTypeId);
                        transactionsHistory.setChannelId(channelId);
                        transactionsHistory.setTransactionDateIssuer(new Timestamp(new Date().getTime()));
                        transactionsHistory.setAcquirerTerminalCode(acquirerTerminalCodeId);
                        transactionsHistory.setAcquirerCountryId(acquirerCountryId);
                        entityManager.persist(transactionsManagement);

                        //Se obtiene el nuevo status, el statusUpdateReason y el usuario responsable
                        CardStatus cardStatus = (CardStatus) entityManager.createNamedQuery("CardStatus.findById", CardStatus.class).setParameter("id", newStatusCardId).getSingleResult();
                        StatusUpdateReason statusUpdateReason = (StatusUpdateReason) entityManager.createNamedQuery("StatusUpdateReason.findById", StatusUpdateReason.class).setParameter("id", statusUpdateReasonId).getSingleResult();
                        User user = (User) entityManager.createNamedQuery("User.findById", User.class).setParameter("id", userResponsabibleStatusUpdateId).getSingleResult();

                        //Se actualiza el estado de la tarjeta
                        cards.setCardStatusId(cardStatus);
                        cards.setStatusUpdateReasonId(statusUpdateReason);
                        cards.setUserResponsibleStatusUpdateId(user);
                        cards.setUpdateDate(new Timestamp(new Date().getTime()));
                        entityManager.persist(cards);

                        return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "", cardNumberEncript, cardStatus.getId().intValue(), observations, messageMiddlewareId, transactionNumberIssuer, localTimeTransaction);

                    } else {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "The maximum time to change status has been exceeded");
                    }
                } else {
                    return new TransactionResponse(ResponseCode.THE_CARD_STATUS_NOT_BE_CHANGED.getCode(), "Card status cannot be updated, it can only be updated to status " + StatusCardE.ACTIVA.statusCardDescription() + "");
                }
            }
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "The card does not match any type of card update reason");
        } else {
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "The card is not a valid card");
        }

    }

    public String transformCardNumber(String cardNumber) {
        StringBuilder cadena = new StringBuilder(cardNumber);
        for (int i = 5; i < cadena.length(); i++) {
            if (i <= 11) {
                cadena.setCharAt(i, '*');
            }
        }
        return cadena.toString();
    }

    public OperationCardBalanceInquiryResponse cardBalanceInquiry(String cardNumber, String CVV, String ARQC, String documentIdentificationNumber, Integer transactionTypeId, Integer channelId, Date transactionDate, Date localTimeTransaction, String acquirerTerminalCodeId, Integer acquirerCountryId, Long messageMiddlewareId, String transactionNumberAcquirer, String cardDueDate, String cardHolder, String PinOffset) {
        try {
            CardResponse cardResponse = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate);
            String maskCardNumber = maskCCNumber(cardNumber);
            if (cardResponse.getCodigoRespuesta().equals(ResponseCode.THE_CARDHOLDER_IS_VERIFIED)) {
                //validar contra la caja 
                Sequences sequences = getSequencesByDocumentTypeByOriginApplication(DocumentTypeE.CARD_BALANCE_INQUIRY.getId(), Constants.ORIGIN_APPLICATION_CMS_ID);
                String transactionNumberIssuer = generateNumberSequence(sequences);
                TransactionsManagement transactionsManagement = new TransactionsManagement();
                transactionsManagement.setAcquirerTerminalCode(acquirerTerminalCodeId);
                transactionsManagement.setAcquirerCountryId(acquirerCountryId);
                transactionsManagement.setTransactionNumberIssuer(transactionNumberIssuer);
                transactionsManagement.setTransactionNumberAcquirer(transactionNumberAcquirer);
                transactionsManagement.setTransactionTypeId(transactionTypeId);
                transactionsManagement.setChannelId(channelId);
                transactionsManagement.setDateTransaction(transactionDate);
                transactionsManagement.setCardNumber(cardNumber);
                transactionsManagement.setCvv(CVV);
                transactionsManagement.setCardHolder(cardHolder);
                transactionsManagement.setLocalTimeTransaction(localTimeTransaction);
                transactionsManagement.setMessageMiddlewareId(messageMiddlewareId);
                transactionsManagement.setCreateDate(new Timestamp(new Date().getTime()));
                transactionsManagement.setTransactionDateIssuer(new Timestamp(new Date().getTime()));
                entityManager.persist(transactionsManagement);

                TransactionsManagementHistory transactionsManagementHistory = new TransactionsManagementHistory();
                transactionsManagementHistory.setAcquirerTerminalCode(acquirerTerminalCodeId);
                transactionsManagementHistory.setAcquirerCountryId(acquirerCountryId);
                transactionsManagementHistory.setTransactionNumberIssuer(transactionNumberIssuer);
                transactionsManagementHistory.setTransactionNumberAcquirer(transactionNumberAcquirer);
                transactionsManagementHistory.setTransactionTypeId(transactionTypeId);
                transactionsManagementHistory.setChannelId(channelId);
                transactionsManagementHistory.setDateTransaction(transactionDate);
                transactionsManagementHistory.setCardNumber(cardNumber);
                transactionsManagementHistory.setCvv(CVV);
                transactionsManagementHistory.setCardHolder(cardHolder);
                transactionsManagementHistory.setLocalTimeTransaction(localTimeTransaction);
                transactionsManagementHistory.setMessageMiddlewareId(messageMiddlewareId);
                transactionsManagementHistory.setCreateDate(new Timestamp(new Date().getTime()));
                transactionsManagementHistory.setTransactionDateIssuer(new Timestamp(new Date().getTime()));
                entityManager.persist(transactionsManagementHistory);
                Card card = getCardByCardNumber(cardNumber);
                Float cardCurrentBalance = getCurrentBalanceCard(card.getId());
                return new OperationCardBalanceInquiryResponse(ResponseCode.SUCCESS, "SUCCESS", maskCardNumber, cardCurrentBalance, transactionNumberIssuer, new Timestamp(new Date().getTime()), messageMiddlewareId);
            } else {
                return new OperationCardBalanceInquiryResponse(ResponseCode.INVALID_CARD, ResponseCode.INVALID_CARD.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new OperationCardBalanceInquiryResponse(ResponseCode.INTERNAL_ERROR, "");
        }

    }

    public static String maskCCNumber(String ccnum) {
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

    
    private Channel getChannelById(Integer channelId) {
        try {
            Query query = entityManager.createQuery("SELECT c FROM Channel c WHERE c.id = " + channelId );
            query.setMaxResults(1);
            Channel result = (Channel) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }
     
     public CalculateBonusCardResponse calculateBonus(String cardNumber, Integer transactionTypeId, Integer channelId, String countryCode,Float amountTransaction, String transactionNumber){   
        Long totalTransactionsByCardDaily = 0L;
        Double totalAmountByCardDaily = 0.00D;
        Long totalTransactionsByCardMonthly = 0L;
        Double totalAmountByUserMonthly = 0.00D;
        boolean isTransactionLocal = false;        
        
        if (cardNumber == null || countryCode ==null || transactionNumber  == null)
            return new CalculateBonusCardResponse(ResponseCode.INVALID_DATA, "The invalid data");
        
        TransactionsManagement transactionsManagement = getTransactionsManagementByNumber(transactionNumber);
        if (transactionsManagement==null)
             return new CalculateBonusCardResponse(ResponseCode.TRANSACTION_MANAGER_BY_NUMBER_NOT_EXISTS, ResponseCode.TRANSACTION_MANAGER_BY_NUMBER_NOT_EXISTS.getMessage());
       
        Card card = getCardByCardNumber(cardNumber);
        if (card==null)
             return new CalculateBonusCardResponse(ResponseCode.CARD_NOT_EXISTS, ResponseCode.CARD_NOT_FOUND.getMessage());
        
        Country country = getCountry(countryCode);
        if (country==null)
            return new CalculateBonusCardResponse(ResponseCode.COUNTRY_NOT_FOUND, ResponseCode.COUNTRY_NOT_FOUND.getMessage());
        
        if (country.getId().equals(card.getProductId().getCountryId().getId()))
            isTransactionLocal = true;
        
        List<ProgramLoyalty> programLoyaltys = getProgramLoyaltybyProductId(card.getProductId().getId());
        if (programLoyaltys.isEmpty())
             return new CalculateBonusCardResponse(ResponseCode.PROGRAM_LOYALTY_BY_CARD_NOT_EXISTS, ResponseCode.PROGRAM_LOYALTY_BY_CARD_NOT_EXISTS.getMessage());
 
         for (ProgramLoyalty programLoyalty : programLoyaltys) {

             DaysWeek dayWeek = getDaysWeekByDate();
             boolean addBonus = false;
             if (checkActiveProgramLoyalty(programLoyalty.getId(), dayWeek.getId())) {
                 ProgramLoyaltyTransaction programLoyaltyTransaction = getProgramLoyaltyTransactionbyParam(programLoyalty.getId(), transactionTypeId, channelId);
                 if (programLoyaltyTransaction != null) {
                     if (programLoyaltyTransaction.getTransactionId().getSubTypeTransactionId().getCode().equals(SubTransactionE.ADMINI.getCode())) {
                         addBonus = true;
                     }
                     totalTransactionsByCardDaily = operationsBD.getTransactionsByCardByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDate(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), isTransactionLocal, country.getId(), entityManager);
                     if (programLoyaltyTransaction.getTotalMaximumTransactions() != null) {
                         if ((totalTransactionsByCardDaily + 1) > programLoyaltyTransaction.getTotalMaximumTransactions()) {
                             addBonus = true;
                         }
                     }
                     totalAmountByCardDaily = operationsBD.getAmountMaxByUserByUserByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDate(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), isTransactionLocal, country.getId(), entityManager);
                     if (programLoyaltyTransaction.getTotalAmountDaily() != null) {
                         if ((totalAmountByCardDaily + amountTransaction) > Double.parseDouble(programLoyaltyTransaction.getTotalAmountDaily().toString())) {
                             addBonus = true;
                         }
                     }
                     totalAmountByUserMonthly = operationsBD.getAmountMaxByUserByUserByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDateMonth(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), isTransactionLocal, country.getId(), entityManager);
                     if (programLoyaltyTransaction.getTotalAmountMonthly() != null) {
                         if ((totalAmountByUserMonthly + amountTransaction) > Double.parseDouble(programLoyaltyTransaction.getTotalAmountMonthly().toString())) {
                             addBonus = true;
                         }
                     }
                     // si la variable addBonus esta activa 
                     if (addBonus) {
                         // Guardar transaccion y ajustar bonificacion
                         if (programLoyaltyTransaction.getProgramLoyaltyId().getProgramLoyaltyTypeId().getCode().equals(ProgramLoyaltyTypeE.PUNTOS.getCode())) {
                             try {
                                 TransactionPoint transactionPoint = new TransactionPoint();
                                 transactionPoint.setCardId(card);
                                 transactionPoint.setCreateDate(new Date());
                                 transactionPoint.setProgramLoyaltyTransactionId(programLoyaltyTransaction);
                                 transactionPoint.setPoints(programLoyaltyTransaction.getTotalPointsValue().intValue());
                                 transactionPoint.setTransactionReference(transactionNumber);
                                 saveTransactionPoint(transactionPoint);  // registrar transaccion de asignacion de puntos
                                 BonusCard bonusCard = getBonusCardByCardId(card.getId());
                                 if (bonusCard == null) {
                                     bonusCard = new BonusCard();
                                     bonusCard.setCardId(card);
                                     bonusCard.setCreateDate(new Date());
                                     bonusCard.setTotalPointsAccumulated(0);
                                 }
                                 bonusCard = updateBonusCard(bonusCard, programLoyaltyTransaction.getTotalPointsValue().intValue(), true); // actualizar los puntos por tarjeta
                                 saveBonusCard(bonusCard);
                             } catch (Exception ex) {
                                 return new CalculateBonusCardResponse(ResponseCode.INTERNAL_ERROR, "Error add points");
                             }
                         } else {
                             try {
                                 TransactionsManagement newTransactionManagement = operationsBD.createTransactionsManagement(transactionsManagement, null, null, null, null, null,
                                         null, ChannelE.INT.getId(), null, null, null, null, null,
                                         card.getProductId().getDomesticCurrencyId().getId(), programLoyaltyTransaction.getTotalBonificationValue(), null, null, null, null,
                                         null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, card.getCardHolder(), card.getSecurityCodeCard(), transactionsManagement.getExpirationCardDate(), null, null, null, null,
                                         null, null, null, ResponseCode.SUCCESS.getCode(), null, DocumentTypeE.BONUS_TRANSACTION_CMS.getId(), entityManager);
                                 try {
                                     newTransactionManagement = operationsBD.saveTransactionsManagement(newTransactionManagement, entityManager);
                                 } catch (Exception e) {
                                     return new CalculateBonusCardResponse(ResponseCode.INTERNAL_ERROR, "an error occurred while saving the transaction");
                                 }
            
                                //Se crea el objeto TransactionManagementHistory y se guarda en BD
                                TransactionsManagementHistory newTransactionManagementHistory = operationsBD.createTransactionsManagementHistory(transactionsManagement, null, null, null, null, null,
                                                      newTransactionManagement.getTransactionSequence(), TransactionE.BONIFICACIONES.getId(), ChannelE.INT.getId(), null, null, null, null, null, 
                                                      card.getProductId().getDomesticCurrencyId().getId(), programLoyaltyTransaction.getTotalBonificationValue(), null, null, null, null, 
                                                      null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, card.getCardHolder(), card.getSecurityCodeCard(), transactionsManagement.getExpirationCardDate(), null, null, null, null, 
                                                      null, null, null, ResponseCode.SUCCESS.getCode(), null, newTransactionManagement.getTransactionNumberIssuer(), entityManager);

                                try {
                                    newTransactionManagementHistory = operationsBD.saveTransactionsManagementHistory(newTransactionManagementHistory, entityManager);
                                } catch (Exception e) {
                                    return new CalculateBonusCardResponse(ResponseCode.INTERNAL_ERROR, "an error occurred while saving the transaction");
                                }
                                 entityManager.flush();
                                 //actualiazar balance_history
                                 BalanceHistoryCard balanceHistoryOld = loadLastBalanceHistoryByCard(card.getId());
                                 BalanceHistoryCard balanceHistory = new BalanceHistoryCard();
                                 balanceHistory.setId(null);
                                 balanceHistory.setCardUserId(card);
                                 Float previosAmount = balanceHistoryOld != null ? balanceHistoryOld.getCurrentBalance() : 0f;
                                 balanceHistory.setPreviousBalance(previosAmount);
                                 Float currentAmount = previosAmount + programLoyaltyTransaction.getTotalBonificationValue();
                                 balanceHistory.setCurrentBalance(currentAmount);
                                 balanceHistory.setTransactionsManagementId(newTransactionManagement);
                                 Date balanceDate = new Date();
                                 Timestamp balanceHistoryDate = new Timestamp(balanceDate.getTime());
                                 balanceHistory.setCreateDate(balanceHistoryDate);
                                 entityManager.persist(balanceHistory);
                                 //actualizar balance de accountCard
                                 AccountCard accountCard = getAccountCardbyCardId(card.getId());
                                 accountCard.setCurrentBalance(currentAmount);
                                 entityManager.merge(accountCard);
                             } catch (Exception ex) {
                                 return new CalculateBonusCardResponse(ResponseCode.INTERNAL_ERROR, "Error save transactionManagement");
                             }

                         }
                     }
                 }
             }
         }
        return new CalculateBonusCardResponse(ResponseCode.SUCCESS, "SUCCESS");
    }
     
    private List<ProgramLoyalty> getProgramLoyaltybyProductId(Long productId) {
        try {
            Query query = entityManager.createQuery("SELECT p FROM ProgramLoyalty p WHERE p.productId.id = " + productId + " AND p.statusProgramLoyaltyId.id=" + Constants.STATUS_LOYALTY_PROGRAM_ACTIVE);
            List<ProgramLoyalty> result = query.setHint("toplink.refresh", "true").getResultList();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    private ProgramLoyaltyTransaction getProgramLoyaltyTransactionbyParam(Long programLoyaltyId, Integer transactionId, Integer channelId) {
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
     
    private boolean checkActiveProgramLoyalty(Long programLoyaltyId, int dayWeekId) {
        try {
            Query query = entityManager.createQuery("SELECT d FROM DaysWeekHasProgramLoyalty d WHERE d.programLoyaltyId.id = " + programLoyaltyId + " AND d.daysWeekId.id=" + dayWeekId);
            query.setMaxResults(1);
            DaysWeekHasProgramLoyalty result = (DaysWeekHasProgramLoyalty) query.setHint("toplink.refresh", "true").getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
     
    private DaysWeek getDaysWeekByDate() {
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_WEEK);		
        try {
            Query query = entityManager.createQuery("SELECT d FROM DaysWeek d WHERE d.id = " + day );
            query.setMaxResults(1);
            DaysWeek result = (DaysWeek) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }
    
     public BonusCard saveBonusCard(BonusCard bonusCard) throws Exception{
        try {
            if (bonusCard.getId()==null)
                entityManager.persist(bonusCard);
            else
                entityManager.merge(bonusCard);
        } catch (Exception e) {
            e.printStackTrace();
           throw new Exception();
        }
        return bonusCard;
    }
     
    public TransactionsManagement saveTransactionsManagement(TransactionsManagement transactionsManagement) throws Exception{
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
            
    public TransactionPoint saveTransactionPoint(TransactionPoint transactionPoint) throws Exception{
        try {
            if (transactionPoint.getId()==null)
                entityManager.persist(transactionPoint);
            else
                entityManager.merge(transactionPoint);
        } catch (Exception e) {
            e.printStackTrace();
           throw new Exception();
        }
        return transactionPoint;
    }    


    public BonusCard updateBonusCard(BonusCard bonusCard, int points, boolean iscredit){
        int newPoints = bonusCard.getTotalPointsAccumulated();
        if (iscredit) {
            newPoints = newPoints + points;     
        } else {
            newPoints = newPoints - points;
        }
        bonusCard.setUpdateDate(new Date());
        bonusCard.setTotalPointsAccumulated(newPoints);
        return bonusCard;
    }        
     
    public TransactionsManagementHistory saveTransactionsManagementHistory(TransactionsManagementHistory transactionsManagement) throws Exception{
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
   
    
   private TransactionsManagement getTransactionsManagementByNumber(String transactionNumber) {
        String sql = "SELECT t FROM TransactionsManagement t WHERE t.transactionNumberAcquirer = ?1";
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("1", transactionNumber);
        try{
        TransactionsManagement result = (TransactionsManagement) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }       
    } 
   
    private BonusCard getBonusCardByCardId(Long cardId) {
        String sql = "SELECT b FROM BonusCard b WHERE b.cardId.id = ?1";
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("1", cardId);
        try{
        BonusCard result = (BonusCard) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }       
    } 
    
    public BalanceHistoryCard loadLastBalanceHistoryByCard(Long cardId) {
        try {
            Query query = entityManager.createQuery("SELECT b FROM BalanceHistoryCard b WHERE b.cardUserId.id = '" + cardId + "'");
            query.setMaxResults(1);
            BalanceHistoryCard result = (BalanceHistoryCard) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }  
    
    public AccountCard getAccountCardbyCardId(Long cardId) {
        try {
            AccountCard result = (AccountCard) entityManager.createNamedQuery("AccountCard.findByCardId", AccountCard.class).setParameter("cardId", cardId).getSingleResult();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    } 
    
    public TransactionResponse transferBetweenAccount(String cardNumberOrigin, String cardNumberDestinate, String CVVOrigin, String cardDueDateOrigin, String cardHolderOrigin, String documentIdentificationNumber, String ARQCOrigin, String CVVDestinate, String cardDueDateDestinate, String cardHolderDestinate, String ARQCDestinate, Integer channelId, Integer transactionTypeId,
            Long messageMiddlewareId, Date transactionDate, Timestamp localTimeTransaction, String acquirerTerminalCodeId, Integer acquirerCountryId, Float amountTransfer, Timestamp dateTimeTransmissionTerminal, Date localDateTransaction) {

        TransactionResponse transactionResponse = new TransactionResponse();
        TransactionsManagement transactionsManagement = new TransactionsManagement();
        TransactionsManagementHistory transactionsManagementHistory = new TransactionsManagementHistory();
        CardResponse cardResponseOrigin = validateCard(cardNumberOrigin, ARQCOrigin, cardHolderOrigin, CVVOrigin, cardDueDateOrigin);

        CardResponse cardResponseDestinate = validateCard(cardNumberDestinate, ARQCDestinate, cardHolderDestinate, CVVDestinate, cardDueDateDestinate);
        CardResponse validateIdentificationNumber = validateDocumentIdentificationCustomer(cardNumberOrigin, documentIdentificationNumber);
        if (cardResponseOrigin.getCodigoRespuesta().equals(ResponseCode.THE_CARDHOLDER_IS_VERIFIED) && cardResponseDestinate.getCodigoRespuesta().equals(ResponseCode.THE_CARDHOLDER_IS_VERIFIED)) {
            if (validateIdentificationNumber.getCodigoRespuesta().equals(ResponseCode.THE_IDENTIFICATION_NUMBER_IS_VERIFIED)) {

                try {
                    Card cardOrigin = getCardByCardNumber(cardNumberOrigin);
                    Card cardDestinate = getCardByCardNumber(cardNumberDestinate);
                    Float amountCardOrigin = getCurrentBalanceCard(cardOrigin.getId());
                    Float amountCardDestination = getCurrentBalanceCard(cardDestinate.getId());
                    transactionResponse = calculateCommisionCMS(cardNumberOrigin, channelId, transactionTypeId, amountTransfer, "1234");
                    Float amountCommission = transactionResponse.getTransactionFeesAmount();
                    Float amountTransferTotal = amountTransfer + amountCommission;
                    if (amountCardOrigin == null || amountCardOrigin < amountTransferTotal) {
                        return new TransactionResponse(ResponseCode.USER_HAS_NOT_BALANCE.getCode(), ResponseCode.USER_HAS_NOT_BALANCE.getMessage());
                    }

                    Sequences sequences = getSequencesByDocumentTypeByOriginApplication(DocumentTypeE.CARD_BALANCE_INQUIRY.getId(), Constants.ORIGIN_APPLICATION_CMS_ID);
                    String transactionNumberIssuer = generateNumberSequence(sequences);
                    transactionsManagement.setAcquirerTerminalCode(acquirerTerminalCodeId);
                    transactionsManagement.setAcquirerCountryId(acquirerCountryId);
                    transactionsManagement.setTransactionNumberIssuer(transactionNumberIssuer);
                    transactionsManagement.setTransactionTypeId(transactionTypeId);
                    transactionsManagement.setChannelId(channelId);
                    transactionsManagement.setDateTransaction(transactionDate);
                    transactionsManagement.setCardNumber(cardNumberOrigin);
                    transactionsManagement.setCvv(CVVOrigin);
                    transactionsManagement.setCardHolder(cardHolderOrigin);
                    transactionsManagement.setLocalTimeTransaction(localTimeTransaction);
                    transactionsManagement.setMessageMiddlewareId(messageMiddlewareId);
                    transactionsManagement.setCreateDate(new Timestamp(new Date().getTime()));
                    transactionsManagement.setTransactionDateIssuer(new Timestamp(new Date().getTime()));
                    entityManager.persist(transactionsManagement);

                    transactionsManagementHistory.setAcquirerTerminalCode(acquirerTerminalCodeId);
                    transactionsManagementHistory.setAcquirerCountryId(acquirerCountryId);
                    transactionsManagementHistory.setTransactionNumberIssuer(transactionNumberIssuer);
                    transactionsManagementHistory.setTransactionTypeId(transactionTypeId);
                    transactionsManagementHistory.setChannelId(channelId);
                    transactionsManagementHistory.setDateTransaction(transactionDate);
                    transactionsManagementHistory.setCardNumber(cardNumberOrigin);
                    transactionsManagementHistory.setCvv(CVVOrigin);
                    transactionsManagementHistory.setCardHolder(cardHolderOrigin);
                    transactionsManagementHistory.setLocalTimeTransaction(localTimeTransaction);
                    transactionsManagementHistory.setMessageMiddlewareId(messageMiddlewareId);
                    transactionsManagementHistory.setCreateDate(new Timestamp(new Date().getTime()));
                    transactionsManagementHistory.setTransactionDateIssuer(new Timestamp(new Date().getTime()));
                    entityManager.persist(transactionsManagementHistory);

                    //Actualizar balance History de Origen
                    BalanceHistoryCard balanceHistoryCardOrigin = new BalanceHistoryCard();
                    balanceHistoryCardOrigin.setCardUserId(cardOrigin);
                    balanceHistoryCardOrigin.setCreateDate(new Timestamp(new Date().getTime()));
                    Float currentBalanceSource = amountCardOrigin - amountTransferTotal;
                    balanceHistoryCardOrigin.setCurrentBalance(currentBalanceSource);
                    balanceHistoryCardOrigin.setPreviousBalance(amountCardOrigin);
                    balanceHistoryCardOrigin.setTransactionsManagementId(transactionsManagement);
                    entityManager.persist(balanceHistoryCardOrigin);

                    //Actualizar balance History de Destino
                    BalanceHistoryCard balanceHistoryCardDestinate = new BalanceHistoryCard();
                    balanceHistoryCardDestinate.setCardUserId(cardDestinate);
                    balanceHistoryCardOrigin.setCreateDate(new Timestamp(new Date().getTime()));
                    if (amountCardDestination == null) {
                        balanceHistoryCardOrigin.setCurrentBalance(amountTransfer);
                        balanceHistoryCardOrigin.setPreviousBalance(Constants.PREVIOUS_BALANCE_DESTINATION);
                    } else {
                        Float currentBalanceDestination = amountCardDestination + amountTransfer;
                        balanceHistoryCardDestinate.setPreviousBalance(amountCardDestination);
                        balanceHistoryCardDestinate.setCurrentBalance(currentBalanceDestination);
                    }
                    balanceHistoryCardDestinate.setTransactionsManagementId(transactionsManagement);
                    entityManager.persist(balanceHistoryCardDestinate);

                    //Actualizar currentBalance de la tarjeta origen en la tabla accountCard
                    AccountCard accountCardOrigin = new AccountCard();
                    cardResponseOrigin = getAccountNumberByCard(cardNumberOrigin);
                    accountCardOrigin.setCurrentBalance(currentBalanceSource);
                    entityManager.merge(accountCardOrigin);

                    //Actualizar currentBalance de la tarjeta destino en la tabla accountCard
                    AccountCard accountCardDestinate = new AccountCard();
                    cardResponseDestinate = getAccountNumberByCard(cardNumberOrigin);
                    accountCardDestinate.setCurrentBalance(currentBalanceSource);
                    entityManager.merge(accountCardDestinate);

                } catch (Exception e) {
                    e.printStackTrace();
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "");
                }
            } else {
                return new TransactionResponse(ResponseCode.THE_IDENTIFICATION_NUMBER_NOT_MATCH.getCode(), ResponseCode.THE_IDENTIFICATION_NUMBER_NOT_MATCH.getMessage());
            }
        } else {
            return new TransactionResponse(ResponseCode.INVALID_CARD.getCode(), ResponseCode.INVALID_CARD.getMessage());
        }

        return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "");

    }

    private List<TransactionsManagementHistory> getCardMovements(String cardNumber, Date startDate, Date endingDate){
        List<TransactionsManagementHistory> transactionsManagementHistory = new ArrayList<TransactionsManagementHistory>();
        String sql = "SELECT t.transactionReference, t.transactionTypeId,t.dateTransaction,t.localCurrencyTransactionAmount  FROM TransactionsManagementHistory t WHERE t.cardNumber = '"+ cardNumber + "' AND t.dateTransaction BETWEEN ?1 AND ?2 AND t.transactionTypeId IN(?3,?4,?5,?6,?7,?8,?9,?10,?11,?12)";
        
        StringBuilder sqlBuilder = new StringBuilder(sql);
        Query query = entityManager.createQuery(sqlBuilder.toString());
        query.setParameter("1", startDate);
        query.setParameter("2", endingDate);
        query.setParameter("3", TransactionE.RECARGA.getId());
        query.setParameter("4", TransactionE.RECARGA_INICIAL.getId());
        query.setParameter("5", TransactionE.RETIRO_DOMESTICO.getId());
        query.setParameter("6", TransactionE.RETIRO_INTERNACIONAL.getId());
        query.setParameter("7", TransactionE.COMPRA_DOMESTICA_PIN.getId());
        query.setParameter("8", TransactionE.COMPRA_INTERNACIONAL_PIN.getId());
        query.setParameter("9", TransactionE.DEPOSITO.getId());
        query.setParameter("10", TransactionE.CONSULTA.getId());
        query.setParameter("11", TransactionE.TRANSFERENCIAS_PROPIAS.getId());
        query.setParameter("12", TransactionE.RECARGA_MANUAL.getId());
        try{
        transactionsManagementHistory = query.setHint("toplink.refresh", "true").getResultList();
        } catch (NoResultException e) {
            return null;
        }
        return transactionsManagementHistory;
    }
    
    public TransactionResponse viewCardMovements(String cardNumber,String CVV,String cardDueDate,String cardHolder,String documentIdentificationNumber,Integer channelId,Integer transactionTypeId,Integer messageMiddlewareId,Date transactionDate,
            Timestamp localTimeTransaction,String acquirerTerminalCodeId,Integer acquirerCountryId,String startDate,String endingDate){
        Card card = null;
        TransactionsManagement transactionManagement = null;
        TransactionsManagementHistory transactionManagementHistory = null;
        String ARQC = null;
        operationsBDImp operationsBD = new operationsBDImp();
        try{
          CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate);
          if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) { 
                //Se le da formato Date a la fecha inicial y fecha final
                Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(startDate);  
                Date date2=new SimpleDateFormat("dd/MM/yyyy").parse(endingDate);
                // Se genera el numero de secuancia
                String transactionNumberIssuer = generateNumberSequence(getSequencesByDocumentTypeByOriginApplication(DocumentTypeE.CHECK_CARD_MOVEMENTS.getId(), Constants.ORIGIN_APPLICATION_CMS_ID));
                //Se buscan los movimientos de la tarjeta
                List<TransactionsManagementHistory> transactionsManagementHistory = getCardMovements(cardNumber,date1,date2);
                if(transactionsManagementHistory != null){
                    //Se obtiene la tarjeta asociada a la transacción y el saldo actual
                    card = getCardByCardNumber(cardNumber);
                    Float currentBalance =  getCurrentBalanceCard(card.getId());
                    //Se crea el objeto TransactionManagement y se guarda en BD
                    String pattern = "MMyy";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    String expirationCardDate = simpleDateFormat.format(card.getExpirationDate());
                    //Se crea el objeto TransactionManagement y se guarda en BD
                    transactionManagement = operationsBD.createTransactionsManagement(null,null,acquirerTerminalCodeId,acquirerCountryId,null,new Date(),transactionTypeId,channelId,
                                            null,localTimeTransaction,null,null,null,null,null,null,null,null,null,null,StatusTransactionManagementE.APPROVED.getId(),
                                            cardNumber,cardHolder,CVV,expirationCardDate,null,null,null,null,null,null,null,ResponseCode.SUCCESS.getCode(),messageMiddlewareId,DocumentTypeE.CHANGE_CARD_STATUS.getId(),entityManager);
                                                                                                                                                                                                                                                                                                                                                                                          
                    try {
                        transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    //Se crea el objeto TransactionManagementHistory y se guarda en BD
                    transactionManagementHistory = operationsBD.createTransactionsManagementHistory(null,null,acquirerTerminalCodeId,acquirerCountryId,null,new Date(),null,transactionTypeId,channelId,null,localTimeTransaction,
                                                    null,null,null,null,null,null,null,null,null,null,StatusTransactionManagementE.APPROVED.getId(),cardNumber,cardHolder,CVV,
                                                    expirationCardDate,null,null,null,null,null,null,null,ResponseCode.SUCCESS.getCode(),messageMiddlewareId,transactionNumberIssuer,entityManager);
                    try {
                        transactionManagementHistory = operationsBD.saveTransactionsManagementHistory(transactionManagementHistory, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    
                    return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "",cardNumber, card.getCardStatusId().getId(), card.getCardStatusId().getDescription(),messageMiddlewareId.longValue(),transactionNumberIssuer,currentBalance, date1, date2,transactionsManagementHistory.size(),transactionsManagementHistory);
                    
                } else {
                    //La tarjeta no tiene movientos
                    String pattern = "MMyy";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    String expirationCardDate = simpleDateFormat.format(card.getExpirationDate());
                    
                    //Se crea el objeto TransactionManagement RECHAZADO y se guarda en BD
                    transactionManagement = operationsBD.createTransactionsManagement(null,null,acquirerTerminalCodeId,acquirerCountryId,null,new Date(),transactionTypeId,channelId,
                                            null,localTimeTransaction,null,null,null,null,null,null,null,null,null,null,StatusTransactionManagementE.REJECTED.getId(),
                                            cardNumber,cardHolder,CVV,expirationCardDate,null,null,null,null,null,null,null,ResponseCode.THE_CARD_HAS_NO_MOVEMENTS.getCode(),messageMiddlewareId,DocumentTypeE.CHANGE_CARD_STATUS.getId(),entityManager);                                                                                                                                                                                                                                                                                                                                                                   
                    try {
                        transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    
                    //Se crea el objeto TransactionManagementHistory RECHAZADO y se guarda en BD
                    transactionManagementHistory = operationsBD.createTransactionsManagementHistory(null,null,acquirerTerminalCodeId,acquirerCountryId,null,new Date(),null,transactionTypeId,channelId,null,localTimeTransaction,
                                                    null,null,null,null,null,null,null,null,null,null,StatusTransactionManagementE.REJECTED.getId(),cardNumber,cardHolder,CVV,
                                                    expirationCardDate,null,null,null,null,null,null,null,ResponseCode.THE_CARD_HAS_NO_MOVEMENTS.getCode(),messageMiddlewareId,transactionNumberIssuer,entityManager);
                    try {
                        transactionManagementHistory = operationsBD.saveTransactionsManagementHistory(transactionManagementHistory, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(ResponseCode.THE_CARD_HAS_NO_MOVEMENTS.getCode(), ResponseCode.THE_CARD_HAS_NO_MOVEMENTS.getMessage());    
                } 
           } else {
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "The card is not a valid card");
           }    
        } catch (Exception e) {
        }
        
        
        return new TransactionResponse(ResponseCode.THE_CARD_HAS_NO_MOVEMENTS.getCode(), ResponseCode.THE_CARD_HAS_NO_MOVEMENTS.getMessage());   
    }

}
