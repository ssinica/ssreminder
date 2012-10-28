package com.ss.reminder.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;

/**
 * Abstract composite which supports click handling. Consider to use public implementations.
 * @author sergey.sinica
 * @see AbstractIdComposite
 * @see AbstractUIDComposite
 *
 */
abstract class AbstractComposite extends Composite {

    private EventListener eventListener;

    public abstract Element getMainIlement();

	public abstract void onClick(Element el, int left, int top);

    public int getEventBits() {
        return Event.ONCLICK;
	}

    @Override
    protected void onAttach() {
        super.onAttach();
        if (eventListener == null) {
            eventListener = new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
					switch (DOM.eventGetType(event)) {
					case Event.ONCLICK:
						com.google.gwt.dom.client.Element el = UIHelper.getElFrom(event);
						if (el != null) {
							onClick((Element) el.cast(), event.getClientX(), event.getClientY());
						}
						break;
					}
                }
            };
        }
        UIHelper.sinkEvents(getMainIlement(), getEventBits());
        UIHelper.addEventListener(getMainIlement(), eventListener);
    }

    @Override
    protected void onDetach() {
        UIHelper.removeEventListener(getMainIlement(), eventListener);
        super.onDetach();
    }

}
