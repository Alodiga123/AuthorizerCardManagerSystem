package com.alodiga.authorizer.cms.topup;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


public class TopUpInfo  implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    private String coutryId;
    private String opertador;
    private String operatorid;
    private String destinationCurrency;
    private Boolean isOpenRange;
    private Float commissionPercent;
    private Timestamp creationDate;
    private Float denomination;
    private Float denominationSale;
    private Float denominationReceiver;
    private Float wholesalePrice;
    private String skuid;
    private Float minimumAmount;
    private Float maximumAmount;
    private Float increment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCoutryId() {
        return coutryId;
    }

    public void setCoutryId(String coutryId) {
        this.coutryId = coutryId;
    }

    public String getOpertador() {
        return opertador;
    }

    public void setOpertador(String opertador) {
        this.opertador = opertador;
    }

    public String getOperatorid() {
        return operatorid;
    }

    public void setOperatorid(String operatorid) {
        this.operatorid = operatorid;
    }

    public String getDestinationCurrency() {
        return destinationCurrency;
    }

    public void setDestinationCurrency(String destinationCurrency) {
        this.destinationCurrency = destinationCurrency;
    }

    public Boolean getIsOpenRange() {
        return isOpenRange;
    }

    public void setIsOpenRange(Boolean isOpenRange) {
        this.isOpenRange = isOpenRange;
    }

    public Float getCommissionPercent() {
        return commissionPercent;
    }

    public void setCommissionPercent(Float commissionPercent) {
        this.commissionPercent = commissionPercent;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Float getDenomination() {
        return denomination;
    }

    public void setDenomination(Float denomination) {
        this.denomination = denomination;
    }

    public Float getWholesalePrice() {
        return wholesalePrice;
    }

    public void setWholesalePrice(Float wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

    public String getSkuid() {
        return skuid;
    }

    public void setSkuid(String skuid) {
        this.skuid = skuid;
    }

    public Float getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(Float minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public Float getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(Float maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public Float getIncrement() {
        return increment;
    }

    public void setIncrement(Float increment) {
        this.increment = increment;
    }

    public Float getDenominationSale() {
        return denominationSale;
    }

    public void setDenominationSale(Float denominationSale) {
        this.denominationSale = denominationSale;
    }

    public Float getDenominationReceiver() {
        return denominationReceiver;
    }

    public void setDenominationReceiver(Float denominationReceiver) {
        this.denominationReceiver = denominationReceiver;
    }
    
    
}
