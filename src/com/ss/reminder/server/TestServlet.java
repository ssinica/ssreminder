package com.ss.reminder.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ss.reminder.server.entity.TaskEntity;
import com.ss.reminder.server.entity.UserEntity;

@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {

	private ReminderService reminderService = new ReminderServiceImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		UserEntity user = reminderService.loadUser("test2@email.ru");
		List<TaskEntity> tasks = reminderService.listTasks("test2@email.ru");

		String emailHtml = EmailFactory.genListEmail(user, tasks);
		//String emailHtml = EmailFactory.genVerifyEmail(user.getEmail(), user.getValidateKey());
		
		resp.setCharacterEncoding("UTF8");
		resp.addHeader("Pragma", "no-cache");
		resp.addHeader("Cache-Control", "no-cache");
		resp.addHeader("Cache-Control", "no-store");
		resp.addHeader("Cache-Control", "must-revalidate");
		resp.addHeader("Expires", "Mon, 8 Aug 2006 10:00:00 GMT");
		PrintWriter wr = resp.getWriter();
		
		String title = "SSReminder-test";

		String html = "<!doctype html>"
		        + "<html>"
		        + "<head>"
		        + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">"
		        + "<title>" + title + "</title>"		       
		        + "</head>"
		        + "<body>"		        		        
		        + emailHtml
		        + "</body>" 
		        + "</html>";

		wr.print(html);
		wr.flush();
	}

}
