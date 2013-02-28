package org.mathematica.logic;

import org.mathematica.globals.AppData;

public class TransfUtils {

	public static int getIndexFromPosition(int row, int column) {
		return (row * AppData.COLUMNS) + column;
	}

	public static int[] getPositionFromIndex(int index) {
		int row = index / AppData.COLUMNS;
		int column = index - (row * AppData.COLUMNS);
		return new int[] { row, column };
	}
}
