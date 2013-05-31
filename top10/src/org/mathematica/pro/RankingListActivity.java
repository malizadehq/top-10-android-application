package org.mathematica.pro;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RankingListActivity extends Fragment {

	private CustomMessagesAdapter _adaptor;
	private ArrayList<RankedUser> users;
	private ListView _routesListView;
	private ArrayList<Bitmap> userProfilePhotos;
	public Activity act;

	private ProgressDialog loadingDialog = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_rankings, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		act = this.getActivity();
		this.getActivity()
				.getWindow()
				.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);

		_routesListView = (ListView) this.getView().findViewById(
				R.id.shop_items_list);
		_routesListView.setDivider(null);
		_routesListView.setDividerHeight(0);
		loadingDialog = ProgressDialog.show(act, "",
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
											+ "?sz=450").getContent()));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			act.runOnUiThread(new Runnable() {
				public void run() {
					_adaptor = new CustomMessagesAdapter(act,
							R.layout.buyable_item, new ArrayList<RankedUser>());
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
				LayoutInflater vi = (LayoutInflater) act
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.event_list_view_item, null);
			}

			RankedUser user = users.get(position);

			((ImageView) v.findViewById(R.id.profile_picture))
					.setImageBitmap(userProfilePhotos.get(position));

			((TextView) v.findViewById(R.id.ranking_position)).setText(""
					+ (position + 1));

			((TextView) v.findViewById(R.id.ranking_name))
					.setText(user.username);

			((TextView) v.findViewById(R.id.ranking_points)).setText(""
					+ user.overall_score + " tokens");

			Calendar currentDate = Calendar.getInstance();
			currentDate.setTime(new Date());
			Calendar last_played = Calendar.getInstance();
			last_played.setTime(user.last_updated);

			String last_played_formatted_string = "";
			int days = countDiffDay(last_played, currentDate);
			if (days == 0) {
				last_played_formatted_string = "Today";
			} else if (days == 1) {
				last_played_formatted_string = "1 day ago";
			} else {
				last_played_formatted_string = "" + days + " days ago";
			}

			((TextView) v.findViewById(R.id.ranking_last_played))
					.setText(last_played_formatted_string);

			return v;
		}

		public int countDiffDay(Calendar c1, Calendar c2) {
			int returnInt = 0;
			while (!c1.after(c2)) {
				c1.add(Calendar.DAY_OF_MONTH, 1);
				returnInt++;
			}

			if (returnInt > 0) {
				returnInt = returnInt - 1;
			}

			return (returnInt);
		}
	}
}
