package com.ss.reminder.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.ss.reminder.server.entity.UserEntity;
import com.ss.reminder.server.exceptions.EmailNotValidatedException;
import com.ss.reminder.server.exceptions.InvalidSecretException;
import com.ss.reminder.server.exceptions.MaxTasksCountException;
import com.ss.reminder.server.util.ServerUtil;

@SuppressWarnings("serial")
public class ActionsServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ActionsServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		try {
			if ("put".equals(action)) {
				putTask(req, resp);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to process user request", e);
			writeResponse("error", "server.error", resp);
		}
	}

	private void putTask(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email = req.getParameter("email");
		String secret = req.getParameter("secret");
		String descr = req.getParameter("descr");
		Integer year = Integer.valueOf(req.getParameter("year"));
		Integer month = Integer.valueOf(req.getParameter("month"));
		Integer day = Integer.valueOf(req.getParameter("day"));
		int tz = Integer.parseInt(req.getParameter("tz"));

		if (StringUtils.isEmpty(email) || StringUtils.isEmpty(secret) || StringUtils.isEmpty(descr)) {
			throw new ServletException("Invalid values");
		}

		long startsAt = new DateTime(year, month + 1, day, 0, 0, 0, DateTimeZone.UTC).getMillis();

		ReminderService rs = ServiceFactory.getReminderService();

		try {
			UserEntity user = rs.addTask(email, secret, descr, startsAt, tz);

			if (user != null) {
				writeResponse("info", "new.user", resp);
			} else {
				writeResponse("info", "ok", resp);
			}

		} catch (EmailNotValidatedException e) {
			writeResponse("error", "not.validated.email", resp);
		} catch (InvalidSecretException e) {
			writeResponse("error", "invalid.secret", resp);
		} catch (MaxTasksCountException e) {
			writeResponse("error", "max.tasks.count", resp);
		}
	}

	private void writeResponse(String param, String value, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json;charset=utf-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(param, value);
		String json = ServerUtil.getGson().toJson(data);
		
		resp.getWriter().write(json);
	}
}
