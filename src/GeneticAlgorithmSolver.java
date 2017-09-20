import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.FixedElapsedTime;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.commons.math3.genetics.UniformCrossover;

/**
 * Solver based on genetic algorithms.
 * 
 * @author Todor Balabanov
 */
public class GeneticAlgorithmSolver {

	/**
	 * Pseudo-random number generator.
	 */
	private final static Random PRNG = new Random();

	/**
	 * 
	 */
	private int populationSize;

	/**
	 * 
	 */
	private double crossoverRate;

	/**
	 * 
	 */
	private double mutationRate;

	/**
	 * 
	 */
	private int tournamentArity;

	/**
	 * 
	 */
	private double elitismRate;

	/**
	 * 
	 */
	private long optimizationTimeout;

	/**
	 * Constructor with all parameters.
	 * 
	 * @param populationSize
	 *            Size of the population.
	 * @param crossoverRate
	 *            Crossover rate.
	 * @param mutationRate
	 *            Mutation rate.
	 * @param tournamentArity
	 *            Tournament arity.
	 * @param elitismRate
	 *            Size of the elite.
	 * @param optimizationTimeout
	 *            Optimization timeout in seconds.
	 */
	public GeneticAlgorithmSolver(int populationSize, double crossoverRate, double mutationRate, int tournamentArity,
			double elitismRate, long optimizationTimeout) {
		super();

		this.populationSize = populationSize;
		this.crossoverRate = crossoverRate;
		this.mutationRate = mutationRate;
		this.tournamentArity = tournamentArity;
		this.elitismRate = elitismRate;
		this.optimizationTimeout = optimizationTimeout;
	}

	/**
	 * Search for solution.
	 * 
	 * @param pipes
	 *            Pipes description.
	 * @param polygon
	 *            Global polygon.
	 * 
	 * @return Best found solution.
	 */
	public List<List<Point>> solve(Polygon polygon, List<Pipe> pipes) {
		/*
		 * Generate initial population.
		 */
		List<Chromosome> list = new LinkedList<Chromosome>();
		for (int i = 0; i < populationSize; i++) {
			/*
			 * Random solution.
			 */
			List<List<Point>> representation = new ArrayList<List<Point>>();
			for (Pipe pipe : pipes) {
				ArrayList<Point> vertices = new ArrayList<Point>();
	
				/*
				 * It is only line.
				 */
				vertices.add(pipe.vertex1);
				vertices.add(pipe.vertex2);

				/*
				 * Form triangle.
				 */
				Point vertex3 = new Point((int) ((pipe.vertex1.x + pipe.vertex2.x + 0.5D) / 2),
						(int) ((pipe.vertex1.y + pipe.vertex2.y + 0.5D) / 2));
				while (polygon.contains(
						vertex3) == false /* And should not be on a straight line! */) {
					vertex3.x += (1 - PRNG.nextInt(3));
					vertex3.y += (1 - PRNG.nextInt(3));
				}
				vertices.add(vertex3);

				/*
				 * Sub-polygon for the pipe side.F
				 */
				representation.add(vertices);
			}

			/*
			 * Add as solution.
			 */
			list.add(new PolygonListChromosome(representation, polygon, pipes));
		}
		Population initial = new ElitisticListPopulation(list, 2 * list.size(), elitismRate);

		/*
		 * Initialize genetic algorithm.
		 */
		GeneticAlgorithm algorithm = new GeneticAlgorithm(new UniformCrossover<PolygonListChromosome>(0.5),
				crossoverRate, new RandomVertexMutation(), mutationRate, new TournamentSelection(tournamentArity));
		/*
		 * Run optimization.
		 */
		Population optimized = algorithm.evolve(initial, new FixedElapsedTime(optimizationTimeout));

		/*
		 * Obtain result.
		 */
		return ((PolygonListChromosome) optimized.getFittestChromosome()).getRepresentation();
	}

}
