package org.mathematica.merchent;

import java.util.ArrayList;

import org.mathematica.constants.BUYABLE_TYPE;

public class BuyableItems {
	public static ArrayList<BuyableItem> items;

	static {
		items = new ArrayList<BuyableItem>();
		items.clear();
		/* Tutorial purchasable items */
		items.add(new BuyableItem(
				"Learn a new way to do math equations quickly", 0, 10, "_TIP1",
				BUYABLE_TYPE.TIPS_AND_TRICKS));
		items.add(new BuyableItem(
				"Learn a new way to do math equations quickly", 0, 25, "_TIP2",
				BUYABLE_TYPE.TIPS_AND_TRICKS));
		items.add(new BuyableItem(
				"Learn a new way to do math equations quickly", 0, 50, "_TIP3",
				BUYABLE_TYPE.TIPS_AND_TRICKS));
		items.add(new BuyableItem(
				"Learn a new way to do math equations quickly", 0, 75, "_TIP4",
				BUYABLE_TYPE.TIPS_AND_TRICKS));
		items.add(new BuyableItem(
				"Learn a new way to do math equations quickly", 0, 100,
				"_TIP5", BUYABLE_TYPE.TIPS_AND_TRICKS));

		/* Extend game time purchasable items */
		items.add(new BuyableItem("Extend total game time by 1 second", 1, 10,
				"_1SEC", BUYABLE_TYPE.EXTRA_TIME));
		items.add(new BuyableItem("Extend total game time by 3 seconds", 3, 25,
				"_3SEC", BUYABLE_TYPE.EXTRA_TIME));
		items.add(new BuyableItem("Extend total game time by 5 seconds", 5, 50,
				"_5SEC", BUYABLE_TYPE.EXTRA_TIME));
		items.add(new BuyableItem("Extend total game time by 10 seconds", 10,
				100, "_10SEC", BUYABLE_TYPE.EXTRA_TIME));
		items.add(new BuyableItem("Extend total game time by 30 seconds", 30,
				250, "_30SEC", BUYABLE_TYPE.EXTRA_TIME));
		items.add(new BuyableItem("Extend total game time by 60 seconds", 60,
				500, "_60SEC", BUYABLE_TYPE.EXTRA_TIME));

		/* Hint purchasable items */
		items.add(new BuyableItem(
				"Have one tile in game already filled out! Only for 6x6 or bigger boards.",
				1, 50, "_HINT1", BUYABLE_TYPE.EXTRA_TILE));
		items.add(new BuyableItem(
				"Have two tiles in game already filled out! Only for 6x6 or bigger boards.",
				2, 100, "_HINT2", BUYABLE_TYPE.EXTRA_TILE));
		items.add(new BuyableItem(
				"Have three tiles in game already filled out! Only for 6x6 or bigger boards.",
				3, 250, "_HINT3", BUYABLE_TYPE.EXTRA_TILE));

		/* Extra live purchasable items */
		items.add(new BuyableItem(
				"One extra mistake allowed during a timed game", 1, 100,
				"_MISTAKE1", BUYABLE_TYPE.EXTRA_ALLOWED_MISTAKE));
		items.add(new BuyableItem(
				"One extra mistakes allowed during a timed game", 1, 300,
				"_MISTAKE2", BUYABLE_TYPE.EXTRA_ALLOWED_MISTAKE));
		items.add(new BuyableItem(
				"One extra mistakes allowed during a timed game", 1, 500,
				"_MISTAKE3", BUYABLE_TYPE.EXTRA_ALLOWED_MISTAKE));
	}
}
