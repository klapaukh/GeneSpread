import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class World extends JComponent implements Runnable {

	private static final long serialVersionUID = -1103859412329702985L;

	private static final int DEFAULT_HEIGHT = 300;
	private static final int DEFAULT_WIDTH = 300;
	private static final double DEFAULT_FREQUENCY = 50.0;

	private final int width, height;
	private final double tickFrequency;
	private final Terrain[][] world;
	private final Random random;

	private int ticks = 0;

	// Updated every 100 ticks.
	private int numGreen = 0;
	private int numBlue = 0;
	private int numPink = 0;
	private int numMale = 0;
	private int numFemale = 0;
	private int numTotal = 0;

	public World(int width, int height, double tickFrequency, long seed, int numWalls, int numFoodSources) {
		this.width = width;
		this.height = height;
		this.tickFrequency = tickFrequency;
		this.random = new Random(seed);
		System.out.println("World created with seed " + seed);

		setDoubleBuffered(true);

		world = new Terrain[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				world[i][j] = new Plains(random.nextDouble() < Plains.FOOD_PROB);
				if (world[i][j].open() && random.nextDouble() < 0.31) {
					((Plains) world[i][j]).add(new Organism(this, world, random.nextLong()));
				}
			}
		}

		/*
		 * Make some food sources
		 */
		if(numFoodSources == -1)
			numFoodSources = random.nextInt(4) + ((width * height) / 25000);
		for (int i = 0; i < numFoodSources; i++) {
			generateFoodBursts(random.nextInt(width), random.nextInt(height), 0);
		}

		/*
		 * Build some walls
		 */
		if(numWalls == -1)
			numWalls = random.nextInt(10) + ((width * height) / 25000);
		for (int i = 0; i < numWalls; i++) {
			placeWalls(random.nextInt(width), random.nextInt(height), 0, random.nextInt(500) + 100, random.nextInt(8));
		}
	}

	protected Terrain getSpace(int x, int y) {
		boolean[] free = new boolean[] { false, false, false, false, false, false, false, false };

		/*
		 * _ _ _ |7|0|1| |6|X|2| |5|4|3|
		 */
		if (y != 0) {
			if (x != 0) {
				free[7] = world[x - 1][y - 1].open();
			}
			free[0] = world[x][y - 1].open();
			if (x < world.length - 1) {
				free[1] = world[x + 1][y - 1].open();
			}
		}
		if (x != 0) {
			free[6] = world[x - 1][y].open();
		}
		if (x < world.length - 1) {
			free[2] = world[x + 1][y].open();
		}

		if (y < world[0].length - 1) {
			if (x != 0) {
				free[5] = world[x - 1][y + 1].open();
			}
			free[4] = world[x][y + 1].open();
			if (x < world.length - 1) {
				free[3] = world[x + 1][y + 1].open();
			}
		}

		int count = 0;
		for (int i = 0; i < free.length; i++) {
			if (free[i]) {
				count++;
			}
		}

		if (count == 0) {
			return null;
		} else {
			int i = random.nextInt(count) + 1;
			count = 0;
			loop: for (int j = 0; j < free.length; j++) {
				if (free[j]) {
					count++;
					if (count == i) {
						count = j;
						break loop;
					}
				}
			}
			switch (count) {
			case 0:
				return world[x][y - 1];
			case 1:
				return world[x + 1][y - 1];
			case 2:
				return world[x + 1][y];
			case 3:
				return world[x + 1][y + 1];
			case 4:
				return world[x][y + 1];
			case 5:
				return world[x - 1][y + 1];
			case 6:
				return world[x - 1][y];
			case 7:
				return world[x - 1][y - 1];
			default:
				return world[x + 1][y - 1];
			}

		}
	}

	private void move() {
		synchronized (world) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					world[i][j].move(i, j);
				}
			}
		}
	}

	public void paint(Graphics g) {
		synchronized (world) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());

			double particleWidth = this.getWidth() / (double)width;
			double particleHeight = (this.getHeight() - 20) / (double)height;

			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					g.setColor(world[i][j].getColor());
					g.fillRect((int)Math.ceil(i * particleWidth), (int)Math.ceil(j * particleHeight), (int)Math.ceil(particleWidth), (int)Math.ceil(particleHeight));
				}
			}
			g.setColor(Color.WHITE);
			g.drawString(String.format("Ticks: %d | Green: %.1f%% Blue: %.1f%% Pink: %.1f%% | Male: %.1f%% Female: %.1f%% | Total: %d", ticks,
				((double)numGreen/numTotal)*100, ((double)numBlue/numTotal)*100, ((double)numPink/numTotal)*100,
				((double)numMale/numTotal)*100, ((double)numFemale/numTotal)*100, numTotal), 5, this.getHeight() - 5);
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(width, height + 20);
	}

	/**
	 * Create a wall
	 * @param x X position to make into a wall
	 * @param y Y position to make into a wal
	 * @param depth How long the wall is
	 * @param maxDepth How long to make the wall
	 * @param lastDir The direction I went to get here
	 * @return Whether or not I actually made a wall
	 */
	private boolean placeWalls(int x, int y, int depth, int maxDepth, int lastDir) {
		if (x < width && y < height && x >= 0 && y >= 0) { //Make sure you are on the map
			if (depth < maxDepth) { //Make sure the wall isn't supposed to finish here
				world[x][y] = new Wall(); //This is a wall now

				//The direction the wall continues in is a gaussian distrobution
				//with sd as narrow.
				double narrow = 0.5;
				int dir = (int)Math.round(random.nextGaussian() * narrow + lastDir) % 8;
				if(dir < 0)
					dir = 8 + dir;
				//Create a new wall in the correct direction
				switch (dir) {
				case 0:
					placeWalls(x - 1, y - 1, depth + 1,maxDepth,dir);
					break;
				case 1:
					placeWalls(x - 1, y, depth + 1,maxDepth,dir);
					break;
				case 2:
					placeWalls(x - 1, y + 1, depth + 1,maxDepth,dir);
					break;
				case 3:
					placeWalls(x, y - 1, depth + 1,maxDepth,dir);
					break;
				case 4:
					placeWalls(x, y + 1, depth + 1,maxDepth,dir);
					break;
				case 5:
					placeWalls(x + 1, y - 1, depth + 1,maxDepth,dir);
					break;
				case 6:
					placeWalls(x + 1, y, depth + 1,maxDepth,dir);
					break;
				case 7:
					placeWalls(x + 1, y + 1, depth + 1,maxDepth,dir);
					break;
				default:
					throw new RuntimeException("Illegal direction: " + dir);
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void generateFoodBursts(int x, int y, int depth) {
		if (x < width && y < height && x >= 0 && y >= 0 && !world[x][y].hasFood) {
			if (random.nextDouble() < (1 / Math.max(1.0, depth / 20.0))) {
				world[x][y].hasFood = true;
				generateFoodBursts(x - 1, y - 1, depth + 1);
				generateFoodBursts(x - 1, y, depth + 1);
				generateFoodBursts(x - 1, y + 1, depth + 1);
				generateFoodBursts(x, y - 1, depth + 1);
				generateFoodBursts(x, y + 1, depth + 1);
				generateFoodBursts(x + 1, y - 1, depth + 1);
				generateFoodBursts(x + 1, y, depth + 1);
				generateFoodBursts(x + 1, y + 1, depth + 1);
			}
		} else if (depth == 0) {
			generateFoodBursts(x - 1, y - 1, depth + 1);
			generateFoodBursts(x - 1, y, depth + 1);
			generateFoodBursts(x - 1, y + 1, depth + 1);
			generateFoodBursts(x, y - 1, depth + 1);
			generateFoodBursts(x, y + 1, depth + 1);
			generateFoodBursts(x + 1, y - 1, depth + 1);
			generateFoodBursts(x + 1, y, depth + 1);
			generateFoodBursts(x + 1, y + 1, depth + 1);
		}
	}

	private void updateStatistics() {
		numPink = numBlue = numGreen = numMale = numFemale = numTotal = 0;
		synchronized(world) {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					Organism o = world[i][j].visitor;
					if(o == null)
						continue;
					if(o.getBaseColor().equals(Color.MAGENTA.brighter())) {
						numPink++;
						numFemale++;
					}
					else if(o.getBaseColor().equals(Color.MAGENTA.darker())) {
						numPink++;
						numMale++;
					}
					else if(o.getBaseColor().equals(Color.BLUE.brighter())) {
						numBlue++;
						numFemale++;
					}
					else if(o.getBaseColor().equals(Color.BLUE.darker())) {
						numBlue++;
						numMale++;
					}
					else if(o.getBaseColor().equals(Color.GREEN.brighter())) {
						numGreen++;
						numFemale++;
					}
					else if(o.getBaseColor().equals(Color.GREEN.darker())) {
						numGreen++;
						numMale++;
					}
					numTotal++;
				}
			}
		}
	}

	public void run() {
		long tickPeriod = (long)((1/tickFrequency)*1000);
		while (true) {
			long start = System.currentTimeMillis();
			move();
			if (ticks % 100 == 0) {
				System.out.print("\rGen " + ticks);
				updateStatistics();
			}
			ticks++;
			repaint();
			long end = System.currentTimeMillis();
			if((end - start) < tickPeriod) {
				try {
					Thread.sleep((tickPeriod) - (end - start));
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public static void main(String args[]) {
		// Default values.
		int width = DEFAULT_WIDTH;
		int height = DEFAULT_HEIGHT;
		double tickFrequency = DEFAULT_FREQUENCY;
		long seed = System.currentTimeMillis();
		int numWalls = -1;
		int numFoodSources = -1;

		// Handle arguments.
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("-w")) {
				if(++i < args.length) {
					try {
						width = Integer.parseInt(args[i]);
					} catch(NumberFormatException e) {
						System.err.println("Invalid width: " + args[i]);
					}
				} else {
					System.err.println("-w requires an argument.");
				}
			}
			else if(args[i].equals("-h")) {
				if(++i < args.length) {
					try {
						height = Integer.parseInt(args[i]);
					} catch(NumberFormatException e) {
						System.err.println("Invalid height: " + args[i]);
					}
				} else {
					System.err.println("-h requires an argument.");
				}
			}
			else if(args[i].equals("-t")) {
				if(++i < args.length) {
					try {
						tickFrequency = Double.parseDouble(args[i]);
					} catch(NumberFormatException e) {
						System.err.println("Invalid tick frequency: " + args[i]);
					}
				} else {
					System.err.println("-t requires an argument.");
				}
			}
			else if(args[i].equals("-s")) {
				if(++i < args.length) {
					seed = args[i].hashCode();
				} else {
					System.err.println("-s requires an argument.");
				}
			}
			else if(args[i].equals("-n")) {
				if(++i < args.length) {
					try {
						numWalls = Integer.parseInt(args[i]);
					} catch(NumberFormatException e) {
						System.err.println("Invalid number of walls: " + args[i]);
					}
				} else {
					System.err.println("-n requires an argument.");
				}
			}
			else if(args[i].equals("-f")) {
				if(++i < args.length) {
					try {
						numFoodSources = Integer.parseInt(args[i]);
					} catch(NumberFormatException e) {
						System.err.println("Invalid number of food sources: " + args[i]);
					}
				} else {
					System.err.println("-f requires an argument.");
				}
			}
			else {
				System.err.println("Unknown argument: " + args[i]);
			}
		}

		// Create GUI.
		JFrame frame = new JFrame("Evolution Simulation");
		World world = new World(width, height, tickFrequency, seed, numWalls, numFoodSources);
		frame.getContentPane().add(world);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

		// Start simulation.
		new Thread(world).start();
	}
}
