package org.mathematica.pro;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TutorialView extends Activity {

	private WebView webView;
	private static HashMap<String, String> tutorialsMapping;
	private ProgressDialog loadingDialog;

	static {
		tutorialsMapping = new HashMap<String, String>();
		tutorialsMapping.clear();
		tutorialsMapping.put("_TIP1",
				"http://www.wikihow.com/Do-Long-Multiplication");
		tutorialsMapping.put("_TIP2",
				"http://www.ehow.com/how_8255165_divide-faster.html");
		tutorialsMapping
				.put("_TIP3",
						"http://www.ehow.com/how_2238661_add-faster-than-calculator.html");
		tutorialsMapping.put("_TIP4",
				"http://www.mathsisfun.com/numbers/subtraction-quick.html");
		tutorialsMapping
				.put("_TIP5",
						"http://www.glad2teach.co.uk/fast_maths_calculation_tricks.htm");
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_tutorial_view);
		loadingDialog = ProgressDialog.show(TutorialView.this, "",
				"Learning...");
		loadingDialog.show();
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);

		Bundle extras = getIntent().getExtras();
		webView.loadUrl(tutorialsMapping.get(extras.getString("KEY")));

		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				loadingDialog.dismiss();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(TutorialView.this, UserProfileActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
}
