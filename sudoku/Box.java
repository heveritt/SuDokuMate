package sudoku;

/**
 * A representation of a box on the SuDoku grid.
 * This class adds little to its ancestor {@link CellGroup} except the box identity expressed
 * in terms of its band (row) and stack (column).
 */
class Box extends CellGroup 
{
	Box(int row, int col) 
	{
		super();
		index = new int[2];
		index[0] = row;
		index[1] = col;
		type = Element.BOX;
	}
}
