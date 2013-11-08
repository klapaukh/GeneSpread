import java.awt.Color;

public abstract class Terrain {

	protected boolean open;
	protected Organism visitor;
	protected boolean hasFood;

	public boolean open() {
		return open && visitor == null;
	}

	public abstract Color getColor();

	public void move(int x, int y) {
		update();

		if(visitor == null) {
			return;
		} else {
			if(hasFood && visitor.hungry()) {
				visitor.feed(this.consume(visitor.hungerLevel()));
			}
			if(!visitor.move(x,y)) {
				this.visitor = null;
			}
			return;
		}
	}

	public void update() {}

	public boolean add(Organism o) {
		return false;
	}

	public int consume(int wanted) { return 0; }
}
