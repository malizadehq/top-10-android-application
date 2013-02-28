package org.mathematica.structures;

import java.io.Serializable;

public class Level implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int rows;
	public int columns;
	public int wordLength;
	public int levelDifficulty;
	public int gameMode;

	public Level(int r, int c, int w, int diff, int mode) {
		rows = r;
		columns = c;
		wordLength = w;
		levelDifficulty = diff;
		this.gameMode = mode;
	}
}
