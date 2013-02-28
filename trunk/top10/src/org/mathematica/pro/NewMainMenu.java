package org.mathematica.pro;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.mathematica.globals.AppData;
import org.mathematica.logging.Logger;
import org.mathematica.logic.ProfileHandler;
import org.mathematica.logic.RetainedChanges;
import org.mathematica.logic.RetainedConfig;
import org.mathematica.logic.SavedGameData;
import org.mathematica.rankings.RankingWrapper;
import org.mathematica.structures.Level;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;

public class NewMainMenu extends Activity implements OnClickListener {
	private final int TIME_BEFORE_BACK_REQUEST_EXPIRES = 5 * 1000;

	private ProgressBar progressBar;

	private RadioButton timedRadioButton;
	private RadioButton normalRadioButton;

	private RadioButton smallRadioButton;
	private RadioButton mediumRadioButton;
	private RadioButton largeRadioButton;

	private Toast activityToast = null;

	private boolean willExitOnBackPressed = false;

	private Button easyGameButton;
	private Button mediumGameButton;
	private Button hardGameButton;
	private Button userProfileButton;

	private int gameDifficulty = 0;

	private boolean hasLoadedSmallImage = false;
	private boolean hasLoadedLargeImage = false;

	private final Logger logger = new Logger(this.getClass().getName());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_new_main_menu);

		smallRadioButton = (RadioButton) findViewById(R.id.radio_small);
		mediumRadioButton = (RadioButton) findViewById(R.id.radio_medium);
		largeRadioButton = (RadioButton) findViewById(R.id.radio_large);

		easyGameButton = ((Button) findViewById(R.id.easy_button));
		easyGameButton.setOnClickListener(this);
		mediumGameButton = ((Button) findViewById(R.id.medium_button));
		mediumGameButton.setOnClickListener(this);
		hardGameButton = ((Button) findViewById(R.id.hard_button));
		hardGameButton.setOnClickListener(this);
		userProfileButton = ((Button) findViewById(R.id.user_profile_picture));
		userProfileButton.setOnClickListener(this);
		easyGameButton.setTypeface(AppData.gameNightFont);
		mediumGameButton.setTypeface(AppData.gameNightFont);
		hardGameButton.setTypeface(AppData.gameNightFont);

		RotateAnimation ranim = (RotateAnimation) AnimationUtils.loadAnimation(
				AppData.applicationContext, R.anim.giggle);
		RotateAnimation ranim1 = (RotateAnimation) AnimationUtils
				.loadAnimation(AppData.applicationContext, R.anim.giggle1);
		RotateAnimation ranim2 = (RotateAnimation) AnimationUtils
				.loadAnimation(AppData.applicationContext, R.anim.giggle2);
		RotateAnimation ranim3 = (RotateAnimation) AnimationUtils
				.loadAnimation(AppData.applicationContext, R.anim.giggle3);
		ranim.setFillAfter(true);
		ranim1.setFillAfter(true);
		ranim2.setFillAfter(true);
		ranim3.setFillAfter(true);
		easyGameButton.clearAnimation();
		easyGameButton.setAnimation(ranim);
		mediumGameButton.clearAnimation();
		mediumGameButton.setAnimation(ranim1);
		hardGameButton.clearAnimation();
		hardGameButton.setAnimation(ranim2);
		userProfileButton.clearAnimation();
		userProfileButton.setAnimation(ranim3);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		timedRadioButton = (RadioButton) findViewById(R.id.radio_timed);
		timedRadioButton.setOnClickListener(this);
		normalRadioButton = (RadioButton) findViewById(R.id.radio_normal);
		normalRadioButton.setOnClickListener(this);

		int game_mode = RetainedConfig.getConfigValue(RetainedConfig.GAME_MODE);
		switch (game_mode) {
		case 0:
			timedRadioButton.setChecked(true);
			userProfileButton
					.setBackgroundResource(R.drawable.profile_gradient);
			setUiTimedGameMode();
			break;
		case 1:
			normalRadioButton.setChecked(true);
			userProfileButton
					.setBackgroundResource(R.drawable.grey_button_selector);
			setUINormalGameMode();
			break;
		default:
			timedRadioButton.setChecked(true);
			break;
		}

		int game_board_size = RetainedConfig
				.getConfigValue(RetainedConfig.BOARD_SIZE);
		switch (game_board_size) {
		case 0:
			smallRadioButton.setChecked(true);
			break;
		case 1:
			mediumRadioButton.setChecked(true);
			break;
		case 2:
			largeRadioButton.setChecked(true);
			break;
		default:
			smallRadioButton.setChecked(true);
			break;
		}

		new ShowLatestNews().execute();

		AppData.username = ProfileHandler.getUsername();
		AppData.userProfilePictureURL = ProfileHandler.getProfilePictureURL();
		if (AppData.email.length() == 0) {
			getAccountName();
		}

		if (AppData.username.equals("")
				&& AppData.userProfilePictureURL.equals("")) {
			new GetNameInForeground(NewMainMenu.this, AppData.email,
					"oauth2:https://www.googleapis.com/auth/userinfo.profile",
					1001).execute();
		} else {
			new RetrieveUserProfile().execute();
			new RetrieveLargeUserProfilePicture().execute();
		}
	}

	private void showToast(String message, int duration) {
		if (activityToast != null) {
			activityToast.cancel();
		}
		activityToast = Toast.makeText(AppData.applicationContext, message,
				duration);
		activityToast.show();
	}

	private int[] getBameBoard() {
		int rows = 4;
		int columns = 4;
		int digits = 2;

		int game_board_size = 0;
		if (smallRadioButton.isChecked()) {
			game_board_size = 0;
			digits = 2;
			rows = ((int) (Math.random() * 2) + 4);
			columns = ((int) (Math.random() * 2) + 4);
		} else if (mediumRadioButton.isChecked()) {
			game_board_size = 1;
			digits = ((int) (Math.random() * 2) + 2);
			rows = ((int) (Math.random() * 3) + 5);
			columns = ((int) (Math.random() * 2) + 5);
		} else {
			game_board_size = 2;
			digits = ((int) (Math.random() * 2) + 3);
			rows = ((int) (Math.random() * 4) + 6);
			columns = ((int) (Math.random() * 2) + 6);
		}

		RetainedConfig.setConfigValue(RetainedConfig.BOARD_SIZE,
				game_board_size);

		return new int[] { rows, columns, digits };
	}

	private int getGameMode() {
		int game_mode = 0;
		if (timedRadioButton.isChecked())
			game_mode = 0;
		if (normalRadioButton.isChecked())
			game_mode = 1;

		RetainedConfig.setConfigValue(RetainedConfig.GAME_MODE, game_mode);

		return game_mode;
	}

	private class UpdateUserInfoFromServer extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			RankingWrapper.updateAppForUser(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	private class RetrieveUserProfile extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				if (AppData.smallUserProfilePicture == null) {
					AppData.smallUserProfilePicture = BitmapFactory
							.decodeStream((InputStream) new URL(
									AppData.userProfilePictureURL + "?sz=600")
									.getContent());
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			showProfilePicture();
			hasLoadedSmallImage = true;
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	private class RetrieveLargeUserProfilePicture extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				if (AppData.largeUserProfilePicture == null) {
					AppData.largeUserProfilePicture = BitmapFactory
							.decodeStream((InputStream) new URL(
									AppData.userProfilePictureURL + "?sz=800")
									.getContent());
				}
				hasLoadedLargeImage = true;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	private class ShowLatestNews extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			return NotificationCenter.showLatestNews(NewMainMenu.this);
		}

		@Override
		protected void onPostExecute(String result) {
			if (!result.isEmpty()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						NewMainMenu.this);
				builder.setMessage(result).setTitle("Latest news")
						.setPositiveButton("Read it!", null).show();
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	private void showProfilePicture() {
		Resources r = getResources();
		final Drawable[] layers = new Drawable[2];
		layers[0] = new BitmapDrawable(r, AppData.smallUserProfilePicture);
		layers[1] = r.getDrawable(R.drawable.frame);
		final Button i = (Button) findViewById(R.id.user_profile_picture);
		runOnUiThread(new Runnable() {
			public void run() {
				LayerDrawable layerDrawable = new LayerDrawable(layers);
				i.setBackgroundDrawable(layerDrawable);
				progressBar.setVisibility(View.GONE);
			}
		});
	}

	public void show(final String username, final String pictureURL) {
		AppData.userProfilePictureURL = pictureURL;
		ProfileHandler.saveProfilePictureURL(AppData.userProfilePictureURL);
		runOnUiThread(new Runnable() {
			public void run() {
				AppData.username = username;
				ProfileHandler.saveUsername(AppData.username);
				new RetrieveUserProfile().execute();
				new RetrieveLargeUserProfilePicture().execute();
			}
		});
	}

	private String getAccountName() {
		AccountManager mAccountManager = AccountManager.get(this);
		Account[] accounts = mAccountManager
				.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		AppData.email = accounts[0].name;
		new UpdateUserInfoFromServer().execute(AppData.email);
		return AppData.email;
	}

	@Override
	public void onBackPressed() {
		if (!willExitOnBackPressed) {
			Toast.makeText(this, "Press the 'Back' key again to exit.",
					Toast.LENGTH_SHORT).show();
			willExitOnBackPressed = true;

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					willExitOnBackPressed = false;
				}
			}, TIME_BEFORE_BACK_REQUEST_EXPIRES);

			return;
		}
		finish();
	}

	public void onClick(View v) {
		if (v == userProfileButton) {
			if (!hasLoadedLargeImage || !hasLoadedSmallImage) {
				showToast("Still loading app. Wait 2 more seconds!",
						Toast.LENGTH_SHORT);
				return;
			}

			Intent intent = new Intent(NewMainMenu.this,
					UserProfileActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_bottom_up_animation,
					R.anim.stay_put);
			return;
		}

		if (v == timedRadioButton) {
			showToast("Timed - Earn points and brag about them",
					Toast.LENGTH_SHORT);
			setUiTimedGameMode();
			return;
		}

		if (v == normalRadioButton) {
			showToast("Normal - Enjoy pointless gameplay", Toast.LENGTH_SHORT);
			setUINormalGameMode();
			return;
		}

		if (v == easyGameButton) {
			gameDifficulty = 0;
		}
		if (v == mediumGameButton) {
			gameDifficulty = 1;
		}
		if (v == hardGameButton) {
			gameDifficulty = 2;
		}

		if (SavedGameData.isGameInProgress()) {
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						Level level = new Level(0, 0, 0, 0, getGameMode());
						Intent intent = new Intent(NewMainMenu.this,
								GameScreen.class);
						intent.putExtra("ITEM", level);
						intent.putExtra("NEW_GAME", false);
						startActivity(intent);
						overridePendingTransition(
								R.anim.slide_top_to_bottom_animation,
								R.anim.stay_put1);
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						int[] settings = getBameBoard();
						Level levelToContinue = new Level(settings[0],
								settings[1], settings[2], gameDifficulty,
								getGameMode());
						RetainedChanges.saveNote("");
						Intent intent1 = new Intent(NewMainMenu.this,
								GameScreen.class);
						intent1.putExtra("ITEM", levelToContinue);
						intent1.putExtra("NEW_GAME", true);
						startActivity(intent1);
						overridePendingTransition(
								R.anim.slide_top_to_bottom_animation,
								R.anim.stay_put1);
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"A previous unfinished game has been detected. Continue last game or start a new one?")
					.setTitle("Unfinished game detected!")
					.setPositiveButton("Continue last", dialogClickListener)
					.setNegativeButton("Start new one", dialogClickListener)
					.show();
		} else {
			int[] settings = getBameBoard();
			Level level = new Level(settings[0], settings[1], settings[2],
					gameDifficulty, getGameMode());
			Intent intent = new Intent(NewMainMenu.this, GameScreen.class);
			intent.putExtra("ITEM", level);
			intent.putExtra("NEW_GAME", true);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_top_to_bottom_animation,
					R.anim.stay_put1);
		}

	}

	private void setUINormalGameMode() {
		easyGameButton.setBackgroundResource(R.drawable.grey_button_selector);
		mediumGameButton.setBackgroundResource(R.drawable.grey_button_selector);
		hardGameButton.setBackgroundResource(R.drawable.grey_button_selector);
	}

	private void setUiTimedGameMode() {
		easyGameButton.setBackgroundResource(R.drawable.easy_button_selector);
		mediumGameButton
				.setBackgroundResource(R.drawable.medium_button_selector);
		hardGameButton.setBackgroundResource(R.drawable.hard_button_selector);
	}
}
