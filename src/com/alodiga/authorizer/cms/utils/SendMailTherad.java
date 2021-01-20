/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alodiga.authorizer.cms.utils;


import com.ericsson.alodiga.ws.Usuario;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;




/**
 *
 * @author usuario
 */
public class SendMailTherad extends Thread {

      
    
    
    private String idioma;
    private Usuario usuario;
    private String userDestinationName;
    private Integer sendType;
    private String accountBank;
    private Float amountWithdrawal;
    private String emailUser;
    private String conceptTransaction;
    private String referenceNumberOperation;
    private Float amountRecharge;
    private Float amountPayment;
    private Long usuarioDestination;
    private String userSource;
    
    
    
    
     /**
     * Método que devuelve el número de ítems (números aleatorios) existentes en la serie
     * @return El número de ítems (números aleatorios) de que consta la serie
     * @param 
     */
    public SendMailTherad(String idioma, Usuario usuario,Integer sendType ) {
    
      this.idioma = idioma;
      this.usuario = usuario;      
      this.sendType = sendType;
              
        
    }
 
    
     public SendMailTherad(String idioma, String accountBank,Float amountWithdrawal,String conceptTransaction,String userSource ,String emailUser,Integer sendType ) {
    
      this.idioma = idioma;
      this.accountBank = accountBank;
      this.amountWithdrawal = amountWithdrawal;
      this.conceptTransaction = conceptTransaction;
      this.emailUser = emailUser;
      this.userSource = userSource;
      this.sendType = sendType;
              
        
    }
     
     
     public SendMailTherad(String idioma, String referenceNumberOperation,  String conceptTransaction, Float amountRecharge, String userSource ,String emailUser, Integer sendType) {
    
      this.idioma = idioma;
      this.referenceNumberOperation = referenceNumberOperation;
      this.conceptTransaction = conceptTransaction;
      this.amountRecharge = amountRecharge;
      this.emailUser = emailUser;
      this.userSource = userSource;
      this.sendType = sendType;
              
        
    }
     
     
     public SendMailTherad(String idioma, Float amountPayment,  String conceptTransaction, String userDestinationName, String emailUser, Integer sendType) {
    
      this.idioma = idioma;
      this.amountPayment = amountPayment;
      this.conceptTransaction = conceptTransaction;
      this.userDestinationName = userDestinationName;
      this.emailUser = emailUser;
      this.sendType = sendType;
              
        
    }
     
     

    public void run() {
      Mail mail = null; 
        switch (sendType) {
            case Constants.SEND_TYPE_EMAIL_RECHARGE:
                // code block
                mail = Utils.SendMailUserRecharge("ES", referenceNumberOperation, amountRecharge, conceptTransaction, userSource, emailUser);
                break;
            case Constants.SEND_TYPE_EMAIL_WITHDRAWALS:
                // code block
                mail = Utils.SendMailUserWithdrawal("ES", accountBank, amountWithdrawal, conceptTransaction, userSource ,emailUser);
                break;
                         
            case Constants.SEND_TYPE_EMAIL_CHANGE_PASSWORD:
            // code block
                mail = Utils.SendMailUserChangePassword("ES", usuario);
                break;
            case Constants.SEND_TYPE_EMAIL_COMERCE_PEYMENT:
            // code block
                mail = Utils.sendMailUserPaymentCommerce("ES", amountPayment,  conceptTransaction, userDestinationName, emailUser);
                break;
        }
        
        // Hace el envio
        try {
            
            
             //Esto es para hacer el envio con Servidor smtp de alodiga
              EnvioCorreo.enviarCorreoHtml(new String[]{ mail.getTo().get(0)},
                mail.getSubject(), mail.getBody(), Utils.obtienePropiedad("mail.user"), null);
            
            //Esto es para hacer el envio con Servidor smtp de amazon
            //AmazonSESSendMail.SendMail(mail.getSubject(), mail.getBody(), mail.getTo().get(0));
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(SendMailTherad.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

}
