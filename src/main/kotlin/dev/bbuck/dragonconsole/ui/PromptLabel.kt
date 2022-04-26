package dev.bbuck.dragonconsole.ui

import dev.bbuck.dragonconsole.text.TextColor
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JLabel

class PromptLabel(labelText: String, var defaultColor: String) : JLabel(labelText) {
    val textColors: MutableList<TextColor> = mutableListOf()
    var colorCodeChar = '&'

    fun addColor(textColor: TextColor) {
        textColors.add(textColor)
        repaint()
    }

    fun removeColor(textColor: TextColor) {
        textColors.remove(textColor)
        repaint()
    }

    fun clearColors() {
        textColors.clear()
    }

    override fun paintComponent(graphics: Graphics) {
        val labelText = super.getText()
        var startX = 0

        val processed = StringBuilder()
        var currentStyle = defaultColor
        var i = 0
        while (i < labelText.length) {
            val currentChar = labelText[i]
            if (currentChar == colorCodeChar) {
                if (i < text.length - 1 && labelText[i + 1] == colorCodeChar) {
                    processed.append(colorCodeChar)
                    // skipping the && (next character is skipped in loop
                    // increment below
                    i += 1
                } else if (i < text.length - 2) {
                    startX = paintProcessed(processed.toString(), currentStyle, graphics, startX)
                    processed.clear()
                    currentStyle = mergeStyles(labelText.substring(i + 1, i + 3), currentStyle)

                    // jump past the color code (two characters)
                    i += 2
                } else {
                    processed.append(labelText[i])
                }
            } else {
                processed.append(labelText[i])
            }

            ++i
        }

        paintProcessed(processed.toString(), currentStyle, graphics, startX)
    }

    // same as super.getText but trims color codes
    override fun getText(): String {
        return trimDCCC(super.getText())
    }

    private fun paintProcessed(processed: String, style: String, graphics: Graphics, x: Int): Int {
        if (processed.isEmpty()) {
            return x
        }

        val graphics2d = graphics as Graphics2D
        graphics2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        )

        val bounds = graphics.getFontMetrics().getStringBounds(processed, graphics)
        val width = bounds.width.toInt()
        val height = bounds.height.toInt()
        val newX = x + width
        // What is 3?!
        val y = height - 3

        // draw background color
        graphics.setColor(getColorFromDCCC(style[1]))
        graphics.fillRect(x, 0, width, height)

        // draw text string
        graphics.setColor(getColorFromDCCC(style[0]))
        graphics.drawString(processed, x, y)

        return newX
    }

    private fun mergeStyles(newStyle: String, currentStyle: String): String {
        // if the newStyleis invalid, keep current style
        // a '--' newStyle means "use the same foreground and background" so just return current
        // style
        if (newStyle.length != 2 || newStyle == "--") {
            return currentStyle
        }

        // a 0 in any position is "reset," so we reset to default color
        if (newStyle.contains('0')) {
            return defaultColor
        }

        if (newStyle[0] == '-') {
            return "${currentStyle[0]}${newStyle[1]}"
        }

        if (newStyle[1] == '-') {
            return "${newStyle[0]}${currentStyle[1]}"
        }

        return newStyle
    }

    private fun getColorFromDCCC(code: Char): Color {
        val foundColor = textColors.find { it.charCode == code }
        if (foundColor != null) {
            foundColor.color
        }

        return Color.GRAY.brighter()
    }

    private fun trimDCCC(textToTrim: String): String {
        val buffer = StringBuilder()
        var i = 0
        while (i < textToTrim.length) {
            if (textToTrim[i] == colorCodeChar) {
                if (i < textToTrim.length - 1 && textToTrim[i + 1] == colorCodeChar) {
                    buffer.append(colorCodeChar)
                    i += 1
                } else if (i < textToTrim.length - 2) {
                    i += 3
                }
            } else {
                buffer.append(textToTrim[i])
            }

            ++i
        }

        return buffer.toString()
    }
}
