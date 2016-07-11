package sudoku;

import java.util.*;

import sudoku.solver.*;

/**
 * Puzzle is the principal class in the sudoku package. 
 * It is a non-visual representation of a SuDoku puzzle. It is not designed to be used on its own,
 * but rather to serve a SuDoku application. It provides the basic SuDoku structure;
 * rows, columns, boxes and cells, the latter of which it makes available to its caller
 * for containing user input "values", the format of which is at the caller's discretion.
 * <P>Two dimensional SuDoku puzzles of any size may be supported, provided they are
 * represented by a simple two dimensional grid of cells which is sub-divided into non-
 * overlapping boxes.
 * <P>A {@link sudoku.solver} package is used to check the solution to a given puzzle and
 * return whether or not it has 0, 1 or many solutions. The solution is retained if there 
 * is only a single solution.
 * <P>After a puzzle with a single solution has been established, hints on solving that 
 * puzzle may be elicited, and will be provided by external classes which complete the
 * abstract class {@link SuDoku.Strategy}. These will be provided access to the current
 * state of the puzzle, and will be consulted in turn for a hint. By default, the puzzle comes
 * with two strategies that look for singletons and hidden singles.
 */
public class SuDoku 
{
	public static final int NO_SOLUTION = 0;
	public static final int SINGLE_SOLUTION = 1;
	public static final int MANY_SOLUTIONS = 2;
	
	static final int DEFAULT_NBANDS = 3;
	static final int DEFAULT_NSTACKS = 3;
	// Create a set of the numbers 1 - 9, rather messy in java 1.4!
	static final Set<Object> DEFAULT_VALUE_SET = new HashSet<Object>(Arrays.asList(new String[] {"1","2","3","4","5","6","7","8","9"}));
	static final String[][] DEFAULT_PUZZLE = new String[][]{{null,null,null,"8",null,"1",null,null,null},
															{null,"5",null,null,"6",null,null,"9",null},
															{null,null,"8",null,null,null,"7",null,null},
															{"2",null,null,"3",null,"8",null,null,"6"},
															{null,"7",null,null,null,null,null,"8",null},
															{"9",null,null,"1",null,"5",null,null,"4"},
															{null,null,"5",null,null,null,"8",null,null},
															{null,"6",null,null,"5",null,null,"2",null},
															{null,null,null,"2",null,"9",null,null,null}};
	static final String[] DEFAULT_STRATEGIES = new String[] {	"sudoku.strategy.SingletonStrategy",
																"sudoku.strategy.HiddenSingleStrategy"};
			
	private int nBands;
	private int nStacks;
	private int nValues;
	private Set<Object> valueSet;
	private Object[][] puzzle;
	private Object[][] solution = null;
	private Strategy[] strategies;
	private Cell[][] cells;
	private Row[] rows;
	private Column[] columns;
	private Box[][] boxes;


	// Initialiser for testing purposes only, creates 3x3 SuDoku with a default puzzle.
	public SuDoku()
	{
		this(DEFAULT_NBANDS, DEFAULT_NSTACKS, DEFAULT_VALUE_SET, DEFAULT_PUZZLE);
	}

