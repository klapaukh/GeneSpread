import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class City {

	private List<Integer> horizontalSpaces;
	private List<Integer> verticalSpaces;
	private List<Building> buildings;
	
	private final int meanRoadWidth = 5;
	private final int meanRoadHeight = 7;
	private final int roadSpaceSd = 3;

	private final int width, height;

	public City(int width, int height) {
		this.height = height;
		this.width = width;

		horizontalSpaces = new ArrayList<>();
		verticalSpaces = new ArrayList<>();

		// create vertical then horizontal roads
		System.out.println("Generating Roads");
		generateRoads(meanRoadWidth, roadSpaceSd, width, verticalSpaces);
		generateRoads(meanRoadHeight, roadSpaceSd, height, horizontalSpaces);
		
		buildings = new ArrayList<>();

		System.out.println("Creating Buildings");
		int count = 0;
		for (int x = 0; x < verticalSpaces.size(); x++) {
			int xspace = verticalSpaces.get(x);
			for (int y = 0; y < horizontalSpaces.size(); y++) {
				int yspace = horizontalSpaces.get(y);
				count += 2*yspace + 2*xspace;
//				generateBuildings(xspace - 1, yspace - 1, x, x + 1, y, y + 1,
//						buildings);
			}
		}
		System.out.println(count);
	}

	public void generateRoads(int meanRoadSpace, int roadSpaceSd, int max,
			List<Integer> spaces) {
		int count = 0;
		while (count < max) {
			int space = (int) Math.abs(Util.random.nextGaussian() * roadSpaceSd
					+ meanRoadSpace);
			if (space == 0) {
				space = 2;
			}
			if (space + count >= max) {
				break;
			}
			spaces.add(space);
			count += space;
		}
	}

	public void generateBuildings(int width, int height, int roadTop,
			int roadBottom, int roadLeft, int roadRight,
			List<Building> buildings) {
		for (int x = 0; x < width; x += width - 1) {
			for (int y = 0; y < height; y++) {
				buildings
						.add(new Building(x == 0 ? roadLeft : roadRight, x, y));
			}
		}
		for (int y = 0; y < height; y += height - 1) {
			for (int x = 0; x < width; x++) {
				buildings
						.add(new Building(y == 0 ? roadTop : roadBottom, x, y));
			}
		}

	}

	public void paint(Graphics g, int width, int height) {
		System.out.println("Drawing " + width + " " + height);
		// fill in background
		g.setColor(Color.red);
		g.fillRect(0, 0, width, height);

		// figure out road thickness
		double xpix = (double) width / this.width;
		double ypix = (double) height / this.height;

		g.setColor(Color.gray);
		double y = 0;
		for (int space : horizontalSpaces) {
			y += space * ypix;
			g.fillRect(0, (int) y, width, (int) ypix);
		}

		double x = 0;
		for (int space : verticalSpaces) {
			x += space * xpix;
			g.fillRect((int) x, 0, (int) xpix, height);
		}
		System.out.println(x + " " + y);

	}

}
