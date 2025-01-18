package com.parser;

/**
 * Base class for exception of the project.
 * 
 * @since 1.0
 * @author hovozounkou
 */
public class ParserException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ParserException() {
		super();
	}

	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParserException(String message) {
		super(message);
	}

	public ParserException(Throwable cause) {
		super(cause);
	}

}
