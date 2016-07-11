package sudoku.gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import sudoku.*;

/**
 * The visual representation of the SuDoku grid.
 * The grid is a panel with the SuDoku boxes as child panels on a grid layout and the cells
 * child text fields on a grid layout within those boxes. The board also "owns" the 
 * {@link SymbolFilter} and {@link MarkUpControl} which support the filtering of the SuDoku and
 * the implementation of mark up (including the ability to cross off candidates) respectively.
 * Finally, the board supports the presentation of a {@link sudoku.Hint} by using background highlighting 
 * of the rows, columns, boxes and cells concerned.
 */
class Board extends JPanel 
{		
	static final Border NORMAL_BORDER = BorderFactory.createLineBorder(SuDokuMate.BACKGROUND);
	static final Border HIGHLIGHT_BORDER = BorderFactory.createLineBorder(SuDokuMate.FOREGROUND);
	static final Color NORMAL_BACKGROUND = Color.WHITE;
	static final Font FONT = new Font("Times New Roman", Font.PLAIN, 36);
	static final Color PRINT_FOREGROUND = Color.BLACK;
	static final Color PEN_FOREGROUND = Color.BLUE;
	static final Color PENCIL_FOREGROUND = Color.GRAY;
	static final Color DIM_FOREGROUND = Color.LIGHT_GRAY;
	static final Color CELLGROUP_HIGHLIGHT = new Color(0.8F, 0.8F, 0.8F); // Very light gray
	static final Color CELL_HIGHLIGHT = new Color(1.0F, 0.8F, 0.8F);      // Very light pink

	private SuDokuMate owner;
	private BoardCell[][] cells;
	private BoardCell currentCell;
	private SymbolSet symbolSet;
	private SymbolFilter symbolFilter;
	private MarkUpControl markUpControl;
	private int nBands;
	private int nStacks;
	private int nValues;
	
	private Hint hint;
	private Set<BoardCell> hintCells = null;
	private Set<BoardCell> hintGroupCells = null;

	Board(SuDokuMate owner, int nBands, int nStacks)
	{
		super();
		this.setLayout(new GridLayout(nBands, nStacks));
		this.setBorder(SuDokuMate.GROUP_BORDER);
		this.setBackground(SuDokuMate.BACKGROUND);
		this.owner = owner;
		this.nBands = nBands;
		this.nStacks = nStacks;
		nValues = nBands * nStacks;
		symbolSet = new SymbolSet(nValues);
		
		initCells();
		initBoxes();	
		initKeyBindings();
		
		currentCell = cells[0][0];
		symbolFilter = new SymbolFilter(this);
		symbolFilter.setEnabled(false);
		markUpControl = new MarkUpControl(this, currentCell);
		markUpControl.setEnabled(false);
	}

	/**
	 * Overrides the parent method, propagating it to each cell.
	 */
	public void setEnabled(boolean state)
	{
		int r,c;
		for (r = 0; r < nValues; r++)
		{
			for (c = 0; c < nValues; c++)
			{
				cells[r][c].setEnabled(state);
			}
		}
	}
	
	SymbolSet.Symbol[][] getSymbols()
	{
		SymbolSet.Symbol[][] symbols = new SymbolSet.Symbol[nValues][nValues];
		int r,c;
		for (r = 0; r < nValues; r++)
		{
			for (c = 0; c < nValues; c++)
			{
				symbols[r][c] = cells[r][c].getDisplayedSymbol();
			}
		}
		return symbols;
	}
	SymbolSet getSymbolSet()
	{
		return symbolSet;
	}
	JPanel getSymbolFilter()
	{
		return symbolFilter;
	}
	JPanel getMarkUpControl()
	{
		return markUpControl;
	}
	void clear()
	{
		clearHint();
		int r,c;
		for (r = 0; r < nValues; r++)
		{
			for (c = 0; c < nValues; c++)
			{
				cells[r][c].clear();
			}
		}
		symbolFilter.setEnabled(false);
		markUpControl.setEnabled(false);
	}
	void setModel(SuDoku puzzle)
	{
		int r,c;
		for (r = 0; r < nValues; r++)
		{
			for (c = 0; c < nValues; c++)
			{
				cells[r][c].setModel(puzzle.getCell(r,c));
			}
		}
		symbolFilter.setEnabled(true);
		markUpControl.setEnabled(true);
	}
	void actionToggleMarkUp() 
	{
		redisplay(); 
		focusOnCell(currentCell);
	}
	void actionFilterChange()
	{
		redisplay(); 
		focusOnCell(currentCell);
	}
	void actionCellChange(BoardCell cell) 
	{
		markUpControl.refresh();
		redisplay();
		if (isComplete()) owner.actionBoardComplete();
	}


