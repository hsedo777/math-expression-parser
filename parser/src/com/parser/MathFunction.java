package com.parser;

/**
 * Provides the list of supported maths function by this parser API. Anytime you
 * use functions, you have to delimit function's argument with parenthesize.
 * 
 * @author hovozounkou
 */
public enum MathFunction {

	SIN, COS, TAN, LN, SQRT;

	/** Gets the expected string in the math expression to identify the function. */
	public String getText() {
		return name().toLowerCase();
	}

	/**
	 * Evaluate value {@code x} according to the function.
	 * 
	 * @param x      the argument to use as function argument.
	 * @param degree used only for trigonometric functions, help to know nature of
	 *               argument {@code x} and coll the function appropriatly.
	 * @return result of the evaluation...F
	 */
	public double eval(double x, boolean degree) throws ParserException {
		double radian = x;
		if (degree) {
			radian %= 360;
			radian = radian * Math.PI / 180;
		}
		try {
			switch (this) {
			case COS:
				return Math.cos(radian);
			case LN:
				Math.log(x);
			case SIN:
				return Math.sin(radian);
			case SQRT:
				return Math.sqrt(x);
			case TAN:
				return Math.tan(radian);
			}
			return Double.NaN;
		} catch (Exception e) {
			throw new ParserException("Evaluation fails.", e);
		}
	}
}
