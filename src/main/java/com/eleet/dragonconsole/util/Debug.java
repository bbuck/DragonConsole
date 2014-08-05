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
 * Debug is a small static class designed for adding Debug statements into
 * your program. The idea is that any data that you may want to see actively
 * displayed (i.e. to System.out) when debugging will be printed as long as
 * Debug.turnOn() has been called prior to your Debug.print() statements. This
 * allows the programmer to add in numerous debugging Print statements and
 * control whether they display by changing one line of code.
 * @author Brandon E Buck
 */
public class Debug {
    private static boolean on = false;

    /** 
     * Turns Debug Printing on.
     */
    public static void turnOn() {
        on = true;
    }

    /** 
     * Turns Debug Printing off.
     */
    public static void turnOff() {
        on = false;
    }

    /** 
     * Prints the String to System.out.println() if Debug printing is turned on.
     * @param output The String to print out if Debug printing is turned on.
     */
    public static void print(String output) {
        if (on)
            System.out.println(output);
    }
}
