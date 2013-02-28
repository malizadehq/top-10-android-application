package org.mathematica.sound;

import org.mathematica.globals.AppData;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class SoundManager {

	private static SharedPreferences sharedPreferences;
	private static Editor editor;

	private static void init() {
		sharedPreferences = AppData.applicationContext.getSharedPreferences(
				"MATHEMATICA_PRO", 0);
		editor = sharedPreferences.edit();
	}

	public static boolean isSoundActive() {
		init();
		return sharedPreferences.getBoolean("SOUND_STATE", true);
	}

	public static void setSoundActive(boolean active) {
		init();
		editor.putBoolean("SOUND_STATE", active);
		editor.commit();
	}

	public static void playSound(int soundID) {
		/* Play sound only if allowed */
		if (isSoundActive()) {
			MediaPlayer mp = MediaPlayer.create(AppData.applicationContext,
					soundID);
			mp.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}
			});
			mp.start();
		}
	}
}
