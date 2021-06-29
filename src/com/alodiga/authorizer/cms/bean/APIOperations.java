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
import com.cms.commons.enumeraciones.SecurityKeyTypeE;
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
import com.cms.commons.util.EjbUtils;
import com.alodiga.authorizer.cms.operationsBDImp.operationsBDImp;
import com.alodiga.authorizer.cms.responses.CardKeyHistoryListResponse;
import com.alodiga.authorizer.cms.responses.TransactionPurchageResponse;
import com.alodiga.authorizer.cms.utils.TripleDES;
import com.alodiga.authorizer.cms.utils.Utils;
import static com.alodiga.hsm.CryptoConnection.connectHsm;
import com.alodiga.hsm.OmniCryptoCommand;
import com.alodiga.hsm.response.GenerateCVVResponse;
import com.alodiga.hsm.response.GenerateKeyResponse;
import com.alodiga.hsm.util.HSMOperations;
import com.cms.commons.models.CardKeyHistory;
import com.cms.commons.models.Currency;
import com.cms.commons.models.HistoryCardStatusChanges;
import com.cms.commons.models.KeyProperties;
import com.cms.commons.models.SecurityKey;
import java.net.ResponseCache;
import java.util.ArrayList;
import com.alodiga.hsm.util.HSMOperations;
import static com.alodiga.hsm.util.HSMOperations.generateKey;
import com.alodiga.hsm.util.ConstantResponse;
import com.alodiga.hsm.util.Constant;
import com.cms.commons.enumeraciones.VerificationTypeSecurityKeyE;
import com.cms.commons.models.HSMBox;
import com.cms.commons.models.SecurityKey;
import com.cms.commons.models.SecurityKeyType;
import com.cms.commons.models.VerificationTypeSecurityKey;
import com.alodiga.hsm.response.GenerateKeyResponse;
import com.alodiga.hsm.response.IBMOfSetResponse;
import com.alodiga.hsm.response.GenerateCVVResponse;
import com.alodiga.hsm.util.ConstantResponse;
import static com.alodiga.hsm.util.HSMOperations.generateCVV;
import static com.alodiga.hsm.util.HSMOperations.generateIBMPinOffSet;
import static com.alodiga.hsm.util.HSMOperations.generateCVV;
import com.alodiga.hsm.util.Test;
import static com.alodiga.hsm.util.HSMOperations.getPinblock;
import static com.alodiga.hsm.util.HSMOperations.translatePINZPKToLMK;
import com.cms.commons.enumeraciones.SecurityKeySizeE;
import com.cms.commons.models.ApplicantNaturalPerson;
import com.cms.commons.models.CardRequestNaturalPerson;
import com.cms.commons.models.PhonePerson;
import com.cms.commons.models.ReviewRequest;
import com.cms.commons.models.IsoHsmEquivalence;
import com.cms.commons.models.PlastiCustomizingRequestHasCard;
import com.cms.commons.models.SecurityKeySize;
import com.cms.commons.models.StatusApplicant;

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
            Query query = entityManager.createQuery("SELECT c FROM Card c WHERE c.cardNumber = '" + cardNumber + "'");
            query.setMaxResults(1);
            Card result = (Card) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CardResponse getValidateCard(String cardNumber) {
        Card card = null;
        try {
            card = getCardByCardNumber(cardNumber);
            if (card == null) {
                return new CardResponse(ResponseCode.CARD_NOT_EXISTS.getCode(), ResponseCode.CARD_NOT_EXISTS.getMessage(), card);
            }

        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error loading card", card);
        }
        return new CardResponse(ResponseCode.CARD_EXISTS.getCode(), ResponseCode.CARD_EXISTS.getMessage(), card);
    }

    public NaturalCustomer getCardCustomer(Long personId) {
        try {
            Query query = entityManager.createQuery("SELECT n FROM NaturalCustomer n WHERE n.personId.id = ?1");
            query.setParameter("1", personId);
            NaturalCustomer result = (NaturalCustomer) query.setHint("toplink.refresh", "true").getSingleResult();
            return result;
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CardResponse validateCardByCardHolder(Card card, String cardHolder) {
        try {
            if (card != null) {
                NaturalCustomer naturalCustomer = null;
                naturalCustomer = getCardCustomer(card.getPersonCustomerId().getId());
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
            e.printStackTrace();
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error loading card");
        }
    }

    public Float getCurrentBalanceCard(Long cardId) {
        try {
            Query query = entityManager.createQuery("SELECT b FROM BalanceHistoryCard b WHERE b.cardUserId.id = '" + cardId + "' ORDER BY b.id DESC");
            query.setMaxResults(1);
            BalanceHistoryCard result = (BalanceHistoryCard) query.setHint("toplink.refresh", "true").getSingleResult();
            return result.getCurrentBalance();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CardResponse getValidateCVVAndDueDateCard(Card card, String cvv, String cardDueDate) {
        CardResponse cardResponse = new CardResponse();
        try {
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
        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error loading card");
        }
        cardResponse.setCard(card);
        return new CardResponse(ResponseCode.SUCCESS.getCode(), "The CVV and expiration date of the card are valid");
    }

    public AccountCard getAccountNumberByCard(String cardNumber) {
        Card card = null;
        AccountCard accountCard = null;
        try {
            card = getCardByCardNumber(cardNumber);
            if (card != null) {
                accountCard = (AccountCard) entityManager.createNamedQuery("AccountCard.findByCardId", AccountCard.class).setParameter("cardId", card.getId()).getSingleResult();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountCard;
    }

    public CardResponse getValidateCardByLUNH(Card card) {
        try {
            if (checkLuhn(card.getCardNumber())) {
                return new CardResponse(ResponseCode.SUCCESS.getCode(), "The verification digit on the card is valid");
            } else {
                System.out.println("This is not a valid card");
                return new CardResponse(ResponseCode.CHECK_DIGIT_CARD_INCORRECT.getCode(), ResponseCode.CHECK_DIGIT_CARD_INCORRECT.getMessage());
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

    public TransactionResponse calculateCommisionCMS(Card card, Integer channelId, Integer transactionTypeId, Float settlementTransactionAmount, String transactionNumberIssuer) {
        RateByCard rateByCard = null;
        RateByProduct rateByProduct = null;
        Long totalTransactionsByCard = 0L;
        Long totalTransactionsPerMonthByCard = 0L;
        int transactionsInitialExempt = 0;
        int transactionExemptPerMonth = 0;
        Float transactionCommisionAmount = 0.00F;
        Float fixedRate = 0.00F;
        Float percentRate = 0.00F;
        TransactionsManagement transactionCommisionCMS = null;
        TransactionsManagementHistory transactionHistoryCommisionCMS = null;
        String transactionConcept = "Comisión CMS";
        Currency transactionCurrency = null;

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
            if (fixedRate > 0) {
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
            TransactionsManagement transactionsManagement = operationsBD.getTransactionsManagementByNumber(transactionNumberIssuer, entityManager);

            //Se crea el objeto TransactionManagement y se guarda en BD
            String pattern = "MMyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String expirationCardDate = simpleDateFormat.format(card.getExpirationDate());
            if (card.getProductId().getDomesticCurrencyId() != null) {
                transactionCurrency = card.getProductId().getDomesticCurrencyId();
            } else {
                transactionCurrency = card.getProductId().getInternationalCurrencyId();
            }
            transactionCommisionCMS = operationsBD.createTransactionsManagement(transactionsManagement, null, null, null, null, null,
                    TransactionE.COMISION_CMS.getId(), ChannelE.INT.getId(), null, null, null, null, null,
                    transactionCurrency.getId(), transactionCommisionAmount, null, null, null, null,
                    null, StatusTransactionManagementE.APPROVED.getId(), card.getCardNumber(), card.getCardHolder(), card.getSecurityCodeCard(), expirationCardDate, null, null, null, null, null,
                    null, null, null, null, null, ResponseCode.SUCCESS.getCode(), null, DocumentTypeE.COMMISION_CMS.getId(), transactionConcept, entityManager);
            try {
                transactionCommisionCMS = operationsBD.saveTransactionsManagement(transactionCommisionCMS, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }
        } else {
            return new TransactionResponse(ResponseCode.COMMISSION_NOT_APPLY.getCode(), "The transaction received did not generate commission to be charged");
        }
        return new TransactionResponse(ResponseCode.COMMISSION_YES_APPLY.getCode(), "The transaction to record the Alodiga commission corresponding to the received transaction was successfully saved in the database.", transactionCommisionAmount, transactionCommisionCMS);
    }

    public CardResponse verifyActiveCard(Card card) {
        try {
            if (card == null) {
                return new CardResponse(ResponseCode.CARD_NOT_EXISTS.getCode(), ResponseCode.CARD_NOT_EXISTS.getMessage());
            } else {
                int statusCard = card.getCardStatusId().getId();
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

    public ValidateLimitsResponse getValidateLimits(Card card, Integer transactionTypeId, Integer channelId, String countryCode, Float amountTransaction) {
        Long totalTransactionsByCardDaily = 0L;
        Double totalAmountByCardDaily = 0.00D;
        Long totalTransactionsByCardMonthly = 0L;
        Double totalAmountByUserMonthly = 0.00D;
        boolean isTransactionLocal = false;
        int countryIssuerId = 0;
        Integer indFuncionality = 1;

        if (card.getCardNumber() == null || countryCode == null) {
            return new ValidateLimitsResponse(ResponseCode.INVALID_DATA, "The invalid data");
        }

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

        //Se obtiene el país asociado al emisor de la tarjeta
        countryIssuerId = card.getProductId().getCountryId().getId();

        ProductHasChannelHasTransaction productHasChannelHasTransaction = operationsBD.getSettingLimits(transactionTypeId, channelId, card.getProductId().getId(), entityManager);

        if (productHasChannelHasTransaction != null) {

            if (amountTransaction < Double.parseDouble(isTransactionLocal ? productHasChannelHasTransaction.getAmountMinimumTransactionDomestic().toString() : productHasChannelHasTransaction.getAmountMinimumTransactionInternational().toString())) {
                return new ValidateLimitsResponse(ResponseCode.MIN_TRANSACTION_AMOUNT, ResponseCode.MIN_TRANSACTION_AMOUNT.getMessage());
            }
            if (amountTransaction > Double.parseDouble(isTransactionLocal ? productHasChannelHasTransaction.getAmountMaximumTransactionDomestic().toString() : productHasChannelHasTransaction.getAmountMaximumTransactionInternational().toString())) {
                return new ValidateLimitsResponse(ResponseCode.MAX_TRANSACTION_AMOUNT, ResponseCode.MAX_TRANSACTION_AMOUNT.getMessage());
            }
            totalTransactionsByCardDaily = operationsBD.getTransactionsByCardByTransactionByProductCurrentDate(card.getCardNumber(), EjbUtils.getBeginningDate(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), entityManager);
            if ((totalTransactionsByCardDaily + 1) > productHasChannelHasTransaction.getMaximumNumberTransactionsDaily()) {
                return new ValidateLimitsResponse(ResponseCode.TRANSACTION_QUANTITY_LIMIT_DAILY, ResponseCode.TRANSACTION_QUANTITY_LIMIT_DAILY.getMessage());
            }
            totalAmountByCardDaily = operationsBD.getAmountMaxByUserByUserByTransactionByProductCurrentDate(card.getCardNumber(), EjbUtils.getBeginningDate(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), isTransactionLocal, countryIssuerId, indFuncionality, entityManager);
            if ((totalAmountByCardDaily + amountTransaction) > Double.parseDouble(isTransactionLocal ? productHasChannelHasTransaction.getDailyAmountLimitDomestic().toString() : productHasChannelHasTransaction.getDailyAmountLimitInternational().toString())) {
                return new ValidateLimitsResponse(ResponseCode.TRANSACTION_AMOUNT_LIMIT_DAILY, ResponseCode.TRANSACTION_AMOUNT_LIMIT_DAILY.getMessage());
            }

            totalTransactionsByCardMonthly = operationsBD.getTransactionsByCardByTransactionByProductCurrentDate(card.getCardNumber(), EjbUtils.getBeginningDateMonth(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), entityManager);
            if ((totalTransactionsByCardMonthly + 1) > productHasChannelHasTransaction.getMaximumNumberTransactionsMonthly()) {
                return new ValidateLimitsResponse(ResponseCode.TRANSACTION_QUANTITY_LIMIT_MONTHLY, ResponseCode.TRANSACTION_QUANTITY_LIMIT_MONTHLY.getMessage());
            }

            totalAmountByUserMonthly = operationsBD.getAmountMaxByUserByUserByTransactionByProductCurrentDate(card.getCardNumber(), EjbUtils.getBeginningDateMonth(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), isTransactionLocal, countryIssuerId, indFuncionality, entityManager);
            if ((totalAmountByUserMonthly + amountTransaction) > Double.parseDouble(isTransactionLocal ? productHasChannelHasTransaction.getMonthlyAmountLimitDomestic().toString() : productHasChannelHasTransaction.getMonthlyAmountLimitInternational().toString())) {
                return new ValidateLimitsResponse(ResponseCode.TRANSACTION_AMOUNT_LIMIT_MONTHLY, ResponseCode.TRANSACTION_AMOUNT_LIMIT_MONTHLY.getMessage());
            }

        }
        return new ValidateLimitsResponse(ResponseCode.SUCCESS, "SUCCESS");
    }

    public CardResponse validateCard(String cardNumber, String ARQC, String cardHolder, String CVV, String cardDueDate, int indValidateCardActive) {
        CardResponse verifyActiveCard = new CardResponse();
        Card card = null;
        try {
            CardResponse validateCard = getValidateCard(cardNumber);
            //Se valida que la tarjeta exista en la BD del CMS
            if (validateCard.getCodigoRespuesta().equals(ResponseCode.CARD_EXISTS.getCode())) {
                card = validateCard.getCard();
                if (indValidateCardActive == 1) {
                    verifyActiveCard = verifyActiveCard(card);
                } else {
                    verifyActiveCard.setCodigoRespuesta(ResponseCode.SUCCESS.getCode());
                }
                //Se valida que la tarjeta tenga estatus ACTIVA
                if (verifyActiveCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                    CardResponse validateCardByLUNH = getValidateCardByLUNH(card);
                    //Se valida el dígito verificador de la tarjeta a través de algoritmo de LUHN
                    if (getValidateCardByLUNH(card).getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                        //CardResponse validateCVVAndDueDate = getValidateCVVAndDueDateCard(card, CVV, cardDueDate);
                        //Se valida el CVV y la fecha de vencimiento de la tarjeta
                        //if (validateCVVAndDueDate.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                            //Se valida el nombre del cliente en la tarjeta (CardHolder)
                            CardResponse validateCardHolder = validateCardByCardHolder(card, cardHolder);
                            if (validateCardHolder.getCodigoRespuesta().equals(ResponseCode.THE_CARDHOLDER_IS_VERIFIED.getCode())) {
                                return new CardResponse(ResponseCode.SUCCESS.getCode(), "The Card is Valid", card);
                            } else {
                                return validateCardHolder;
                            }
//                        } else {
//                            return validateCVVAndDueDate;
//                        }
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
            Integer transactionTypeId, Integer channelId, Date transactionDate, String localTimeTransaction,
            String acquirerTerminalCodeId, String transactionNumberAcquirer, Integer acquirerCountryId) {

        String ARQC = null;
        TransactionsManagement transactionActivateCard = null;
        Card card = null;
        String phoneNumberCustomer = "";
        String emailCustomer = "";
        Date dateBirthCustomer;
        CardStatus cardStatusActive;
        HistoryCardStatusChanges historyCardStatusChanges;
        int indValidateCardActive = 0;
        String conceptTransaction = "Activación de Tarjeta";
        String customerIdentificationNumber = "";
        GenerateCVVResponse generateCVVResponse = null;

        try {
            CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);
            if (validateCard.getCard() != null) {
                //Se obtiene la tarjeta asociada
                card = validateCard.getCard();
                //Se obtiene el numero de indentifiación del cliente
                if (card.getPersonCustomerId().getPersonTypeId().getIndNaturalPerson() == true) {
                    customerIdentificationNumber = card.getPersonCustomerId().getNaturalCustomer().getIdentificationNumber();
                } else {
                    customerIdentificationNumber = card.getPersonCustomerId().getLegalCustomer().getIdentificationNumber();
                }
            }

            //Se registra la transacción de Activación de Tarjeta en el CMS en la BD
            Country country = operationsBD.getCountry(String.valueOf(acquirerCountryId), entityManager);
            transactionActivateCard = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, country.getId(), transactionNumberAcquirer, transactionDate,
                    TransactionE.ACTIVACION_TARJETA.getId(), ChannelE.INT.getId(), null, localTimeTransaction, null, null, null,
                    null, null, null, null, null, null,
                    null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV, cardDueDate, customerIdentificationNumber, null, null, null, null,
                    null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.ACTIVATE_CARD.getId(), conceptTransaction, entityManager);

            try {
                transactionActivateCard = operationsBD.saveTransactionsManagement(transactionActivateCard, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            //Se valida la tarjeta
            if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //Se obtiene el id de la llave CVK o KVC asociada a la tarjeta a validar
                PlastiCustomizingRequestHasCard plasticRequest = operationsBD.getSecurityKeyByCard(card.getId(), entityManager);
                generateCVVResponse = generateCVV(plasticRequest.getSecurityKeyId().getEncryptedValue(), cardNumber, cardDueDate, Constants.HSM_REQUEST_VALUE_CVV2);
                if (!generateCVVResponse.getCvv().equals(CVV)) {
                    //Se valida si la tarjeta ya esta ACTIVADA
                    if (card.getCardStatusId().getDescription().equals(StatusCardE.ACTIVA.statusCardDescription())) {
                        transactionActivateCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                        transactionActivateCard.setResponseCode(ResponseCode.CARD_ALREADY_ACTIVE.getCode());
                        try {
                            transactionActivateCard = operationsBD.saveTransactionsManagement(transactionActivateCard, entityManager);
                        } catch (Exception e) {
                            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                        }
                        return new TransactionResponse(ResponseCode.CARD_ALREADY_ACTIVE.getCode(), ResponseCode.CARD_ALREADY_ACTIVE.getMessage());
                    }

                    //Se validan si las respuestas del tarjetahabiente son correctas
                    //1. Se valida respuesta del documento de identificación
                    CardResponse validateDocumentIdentification = validateDocumentIdentificationCustomer(card, answerDocumentIdentificationNumber);
                    if (validateDocumentIdentification.getCodigoRespuesta().equals(ResponseCode.THE_IDENTIFICATION_NUMBER_IS_VERIFIED.getCode())) {
                        //2. Se valida respuesta de número de teléfono del tarjehabiente
                        phoneNumberCustomer = card.getPersonCustomerId().getPhonePerson().getAreaCode().concat(card.getPersonCustomerId().getPhonePerson().getNumberPhone());
                        if (phoneNumberCustomer.equals(answerNumberPhoneCustomer)) {
                            //3. Se valida respuesta del correo del tarjetabiente
                            emailCustomer = card.getPersonCustomerId().getEmail();
                            if (emailCustomer.equals(answerEmailCustomer)) {
                                //4. Se valida respuesta de la fecha de nacimiento
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
                    //Se actualiza el estatus de la transacción a RECHAZADA, debido a la validacion del CVV
                    transactionActivateCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionActivateCard.setResponseCode(generateCVVResponse.getResponseCode());
                    try {
                        transactionActivateCard = operationsBD.saveTransactionsManagement(transactionActivateCard, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(generateCVVResponse.getResponseCode(), generateCVVResponse.getResponseMessage());
                }

            } else {
                //Se actualiza el estatus de la transacción a RECHAZADA, debido a que falló la validación de la tarjeta
                transactionActivateCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionActivateCard.setResponseCode(validateCard.getCodigoRespuesta());
                try {
                    transactionActivateCard = operationsBD.saveTransactionsManagement(transactionActivateCard, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionResponse(validateCard.getCodigoRespuesta(), validateCard.getMensajeRespuesta());
            }
        } catch (Exception e) {
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an unexpected error occurred");
        }
    }

    public CardResponse validateDocumentIdentificationCustomer(Card card, String identificationNumber) {
        try {
            if (card == null) {
                return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "The card does not exist in the CMS");
            } else {
                NaturalCustomer naturalCustomer = new NaturalCustomer();
                naturalCustomer = getCardCustomer(card.getPersonCustomerId().getId());
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
            String documentIdentificationNumber, Integer transactionTypeId, Integer channelId, Date transactionDate, String localTimeTransaction, String acquirerTerminalCodeId, Integer acquirerCountryId) {

        int indValidateCardActive = 0;
        String ARQC = null;
        String transactionConcept = "Cambiar estado de la tarjeta";
        Card card = null;
        TransactionsManagement transactionManagement = null;
        String customerIdentificationNumber = "";
        GenerateCVVResponse generateCVVResponse = null;
        try {

            CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);
            if (validateCard.getCard() != null) {
                //Se obtiene la tarjeta asociada
                card = validateCard.getCard();
                //Se obtiene el numero de indentifiación del cliente
                if (card.getPersonCustomerId().getPersonTypeId().getIndNaturalPerson() == true) {
                    customerIdentificationNumber = card.getPersonCustomerId().getNaturalCustomer().getIdentificationNumber();
                } else {
                    customerIdentificationNumber = card.getPersonCustomerId().getLegalCustomer().getIdentificationNumber();
                }
            }

            //Se crea el objeto TransactionManagement Aprobado y se guarda en BD
            Country country = operationsBD.getCountry(String.valueOf(acquirerCountryId), entityManager);
            transactionManagement = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, country.getId(), null, new Date(), transactionTypeId, channelId,
                    null, localTimeTransaction, null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.APPROVED.getId(),
                    cardNumber, cardHolder, CVV, cardDueDate, customerIdentificationNumber, null, null, null, null, null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.CHANGE_CARD_STATUS.getId(), transactionConcept, entityManager);
            try {
                transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //Se obtiene el id de la llave CVK o KVC asociada a la tarjeta a validar
                PlastiCustomizingRequestHasCard plasticRequest = operationsBD.getSecurityKeyByCard(card.getId(), entityManager);
                generateCVVResponse = generateCVV(plasticRequest.getSecurityKeyId().getEncryptedValue(), cardNumber, cardDueDate, Constants.HSM_REQUEST_VALUE_CVV2);
                if (!generateCVVResponse.getCvv().equals(CVV)) {
                    //Colocar asteriscos al cardNumber
                    String cardNumberEncript = operationsBD.transformCardNumber(card.getCardNumber());

                    //Numero de expiracion de la tarjeta
                    String pattern = "MMyy";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    String expirationCardDate = simpleDateFormat.format(card.getExpirationDate());

                    //Se obtiene el nuevo status, el statusUpdateReason y el usuario responsable
                    CardStatus cardStatus = (CardStatus) entityManager.createNamedQuery("CardStatus.findById", CardStatus.class).setParameter("id", newStatusCardId).getSingleResult();
                    StatusUpdateReason statusUpdateReason = (StatusUpdateReason) entityManager.createNamedQuery("StatusUpdateReason.findById", StatusUpdateReason.class).setParameter("id", statusUpdateReasonId).getSingleResult();
                    User user = (User) entityManager.createNamedQuery("User.findById", User.class).setParameter("id", userResponsabibleStatusUpdateId).getSingleResult();

                    //Se actualiza el estado de la tarjeta
                    card.setCardStatusId(cardStatus);
                    card.setStatusUpdateReasonId(statusUpdateReason);
                    card.setUserResponsibleStatusUpdateId(user);
                    card.setObservations(observations);
                    card.setStatusUpdateReasonDate(statusUpdateReasonDate);
                    card.setUpdateDate(statusUpdateReasonDate);
                    entityManager.persist(card);
                    return new TransactionResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.CARD_STATUS_UPDATE.getMessage(), cardNumberEncript, cardStatus.getId(), observations, messageMiddlewareId, transactionManagement.getTransactionNumberIssuer(), transactionManagement.getTransactionDateIssuer());
                } else {
                    //Se actualiza el estatus de la transacción a RECHAZADA, debido a la validacion del CVV
                    transactionManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionManagement.setResponseCode(generateCVVResponse.getResponseCode());
                    try {
                        transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(generateCVVResponse.getResponseCode(), generateCVVResponse.getResponseMessage());
                }
            } else {
                //Se actualiza el transaction Management a rechazado debido a que no paso la validación de la tarjeta
                transactionManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionManagement.setResponseCode(validateCard.getCodigoRespuesta());
                try {
                    transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionResponse(validateCard.getCodigoRespuesta(), validateCard.getMensajeRespuesta());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an unexpected error occurred");
        }

    }

    public OperationCardBalanceInquiryResponse cardBalanceInquiry(String cardNumber, String CVV, String ARQC, String documentIdentificationNumber, Integer transactionTypeId, Integer channelId, Date transactionDate, String localTimeTransaction,
            String acquirerTerminalCodeId, Integer acquirerCountryId, Long messageMiddlewareId, String transactionNumberAcquirer, String cardDueDate, String cardHolder, String pinClear, String terminalId) {

        int indValidateCardActive = 1;
        Utils utils = new Utils();
        TransactionsManagement transactionsManagement = new TransactionsManagement();
        HSMOperations HSMOperation = new HSMOperations();
        Float cardCurrentBalance = 0.00F;
        AccountCard accountCard = null;
        String customerIdentificationNumber = "";
        String maskCardNumber = "";
        Card card = null;
        String transactionConcept = "Consulta de Saldo sin Movimientos";
        String pinELMK = "";
        SecurityKeyType securityKeyType = null;

        try {
            CardResponse cardResponse = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);
            if (cardResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //Se obtiene la tarjeta asociada
                card = cardResponse.getCard();
                //Se obtiene el numero de indentifiación del cliente
                if (card.getPersonCustomerId().getPersonTypeId().getIndNaturalPerson() == true) {
                    customerIdentificationNumber = card.getPersonCustomerId().getNaturalCustomer().getIdentificationNumber();
                } else {
                    customerIdentificationNumber = card.getPersonCustomerId().getLegalCustomer().getIdentificationNumber();
                }
            }

            //Se crea el objeto TransactionManagement Aprobado y se guarda en BD
            Country country = operationsBD.getCountry(String.valueOf(acquirerCountryId), entityManager);
            transactionsManagement = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, country.getId(), transactionNumberAcquirer, transactionDate,
                    TransactionE.CONSULTA.getId(), channelId, null, localTimeTransaction, null, null, null,
                    null, null, null, null, null, null,
                    null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV, cardDueDate, customerIdentificationNumber, null, null, null, null,
                    null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.CARD_BALANCE_INQUIRY.getId(), transactionConcept, entityManager);
            try {
                transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
            } catch (Exception e) {
                return new OperationCardBalanceInquiryResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            if (cardResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                maskCardNumber = operationsBD.maskCCNumber(cardNumber);
                //Se obtiene el tipo de llave
                securityKeyType = operationsBD.getSecurityKeyTypeById(SecurityKeyTypeE.KWP.getId(), entityManager);
                //Se busca la llave de seguridad en la BD en caso de no conseguir se genera la llave y se busca la llave generada
                SecurityKey keyKWP = operationsBD.getSecurityKey(securityKeyType.getId(), Constants.KEY_LENGHT_SINGLE, entityManager);
                if (keyKWP == null) {
                    TransactionResponse generateKey = generateSecurityKey("KWP", "Single");
                    keyKWP = operationsBD.getSecurityKey(securityKeyType.getId(), Constants.KEY_LENGHT_SINGLE, entityManager);
                }
                
                //Se genera el pinBlock      
                String pinBlock = getPinblock(keyKWP.getClearSecurityKey(), pinClear, cardNumber);
                //Transformar el CardNumber en el formato requerido para el servicio translatePINZPKToLMK
                String convertCardNumber = operationsBD.convertCardNumber(cardNumber);

                //Se realizan las validaciones del HSM
                pinELMK = HSMOperations.translatePINZPKToLMK(pinBlock, convertCardNumber, keyKWP.getEncryptedValue(), keyKWP.getSecurityKeySizeId().getName());
                IBMOfSetResponse responseGeneratePinOffSet = (IBMOfSetResponse) generateIBMPinOffSet(pinELMK, cardNumber);
                String pinOffSetHSM = responseGeneratePinOffSet.getIBMoffset();

                //Se valida el PinOffSet generado por la caja HSM con el de la BD
                CardResponse validatePinOffset = validatePinOffset(cardNumber, pinOffSetHSM);
                if (validatePinOffset.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                    //Se valida el CVV ingresado contra el generado por la caja HSM
                    //Se obtiene el id de la llave CVK o KVC asociada a la tarjeta a validar
                    PlastiCustomizingRequestHasCard plasticRequest = operationsBD.getSecurityKeyByCard(card.getId(), entityManager);
                    //Se genera el CVV con la caja HSM
                    GenerateCVVResponse CVVHSMGenerate = (GenerateCVVResponse) generateCVV(plasticRequest.getSecurityKeyId().getEncryptedValue(),card.getCardNumber(),cardDueDate,Constants.HSM_REQUEST_VALUE_CVV2);    
                    if(CVV.trim().equals(CVVHSMGenerate.getCvv().trim())){
                        //Se obtiene el saldo de la tarjeta desde el AccountCard
                        accountCard = getAccountNumberByCard(card.getCardNumber());
                        if (accountCard.getCurrentBalance() != null || accountCard.getCurrentBalance() != 0.00F) {
                            cardCurrentBalance = accountCard.getCurrentBalance();
                        }  
                   } else {
                       //Se actualiza el estatus de la transacción a RECHAZADA, debido a que el CVV no es válido 
                       transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                       transactionsManagement.setResponseCode(ResponseCode.CVV_DIFFERENT.getCode());
                       try {
                           transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                       } catch (Exception e) {
                           return new OperationCardBalanceInquiryResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                       }
                       return new OperationCardBalanceInquiryResponse(ResponseCode.CVV_DIFFERENT.getCode(), ResponseCode.CVV_DIFFERENT.getMessage());
                   }   
                } else {
                    //Fallo en la verificación del PinOffset
                    transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionsManagement.setResponseCode(validatePinOffset.getCodigoRespuesta());
                    try {
                        transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                    } catch (Exception e) {
                        return new OperationCardBalanceInquiryResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new OperationCardBalanceInquiryResponse(validatePinOffset.getCodigoRespuesta(), validatePinOffset.getMensajeRespuesta());
                }
            } else {
                //Fallo en la validación de la tarjeta
                transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionsManagement.setResponseCode(ResponseCode.INVALID_CARD.getCode());
                try {
                    transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                } catch (Exception e) {
                    return new OperationCardBalanceInquiryResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new OperationCardBalanceInquiryResponse(ResponseCode.CARD_NOT_VALIDATE.getCode(), cardResponse.getMensajeRespuesta());
            }
            return new OperationCardBalanceInquiryResponse(ResponseCode.SUCCESS, "SUCCESS", maskCardNumber, cardCurrentBalance, transactionsManagement.getTransactionNumberIssuer(), new Timestamp(new Date().getTime()), messageMiddlewareId);
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
        String conceptTransaction = "Bonificación CMS";
        Integer indFuncionality = 2;
        Currency currencyTransaction = null;
        TransactionsManagement transactionBonificationCMS = null;
        Float bonusAmount = 0.00F;

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
                    totalTransactionsByCardDaily = operationsBD.getTransactionsByCardByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDate(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), entityManager);
                    if (programLoyaltyTransaction.getTotalMaximumTransactions() != null) {
                        if ((totalTransactionsByCardDaily + 1) > programLoyaltyTransaction.getTotalMaximumTransactions()) {
                            addBonus = true;
                        }
                    }
                    totalAmountByCardDaily = operationsBD.getAmountMaxByUserByUserByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDate(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), isTransactionLocal, country.getId(), indFuncionality, entityManager);
                    if (programLoyaltyTransaction.getTotalAmountDaily() != null) {
                        if ((totalAmountByCardDaily + amountTransaction) > Double.parseDouble(programLoyaltyTransaction.getTotalAmountDaily().toString())) {
                            addBonus = true;
                        }
                    }
                    totalAmountByUserMonthly = operationsBD.getAmountMaxByUserByUserByTransactionByProductCurrentDate(cardNumber, EjbUtils.getBeginningDateMonth(new Date()), EjbUtils.getEndingDate(new Date()), transactionTypeId, channelId, ResponseCode.SUCCESS.getCode(), isTransactionLocal, country.getId(), indFuncionality, entityManager);
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
                            if (isTransactionLocal == true) {
                                currencyTransaction = card.getProductId().getDomesticCurrencyId();
                            } else {
                                currencyTransaction = card.getProductId().getInternationalCurrencyId();
                            }
                            try {
                                bonusAmount = programLoyaltyTransaction.getTotalBonificationValue();
                                transactionBonificationCMS = operationsBD.createTransactionsManagement(transactionsManagement, null, null, null, null, null,
                                        TransactionE.BONIFICATION_CMS.getId(), ChannelE.INT.getId(), null, null, null, null, null,
                                        currencyTransaction.getId(), bonusAmount, null, null, null, null,
                                        null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, card.getCardHolder(), card.getSecurityCodeCard(), transactionsManagement.getExpirationCardDate(), null, null, null, null, null,
                                        null, null, null, null, null, ResponseCode.SUCCESS.getCode(), null, DocumentTypeE.BONUS_TRANSACTION_CMS.getId(), conceptTransaction, entityManager);
                                try {
                                    transactionBonificationCMS = operationsBD.saveTransactionsManagement(transactionBonificationCMS, entityManager);
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
                                balanceHistory.setTransactionsManagementId(transactionBonificationCMS.getId());
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
        return new CalculateBonusCardResponse(ResponseCode.SUCCESS, "SUCCESS", bonusAmount);
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
            Long messageMiddlewareId, Date transactionDate, String localTimeTransaction, String acquirerTerminalCodeId, Integer acquirerCountryId, Float amountTransfer, String dateTimeTransmissionTerminal, Date localDateTransaction, String conceptTransaction) {

        TransactionResponse transactionResponse = new TransactionResponse();
        TransactionsManagement transactionsManagement = new TransactionsManagement();
        int indValidateCardActive = 1;
        Float amountCommission = 0.00F;
        Float currentBalanceCardOrigin = 0.00F;
        Float currentBalanceCardDestination = 0.00F;
        AccountCard OriginCardAccount = null;
        AccountCard DestinationCardAccount = null;
        String customerIdentificationNumber = "";
        String customerIdentificationNumberDestinationCard = "";
        String customerNameDestinationCard = "";
        Card cardOrigin = null;
        Card cardDestinate = null;
        Float amountTransferTotal = 0.00F;
        Float currentBalanceOrigin = 0.00F;
        Float currentBalanceDestinate = 0.00F;
        Float bonusAmount = 0.00F;

        try {
            CardResponse cardResponseOrigin = validateCard(cardNumberOrigin, ARQCOrigin, cardHolderOrigin, CVVOrigin, cardDueDateOrigin, indValidateCardActive);
            CardResponse cardResponseDestinate = validateCard(cardNumberDestinate, ARQCDestinate, cardHolderDestinate, CVVDestinate, cardDueDateDestinate, indValidateCardActive);
            cardOrigin = cardResponseOrigin.getCard();
            if (cardOrigin.getPersonCustomerId().getPersonTypeId().getIndNaturalPerson() == true) {
                customerIdentificationNumber = cardOrigin.getPersonCustomerId().getNaturalCustomer().getIdentificationNumber();
            } else {
                customerIdentificationNumber = cardOrigin.getPersonCustomerId().getLegalCustomer().getIdentificationNumber();
            }
            cardDestinate = cardResponseDestinate.getCard();
            if (cardDestinate.getPersonCustomerId().getPersonTypeId().getIndNaturalPerson() == true) {
                customerIdentificationNumberDestinationCard = cardDestinate.getPersonCustomerId().getNaturalCustomer().getIdentificationNumber();
                customerNameDestinationCard = cardDestinate.getPersonCustomerId().getNaturalCustomer().getFirstNames().concat(" ").concat(cardDestinate.getPersonCustomerId().getNaturalCustomer().getLastNames());
            } else {
                customerIdentificationNumberDestinationCard = cardDestinate.getPersonCustomerId().getLegalCustomer().getIdentificationNumber();
                customerNameDestinationCard = cardDestinate.getPersonCustomerId().getLegalCustomer().getEnterpriseName();
            }
            Country country = operationsBD.getCountry(String.valueOf(acquirerCountryId), entityManager);
            transactionsManagement = (TransactionsManagement) operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, country.getId(), null, transactionDate,
                    TransactionE.TRANSFER_BETWEEN_ACCOUNT.getId(), channelId, dateTimeTransmissionTerminal, localTimeTransaction, localDateTransaction, null, null,
                    null, amountTransfer, null, null, null, null,
                    null, StatusTransactionManagementE.APPROVED.getId(), cardNumberOrigin, cardHolderOrigin, CVVOrigin, cardDueDateOrigin, customerIdentificationNumber, null, cardNumberDestinate,
                    customerIdentificationNumberDestinationCard, customerNameDestinationCard, null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId,
                    DocumentTypeE.TRANSFER_BETWEEN_ACCOUNT.getId(), conceptTransaction, entityManager);
            try {
                transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            if (cardResponseOrigin.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                if (cardResponseDestinate.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                    ValidateLimitsResponse validateLimits = getValidateLimits(cardOrigin, TransactionE.TRANSFER_BETWEEN_ACCOUNT.getId(), channelId, acquirerCountryId.toString(), amountTransfer);
                    if (validateLimits.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                        OriginCardAccount = operationsBD.getAccountCardbyCardId(cardOrigin.getId(), entityManager);
                        currentBalanceCardOrigin = OriginCardAccount.getCurrentBalance();
                        DestinationCardAccount = operationsBD.getAccountCardbyCardId(cardDestinate.getId(), entityManager);
                        currentBalanceCardDestination = DestinationCardAccount.getCurrentBalance();
                        transactionResponse = calculateCommisionCMS(cardOrigin, channelId, transactionTypeId, amountTransfer, "1234");
                        if (transactionResponse.getCodigoRespuesta().equals(ResponseCode.COMMISSION_YES_APPLY.getCode())) {
                            amountCommission = transactionResponse.getTransactionCommissionAmount();
                        }
                        amountTransferTotal = amountTransfer + amountCommission;
                        if (currentBalanceCardOrigin == null || currentBalanceCardOrigin < amountTransferTotal) {
                            return new TransactionResponse(ResponseCode.USER_HAS_NOT_BALANCE.getCode(), ResponseCode.USER_HAS_NOT_BALANCE.getMessage());
                        }

                        currentBalanceOrigin = currentBalanceCardOrigin - amountTransferTotal;
                        if (currentBalanceOrigin < cardOrigin.getProductId().getMinimumBalance()) {
                            return new TransactionResponse(ResponseCode.BALANCE_LESS_THAN_ALLOWED.getCode(), ResponseCode.BALANCE_LESS_THAN_ALLOWED.getMessage());
                        }

                        currentBalanceDestinate = currentBalanceCardDestination + amountTransfer;
                        if (currentBalanceDestinate > cardDestinate.getProductId().getMaximumBalance()) {
                            return new TransactionResponse(ResponseCode.BALANCE_GREATER_THAN_ALLOWED.getCode(), ResponseCode.BALANCE_GREATER_THAN_ALLOWED.getMessage());
                        }

                        //Se revisa si la transacción genera una bonificación
                        CalculateBonusCardResponse calculateBonus = calculateBonus(cardOrigin.getCardNumber(), transactionTypeId, channelId, country.getCodeIso3(), amountTransfer, transactionsManagement.getTransactionNumberIssuer());
                        //Si aplica bonificación se obtiene el monto aplicado
                        if (calculateBonus.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                            bonusAmount = calculateBonus.getBonusAmount();
                        }

                        //Actualizar balance History de Origen
                        BalanceHistoryCard balanceHistoryCardOrigin = new BalanceHistoryCard();
                        balanceHistoryCardOrigin.setCardUserId(cardOrigin);
                        balanceHistoryCardOrigin.setCreateDate(new Timestamp(new Date().getTime()));
                        Float currentBalanceSource = currentBalanceCardOrigin - amountTransferTotal;
                        balanceHistoryCardOrigin.setCurrentBalance(currentBalanceSource);
                        balanceHistoryCardOrigin.setPreviousBalance(currentBalanceCardOrigin);
                        balanceHistoryCardOrigin.setTransactionsManagementId(transactionsManagement.getId());
                        entityManager.persist(balanceHistoryCardOrigin);

                        //Actualizar balance History de Destino
                        BalanceHistoryCard balanceHistoryCardDestinate = new BalanceHistoryCard();
                        balanceHistoryCardDestinate.setCardUserId(cardDestinate);
                        balanceHistoryCardOrigin.setCreateDate(new Timestamp(new Date().getTime()));
                        if (currentBalanceCardDestination == null) {
                            balanceHistoryCardDestinate.setCurrentBalance(amountTransfer);
                            balanceHistoryCardDestinate.setPreviousBalance(Constants.PREVIOUS_BALANCE_DESTINATION);
                        } else {
                            Float currentBalanceDestination = currentBalanceCardDestination + amountTransfer;
                            balanceHistoryCardDestinate.setPreviousBalance(currentBalanceCardDestination);
                            balanceHistoryCardDestinate.setCurrentBalance(currentBalanceDestination);
                        }
                        balanceHistoryCardDestinate.setTransactionsManagementId(transactionsManagement.getId());
                        entityManager.persist(balanceHistoryCardDestinate);

                        //Actualizar currentBalance de la tarjeta origen en la tabla accountCard
                        OriginCardAccount.setUpdateDate(new Timestamp(new Date().getTime()));
                        OriginCardAccount.setCurrentBalance(currentBalanceSource);
                        entityManager.merge(OriginCardAccount);

                        //Actualizar currentBalance de la tarjeta destino en la tabla accountCard
                        DestinationCardAccount.setUpdateDate(new Timestamp(new Date().getTime()));
                        DestinationCardAccount.setCurrentBalance(currentBalanceSource);
                        entityManager.merge(DestinationCardAccount);
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
                    //Fallo en la validación de la tarjeta de destino
                    transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionsManagement.setResponseCode(cardResponseDestinate.getCodigoRespuesta());
                    try {
                        transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(cardResponseDestinate.getCodigoRespuesta(), cardResponseDestinate.getMensajeRespuesta());
                }
            } else {
                //Fallo en la validación de la tarjeta de origen
                transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionsManagement.setResponseCode(cardResponseOrigin.getCodigoRespuesta());
                try {
                    transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionResponse(cardResponseOrigin.getCodigoRespuesta(), cardResponseOrigin.getMensajeRespuesta());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "INTERNAL_ERROR");
        }
        return new TransactionResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), cardOrigin.getCardNumber(), cardOrigin.getCardStatusId().getId(), cardOrigin.getCardStatusId().getDescription(), transactionsManagement.getMessageMiddlewareId(),
                transactionsManagement.getTransactionNumberIssuer(), transactionsManagement.getTransactionDateIssuer(), transactionsManagement.getTransactionSequence(), currentBalanceOrigin, amountTransfer, amountCommission, bonusAmount);
    }

    public TransactionResponse viewCardMovements(String cardNumber, String CVV, String cardDueDate, String cardHolder, String documentIdentificationNumber, Integer channelId, Integer transactionTypeId, Long messageMiddlewareId, Date transactionDate,
            String localTimeTransaction, String acquirerTerminalCodeId, Integer acquirerCountryId, String startDate, String endingDate) {

        Card card = null;
        TransactionsManagement transactionManagement = null;
        TransactionsManagementHistory transactionManagementHistory = null;
        String ARQC = null;
        int indValidateCardActive = 1;
        List<TransactionsManagement> transactionsManagementList = new ArrayList<TransactionsManagement>();
        String conceptTransaction = "Consultar Movimientos de la Tarjeta";
        String customerIdentificationNumber = "";
        IsoHsmEquivalence isoHsmEquivalence = null;
        Integer isoItemNumber = 22;
        try {
            CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);

            if (validateCard.getCard() != null) {
                //Se obtiene la tarjeta asociada
                card = validateCard.getCard();
                //Se obtiene el numero de indentifiación del cliente
                if (card.getPersonCustomerId().getPersonTypeId().getIndNaturalPerson() == true) {
                    customerIdentificationNumber = card.getPersonCustomerId().getNaturalCustomer().getIdentificationNumber();
                } else {
                    customerIdentificationNumber = card.getPersonCustomerId().getLegalCustomer().getIdentificationNumber();
                }
            }

            //Se crea el objeto TransactionManagement y se guarda en BD
            Country country = operationsBD.getCountry(String.valueOf(acquirerCountryId), entityManager);
            transactionManagement = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, country.getId(), null, new Date(), transactionTypeId, channelId,
                    null, localTimeTransaction, null, null, null, null, null, null, null, null, null, null, StatusTransactionManagementE.APPROVED.getId(),
                    cardNumber, cardHolder, CVV, cardDueDate, customerIdentificationNumber, null, null, null, null, null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.CHECK_CARD_MOVEMENTS.getId(), conceptTransaction, entityManager);

            try {
                transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //Se valida el CVV Ingresado con el generado por la caja HSM
                //Se obtiene el id de la llave CVK o KVC asociada a la tarjeta a validar
                PlastiCustomizingRequestHasCard plasticRequest = operationsBD.getSecurityKeyByCard(card.getId(), entityManager);
                //Se genera el CVV con la caja HSM
                GenerateCVVResponse CVVHSMGenerate = (GenerateCVVResponse) generateCVV(plasticRequest.getSecurityKeyId().getEncryptedValue(),card.getCardNumber(),cardDueDate,Constants.HSM_REQUEST_VALUE_CVV2);    
                if(CVV.trim().equals(CVVHSMGenerate.getCvv().trim())){
                    //Se le da formato Date a la fecha inicial y fecha final
                    Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
                    Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse(endingDate);
                    //Colocar asteriscos al cardNumber
                    String cardNumberEncript = operationsBD.transformCardNumber(cardNumber);
                    //Se buscan los movimientos de la tarjeta
                    List<TransactionsManagement> transactionsManagement = operationsBD.getCardMovements(cardNumber, date1, date2, entityManager);
                    if (!transactionsManagement.isEmpty() || transactionsManagement.size() != 0) {

                        //Se guarda la lista de respuesta solamente con los campos deseados a mostrar
                        for (TransactionsManagement tm : transactionsManagement) {
                            TransactionsManagement movements = new TransactionsManagement();
                            movements.setTransactionSequence(tm.getTransactionSequence());
                            movements.setTransactionTypeId(tm.getTransactionTypeId());
                            movements.setTransactionDateIssuer(tm.getTransactionDateIssuer());
                            movements.setSettlementTransactionAmount(tm.getSettlementTransactionAmount());
                            movements.setTransactionConcept(tm.getTransactionConcept());
                            if (tm.getTransactionTypeId() == TransactionE.TRANSFER_BETWEEN_ACCOUNT.getId()) {
                                movements.setTransferDestinationCardNumber(tm.getTransferDestinationCardNumber());
                            }
                            transactionsManagementList.add(movements);
                        }

                        //Se obtiene el saldo actual
                        Float currentBalance = getCurrentBalanceCard(card.getId());

                        return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "", cardNumberEncript, card.getCardStatusId().getId(), card.getCardStatusId().getDescription(), messageMiddlewareId.longValue(), transactionManagement.getTransactionNumberIssuer(), currentBalance, date1, date2, transactionsManagement.size(), transactionsManagementList);
                    } else {
                        //Se actualiza el estatus de la transacción a RECHAZADA, debido a que la tarjeta no tiene movimientos
                        transactionManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                        transactionManagement.setResponseCode(ResponseCode.THE_CARD_HAS_NO_MOVEMENTS.getCode());
                        try {
                            transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                        } catch (Exception e) {
                            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                        }
                        return new TransactionResponse(ResponseCode.THE_CARD_HAS_NO_MOVEMENTS.getCode(), ResponseCode.THE_CARD_HAS_NO_MOVEMENTS.getMessage());
                    }
                } else{
                    //Se actualiza el estatus de la transacción a RECHAZADA, debido a que falló la validación del CVV
                    transactionManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionManagement.setResponseCode(ResponseCode.CVV_DIFFERENT.getCode());
                    try {
                        transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(ResponseCode.CVV_DIFFERENT.getCode(), ResponseCode.CVV_DIFFERENT.getMessage());
                }
            } else {
                //Se actualiza el estatus de la transacción a RECHAZADA, debido a que falló la validación de la tarjeta
                transactionManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionManagement.setResponseCode(validateCard.getCodigoRespuesta());
                try {
                    transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionResponse(validateCard.getCodigoRespuesta(), validateCard.getMensajeRespuesta());
            }
        } catch (Exception e) {
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an unexpected error occurred");
        }
    }

    public TransactionResponse cardRecharge(String cardNumber, String cardHolder, String CVV, String cardDueDate, Long messageMiddlewareId,
            Integer transactionTypeId, Integer channelId, Date transactionDate, String localTimeTransaction,
            String acquirerTerminalCodeId, String transactionNumberAcquirer, String acquirerCountryId,
            Float amountRecharge, String conceptTransaction) {

        String ARQC = null;
        TransactionsManagement transactionRechargeCard = null;
        TransactionsManagementHistory transactionHistoryRechargeCard = null;
        int indValidateCardActive = 1;
        Card card = null;
        ValidateLimitsResponse validateLimits = null;
        Float currentBalance = 0.00F;
        AccountCard accountCard = null;
        TransactionResponse commissionCMS = null;
        Float amountCommission = 0.00F;
        Float totalAmountRecharge = 0.00F;
        CalculateBonusCardResponse calculateBonification = null;
        BalanceHistoryCard balanceHistoryCard = null;
        Float newBalance = 0.00F;
        Float bonusAmount = 0.00F;

        try {
            //Se registra la transacción de Recarga de la Tarjeta en la BD
            Country country = operationsBD.getCountry(String.valueOf(acquirerCountryId), entityManager);
            transactionRechargeCard = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, country.getId(), transactionNumberAcquirer, transactionDate,
                    TransactionE.CARD_RECHARGE.getId(), channelId, null, localTimeTransaction, null, null, null,
                    null, amountRecharge, null, null, null, null,
                    null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV, cardDueDate, null, null, null, null, null, null, null,
                    null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.CARD_RECHARGE.getId(), conceptTransaction, entityManager);

            try {
                transactionRechargeCard = operationsBD.saveTransactionsManagement(transactionRechargeCard, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            String cardNumberEncript = operationsBD.transformCardNumber(cardNumber);
            //Se valida la tarjeta
            CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);
            if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //Se obtiene la tarjeta asociada a la transacción
                card = validateCard.getCard();

                //Se validan los límites transaccionales
                validateLimits = getValidateLimits(card, transactionTypeId, channelId, acquirerCountryId.toString(), amountRecharge);
                if (validateLimits.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                    //Se verificar que el monto de la recarga no supere el monto de recarga de la tarjeta
                    if (amountRecharge > card.getMaximumRechargeAmount()) {
                        //Se actualiza el estatus de la transacción a RECHAZADA, debido a que excedió el monto máximo de recarga de la tarjeta
                        transactionRechargeCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                        transactionRechargeCard.setResponseCode(ResponseCode.RECHARGE_AMOUNT_EXCEEDED.getCode());
                        try {
                            transactionRechargeCard = operationsBD.saveTransactionsManagement(transactionRechargeCard, entityManager);
                        } catch (Exception e) {
                            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                        }
                        return new TransactionResponse(ResponseCode.RECHARGE_AMOUNT_EXCEEDED.getCode(), ResponseCode.RECHARGE_AMOUNT_EXCEEDED.getMessage());
                    } else {
                        //Se revisa si la transacción genera una comisión
                        commissionCMS = calculateCommisionCMS(card, channelId, transactionTypeId, amountRecharge, transactionRechargeCard.getTransactionNumberIssuer());
                        if (commissionCMS.getCodigoRespuesta().equals(ResponseCode.COMMISSION_YES_APPLY.getCode())) {
                            amountCommission = commissionCMS.getTransactionCommissionAmount();
                        }
                        //Se obtiene el saldo de la cuenta asociada a la tarjeta
                        accountCard = operationsBD.getAccountCardbyCardId(card.getId(), entityManager);
                        currentBalance = accountCard.getCurrentBalance();
                        totalAmountRecharge = amountRecharge - amountCommission;
                        newBalance = currentBalance + totalAmountRecharge;
                        //Se verifica que el total de la recarga sumado el saldo actual no supere el monto máximo permitido para la cuenta
                        if (newBalance > card.getProductId().getMaximumBalance()) {
                            //Se actualiza el estatus de la transacción a RECHAZADA, debido a que excedió el monto máximo permitido para la cuenta
                            transactionRechargeCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                            transactionRechargeCard.setResponseCode(ResponseCode.BALANCE_GREATER_THAN_ALLOWED.getCode());
                            try {
                                transactionRechargeCard = operationsBD.saveTransactionsManagement(transactionRechargeCard, entityManager);
                            } catch (Exception e) {
                                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                            }
                            return new TransactionResponse(ResponseCode.BALANCE_GREATER_THAN_ALLOWED.getCode(), ResponseCode.BALANCE_GREATER_THAN_ALLOWED.getMessage());
                        } else {
                            //Se actualiza el historial del saldos de la tarjeta en la BD del CMS
                            balanceHistoryCard = operationsBD.createBalanceHistoryCard(card, transactionRechargeCard.getId(), currentBalance, newBalance, entityManager);
                            try {
                                balanceHistoryCard = operationsBD.saveBalanceHistoryCard(balanceHistoryCard, entityManager);
                            } catch (Exception e) {
                                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                            }

                            //Se actualiza el saldo de la cuenta en la BD del CMS
                            accountCard.setCurrentBalance(newBalance);
                            accountCard.setUpdateDate(new Timestamp(new Date().getTime()));
                            entityManager.persist(accountCard);

                            //Se verifica si aplica bonificación
                            CalculateBonusCardResponse calculateBonus = calculateBonus(cardNumber, transactionTypeId, channelId, country.getCodeIso3(), amountRecharge, transactionRechargeCard.getTransactionNumberIssuer());
                            //Si aplica bonificación se obtiene el monto aplicado
                            if (calculateBonus.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                                bonusAmount = calculateBonus.getBonusAmount();
                            }

                            //Se actualiza la transacción
                            if (card.getProductId().getDomesticCurrencyId() != null) {
                                transactionRechargeCard.setSettlementCurrencyTransactionId(card.getProductId().getDomesticCurrencyId().getId());
                            } else {
                                transactionRechargeCard.setSettlementCurrencyTransactionId(card.getProductId().getInternationalCurrencyId().getId());
                            }
                            transactionRechargeCard.setSettlementTransactionAmount(totalAmountRecharge);
                            transactionRechargeCard.setTransactionCommissionAmount(amountCommission);
                            transactionRechargeCard.setResponseCode(ResponseCode.SUCCESS.getCode());
                            transactionRechargeCard.setUpdateDate(new Timestamp(new Date().getTime()));

                            try {
                                transactionRechargeCard = operationsBD.saveTransactionsManagement(transactionRechargeCard, entityManager);
                            } catch (Exception e) {
                                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                            }

                            //Se retorna que la recarga de la tarjeta se realizó satisfactoriamente
                            return new TransactionResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), cardNumberEncript, card.getCardStatusId().getId(), card.getCardStatusId().getDescription(), messageMiddlewareId.longValue(), transactionRechargeCard.getTransactionNumberIssuer(),
                                    transactionRechargeCard.getTransactionDateIssuer(), transactionRechargeCard.getTransactionReference(), newBalance, totalAmountRecharge, amountCommission, bonusAmount);
                        }
                    }
                } else {
                    //Se actualiza el estatus de la transacción a RECHAZADA, debido a que excedió los límites transaccionales
                    transactionRechargeCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionRechargeCard.setResponseCode(validateLimits.getCodigoRespuesta());
                    try {
                        transactionRechargeCard = operationsBD.saveTransactionsManagement(transactionRechargeCard, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(ResponseCode.INVALID_TRANSACTIONAL_LIMITS.getCode(), validateLimits.getMensajeRespuesta());
                }
            } else {
                //Se actualiza el estatus de la transacción a RECHAZADA, debido a que falló la validación de la tarjeta
                transactionRechargeCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionRechargeCard.setResponseCode(validateCard.getCodigoRespuesta());
                try {
                    transactionRechargeCard = operationsBD.saveTransactionsManagement(transactionRechargeCard, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionResponse(ResponseCode.CARD_NOT_VALIDATE.getCode(), validateCard.getMensajeRespuesta());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Se actualiza el estatus de la transacción a RECHAZADA, debido a que ocurrió un error inesperado
            transactionRechargeCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
            transactionRechargeCard.setResponseCode(ResponseCode.INTERNAL_ERROR.getCode());
            try {
                transactionRechargeCard = operationsBD.saveTransactionsManagement(transactionRechargeCard, entityManager);
            } catch (Exception ex) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "INTERNAL_ERROR");
        }
    }

    public CardResponse validatePinOffset(String cardNumber, String pinOffset) {
        Card card;
        CardResponse cardResponse = new CardResponse();
        try {
            card = getCardByCardNumber(cardNumber);
            if (!card.getPinOffset().equals(pinOffset)) {
                return new CardResponse(ResponseCode.PIN_OFFSET_DIFFERENT.getCode(), ResponseCode.PIN_OFFSET_DIFFERENT.getMessage());
            }

        } catch (Exception e) {
            return new CardResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error loading card");
        }
        cardResponse.setCard(card);
        return new CardResponse(ResponseCode.SUCCESS.getCode(), "The PinOffset is correct and exists in the CMS");
    }

    public TransactionResponse cardWithdrawal(String cardNumber, String CVV, String cardDueDate, String cardHolder, String documentIdentificationNumber, Integer channelId, Integer transactionTypeId, Long messageMiddlewareId, Date transactionDate,
            String localTimeTransaction, String acquirerTerminalCodeId, String acquirerCountryId, String transactionNumberAcquirer, Date localDateTransaction, Float withdrawalAmount, String conceptTransaction) {

        Card card = null;
        AccountCard accountCard = null;
        TransactionResponse transactionResponse = new TransactionResponse();
        TransactionsManagement transactionManagement = null;
        BalanceHistoryCard balanceHistoryCardOrigin = null;
        String ARQC = null;
        int indValidateCardActive = 1;
        Float amountCommission = 0.00F;
        Float bonusAmount = 0.00F;
        String cardNumberEncript = operationsBD.transformCardNumber(cardNumber);
        Float currentBalance = 0.00F;
        Float newBalance = 0.00F;
        Float amountWithdrawlTotal = 0.00F;
        ValidateLimitsResponse validateLimits = null;
        String customerIdentificationNumber = "";

        try {
            CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);

            if (validateCard.getCard() != null) {
                //Se obtiene la tarjeta asociada
                card = validateCard.getCard();
                //Se obtiene el numero de indentifiación del cliente
                if (card.getPersonCustomerId().getPersonTypeId().getIndNaturalPerson() == true) {
                    customerIdentificationNumber = card.getPersonCustomerId().getNaturalCustomer().getIdentificationNumber();
                } else {
                    customerIdentificationNumber = card.getPersonCustomerId().getLegalCustomer().getIdentificationNumber();
                }
            }

            //Se crea el objeto TransactionManagement y se guarda en BD
            Country country = operationsBD.getCountry(acquirerCountryId, entityManager);
            transactionManagement = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, country.getId(), transactionNumberAcquirer, transactionDate, TransactionE.RETIRO_DOMESTICO.getId(),
                    channelId, null, localTimeTransaction, localDateTransaction, null, null,
                    null, withdrawalAmount, null, null, null, null, null, StatusTransactionManagementE.APPROVED.getId(), cardNumber,
                    cardHolder, CVV, cardDueDate, customerIdentificationNumber, null, null, null, null, null, null, null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.CARD_WITHDRAWL.getId(), conceptTransaction, entityManager);

            try {
                transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                validateLimits = getValidateLimits(card, TransactionE.RETIRO_DOMESTICO.getId(), channelId, card.getProductId().getIssuerId().getCountryId().getCodeIso3(), withdrawalAmount);
                if (validateLimits.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                    transactionResponse = calculateCommisionCMS(card, channelId, transactionTypeId, withdrawalAmount, "12456");
                    if (transactionResponse.getCodigoRespuesta().equals(ResponseCode.COMMISSION_YES_APPLY.getCode())) {
                        if (transactionResponse.getTransactionCommissionAmount() != null) {
                            amountCommission = transactionResponse.getTransactionCommissionAmount();
                        }
                    }
                    //Se obtiene el saldo de la cuenta asociada a la tarjeta
                    accountCard = operationsBD.getAccountCardbyCardId(card.getId(), entityManager);
                    currentBalance = accountCard.getCurrentBalance();
                    amountWithdrawlTotal = withdrawalAmount + amountCommission;
                    newBalance = currentBalance - amountWithdrawlTotal;
                    //Se verifica que el total del retiro restado al saldo actual no sea menor al monto mínimo permitido para la cuenta
                    if (newBalance < card.getProductId().getMinimumBalance()) {
                        //Se rechaza la transacción por el balance minimo
                        transactionManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                        transactionManagement.setResponseCode(ResponseCode.BALANCE_LESS_THAN_ALLOWED.getCode());
                        try {
                            transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                        } catch (Exception e) {
                            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                        }
                        return new TransactionResponse(ResponseCode.BALANCE_LESS_THAN_ALLOWED.getCode(), ResponseCode.BALANCE_LESS_THAN_ALLOWED.getMessage());
                    } else {

                        if (currentBalance == null || currentBalance < amountWithdrawlTotal) {
                            //Se rechaza la transacción por no tener balance
                            transactionManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                            transactionManagement.setResponseCode(ResponseCode.USER_HAS_NOT_BALANCE.getCode());
                            try {
                                transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                            } catch (Exception e) {
                                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                            }
                            return new TransactionResponse(ResponseCode.USER_HAS_NOT_BALANCE.getCode(), ResponseCode.USER_HAS_NOT_BALANCE.getMessage());
                        }
                    }
                    Float currentBalanceSource = currentBalance - amountWithdrawlTotal;
                    //Actualizar Balance History de la tarjeta
                    balanceHistoryCardOrigin = operationsBD.createBalanceHistoryCard(card, transactionManagement.getId(), currentBalance, currentBalanceSource, entityManager);
                    try {
                        balanceHistoryCardOrigin = operationsBD.saveBalanceHistoryCard(balanceHistoryCardOrigin, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving Balance History");
                    }

                    //Se actualiza el saldo de la cuenta en la BD del CMS
                    accountCard.setUpdateDate(new Timestamp(new Date().getTime()));
                    accountCard.setCurrentBalance(currentBalanceSource);
                    entityManager.persist(accountCard);

                    //Se verifica si aplica bonificación
                    CalculateBonusCardResponse calculateBonus = calculateBonus(cardNumber, transactionTypeId, channelId, country.getCodeIso3(), withdrawalAmount, transactionManagement.getTransactionNumberIssuer());
                    //Si aplica bonificación se obtiene el monto aplicado
                    if (calculateBonus.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                        bonusAmount = calculateBonus.getBonusAmount();
                    }
                    return new TransactionResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), cardNumberEncript, card.getCardStatusId().getId(), card.getCardStatusId().getDescription(), messageMiddlewareId.longValue(), transactionManagement.getTransactionNumberIssuer(),
                            transactionManagement.getTransactionDateIssuer(), transactionManagement.getTransactionReference(), currentBalanceSource, amountWithdrawlTotal, amountCommission, bonusAmount);

                } else {
                    //El cliente excedio los límites transaccionales
                    transactionManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionManagement.setResponseCode(validateLimits.getCodigoRespuesta());
                    try {
                        transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(validateLimits.getCodigoRespuesta(), validateLimits.getMensajeRespuesta());

                }
            } else {
                //Se actualiza el estatus de la transacción a RECHAZADA, debido a que falló la validación de la tarjeta
                transactionManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionManagement.setResponseCode(ResponseCode.INVALID_CARD.getCode());
                try {
                    transactionManagement = operationsBD.saveTransactionsManagement(transactionManagement, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }

                return new TransactionResponse(ResponseCode.INVALID_CARD.getCode(), ResponseCode.INVALID_CARD.getMessage());
            }
        } catch (Exception e) {
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "");
        }

    }

    public TransactionResponse keyChange(String cardNumber, String CVV, String cardDueDate, String cardHolder, String ARQC, Integer channelId, Integer transactionTypeId,
            Long messageMiddlewareId, Date transactionDate, String localTimeTransaction, String acquirerTerminalCodeId, String acquirerCountryId, String newPinClear, String terminalId) {

        TransactionsManagement transactionsManagement = new TransactionsManagement();
        int indValidateCardActive = 1;
        TransactionResponse transactionResponse = null;
        String pinELMK = "";
        Card card = null;
        String customerIdentificationNumber = "";
        String conceptTransaction = "Cambio de Pin de tarjeta";
        GenerateCVVResponse generateCVVResponse = null;
        try {
            CardResponse cardResponse = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);
            if (cardResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //Se obtiene la tarjeta asociada
                card = cardResponse.getCard();
                //Se obtiene el numero de indentifiación del cliente
                if (card.getPersonCustomerId().getPersonTypeId().getIndNaturalPerson() == true) {
                    customerIdentificationNumber = card.getPersonCustomerId().getNaturalCustomer().getIdentificationNumber();
                } else {
                    customerIdentificationNumber = card.getPersonCustomerId().getLegalCustomer().getIdentificationNumber();
                }
            }
            //Buscar pais
            Country country = operationsBD.getCountry(acquirerCountryId, entityManager);
            //Se guarda el TransactionsManagement
            transactionsManagement = (TransactionsManagement) operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, country.getId(), null, transactionDate,
                    TransactionE.KEY_CHANGE.getId(), channelId, null, localTimeTransaction, null, null, null,
                    null, null, null, null, null, null,
                    null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV, cardDueDate, customerIdentificationNumber, null, null, null, null, null, null,
                    null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.KEY_CHANGE.getId(), conceptTransaction, entityManager);
            try {
                transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }
            if (cardResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //Se obtiene la llave de seguridad KWP
                SecurityKeyType securityKeyType = operationsBD.getSecurityKeyTypeById(SecurityKeyTypeE.KWP.getId(), entityManager);
                //Busqueda de la llave de seguridad
                SecurityKey securityKey = operationsBD.getSecurityKey(securityKeyType.getId(), Constants.KEY_LENGHT_SINGLE, entityManager);
                String pinBlock = getPinblock(securityKey.getClearSecurityKey(), newPinClear, card.getCardNumber());
                String pan = operationsBD.convertCardNumber(cardNumber);                
                HSMOperations hSMOperations = new HSMOperations();
                //Falta cambiar el securityKey                    
                pinELMK = translatePINZPKToLMK(pinBlock, pan, securityKey.getClearSecurityKey(), securityKey.getSecurityKeySizeId().getName());
                //pinELMK = hSMOperations.translatePINZPKToLMK(pinBlock, pan, "B563D6ABD6692220", Constants.SECURITY_KEY_TYPE_SINGLE);
                com.alodiga.hsm.response.IBMOfSetResponse IBMOfSetResponse = hSMOperations.generateIBMPinOffSet(pinELMK, pan);
                transactionResponse = validatePropertiesKey(card, newPinClear, channelId, true);
                //Se obtiene el id de la llave CVK o KVC asociada a la tarjeta a validar
                PlastiCustomizingRequestHasCard plasticRequest = operationsBD.getSecurityKeyByCard(card.getId(), entityManager);
                generateCVVResponse = generateCVV(plasticRequest.getSecurityKeyId().getEncryptedValue(), cardNumber, cardDueDate, Constants.HSM_REQUEST_VALUE_CVV2);
                if (!generateCVVResponse.getCvv().equals(CVV)) {
                    if (transactionResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                        if (IBMOfSetResponse.getResponseCode().equals(ResponseCode.SUCCESS.getCode())) {
                            card.setPinOffset(IBMOfSetResponse.getIBMoffset());
                            card.setUpdateDate(new Timestamp(new Date().getTime()));
                            CardKeyHistory cardKeyHistory = new CardKeyHistory();
                            cardKeyHistory.setCardId(card);
                            cardKeyHistory.setPreviousPinOffset(card.getPinOffset());
                            cardKeyHistory.setCreateDate(new Date());
                            entityManager.merge(cardKeyHistory);
                            entityManager.merge(card);
                        } else {
                            //Fallo en la generación del pinOffset
                            transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                            transactionsManagement.setResponseCode(transactionResponse.getCodigoRespuesta());
                            try {
                                transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                            } catch (Exception e) {
                                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                            }
                            return new TransactionResponse(IBMOfSetResponse.getResponseCode(), IBMOfSetResponse.getResponseMessage());

                        }
                    } else {
                        //Fallo en la validación de las propiedades
                        transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                        transactionsManagement.setResponseCode(transactionResponse.getCodigoRespuesta());
                        try {
                            transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                        } catch (Exception e) {
                            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                        }
                        return new TransactionResponse(transactionResponse.getCodigoRespuesta(), transactionResponse.getMensajeRespuesta());
                    }
                } else {
                    //Se actualiza el estatus de la transacción a RECHAZADA, debido a la validacion del CVV
                    transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionsManagement.setResponseCode(generateCVVResponse.getResponseCode());
                    try {
                        transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(generateCVVResponse.getResponseCode(), generateCVVResponse.getResponseMessage());
                }

            } else {
                //Fallo en la validación de la tarjeta
                transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionsManagement.setResponseCode(cardResponse.getCodigoRespuesta());
                try {
                    transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionResponse(cardResponse.getCodigoRespuesta(), cardResponse.getMensajeRespuesta());

            }

        } catch (Exception e) {
            e.printStackTrace();
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "INTERNAL_ERROR");
        }

        return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "SUCCESS");

    }

    public TransactionResponse validatePropertiesKey(Card card, String pinOffset, Integer channelId, boolean typeTrasaction) {
        int milisecondsByDay = 86400000;
        try {

            card = getCardByCardNumber(card.getCardNumber());

            KeyProperties keyProperties = operationsBD.getKeyPropertiesByProductIdByChanelId(card.getProductId().getId(), channelId, entityManager);

            if (!keyProperties.getKeyLength().equals(pinOffset.length())) {
                return new TransactionResponse(ResponseCode.DIFFERENT_PIN_OFFSET_LENGTH.getCode(), ResponseCode.DIFFERENT_PIN_OFFSET_LENGTH.getMessage());
            }

            Integer experitationDaysProperties = keyProperties.getExpirationDays();

            if (keyProperties != null) {
                if (typeTrasaction) {

                    CardKeyHistoryListResponse cardKeyHistoryListResponse = operationsBD.getCardKeyHistoryByCardId(card.getId(), keyProperties.getTotalPreviousKeys(), entityManager);
                    Date creationDate = cardKeyHistoryListResponse.getCardKeyHistorys().get(0).getCreateDate();
                    Date currentDate = new Date(System.currentTimeMillis());
                    int dias = (int) ((currentDate.getTime() - creationDate.getTime()) / milisecondsByDay);
                    for (CardKeyHistory keyHistory : cardKeyHistoryListResponse.getCardKeyHistorys()) {
                        if (keyHistory.getPreviousPinOffset().equals(pinOffset)) {
                            return new TransactionResponse(ResponseCode.DIFFERENT_KEY.getCode(), ResponseCode.DIFFERENT_KEY.getMessage());
                        }
                        if (dias >= experitationDaysProperties) {
                            return new TransactionResponse(ResponseCode.EXPIRED_KEY.getCode(), ResponseCode.EXPIRED_KEY.getMessage());
                        }
                    }

                }
                if (operationsBD.isNumeric(pinOffset) == true) {
                    if (operationsBD.testContinuous(pinOffset)) {
                        if (!keyProperties.getIndContinuousCharacters()) {
                            return new TransactionResponse(ResponseCode.CONTINUOUS_KEY.getCode(), ResponseCode.CONTINUOUS_KEY.getMessage());
                        }
                    }
                    if (operationsBD.testConsecutive(pinOffset)) {
                        if (!keyProperties.getIndConsecutiveEqualCharacters()) {
                            return new TransactionResponse(ResponseCode.CONSECUTIVE_KEY.getCode(), ResponseCode.CONSECUTIVE_KEY.getMessage());
                        }

                    }
                } else {
                    return new TransactionResponse(ResponseCode.NO_NUMBER.getCode(), ResponseCode.NO_NUMBER.getMessage());
                }
            } else {
                return new TransactionResponse(ResponseCode.HAS_NO_PROPERTIES.getCode(), ResponseCode.HAS_NO_PROPERTIES.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "INTERNAL_ERROR");
        }

        return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "SUCCESS");
    }

    public TransactionPurchageResponse cardPurchage(String cardNumber, String cardHolder, String CVV, String cardDueDate, Long messageMiddlewareId,
            Integer transactionTypeId, Integer channelId, Date transactionDate, String localTimeTransaction,
            String acquirerTerminalCodeId, String transactionNumberAcquirer, String acquirerCountryId,
            Float amountPurchage, String pinBlock, String ARQC, String terminalId, String entryMode, String schemeEMV,
            String seqNumber, String atc, String unpredictableNumber, String transactionData, String tradeName) {
           
        TransactionsManagement transactionPurchageCard = null;
        TransactionsManagementHistory transactionHistoryRechargeCard = null;
        int indValidateCardActive = 1;
        Card card = null;
        ValidateLimitsResponse validateLimits = null;
        Float currentBalance = 0.00F;
        AccountCard accountCard = null;
        TransactionResponse commissionCMS = null;
        Float amountCommission = 0.00F;
        Float totalAmountPurchage = 0.00F;
        CalculateBonusCardResponse calculateBonification = null;
        GenerateCVVResponse generateCVVResponse = null;
        BalanceHistoryCard balanceHistoryCard = null;
        Float newBalance = 0.00F;
        String arpc = "";
        String customerIdentificationNumber = "";
        String pinELMK = "";
        String conceptTransaction = "Compra POS - ";
        IsoHsmEquivalence isoHsmEquivalence = null;

        try {
            conceptTransaction.concat(tradeName);
            //Buscar pais
            Country country = operationsBD.getCountry(acquirerCountryId, entityManager);
            //Se valida la tarjeta
            CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);
            if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //Se obtiene la tarjeta asociada
                card = validateCard.getCard();
                //Se obtiene el numero de indentifiación del cliente
                if (card.getPersonCustomerId().getPersonTypeId().getIndNaturalPerson() == true) {
                    customerIdentificationNumber = card.getPersonCustomerId().getNaturalCustomer().getIdentificationNumber();
                } else {
                    customerIdentificationNumber = card.getPersonCustomerId().getLegalCustomer().getIdentificationNumber();
                }
            }
            //Se registra la transacción de Compra con Tarjeta en la BD
            transactionPurchageCard = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, country.getId(), transactionNumberAcquirer, transactionDate,
                    TransactionE.COMPRA_DOMESTICA_PIN.getId(), channelId, null, localTimeTransaction, null, null, null,
                    null, amountPurchage, null, null, null, null,
                    null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV, cardDueDate, customerIdentificationNumber, null, null, null, null, null, null,
                    null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.CARD_PURCHAGE.getId(), conceptTransaction, entityManager);

            try {
                transactionPurchageCard = operationsBD.saveTransactionsManagement(transactionPurchageCard, entityManager);
            } catch (Exception e) {
                return new TransactionPurchageResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }
            if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //Se obtiene la llave de seguridad KWP
                SecurityKeyType securityKeyType = operationsBD.getSecurityKeyTypeById(SecurityKeyTypeE.KWP.getId(), entityManager);
                //Busqueda de la llave de seguridad
                SecurityKey securityKey = operationsBD.getSecurityKey(securityKeyType.getId(), Constants.KEY_LENGHT_SINGLE, entityManager);
                String pan = operationsBD.convertCardNumber(cardNumber);
                HSMOperations hSMOperations = new HSMOperations();
                pinELMK = translatePINZPKToLMK(pinBlock, pan, securityKey.getClearSecurityKey(), securityKey.getSecurityKeySizeId().getName());
                //pinELMK = hSMOperations.translatePINZPKToLMK(pinBlock, pan, "B563D6ABD6692220", Constants.SECURITY_KEY_TYPE_SINGLE);
                com.alodiga.hsm.response.IBMOfSetResponse IBMOfSetResponse = hSMOperations.generateIBMPinOffSet(pinELMK, pan);
                //Se valida el pinOffset
                CardResponse validatePinOffset = validatePinOffset(cardNumber, IBMOfSetResponse.getIBMoffset());
                //Se obtiene el codigo del tipo de CVV que se va a validar contra la caja HSM mediante el mensaje ISO 
                isoHsmEquivalence = operationsBD.getHSMRequestValue(entryMode, 22, entityManager);
                //Se obtiene el id de la llave CVK o KVC asociada a la tarjeta a validar
                PlastiCustomizingRequestHasCard plasticRequest = operationsBD.getSecurityKeyByCard(card.getId(), entityManager);
                generateCVVResponse = generateCVV(plasticRequest.getSecurityKeyId().getEncryptedValue(), cardNumber, cardDueDate, isoHsmEquivalence.getHsmRequestValue());
                if (!generateCVVResponse.getCvv().equals(CVV)) {
                    if (validatePinOffset.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                        //Se validan los límites transaccionales
                        validateLimits = getValidateLimits(card, transactionTypeId, channelId, acquirerCountryId.toString(), amountPurchage);
                        if (validateLimits.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                            //Se revisa si la transacción genera una comisión
                            commissionCMS = calculateCommisionCMS(card, channelId, transactionTypeId, amountPurchage, transactionNumberAcquirer);
                            if (commissionCMS.getCodigoRespuesta().equals(ResponseCode.COMMISSION_YES_APPLY.getCode())) {
                                amountCommission = commissionCMS.getTransactionCommissionAmount();
                            }
                            //Se obtiene el saldo de la cuenta asociada a la tarjeta
                            accountCard = operationsBD.getAccountCardbyCardId(card.getId(), entityManager);
                            currentBalance = accountCard.getCurrentBalance();
                            totalAmountPurchage = amountPurchage + amountCommission;
                            newBalance = currentBalance - totalAmountPurchage;
                            //Se verifica que el total de la compra no supere el saldo actual
                            if (newBalance < 0) {
                                //Se actualiza el estatus de la transacción a RECHAZADA, debido a que la compra excedió el limite disponible
                                transactionPurchageCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                                transactionPurchageCard.setResponseCode(ResponseCode.BALANCE_GREATER_THAN_ALLOWED.getCode());
                                try {
                                    transactionPurchageCard = operationsBD.saveTransactionsManagement(transactionPurchageCard, entityManager);
                                } catch (Exception e) {
                                    return new TransactionPurchageResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                                }
                                return new TransactionPurchageResponse(ResponseCode.BALANCE_GREATER_THAN_ALLOWED.getCode(), ResponseCode.BALANCE_GREATER_THAN_ALLOWED.getMessage());
                            } else {
                                //Verificar si la transacción genera bonificación
                                calculateBonification = calculateBonus(card.getCardNumber(), transactionTypeId, channelId, acquirerCountryId.toString(), amountPurchage, transactionPurchageCard.getTransactionNumberIssuer());

                                //Se actualiza el historial del saldos de la tarjeta en la BD del CMS
                                balanceHistoryCard = operationsBD.createBalanceHistoryCard(card, transactionPurchageCard.getId(), currentBalance, newBalance, entityManager);
                                try {
                                    balanceHistoryCard = operationsBD.saveBalanceHistoryCard(balanceHistoryCard, entityManager);
                                } catch (Exception e) {
                                    return new TransactionPurchageResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                                }

                                //Se actualiza el saldo de la cuenta en la BD del CMS
                                accountCard.setCurrentBalance(newBalance);
                                accountCard.setUpdateDate(new Timestamp(new Date().getTime()));
                                entityManager.persist(accountCard);

                                //Se actualiza la transacción
                                transactionPurchageCard.setSettlementCurrencyTransactionId(card.getProductId().getDomesticCurrencyId().getId());
                                transactionPurchageCard.setSettlementTransactionAmount(totalAmountPurchage);
                                transactionPurchageCard.setResponseCode(ResponseCode.CARD_PURCHAGE_SUCCESS.getCode());
                                transactionPurchageCard.setUpdateDate(new Timestamp(new Date().getTime()));
                                try {
                                    transactionPurchageCard = operationsBD.saveTransactionsManagement(transactionPurchageCard, entityManager);
                                } catch (Exception e) {
                                    return new TransactionPurchageResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                                }

                                //Se retorna que la compra de la tarjeta se realizó satisfactoriamente
                                return new TransactionPurchageResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), arpc);
                            }
                        } else {
                            //Se actualiza el estatus de la transacción a RECHAZADA, debido a que excedió los límites transaccionales
                            transactionPurchageCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                            transactionPurchageCard.setResponseCode(validateLimits.getCodigoRespuesta());
                            try {
                                transactionPurchageCard = operationsBD.saveTransactionsManagement(transactionPurchageCard, entityManager);
                            } catch (Exception e) {
                                return new TransactionPurchageResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                            }
                            return new TransactionPurchageResponse(validateLimits.getCodigoRespuesta(), validateLimits.getMensajeRespuesta());
                        }
                    } else {
                        //Se actualiza el estatus de la transacción a RECHAZADA, debido a la validacion del pinOffset
                        transactionPurchageCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                        transactionPurchageCard.setResponseCode(validatePinOffset.getCodigoRespuesta());
                        try {
                            transactionPurchageCard = operationsBD.saveTransactionsManagement(transactionPurchageCard, entityManager);
                        } catch (Exception e) {
                            return new TransactionPurchageResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                        }
                        return new TransactionPurchageResponse(validatePinOffset.getCodigoRespuesta(), validatePinOffset.getMensajeRespuesta());
                    }
                } else {
                    //Se actualiza el estatus de la transacción a RECHAZADA, debido a la validacion del CVV
                    transactionPurchageCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionPurchageCard.setResponseCode(generateCVVResponse.getResponseCode());
                    try {
                        transactionPurchageCard = operationsBD.saveTransactionsManagement(transactionPurchageCard, entityManager);
                    } catch (Exception e) {
                        return new TransactionPurchageResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionPurchageResponse(generateCVVResponse.getResponseCode(), generateCVVResponse.getResponseMessage());
                }

            } else {
                //Se actualiza el estatus de la transacción a RECHAZADA, debido a que falló la validación de la tarjeta
                transactionPurchageCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionPurchageCard.setResponseCode(validateCard.getCodigoRespuesta());
                try {
                    transactionPurchageCard = operationsBD.saveTransactionsManagement(transactionPurchageCard, entityManager);
                } catch (Exception e) {
                    return new TransactionPurchageResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionPurchageResponse(validateCard.getCodigoRespuesta(), validateCard.getMensajeRespuesta());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TransactionPurchageResponse(ResponseCode.INTERNAL_ERROR.getCode(), "INTERNAL_ERROR");
        }
    }

    public TransactionResponse reverseWalletWithdrawal(String cardNumber, String CVV, String cardDueDate, String cardHolder, String ARQC, Integer channelId, Integer transactionTypeId, Long messageMiddlewareId, Date transactionDate,
            String localTimeTransaction, String acquirerTerminalCodeId, Integer acquirerCountryId, String transactionNumber, String transactionSequence, String conceptTransaction) {
        Card card = null;
        TransactionsManagement transactionsManagementWithdrawal = null;
        TransactionsManagement transactionsManagement = null;
        int indValidateCardActive = 1;
        Float currentBalance = 0.00F;
        Float newBalance = 0.00F;
        BalanceHistoryCard balanceHistoryCard = null;
        try {
            //Se crea la transaction
            transactionsManagement = (TransactionsManagement) operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, null, transactionDate,
                    TransactionE.REVERSE_WITHDRAWAL.getId(), channelId, null, localTimeTransaction, null, null, null,
                    null, null, null, null, null, null,
                    null, null, cardNumber, cardHolder, CVV, cardDueDate, null, null, null, null, null, null, null,
                    null, null, null, null, messageMiddlewareId, DocumentTypeE.REVERSE_WITHDRAWAL.getId(), conceptTransaction, entityManager);
            try {
                transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            CardResponse cardResponse = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);
            if (cardResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                card = getCardByCardNumber(cardNumber);

                //Se busca la transacción para hacer el reverso
                transactionsManagementWithdrawal = operationsBD.getTransactionByNumberAndSequence(transactionNumber, transactionSequence, entityManager);
                if (transactionsManagementWithdrawal != null) {
                    //Se cancela la transaction original
                    transactionsManagementWithdrawal.setStatusTransactionManagementId(StatusTransactionManagementE.CANCELLED.getId());
                    transactionsManagementWithdrawal.setResponseCode(ResponseCode.CANCEL.getCode());
                    transactionsManagementWithdrawal.setUpdateDate(new Timestamp(new Date().getTime()));
                    try {
                        transactionsManagementWithdrawal = operationsBD.saveTransactionsManagement(transactionsManagementWithdrawal, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }

                    //Se ejecuta la transaction del reverso
                    transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.APPROVED.getId());
                    transactionsManagement.setResponseCode(ResponseCode.SUCCESS.getCode());
                    transactionsManagement.setSettlementTransactionAmount(transactionsManagementWithdrawal.getSettlementTransactionAmount());
                    try {
                        transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }

                    //Se obtiene el balance actual del usuario y se le suma el reverso de la operación
                    currentBalance = getCurrentBalanceCard(card.getId());
                    newBalance = currentBalance + transactionsManagementWithdrawal.getSettlementTransactionAmount();

                    //Se actualiza el balance history y saldo de la cuenta
                    balanceHistoryCard = operationsBD.createBalanceHistoryCard(card, transactionsManagement.getId(), currentBalance, newBalance, entityManager);
                    try {
                        balanceHistoryCard = operationsBD.saveBalanceHistoryCard(balanceHistoryCard, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving Balance History");
                    }

                    AccountCard accountNumber = getAccountNumberByCard(cardNumber);
                    AccountCard accountCard = entityManager.find(AccountCard.class, accountNumber.getId());
                    accountCard.setUpdateDate(new Timestamp(new Date().getTime()));
                    accountCard.setCurrentBalance(newBalance);
                    entityManager.merge(accountCard);

                    return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "SUCCESS");

                } else {
                    //No se encontro ninguna transacción para hacer el reverso
                    transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionsManagement.setResponseCode(ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getCode());
                    try {
                        transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getCode(), ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getMessage());
                }
            } else {
                //La tarjeta no es valida
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

    }

    public TransactionResponse reverseCardRecharge(String cardNumber, String cardHolder, String CVV, String cardDueDate, Long messageMiddlewareId,
            Integer transactionTypeId, Integer channelId, Date transactionDate, String localTimeTransaction,
            String acquirerTerminalCodeId, String transactionNumberAcquirer, Integer acquirerCountryId,
            Float amountReverseRecharge, String transactionNumberCardRecharge, String sequenceTransactionCardRecharge, String conceptTransaction) {

        String ARQC = null;
        TransactionsManagement transactionReverseRechargeCard = null;
        TransactionsManagement transactionRechargeCardOriginal = null;
        TransactionsManagement transactionReverseComissionRechargeCard = null;
        TransactionsManagement transactionsReverseManagementBonification = null;
        int indValidateCardActive = 1;
        Card card = null;
        Float currentBalance = 0.00F;
        AccountCard accountCard = null;
        BalanceHistoryCard balanceHistoryCard = null;
        Float newBalance = 0.00F;
        String customerIdentificationNumber = "";

        try {
            CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);
            if (validateCard.getCard() != null) {
                //Se obtiene la tarjeta asociada
                card = validateCard.getCard();
                //Se obtiene el numero de indentifiación del cliente
                if (card.getPersonCustomerId().getPersonTypeId().getIndNaturalPerson() == true) {
                    customerIdentificationNumber = card.getPersonCustomerId().getNaturalCustomer().getIdentificationNumber();
                } else {
                    customerIdentificationNumber = card.getPersonCustomerId().getLegalCustomer().getIdentificationNumber();
                }
            }

            //Se busca el país asociado al adquiriente
            Country country = operationsBD.getCountry(acquirerCountryId.toString(), entityManager);

            //Se obtiene la transacción de recarga a reversar
            transactionRechargeCardOriginal = operationsBD.getTransactionByNumberAndSequence(transactionNumberCardRecharge, sequenceTransactionCardRecharge, entityManager);

            //Se registra la transacción de Reverso de Recarga de la Tarjeta en la BD
            transactionReverseRechargeCard = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, country.getId(), transactionNumberAcquirer, transactionDate,
                    TransactionE.REVERSE_CARD_RECHARGE.getId(), channelId, null, localTimeTransaction, null, null, null,
                    null, amountReverseRecharge, null, null, null, null,
                    null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV, cardDueDate, null, null, null, null, null, null, null,
                    null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.REVERSE_CARD_RECHARGE.getId(), conceptTransaction, entityManager);

            //Se actualiza la referencia de la transacción original en la transacción de reverso 
            transactionReverseRechargeCard.setTransactionReference(transactionRechargeCardOriginal.getTransactionNumberIssuer());

            //Se guarda la transacción de reverso de recarga
            try {
                transactionReverseRechargeCard = operationsBD.saveTransactionsManagement(transactionReverseRechargeCard, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            //Se valida la tarjeta
            if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {

                if (transactionRechargeCardOriginal != null) {
                    //Se cancela la transacción de recarga
                    transactionRechargeCardOriginal.setStatusTransactionManagementId(StatusTransactionManagementE.CANCELLED.getId());
                    transactionRechargeCardOriginal.setUpdateDate(new Timestamp(new Date().getTime()));
                    try {
                        transactionRechargeCardOriginal = operationsBD.saveTransactionsManagement(transactionRechargeCardOriginal, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }

                    //Se actualiza el saldo del cliente
                    currentBalance = getCurrentBalanceCard(card.getId());
                    newBalance = currentBalance - transactionRechargeCardOriginal.getSettlementTransactionAmount();

                    //Se actualiza el balance history y saldo de la cuenta
                    balanceHistoryCard = operationsBD.createBalanceHistoryCard(card, transactionReverseRechargeCard.getId(), currentBalance, newBalance, entityManager);
                    try {
                        balanceHistoryCard = operationsBD.saveBalanceHistoryCard(balanceHistoryCard, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving Balance History");
                    }

                    AccountCard accountNumber = getAccountNumberByCard(cardNumber);
                    accountCard = entityManager.find(AccountCard.class, accountNumber.getId());
                    accountCard.setUpdateDate(new Timestamp(new Date().getTime()));
                    accountCard.setCurrentBalance(newBalance);
                    entityManager.merge(accountCard);

                    //Se revisa si la transacción de recarga generó comisión
                    if (transactionRechargeCardOriginal.getTransactionCommissionAmount() != null) {
                        if (transactionRechargeCardOriginal.getTransactionCommissionAmount() > 0) {
                            conceptTransaction = "Reverso Comisión CMS";
                            //Se registra la transacción de reverso de la comission
                            transactionReverseComissionRechargeCard = (TransactionsManagement) operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, null, transactionDate,
                                    TransactionE.REVERSE_COMISSION.getId(), channelId, null, localTimeTransaction, null, null, null,
                                    null, null, null, null, null, null,
                                    null, null, cardNumber, cardHolder, CVV, cardDueDate, null, null, null, null, null, null, null,
                                    null, null, null, null, messageMiddlewareId, DocumentTypeE.REVERSE_COMISSION.getId(), conceptTransaction, entityManager);

                            //Se realiza el reverso de la comisión generada por le recarga
                            TransactionResponse transactionResponse = reverseComission(transactionNumberCardRecharge, transactionReverseComissionRechargeCard, card);
                            if (!transactionResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                                return transactionResponse;
                            }
                        }
                    }

                    //Se revisa si la transacción de recarga generó una bonificación
                    transactionsReverseManagementBonification = operationsBD.getTransactionsManagementByTransactionReference(transactionNumberCardRecharge, TransactionE.BONIFICATION_CMS.getId(), entityManager);
                    if (transactionsReverseManagementBonification != null) {
                        TransactionResponse transactionResponse = reverseBonification(transactionNumberCardRecharge, transactionsReverseManagementBonification, card);
                        if (!transactionResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                            return transactionResponse;
                        }
                    }

                    //Se actualiza la transacción de recarga para indicar que fué reversada
                    transactionRechargeCardOriginal.setIndReverseTransaction(Boolean.TRUE);
                    try {
                        transactionRechargeCardOriginal = operationsBD.saveTransactionsManagement(transactionRechargeCardOriginal, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }

                    //Se retorna mensaje de éxito para la transacción de reverso de recarga
                    return new TransactionResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage());
                } else {
                    //Se generó un error inesperado, no se encontró la transacción de recarga a reversar
                    transactionReverseRechargeCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionReverseRechargeCard.setResponseCode(ResponseCode.INTERNAL_ERROR.getCode());
                    try {
                        transactionReverseRechargeCard = operationsBD.saveTransactionsManagement(transactionReverseRechargeCard, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getCode(), ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getMessage());
                }
            } else {
                //Se actualiza el estatus de la transacción a RECHAZADA, debido a que falló la validación de la tarjeta
                transactionReverseRechargeCard.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionReverseRechargeCard.setResponseCode(validateCard.getCodigoRespuesta());
                try {
                    transactionReverseRechargeCard = operationsBD.saveTransactionsManagement(transactionReverseRechargeCard, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionResponse(validateCard.getCodigoRespuesta(), validateCard.getMensajeRespuesta());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "INTERNAL_ERROR");
        }
    }

    public TransactionResponse saveRegisterPin(String cardNumber, String CVV, String ARQC, Integer transactionTypeId, Integer channelId, Date transactionDate, String localTimeTransaction, String acquirerTerminalCodeId, Integer acquirerCountryId, Long messageMiddlewareId, String cardDueDate, String cardHolder, String pinClear, String terminalId) {

        Utils utils = new Utils();
        TransactionResponse transactionResponse = null;
        TransactionsManagement transactionsManagement = null;
        int indValidateCardActive = 1;
        String conceptTransaction = "Registro de Pin de Tarjeta";
        SecurityKey securityKey = null;
        String pinBlock = "";
        String responsePinELMK;
        String convertCard = "";
        SecurityKeyType securityKeyType = null;
        GenerateCVVResponse CVVHSMGenerate = null;
        PlastiCustomizingRequestHasCard plasticRequest = null;
        try {
            //Se crea la transaction
            transactionsManagement = (TransactionsManagement) operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, null, transactionDate,
                    TransactionE.KEY_REGISTER.getId(), channelId, null, localTimeTransaction, null, null, null,
                    null, null, null, null, null, null,
                    null, null, cardNumber, cardHolder, CVV, cardDueDate, null, null, null, null, null, null, null,
                    null, null, null, null, messageMiddlewareId, DocumentTypeE.KEY_REGISTER.getId(), conceptTransaction, entityManager);
            try {
                transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            CardResponse cardResponse = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);
            if (cardResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                Card card = cardResponse.getCard();
                transactionResponse = validatePropertiesKey(card, pinClear, channelId, false);
                if (transactionResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                    convertCard = operationsBD.convertCardNumber(cardNumber);
                    //Se obtiene la llave de seguridad KEK
                    securityKeyType = operationsBD.getSecurityKeyTypeById(SecurityKeyTypeE.KWP.getId(), entityManager);
                    securityKey = operationsBD.getSecurityKey(securityKeyType.getId(), Constants.KEY_LENGHT_SINGLE, entityManager);
                    //Se genera el pinBlock
                    pinBlock = getPinblock(securityKey.getClearSecurityKey(), pinClear, cardNumber);
                    //Se genera el pinELMK
                    responsePinELMK = translatePINZPKToLMK(pinBlock, convertCard, securityKey.getEncryptedValue(), securityKey.getSecurityKeySizeId().getName());
                    //Se genera el pinOffset y se guarda en la BD
                    IBMOfSetResponse ibmOfSetResponse = generateIBMPinOffSet(responsePinELMK, convertCard);
                    if (ibmOfSetResponse.getResponseCode().equals(ConstantResponse.SUCESSFULL_RESPONSE_CODE)) {
                        //Se obtiene el id de la llave CVK o KVC asociada a la tarjeta a validar
                        plasticRequest = operationsBD.getSecurityKeyByCard(card.getId(), entityManager);
                        //Se genera el CVV con la caja HSM
                        CVVHSMGenerate = (GenerateCVVResponse) generateCVV(plasticRequest.getSecurityKeyId().getEncryptedValue(),card.getCardNumber(),cardDueDate,"000"); 
                        if(CVV.trim().equals(CVVHSMGenerate.getCvv().trim())){
                            //Se guarda el pinOffset en la BD del CMS
                            card.setPinOffset(ibmOfSetResponse.getIBMoffset());
                            entityManager.merge(card);
                            //Se guarda la clave en el historial de claves
                            CardKeyHistory cardKeyHistory = new CardKeyHistory();
                            cardKeyHistory.setCardId(card);
                            cardKeyHistory.setPreviousPinOffset(ibmOfSetResponse.getIBMoffset());
                            cardKeyHistory.setCreateDate(new Date());
                        } else {
                            //Fallo en la validación del CVV
                            transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                            transactionsManagement.setResponseCode(ResponseCode.CVV_DIFFERENT.getCode());
                            try {
                                transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                            } catch (Exception e) {
                                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                            }
                            return new TransactionResponse(ResponseCode.CVV_DIFFERENT.getCode(), ResponseCode.CVV_DIFFERENT.getMessage());
                        }
                        
                    } else {
                        //Se actualiza la transacción a RECHAZADA debido a que falló la validación del pin
                        transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                        transactionsManagement.setResponseCode(ResponseCode.INVALID_PIN.getCode());
                        try {
                            transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                        } catch (Exception e) {
                            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                        }
                        return new TransactionResponse(ResponseCode.INVALID_PIN.getCode(), ResponseCode.INVALID_PIN.getMessage());
                    }
                } else {
                    //Fallo en la validación de las propiedades
                    transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionsManagement.setResponseCode(transactionResponse.getCodigoRespuesta());
                    try {
                        transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(transactionResponse.getCodigoRespuesta(), transactionResponse.getMensajeRespuesta());
                }
            } else {
                //Fallo en la validación de la tarjeta
                transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionsManagement.setResponseCode(cardResponse.getCodigoRespuesta());
                try {
                    transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionResponse(cardResponse.getCodigoRespuesta(), cardResponse.getMensajeRespuesta());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "INTERNAL_ERROR");
        }

        return new TransactionResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage());
    }

    public TransactionResponse reverseCardPurchage(String cardNumber, String CVV, String cardDueDate, String cardHolder, String ARQC, Integer channelId, Integer transactionTypeId, Long messageMiddlewareId, Date transactionDate,
            String localTimeTransaction, String acquirerTerminalCodeId, Integer acquirerCountryId, String transactionNumber, String tradeName) {

        Card card = null;
        TransactionsManagement transactionsManagementReverse = null;
        TransactionsManagement transactionsManagement = null;
        TransactionsManagement transactionsManagementReverseCommision = null;
        TransactionsManagement transactionsManagementReverseBonification = null;
        int indValidateCardActive = 1;
        Float currentBalance = 0.00F;
        Float newBalance = 0.00F;
        BalanceHistoryCard balanceHistoryCard = null;
        String conceptTransaction = "Reverso de Compra POS - ";
        String customerIdentificationNumber = "";
        try {
            conceptTransaction.concat(tradeName);
            CardResponse cardResponse = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);
            if (cardResponse.getCard() != null) {
                //Se obtiene la tarjeta asociada
                card = cardResponse.getCard();
                //Se obtiene el numero de indentifiación del cliente
                if (card.getPersonCustomerId().getPersonTypeId().getIndNaturalPerson() == true) {
                    customerIdentificationNumber = card.getPersonCustomerId().getNaturalCustomer().getIdentificationNumber();
                } else {
                    customerIdentificationNumber = card.getPersonCustomerId().getLegalCustomer().getIdentificationNumber();
                }
            }
            //Buscar pais
            Country country = operationsBD.getCountry(acquirerCountryId.toString(), entityManager);
            //Se crea la transaction
            transactionsManagement = (TransactionsManagement) operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, country.getId(), null, transactionDate,
                    TransactionE.REVERSE_CARD_PURCHAGE.getId(), channelId, null, localTimeTransaction, null, null, null,
                    null, null, null, null, null, null,
                    null, null, cardNumber, cardHolder, CVV, cardDueDate, null, null, null, null, null, null, null,
                    null, null, null, null, messageMiddlewareId, DocumentTypeE.REVERSE_CARD_PURCHAGE.getId(), conceptTransaction, entityManager);
            try {
                transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            if (cardResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //Se busca la transacción para hacer el reverso
                transactionsManagementReverse = operationsBD.getTransactionsManagementByNumber(transactionNumber, entityManager);
                if (transactionsManagementReverse != null) {
                    //Se cancela la transaction original
                    transactionsManagementReverse.setStatusTransactionManagementId(StatusTransactionManagementE.CANCELLED.getId());
                    transactionsManagementReverse.setResponseCode(ResponseCode.CANCEL.getCode());
                    transactionsManagementReverse.setUpdateDate(new Timestamp(new Date().getTime()));
                    try {
                        transactionsManagementReverse = operationsBD.saveTransactionsManagement(transactionsManagementReverse, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }

                    //Se ejecuta la transaction del reverso
                    transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.APPROVED.getId());
                    transactionsManagement.setResponseCode(ResponseCode.SUCCESS.getCode());
                    transactionsManagement.setSettlementTransactionAmount(transactionsManagementReverse.getSettlementTransactionAmount());
                    try {
                        transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }

                    //Se obtiene el balance actual del usuario y se le resta el reverso de la operación
                    currentBalance = getCurrentBalanceCard(card.getId());
                    newBalance = currentBalance + transactionsManagementReverse.getSettlementTransactionAmount();

                    //Se actualiza el balance history y saldo de la cuenta
                    balanceHistoryCard = operationsBD.createBalanceHistoryCard(card, transactionsManagement.getId(), currentBalance, newBalance, entityManager);
                    try {
                        balanceHistoryCard = operationsBD.saveBalanceHistoryCard(balanceHistoryCard, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving Balance History");
                    }

                    AccountCard accountNumber = getAccountNumberByCard(card.getCardNumber());
                    AccountCard accountCard = entityManager.find(AccountCard.class, accountNumber.getId());
                    accountCard.setUpdateDate(new Timestamp(new Date().getTime()));
                    accountCard.setCurrentBalance(newBalance);
                    entityManager.merge(accountCard);

                    //Se revisa si la transaccion genero comission
                    if (transactionsManagementReverse.getAcquirerCommisionAmount() != null) {
                        if (transactionsManagementReverse.getAcquirerCommisionAmount() > 0) {
                            conceptTransaction = "Reverso de Comisión CMS";
                            //Se crea la transaction de reverso de comission
                            transactionsManagementReverseCommision = (TransactionsManagement) operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, null, transactionDate,
                                    TransactionE.REVERSE_COMISSION.getId(), channelId, null, localTimeTransaction, null, null, null,
                                    null, null, null, null, null, null,
                                    null, null, cardNumber, cardHolder, CVV, cardDueDate, null, null, null, null, null, null, null,
                                    null, null, null, null, messageMiddlewareId, DocumentTypeE.REVERSE_COMISSION.getId(), conceptTransaction, entityManager);

                            reverseComission(transactionNumber, transactionsManagementReverseCommision, card);
                        }
                    }

                    //Se revisa si la transacción de recarga generó una bonificación
                    transactionsManagementReverseBonification = operationsBD.getTransactionsManagementByTransactionReference(transactionNumber, TransactionE.BONIFICATION_CMS.getId(), entityManager);
                    if (transactionsManagementReverseBonification != null) {
                        TransactionResponse transactionResponse = reverseBonification(transactionNumber, transactionsManagementReverseBonification, card);
                        if (!transactionResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                            return transactionResponse;
                        }
                    }
                    return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "SUCCESS");

                } else {
                    //No se encontro ninguna transacción para hacer el reverso
                    transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionsManagement.setResponseCode(ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getCode());
                    try {
                        transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getCode(), ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getMessage());
                }
            } else {
                //La tarjeta no es valida
                transactionsManagement.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionsManagement.setResponseCode(cardResponse.getCodigoRespuesta());
                try {
                    transactionsManagement = operationsBD.saveTransactionsManagement(transactionsManagement, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionResponse(cardResponse.getCodigoRespuesta(), cardResponse.getMensajeRespuesta());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "INTERNAL_ERROR");
        }

    }

    public TransactionResponse reverseComission(String transactionNumber, TransactionsManagement transactionsManagementReverseCommision, Card card) {
        Float currentBalance = 0.00F;
        Float newBalance = 0.00F;
        BalanceHistoryCard balanceHistoryCard = null;
        try {
            transactionsManagementReverseCommision = operationsBD.saveTransactionsManagement(transactionsManagementReverseCommision, entityManager);
        } catch (Exception e) {
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
        }
        TransactionsManagement transactionsManagementCommission = operationsBD.getTransactionsManagementByTransactionReference(transactionNumber, TransactionE.COMISION_CMS.getId(), entityManager);
        if (transactionsManagementCommission != null) {
            //Se cancela la transaction de comision
            transactionsManagementCommission.setStatusTransactionManagementId(StatusTransactionManagementE.CANCELLED.getId());
            transactionsManagementCommission.setResponseCode(ResponseCode.CANCEL.getCode());
            transactionsManagementCommission.setUpdateDate(new Timestamp(new Date().getTime()));
            try {
                transactionsManagementCommission = operationsBD.saveTransactionsManagement(transactionsManagementCommission, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            //Se ejecuta la transaction del reverso
            transactionsManagementReverseCommision.setStatusTransactionManagementId(StatusTransactionManagementE.APPROVED.getId());
            transactionsManagementReverseCommision.setResponseCode(ResponseCode.SUCCESS.getCode());
            transactionsManagementReverseCommision.setTransactionCommissionAmount(transactionsManagementReverseCommision.getTransactionCommissionAmount());
            try {
                transactionsManagementReverseCommision = operationsBD.saveTransactionsManagement(transactionsManagementReverseCommision, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            //Se obtiene el balance actual del usuario y se le rest el reverso de la operación
            currentBalance = getCurrentBalanceCard(card.getId());
            newBalance = currentBalance + transactionsManagementReverseCommision.getTransactionCommissionAmount();

            //Se actualiza el balance history y saldo de la cuenta
            balanceHistoryCard = operationsBD.createBalanceHistoryCard(card, transactionsManagementReverseCommision.getId(), currentBalance, newBalance, entityManager);
            try {
                balanceHistoryCard = operationsBD.saveBalanceHistoryCard(balanceHistoryCard, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving Balance History");
            }

            AccountCard accountNumber = getAccountNumberByCard(card.getCardNumber());
            AccountCard accountCard = entityManager.find(AccountCard.class, accountNumber.getId());
            accountCard.setUpdateDate(new Timestamp(new Date().getTime()));
            accountCard.setCurrentBalance(newBalance);
            entityManager.merge(accountCard);

            return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "SUCCESS");

        } else {
            //No se encontro ninguna transacción para hacer el reverso
            transactionsManagementReverseCommision.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
            transactionsManagementReverseCommision.setResponseCode(ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getCode());
            try {
                transactionsManagementReverseCommision = operationsBD.saveTransactionsManagement(transactionsManagementReverseCommision, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }
            return new TransactionResponse(ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getCode(), ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getMessage());
        }
    }

    public TransactionResponse reverseBonification(String transactionNumber, TransactionsManagement transactionsManagementReverseBonification, Card card) {
        Float currentBalance = 0.00F;
        Float newBalance = 0.00F;
        BalanceHistoryCard balanceHistoryCard = null;

        //buscar bonifaciones asociada al transactionNumber
        TransactionPoint transactionPoint = operationsBD.getTransactionPointByTransactionReference(transactionNumber, entityManager);
        //encontro bonificacion
        if (transactionPoint != null) {

            // Guardar transaccion y ajustar bonificacion
            if (transactionPoint.getProgramLoyaltyTransactionId().getProgramLoyaltyId().getProgramLoyaltyTypeId().getCode().equals(ProgramLoyaltyTypeE.PUNTOS.getCode())) {
                // eliminar puentos
                int points = transactionPoint.getPoints();
                try {
                    transactionPoint.setUpdateDate(new Date());
                    transactionPoint.setIndReversed(true);
                    saveTransactionPoint(transactionPoint);  // registrar transaccion de asignacion de puntos
                    BonusCard bonusCard = operationsBD.getBonusCardByCardId(card.getId(), entityManager);
                    if (bonusCard != null) {
                        bonusCard.setUpdateDate(new Date());
                        bonusCard.setTotalPointsAccumulated(bonusCard.getTotalPointsAccumulated() - points);
                    }
                    saveBonusCard(bonusCard);
                } catch (Exception ex) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "Error add points");
                }
            }
            return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "SUCCESS");
        } else {

            //Buscar bonificacion por saldo
            TransactionsManagement transactionsManagementBonification = operationsBD.getTransactionsManagementByTransactionReference(transactionNumber, TransactionE.BONIFICATION_CMS.getId(), entityManager);
            if (transactionsManagementBonification != null) {
                try {
                    transactionsManagementReverseBonification = operationsBD.saveTransactionsManagement(transactionsManagementReverseBonification, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                //Se cancela la transaction de bonificacion
                transactionsManagementBonification.setStatusTransactionManagementId(StatusTransactionManagementE.CANCELLED.getId());
                transactionsManagementBonification.setResponseCode(ResponseCode.CANCEL.getCode());
                transactionsManagementBonification.setUpdateDate(new Timestamp(new Date().getTime()));
                try {
                    transactionsManagementBonification = operationsBD.saveTransactionsManagement(transactionsManagementBonification, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }

                //Se ejecuta la transaction del reverso
                transactionsManagementReverseBonification.setStatusTransactionManagementId(StatusTransactionManagementE.APPROVED.getId());
                transactionsManagementReverseBonification.setResponseCode(ResponseCode.SUCCESS.getCode());
                transactionsManagementReverseBonification.setTransactionCommissionAmount(transactionsManagementReverseBonification.getTransactionCommissionAmount());
                try {
                    transactionsManagementReverseBonification = operationsBD.saveTransactionsManagement(transactionsManagementReverseBonification, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }

                //Se obtiene el balance actual del usuario y se le suma el reverso de la operación
                currentBalance = getCurrentBalanceCard(card.getId());
                newBalance = currentBalance - transactionsManagementReverseBonification.getTransactionCommissionAmount();

                //Se actualiza el balance history y saldo de la cuenta
                balanceHistoryCard = operationsBD.createBalanceHistoryCard(card, transactionsManagementReverseBonification.getId(), currentBalance, newBalance, entityManager);
                try {
                    balanceHistoryCard = operationsBD.saveBalanceHistoryCard(balanceHistoryCard, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving Balance History");
                }

                AccountCard accountNumber = getAccountNumberByCard(card.getCardNumber());
                AccountCard accountCard = entityManager.find(AccountCard.class, accountNumber.getId());
                accountCard.setUpdateDate(new Timestamp(new Date().getTime()));
                accountCard.setCurrentBalance(newBalance);
                entityManager.merge(accountCard);
            }
            return new TransactionResponse(ResponseCode.SUCCESS.getCode(), "SUCCESS");
        }
    }

    public TransactionResponse atmCardWithdrawal(String cardNumber, String cardHolder, String CVV, String cardDueDate, Long messageMiddlewareId,
            Integer transactionTypeId, Integer channelId, Date transactionDate, String localTimeTransaction,
            String acquirerTerminalCodeId, String transactionNumberAcquirer, Integer acquirerCountryId,
            Float amountWithdrawal, String pinBlock, String ARQC, String terminalId, String entryMode, String schemeEMV,
            String seqNumber, String atc, String unpredictableNumber, String transactionData, String tradeName) {

        TransactionsManagement transactionAtmCardWithdrawal = null;
        int indValidateCardActive = 1;
        Card card = null;
        ValidateLimitsResponse validateLimits = null;
        Float currentBalance = 0.00F;
        AccountCard accountCard = null;
        TransactionResponse commissionCMS = null;
        Float amountCommission = 0.00F;
        Float totalAmountWithDrawall = 0.00F;
        CalculateBonusCardResponse calculateBonification = null;
        BalanceHistoryCard balanceHistoryCard = null;
        Float newBalance = 0.00F;
        String arpc = "";
        String conceptTransaction = "Retiro Cajero ATM - ";
        String customerIdentificationNumber = "";
        String pinELMK = "";
        SecurityKeyType securityKeyType = null;
        SecurityKey securityKey = null;
        IsoHsmEquivalence isoHsmEquivalence = null;
        try {

            CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);
            if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
               //Se obtiene la tarjeta asociada
                card = validateCard.getCard();
                //Se obtiene el numero de indentifiación del cliente
                if (card.getPersonCustomerId().getPersonTypeId().getIndNaturalPerson() == true) {
                    customerIdentificationNumber = card.getPersonCustomerId().getNaturalCustomer().getIdentificationNumber();
                } else {
                    customerIdentificationNumber = card.getPersonCustomerId().getLegalCustomer().getIdentificationNumber();
                }
            }

            //Se registra la transacción de retiro por cajero ATM en la BD
            conceptTransaction.concat(tradeName);
            Country country = operationsBD.getCountry(String.valueOf(acquirerCountryId), entityManager);
            transactionAtmCardWithdrawal = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, country.getId(), transactionNumberAcquirer, transactionDate,
                    TransactionE.ATM_CARD_WITHDRAWAL.getId(), channelId, null, localTimeTransaction, null, null, null,
                    null, amountWithdrawal, null, null, null, null,
                    null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV, cardDueDate, customerIdentificationNumber, null, null, null, null, null, null,
                    null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.ATM_CARD_WITHDRAWAL.getId(), conceptTransaction, entityManager);

            try {
                transactionAtmCardWithdrawal = operationsBD.saveTransactionsManagement(transactionAtmCardWithdrawal, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            //Se valida la tarjeta
            if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                //Se obtiene el tipo de llave
                securityKeyType = operationsBD.getSecurityKeyTypeById(SecurityKeyTypeE.KWP.getId(), entityManager);
                //Se busca la llave en la BD en caso de no conseguir se genera la llave y se busca la llave generada
                SecurityKey keyKWP = operationsBD.getSecurityKey(securityKeyType.getId(), Constants.KEY_LENGHT_SINGLE, entityManager);
                if (keyKWP == null) {
                    TransactionResponse generateKey = generateSecurityKey("KWP", "Single");
                    keyKWP = operationsBD.getSecurityKey(securityKeyType.getId(), Constants.KEY_LENGHT_SINGLE, entityManager);
                }

                //Se genera el pinBlock y se transformar el CardNumber en el formato requerido para el servicio translatePINZPKToLMK
                String generatePinBlock = getPinblock(keyKWP.getClearSecurityKey(), pinBlock, cardNumber);
                String convertCardNumber = operationsBD.convertCardNumber(cardNumber);

                //Se realizan las validaciones del HSM
                pinELMK = HSMOperations.translatePINZPKToLMK(generatePinBlock, convertCardNumber, keyKWP.getEncryptedValue(), keyKWP.getSecurityKeySizeId().getName());
                IBMOfSetResponse responseGeneratePinOffSet = (IBMOfSetResponse) generateIBMPinOffSet(pinELMK, cardNumber);

                //Se valida el PinOffSet generado por la caja HSM con el de la BD
                CardResponse validatePinOffset = validatePinOffset(cardNumber, responseGeneratePinOffSet.getIBMoffset());
                if (validatePinOffset.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                    //Se obtiene el codigo del tipo de CVV que se va a validar contra la caja HSM mediante el mensaje ISO 
                    isoHsmEquivalence = operationsBD.getHSMRequestValue(entryMode,22, entityManager);
                    //Se obtiene el id de la llave CVK o KVC asociada a la tarjeta a validar
                    PlastiCustomizingRequestHasCard plasticRequest = operationsBD.getSecurityKeyByCard(card.getId(), entityManager);
                    //Se genera el CVV con la caja HSM
                    GenerateCVVResponse CVVHSMGenerate = (GenerateCVVResponse) generateCVV(plasticRequest.getSecurityKeyId().getEncryptedValue(),card.getCardNumber(),cardDueDate,isoHsmEquivalence.getHsmRequestValue());
                    //Se valida el CVV generado por la caja HSM con el CVV del parametro de entrada
                    if(CVV.trim().equals(CVVHSMGenerate.getCvv().trim())){
                     
                    //Se valida el criptograma ARQC
//                    metod = lp.getProperties("prop.validateQRQC");
//                    params = request.getARPCRequest(terminalId, oPMode, schemeEMV, card.getCardNumber(), seqNumber, atc, unpredictableNumber, transactionData, ARQC);
//                    ARPCResponse arpcResponse = (ARPCResponse) getResponse(metod, params, ARPCResponse.class);
//                    if (response.getResponseCode().equals(ResponseCode.SUCCESS.getCode())) {
//                        //Se obtiene la respuesta que se envía al terminal (ARPC)
//                        arpc = arpcResponse.getArpc();
                    //Se validan los límites transaccionales
                    validateLimits = getValidateLimits(card, transactionTypeId, channelId, acquirerCountryId.toString(), amountWithdrawal);
                    if (validateLimits.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                        //Se revisa si la transacción genera una comisión
                        commissionCMS = calculateCommisionCMS(card, channelId, transactionTypeId, amountWithdrawal, transactionNumberAcquirer);
                        if (commissionCMS.getCodigoRespuesta().equals(ResponseCode.COMMISSION_YES_APPLY.getCode())) {
                            amountCommission = commissionCMS.getTransactionCommissionAmount();
                        }
                        //Se obtiene el saldo de la cuenta asociada a la tarjeta
                        accountCard = operationsBD.getAccountCardbyCardId(card.getId(), entityManager);
                        currentBalance = accountCard.getCurrentBalance();
                        totalAmountWithDrawall = amountWithdrawal + amountCommission;
                        newBalance = currentBalance - totalAmountWithDrawall;
                        //Se verifica que el monto total del retiro no sea mayor al saldo actual del usuario
                        if (newBalance < 0) {
                            //Se actualiza el estatus de la transacción a RECHAZADA, debido a que el monto del retiro excedió el saldo disponible
                            transactionAtmCardWithdrawal.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                            transactionAtmCardWithdrawal.setResponseCode(ResponseCode.BALANCE_GREATER_THAN_ALLOWED.getCode());
                            try {
                                transactionAtmCardWithdrawal = operationsBD.saveTransactionsManagement(transactionAtmCardWithdrawal, entityManager);
                            } catch (Exception e) {
                                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                            }
                            return new TransactionResponse(ResponseCode.BALANCE_GREATER_THAN_ALLOWED.getCode(), ResponseCode.BALANCE_GREATER_THAN_ALLOWED.getMessage());
                        }
                        //Se verifica que el saldo actualizado luego del retiro no sea menor al saldo mínimo permitido para la tarjeta
                        if (newBalance < card.getProductId().getMinimumBalance()) {
                            //Se actualiza el estatus de la transacción a RECHAZADA, debido a que el nuevo saldo es menor al monto mínimo permitido para la tarjeta
                            transactionAtmCardWithdrawal.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                            transactionAtmCardWithdrawal.setResponseCode(ResponseCode.BALANCE_LESS_THAN_ALLOWED.getCode());
                            try {
                                transactionAtmCardWithdrawal = operationsBD.saveTransactionsManagement(transactionAtmCardWithdrawal, entityManager);
                            } catch (Exception e) {
                                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                            }
                            return new TransactionResponse(ResponseCode.BALANCE_LESS_THAN_ALLOWED.getCode(), ResponseCode.BALANCE_LESS_THAN_ALLOWED.getMessage());
                        } else {
                            //Se Verifica si la transacción genera bonificación
                            calculateBonification = calculateBonus(card.getCardNumber(), transactionTypeId, channelId, acquirerCountryId.toString(), amountWithdrawal, transactionAtmCardWithdrawal.getTransactionNumberIssuer());

                            //Se actualiza el historial de saldos de la tarjeta en la BD del CMS
                            balanceHistoryCard = operationsBD.createBalanceHistoryCard(card, transactionAtmCardWithdrawal.getId(), currentBalance, newBalance, entityManager);
                            try {
                                balanceHistoryCard = operationsBD.saveBalanceHistoryCard(balanceHistoryCard, entityManager);
                            } catch (Exception e) {
                                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                            }

                            //Se actualiza el saldo de la cuenta en la BD del CMS
                            accountCard.setCurrentBalance(newBalance);
                            accountCard.setUpdateDate(new Timestamp(new Date().getTime()));
                            entityManager.persist(accountCard);

                            //Se retorna que el retiro por cajero ATM se realizó satisfactoriamente
                            return new TransactionResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), arpc);
                        }
                    } else {
                        //Se actualiza el estatus de la transacción a RECHAZADA, debido a que excedió los límites transaccionales
                        transactionAtmCardWithdrawal.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                        transactionAtmCardWithdrawal.setResponseCode(validateLimits.getCodigoRespuesta());
                        try {
                            transactionAtmCardWithdrawal = operationsBD.saveTransactionsManagement(transactionAtmCardWithdrawal, entityManager);
                        } catch (Exception e) {
                            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                        }
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
//                    } else {
//                        //Se actualiza el estatus de la transacción a RECHAZADA, debido a que el ARQC no es válido
//                        transactionAtmCardWithdrawal.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
//                        transactionAtmCardWithdrawal.setResponseCode(response.getResponseCode());
//                        try {
//                            transactionAtmCardWithdrawal = operationsBD.saveTransactionsManagement(transactionAtmCardWithdrawal, entityManager);
//                        } catch (Exception e) {
//                            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
//                        }
//                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
//                    }
                    }else {
                        //Se actualiza el estatus de la transacción a RECHAZADA, debido a que el CVV no es válido
                        transactionAtmCardWithdrawal.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                        transactionAtmCardWithdrawal.setResponseCode(ResponseCode.CVV_DIFFERENT.getCode());
                        try {
                            transactionAtmCardWithdrawal = operationsBD.saveTransactionsManagement(transactionAtmCardWithdrawal, entityManager);
                        } catch (Exception e) {
                            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                        }
                        return new TransactionResponse(ResponseCode.CVV_DIFFERENT.getCode(), ResponseCode.CVV_DIFFERENT.getMessage());
                    }
                } else {
                    //Se actualiza el estatus de la transacción a RECHAZADA, debido a validaciones de HSM
                    transactionAtmCardWithdrawal.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionAtmCardWithdrawal.setResponseCode(validatePinOffset.getCodigoRespuesta());
                    try {
                        transactionAtmCardWithdrawal = operationsBD.saveTransactionsManagement(transactionAtmCardWithdrawal, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(validatePinOffset.getCodigoRespuesta(), validatePinOffset.getMensajeRespuesta());
                }
            } else {
                //Se rechaza la transacción debido a que la tarjeta no es valida
                transactionAtmCardWithdrawal.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionAtmCardWithdrawal.setResponseCode(validateCard.getCodigoRespuesta());
                try {
                    transactionAtmCardWithdrawal = operationsBD.saveTransactionsManagement(transactionAtmCardWithdrawal, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionResponse(validateCard.getCodigoRespuesta(), validateCard.getMensajeRespuesta());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "INTERNAL_ERROR");
        }
    }

    public TransactionResponse reverseAtmCardWithdrawal(String cardNumber, String cardHolder, String CVV, String cardDueDate, Long messageMiddlewareId,
            Integer transactionTypeId, Integer channelId, Date transactionDate, String localTimeTransaction,
            String acquirerTerminalCodeId, Integer acquirerCountryId, String transactionNumberAcquirer, Float amountReverseAtmWithdrawal,
            String transactionNumberAtmCardWithdrawal, String sequenceTransactionAtmCardWithdrawal, String tradeName) {

        String ARQC = null;
        TransactionsManagement transactionReverseAtmCardWithdrawal = null;
        TransactionsManagement transactionAtmCardWithdrawalOriginal = null;
        TransactionsManagement transactionReverseComissionRechargeCard = null;
        TransactionsManagement transactionsReverseManagementBonification = null;
        int indValidateCardActive = 1;
        Card card = null;
        Float currentBalance = 0.00F;
        AccountCard accountCard = null;
        BalanceHistoryCard balanceHistoryCard = null;
        Float newBalance = 0.00F;
        String customerIdentificationNumber = "";
        String conceptTransaction = "Reverso Retiro Cajero ATM - ";
        try {

            CardResponse validateCard = validateCard(cardNumber, ARQC, cardHolder, CVV, cardDueDate, indValidateCardActive);

            if (validateCard.getCard() != null) {
                //Se obtiene la tarjeta asociada
                card = validateCard.getCard();
                //Se obtiene el numero de indentifiación del cliente
                if (card.getPersonCustomerId().getPersonTypeId().getIndNaturalPerson() == true) {
                    customerIdentificationNumber = card.getPersonCustomerId().getNaturalCustomer().getIdentificationNumber();
                } else {
                    customerIdentificationNumber = card.getPersonCustomerId().getLegalCustomer().getIdentificationNumber();
                }
            }

            //Buscar pais
            Country country = operationsBD.getCountry(acquirerCountryId.toString(), entityManager);
            //Se registra la transacción de Reverso de Recarga de la Tarjeta en la BD
            transactionReverseAtmCardWithdrawal = operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, country.getId(), transactionNumberAcquirer, transactionDate,
                    TransactionE.REVERSE_ATM_CARD_WITHDRAWAL.getId(), channelId, null, localTimeTransaction, null, null, null,
                    null, amountReverseAtmWithdrawal, null, null, null, null,
                    null, StatusTransactionManagementE.APPROVED.getId(), cardNumber, cardHolder, CVV, cardDueDate, customerIdentificationNumber, null, null, null, null, null, null,
                    null, null, null, ResponseCode.SUCCESS.getCode(), messageMiddlewareId, DocumentTypeE.REVERSE_CARD_RECHARGE.getId(), conceptTransaction.concat(tradeName), entityManager);
            try {
                transactionReverseAtmCardWithdrawal = operationsBD.saveTransactionsManagement(transactionReverseAtmCardWithdrawal, entityManager);
            } catch (Exception e) {
                return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
            }

            //Se valida la tarjeta
            if (validateCard.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {

                //Se obtiene la transacción de compra ATM a reversar
                transactionAtmCardWithdrawalOriginal = operationsBD.getTransactionByNumberAndSequence(transactionNumberAtmCardWithdrawal, sequenceTransactionAtmCardWithdrawal, entityManager);
                if (transactionAtmCardWithdrawalOriginal != null) {
                    //Se cancela la transacción de compra ATM
                    transactionAtmCardWithdrawalOriginal.setStatusTransactionManagementId(StatusTransactionManagementE.CANCELLED.getId());
                    transactionAtmCardWithdrawalOriginal.setUpdateDate(new Timestamp(new Date().getTime()));
                    try {
                        transactionAtmCardWithdrawalOriginal = operationsBD.saveTransactionsManagement(transactionAtmCardWithdrawalOriginal, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }

                    //Se obtiene el saldo actual y se actualiza el monto del saldo aplicando el reverso
                    currentBalance = getCurrentBalanceCard(card.getId());
                    newBalance = currentBalance + transactionAtmCardWithdrawalOriginal.getSettlementTransactionAmount();

                    //Se actualiza el balance history y saldo de la cuenta
                    balanceHistoryCard = operationsBD.createBalanceHistoryCard(card, transactionReverseAtmCardWithdrawal.getId(), currentBalance, newBalance, entityManager);
                    try {
                        balanceHistoryCard = operationsBD.saveBalanceHistoryCard(balanceHistoryCard, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving Balance History");
                    }

                    //Se actualiza el saldo del cliente
                    AccountCard accountNumber = getAccountNumberByCard(cardNumber);
                    accountCard = entityManager.find(AccountCard.class, accountNumber.getId());
                    accountCard.setUpdateDate(new Timestamp(new Date().getTime()));
                    accountCard.setCurrentBalance(newBalance);
                    entityManager.merge(accountCard);

                    //Se revisa si la transacción de recarga generó comisión
                    if (transactionAtmCardWithdrawalOriginal.getTransactionCommissionAmount() != null) {
                        if (transactionAtmCardWithdrawalOriginal.getTransactionCommissionAmount() > 0) {
                            conceptTransaction = "Reverso Comisión CMS";
                            //Se registra la transacción de reverso de la comission
                            transactionReverseComissionRechargeCard = (TransactionsManagement) operationsBD.createTransactionsManagement(null, null, acquirerTerminalCodeId, acquirerCountryId, null, transactionDate,
                                    TransactionE.REVERSE_COMISSION.getId(), channelId, null, localTimeTransaction, null, null, null,
                                    null, null, null, null, null, null,
                                    null, null, cardNumber, cardHolder, CVV, cardDueDate, null, null, null, null, null, null, null,
                                    null, null, null, null, messageMiddlewareId, DocumentTypeE.REVERSE_COMISSION.getId(), conceptTransaction, entityManager);

                            //Se realiza el reverso de la comisión generada por le recarga
                            TransactionResponse transactionResponse = reverseComission(transactionNumberAtmCardWithdrawal, transactionReverseComissionRechargeCard, card);
                            if (!transactionResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                                return transactionResponse;
                            }
                        }
                    }

                    //Se revisa si la transacción de recarga generó una bonificación
                    transactionsReverseManagementBonification = operationsBD.getTransactionsManagementByTransactionReference(transactionNumberAtmCardWithdrawal, TransactionE.BONIFICATION_CMS.getId(), entityManager);
                    if (transactionsReverseManagementBonification != null) {
                        TransactionResponse transactionResponse = reverseBonification(transactionNumberAtmCardWithdrawal, transactionsReverseManagementBonification, card);
                        if (!transactionResponse.getCodigoRespuesta().equals(ResponseCode.SUCCESS.getCode())) {
                            return transactionResponse;
                        }
                    }

                    return new TransactionResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage());

                } else {
                    //Se generó un error inesperado, no se encontró la transacción de compra ATM a reversar
                    transactionReverseAtmCardWithdrawal.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                    transactionReverseAtmCardWithdrawal.setResponseCode(ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getCode());
                    try {
                        transactionReverseAtmCardWithdrawal = operationsBD.saveTransactionsManagement(transactionReverseAtmCardWithdrawal, entityManager);
                    } catch (Exception e) {
                        return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                    }
                    return new TransactionResponse(ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getCode(), ResponseCode.REVERSE_TRANSACTION_NOT_FOUND.getMessage());
                }
            } else {
                //Se actualiza el estatus de la transacción a RECHAZADA, debido a que falló la validación de la tarjeta
                transactionReverseAtmCardWithdrawal.setStatusTransactionManagementId(StatusTransactionManagementE.REJECTED.getId());
                transactionReverseAtmCardWithdrawal.setResponseCode(validateCard.getCodigoRespuesta());
                try {
                    transactionReverseAtmCardWithdrawal = operationsBD.saveTransactionsManagement(transactionReverseAtmCardWithdrawal, entityManager);
                } catch (Exception e) {
                    return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "an error occurred while saving the transaction");
                }
                return new TransactionResponse(validateCard.getCodigoRespuesta(), validateCard.getMensajeRespuesta());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TransactionResponse(ResponseCode.INTERNAL_ERROR.getCode(), "INTERNAL_ERROR");
        }
    }

    public TransactionResponse generateSecurityKey(String keyType, String lenght) {

        String securityKeyEncripted = "";
        SecurityKeyType securityKeyType = null;
        SecurityKeySize securityKeySize = null;
        VerificationTypeSecurityKey verificationTypeSecurityKey = null;
        HSMBox hsmBox = null;

        try {
            GenerateKeyResponse responseKey = new GenerateKeyResponse();
            responseKey = generateKey(keyType, lenght); //Se genera la llave en la caja HSM

            //Se guarda en la BD del cms la llave de seguridad
            SecurityKey securityKey = new SecurityKey();
            securityKey.setEncryptedValue(responseKey.getKeyValue());
            securityKey.setCheckDigit(responseKey.getVerificationDigit());
            switch (keyType) {
                case Constants.SECURITY_KEY_KEK:
                    securityKey.setName("Llave de Seguridad KEK");
                    securityKeyType = operationsBD.getSecurityKeyTypeById(SecurityKeyTypeE.KEK.getId(), entityManager);
                    securityKey.setSecurityKeyTypeId(securityKeyType);
                    securityKey.setLenght(Constants.KEY_LENGHT_SINGLE);
                    break;
                case Constants.SECURITY_KEY_KWP:
                    securityKey.setName("Llave de Seguridad KWP");
                    securityKeyType = operationsBD.getSecurityKeyTypeById(SecurityKeyTypeE.KWP.getId(), entityManager);
                    securityKey.setSecurityKeyTypeId(securityKeyType);
                    securityKey.setLenght(Constants.KEY_LENGHT_DOUBLE);
                    break;
                case Constants.SECURITY_KEY_KVC:
                    securityKeyType = operationsBD.getSecurityKeyTypeById(SecurityKeyTypeE.KVC.getId(), entityManager);
                    securityKey.setSecurityKeyTypeId(securityKeyType);
                    securityKey.setLenght(Constants.KEY_LENGHT_TRIPLE);
                    break;
                default:
                    break;
            }
            switch (lenght) {
                case Constants.SECURITY_KEY_TYPE_SINGLE:
                    securityKeySize = operationsBD.getSecurityKeySizeById(SecurityKeySizeE.Single.getId(), entityManager);
                    securityKey.setSecurityKeySizeId(securityKeySize);
                    securityKey.setLenght(Constants.KEY_LENGHT_SINGLE);
                    break;
                case Constants.SECURITY_KEY_TYPE_DOUBLE:
                    securityKeySize = operationsBD.getSecurityKeySizeById(SecurityKeySizeE.Double.getId(), entityManager);
                    securityKey.setSecurityKeySizeId(securityKeySize);
                    securityKey.setLenght(Constants.KEY_LENGHT_DOUBLE);
                    break;
                case Constants.SECURITY_KEY_TYPE_TRIPLE:
                    securityKeySize = operationsBD.getSecurityKeySizeById(SecurityKeySizeE.Triple.getId(), entityManager);
                    securityKey.setSecurityKeySizeId(securityKeySize);
                    securityKey.setLenght(Constants.KEY_LENGHT_TRIPLE);
                    break;
                default:
                    break;
            }
            verificationTypeSecurityKey = operationsBD.getVerificationTypeSecurityKeyById(VerificationTypeSecurityKeyE.IBM.getId(), entityManager);
            securityKey.setVerificationTypeSecurityKeyId(verificationTypeSecurityKey);
            hsmBox = operationsBD.getHSMBoxById(Constants.HSM_BOX_ALODIGA, entityManager);
            securityKey.setHSMBoxId(hsmBox);
            securityKey.setCreateDate(new Timestamp(new Date().getTime()));
            entityManager.persist(securityKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new TransactionResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage());
    }
    
    public TransactionResponse createCard(String keyType, String lenght) {
        
        Card card = null;
        boolean indRenewal = true;
        CardStatus cardStatus = null;
        StatusApplicant statusApplicant = null;
        List<ReviewRequest> reviewRequestList = null;
        List<ApplicantNaturalPerson> cardComplementaryList = null;
        List<CardRequestNaturalPerson> cardRequestList = null;
        List<PhonePerson> phonePersonList = null;
        String cardNumber = null;
        String accountAssigned = null;
        Long countCardComplementary = 0L;
        
        return new TransactionResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage());
        
    }

}
