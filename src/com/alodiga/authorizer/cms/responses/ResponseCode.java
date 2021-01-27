package com.alodiga.authorizer.cms.responses;

public enum ResponseCode {

    SUCCESS("00",""),
    INVALID_DATA("01",""),
    EXPIRED_PASSWORD("03",""),
    NO_TRUST_IP("04",""),
    INVALID_CREDENTIALS("05",""),
    BLOCKED_USER("06",""),
    INTERNAL_ERROR("99",""),
      
    //Mensajes Status Card
    CARD_EXISTS("50","The Card exists in the Card Manager System database"),
    CARD_NOT_EXISTS("51","The card does not exist in the Card Manager System database"),  
    
    THE_CARDHOLDER_IS_VERIFIED("145","Cardholder data has been successfully verified"),
    THE_CARDHOLDER_NOT_MATCH("145","Cardholder details do not match"),
    CARD_OWNER_NOT_FOUND("146","Error finding card owner"),
    CARD_NOT_FOUND("147","Error finding the card to verify cardholder data"),
    
    
    //Cáculo Tarifas CMS
    RATE_BY_CARD_NOT_FOUND("401",""),
    RATE_BY_PRODUCT_NOT_FOUND("403","");

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