	/**
	 * Creates a SuDoku puzzle of the given dimensions, with the given set of allowable Objects,
	 * and the givens supplied.
	 * The puzzle will use the default strategies to search for singletons and hidden singles.
	 * <P><B>Note:<B> The number of bands (rows of boxes), multiplied by the number of stacks
	 * (columns of boxes) must equal the number of values in the valueSet. E.g. a 3x3 SuDoku implies
	 * 9 possible values; a 4x2 implies 8 possible values.
	 * @param nBands Number of bands.
	 * @param nStacks Number of stacks.
	 * @param valueSet A complete set of the possible Objects that may be included in the puzzle.
	 * @param puzzle Array of objects representing the givens. A null object => a blank cell. 
	 */
	public SuDoku(int nBands, int nStacks, Set<? extends Object> valueSet, Object[][] puzzle)
	{
		this(nBands, nStacks, valueSet, puzzle, DEFAULT_STRATEGIES);
	}
	/**	 
	 * Creates a SuDoku puzzle of the given dimensions, with the given set of allowable Objects,
	 * and the givens supplied.
	 * The strategy classes whose names are supplied will be used in the order in which they are supplied to
	 * provide hints to the user if requested. They will be interrogated one-by-one until either a
	 * hint is supplied, or there are no more strategies left. Each class must complete the abstract
	 * class {@link SuDoku.Strategy}.
	 * <P><B>Note:<B> The number of bands (rows of boxes), multiplied by the number of stacks
	 * (columns of boxes) must equal the number of values in the valueSet. E.g. a 3x3 SuDoku implies
	 * 9 possible values; a 4x2 implies 8 possible values.
	 * @param nBands Number of bands.
	 * @param nStacks Number of stacks.
	 * @param valueSet A complete set of the possible Objects that may be included in the puzzle.
	 * @param puzzle Array of objects representing the givens. A null object => a blank cell. 
	 * @param strategies Class names of strategies to be applied, in the order they are to be applied.
	 */
	public SuDoku(int nBands, int nStacks, Set<? extends Object> valueSet, Object[][] puzzle, String[] strategies)
	{
		this.nBands = nBands;
		this.nStacks = nStacks;
		this.nValues = nBands * nStacks;
		this.valueSet = new HashSet<Object>(valueSet);
		this.puzzle = new Object[nValues][nValues];
		for (int r = 0; r < nValues; r++)
		{
			for (int c = 0; c < nValues; c++)
			{
				this.puzzle[r][c] = puzzle[r][c];
			}
		}
		initRows();
		initColumns();
		initBoxes();	
		initCells();
		initStrategies(strategies);
	}

	private void initCells() 
	{
		cells = new Cell[nValues][nValues];
		int r,c;		
		Row row;
		Column column;
		Box box;
		for (r = 0; r < nValues; r++)
		{
			row = rows[r];
			for (c = 0; c < nValues; c++)
			{
				column = columns[c];
				box = boxes[r/nStacks][c/nBands];
				cells[r][c] = new Cell(row, column, box, this);
				row.addCell(cells[r][c]);
				column.addCell(cells[r][c]);
				box.addCell(cells[r][c]);
				if (puzzle[r][c] != null) cells[r][c].setValue(puzzle[r][c]);
			}
		}
	}
	private void initBoxes() 
	{
		boxes = new Box[nBands][nStacks];
		int b,s;
		for (b = 0; b < nBands; b++)
		{
			for (s = 0; s < nStacks; s++)
			{
				boxes[b][s] = new Box(b, s);
			}
		}
	}
	private void initColumns() 
	{
		columns = new Column[nValues];
		int c;
		for (c = 0; c < nValues; c++)
		{
			columns[c] = new Column(c);
		}
	}
	private void initRows() 
	{
		rows = new Row[nValues];
		int r;
		for (r = 0; r < nValues; r++)
		{
			rows[r] = new Row(r);
		}
	}
	private void initStrategies(String[] strategies)
	{
		this.strategies = new Strategy[strategies.length];
		for (int i = 0; i < strategies.length; i++)
		{
			try {
				this.strategies[i] = (Strategy)Class.forName(strategies[i]).newInstance();
				this.strategies[i].setPuzzle(this);
			} catch (Exception e) {
				System.err.println("Unable to load strategy class: " + strategies[i] + "(" + e.getMessage() + ")");
			}
		}
	}

