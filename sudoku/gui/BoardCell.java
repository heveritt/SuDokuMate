package sudoku.gui;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import sudoku.*;

/**
 * The visual representation of a cell on the SuDoku grid.
 * The cell is represented as a {@link javax.swing.JFormattedTextField} to make use of the
 * text input and validation facilities by this Swing component. 
 * <P>At first, when the givens are being entered for the SuDoku puzzle, the cell does little
 * more than its text field ancestor, however, once the puzzle has been established, each board cell
 * is linked to an underlying {@link sudoku.Cell}, which acts as its model.
 */
class BoardCell extends JFormattedTextField implements PropertyChangeListener 
{
	private Board board;
	private int rowID;
	private int colID;
	private int[] boxID;
	private Cell model;
	
	BoardCell(int rowID, int colID, int[] boxID, Board board) 
	{	
		super(board.getSymbolSet().getFormatter());
		this.rowID = rowID;
		this.colID = colID;
		this.boxID = boxID;
		this.board = board;
		this.model = null;
		this.setBorder(Board.NORMAL_BORDER);
		this.setFont(Board.FONT);
		this.setBackground(Board.NORMAL_BACKGROUND);
		this.setForeground(Board.PENCIL_FOREGROUND);
		this.setDisabledTextColor(Board.PRINT_FOREGROUND);
		setHorizontalAlignment(CENTER);
		setColumns(1);
		setOpaque(false);
		overrideKeyBindings();
		addPropertyChangeListener("value", this);
	}	

	/**
	 * Any changes in value are intercepted and propagated to the underlying {@link sudoku.Cell}.
	 */
	public void propertyChange(PropertyChangeEvent e) 
	{
		if (e.getPropertyName().equals("value"))
		{
			repaint();
			SymbolSet.Symbol displayedSymbol = getDisplayedSymbol();
			if (model != null && getModelSymbol() != displayedSymbol)
			{
				if (displayedSymbol == null) {
					model.eraseValue();
				} else {
					model.setValue(displayedSymbol);
				}
				board.actionCellChange(this);
			}
		}
	}

	/**
	 * The paintComponent method is overridden so that mark-up may be provided.
	 * After the givens have been entered, mark-up may be displayed in empty cells,
	 * should it have been turned on using the {@link MarkUpControl}. In this case, the mark-up
	 * is drawn as a sort of background to the cell which continues to allow text entry over
	 * the top of that mark-up.
	 */
	protected void paintComponent(Graphics g)
	{
		paintBackground(g);
		if (model != null)
		{
			if (getModelSymbol() == null)
			{
				if (board.isMarkedUp())
				{
					Graphics2D g2d = (Graphics2D)g.create(); 
					paintCandidates(g2d);
					g2d.dispose();
				}
			}
		}
		super.paintComponent(g);
	}
	
	/**
	 * The left and right arrow key bindings are overridden so that they may be used to move
	 * around the SuDoku grid.
	 */
	private void overrideKeyBindings() 
	{
		InputMap keyMap = getInputMap();
		keyMap.put(KeyStroke.getKeyStroke("LEFT"), "none");
		keyMap.put(KeyStroke.getKeyStroke("KP_LEFT"), "none");
		keyMap.put(KeyStroke.getKeyStroke("RIGHT"), "none");
		keyMap.put(KeyStroke.getKeyStroke("KP_RIGHT"), "none");
	}
	SymbolSet.Symbol getDisplayedSymbol()
	{
		return board.getSymbolSet().getSymbol((String)getValue());
	}
	SymbolSet.Symbol getModelSymbol()
	{
		if (model != null)
		{
			return (SymbolSet.Symbol)model.getValue();
		} else {
			return null;
		}
	}
	int getRowID()
	{
		return rowID;
	}
	int getColID()
	{
		return colID;
	}
	int[] getBoxID()
	{
		return boxID;
	}
	void clear()
	{
		model = null;
		setValue(null);
		setEnabled(true);
		this.setForeground(Board.PENCIL_FOREGROUND);
	}
	void setModel(Cell model)
	{
		this.model = model;
		SymbolSet.Symbol modelSymbol = getModelSymbol();
		if (modelSymbol != null)
		{
			setValue(modelSymbol.toString());
			setEnabled(false);
		} else {
			setValue(null);
			setForeground(Board.PEN_FOREGROUND);
		}
	}
	Cell getModel()
	{
		return model;
	}
	void redisplay()
	{
		if (model != null)
		{
			SymbolSet.Symbol modelSymbol = getModelSymbol();
			if (modelSymbol == null)
			{
				repaint();
			} else {
				if (board.isFiltered() && ! board.getFilter().contains(modelSymbol))
				{		
					setForeground(Board.DIM_FOREGROUND);
					setDisabledTextColor(Board.DIM_FOREGROUND);
				} else {
					setForeground(Board.PEN_FOREGROUND);
					setDisabledTextColor(Board.PRINT_FOREGROUND);
				}
			}
		}
	}
	private void paintBackground(Graphics g)
	{
		Color savedColor = g.getColor();
		g.setColor(this.getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(savedColor);
	}
	private void paintCandidates(Graphics2D g2d)
	{
		Set<Object> candidates = model.getCandidates();
		Set<Object> crossedOffSet = model.getCrossedOffCandidates();
		int nCandidates = candidates.size();
		if (nCandidates > 0)
		{
			// Break cell into a grid big enough for all the candidates (e.g. 2x2 for 4 candidates, 2x3 for 5 etc.)
			int nCols = (int)Math.ceil(Math.sqrt(nCandidates));
			int nRows = ( (nCandidates - 1) / nCols) + 1;
			Insets insets = getInsets();
			float dx = (float)(getWidth() - insets.left - insets.right) / (float)nCols;
			float dy = (float)(getHeight() - insets.top - insets.bottom) / (float)nRows;
			// Only display the mark up if there is at least 12 points square to display each symbol in.
			if (Math.min(dx, dy) >= 12.0F)
			{
				// Choose a font size that will fit in the sub-grid leaving reasonable spacing between symbols.
				float fontSize = Math.min( (Math.min(dx, dy) * 0.8F), 18.0F);
				int x, y, i = 0;
				// Display mark-up, highlighting if filter is on, and showing an "X" over those that have been crossed off.
				for (SymbolSet.Symbol symbol: board.getSymbolSet().getReadOnlySet())
				{
					if (candidates.contains(symbol))
					{
						applyFilterToMarkUp(g2d, symbol, fontSize);
						x = insets.left + (int)( (((float)(i % nCols) + 0.5F) * dx) - (fontSize / 3.0F) );
						y = insets.top + (int)( (((float)(i / nCols) + 0.5F) * dy) + (fontSize / 3.0F) );
						g2d.drawString(symbol.toString(), x, y);
						if (crossedOffSet.contains(symbol)) g2d.drawString("X", x, y);
						i++;
					}
		
				}
			}
		}
	}
	private void applyFilterToMarkUp(Graphics2D g2d, SymbolSet.Symbol symbol, float fontSize)
	{
		if (board.isFiltered()) {
			if (board.getFilter().contains(symbol)) {		
				g2d.setColor(Board.PRINT_FOREGROUND);
				g2d.setFont(getFont().deriveFont(Font.BOLD, fontSize));
			} else {
				g2d.setColor(Board.DIM_FOREGROUND);
				g2d.setFont(getFont().deriveFont(Font.PLAIN, fontSize));
			}
		} else {
			g2d.setColor(Board.PENCIL_FOREGROUND);
			g2d.setFont(getFont().deriveFont(Font.PLAIN, fontSize));
		}
	}

}
