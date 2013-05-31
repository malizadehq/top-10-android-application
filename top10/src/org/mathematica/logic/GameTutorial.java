package org.mathematica.logic;

import android.text.Html;
import android.text.Spanned;

public class GameTutorial {

	public Spanned getTutorial() {
		String tutorial = "";

		tutorial += addChapterHeader("Main Menu");
		tutorial += addChapterTitle("Game modes");
		tutorial += addNormalText("You have two game modes available:<br/><br/>- Timed is when you race against the clock to finish the board but get points in return that translate to tokens.<br/>- Normal is when take your time and focus on learning but receive no points.");
		tutorial += addChapterTitle("Board sizes");
		tutorial += addNormalText("You can choose from three sizes:<br/><br/>- Small is a 5x5 board<br/>- Medium is a 6x6 board<br/>- Large is a 7x7 board");

		tutorial += addChapterHeader("Game Rules");
		tutorial += addChapterTitle("Fill a tile");
		tutorial += addNormalText("Long press a tile to show the number chooser.");
		tutorial += addChapterTitle("Race against the time");
		tutorial += addNormalText("Every timed level has a certain duration taking in consideration the board size and difficulty. Be sure to finish the board before the time runs out.");
		tutorial += addChapterTitle("Make no mistakes");
		tutorial += addNormalText("You start with three lives. Every time you fill a tile with a wrong number you loose a life. Loose them all and you loose the game.");

		tutorial += addChapterHeader("Points To Tokens Conversion");
		tutorial += addNormalText("For every 50 points your make into a game you will receive one token.");

		tutorial += addChapterHeader("Profile Page");
		tutorial += addChapterTitle("Virtual items");
		tutorial += addNormalText("You can use your earned tokens to unlock virtual items:<br/><br/>- Extra time<br/>- Extra mistakes<br/>- Unlocked tiles<br/>- Tips&Tricks");
		tutorial += addChapterTitle("Settings");
		tutorial += addNormalText("You can mute/unmute the sound, rate the game, share with friends or acces this manual.");

		return Html.fromHtml(tutorial);
	}

	private String addChapterHeader(String header) {
		return "<b><font size=\"7\" color=\"#ff4444\">" + header
				+ "</font></b><br/><br/>";
	}

	private String addChapterTitle(String title) {
		return "<b><font size=\"7\" color=\"#33b5e5\">" + title + "</font></b>";
	}

	private String addNormalText(String text) {
		return "<p>" + text + "</p>";
	}

}
