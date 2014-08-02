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

package com.eleet.dragonconsole.util;

import javax.swing.JTextPane;
import javax.swing.text.*;
import java.awt.Toolkit;

/**
 * This class is used when the DragonConsole is using the Inline Input Scheme
 * and the purpose of this class is to act as a DocumentFilter to control how
 * all text is added to the DragonConsole. In addition to it's purpose as a
 * DocumentFilter this method also controls the handling of Input by storing it
 * in an InputString (if and when Input is actively being received) and then
 * determines how to display the input.<br /><br />
 * For example if Input is ranged this method will prevent the user from
 * entering input outside of the proper range, and if this input is infinite it
 * will prevent the user from entering anything before the beginning point of
 * the input. If input is protected it will properly store all text in the
 * InputString and display only the protected character in it's place.
 * @author Brandon E Buck
 * @version 1.4
 */
public class InputController extends DocumentFilter {
    /**
     * A String of Characters that can be used to BYPASS the processing in the
     * DocumentFilter methods
     */
    private final String BYPASS = "<DCb />-";

    /**
     * The beginning of a range of input, this will always have a positive
     * value
     */
    private int rangeStart;
    
    /**
     * The end of the input range (if it has a range limit) or '-1' if the
     * input range is Infinite.
     */
    private int rangeEnd;

    /**
     * Determines if the current series of input should be protected, which
     * means that instead of the actual character being displayed it displays
     * the protectedChar.
     */
    private boolean protect;

    /**
     * The InputString that all input is added to as this DocumentFilter
     * receives input form the user.
     */
    private InputString input;

    /**
     * The Character to display in place of actual input when Input is
     * protected.
     */
    private String protectedChar = "*";
    
    /**
     * Boolean flag that determines if Input is actively being take by this
     * InputController.
     */
    private boolean isReceivingInput;

    /**
     * The Attribute to use for styling all input (as it's entered by the user)
     * This is changed by the DragonConsole when the <code>inputColor</code>
     * is changed.
     */
    private AttributeSet inputAttr;

    /**
     * The JTextPane that acts as the actual console (for input/output) in
     * the DragonConsole class. This is here for access to the Document.
     */
    private JTextPane console;

    /**
     * Flag that allows the call to remove to function without any special
     * processing. This flag is only activated when something needs to be
     * removed from the Console programmatically.
     */
    private boolean bypassRemove = false;
    
    /**
     * Flag that determines if the Input is being ignored by the Console. If
     * input is being ignored by the Console then the InputController assumes
     * any additions to the Document are made by the Console and does not
     * process the changes.
     */
    private boolean ignoreInput = false;

    /**
     * Object used to stored all information needed about Input, this is used
     * when Input is interrupted by a new append. If Input is interrupted then
     * it's current state is stored in this StoredInput Object and if the next
     * series of Input matches the StoredInput, input will be restored as it was
     * before the interruption.
     */
    private StoredInput stored = null;
    
    /**
     * Flag that determines which input method is being used by the Console.
     * This flag is <code>true</code> for Inline Input and 
     * <code>false</code> if otherwise.
     */
    private boolean consoleInputMethod = true;

    /**
     * Default constructor
     * rangeStart - The beginning of the input range, will always contain a
     *    value
     * rangeEnd - The end of the input range, -1 if the range has no limit
     *    or the max value of the input range
     * protect - true or false, whether or not the input needs to be
     *    protected
     * protectedText - if the input needs to be protected the actual input
     *    will be stored here.
     */
    public InputController(AttributeSet attr) {
        super();
        rangeStart = 0;
        rangeEnd = 0;
        protect = false;
        input = new InputString("");
        isReceivingInput = false;
        inputAttr = attr;
        console = null;
    }

    /** 
     * Changes the default <code>AttributeSet</code> for this
     * <code>InputController</code> that is used to style all input.
     * @param newInputAttr The new <code>AttributeSet</code> to use
     */
    public void setInputAttributeSet(AttributeSet newInputAttr) {
        inputAttr = newInputAttr;
    }

    /** 
     * This method is called once the JTextPane has been initialized in the
     * initializeConsole() method of the DragonConsole class to make sure
     * that the console variable in this InputController is the proper
     * JTextPane.
     * @param jtp The JTextPane to set as the console.
     */
    public void installConsole(JTextPane jtp) {
        console = jtp;
    }

