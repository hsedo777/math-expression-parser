package com.parser;

import java.util.Map;

/** Clause of being maths expression. */
public interface Expression {

	/** Marker of generated variables when process expression evaluation. */
	static final String SYSTEM_VAR_MARK = "#";

	/**
	 * The max authorized length that one variable to attach to an expression can
	 * have.
	 */
	static final int VARIABLE_NAME_MAX_LENGTH = 64;

	/**
	 * Evals expression and produces the value as {@code double}.
	 * 
	 * @throws ParserException if parsing fails.
	 * @since 1.0
	 * @author hovozounkou
	 */
	public double eval() throws ParserException;

	/**
	 * Defines a variable of specified name atached to the specified value, as
	 * usable for expression evaluation.
	 * 
	 * @param name  the name of the variable.
	 * @param value the value of the variable. Must be not null.
	 * @throws ParserException if the name or the value isn't usable.
	 */
	default public void withVariable(String name, Number value) throws ParserException {
		if (!isUsableAsVariableName(name) || value == null) {
			throw new ParserException("Invalid variable definition !");
		}
	}

	/**
	 * Defines a list of variables, wrapped into a map, as usable for expression
	 * evaluation.
	 * 
	 * @param variables the map of variables to bind.
	 * @throws ParserException if the name or the value isn't usable for any entry
	 *                         in the map.
	 */
	default public void withVariables(Map<String, ? extends Number> variables) throws ParserException {
		if (variables != null) {
			for (Map.Entry<String, ? extends Number> e : variables.entrySet()) {
				withVariable(e.getKey(), e.getValue());
			}
		}
	}

	/**
	 * Tests if the supplyed string can be used as variable name.
	 * 
	 * @param name the value to check.
	 * @return {@code true} if and only if the name is syntaxically valid.
	 */
	static boolean isUsableAsVariableName(String name) {
		if (name == null || name.isBlank() || name.matches(".*\\s+.*")) {
			// the name is null or contains white character
			return false;
		}
		if (name.matches("^[^a-zA-Z_].*")) {
			// the first character is not '_' and is not in a-z or A-Z
			return false;
		}
		if (name.matches(".*[^a-zA-Z1-9_]+.*")) {
			// the name isn't alpha numeric
			return false;
		}
		if (name.length() > VARIABLE_NAME_MAX_LENGTH) {
			return false;
		}
		return true;
	}

}
