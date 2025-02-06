package com.parser;

public class MathMain {

	public static void main(String[] args) {
		String expression = "(a  ^          3) -    _x1*2";
		ParenthesisExpression se = new ParenthesisExpression(expression);
		se.withVariable("_x1", 10);
		se.withVariable("a", 3);
		System.out.println(se.eval());
		FunctionExpression fe = new FunctionExpression("(cos(pi) + 2) - sin(pi/2)", false);
		System.out.println(fe.eval());
	}

}
