package com.alodiga.authorizer.cms.responses;

public enum ResponseCode {

    SUCCESS("00","SUCCESS"),
    INVALID_DATA("01","Invalid data"),
    EXPIRED_PASSWORD("03",""),
    NO_TRUST_IP("04",""),
    INVALID_CREDENTIALS("05",""),
    BLOCKED_USER("06",""),
    COUNTRY_NOT_FOUND("07","The country not found"),
    CHANNEL_NOT_FOUND("07","The channel not found"),
    INTERNAL_ERROR("99",""),
    
     //Validacion de Limites
    TRANSACTION_QUANTITY_LIMIT_DAILY("34","The card exceeded the maximum number of transactions per day"),
    TRANSACTION_AMOUNT_LIMIT_DAILY("35","The card exceeded the maximum amount per day"),
    TRANSACTION_QUANTITY_LIMIT_MONTHLY("36","The card exceeded the maximum number of transactions per month"),
    TRANSACTION_AMOUNT_LIMIT_MONTHLY("37","The card exceeded the maximum amount per month"),
    MIN_TRANSACTION_AMOUNT("38","The card exceeded the minimun amount per transaction"),
    MAX_TRANSACTION_AMOUNT("39","The card exceeded the maximum amount per transaction"),
         
    //Mensajes Status Card
    CARD_EXISTS("50","The Card exists in the Card Manager System database"),
    CARD_NOT_EXISTS("51","The card does not exist in the Card Manager System database"),  
    CARD_NOT_VALIDATE("52","The card did not pass the validations"),
    
    //validaciones para calcular bonificaciones
    PROGRAM_LOYALTY_BY_CARD_NOT_EXISTS("60","The card does not have a loyalty program associated with it"), 
    TRANSACTION_MANAGER_BY_NUMBER_NOT_EXISTS("60","The transaction number does not exist"), 
    
    //validacion de Tarjeta
    THE_CARDHOLDER_IS_VERIFIED("145","Cardholder data has been successfully verified"),
    THE_CARDHOLDER_NOT_MATCH("145","Cardholder details do not match"),
    CARD_OWNER_NOT_FOUND("146","Error finding card owner"),
    CARD_NOT_FOUND("147","Error finding the card to verify cardholder data"),
    THE_CARD_IS_NOT_ACTIVE("148", ""),
    THE_IDENTIFICATION_NUMBER_IS_VERIFIED("150","The identification number matches"),
    THE_IDENTIFICATION_NUMBER_NOT_MATCH("149","The identification number does not match"),
    THE_CARD_STATUS_NOT_BE_CHANGED("151",""),
    CVV_DIFFERENT("152","The CVV is Different"),
    DATE_DIFFERENT("153","Expiration Date is Different"),
    ACCOUNT_NOT_ASSOCIATED("154","There is no Account Associated with the Card"),
    USER_HAS_NOT_BALANCE("155","The user has no balance available to complete the transaction"),
    THE_CARD_HAS_NO_MOVEMENTS("155","The card has no movements"),
    
    //Activación de Tarjeta
    ACTIVE_CARD_NO("20","The card cannot be activated. Security responses do not match"),
    ACTIVE_CARD_YES("21","The card was activated"),
    DATE_BIRTH_NOT_MATCH("22","The card was not active because the client's date of birth did not match"),
    EMAIL_CUSTOMER_NOT_MATCH("23","The card was not active because the customer's email did not match"),
    PHONE_CUSTOMER_NOT_MATCH("24","The card was not active because the customer's phone did not match"),
    CARD_ALREADY_ACTIVE("25","The card is already active"),

    
    //Validaciones de la tarjeta
    MINIMUM_AMOUNT_NOT_ALLOWED("156","The minimum amount is not allowed"),
    MAXIMUM_AMOUNT_IS_NOT_ALLOWED("157","The maximum amount is not allowed"),
    
    
    //Cáculo Tarifas CMS
    RATE_BY_CARD_NOT_FOUND("401","The rate for the card has not been defined"),
    RATE_BY_PRODUCT_NOT_FOUND("403","The rate for the product has not been defined"),
    
    //Algoritmo LUNH
    
    
    //Consulta de Saldo
    INVALID_CARD("70","INVALID CARD");
           
    private final String code;
    private final String message;
        
    private ResponseCode(String code,String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }

}
