package sudoku.solver;

import java.util.*;
/**
 * A single candidate constraint is a constraint where the solution set consists of one candidate only.
 * Generally constraints may include one or more candidates in a satisfactory solution to that constraint.
 * The single candidate constraint will only have one solution candidate. Thus if it is known (through another constraint)
 * that a particular candidate is part of the solution of the overall constrained problem, then all other
 * candidates associated with this constraint may be eliminated.
 */
public class SingleCandidateConstraint extends Constraint 
{
	private int nSolutions;
	
	/**
	 * Create an anonymous single value constraint.
	 * It is advised to use the alternate constructor, taking a label, to aid debugging. 
	 */
	public SingleCandidateConstraint()
	{
		this("Anonymous Single Value Constraint");
	}
	/**
	 * Create a single value constraint with the given label.
	 * The label will be returned by the toString method, aiding debugging.
	 */
	public SingleCandidateConstraint(String label)
	{
		super(label);
		nSolutions = getNCandidates();
	}

	/**
	 * Applies the single value constraint.
	 * Since the constraint has a single candidate solution, if more than one of its candidates has been marked as forming
	 * part of the solution, or if there are no candidates left, then the constraint is considered to have no solutions.
	 * Otherwise, if a single solution has been found, then all other candidates are eliminated. 
	 * Finally, if only one candidate remains, then it is considered to be the solution.
	 */
	public void apply()
	{
		if (getNDefinites() > 1) {
			nSolutions = 0;
		} else if (getNDefinites() == 1) {
			if (getNCandidates() > 0) eliminate(getCandidates());
			nSolutions = 1;
		} else {
			if (getNCandidates() > 1) {
				nSolutions = getNCandidates();
			} else if (getNCandidates() == 1) {
				includeInSolution(getCandidates());
				nSolutions = 1;
			} else {
				nSolutions = 0;
			}
		}
	}
	/**
	 * Returns the number of solutions to this constraint.
	 * This will return 0 if the constraint cannot be met (e.g. no single candidate solution is possible).
	 * A 1 will be returned if the single candidate solution has been found.
	 * Otherwise, the number of remaining candidates will be returned, as being the number of possible solutions.
	 */
	public int getNSolutions()
	{
		return nSolutions;
	}
	/**
	 * If a solution has been found, a set containing just the solution candidate will be returned, otherwise a null
	 * will be returned.
	 */
	public Set<Candidate> getSolution() 
	{
		if (nSolutions == 1) {
			return getDefinites();
		} else {
			return null;
		}
	}
	/**
	 * A set containing individual singleton sets of each of the remaining candidate will be returned.
	 */
	public Set<Set<Candidate>> getPossibleSolutions() 
	{
		Set<Set<Candidate>> possibles = new HashSet<Set<Candidate>>();
		for (Candidate candidate : getCandidates())
		{
			possibles.add(Collections.singleton(candidate));
		}
		return possibles;
	}
}