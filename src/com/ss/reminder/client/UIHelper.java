package com.ss.reminder.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class UIHelper {

	private static long nextId = 1;

	private UIHelper() {
	}

	public static String setId(com.google.gwt.user.client.Element el) {
		return setId((Element) el.cast());
	}

	public static String setId(Element el) {
		String id = "rmnd-id-" + nextId;
		el.setId(id);
		nextId++;
		return id;
	}

	public static void sinkEvents(com.google.gwt.user.client.Element element, int eventBits) {
		if (element == null) {
			return;
		}
		int value = eventBits | DOM.getEventsSunk(element);
		DOM.sinkEvents(element, value);
	}

	public static void addEventListener(com.google.gwt.user.client.Element element, EventListener listener) {
		DOM.setEventListener(element, listener);
	}

	public static boolean isEmpty(String val) {
		return val == null || "".equals(val);
	}

	public static Element getElFrom(Event event) {
		EventTarget target = event.getEventTarget();
		if (target == null) {
			return null;
		}
		Element el = target.cast();
		return el;
	}

	public static void removeEventListener(com.google.gwt.user.client.Element element, final EventListener listener) {
		if (listener == null) {
			return;
		}
		DOM.setEventListener(element, null);
	}
}
