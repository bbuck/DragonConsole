/*
 * Copyright (c) 2010 3l33t Software Developers, L.L.C.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.eleet.dragonconsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;
import com.eleet.dragonconsole.util.*;
import com.eleet.dragonconsole.file.*;
import java.awt.datatransfer.DataFlavor;;

/**
 * DragonConsole is a console mimic designed to give Java programmers a RTF
 * console for which to use with any console based/styled applications they
 * wish to write.
 * <br /><br />
 * DragonConsole allows for color output using color codings in text, default
 * char for color code is '&' followed by a letter disignator that represents
 * the foreground color (text) and then another letter for the background
 * color (i.e. '&lb' is Blue font with Black background).
 * <br /><pre>
 * Color        Code            Variable
 * Red          r               ANSI.INTENSE_RED
 * Dark Red     R               ANSI.RED
 * Blue         l               ANSI.INTENSE_BLUE
 * Dark Blue    L               ANSI.BLUE
 * Green        g               ANSI.INTENSE_GREEN
 * Dark Green   G               ANSI.GREEN
 * Yellow       y               ANSI.INTENSE_YELLOW
 * Dark Yellow  Y               ANSI.YELLOW
 * Cyan         c               ANSI.INTENSE_CYAN
 * Dark Cyan    C               ANSI.CYAN
 * Gray         x (Default)     ANSI.WHITE
 * Dark Gray    X               ANSI.INTENSE_BLACK
 * Purple       p               DragonConsole.INTENSE_PURPLE
 * Dark Purple  P               DragonConsole.PURPLE
 * Gold         d               DragonConsole.INTENSE_GOLD
 * Dark Gold    D               DragonConsole.GOLD
 * Black        b               ANSI.BLACK
 * White        w               ANSI.INTENSE_WHITE
 * Orange       o               DragonConsole.INTENSE_ORANGE
 * Dark Orange  O               DragonCOnsole.ORANGE
 * </pre><br />
 * There are two unique characters that cannot be used as Color codes since they
 * are used for style control. That is "0" which is used in "&00" which is the
 * equivalent of ANSIStyle "\003[0m". Both reset the style back to it's default. 
 * The second is "-" which can be substituted in place of any color in a color
 * code to "carry over" that color. For Example, I have the code "&ob" for
 * Orange foreground and black background and I want to change the foreground
 * without changing the background I can add "&r-" which changes the foreground
 * to red and the background stays the same so the current style code is "&rb".
 * <br /><br />
 * Built in Defaults:<br />
 * - Color Code char: '&'<br />
 * - Default Fore/Background color: 'xb'<br />
 * - Default System Message color: 'cb'<br />
 * - Default Error Message color: 'rb'<br />
 * -- Mac Style Defaults<br />
 * - Color Code char: '&'<br />
 * - Default Fore/Background color: 'bw'<br />
 * - Default System Message color: 'ow'<br />
 * - Default System Message color: 'rw'<br />
 * <br /><br/>
 * Additional Features include arrow key navigation for previous entered
 * commands (default stored is the last 10, but this is configurable).
 * <br /><br />
 * Configurable controls for adding a new line in the input box with the "Enter"
 * key or having it just send the input to the output window.
 *
 * @author Brandon E Buck
 * @since October 30, 2009
 * @version 3.0.0
 */
