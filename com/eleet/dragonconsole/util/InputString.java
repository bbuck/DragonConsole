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

/**
 * DCString acts like a slightly modified String object class that includes
 * three methods not present in the default String class and those are
 * <code>insert(int location, String s)</code>,
 * <code>remove(int location, int length)</code>, and
 * <code>replace(int location, int length, String s)</code>.
 * These function similar to a Document.
 * @author Brandon E Buck
 * @version 1.0
 */
public class InputString {
    private String s;

    /**
     * Constructs a new Input String from the given String.
     * @param s The String used to build this InputString.
     */
    public InputString(String s) {
        this.s = s;
    }

    /**
     * Appends the given String to the end of contents of this InputString.
     * @param s The String to append.
     */
    public void append(String s) {
        this.s += s;
        Debug.print("\"" + this.s + "\" - APPEND");
    }

    /**
     * Inserts a String into the contents of this InputString at the given
     * location.
     * @param location The location in the InputString to place the given
     *  String.
     * @param s The String to insert at the given location.
     */
    public void insert(int location, String s) {
        if (location <= this.s.length()) {
            String before = this.s.substring(0, location);
            String after = this.s.substring(location);
            this.set(before + s + after);
            Debug.print("\"" + this.s + "\" - INSERT");
        }
    }

    /**
     * Removes a substring from the contents of this InpuString. The Range that
     * is removed is (location + length).
     * @param location The location (beginning) of the portion of the
     *  InputString that needs to be removed.
     * @param length The length of the range that should be removed from the
     *  InputString.
     */
    public void remove(int location, int length) {
        if (location < this.s.length() && (location + length) <= this.s.length()) {
            int end = location + length;
            String before = this.s.substring(0, location);
            String after = "";
            
            if ((location + length) < this.s.length())
                after = this.s.substring(end);

            set(before + after);

            Debug.print("\"" + this.s + "\" - REMOVE");
        }
    }

    /**
     * This method is used to remove a portion of the InputString when the
     * method of input is Ranged. This is different because when it removes a
     * character it tags a space on the end of the InputString to preserve the
     * length.
     * @param location The location (beginning) of the range that needs to be
     *  removed.
     * @param length The length of the substring that needs to be removed.
     */
    public void rangeRemove(int location, int length) {
        if (location < this.s.length() && (location + length) <= this.s.length()) {
            int end = location + length;
            String before = this.s.substring(0, location);
            String after = "";

            if (end < this.s.length())
                after = this.s.substring(end);

            set(before + after + " ");

            Debug.print("\"" + this.s + "\" - RANGE REMOVE");
        }
    }

    /**
     * This method is used to insert a String into the InputString if the input
     * type is Ranged. This method will replace (unless called from outside of
     * the InputController) one character. It will only Insert the String if the
     * last character of the InputString is a blank space.
     * @param location The location of the InputString in which the String
     *  needs to be inserted.
     * @param s The String to insert into this InputString.
     * @return Returns true if the character was inserted into the InputString
     *  or false if it was not.
     */
    public boolean rangeInsert(int location, String s) {
        if (location < this.s.length() && endIsEmpty()) {
            String before = this.s.substring(0, location);
            String after = this.s.substring(location, this.s.length() - 1);
            this.set(before + s + after);

            Debug.print("\"" + this.s + "\" - RANGE INSERT");
            return true;
        }

        return false;
    }

    /**
     * Replaces a portion of the InputString with the Given String.
     * @param location The beginning location for the substring that will be
     *  replaced by the given String.
     * @param length The length of the substring that will be replaced.
     * @param s The String to replace the substring of this InputString with.
     */
    public void replace(int location, int length, String s) {
        if (location < this.s.length() && (location + length) <= this.s.length()) {
            int end = location + length;
            String before = this.s.substring(0, location);
            String after = this.s.substring(end);
            set(before + s + after);

            Debug.print("\"" + this.s + "\" - REPLACE");
        } else
            append(s);
    }

    /**
     * Sets the contents of the InputString to that of the given String.
     * @param s The new String for this InputString.
     */
    public void set(String s) {
        this.s = s;
    }

    /**
     * Returns the contents of this InputString as a String.
     * @return The contents of the InputString as a String object.
     */
    public String get() {
        return this.s;
    }

    /**
     * This method will determine if the last character of this InputString is
     * a blank space or not.
     * @return <code>true</code> if the last character of this
     *  InputString is a space, or <code>false</code> otherwise.
     */
    public boolean endIsEmpty() {
        return (this.s.charAt(length() - 1) == ' ');
    }

    /**
     * Returns the length of the contents of this InputString.
     * @return The length of the contents of this InputString.
     */
    public int length() {
        return this.s.length();
    }

    /**
     * Returns a String representation for this InputString.
     * @return String representation for this InputString.
     */
    @Override
    public String toString() {
        return this.s;
    }
}
