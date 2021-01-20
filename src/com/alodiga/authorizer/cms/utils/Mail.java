/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alodiga.authorizer.cms.utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author ltoro
 */
public class Mail implements Serializable {
    
    private Mail mail;
    /** Tema o asunto del correo. */
    private String subject;
    /** Cuerpo del correo. */
    private String body;
    /** Correo fuente. */
    private String from;
    /** Lista de destinos. */
    private ArrayList<String> to;

    public Mail(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public Mail(Mail mail) {
        this.mail = mail;
    }

       public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public ArrayList<String> getTo() {
        return to;
    }

    public void setTo(ArrayList<String> to) {
        this.to = to;
    }
    
}
