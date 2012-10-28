package com.ss.reminder.server;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.ss.reminder.server.entity.TaskEntity;
import com.ss.reminder.server.entity.UserEntity;
import com.ss.reminder.server.util.ServerUtil;

public class EmailFactory {

	private EmailFactory() {

	}

	public static String genVerifyEmail(String email, String code) {
		String href = "http://reminder.sinica.me/api?action=verify&email=" + email + "&code=" + code;
		StringBuilder sb = new StringBuilder();
		sb.append("<div>");
		sb.append("<div>Please verify your SSReminder account clicking on link: </div>");
		sb.append("<a href='" + href + "'>Verify my account now</a>");
		sb.append("</div>");
		return genEmail(sb.toString(), email, code);
	}

	public static String genListEmail(UserEntity user, List<TaskEntity> tasks) {

		DateTime dtCurrent = new DateTime(System.currentTimeMillis(), DateTimeZone.UTC);
		long currentTime = ServerUtil.round(dtCurrent).getMillis();

		StringBuilder sb = new StringBuilder();
		sb.append("<div>");

		sb.append("<div style='padding-bottom:10px;'>Status on " + ServerUtil.dateToString(currentTime, 0) + ":</div>");

		if (CollectionUtils.isEmpty(tasks)) {
			sb.append("<span>No tasks</span>");
		} else {

			sb.append("<table width='100%' height='100%' cellpadding='0' cellspacing='0'>");
			int i = 0;
			for (TaskEntity task : tasks) {
				sb.append(genTaskHtml(user, task, currentTime, i % 2 > 0));
				i++;
			}
			sb.append("</table>");

		}

		sb.append("</div>");

		return genEmail(sb.toString(), user.getEmail(), user.getValidateKey());
	}

	private static String genTaskHtml(UserEntity user, TaskEntity task, long currentTime, boolean second) {

		Long startsAt = task.getStartAt();
		long period = startsAt - currentTime;

		boolean isNegative = false;
		if (period < 0) {
			isNegative = true;
			period = Math.abs(period);
		}

		long oneDay = TimeUnit.DAYS.toMillis(1);
		long days = period / oneDay;

		String tdStyle = "style='font-size:16px;";
		tdStyle += second ? "background-color:#f5f5f5;" : "";
		tdStyle += "padding:10px;vertical-align:middle;";

		String td = tdStyle + "";
		String td2 = tdStyle + "text-align:center;font-size:24px;";
		String td3 = tdStyle + "text-align:center;font-size:12px;cursor:pointer;";

		if (days < 5 || isNegative) {
			td2 += "color:#FF0000;";
		} else if (days < 30) {
			td2 += "color:#4B83AB;";
		} else {
			td2 += "color:#5BB75B;";
		}
		
		td += "'";
		td2 += "'";
		td3 += "'";

		String email = user.getEmail();
		String code = user.getSecret();
		Long taskId = task.getId();
		String removeTaskHref = "http://reminder.sinica.me/api?action=remtask&email=" + email + "&code=" + code + "&taskId=" + taskId;
		String remHtml = "<a href='" + removeTaskHref + "' style='color:#999999;'>remove</a>";

		StringBuilder sb = new StringBuilder();
		sb.append("<tr>");
			sb.append("<td " + td + " width='50%'>" + task.getDescription() + " (" + ServerUtil.dateToString(startsAt, 0) + ")" + "</td>");
			sb.append("<td " + td2 + " width='30%'>" + (isNegative ? "-" : "") + days + "</td>");
			sb.append("<td " + td3 + " width='20%'>" + remHtml + "</td>");
		sb.append("</tr>");
		return sb.toString();
	}


	private static String genEmail(String innerHtml, String email, String code) {
		StringBuilder sb = new StringBuilder();

		sb.append("<div style='padding:0;margin:0;color:#666;' bgcolor='#f5f5f5'>");

		sb.append("<table width='100%' height='100%' cellpadding='0' cellspacing='0' bgcolor='#f5f5f5'>");

		sb.append("<tbody><tr>");
		sb.append("<td height='20'>&nbsp;</td>");
		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td align='center' valign='top'>");

		sb.append("<table width='700' height='100%' cellpadding='0' cellspacing='0' bgcolor='#ffffff' style='border:1px solid #e9e9e9'>");
		sb.append("<tbody><tr>");
		sb.append("<td style='border-bottom:1px solid #e9e9e9' width='40'>&nbsp;</td>");
		sb.append("<td style='border-bottom:1px solid #e9e9e9' colspan='2' height='104' valign='center' align='left'>");
		sb.append("<a href='http://reminder.sinica.me/' target='_blank' style='color:#AAD722;font-size:32px;font-weight:bold;text-decoration:none;font-family:arial;'>SSReminder</a>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td colspan='3' height='32'>");
		sb.append("&nbsp;");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td width='40'>&nbsp;</td>");
		sb.append("<td align='left'>");

		sb.append(innerHtml);

		sb.append("</td>");
		sb.append("<td width='40'>&nbsp;</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td colspan='3' height='50'>");
		sb.append("&nbsp;");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td width='40'>&nbsp;</td>");
		sb.append("<td align='left'>");
		sb.append("<div style='font-family:Arial,Helvetica,sans-serif;font-size:13px;color:#666;line-height:23px'>");
		sb.append("SSRemainder is a service, which helps you to remember important dates.");
		sb.append("</div>");
		sb.append("</td>");
		sb.append("<td width='40'>&nbsp;</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td colspan='3' height='117'>");
		sb.append("&nbsp;");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</tbody></table>");

		sb.append("</td>");
		sb.append("</tr>");

		String remAccountHref = "http://reminder.sinica.me/api?action=remaccount&email=" + email + "&code=" + code;
		sb.append("<tr>");
		sb.append("<td align='center' valign='top'>");
		sb.append("<a href='" + remAccountHref + "' style='color:#999999;font-size:11px;'>Remove account</a>");
		sb.append("</td>");
		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td align='center' valign='top'>");
		sb.append("<table width='700' height='30' cellpadding='0' cellspacing='0'>");
		sb.append("<tbody><tr>");
		sb.append("<td width='20'>&nbsp;</td>");
		sb.append("<td height='30' valign='bottom' align='left'><div style='font-family:Arial,Helvetica,sans-serif;font-size:11px;color:#999'>© 2012 DoubleS, Inc. All rights reserved.</div></td>");
		sb.append("</tr>");
		sb.append("</tbody></table>");
		sb.append("</td>");
		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td height='10'>");
		sb.append("&nbsp;");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</tbody></table>");

		sb.append("</div>");

		return sb.toString();
	}


}
