package tk.mightyelemental.evolution.particles;

import org.newdawn.slick.Color;

public enum FoodType {

	TYPE_1(4, 5, Color.red),
	TYPE_2(5, 10, Color.yellow),
	TYPE_3(3, 3, Color.blue),
	TYPE_4(3, 0, Color.pink),
	GOD_FOOD(99, 0, Color.green);

	int		energyDensity;
	int		toxicity;
	Color	col;

	private FoodType(int ed, int tox, Color c) {
		this.energyDensity = ed;
		this.toxicity = tox;
		this.col = c;
	}

	public int getEnergyDensity() {
		return energyDensity;
	}

	public int getToxicity() {
		return toxicity;
	}

	public Color getColor() {
		return col;
	}

}
