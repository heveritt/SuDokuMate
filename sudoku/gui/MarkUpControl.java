package sudoku.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import sudoku.*;
/**
 * Toggles mark up on/off, and when on, allows the user to cros off candidates.
 * When selected, mark-up will appear in the empty board cells showing what symbols are possible in those cells.
 * For the currently selected cell, the mark up control also allows the crossing off of candidates. These crossed
 * off candidates will be considered when hints are given.
 */
class MarkUpControl extends JPanel implements ActionListener 
{
	private JToggleButton onOffButton;
	private SymbolSelector symbolSelector;
	private Board board;
	private BoardCell cell;
	
	MarkUpControl(Board board, BoardCell cell)
	{
		super();
		this.board = board;
		this.cell = cell;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(SuDokuMate.GROUP_BORDER);
		this.setBackground(SuDokuMate.BACKGROUND);		
		this.add(initOnOffButton());
		this.add(Box.createRigidArea(new Dimension(0,5)));
		symbolSelector = new SymbolSelector(board.getSymbolSet());
		symbolSelector.addActionListener(this);
		symbolSelector.setToolTipText("Click to cross off");
		symbolSelector.setVisible(false);
		symbolSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(symbolSelector);
	}

	/**
	 * Allows the mark-up control to be enabled and disabled.
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
	 * Responds to the mark-up on/off button by showing/hiding the {@link SymbolSelector}; also supports
	 * the crossing off and reinstating of candidates by responding to the {@link SymbolSelector} buttons.
	 */
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == onOffButton)
		{
			symbolSelector.setVisible(onOffButton.isSelected());
			refresh();
			board.actionToggleMarkUp();
		} else if (e.getSource() instanceof SymbolSelector.Button) {
			SymbolSelector.Button button = (SymbolSelector.Button)e.getSource();
			SymbolSet.Symbol symbol = button.getSymbol();
			if (button.isSelected())
			{
				cell.getModel().crossOffCandidate(symbol);
				cell.repaint();
			} else {
				cell.getModel().reinstateCandidate(symbol);
				cell.repaint();
			}
		}
	}
	
	boolean isSelected()
	{
		return onOffButton.isSelected();
	}
	void setCurrentCell(BoardCell cell)
	{
		this.cell = cell;
		refresh();
	}
	void refresh()
	{
		if (onOffButton.isSelected())
		{
			Cell model = cell.getModel();
			if (model != null && model.getValue() == null)
			{
				symbolSelector.setEnabled(model.getCandidates());
				symbolSelector.setSelected(model.getCrossedOffCandidates());
			} else {
				symbolSelector.setSelected(false);
				symbolSelector.setEnabled(false);
			}
		}
	}
	
	private JToggleButton initOnOffButton()
	{
		onOffButton = new JToggleButton("Mark");
		onOffButton.setMnemonic('M');
		onOffButton.setToolTipText("Show mark up and allow candidates to be crossed off");
		onOffButton.addActionListener(this);
		onOffButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension dimension = new Dimension(onOffButton.getPreferredSize());
		dimension.width = Integer.MAX_VALUE;
		onOffButton.setMaximumSize(dimension);
		return onOffButton;
	}
}
