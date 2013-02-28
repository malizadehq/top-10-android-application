package org.mathematica.globals;

import java.util.ArrayList;

import org.mathematica.logic.Question;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;

public class AppData {
	public static int[][] board;
	public static ArrayList<Question> questions = null;
	public static short ROWS = 9;
	public static short COLUMNS = 7;

	public static Context applicationContext;

	public static Bitmap largeUserProfilePicture = null;
	public static Bitmap smallUserProfilePicture = null;

	public static Typeface joystickFont;
	public static Typeface quicksandFont;
	public static Typeface gameNightFont;
	public static Typeface westernFont;
	
	public static String username = "";
	public static String email = "";
	public static String userProfilePictureURL;
	
	public static String oldMessage = "";
}
