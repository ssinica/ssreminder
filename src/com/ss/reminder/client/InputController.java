package com.ss.reminder.client;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class InputController implements EventListener {

	private InputElement el;
	private DivElement elFld;

	public InputController(InputElement el, DivElement elFld) {
		this.el = el;
		this.elFld = elFld;
		UIHelper.sinkEvents((Element) el.cast(), Event.ONFOCUS | Event.ONBLUR);
		UIHelper.addEventListener((Element) el.cast(), this);
	}

	@Override
	public void onBrowserEvent(Event event) {
		int type = event.getTypeInt();
		if (type == Event.ONFOCUS) {
			onFocus();
		} else if (type == Event.ONBLUR) {
			onBlur();
		}
	}

	private void onFocus() {
		elFld.addClassName("rmnd-fld__focus");
	}

	private void onBlur() {
		if (UIHelper.isEmpty(el.getValue())) {
			elFld.removeClassName("rmnd-fld__focus");
		}
	}

}
