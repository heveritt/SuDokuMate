package sudoku.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * Allows one or more symbols to be selected for filtering on the SuDoku grid.
 * If a filter of one or more symbols has been selected, all other symbols on the SuDoku grid will be faded
 * into the background, leaving those selected to stand out. If no symbol has been selected, then the filter 
 * behaves as if there were no filter being applied, thus all symbols appear without fading.
 */
class SymbolFilter extends JPanel implements ActionListener 
{
	private JToggleButton onOffButton;
	private SymbolSelector symbolSelector;
	private Board board;
	
	SymbolFilter(Board board)
	{
		super();
		this.board = board;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(SuDokuMate.GROUP_BORDER);
		this.setBackground(SuDokuMate.BACKGROUND);		
		this.add(initOnOffButton());
		this.add(Box.createRigidArea(new Dimension(0,5)));
		symbolSelector = new SymbolSelector(board.getSymbolSet());
		symbolSelector.addActionListener(this);
		symbolSelector.setToolTipText("Filter");
		symbolSelector.setVisible(false);
		symbolSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(symbolSelector);
	}

	/**
	 * Allows filter to be enabled and disabled.
	 * When disabled, the {@link SymbolFilter} is hidden.
	 */
	public void setEnabled(boolean state)
	{
		if (this.isEnabled() && ! state)
		{
			symbolSelector.setVisible(false);
			onOffButton.setSelected(false);
		}
		onOffButton.setEnabled(state);
		super.setEnabled(state);
	}
	/**
	 * Responds to the filter on/off button by showing/hiding the {@link SymbolSelector}.
	 */
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == onOffButton)
		{
			symbolSelector.setVisible(onOffButton.isSelected());
		}
		board.actionFilterChange();
	}

	boolean isSelected()
	{
		return onOffButton.isSelected();
	}
	Set<SymbolSet.Symbol> getFilter()
	{
		return symbolSelector.getSelectedSet();
	}
	void highlight(Set<SymbolSet.Symbol> symbolSet)
	{
		symbolSelector.setSelected(symbolSet);
	}
	
	private JToggleButton initOnOffButton()
	{
		onOffButton = new JToggleButton("Filter");
		onOffButton.setMnemonic('F');
		onOffButton.setToolTipText("Filter the board to show specific symbols");
		onOffButton.addActionListener(this);
		onOffButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension dimension = new Dimension(onOffButton.getPreferredSize());
		dimension.width = Integer.MAX_VALUE;
		onOffButton.setMaximumSize(dimension);
		return onOffButton;
	}


}
