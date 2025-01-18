package com.parser;

import java.util.Map;

import com.parser.MathOperator.OperatorToken;

/**
 * Simple expressions are expressions that only use values and operators. It
 * doesn't contains any parenthesis. It must be able to be evaluated without
 * knowing external values or expressions.
 */
public class SimpleExpression extends ValueExpression {

	private int variableKey = 0;

	public SimpleExpression(String expression) throws ParserException {
		super(expression);
	}

	protected SimpleExpression(String expression, Map<String, Number> variables) throws ParserException {
		super(expression, variables);
	}

	private OperatorToken getPreviousToken(OperatorToken origin) {
		String above = getAsText().substring(0, origin.getIndex());
		if (!above.isBlank()) {
			return MathOperator.lastOperator(0, above);
		}
		return null;
	}

	private OperatorToken getNextToken(OperatorToken origin) {
		return MathOperator.nextOperator(origin.getIndex() + origin.getOperator().getText().length(), getAsText());
	}

	protected String newSysVar() {
		String name;
		do {
			name = SYSTEM_VAR_MARK + variableKey++;
		} while (getVariables().containsKey(name));
		return name;
	}

	/**
	 * Execute operator on the closest(left and right) operands. Push the result, as
	 * variable, in the expression by replacing the corresponding sub-text in the
	 * expression by the variable. Then return the new value of the expression.
	 */
	protected String eval(OperatorToken token) throws ParserException {
		try {
			String exp = getAsText();
			if (token == null) {
				return exp;
			}
			String after = exp.substring(token.getComputedIndex());
			if (after.isBlank()) {
				throw new ExpressionFormatException("Expression ended with an operator !");
			}

			if (token.getIndex() == 0 && !token.getOperator().mightUnary()) {
				throw new ExpressionFormatException("Only unary expression can be put at start of expression.");
			}

			// Check previous operator validity
			OperatorToken previous = getPreviousToken(token);
			if (previous != null && previous.getIndex() != 0) {
				/*
				 * The operator isn't at the begining of the expression.
				 * 
				 * We will check if it isn't an unary operator.
				 */
				if (previous.isStuck(token, exp)) {
					throw new ExpressionFormatException("The expression '" + exp + "' is malformed.");
				}
				/*
				 * OperatorToken previous2Previous = getPreviousToken(previous); if
				 * (previous.isStuck(previous2Previous)) { // `previous` must be unary if
				 * (!previous.getOperator().mightUnary()) { throw new
				 * ExpressionFormatException("The expression '" + exp + "' is malformed."); } //
				 * Adapt the previous operator previous = previous2Previous; }
				 */
			}

			// Check next operator validity
			OperatorToken next = getNextToken(token);
			if (token.isStuck(next, exp)) {
				/*
				 * There might have unary operator. Only unary operator can directly follow
				 * another operator
				 */
				MathOperator nextOp = next.getOperator();
				if (token.getIndex() == 0 || !nextOp.mightUnary()) {
					/*
					 * `token.getIndex() == 0` : prevent next stick operator when token is unary
					 * 
					 * `!nextOp.mightUnary()` : only unary operator is able to directly folow binary
					 * operation
					 */
					throw new ExpressionFormatException(
							"The operator '" + nextOp.getText() + "' is bad placed in expression '" + after + "'");
				}
				// Use operator `nextOp` as unary
				OperatorToken next2Next = getNextToken(next);
				if (next.isStuck(next2Next, exp)) {
					// Three operators linked directly
					throw new ExpressionFormatException("Expression '" + exp + "' is malformed.");
				}
				// Use as next operator
				next = next2Next;
			}

			Map<String, Number> variables = getVariables();
			int afteri = (next == null) ? exp.length() : next.getIndex();
			String above;
			if (token.getIndex() == 0) {
				/* So `token` is an unary operator */
				after = exp.substring(0, afteri);
				Expression el = new ValueExpression(after, variables);
				double value = el.eval();
				String vn = newSysVar();
				putVariable(vn, value);
				return vn + exp.substring(afteri);
			}

			int abovei = (previous == null) ? 0 : previous.getComputedIndex();
			above = exp.substring(abovei, token.getIndex());
			after = exp.substring(token.getComputedIndex(), afteri);

			double a = new ValueExpression(above, variables).eval();
			double b = new ValueExpression(after, variables).eval();
			double r = token.getOperator().eval(a, b);

			String vn = newSysVar();
			putVariable(vn, r);
			return exp.substring(0, abovei) + vn + exp.substring(afteri);
		} catch (ParserException e) {
			throw e;
		} catch (Exception e) {
			throw new ParserException(e);
		}
	}

	@Override
	public double eval() {
		OperatorToken token = null;
		do {
			token = MathOperator.nextMaxPrecedence(0, getAsText());
			setAsText(eval(token));
		} while (token != null);
		ValueExpression value = new ValueExpression(getAsText(), getVariables());
		return value.eval();
	}
}
