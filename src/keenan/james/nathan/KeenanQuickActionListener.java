package keenan.james.nathan;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.PrinterException;
import java.io.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;

public class KeenanQuickActionListener implements ActionListener, ItemListener, Runnable {

	private KeenanQuickRolodexFrame frame;
	
	StyledDocument[] styledCards = new StyledDocument[1000];
	Style defaultStyle;
	
	int cardLimit = 0;
	int cardNumber = 0;
	
	private String findString;
	private int lastFindIndex;
	
	private boolean ignoreEvent = false;
	
	public KeenanQuickActionListener(KeenanQuickRolodexFrame frame)
	{
		this.frame = frame;
		
		defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setFontFamily(defaultStyle, "arial");
		StyleConstants.setFontSize(defaultStyle, 11);
		
		//ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		//executor.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
		
		init();
	}
	
	private void init()
	{
		try
		{
			// read from RTF
			EditorKit rtfKit = frame.noteText.getEditorKitForContentType("text/rtf");
			for (int i = 0; i < 1000; i++)
			{
				styledCards[i] = (StyledDocument) rtfKit.createDefaultDocument();
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		
		loadStyledCards();
		
		/*
		if (cardLimit == 0)
		{
			cards[0] = "Nathan Keenan Systems\nwww.samplewebsite.com";
			cardLimit = 1;
		}
		*/
		
		displayCurrentCard();
		
		frame.noteNumber.setText(Integer.toString(cardNumber + 1));
	}
	
	private void loadStyledCards()
	{
		File cardsDirectory = new File("cards");
		if (!cardsDirectory.exists())
		{
			cardsDirectory.mkdir();
			loadCards();
			return;
		}
		
		try
		{
			for (int i = 0; i < 1000; i++)
			{
				File styledCardFile = new File("cards" + File.separator + "card" + i + ".rtf");
				if (!styledCardFile.exists() || !styledCardFile.isFile())
				{
					cardLimit = i;
					break;
				}
				
				// read from RTF
				EditorKit rtfKit = frame.noteText.getEditorKitForContentType("text/rtf");
				FileInputStream fileInputStream = new FileInputStream(styledCardFile);
				rtfKit.read(fileInputStream, styledCards[i], 0);
				trimStyledDocument(styledCards[i]);
				fileInputStream.close();
			}
		}
		catch (IOException | BadLocationException e1)
		{
			e1.printStackTrace();
		}
	}
	
	private void loadCards()
	{
		File file = new File("knqnotes1.dat");
		
		if (!file.exists() || !file.isFile())
			return;
		
		int importOldNotes = JOptionPane.showConfirmDialog(null, "There is an old version of your notes available. Would you like to import them?", "Import Notes", JOptionPane.YES_NO_OPTION);
		
		if (importOldNotes == JOptionPane.NO_OPTION)
			return;
		
		try
		{
			BufferedReader fileReader = new BufferedReader(new FileReader(file));
			
			String line = "";
			while (cardLimit < 1000 && !line.equals("EOF"))
			{
				String card = "";
				
				while (!(line = fileReader.readLine()).equals("EOC") && !line.equals("EOF"))
					card += line + "\n";
				
				if (!line.equals("EOF"))
				{
					//cards[cardLimit] = card.trim() + "\n";
					styledCards[cardLimit].insertString(0, card, defaultStyle);
					cardLimit += 1;
				}
			}
			
			fileReader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void cardRead()
	{
		styledCards[cardNumber] = frame.noteText.getStyledDocument();
		frame.noteNumber.setText(Integer.toString(cardNumber + 1));
	}
	
	private void switchToCardNumber(int newCardNumber)
	{
		if (newCardNumber < 0)
			return;
		
		if (newCardNumber + 1 > cardLimit)
			return;
		
		cardRead();
		
		cardNumber = newCardNumber;
		
		displayCurrentCard();
	}
	
	private void displayCurrentCard()
	{
		if (styledCards[cardNumber] != null)
			frame.noteText.setStyledDocument(styledCards[cardNumber]);
		frame.noteNumber.setText(Integer.toString(cardNumber + 1));
	}
	
	private Object getSelectionAttributeValue(Object attribute)
	{
		int selectionStart = frame.noteText.getSelectionStart();
		
		Element firstCharacter = styledCards[cardNumber].getCharacterElement(selectionStart);
		return firstCharacter.getAttributes().getAttribute(attribute);
	}
	
	private void setSelectionAttributeValue(Object attribute, Object value)
	{
		int selectionStart = frame.noteText.getSelectionStart();
		String selection = frame.noteText.getSelectedText();
		
		try
		{
			styledCards[cardNumber] = frame.noteText.getStyledDocument();
			
			for (int i = selectionStart; i < selectionStart + selection.length(); i++)
			{
				Element currentCharacter = styledCards[cardNumber].getCharacterElement(i);
				
				styledCards[cardNumber].remove(i, 1);
				Style characterStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
				characterStyle.addAttributes(currentCharacter.getAttributes());
				characterStyle.addAttribute(attribute, value);
					
				styledCards[cardNumber].insertString(i, selection.substring(i - selectionStart, i - selectionStart + 1), characterStyle);
			}
			
			frame.noteText.requestFocusInWindow();
			frame.noteText.select(selectionStart, selectionStart + selection.length());
		}
		catch (Exception e1) {}
	}
	
	private void trimStyledDocument(StyledDocument styledDocument)
	{
		try {
			// delete only trailing whitespace
			String text = styledDocument.getText(0, styledDocument.getLength());
			int removalStart = text.length();
			for (int i = text.length() - 1; i >= 0; i--)
			{
				if (!Character.isWhitespace(text.charAt(i)))
				{
					removalStart = i + 1;
					break;
				}
			}
			styledDocument.remove(removalStart, styledDocument.getLength() - removalStart);
			styledDocument.insertString(styledDocument.getLength(), "\n", null);
			
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	private int findString(String findString, int startIndex)
	{
		int findIndex = -1;
		for (int i = cardNumber; i < cardLimit; i++)
		{
			try
			{
				if((findIndex = (styledCards[i].getText(0, styledCards[i].getLength()).toLowerCase().indexOf(findString, startIndex))) != -1)
				{
					cardNumber = i;
					displayCurrentCard();
					frame.noteText.requestFocusInWindow();
					frame.noteText.select(findIndex, findIndex + findString.length());
					return findIndex;
				}
			} 
			catch (BadLocationException e1)
			{
				e1.printStackTrace();
			}
		}
		
		JOptionPane.showMessageDialog(null, "No more instances found!", "Find String", JOptionPane.ERROR_MESSAGE);
		
		return findIndex;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (ignoreEvent)
			return;
		
		if (e.getSource().equals(frame.deleteButton))
		{
			if (cardNumber == cardLimit)
				return;
			
			int answer = JOptionPane.showConfirmDialog(null, "Delete: Are you sure?", "Confirm Delete", JOptionPane.OK_CANCEL_OPTION);
			if (answer == JOptionPane.CANCEL_OPTION)
				return;
			
			for (int i = cardNumber + 1; i <= cardLimit; i++)
				styledCards[i - 1] = styledCards[i];
			
			cardLimit -= 1;
			
			if (cardNumber >= cardLimit && cardNumber > 0)
				cardNumber -= 1;
			
			if (cardLimit == 0)
				cardLimit = 1;
			
			displayCurrentCard();
		}
		else if (e.getSource().equals(frame.newButton))
		{
			try {
				if (styledCards[cardNumber].getText(0, styledCards[cardNumber].getLength()).trim().length() == 0)
					return;
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
			
			if (cardLimit >= 999)
				return;
			
			cardNumber = cardLimit;
			
			frame.noteText.setStyledDocument(styledCards[cardNumber]);
			try {
				styledCards[cardNumber].remove(0, styledCards[cardNumber].getLength());
				styledCards[cardNumber].insertString(0, "New Card.\nClick on Save when done.", defaultStyle);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
			frame.noteText.requestFocusInWindow();
			frame.noteText.selectAll();
			
			frame.noteNumber.setText(Integer.toString(cardNumber + 1));
		}
		else if (e.getSource().equals(frame.saveButton))
		{
			cardRead();
			if (cardNumber == cardLimit)
				cardLimit += 1;
			
			try
			{
				for (int i = 0; i < cardLimit; i++)
				{
					File styledCardFile = new File("cards" + File.separator + "card" + i + ".rtf");
					// write to RTF
					EditorKit rtfKit = frame.noteText.getEditorKitForContentType("text/rtf");
					FileOutputStream fileOutputStream = new FileOutputStream(styledCardFile);
					rtfKit.write(fileOutputStream, styledCards[i], 0, styledCards[i].getLength());
					fileOutputStream.flush();
					fileOutputStream.close();
				}
				
				int numberToDelete = cardLimit;
				File cardToDelete;
				while ((cardToDelete = new File("cards" + File.separator + "card" + (numberToDelete++) + ".rtf")).isFile())
					cardToDelete.delete();
			}
			catch (IOException | BadLocationException e1)
			{
				e1.printStackTrace();
			}
			
			frame.noteNumber.setText(Integer.toString(cardNumber + 1));
		}
		else if (e.getSource().equals(frame.printButton))
		{
			try {
				frame.noteText.print();
			} catch (PrinterException e1) {
				e1.printStackTrace();
			}
		}
		else if (e.getSource().equals(frame.firstButton)) { switchToCardNumber(0); }
		else if (e.getSource().equals(frame.previousButton)) { switchToCardNumber(cardNumber - 1); }
		else if (e.getSource().equals(frame.nextButton)) { switchToCardNumber(cardNumber + 1); }
		else if (e.getSource().equals(frame.lastButton)) { switchToCardNumber(cardLimit - 1); }
		else if (e.getSource().equals(frame.findButton)) { lastFindIndex = findString(findString = (JOptionPane.showInputDialog(null, "Find what?", "Find String", JOptionPane.PLAIN_MESSAGE)).toLowerCase(), 0); }
		else if (e.getSource().equals(frame.findNextButton)) { lastFindIndex = findString(findString, lastFindIndex + 1); }
		else if (e.getSource().equals(frame.sizeDropDown)) { setSelectionAttributeValue(StyleConstants.FontSize, frame.sizeDropDown.getSelectedItem()); }
		else if (e.getSource().equals(frame.boldButton)) { setSelectionAttributeValue(StyleConstants.Bold, !((Boolean) getSelectionAttributeValue(StyleConstants.Bold))); }
		else if (e.getSource().equals(frame.italicButton)) { setSelectionAttributeValue(StyleConstants.Italic, !((Boolean) getSelectionAttributeValue(StyleConstants.Italic))); }
		else if (e.getSource().equals(frame.underlineButton)) { setSelectionAttributeValue(StyleConstants.Underline, !((Boolean) getSelectionAttributeValue(StyleConstants.Underline))); }
	}
	
	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Color newTextColor = Color.black;
		
		if (e.getItem().equals("Black"))
			newTextColor = Color.black;
		else if (e.getItem().equals("Red"))
			newTextColor = Color.red;
		else if (e.getItem().equals("Orange"))
			newTextColor = Color.orange;
		else if (e.getItem().equals("Yellow"))
			newTextColor = Color.yellow;
		else if (e.getItem().equals("Green"))
			newTextColor = Color.green;
		else if (e.getItem().equals("Blue"))
			newTextColor = Color.blue;
		
		setSelectionAttributeValue(StyleConstants.Foreground, newTextColor);
	}

	@Override
	public void run()
	{
		// custom caret updater that runs at a set interval
		
		// TODO FIX THIS AND FIX LAG
		
		int caretPosition = frame.noteText.getCaretPosition();
		System.out.println(caretPosition);
		//int endOfSelection = e.getMark();
		
		Element firstCharacter = styledCards[cardNumber].getCharacterElement(caretPosition);
		Style characterStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		characterStyle.addAttributes(firstCharacter.getAttributes());
		
		int fontSize = (int) characterStyle.getAttribute(StyleConstants.FontSize);
		//boolean isBold = (boolean) characterStyle.getAttribute(StyleConstants.Bold);
		//boolean isItalic = (boolean) characterStyle.getAttribute(StyleConstants.Italic);
		//boolean isUnderlined = (boolean) characterStyle.getAttribute(StyleConstants.Underline);
		//Color fontColor = (Color) characterStyle.getAttribute(StyleConstants.Foreground);
		
		frame.sizeDropDown.setSelectedItem(fontSize);
		
		/*
		ignoreEvent = true;
		frame.boldButton.getModel().setPressed(isBold);
		frame.italicButton.getModel().setPressed(isItalic);
		frame.underlineButton.getModel().setPressed(isUnderlined);
		ignoreEvent = false;
		
		if (fontColor.equals(Color.red))
			frame.colorDropDown.select("Red");
		else if (fontColor.equals(Color.orange))
			frame.colorDropDown.select("Orange");
		else if (fontColor.equals(Color.yellow))
			frame.colorDropDown.select("Yellow");
		else if (fontColor.equals(Color.green))
			frame.colorDropDown.select("Green");
		else if (fontColor.equals(Color.blue))
			frame.colorDropDown.select("Blue");
		else
			frame.colorDropDown.select("Black");
		
		*/
		
		// TODO iterate over every character and create conglomerate of attributes
		/*
		for (int i = caretPosition; i < endOfSelection; i++)
		{
			Element firstCharacter = styledCards[cardNumber].getCharacterElement(i);
		}
		*/
	}
}