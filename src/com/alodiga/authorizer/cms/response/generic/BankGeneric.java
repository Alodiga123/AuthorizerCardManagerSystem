/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alodiga.authorizer.cms.response.generic;

import java.io.Serializable;


public class BankGeneric implements Serializable {
    
   private String id;
   private String name;
   private String aba;

    public BankGeneric(String id, String name, String aba) {
        this.id = id;
        this.name = name;
        this.aba = aba;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAba() {
        return aba;
    }

    public void setAba(String aba) {
        this.aba = aba;
    }
    
    
}
