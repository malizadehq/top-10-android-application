package org.mathematica.logic;

import org.mathematica.globals.AppData;

public class Board {

	private static int DEFAULT_MAX_WORD_LENGHT = 4;

	private int nrColumns;
	private int nrRows;
	private int maxWordLength;

	private int board[][];

	public Board(int rows, int columns, int length) {
		this.setNrColumns(columns);
		this.setNrRows(rows);
		this.setBoard(new int[AppData.ROWS][AppData.COLUMNS]);
		setMaxWordLength(length);
		BoardTilesHandler.addEmptySpaces(getBoard(), maxWordLength);
		BoardTilesHandler.addWorkingSpaces(getBoard());
		BoardTilesHandler.resizeBoard(board, rows, columns);
	}

	public Board(int rows, int columns) {
		this(rows, columns, DEFAULT_MAX_WORD_LENGHT);
	}

	private void setNrColumns(int nrColumns) {
		this.nrColumns = nrColumns;
	}

	public int getNrColumns() {
		return nrColumns;
	}

	private void setNrRows(int nrRows) {
		this.nrRows = nrRows;
	}

	public int getNrRows() {
		return nrRows;
	}

	private void setBoard(int board[][]) {
		this.board = board;
	}

	private void setMaxWordLength(int maxWordLength) {
		this.maxWordLength = maxWordLength;
	}

	public int[][] getBoard() {
		return board;
	}
}
