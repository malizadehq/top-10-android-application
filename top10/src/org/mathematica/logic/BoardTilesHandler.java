package org.mathematica.logic;

import org.mathematica.globals.AppData;

public class BoardTilesHandler {
	private static int rows;
	private static int columns;

	public static void addEmptySpaces(int board[][], int maxWordLenght) {
		rows = board.length;
		columns = board[0].length;

		for (int pos = 0; pos < rows * columns; pos += randomNextWordSize(maxWordLenght)) {
			board[getBoardPosition(pos)[0]][getBoardPosition(pos)[1]] = -1;
		}

		/*
		 * Make sure there are no vertical words with length larger then max
		 */
		for (int col = 0; col < columns; col++) {
			int length = 0;
			for (int rw = 0; rw < rows; rw++) {
				if (board[rw][col] != -1) {
					length++;
					if (length > maxWordLenght) {
						board[rw][col] = -1;
						length = 0;
					}
				}
			}
		}
	}

	public static void addWorkingSpaces(int board[][]) {
		rows = board.length;
		columns = board[0].length;

		int nextRandomNumber = (int) (Math.random() * 9) + 1;

		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				if (board[row][column] != -1) {
					board[row][column] = nextRandomNumber;
					nextRandomNumber = (int) (Math.random() * 9) + 1;
				}
			}
		}
	}

	private static int[] getBoardPosition(int pos) {
		int boardPosition[] = new int[2];
		boardPosition[0] = pos / columns;
		boardPosition[1] = pos - (boardPosition[0] * columns);
		return boardPosition;
	}

	private static int randomNextWordSize(int maxWordLenght) {
		int nextWordSize = (int) ((Math.random() * maxWordLenght) + 2);
		return nextWordSize;
	}

	public static void resizeBoard(int[][] board, int rows, int columns) {
		for (int i = rows; i < AppData.ROWS; i++) {
			for (int j = 0; j < AppData.COLUMNS; j++) {
				board[i][j] = -1;
			}
		}

		for (int i = columns; i < AppData.COLUMNS; i++) {
			for (int j = 0; j < AppData.ROWS; j++) {
				board[j][i] = -1;
			}
		}

	}
}