	void redisplay()
	{
		clearHint();
		int r,c;
		for (r = 0; r < nValues; r++)
		{
			for (c = 0; c < nValues; c++)
			{
				cells[r][c].redisplay();
			}
		}
		owner.actionBoardRedisplay();
	}
	void showHint(Hint hint)
	{
		this.hint = hint;
		
		hintCells = new HashSet<BoardCell>();
		hintGroupCells = new HashSet<BoardCell>();
		BoardCell cell = null;
		int type, id[];
		for (Element element : hint.getElements())
		{
			type = element.getType();
			id = element.getIndex();
			if 		  (type == Element.BOX) {
				addBoxCellsToSet(id, hintGroupCells );
			} else if (type == Element.ROW) {
				addRowCellsToSet(id[0], hintGroupCells );
			} else if (type == Element.COLUMN) {
				addColCellsToSet(id[0], hintGroupCells);
			} else if (element.getType() == Element.CELL) {
				cell = cells[id[0]][id[1]];
				hintCells.add(cell);
				hintGroupCells.remove(cell);
			}
		}
		setCellBackground(hintGroupCells, CELLGROUP_HIGHLIGHT);
		setCellBackground(hintCells, CELL_HIGHLIGHT);
		if (cell != null) focusOnCell(cell);
	}
	void clearHint()
	{
		if (hint != null)
		{
			setCellBackground(hintGroupCells, NORMAL_BACKGROUND);
			setCellBackground(hintCells, NORMAL_BACKGROUND);
			hint = null;
			hintCells = null;
			hintGroupCells = null;
		}
	}
	boolean isMarkedUp()
	{
		return markUpControl.isSelected();
	}
	boolean isFiltered()
	{
		return (symbolFilter.isSelected() && ! getFilter().isEmpty());
	}
	Set<SymbolSet.Symbol> getFilter()
	{
		if (hint == null)
		{
			return symbolFilter.getFilter();
		} else {
			return symbolSet.getSubSetMatching(hint.getValues());
		}
	}
	
