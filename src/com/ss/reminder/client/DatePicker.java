package com.ss.reminder.client;

import com.google.gwt.core.client.JavaScriptObject;

public class DatePicker {

	private DatePicker() {

	}

	public static native void initCalendar() /*-{
		$wnd.remainder.init();
	}-*/;

	public static native JavaScriptObject getSelectedDate() /*-{
		return $wnd.remainder.getDate();
	}-*/;

}
