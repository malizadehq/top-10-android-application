package org.mathematica.pro;

import java.util.ArrayList;

import org.mathematica.constants.BUYABLE_TYPE;
import org.mathematica.globals.AppData;
import org.mathematica.logic.CheckInternet;
import org.mathematica.logic.CheckPurchase;
import org.mathematica.logic.PointsManager;
import org.mathematica.merchent.BuyableItem;
import org.mathematica.merchent.BuyableItems;
import org.mathematica.sound.SoundManager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfileActivity extends Activity implements OnClickListener,
		OnLongClickListener {

	private CustomMessagesAdapter _adaptor;
	private ListView _routesListView;

	ImageButton soundOption;
	ImageButton helpOption;
	ImageButton rateOption;
	ImageButton shareContentButton;
	ImageButton podiumOption;

	private Toast activityToast = null;
	private ActionBar actionBar = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_user_profile);

		soundOption = (ImageButton) findViewById(R.id.sound_option);
		helpOption = (ImageButton) findViewById(R.id.help_option);
		rateOption = (ImageButton) findViewById(R.id.rate_option);
		podiumOption = (ImageButton) findViewById(R.id.podium_option);
		shareContentButton = (ImageButton) findViewById(R.id.share_button);
		soundOption.setOnClickListener(this);
		soundOption.setOnLongClickListener(this);
		helpOption.setOnClickListener(this);
		helpOption.setOnLongClickListener(this);
		rateOption.setOnClickListener(this);
		rateOption.setOnLongClickListener(this);
		shareContentButton.setOnClickListener(this);
		shareContentButton.setOnLongClickListener(this);
		podiumOption.setOnClickListener(this);
		podiumOption.setOnLongClickListener(this);

		_routesListView = (ListView) findViewById(R.id.shop_items_list);
		_routesListView.setDivider(null);
		_routesListView.setDividerHeight(0);

		new LoadUi().execute();

		actionBar = getActionBar();
		actionBar.setTitle(AppData.username);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#99cc00")));

		setCurrentAvailablePoints(PointsManager.getPoints());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(UserProfileActivity.this,
					NewMainMenu.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_top_to_bottom_animation,
					R.anim.stay_put1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setCurrentAvailablePoints(int points) {
		actionBar.setSubtitle("" + points + " token" + (points == 1 ? "" : "s")
				+ " available");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.no_items_action_bar, menu);
		return true;
	}

	private class LoadUi extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			_adaptor = new CustomMessagesAdapter(UserProfileActivity.this,
					R.layout.buyable_item, new ArrayList<BuyableItem>());
			_routesListView.setAdapter(_adaptor);

			for (int i = 0; i < BuyableItems.items.size(); i++)
				_adaptor.add(BuyableItems.items.get(i));

			if (SoundManager.isSoundActive()) {
				soundOption.setImageResource(R.drawable.sound_state_action);
			} else {
				soundOption.setImageResource(R.drawable.mute_state_action);
			}
			return null;
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

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(UserProfileActivity.this, NewMainMenu.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_top_to_bottom_animation,
				R.anim.stay_put1);
		return;
	}

	private class CustomMessagesAdapter extends ArrayAdapter<BuyableItem> {

		public CustomMessagesAdapter(Context context, int textViewResourceId,
				ArrayList<BuyableItem> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.buyable_item, null);
			}

			BuyableItem item = BuyableItems.items.get(position);

			((TextView) v.findViewById(R.id.points_needed)).setText(""
					+ item.pointsNeededToBuy);
			((TextView) v.findViewById(R.id.item_description))
					.setText(item.description);

			if (CheckPurchase.isPurchased(item.key)) {
				if (item.type == BUYABLE_TYPE.TIPS_AND_TRICKS) {
					((ImageView) v.findViewById(R.id.image))
							.setImageResource(R.drawable.right_arrow);
				} else {
					((ImageView) v.findViewById(R.id.image))
							.setImageResource(R.drawable.purchased);
				}
			} else {
				((ImageView) v.findViewById(R.id.image))
						.setImageResource(R.drawable.locked);
			}

			((ImageView) v.findViewById(R.id.image)).setTag("" + position);

			((ImageView) v.findViewById(R.id.image))
					.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							int position = Integer.parseInt(v.getTag()
									.toString());

							if (CheckPurchase.isPurchased(BuyableItems.items
									.get(position).key)) {
								if (BuyableItems.items.get(position).type == BUYABLE_TYPE.TIPS_AND_TRICKS) {

									Intent intent = new Intent(
											UserProfileActivity.this,
											TutorialView.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									intent.putExtra(
											"KEY",
											BuyableItems.items.get(position).key);
									startActivity(intent);
									overridePendingTransition(
											R.anim.push_left_in,
											R.anim.push_left_out);
								} else {
									showToast("Already purchased!",
											Toast.LENGTH_SHORT);
								}
							} else {
								int pointsAvailable = PointsManager.getPoints();
								if (pointsAvailable >= BuyableItems.items
										.get(position).pointsNeededToBuy) {
									CheckPurchase.setPurchased(
											BuyableItems.items.get(position).key,
											true);
									showToast("Purchased", Toast.LENGTH_SHORT);
									PointsManager.removePoints(BuyableItems.items
											.get(position).pointsNeededToBuy);

									if (BuyableItems.items.get(position).type == BUYABLE_TYPE.TIPS_AND_TRICKS) {
										((ImageView) v.findViewById(R.id.image))
												.setImageResource(R.drawable.right_arrow);
									} else {
										((ImageView) v.findViewById(R.id.image))
												.setImageResource(R.drawable.purchased);
									}
									setCurrentAvailablePoints(PointsManager
											.getPoints());
								} else {
									showToast("Need more tokens",
											Toast.LENGTH_SHORT);
								}
							}
						}
					});

			return v;
		}
	}

	public void onClick(View v) {
		if (v == helpOption) {
			Intent intent = new Intent(UserProfileActivity.this,
					TutorialActivity.class);
			startActivity(intent);
		}
		if (v == podiumOption) {
			if (CheckInternet.isOnline()) {
				// Intent intent = new Intent(UserProfileActivity.this,
				// RankingListActivity.class);
				startActivity(new Intent(this, MainActivity.class));
			} else {
				Toast.makeText(this, "No internet connection found!",
						Toast.LENGTH_SHORT).show();
			}
		}
		if (v == rateOption) {
			openAndroidMarketToRate();
		}

		if (v == shareContentButton) {
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Join the climb to the Top");
			String shareMessage = "Join the battle to be in the Top 10! How fast can you do calculus, ready to find out? http://tinyurl.com/at43o6v";
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					shareMessage);
			startActivity(Intent.createChooser(shareIntent,
					"Share with friends"));
		}

		if (v == soundOption) {
			SoundManager.setSoundActive(!SoundManager.isSoundActive());
			if (SoundManager.isSoundActive()) {
				soundOption.setImageResource(R.drawable.sound_state_action);
			} else {
				soundOption.setImageResource(R.drawable.mute_state_action);
			}
		}
	}

	private void openAndroidMarketToRate() {
		String appPackageName = "org.mathematica.pro";
		Intent marketIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("market://details?id=" + appPackageName));
		marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
				| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		startActivity(marketIntent);
	}

	public boolean onLongClick(View v) {
		if (v == helpOption) {
			showToast("Game manual", Toast.LENGTH_SHORT);
		}

		if (v == rateOption) {
			showToast("Rate on Play Store", Toast.LENGTH_SHORT);
		}

		if (v == shareContentButton) {
			showToast("Share with friends", Toast.LENGTH_SHORT);
		}

		if (v == soundOption) {
			showToast("Mute/unmute sound in-game", Toast.LENGTH_SHORT);
		}

		if (v == podiumOption) {
			showToast("Top 10", Toast.LENGTH_SHORT);
		}
		return false;
	}

}
