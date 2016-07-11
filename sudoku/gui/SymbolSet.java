package sudoku.gui;

import java.awt.Color;
import java.text.ParseException;
import java.util.*;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
/**
 * The set of symbols that may be placed in each cell of the SuDoku.
 * The current implementation restricts the symbols to being the digits 1 to 9 for a regular SuDoku,
 * adding 0 and the letters A - F for the largest supported SuDoku (4x4). The symbols are also given colours;
 * and it was originally my intention to use the colours on the SuDoku grid, however this was too garish and now the
 * colours are only used in the {@link SymbolFilter} and {@link MarkUpControl}!
 */
class SymbolSet 
{
	private static final float SATURATION = 0.5F;
	private static final float BRIGHTNESS = 1.0F;
	private static final String[] TEXTS = {"1","2","3","4","5","6","7","8","9","0","A","B","C","D","E","F"};
	
	private LinkedHashSet<Symbol> symbolSet;	// Important to have the symbols in a particular order for symbol filter
	private Map<String, Symbol> textMap;
	private int nSymbols;
	
	SymbolSet(int nSymbols)
	{
		Symbol symbol;
		this.nSymbols = nSymbols;
		symbolSet = new LinkedHashSet<Symbol>(nSymbols);
		textMap = new HashMap<String, Symbol>(nSymbols);
		for (int i = 0; i < nSymbols; i++)
		{
			symbol = new Symbol(i);
			symbolSet.add(symbol);
			textMap.put(TEXTS[i], symbol);
		}

	}
	Set<Symbol> getReadOnlySet()
	{
		return Collections.unmodifiableSet(symbolSet);
	}
	
	Symbol[] getSymbols()
	{
		return symbolSet.toArray(new Symbol[nSymbols]);
	}

	Symbol getSymbol(String text)
	{
		return textMap.get(text);
	}
	Set<Symbol> getSubSetMatching(Collection<?> symbols)
	{
		Set<Symbol> subSet = new LinkedHashSet<Symbol>(symbolSet);
		subSet.containsAll(symbols);
		return subSet;
	}

	JFormattedTextField.AbstractFormatter getFormatter()
	{
		try {
			MaskFormatter formatter = new MaskFormatter("*");
			StringBuffer validCharacters = new StringBuffer();
			for (Symbol symbol : symbolSet) validCharacters.append(symbol.toString());
			formatter.setPlaceholder(" ");
			validCharacters.append(" ");
			formatter.setValidCharacters(validCharacters.toString());
			return formatter;
		} catch (ParseException e) {
			// This cannot occur as the input to the mask formatter not variable.
			return null;
		}
	}
	
	class Symbol
	{
		private Color color;
		private String text;
		private int id;
		
		private Symbol(int id)
		{
			this.id = id;
			this.text = TEXTS[id];
			float hue = (float)id / (float)(nSymbols + 1);
			this.color = Color.getHSBColor(hue, SATURATION, BRIGHTNESS);
		}
		
		public String toString()
		{
			return text;
		}
		
		int getID()
		{
			return id;
		}
		Color getColor()
		{
			return color;
		}
	}
}
