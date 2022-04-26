package dev.bbuck.dragonconsole.text

import java.awt.Color
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.Style
import javax.swing.text.StyleConstants

/** Provides utilities for converting to and from ANSI color codes and DragonConsole color codes. */
object ANSI {
    /** ANSI escape code that begins ANSI control codes. */
    const val ESCAPE = '\u001b'

    // Bright ANSI standard colors
    @JvmField public val INTENSE_BLACK = Color.GRAY.darker()
    @JvmField public val INTENSE_RED = Color.RED
    @JvmField public val INTENSE_GREEN = Color.GREEN
    @JvmField public val INTENSE_YELLOW = Color.YELLOW
    @JvmField public val INTENSE_BLUE = Color(66, 66, 255)
    @JvmField public val INTENSE_MAGENTA = Color.MAGENTA
    @JvmField public val INTENSE_CYAN = Color.CYAN
    @JvmField public val INTENSE_WHITE = Color.WHITE

    // Colors associated to ANSI standard colors
    @JvmField public val BLACK = Color.BLACK
    @JvmField public val RED = INTENSE_RED.darker()
    @JvmField public val GREEN = INTENSE_GREEN.darker()
    @JvmField public val YELLOW = INTENSE_YELLOW.darker()
    @JvmField public val BLUE = INTENSE_BLUE.darker()
    @JvmField public val MAGENTA = INTENSE_MAGENTA.darker()
    @JvmField public val CYAN = INTENSE_CYAN.darker()
    @JvmField public val WHITE = Color.GRAY.brighter()

    /** Normal colors in ANSI color order (0 -> Black, 1 -> Red, etc...) */
    @JvmField
    public val NORMAL_COLORS = listOf(BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE)

    /** Intense colors in ANSI color order (0 -> Black, 1 -> Red, etc...) */
    @JvmField
    public val INTENSE_COLORS =
            listOf(
                    INTENSE_BLACK,
                    INTENSE_RED,
                    INTENSE_GREEN,
                    INTENSE_YELLOW,
                    INTENSE_BLUE,
                    INTENSE_MAGENTA,
                    INTENSE_CYAN,
                    INTENSE_WHITE
            )

    /** Signifies that following colors should be "bright" or "intense" */
    const val BRIGHT_CODE = 1

    /** Resets the current ANSI color settings back to the default. */
    const val RESET = 0

    /** Special color that represents the "default" color. */
    const val DEFAULT_COLOR_CODE = 9

    /** Color codes are always in 0 to 7. */
    val VALID_COLOR_RANGE = 0..7

    /** Foreground codes are 30-37, but we only care about the digit. */
    const val FOREGROUND_DIGIT = 3

    /** Background codes are 40-47, but we only care about the digit. */
    const val BACKGROUND_DIGIT = 4

    /** Numeric regular expression for filtering out ANSI codes not needed. */
    val NUMERIC_CODE = """\A\d+\z""".toRegex()

    /**
     * ANSI code to use the default foreground and background, used as a default for empty/incorrect
     * DCCode provided for conversion.
     */
    const val USE_DEFAULTS_CODE = "$ESCAPE[39;49m"

    /**
     * Convert the string ANSI code into a `SimpleAttributeSet` for use in a `StyledDocument`. This
     * method only considers the ANSI color code portions and ignores the rest of the code. This
     * method does not validate the code.
     *
     * @param existingAttributeSet is the current active document style as ANSI codes modify
     * existing styles instead of replacing everything about them.
     * @param ansiCode is the actual string representation of current ANSI code.
     * @param defaultStyle is the base style of the document we pull from when the ANSI code refers
     * to reverting to the default values.
     * @return the modified `SimpleAttributeSet` with the new current ANSI color configuration.
     *
     * @since 3.0
     * @author Brandon Buck
     */
    @JvmStatic
    public fun getANSIAttribute(
            existingAttributeSet: SimpleAttributeSet?,
            ansiCode: String,
            defaultStyle: Style
    ): SimpleAttributeSet? {
        if (ansiCode.length <= 3) {
            return null
        }

        var ansiAttributeSet = existingAttributeSet

        if (ansiAttributeSet == null) {
            ansiAttributeSet = SimpleAttributeSet()
        }

        var codeParts = listOf<Int>()
        // minimum code length would be the escape, brace, and 'm' (end of code)
        if (ansiCode.length > 3) {
            // trims the escape and opne character as well as the final 'm' ending
            // the code
            codeParts =
                    ansiCode.substring(2, ansiCode.length - 1)
                            .split(';')
                            .filter { it.matches(NUMERIC_CODE) }
                            .map(String::toInt)
        }

        var brightColors = false
        for (codePart in codeParts) {
            if (codePart == 0) {
                brightColors = false
                ansiAttributeSet = SimpleAttributeSet()
            } else if (codePart == 1) {
                brightColors = true
            }

            val tensDigit = codePart / 10
            val onesDigit = codePart % 10

            if (onesDigit in VALID_COLOR_RANGE) {
                val color = getColorFromAnsiCode(codePart, brightColors)
                when (tensDigit) {
                    FOREGROUND_DIGIT -> StyleConstants.setForeground(ansiAttributeSet, color)
                    BACKGROUND_DIGIT -> StyleConstants.setBackground(ansiAttributeSet, color)
                }
            } else if (onesDigit == DEFAULT_COLOR_CODE) {
                if (tensDigit == FOREGROUND_DIGIT) {
                    val color = StyleConstants.getForeground(defaultStyle)
                    StyleConstants.setBackground(ansiAttributeSet, color)
                } else if (tensDigit == BACKGROUND_DIGIT) {
                    val color = StyleConstants.getBackground(defaultStyle)
                    StyleConstants.setBackground(ansiAttributeSet, color)
                }
            }
        }

        return ansiAttributeSet
    }

