package org.mathematica.logic;

import org.mathematica.globals.AppData;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PointsManager {

	private static final String PLAYER_POINTS_KEY = "PLAYER_POINTS";
	private static final String TOTAL_POINTS_KEY = "TOTAL_POINTS";
	private static SharedPreferences storage;
	private static Editor commiter;

	private static void initStuff() {
		storage = AppData.applicationContext.getSharedPreferences(
				"MATHEMATICA_PRO", 0);
		commiter = storage.edit();
	}

	public static void setPoints(int value) {
		initStuff();
		commiter.putInt(PLAYER_POINTS_KEY, value);
		commiter.commit();
	}

	public static void setTotalPoints(int value) {
		initStuff();
		commiter.putInt(TOTAL_POINTS_KEY, value);
		commiter.commit();
	}

	public static int getTotalPoints() {
		initStuff();
		int total_points = storage.getInt(TOTAL_POINTS_KEY, -1);
		if (total_points == -1) {
			total_points = getPoints();
		}
		return total_points;
	}

	public static int getPoints() {
		initStuff();
		return storage.getInt(PLAYER_POINTS_KEY, 0);
	}

	public static void addPoints(int value) {
		int currentPoints = getPoints();
		setTotalPoints(getTotalPoints() + value);
		setPoints(currentPoints + value);
	}

	public static void removePoints(int value) {
		int currentPoints = getPoints();
		setPoints(currentPoints - value);
	}

}
