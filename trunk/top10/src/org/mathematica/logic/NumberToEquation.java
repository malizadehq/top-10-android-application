package org.mathematica.logic;

import org.mathematica.constants.OP_DIFF;

public class NumberToEquation {

	private static NumberToEquation $static;

	public static NumberToEquation getInstance() {
		if ($static == null)
			$static = new NumberToEquation();

		return $static;
	}

	public String getEquation(int result, OP_DIFF questionDifficulty) {
		int threeDepth = (int) ((Math.random() * 2) + 2);
		return getEquationInt(result, questionDifficulty, threeDepth);
	}

	private String getEquationInt(int result, OP_DIFF questionDifficulty,
			int treeDepth) {

		if (treeDepth <= 1 || result <= 1)
			return "" + result;

		OP_DIFF sourceDifficulty = OP_DIFF.EASY_DIFF;

		if (questionDifficulty == OP_DIFF.MEDIUM_DIFF)
			sourceDifficulty = OP_DIFF.values()[(int) (Math.random() * 2)];
		else if (questionDifficulty == OP_DIFF.HARD_DIFF)
			sourceDifficulty = OP_DIFF.values()[(int) (Math.random() * 3)];

		int results[] = getTuple(result, sourceDifficulty);
		int first = results[0];
		int other = results[1];
		String operation = " " + (char) results[2] + " ";

		treeDepth--;

		String equation = "";
		if (other == (char) 'R') {
			int randomizer = (int) (Math.random() * 2);
			switch (randomizer) {
			case 0:
				equation = operation + "("
						+ getEquationInt(first, questionDifficulty, treeDepth)
						+ ")";
				break;

			case 1:
				equation = operation + "(" + first + ")";
				break;

			default:
				break;
			}
		} else if (results[2] == (char) 'P') {
			equation = "(" + other + "% of " + first + ")";
		} else {
			int randomizer = (int) (Math.random() * 3);
			switch (randomizer) {
			case 0:
				equation = "("
						+ getEquationInt(first, questionDifficulty, treeDepth)
						+ operation
						+ getEquationInt(other, questionDifficulty, treeDepth)
						+ ")";
				break;

			case 1:
				equation = "(" + first + operation
						+ getEquationInt(other, questionDifficulty, treeDepth)
						+ ")";
				break;

			case 2:
				equation = "("
						+ getEquationInt(first, questionDifficulty, treeDepth)
						+ operation + other + ")";
				break;

			default:
				break;
			}
		}

		return equation;
	}

	private int[] getTuple(int result, OP_DIFF questionDifficulty) {
		int firstOperand = 0;
		int secondOperand = 0;
		char operator = '+';

		switch (questionDifficulty) {
		case EASY_DIFF:
			int direction = (int) (Math.random() * 2);

			if (direction == 0) {
				firstOperand = result / 2;
				firstOperand = (int) (Math.random() * firstOperand) + 1;
				secondOperand = result - firstOperand;
				operator = (char) 0x002B;
			} else if (direction == 1) {
				firstOperand = (int) (Math.random() * result) + 1;
				secondOperand = firstOperand;
				firstOperand += result;
				operator = (char) 0x2212;
			}
			break;
		case MEDIUM_DIFF:
			direction = (int) (Math.random() * 2);

			if (direction == 0) {
				firstOperand = result * 2;
				secondOperand = 50;
				operator = (char) 'P';
			} else if (direction == 1) {
				if (result % 2 == 0) {
					firstOperand = cmmdc(result);
					secondOperand = result / firstOperand;
					operator = (char) 0x00D7;
				} else {
					secondOperand = (int) ((Math.random() * 10) + 1);
					firstOperand = result * secondOperand;
					operator = (char) 0x00F7;
				}
			}
			break;
		case HARD_DIFF:
			if (result < 20) {
				firstOperand = result * result;
				secondOperand = 'R';
				operator = (char) 0x221A;
			} else {
				secondOperand = (int) ((Math.random() * result) + result);
				firstOperand = (int) ((Math.random() * 9) + 1) * secondOperand
						+ result;
				operator = '%';
			}
			break;
		default:
			break;
		}

		return new int[] { firstOperand, secondOperand, operator };
	}

	private int cmmdc(int value) {
		int result = 0;
		for (int i = value - 1; i > 0; i--) {
			if ((value % i) == 0) {
				result = i;
				break;
			}
		}
		return result;
	}
}
