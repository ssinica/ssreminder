package com.ss.reminder.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Objectify;
import com.ss.reminder.server.entity.TaskEntity;
import com.ss.reminder.server.entity.UserEntity;
import com.ss.reminder.server.util.ServerUtil;

@SuppressWarnings("serial")
public class SendEmailTask extends AbstractEmailTask {

	private static final Logger log = Logger.getLogger(SendEmailTask.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email = req.getParameter("email");
		Long time = Long.valueOf(req.getParameter("time"));

		ReminderService rs = ServiceFactory.getReminderService();
		UserEntity user = rs.loadUser(email);

		if (user == null) {
			log.log(Level.WARNING, "No user found with email " + email);
			return;
		}

		List<TaskEntity> tasks = rs.listTasks(email);
		String html = EmailFactory.genListEmail(user, tasks);

		markUserAsSend(user, time);

		try {
			sendEmail(html, email);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to send email to user: " + email);
		}
	}

	private void markUserAsSend(UserEntity u, long time) {
		u.setLastNotifyTime(time);
		Objectify obf = ServerUtil.obft();
		try {
			obf.put(u);
			obf.getTxn().commit();
		} finally {
			if (obf.getTxn().isActive()) {
				obf.getTxn().rollback();
			}
		}
	}
}
