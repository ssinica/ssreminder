package com.ss.reminder.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ss.reminder.client.AjaxRequest.AjaxCallback;

public class ReminderPanel extends AbstractUIDComposite implements KeyDownHandler {

	private static ReminderPanelUiBinder uiBinder = GWT.create(ReminderPanelUiBinder.class);

	interface ReminderPanelUiBinder extends UiBinder<Widget, ReminderPanel> {
	}
	
	private static final String COOKIE_EMAIL = "rme";
	private static final String COOKIE_CODE = "rmc";

	private static final String UID_REMEMBER = "uid-remember";

	@UiField HTMLPanel elMain;	
	@UiField InputElement elEmail;
	@UiField DivElement elEmailFld;
	@UiField InputElement elSecret;
	@UiField DivElement elSecretFld;
	@UiField InputElement elDescription;
	@UiField DivElement elDescriptionFld;	
	@UiField AnchorElement elBtn;

	public ReminderPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		initUI();
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {

	}

	private void initUI() {
		
		UIDHelper.setUID(UID_REMEMBER, elBtn);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				DatePicker.initCalendar();
			}
		});

		String email = Cookies.getCookie(COOKIE_EMAIL);
		String code = Cookies.getCookie(COOKIE_CODE);
		if (!UIHelper.isEmpty(email)) {
			elEmail.setValue(email);
		}
		if (!UIHelper.isEmpty(code)) {
			elSecret.setValue(code);
		}

	}

	@Override
	public void onUidClick(String uid, JSONObject json, int left, int top) {
		if (UID_REMEMBER.equals(uid)) {
			onRememberClick();
		}
	}

	@Override
	public Element getMainIlement() {
		return elMain.getElement();
	}

	private void onRememberClick() {
		final String email = elEmail.getValue();
		final String secret = elSecret.getValue();
		String descr = elDescription.getValue();
		JsDate jsDate = DatePicker.getSelectedDate().cast();
		int year = jsDate.getFullYear();
		int month = jsDate.getMonth();
		int day = jsDate.getDate();

		if (UIHelper.isEmpty(email) || UIHelper.isEmpty(secret) || UIHelper.isEmpty(descr)) {
			Window.alert("All fields are required");
			return;
		}
		
		Date d = new Date();
		String tz = DateTimeFormat.getFormat("z").format(d);
		tz = tz.substring(3);
		if (tz.startsWith("+")) {
			tz = tz.substring(1);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("action=put");
		sb.append("&email=" + email);
		sb.append("&secret=" + secret);
		sb.append("&descr=" + descr);
		sb.append("&year=" + year);
		sb.append("&month=" + month);
		sb.append("&day=" + day);
		sb.append("&tz=" + tz);

		new AjaxRequest(sb.toString()).send(new AjaxCallback() {
			@Override
			public void onResponse(String data) {
				clearForm();
				if ("new.user".equals(data)) {
					Window.alert("Remembered and new account created. To validate account click on validationin link in email we have send to you.");
				} else if ("ok".equals(data)) {
					Window.alert("Remembered!");
				}
				rememberUser(email, secret);
			}			
			@Override
			public void onError(Throwable exception) {
				Window.alert("Ups! " + exception.getMessage());				
			}
			@Override
            public void onApplicationError(String error) {
				if ("not.validated.email".equals(error)) {
					Window.alert("Your account is not validated. To validate account click on validationin link in email we have send to you.");
				} else if ("invalid.secret".equals(error)) {
					Window.alert("Inavlid secret.");
				} else if ("max.tasks.count".equals(error)) {
					Window.alert("You have reached tasks count limits. To add more tasks remove obsolete tasks");
				} else {
					Window.alert("Error: " + error);
				}
            }
			private void clearForm() {
				elDescription.setValue("");
			}
		});
	}

	protected void rememberUser(String email, String secret) {
		Date expireAt = new Date(System.currentTimeMillis() + 5184000000L); // +60 days		
		Cookies.setCookie(COOKIE_EMAIL, email, expireAt);
		Cookies.setCookie(COOKIE_CODE, secret, expireAt);

	}

}