public class DragonConsole extends JPanel implements KeyListener, 
                                                       CaretListener,
                                                       AdjustmentListener {
    // Version variables
    /** 
     * Major Release Version Number
     */
    private static final String VERSION = "3";

    /**
     * Minor Update/Sub-Version number, usually comes with additional features.
     */
    private static final String SUB_VER = "0";

    /** 
     * Minor Change/Bug Fix number, does not include additional features.
     */
    private static final String BUG_FIX = "2";

    /**
     * The Version Tag is "b" for Beta or "a" for Alpha, if it's a full release
     * it's blank.
     */
    private static final String VER_TAG = "b";

    // Default Finals
    /** 
     * The Default Width of the DragonConsole JPanel
     */
    private static final int DEFAULT_WIDTH = 725;

    /** 
     * The Default Height of the DragonConsole JPanel
     */
    private static final int DEFAULT_HEIGHT = 450;

    /** 
     * The Intense Color Orange, as used as a Default TextColor
     */
    public static final Color INTENSE_ORANGE = Color.ORANGE;

    /** 
     * The Color Orange, as used as a Default TextColor
     */
    public static final Color ORANGE = Color.ORANGE.darker();

    /** 
     * The Intense Color Purple, as used as a Default TextColor
     */
    public static final Color INTENSE_PURPLE = new Color(128, 0, 255);

    /** 
     * The Color Purple, as used as a Default TextColor
     */
    public static final Color PURPLE = INTENSE_PURPLE.darker();

    /** 
     * The Intense Color Gold, as used as a Default TextColor
     */
    public static final Color INTENSE_GOLD = new Color(241, 234, 139);

    /** 
     * The Color Gold, as used as a Default TextColor
     */
    public static final Color GOLD = INTENSE_GOLD.darker();

    // GUI
    /**
     * The JTextPane that represents the Console output (and input with
     * inlineInput) area.
     */
    private JTextPane consolePane;

    /**
     * The JTextArea that represents the input area when not using inlineInput
     */
    private JTextArea inputArea;

    /** 
     * The StyledDocument of the Console JTextPane, this Document is used for
     * anything that involves dealing with the Text that he user sees.
     */
    private StyledDocument consoleStyledDocument;

    /**
     * The JScrollPane used to hold the JTextPane that represents the Console.
     * This is it's own global for the purpose of pulling out the JScrollBar
     * when the value needs to be adjusted programmatically.
     */
    private JScrollPane consoleScrollPane;

    /**
     * The Width of the JPanel, default value is <code>DEFAULT_WIDTH</code>
     */
    private int cWidth = DEFAULT_WIDTH;

    /**
     * The Height of the JPanel, default value is <code>DEFAULT_HEIGHT</code>
     */
    private int cHeight = DEFAULT_HEIGHT;

    // Console
    /**
     * The CommandProcessor registered with this DragonConsole, defaults to
     * null
     */
    private CommandProcessor commandProcessor = null;

    /**
     * The InputController that will control all console input if using inline
     * input.
     */
    private InputController inputControl;

    // Console GUI (fonts/colors/etc)
    /**
     * The Font that is displayed in both the <code>inputArea</code> and
     * <code>consolePane</code>
     */
    private Font consoleFont;

    /**
     * The PromptPanel that displays the Prompt and inputArea to the user when
     * not using inline input.
     */
    private PromptPanel consolePrompt;

    /** 
     * The list of Text Colors that have been added to this Console.
     */
    private ArrayList<TextColor> textColors;

    // Input Utility
    /** 
     * The list of previous entries, used for navigating previous input
     */
    private ArrayList<String> previousEntries;

    /** 
     * The maximum number of previous entries to store, can be adjusted.
     */
    private int numberOfPreviousEntries;

    /**
     * The Current entry in the list of Previous Entries, changed when the user
     * navigates previous entries.
     */
    private int currentPreviousEntry;

    // Flags
    /**
     * Determines if pressing SHIFT + ENTER (when not using inline input) will
     * create a new line or not. Can be adjusted
     */
    private boolean inputFieldNewLine = true;

    /**
     * Tells the Console whether it should print the DragonConsole logo when
     * initialized or when the display style has been changed.
     */
    private boolean printDefaultMessage = true;

    /**
     * Determines whether or not to use Inline Input or a separate Inptu Area
     */
    private boolean useInlineInput = true;

    /**
     * This is used to ignore any input from the user. This can be changed from
     * outside the class as needed.
     */
    private boolean ignoreInput = false;

    /**
     * Tells the Console if it should save user input if output is appended
     * while the user is currently typing something in.
     */
    private boolean inputCarryOver = true;

    /**
     * This option determines if the Scroll bar should be kept at it's max
     * value anytime that text is added or input (via the input area) would
     * cause it not to be at it's max value.
     */
    private boolean alwaysKeepScrollBarMaxed = false;

    /**
     * This variable is determined from within the DragonConsole and is
     * <code>true</code> when the JScrollBar is at it's maximum value or
     * <code>false</code> if something has caused it's value to change.
     */
    private boolean isScrollBarAtMax = false;

    /**
     * This is an internal variable that tells the Adjustment Listener for the
     * JScrollBar to ignore any incoming adjustments. This is used to ignore
     * adjustments made programmatically so only user adjustments are filtered.
     */
    private boolean ignoreAdjustment = false;

    // Default text variables
    /**
     * The two character Default Style that text will use. This is the DCCC
     * for the Style without the <code>colorCodeChar</code>.
     */
    private String defaultColor = "xb";

    /**
     * The two character System Style that text will use. This is the DCCC
     * for the Style without the <code>colorCodeChar</code>.
     */
    private String systemColor = "cb";

    /**
     * The two character Error Style that text will use. This is the DCCC
     * for the Style without the <code>colorCodeChar</code>.
     */
    private String errorColor = "rb";

    /**
     * The two character Input Style that text will use. This is the DCCC
     * for the Style without the <code>colorCodeChar</code>.
     */
    private String inputColor = "xb";

    /**
     * The Character used by the DragonConsole to denote the beginning of a
     * DCCC. The default is '&' but this can be altered.
     */
    private char colorCodeChar = '&';

    // Default Console Colors
    /** 
     * Default Console/Input Area background color
     */
    private Color defaultBackground = Color.BLACK;

    /** 
     * Default Console/Input Area foreground color
     */
    private Color defaultForeground = Color.GRAY.brighter();

    /** 
     * Default Console/Input Area Caret color
     */
    private Color defaultCaret = Color.GRAY.brighter();

    /**
     * Default Mac Style Console/Input Area background color.
     */
    private Color defaultMacBackground = Color.WHITE;

    /** 
     * Default Mac Style Console/Input Area foreground color.
     */
    private Color defaultMacForeground = Color.BLACK;

    /** 
     * Default Mac Style Console/Input Area Caret color.
     */
    private Color defaultMacCaret = Color.BLACK;

    // ANSIStyle Support
    /**
     * Determines if ANSI Colors and not DCCCs should be processed and used,
     * by default this value is <code>false</code>.
     */
    private boolean useANSIColorCodes = false;

    // Style Variables
    /**
     * The Current ANSI Style that will be used for any new, text to be
     * appended if no new ANSI style is specified. This value is null until
     * <code>setUseANSIColorCodes(true);</code> is called.
     */
    private SimpleAttributeSet ANSIStyle = null;

    /**
     * The two character DCCC that represents the current DCCC to use to style
     * text unless another DCCC is processed and the <code>currentStyle</code>
     * is changed. This was added to make the DCCCs function the same way as
     * ANSI Color Codes do.
     */
    private String currentStyle = defaultColor;

    /**
     * Default Constructor uses all the default values.
     */
    public DragonConsole() {
        super();
        this.useInlineInput = true;
        this.printDefaultMessage = true;

        this.initializeConsole();
    }

    /**
     * Creates a Console with the specified input method.
     * @param useInlineInput <code>true</code> to use inline input or
     *  <code>false</code> to use a text area for input.
     */
    public DragonConsole(boolean useInlineInput) {
        super();
        this.useInlineInput = useInlineInput;
        this.printDefaultMessage = true;

        this.initializeConsole();
    }

    /** 
     * Tells the console to use either inline input or a text area for input
     * and whether or not to print the default message (DragonConsole logo).
     * @param useInlineInput <code>true</code> to use inline input or
     *  <code>false</code> to use a text area for input.
     * @param printDefaultMessage <code>true</code> to print the
     *  DragonConsole logo or <code>false</code> to print nothing.
     */
    public DragonConsole(boolean useInlineInput, boolean printDefaultMessage) {
        super();
        this.useInlineInput = useInlineInput;
        this.printDefaultMessage = printDefaultMessage;

        this.initializeConsole();
    }

    /** 
     * Allows the programmer to initialize a new console with custom initial
     * settings.
     * @param width Width of the Console.
     * @param height Height of the Console.
     */
    public DragonConsole(int width, int height) {
        super();
        this.cWidth = width;
        this.cHeight = height;
        this.useInlineInput = true;
        this.printDefaultMessage = true;

        this.initializeConsole();
    }

    /** 
     * Allows the programmer to initialize a new console with custom initial
     * settings.
     * @param width Width of the Console.
     * @param height Height of the Console.
     * @param useInlineInput <code>true</code> to use inline input or
     *  <code>false</code> to use a text area for input.
     */
    public DragonConsole(int width, int height, boolean useInlineInput) {
        super();
        this.cWidth = width;
        this.cHeight = height;
        this.useInlineInput = useInlineInput;
        this.printDefaultMessage = true;

        this.initializeConsole();
    }

    /** 
     * Allows the programmer to initialize a new console with custom initial
     * settings.
     * @param width Width of the Console.
     * @param height Height of the Console.
     * @param useInlineInput <code>true</code> to use inline input or
     *  <code>false</code> to use a text area for input.
     * @param printDefaultMessage <code>true</code> to print the
     *  DragonConsole logo or <code>false</code> to print nothing.
     */
    public DragonConsole(int width, int height, boolean useInlineInput, boolean printDefaultMessage) {
        super();
        this.cHeight = height;
        this.cWidth = width;
        this.useInlineInput = useInlineInput;
        this.printDefaultMessage = printDefaultMessage;

        this.initializeConsole();
    }

    /** 
     * Set the background to White and the foreground text to Black to mimic
     * the default color settings of a Mac Terminal.
     * Also changes the defaultColor, systemColor, and errorColor codes so
     * that they take into account the new background/foreground colors.
     */
    public void setMacStyle() {
        consolePane.setBackground(defaultMacBackground);
        consolePane.setCaretColor(defaultMacCaret);
        consolePane.setForeground(defaultMacForeground);
        inputArea.setBackground(defaultMacBackground);
        inputArea.setCaretColor(defaultMacCaret);
        inputArea.setForeground(defaultMacForeground);
        consolePrompt.setPromptForeground(defaultMacForeground);
        consolePrompt.setBackground(defaultMacBackground);
        defaultColor = "bw";
        systemColor = "ow";
        errorColor = "rw";
        inputColor = "bw";

        setInputAttribute();
        if (!useInlineInput)
            consolePrompt.setDefaultColor(defaultColor);
        clearConsole();
        printDefault();
    }

    /** 
     * Sets the default styles so the console colors mimic that of a standard
     * Gray on Black color setting. Sets the default colors for defaultColor,
     * systemColor, and errorColor.
     */
    public void setDefaultStyle() {
        consolePane.setBackground(defaultBackground);
        consolePane.setCaretColor(defaultCaret);
        consolePane.setForeground(defaultForeground);
        inputArea.setBackground(defaultBackground);
        inputArea.setCaretColor(defaultCaret);
        inputArea.setForeground(defaultForeground);
        consolePrompt.setPromptForeground(defaultForeground);
        consolePrompt.setBackground(defaultBackground);
        defaultColor = "xb";
        systemColor = "cb";
        errorColor = "rb";
        inputColor = "xb";

        setInputAttribute();
        if (!useInlineInput)
            consolePrompt.setDefaultColor(defaultColor);
        clearConsole();
        printDefault();
    }

    /** 
     * Removes all text that has been added to the console, effectively acts as
     * a "reset."
     */
    public void clearConsole() {
        inputControl.clearText();
    }

    /**
     * This is called when the input color is changed so that all input is added
     * with the AttributeSet (Style) that has been specified for input.
     */
    private void setInputAttribute() {
        inputControl.setInputAttributeSet(consoleStyledDocument.getStyle(inputColor));

        if (!useInlineInput) {
            inputArea.setForeground(getStyleColorFromCode(inputColor.charAt(0))); // Foreground
            inputArea.setBackground(getStyleColorFromCode(inputColor.charAt(1))); // Background
        }
    }

    /** 
     * Scans the list of TextColors added to the console for one matching the
     * <code>char code</code> passed and returns the Color that corresponds to
     * the <code>char code</code> given. If no Color that matches the
     * <code>code</code> is found returns <code>Color.WHITE</code>
     * @param code The <code>char code</code> to find the corresponding Color
     *  for.
     * @return The Color corresponding to the given <code>code</code> or
     *  White if none is found.
     */
    private Color getStyleColorFromCode(char code) {
        for (int i = 0; i < textColors.size(); i++) {
            TextColor tc = textColors.get(i);
            if (tc.equals(code))
                return tc.getColor();
        }

        return Color.WHITE;
    }

    /** 
     * Allows the programmer to set the method in which to control the Vertical
     * ScrollBar. If <code>true</code> is passed then anytime new text is
     * added to the <code>consolePane</code> the Vertical JScrollBar will be
     * set to it's max value. If <code>false</code> is passed then as long as
     * The JScrollBar is at it's max value when text is added, then it
     * automatically update to the new max value after the text has been added.
     * If it's moved from the max value by the user then it will stay at
     * whatever value it was placed at by the user.
     * @param alwaysScrollMax The boolean value determining the method
     *  on how to control the Vertical JScrollBar.
     */
    public void setKeepScrollBarMax(boolean alwaysScrollMax) {
        this.alwaysKeepScrollBarMaxed = alwaysScrollMax;
    }

    /** 
     * This method is overridden so that it will change the Maximum, Minimum,
     * and Preferred Size, as well as call the super.setSize(dim) method.
     * @param width The width of this object
     * @param height The height of this object.
     */
    public void setConsoleSize(int width, int height) {
        Dimension dim = new Dimension(width, height);
        this.setConsoleSize(dim);
    }

    /** 
     * This method is overridden so that it will change the Maximum, Minimum,
     * and Preferred Size, as well as call the super.setSize(dim) method.
     * @param dim The new size of this object.
     */
    public void setConsoleSize(Dimension dim) {
        super.setMaximumSize(dim);
        super.setMinimumSize(dim);
        super.setPreferredSize(dim);
    }

    /** 
     * Changes the character that should be used for protected input. By default
     * the character is a "*".
     * @param protectedChar The new character for protected input.
     */
    public void setProtectedCharacter(char protectedChar) {
        inputControl.setProtectedChar(protectedChar);
    }

    /** 
     * Sets all the values that are shared between all constructors so that
     * the console maintains similarities when other settings are turned on/off
     * or changed.
     */
    protected void initializeConsole() {
        setConsoleSize(cWidth, cHeight);

        // Navigating Previous Entries default behaviour
        //  Default holds 10 values, most recent entry at index 0, and oldest at
        //  the end.
        previousEntries = new ArrayList<String>();
        numberOfPreviousEntries = 10;
        currentPreviousEntry = 0;

        // Initialzie textColors ArrayList
        textColors = new ArrayList<TextColor>();

        // Create a new input controller
        inputControl = new InputController(null);

        // Initialize consolePrompt
        consolePrompt = new PromptPanel(">> ", defaultColor);

        // Setting the Font properly for the Prompt
        consoleFont = FileProcessor.getConsoleFont().deriveFont(Font.PLAIN, 14f);
        consolePrompt.setPromptFont(consoleFont);

        if (useInlineInput) {
            consolePane = new JTextPane() {
                @Override
                public void paste() {
                    try {
                        String pasteText = (String)(Toolkit.getDefaultToolkit()
                                .getSystemClipboard()
                                .getData(DataFlavor.stringFlavor));
                        getDocument().insertString(getCaretPosition(),
                                pasteText, null);
                    } catch (Exception exc) {
                        JOptionPane.showMessageDialog(this,
                                "Error #0001\nFailed to paste text to the "
                              + "document!\n" + exc.getMessage(),
                              "Error Caught", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            consolePane.addKeyListener(this);
            consolePane.addCaretListener(this);

        } else {
            consolePane = new JTextPane();
            //consolePane.setFocusable(false);
            consolePane.setEditable(false);
        }

        consolePane.setBackground(defaultBackground);
        consolePane.setForeground(defaultForeground);
        consolePane.setCaretColor(defaultCaret);
        consolePane.setFont(consoleFont);
        consolePane.setBorder(null);

        inputControl.installConsole(consolePane);
        inputControl.setConsoleInputMethod(useInlineInput);

        // Add Copy functionality to the Console
        //   and remove Cut/Paste functionality
        JTextComponent.KeyBinding newBindings[] = {
            new JTextComponent.KeyBinding(
                    KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK),
                    javax.swing.text.DefaultEditorKit.copyAction),
           new JTextComponent.KeyBinding(
                    KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK),
                    javax.swing.text.DefaultEditorKit.pasteAction),
           new JTextComponent.KeyBinding(
                    KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK),
                    javax.swing.text.DefaultEditorKit.beepAction)
        };
        Keymap k = consolePane.getKeymap();
        JTextComponent.loadKeymap(k, newBindings, consolePane.getActions());

        // Get the outputPanes StyledDocument and add the DocumentFilter to it
        consoleStyledDocument = consolePane.getStyledDocument();

        if (useInlineInput)
            ((AbstractDocument)consoleStyledDocument).setDocumentFilter(inputControl);

        inputArea = new JTextArea();
        inputArea.setBackground(defaultBackground);
        inputArea.setForeground(defaultForeground);
        inputArea.setCaretColor(defaultCaret);
        inputArea.setWrapStyleWord(true);
        inputArea.setLineWrap(true);
        inputArea.setFont(consoleFont);
        inputArea.setBorder(null);
        inputArea.addKeyListener(this);

        consoleScrollPane = new JScrollPane(consolePane);
        consoleScrollPane.setBorder(null);
        consoleScrollPane.getVerticalScrollBar().addAdjustmentListener(this);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(consolePrompt, BorderLayout.WEST);
        inputPanel.add(inputArea, BorderLayout.CENTER);

        JPanel splitPane = new JPanel(new BorderLayout());
        splitPane.add(consoleScrollPane, BorderLayout.CENTER);
        splitPane.add(inputPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        if (useInlineInput)
            add(consoleScrollPane, BorderLayout.CENTER);
        else
            add(splitPane, BorderLayout.CENTER);

        setOutputStyles();
        setDefaultStyle();
    }

    /** 
     * This method aids outside interfaces (such as DragonConsoleFrame) by
     * allowing it to give Focus to the inputArea. This method is only useful
     * if <code>useInlineInput</code> is <code>false</code>.
     */
    public void setInputFocus() {
        if (!useInlineInput)
            inputArea.requestFocusInWindow();
    }

    /** 
     * Allows the programmer to specify whether to allow ANSIStyle Color Codes
     * or not. If ANSIStyle Color Codes are allowed then you will be unable to
     * use the default DragonConsole Color Codes (although they will still be
     * processed out of any String passed through append().
     * @param useANSIColorCodes <code>true</code> to use ANSIStyle Color
     *  Codes, or <code>false</code> to use DragonConsole Color Codes.
     */
    public void setUseANSIColorCodes(boolean useANSIColorCodes) {
        this.useANSIColorCodes = useANSIColorCodes;
    }

    /**
     * This method returns <code>true</code> if the console is currently 
     * processing ANSI codes instead of DCCCs.
     * @return <code>true</code> if the Console is actively processing and
     *  using ANSI color codes instead of DCCCs.
     */
    public boolean isUseANSIColorCodes() {
        return this.useANSIColorCodes;
    }

    /** 
     * Sets the prompt for the Input Area to the specified prompt String passed.
     * This method does nothing when using Inline Input since no Input Area is
     * displayed.<br /><br />
     * ** NOTE: Only DragonConsole color codes can be used to style the Prompt
     * **
     * @param newPrompt The new prompt for the input area.
     */
    public void setPrompt(String newPrompt) {
        consolePrompt.setPrompt(newPrompt);
    }

    /** 
     * Prints the DragonConsole logo that is saved in logo_b or lobo_w in the
     * .jar depending on if the background color is White or not White.
     * (logo_w is for White backgrounds).
     * if <code>printDefaultMessage</code> is set to <code>true</code>.
     */
    private void printDefault() {
        if (printDefaultMessage) {
            try {
                String color = "b";
                if (consolePane.getBackground().equals(Color.WHITE))
                    color = "w";

                append(FileProcessor.readDCResource("logo_" + color));
                
            } catch(Exception exc) {
                JOptionPane.showMessageDialog(this,
                        "Error #0002\n"
                      + "Failed to read the Logo from the jar!\n"
                      + exc.getMessage(),
                      "Error Caught", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** 
     * Changes the Style that all input should use. The String passed is the
     * name of the style which is the character for the foreground color and the
     * character for the background color.
     * @param inputColor The new input color string to use for input.
     */
    public void setInputColor(String inputColor) {
        this.inputColor = inputColor;
        setInputAttribute();
    }

    /** 
     * decides to add custom control via text based commands with the
     * DragonConsole.
     * @param newCommandProcessor The new command processor that this console
     *  should send input to.
     */
    public void setCommandProcessor(CommandProcessor newCommandProcessor) {
        if (commandProcessor != null)
            commandProcessor.uninstall();

        commandProcessor = newCommandProcessor;
        commandProcessor.install(this);
    }

    /** 
     * Sets the default character to test for when looking for two character
     * color codes, the default colorCodeChar is '&'.
     * @param colorCodeChar The new character for color codes.
     */
    public void setColorCodeChar(char colorCodeChar) {
        this.colorCodeChar = colorCodeChar;
    }

    /** 
     * Changes the action of the Enter key, if true is passed then when holding
     * the SHIFT key is pressed then ENTER will make create a new line, without
     * pressing SHIFT the ENTER key sends the input text to the output field.
     * If false is passed then pressing the ENTER key with or without SHIFT
     * pressed will pass the text in the input field to the output field.
     * @param inputFieldNewLine True for new line, false if not.
     */
    public void setInputFieldNewLine(boolean inputFieldNewLine) {
        this.inputFieldNewLine = inputFieldNewLine;
    }

    /**
     * Sets the default output color for all uncolored text.
     * @param defaultColor The new default color for console text.
     */
    public void setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
    }

    /**
     * Sets the default color for all Error messages sent to the console.
     * @param errorColor The new color for default error messages.
     */
    public void setErrorColor(String errorColor) {
        this.errorColor = errorColor;
    }

    /**
     * Sets the default font for the console and changes the styles to match.
     * @param consoleFont The new Font for the console to use.
     */
    public void setConsoleFont(Font consoleFont) {
        this.consoleFont = consoleFont;
        this.setOutputStyles();
        consolePane.setFont(consoleFont);
        inputArea.setFont(consoleFont);
        consolePrompt.setPromptFont(consoleFont);

        DocumentStyler.changeFont(consoleStyledDocument, consoleFont);
    }

    /**
     * Sets the default system color for system messages passed to the console.
     * @param systemColor The new color for default system messages.
     */
    public void setSystmeColor(String systemColor) {
        this.systemColor = systemColor;
    }

    /** 
     * This method adds all the basic ANSI colors as Default TextColors as well
     * as orange, purple, and gold which are unique colors to the DragonConsole.
     */
    private void fillConsoleColors() {
        try {
            // ANSI Colors
            addTextColor('r', ANSI.INTENSE_RED); // Red
            addTextColor('R', ANSI.RED); // Dark Red
            addTextColor('l', ANSI.INTENSE_BLUE); // Blue
            addTextColor('L', ANSI.BLUE); // Dark Blue
            addTextColor('g', ANSI.INTENSE_GREEN); // Green
            addTextColor('G', ANSI.GREEN); // Dark Green
            addTextColor('y', ANSI.INTENSE_YELLOW); // Yellow
            addTextColor('Y', ANSI.YELLOW); // Dark Yellow
            addTextColor('x', ANSI.WHITE); // Gray
            addTextColor('X', ANSI.INTENSE_BLACK); // Dark Gray
            addTextColor('c', ANSI.INTENSE_CYAN); // Cyan
            addTextColor('C', ANSI.CYAN); // Dark Cyan
            addTextColor('m', ANSI.INTENSE_MAGENTA); // Magenta
            addTextColor('M', ANSI.MAGENTA); // Dark Magenta

            // DC Only Colors
            addTextColor('o', INTENSE_ORANGE); // Orange
            addTextColor('O', ORANGE); // Orange
            addTextColor('p', INTENSE_PURPLE); // Purple
            addTextColor('P', PURPLE); // Dark Purple
            addTextColor('d', INTENSE_GOLD); // Gold
            addTextColor('D', GOLD); // Dark Gold

            // Black and White -- Single colors (no dark version)
            addTextColor('b', ANSI.BLACK); // Black
            addTextColor('w', ANSI.INTENSE_WHITE); // White
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(this,
                    "Error #0003\n"
                  + "Failed add the default TextColors!\n"
                  + exc.getMessage(),
                  "Error Caught", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 
     * Removes a TextColor from the list of TextColors added to the Console that
     * matches the character code provided. If no such TextColor is found this
     * method will return null. If a TextColor was successfully removed from the
     * list of TextColors then it will also purge the StyledDocument of all
     * Styles containing the Color so that it will no longer render.
     * @param code The Character code of the TextColor to remove.
     * @return The TextColor object that was removed, or null if nothing was
     *  removed.
     */
    public TextColor removeTextColor(char code) {
        TextColor test = TextColor.getTestTextColor(code);
        for (int i = 0; i < textColors.size(); i++) {
            if (textColors.get(i).equals(test)) {
                TextColor remove = textColors.remove(i);
                DocumentStyler.removeColor(consoleStyledDocument, remove, 
                        textColors);

                if (!useInlineInput)
                    consolePrompt.removeColor(remove);

                return remove;
            }
        }

        return null;
    }

    /** 
     * This method will remove all TextColors from the Console as well as the
     * StyledDocument so they will no longer Render.
     */
    public void clearTextColors() {
        for (int i = 0; i < textColors.size(); i++) {
            TextColor temp = textColors.get(i);
            DocumentStyler.removeColor(consoleStyledDocument, temp, textColors);
        }

        textColors.clear();
        if (!useInlineInput)
            consolePrompt.clearColors();
    }

    /** 
     * Removes a TextColor from the list of TextColors added to the Console that
     * matches the Color provided. If no such TextColor is found this
     * method will return null. If a TextColor was successfully removed from the
     * list of TextColors then it will also purge the StyledDocument of all
     * Styles containing the Color so that it will no longer render.
     * @param color The Color of the TextColor to remove.
     * @return The TextColor object that was removed, or null if nothing was
     *  removed.
     */
    public TextColor removeTextColor(Color color) {
        TextColor test = TextColor.getTestTextColor(color);
        for (int i = 0; i < textColors.size(); i++) {
            if (textColors.get(i).equals(test)) {
                TextColor remove = textColors.remove(i);
                DocumentStyler.removeColor(consoleStyledDocument, remove,
                        textColors);

                if (!useInlineInput)
                    consolePrompt.removeColor(remove);

                return remove;
            }
        }

        return null;
    }

    /** 
     * This method is a helper method for <code>setCurrentStyle</code> and
     * determines if there is a valid TextColor that has been added to the
     * console with a matching Character Code. The purpose of this allows
     * <code>setCurrentStyle</code> to bypass character codes that do not
     * represent a valid color and maintain the previous Style.
     * @param code The Character code that needs to be verified.
     * @return <code>true</code> if there is a valid TextColor with this
     *  Character Code.
     */
    private boolean containsColorCode(char code) {
        TextColor test = TextColor.getTestTextColor(code);

        return textColors.contains(test);
    }

    /** 
     * This method takes a Character Code identifier to find the TextColor that
     * the new Color should be added to.
     * @param code The Character Code Identifier used to search for the
     *  TextColor.
     * @param newColor The New Color for the given TextColor.
     */
    public void updateTextColor(char code, Color newColor) {
        TextColor removed = removeTextColor(code);

        if (removed != null) {
            try {
                addTextColor(code, newColor);
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(this,
                        "Error #0004\n"
                      + "Failed to update the TextColor!\n"
                      + exc.getMessage(),
                      "Error Caught", JOptionPane.ERROR_MESSAGE);
            } finally {
                textColors.add(removed);
            }
        }
    }

    /** 
     * This method takes a Color identifier to find the TextColor that
     * the new Character Code should be added to.
     * @param color The Color Identifier used to search for the TextColor.
     * @param newCode The New Character Code for the given TextColor.
     */
    public void updateTextColor(Color color, char newCode) {
        TextColor removed = removeTextColor(color);

        if (removed != null) {
            try {
                addTextColor(newCode, color);
            } catch (Exception exc) {
                JOptionPane.showMessageDialog(this,
                        "Error #0004\n"
                      + "Failed to update the TextColor!\n"
                      + exc.getMessage(),
                      "Error Caught", JOptionPane.ERROR_MESSAGE);
            } finally {
                textColors.add(removed);
            }
        }
    }

    /** 
     * This method adds a new Character to the Console with the given
     * Character code and Color. If '0' (zero) or '-' (hyphen) are passed as a
     * Character code then this method will throw an InvalidCharCodeException.
     * @param code The Character code for the new TextColor.
     * @param color The background color for the new Character code.
     * @throws
     * com.eleet.dragonconsole.util.TextColor.InvalidCharCodeException
     */
    public void addTextColor(char code, Color color) 
            throws com.eleet.dragonconsole.util.TextColor.InvalidCharCodeException {
        TextColor newColor = new TextColor(code, color);
        textColors.add(newColor);
        if (!useInlineInput)
            consolePrompt.addColor(newColor);

        consoleStyledDocument =
                DocumentStyler.addNewColor(consoleStyledDocument, consoleFont, newColor, textColors);
    }

    /** 
     * This method calls <code>fillConsoleColors()</code> to add all the
     * default TextColors to the StyledDocument and then calls
     * <code>setInputAttribute</code> which set the Input TextColor attribute
     * in then inputController based on the inputColor string.
     */
    private void setOutputStyles() {
        fillConsoleColors();
        setInputAttribute();
    }

    /** 
     * This method can be used to ignore input scripts passed to the console
     * via append() and ignore any input from the user.
     * @param ignoreInput true if Input should be disabled, or false if Input
     *  should be enabled.
     */
    public void setIgnoreInput(boolean ignoreInput) {
        this.ignoreInput = ignoreInput;
        this.inputControl.setIgnoreInput(ignoreInput);
    }

    /** 
     * Gets a string representation of the current console version. The version
     * string is formatted as "v" + Version number + Mini Release Version +
     * Bug fix number.
     *
     * All numbers are integers.
     * @return Returns the version string for use with anything.
     */
    public String getVersion() {
        return "v" + VERSION + "." + SUB_VER + "." + BUG_FIX + VER_TAG;
    }

    /** 
     * This method tells the Console if it should carry over input if the user
     * is interrupted by a message while typing or not. Input will only be
     * carried over if the previous input and the current input match (have the
     * same parameters) otherwise the previous input will be lost.
     * @param inputCarryOver <code>true</code> to carry input over
     *  <code>false</code> to ignore it.
     */
    public void setInputCarryOver(boolean inputCarryOver) {
        this.inputCarryOver = inputCarryOver;
    }

    /**
     * Processes a String and prints the String according to all embedded
     * color codes. If called from the CommandProcessor you can add in your
     * own color codes to the String to give a "System" color. ** DEPRECATED Use
     * <code>append(String)</code> instead.
     * @param outputToProcess The string to be color coded and printed.
     * @deprecated Replaced by <code>append(String)</code> as of 3.0.
     */
    @Deprecated
    public void displayString(String outputToProcess) {
        append(outputToProcess);
    }

    /** 
     * This method will convert the DragonConsole Color Codes in the passed
     * String into their ANSI equivalent. This method should be used in place of
     * calling the ANSI class directly.
     * @param toConvert The String that needs to be converted.
     * @return The String after the DragonConsole Codes have been converted to
     *  ANSI Codes.
     */
    public String convertToANSIColors(String toConvert) {
        return ANSI.convertDCtoANSIColors(toConvert, textColors, colorCodeChar);
    }

    /** 
     * This method will convert ANSI Color Codes into their DragonConsole Color
     * Code equivalent. This method should be used in place of calling the ANSI
     * class directly.
     * @param toConvert The String that needs to be converted.
     * @return The String after the ANSI Codes have been converted to
     *  DragonConsole Codes.
     */
    public String convertToDCColors(String toConvert) {
        return ANSI.convertANSIToDCColors(toConvert, textColors, colorCodeChar, defaultColor);
    }

    /** 
     * This method prints the passed output String to the console without
     * processing it for scripts or color codes. This would be used if you
     * wanted to directly post user input to the console to prevent the user
     * from creating his/her own input areas that would cause issues with your
     * program.
     * @param ouput The output to print.
     */
    public void appendWithoutProcessing(String ouput) {
        print(ouput, defaultColor);
        inputControl.setBasicInput(consoleStyledDocument.getLength());
    }

    /**
     * Processes a String and prints the String according to all embedded
     * color codes. If called from the CommandProcessor you can add in your
     * own color codes to the String to give a "System" color.
     * @param outputToProcess The string to be color coded and printed.
     */
    public void append(String outputToProcess) {
        if (!ignoreInput && inputCarryOver && inputControl.isReceivingInput()) {
            inputControl.storeInput();
        }

        boolean hasInput = false;
        String processed = "";

        for (int i = 0; i < outputToProcess.length(); i++) {
            if (outputToProcess.charAt(i) == colorCodeChar) {
                if ( ((i + 1) < outputToProcess.length()) &&
                        (outputToProcess.charAt(i + 1) == colorCodeChar)) {
                    processed += colorCodeChar;
                    i += 1; // Jump past the - (&&)

                } else if ((i + 2) < outputToProcess.length()) {
                    print(processed);
                    processed = "";

                    setCurrentStyle(outputToProcess.substring(i + 1, i + 3));

                    i += 2; // Jump past the two character color code

                } else
                    processed += outputToProcess.charAt(i);

            } else if (outputToProcess.charAt(i) == '\033') {
                if (outputToProcess.indexOf('m', i) < outputToProcess.length()) {
                    print(processed);
                    processed = "";

                    ANSIStyle = ANSI.getANSIAttribute(ANSIStyle,
                            outputToProcess.substring(i,
                                outputToProcess.indexOf('m', i) + 1),
                            consoleStyledDocument.getStyle(defaultColor));

                    i = outputToProcess.indexOf('m', i);
                }
            } else if (outputToProcess.charAt(i) == '%' && !ignoreInput) {

                if ((i + 1) < outputToProcess.length() &&
                        outputToProcess.charAt(i + 1) == '%') {
                    processed += "%";
                    i += 1; // Jump past the "%%"

                } else if (outputToProcess.indexOf(';', i) > i) {
                    if (outputToProcess.charAt(i + 1) == 'i') {
                        hasInput = true;
                        String inputCommand = outputToProcess.substring(i, outputToProcess.indexOf(';', i) + 1);

                        if (inputControl.setInputStyle(inputCommand)) {
                            print(processed);
                            processed = "";

                            inputControl.setRangeStart(consoleStyledDocument.getLength());
                            print(inputControl.getInputRangeString(), defaultColor); // Print the blank space if the input is not infinite

                        } else {
                            outputToProcess = ""; // Clear out the output to process if input is infinite, which means anything after the input string is ignored.
                            
                            print(processed);
                            processed = "";

                            inputControl.setRangeStart(consoleStyledDocument.getLength());
                        }

                        i = outputToProcess.indexOf(';', i);
                    }
                } else
                    processed += outputToProcess.charAt(i);
            } else
                processed += outputToProcess.charAt(i);
        }
        print(processed);

        if (!(hasInput))
            inputControl.setBasicInput(consoleStyledDocument.getLength());

        setConsoleCaretPosition();
    }

    /** 
     * This method processes a color code passed from append and sets the
     * <code>currentStyle</code> variable accordingly.
     * @param code The new color code by which to set
     *  <code>currentStyle</code>.
     */
    protected void setCurrentStyle(String code) {
        String oldStyle = currentStyle;
        currentStyle = "";
        String newStyle = "";
        if (code.length() == 2) {
            if (code.contains("0")) {
                newStyle = defaultColor;
            } else if (code.contains("-")) {
                if (!(code.equals("--"))) {
                    if (code.charAt(0) == '-')
                        newStyle = "" + oldStyle.charAt(0) + code.charAt(1);
                    else
                        newStyle = "" + code.charAt(0) + oldStyle.charAt(1);
                }
            } else
                newStyle = code;

            if (containsColorCode(newStyle.charAt(0)))
                currentStyle += "" + newStyle.charAt(0);
            else
                currentStyle += "" + oldStyle.charAt(0);

            if (containsColorCode(newStyle.charAt(1)))
                currentStyle += "" + newStyle.charAt(1);
            else
                currentStyle += "" + oldStyle.charAt(1);
        } else
            currentStyle = oldStyle;
    }

    /** 
     * This is a helper method for append() which prints the current text out
     * depending on which color code method is set (ANSIStyle or DCCodes). This
     * method was added to cut back on code.
     * @param processed The current String of processed text in append().
     */
    private void print(String processed) {
        if (processed.length() > 0) {
            if (useANSIColorCodes && ANSIStyle != null)
                print(processed, ANSIStyle);
            else
                print(processed, currentStyle);
        }
    }

    /** 
     * Sets the caret position to the start of the current input, if not
     * currently receiving input then it sets the caret position to the end of
     * the document.
     */
    private void setConsoleCaretPosition() {
        int caretLocation = inputControl.getInputRangeStart();
        if (!ignoreInput) {
            if (caretLocation > -1)
                consolePane.setCaretPosition(caretLocation);
            else
                consolePane.setCaretPosition(consoleStyledDocument.getLength());

            if (inputCarryOver && inputControl.hasStoredInput())
                inputControl.restoreInput();

            if (alwaysKeepScrollBarMaxed || !alwaysKeepScrollBarMaxed && isScrollBarAtMax)
                setScrollBarMax();
        }
        else {
            consolePane.setCaretPosition(0);
        }

    }

    /** 
     * This method updates the
     * <code>consoleScrollPane</code>s Vertical <code>JScrollBar</code> to
     * it's maximum value.
     */
    protected void setScrollBarMax() {
        final JScrollBar vBar = consoleScrollPane.getVerticalScrollBar();
        final DragonConsole console = this;
        if (isScrollBarAtMax) {
            SwingUtilities.invokeLater(
                    new Runnable() {
                        public void run() {
                            try {
                                if (vBar.isVisible())
                                    vBar.setValue(vBar.getMaximum() - vBar.getModel().getExtent());

                                console.repaint();
                            } catch (Exception exc) {
                                JOptionPane.showMessageDialog(null,
                                        "Error #0005\n"
                                                + "Failed to set the JScrollBar to Max Value!\n"
                                                + exc.getMessage(),
                                        "Error Caught", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
        }
    }

    /**
     * Prints a message to the console using the default specified System color
     * code. ** DEPRECATED use <code>appendSystemMessage(String)</code>
     * instead.
     * @param message The string to display as a System Message
     * @deprecated Replaced by <code>appendSystemMessage(String)</code> as
     *  of 3.0.
     */
    @Deprecated
    public void displaySystemMessage(String message) {
        appendSystemMessage(message);
    }

    /** 
     * Prints a message to the console using the default specified System color
     * code.
     * @param message The string to display as a System Message
     */
    public void appendSystemMessage(String message) {
        this.print(message, systemColor);
    }

    /** 
     * Prints a message to the console using the default specified Error color
     * code. DEPRECATED use <code>appendErrorMessage(String)</code> instead.
     * @param message The message to display as an Error Message.
     * @deprecated Replaced by <code>appendErrorMessage(String)</code> as of
     *  3.0.
     */
    @Deprecated
    public void displayErrorMessage(String message) {
        appendErrorMessage(message);
    }

    /** 
     * Prints a message to the console using the default specified Error color
     * code.
     * @param message The message to display as an Error Message.
     */
    public void appendErrorMessage(String message) {
        this.print(message, errorColor);
    }

    /**
     * This method adds a string to consolePane's StyledDocument allowing the
     * text to be styled by the predefined color styles.
     * @param output The text string to add to the Console.
     * @param style The color code for this text String.
     */
    protected void print(String output, String style) {
        try { // Try to add the colored string to the output area document
            ignoreAdjustment = true;
            
            if (useInlineInput)
                output = inputControl.getBypassPrefix() + output;

            consoleStyledDocument.insertString(
                    consoleStyledDocument.getLength(), output,
                    consoleStyledDocument.getStyle(style));
        }
        catch (BadLocationException exc) {
            JOptionPane.showMessageDialog(this,
                    "Error #0006\n"
                  + "Failed to print the text with the given Style!\n"
                  + exc.getMessage(),
                  "Error Caught", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method adds a string to consolePane's StyledDocument allowing the
     * text to be styled by the predefined color styles.
     * @param output The text string to add to the Console.
     * @param style The color code for this text String.
     */
    protected void print(String output, SimpleAttributeSet style) {
        try { // Try to add the colored string to the output area document
            ignoreAdjustment = true;

            if (useInlineInput)
                output = inputControl.getBypassPrefix() + output;

            consoleStyledDocument.insertString(
                    consoleStyledDocument.getLength(), output, style);
        }
        catch (BadLocationException exc) {
            JOptionPane.showMessageDialog(this,
                    "Error #0007\n"
                  + "Failed to print the text with the ANSI Style!\n"
                  + exc.getMessage(),
                  "Error Caught", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 
     * This method adds a Previous Entry (the current input that has just been
     * received by the console) to the list of previous entries, and then resets
     * the "location" in the list of Previous Entries the user is currently at
     * to the end of the list.
     * @param entry The new piece of input to add to the Previous Entries list.
     */
    private void addPreviousEntry(String entry) {
        previousEntries.add(entry);
        if (previousEntries.size() > numberOfPreviousEntries)
            previousEntries.remove(0);

        currentPreviousEntry = previousEntries.size();
    }

    /** 
     * If the user navigates to a Previous Entry in the list of entries then
     * this method is called to fill the Input Area with the text of the
     * current Previous Entry. If the Console is using the Inline Input scheme
     * then this entry is only added to the Console if the InputController is
     * receiving input and the current input range is Infinite. If not using the
     * Inline Input Scheme then input is added to the input area.
     */
    private void setPreviousEntryText() {
        String text = "";
        if (currentPreviousEntry < previousEntries.size()
                && currentPreviousEntry >= 0)
            text = previousEntries.get(currentPreviousEntry);

        if (useInlineInput) {
            if (inputControl.isReceivingInput() && inputControl.isInfiniteInput())
                inputControl.setInput(text);
        } else {
            inputArea.setText(text);
            if (alwaysKeepScrollBarMaxed || (!alwaysKeepScrollBarMaxed && isScrollBarAtMax)) {
                ignoreAdjustment = true;
                setScrollBarMax();
            }
        }
    }

    /** Unused */
    public void keyTyped(KeyEvent e) { }

    /**
     * If Using Inline Input:<br />
     *  - ENTER gets input from the InputController and sends it to the<br />
     *    CommandProcessor if there is one. If not it calls appendWithoutProcessing<br />
     * If Not Using Inline Input:<br />
     *  - ENTER gets input from the inputArea and sends it to the CommandProcessor<br />
     *    if there is one, if not it calls appendWithoutProcessing.<br />
     *  - if SHIFT+ENTER is pressed and inputNewLine == true and a "\n\r" is<br />
     *    added to the input area.<br />
     * <br />
     * In BOTH Cases (With SHIFT pressed):<br />
     *  - RIGHT Arrow Key will navigate backwards through the list of previous<br />
     *    entries.<br />
     *  - LEFT Arrow Key will navigate forwards through the list of previous<br />
     *    entries.<br />
     * @param e The KeyEvent that has occurred and should be processed.
     */
    public void keyPressed(KeyEvent e) {
        if (!useInlineInput) {
            if (alwaysKeepScrollBarMaxed || (!alwaysKeepScrollBarMaxed && isScrollBarAtMax)) {
                JScrollBar vBar = consoleScrollPane.getVerticalScrollBar();
                ignoreAdjustment = true;
                setScrollBarMax();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_TAB)
            e.consume();

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!ignoreInput) {
                if (useInlineInput) {
                    e.consume();
                    boolean isProtected = inputControl.isProtected();
                    String input = inputControl.getInput();
                    
                    if (commandProcessor != null)
                        commandProcessor.processCommand(input);
                    else
                        appendWithoutProcessing(input);

                    if (!(isProtected))
                        addPreviousEntry(input);
                    
                } else {
                    if ((!inputFieldNewLine) || !(e.isShiftDown())) {
                        e.consume();

                        String input = inputArea.getText();
                        inputArea.setText("");
                        if (commandProcessor != null)
                            commandProcessor.processCommand(input);
                        else
                            appendWithoutProcessing(input);

                        addPreviousEntry(input);

                    } else {
                        inputArea.append("\n");
                    }
                }
            }
        }

        if ((e.getKeyCode() == KeyEvent.VK_RIGHT) && e.isShiftDown()) {
            e.consume();

            if (!useInlineInput
                    || (useInlineInput
                       && inputControl.isReceivingInput()
                       && inputControl.isInfiniteInput())) {
                currentPreviousEntry--;
                if (currentPreviousEntry < 0)
                    currentPreviousEntry = previousEntries.size();

                setPreviousEntryText();
            }
        }

        if ((e.getKeyCode() == KeyEvent.VK_LEFT) && (e.isShiftDown())) {
            e.consume();

            if (!useInlineInput
                    || (useInlineInput
                       && inputControl.isReceivingInput()
                       && inputControl.isInfiniteInput())) {
            currentPreviousEntry++;
                if (currentPreviousEntry >= previousEntries.size())
                    currentPreviousEntry = previousEntries.size();
                
                setPreviousEntryText();
            }
        }
    }

    /** Not used */
    public void keyReleased(KeyEvent e) { }

    /**
     * If using the Inline Input Scheme this method will prevent the user from
     * using the Arrow Keys to move outside of the Valid Input Range; however,
     * this does not prevent the user from selecting a range of text (to copy
     * per se) outside of the input area.
     * @param e The CaretEvent that needs to be processed.
     */
    public void caretUpdate(CaretEvent e) {
        Caret caret = consolePane.getCaret();
        int location = e.getDot();

        if (!ignoreInput) {
            if (inputControl.isReceivingInput() && e.getDot() == e.getMark()) {
                if (location < inputControl.getInputRangeStart()) {
                    consolePane.setCaretPosition(inputControl.getInputRangeStart());
                    //Toolkit.getDefaultToolkit().beep();
                }

                if (!(inputControl.isInfiniteInput()) && location > inputControl.getInputRangeEnd()) {
                    consolePane.setCaretPosition(inputControl.getInputRangeEnd());
                    //Toolkit.getDefaultToolkit().beep();
                }
            }
        }
    }

    /**
     * This method will watch any adjustments made to the Vertical JScrollBar of
     * the consoleScrollPane to see if it's been adjusted away from or to it's
     * max value. If the JScrollBar is moved away from it's max value then
     * <code>isScrollBarAtMax</code> is set to false. This is used when a
     * user scrolls "up" in the Console to view text, and unless using Inline
     * Input, will prevent the Console from being placed at it's max Value
     * unless the user adds Input to the console.<br />
     * For cases where the ScrollBar is adjusted programmatically a variable is
     * set that will cause this method to essentially do nothing but reset the
     * flag to <code>false</code> which will cause any more events (unless the
     * flag is reset) to be processed.
     * @param e The AdjustmentEvent that needs to be processed.
     */
    public void adjustmentValueChanged(AdjustmentEvent e) {
        JScrollBar sBar = (JScrollBar)e.getSource();
        
        if (!ignoreAdjustment) {
            int value = e.getValue();
            int maxValue = sBar.getMaximum() - sBar.getModel().getExtent();

            if (value == maxValue)
                isScrollBarAtMax = true;
            else
                isScrollBarAtMax = false;
        } else
            ignoreAdjustment = false;
    }
}