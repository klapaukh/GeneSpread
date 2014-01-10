import java.awt.Color;
import java.util.Random;

public class Organism {

	private static final int NUM_GENDER = 2, MAX_INACTIVITY = 2, DISEASE_RECURANCE_TIME = -50, CANNIBAL_TIME_MAX = 10;
	private static final double DISEASE_CREATION_CHANCE = 0.0001;

	private final World parent;
	private final Terrain[][] world;
	private final long seed;
	private final Random random;

	private final boolean immune;
	private final double diseaseVirulence;
	private final int diseaseDeathTime;
	private final int maxHunger;
	private final int fertility;
	private final int strength;
	private final int life;
	private final DNA dna;
	private final Color color;

	private boolean fertile;
	private int lastChild;
	private int age;
	private int lastMove;
	private int lastMeal;
	private int timeSick;
	private int cannibalTime;

	public Organism(World parent, Terrain[][] world, long seed) {
		this.parent = parent;
		this.world = world;
		this.seed = seed;
		this.random = new Random(seed);
		this.age = 0;
		this.lastChild = 0;
		this.fertile = false;
		this.lastMove = 0;
		this.lastMeal = 0;
		this.cannibalTime = -1;
		this.timeSick = DISEASE_RECURANCE_TIME - 1;
		this.dna = new DNA(random.nextLong());
		this.fertility = dna.getFertility();
		this.life = dna.getLifespan();
		this.color = dna.getEyeColor();
		this.immune = dna.isImmune();
		this.diseaseDeathTime = dna.diseaseDeathTime();
		this.maxHunger = dna.maxHunger();
		this.diseaseVirulence = dna.diseaseVirulence();
		this.strength = dna.getStrength();
	}

	public Organism(World parent, Terrain[][] world, Organism p1, Organism p2) {
		this.parent = parent;
		this.world = world;
		this.seed = p1.seed ^ p2.seed;
		this.random = new Random(seed);
		this.age = 0;
		this.lastChild = 0;
		this.fertile = false;
		this.lastMove = 0;
		this.lastMeal = 0;
		this.cannibalTime = -1;
		this.timeSick = DISEASE_RECURANCE_TIME - 1;
		this.dna = new DNA(p1.dna, p2.dna);
		this.fertility = dna.getFertility();
		this.life = dna.getLifespan();
		this.color = dna.getEyeColor();
		this.immune = dna.isImmune();
		this.diseaseDeathTime = dna.diseaseDeathTime();
		this.maxHunger = dna.maxHunger();
		this.diseaseVirulence = dna.diseaseVirulence();
		this.strength = dna.getStrength();
	}

	public boolean move(int x, int y) {
		// If you're over 100 year old, aren't moving, or are just unlucky you might just die!
		if ((this.age > life && random.nextDouble() > 0.1) || (lastMove > MAX_INACTIVITY && random.nextDouble() > 0.9)
				|| (lastMeal > maxHunger) || (this.timeSick > diseaseDeathTime && !immune)) {
			// System.out.println(age);
			return false;
		}
		if (this.cannibalTime >= 0) {
			this.cannibalTime++;
		}
		if (this.cannibalTime > CANNIBAL_TIME_MAX) {
			this.cannibalTime = -1;
		}
		if (this.timeSick >= 0 || (this.timeSick < DISEASE_RECURANCE_TIME && random.nextDouble() < DISEASE_CREATION_CHANCE)) {
			this.timeSick = Math.max(0, timeSick + 1);
		} else {
			this.timeSick--;
		}
		if (this.immune && this.timeSick > diseaseDeathTime) {
			timeSick = -1;
		}
		this.age++;
		this.lastChild++;
		this.lastMeal++;
		if (this.age == 20) {
			this.fertile = true;
		}
		if (!fertile && this.lastChild > fertility) {
			this.fertile = true;
		}

		/*
		 * _ _ _ |7|0|1| |6|X|2| |5|4|3|
		 */
		int dir = random.nextInt(8);
		Terrain t = null;
		switch (dir) {
		case 0:
			// Move up if you can
			if (y != 0) {
				t = world[x][y - 1];
			}
			break;
		case 1:
			if (y != 0 && x < world.length - 1) {
				t = world[x + 1][y - 1];
			}
			break;
		case 2:
			if (x < world.length - 1) {
				t = world[x + 1][y];
			}
			break;
		case 3:
			if (y < world[0].length - 1 && x < world.length - 1) {
				t = world[x + 1][y + 1];
			}
			break;
		case 4:
			if (y < world[0].length - 1) {
				t = world[x][y + 1];
			}
			break;
		case 5:
			if (x > 0 && y < world[0].length - 1) {
				t = world[x - 1][y + 1];
			}
			break;
		case 6:
			if (x > 0) {
				t = world[x - 1][y];
			}
			break;
		case 7:
			if (y != 0 && x > 0) {
				t = world[x - 1][y - 1];
			}
			break;
		default:
			if (y != 0 && x < world.length - 1) {
				t = world[x + 1][y - 1];
			}
			break;
		}

		if (t == null) {
			lastMove++;
			return true;
		}

		if (t.open()) {
			t.add(this);
			lastMove = 0;
			return false;
		} else if(t.visitor != null) {
			infect(t.visitor, this);
			if (offer(t.visitor, this)) {
				Terrain t2 = parent.getSpace(x, y);
				lastMove++;
				if (t2 == null) {
					// No space to have a child
					return true;
				}
				t2.add(new Organism(parent, world, this, t.visitor));
				infect(this, t2.visitor);
				this.lastChild = 0;
				this.fertile = false;
				t.visitor.lastChild = 0;
				t.visitor.fertile = false;
			} else if (attack(t.visitor, this)) {
				boolean win = fight(this, t.visitor);
				if (win) {
					t.visitor = null;
					this.lastMeal = 0;
					this.cannibalTime = 0;
				} else {
					t.visitor.cannibalTime = 0;
					t.visitor.lastMeal = 0;
					return false;
				}
			} else {
//				System.out.println("PEACE");
			}
		}

		return true;
	}

