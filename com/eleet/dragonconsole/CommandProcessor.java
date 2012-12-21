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

/**
 * CommandProcessor is a helper class for the DragonConsole. A DragonConsole can
 * have one CommandProcessor registered to it, and if it does have a
 * CommandProcessor registered it will send all input to the
 * <code>processCommand(String)</code> method of the registered
 * CommandProcessor.<br />
 * This class was created separately to better facilitate ease of implementation
 * of the console as any commands for a given console are processed here (this
 * class does nothing by itself, it's purpose is to be extending by the
 * programmer who wishes to use a DragonConsole) instead of within the
 * DragonConsole class itself. This is not suggested, but CommandProcessors
 * could be "rotated" through a DragonConsole as well if one so chose to do so.
 * @author Brandon E Buck
 */
public class CommandProcessor {
    /** 
     * The DragonConsole this Processor is registered to
     */
    private DragonConsole console;

    /** 
     * The Default Constructor, this method sets the console to null.
     */
    public CommandProcessor() {
        console = null;
    }

    /** 
     * This method is called from the DragonConsoles setCommandProcessor()
     * method and SHOULT NOT be called by the programmer or any other class
     * (excluding possible extensions of DragonConsole).
     * @param console The console where the output will be sent to.
     */
    public void install(DragonConsole console) {
        this.console = console;
    }
    
    /** 
     * Used to remove the ties to the DragonConsole if the CommandProcessor is
     * changed. This method is only called if a new CommandProcessor is added
     * to the DragonConsole this CommandProcessor is registered with. This
     * method SHOULD NOT be called manually at all.
     */
    public void uninstall() {
        this.console = null;
    }

    /** 
     * Processes a String entered via a DragonConsole for commands and handles
     * and special cases that need to be taken care of based on the command.
     * This method should just send the string to print if no valid command is
     * entered.
     * @param input The String that might possibly contain a command.
     */
    public void processCommand(String input) {
        output(input + "\n");
    }

    /** 
     * ** USE <code>output(String)</code> instead **
     * @param output The String to be sent to the console for output.
     */
    @Deprecated
    public void outputToConsole(String output) {
        if (console != null)
            console.append(output);
    }

    /** 
     * Sends the text to the console for Color Code/Script processing.
     * @param output The text for the console to output.
     */
    public void output(String output) {
        if (console!= null)
            console.append(output);
    }
    
    /** 
     * Appends the output, without processing, to the console as a System
     * message. The appendSystemMessage() uses whatever style is set as the
     * default system message color.
     * @param output The output that should be set as a System message.
     */
    public void outputSystem(String output) {
        if (console != null)
            console.appendSystemMessage(output);
    }
    
    /** 
     * Appends the output, without processing, to the console as an Error
     * message. The appendErrorMessage() uses whatever style is set as the
     * default Error message color.
     * @param output The output that should be set as an Error message.
     */
    public void outputError(String output) {
        if (console != null)
            console.appendErrorMessage(output);
    }

    /** 
     * This method interfaces with the FileProcessors readText(File) method and
     * can be used instead of importing and using FileProcessor yourself.
     * @param file The File object that you want text read from.
     */
    public String readText(java.io.File file) throws Exception {
        return com.eleet.dragonconsole.file.FileProcessor.readText(file);
    }

    /** 
     * This method interfaces with the FileProcessors readText(File) method and
     * can be used instead of importing and using FileProcessor yourself.
     * @param filePath The path to the file, formatted as a String.
     */
    public String readText(String filePath) throws Exception {
        return com.eleet.dragonconsole.file.FileProcessor.readText(filePath);
    }

    /** 
     * Passes the given String to the console to convert all the DCCCs in the
     * String to their ANSI equivalent.
     * @param string The String to convert.
     * @return Returns the String after all DCCCs have been converted into ANSI
     *  codes.
     */
    public String convertToANSIColors(String string) {
        return console.convertToANSIColors(string);
    }

    /** 
     * Passes the given String to the console to convert all the ANSI Codes in
     * the String to their DCCC equivalent.
     * @param string The String to convert.
     * @return Returns the String after all ANSI Codes have been converted into
     *  DCCCs.
     */
    public String convertToDCColors(String string) {
        return console.convertToDCColors(string);
    }

    /**
     * Allows the programmer to get access to the DragonConsole object in the
     * CommandProcessor.
     * @return Returns the DragonConsole that this CommandProcessor is
     *  registered to.
     */
    protected DragonConsole getConsole() {
        return console;
    }
}
