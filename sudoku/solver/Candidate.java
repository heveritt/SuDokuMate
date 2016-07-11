package sudoku.solver;

import java.util.*;

/**
 * A representation of a solution candidate in a constrained problem.
 * @see ConstrainedProblem
 */
public class Candidate 
{
	Set<Constraint> constraints;
	String label;
	
	/**
	 * Create an anonymous candidate.
	 * It is advised to use the alternate constructor, taking a label, to aid debugging. 
	 */
	public Candidate()
	{
		this("Anonymous Candidate");
	}
	
	/**
	 * Create a candidate with the given label.
	 * The label will be returned by the toString method, aiding debugging.
	 */
	public Candidate(String label)
	{
		constraints = new HashSet<Constraint>();
		this.label = label;
	}
	
	/**
	 * Returns the label attributed to this candidate.
	 */
	public String toString()
	{
		return label;
	}
	
	void add(Constraint constraint)
	{
		constraints.add(constraint);
	}
	
	void eliminate(Constraint origin)
	{
		for(Constraint constraint : constraints)
		{
			if (! constraint.equals(origin))
			{
				constraint.privateEliminate(this);
				constraint.setDirty();
			}
		}
	}
	
	void includeInSolution(Constraint origin)
	{
		for(Constraint constraint : constraints)
		{
			if (! constraint.equals(origin))
			{
				constraint.privateIncludeInSolution(this);
				constraint.setDirty();
			}
		}
	}
	
	void reinstate(Constraint origin)
	{
		for(Constraint constraint : constraints)
		{
			if (! constraint.equals(origin))
			{
				constraint.privateReinstate(this);
				constraint.setDirty();
			}
		}
	}
}
