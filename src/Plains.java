import java.awt.Color;

public class Plains extends Terrain {

	// TODO exhaustable resaurces with a radius of effect
	private int foodSupply;
	private static final int MAX_FOOD_SUPPLY = 50;
	public static final double FOOD_PROB = 0.15;

	public Plains() {
		open = true;
		if (Math.random() < FOOD_PROB) {
			hasFood = true;
			foodSupply = MAX_FOOD_SUPPLY;
		} else {
			hasFood = false;
		}
	}

	public int consume(int wanted) {
		if (this.foodSupply >= wanted) {
			this.foodSupply -= wanted;
			return wanted;
		}else{
			int t = this.foodSupply;
			this.foodSupply = 0;
			return t;
		}
	}

	public void update() {
		if (hasFood && foodSupply < MAX_FOOD_SUPPLY) {
			foodSupply++;
		}
	}

	public Color getColor() {
		if (visitor == null && !hasFood) {
			return Color.PINK.darker();
		} else if (visitor == null && hasFood && foodSupply > 0) {
			return Color.red.darker();
		}else if (visitor == null && hasFood && foodSupply == 0) {
			return Color.red;
		}
		return visitor.getColor();
	}

	public boolean add(Organism o) {
		if (this.visitor == null) {
			this.visitor = o;
			return true;
		}
		return false;
	}

	public boolean hasFood() {
		return hasFood && foodSupply > 0;
	}

}