	/**
	 * @returns True If o1 kills o2 False If o2 kills o1
	 */
	private static boolean fight(Organism o1, Organism o2) {
		int total = o1.strength + o2.strength;
		return o1.random.nextInt(total) < o1.strength;
	}

	private static boolean attack(Organism o1, Organism o2) {
		boolean f1 = o1.random.nextDouble() < o1.dna.getAnger() * o1.hungerProportion();
		boolean f2 = o2.random.nextDouble() < o2.dna.getAnger() * o2.hungerProportion();

//		if(f1 && f2) {
//			System.out.println(o1.lastMeal + " " + o2.lastMeal);
//		}
		return f1 || f2;
	}

	private static void infect(Organism o1, Organism o2) {
		if ((o1.timeSick < 0 && o2.timeSick < 0) || (o1.timeSick > -1 && o2.timeSick > -1)) {
			return;
		}
		if (o1.timeSick > -1 && o2.timeSick < DISEASE_RECURANCE_TIME) {
			o2.timeSick = o1.random.nextDouble() < o1.diseaseVirulence ? 0 : -1;
		} else if (o1.timeSick < DISEASE_RECURANCE_TIME) {
			o1.timeSick = o2.random.nextDouble() < o2.diseaseVirulence ? 0 : -1;
		}
	}

	public Color getColor() {
		if (timeSick > -1) {
			if (!immune) {
				return Color.white;
			} else {
				return Color.yellow;
			}
		} else if (cannibalTime >= 0) {
			return Color.black;
		}
		return color;
	}

	private static boolean offer(Organism o1, Organism o2) {
		if (NUM_GENDER > 1 && o1.dna.getGender() == o2.dna.getGender()) {
			return false;
		}
		if (!(o2.fertile && o1.fertile)) {
			return false;
		}
		int eyerating1 = o1.dna.eyeRating();
		int eyerating2 = o2.dna.eyeRating();
		double canrating1 =o1.dna.getAnger();
		double canrating2 = o2.dna.getAnger();

		// Xenophobic - reverse to make antixenophobic
//		switch (Math.abs(eyerating1 - eyerating2)) {
//		case 0:
//			return o1.random.nextDouble() < 0.9;// - Math.abs(canrating1 + canrating2)*2;
//		case 1:
//			return o1.random.nextDouble() < 0.6;// - Math.abs(canrating1 + canrating2)*2;
//		case 2:
//			return o1.random.nextDouble() < 0.3;// - Math.abs(canrating1 + canrating2)*2;
//		default:
//			return o1.random.nextDouble() < 0.1;// - Math.abs(canrating1 + canrating2)*2;
//		}
		return o1.random.nextDouble() < 0.5;
	}

	public boolean hungry() {
		return lastMeal > 3;
	}

	public int hungerLevel() {
		return lastMeal;
	}

	public double hungerProportion() {
		if (lastMeal > 5) {
			return (double) lastMeal / maxHunger*10.0;
		}
		return -1;
	}

	public void feed(int fed) {
		lastMeal -= fed;
	}

	public Color getBaseColor() {
		return color;
	}
}
