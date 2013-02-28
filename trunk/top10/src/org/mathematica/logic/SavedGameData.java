package org.mathematica.logic;

import org.mathematica.globals.AppData;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SavedGameData {

	private static SharedPreferences storage;
	private static Editor commiter;

	public static final String SAVED_ROWS = "SAVED_ROWS";
	public static final String SAVED_COLUMNS = "SAVED_COLUMNS";
	public static final String SAVED_TIME = "SAVED_TIME";
	public static final String SAVED_XP = "SAVED_XP";
	public static final String SAVED_COMBO = "SAVED_COMBO";
	public static final String SAVED_GAME = "SAVED_GAME";
	public static final String SAVED_BOARD = "SAVED_BOARD";
	public static final String SAVED_DIFF = "SAVED_DIFF";
	public static final String SAVED_GAME_MODE = "SAVED_GAME_MODE";
	public static final String SAVED_LIVES_LEFT = "SAVED_LIFES_LEFT";

	private static void initStuff() {
		storage = AppData.applicationContext.getSharedPreferences(
				"MATHEMATICA_PRO", 0);
		commiter = storage.edit();
	}

	public static void saveInt(String key, int value) {
		initStuff();
		commiter.putInt(key, value);
		commiter.commit();
	}

	public static int getSavedInt(String key) {
		initStuff();
		return storage.getInt(key, 0);
	}

	public static void saveGame(String game) {
		initStuff();
		commiter.putString(SAVED_GAME, game);
		commiter.commit();
	}

	public static String getLastGame() {
		initStuff();
		return storage.getString(SAVED_GAME, "");
	}

	public static void saveBoard(String game) {
		initStuff();
		commiter.putString(SAVED_BOARD, game);
		commiter.commit();
	}

	public static String getLastBoard() {
		initStuff();
		return storage.getString(SAVED_BOARD, "");
	}

	public static void setGameInProgress(boolean gameInProgress) {
		initStuff();
		commiter.putBoolean("GAME_IN_PROGRESS", gameInProgress);
		commiter.commit();
	}

	public static boolean isGameInProgress() {
		initStuff();
		return storage.getBoolean("GAME_IN_PROGRESS", false);
	}

}