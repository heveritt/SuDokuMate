package sudoku;

/**
 * A representation of a column on the SuDoku grid.
 * This class adds little to its ancestor {@link CellGroup} except the column ID.
 */
class Column extends CellGroup 
{
	Column(int col)
	{
		super();
		index = new int[1];
		index[0] = col;
		type = Element.COLUMN;
	}
}