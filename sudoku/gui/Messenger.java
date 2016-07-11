package sudoku.gui;

import javax.swing.*;
import java.awt.*;
import sudoku.*;

/**
 * The messenger class handles user messages for the SuDoku application.
 */
class Messenger 
{
	private JLabel messageBar;
	
	Messenger()
	{
		this.messageBar = new JLabel();
	}
	
	JLabel getMessageBar()
	{
		return messageBar;
	}
	void clearMessage()
	{
		messageBar.setText("");
	}
	void showMessage(String message)
	{
		messageBar.setText(message);
		messageBar.setForeground(Color.WHITE);
	}
	void showError(String error)
	{
		messageBar.setText("ERROR: " + error);
		messageBar.setForeground(Color.RED);
	}
	void showHint(Hint hint)
	{
		showMessage(hint.toString());
	}
}
