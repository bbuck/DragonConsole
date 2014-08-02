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

import java.awt.Color;

/**
 * This Class is a representation of the Styles that are added to the
 * StyledDocuemnt. Each TextColor contains a Character and a Color. The
 * Character is the "code" that will signal to use this Color as the foreground
 * or background (whichever position it's placed in the DCCC). TextColors
 * cannot have the Characters '0' or '-' used to represent a color because they
 * are special Characters used by the DragonConsole when processing DCCCs.
 * @author Brandon E Buck
 */
public class TextColor implements Comparable {
    private char charCode;
    private Color color;

    /**
     * This Constructor will create a new TextColor with the given Character
     * as the code for the Color passed. If '0' (zero) or '-' (hyphen) are
     * passed as the Character code then an InvalidCharCodeException is thrown
     * because both of those Characters are reserved by the DragonConosle.
     * @param charCode The Character Code that represents the given Color.
     * @param color The Color represented by this TextColor.
     * @throws com.eleet.dragonconsole.util.TextColor.InvalidCharCodeException
     */
    public TextColor(char charCode, Color color) throws InvalidCharCodeException {
        if (charCode == '0' || charCode == '-')
            throw new InvalidCharCodeException("The char \'" + charCode + "\' is reserved and cannot be used.");

        this.charCode = charCode;
        this.color = color;
    }

    /**
     * This Constructor is only called from an static method in this class and
     * is used for testing purposes only. A TextColor object is created with
     * the given Character as a code but the Color is set to null.
     * @param charCode The Character Code to use for comparisons.
     */
    private TextColor(char charCode) {
        this.charCode = charCode;
        this.color = null;
    }

    /**
     * This Constructor is only called from an static method in this class and
     * is used for testing purposes only. A TextColor object is created with
     * the given Color but the Character Code is set to '-'.
     * @param color The Color to use for comparisons.
     */
    private TextColor(Color color) {
        this.charCode = '-';
        this.color = color;
    }

    /**
     * This method creates a dummy TextColor with the specified Character code
     * but no Color, this dummy Objects purpose is for making comparisons to
     * other TextColor Objects when all you have to compare with is the
     * Character code.
     * @param charCode The Character Code to construct the Dummy Object with.
     * @return A Dummy TextColor containing the given Character code.
     */
    public static TextColor getTestTextColor(char charCode) {
        return new TextColor(charCode);
    }

    /**
     * This method creates a dummy TextColor with the specified Color but no
     * Character code, this dummy Objects purpose is for making comparisons to
     * other TextColor Objects when all you have to compare with is the
     * Color.
     * @param color The Color to construct the Dummy Object with.
     * @return A Dummy TextColor containing the given Color.
     */
    public static TextColor getTestTextColor(Color color) {
        return new TextColor(color);
    }

    /**
     * Returns the Character Code assigned to this TextColor Object.
     * @return The Character Code assigned to this TextColor Object.
     */
    public char getCharCode() {
        return charCode;
    }

    /**
     * Returns the Color assigned to this TextColor Object.
     * @return The Color assigned to this TextColor Object.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Compares a Character or the Character of another TextColor Object to the
     * Character code of this TextColor Object and returns their compareTo()
     * result.
     * @param o The other Object to compare to, either Character or TextColor.
     * @return The compareTo() value of the two Characters.
     */
    public int compareTo(Object o) {
        String cName = o.getClass().getName();

        if (cName.equals("java.lang.Character")) {
            Character c = new Character(charCode);

            return c.compareTo((Character)o);
        } else if (cName.equals("com.eleet.dragonconsole.util.TextColor")) {
            Character c = new Character(charCode);
            Character otherC = new Character(((TextColor)o).getCharCode());

            return c.compareTo(otherC);
        }

        return 0;
    }

    /**
     * This method determines if this TextColor is equal to the Object given.
     * This method will compare a Character to this TextColors Character Code
     * or compare this TextColor to another TextColor. If a Dummy TextColor is
     * given (TextColor with just a Character code, or just a Color) then it
     * only compares the Character or Color and returns if they are a match.
     * The only requirement for <code>true</code> to be returned (excluding
     * a dummy TextColor with just a Color) is for the Character Codes to
     * match.
     * @param o The Object for equals Comparison, either a Character or
     *  TextColor.
     * @return <code>true</code> if the two object are equal.
     */
    @Override
    public boolean equals(Object o) {
        String cName = o.getClass().getName();

        if (cName.equals("com.eleet.dragonconsole.util.TextColor")) {
            TextColor otc = (TextColor)o;

            if (otc.getColor() == null || color == null)
                return ((charCode == otc.getCharCode()));
            else
                return ((color.equals(otc.getColor())));

        } else if (cName.equals("java.lang.Character")) {
            Character oc = (Character)o;

            return ((charCode == oc.toString().charAt(0)));
        } else
            return false;
    }

    /**
     * This method will display the Character code, and then called the
     * toString() method on the Color that it represents. In the case of a
     * dummy TextColor then the Character code or the Color (whichever is not
     * used by the Dummy) is displayed as "TEST_TextColor".
     * @return A String representation of this Object.
     */
    @Override
    public String toString() {
        return "Code: " + ((charCode == '-') ? "TEST_TextColor" : charCode)
             + " = " + ((color == null) ? "TEST_TextColor" : color.toString());
    }

    public class InvalidCharCodeException extends Exception {
        public InvalidCharCodeException(String msg) {
            super(msg);
        }
    }
}
