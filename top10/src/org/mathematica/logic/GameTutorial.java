package org.mathematica.logic;

import android.text.Html;
import android.text.Spanned;

public class GameTutorial {

	public Spanned getTutorial() {
		String tutorial = "";

		tutorial += addChapterHeader("Main Menu");
		tutorial += addChapterTitle("Game modes");
		tutorial += addNormalText("You have two game modes: Timed/Normal. Timed is against the clock and you earn experience that translates to points. Normal is untimed but not experience is earned so no points are earned.");
		tutorial += addChapterTitle("Configure your game");
		tutorial += addNormalText("You can change the level number of rows, columns, max digits an answer can contain and the difficulty.");
		tutorial += addChapterTitle("Expand user profile");
		tutorial += addNormalText("On the bottom of the main menu you will see a picture of your Google+ profile and an up pointed arrow that you can click and expand your profile.");

		tutorial += addChapterHeader("User profile");
		tutorial += addChapterTitle("Upper section");
		tutorial += addNormalText("This sections contains an enlarged profile picture, the total number of points you own and a button to go back to the main menu.");
		tutorial += addChapterTitle("Settings");
		tutorial += addNormalText("You can enable/disable sound, open this tutorial, rate the game on Google Play Store or reset all progress (points, bought items)");
		tutorial += addChapterTitle("Items to buy");
		tutorial += addNormalText("Using your points you can buy any of the items in the list. This will help expand your game time and give you more time to think.");

		tutorial += addChapterHeader("Inside the level");
		tutorial += addChapterTitle("Single click on tile");
		tutorial += addNormalText("With a single click on a tile you will mark that tile, also preview the horizontal/vertical answer length and view the equations you need to solve in order to fill in this tiles.");
		tutorial += addChapterTitle("Long click on tile");
		tutorial += addNormalText("This will open the dialpad that wil let you choose the number you want the tile to contain.");
		tutorial += addChapterTitle("Other informations");
		tutorial += addNormalText("You can also see the remaining time, total experience earned, as well as the notes board button and back to main menu button.");

		tutorial += addChapterHeader("A note from the developer");
		tutorial += addNormalText("Glad you like this game. Please rate it and comment with suggestions, improvements or bugs found inside the application.");

		return Html.fromHtml(tutorial);
	}

	private String addChapterHeader(String header) {
		return "<b><font size=\"7\" color=\"#990000\">" + header
				+ "</font></b><br/><br/>";
	}

	private String addChapterTitle(String title) {
		return "<b>" + title + "</b>";
	}

	private String addNormalText(String text) {
		return "<p>" + text + "</p>";
	}

}
