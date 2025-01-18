package com.parser;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classification of supported operators.
 * 
 * @author hovozounkou
 * @since 1.0
 */
public enum MathOperator {

	PLUS(1, "+"), MINUS(1, "-"), TIMES(2, "*"),

	DIV(2, "/"), POW(3, "^"), MOD(2, "%");

	private MathOperator(int precedence, String text) {
		this.precedence = precedence;
		this.text = text;
	}

	public String getText() {
		return text;
	}

	/**
	 * Tests if the operator may be used as unary.
	 * 
	 * @return {@code true} if and only if this operator may be used as unary
	 *         operator.
	 * @since 1.0
	 */
	public boolean mightUnary() {
		return this == PLUS || this == MINUS;
	}

	/**
	 * Do {@code x op y} and return the result, where {@code op} is this enum
	 * constant.
	 * 
	 * @param x left operand of the operation
	 * @param y right operand of the operation
	 * @return the result of the evaluation.
	 */
	public double eval(double x, double y) {
		switch (this) {
		case DIV:
			return x / y;
		case MINUS:
			return x - y;
		case PLUS:
			return x + y;
		case TIMES:
			return x * y;
		case POW:
			return Math.pow(x, y);
		case MOD:
			return x % y;
		}
		throw new ParserException("Evaluation fails.");
	}

	/**
	 * Tries to fetch the first occurence of an operator, of max precedence, in the
	 * supplyed expression. When found, an instance of {@link OperatorToken} is
	 * returned using operator index as token index and the matched operator as
	 * token operator.
	 * 
	 * @param from       the index, inclusive, from which to start seaching in the
	 *                   expression.
	 * @param expression the maths expression to parse.
	 * @return the matched token, {@code null} if there isn't matching.
	 */
	public static OperatorToken nextMaxPrecedence(int from, String expression) {
		if (from < 0 || expression == null || expression.isBlank() || expression.length() <= from) {
			return null;
		}
		MathOperator[] ops = MathOperator.values();
		int i = from;
		String sub;
		OperatorToken token = null, tmp = null;
		do {
			sub = expression.substring(i);
			for (MathOperator o : ops) {
				if (sub.startsWith(o.text)) {
					tmp = new OperatorToken(i, o);
					if (token == null || o.precedence > token.operator.precedence) {
						token = tmp;
					}
				}
			}
		} while (++i < expression.length());
		return token;
	}

	/**
	 * Tries to fetch the next occurence of an operator in the supplyed text. When
	 * found, an instance of {@link OperatorToken} is returned using operator index
	 * as token index and the matched operator as token operator.
	 * 
	 * @param from       the index, inclusive, from which to start seaching in the
	 *                   expression.
	 * @param expression the maths expression to parse.
	 * @return the matched token, {@code null} if there isn't matching.
	 */
	public static OperatorToken nextOperator(int from, String expression) {
		if (from < 0 || expression == null || expression.isBlank() || expression.length() <= from) {
			return null;
		}
		int i = from;
		String sub;
		do {
			sub = expression.substring(i);
			for (MathOperator o : MathOperator.values()) {
				if (sub.startsWith(o.text)) {
					return new OperatorToken(i, o);
				}
			}
		} while (++i < expression.length());
		return null;
	}

	/**
	 * Tries to fetch the last occurence of an operator in the supplyed text. When
	 * found, an instance of {@link OperatorToken} is returned using operator index
	 * as token index and the matched operator as token operator.
	 * 
	 * @param to         the index, inclusive, to which to stop seaching in the
	 *                   expression.
	 * @param expression the maths expression to parse.
	 * @return the matched token, {@code null} if there isn't matching.
	 */
	public static OperatorToken lastOperator(int to, String expression) {
		if (to < 0 || expression == null || expression.isBlank() || expression.length() <= to) {
			return null;
		}
		int i = expression.length() - 1;
		String sub;
		do {
			sub = expression.substring(i);
			for (MathOperator o : MathOperator.values()) {
				if (sub.startsWith(o.text)) {
					return new OperatorToken(i, o);
				}
			}
		} while (to <= --i);
		return null;
	}

	private final int precedence;
	private final String text;

	public static final class OperatorToken implements Serializable, Comparable<OperatorToken> {

		private static final long serialVersionUID = 4114230669119867516L;

		final private int index;
		final private MathOperator operator;

		// Keep constructor private to prevent bad instanciation
		private OperatorToken(int index, MathOperator operator) {
			super();
			this.index = index;
			this.operator = operator;
		}

		@Override
		public int hashCode() {
			return Objects.hash(index, operator);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			OperatorToken other = (OperatorToken) obj;
			return index == other.index && operator == other.operator;
		}

		public int getIndex() {
			return index;
		}

		public MathOperator getOperator() {
			return operator;
		}

		/** Gets index of the next character after this operator. */
		public int getComputedIndex() {
			return index + operator.text.length();
		}

		@Override
		public int compareTo(OperatorToken token) {
			if (token == null) {
				// Assume null is biggest
				return 1;
			}
			// Sort by precedence and index
			int comp = Integer.compare(operator.precedence, token.operator.precedence);
			if (comp == 0) {
				comp = Integer.compare(index, token.index);
			}
			return comp;
		}

		/**
		 * Tests if this token is stuck to the specified, according to theirs positions
		 * in the text.
		 * 
		 * Two operators are considered stuck if the text between them is blank.
		 * 
		 * @param token      the token operator to which to compare {@code this}.
		 * @param expression the expression in which tokens are present. It's parsed to
		 *                   know if operators are stick.
		 * @return {@code true} if and only if wrapped operators at them positions are
		 *         stick in text {@code expression}.
		 */
		public boolean isStuck(OperatorToken token, String expression) {
			if (token == null || expression == null) {
				return false;
			}
			if (index <= token.index) {
				try {
					return expression.substring(getComputedIndex(), token.index).isBlank();
				} catch (Exception e) {
					return false;
				}
			}
			return token.isStuck(this, expression);
		}
	}
}