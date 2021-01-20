/**
 * 
 */
package com.alodiga.authorizer.cms.utils;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

/**
 * @author jonathanxuya
 *
 */
public class EnvioCorreo {

	private static Logger logger = Logger.getLogger(EnvioCorreo.class);

	/**
	 * Envia correo plano a la(s) direccion(es) especificadas en recipients.
	 * 
	 * @param recipients
	 * @param subject
	 * @param message
	 * @param from
	 */
	public static boolean enviarCorreoHtml(String[] recipients, String subject,
			String content, String from, File archivoAdjunto) {
		try {
			Properties props = new Properties();
			// *** TEST ONLY ***//
			
//			  props.put("mail.smtp.auth", "true");
//			  props.put("mail.smtp.starttls.enable", "true");
//			  props.put("mail.smtp.host", "smtp.gmail.com");
//			  props.put("mail.smtp.port", "587"); final String username =
//			  "customer@alodiga.com"; final String password = "iximche#2013!";
//			  Session session = Session.getInstance(props, new
//			  javax.mail.Authenticator() { protected PasswordAuthentication
//			  getPasswordAuthentication() { return new
//			  PasswordAuthentication(username, password); } });
//			 
			// *** TEST ONLY ***//
			// TODO comentar lo de arriba y descomentar lo de abajo para enviar
			// desde mail gmail.
			// *** CODIGO PARA PRODUCCION ***//
			// TODO colocar valores
			String SMTP_HOST_NAME = Utils.obtienePropiedad("mail.smtp.host");
			String SMTP_DEBUG = Utils.obtienePropiedad("mail.debug");
			props.put("mail.smtp.host", SMTP_HOST_NAME);
			props.put("mail.debug", SMTP_DEBUG);
			Session session = Session.getInstance(props);
			// *** CODIGO PARA PRODUCCION ***//

			Message message = new MimeMessage(session);
			InternetAddress addressFrom = new InternetAddress(from);
			message.setFrom(addressFrom);
			InternetAddress[] addressTo = new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i]);
			}
			message.setRecipients(Message.RecipientType.TO, addressTo);
			message.setSubject(subject);
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(content, "text/html");
			MimeMultipart multipart = new MimeMultipart("related");
			multipart.addBodyPart(messageBodyPart);
			if (archivoAdjunto != null) {
				messageBodyPart = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(archivoAdjunto);
				messageBodyPart.setDataHandler(new DataHandler(fds));
				messageBodyPart.setFileName("info.png");
				messageBodyPart.setHeader("Content-ID", "image_cid");
				multipart.addBodyPart(messageBodyPart);
			}
			message.setContent(multipart);
			Transport.send(message);
			return true;
		} catch (Exception e) {
			logger.debug("EnvioCorreo-envioMail:" + e.getMessage());
			return false;
		}
	}
}
