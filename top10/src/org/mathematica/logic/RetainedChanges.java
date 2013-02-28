package org.mathematica.logic;

import org.mathematica.globals.AppData;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class RetainedChanges {

	private static SharedPreferences storage;
	private static Editor commiter;

	private static void initStuff() {
		storage = AppData.applicationContext.getSharedPreferences(
				"MATHEMATICA_PRO", 0);
		commiter = storage.edit();
	}

	public static void saveNote(String message) {
		initStuff();
		commiter.putString("NOTE_MESSAGE", message);
		commiter.commit();
	}

	public static String getStoredNote() {
		initStuff();
		return storage.getString("NOTE_MESSAGE", "");
	}
}