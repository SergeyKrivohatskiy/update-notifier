package net.thumbtack.updateNotifierBackend.listeners;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;

public class ResourcesUpdateListener {
	
	private static final String PASS = "updatenotifierpass";
	private static final String USER = "UpdateNotifier@mail.ru";
	private static final Logger log = LoggerFactory.getLogger(ResourcesUpdateListener.class);
	private static Properties PROPS;
	private static final String FROM = USER;
	private static final String HOST = "smtp.mail.ru";
	
	static {
        PROPS = new Properties();

        PROPS.put("mail.transport.protocol", "smtp");
        PROPS.put("mail.smtp.host", HOST);
        PROPS.put("mail.smtp.auth", "true");
        
        if(log.isDebugEnabled()) {
            PROPS.put("mail.debug", "true");
        }
	}
	
	public void onResourceUpdate(Resource resource) {
		log.trace("Resource updated URL =  " + resource.getUrl());
		if(!sendEmail(resource)) {
			log.error("SendEmail failed.");
		}
	}

	private boolean sendEmail(Resource resource) {
        String to = UpdateNotifierBackend.getDatabaseService().
				getUserEmailById(resource.getId());
        if(to == null) {
        	return false;
        }
        
        Session session = Session.getInstance(PROPS, new SMTPAuthenticator());
 
        try {
            Message msg = new MimeMessage(session);
 
            msg.setFrom(new InternetAddress(FROM));
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("Resource " + resource.getUrl() + " was updated");
            msg.setSentDate(new Date());
 
            msg.setText("Empty body");
 
            Transport.send(msg);
    		return true;
        } catch (MessagingException mEx) {
        	log.error("MessagingException", mEx);
        	return false;
        }
	}
	
	private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
           return new PasswordAuthentication(USER, PASS);
        }
    }
	
}
