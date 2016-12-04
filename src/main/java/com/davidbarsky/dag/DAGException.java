package com.davidbarsky.dag;

public class DAGException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DAGException(String msg) {
		super(msg);
	}

	public DAGException() {super("Invarient Broken!"); }
}
