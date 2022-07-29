package com.topcoder.arena.exception;

public class ArenaRuntimeException extends RuntimeException {

	public ArenaRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArenaRuntimeException(String message) {
		super(message);
	}

	public ArenaRuntimeException(Throwable cause) {
		super(cause);
	}

}
