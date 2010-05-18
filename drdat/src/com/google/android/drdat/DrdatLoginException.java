package com.google.android.drdat;

/**
 * Complain if we get bad login data.
 */
public class DrdatLoginException extends RuntimeException {

	private static final long serialVersionUID = -1042522693242204427L;
	public DrdatLoginException(String msg) {
		super(msg);
	}
}
