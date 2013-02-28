package org.mathematica.logic;

import java.util.ArrayList;

import org.mathematica.constants.OP_DIFF;
import org.mathematica.constants.OP_DIRECTION;

public class QuestionsExtracter {

	private ArrayList<Question> extractedSequences;

	public QuestionsExtracter(int board[][]) {
		this(board, OP_DIFF.EASY_DIFF);
	}

	public QuestionsExtracter(int[][] board, OP_DIFF diff) {
		setExtractedSequences(new ArrayList<Question>());
		analizeAndExtractSequences(board, diff);
	}

	private void analizeAndExtractSequences(int[][] board, OP_DIFF diff) {
		int rows = board.length;
		int columns = board[0].length;

		for (int index = 0; index < rows; index++) {
			int sum = 0;

			int StartColumn = 0;
			for (int i = 0; i < columns; i++) {
				if ((board[index][i] == -1) && sum != 0) {
					this.extractedSequences.add(new Question(index,
							StartColumn, sum, OP_DIRECTION.HOR, diff));
					sum = 0;
					continue;
				} else if (board[index][i] == -1 && sum == 0)
					continue;

				if (sum == 0)
					StartColumn = i;

				sum *= 10;
				sum += board[index][i];

				if (i == columns - 1) {
					this.extractedSequences.add(new Question(index,
							StartColumn, sum, OP_DIRECTION.HOR, diff));
					sum = 0;
					continue;
				}
			}

		}
		for (int index = 0; index < columns; index++) {
			int sum = 0;

			int StartRow = 0;
			for (int i = 0; i < rows; i++) {
				if ((board[i][index] == -1) && sum != 0) {
					this.extractedSequences.add(new Question(StartRow, index,
							sum, OP_DIRECTION.VERT, diff));
					sum = 0;
					continue;
				} else if (board[i][index] == -1 && sum == 0)
					continue;

				if (sum == 0)
					StartRow = i;

				sum *= 10;
				sum += board[i][index];

				if (i == columns - 1) {
					this.extractedSequences.add(new Question(StartRow, index,
							sum, OP_DIRECTION.VERT, diff));
					sum = 0;
					continue;
				}
			}
		}
	}

	public void setExtractedSequences(ArrayList<Question> extractedSequences) {
		this.extractedSequences = extractedSequences;
	}

	public ArrayList<Question> getExtractedSequences() {
		return extractedSequences;
	}
}
