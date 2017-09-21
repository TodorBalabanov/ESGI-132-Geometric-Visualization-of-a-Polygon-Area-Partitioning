import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

/**
 * Chromosome with list of vertices. Chromosomes are list of polygons. Each
 * polygon is presented with list of points.
 * 
 * @author Todor Balabanov
 */
public class PolygonListChromosome extends AbstractListChromosome<List<Point>> {

	/**
	 * Reference to the global polygon.
	 */
	private static Polygon polygon = null;

	/**
	 * List of pipes references.
	 */
	private static List<Pipe> pipes = null;

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
	private double distance(Point a, Point b, Point p) {
		double normal = Math.sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y));
		return Math.abs((p.x - a.x) * (b.y - a.y) - (p.y - a.y) * (b.x - a.x)) / normal;
	}

	/**
	 * Constructor with array representation.
	 * 
	 * @param representation
	 *            Some solution.
	 * 
	 * @throws InvalidRepresentationException
	 *             It is thrown if the chromosome would not be valid.
	 */
	public PolygonListChromosome(List<Point>[] representation) throws InvalidRepresentationException {
		this(representation, polygon, pipes);
	}

	/**
	 * Constructor with array representation.
	 * 
	 * @param representation
	 *            Some solution.
	 * @param polygon
	 *            Reference to the global polygon.
	 * @param pipes
	 *            Reference to the pipes structure.
	 * 
	 * @throws InvalidRepresentationException
	 *             It is thrown if the chromosome would not be valid.
	 */
	public PolygonListChromosome(List<Point>[] representation, Polygon polygon, List<Pipe> pipes)
			throws InvalidRepresentationException {
		super(representation);

		/*
		 * Keep reference to the global polygon and its pipes.
		 */
		PolygonListChromosome.polygon = polygon;
		PolygonListChromosome.pipes = pipes;
	}

	/**
	 * Constructor with list representation and flag for deep copyF.
	 * 
	 * @param representation
	 *            Some solution.
	 * @param copy
	 *            True for deep copy, false otherwise.
	 */
	public PolygonListChromosome(List<List<Point>> representation, boolean copy) {
		this(representation, copy, polygon, pipes);
	}

	/**
	 * Constructor with list representation and flag for deep copyF.
	 * 
	 * @param representation
	 *            Some solution.
	 * @param copy
	 *            True for deep copy, false otherwise.
	 * @param polygon
	 *            Reference to the global polygon.
	 * @param pipes
	 *            Reference to the pipes structure.
	 */
	public PolygonListChromosome(List<List<Point>> representation, boolean copy, Polygon polygon, List<Pipe> pipes) {
		super(representation, copy);

		/*
		 * Keep reference to the global polygon and its pipes.
		 */
		PolygonListChromosome.polygon = polygon;
		PolygonListChromosome.pipes = pipes;
	}

	/**
	 * Constructor with list representation.
	 * 
	 * @param representation
	 *            Some solution.
	 * 
	 * @throws InvalidRepresentationException
	 *             It is thrown if the chromosome would not be valid.
	 */
	public PolygonListChromosome(List<List<Point>> representation) throws InvalidRepresentationException {
		this(representation, polygon, pipes);
	}

	/**
	 * Constructor with list representation.
	 * 
	 * @param representation
	 *            Some solution.
	 * @param polygon
	 *            Reference to the global polygon.
	 * @param pipes
	 *            Reference to the pipes structure.
	 * 
	 * @throws InvalidRepresentationException
	 *             It is thrown if the chromosome would not be valid.
	 */
	public PolygonListChromosome(List<List<Point>> representation, Polygon polygon, List<Pipe> pipes)
			throws InvalidRepresentationException {
		super(representation);

		/*
		 * Keep reference to the global polygon and its pipes.
		 */
		PolygonListChromosome.polygon = polygon;
		PolygonListChromosome.pipes = pipes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double fitness() {
		/*
		 * Form image to calculate on.
		 */
		BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = image.getGraphics();

		/*
		 * White background.
		 */
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());

		/*
		 * Initial black area for the global polygon.
		 */
		graphics.setColor(Color.BLACK);
		graphics.drawPolygon(polygon);
		graphics.fillPolygon(polygon);

		/*
		 * Vertices counter.
		 */
		int numberOfVertices = 0;

		/*
		 * Count pixels in the areas.
		 */
		Map<Integer, Integer> counters = new HashMap<Integer, Integer>();
		counters.put(Color.BLACK.getRGB(), 0);

		/*
		 * Draw solution.
		 */
		for (Pipe pipe : pipes) {
			/*
			 * Start counters from zero.
			 */
			counters.put(pipe.color.getRGB(), 0);

			/*
			 * List all polygons.
			 */
			for (List<Point> points : getRepresentation()) {
				Polygon shape = new Polygon();
				for (Point vertex : points) {
					shape.addPoint(vertex.x, vertex.y);
				}
				graphics.setColor(pipe.color);
				graphics.drawPolygon(shape);
				graphics.fillPolygon(shape);

				numberOfVertices += shape.npoints;
			}
		}

		/*
		 * Map colors to polygon side.
		 */
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		for (Pipe pipe : pipes) {
			map.put(pipe.color.getRGB(), new Point[] { pipe.vertex1, pipe.vertex2 });
		}

		/*
		 * Calculate histogram and measure polygons closeness to their sides.
		 */
		double closeness = 0;
		for (int j = 0; j < image.getHeight(); j++) {
			for (int i = 0; i < image.getWidth(); i++) {
				/*
				 * Histogram.
				 */
				int key = image.getRGB(i, j);
				if (counters.containsKey(key) == false) {
					counters.put(key, 0);
				}
				counters.put(key, counters.get(key) + 1);

				/*
				 * Do not measure empty areas.
				 */
				if (key == Color.BLACK.getRGB() || key == Color.WHITE.getRGB()) {
					continue;
				}

				/*
				 * Closeness.
				 */
				Point side[] = (Point[]) map.get(key);
				closeness += distance(side[0], side[1], new Point());
			}
		}

		/*
		 * Count overdrawing.
		 */
		int overdrawing = 0;
		for (Pipe pipe : pipes) {
			int over = pipe.area - counters.get(pipe.color.getRGB());
			over = (over < 0) ? 0 : over;
			overdrawing += over;
		}

		/*
		 * cost function <- pixel to side distance * (vertices number + black
		 * area + overdrawn area)
		 * 
		 * black area - should be zero
		 * 
		 * pixels to side distance - should be minimal
		 * 
		 * vertices number - should be minimal
		 * 
		 * overdrawn area - should be zero
		 */
		double fitness = closeness * (numberOfVertices + counters.get(Color.BLACK.getRGB()) + overdrawing);

		/*
		 * Bigger fitness is better chromosome.
		 */
		return -fitness;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void checkValidity(List<List<Point>> representation) throws InvalidRepresentationException {
		// TODO It is not needed at this point.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractListChromosome<List<Point>> newFixedLengthChromosome(List<List<Point>> representation) {
		return new PolygonListChromosome(representation, true, polygon, pipes);
	}

	/**
	 * Chromosome representation getter.
	 * 
	 * @return List of polygons represented as lists of vertices.
	 */
	public List<List<Point>> getRepresentation() {
		return super.getRepresentation();
	}

}
