/**
 * 
 */
package com.alodiga.authorizer.cms.utils;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * @author jonathanxuya
 *
 */
public class Conexion {

	/**
	 * Obtiene conexion del Pool de Conexiones con el nombre indicado.
	 * 
	 * @param sNombre
	 * @return Connection
	 */
	public static Connection obtenerConexion(String sNombre) {
		try {
			Context ctx = new InitialContext();
//			System.out.println("getConexion: " + sNombre);
//			System.out.println("getConexion: " + "java:comp/env/" + sNombre);
			return ((DataSource) ctx.lookup(sNombre))
					.getConnection();
		} catch (Exception ex) {
			System.out.println("error-getConexion: " + ex.getMessage());
			ex.printStackTrace();
		}
		return null;
	}

}
