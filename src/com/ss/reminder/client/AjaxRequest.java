package com.ss.reminder.client;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;

public class AjaxRequest implements RequestCallback {

	private AjaxCallback callback;
	private String data;

	public AjaxRequest(String data) {
		this.data = data;
	}

	public void send(AjaxCallback callback) {
		this.callback = callback;
		RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/cmd");
		rb.setHeader("Content-Type", "application/x-www-form-urlencoded");
		rb.setRequestData(data);
		rb.setCallback(this);
		try {
			rb.send();
		} catch (RequestException e) {
			callback.onError(e);
		}
	}

	/**
	 * {@code RequestCallback}
	 */
	@Override
	public void onResponseReceived(Request request, Response response) {
		String responseText = response.getText();
		try {
			JSONObject json = JSONHelper.getJsonFromString(responseText);
			String error = JSONHelper.getString(json, "error");

			if (UIHelper.isEmpty(error)) {

				String info = JSONHelper.getString(json, "info");
				callback.onResponse(info);

			} else if ("server.error".equals(error)) {
				Window.alert("Server error.");
			} else {
				callback.onApplicationError(error);
			}

		} catch (Exception e) {
			callback.onError(e);
		}
	}

	/**
	 * {@code RequestCallback}
	 */
	@Override
	public void onError(Request request, Throwable exception) {
		callback.onError(exception);
	}

	// ------------------------------------------------

	public interface AjaxCallback {
		void onResponse(String msg);

		void onApplicationError(String error);

		void onError(Throwable exception);
	}

}
