package com.ss.reminder.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class WelcomeServlet extends HttpServlet {

	public static final int STATIC_FILES_VERSION = 5;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF8");
		resp.addHeader("Pragma", "no-cache");
		resp.addHeader("Cache-Control", "no-cache");
		resp.addHeader("Cache-Control", "no-store");
		resp.addHeader("Cache-Control", "must-revalidate");
		resp.addHeader("Expires", "Mon, 8 Aug 2006 10:00:00 GMT");
		PrintWriter wr = resp.getWriter();
		
		String css = "";
		css += "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/bootstrap.min.css?v=" + STATIC_FILES_VERSION + "\">";
		css += "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/datepicker.css?v=" + STATIC_FILES_VERSION + "\">";
		css += "<link type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css?v=" + STATIC_FILES_VERSION + "\">";

		String title = "SSReminder";

		// google analitics
		String gag = "";
		String gagDomain = "";
		String gagTrack = "";
		if (!StringUtils.isEmpty(gagDomain) && !StringUtils.isEmpty(gagTrack)) {
			gag = genGoogleAnaliticsScript(gagTrack, gagDomain);
		}

		String html = "<!doctype html>"
		        + "<html>"
		        + "<head>"
		        + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">"
		        + css
		        + "<title>" + title + "</title>"
		        + "<script type=\"text/javascript\" language=\"javascript\" src=\"reminder/reminder.nocache.js\"></script>"
		        + "<script type=\"text/javascript\" language=\"javascript\" src=\"js/jquery.js?v" + STATIC_FILES_VERSION + "\"></script>"
		        + "<script type=\"text/javascript\" language=\"javascript\" src=\"js/datepicker.js?v" + STATIC_FILES_VERSION + "\"></script>"
		        + "<script type=\"text/javascript\" language=\"javascript\" src=\"js/remainder.js?v" + STATIC_FILES_VERSION + "\"></script>"
		        + gag
		        + "</head>"
		        + "<body>"		        		        
		        + "<iframe src=\"javascript:''\" id=\"__gwt_historyFrame\" tabIndex='-1' style=\"position:absolute;width:0;height:0;border:0\"></iframe>"
		        + "<noscript>"
		        + "<div style=\"width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif\">"
		        + "Your web browser must have JavaScript enabled" + "in order for this application to display correctly." + "</div>"
		        + "</noscript>"
		        + "</body>" 
		        + "</html>";

		wr.print(html);
		wr.flush();
	}

	private String genGoogleAnaliticsScript(String trackingCode, String domainName) {
		StringBuilder sb = new StringBuilder();
		sb.append("<script type='text/javascript'>");
		sb.append("var _gaq = _gaq || [];");
		sb.append("_gaq.push(['_setAccount', '" + trackingCode + "']);");
		sb.append("_gaq.push(['_setDomainName', '" + domainName + "']);");
		sb.append("_gaq.push(['_trackPageview']);");
		sb.append("(function() {");
		sb.append("var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;");
		sb.append("ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';");
		sb.append("var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);");
		sb.append("})();");
		sb.append("</script>");
		return sb.toString();
	}

}
