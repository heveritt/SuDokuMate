package sudoku.solver;

import java.util.*;

/**
 * An abstract representation of a constraint in a constrained problem.
 * @see ConstrainedProblem
 */
public abstract class Constraint 
{
	/** To be returned by {@link #getNSolutions()}, if the number of solutions to the constraint is unknown */
	public final static int UNKNOWN_NO_SOLUTIONS = Integer.MAX_VALUE;

	private final static int CLEAN = 0;
	private final static int DIRTY = 1;

	private Set<Candidate> candidates;
	private Set<Candidate> definites;
	private String label;
	private int state = CLEAN;
	private ConstrainedProblem parent = null;

	/**
	 * Create an anonymous constraint.
	 * It is advised to use the alternate constructor, taking a label, to aid debugging. 
	 */
	public Constraint()
	{
		this("Anonymous Constraint");
	}
	/**
	 * Create a constraint with the given label.
	 * The label will be returned by the toString method, aiding debugging.
	 */
	public Constraint(String label)
	{
		this.candidates = new HashSet<Candidate>();
		this.definites = new HashSet<Candidate>();
		this.label = label;
	}
	
	/**
	 * Used to associate candidates that may be part of the solution to this constraint.
	 */
	public void add(Candidate candidate)
	{
		candidates.add(candidate);
		candidate.add(this);
	}
	/**
	 * Associates the parent constrained problem to this constraint, allowing eliminations
	 * and solution inclusions to be communicated to it.
	 */
	public void setParent(ConstrainedProblem parent)
	{
		this.parent = parent;
	}
	/**
	 * Returns the label attributed to this constraint.
	 */
	public String toString()
	{
		return label;
	}
	/**
	 * Applies the constraint to the remaining candidates, possibly resulting in some being eliminated
	 * and some being included in the solution.
	 */
	public abstract void apply();
	/**
	 * Returns the number of possible solutions (sets of solution candidates) to this constraint.
	 * If there is only one remaining solution, then the constraint is considered solved. If it is known that
	 * there is still more than one solution remaining, but that to work out how many would require significant
	 * processing, then UNKNOWN_NO_SOLUTIONS should be returned. Otherwise returning the number of remaining
	 * solution sets may help optimise the constrained problem solver.
	 */
	public abstract int getNSolutions();
	/**
	 * Returns the set of candidates that forms the single solution to a solved constraint.
	 */
	public abstract Set<Candidate> getSolution();
	/**
	 * Returns a set of each of the remaining possible solutions (sets of solution candidates).
	 */
	public abstract Set<Set<Candidate>> getPossibleSolutions();

	/**
	 * Returns the number of remaining candidates (not including those that are solution definites).
	 */
	protected int getNCandidates()
	{
		return candidates.size();
	}
	/**
	 * Returns the number candidates that definitely form part of the solution.
	 */
	protected int getNDefinites()
	{
		return definites.size();
	}
	/**
	 * Returns a copied set of remaining candidates (not including those that are solution definites).
	 */
	protected Set<Candidate> getCandidates()
	{
		return new HashSet<Candidate>(candidates);
	}
	/**
	 * Returns a copied set of candidates that definitely form part of the solution.
	 */
	protected Set<Candidate> getDefinites()
	{
		return new HashSet<Candidate>(definites);
	}
	/**
	 * Marks the constraint as "DIRTY", indicating that the {@link #apply()} method needs to be called.
	 * A constraint may be marked as "DIRTY" from its creation, if it is thought that by its subsequent application, 
	 * some candidates may be eliminated, or included in the solution. Every time the set of available candidates
	 * changes a constraint is automatically marked "DIRTY" indicating that it needs to be re-applied.
	 */
	protected void setDirty()
	{
		state = DIRTY;
		if (parent != null) parent.addDirtyConstraint(this);
	}
	/**
	 * Marks the constraint as "CLEAN".
	 * A constraint will be marked as "CLEAN" after it has been applied, and candidates eliminated, 
	 * or included in the solution.
	 */
	protected void setClean()
	{
		state = CLEAN;
		if (parent != null) parent.removeDirtyConstraint(this);
	}
	/**
	 * Returns true if a constraint is "DIRTY", and the {@link #apply()} method needs to be called.
	 */
	protected boolean isDirty()
	{
		return (state == DIRTY);
	}
	/**
	 * Eliminate a collection of candidates.
	 */
	protected void eliminate(Collection<Candidate> candidates)
	{
		for (Candidate candidate : candidates) eliminate(candidate);
	}
	/**
	 * Include a collection of candidates in the solution.
	 */
	protected void includeInSolution(Collection<Candidate> candidates)
	{
		for (Candidate candidate : candidates) includeInSolution(candidate);
	}
	/**
	 * Reinstate a collection of candidates, removing them from the solution if necessary.
	 */
	protected void reinstate(Collection<Candidate> candidates)
	{
		for (Candidate candidate : candidates) reinstate(candidate);
	}
	/**
	 * Eliminate a candidate.
	 */
	protected void eliminate(Candidate candidate)
	{
		privateEliminate(candidate);
		candidate.eliminate(this);
	}
	/**
	 * Include a candidate in the solution.
	 */
	protected void reinstate(Candidate candidate)
	{
		privateReinstate(candidate);
		candidate.reinstate(this);
	}
	/**
	 * Reinstate a candidate, removing it from the solution if necessary.
	 */
	protected void includeInSolution(Candidate candidate)
	{
		privateIncludeInSolution(candidate);
		candidate.includeInSolution(this);
	}
	
	int solve()
	{
		apply();
		setClean();
		return getNSolutions();
	}
	void privateEliminate(Candidate candidate)
	{
		candidates.remove(candidate);
		definites.remove(candidate);
	}
	void privateReinstate(Candidate candidate)
	{
		candidates.add(candidate);
		definites.remove(candidate);
	}
	void privateIncludeInSolution(Candidate candidate)
	{
		candidates.remove(candidate);
		definites.add(candidate);
	}
}
