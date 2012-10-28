package com.ss.reminder.client;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;

/**
 * Utility class to do misc stuff with UID.
 * @author sergey.sinica
 *
 */
public class UIDHelper {

	private static final String UID_ATTR = "uid";
	private static final String UID_DATA_ATTR = "data-uid";

	private UIDHelper() {

	}

	public static String getUidAttr() {
		return UID_ATTR;
	}

	public static String getUidDataAttr() {
		return UID_DATA_ATTR;
	}

	public static String getUID(Element el) {
		if (el == null) {
			return null;
		}
		String attr = el.getAttribute(UID_ATTR);
		return attr == null || "".equals(attr) ? null : attr;
	}

	public static String getUidData(Element el) {
		if (el == null) {
			return null;
		}
		String data = el.getAttribute(UID_DATA_ATTR);
		return data == null || "".equals(data) ? null : data;
	}

	public static JSONObject getUidDataAsJson(Element el) {
		String data = getUidData(el);
		if (data == null) {
			return null;
		}
		JSONValue jsonValue = JSONParser.parseStrict(data);
		if (jsonValue == null) {
			return null;
		} else {
			return jsonValue.isObject();
		}
	}

	public static void setUID(String uid, com.google.gwt.dom.client.Element el) {
		setUID(uid, (Element) el.cast());
	}

	public static void setUID(String uid, Element el) {
		el.setAttribute(UID_ATTR, uid);
	}

	public static void setUidData(String data, Element el) {
		el.setAttribute(UID_DATA_ATTR, data);
	}

	public static String genUID(String uid) {
		return UID_ATTR + "='" + uid + "'";
	}

	public static String genData(String json) {
		return UID_DATA_ATTR + "='" + json + "'";
	}


}
