package org.mathematica.pro;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.mathematica.constants.BUYABLE_TYPE;
import org.mathematica.constants.OP_DIFF;
import org.mathematica.constants.OP_DIRECTION;
import org.mathematica.constants.TILE_TYPE;
import org.mathematica.globals.AppData;
import org.mathematica.logic.Board;
import org.mathematica.logic.CheckInternet;
import org.mathematica.logic.CheckPurchase;
import org.mathematica.logic.PointsManager;
import org.mathematica.logic.Question;
import org.mathematica.logic.QuestionsExtracter;
import org.mathematica.logic.SavedGameData;
import org.mathematica.logic.TransfUtils;
import org.mathematica.merchent.BuyableItem;
import org.mathematica.merchent.BuyableItems;
import org.mathematica.rankings.RankingWrapper;
import org.mathematica.sound.SoundManager;
import org.mathematica.structures.Level;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GameScreen extends Activity implements OnClickListener,
		OnLongClickListener {

	private static final String NO_QUESTION_STRING = "";
	private static final int DIFFICULTY_TIME_RATIO = 6;
	private static final int XP_TO_POINTS_RATIO = 50;
	private ArrayList<Button> tiles;
	private TextView horQuestion;
	private TextView vertQuestion;

	private boolean newGame = false;

	private int currentGamePoints = 0;

	private int currentLevelRows = 0;
	private int currentLevelColumns = 0;
	private int currentLevelDigits = 0;
	private int combo = 1;
	private int currentLevelDifficulty;
	private int currentLevelGameMode;

	private Timer timer;
	private int secondsLeft = 0;
	private int totalGameTime = 0;

	private Toast activityToast = null;
	private int previousSelectedTileIndex = 0;
	private int availableLives;
	private int livesLeft;

	ActionBar actionBar = null;
	MenuItem remainingTimeAction = null;
	MenuItem remainingLivesAction = null;
	TextView statusMessage = null;

	private ProgressBar progressBar = null;
	private int totalAvailableTiles = 0;

	private boolean backWillNotWork = false;

	private int perfectGameBonus = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_game_screen);

		actionBar = getActionBar();

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setProgress(0);
		progressBar.setMax(100);

		horQuestion = (TextView) findViewById(R.id.hor_question);
		vertQuestion = (TextView) findViewById(R.id.vert_question);

		Bundle extras = getIntent().getExtras();
		Level levelInformations = (Level) extras.getSerializable("ITEM");

		currentLevelRows = levelInformations.rows;
		currentLevelColumns = levelInformations.columns;
		currentLevelDifficulty = levelInformations.levelDifficulty;
		currentLevelDigits = levelInformations.wordLength;
		currentLevelGameMode = levelInformations.gameMode;
		availableLives = calculateGameLives();
		livesLeft = availableLives;

		newGame = extras.getBoolean("NEW_GAME");

		if (!newGame) {
			currentLevelRows = SavedGameData
					.getSavedInt(SavedGameData.SAVED_ROWS);
			currentLevelColumns = SavedGameData
					.getSavedInt(SavedGameData.SAVED_COLUMNS);
			currentLevelDifficulty = SavedGameData
					.getSavedInt(SavedGameData.SAVED_DIFF);
			currentLevelGameMode = SavedGameData
					.getSavedInt(SavedGameData.SAVED_GAME_MODE);
			livesLeft = SavedGameData
					.getSavedInt(SavedGameData.SAVED_LIVES_LEFT);
		}

		setUpTiles();
		setTilesListener();
		drawGameBoard();
		setUpQuestions(currentLevelDifficulty);
		refreshGameTilesBackground();

		totalAvailableTiles = 0;
		for (int i = 0; i < AppData.ROWS; i++) {
			for (int j = 0; j < AppData.COLUMNS; j++) {
				if (AppData.board[i][j] != -1) {
					totalAvailableTiles++;
				}
			}
		}

		secondsLeft = calculateGameTime();
		totalGameTime = secondsLeft;

		if (newGame) {
			showHints();
		}
		AppData.oldMessage = "";

		new LoadUI().execute();

		timer = new Timer();
		if (currentLevelGameMode == 0) {
			timer = new Timer();
			timer.execute();
		}

		actionBar.setDisplayHomeAsUpEnabled(true);

		statusMessage = ((TextView) findViewById(R.id.game_info_screen));
	}

	private void increaseLivesLeft() {
		if (livesLeft < availableLives) {
			livesLeft++;
		}
		showLivesLeft(livesLeft);
	}

	private void updateGameProgressBar(int filledInTiles) {
		progressBar
				.setProgress((int) ((filledInTiles * 100) / (int) totalAvailableTiles));
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (secondsLeft <= 0 && !SavedGameData.isGameInProgress()) {
			return;
		}

		if (livesLeft == 0 && !SavedGameData.isGameInProgress()) {
			return;
		}

		if (livesLeft != 0 && secondsLeft != 0
				&& !SavedGameData.isGameInProgress()) {
			return;
		}

		timer.cancel(true);
		saveCurrentLevelDetails();
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			timer.cancel(true);
			saveCurrentLevelDetails();
			Intent intent = new Intent(GameScreen.this, NewMainMenu.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_bottom_up_animation,
					R.anim.stay_put);
			return true;
		case R.id.show_notes_board:
			Dialog dialog = null;
			final CustomNotesDialog.Builder customBuilder = new CustomNotesDialog.Builder(
					GameScreen.this);
			dialog = customBuilder.create();
			dialog.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game_screen_action_bar, menu);
		remainingTimeAction = (MenuItem) menu.findItem(R.id.remaining_time);
		remainingLivesAction = (MenuItem) menu.findItem(R.id.remaining_lives);

		if (currentLevelGameMode == 1) {
			remainingTimeAction.setTitle("Unlimited time");
			actionBar.setBackgroundDrawable(null);
		}

		if (currentLevelGameMode == 0) {
			showLivesLeft(livesLeft);
		}

		return true;
	}

	private class LoadUI extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			runOnUiThread(new Runnable() {
				public void run() {

					for (Button s : tiles) {
						s.setTextSize(32);
						s.setTextColor(Color.BLACK);

					}

					for (int i = 0; i < AppData.ROWS; i++) {
						for (int j = 0; j < AppData.COLUMNS; j++) {
							tiles.get(TransfUtils.getIndexFromPosition(i, j))
									.setVisibility(View.GONE);
						}
					}

					for (int i = 0; i < currentLevelRows; i++) {
						for (int j = 0; j < currentLevelColumns; j++) {
							tiles.get(TransfUtils.getIndexFromPosition(i, j))
									.setVisibility(View.VISIBLE);
						}
					}

					for (int i = AppData.ROWS; i > currentLevelRows; i--) {
						switch (i) {
						case 9:
							((LinearLayout) findViewById(R.id.line9))
									.setVisibility(View.GONE);
							break;
						case 8:
							((LinearLayout) findViewById(R.id.line8))
									.setVisibility(View.GONE);
							break;
						case 7:
							((LinearLayout) findViewById(R.id.line7))
									.setVisibility(View.GONE);
							break;
						case 6:
							((LinearLayout) findViewById(R.id.line6))
									.setVisibility(View.GONE);
							break;
						case 5:
							((LinearLayout) findViewById(R.id.line5))
									.setVisibility(View.GONE);
							break;
						case 4:
							((LinearLayout) findViewById(R.id.line4))
									.setVisibility(View.GONE);
							break;
						case 3:
							((LinearLayout) findViewById(R.id.line3))
									.setVisibility(View.GONE);
							break;
						case 2:
							((LinearLayout) findViewById(R.id.line2))
									.setVisibility(View.GONE);
							break;
						case 1:
							((LinearLayout) findViewById(R.id.line1))
									.setVisibility(View.GONE);
							break;
						}
					}

					if (Math.abs(currentLevelRows - currentLevelColumns) >= 2) {
						((LinearLayout) findViewById(R.id.line9))
								.setVisibility(View.VISIBLE);
						((LinearLayout) findViewById(R.id.line8))
								.setVisibility(View.VISIBLE);
					}

					if (!newGame) {
						secondsLeft = SavedGameData
								.getSavedInt(SavedGameData.SAVED_TIME);
						totalGameTime = secondsLeft;
						currentGamePoints = SavedGameData
								.getSavedInt(SavedGameData.SAVED_XP);
						if (currentLevelGameMode == 0) {
							actionBar.setTitle("" + currentGamePoints
									+ " POINTS");
						} else {
							actionBar.setTitle("POINTLESS");
						}
						combo = SavedGameData
								.getSavedInt(SavedGameData.SAVED_COMBO);

						String game = SavedGameData.getLastGame();
						StringTokenizer st = new StringTokenizer(game, ";");
						for (int i = 0; i < AppData.ROWS; i++) {
							for (int j = 0; j < AppData.COLUMNS; j++) {
								if (st.hasMoreTokens()) {
									String val = st.nextToken();
									if (!val.trim().equals("X")) {
										tiles.get(
												TransfUtils
														.getIndexFromPosition(
																i, j)).setText(
												"" + val);
									}
								}
							}
						}
						int correctTiles = 0;
						for (int i = 0; i < currentLevelRows; i++) {
							for (int j = 0; j < currentLevelColumns; j++) {
								int index = TransfUtils.getIndexFromPosition(i,
										j);
								if (tiles.get(index).isEnabled()) {
									try {
										if (Integer.parseInt(tiles.get(index)
												.getText().toString()) == AppData.board[i][j]) {
											correctTiles++;
										}
									} catch (Exception e) {
									}
								}
							}
						}
						updateGameProgressBar(correctTiles);
					} else {
						if (currentLevelGameMode == 0) {
							actionBar.setTitle("0 POINTS");
						} else {
							actionBar.setTitle("POINTLESS");
						}
					}
				}
			});
			return null;
		}
	}

	private void saveCurrentLevelDetails() {
		SavedGameData.saveInt(SavedGameData.SAVED_ROWS, currentLevelRows);
		SavedGameData.saveInt(SavedGameData.SAVED_COLUMNS, currentLevelColumns);
		SavedGameData.saveInt(SavedGameData.SAVED_XP, currentGamePoints);
		SavedGameData.saveInt(SavedGameData.SAVED_COMBO, combo);
		SavedGameData.saveInt(SavedGameData.SAVED_TIME, secondsLeft);
		SavedGameData.saveInt(SavedGameData.SAVED_DIFF, currentLevelDifficulty);
		SavedGameData.saveInt(SavedGameData.SAVED_LIVES_LEFT, livesLeft);
		SavedGameData.saveInt(SavedGameData.SAVED_GAME_MODE,
				currentLevelGameMode);
		String game = "";
		for (int i = 0; i < AppData.ROWS; i++) {
			for (int j = 0; j < AppData.COLUMNS; j++) {
				String val = tiles.get(TransfUtils.getIndexFromPosition(i, j))
						.getText().toString();

				game += (val.length() == 0) ? "X" : val;
				game += ";";
			}
		}

		String board = "";
		for (int i = 0; i < AppData.ROWS; i++) {
			for (int j = 0; j < AppData.COLUMNS; j++) {
				board += "" + AppData.board[i][j];
				board += ";";
			}
		}

		SavedGameData.setGameInProgress(true);
		SavedGameData.saveBoard(board);
		SavedGameData.saveGame(game);

		Toast.makeText(this, "Game progress saved", Toast.LENGTH_SHORT).show();
	}

	private void showToast(String message, int duration) {
		if (activityToast != null) {
			activityToast.cancel();
		}
		activityToast = Toast.makeText(AppData.applicationContext, message,
				duration);
		activityToast.show();
	}

	private void showHints() {

		if (currentLevelColumns < 6 || currentLevelRows < 6) {
			return;
		}

		int availableHints = 0;
		for (BuyableItem item : BuyableItems.items) {
			if (CheckPurchase.isPurchased(item.key)
					&& item.type == BUYABLE_TYPE.EXTRA_TILE) {
				availableHints += item.value;
			}
		}

		if (availableHints > 0) {
			while (availableHints > 0) {
				int x = (int) (Math.random() * currentLevelRows);
				int y = (int) (Math.random() * currentLevelColumns);
				Button highlighted = tiles.get(TransfUtils
						.getIndexFromPosition(x, y));
				if (highlighted.isEnabled() && highlighted.getText() == "") {
					highlighted.setText("" + AppData.board[x][y]);
					--availableHints;
				}
			}
		}

	}

	private int calculateGameTime() {
		int gameTime = 0;
		for (Button tile : tiles) {
			if (tile.isEnabled()) {
				gameTime += (DIFFICULTY_TIME_RATIO * (currentLevelDifficulty + 1));
			}
		}

		int bonusTime = 0;
		for (BuyableItem item : BuyableItems.items) {
			if (CheckPurchase.isPurchased(item.key)
					&& item.type == BUYABLE_TYPE.EXTRA_TIME) {
				bonusTime += item.value;
			}
		}
		return (gameTime + bonusTime);
	}

	private int calculateGameLives() {
		int lives = 3;
		int bonusLives = 0;
		for (BuyableItem item : BuyableItems.items) {
			if (CheckPurchase.isPurchased(item.key)
					&& item.type == BUYABLE_TYPE.EXTRA_ALLOWED_MISTAKE) {
				bonusLives += item.value;
			}
		}
		return (lives + bonusLives);
	}

	private void updateTime() {
		remainingTimeAction.setTitle(formatSeconds(secondsLeft));

		int timeUpdatesTextColor = Color.parseColor("#33b5e5");

		if (secondsLeft == (int) (totalGameTime * 0.75)) {
			showCustomMessageOnScreen("Don't forget to watch the time!",
					Toast.LENGTH_LONG, timeUpdatesTextColor);
			return;
		}

		if (secondsLeft == (int) (totalGameTime * 0.5)) {
			showCustomMessageOnScreen("Half the time, gone!",
					Toast.LENGTH_LONG, timeUpdatesTextColor);
			return;
		}

		if (secondsLeft == (int) (totalGameTime * 0.25)) {
			showCustomMessageOnScreen("Three quarters into the round!",
					Toast.LENGTH_LONG, timeUpdatesTextColor);
			return;
		}

		if (secondsLeft == 1) {
			showCustomMessageOnScreen("Out of time!", Toast.LENGTH_SHORT,
					timeUpdatesTextColor);
			return;
		}

		if (secondsLeft == 0) {
			SoundManager.playSound(R.raw.sound_lost_level);
			timer.cancel(true);
			SavedGameData.setGameInProgress(false);
			Intent intent = new Intent(GameScreen.this, NewMainMenu.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_bottom_up_animation,
					R.anim.stay_put);
		}
	}

	private String formatSeconds(int seconds) {
		int min = seconds / 60;
		int sec = seconds % 60;
		String formattedTime = "";
		formattedTime = (min > 9) ? ("" + min) : ("0" + min);
		formattedTime += ":";
		formattedTime += (sec > 9) ? ("" + sec) : ("0" + sec);
		return formattedTime;
	}

	private class Timer extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			while (secondsLeft > 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					return null;
				}
				--secondsLeft;
				runOnUiThread(new Runnable() {
					public void run() {
						updateTime();
					}
				});
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

	@Override
	public void onBackPressed() {

		if (backWillNotWork) {
			return;
		}

		timer.cancel(true);
		saveCurrentLevelDetails();
		Intent intent = new Intent(GameScreen.this, NewMainMenu.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_bottom_up_animation,
				R.anim.stay_put);
		return;
	}

	private void showLivesLeft(final int livesLeft) {
		Handler mMusicHandler = new Handler() {
		};

		Runnable mMusicRunnable = new Runnable() {
			public void run() {
				final char ALIVE_HEART = (char) 0x2665;
				final char DEAD_HEART = (char) 0x2661;
				char message[] = new char[9];
				for (int i = 0; i < 9; i++) {
					message[i] = '\0';
				}
				for (int i = 0; i < availableLives; i++) {
					message[i] = DEAD_HEART;
				}
				for (int i = 0; i < livesLeft; i++) {
					message[i] = ALIVE_HEART;
				}
				remainingLivesAction.setTitle(new String(message));
			}
		};
		mMusicHandler.post(mMusicRunnable);
	}

	private void showCustomMessageOnScreen(final String message,
			final int duration, final int color) {

		statusMessage.setTextColor(color);
		statusMessage.setText(message);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				statusMessage.setText("");
			}
		}, 2500);
	}

	private void drawQuestionTiles() {
		for (Question elem : AppData.questions) {
			paintTile(tiles.get(TransfUtils.getIndexFromPosition(elem.getRow(),
					elem.getColumn())), TILE_TYPE.WITH_QUESTIONS);
		}
	}

	private void setUpQuestions(int diff) {
		QuestionsExtracter analizer = new QuestionsExtracter(AppData.board,
				OP_DIFF.values()[diff]);
		AppData.questions = analizer.getExtractedSequences();

		switch (diff) {
		case 0:
			actionBar.setBackgroundDrawable(new ColorDrawable(Color
					.parseColor("#33b5e5")));
			break;
		case 1:
			actionBar.setBackgroundDrawable(new ColorDrawable(Color
					.parseColor("#ffbb33")));
			break;
		case 2:
			actionBar.setBackgroundDrawable(new ColorDrawable(Color
					.parseColor("#ff4444")));
			break;
		}
	}

	private void drawGameBoard() {
		if (!newGame) {
			AppData.board = new int[AppData.ROWS][AppData.COLUMNS];
			String board = SavedGameData.getLastBoard();
			StringTokenizer st = new StringTokenizer(board, ";");
			for (int i = 0; i < AppData.ROWS; i++) {
				for (int j = 0; j < AppData.COLUMNS; j++) {
					if (st.hasMoreTokens()) {
						String val = st.nextToken();
						AppData.board[i][j] = Integer.parseInt(val);
					}
				}
			}
		} else {
			Board board = new Board(currentLevelRows, currentLevelColumns,
					currentLevelDigits);
			AppData.board = board.getBoard();
		}
	}

	private void refreshGameTilesBackground() {
		for (int i = 0; i < AppData.ROWS; i++) {
			for (int j = 0; j < AppData.COLUMNS; j++) {
				paintTile(tiles.get(TransfUtils.getIndexFromPosition(i, j)),
						TILE_TYPE.NORMAL);
			}
		}

		for (int i = 0; i < AppData.ROWS; i++) {
			for (int j = 0; j < AppData.COLUMNS; j++) {
				int value = AppData.board[i][j];
				if (value == -1) {
					paintTile(
							tiles.get(TransfUtils.getIndexFromPosition(i, j)),
							TILE_TYPE.BLOCKED);
					tiles.get(TransfUtils.getIndexFromPosition(i, j)).setText(
							"");
					tiles.get(TransfUtils.getIndexFromPosition(i, j))
							.setEnabled(false);
				}
			}
		}

		drawQuestionTiles();
	}

	private void setTilesListener() {
		for (Button tile : tiles) {
			tile.setOnClickListener(this);
			tile.setOnLongClickListener(this);
		}
	}

	private void setUpTiles() {
		tiles = new ArrayList<Button>();
		tiles.add((Button) findViewById(R.id.tile11));
		tiles.add((Button) findViewById(R.id.tile12));
		tiles.add((Button) findViewById(R.id.tile13));
		tiles.add((Button) findViewById(R.id.tile14));
		tiles.add((Button) findViewById(R.id.tile15));
		tiles.add((Button) findViewById(R.id.tile16));
		tiles.add((Button) findViewById(R.id.tile17));
		tiles.add((Button) findViewById(R.id.tile21));
		tiles.add((Button) findViewById(R.id.tile22));
		tiles.add((Button) findViewById(R.id.tile23));
		tiles.add((Button) findViewById(R.id.tile24));
		tiles.add((Button) findViewById(R.id.tile25));
		tiles.add((Button) findViewById(R.id.tile26));
		tiles.add((Button) findViewById(R.id.tile27));
		tiles.add((Button) findViewById(R.id.tile31));
		tiles.add((Button) findViewById(R.id.tile32));
		tiles.add((Button) findViewById(R.id.tile33));
		tiles.add((Button) findViewById(R.id.tile34));
		tiles.add((Button) findViewById(R.id.tile35));
		tiles.add((Button) findViewById(R.id.tile36));
		tiles.add((Button) findViewById(R.id.tile37));
		tiles.add((Button) findViewById(R.id.tile41));
		tiles.add((Button) findViewById(R.id.tile42));
		tiles.add((Button) findViewById(R.id.tile43));
		tiles.add((Button) findViewById(R.id.tile44));
		tiles.add((Button) findViewById(R.id.tile45));
		tiles.add((Button) findViewById(R.id.tile46));
		tiles.add((Button) findViewById(R.id.tile47));
		tiles.add((Button) findViewById(R.id.tile51));
		tiles.add((Button) findViewById(R.id.tile52));
		tiles.add((Button) findViewById(R.id.tile53));
		tiles.add((Button) findViewById(R.id.tile54));
		tiles.add((Button) findViewById(R.id.tile55));
		tiles.add((Button) findViewById(R.id.tile56));
		tiles.add((Button) findViewById(R.id.tile57));
		tiles.add((Button) findViewById(R.id.tile61));
		tiles.add((Button) findViewById(R.id.tile62));
		tiles.add((Button) findViewById(R.id.tile63));
		tiles.add((Button) findViewById(R.id.tile64));
		tiles.add((Button) findViewById(R.id.tile65));
		tiles.add((Button) findViewById(R.id.tile66));
		tiles.add((Button) findViewById(R.id.tile67));
		tiles.add((Button) findViewById(R.id.tile71));
		tiles.add((Button) findViewById(R.id.tile72));
		tiles.add((Button) findViewById(R.id.tile73));
		tiles.add((Button) findViewById(R.id.tile74));
		tiles.add((Button) findViewById(R.id.tile75));
		tiles.add((Button) findViewById(R.id.tile76));
		tiles.add((Button) findViewById(R.id.tile77));
		tiles.add((Button) findViewById(R.id.tile81));
		tiles.add((Button) findViewById(R.id.tile82));
		tiles.add((Button) findViewById(R.id.tile83));
		tiles.add((Button) findViewById(R.id.tile84));
		tiles.add((Button) findViewById(R.id.tile85));
		tiles.add((Button) findViewById(R.id.tile86));
		tiles.add((Button) findViewById(R.id.tile87));
		tiles.add((Button) findViewById(R.id.tile91));
		tiles.add((Button) findViewById(R.id.tile92));
		tiles.add((Button) findViewById(R.id.tile93));
		tiles.add((Button) findViewById(R.id.tile94));
		tiles.add((Button) findViewById(R.id.tile95));
		tiles.add((Button) findViewById(R.id.tile96));
		tiles.add((Button) findViewById(R.id.tile97));
	}

	public void onClick(View v) {

		refreshGameTilesBackground();
		for (int i = 0; i < tiles.size(); i++) {
			if (v == tiles.get(i)) {
				SoundManager.playSound(R.raw.sound_tile_clicked);
				ScaleAnimation ranim = (ScaleAnimation) AnimationUtils
						.loadAnimation(AppData.applicationContext, R.anim.zoom);
				ranim.setFillAfter(true);
				v.clearAnimation();
				v.setAnimation(ranim);
				paintTile(tiles.get(i), TILE_TYPE.SELECTED);
				int[] pos = TransfUtils.getPositionFromIndex(i);
				Question questionHor = getQuestion(pos[0], pos[1],
						OP_DIRECTION.HOR);
				Question questionVert = getQuestion(pos[0], pos[1],
						OP_DIRECTION.VERT);
				if (questionHor != null && questionVert != null
						&& questionHor.getCorrectAnswer() < 10
						&& questionVert.getCorrectAnswer() < 10) {
					if (questionHor.getQuestionString().length() > questionVert
							.getQuestionString().length()) {
						setQuestionString(horQuestion,
								questionHor.getQuestionString());
						setQuestionString(vertQuestion, NO_QUESTION_STRING);
					} else if (questionHor.getQuestionString().length() < questionVert
							.getQuestionString().length()) {
						setQuestionString(horQuestion,
								questionHor.getQuestionString());
						setQuestionString(vertQuestion, NO_QUESTION_STRING);
					} else {
						setQuestionString(horQuestion,
								questionHor.getQuestionString());
						setQuestionString(vertQuestion, NO_QUESTION_STRING);
					}
				} else {
					if (questionHor != null)
						setQuestionString(horQuestion,
								questionHor.getQuestionString());
					else
						setQuestionString(horQuestion, NO_QUESTION_STRING);

					if (questionVert != null)
						setQuestionString(vertQuestion,
								questionVert.getQuestionString());
					else
						setQuestionString(vertQuestion, NO_QUESTION_STRING);
				}

				updateQuestionSpanOnBoard(pos[0], pos[1]);
			}
		}

	}

	private void setQuestionString(TextView view, String question) {
		view.setText(question);
	}

	private void paintTile(Button tile, TILE_TYPE type) {
		switch (type) {
		case NORMAL:
			tile.setBackgroundResource(R.drawable.tile_normal_shade);
			break;
		case WITH_QUESTIONS:
			Resources r = getResources();
			Drawable[] layers = new Drawable[2];
			LayerDrawable layerDrawable = null;
			layers[0] = r.getDrawable(R.drawable.tile_normal_shade);
			layers[1] = r.getDrawable(R.drawable.question_mark_layer);
			layerDrawable = new LayerDrawable(layers);
			tile.setBackgroundDrawable(layerDrawable);
			break;
		case BLOCKED:
			tile.setBackgroundResource(R.drawable.tile_blocked_shade);
			break;
		case SELECTED:
			if (currentLevelGameMode == 1) {
				tile.setBackgroundResource(R.drawable.tile_unlimited_shade);
				return;
			}

			switch (currentLevelDifficulty) {
			case 0:
				tile.setBackgroundResource(R.drawable.tile_easy_shade);
				break;
			case 1:
				tile.setBackgroundResource(R.drawable.tile_medium_shade);
				break;
			case 2:
				tile.setBackgroundResource(R.drawable.tile_hard_shade);
				break;
			}
			break;
		case HIGHLIGHTED:
			if (currentLevelGameMode == 1) {
				tile.setBackgroundResource(R.drawable.tile_unlimited_highlight_shade);
				return;
			}

			switch (currentLevelDifficulty) {
			case 0:
				tile.setBackgroundResource(R.drawable.tile_easy_highlight_shade);
				break;
			case 1:
				tile.setBackgroundResource(R.drawable.tile_medium_highlight_shade);
				break;
			case 2:
				tile.setBackgroundResource(R.drawable.tile_hard_highlight_shade);
				break;
			}
			break;
		}
	}

	private void updateQuestionSpanOnBoard(int row, int column) {

		ScaleAnimation ranim = (ScaleAnimation) AnimationUtils.loadAnimation(
				AppData.applicationContext, R.anim.zoom);
		ranim.setFillAfter(true);

		for (Question elem : AppData.questions) {
			if (elem.getRow() == row && elem.getColumn() == column) {
				if (elem.getAnswerDirection() == OP_DIRECTION.HOR) {
					for (int i = column + 1; i < currentLevelColumns; i++) {
						int index = TransfUtils.getIndexFromPosition(row, i);
						if (tiles.get(index).isEnabled()) {
							paintTile(tiles.get(index), TILE_TYPE.HIGHLIGHTED);
							tiles.get(index).clearAnimation();
							tiles.get(index).setAnimation(ranim);
						} else {
							break;
						}
					}
				}

				if (elem.getAnswerDirection() == OP_DIRECTION.VERT) {
					for (int i = row + 1; i < currentLevelRows; i++) {
						int index = TransfUtils.getIndexFromPosition(i, column);
						if (tiles.get(index).isEnabled()) {
							paintTile(tiles.get(index), TILE_TYPE.HIGHLIGHTED);
							tiles.get(index).clearAnimation();
							tiles.get(index).setAnimation(ranim);
						} else {
							break;
						}
					}
				}
			}
		}
	}

	private Question getQuestion(int i, int j, OP_DIRECTION dir) {
		for (Question elem : AppData.questions) {
			if (elem.getRow() == i && elem.getColumn() == j
					&& elem.getAnswerDirection() == dir) {
				return elem;
			}
		}
		return null;
	}

	public boolean onLongClick(View v) {

		for (int i = 0; i < tiles.size(); i++) {
			if (v == tiles.get(i)) {
				final Button current = tiles.get(i);
				final int previous = previousSelectedTileIndex;
				final int currentIndex = i;
				Dialog dialog = null;
				final CustomTextDialog.Builder customBuilder = new CustomTextDialog.Builder(
						GameScreen.this);
				customBuilder
						.setCloseListener(new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								String value = customBuilder.getValue();
								current.setText(value);

								checkAnswer(currentIndex, value);

								if (currentIndex != previous) {
									checkCurrentEvolution(current,
											Integer.parseInt(value));
								}
								previousSelectedTileIndex = currentIndex;
								checkGameProgress();

								dialog.dismiss();
							}
						});
				dialog = customBuilder.create();
				dialog.show();
			}
		}
		return false;
	}

	private void checkAnswer(int index, String value) {
		int inserted = Integer.parseInt(value);
		int pos[] = TransfUtils.getPositionFromIndex(index);
		int desired = AppData.board[pos[0]][pos[1]];
		if (inserted != desired) {

			showCustomMessageOnScreen("WRONG!", Toast.LENGTH_SHORT,
					Color.parseColor("#ff4444"));
			perfectGameBonus = 0;

			if (currentLevelGameMode == 1) {
				return;
			}

			livesLeft--;
			if (livesLeft == 0) {
				SoundManager.playSound(R.raw.sound_lost_level);
				showCustomMessageOnScreen("Out of lives!", Toast.LENGTH_SHORT,
						0);
				timer.cancel(true);
				SavedGameData.setGameInProgress(false);
				Intent intent = new Intent(GameScreen.this, NewMainMenu.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_bottom_up_animation,
						R.anim.stay_put);
			} else {
				showLivesLeft(livesLeft);
			}
		} else {
			showCustomMessageOnScreen("CORRECT!", Toast.LENGTH_SHORT,
					Color.parseColor("#99cc00"));
		}
	}

	private void checkCurrentEvolution(Button pressedButton, int insertedValue) {
		for (int i = 0; i < tiles.size(); i++) {
			if (tiles.get(i) == pressedButton) {
				int[] pos = TransfUtils.getPositionFromIndex(i);
				if (AppData.board[pos[0]][pos[1]] == insertedValue) {
					combo++;
				} else {
					combo = 0;
				}
				showCombo(combo);

				boolean comboIsNotZero = (combo != 0);
				boolean highEnoughtCombo = (combo
						% (5 * (currentLevelDifficulty + 1)) == 0);
				if (comboIsNotZero && highEnoughtCombo) {
					increaseLivesLeft();
				}

			}
		}
	}

	private void showCombo(int combo) {
		int pointsToAdd = combo * (currentLevelDifficulty + 1);
		increaseGamePoints(pointsToAdd);
	}

	private void increaseGamePoints(int pointsToAdd) {
		if (currentLevelGameMode == 1) {
			currentGamePoints = 1;
			return;
		}
		currentGamePoints += pointsToAdd;
		actionBar.setTitle("" + currentGamePoints + " POINTS");

		String message = "";
		if (currentGamePoints >= 5000) {
			message = "Ph.D!";
		} else if (currentGamePoints >= 3000) {
			message = "MAXIMUM!";
		} else if (currentGamePoints >= 1000) {
			message = "WICKED!";
		} else if (currentGamePoints >= 500) {
			message = "GENIUS!";
		} else if (currentGamePoints >= 300) {
			message = "SUPERB!";
		} else if (currentGamePoints >= 100) {
			message = "GREAT!";
		} else if (currentGamePoints >= 50) {
			message = "GOOD!";
		} else if (currentGamePoints >= 10) {
			message = "WARMING UP!";
		}

		int color = Color.parseColor("#509DA0");

		if (message.length() > 0) {
			if (!AppData.oldMessage.equals(message)) {
				showCustomMessageOnScreen(message, Toast.LENGTH_SHORT, color);
			}
			AppData.oldMessage = message;
		}
	}

	private void checkGameProgress() {
		boolean allTilesFilledOut = true;
		for (Button s : tiles) {
			String value = s.getText().toString();
			if (s.isEnabled() && s.getVisibility() == View.VISIBLE) {
				try {
					if (value == "") {
						allTilesFilledOut = false;
					}
				} catch (NumberFormatException e) {
					allTilesFilledOut = false;
				}
			}
		}

		int correctTiles = 0;
		for (int i = 0; i < currentLevelRows; i++) {
			for (int j = 0; j < currentLevelColumns; j++) {
				int index = TransfUtils.getIndexFromPosition(i, j);
				if (tiles.get(index).isEnabled()) {
					try {
						if (Integer.parseInt(tiles.get(index).getText()
								.toString()) == AppData.board[i][j]) {
							correctTiles++;
						}
					} catch (Exception e) {
					}
				}
			}
		}
		updateGameProgressBar(correctTiles);

		if (allTilesFilledOut) {
			/*
			 * Check is tiles have been filled out all-right If YES then finish
			 * the game if NO then mark the wrong tiles
			 */
			refreshGameTilesBackground();
			boolean gameFinished = true;
			int mistakes = 0;
			for (int i = 0; i < currentLevelRows; i++) {
				for (int j = 0; j < currentLevelColumns; j++) {
					int index = TransfUtils.getIndexFromPosition(i, j);
					if (tiles.get(index).isEnabled()) {
						if (Integer.parseInt(tiles.get(index).getText()
								.toString()) != AppData.board[i][j]) {
							tiles.get(index).setBackgroundResource(
									R.drawable.tile_with_wrong_answer);
							gameFinished = false;
							mistakes++;
						} else {
							correctTiles++;
						}
					}
				}
			}
			if (gameFinished) {
				SoundManager.playSound(R.raw.sound_completed_level);
				doGameFinishedActions();
			} else {
				showToast("You still have " + mistakes + " mistake(s)",
						Toast.LENGTH_SHORT);
			}
		}
	}

	private void doGameFinishedActions() {
		int earnedPoints = currentGamePoints / XP_TO_POINTS_RATIO;
		timer.cancel(true);
		backWillNotWork = true;

		if (currentLevelGameMode == 1) {
			SavedGameData.setGameInProgress(false);
			PointsManager.addPoints(earnedPoints);
			new UpdateScoreOnServer().execute();
			Intent intent = new Intent(GameScreen.this, NewMainMenu.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_bottom_up_animation,
					R.anim.stay_put);
			return;
		}

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					SavedGameData.setGameInProgress(false);
					int earnedPoints = currentGamePoints / XP_TO_POINTS_RATIO;
					PointsManager.addPoints(earnedPoints + perfectGameBonus);
					new UpdateScoreOnServer().execute();
					Intent intent = new Intent(GameScreen.this,
							NewMainMenu.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_bottom_up_animation,
							R.anim.stay_put);
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					SavedGameData.setGameInProgress(false);
					earnedPoints = currentGamePoints / XP_TO_POINTS_RATIO;
					PointsManager.addPoints(earnedPoints + perfectGameBonus);
					new UpdateScoreOnServer().execute();
					intent = new Intent(GameScreen.this, NewMainMenu.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_bottom_up_animation,
							R.anim.stay_put);
					Intent shareIntent = new Intent(
							android.content.Intent.ACTION_SEND);
					shareIntent.setType("text/plain");
					shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
							"Calculus expert");
					String shareMessage = "I have just obtained a score of "
							+ currentGamePoints
							+ " points. I am doing my best to reach the Top 10! http://tinyurl.com/at43o6v";
					shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
							shareMessage);
					startActivity(Intent.createChooser(shareIntent,
							"Let the world know"));
					break;
				}
			}
		};

		String extra = "";
		if (perfectGameBonus != 0) {
			extra = " + 1 bonus token";
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = "SCORE:<br/><b>" + currentGamePoints
				+ " points</b><br/><br/>TOKENS:<br/><b>" + earnedPoints
				+ " token" + (earnedPoints == 1 ? "" : "s") + extra
				+ "</b><br/>";

		builder.setMessage(Html.fromHtml(message)).setTitle("Congratulations")
				.setCancelable(false).setIcon(R.drawable.dialog_icon)
				.setNegativeButton("Share", dialogClickListener)
				.setPositiveButton("Leave", dialogClickListener).show();
	}

	class UpdateScoreOnServer extends AsyncTask<String, int[], String> {

		protected String doInBackground(String... urls) {
			if (CheckInternet.isOnline()) {
				RankingWrapper.updateData();
			}
			return "";
		}

		protected void onPostExecute(String feed) {
		}
	}
}
