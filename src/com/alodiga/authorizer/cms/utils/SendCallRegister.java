package com.alodiga.authorizer.cms.utils;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendCallRegister extends Thread {
    private static final long serialVersionUID = 1L;
    private String movil;
    private String codigo;

    public SendCallRegister(String movil, String codigo_)  {
       this.codigo = codigo_;
       this.movil = movil;
    }

//    public void run() {
//        try {
//            Utils.getWsaPort().realizarLlamadaRegistro(
//                    Utils.obtienePropiedad("usuarioWSA"),
//                    Utils.obtienePropiedad("passwordWSA"), "1", movil, codigo);
//        } catch (RemoteException ex) {
//           ex.printStackTrace();
//        }
//    }
}
