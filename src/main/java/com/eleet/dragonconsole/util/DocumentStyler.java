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

import java.awt.*;
import javax.swing.text.*;
import java.util.ArrayList;

/** 
 * This class is designed to control and make easier the addition of text
 * styles to the StyledDocument of the Consoles JTextPane component.
 * The DocumentStyler is a static class with numerous helper methods for adding
 * styles, however it should never be used outside of the DragonConsole class
 * or any extension of this class (unless you are using it outside of the
 * DragonConsole implementation) because the DocumentStyler was written with a
 * specific use in mind and not as a general utility.
 * @author Brandon E Buck
 * @version 1.3
 */
public class DocumentStyler {
    /** 
     * Uses the char codes from each TextColor object passed to create a Style
     * name and then adds the Style to the StyledDocument with the appropriate
     * foreground and background colors supplied by the TextColor objects.
     * @param documentToUpdate The StyledDocument the colors will be added to.
     * @param styleFont The font for each style to use.
     * @param foreground The TextColor that represents the foreground color.
     * @param background The TextColor that represents the background color.
     * @return The altered StyledDocument with the new Style.
     */
    private static StyledDocument addNewStyle(StyledDocument documentToUpdate,
            Font styleFont, TextColor foreground, TextColor background) {
        String styleName = "" + foreground.getCharCode() + background.getCharCode();
        
        Style parentStyle = StyleContext.getDefaultStyleContext()
                .getStyle(StyleContext.DEFAULT_STYLE);

        Style temp = documentToUpdate.addStyle(styleName, parentStyle);
        //setStyleFont(temp, styleFont);
        StyleConstants.setForeground(temp, foreground.getColor());
        StyleConstants.setBackground(temp, background.getColor());

        return documentToUpdate;
    }
    
    /** 
     * same Font style as newFont. This method is a helper, it sets the 
     * SimpleAttributeSets Font Family, Font Size, and whether or not the Font 
     * is Bold and/or Italic.
     * @param sas The SimpleAttributeSet the Font style needs to be saved in.
     * @param newFont The newFont that needs to be set to the Style object.
     * @return The modified SimpleAttributeSet.
     */
    private static SimpleAttributeSet setSASFont(SimpleAttributeSet sas, Font newFont) {
        StyleConstants.setFontFamily(sas, newFont.getFamily());
        StyleConstants.setFontSize(sas, newFont.getSize());
        StyleConstants.setBold(sas, newFont.isBold());
        StyleConstants.setItalic(sas, newFont.isItalic());

        return sas;
    }

    /** 
     * Changes the Font attribute of all styles currently in a Document to a
     * new Font, this method is called when the consoleFont is changed.
     * @param documentToUpdate The StyledDocument containing the text to be
     *  changed.
     * @param newFont The new Font to show in the StyledDocument.
     */
    public static StyledDocument changeFont(StyledDocument documentToUpdate, Font newFont) {
        SimpleAttributeSet newFontStyle = new SimpleAttributeSet();
        newFontStyle = setSASFont(newFontStyle, newFont);
        documentToUpdate.setCharacterAttributes(0, documentToUpdate.getLength(), newFontStyle, false);

        return documentToUpdate;
    }

    /**  
     * This methods adds the new color as a foreground color for all
     * available background colors as well as a background for all available
     * foreground colors. This method should never be called directly, instead
     * it should be indirectly called through <code>DragonConsoles</code>
     * <code>addTextColor(char, Color)</code> method.
     * @param documentToUpdate The StyledDocument the new Color is to be added to.
     * @param consoleFont The Font these styles will use.
     * @param newColor The TextColor that needs to be added as a foreground and
     *  background.
     * @param textColors The list of colors already in the Console, used so the
     *  newColor can have each background and foreground of previously added colors.
     * @return The modified StyledDocument.
     */
    public static StyledDocument addNewColor(StyledDocument documentToUpdate,
            Font consoleFont, TextColor newColor,
            ArrayList<TextColor> textColors) {
        for (int i = 0; i < textColors.size(); i++) {
            TextColor tc = textColors.get(i);
            documentToUpdate = addNewStyle(documentToUpdate, consoleFont, newColor, tc); // Give the New Color every background
            documentToUpdate = addNewStyle(documentToUpdate, consoleFont, tc, newColor); // Give Every Color the new background
        }

        return documentToUpdate;
    }

    /** 
     * This method will remove any and all Styles in the StyledDocument that
     * contain it as a foreground or background.
     * @param documentToUpdate The document the color should be removed from.
     * @param remove The Color that need to be removed.
     * @param colors The list of Colors that have been added to the StyledDocument.
     * @return The StyledDocument after the colors have been removed.
     */
    public static StyledDocument removeColor(StyledDocument documentToUpdate,
            TextColor remove, ArrayList<TextColor> colors) {
        // Remove this here since this will be called after it's been removed from the list
        documentToUpdate.removeStyle("" + remove.getCharCode() + remove.getCharCode());

        for (int i = 0; i < colors.size(); i++) {
            TextColor temp = colors.get(i);

            String s1 = "" + temp.getCharCode() + remove.getCharCode();
            String s2 = "" + remove.getCharCode() + temp.getCharCode();

            documentToUpdate.removeStyle(s1);
            documentToUpdate.removeStyle(s2);
        }

        return documentToUpdate;
    }
}