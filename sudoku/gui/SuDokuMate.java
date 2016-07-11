package sudoku.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import sudoku.*;
/**
 * The primary "SuDoku Mate" application class.
 * This class represents the top level of the SuDoku Mate application.
 * It uses the {@link sudoku.SuDoku} class to perform the back end functions of solving the SuDoku and
 * providing hints. It "owns" the SuDoku {@link Board} which does most of the GUI work and the
 * {@link Messenger} class which provides textual feedback to the user.
 * <P>The class initialises all of the GUI components. 
 * Its role once all the components have been initialised is to intercept user actions and
 * call on the {@link sudoku.SuDoku}, {@link Board} and {@link Messenger} classes as appropriate in 
 * response to those actions. 
 */
public class SuDokuMate extends JFrame implements ActionListener 
{
	static final Color BACKGROUND = Color.BLACK;
	static final Color FOREGROUND = Color.RED;
	static final Border SPACE_BORDER = BorderFactory.createEmptyBorder(2,2,2,2);
	static final Border GROUP_BORDER = 	BorderFactory.createCompoundBorder(SPACE_BORDER,
										BorderFactory.createCompoundBorder(
										BorderFactory.createLineBorder(FOREGROUND), SPACE_BORDER));
	static final Font FONT = new Font("Forte", Font.BOLD, 48);

	private Board board = null;
	private JPanel topRightPane = null;
	private CardLayout goRetryDeck = null;
	private Messenger messenger = null;

	private JButton newButton = null;
	private JButton goButton = null;
	private JButton retryButton = null;
	private JButton hintButton = null;

	private SuDoku puzzle = null;
	private int nBands;
	private int nStacks;
	
	public static void main(String[] args)
	{
		new SuDokuMate();
	}
	/**
	 * Initialises the application, reading any parameters and building the content pane.
	 */
	public SuDokuMate() 
	{
		setSize(540, 520);
		this.nBands = 3;
		this.nStacks = 3;
		messenger = new Messenger();
		board = new Board(this, nBands, nStacks);
		setContentPane(initContentPane());
		setVisible(true);
	}

	/**
	 * Responds to actions on the "New", "Go", "ReTry" and "Hint" buttons.
	 */
	public void actionPerformed(ActionEvent event) 
	{
		if (event.getSource().equals(newButton)) clear();
		if (event.getSource().equals(goButton)) startSolving();
		if (event.getSource().equals(retryButton)) reset();
		if (event.getSource().equals(hintButton)) showHint(puzzle.getHint());
	}

	void actionBoardRedisplay()
	{
		messenger.clearMessage();
	}
	void actionBoardComplete()
	{
		Hint hint = puzzle.checkPuzzle();
		if (hint == null) {
			goToCompletedState();
		} else {
			showHint(hint);
		}
	}
	
