package org.mathematica.merchent;

import org.mathematica.constants.BUYABLE_TYPE;

public class BuyableItem {
	public String description;
	public int value;
	public int pointsNeededToBuy;
	public String key;
	public BUYABLE_TYPE type;

	public BuyableItem() {
	}

	public BuyableItem(String d, int v, int p, String k, BUYABLE_TYPE t) {
		this.description = d;
		this.value = v;
		this.pointsNeededToBuy = p;
		this.key = k;
		this.type = t;
	}
}