    /** 
     * This method is used to determine if the Input is currently being
     * protected. An important note is that this method will return false if
     * input is not being protected, as well as if this InputController is not
     * currently receiving input.
     * @return <code>true</code> if Input is being protected.
     */
    public boolean isProtected() {
        return protect;
    }

    /** 
     * This method returns the BYPASS prefix that should be added to any String
     * that needs to bypass the DocumentFilter.
     * @return The BYPASS prefix used to bypass the DocumentFilter.
     */
    public String getBypassPrefix() {
        return BYPASS;
    }

    /** 
     * This method is used to reset the current state of input in this
     * InputController. This method will also set
     * <code>isReceivingInput</code>.
     */
    public void reset() {
        rangeStart = -1;
        rangeEnd = 0;
        protect = false;
        input = new InputString("");
        isReceivingInput = false;
    }

    /** 
     * This method is used to tell the InputController the method of input
     * that is being used by the DragonConsole this InputController belongs to.
     * @param consoleInputMethod The currentInputMethod used by the
     *  DragonConsole.
     */
    public void setConsoleInputMethod(boolean consoleInputMethod) {
        this.consoleInputMethod = consoleInputMethod;
    }

    /** 
     * Used by the DragonConsole when it's told to IgnoreInput to relay the
     * information to this InputController so that it, too, can ignore input.
     * @param ignoreInput
     */
    public void setIgnoreInput(boolean ignoreInput) {
        this.ignoreInput = ignoreInput;
    }