	private JPanel initContentPane() 
	{
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(GROUP_BORDER);
		contentPane.setBackground(BACKGROUND);
		initActionButtons();	
		contentPane.add(board, BorderLayout.CENTER);
		contentPane.add(initTitleBar(), BorderLayout.PAGE_START);
		contentPane.add(initLeftPane(), BorderLayout.LINE_START);
		contentPane.add(initRightPane(), BorderLayout.LINE_END);
		contentPane.add(initMessagePane(), BorderLayout.PAGE_END);
		goToSetUpState();
		return contentPane;
	}
	private void initActionButtons()
	{
		newButton = initActionButton("New", 'N', "Clear the SuDoku board and re-start");
		goButton = initActionButton("Go", 'G', "Start to solve the SuDoku");
		retryButton = initActionButton("ReTry", 'R', "Clear all but the givens from the SuDoku");
		hintButton = initActionButton("Hint:", 'H', "Want a hint?");
		sizeActionButtons(new JButton[] {newButton, goButton, retryButton, hintButton} );
	}
	private JButton initActionButton(String name, char mnemonic, String tip)
	{
		JButton actionButton = new JButton(name);
		actionButton.setMnemonic(mnemonic);
		actionButton.setToolTipText(tip);
		actionButton.addActionListener(this);
		return actionButton;
	}
	private void sizeActionButtons(JButton[] buttons)
	{
		int maxWidth = 0;
		Dimension preferredSize;
		for (int i = 0; i < buttons.length; i++)
		{
			maxWidth = Math.max(maxWidth, buttons[i].getPreferredSize().width);
		}
		for (int i = 0; i < buttons.length; i++)
		{
			preferredSize = buttons[i].getPreferredSize();
			preferredSize.width = maxWidth;
			buttons[i].setPreferredSize(preferredSize);
		}
	}
	private JLabel initTitleBar()
	{
		JLabel titleBar = new JLabel("SuDoku Mate");
		titleBar.setHorizontalAlignment(SwingConstants.CENTER);
		titleBar.setBorder(GROUP_BORDER);
		titleBar.setForeground(FOREGROUND);
		titleBar.setFont(FONT);
		return titleBar;
	}
	private JPanel initLeftPane() 
	{
		JPanel leftPane = new JPanel();
		leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.PAGE_AXIS));
		leftPane.setBackground(SuDokuMate.BACKGROUND);		
		JPanel topLeftPane = new JPanel(new GridLayout(1,1));
		topLeftPane.setBorder(GROUP_BORDER);
		topLeftPane.setBackground(BACKGROUND);
		topLeftPane.add(dressForVerticalBox(newButton));
		leftPane.add(dressForVerticalBox(topLeftPane));
		JPanel symbolFilter = board.getSymbolFilter();
		leftPane.add(symbolFilter);
		return leftPane;
	}
	private JPanel initRightPane() 
	{
		JPanel rightPane = new JPanel();
		rightPane.setLayout(new BoxLayout(rightPane, BoxLayout.PAGE_AXIS));
		rightPane.setBackground(BACKGROUND);
		goRetryDeck = new CardLayout();
		topRightPane = new JPanel(goRetryDeck);
		topRightPane.setBorder(GROUP_BORDER);
		topRightPane.setBackground(BACKGROUND);
		topRightPane.add(goButton, "Go");
		topRightPane.add(retryButton, "ReTry");
		rightPane.add(dressForVerticalBox(topRightPane));
		JPanel markUpControl = board.getMarkUpControl();
		rightPane.add(markUpControl);
		return rightPane;
	}
	private JPanel initMessagePane() 
	{
		JPanel messagePane = new JPanel();
		messagePane.setLayout(new BoxLayout(messagePane, BoxLayout.LINE_AXIS));
		messagePane.setBorder(GROUP_BORDER);
		messagePane.setBackground(BACKGROUND);
		messagePane.add(hintButton);
		messagePane.add(Box.createRigidArea(new Dimension(10,0)));
		JLabel messageBar = messenger.getMessageBar();
		messageBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		messagePane.add(messageBar);
		return messagePane;
	}
	/**
	 * Prepares components so that they line up properly in a vertical box layout.
	 * Unfortunately, the vertical box layout produces some unwanted alignment problems due to 
	 * the not too well thought through component alignment and maximum size defaults. Normally,
	 * I would address this by creating a new layout manager, or at least putting the function below in
	 * some layout utility class, so please consider the below a hack that should not live here in this
	 * file!
	 */
	private JComponent dressForVerticalBox(JComponent component)
	{
		component.setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension dimension = new Dimension(component.getPreferredSize());
		dimension.width = Integer.MAX_VALUE;
		component.setMaximumSize(dimension);
		return component;
	}
	

	private void clear()
	{
		goToSetUpState();
		puzzle = null;
		board.clear();
	}
	private void startSolving()
	{
		puzzle = new SuDoku(nBands, nStacks, board.getSymbolSet().getReadOnlySet(), board.getSymbols());
		int nSolutions = puzzle.checkSolutions();
		if (nSolutions == SuDoku.SINGLE_SOLUTION) {
			goToSolveState();
			board.setModel(puzzle);
		} else if (nSolutions == SuDoku.NO_SOLUTION) {
			messenger.showError("There are no solutions to this puzzle");
		} else if (nSolutions == SuDoku.MANY_SOLUTIONS) {
			messenger.showError("There are multiple solutions to this puzzle");
		}
	}
	private void reset()
	{
		goToSolveState();
		puzzle.reset();
		board.setModel(puzzle);
		board.redisplay();
	}
	private void showHint(Hint hint)
	{
		if (hint != null)
		{
			board.showHint(hint);
			messenger.showHint(hint);
		} else {
			messenger.showMessage("Sorry - Can't help you out here!");
		}
	}	
	private void goToSetUpState()
	{
		hintButton.setEnabled(false);
		board.setEnabled(true);
		goRetryDeck.show(topRightPane, "Go");
		messenger.showMessage("Enter givens, then click \"Go\" to start solving...");
	}
	private void goToSolveState()
	{		
		hintButton.setEnabled(true);
		board.setEnabled(true);
		goRetryDeck.show(topRightPane, "ReTry");
		messenger.showMessage("Good Luck !");
	}
	private void goToCompletedState()
	{
		hintButton.setEnabled(false);
		board.setEnabled(false);
		messenger.showMessage("Well Done !");
	}
}
