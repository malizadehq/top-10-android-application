package org.mathematica.pro;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.mathematica.globals.AppData;
import org.mathematica.logic.ProfileHandler;
import org.mathematica.logic.RetainedChanges;
import org.mathematica.logic.RetainedConfig;
import org.mathematica.logic.SavedGameData;
import org.mathematica.structures.Level;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;

public class MainMenu extends Activity implements OnClickListener,
		OnLongClickListener {

	private static final int TIME_BEFORE_BACK_REQUEST_EXPIRES = 5 * 1000;

	private Button newGameButton;
	private ProgressBar progressBar;

	private ImageButton rowsIncrButton;
	private ImageButton rowsDecrButton;
	private ImageButton columnsIncrButton;
	private ImageButton columnsDecrButton;
	private ImageButton digitsIncrButton;
	private ImageButton digitsDecrButton;

	private TextView rowsValue;
	private TextView columnsValue;
	private TextView digitsValue;

	private RadioButton easyDifficulty;
	private RadioButton mediumDifficulty;
	private RadioButton hardDifficulty;

	private RadioButton timedRadioButton;
	private RadioButton normalRadioButton;

	private int MIN_DIGITS = 2;
	private int MAX_DIGITS = 4;
	private int MIN_ROWS = 4;
	private int MAX_ROWS = 9;
	private int MIN_COLUMNS = 4;
	private int MAX_COLUMNS = 7;

	private Toast activityToast = null;

	private ImageButton profileExpander;

	private boolean willExitOnBackPressed = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main_menu);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		profileExpander = (ImageButton) findViewById(R.id.user_profile_expander);
		newGameButton = (Button) findViewById(R.id.play_game);
		newGameButton.setOnClickListener(this);

		rowsValue = (TextView) findViewById(R.id.rows_value);
		columnsValue = (TextView) findViewById(R.id.columns_value);
		digitsValue = (TextView) findViewById(R.id.digits_value);

		rowsIncrButton = (ImageButton) findViewById(R.id.rows_incr);
		rowsIncrButton.setOnClickListener(this);
		rowsDecrButton = (ImageButton) findViewById(R.id.rows_decr);
		rowsDecrButton.setOnClickListener(this);
		columnsIncrButton = (ImageButton) findViewById(R.id.columns_incr);
		columnsIncrButton.setOnClickListener(this);
		columnsDecrButton = (ImageButton) findViewById(R.id.columns_decr);
		columnsDecrButton.setOnClickListener(this);
		digitsIncrButton = (ImageButton) findViewById(R.id.digits_incr);
		digitsIncrButton.setOnClickListener(this);
		digitsDecrButton = (ImageButton) findViewById(R.id.digits_decr);
		digitsDecrButton.setOnClickListener(this);

		easyDifficulty = (RadioButton) findViewById(R.id.radio_easy);
		easyDifficulty.setOnLongClickListener(this);
		mediumDifficulty = (RadioButton) findViewById(R.id.radio_medium);
		mediumDifficulty.setOnLongClickListener(this);
		hardDifficulty = (RadioButton) findViewById(R.id.radio_hard);
		hardDifficulty.setOnLongClickListener(this);

		timedRadioButton = (RadioButton) findViewById(R.id.radio_timed);
		timedRadioButton.setOnLongClickListener(this);
		normalRadioButton = (RadioButton) findViewById(R.id.radio_normal);
		normalRadioButton.setOnLongClickListener(this);

		profileExpander.setOnClickListener(this);
		profileExpander.setOnLongClickListener(this);

		rowsValue.setText(""
				+ (RetainedConfig.getConfigValue(RetainedConfig.NR_ROWS)));
		columnsValue.setText(""
				+ RetainedConfig.getConfigValue(RetainedConfig.NR_COLUMNS));
		digitsValue.setText(""
				+ RetainedConfig.getConfigValue(RetainedConfig.NR_DIGITS));

		int diff = RetainedConfig.getConfigValue(RetainedConfig.DIFFICULTY);
		switch (diff) {
		case 0:
			easyDifficulty.setChecked(true);
			break;
		case 1:
			mediumDifficulty.setChecked(true);
			break;
		case 2:
			hardDifficulty.setChecked(true);
			break;
		default:
			easyDifficulty.setChecked(true);
			break;
		}

		int game_mode = RetainedConfig.getConfigValue(RetainedConfig.GAME_MODE);
		switch (game_mode) {
		case 0:
			timedRadioButton.setChecked(true);
			break;
		case 1:
			normalRadioButton.setChecked(true);
			break;
		default:
			timedRadioButton.setChecked(true);
			break;
		}

		AppData.username = ProfileHandler.getUsername();
		AppData.userProfilePictureURL = ProfileHandler.getProfilePictureURL();
		if (AppData.email.length() == 0) {
			getAccountName();
		}

		if (AppData.username.equals("")
				&& AppData.userProfilePictureURL.equals("")) {
//			new GetNameInForeground(MainMenu.this, AppData.email,
//					"oauth2:https://www.googleapis.com/auth/userinfo.profile",
//					1001).execute();
		} else {
			new RetrieveUserProfile().execute();
			final TextView name = (TextView) findViewById(R.id.user_profile_name);
			name.setText("Welcome, " + AppData.username + "!");
			new RetrieveLargeUserProfilePicture().execute();
		}
	}

	public void onClick(View v) {
		if (v == profileExpander && !progressBar.isIndeterminate()) {
			Intent intent = new Intent(MainMenu.this, UserProfileActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_bottom_up_animation,
					R.anim.stay_put);
		}
		if (v == newGameButton) {
			if (SavedGameData.isGameInProgress()) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							Level level = new Level(Integer.parseInt(rowsValue
									.getText().toString()),
									Integer.parseInt(columnsValue.getText()
											.toString()),
									Integer.parseInt(digitsValue.getText()
											.toString()), getDifficulty(),
									getGameMode());
							Intent intent = new Intent(MainMenu.this,
									GameScreen.class);
							intent.putExtra("ITEM", level);
							intent.putExtra("NEW_GAME", false);
							startActivity(intent);
							overridePendingTransition(
									R.anim.slide_top_to_bottom_animation,
									R.anim.stay_put1);
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							RetainedChanges.saveNote("");
							Level levelToContinue = new Level(
									Integer.parseInt(rowsValue.getText()
											.toString()),
									Integer.parseInt(columnsValue.getText()
											.toString()),
									Integer.parseInt(digitsValue.getText()
											.toString()), getDifficulty(),
									getGameMode());
							Intent intent1 = new Intent(MainMenu.this,
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
				Level level = new Level(Integer.parseInt(rowsValue.getText()
						.toString()), Integer.parseInt(columnsValue.getText()
						.toString()), Integer.parseInt(digitsValue.getText()
						.toString()), getDifficulty(), getGameMode());
				Intent intent = new Intent(MainMenu.this, GameScreen.class);
				intent.putExtra("ITEM", level);
				intent.putExtra("NEW_GAME", true);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_top_to_bottom_animation,
						R.anim.stay_put1);
			}
		}
		if (v == rowsIncrButton) {
			updateValue(rowsValue, MIN_ROWS, MAX_ROWS, '+');
			RetainedConfig.setConfigValue(RetainedConfig.NR_ROWS,
					Integer.parseInt(rowsValue.getText().toString()));
		}

		if (v == rowsDecrButton) {
			updateValue(rowsValue, MIN_ROWS, MAX_ROWS, '-');
			RetainedConfig.setConfigValue(RetainedConfig.NR_ROWS,
					Integer.parseInt(rowsValue.getText().toString()));
		}

		if (v == columnsIncrButton) {
			updateValue(columnsValue, MIN_COLUMNS, MAX_COLUMNS, '+');
			RetainedConfig.setConfigValue(RetainedConfig.NR_COLUMNS,
					Integer.parseInt(columnsValue.getText().toString()));
		}

		if (v == columnsDecrButton) {
			updateValue(columnsValue, MIN_COLUMNS, MAX_COLUMNS, '-');
			RetainedConfig.setConfigValue(RetainedConfig.NR_COLUMNS,
					Integer.parseInt(columnsValue.getText().toString()));
		}

		if (v == digitsIncrButton) {
			updateValue(digitsValue, MIN_DIGITS, MAX_DIGITS, '+');
			RetainedConfig.setConfigValue(RetainedConfig.NR_DIGITS,
					Integer.parseInt(digitsValue.getText().toString()));
		}

		if (v == digitsDecrButton) {
			updateValue(digitsValue, MIN_DIGITS, MAX_DIGITS, '-');
			RetainedConfig.setConfigValue(RetainedConfig.NR_DIGITS,
					Integer.parseInt(digitsValue.getText().toString()));
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

	private void updateValue(TextView valueLabel, int min, int max, char op) {
		int currentValue = Integer.parseInt(valueLabel.getText().toString());
		currentValue = op == '+' ? (currentValue + 1) : (currentValue - 1);
		if (currentValue > max)
			currentValue = max;
		if (currentValue < min)
			currentValue = min;

		valueLabel.setText("" + currentValue);
	}

	private int getDifficulty() {
		int difficulty = 0;
		if (easyDifficulty.isChecked())
			difficulty = 0;
		if (mediumDifficulty.isChecked())
			difficulty = 1;
		if (hardDifficulty.isChecked())
			difficulty = 2;

		RetainedConfig.setConfigValue(RetainedConfig.DIFFICULTY, difficulty);

		return difficulty;
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

	private class RetrieveUserProfile extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				if (AppData.smallUserProfilePicture == null) {
					AppData.smallUserProfilePicture = BitmapFactory
							.decodeStream((InputStream) new URL(
									AppData.userProfilePictureURL + "?sz=100")
									.getContent());
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			showProfilePicture();
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
									AppData.userProfilePictureURL + "?sz=500")
									.getContent());
				}
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

	private void showProfilePicture() {
		final ImageView i = (ImageView) findViewById(R.id.user_profile_picture);
		runOnUiThread(new Runnable() {
			public void run() {
				i.setImageBitmap(AppData.smallUserProfilePicture);
				progressBar.setIndeterminate(false);
				progressBar.setVisibility(View.GONE);
			}
		});
	}

	public void show(final String username, final String pictureURL) {
		AppData.userProfilePictureURL = pictureURL;
		ProfileHandler.saveProfilePictureURL(AppData.userProfilePictureURL);
		runOnUiThread(new Runnable() {
			public void run() {
				final TextView name = (TextView) findViewById(R.id.user_profile_name);
				name.setText("Welcome, " + username + "!");
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
		return accounts[0].name;
	}

	public boolean onLongClick(View v) {
		if (v == profileExpander) {
			showToast("Expand user profile", Toast.LENGTH_SHORT);
		}
		if (v == timedRadioButton) {
			showToast("Timed game with earned experience!", Toast.LENGTH_SHORT);
		}
		if (v == normalRadioButton) {
			showToast("No time stress but no experience gained!",
					Toast.LENGTH_SHORT);
		}
		if (v == easyDifficulty) {
			showToast("Additions/Subtractions", Toast.LENGTH_SHORT);
		}
		if (v == mediumDifficulty) {
			showToast("Multiplications/Division + Previous", Toast.LENGTH_SHORT);
		}
		if (v == hardDifficulty) {
			showToast("Modulo + Previous", Toast.LENGTH_SHORT);
		}
		return false;
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
}
