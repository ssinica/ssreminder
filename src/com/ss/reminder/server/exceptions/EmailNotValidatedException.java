package com.ss.reminder.server.exceptions;

public class EmailNotValidatedException extends Exception {
	private static final long serialVersionUID = 1L;

	private String email;

	public EmailNotValidatedException(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

}
