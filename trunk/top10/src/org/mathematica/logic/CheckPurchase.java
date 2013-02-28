package org.mathematica.logic;

import org.mathematica.globals.AppData;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CheckPurchase {

	private static SharedPreferences storage;
	private static Editor commiter;

	private static void initStuff() {
		storage = AppData.applicationContext.getSharedPreferences(
				"MATHEMATICA_PRO", 0);
		commiter = storage.edit();
	}

	public static boolean isPurchased(String key) {
		initStuff();
		return storage.getBoolean(key, false);
	}

	public static void setPurchased(String key, boolean purchased) {
		initStuff();
		commiter.putBoolean(key, purchased);
		commiter.commit();
	}
}