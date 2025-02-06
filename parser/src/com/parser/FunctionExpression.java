package com.parser;

import com.parser.MathFunction.MathFunctionToken;

public class FunctionExpression extends ValueExpression {

	private boolean degree;
	private int variableKey = 0;

	/**
	 * Create new expression that can contain functions.
	 * 
	 * @param expression the body of the expression
	 * @param degree     use true if trigonometrics functions are to eval using
	 *                   angle measure in degree. Value {@code false} means using
	 *                   radian.
	 * @throws ParserException if parsing fails.
	 */
	public FunctionExpression(String expression, boolean degree) throws ParserException {
		super(expression);
		this.degree = degree;
	}

	/**
	 * Alias of {@link #FunctionExpression(String, boolean)} using {@code true} as
	 * second argument.
	 * 
	 * @param expression the expression to parse.
	 */
	public FunctionExpression(String expression) throws ParserException {
		this(expression, true);
	}

	protected String newSysVar() {
		String name;
		do {
			name = SYSTEM_VAR_MARK + variableKey++;
		} while (getVariables().containsKey(name));
		return name;
	}

	/**
	 * Tries to fetch the matched closing parenthesis, according that at index
	 * {@code opening}, there is effectivly opening bracket. <br/>
	 * 
	 * <b>THIS METHOD DO NOT CONTROL EXISTENCE OF AN OPENING BRACKET AT INDEX
	 * {@code opening}. IT DOESN'T GRANT IF THE EXPRESSION IS CORRECTLY
	 * PARENTHESIS.</b>
	 * 
	 * @param opening assumed index of the opening bracket to match in the
	 *                expression
	 * @return the matched closing bracket index, {@code -1} if there is no
	 *         matching.
	 */
	protected int fetchClosure(int opening) {
		int closure = -1, open = 0, closed = 0, i = opening + 1;
		try {
			String exp = getAsText();
			char c;
			while (i < exp.length() && (closed != open + 1)) {
				c = exp.charAt(i);
				if (c == '(') {
					open++;
				} else if (c == ')') {
					closed++;
				}
				i++;
			}
			closure = (closed == open + 1) ? i - 1 : -1;
		} catch (Exception e) {
		}
		return closure;
	}

	//
	protected String eval(MathFunctionToken token) throws ParserException {
		try {
			String exp = getAsText();
			if (token == null) {
				return exp;
			}
			int closure, opening, funSize = token.function.getText().length();
			int from = funSize + token.index;
			opening = exp.indexOf('(', from);
			String sub = exp.substring(from, opening);
			if (!sub.isBlank()) {
				throw new ExpressionFormatException("The text '" + sub + "' can't folow function name.");
			}
			closure = fetchClosure(opening);
			if (closure < 0) {
				throw new ExpressionFormatException(
						"Missing closing parenthesis for fonction '" + token.function.name() + "'");
			}
			String subExp = exp.substring(opening + 1, closure);
			FunctionExpression fe = new FunctionExpression(subExp, degree);
			fe.putAll(getVariables());
			double value = fe.eval();
			double result = token.function.eval(value, degree);
			StringBuilder sb = new StringBuilder();
			sb.append(exp.substring(0, token.index));
			String var = newSysVar();
			putVariable(var, result);
			sb.append(var);
			sb.append(exp.substring(closure + 1));
			return sb.toString();
		} catch (ParserException e) {
			throw e;
		} catch (Exception e) {
			throw new ParserException(e);
		}
	}

	public double eval() {
		MathFunctionToken token;
		do {
			token = MathFunction.nextFunction(0, getAsText());
			setAsText(eval(token));
		} while (token != null);
		ParenthesisExpression pe = new ParenthesisExpression(getAsText(), getVariables());
		return pe.eval();
	}

	public boolean isDegree() {
		return degree;
	}

	public void setDegree(boolean degree) {
		this.degree = degree;
	}
}