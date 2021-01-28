package com.alodiga.authorizer.cms.responses;
import com.cms.commons.models.TransactionsManagement;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionFeesResponse extends Response {

	public TransactionsManagement transactionsManagement;
        private Float transactionFeesAmount;
        
	public TransactionFeesResponse() {
		super();
	}
	
	public TransactionFeesResponse(ResponseCode code) {
		super(new Date(), code.getCode(), code.name());
		this.transactionsManagement = null;
	}
        
        public TransactionFeesResponse(String code, String message) {
		super(new Date(), code, message);
		this.transactionsManagement = null;
	}        

    public TransactionFeesResponse(String code, String mensaje, Float transactionFeesAmount, TransactionsManagement transactionsManagement) {
        super(new Date(), code, mensaje);
        this.transactionFeesAmount = transactionFeesAmount;
        this.transactionsManagement = transactionsManagement;
    }

    public TransactionsManagement getTransactionsManagement() {
        return transactionsManagement;
    }

    public void setTransactionsManagement(TransactionsManagement transactionsManagement) {
        this.transactionsManagement = transactionsManagement;
    }

    public Float getTransactionFeesAmount() {
        return transactionFeesAmount;
    }

    public void setTransactionFeesAmount(Float transactionFeesAmount) {
        this.transactionFeesAmount = transactionFeesAmount;
    }
       
}
