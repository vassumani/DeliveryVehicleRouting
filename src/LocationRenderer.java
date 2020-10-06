import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * Used to render location and route data
 */
@SuppressWarnings("serial")
public class LocationRenderer extends JPanel {
	private final LocationList location;
	private final Route route;

	/**
	 * Constructor.
	 */
	public LocationRenderer(LocationList l, Route r) {
		location = l;
		route = r;
		
		l.add(0, 0);
		l.add(0, 20);
		l.add(20, 0);
		l.add(-20, -20);
		
		r.add(0);
		r.add(1);
		r.add(2);
		r.add(3);
		r.add(0);
	}
	
	/**
	 * Draw the location data to the Graphics object.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Dimension size = getSize();
		drawGrid((Graphics2D)g, size);
		drawLocations((Graphics2D)g, size);
	}
	
	/**
	 * Draw a regular grid on the panel.
	 */
	private void drawGrid(Graphics2D g, Dimension size) {
		final int targetGridSize = 50;

		// Get number of lines to draw
		int xMax = size.width / targetGridSize;
		int yMax = size.height / targetGridSize;
		
		// Get spacing between lines
		int xStep = size.width / xMax;
		int yStep = size.height / yMax;
		
		// Draw lines
		g.setColor(Color.LIGHT_GRAY);
		g.setStroke(new BasicStroke(1));
		for (int x=1; x<xMax; x++) {
			g.drawLine(x * xStep, 0, x * xStep, size.height);
		}
		for (int y=1; y<yMax; y++) {
			g.drawLine(0, y * yStep, size.width, y * yStep);
		}
	}

	/**
	 * Draw a regular grid on the panel.
	 */
	private void drawLocations(Graphics2D g, Dimension size) {
		final int z = 2;
		final double arrowLength = 10;
		final double arrowAngle = 0.35;

		// Find max distance from (0,0)
		long max = 0;
		for (int i=0; i<location.size(); i++) {
			Location l = location.get(i);
			if (max < Math.abs(l.x)) max = Math.abs(l.x);
			if (max < Math.abs(l.y)) max = Math.abs(l.y);
		}

		// Calculate scale and offsets
		float scale = 0.45f * (float)Math.min(size.width, size.height) / (float)max;
		int xOffset = size.width / 2;
		int yOffset = size.height / 2;
		
		// Draw route
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(2));
		for (int i=1; i<route.size(); i++) {
			Location l0 = route.get(i - 1);
			Location l1 = route.get(i);
			int x0 = (int)((float)l0.x * scale) + xOffset;
			int y0 = (int)((float)l0.y * scale) + yOffset;
			int x1 = (int)((float)l1.x * scale) + xOffset;
			int y1 = (int)((float)l1.y * scale) + yOffset;
			g.drawLine(x0, y0, x1, y1);
			double a = Math.atan2(y1 - y0, x1 - x0);
			g.drawLine(x1, y1, x1 - (int)(arrowLength * Math.cos(a + arrowAngle)), y1 - (int)(arrowLength * Math.sin(a + arrowAngle)));
			g.drawLine(x1, y1, x1 - (int)(arrowLength * Math.cos(a - arrowAngle)), y1 - (int)(arrowLength * Math.sin(a - arrowAngle)));
		}
		
		//System.out.println(Math.cos(0.1 + Math.atan2(20, 0)));
		
		// Draw locations
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2));
		for (int i=0; i<location.size(); i++) {
			Location l = location.get(i);
			int x = (int)((float)l.x * scale) + xOffset;
			int y = (int)((float)l.y * scale) + yOffset;
			g.drawLine(x - z, y - z, x + z, y + z);
			g.drawLine(x - z, y + z, x + z, y - z);
			g.drawString(Integer.toString(i), x + z + 2, y);
		}
	}
}
