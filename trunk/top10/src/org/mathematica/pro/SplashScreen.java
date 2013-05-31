package org.mathematica.pro;

import org.mathematica.globals.AppData;
import org.mathematica.logic.CheckInternet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class SplashScreen extends Activity {

	private WebView welcomePage;
	private Handler splashHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent intent = new Intent(SplashScreen.this, NewMainMenu.class);
			startActivity(intent);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash_screen);

		welcomePage = (WebView) findViewById(R.id.welcomePage);
		welcomePage.loadData(getText(R.string.html_page).toString(),
				"text/html", "UTF-8");

		AppData.applicationContext = this.getApplicationContext();

		AppData.gameNightFont = Typeface.createFromAsset(getAssets(),
				"fonts/game_night.ttf");

		if (!CheckInternet.isOnline()) {
			showOfflineDialog();
		} else {
			Message stopSplashMessage = new Message();
			splashHandler.sendMessageDelayed(stopSplashMessage, 2000);
		}

	}

	private void showOfflineDialog() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					startActivity(new Intent(
							android.provider.Settings.ACTION_SETTINGS));
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Sharpening your mind requires for your device to be online.\n\nClick settings to enable a WiFi connection or enable 3G data.")
				.setTitle("You are offline").setCancelable(false)
				.setIcon(R.drawable.dialog_icon)
				.setNegativeButton("Settings", dialogClickListener)
				.setPositiveButton("Leave", dialogClickListener).show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}