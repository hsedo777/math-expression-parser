package com.parser;

public class ExpressionFormatException extends ParserException {

	public ExpressionFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpressionFormatException(String message) {
		super(message);
	}

	public ExpressionFormatException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = 8940151603765126613L;

}
