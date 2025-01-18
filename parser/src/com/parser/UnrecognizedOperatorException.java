package com.parser;

/**
 * Thrown when trying to use/apply invalid text sequence as operator.
 * 
 * @since 1.0
 * @author hovozounkou
 */
public class UnrecognizedOperatorException extends ParserException {

	private static final long serialVersionUID = 1L;

	public UnrecognizedOperatorException() {
		super();
	}

	public UnrecognizedOperatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnrecognizedOperatorException(String message) {
		super(message);
	}

	public UnrecognizedOperatorException(Throwable cause) {
		super(cause);
	}

}
