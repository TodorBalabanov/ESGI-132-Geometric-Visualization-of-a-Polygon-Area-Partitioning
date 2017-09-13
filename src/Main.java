import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Application single entry point class.
 * 
 * @author Todor Balabanov
 */
public class Main {

	/**
	 * Pipe description.
	 * 
	 * @author Todor Balabanov
	 */
	private static class Pipe {
		/**
		 * Area visualization color.
		 */
		public Color color = Color.WHITE;

		/**
		 * Location of the pipe is in the middle of the polygon side.
		 */
		public Point location = new Point(0, 0);

		/**
		 * First vertex of the polygon side.
		 */
		public Point vertex1 = new Point(0, 0);

		/**
		 * Second vertex of the polygon side.
		 */
		public Point vertex2 = new Point(0, 0);

		/**
		 * Pipe share in percentage of the total amount.
		 */
		public double share = 0;

		/**
		 * Maximum allowed area in pixels.
		 */
		public int area = 0;

		/**
		 * Current occupied area in pixels.
		 */
		public int occupied = 0;

		/**
		 * List of color flood candidate pixels.
		 */
		public List<Point> candidates = new ArrayList<Point>();

		/**
		 * Constructor with all parameters.
		 * 
		 * @param color
		 *            Color which will be used for visualization of the area associated
		 *            with the pipe.
		 * @param vertex1
		 *            Point for the first side.
		 * @param vertex2
		 *            Point for the second side.
		 * @param totalArea
		 *            Total polygon area in pixels is used to calculate pipe share in
		 *            pixels.
		 * @param share
		 *            Pipe share in percents.
		 */
		Pipe(final Color color, final Point vertex1, final Point vertex2, int totalArea, double share) {
			this.color = color;
			this.vertex1 = vertex1;
			this.vertex2 = vertex2;
			this.share = share;

			location = new Point((int) Math.round((vertex1.x + vertex2.x) / 2D),
					(int) Math.round((vertex1.y + vertex2.y) / 2D));
			area = (int) Math.round(share * totalArea / 100D);
			occupied = 0;

			/*
			 * Start flooding from initial location.
			 */
			candidates.add(location);
		}
	}

	/**
	 * First flood algorithm.
	 */
	private static void flood1() {

		/*
		 * Draw positions of the pipes.
		 */
		for (Pipe pipe : pipes) {
			g.setColor(pipe.color);
			g.drawLine(pipe.location.x, pipe.location.y, pipe.location.x, pipe.location.y);
		}

		/*
		 * Flood image.
		 */
		for (int a = /* area - shares.size() */2047; a >= 0; a--) {
			for (Pipe pipe : pipes) {
				/*
				 * Find random proper pixel to flood.
				 */
				while (true) {
					int i = PRNG.nextInt(output.getWidth());
					int j = PRNG.nextInt(output.getHeight());

					/*
					 * Only black pixels can be converted to color pixels.
					 */
					if (output.getRGB(i, j) != Color.BLACK.getRGB()) {
						continue;
					}

					if (i > 0 && output.getRGB(i - 1, j) == pipe.color.getRGB()) {
						g.setColor(pipe.color);
						g.drawLine(i, j, i, j);
						break;
					}
					if (j > 0 && output.getRGB(i, j - 1) == pipe.color.getRGB()) {
						g.setColor(pipe.color);
						g.drawLine(i, j, i, j);
						break;
					}
					if (i < output.getWidth() - 1 && output.getRGB(i + 1, j) == pipe.color.getRGB()) {
						g.setColor(pipe.color);
						g.drawLine(i, j, i, j);
						break;
					}
					if (j < output.getHeight() - 1 && output.getRGB(i, j + 1) == pipe.color.getRGB()) {
						g.setColor(pipe.color);
						g.drawLine(i, j, i, j);
						break;
					}
				}
			}
		}
	}

	/**
	 * Pseudo-random number generator.
	 */
	private final static Random PRNG = new Random();

	/**
	 * List of pipes.
	 */
	private static List<Pipe> pipes = new ArrayList<Pipe>();

	/**
	 * Output image for results reporting.
	 */
	private static BufferedImage output = null;

	/**
	 * Graphic context.
	 */
	private static Graphics g = null;

	/**
	 * Application single entry point method.
	 * 
	 * @param args
	 *            Command line arguments.
	 * @throws IOException
	 *             When there is a problem with the input-output system.
	 * @throws ParseException
	 *             If there is a problem with JSON parsing.
	 */
	public static void main(String[] args) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(new FileReader("./dat/in02.json"));

		/*
		 * Read polygon vertices.
		 */
		List<Integer> x = new ArrayList<Integer>();
		List<Integer> y = new ArrayList<Integer>();
		for (Object value : (JSONArray) json.get("vertices")) {
			x.add(Integer.valueOf("" + ((JSONObject) value).get("x")));
			y.add(Integer.valueOf("" + ((JSONObject) value).get("y")));
		}

		/*
		 * Read pipes shares.
		 */
		List<Double> shares = new ArrayList<Double>();
		for (Object value : (JSONArray) json.get("shares")) {
			shares.add(Double.valueOf("" + value));
		}

		/*
		 * Pipes colors.
		 */
		List<Color> colors = new ArrayList<Color>();
		loop: do {
			/*
			 * Clear from previous loops.
			 */
			colors.clear();

			/*
			 * Select random colors.
			 */
			for (int c = 0; c < shares.size(); c++) {
				colors.add(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
			}

			/*
			 * All colors should be different.
			 */
			for (int a = 0; a < colors.size(); a++) {
				for (int b = a + 1; b < colors.size(); b++) {
					if (colors.get(a).getRGB() == colors.get(b).getRGB()) {
						continue loop;
					}
				}
			}
		} while (colors.contains(Color.BLACK) || colors.contains(Color.WHITE));

		/*
		 * Save result.
		 */
		output = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
		g = output.getGraphics();

		/*
		 * White background.
		 */
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, output.getWidth(), output.getHeight());

		/*
		 * Generate polygon.
		 */
		Polygon polygon = new Polygon(x.stream().mapToInt(Integer::intValue).toArray(),
				y.stream().mapToInt(Integer::intValue).toArray(), shares.size());

		/*
		 * Black initial area.
		 */
		g.setColor(Color.BLACK);
		g.drawPolygon(polygon);
		g.fillPolygon(polygon);

		/*
		 * Calculate area in number of pixels.
		 */
		int area = 0;
		for (int j = 0; j < output.getHeight(); j++) {
			for (int i = 0; i < output.getWidth(); i++) {
				if (output.getRGB(i, j) == Color.BLACK.getRGB()) {
					area++;
				}
			}
		}

		/*
		 * Fill pipes structure.
		 */
		for (int k = 0, l; k < shares.size(); k++) {
			l = (k + 1) % shares.size();
			pipes.add(new Pipe(colors.get(k), new Point(x.get(k), y.get(k)), new Point(x.get(l), y.get(l)), area,
					shares.get(k)));
		}

		flood1();

		/*
		 * Store current image in an image file.
		 */
		ImageIO.write((RenderedImage) output, "png", new File("./bin/out.png"));
	}

}
