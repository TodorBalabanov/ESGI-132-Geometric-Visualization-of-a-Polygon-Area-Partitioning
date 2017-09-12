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
		JSONObject json = (JSONObject) parser.parse(new FileReader("./dat/in01.json"));

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
		 * Pipes coordinates.
		 */
		List<Point> pipes = new ArrayList<Point>();
		for (int k = 0, l; k < x.size() && k < y.size(); k++) {
			l = (k + 1) % shares.size();
			pipes.add(new Point((int) Math.round((x.get(k) + x.get(l)) / 2D),
					(int) Math.round((y.get(k) + y.get(l)) / 2D)));
		}

		/*
		 * Pipes colors.
		 */
		List<Color> colors = new ArrayList<Color>();
		do {
			colors.clear();
			for (int c = 0; c < shares.size(); c++) {
				colors.add(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
			}
		} while (colors.contains(Color.BLACK) || colors.contains(Color.WHITE));

		/*
		 * Generate polygon.
		 */
		Polygon polygon = new Polygon(x.stream().mapToInt(Integer::intValue).toArray(),
				y.stream().mapToInt(Integer::intValue).toArray(), shares.size());

		/*
		 * Save result.
		 */
		BufferedImage output = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		Graphics g = output.getGraphics();

		/*
		 * White background.
		 */
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, output.getWidth(), output.getHeight());

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
		 * Recalculate shares in pixels.
		 */
		List<Integer> areas = new ArrayList<Integer>();
		for (Double share : shares) {
			areas.add((int) Math.round(share * area / 100D));
		}

		/*
		 * Draw positions of the pipes.
		 */
		for (int k = 0; k < pipes.size(); k++) {
			g.setColor(colors.get(k));
			g.drawLine(pipes.get(k).x, pipes.get(k).y, pipes.get(k).x, pipes.get(k).y);
		}

		ImageIO.write((RenderedImage) output, "png", new File("./bin/out.png"));
	}

}