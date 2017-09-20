import java.awt.Point;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

/**
 * Change position of random vertex.
 * 
 * @author Todor Balabanov
 */
public class RandomVertexMutation implements MutationPolicy {

	/**
	 * Pseudo-random number generator.
	 */
	private final static Random PRNG = new Random();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Chromosome mutate(Chromosome original) throws MathIllegalArgumentException {
		/*
		 * Do not mutate other types of chromosomes.
		 */
		if (original instanceof PolygonListChromosome == false) {
			return original;
		}

		/*
		 * Obtain point to mutate.
		 */
		List<List<Point>> representation = ((PolygonListChromosome) original).getRepresentation();
		List<Point> polygon = representation.get(PRNG.nextInt(representation.size()));
		Point vertex = polygon.get(2 + PRNG.nextInt(polygon.size() - 2));

		/*
		 * Mutate by vertex move.
		 */
		switch (PRNG.nextInt(4)) {
		case 0:
			vertex.x++;
			break;
		case 1:
			vertex.y++;
			break;
		case 2:
			vertex.x--;
			break;
		case 3:
			vertex.y--;
			break;
		}

		/*
		 * Mutate by vertex count change.
		 */
		if (PRNG.nextInt(1000) < 1) {
			// TODO Implement better way to control when number of vertices will
			// be changed.

			switch (PRNG.nextInt(2)) {
			case 0:
				/*
				 * Add vertex.
				 */
				int index1 = 1 + PRNG.nextInt(polygon.size() - 1);
				int index2 = (index1 + 1) % polygon.size();
				Point vertex1 = polygon.get(index1);
				Point vertex2 = polygon.get(index2);
				Point middle = new Point((int) Math.round((vertex1.x + vertex2.x) / 2D),
						(int) Math.round((vertex1.y + vertex2.y) / 2D));
				polygon.add(index1, middle);
				break;

			case 1:
				/*
				 * Remove vertex.
				 */
				if (polygon.size() > 3) {
					polygon.remove(2 + PRNG.nextInt(polygon.size() - 2));
				}
				break;
			}
		}

		PolygonListChromosome result = new PolygonListChromosome(representation, true);

		return result;
	}

}
