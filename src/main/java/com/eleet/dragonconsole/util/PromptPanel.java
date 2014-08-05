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

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

/**
 * This class is used with the alternative method of input (not Inline Input).
 * The purpose of this class is as an expanding panel that contains an editable
 * PromptLabel (editable meaning it can be changed during program execution) and
 * the InputArea that expands automatically as text wraps.
 * @author Brandon E Buck
 */
public class PromptPanel extends JPanel {
    private PromptLabel promptLabel;

    /**
     * Constructs a new PromptPanel, and initializes the promptLabel with the
     * values passed.
     * @param prompt String representation of the Prompt that you want
     *  displayed before the <code>inputArea</code>.
     * @param defaultColor The default color code from the DragonConsole, used
     *  for coloring the PromptPanel.
     */
    public PromptPanel(String prompt, String defaultColor) {
        promptLabel = new PromptLabel(prompt, defaultColor);
        promptLabel.setOpaque(false);
        setLayout(new BorderLayout());
        add(promptLabel, BorderLayout.NORTH);
    }

    /**
     * This method will set the text of the <code>promptLabel</code> to the
     * new String given.
     * @param newPrompt String containing the new prompt for the PromptPanel to
     *  display.
     */
    public void setPrompt(String newPrompt) {
        promptLabel.setText(newPrompt);
    }

    /**
     * This method is used to prevent the programmer from getting access to the
     * PromptLabel. It sends the parameter given to the same method within the
     * PromptLabel.
     * @param defaultColor The new defaultColor that has been changed in the
     *  DragonConsole.
     */
    public void setDefaultColor(String defaultColor) {
        promptLabel.setDefaultColor(defaultColor);
    }

    /**
     * This method is used to prevent the programmer from getting access to the
     * PromptLabel. It sends the parameter given to the same method within the
     * PromptLabel.
     * @param color The new TextColor that needs to be added to the list of
     *  colors in the PromptLabel.
     */
    public void addColor(TextColor color) {
        promptLabel.addColor(color);
    }

    /**
     * This method is used to prevent the programmer from getting access to the
     * PromptLabel. It calls the method within the PromptLabel with the same
     * name.
     */
    public void clearColors() {
        promptLabel.clearColors();
    }

    /**
     * This method is used to prevent the programmer from getting access to the
     * PromptLabel. It sends the parameter given to the same method within the
     * PromptLabel.
     * @param color The TextColor that needs to be removed from the list of
     *  TextColors in the PromptLabel.
     */
    public void removeColor(TextColor color) {
        promptLabel.removeColor(color);
    }

    /**
     * This method is used to prevent the programmer from getting access to the
     * PromptLabel. It sends the parameter given to the same method within the
     * PromptLabel.
     * @param colorCodeChar The new colorCodeChar if it's been changed in the
     *  DragonConsole.
     */
    public void setColorCodeChar(char colorCodeChar) {
        promptLabel.setColorCodeChar(colorCodeChar);
    }

    /**
     * Returns the prompt, stripped of color codes, from the PromptLabel.
     * @return The text in the PromptLabel stripped of DCCC codes.
     */
    public String getPrompt() {
        return promptLabel.getText();
    }

    /**
     * Changes the font of the PromptLabel so that it reflects the same font as
     * that of the DragonConsole.
     * @param font The new Font that should be used by the PromptLabel.
     */
    public void setPromptFont(Font font) {
        promptLabel.setFont(font);
        promptLabel.revalidate();
        promptLabel.repaint();
    }

    /**
     * Sets the default foreground color of the PromptLabel so that prompts that
     * are not colored by DCCCs will still feel like part of the console.
     * @param c The new Color to use for the Foreground for the PromptLabel.
     */
    public void setPromptForeground(Color c) {
        promptLabel.setForeground(c);
        promptLabel.revalidate();
        promptLabel.repaint();
    }

    /**
     * This class is the label that displays the Prompt in the PromptPanel. It
     * contains all the TextColors and Color information that is stored within
     * the DragonConsole. The PromptLabel is capable of processing DCCCs and
     * coloring the prompt properly (foreground and background) but it is not
     * capable of processing ANSI Color Codes even if ANSI support is enabled.
     */
    private class PromptLabel extends JLabel {
        private ArrayList<TextColor> colors;
        private char colorCodeChar = '&';
        private String defaultColor;

        /**
         * Constructs a new PromptLabel with the given text as a prompt and the
         * defaultColor that is given.
         * @param text The text to use as the initial prompt.
         * @param defaultColor The initial defaultColor of the DragonConsole.
         */
        public PromptLabel(String text, String defaultColor) {
            super(text);
            this.defaultColor= defaultColor;
            this.colors = new ArrayList<TextColor>();
        }

        /**
         * Adds a new TextColor to the list of TextColors in the PromptLabel,
         * these Colors are used for processing the DCCCs in the paint method to
         * properly display the Prompt.
         * @param color The new TextColor to add to the list of TextColors.
         */
        public void addColor(TextColor color) {
            this.colors.add(color);
            this.repaint();
        }

        /**
         * Removes a TextColor from the list of TextColors. This method is
         * called when a TextColor is removed from the DragonConsole to keep
         * the two lists equivalent.
         * @param color The TextColor to remove from the list of TextColors.
         */
        public void removeColor(TextColor color) {
            this.colors.remove(color);
            this.repaint();
        }

