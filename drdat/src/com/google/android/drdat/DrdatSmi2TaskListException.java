package com.google.android.drdat;

/**
 * Complains if you are trying to update a list of tasks without an email / password pair.
 */
public class DrdatSmi2TaskListException extends RuntimeException {

	private static final long serialVersionUID = -2558561030015519763L;
	
	public DrdatSmi2TaskListException(String msg) {
		super(msg);
	}

}
