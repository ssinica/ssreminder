package com.ss.reminder.server;

public class ServiceFactory {

	private static ReminderServiceImpl rs;

	private ServiceFactory() {

	}

	public static ReminderService getReminderService() {
		if (rs == null) {
			rs = new ReminderServiceImpl();
		}
		return rs;
	}

}
