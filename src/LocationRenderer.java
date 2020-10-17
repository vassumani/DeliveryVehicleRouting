import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Vector;

import javax.swing.JPanel;

/**
 * Used to render location and route data
 */
@SuppressWarnings("serial")
public class LocationRenderer extends JPanel {
	private final DistanceMatrix distanceMatrix;
	private final UsageMatrix usageMatrix;
	private Vector<Route> routes;

	/**
	 * Location renderer constructor.
	 * @param d The reference distance matrix for this location renderer.
	 * @param u The usage matrix for this location renderer.
	 */
	public LocationRenderer(DistanceMatrix d, UsageMatrix u) {
		distanceMatrix = d;
		usageMatrix = u;
		routes = new Vector<Route>();
	}
	
	/**
	 * Access the list of routes to be rendered.
	 * @param Reference to the actual list used internally.
	 */
	public Vector<Route> getRoutes() {
		return routes;
	}
	

	/**
	 * A private class used to calculate scale and offset of locations for rendering.
	 */
	private class ScaleOffset {
		public final Dimension size;
		public final float scale;
		public final float xOffset;
		public final float yOffset;
		
		/**
		 * Setup the scale and offset needed for rendering locations.
		 * @param panelSize Size of the graphics panel which will be getting rendered too.
		 * @param dm Distance matrix which contains the locations to be rendered.
		 * @throws Exception 
		 */
		public ScaleOffset(Dimension panelSize, DistanceMatrix dm)  {
			final int padding = 15;
			
			// Calculate distance matrix area along access
			AABB aabb = dm.getLocationAABB();
			assert aabb.isValid() != false;
			float xScale = (float)(panelSize.width - (padding * 2)) / (float)(aabb.xMax - aabb.xMin);
			float yScale = (float)(panelSize.height - (padding * 2)) / (float)(aabb.yMax - aabb.yMin);

			// Record values
			scale = Math.min(xScale, yScale);
			xOffset = (panelSize.width / 2) - (scale * (aabb.xMax + aabb.xMin) / 2);
			yOffset = (panelSize.height / 2) - (scale * (aabb.yMax + aabb.yMin) / 2);
			size = panelSize;
		}
		
		/**
		 * Update a coordinate by scaling it and applying an offset.
		 * @param inOut The coordinate to be scaled and offset.
		 * @return A scaled and offset coordinate.
		 */
		public Coordinate Update(Coordinate c) {
			return new Coordinate(
				(long)(xOffset + (scale * c.x)),
				(long)(yOffset + (scale * c.y)));
		}
	}
	
	/**
	 * Draw the location data to the Graphics object.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2D = (Graphics2D)g;
		ScaleOffset scale = new ScaleOffset(getSize(), distanceMatrix);
		drawGrid(g2D, scale);
		drawUsage(g2D, scale);
		float h = 0;
		for (Route r : routes) {
			drawRoute(g2D, scale, r, Color.getHSBColor(h, 1, 0.9f));
			h += 0.2;
		}
		drawLocations(g2D, scale);
	}
	
	/**
	 * Draw a regular grid on the panel.
	 * @param g The target graphics object.
	 * @param size The size of the target graphics object.
	 */
	private void drawGrid(Graphics2D g, ScaleOffset scale) {
		final int targetGridSize = 50;

		// Get number of lines to draw
		int xMax = scale.size.width / targetGridSize;
		int yMax = scale.size.height / targetGridSize;
		
		// Get spacing between lines
		int xStep = scale.size.width / xMax;
		int yStep = scale.size.height / yMax;
		
		// Draw lines
		g.setColor(Color.getHSBColor(0, 0, 0.8f));
		g.setStroke(new BasicStroke(1));
		for (int x=1; x<xMax; x++) {
			g.drawLine(x * xStep, 0, x * xStep, scale.size.height);
		}
		for (int y=1; y<yMax; y++) {
			g.drawLine(0, y * yStep, scale.size.width, y * yStep);
		}
	}

	/**
	 * Draw to the the panel the locations within the distance matrix.
	 * @param g The target graphics object.
	 * @param size The size of the target graphics object.
	 */
	private void drawLocations(Graphics2D g, ScaleOffset scale) {
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2));
		final int z = 2;
		for (int i=0; i<distanceMatrix.size(); i++) {
			Location l = distanceMatrix.getLocation(i);
			Coordinate c = scale.Update(l.coord);
			g.drawLine(
				(int)c.x - z,
				(int)c.y - z,
				(int)c.x + z,
				(int)c.y + z);
			g.drawLine(
				(int)c.x - z,
				(int)c.y + z,
				(int)c.x + z,
				(int)c.y - z);
			g.drawString(
				l.name,
				c.x + z + 2,
				c.y);
		}
	}

	/**
	 * Draw to the the panel the path usage between all locations.
	 * @param g The target graphics object.
	 * @param size The size of the target graphics object.
	 */
	private void drawUsage(Graphics2D g, ScaleOffset scale) {
		g.setStroke(new BasicStroke(1));
		final int size = usageMatrix.size();
		final float maxUsage = usageMatrix.getMaxUsage();
		for (int x=0; x<size; x++) {
			for (int y=x+1; y<size; y++) {
				float usage = usageMatrix.getUsage(x, y);
				if (usage > 0.01) {
					g.setColor(Color.getHSBColor(0.8f, 0.1f + (0.5f * usage / maxUsage), 1f - (0.2f * usage / maxUsage)));
					Coordinate a = scale.Update(distanceMatrix.getLocation(x).coord);
					Coordinate b = scale.Update(distanceMatrix.getLocation(y).coord);
					g.drawLine(
						(int)a.x,
						(int)a.y,
						(int)b.x,
						(int)b.y);
				}
			}
		}
	}
	
	/**
	 * Draw to the the panel the given route.
	 * @param g The target graphics object.
	 * @param size The size of the target graphics object.
	 * @param r Route to be rendered.
	 * @param c The colour to use when drawing the route.
	 */
	private void drawRoute(Graphics2D g, ScaleOffset scale, Route route, Color c) {
		final double arrowLength = 12;
		final double arrowAngle = 0.35;
		g.setColor(c);
		g.setStroke(new BasicStroke(1.8f));
		for (int i=1; i<route.size(); i++) {
			Coordinate a = scale.Update(route.getLocation(i - 1).coord);
			Coordinate b = scale.Update(route.getLocation(i).coord);
			g.drawLine(
				(int)a.x,
				(int)a.y,
				(int)b.x,
				(int)b.y);
			double angle = Math.atan2(b.y - a.y, b.x - a.x);
			g.drawLine(
				(int)b.x,
				(int)b.y,
				(int)b.x - (int)(arrowLength * Math.cos(angle + arrowAngle)),
				(int)b.y - (int)(arrowLength * Math.sin(angle + arrowAngle)));
			g.drawLine(
				(int)b.x,
				(int)b.y,
				(int)b.x - (int)(arrowLength * Math.cos(angle - arrowAngle)),
				(int)b.y - (int)(arrowLength * Math.sin(angle - arrowAngle)));
		}
	}
}