    /**
     * Converts a given DCCode into an ANSI color code based on the color values of the current
     * theme. If no match is found or an invalid code is provided then a default "use default color"
     * (`[39;49m`) is returned.
     *
     * @param dcCode is the code that should be converted to an ANSI code.
     * @param colors is the list of colors currently present in the DragonConsole theme.
     * @return the best guess at an ANSI color code equivalent to the provided DCCode.
     *
     * @since 3.0
     * @author Brandon Buck
     */
    @JvmStatic
    public fun getANSICodeFromDCCode(dcCode: String, colors: List<TextColor>): String {
        if (dcCode.length != 3) {
            return USE_DEFAULTS_CODE
        }

        val colorMap = colors.associate { it.charCode to it }

        // DCCode format is &fb and we only care about the foreground and background
        // characters
        val foreground = dcCode[1]
        val background = dcCode[2]

        val ansiCode = mutableListOf("0")

        if (colorMap.containsKey(foreground)) {
            val textColor = colorMap.getValue(foreground)
            var bright = false
            var colorIndex = NORMAL_COLORS.indexOf(textColor.color)
            if (colorIndex < 0) {
                bright = true
                colorIndex = INTENSE_COLORS.indexOf(textColor.color)
            }

            if (colorIndex >= 0) {
                if (bright) {
                    ansiCode.add("1")
                }
                ansiCode.add("$FOREGROUND_DIGIT$colorIndex")
            }
        }

        if (colorMap.containsKey(background)) {
            val textColor = colorMap.getValue(background)
            var bright = false
            var colorIndex = NORMAL_COLORS.indexOf(textColor.color)
            if (colorIndex < 0) {
                bright = true
                colorIndex = INTENSE_COLORS.indexOf(textColor.color)
            }

            if (colorIndex >= 0) {
                if (bright) {
                    ansiCode.add("1")
                }
                ansiCode.add("$BACKGROUND_DIGIT$colorIndex")
            }
        }

        val generatedCode =
                ansiCode.joinToString(separator = ";", prefix = "$ESCAPE[", postfix = "m")
        if (generatedCode == "[0m") {
            return USE_DEFAULTS_CODE
        } else {
            return generatedCode
        }
    }

    /**
     * Convert all the DCCode characters in a block of text into corresponding ANSI color codes and
     * return the new string with ANSI color codes in it.
     *
     * @param text is the text contianing DCCodes.
     * @param colors are all the colors currently defined in the DragonConsole.
     * @param colorCodeChar is the character that denotes a DCCode follows, typically this will be
     * `&`.
     * @return the block of text provided withall DCCode's converted into rough equivalents of ANSI
     * codes.
     *
     * @since 3.0
     * @author Brandon Buck
     */
    @JvmStatic
    public fun convertDCtoANSIColors(
            text: String,
            colors: List<TextColor>,
            colorCodeChar: Char
    ): String {
        val output = StringBuilder()

        var i = 0
        while (i < text.length) {
            if (text[i] == colorCodeChar) {
                if (i < text.length - 1 && text[i + 1] == colorCodeChar) {
                    // double color code characters print a single color code
                    // character
                    output.append(colorCodeChar).append(colorCodeChar)
                    i += 2
                    continue
                }

                if (i < text.length - 2) {
                    val code = text.substring(i, i + 3)

                    val ansiCode = getANSICodeFromDCCode(code, colors)
                    output.append(ansiCode)
                    i += 3
                    continue
                }
            }

            output.append(text[i])
            ++i
        }

        return output.toString()
    }

