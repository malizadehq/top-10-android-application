package org.mathematica.pro;

import org.mathematica.globals.AppData;
import org.mathematica.logic.CheckPurchase;
import org.mathematica.merchent.BuyableItems;

import android.app.Activity;
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
		CheckPurchase.setPurchased(BuyableItems.items.get(17).key, true);

		AppData.quicksandFont = Typeface.createFromAsset(getAssets(),
				"fonts/qs_reg.otf");
		AppData.joystickFont = Typeface.createFromAsset(getAssets(),
				"fonts/joystick.ttf");
		AppData.gameNightFont = Typeface.createFromAsset(getAssets(),
				"fonts/game_night.ttf");
		AppData.westernFont = Typeface.createFromAsset(getAssets(),
				"fonts/western.otf");

		Message stopSplashMessage = new Message();
		splashHandler.sendMessageDelayed(stopSplashMessage, 3000);

	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}