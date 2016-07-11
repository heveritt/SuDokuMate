package sudoku;

import java.util.*;

/**
 * Provides a communication medium for hints produced by SuDoku strategies.
 * Hints are the result of applying an implementation of a {@link SuDoku.Strategy}
 * to the SuDoku as it currently stands. They encapsulate all that may be required to be 
 * communicated to the user for any type of hint: text, the {@link Element}s and the
 * values involved.
 */
public class Hint 
{
	private StringBuffer text;
	private List<Element> elements;
	private Set<Object> values;

	/**
	 * Creates an empty hint, labelled with the strategy name.
	 */
	public Hint (String strategyName)
	{
		this.elements = new ArrayList<Element>();
		this.values = new HashSet<Object>();
		this.text = new StringBuffer(strategyName + ": ");
	}
	
	/**
	 * Appends text to the hint. The text will always start with the strategy name
	 * supplied on initialisation followed by a colon and space. If multiple text items are
	 * appended, it is the caller's responsibility to add any necessary spaces or
	 * punctuation. Hint text should generally not refer to the elements or values
	 * involved directly (e.g. cell [1,2] or the value "3"), but rather in a more
	 * general sense (e.g. the cells shown, or the values indicated). 
	 */
	public void addText(String text)
	{
		this.text.append(text);
	}
	/**
	 * Adds a value to the hint. Should the hint refer to a specific value or values,
	 * they should be added to it using this method.
	 */
	public void addValue(Object value)
	{
		values.add(value);
	}
	/**
	 * Adds a cell group to the hint. Should the hint refer to a specific cell group (or groups),
	 * they should be added to it using this method.
	 */
	public void addElement(CellGroup cellGroup)
	{
		elements.add(cellGroup);
	}	
	/**
	 * Adds a cell to the hint. Should the hint refer to a specific cell (or cells),
	 * they should be added to it using this method.
	 */
	public void addElement(Cell cell)
	{
		elements.add(cell);
	}

	/**
	 * Returns the text of the hint. The text will always start with the strategy name
	 * supplied on initialisation followed by a colon and space. 
	 */
	public String toString() { return text.toString(); }
	/**
	 * Returns the elements of the hint, so that they may be indicated to the user. 
	 */
	public Collection<Element> getElements() { return elements; }
	/**
	 * Returns the values involved in the hint, so that they may be indicated to the user. 
	 */
	public Set<Object> getValues() { return values; }
}