	private boolean isComplete()
	{
		boolean isComplete = true;
		int r,c;
		for (r = 0; r < nValues; r++)
		{
			for (c = 0; c < nValues; c++)
			{
				if (cells[r][c].getValue() == null) isComplete = false;
			}
		}
		return isComplete;
	}
	private void initCells() 
	{
		cells = new BoardCell[nValues][nValues];
		int r,c;		
		for (r = 0; r < nValues; r++)
		{
			for (c = 0; c < nValues; c++)
			{
				int[] b = {r/nStacks, c/nBands};
				cells[r][c] = new BoardCell(r,c,b,this);
			}
		}
	}
	private void initBoxes() 
	{
		JPanel[][] boxes = new JPanel[nBands][nStacks];
		int r,c,b[];
		for (r = 0; r < nBands; r++)
		{
			for (c = 0; c < nStacks; c++)
			{
				boxes[r][c] = new JPanel(new GridLayout(nStacks, nBands));
				boxes[r][c].setBorder(NORMAL_BORDER);
				boxes[r][c].setBackground(SuDokuMate.BACKGROUND);
				this.add(boxes[r][c]);
			}
		}
		for (r = 0; r < nValues; r++)
		{
			for (c = 0; c < nValues; c++)
			{
				b = cells[r][c].getBoxID();
				boxes[b[0]][b[1]].add(cells[r][c]);
			}
		}
	}
	private void initKeyBindings() 
	{
		InputMap keyMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		keyMap.put(KeyStroke.getKeyStroke("UP"), "Move Up");
		keyMap.put(KeyStroke.getKeyStroke("KP_UP"), "Move Up");
		keyMap.put(KeyStroke.getKeyStroke("DOWN"), "Move Down");
		keyMap.put(KeyStroke.getKeyStroke("KP_DOWN"), "Move Down");
		keyMap.put(KeyStroke.getKeyStroke("LEFT"), "Move Left");
		keyMap.put(KeyStroke.getKeyStroke("KP_LEFT"), "Move Left");
		keyMap.put(KeyStroke.getKeyStroke("RIGHT"), "Move Right");
		keyMap.put(KeyStroke.getKeyStroke("KP_RIGHT"), "Move Right");
		
		ActionMap actionMap = getActionMap();
		actionMap.put("Move Up", new AbstractAction() {public void actionPerformed(ActionEvent e) {moveFocus(e, -1, 0);}});
		actionMap.put("Move Down", new AbstractAction() {public void actionPerformed(ActionEvent e) {moveFocus(e, 1, 0);}});
		actionMap.put("Move Left", new AbstractAction() {public void actionPerformed(ActionEvent e) {moveFocus(e, 0, -1);}});
		actionMap.put("Move Right", new AbstractAction() {public void actionPerformed(ActionEvent e) {moveFocus(e, 0, 1);}});
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(
			    new PropertyChangeListener() {public void propertyChange(PropertyChangeEvent e) {setFocusedCell(e);}});

	}	
	private void setFocusedCell(PropertyChangeEvent e)
	{
        if (e.getPropertyName().equals("focusOwner"))
        {
            if ((e.getNewValue() != null) && ((e.getNewValue()) instanceof BoardCell)) 
	        {
  	        	setCurrentCell( (BoardCell)e.getNewValue() );
	        }
        }
	}
	private void moveFocus(ActionEvent e, int dRow, int dColumn)
	{
		if (currentCell != null)
		{
			int row = currentCell.getRowID();
			int column = currentCell.getColID();
			int newRow = (row + dRow + nValues) % nValues;
			int newColumn = (column + dColumn + nValues) % nValues;
			focusOnCell(cells[newRow][newColumn]);
		}
	}
	private void focusOnCell(BoardCell cell)
	{
		cell.requestFocusInWindow();
	}
	private void setCurrentCell(BoardCell cell)
	{
     	if (cell != currentCell)
     	{
     		currentCell.setBorder(NORMAL_BORDER);
     		currentCell = cell;
     		currentCell.setBorder(HIGHLIGHT_BORDER);
     	}
		markUpControl.setCurrentCell(currentCell);
	}

	private void addRowCellsToSet(int rowID, Set<BoardCell> set)
	{
		for (int c = 0; c < nValues; c++)
		{
			set.add(cells[rowID][c]);
		}
	}
	private void addColCellsToSet(int colID, Set<BoardCell> set)
	{
		for (int r = 0; r < nValues; r++)
		{
			set.add(cells[r][colID]);
		}
	}
	private void addBoxCellsToSet(int[] boxID, Set<BoardCell> set)
	{
		int r,c;		
		int rowStart = boxID[0] * nStacks;
		int colStart = boxID[1] * nBands;
		for (r = 0; r < nStacks; r++)
		{
			for (c = 0; c < nBands; c++)
			{
				set.add(cells[rowStart+r][colStart+c]);
			}
		}
	}
	private void setCellBackground(Collection<BoardCell> cells, Color color)
	{
		for(BoardCell cell : cells) cell.setBackground(color);
	}
}
