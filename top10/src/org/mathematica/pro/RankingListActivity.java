package org.mathematica.pro;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.mathematica.globals.AppData;
import org.mathematica.logic.CheckInternet;
import org.mathematica.rankings.RankingWrapper;
import org.mathematica.structures.RankedUser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RankingListActivity extends Activity {

	private CustomMessagesAdapter _adaptor;
	private ArrayList<RankedUser> users;
	private ListView _routesListView;
	private ArrayList<Bitmap> userProfilePhotos;

	private ProgressDialog loadingDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_rankings);

		_routesListView = (ListView) findViewById(R.id.shop_items_list);
		_routesListView.setDivider(null);
		_routesListView.setDividerHeight(0);
		loadingDialog = ProgressDialog.show(RankingListActivity.this, "",
				"Searching around the world...");
		loadingDialog.show();
		new UpdateScoreOnServer().execute();
	}

	class UpdateScoreOnServer extends AsyncTask<String, int[], String> {

		protected String doInBackground(String... urls) {
			if (CheckInternet.isOnline()) {
				RankingWrapper.updateData();
			}
			users = RankingWrapper.getTopTen();
			userProfilePhotos = new ArrayList<Bitmap>();
			userProfilePhotos.clear();
			for (int i = 0; i < users.size(); i++) {
				try {
					userProfilePhotos.add(BitmapFactory
							.decodeStream((InputStream) new URL(
									users.get(i).profile_picture_url
											+ "?sz=250").getContent()));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			runOnUiThread(new Runnable() {
				public void run() {
					_adaptor = new CustomMessagesAdapter(
							AppData.applicationContext, R.layout.buyable_item,
							new ArrayList<RankedUser>());
					_routesListView.setAdapter(_adaptor);

					for (int i = 0; i < users.size(); i++) {

						_adaptor.add(new RankedUser());
					}

				}
			});
			return "";
		}

		protected void onPostExecute(String feed) {
			loadingDialog.dismiss();
		}
	}

	private class CustomMessagesAdapter extends ArrayAdapter<RankedUser> {

		public CustomMessagesAdapter(Context context, int textViewResourceId,
				ArrayList<RankedUser> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.event_list_view_item, null);
			}

			RankedUser user = users.get(position);

			((ImageView) v.findViewById(R.id.profile_picture))
					.setImageBitmap(userProfilePhotos.get(position));

			((TextView) v.findViewById(R.id.name_and_position)).setText(""
					+ (position + 1) + ". " + user.username + " : "
					+ user.overall_score + " point"
					+ (user.overall_score == 1 ? "" : "s"));

			return v;
		}
	}
}
