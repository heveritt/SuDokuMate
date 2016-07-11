package sudoku;

/**
 * Implemented by an element of a {@link Hint}.
 * Each of the elements of the SuDoku referred to in any hints that are given to the user
 * will implement this interface. 
 */
public interface Element 
{
	public static final int CELL   = 1;
	public static final int ROW    = 2;
	public static final int COLUMN = 3;
	public static final int BOX    = 4;
	
	/**
	 * Returns the type of the element, one of the constants defined within this interface.
	 */
	public int getType();
	/**
	 * Returns an index specific to each type of the element. 
	 * e.g. the row ID for a {@link Row}, or the row and column of a {@link Cell}.
	 */
	public int[] getIndex();
}
