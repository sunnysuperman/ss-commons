package com.sunnysuperman.commons.exception;

public class Exceptions {

	private Exceptions() {
	}

	public static RuntimeException wrapRuntimeException(Throwable e) {
		if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new UnexpectedException(e);
	}

	public static void rethrowRuntimeException(Throwable e) {
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}
		throw new UnexpectedException(e);
	}

}
