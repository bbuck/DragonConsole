package dev.bbuck.dragonconsole.demo

import dev.bbuck.dragonconsole.CommandProcessor
import dev.bbuck.dragonconsole.DragonConsole
import dev.bbuck.dragonconsole.file.readDCResource
import kotlin.system.exitProcess

val VALID_INFO_SUBCOMMANDS = listOf("colors", "ansi", "input")
val VALID_LICENSE_TARGETS =
        mapOf(
                "font" to "l_font",
                "dragonconsole" to "l_console",
        )

val HELP_STRING =
        """

&c-== &X-Dragon&x-Console&c- ${DragonConsole.getVersion()} ===============
  &w-INFO    &o-View information on certain topics.
  &w-   EX: INFO COLORS
     OPTS: COLORS, ANSI, or INPUT

  LICENSE &o-View the License for DragonConsole or third party
        components.
  &w-   EX: LICENSE DRAGONCONSOLE
     OPTS: DRAGONCONSOLE or FONT

  ANSI    &o-Enable or Disable ANSI codes (this will also
        toggle DCCCs
  &w-   EX ANSI ON
     OPTS: ON or OFF

  DEMO    &o- Demo the difference input methods (ranged and
     infinite) as well as protected and unprotected
  &w-   EX: DEMO INPUT RANGED
     OPTS: INPUT [RANGED|INFINITE] [PROTECTED]

  EXIT    &o-Exit the console demo application.

  &w-HELP    &o-Show this help screen.
&c-========================================"""

public class DemoProcessor : CommandProcessor() {
    var activelyDemoingInput = false

    override fun processCommand(input: String) {
        if (activelyDemoingInput) {
            output("\n\n&c-Your input:&00 ${input}")
            activelyDemoingInput = false
            output("\n\n&ob>>&00 ")

            return
        }

        val cmdParts = input.lowercase().split(' ')
        when {
            cmdParts.isEmpty() ->
                    outputSystem(
                            "\n\nYou must enter a command! Valid commands are INFO, LICENSE, DEMO, ANSI, and EXIT"
                    )
            cmdParts.first() == "info" -> {
                if (cmdParts.size > 1) {
                    val fileName = cmdParts[1]
                    if (fileName in VALID_INFO_SUBCOMMANDS) {
                        output("\n\n${readDCResource(fileName)}")
                    } else {
                        outputSystem("\n\nNot a valid argument to INFO, type 'INFO' for help.")
                    }
                } else {
                    outputSystem("\n\nValid arguments for INFO are: COLORS, ANSI, or INPUT")
                }
            }
            cmdParts.first() == "license" -> {
                if (cmdParts.size > 1) {
                    val licenseTarget = cmdParts[1]
                    val licenseFile = VALID_LICENSE_TARGETS.get(licenseTarget)
                    if (licenseFile != null) {
                        output("\n\n${readDCResource(licenseFile)}")
                    } else {
                        outputSystem(
                                "\n\nNot a valid argument for LICENSE, type \"LICENSE\" for help."
                        )
                    }
                } else {
                    outputSystem("\n\nValid arguments for LICENSE are FONT and DRAGONCONSOLE.")
                }
            }
            cmdParts.first() == "ansi" -> {
                if (cmdParts.size > 1) {
                    when (cmdParts[1]) {
                        "on" -> {
                            console?.useANSIColorCodes = true
                            outputSystem("\n\nANSI Color Codes are now on.")
                        }
                        "off" -> {
                            console?.useANSIColorCodes = false
                            outputSystem("\n\nANSI Color Codes are now off.")
                        }
                        else ->
                                outputSystem(
                                        "\n\nInvalid argument for ANSI, type \"ANSI\" for help."
                                )
                    }
                } else {
                    outputSystem("\n\nValid arguments for ANSI are ON and OFF.")
                }
            }
            cmdParts.first() == "demo" -> {
                val demoOption = cmdParts.getOrNull(1)
                if (demoOption == "input") {
                    var inputKind = cmdParts.getOrNull(2)
                    var inputModifier = cmdParts.getOrNull(3)
                    when (inputKind) {
                        "ranged" -> {
                            if (inputModifier != null && inputModifier != "protected") {
                                outputSystem(
                                        "\n\nInvalid option for DEMO INPUT RANGED, only PROTECTED or no argument is valid."
                                )
                            } else {
                                val protected = inputModifier == "protected"
                                val inputCode =
                                        "%i20" +
                                                if (protected) {
                                                    "+"
                                                } else {
                                                    ""
                                                } +
                                                ";"
                                val inputLabel =
                                        "Enter " +
                                                if (protected) {
                                                    "Protected"
                                                } else {
                                                    " Ranged  "
                                                } +
                                                " Input:"
                                output(
                                        "\n\n&c-                       +--------------------+\n&l-$inputLabel&c- |$inputCode|\n&c-                       +--------------------+&00"
                                )
                                activelyDemoingInput = true
                            }
                        }
                        "infinite" -> {
                            if (inputModifier != null && inputModifier != "protected") {
                                outputSystem(
                                        "\n\nInvalid option for DEMO INPUT INFINITE, only PROTECTED or no argument is valid."
                                )
                            } else {
                                val protected = inputModifier == "protected"
                                if (protected) {
                                    output("\n\n&l-::> %i+;")
                                } else {
                                    output("\n\n&l-::> ")
                                }
                                activelyDemoingInput = true
                            }
                        }
                        else ->
                                outputSystem(
                                        "\n\nValid arguments to INPUT are RANGED [PROTECTED] or INFINITE [PROTECTED]."
                                )
                    }
                } else {
                    outputSystem("\n\nThe valid argument to DEMO is INPUT.")
                }
            }
            cmdParts.first() == "exit" -> exitProcess(0)
            cmdParts.first() == "help" -> output(HELP_STRING)
            else ->
                    outputSystem(
                            "\n\nYou must enter a command! Valid commands are INFO, LICENSE, DEMO, ANSI, and EXIT"
                    )
        }

        if (!activelyDemoingInput) {
            output("\n\n&ob>>&00 ")
        }
    }

    public override fun output(message: String) {
        if (console?.useANSIColorCodes ?: false) {
            super.output(convertToANSIColors(message))
        } else {
            super.output(message)
        }
    }
}
