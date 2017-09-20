import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

/**
 * Chromosome with list of vertices. Cromosomes are list of polygons. Each
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
	public PolygonListChromosome(List<Point>[] representation, Polygon polygon, List<Pipe> pipes) throws InvalidRepresentationException {
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
		 * Draw solution.
		 */
		for (Pipe pipe : pipes) {
			// TODO
		}

		double fitness = 0D;

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
