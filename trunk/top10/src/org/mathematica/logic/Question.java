package org.mathematica.logic;

import org.mathematica.constants.OP_DIFF;
import org.mathematica.constants.OP_DIRECTION;

public class Question {

	private int row;
	private int column;

	private int correctAnswer;
	private OP_DIFF questionDifficulty;
	private OP_DIRECTION answerDirection;
	private String questionString;

	public Question(int row, int column, int answer, OP_DIRECTION direction,
			OP_DIFF difficulty) {
		this.row = row;
		this.column = column;
		this.correctAnswer = answer;
		this.answerDirection = direction;
		this.questionDifficulty = difficulty;

		questionString = getFormulaString(answer);
	}

	private String getFormulaString(int number) {
		return NumberToEquation.getInstance().getEquation(number,
				questionDifficulty);
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(int correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public OP_DIRECTION getAnswerDirection() {
		return answerDirection;
	}

	public String getQuestionString() {
		return questionString;
	}

	public void setQuestionString(String questionString) {
		this.questionString = questionString;
	}

	@Override
	public String toString() {
		String returnValue = "";
		returnValue += "[" + getRow() + "]";
		returnValue += "[" + getColumn() + "]";
		returnValue += "["
				+ (getAnswerDirection() == OP_DIRECTION.HOR ? "hor" : "vert")
				+ "]";
		returnValue += "[" + getQuestionString() + "]";
		returnValue += "[" + getCorrectAnswer() + "]";
		return returnValue;
	}
}
