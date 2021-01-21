/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alodiga.authorizer.cms.ws.enumeradas;

/**
 *
 * @author usuario
 */
public enum WithdrawalType {
    MANUAL("1"),
    AUTOMATIC("2");
    
    private String codigo;
    
    WithdrawalType(String codigo) { 
       this.codigo = codigo;
    }
   
    public String getCodigo(){
        return codigo;
    }
    
}
