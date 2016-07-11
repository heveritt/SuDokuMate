package sudoku;

import java.util.*;

/**
 * A representation of a cell on the SuDoku grid.
 * Each cell on the grid has a reference to the cell groups ({@link Row}, {@link Column} & {@link Box}) 
 * which contain it. It is indexed by its row and column within the grid. The cell keeps track of any 
 * entered value and provides methods to support mark-up of possible candidates.
 */
public class Cell implements Element
{
	private Row row;
	private Column column;
	private Box box;
	private Object value;
	private SuDoku suDoku;
	private Set<Object> crossedOffCandidates;
	private int[] index = new int[2];
	
	Cell(Row row, Column column, Box box, SuDoku suDoku)
	{
		this.value = null;
		this.row = row;
		this.index[0] = row.getIndex()[0];
		this.column = column;
		this.index[1] = column.getIndex()[0];
		this.box = box;
		this.suDoku = suDoku;
		this.crossedOffCandidates = new HashSet<Object>(suDoku.getNValues());
	}
	
	/**
	 * Returns the row and column index of this cell.
	 */
	public int[] getIndex() 
	{
		return index;
	}
	/**
	 * Returns an element type of <B>CELL<B>.
	 */
	public int getType()
	{
		return Element.CELL;
	}
	/**
	 * Clears any value and mark-up associated with the cell.
	 */
	public void reset()
	{
		this.value = null;
		crossedOffCandidates.clear();
	}
	/**
	 * Sets the cell value.
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
	/**
	 * Erases the cell value.
	 */
	public void eraseValue()
	{
		this.value = null;
	}
	/**
	 * Returns the cell value.
	 */
	public Object getValue()
	{
		return value;
	}
	/**
	 * Crosses off the given candidate from the cell's mark-up.
	 */
	public void crossOffCandidate(Object value) 
	{
		crossedOffCandidates.add(value);
	}
	/**
	 * Re-instates a previously crossed off candidate to the cell's mark-up.
	 */
	public void reinstateCandidate(Object value) 
	{
		crossedOffCandidates.remove(value);
	}
	/**
	 * Returns the set of candidates that have been crossed off.
	 */
	public Set<Object> getCrossedOffCandidates() 
	{
		return crossedOffCandidates;
	}
	/**
	 * Returns a set of all candidates not already taken by other cells within the same
	 * row, column or box. <B>Note:<B> Any crossed off candidates will be ignored.
	 * @see #getRemainingCandidates()
	 */
	public Set <Object>getCandidates() 
	{
		if (value == null) 
		{
			HashSet<Object> candidates = new HashSet<Object>(suDoku.getValueSet());
			candidates.removeAll(row.getValues());
			candidates.removeAll(column.getValues());
			candidates.removeAll(box.getValues());
			return candidates;
		} else {
			// If we've already filled in the value, that's our only candidate!
			return Collections.singleton(value);
		}
	}
	/**
	 * Returns a set of all remaining candidates after those that have been crossed off
	 * have been removed from the set of possible candidates.
	 * @see #getCandidates()
	 */
	public Set<Object> getRemainingCandidates()
	{
		Set<Object> candidates = getCandidates();
		candidates.removeAll(crossedOffCandidates);
		return candidates;
	}
}