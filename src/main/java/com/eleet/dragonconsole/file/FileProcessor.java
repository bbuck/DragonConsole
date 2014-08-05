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

package com.eleet.dragonconsole.file;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Receives a file path and then reads the contents of the file into the proper
 * format and returns the Object back to the caller. This is a static class and
 * should not be instantiated.
 * @author Brandon E Buck
 * @version 1.0
 */
public class FileProcessor {
    /**
     * This method creates a File Object from the given path and then returns
     * the String that is read by <code>redText(File)</code>.
     * @param filePath The Absolute or Relative path to the File.
     * @return The String contents contained within the File.
     * @throws FileNotFoundException
     */
    public static String readText(String filePath) throws FileNotFoundException {
        return readText(new File(filePath));
    }

    /**
     * This method will read plain text files and return the contents as a
     * String value, if the file exists. This method will separate each line
     * with a "\n\r" as the DragonConsole ignores the character '\r' but this
     * can still be a useful line separator.
     * @param file The File that needs to be read.
     * @return The String contents of the File given.
     * @throws FileNotFoundException
     */
    public static String readText(File file) throws FileNotFoundException {
        String contents = "";

        if (file.exists()) {
            try {
                FileReader fread = new FileReader(file);
                BufferedReader in = new BufferedReader(fread);

                String line = in.readLine();
                while (line != null) {
                    contents += line + "\n";
                    line = in.readLine();
                }

                in.close();
                fread.close();

            } catch(Exception exc) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Error #0008\n"
                      + "Failed to read the given File.\n"
                      + exc.getMessage(),
                      "Error Caught", javax.swing.JOptionPane.ERROR_MESSAGE);
            }

        } else
            throw new FileNotFoundException("Invalid File Path provided (" + file.getName() + ").");

        return contents;
    }

    /**
     * This method will read a plain text file in the
     * "/com/eleet/dragonconsole/resources/" package in the JAR and return its
     * contents as a String. All files in this directory are help files of
     * some kind written for the developer using DragonConsole (not the user)
     * and the License for this Project as well as the License for the Font.
     * @param file The File in the resources folder in the JAR to read.
     * @return The String contents of the File with the given name.
     */
    public static String readDCResource(String file) {
        String contents = "";

        try {
            InputStream is = FileProcessor.class.getResourceAsStream("/com/eleet/dragonconsole/resources/" + file);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader in = new BufferedReader(isr);

            String line = in.readLine();
            while (line != null) {
                contents += line + "\n";
                line = in.readLine();
            }

            in.close();
            isr.close();
            is.close();

        } catch(Exception exc) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Error #0009\n"
                  + "Failed to read the file from the jar!\n"
                  + exc.getMessage(),
                  "Error Caught", javax.swing.JOptionPane.ERROR_MESSAGE);
        }

        return contents;
    }

    /**
     * This method will create an InputStream used to load the Font file
     * stored in the JAR in as a Font object for use with DragonConsole.
     * @return Font Object created from the Font file in the JAR.
     */
    public static Font getConsoleFont() {
        Font consoleFont = null;

        try {
            InputStream is = FileProcessor.class.getResourceAsStream("/com/eleet/dragonconsole/font/dvsm.ttf");

            consoleFont = Font.createFont(Font.PLAIN, is);

            is.close();

        } catch(Exception exc) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Error #0010\n"
                  + "Failed to load the font file from the jar!\n"
                  + exc.getMessage(),
                  "Error Caught", javax.swing.JOptionPane.ERROR_MESSAGE);
        }

        return consoleFont;
    }
}

