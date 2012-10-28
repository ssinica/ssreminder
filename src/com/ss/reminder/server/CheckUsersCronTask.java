package com.ss.reminder.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Objectify;
import com.ss.reminder.server.entity.UserEntity;
import com.ss.reminder.server.util.ServerUtil;

@SuppressWarnings("serial")
public class CheckUsersCronTask extends HttpServlet {

	private static final Logger log = Logger.getLogger(CheckUsersCronTask.class.getName());

	private static final int HOUR_TO_SEND_EMAILS = 9; // send emails each morning 9:00

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		DateTime dtCurrentUtc = new DateTime(System.currentTimeMillis(), DateTimeZone.UTC);
		int utcHour = dtCurrentUtc.getHourOfDay();

		long currentTime = ServerUtil.roundToHour(dtCurrentUtc).getMillis();

		// for which Time Zone we should send emails?
		int tz = HOUR_TO_SEND_EMAILS - utcHour;

		// load users which should be notified
		Objectify obf = ServerUtil.obf();
		List<UserEntity> users = obf.query(UserEntity.class).filter("tz =", tz).limit(100).list();
		for (UserEntity u : users) {
			if (u.getLastNotifyTime() != currentTime) {
				log.log(Level.INFO, "Scheduled email for user " + u.getEmail());
				scheduleEmail(u, currentTime);
			} else {
				log.log(Level.INFO, "Already scheduled email notification for user " + u.getEmail() + ", which is in tz " + tz + ", will remove it from list.");
			}
		}
		if (users.size() == 0) {
			log.log(Level.INFO, "No users to send emails for tz " + tz);
		}

	}

	private void scheduleEmail(UserEntity u, long currentTime) {
		TaskOptions task = TaskOptions.Builder.withUrl("/tasks/sendemail");
		task = task.param("email", u.getEmail());
		task = task.param("time", Long.toString(currentTime));
		Queue qu = QueueFactory.getQueue("schedulledemail");
		qu.add(task);
	}

}
