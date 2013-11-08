import java.awt.Color;

public class Wall extends Terrain {

	private static Color col = Color.GRAY.darker();

	public Wall(){
		this.hasFood = false;
		this.open = false;
	}

	public Color getColor() {
		return col;
	}
}
