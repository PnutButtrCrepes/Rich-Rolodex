package keenan.james.nathan;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class KeenanQuickRolodexFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public JTextField noteNumber;
	public JTextPane noteText;
	
	public JButton deleteButton;
	public JButton newButton;
	public JButton saveButton;
	public JButton printButton;
	
	public JButton firstButton;
	public JButton previousButton;
	public JButton nextButton;
	public JButton lastButton;
	
	public JButton findButton;
	public JButton findNextButton;
	
	public JComboBox<Integer> sizeDropDown;
	public JButton boldButton;
	public JButton italicButton;
	public JButton underlineButton;
	public Choice colorDropDown;

	public KeenanQuickRolodexFrame()
	{
		super("Keen 'n' Quick Rolodex");
		
		noteNumber = new JTextField();
		noteText = new JTextPane();
		noteText.setContentType("text/rtf");
		KeenanQuickActionListener actionListener = new KeenanQuickActionListener(this);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 25, 820, 1000);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane(noteText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrollPane);
		
		JPanel headerPanel = new JPanel();
		contentPane.add(headerPanel, BorderLayout.NORTH);
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		
		JPanel cardPanel = new JPanel();
		headerPanel.add(cardPanel);
		cardPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
		
		JPanel filePanel = new JPanel();
		cardPanel.add(filePanel);
		filePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		
		deleteButton = new JButton("Delete");
		deleteButton.addActionListener(actionListener);
		filePanel.add(deleteButton);
		
		newButton = new JButton("New");
		newButton.addActionListener(actionListener);
		filePanel.add(newButton);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(actionListener);
		filePanel.add(saveButton);
		
		printButton = new JButton("Print");
		printButton.addActionListener(actionListener);
		filePanel.add(printButton);
		
		JPanel searchPanel = new JPanel();
		cardPanel.add(searchPanel);
		searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		
		firstButton = new JButton("<<<");
		firstButton.addActionListener(actionListener);
		searchPanel.add(firstButton);
		
		previousButton = new JButton("<<");
		previousButton.addActionListener(actionListener);
		searchPanel.add(previousButton);
		
		nextButton = new JButton(">>");
		nextButton.addActionListener(actionListener);
		searchPanel.add(nextButton);
		
		lastButton = new JButton(">>>");
		lastButton.addActionListener(actionListener);
		searchPanel.add(lastButton);
		
		findButton = new JButton("Find");
		findButton.addActionListener(actionListener);
		searchPanel.add(findButton);
		
		findNextButton = new JButton("Find Next");
		findNextButton.addActionListener(actionListener);
		searchPanel.add(findNextButton);
		
		noteNumber.setColumns(5);
		cardPanel.add(noteNumber);
		
		JPanel editPanel = new JPanel();
		headerPanel.add(editPanel);
		
		sizeDropDown = new JComboBox<Integer>();
		sizeDropDown.addItem(8);
		sizeDropDown.addItem(9);
		sizeDropDown.addItem(10);
		sizeDropDown.addItem(11);
		sizeDropDown.addItem(12);
		sizeDropDown.addItem(14);
		sizeDropDown.addItem(18);
		sizeDropDown.addItem(24);
		sizeDropDown.addItem(30);
		sizeDropDown.addItem(36);
		sizeDropDown.addItem(48);
		sizeDropDown.addItem(60);
		sizeDropDown.addItem(72);
		sizeDropDown.setEditable(true);
		sizeDropDown.addActionListener(actionListener);
		editPanel.add(sizeDropDown);
		
		boldButton = new JButton("B");
		boldButton.setFont(new Font("Times New Roman", Font.BOLD, 11));
		boldButton.addActionListener(actionListener);
		editPanel.add(boldButton);
		
		italicButton = new JButton("I");
		italicButton.setFont(new Font("Times New Roman", Font.ITALIC, 11));
		italicButton.addActionListener(actionListener);
		editPanel.add(italicButton);
		
		underlineButton = new JButton("U");
		Map<TextAttribute, Integer> fontAttributes = new HashMap<TextAttribute, Integer>();
		fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		Font underline = new Font("Times New Roman",Font.PLAIN, 11).deriveFont(fontAttributes);
		underlineButton.setFont(underline);
		underlineButton.addActionListener(actionListener);
		/*
		underlineButton.addActionListener((e) ->
		{
			// this is the only listener that needs to remain here. Some weird Java reference shit is probably fucking things up.
			
			int selectionStart = noteText.getSelectionStart();
			String selection = noteText.getSelectedText();
			
			try
			{
				actionListener.styledCards[actionListener.cardNumber] = noteText.getStyledDocument();
				
				for (int i = selectionStart; i < selectionStart + selection.length(); i++)
				{
					Element currentCharacter = actionListener.styledCards[actionListener.cardNumber].getCharacterElement(i);
					Boolean isBold = (Boolean) currentCharacter.getAttributes().getAttribute(StyleConstants.Underline);
					
					actionListener.styledCards[actionListener.cardNumber].remove(i, 1);
					
					Style bold = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
					bold.addAttributes(currentCharacter.getAttributes());
					StyleConstants.setUnderline(bold, !isBold);
					
					actionListener.styledCards[actionListener.cardNumber].insertString(i, selection.substring(i - selectionStart, i - selectionStart + 1), bold);
					
					noteText.requestFocusInWindow();
					noteText.select(selectionStart, selectionStart + selection.length());
				}
			}
			catch (NullPointerException e1) { /* if nothing selected  }
			catch (BadLocationException e1)
			{
				e1.printStackTrace();
			}
		});
		*/
		editPanel.add(underlineButton);
		
		colorDropDown = new Choice();
		colorDropDown.addItem("Black");
		colorDropDown.addItem("Red");
		colorDropDown.addItem("Orange");
		colorDropDown.addItem("Yellow");
		colorDropDown.addItem("Green");
		colorDropDown.addItem("Blue");
		colorDropDown.addItemListener(actionListener);
		editPanel.add(colorDropDown);
	}
	
	public static void main(String[] args)
	{
		KeenanQuickRolodexFrame frame = new KeenanQuickRolodexFrame();
		frame.setVisible(true);
	}
}