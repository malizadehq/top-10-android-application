package org.mathematica.logic;

import org.mathematica.constants.BUYABLE_TYPE;
import org.mathematica.globals.AppData;
import org.mathematica.merchent.BuyableItem;
import org.mathematica.merchent.BuyableItems;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class VisualThemeManager {

	private static SharedPreferences storage;
	private static Editor commiter;

	private static void initStuff() {
		storage = AppData.applicationContext.getSharedPreferences(
				"MATHEMATICA_PRO", 0);
		commiter = storage.edit();
	}

	public static void setCurrentTheme(String key) {
		initStuff();
		for (BuyableItem item : BuyableItems.items) {
			if (item.type == BUYABLE_TYPE.VISUAL_THEME) {
				if (item.key.equals(key)) {
					commiter.putBoolean("VISUAL_"+item.key, true);
				} else {
					commiter.putBoolean("VISUAL_"+item.key, false);
				}
				commiter.commit();
			}
		}
	}

	public static String getCurrentTheme() {
		initStuff();
		String currentTheme = "THEME1";
		for (BuyableItem item : BuyableItems.items) {
			if (item.type == BUYABLE_TYPE.VISUAL_THEME) {
				if (storage.getBoolean("VISUAL_" + item.key, false)) {
					currentTheme = item.key;
					break;
				}
			}
		}
		return currentTheme;
	}
}
