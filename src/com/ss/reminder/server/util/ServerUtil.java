package com.ss.reminder.server.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.ss.reminder.server.entity.TaskEntity;
import com.ss.reminder.server.entity.UserEntity;

public class ServerUtil {

	private static final Logger log = Logger.getLogger(ServerUtil.class.getName());

	private static Gson GSON;

	private ServerUtil() {
	}
	
	static {
		ObjectifyService.register(UserEntity.class);
		ObjectifyService.register(TaskEntity.class);
	}

	public static Objectify obf() {
		return ObjectifyService.begin();
	}

	public static Objectify obft() {
		return ObjectifyService.beginTransaction();
	}

	public static boolean isDev() {
		return true;
	}

	public static DateTime round(DateTime dt) {
		int dayOfMonth = dt.getDayOfMonth();
		DateTime d = new DateTime(dt.getYear(), dt.getMonthOfYear(), dayOfMonth, 0, 0, 0, 0, dt.getZone());
		return d;
	}

	public static DateTime roundToHour(DateTime dt) {
		int dayOfMonth = dt.getDayOfMonth();
		DateTime d = new DateTime(dt.getYear(), dt.getMonthOfYear(), dayOfMonth, dt.getHourOfDay(), 0, 0, 0, dt.getZone());
		return d;
	}

	public static String dateToString(long t, int timeZoneShift) {
		DateTimeZone timeZone = DateTimeZone.forOffsetHours(timeZoneShift);
		DateTime time = new DateTime(t);
		time = time.withZone(timeZone);

		DateTimeFormatter fmt = ISODateTimeFormat.date();
		return fmt.print(time);
	}

	public static String dateTimeToString(long t, int timeZoneShift) {
		DateTimeZone timeZone = DateTimeZone.forOffsetHours(timeZoneShift);
		DateTime time = new DateTime(t);
		time = time.withZone(timeZone);

		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		return fmt.print(time);
	}

	public static Gson getGson() {
		if (GSON == null) {
			GSON = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
		}
		return GSON;
	}

	public static Long parseLong(String param, HttpServletRequest req) {
		try {
			return Long.parseLong(req.getParameter(param));
		} catch (NumberFormatException e) {
			log.log(Level.SEVERE, "Failed to parse long value from request for param " + param);
			return null;
		}

	}

}