        /**
         * Changes the default color String to the new String. This method is
         * called when the defaultColor is altered in the DragonConsole.
         * @param defaultColor The new two character defaultColor String.
         */
        public void setDefaultColor(String defaultColor) {
            this.defaultColor = defaultColor;
        }

        /**
         * Removes all of the TextColors from the list of TextColors, this
         * method is called when the TextColor list is cleared in the
         * DragonConsole.
         */
        public void clearColors() {
            this.colors.clear();
        }

        /**
         * Sets the colorCodeCharacter used to process DCCCs, this method is
         * called when the colorCodeChar is altered in the DragonConsole so
         * that processing DCCCs works the same for the Prompt and adding text
         * to the Console.
         * @param colorCodeChar The new colorCodeChar to note the beginning of
         *  a DCCC.
         */
        public void setColorCodeChar(char colorCodeChar) {
            this.colorCodeChar = colorCodeChar;
        }

        /**
         * Overrides the default paintComponent so that each character is
         * processed in the String to determine if it's the start of a DCCC and
         * if not it adds it to a String of "processed" characters. When a
         * DCCC is encountered it paints the text at the proper location with
         * the current style and then continues processing the prompt String for
         * mor DCCCs until it finished.
         * @param g The pre built Graphics object for rendering this component.
         */
        @Override
        public void paintComponent(Graphics g) {
            String text = super.getText(); // Get text withot chopping codes off
            int startx = 0;

            String processed = "";
            String style = defaultColor;
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == colorCodeChar) {
                    if ( ((i + 1) < text.length()) &&
                            (text.charAt(i + 1) == colorCodeChar)) {
                        processed += colorCodeChar;
                        i += 1; // Jump past the - (&&)

                    } else if ((i + 2) < text.length()) {
                        startx = paintProcessed(processed, style, g,
                                startx);
                        processed = "";

                        style = setCurrentStyle(text.substring(i + 1, i + 3), style);

                        i += 2; // Jump past the two character color code

                    } else
                        processed += text.charAt(i);
                } else
                    processed += text.charAt(i);
            }

            paintProcessed(processed, style, g, startx);
        }

        /**
         * Paints a portion of the processed Prompt with the Colors according
         * to the given style. The background and foreground will painted from
         * the x coordinate giving and if anything is painting this method will
         * return the x coordinate where the next portion of text should be
         * painted.
         * @param processed The portion of the String that needs to be painted.
         * @param style The Style containing the Colors to paint the String
         *  with.
         * @param g The Graphics Object that will do the painting.
         * @param x The x coordinate that tells where to begin painting.
         * @return The ending x coordinate so that the next String to get
         *  painted will not overlap any other String.
         */
        private int paintProcessed(String processed, String style, Graphics g, int x) {
            if (processed.length() > 0) {
                ((Graphics2D)g).setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


                Rectangle2D bounds = g.getFontMetrics().getStringBounds(processed, g);

                int w = (int)(bounds.getWidth());
                int h = (int)(bounds.getHeight());
                int newX = x + (int)(bounds.getWidth());
                int y = h - 3;

                g.setColor(getColorFromDCCC(style.charAt(1)));
                g.fillRect(x, 0, w, h);

                g.setColor(getColorFromDCCC(style.charAt(0)));
                g.drawString(processed, x, y);

                return newX;
            }

            return x;
        }

        /** .
         * This method processes a color code passed from append and sets the
         * <code>currentStyle</code> variable accordingly.
         * @param code The new color code by which to set
         *  <code>currentStyle</code>.
         */
        private String setCurrentStyle(String code, String currentStyle) {
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

                currentStyle = newStyle;

            } else
                currentStyle = oldStyle;
            
            return currentStyle;
        }

        /**
         * Searches the list of TextColors for the one associated with the
         * given character and returns the Color associated with it.
         * @param code The character code to find the Color for.
         * @return The Color that is assigned to this Character code.
         */
        private Color getColorFromDCCC(char code) {
            TextColor test = TextColor.getTestTextColor(code);
            for (int i = 0; i < colors.size(); i++) {
                if (colors.get(i).equals(test))
                    return colors.get(i).getColor();
            }

            return Color.GRAY.brighter();
        }

        /**
         * Returns the text stored in this label without the DCCC codes.
         * @return The text stored in this label without the DCCC codes.
         */
        @Override
        public String getText() {
            return trimDCCC(super.getText());
        }

        /**
         * Steps through a String character by character and removes all of the
         * DCCCs that begin with the <code>colorCodeChar</code> and returns
         * the String without any DCCCs present.
         * @param toTrim The String with DCCCs that needs to be trimmed.
         * @return The String after all the DCCCs have been removed from it.
         */
        private String trimDCCC(String toTrim) {
            StringBuilder buffer = new StringBuilder(toTrim);
            for (int i = 0; i < buffer.length(); i++) {
                if (buffer.charAt(i) == colorCodeChar) {
                    if (buffer.charAt(i) == colorCodeChar) {
                        if ( ((i + 1) < buffer.length()) &&
                                (buffer.charAt(i + 1) == colorCodeChar)) {
                            buffer.replace((i + 1), (i + 2), "");
                            i += 1; // Jump past the - (&&)

                        } else if ((i + 2) < buffer.length()) {
                            buffer.replace(i, i + 3, "");
                        }
                    }
                }
            }

            return buffer.toString();
        }
    }
}
