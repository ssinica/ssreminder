package com.ss.reminder.server;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;

@SuppressWarnings("serial")
public abstract class AbstractEmailTask extends HttpServlet {

	private static final Logger log = Logger.getLogger(AbstractEmailTask.class.getName());

	public void sendEmail(String html, String to) {
		final String noreplyFrom = "sergey@sinica.me";
		final String noreplyFromPersonal = "ssreminder";
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(noreplyFrom, noreplyFromPersonal));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to, ""));
			msg.setSubject("ssreminder");
			msg.setContent(html, "text/html");
			Transport.send(msg);
		} catch (AddressException e) {
			log.severe(e.getMessage());
			throw new RuntimeException("Failed to send email", e);
		} catch (MessagingException e) {
			log.severe(e.getMessage());
			throw new RuntimeException("Failed to send email", e);
		} catch (UnsupportedEncodingException e) {
			log.severe(e.getMessage());
			throw new RuntimeException("Failed to send email", e);
		}
	}

}
