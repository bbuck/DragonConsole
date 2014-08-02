package com.eleet.dragonconsole;

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

import com.eleet.dragonconsole.file.FileProcessor;

/**
 * This class is used when the .jar for DragonConsole is run by itself and
 * functions as a Demonstration of what DragonConsole is and what it is capable
 * of doing. This class is an extension of CommandProcessor and is used to
 * process basic user input.<br /><br />
 * <b>Usable Commands</b><br />
 * info (colors|ANSI|input) - Displays the corresponding file<br />
 * license (dragonconsole|font) - Displays the license for the corresponding
 * argument.<br />
 * ansi (on|off) - Turns on ANSI Color Codes<br />
 * demo input (ranged|infinite) (protected| ) - Demonstration of the four
 * different styles of input possible.<br />
 * exit - Exits the program.
 * @author Brandon E Buck
 */
public class DemoProcessor extends CommandProcessor {
    private boolean inputDemo = false;

    public DemoProcessor() {
        super();
    }

    @Override
    public void processCommand(String input) {
        if (inputDemo) {
            output("\n\n&c-Your input:&00 " + input);
            inputDemo = false;
        } else {
            input = input.toLowerCase();
            String cmd[] = input.split(" ");

            if (cmd.length == 0)
                outputSystem("\n\nYou must enter a command! Valid commands are INFO, LICENSE, DEMO, ANSI, and EXIT");

            else if (cmd[0].equals("info")) {
                if (cmd.length > 1) {
                    try {
                        if (cmd[1].equals("colors"))
                            output("\n\n" + FileProcessor.readDCResource("colors"));
                        else if (cmd[1].equals("ansi"))
                            output("\n\n" + FileProcessor.readDCResource("ansi"));
                        else if (cmd[1].equals("input"))
                            output("\n\n" + FileProcessor.readDCResource("input"));
                        else
                            outputSystem("\n\nNot a valid argument to INFO, type \"INFO\" for help.");

                    } catch (Exception exc) {
                        outputError("\n\n" + exc.getMessage());
                    }
                } else
                    outputSystem("\n\nValid arguments for INFO are: COLORS, ANSI, or INPUT.");

            } else if (cmd[0].equals("license")) {
                if (cmd.length > 1) {
                    try {
                        if (cmd[1].equals("font"))
                            output("\n\n" + FileProcessor.readDCResource("l_font"));
                        else if (cmd[1].equals("dragonconsole"))
                            output("\n\n" + FileProcessor.readDCResource("l_console"));
                        else
                            outputSystem("\n\nNot a valid argument for LICENSE, type \"LICENSE\" for help.");

                    } catch (Exception exc) {
                        outputError("\n\n" + exc.getMessage());
                    }
                } else
                    outputSystem("\n\nValid arguments for LICENSE are FONT and DRAGONCONSOLE.");
            } else if (cmd[0].equals("ansi")) {
                if (cmd.length > 1) {
                    if (cmd[1].equals("on")) {
                        getConsole().setUseANSIColorCodes(true);
                        outputSystem("\n\nANSI Color Codes are now on.");
                    } else if (cmd[1].equals("off")) {
                        getConsole().setUseANSIColorCodes(false);
                        outputSystem("\n\nANSI Color Codes are now off.");
                    } else
                        outputSystem("\n\nValid arguments for ANSI are ON and OFF.");
                } else
                    outputSystem("\n\nValid arguments for ANSI are ON and OFF.");
            } else if (cmd[0].equals("demo")) {
                if (cmd.length > 1 && cmd[1].equals("input")) {
                    if (cmd.length > 2 && cmd[2].equals("ranged")) {
                        if (cmd.length > 3) {
                            if (cmd[3].equals("protected")) {
                                output("\n\n&c-                       +--------------------+\n"
                                     + "&l-Enter Protected Input:&c- |%i20+;|\n"
                                     + "&c-                       +--------------------+&00");
                                inputDemo = true;
                            }
                        } else {
                            output("\n\n&c-                    +--------------------+\n"
                                 + "&l-Enter Ranged Input:&c- |%i20;|\n"
                                 + "&c-                    +--------------------+&00");
                            inputDemo = true;
                        }
                    } else if (cmd.length > 2 && cmd[2].equals("infinite")) {
                        if (cmd.length > 3) {
                            if (cmd[3].equals("protected")) {
                                output("\n\n&l-::> %i+;");
                                inputDemo = true;
                            }
                        } else {
                            output("\n\n&l-::> ");
                            inputDemo = true;
                        }
                    } else
                        outputSystem("\n\nValid arguments for INPUT are RANGED (PROTECTED) or INFINITE (PROTECTED)");
                } else
                    outputSystem("\n\nThe valid argument for DEMO is INPUT.");

            } else if (cmd[0].equals("exit"))
                System.exit(0);
            else if (cmd[0].equals("help"))
                outputHelp();
            else
                outputSystem("\n\nYou must enter a command! Valid commands are INFO, LICENSE, DEMO, ANSI, and EXIT");
        }

        if (!inputDemo)
            output("\n\n&ob>>&00 ");
    }

    /**
     * Displays a help menu with available output options for the user to view.
     */
    public void outputHelp() {
        String help = "\n\n&c--- &X-Dragon&x-Console &c-" + getConsole().getVersion()
                + " ---------------\n"
                + "   &w-INFO &o-View information on certain topics.\n"
                + "   &w-   EX: INFO COLORS\n"
                + "   LICENSE &o-View the License for DragonConsole or it's"
                + "default font.\n"
                + "         &w-EX: LICENSE DRAGONCONSOLE\n"
                + "   ANSI &o- Enable or Disable ANSI codes (this will alos"
                + " enable/disable DCCCs\n"
                + "         &w-EX: ANSI ON\n"
                + "                ANSI OFF\n"
                + "   &w-DEMO &o-Demo the different input methods (ranged and"
                + "infinite) as well as protected \n"
                + "           and unprotected.\n"
                + "   &w-   EX: DEMO INPUT RANGED\n          "
                + "DEMO INPUT RANGED PROTECTED\n"
                + "   EXIT &o-Quits the demonstration program\n"
                + "&c----------------------------------------";

        output(help);
    }

    /**
     * Overrides the default output in CommandProcessor to determine if ANSI
     * Colors are processed or DCCC and converts accordingly.
     * @param s The String to output.
     */
    @Override
    public void output(String s) {
        if (getConsole().isUseANSIColorCodes())
            super.output(convertToANSIColors(s));
        else
            super.output(s);
    }
}