	/*
	 * Check the solutions to the puzzle. 
	 * @return One of ({@link #NO_SOLUTION}, {@link #SINGLE_SOLUTION}, {@link #MANY_SOLUTIONS})
	 */
	public int checkSolutions()
	{
		SuDokuProblem problem;
		int nSolutions = 1;

		problem = new SuDokuProblem(nBands, nStacks, valueSet, puzzle);
		nSolutions = problem.solve();
		if (nSolutions == 0) {
			return NO_SOLUTION;
		} else if (nSolutions > 1) {
			return MANY_SOLUTIONS;
		} else {
			solution = problem.getSolution();
			return SINGLE_SOLUTION;
		}
	}
	/**
	 * Returns the required cell to the client caller.
	 */
	public Cell getCell(int row, int col)
	{
		return cells[row][col];
	}
	/*
	 * Clears all user entered information from the puzzle and returns to just the givens.
	 */
	public void reset()
	{
		int r,c;
		for (r = 0; r < nValues; r++)
		{
			for (c = 0; c < nValues; c++)
			{
				cells[r][c].reset();
				if (puzzle[r][c] != null) cells[r][c].setValue(puzzle[r][c]);
			}
		}
	}
	/**
	 * Returns a hint, directed to the user, as to what they may do next.
	 * The supplied strategies will be checked in turn for a hint, and that hint will be returned.
	 * It is possible that no strategy can provide a hint, in which case a null is returned. 
	 * <P><B>Note:<B> The user entered values will be checked against the solution first,
	 * and a hint returned if there are any errors. 
	 */
	public Hint getHint()
	{
		Hint hint = checkPuzzle();
		for (int i = 0; hint == null && i < strategies.length; i++)
		{
			hint = strategies[i].getHint();
		}
		return hint;
	}
	/**
	 * Checks the puzzle against the solution, and returns a hint if there are errors, or a null if not.
	 * This method is called automatically every time a hint is sought. It is provided 
	 * independently for the caller so that the user entered values may be checked against
	 * the solution independently, for instance, when the user has filled in all of the values.
	 */
	public Hint checkPuzzle()
	{
		int r,c;
		Object value;
		Hint hint = new Hint("BOO BOO");
		int nErrors = 0;
		
		for (r = 0; r < nValues; r++)
		{
			for (c = 0; c < nValues; c++)
			{
				value = cells[r][c].getValue();
				if (value != null) {
					if (! value.equals(solution[r][c])) 
					{
						hint.addElement(cells[r][c]);
						nErrors++;
					}
				} else if (cells[r][c].getCrossedOffCandidates().contains(solution[r][c])) {
					hint.addElement(cells[r][c]);
					nErrors++;
				}
			}
		}
		if (nErrors > 0) {
			hint.addText("The cells shown contain invalid values or mark up");
			return hint;
		} else {
			return null;
		}
	}


	int getNValues() { return nValues; }
	Set<Object> getValueSet() { return valueSet; }		

	/**
	 * Provided as a parent class of any strategies for providing hints.
	 * This nested class provides restricted access to the elements of the puzzle to
	 * any implementing class. The implementing class must complete the abstract class
	 * by providing the code to return a {@link Hint} to the user (or null if no hint can be provided).
	 */
	public static abstract class Strategy
	{	
		private SuDoku puzzle;
				
		/**
		 * A {@link Hint} should be constructed and returned by the implementing strategy class.
		 */
		public abstract Hint getHint();
		/**
		 * Returns the number of bands of the puzzle.
		 */
		protected int getNBands()
		{
			return puzzle.nBands;
		}
		/**
		 * Returns the number of stacks of the puzzle.
		 */
		protected int getNStacks()
		{
			return puzzle.nStacks;
		}
		/**
		 * Returns the number of values in the value set.
		 */
		protected int getNValues()
		{
			return puzzle.nValues;
		}
		/**
		 * Returns the set of values used in the puzzle.
		 */
		protected Set<Object> getValueSet()
		{
			return puzzle.getValueSet();
		}
		/**
		 * Returns the cell at the givel row and column.
		 */
		protected Cell getCell(int row, int col)
		{
			return puzzle.cells[row][col];
		}
		/**
		 * Returns the cell group representing the box at the given row (band) and column (stack).
		 */
		protected CellGroup getBox(int row, int col)
		{
			return puzzle.boxes[row][col];
		}
		/**
		 * Returns the cell group representing the row at the given ID.
		 */
		protected CellGroup getRow(int row)
		{
			return puzzle.rows[row];
		}
		/**
		 * Returns the cell group representing the column at the given ID.
		 */
		protected CellGroup getColumn(int col)
		{
			return puzzle.columns[col];
		}
		private void setPuzzle(SuDoku puzzle)
		{
			this.puzzle = puzzle;
		}
	}
}
