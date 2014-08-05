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

import javax.swing.JFrame;
import java.awt.*;

/**
 * DragonConsoleFrame is designed to act as a simple interface if the programmer
 * just wishes to create a single JFrame for the console. This class does
 * everything the JFrame controls DragonConsole class used to do before it was
 * altered from an extension from JFrame to JPanel. The Default Constructor for
 * DragonConsoleFrame creates a basic DragonConsole and adds it to the JFrame
 * and the other Constructor takes in a pre-initialized DragonConsole and adds
 * it to the JFrame.
 * @author Brandon E Buck
 */
public class DragonConsoleFrame extends JFrame {
    private DragonConsole console;

    /** This Constructor builds a DragonConsoleFrame with the given title and console.
     * Builds a DragonConsoleFrame with the specified title and uses the console
     * passed.
     * @param title The Title to use for this DragonConsoleFrame.
     * @param console The DragonConsole to add to this DragonConsoleFrame.
     */
    public DragonConsoleFrame(String title, DragonConsole console) {
        this.console = console;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(title);
        this.setResizable(false);
        
        this.add(console);
        this.pack();
        console.setInputFocus();

        this.centerWindow();
    }

    /** Default Constructor that uses a default title and creates a basic console.
     * This Constructor makes a basic title which is "DragonConsole " plus the
     * version number of the Console. It also creates a basic Console and adds
     * it to the JFrame.
     */
    public DragonConsoleFrame() {
        this.console = new DragonConsole();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("DragonConsole " + console.getVersion());
        this.setResizable(false);

        this.add(console);
        this.pack();
        console.setInputFocus();

        this.centerWindow();
    }

    /** Constructs a DragonConsole frame with the given Console and the default title.
     * Uses the given Console to add to the DragonConsoleFrame and uses the
     * default title which is "DragonConsole " plus the version of the number
     * of the console.
     * @param console The DragonConsole to use for this DragonConsoleFrame.
     */
    public DragonConsoleFrame(DragonConsole console) {
        this.console = console;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("DragonConsole " + console.getVersion());
        this.setResizable(false);
        
        this.add(console);
        this.pack();
        console.setInputFocus();

        this.centerWindow();
    }

    /** Constructs a DragonConsoleFrame with the given title and a default DragonConsole.
     * Creates a DragonConsoleFrame with a default DragonConsole and the given
     * title.
     * @param title The Custom title for the DragonConsoleFrame.
     */
    public DragonConsoleFrame(String title) {
        console = new DragonConsole();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(title);
        this.setResizable(false);

        this.add(console);
        this.pack();
        console.setInputFocus();

        this.centerWindow();
    }

    /** Centers the window based on screen size and window size.
     * Determines the Screen Size and then centers the Window. This can cause
     * funky problems on multi-screen display systems.
     */
    private void centerWindow() {
        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = defaultToolkit.getScreenSize();
        this.setLocation(
                (int)((screenSize.getWidth() / 2) - (this.getWidth() / 2)),
                (int)((screenSize.getHeight() / 2) - (this.getHeight() / 2)));
    }

    /** Returns the Console contained in this DragonConsoleFrame.
     * @return The DragonConsole in this DragonConsoleFrame.
     */
    public DragonConsole getConsole() {
        return console;
    }
}
