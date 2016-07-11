package sudoku;

import java.util.*;

/**
 * A group of cells on the SuDoku grid; a row, column or box.
 * Each cell group will be an instance of ({@link Row}, {@link Column} or {@link Box}). 
 * It contains a collection of the cells in tht group, and is identified by its sub-class type and index.
 */
public class CellGroup implements Element, Iterable<Cell>
{	
	protected int[] index;
	protected int type;
	
	private List<Cell> cells;
	
	CellGroup()
	{
		cells = new ArrayList<Cell>();
	}
	/**
	 * Returns an index specific to the row, colomn or box represented.
	 */	
	public int[] getIndex() 
	{
		return index;
	}
	/**
	 * Returns one of <B>ROW<B>, <B>COLUMN<B> or <B>BOX<B>.
	 */
	public int getType()
	{
		return type;
	}
	/**
	 * Returns an iterator for the cells in the group.
	 */
	public Iterator<Cell> iterator()
	{
		return cells.iterator();
	}
	/**
	 * Returns a set containing the values already used in this cell group.
	 */
	public Set<Object> getValues()
	{
		Cell cell;
		HashSet<Object> valueSet = new HashSet<Object>(cells.size());
		for (Iterator<Cell> iter = cells.iterator(); iter.hasNext(); )
		{
			cell = (Cell)iter.next();
			if (cell.getValue() != null) valueSet.add(cell.getValue());
		}
		return valueSet;
	}

	void addCell(Cell cell)
	{
		cells.add(cell);
	}
}