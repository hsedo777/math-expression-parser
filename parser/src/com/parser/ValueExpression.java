package com.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.parser.MathOperator.OperatorToken;

/**
 * Evaluated as an expression without any operator.
 * 
 * @since 1.0
 * @author hovozounkou
 */
public class ValueExpression implements Expression {

	private String expression, expressionTampon;
	private Map<String, Number> variables = new HashMap<>();

	public ValueExpression(String expression) throws ParserException {
		super();
		if (expression == null || expression.isBlank()) {
			throw new ValueException("Null or blank value aren't usable to instance expression.");
		}
		this.expression = expression;
		// this.expressionTampon = expression.replaceAll("\\s+", "");
		this.expressionTampon = expression.trim();
	}

	protected ValueExpression(String expression, Map<String, Number> variables) throws ParserException {
		this(expression);
		this.variables.putAll(variables);
	}

	@Override
	public double eval() {
		try {
			Map<String, Number> variables = getVariables();
			String exp = getAsText();

			// Check sign occurence
			int sign = 1;
			OperatorToken token = MathOperator.nextOperator(0, exp);
			if (token != null) {
				/*
				 * There is an unary operator
				 * 
				 * Matching case : (-|+)\s*(\d|variable)
				 */
				exp = exp.substring(token.getComputedIndex()).trim();
				if (MathOperator.MINUS == token.getOperator()) {
					sign = -1;
				}
			}
			if (exp.startsWith(SYSTEM_VAR_MARK)) {
				// Matching system variable
				Number value = variables.get(exp);
				if (value == null) {
					throw new ExpressionFormatException("FATAL : Parsing '" + exp + "' fails.");
				}
				return sign * value.doubleValue();
			}
			try {
				return sign * Double.valueOf(exp).doubleValue();
			} catch (NumberFormatException e) {
				// may be because of variable presence
				if (Expression.isUsableAsVariableName(exp)) {
					/* First checks if the variable is a maths constant. */
					Number value = variables.get(exp);
					if (exp.equalsIgnoreCase("e")) {
						value = Math.E;
					}
					if (exp.equalsIgnoreCase("pi")) {
						value = Math.PI;
					}
					if (value == null) {
						throw new ValueException("Impossible to find the value of variable '" + exp + "'");
					}
					return sign * value.doubleValue();
				}
				throw e;
			}
		} catch (NumberFormatException e) {
			throw new ExpressionFormatException("Invalid numeric value.", e);
		} catch (ParserException e) {
			throw (e);
		} catch (Exception e) {
			throw new ParserException(e);
		}
	}

	/** Gets the current value of the expression during evaluation. */
	public String getAsText() {
		return expressionTampon;
	}

	protected void setAsText(String expression) {
		this.expressionTampon = expression;
	}

	/** Gets the original value of the expression at creation. */
	public String getExpression() {
		return expression;
	}

	/** Gets an unmodifiable copy of the map of variables. */
	protected Map<String, Number> getVariables() {
		return Collections.unmodifiableMap(variables);
	}

	protected void putVariable(String name, Number value) {
		variables.put(name, value);
	}

	protected void putAll(Map<String, ? extends Number> variables) {
		this.variables.putAll(variables);
	}

	public void withVariable(String name, Number value) {
		Expression.super.withVariable(name, value);
		putVariable(name, value);
	}
}
