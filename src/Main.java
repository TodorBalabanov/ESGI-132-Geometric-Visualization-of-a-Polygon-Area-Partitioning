import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

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
	 * Add more candidates on the boundaries.
	 * 
	 * @param pipe
	 *            Pipe to be checked.
	 */
	private static void boundaries(Pipe pipe) {
		for (int j = 1; j < output.getHeight() - 1; j++) {
			for (int i = 1; i < output.getWidth() - 1; i++) {
				/*
				 * It should be part of this pipe area.
				 */
				if (output.getRGB(i, j) != pipe.color.getRGB()) {
					continue;
				}

				/*
				 * Check for empty neighbors.
				 */
				if (output.getRGB(i - 1, j) == Color.BLACK.getRGB()) {
					pipe.candidates.add(new Point(i - 1, j));
				}
				if (output.getRGB(i, j - 1) == Color.BLACK.getRGB()) {
					pipe.candidates.add(new Point(i, j - 1));
				}
				if (output.getRGB(i + 1, j) == Color.BLACK.getRGB()) {
					pipe.candidates.add(new Point(i + 1, j));
				}
				if (output.getRGB(i, j + 1) == Color.BLACK.getRGB()) {
					pipe.candidates.add(new Point(i, j + 1));
				}
			}
		}
	}

	/**
	 * Distance between line AB and point P.
	 * 
	 * @param a
	 *            First end of the line.
	 * @param b
	 *            Second end of the line.
	 * @param p
	 *            Point in 2D space.
	 * 
	 * @return Distance between the point and the line.
	 */
	private static double distance(Point a, Point b, Point p) {
		double normal = Math.sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y));
		return Math.abs((p.x - a.x) * (b.y - a.y) - (p.y - a.y) * (b.x - a.x)) / normal;
	}

	/**
	 * Do Monte-Carlo flooding step.
	 * 
	 * @param refine
	 *            Refine number of candidates.
	 * 
	 * @return True if a step was done, false otherwise.
	 */
	private static boolean step(int refine) {
		boolean result = false;

		/*
		 * Draw position of pipes areas.
		 */
		for (Pipe pipe : pipes) {
			/*
			 * Do nothing if the share of this pipe is complete.
			 */
			if (pipe.occupied >= pipe.area) {
				continue;
			}

			/*
			 * If there is no candidates check the boundaries.
			 */
			if (pipe.candidates.size() <= 0) {
				boundaries(pipe);

				/*
				 * If there is no candidate pixels do nothing.
				 */
				if (pipe.candidates.size() <= 0) {
					continue;
				}
			}

			/*
			 * Select random pixel to flood.
			 */
			Point next = pipe.candidates.get(PRNG.nextInt(pipe.candidates.size()));
			for (int r = refine; r > 0; r--) {
				Point alternative = pipe.candidates.get(PRNG.nextInt(pipe.candidates.size()));

				/*
				 * Distance to the line.
				 */
				double distance1 = distance(pipe.vertex1, pipe.vertex2, alternative);
				double distance2 = distance(pipe.vertex1, pipe.vertex2, next);

				if (distance1 < distance2) {
					next = alternative;
				} else if (distance1 == distance2
						&& alternative.distance(pipe.location) < next.distance(pipe.location)) {
					next = alternative;
				}
			}
			pipe.candidates.remove(next);

			/*
			 * Change color only for unused pixels.
			 */
			if (output.getRGB(next.x, next.y) == Color.BLACK.getRGB()
					|| output.getRGB(next.x, next.y) == Color.WHITE.getRGB()) {
				g.setColor(pipe.color);
				g.drawLine(next.x, next.y, next.x, next.y);
				pipe.occupied++;
			}

			/*
			 * Add neighbors for next flooding steps.
			 */
			if (next.x > 0 && output.getRGB(next.x - 1, next.y) == Color.BLACK.getRGB()) {
				pipe.candidates.add(new Point(next.x - 1, next.y));
			}
			if (next.y > 0 && output.getRGB(next.x, next.y - 1) == Color.BLACK.getRGB()) {
				pipe.candidates.add(new Point(next.x, next.y - 1));
			}
			if (next.x < output.getWidth() - 1 && output.getRGB(next.x + 1, next.y) == Color.BLACK.getRGB()) {
				pipe.candidates.add(new Point(next.x + 1, next.y));
			}
			if (next.y < output.getHeight() - 1 && output.getRGB(next.x, next.y + 1) == Color.BLACK.getRGB()) {
				pipe.candidates.add(new Point(next.x, next.y + 1));
			}

			/*
			 * Step was done.
			 */
			result = true;
		}

		return result;
	}

	/**
	 * Second flood algorithm.
	 * 
	 * @param refine
	 *            How many random attempts to be done in order to refine the
	 *            selection.
	 */
	private static void flood(int refine) {
		for (Pipe pipe : pipes) {
			g.setColor(pipe.color);
			g.drawLine(pipe.vertex1.x, pipe.vertex1.y, pipe.vertex2.x, pipe.vertex2.y);
		}

		while (step(refine) == true) {
		}
	}

	/**
	 * Transform the picture in squares.
	 * 
	 * @param side
	 *            Square side size.
	 */
	private static void squareization(int side) {
		/*
		 * Step on each square.
		 */
		for (int a = 0; a < output.getHeight(); a += side) {
			for (int b = 0; b < output.getWidth(); b += side) {
				Map<Integer, Integer> counters = new HashMap<Integer, Integer>();

				/*
				 * Count all colors.
				 */
				for (int j = a; j < a + side && j < output.getHeight(); j++) {
					for (int i = b; i < b + side && i < output.getWidth(); i++) {
						int pixel = output.getRGB(i, j);

						/*
						 * Do not count the white color.
						 */
						if (pixel == Color.WHITE.getRGB()) {
							continue;
						}

						if (counters.containsKey(pixel) == false) {
							counters.put(pixel, 1);
						} else {
							counters.put(pixel, counters.get(pixel) + 1);
						}
					}
				}

				/*
				 * Find the most presented color.
				 */
				int max = 0;
				int color = output.getRGB(a, b);
				for (Integer key : counters.keySet()) {
					if (counters.get(key) > max) {
						color = key;
						max = counters.get(key);
					}
				}

				/*
				 * Occupy the square.
				 */
				g.setColor(new Color(color));
				for (int j = a; j < a + side && j < output.getHeight(); j++) {
					for (int i = b; i < b + side && i < output.getWidth(); i++) {
						/*
						 * Do not change pixels with white color.
						 */
						if (output.getRGB(i, j) == Color.WHITE.getRGB()) {
							continue;
						}

						g.drawLine(i, j, i, j);
					}
				}
			}
		}
	}

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
		JSONObject json = (JSONObject) parser.parse(new FileReader("./dat/in03.json"));

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

		// flood(0);

		GeneticAlgorithmSolver ga = new GeneticAlgorithmSolver(37, 0.9, 0.01, 2, 0.1, 60);
		List<List<Point>> solution = ga.solve(polygon, pipes);
		int index = 0;
		for (List<Point> points : solution) {
			Polygon shape = new Polygon();
			for (Point vertex : points) {
				shape.addPoint(vertex.x, vertex.y);
			}
			g.setColor(pipes.get(index++).color);
			g.drawPolygon(shape);
			g.fillPolygon(shape);
		}

		/*
		 * Store current image in an image file.
		 */
		ImageIO.write((RenderedImage) output, "png", new File("./bin/out.png"));
	}

}
