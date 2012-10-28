package com.ss.reminder.client;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Element;

/**
 * <b>This is preferred way to implement widgets</b><br><br>
 * Use this widget as parent of your widget, if you want to catch clicks on elements with UID defined.
 * @author sergey.sinica
 *
 */
public abstract class AbstractUIDComposite extends AbstractComposite {

	public abstract void onUidClick(String uid, JSONObject json, int left, int top);

	@Override
	public void onClick(Element el, int left, int top) {

		Element uidEl = findUidEl(el);
		if (uidEl == null) {
			return;
		}

		String uid = UIDHelper.getUID(uidEl);
		if (uid != null) {
			JSONObject json = UIDHelper.getUidDataAsJson(uidEl);
			onUidClick(uid, json, left, top);
		}

	}

	/**
	 * Find first element with UID, by looking up in DOM tree starting from specified element.
	 * Search finishes when element is found or is reached top element of this widget.
	 * @param from
	 * @return
	 */
	private Element findUidEl(Element from) {
		String uid = UIDHelper.getUID(from);
		if (uid != null) {
			return from; // bingo!
		} else if (from.equals(getElement())) {
			return null; // it is the top element, but it does not have UID - element with UID not found.
		} else {
			com.google.gwt.dom.client.Element parent = from.getParentElement();
			if (parent == null) {
				return null; // no parent
			} else {
				return findUidEl((Element) parent.cast()); // move to parent and repeat
			}
		}

	}
}
