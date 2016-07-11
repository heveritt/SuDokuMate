package sudoku.strategy;

import java.util.*;
import sudoku.*;

/**
 * This strategy looks for hidden singles, and returns a {@link sudoku.Hint} if any are found.
 * A hidden single occurs when a particular value may only placed in one cell of a row, column or box.
 */
public class HiddenSingleStrategy extends SuDoku.Strategy 
{	
	/**
	 * Each box, row and column are checked in turn (in that order), and a hint returned if a hidden single is found in
	 * any of them, or null if not.
	 */
	public Hint getHint() 
	{
		Hint hint = null;
		hint = checkBoxes();
		if (hint != null) return hint;
		hint = checkRows();
		if (hint != null) return hint;
		hint = checkColumns();
		return hint;
	}
	
	private Hint checkBoxes()
	{
		Hint hint;
		int nBands = getNBands();
		int nStacks = getNStacks();
		for (int r = 0; r < nBands; r++)
		{
			for (int c = 0; c < nStacks; c++)
			{
				hint = checkCellGroup(getBox(r,c));
				if (hint != null) return hint;
			}
		}
		return null;
	}
	private Hint checkRows()
	{
		Hint hint;
		for (int r = 0; r < getNValues(); r++)
		{
			hint = checkCellGroup(getRow(r));
			if (hint != null) return hint;
		}
		return null;
	}
	private Hint checkColumns()
	{
		Hint hint;
		for (int c = 0; c < getNValues(); c++)
		{
			hint = checkCellGroup(getColumn(c));
			if (hint != null) return hint;
		}
		return null;
	}
	private Hint checkCellGroup(CellGroup cellGroup)
	{
		Cell cell;
		HashSet<Object> set = new HashSet<Object>(getValueSet());
		set.removeAll(cellGroup.getValues());
		for (Object value : set)
		{
			cell = checkForHiddenSingle(cellGroup, value);
			if (cell != null) return constructHint(value, cell, cellGroup);
		}
		return null;
	}
	private Cell checkForHiddenSingle(CellGroup cellGroup, Object value)
	{
		Cell possibleCell = null;
		for(Cell cell : cellGroup)
		{
			if (cell.getRemainingCandidates().contains(value))
			{
				if (possibleCell == null)
				{
					possibleCell = cell;	// First time we have this value
				} else {
					return null;			// Found in more than one cell - abort
				}
			}
		}
		return possibleCell;
	}
	
	private Hint constructHint(Object value, Cell cell, CellGroup cellGroup)
	{
		Hint hint = new Hint("HIDDEN SINGLE");
		hint.addText("Within this cell group, only one cell can contain the value ");
		hint.addText(value.toString());
		hint.addElement(cellGroup);
		hint.addValue(value);
		hint.addElement(cell);
		return hint;
	}
}
