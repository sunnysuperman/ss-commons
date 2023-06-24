package com.sunnysuperman.commons.exception;

public class UnexpectedException extends RuntimeException {

	public UnexpectedException() {
		super();
	}

	public UnexpectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnexpectedException(String message) {
		super(message);
	}

	public UnexpectedException(Throwable cause) {
		super(cause);
	}

}