    /** 
     * This method is called when Text is pasted to the Console while Inline
     * Input is being used, or when Input is restored.
     * @param newInput The new Input text to add to the Document.
     */
    public void setInput(String newInput) {
        if (isReceivingInput && isInfiniteInput()) {
            StyledDocument doc = console.getStyledDocument();
            try {
                int length = doc.getLength() - rangeStart;
                bypassRemove = true;
                doc.remove(rangeStart, length);

                String prefix = "";
                if (consoleInputMethod) // True if inline
                    prefix = BYPASS;

                if (protect)
                    doc.insertString(rangeStart, prefix + getProtectedString(newInput.length()), inputAttr);
                else
                    doc.insertString(rangeStart, prefix + newInput, inputAttr);

                input = new InputString("");
                input.append(newInput);
            } catch (Exception exc) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Error #0011\n"
                      + "Failed to set the input in the Document!\n"
                      + exc.getMessage(),
                      "Error Caught", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** 
     * This method was moved out of it's previous location in
     * <code>setInputStyle</code> to make it much easier for the DragonConsole
     * to set the proper start location of this input. If the input type is
     * ranged this method will adjust the <code>rangeEnd</code> value to
     * hold the proper ending range index of this Input.
     * @param newRangeStart The new location of the beginning of this input.
     */
    public void setRangeStart(int newRangeStart) {
        if (isReceivingInput) {
            rangeStart = newRangeStart;
            if (rangeEnd > 0)
                rangeEnd += newRangeStart;
        }
    }

    /** 
     * Passed an input String from the console and breaks it down and sets
     * up the input controller according the input string passed to it.
     * The input strings format is:
     *      %i#[+|-|];
     *
     * Accepted minimum is "%i;" which means input from the position of %
     * forward.
     *
     * The # can be any number (int) that specifies the amount of characters
     * that fit in this input range.
     *
     * The "[+|-|]" means that either a "+", "-", or blank space. "-" and
     * "" both mean the same thing, unprotected input, if a "+" is present
     * that means the input is protected.
     *
     * If there is an error, or the minimum is passed ("%i;") then input is
     * treated as "UNLIMITED"
     *
     * <strong>IMPORTANT: The Start position MUST be specified after the
     * input style is set. This was added in to give the console more
     * control as to where the cursor needs to default for input control and
     * make input ranges MUCH more accurate.</strong>
     * @param newInputStyle String containing the new input style for the input
     *  controller.
     * @return Returns true if all text after the input command should be
     *  ignored (if the input has an unlimited Range);
     */
    public boolean setInputStyle(String newInputStyle) {
        rangeStart = -1;
        rangeEnd = 0;
        protect = false;
        input = new InputString("");
        isReceivingInput = true;

        if (newInputStyle.equals("%i;")) {
            rangeEnd = -1;
            return false;
        } else {
            String inputStyle = newInputStyle.substring(2); // Chop off the "%i"
            inputStyle = inputStyle.substring(0, inputStyle.length() - 1); // Chop off the ";"

            if (inputStyle.length() > 0) {
                if (inputStyle.charAt(inputStyle.length() - 1) == '+' || inputStyle.charAt(inputStyle.length() - 1) == '-') {
                    char tempProtect = inputStyle.charAt(inputStyle.length() - 1);
                    inputStyle = inputStyle.substring(0, inputStyle.length() - 1);

                    if (tempProtect == '+')
                        protect = true;
                }

                if (inputStyle.length() > 0) {
                    rangeEnd = Integer.parseInt(inputStyle);

                    input.set(getInputRangeString());

                    return true;
                } else {
                    rangeEnd = -1;

                    return false;
                }

            } else {
                rangeEnd = -1;
                return false;
            }
        }
    }

    /** 
     * The location the input range starts, used for programmatically
     * setting the outputPane's Caret position.
     * @return The start position of the input, if not currently receiving
     * input then -1
     */
    public int getInputRangeStart() {
        if (isReceivingInput)
            return rangeStart;
        else
            return -1;
    }

    /** 
     * This method is used in conjuction with <code>clearText()</code> from
     * the DragonConsole class and removes all text from the Document as well
     * as reset the current state of input (if any).
     */
    public void clearText() {
        reset();
        bypassRemove = true;
        StyledDocument doc = console.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch(Exception exc) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Error #0012\n"
                  + "Failed to clear the text in Document!\n"
                  + exc.getMessage(),
                  "Error Caught", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Returns the ending location of the current input.
     * This method returns the ending location (in the StyledDocument) of the
     * current Input. This method is "-1" if input is Infinite (or if not
     * receiving input).
     * @return The end of the Input range, or -1 if Input is Infinite.
     */
    public int getInputRangeEnd() {
        return rangeEnd;
    }

    /** 
     * Returns true if this input has no maximum range, or false if the
     * input is limited by a range.
     * @return true if input has no limited range, or false if it does.
     */
    public boolean isInfiniteInput() {
        return (rangeEnd == -1);
    }

    /** 
     * Sets input to it's basic level which is an unlimited number of
     * characters after the startPosition which is the equivalent of calling
     * <code>setInputStyle("%i;");</code>. This method is called from
     * the append method if no input script was detected in the output. (By
     * default, all output MUST have input).
     * @param startPosition The last position of output, and the start of the
     *  input.
     */
    public void setBasicInput(int startPosition) {
        rangeStart = startPosition;
        rangeEnd = -1;
        protect = false;
        input = new InputString("");
        isReceivingInput = true;
    }

    /** 
     * This method creates a String of "blank spaces" which are used as space
     * holders for ranged input. The number of blank spaces that fill this
     * String is equal to the length of the Range for this input as determined
     * by (rangeEnd - rangeStart).
     * @return A String of Spaces equal to the length of the Input range.
     */
    public String getInputRangeString() {
        if (isReceivingInput && rangeEnd > 0) {
            String inputRangeString = "";
            int counter = 0;
            if (rangeStart >= 0)
                counter = rangeStart;

            for (int x = counter; x < rangeEnd; x++)
                inputRangeString += " ";

            return inputRangeString;
        } else
            return "";
    }

    /**
     * Sets a custom protected character if '*' is not desired.
     * @param protectedChar The new protected char.
     */
    public void setProtectedChar(char protectedChar) {
        this.protectedChar = "" + protectedChar;
    }

    /** 
     * This method is used to determine if the InputController has input
     * controls in place for current input. This is used to help prevent
     * tampering with output while
     * @return <code>true</code> if this InputController is actively
     *  receiving and processing input.
     */
    public boolean isReceivingInput() {
        return isReceivingInput;
    }

    /** 
     * This method will return the current text stored in the InputString that
     * has been trimmed.
     * @return The Text in the InputString that has been trimmed.
     */
    public String getInput() {
        isReceivingInput = false;
        return input.get().trim();
    }

    /** 
     * Creates a String of nothing but Protected Characters with the length that
     * is given. Used when bulk text is pasted/added to the Document if input
     * is protected.
     * @param length The number of Protected Characters to fill this String
     *  with.
     * @return The String of Protected Characters of the specified length.
     */
    private String getProtectedString(int length) {
        String pString = "";
        for (int i = 0; i < length; i++)
            pString += protectedChar;

        return pString;
    }

    /** 
     * This method is used to create a Protected String that mimics a Ranged
     * input String by replacing all characters (aside from a space) with a
     * protected character.
     * @param string The String to create a Protected String of.
     * @return The Protected String that "mimics" the given String.
     */
    private String restoreProtectedString(String string) {
        String returnString = "";

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != ' ')
                returnString += protectedChar;
            else
                returnString += " ";
        }

        return returnString;
    }

    /** 
     * If the String starts with the BYPASS prefix this method adds it to the
     * Document with the given Attribute. If not, then the program will
     * determine if input is protected or not and add the bulk input accordingly
     * as well as update the InputString with the new input.
     * @param fb The FilterBypass Object used to bypass this DocumentFilter.
     * @param offset The location in the Document to place this String.
     * @param string The String to add to the Document at the given location.
     * @param attr The Attribute to use only if bypassing the processing.
     * @throws BadLocationException
     */
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string.startsWith(BYPASS)) {
            string = string.substring(BYPASS.length());
            fb.insertString(offset, string, attr);
        } else if (isReceivingInput && isInfiniteInput() && offset >= rangeStart) {
            if (protect)
                fb.insertString(offset, getProtectedString(string.length()), inputAttr);
            else
                fb.insertString(offset, string, inputAttr);

            input.insert(offset - rangeStart, string);
        } else
            Toolkit.getDefaultToolkit().beep();
    }

    /** 
     * This method will replace the portion of text with all the given
     * attributes if the String starts with the BYPASS prefix. If it doesn't
     * then it will determine if Input is infinite, or ranged. If Input is
     * Ranged it will attempt to insert the String in the InputString and if
     * that succeeds then it inserts the text into the Document with the
     * inputAttr. If Input is Infinite then this will replace the Text as long
     * as it's after the beginning of this Input and then adds the String to
     * the InputString.
     * @param fb The FilterBypass Object used to bypass this DocumentFilter.
     * @param offset The location in the Document to place this String.
     * @param length The length of the Range that is being replaced.
     * @param string The String to insert in place of the Replaced portion.
     * @param attr The Attribute to use only if bypassing the processing.
     * @throws BadLocationException
     */
    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
        if (string.startsWith(BYPASS)) {
            String newString = string.substring(BYPASS.length());
            if (protect)
                newString = restoreProtectedString(newString);
            
            fb.replace(offset, length, newString, attr);
            
        } else {
            if (!ignoreInput) {
                if (isReceivingInput && rangeStart > 0) {
                    if (offset >= rangeStart) {
                        if (!isInfiniteInput() && (offset + 1) <= rangeEnd) {
                            boolean inserted = input.rangeInsert((offset - rangeStart), string);

                            if (inserted) {
                                if (protect)
                                    fb.replace(offset, length, protectedChar, inputAttr);
                                else
                                    fb.replace(offset, length, string, inputAttr);

                                if (input.endIsEmpty())
                                    fb.remove(rangeEnd - 1, 1);
                                else
                                    fb.remove(rangeEnd, 1);
                            } else
                                Toolkit.getDefaultToolkit().beep();



                        } else if (isInfiniteInput()) {
                            if (protect)
                                fb.replace(offset, length, protectedChar, inputAttr);
                            else
                                fb.replace(offset, length, string, inputAttr);

                            input.replace((offset - rangeStart), length, string);
                        } else
                            Toolkit.getDefaultToolkit().beep();
                    } else
                        Toolkit.getDefaultToolkit().beep();
                } else
                    Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    /** 
     * Removes a given range from the Document. This method will bypass
     * processing if <code>bypassRemove</code> is set to <code>true</code>
     * but otherwise it will process according to the type of Input. If Input is
     * Ranged it will the character form the Document and replace it with a
     * space, as well as remove from the InputString. If Input is Infinite it
     * will remove it from the Document as well as the InputString.
     * @param fb The FilterBypass Object used to bypass this DocumentFilter.
     * @param offset The location in the Document to place this String.
     * @param length The length of the Range that is being replaced.
     * @throws BadLocationException
     */
    @Override
    public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {
        if (!ignoreInput) {
            if (!bypassRemove) {
                if (isReceivingInput && rangeStart > 0) {
                    if (!(offset < rangeStart)) {
                        if (!isInfiniteInput()) {
                            fb.remove(offset, length);
                            fb.insertString((rangeEnd - 1), " ", inputAttr);

                            if (console.getCaretPosition() == rangeEnd)
                                console.setCaretPosition(rangeEnd - 1);
                        } else
                            fb.remove(offset, length);


                        if (!isInfiniteInput())
                            input.rangeRemove((offset - rangeStart), length);
                        else
                            input.remove((offset - rangeStart), length);
                    } else
                        Toolkit.getDefaultToolkit().beep();
                } else
                    Toolkit.getDefaultToolkit().beep();
            } else {
                bypassRemove = false;
                fb.remove(offset, length);
            }
        }
    }

    /** 
     * This method is used to determine if there is any StoredInput or not. Used
     * to determine if Input should be restored or not.
     * @return <code>true</code> if there is StoredInput, or else it returns
     *  false.
     */
    public boolean hasStoredInput() {
        return (stored != null);
    }

    /** 
     * This method will store the information needed to later determine if it
     * can restore Input as well as actually restore input.
     */

    public void storeInput() {
        stored = new StoredInput(isInfiniteInput(), protect, (rangeEnd - rangeStart), input);
        reset();
    }

    /** 
     * Restores the StoredInput if, and only if, the settings match the input
     * settings that are in the StoredInput.
     * @return <code>true</code> if Input was restored or not.
     */
    public boolean restoreInput() {
        if (stored != null && isReceivingInput) {
            if (stored.matches(isInfiniteInput(), protect, (rangeEnd - rangeStart))) {
                input = stored.getInput();
                
                int end;
                if (isInfiniteInput())
                    end = 0;
                else
                    end = rangeEnd - rangeStart;
                try {
                    if (consoleInputMethod)
                        ((AbstractDocument)console.getStyledDocument()).replace(rangeStart, end, BYPASS + input.get(), inputAttr);
                    else
                        ((AbstractDocument)console.getStyledDocument()).replace(rangeStart, end, input.get(), inputAttr);
                
                } catch (Exception exc) {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Error #0013\n"
                          + "Failed to restore the Input!\n"
                          + exc.getMessage(),
                          "Error Caught", javax.swing.JOptionPane.ERROR_MESSAGE);
                }

                stored = null;

                return true;
            }
        }

        stored = null;

        return false;
    }

    /**
     * This Class is used to save a single state of Input with the possibility
     * to restore it later.
     */
    private class StoredInput {
        private boolean isInfinite;
        private boolean protect;
        private int range;
        private InputString input;

        /** 
         * Constructs a new StoredInput with the given State of input
         */
        public StoredInput(boolean isInfinite, boolean protect, int range, InputString input) {
            this.isInfinite = isInfinite;
            this.protect = protect;
            this.range = range;
            this.input = input;
        }

        /** 
         * This method determines if the given Input settings match the Input
         * settings that have been Stored in this Object.
         * @param isInfinite If the Current state of input is Infinite.
         * @param protect If the Current input is being protected.
         * @param range The Current range (if any) of the the input.
         * @return <code>true</code> if the Given input settings match those
         *  Stored.
         */
        public boolean matches(boolean isInfinite, boolean protect, int range) {
            if (this.isInfinite == isInfinite) {
                if (!this.isInfinite) {
                    if (this.range == range && this.protect == protect)
                        return true;
                } else {
                    if (this.protect == protect)
                        return true;
                    else
                        return false;
                }
            }

            return false;
        }

        /**
         * Returns the InputString of this StoredInput.
         * @return The InputString of this StoredInput.
         */
        public InputString getInput() {
            return input;
        }
    }
}
