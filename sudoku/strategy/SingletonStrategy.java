package sudoku.strategy;

import java.util.*;
import sudoku.*;
/**
 * This strategy looks for singletons, and returns a {@link sudoku.Hint} if any are found.
 * A singleton occurs when a particular cell may only contain one value.
 */
public class SingletonStrategy extends SuDoku.Strategy 
{	
	/**
	 * Each cell is checked in turn for a singleton, and a hint returned if one is found, or null otherwise.
	 */
	public Hint getHint() 
	{
		int r,c;
		Object value;
		for (r = 0; r < getNValues(); r++)
		{
			for (c = 0; c < getNValues(); c++)
			{
				value = checkForSingleton(getCell(r,c));
				if (value != null) return constructHint(value, getCell(r,c));
			}
		}
		return null;
	}
	
	private Object checkForSingleton(Cell cell)
	{
		if (cell.getValue() == null)
		{
			Set<Object> candidates = cell.getRemainingCandidates();
			if (candidates.size() == 1) 
			{
				return (Object)candidates.iterator().next();
			}
		}
		return null;
	}
	private Hint constructHint(Object value, Cell cell)
	{
		Hint hint = new Hint("SINGLETON");
		hint.addText("The cell highlighted can only contain the value ");
		hint.addText(value.toString());
		hint.addElement(cell);
		hint.addValue(value);
		return hint;
	}
}
