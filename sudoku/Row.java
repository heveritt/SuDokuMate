package sudoku;

/**
 * A representation of a row on the SuDoku grid.
 * This class adds little to its ancestor {@link CellGroup} except the row ID.
 */
class Row extends CellGroup 
{
	Row(int row)
	{
		super();
		index = new int[1];
		index[0] = row;
		type = Element.ROW;
	}
}
