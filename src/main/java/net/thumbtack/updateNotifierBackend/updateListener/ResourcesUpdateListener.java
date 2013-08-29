package net.thumbtack.updateNotifierBackend.updateListener;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.entities.User;
import net.thumbtack.updateNotifierBackend.database.exceptions.DatabaseException;

public class ResourcesUpdateListener {
	//REVU: extract authentification credentails to paramaters file.
	private static final String PASS = "updatenotifierpass";
	private static final String USER = "UpdateNotifier@mail.ru";
	private static final Logger log = LoggerFactory
			.getLogger(ResourcesUpdateListener.class);
	private static Properties PROPS = null;
	private static final String FROM = USER;
	private static final String HOST = "smtp.mail.ru";
	private static Address addressFrom;
	private static Authenticator authenticator;
	private static ResourcesUpdateListener updateListener = null;
	
	public static ResourcesUpdateListener getInstance() {
		if (updateListener == null) {
			updateListener = new ResourcesUpdateListener();
		}
		return updateListener;
	}
	
	private ResourcesUpdateListener() {
		PROPS = new Properties();

		PROPS.put("mail.transport.protocol", "smtp");
		PROPS.put("mail.smtp.host", HOST);
		PROPS.put("mail.smtp.auth", "true");

		if (log.isDebugEnabled()) {
			PROPS.put("mail.debug", "true");
		}
		authenticator = new SMTPAuthenticator();
		try {
			addressFrom = new InternetAddress(FROM);
		} catch (AddressException e) {
			// Ignore. Address from must be valid
			log.error("Address from no valid!", e);
		} 
	}

	public void onResourceUpdate(Resource resource) {
		log.trace("Resource updated URL =  " + resource.getUrl());
		if (!sendEmail(resource)) {
			log.error("SendEmail failed.");
		}
	}

	private boolean sendEmail(Resource resource) {
		String to = null;
		User user = null;
		try {
			user = UpdateNotifierBackend.getDatabaseService().getUserEmailById(
					resource.getUserId());
			to = user.getEmail();
		} catch (DatabaseException e) {
			log.error("Get email to {} failed", to);
			return false;
		}
		log.debug("Email to {}", to);
		Session session = Session.getInstance(PROPS, authenticator);

		try {
			Message msg = new MimeMessage(session);

			msg.setFrom(addressFrom);
			InternetAddress[] address = { new InternetAddress(to) };
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject("Resource " + resource.getUrl() + " was updated");
			msg.setSentDate(new Date());

			msg.setText("Dear "+user.getName()+", your resource "+resource.getName()+" was changed :-)");

			Transport.send(msg);
			return true;
		} catch (MessagingException e) {
			log.error("MessagingException", e);
			return false;
		}
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			// TODO may be move this allocation to the constructor?
			return new PasswordAuthentication(USER, PASS);
		}
	}

}
