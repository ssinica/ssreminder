package com.ss.reminder.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SendVerifyEmailTask extends AbstractEmailTask {

	private static final Logger log = Logger.getLogger(SendVerifyEmailTask.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email = req.getParameter("email");
		String code = req.getParameter("code");
		String html = EmailFactory.genVerifyEmail(email, code);
		try {
			sendEmail(html, email);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to send email to user: " + email);
		}
	}


}
