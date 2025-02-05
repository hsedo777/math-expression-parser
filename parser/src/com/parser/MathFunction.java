package com.parser;

import java.io.Serializable;
import java.util.Objects;

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

	/**
	 * Tries to fetch the next occurence of a function in the supplyed expression.
	 * When found, an instance of {@link MathFunctionToken} is returned using index
	 * of first letter of the function in the expression as token's index and the
	 * matched function as token's function.
	 * 
	 * @param from       the index, inclusive, from which to start seaching in the
	 *                   expression.
	 * @param expression the maths expression to parse.
	 * @return the matched token, {@code null} if there isn't matching.
	 */
	public static MathFunctionToken nextFunction(int from, String expression) {
		if (from < 0 || expression == null || expression.isBlank() || expression.length() <= from) {
			return null;
		}
		int i = from;
		String sub;
		expression = expression.toLowerCase();
		do {
			sub = expression.substring(i);
			for (MathFunction f : MathFunction.values()) {
				if (sub.startsWith(f.getText().toLowerCase())) {
					return new MathFunctionToken(f, i);
				}
			}
		} while (++i < expression.length());
		return null;
	}

	/**
	 * This class is a helper to the closest class. Its used to tokenize function
	 * occurences in a maths expression. It wraps the function and its index in the
	 * expression.
	 * 
	 * @author hovozounkou
	 */
	public static final class MathFunctionToken implements Serializable {

		private static final long serialVersionUID = -4045931994861839883L;

		final public MathFunction function;
		final public int index;

		private MathFunctionToken(MathFunction function, int index) {
			super();
			this.function = function;
			this.index = index;
		}

		@Override
		public int hashCode() {
			return Objects.hash(function, index);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MathFunctionToken other = (MathFunctionToken) obj;
			return function == other.function && index == other.index;
		}
	}
}
