package com.parser;

/**
 * Thrown when number value parsing fails.
 * 
 * @since 1.0
 * @author hovozounkou
 */
public class ValueException extends ParserException {

	public ValueException() {
		super();
	}

	public ValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValueException(String message) {
		super(message);
	}

	public ValueException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = 6619177202064242192L;
}
