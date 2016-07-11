package sudoku.solver;

import java.util.*;

/**
 * This class creates and then uses a {@link ConstrainedProblem} to solve a SuDoku puzzle.
 * The class bundles together all the code required to translate from a SuDoku to a generic constrained problem
 * and then to translate the solution back to something that may be mapped back to the SuDoku grid.
 * To create the constrained problem, the following steps are required:
 * <UL>
 * <LI>{@link SuDokuCandidate} objects are created for all row, column and value combinations.</LI>
 * <LI>{@link SingleCandidateConstraint} objects are created for the following 4 constraints:
 * <OL>
 * <LI>Each cell may contain only one value</LI>
 * <LI>Each row must contain each value only once</LI>
 * <LI>Each column must contain each value only once</LI>
 * <LI>Each box must contain each value only once</LI>
 * </OL>
 * They are then linked to the relevant candidates 
 * (e.g. the constraint that row 2 must contain the value 3 will be linked to the candidates representing
 * [row 2, column 1, value 3]; [row 2, column 2, value 3] etc.)</LI>
 * <LI>The givens, that is the candidates that we know are part of the solution, are added</LI>
 * </UL>
 * After creating the problem, it may be solved using the {@link #solve()} method, which will return whether or
 * not the puzzle has zero, one or many solutions.
 * If there is only one solution, the {@link #getSolution()} method will retrieve the solution candidates, and convert
 * them into a 2D array representing the puzzle solution.
 */
public class SuDokuProblem 
{
	private int nBands;
	private int nStacks;
	private int nValues;
	private Object[] values;
	private Object[][] puzzle;
	private Candidate[][][] candidates;
	private List<Candidate> candidateList;
	private List<Constraint> constraintList;
	private List<Candidate> givenList;
	private ConstrainedProblem problem;
	
	/**
	 * Creates a constrained problem representing the given SuDoku puzzle.
	 * @param nBands Number of bands.
	 * @param nStacks Number of stacks.
	 * @param valueSet A complete set of the possible Objects that may be included in the puzzle.
	 * @param puzzle Array of objects representing the givens. A null object => a blank cell. 
	 */
	public SuDokuProblem(int nBands, int nStacks, Set<Object> valueSet, Object[][] puzzle)
	{
		this.nBands = nBands;
		this.nStacks = nStacks;
		this.nValues = nBands * nStacks;
		this.puzzle = puzzle;
		this.values = valueSet.toArray();
		initCandidates();
		initConstraints();
		initGivens();
		problem = new ConstrainedProblem(candidateList, constraintList, givenList);	
	}
	/**
	 * Solves the constrained problem, returning the number of solutions.
	 * @return 0 indicates no solutions, 1 indicates a unique solution, >1 indicates many solutions.
	 */
	public int solve()
	{
		return problem.solve();
	}
	/**
	 * Returns the unique solution of the SuDoku to the caller in the same form as the puzzle was originally
	 * expressed; a 2D array of objects.
	 */
	public Object[][] getSolution()
	{
		SuDokuCandidate element;
		Object[][] solution = new Object[nValues][nValues];
		for (Candidate candidate : problem.getSolution())
		{
			element = (SuDokuCandidate)candidate;
			solution[element.getRow()][element.getColumn()] = values[element.getValue()];
		}
		return solution;
	}
	
	private void initCandidates()
	{
		candidateList = new ArrayList<Candidate>();
		candidates = new SuDokuCandidate[nValues][nValues][nValues];
		int r,c,v;		
		for (r = 0; r < nValues; r++)
		{
			for (c = 0; c < nValues; c++)
			{
				for (v = 0; v < nValues; v++)
				{
					candidates[r][c][v] = new SuDokuCandidate(r,c,v);
					candidateList.add(candidates[r][c][v]);
				}
			}
		}
	}
	private void initConstraints()
	{
		constraintList = new ArrayList<Constraint>();
		initCellConstraints();
		initRowConstraints();
		initColumnConstraints();
		initBoxConstraints();
	}
	private void initCellConstraints()
	{
		int r,c,v;		
		for (r = 0; r < nValues; r++)
		{
			for (c = 0; c < nValues; c++)
			{
				Constraint cellConstraint = new SingleCandidateConstraint("Cell: [" + r + "," + c + "]");
				for (v = 0; v < nValues; v++)
				{
					cellConstraint.add(candidates[r][c][v]);
				}
				constraintList.add(cellConstraint);
			}
		}
	}
	private void initRowConstraints()
	{
		int r,c,v;		
		for (r = 0; r < nValues; r++)
		{
			for (v = 0; v < nValues; v++)
			{
				Constraint rowConstraint = new SingleCandidateConstraint("Row: " + r + " Value: " + v);
				for (c = 0; c < nValues; c++)
				{
					rowConstraint.add(candidates[r][c][v]);
				}
				constraintList.add(rowConstraint);
			}
		}
	}
	private void initColumnConstraints()
	{
		int r,c,v;		
		for (c = 0; c < nValues; c++)
		{
			for (v = 0; v < nValues; v++)
			{
				Constraint colConstraint = new SingleCandidateConstraint("Col: " + c + " Value: " + v);
				for (r = 0; r < nValues; r++)
				{
					colConstraint.add(candidates[r][c][v]);
				}
				constraintList.add(colConstraint);
			}
		}
	}
	private void initBoxConstraints()
	{
		int r,c,v,br,bc;
		int rStart, cStart;
		for (br = 0; br < nBands; br++)
		{
			rStart = br * nStacks;
			for (bc = 0; bc < nStacks; bc++)
			{
				cStart = bc * nBands;
				for (v = 0; v < nValues; v++)
				{
					Constraint boxConstraint = new SingleCandidateConstraint("Box: [" + br + "," + bc + "] Value: " + v);
					for (r = 0; r < nBands; r++)
					{
						for (c = 0; c < nStacks; c++)
						{
							boxConstraint.add(candidates[rStart+r][cStart+c][v]);
						}
					}
					constraintList.add(boxConstraint);
				}
			}
		}
	}
	private void initGivens()
	{
		givenList = new ArrayList<Candidate>();
		int r,c,v;		
		for (r = 0; r < nValues; r++)
		{
			for (c = 0; c < nValues; c++)
			{
				if (puzzle[r][c] != null) 
				{
					for (v = 0; v < nValues; v++)
					{
						if (puzzle[r][c].equals(values[v])) givenList.add(candidates[r][c][v]);
					}
				}
			}
		}
	}
}