    /**
     * Convert the ANSI color codes in the given text to DragonConsole color codes.
     *
     * @param text contains the ANSI color codes that need to be converted.
     * @param colors are the set of colors currently added to the DragonConsole.
     * @param colorCodeChar is the character that begins a DragonConsole color code.
     * @param defaultStyle is the default foreground and background color.
     * @return the text with all ANSI color codes converted into DragonConsole color codes.
     *
     * @since 3.0
     * @author Brandon Buck
     */
    @JvmStatic
    public fun convertANSIToDCColors(
            text: String,
            colors: List<TextColor>,
            colorCodeChar: Char,
            defaultStyle: String
    ): String {
        val output = StringBuilder()

        var i = 0
        while (i < text.length) {
            if (text[i] == ESCAPE) {
                val endIndex = text.indexOf('m', i + 1)
                if (endIndex >= 0) {
                    val ansiCode = text.substring(i, endIndex + 1)
                    val dcCode =
                            getDCCodeFromAnsiCode(ansiCode, colors, colorCodeChar, defaultStyle)
                    output.append(dcCode)
                    i = endIndex + 1
                    continue
                }
            }

            output.append(text[i])
            ++i
        }

        return output.toString()
    }

    /**
     * Converts a given ANSI code into it's DCCode equivalent based on the current defined colors in
     * the console.
     *
     * @param code is the ANSI code that needs to be converted.
     * @param colors are all the colors defined in the Console.
     * @param colorCodeChar is the character that denotes the beginning fo a DCCode, which is
     * typically `&`.
     * @param defaultStyle is the "default" DCCode to pull color characters from as needed.
     * @return the DCCode equivalent (based on color value) of the input ANSI code.
     *
     * @since 3.0
     * @author Brandon Buck
     */
    private fun getDCCodeFromAnsiCode(
            ansiCode: String,
            colors: List<TextColor>,
            colorCodeChar: Char,
            defaultStyle: String
    ): String {
        var bright = false
        val ansiCodeContents = ansiCode.substring(2)
        var foreground = defaultStyle[0]
        var background = defaultStyle[1]

        val ansiParts = ansiCodeContents.split(";")
        for (part in ansiParts) {
            if (part.matches("""\d+""".toRegex())) {
                var colorCode = part.toInt()
                when (colorCode) {
                    0 -> {
                        bright = false
                    }
                    1 -> {
                        bright = true
                    }
                    in 30..37 -> {
                        foreground =
                                getDCCharCodeFromColor(
                                        getColorFromAnsiCode(colorCode, bright),
                                        colors
                                )
                    }
                    39 -> {
                        foreground = defaultStyle[0]
                    }
                    in 40..47 -> {
                        background =
                                getDCCharCodeFromColor(
                                        getColorFromAnsiCode(colorCode, bright),
                                        colors
                                )
                    }
                    49 -> {
                        background = defaultStyle[1]
                    }
                }
            }
        }

        return "$colorCodeChar$foreground$background"
    }

    /**
     * Find the corresponding char code associated with a given color value based on what colors are
     * defined in the console.
     *
     * @param color is the search color, the color for which a character is desired.
     * @param colors are all the colors currently defined in the console.
     * @return the character representing the input color or simply an empty character.
     *
     * @since 3.0
     * @author Brandon Buck
     */
    private fun getDCCharCodeFromColor(color: Color, colors: List<TextColor>): Char {
        for (textColor in colors) {
            if (textColor.color == color) {
                return textColor.charCode
            }
        }

        return ' '
    }

    /**
     * Fetch the actual color specified by the given ANSI code.
     *
     * Valid colors fall in the range of 0 to 7 (with a 3 or 4 in the tens digit). Each of these
     * values directly corresponds to a color in ANSI so we fetch and return the correct color for
     * the code.
     *
     * @param code is the numeric ANSI code representing the color.
     * @param bright determines if the normal or intense color list should be used.
     * @return the associated color.
     *
     * @since 3.0
     * @author Brandon Buck
     */
    private fun getColorFromAnsiCode(code: Int, bright: Boolean): Color {
        val colorCode = code % 10

        if (colorCode !in VALID_COLOR_RANGE) {
            throw IllegalArgumentException("Invalid ANSI color code '$code' provided")
        }

        if (bright) {
            return INTENSE_COLORS[colorCode]
        }

        return NORMAL_COLORS[colorCode]
    }
}
