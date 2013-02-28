package org.mathematica.logic;

import org.mathematica.globals.AppData;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternet {
	public static boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) AppData.applicationContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}
}
