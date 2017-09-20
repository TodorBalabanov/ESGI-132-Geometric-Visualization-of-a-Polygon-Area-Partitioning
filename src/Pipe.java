import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

/**
 * Pipe description.
 * 
 * @author Todor Balabanov
 */
class Pipe {
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
	 * It is used in genetic algorithm to adjust polygon vertices.
	 */
	public Polygon polygon = new Polygon();

	/**
	 * Constructor with all parameters.
	 * 
	 * @param color
	 *            Color which will be used for visualization of the area
	 *            associated with the pipe.
	 * @param vertex1
	 *            Point for the first side.
	 * @param vertex2
	 *            Point for the second side.
	 * @param totalArea
	 *            Total polygon area in pixels is used to calculate pipe share
	 *            in pixels.
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