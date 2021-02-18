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
import java.util.Calendar;
import com.alodiga.authorizer.cms.operationsBDImp.operationsBDImp;
import com.cms.commons.models.AccountProperties;
import com.cms.commons.models.HistoryCardStatusChanges;
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
        Card card;
        CardResponse cardResponse = new CardResponse();
        try {
            card = getCardByCardNumber(cardNumber);
            if (card.getCardNumber() != null) {
                if (!card.getSecurityCodeCard().equals(cvv)) {
                    return new CardResponse(ResponseCode.CVV_DIFFERENT.getCode(), ResponseCode.CVV_DIFFERENT.getMessage());
                }
                Date cardExpiration = card.getExpirationDate();
                SimpleDateFormat sdf = new SimpleDateFormat("MMyy");
                if (!sdf.format(cardExpiration).equals(cardDueDate)) {
                    return new CardResponse(ResponseCode.DATE_DIFFERENT.getCode(), ResponseCode.DATE_DIFFERENT.getMessage());
                }

            }
            if (!card.getSecurityCodeCard().equals(cvv)) {
                return new CardResponse(ResponseCode.CVV_DIFFERENT.getCode(), ResponseCode.CVV_DIFFERENT.getMessage());
            }
            Date cardExpiration = card.getExpirationDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMyy");
            System.out.println("fecha" + sdf.format(cardExpiration));
            if (!sdf.format(cardExpiration).equals(cardDueDate)) {
                return new CardResponse(ResponseCode.DATE_DIFFERENT.getCode(), ResponseCode.DATE_DIFFERENT.getMessage());
            }

        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error loading card");
        }
        cardResponse.setCard(card);
        return new CardResponse(ResponseCode.SUCCESS.getCode(), "The Card exists in the CMS");
    }

    public AccountCard getAccountNumberByCard(String cardNumber) {
        Card card = null;
        AccountCard accountCard = null;
        String accountNumber = "";
        try {
            card = getCardByCardNumber(cardNumber);
            if (card != null) {
                accountCard = (AccountCard) entityManager.createNamedQuery("AccountCard.findByCardId", AccountCard.class).setParameter("cardId", card.getId()).getSingleResult();

//                if (accountCard != null) {
//                    accountNumber = accountCard.getAccountNumber();
//                } else {
//                    return new CardResponse(ResponseCode.ACCOUNT_NOT_ASSOCIATED.getCode(), ResponseCode.ACCOUNT_NOT_ASSOCIATED.getMessage());
//                }
//            } else {
//                return new CardResponse(ResponseCode.CARD_NOT_EXISTS.getCode(), ResponseCode.CARD_NOT_EXISTS.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        return new CardResponse(ResponseCode.SUCCESS.getCode(), "SUCCESS", accountNumber);
        return accountCard;
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
            TransactionsManagement transactionsManagement = operationsBD.getTransactionsManagementByNumber(transactionNumberAcquirer, entityManager);

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
            return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "The transaction received did not generate commission to be charged");
        }
        return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "The transaction to record the Alodiga commission corresponding to the received transaction was successfully saved in the database.", transactionCommisionAmount, transactionCommisionCMS);
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

        Country country = operationsBD.getCountry(countryCode, entityManager);
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

    public CardResponse validateCard(String cardNumber, String ARQC, String cardHolder, String CVV, String cardDueDate, int indValidateCardActive) {
        CardResponse verifyActiveCard = new CardResponse();
        try {
            CardResponse validateCard = getValidateCard(cardNumber);
            //Se valida que la tarjeta exista en la BD del CMS
            if (validateCard.getCodigoRespuesta().equals(ResponseCode.CARD_EXISTS.getCode())) {
                if (indValidateCardActive == 1) {
                    verifyActiveCard = verifyActiveCard(cardNumber);
                } else {
                   verifyActiveCard.setCodigoRespuesta(ResponseCode.SUCCESS.getCode());
                }                
                //Se valida que la tarjeta tenga estatus ACTIVA
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
        CardStatus cardStatusActive;
        HistoryCardStatusChanges historyCardStatusChanges;
        int indValidateCardActive = 1;

        try {
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
                    transactionActivateCard.getTransactionSequence(), TransactionE.ACTIVACION_TARJETA.getId(), ChannelE.INT.getId(), null, localTimeTransaction, null, null, null,
                    card.getProductId().getDomesticCurrencyId().getId(), null, null, null, null, null,
                    null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, card.getCardHolder(), card.getSecurityCodeCard(), expirationCardDate, null, null, null, null,
                    null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, transactionActivateCard.getTransactionNumberIssuer(), entityManager);

            try {
                transactionHistoryActivateCard = operationsBD.saveTransactionsManagementHistory(transactionHistoryActivateCard, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }
            //Se valida la tarjeta
            CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate,indValidateCardActive);
            if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //Se obtiene la tarjeta asociada a la transacción
                card = getCardByCardNumber(cardNumber);
                //Se validan si las respuestas del tarjetahabiente son correctas
                //TODO: Validar si la tarjeta ya está ACTIVA
                //1. Se valida respuesta del documento de identificación
                CardResponse validateDocumentIdentification = validateDocumentIdentificationCustomer(cardNumber, answerDocumentIdentificationNumber);
                if (validateDocumentIdentification.getCodigoRespuesta().equals(ResponseCode.THE_IDENTIFICATION_NUMBER_IS_VERIFIED.getCode())) {
                    //2. Se valida respuesta de número de teléfono del tarjehabiente
                    phoneNumberCustomer = card.getPersonCustomerId().getPhonePerson().getAreaCode().concat(card.getPersonCustomerId().getPhonePerson().getNumberPhone());
                    if (phoneNumberCustomer.equals(answerNumberPhoneCustomer)) {
                        //3. Se valida respuesta del correo del tarjetabiente
                        emailCustomer = card.getPersonCustomerId().getEmail();
                        if (emailCustomer.equals(answerEmailCustomer)) {
                            //4. Se valida respuesta de la fecha de nacimiento
                            answerDateBirth = card.getPersonCustomerId().getNaturalCustomer().getDateBirth();
                            dateBirthCustomer = card.getPersonCustomerId().getNaturalCustomer().getDateBirth();
                            if (dateBirthCustomer.compareTo(answerDateBirth) == 0) {
                                //Se activa la tarjeta cambiando el estatus a ACTIVADA
                                cardStatusActive = operationsBD.getStatusCard(StatusCardE.ACTIVA.getId(), entityManager);
                                card.setCardStatusId(cardStatusActive);
                                card.setUpdateDate(new Timestamp(new Date().getTime()));
                                operationsBD.saveCard(card, entityManager);

                                //Se actualiza el historial de cambios de estados de la tarjeta
                                historyCardStatusChanges = operationsBD.createHistoryCardStatusChanges(card, cardStatusActive, null, null, entityManager);
                                operationsBD.saveHistoryCardStatusChanges(historyCardStatusChanges, entityManager);

                                //Se retorna que la tarjeta fué activada con éxito
                                return new TransactionResponse(ResponseCode.ACTIVE_CARD_YES.getCode(), ResponseCode.ACTIVE_CARD_YES.getMessage());
                            } else {
                                //La tarjeta no fué activada debido a que la fecha de nacimiento no coincide
                                transactionActivateCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                                transactionActivateCard.setResponseCode(ResponseCode.DATE_BIRTH_NOT_MATCH.getCode());
                                try {
                                    transactionActivateCard = operationsBD.saveTransactionsManagement(transactionActivateCard, entityManager);
                                } catch (Exception e) {
                                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                                }
                                return new TransactionResponse(ResponseCode.DATE_BIRTH_NOT_MATCH.getCode(), ResponseCode.DATE_BIRTH_NOT_MATCH.getMessage());
                            }
                        } else {
                            //La tarjeta no fué activada debido a que el correo suministrado no coincide
                            transactionActivateCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                            transactionActivateCard.setResponseCode(ResponseCode.EMAIL_CUSTOMER_NOT_MATCH.getCode());
                            try {
                                transactionActivateCard = operationsBD.saveTransactionsManagement(transactionActivateCard, entityManager);
                            } catch (Exception e) {
                                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                            }
                            return new TransactionResponse(ResponseCode.EMAIL_CUSTOMER_NOT_MATCH.getCode(), ResponseCode.EMAIL_CUSTOMER_NOT_MATCH.getMessage());
                        }
                    } else {
                        //La tarjeta no fué activada debido a que el teléfono del cliente no coincide
                        transactionActivateCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                        transactionActivateCard.setResponseCode(ResponseCode.PHONE_CUSTOMER_NOT_MATCH.getCode());
                        try {
                            transactionActivateCard = operationsBD.saveTransactionsManagement(transactionActivateCard, entityManager);
                        } catch (Exception e) {
                            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                        }
                        return new TransactionResponse(ResponseCode.PHONE_CUSTOMER_NOT_MATCH.getCode(), ResponseCode.PHONE_CUSTOMER_NOT_MATCH.getMessage());
                    }
                } else {
                    //La tarjeta no fué activada debido a que el documento de identificaciónd del cliente no coincide
                    transactionActivateCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionActivateCard.setResponseCode(ResponseCode.THE_IDENTIFICATION_NUMBER_NOT_MATCH.getCode());
                    try {
                        transactionActivateCard = operationsBD.saveTransactionsManagement(transactionActivateCard, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(ResponseCode.THE_IDENTIFICATION_NUMBER_NOT_MATCH.getCode(), ResponseCode.THE_IDENTIFICATION_NUMBER_NOT_MATCH.getMessage());
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

    public TransactionResponse changeCardStatus(String cardNumber, String CVV, String cardDueDate, String cardHolder, Long messageMiddlewareId, Long newStatusCardId, Integer statusUpdateReasonId, String observations, Date statusUpdateReasonDate, Long userResponsabibleStatusUpdateId,
                                                String documentIdentificationNumber, Integer transactionTypeId, Integer channelId, Date transactionDate, Timestamp localTimeTransaction, String acquirerTerminalCodeId, Integer acquirerCountryId) {
        //Se valida que la tarjeta exista en la BD del CMS
        CardResponse validateCard = getValidateCard(cardNumber);
        if (validateCard.getCodigoRespuesta().equals(ResponseCode.CARD_EXISTS.getCode())) {
            TransactionsManagement transactionManagement = null;
            TransactionsManagementHistory transactionManagementHistory = null;
            String transactionNumberIssuer;
            //Razones para actualizar el estatus de la tarjeta
            Integer reasonLost = StatusUpdateReasonE.PERDID.getId();
            Integer reasonStole = StatusUpdateReasonE.ROBO.getId();
            Integer reasonDamaged = StatusUpdateReasonE.DAÑADA.getId();
            Integer reasonCloning = StatusUpdateReasonE.CLONAC.getId();
            Integer reasonNoInterested = StatusUpdateReasonE.NOINT.getId();
            Integer reasonFound = StatusUpdateReasonE.ENCONT.getId();
            //Colocar asteriscos al cardNumber
            String cardNumberEncript = operationsBD.transformCardNumber(cardNumber);
            //Se obtiene la tarjeta
            Card cards = getCardByCardNumber(cardNumber);
            //Numero de expiracion de la tarjeta
            String pattern = "MMyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String expirationCardDate = simpleDateFormat.format(cards.getExpirationDate());

            //Se obtiene el nuevo status, el statusUpdateReason y el usuario responsable
            CardStatus cardStatus = (CardStatus) entityManager.createNamedQuery("CardStatus.findById", CardStatus.class).setParameter("id", newStatusCardId).getSingleResult();
            StatusUpdateReason statusUpdateReason = (StatusUpdateReason) entityManager.createNamedQuery("StatusUpdateReason.findById", StatusUpdateReason.class).setParameter("id", statusUpdateReasonId).getSingleResult();
            User user = (User) entityManager.createNamedQuery("User.findById", User.class).setParameter("id", userResponsabibleStatusUpdateId).getSingleResult();

            if (statusUpdateReasonId == reasonLost || statusUpdateReasonId == reasonStole || statusUpdateReasonId == reasonDamaged) {
                if (newStatusCardId == StatusCardE.BLOQUE.getId() || newStatusCardId == StatusCardE.ANULAD.getId()) {

                    //Se crea el objeto TransactionManagement Aprobado y se guarda en BD
                    transactionManagement = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, null, new Date(), transactionTypeId, channelId,
                            null, localTimeTransaction, null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.APPROVED.getId(),
                            cardNumber, cardHolder, CVV, expirationCardDate, null, null, null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.CHANGE_CARD_STATUS.getId(), entityManager);
                    try {
                        transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }

                    //Se crea el objeto TransactionManagementHistory Aprobado y se guarda en BD
                    transactionManagementHistory = operationsBD.createTransactionsManagementHistory(null, null, acquirerTerminalCodeId, acquirerCountryId, null, new Date(), null, transactionTypeId, channelId, null, localTimeTransaction,
                            null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV,
                            expirationCardDate, null, null, null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, transactionManagement.getTransactionNumberIssuer(), entityManager);
                    try {
                        transactionManagementHistory = operationsBD.saveTransactionsManagementHistory(transactionManagementHistory, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }

                    //Se obtiene la tarjeta y se actualiza el estado de la tarjeta
                    cards.setCardStatusId(cardStatus);
                    cards.setStatusUpdateReasonId(statusUpdateReason);
                    cards.setUserResponsibleStatusUpdateId(user);
                    cards.setObservations(observations);
                    cards.setUpdateDate(new Timestamp(new Date().getTime()));
                    entityManager.persist(cards);

                    return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "", cardNumberEncript, cardStatus.getId().intValue(), observations, messageMiddlewareId, transactionManagement.getTransactionNumberIssuer(), localTimeTransaction);

                }
            } else if (statusUpdateReasonId == reasonCloning || statusUpdateReasonId == reasonNoInterested) {
                if (newStatusCardId == StatusCardE.ANULAD.getId()) {

                    //Se crea el objeto TransactionManagement Aprobado y se guarda en BD
                    transactionManagement = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, null, new Date(), transactionTypeId, channelId,
                            null, localTimeTransaction, null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.APPROVED.getId(),
                            cardNumber, cardHolder, CVV, expirationCardDate, null, null, null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.CHANGE_CARD_STATUS.getId(), entityManager);
                    try {
                        transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }

                    //Se crea el objeto TransactionManagementHistory Aprobado y se guarda en BD
                    transactionManagementHistory = operationsBD.createTransactionsManagementHistory(null, null, acquirerTerminalCodeId, acquirerCountryId, null, new Date(), null, transactionTypeId, channelId, null, localTimeTransaction,
                            null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV,
                            expirationCardDate, null, null, null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, transactionManagement.getTransactionNumberIssuer(), entityManager);
                    try {
                        transactionManagementHistory = operationsBD.saveTransactionsManagementHistory(transactionManagementHistory, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }

                    //Se obtiene la tarjeta y se actualiza el estado de la tarjeta
                    cards.setCardStatusId(cardStatus);
                    cards.setStatusUpdateReasonId(statusUpdateReason);
                    cards.setUserResponsibleStatusUpdateId(user);
                    cards.setObservations(observations);
                    cards.setUpdateDate(new Timestamp(new Date().getTime()));
                    entityManager.persist(cards);

                    return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "", cardNumberEncript, cardStatus.getId().intValue(), observations, messageMiddlewareId, transactionManagement.getTransactionNumberIssuer(), localTimeTransaction);

                }
            } else if (statusUpdateReasonId == reasonFound) {
                if (newStatusCardId == StatusCardE.ACTIVA.getId()) {
                    //Se el producto por el id
                    Product product = (Product) entityManager.createNamedQuery("Product.findById", Product.class).setParameter("id", cards.getProductId().getId()).getSingleResult();

                    //Validar cuantos dias han transcurrido con el estados actual de la tarjeta
                    Date currentDate = new Timestamp(new Date().getTime());
                    int days = (int) ((currentDate.getTime() - cards.getUpdateDate().getTime()) / 86400000);

                    //Si es menor o igual al tiempo permitido por el producto sigue con el proceso
                    if (days <= product.getMaximunDeactivationTimeBlocking()) {

                        //Se crea el objeto TransactionManagement Aprobado y se guarda en BD
                        transactionManagement = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, null, new Date(), transactionTypeId, channelId,
                                null, localTimeTransaction, null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.APPROVED.getId(),
                                cardNumber, cardHolder, CVV, expirationCardDate, null, null, null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.CHANGE_CARD_STATUS.getId(), entityManager);
                        try {
                            transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                        } catch (Exception e) {
                            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                        }

                        //Se crea el objeto TransactionManagementHistory Aprobado y se guarda en BD
                        transactionManagementHistory = operationsBD.createTransactionsManagementHistory(null, null, acquirerTerminalCodeId, acquirerCountryId, null, new Date(), null, transactionTypeId, channelId, null, localTimeTransaction,
                                null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV,
                                expirationCardDate, null, null, null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, transactionManagement.getTransactionNumberIssuer(), entityManager);
                        try {
                            transactionManagementHistory = operationsBD.saveTransactionsManagementHistory(transactionManagementHistory, entityManager);
                        } catch (Exception e) {
                            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                        }

                        //Se actualiza el estado de la tarjeta
                        cards.setCardStatusId(cardStatus);
                        cards.setStatusUpdateReasonId(statusUpdateReason);
                        cards.setUserResponsibleStatusUpdateId(user);
                        cards.setObservations(observations);
                        cards.setUpdateDate(new Timestamp(new Date().getTime()));
                        entityManager.persist(cards);

                        return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "", cardNumberEncript, cardStatus.getId().intValue(), observations, messageMiddlewareId, transactionManagement.getTransactionNumberIssuer(), localTimeTransaction);

                    } else {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "The maximum time to change status has been exceeded");
                    }
                }
            } else {
                //Se crea el objeto TransactionManagement RECHAZADO y se guarda en BD
                transactionManagement = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, null, new Date(), transactionTypeId, channelId,
                        null, localTimeTransaction, null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.REJECTED.getId(),
                        cardNumber, cardHolder, CVV, expirationCardDate, null, null, null, null, null, null, null, ResponseCode.THE_CARD_STATUS_NOT_BE_CHANGED.getCode(), messageMiddlewareId, DocumentTypeE.CHANGE_CARD_STATUS.getId(), entityManager);
                try {
                    transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }

                //Se crea el objeto TransactionManagementHistory RECHAZADO y se guarda en BD
                transactionManagementHistory = operationsBD.createTransactionsManagementHistory(null, null, acquirerTerminalCodeId, acquirerCountryId, null, new Date(), null, transactionTypeId, channelId, null, localTimeTransaction,
                        null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.REJECTED.getId(), cardNumber, cardHolder, CVV,
                        expirationCardDate, null, null, null, null, null, null, null, ResponseCode.THE_CARD_STATUS_NOT_BE_CHANGED.getCode(), messageMiddlewareId, transactionManagement.getTransactionNumberIssuer(), entityManager);
                try {
                    transactionManagementHistory = operationsBD.saveTransactionsManagementHistory(transactionManagementHistory, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionResponse(ResponseCode.THE_CARD_STATUS_NOT_BE_CHANGED.getCode(), "Card status cannot be updated");
            }
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "The card does not match any type of card update reason");
        } else {
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "The card is not a valid card");
        }

    }

    public OperationCardBalanceInquiryResponse cardBalanceInquiry(String cardNumber, String CVV, String ARQC, String documentIdentificationNumber, Integer transactionTypeId, Integer channelId, Date transactionDate, Timestamp localTimeTransaction, String acquirerTerminalCodeId, Integer acquirerCountryId, Long messageMiddlewareId, String transactionNumberAcquirer, String cardDueDate, String cardHolder, String PinOffset) {
        int indValidateCardActive = 1;
        try {
            CardResponse cardResponse = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);
            String maskCardNumber = operationsBD.maskCCNumber(cardNumber);
            TransactionsManagement transactionsManagement = new TransactionsManagement();
            TransactionsManagementHistory transactionsManagementHistory = new TransactionsManagementHistory();
            if (cardResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //validar contra la caja 
                transactionsManagement = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, transactionNumberAcquirer, transactionDate,
                        TransactionE.CONSULTA.getId(), channelId, null, localTimeTransaction, null, null, null,
                        null, null, null, null, null, null,
                        null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV, cardDueDate, null, null, null, null,
                        null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.CARD_BALANCE_INQUIRY.getId(), entityManager);
                try {
                    transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                } catch (Exception e) {
                    return new OperationCardBalanceInquiryResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }

                //Se crea el objeto TransactionManagementHistory y se guarda en BD
                transactionsManagementHistory = operationsBD.createTransactionsManagementHistory(null, null, acquirerTerminalCodeId, acquirerCountryId, null, transactionDate,
                        transactionsManagement.getTransactionSequence(), TransactionE.CONSULTA.getId(), channelId, null, localTimeTransaction, null, null, null,
                        null, null, null, null, null, null,
                        null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV, cardDueDate, null, null, null, null,
                        null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, transactionsManagement.getTransactionNumberIssuer(), entityManager);

                try {
                    transactionsManagementHistory = operationsBD.saveTransactionsManagementHistory(transactionsManagementHistory, entityManager);
                } catch (Exception e) {
                    return new OperationCardBalanceInquiryResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                Card card = getCardByCardNumber(cardNumber);
                Float cardCurrentBalance = getCurrentBalanceCard(card.getId());
                if (cardCurrentBalance == null) {
                    cardCurrentBalance = 0.00F;
                }
                return new OperationCardBalanceInquiryResponse(ResponseCode.SUCCESS, "SUCCESS", maskCardNumber, cardCurrentBalance, transactionsManagement.getTransactionNumberIssuer(), new Timestamp(new Date().getTime()), messageMiddlewareId);
            } else {
                return new OperationCardBalanceInquiryResponse(ResponseCode.INVALID_CARD.getCode(), ResponseCode.INVALID_CARD.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new OperationCardBalanceInquiryResponse(ResponseCode.INTERNAL_ERROR.getCode(), "");
        }
    }

    public CalculateBonusCardResponse calculateBonus(String cardNumber, Integer transactionTypeId, Integer channelId, String countryCode, Float amountTransaction, String transactionNumber) {
        Long totalTransactionsByCardDaily = 0L;
        Double totalAmountByCardDaily = 0.00D;
        Long totalTransactionsByCardMonthly = 0L;
        Double totalAmountByUserMonthly = 0.00D;
        boolean isTransactionLocal = false;

        if (cardNumber == null || countryCode == null || transactionNumber == null) {
            return new CalculateBonusCardResponse(ResponseCode.INVALID_DATA, "The invalid data");
        }

        TransactionsManagement transactionsManagement = operationsBD.getTransactionsManagementByNumber(transactionNumber, entityManager);
        if (transactionsManagement == null) {
            return new CalculateBonusCardResponse(ResponseCode.TRANSACTION_MANAGER_BY_NUMBER_NOT_EXISTS, ResponseCode.TRANSACTION_MANAGER_BY_NUMBER_NOT_EXISTS.getMessage());
        }

        Card card = getCardByCardNumber(cardNumber);
        if (card == null) {
            return new CalculateBonusCardResponse(ResponseCode.CARD_NOT_EXISTS, ResponseCode.CARD_NOT_FOUND.getMessage());
        }

        Country country = operationsBD.getCountry(countryCode, entityManager);
        if (country == null) {
            return new CalculateBonusCardResponse(ResponseCode.COUNTRY_NOT_FOUND, ResponseCode.COUNTRY_NOT_FOUND.getMessage());
        }

        if (country.getId().equals(card.getProductId().getCountryId().getId())) {
            isTransactionLocal = true;
        }

        List<ProgramLoyalty> programLoyaltys = operationsBD.getProgramLoyaltybyProductId(card.getProductId().getId(), entityManager);
        if (programLoyaltys.isEmpty()) {
            return new CalculateBonusCardResponse(ResponseCode.PROGRAM_LOYALTY_BY_CARD_NOT_EXISTS, ResponseCode.PROGRAM_LOYALTY_BY_CARD_NOT_EXISTS.getMessage());
        }

        for (ProgramLoyalty programLoyalty : programLoyaltys) {

            DaysWeek dayWeek = operationsBD.getDaysWeekByDate(entityManager);
            boolean addBonus = false;
            if (operationsBD.checkActiveProgramLoyalty(programLoyalty.getId(), dayWeek.getId(), entityManager)) {
                ProgramLoyaltyTransaction programLoyaltyTransaction = operationsBD.getProgramLoyaltyTransactionbyParam(programLoyalty.getId(), transactionTypeId, channelId, entityManager);
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
                                BonusCard bonusCard = operationsBD.getBonusCardByCardId(card.getId(), entityManager);
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
                                BalanceHistoryCard balanceHistoryOld = operationsBD.loadLastBalanceHistoryByCard(card.getId(), entityManager);
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
                                AccountCard accountCard = operationsBD.getAccountCardbyCardId(card.getId(), entityManager);
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

    public BonusCard saveBonusCard(BonusCard bonusCard) throws Exception {
        try {
            if (bonusCard.getId() == null) {
                entityManager.persist(bonusCard);
            } else {
                entityManager.merge(bonusCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        return bonusCard;
    }

    public TransactionPoint saveTransactionPoint(TransactionPoint transactionPoint) throws Exception {
        try {
            if (transactionPoint.getId() == null) {
                entityManager.persist(transactionPoint);
            } else {
                entityManager.merge(transactionPoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        return transactionPoint;
    }

    public BonusCard updateBonusCard(BonusCard bonusCard, int points, boolean iscredit) {
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

    public TransactionResponse transferBetweenAccount(String cardNumberOrigin, String cardNumberDestinate, String CVVOrigin, String cardDueDateOrigin, String cardHolderOrigin, String ARQCOrigin, String CVVDestinate, String cardDueDateDestinate, String cardHolderDestinate, String ARQCDestinate, Integer channelId, Integer transactionTypeId,
            Long messageMiddlewareId, Date transactionDate, Timestamp localTimeTransaction, String acquirerTerminalCodeId, Integer acquirerCountryId, Float amountTransfer, Timestamp dateTimeTransmissionTerminal, Date localDateTransaction) {

        TransactionResponse transactionResponse = new TransactionResponse();
        TransactionsManagement transactionsManagement = new TransactionsManagement();
        int indValidateCardActive = 1;
        try {
            Float amountCommission = 0.00F;
            TransactionsManagementHistory transactionsManagementHistory = new TransactionsManagementHistory();
            CardResponse cardResponseOrigin = validateCard(cardNumberOrigin, ARQCOrigin, cardHolderOrigin, CVVOrigin, cardDueDateOrigin, indValidateCardActive);
            CardResponse cardResponseDestinate = validateCard(cardNumberDestinate, ARQCDestinate, cardHolderDestinate, CVVDestinate, cardDueDateDestinate, indValidateCardActive);
            Card cardOrigin = getCardByCardNumber(cardNumberOrigin);
            Card cardDestinate = getCardByCardNumber(cardNumberDestinate);
            ValidateLimitsResponse validateLimits = getValidateLimits(cardNumberOrigin, TransactionE.TRANSFERENCIAS_ENTRE_CUENTAS.getId(), channelId, cardOrigin.getProductId().getIssuerId().getCountryId().getCode(), amountTransfer);
            transactionsManagement = (TransactionsManagement) operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, null, transactionDate,
                    TransactionE.TRANSFERENCIAS_ENTRE_CUENTAS.getId(), channelId, dateTimeTransmissionTerminal, localTimeTransaction, localDateTransaction, null, null,
                    null, amountTransfer, null, null, null, null,
                    null, StatusTransactionManagementE.APPROVED.getId(), cardNumberOrigin, cardHolderOrigin, CVVOrigin, cardDueDateOrigin, null, cardNumberDestinate, null, null,
                    null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.TRANSFER_BETWEEN_ACCOUNT.getId(), entityManager);
            try {
                transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            //Se crea el objeto TransactionManagementHistory y se guarda en BD
            transactionsManagementHistory = (TransactionsManagementHistory) operationsBD.createTransactionsManagementHistory(null, null, acquirerTerminalCodeId, acquirerCountryId, null, transactionDate,
                    transactionsManagement.getTransactionSequence(), TransactionE.TRANSFERENCIAS_ENTRE_CUENTAS.getId(), channelId, dateTimeTransmissionTerminal, localTimeTransaction, localDateTransaction, null, null,
                    null, amountTransfer, null, null, null, null,
                    null, StatusTransactionManagementE.APPROVED.getId(), cardNumberOrigin, cardHolderOrigin, CVVOrigin, cardDueDateOrigin, null, cardNumberDestinate, null, null,
                    null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, transactionsManagement.getTransactionNumberIssuer(), entityManager);

            try {
                transactionsManagementHistory = operationsBD.saveTransactionsManagementHistory(transactionsManagementHistory, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            if (cardResponseOrigin.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode()) && cardResponseDestinate.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                if (validateLimits.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {

                    Float amountCardOrigin = getCurrentBalanceCard(cardOrigin.getId());
                    Float amountCardDestination = getCurrentBalanceCard(cardDestinate.getId());
                    transactionResponse = calculateCommisionCMS(cardNumberOrigin, channelId, transactionTypeId, amountTransfer, "1234");
                    if (transactionResponse.getTransactionFeesAmount() != null) {
                        amountCommission = transactionResponse.getTransactionFeesAmount();
                    }
                    Float amountTransferTotal = amountTransfer + amountCommission;
                    if (amountCardOrigin == null || amountCardOrigin < amountTransferTotal) {
                        return new TransactionResponse(ResponseCode.USER_HAS_NOT_BALANCE.getCode(), ResponseCode.USER_HAS_NOT_BALANCE.getMessage());
                    }

                    Float currentBalanceOrigin = amountCardOrigin - amountTransferTotal;
                    if (currentBalanceOrigin < cardOrigin.getProductId().getMinimumBalance()) {
                        return new TransactionResponse(ResponseCode.MINIMUM_AMOUNT_NOT_ALLOWED.getCode(), ResponseCode.MINIMUM_AMOUNT_NOT_ALLOWED.getMessage());
                    }

                    Float currentBalanceDestinate = amountCardDestination + amountTransfer;
                    if (currentBalanceDestinate > cardDestinate.getProductId().getMaximumBalance()) {
                        return new TransactionResponse(ResponseCode.MAXIMUM_AMOUNT_IS_NOT_ALLOWED.getCode(), ResponseCode.MAXIMUM_AMOUNT_IS_NOT_ALLOWED.getMessage());
                    }

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
                    AccountCard accountNumberOrigin = getAccountNumberByCard(cardNumberOrigin);
                    AccountCard accountCardOrigin = entityManager.find(AccountCard.class, accountNumberOrigin.getId());
                    accountCardOrigin.setUpdateDate(new Timestamp(new Date().getTime()));
                    accountCardOrigin.setCurrentBalance(currentBalanceSource);
                    entityManager.merge(accountCardOrigin);

                    //Actualizar currentBalance de la tarjeta destino en la tabla accountCard
                    AccountCard accountNumberDestinate = getAccountNumberByCard(cardNumberDestinate);
                    AccountCard accountCardDestinate = entityManager.find(AccountCard.class, accountNumberDestinate.getId());
                    accountCardDestinate.setUpdateDate(new Timestamp(new Date().getTime()));
                    accountCardDestinate.setCurrentBalance(currentBalanceSource);
                    entityManager.merge(accountCardDestinate);

                } else {
                    //Fallo en la validación de los limites
                    transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionsManagement.setResponseCode(ResponseCode.CARD_NOT_VALIDATE.getCode());
                    try {
                        transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(ResponseCode.CARD_NOT_VALIDATE.getCode(), ResponseCode.CARD_NOT_VALIDATE.getMessage());

                }
            } else {

                //Fallo en la validación de los limites
                transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionsManagement.setResponseCode(ResponseCode.INVALID_CARD.getCode());
                try {
                    transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionResponse(ResponseCode.INVALID_CARD.getCode(), ResponseCode.INVALID_CARD.getMessage());

            }

        } catch (Exception e) {
            e.printStackTrace();
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "INTERNAL_ERROR");
        }

        return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "SUCCESS");

    }

    public TransactionResponse viewCardMovements(String cardNumber,String CVV,String cardDueDate,String cardHolder,String documentIdentificationNumber,Integer channelId,Integer transactionTypeId,Long messageMiddlewareId,Date transactionDate,
                               Timestamp localTimeTransaction,String acquirerTerminalCodeId,Integer acquirerCountryId,String startDate,String endingDate){
        
        Card card = null;
        TransactionsManagement transactionManagement = null;
        TransactionsManagementHistory transactionManagementHistory = null;
        String ARQC = null;
        int indValidateCardActive = 1;
        try{
          CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);
          if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) { 
                //Se le da formato Date a la fecha inicial y fecha final
                Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(startDate);  
                Date date2=new SimpleDateFormat("dd/MM/yyyy").parse(endingDate);
                //Colocar asteriscos al cardNumber
                String cardNumberEncript = operationsBD.transformCardNumber(cardNumber);
                //Se buscan los movimientos de la tarjeta
                List<TransactionsManagementHistory> transactionsManagementHistory = operationsBD.getCardMovements(cardNumber, date1, date2, entityManager);
                if(transactionsManagementHistory != null){
                    //Se obtiene la tarjeta asociada a la transacción y el saldo actual
                    card = getCardByCardNumber(cardNumber);
                    Float currentBalance = getCurrentBalanceCard(card.getId());
                    //Se crea el objeto TransactionManagement y se guarda en BD
                    String pattern = "MMyy";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    String expirationCardDate = simpleDateFormat.format(card.getExpirationDate());
                    //Se crea el objeto TransactionManagement y se guarda en BD
                    transactionManagement = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, null, new Date(), transactionTypeId, channelId,
                            null, localTimeTransaction, null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.APPROVED.getId(),
                            cardNumber, cardHolder, CVV, expirationCardDate, null, null, null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.CHECK_CARD_MOVEMENTS.getId(), entityManager);

                    try {
                        transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    //Se crea el objeto TransactionManagementHistory y se guarda en BD
                    transactionManagementHistory = operationsBD.createTransactionsManagementHistory(null, null, acquirerTerminalCodeId, acquirerCountryId, null, new Date(), null, transactionTypeId, channelId, null, localTimeTransaction,
                            null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV,
                            expirationCardDate, null, null, null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, transactionManagement.getTransactionNumberIssuer(), entityManager);
                    try {
                        transactionManagementHistory = operationsBD.saveTransactionsManagementHistory(transactionManagementHistory, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    
                    return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "",cardNumberEncript, card.getCardStatusId().getId(), card.getCardStatusId().getDescription(),messageMiddlewareId.longValue(),transactionManagement.getTransactionNumberIssuer(),currentBalance, date1, date2,transactionsManagementHistory.size(),transactionsManagementHistory);
                    
                } else {
                    //La tarjeta no tiene movientos
                    String pattern = "MMyy";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    String expirationCardDate = simpleDateFormat.format(card.getExpirationDate());

                    //Se crea el objeto TransactionManagement RECHAZADO y se guarda en BD
                    transactionManagement = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, null, new Date(), transactionTypeId, channelId,
                            null, localTimeTransaction, null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.REJECTED.getId(),
                            cardNumber, cardHolder, CVV, expirationCardDate, null, null, null, null, null, null, null, ResponseCode.THE_CARD_HAS_NO_MOVEMENTS.getCode(), messageMiddlewareId, DocumentTypeE.CHECK_CARD_MOVEMENTS.getId(), entityManager);
                    try {
                        transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }

                    //Se crea el objeto TransactionManagementHistory RECHAZADO y se guarda en BD
                    transactionManagementHistory = operationsBD.createTransactionsManagementHistory(null, null, acquirerTerminalCodeId, acquirerCountryId, null, new Date(), null, transactionTypeId, channelId, null, localTimeTransaction,
                            null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.REJECTED.getId(), cardNumber, cardHolder, CVV,
                            expirationCardDate, null, null, null, null, null, null, null, ResponseCode.THE_CARD_HAS_NO_MOVEMENTS.getCode(), messageMiddlewareId, transactionManagement.getTransactionNumberIssuer(), entityManager);
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
