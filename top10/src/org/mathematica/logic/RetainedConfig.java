package org.mathematica.logic;

import org.mathematica.globals.AppData;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class RetainedConfig {

	private static SharedPreferences storage;
	private static Editor commiter;

	public static String NR_COLUMNS = "NR_COLUMNS";
	public static String NR_ROWS = "NR_ROWS";
	public static String NR_DIGITS = "NR_DIGITS";
	public static String DIFFICULTY = "DIFFICULTY";
	public static String GAME_MODE = "GAME_MODE";
	public static String BOARD_SIZE = "BOARD_SIZE";
	public static String STORED_NEWS_ID = "STORED_NEWS_ID";

	private static void initStuff() {
		storage = AppData.applicationContext.getSharedPreferences(
				"MATHEMATICA_PRO", 0);
		commiter = storage.edit();
	}

	public static void setConfigValue(String key, int value) {
		initStuff();
		commiter.putInt(key, value);
		commiter.commit();
	}

	public static int getConfigValue(String key) {
		initStuff();
		return storage.getInt(key, 4);
	}

}