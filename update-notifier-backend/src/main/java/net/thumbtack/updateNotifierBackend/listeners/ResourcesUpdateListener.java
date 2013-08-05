package net.thumbtack.updateNotifierBackend.listeners;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;

public class ResourcesUpdateListener {
	
	private static final Logger log = LoggerFactory.getLogger(ResourcesUpdateListener.class);
	private static Properties PROPS;
	private static final String FROM = "Update_Notifier";
    // TODO change SMTP server
	private static final String HOST = "mail.smtp.host";
	
	static {
        PROPS = new Properties();
 
        PROPS.put("mail.smtp.host", HOST);
        
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
        
        Session session = Session.getInstance(PROPS);
 
        try {
            Message msg = new MimeMessage(session);
 
            msg.setFrom(new InternetAddress(FROM));
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("Resource " + resource.getUrl() + "updated");
            msg.setSentDate(new Date());
 
            msg.setText("Empty body");
 
            Transport.send(msg);
    		return true;
        }
        catch (MessagingException mex) {
        	return false;
        }
	}
	
}
