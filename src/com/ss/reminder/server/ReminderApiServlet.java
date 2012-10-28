package com.ss.reminder.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.ss.reminder.server.entity.TaskEntity;
import com.ss.reminder.server.entity.UserEntity;
import com.ss.reminder.server.exceptions.ApiApplicationException;
import com.ss.reminder.server.exceptions.ApiInvalidRequestDataException;
import com.ss.reminder.server.util.ServerUtil;

@SuppressWarnings("serial")
public class ReminderApiServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(ReminderApiServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		String resultMsg = "";
		try {
			if ("verify".equals(action)) {
				resultMsg = verifyAccount(req);
			} else if ("remtask".equals(action)) {
				resultMsg = removeTask(req);
			} else if ("remaccount".equals(action)) {
				resultMsg = removeAccount(req);
			} else {
				throw new ApiInvalidRequestDataException();
			}

			printOkResponse(resultMsg, resp);
		} catch (ApiInvalidRequestDataException e) {
			log.log(Level.SEVERE, "Failed to process api request.", e);
			printErrorResponse("Invald request data.", resp);
		} catch (ApiApplicationException e) {
			log.log(Level.SEVERE, "Failed to process api request.", e);
			printErrorResponse(e.getMsg(), resp);
		}
	}


	private String removeAccount(HttpServletRequest req) throws ApiInvalidRequestDataException, ApiApplicationException {
		String email = req.getParameter("email");
		String code = req.getParameter("code");

		if (StringUtils.isEmpty(email) || StringUtils.isEmpty(code)) {
			throw new ApiInvalidRequestDataException();
		}

		ReminderService rs = ServiceFactory.getReminderService();
		UserEntity user = rs.loadUser(email);
		if (user == null) {
			throw new ApiApplicationException("Account " + email + " not found.");
		}
		if (!code.equals(user.getValidateKey())) {
			throw new ApiApplicationException("Incorrect validation code '" + code + "' for account " + email + ".");
		}

		removeUser(email);

		return "Account " + email + " is removed. To restore account, just create and activate it one more time.";
	}

	private String removeTask(HttpServletRequest req) throws ApiInvalidRequestDataException, ApiApplicationException {
		String email = req.getParameter("email");
		String code = req.getParameter("code");
		Long taskId = ServerUtil.parseLong("taskId", req);

		if (StringUtils.isEmpty(email) || StringUtils.isEmpty(code) || taskId == null) {
			throw new ApiInvalidRequestDataException();
		}

		ReminderService rs = ServiceFactory.getReminderService();
		UserEntity user = rs.loadUser(email);
		if (user == null) {
			throw new ApiApplicationException("Account " + email + " not found.");
		}
		if (!code.equals(user.getSecret())) {
			throw new ApiApplicationException("Incorrect secret code for account " + email + ".");
		}

		Objectify obf = ServerUtil.obf();
		TaskEntity task = null;
		try {
			task = obf.get(TaskEntity.class, taskId);
		} catch (NotFoundException e) {
			throw new ApiApplicationException("Task not found!");
		}

		Key<UserEntity> userKey = new Key<UserEntity>(UserEntity.class, email);
		if (!userKey.equals(task.getUserKey())) {
			throw new ApiApplicationException("Invalid user data! No permissions to remove task");
		}

		removeTaskImpl(task);

		decTasksCount(user);

		return "Task '" + task.getDescription() + "' removed!";
	}

	private void removeUser(String email) {
		Objectify obft = ServerUtil.obft();
		try {
			obft.delete(UserEntity.class, email);
			obft.getTxn().commit();
		} finally {
			if (obft.getTxn().isActive()) {
				obft.getTxn().rollback();
			}
		}
	}

	private void removeTaskImpl(TaskEntity task) {
		Objectify obft = ServerUtil.obft();
		try {
			obft.delete(TaskEntity.class, task.getId());
			obft.getTxn().commit();
		} finally {
			if (obft.getTxn().isActive()) {
				obft.getTxn().rollback();
			}
		}
	}

	private void decTasksCount(UserEntity user) {
		int newTaskCount = user.getTasksCount() - 1;
		if (newTaskCount < 0) {
			newTaskCount = 0;
		}
		user.setTasksCount(newTaskCount);

		Objectify obft = ServerUtil.obft();
		try {
			obft.put(user);
			obft.getTxn().commit();
		} finally {
			if (obft.getTxn().isActive()) {
				obft.getTxn().rollback();
			}
		}
	}

	private void validateUser(UserEntity user, String email, String code) throws ApiApplicationException {
		if (user == null) {
			throw new ApiApplicationException("Account " + email + " not found.");
		}
		if (user.getValidated()) {
			throw new ApiApplicationException("Account " + email + " already validated.");
		}
		if (!code.equals(user.getValidateKey())) {
			throw new ApiApplicationException("Incorrect validation code '" + code + "' for account " + email + ".");
		}
	}

	private String verifyAccount(HttpServletRequest req) throws ApiInvalidRequestDataException, ApiApplicationException {
		String email = req.getParameter("email");
		String code = req.getParameter("code");

		if (StringUtils.isEmpty(email) || StringUtils.isEmpty(code)) {
			throw new ApiInvalidRequestDataException();
		}

		ReminderService rs = ServiceFactory.getReminderService();
		UserEntity user = rs.loadUser(email);
		validateUser(user, email, code);


		user.setValidated(true);

		Objectify obf = ServerUtil.obft();
		try {
			obf.put(user);
			obf.getTxn().commit();
		} finally {
			if (obf.getTxn().isActive()) {
				obf.getTxn().rollback();
			}
		}

		ServiceFactory.getReminderService().scheduleReportEmail(email);

		return "Account for email " + email + " activated. Thank you!";
	}
	
	private void printOkResponse(String msg, HttpServletResponse resp) throws IOException {
		String html = "<div class='ok-msg'>" + msg + "</div>";
		printResponse(html, resp);
	}

	private void printErrorResponse(String errorMsg, HttpServletResponse resp) throws IOException {
		String html = "<div class='error-msg'>" + errorMsg + "</div>";
		printResponse(html, resp);
	}

	private void printResponse(String html, HttpServletResponse resp) throws IOException {
		resp.setCharacterEncoding("UTF8");
		resp.addHeader("Pragma", "no-cache");
		resp.addHeader("Cache-Control", "no-cache");
		resp.addHeader("Cache-Control", "no-store");
		resp.addHeader("Cache-Control", "must-revalidate");
		resp.addHeader("Expires", "Mon, 8 Aug 2006 10:00:00 GMT");
		PrintWriter wr = resp.getWriter();
		
		String css = "";
		css += "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/bootstrap.min.css?v=" + WelcomeServlet.STATIC_FILES_VERSION + "\">";
		css += "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css?v=" + WelcomeServlet.STATIC_FILES_VERSION + "\">";

		String title = "SSReminder // API";		

		String result = "<!doctype html>"
		        + "<html>"
		        + "<head>"
		        + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">"
		        + css
		        + "<title>" + title + "</title>"		        
		        + "</head>"
		        + "<body>"		        
		        + "<div class='rmnd'>"
		        + "<div class='rmnd-top'> <a href='http://reminder.sinica.me' class='logo'>SSReminder</a> <div class='logo-text'>service, which helps you to remember important dates</div> </div>"
		        + html
		        + "</div>"
		        + "</body>" 
		        + "</html>";

		wr.print(result);
		wr.flush();
	}

}
