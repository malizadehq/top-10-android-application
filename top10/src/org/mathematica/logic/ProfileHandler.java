package org.mathematica.logic;

import org.mathematica.globals.AppData;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ProfileHandler {

	private static SharedPreferences storage;
	private static Editor commiter;

	private static void initStuff() {
		storage = AppData.applicationContext.getSharedPreferences(
				"MATHEMATICA_PRO", 0);
		commiter = storage.edit();
	}

	public static void saveUsername(String username) {
		initStuff();
		commiter.putString("PROFILE_USERNAME", username);
		commiter.commit();
	}

	public static String getUsername() {
		initStuff();
		return storage.getString("PROFILE_USERNAME", "");
	}

	public static void saveProfilePictureURL(String pictureURL) {
		initStuff();
		commiter.putString("PROFILE_PICTURE_URL", pictureURL);
		commiter.commit();
	}

	public static String getProfilePictureURL() {
		initStuff();
		return storage.getString("PROFILE_PICTURE_URL", "");
	}
}