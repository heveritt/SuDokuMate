package sudoku.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * Allows selection and other actions on one or more symbols.
 * Used by both the {@link SymbolFilter} and the {@link MarkUpControl}, the symbol selector allows manipulation
 * of a group of buttons representing the symbol set.
 */
class SymbolSelector extends JPanel 
{
	private Button[] selectorButtons;
	private SymbolSet.Symbol[] symbols;
	
	SymbolSelector(SymbolSet symbolSet)
	{
		super();
		this.symbols = symbolSet.getSymbols();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBackground(SuDokuMate.BACKGROUND);		
		selectorButtons = new Button[symbols.length];
		for (int i = 0; i < symbols.length; i++)
		{
			selectorButtons[i] = new Button(symbols[i]);
			this.add(selectorButtons[i]);
		}
	}	
	/**
	 * A button for the selection of a particular symbol within the {@link SymbolSet}.
	 */
	class Button extends JToggleButton
	{
		SymbolSet.Symbol symbol;
		
		private Button(SymbolSet.Symbol symbol)
		{
			super();
			this.symbol = symbol;
			this.setText(symbol.toString());
			this.setBackground(symbol.getColor());
			this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		}
		
		SymbolSet.Symbol getSymbol()
		{
			return symbol;
		}
	}

	/**
	 * Sets all buttons to the same enabled/disabled state.
	 */
	public void setEnabled(boolean state)
	{
		for (int i = 0; i < symbols.length; i++)
		{
			selectorButtons[i].setEnabled(state);
		}
	}
	/**
	 * Sets all buttons to the same selected/unselected state.
	 */
	public void setSelected(boolean state)
	{
		for (int i = 0; i < symbols.length; i++)
		{
			selectorButtons[i].setSelected(state);
		}
	}
	/**
	 * Sets the tool tip text for all buttons as per the input, suffixed by symbol "x".
	 */
	public void setToolTipText(String text)
	{
		for (int i = 0; i < symbols.length; i++)
		{
			selectorButtons[i].setToolTipText(text + " symbol \"" + symbols[i].toString() + "\"");
		}
	}
	/**
	 * Adds an action listener to all buttons.
	 */
	public void addActionListener(ActionListener actionListener)
	{
		for (int i = 0; i < symbols.length; i++)
		{
			selectorButtons[i].addActionListener(actionListener);
		}
	}
	
	void setEnabled(Set<?> set)
	{
		for (int i = 0; i < symbols.length; i++)
		{
			selectorButtons[i].setEnabled(set.contains(symbols[i]));
		}
	}
	void setSelected(Set<?> set)
	{
		for (int i = 0; i < symbols.length; i++)
		{
			selectorButtons[i].setSelected(set.contains(symbols[i]));
		}
	}
	Set<SymbolSet.Symbol> getSelectedSet()
	{
		HashSet<SymbolSet.Symbol> selectedSet = new HashSet<SymbolSet.Symbol>(symbols.length);
		for (int i = 0; i < symbols.length; i++)
		{
			if (selectorButtons[i].isSelected()) selectedSet.add(symbols[i]);
		}
		return selectedSet;
	}
}
