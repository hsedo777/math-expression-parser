package com.parser;

import java.util.Map;

import com.parser.MathOperator.OperatorToken;

public class ParenthesisExpression extends ValueExpression {

	private int variableKey = 0;

	public ParenthesisExpression(String expression) throws ParserException {
		super(expression);
		init(expression);
	}

	/**
	 * We add this constructor in favor to take care of passing system
	 * variables(remember, system variables may not respect standard variables
	 * syntax).
	 */
	protected ParenthesisExpression(String expression, Map<String, Number> variables) {
		super(expression, variables);
		init(expression);
	}

	private void init(String expression) {
		if (Expression.checkParenthesize(expression) >= 0) {
			throw new ExpressionFormatException("Bad parenthesizes!");
		}
	}

	protected String newSysVar() {
		String name;
		do {
			name = SYSTEM_VAR_MARK + variableKey++;
		} while (getVariables().containsKey(name));
		return name;
	}

	/**
	 * Assuming the expression returned by {@link #getAsText()} is good parenthesis,
	 * tries to get index of the last opening parenthesis with the max priority.
	 * 
	 * @return if found, index of the prior opening parenthesis, {@code -1} by
	 *         default.
	 */
	private int nextPriorOpen() {
		int index = -1, prior = 0, maxPrior = -1;
		String exp = getAsText();
		int i = 0, len = exp.length();
		char c = 0;
		while (i < len) {
			c = exp.charAt(i);
			if (c == '(') {
				if (prior >= maxPrior) {
					index = i;
					maxPrior = prior;
				}
				prior++;
			}
			if (c == ')') {
				prior--;
			}
			i++;
		}
		return index;
	}

	//
	protected boolean isParenthesisSideTokenValuable(String textSide, boolean begin/* OrEnd */) {
		try {
			textSide = textSide.trim();
			String[] tokens = textSide.split("\\s+");
			String token = begin ? tokens[0] : tokens[tokens.length - 1];
			token = token.replace("(", "").replace(")", "");
			new ValueExpression(token, getVariables()).eval();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	protected String eval(int openIndex) throws ParserException {
		try {
			String exp = getAsText();
			if (openIndex < 0) {
				return exp;
			}
			int closedIndex = exp.indexOf(')', openIndex + 1);
			if (closedIndex < 0) {
				throw new ExpressionFormatException("FATAL : Parenthesis closing missed.");
			}
			if (closedIndex - openIndex < 2) {
				// case : ()
				throw new ExpressionFormatException("Unable to execute expression '()'.");
			}
			String sub = exp.substring(openIndex + 1, closedIndex);
			SimpleExpression se = new SimpleExpression(sub, getVariables());

			/*
			 * Checks if there is an implicit multiplication before or after the parenthesis
			 * block.
			 */
			OperatorToken token;
			// before
			boolean timesBefore = false, timesAfter = false;
			if (openIndex != 0) {
				// We're not at the begining of the expression
				String before = exp.substring(0, openIndex);
				token = MathOperator.lastOperator(0, before);
				if (token != null) {
					// op.*(.*)
					before = exp.substring(token.getComputedIndex(), openIndex);
				}
				if (!before.isBlank()) {
					// possible implicit multiplication
					timesBefore = isParenthesisSideTokenValuable(before, false);
				}
			}
			// after
			if (closedIndex != exp.length() - 1) {
				String after = exp.substring(closedIndex + 1);
				token = MathOperator.nextOperator(0, after);
				if (token != null) {
					// (.*).*op
					after = exp.substring(closedIndex + 1, closedIndex + 1 + token.getIndex());
				}
				if (!after.isBlank()) {
					// possible implicit multiplication
					timesAfter = isParenthesisSideTokenValuable(after, true);
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append(exp.substring(0, openIndex).trim());
			if (timesBefore) {
				sb.append(MathOperator.TIMES.getText());
			}
			String sn = newSysVar();
			putVariable(sn, se.eval());
			sb.append(sn);
			if (timesAfter) {
				sb.append(MathOperator.TIMES.getText());
			}
			sb.append(exp.substring(closedIndex + 1).trim());
			return sb.toString();
		} catch (ParserException e) {
			throw e;
		} catch (Exception e) {
			throw new ParserException(e);
		}
	}

	public double eval() {
		int open;
		do {
			open = nextPriorOpen();
			// System.out.println(open + "\t:\t" + getAsText());
			setAsText(eval(open));
		} while (open >= 0);
		SimpleExpression se = new SimpleExpression(getAsText(), getVariables());
		return se.eval();
	}
}
