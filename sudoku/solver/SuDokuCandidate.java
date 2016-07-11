package sudoku.solver;

/**
 * A SuDoku solution candidate will be a a combination of a row, column and value.
 * Considering a SuDoku as a constrained problem, the solution candidates to that SuDoku puzzle
 * are identifiable as the set of possibilities for putting a value in a cell. Thus on a traditional 
 * SuDoku we will have 9x9 cells, each of which can contain one of 9 values - 9x9x9 = 729
 * possible candidates, each of which may be identified by its row, column and value.
 */
public class SuDokuCandidate extends Candidate 
{
	private int row;
	private int column;
	private int value;
	
	SuDokuCandidate(int row, int column, int value)
	{
		super("Row: " + row + ", Col: " + column + ", Val: " + value);
		this.row = row;
		this.column = column;
		this.value = value;
	}
	
	int getRow()
	{
		return row;
	}
	
	int getColumn()
	{
		return column;
	}
	
	int getValue()
	{
		return value;
	}
}
