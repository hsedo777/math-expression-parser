# Math Expression Parser and Evaluator

A powerful tool for parsing and evaluating mathematical expressions in Java, designed for developers and enthusiasts of symbolic computation. Easy to use, extensible, and open source.

## 📋 Features
- Syntax analysis of mathematical expressions.
- Accurate evaluation of expressions.
- Support for common mathematical operators:
  - addition (+),
  - subtraction (-),
  - multiplication (*),
  - division (/),
  - power (^) and
  - modulo (%).
- Easy addition of new mathematical operators.
- Variable management for dynamic calculations.
- Support for parentheses in complex expressions.
- Extensible with custom functions.

## 🛠️ Requirements
- **Java 11** or later.
- A Java-compatible IDE or text editor (e.g., IntelliJ IDEA, Eclipse, or VS Code).

## 🚀 Installation
1. Clone this repository to your local machine:
   ```bash
   git clone https://github.com/hsedo777/math-expression-parser.git
   ```
2. Import the project into your preferred IDE and run it.

## ✨ Usage
Here is an example of how to parse and evaluate a mathematical expression:
```java
String expression = "(a ^ 3) - _x1 * 2";
ParenthesisExpression se = new ParenthesisExpression(expression);
se.withVariable("_x1", 10);
se.withVariable("a", 3);
System.out.println(se.eval());
```
Expected output:
```text
Output: 7.0
```

## 📄 License
This project is licensed under the [MIT License](./LICENSE). You are free to use, modify, and redistribute this project as long as you comply with the terms of the license.

## 🖋️ Author
**hsedo777**
- [GitHub](https://github.com/hsedo777)
- Contact: [hsedo777@gmail.com](mailto:hsedo777@gmail.com)

## 🛠️ Contributions
Contributions are welcome! If you want to contribute:
1. Fork this repository.
2. Create a branch for your modifications.
3. Submit a pull request.

## 📧 Contact
For any questions or suggestions, feel free to reach out at [hsedo777@gmail.com](mailto:hsedo777@gmail.com).

