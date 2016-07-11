package sudoku.solver;

import java.util.*;
/**
 * A generic representation of a constrained problem.
 * A constrained problem is one which may be expressed in terms of a finite set of solution candidates, a
 * set of constraints that constrain the solution and optionally a set of givens which represent elements of the
 * solution that have already been given. The solution to the problem will be a subset of the supplied candidates,
 * and of course must always include the givens. Each constraint will be associated with a subset of the candidates,
 * and any valid solution to that constraint will consist of a subset of that associated subset! In fact a constrained
 * problem itself can also be considered to be a constraint, if included in a larger constrained problem. 
 * <P>The solving of a constrained problem is a process of applying the constraints. The application of a constraint may
 * result in one or more solution candidates being eliminated, or being included in the solution. This in turn
 * will affect all other constraints associated with the same candidates, making them "dirty", i.e. in need of 
 * re-application. Hence, they will be put on a queue to be re-applied, and the process will continue, until
 * there are no more "dirty" constraints left. If a solution has not been reached at this stage, then the first
 * remaining unresolved constraint is chosen, and each possible solution to it is tried in turn. If that
 * solution results in the breaking of other constraints then it is rejected, otherwise the process continues as before.
 * <P>Through the above process, it may be determined if the constrained problem has 0, 1 or many solutions, and if
 * just the one solution, the set of solution candidates representing that solution may be returned.
 * <P>Many puzzles may be represented as constrained problems. The SuDoku puzzle is the most well known of these,
 * where the candidates are the numbers 1-9 in each of the 81 cells of the grid (729 candidates), and the constraints
 * are that each cell must contain only one of those numbers, and that each row, column and box must contain each
 * number only once. Here the givens are the numbers already supplied in the Sudoku grid.
 * <P>Another example is the Bridges puzzle, where islands have to be joined by one or two bridges to form a complete network. 
 * Here the candidates are the drawing of 0,1 or 2 bridges between each adjacent island pair. The constraints are that 
 * only one of the candidates (0,1 or 2) is possible for each adjacent pair, that the total number of bridges from each island
 * must equal the number in that island, and finally that each island must be connected to each other island. The
 * Bridges puzzle has no givens. 
 */
public class ConstrainedProblem extends Constraint
{
	private Set<Constraint> constraints;
	private Set<Constraint> dirtyConstraints;
	private Set<Candidate> currentSolution = null;
	private int nSolutions = UNKNOWN_NO_SOLUTIONS;
	
	/**
	 * Create a constrained problems with the supplied candidates and constraints, and those
	 * candidates which have been given as being included in the solution.
	 * This is the constructor to be used for puzzles where elements of the solution are given as part of
	 * the puzzle (e.g. a SuDoku puzzle).
	 */	
	public ConstrainedProblem(Collection<Candidate> candidates, Collection<Constraint> constraints, Collection<Candidate> givens)
	{
		this(candidates, constraints);
		includeInSolution(givens);
	}
	/**
	 * Create a constrained problems with the supplied candidates and constraints.
	 * Certain types of constrained problem (e.g. the representation of a Bridges puzzle)
	 * may be expressed without the need for any givens. In these cases, this constructor 
	 * should be used.
	 */
	public ConstrainedProblem(Collection<Candidate> candidates, Collection<Constraint> constraints)
	{
		super();
		this.constraints = new HashSet<Constraint>(constraints);
		this.dirtyConstraints = new HashSet<Constraint>();
		for(Constraint constraint : constraints)
		{
			constraint.setParent(this);
			if (constraint.isDirty()) dirtyConstraints.add(constraint);
		}
		;
		for(Candidate candidate : candidates) add(candidate);
	}

	/**
	 * Solves the problem by applying all constraints.
	 */
	public int solve()
	{
		return super.solve();
	}
	/**
	 * Applies all constraints in the constrained problem recursively until it is determined
	 * how many solutions the problem has.
	 */
	public void apply() 
	{
		nSolutions = solveRecurse();
		if (nSolutions > 1) nSolutions = UNKNOWN_NO_SOLUTIONS;
	}
	/**
	 * Returns 0, 1, or UNKNOWN_NO_SOLUTIONS
	 */
	public int getNSolutions()
	{
		return nSolutions;
	}
	/**
	 * Returns the set of candidates representing the solution, should a single solution have been found. 
	 */
	public Set<Candidate> getSolution()
	{
		return currentSolution;
	}
	/**
	 * This method is NOT YET implemented - DO NOT USE.
	 * As it currently stands, the method will return a set of just the first solution set.
	 */
	public Set<Set<Candidate>> getPossibleSolutions()
	{
		return Collections.singleton(getSolution());
	}	
		
	void addDirtyConstraint(Constraint constraint)
	{
		dirtyConstraints.add(constraint);
	}
	void removeDirtyConstraint(Constraint constraint)
	{
		dirtyConstraints.remove(constraint);
	}

	private int solveRecurse()
	{
		Constraint constraint;
		
		// Look at all "dirty" constraints. That will be constraints which either have never been
		// applied, or constraints with associated candidates that have moved (either been eliminated or
		// included in the solution) since the constraint was last applied.
		while(dirtyConstraints.size() > 0)
		{
			constraint = (Constraint)dirtyConstraints.iterator().next();
			// Solving the constraint may result in candidates moving, and other constraints becoming "dirty"
			int nConstraintSolutions = constraint.solve();
			if (nConstraintSolutions == 0) return 0; // Have reached a dead end => no solution this way.
			if (nConstraintSolutions == 1)           // Have resolved this constraint
			{
				constraints.remove(constraint);
			}
		}
		
		int nSolutions;
		if (constraints.size() == 0)
		{
			// Got to the end and found a single solution.
			nSolutions = 1;
			currentSolution = getDefinites();
		} else {
			// Pick the first constraint and try each of its solutions in turn.
			constraint = (Constraint)constraints.iterator().next();
			nSolutions = 0;
			Iterator<Set<Candidate>> iter = constraint.getPossibleSolutions().iterator();
			Set<Candidate> solutionSet;
			State state = getState();
			// Stop at 2 solutions, as could take a long time to find all solutions!
			while ( iter.hasNext() && nSolutions < 2)
			{
				solutionSet = iter.next();
				includeInSolution(solutionSet);
				nSolutions += solveRecurse();
				restoreState(state);
			}
		}
		// Will be 0, 1 or 2, 2 indicating 2 or more.
		return nSolutions;
	}
	private State getState()
	{
		return new State();
	}
	private void restoreState(State state)
	{
		reinstate(state.candidatesCopy);
		constraints = state.constraintsCopy;
	}
	
	private class State
	{
		private Set<Candidate> candidatesCopy;
		private Set<Constraint> constraintsCopy;
		
		State()
		{
			this.candidatesCopy = new HashSet<Candidate>(getCandidates());
			this.constraintsCopy = new HashSet<Constraint>(constraints);
		}
	}
}
