package dev.bbuck.dragonconsole.text

import java.awt.Color

private val ILLEGAL_TEXT_COLOR_VARIABLES = setOf('0', '-')

/**
 * A TextColor contains a character and a color.
 *
 * This represents a DCCC color code to perform colorization of text in the DragonConsole.
 *
 * @since 3.0
 * @author Brandon Buck
 */
public data class TextColor
@JvmOverloads
constructor(val charCode: Char = '\u0000', val color: Color = Color.WHITE) : Comparable<TextColor> {
    init {
        if (charCode in ILLEGAL_TEXT_COLOR_VARIABLES) {
            throw IllegalArgumentException("Cannot use reserved character '$charCode' in TextColor")
        }
    }

    /**
     * Compare this TextColor's char to the other TextColor's char.
     *
     * @param other is the TextColor to compare this TextColor to.
     * @return the ordering representing the relationship between this TextColor and the other
     * TextColor.
     *
     * @since 3.0
     * @author Brandon Buck
     */
    override operator fun compareTo(other: TextColor): Int = charCode.compareTo(other.charCode)

    override fun toString(): String = "Code: $charCode = $color"
}
