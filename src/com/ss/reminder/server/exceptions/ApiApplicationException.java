package com.ss.reminder.server.exceptions;

public class ApiApplicationException extends Exception {
	private static final long serialVersionUID = 1L;

	private String msg;

	public ApiApplicationException(String msg) {
		this.msg = msg;
	}

	public ApiApplicationException(String msg, Throwable cause) {
		super(cause);
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

}
