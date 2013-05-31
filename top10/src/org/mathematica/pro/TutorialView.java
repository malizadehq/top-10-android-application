package org.mathematica.pro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.mathematica.rankings.HTTPRequests;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TutorialView extends Activity {

	private WebView webView;
	private static HashMap<String, String> tutorialsMapping;
	private ProgressDialog loadingDialog = null;
	private ActionBar actionBar = null;

	static {
		tutorialsMapping = new HashMap<String, String>();
		tutorialsMapping.clear();
		tutorialsMapping.put("_TIP1", "1");
		tutorialsMapping.put("_TIP2", "2");
		tutorialsMapping.put("_TIP3", "3");
		tutorialsMapping.put("_TIP4", "4");
		tutorialsMapping.put("_TIP5", "5");
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_tutorial_view);

		actionBar = getActionBar();
		actionBar.setTitle("Tutorial");
		actionBar.setDisplayHomeAsUpEnabled(true);

		loadingDialog = ProgressDialog.show(TutorialView.this, "",
				"Learning...");
		loadingDialog.show();
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);

		runOnUiThread(new Runnable() {
			public void run() {
				new LoadWebPageReceivedFromServer().execute();
			}
		});

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

	class LoadWebPageReceivedFromServer extends
			AsyncTask<Integer, int[], String> {

		protected String doInBackground(Integer... urls) {
			Bundle extras = getIntent().getExtras();

			HTTPRequests httpRequest = HTTPRequests.getInstante();
			List<NameValuePair> arguments = new ArrayList<NameValuePair>(1);
			String tutorial_id = tutorialsMapping.get(extras.getString("KEY"))
					.toString();
			arguments.add(new BasicNameValuePair("tutorial_id", tutorial_id));
			String module = "retrieve_tutorial.php";
			String tutorial_link = httpRequest.doHTTPRequestStringPost(module,
					arguments);

			tutorial_link = tutorial_link.substring(0,
					tutorial_link.indexOf("\n"));

			webView.loadUrl(tutorial_link);
			return "";
		}

		protected void onPostExecute(String feed) {
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(TutorialView.this,
					UserProfileActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.no_items_action_bar, menu);
		return true;
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
