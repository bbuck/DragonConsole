@file:JvmName("DocumentStyler")

package dev.bbuck.dragonconsole.text

import java.awt.Font
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyleContext
import javax.swing.text.StyledDocument

/**
 * Creates a style and adds it to the `StyledDocument`.
 *
 * Using the foreground and backgroun character codes it builds a new style name combining them and
 * adds this style to the document updating the actual foreground and background color of the style.
 *
 * @param document is the document that the newly created style should be added to.
 * @param foreground is the color representing the foreground color of the text in this style (color
 * of the text).
 * @param background is the color representing the background color of the text in this style (color
 * behind/around the text).
 * @return the updated document.
 *
 * @since 3.0
 * @author Brandon Buck
 */
private fun addNewStyle(
        document: StyledDocument,
        foreground: TextColor,
        background: TextColor
): StyledDocument {
    val styleName = "${foreground.charCode}${background.charCode}"
    val parentStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE)

    val temp = document.addStyle(styleName, parentStyle)
    StyleConstants.setForeground(temp, foreground.color)
    StyleConstants.setBackground(temp, background.color)

    return document
}

/**
 * Update a `SimpleAttributeSet's` font with the provided font.
 *
 * @param attributeSet is the attribute set that will have it's font updated.
 * @param font is the font that should be applied to the provided `SimpleAttributeSet`.
 * @return the `SimpleAttributeSet` after having had it's font updated.
 *
 * @since 3.0
 * @author Brandon Buck
 */
private fun setSasFont(attributeSet: SimpleAttributeSet, font: Font): SimpleAttributeSet {
    StyleConstants.setFontFamily(attributeSet, font.family)
    StyleConstants.setFontSize(attributeSet, font.size)
    StyleConstants.setBold(attributeSet, font.isBold)
    StyleConstants.setItalic(attributeSet, font.isItalic)

    return attributeSet
}

/**
 * Change the font for the entire document.
 *
 * @param document is the document that should have it's font updated.
 * @param font is the new font for the document's text.
 * @return the `StyledDocument` after the font has been changed.
 *
 * @since 3.0
 * @author Brandon Buck
 */
fun changeFont(document: StyledDocument, font: Font): StyledDocument {
    var newAttributeSet = SimpleAttributeSet()
    newAttributeSet = setSasFont(newAttributeSet, font)
    document.setCharacterAttributes(0, document.length, newAttributeSet, false)

    return document
}

/**
 * Add a new `TextColor` to the provided `StyledDocument`.
 *
 * Adding a color requires all the previously added colors so that the new color can be setup with
 * all existing colors as background colors as well as all previously defined colors as foreground
 * colors with the new color acting as foreground and background (respectively).
 *
 * @param document the dcoument that the new color will be added to.
 * @param newColor is the new text color that will be added to the document.
 * @apram allColors is the list of all the existing colors that have been added previously.
 * @return the updated document containing the necessary rules for the new color.
 *
 * @since 3.0
 * @author Brandon Buck
 */
fun addNewColor(
        document: StyledDocument,
        newColor: TextColor,
        allColors: List<TextColor>
): StyledDocument {
    var updatedDocument = document
    for (color in allColors) {
        // apply all color backgrounds to the new color as foreground
        updatedDocument = addNewStyle(updatedDocument, newColor, color)
        // apply all color foregrounds to the new color as background
        updatedDocument = addNewStyle(updatedDocument, color, newColor)
    }

    return updatedDocument
}

/**
 * Remove a color from the document.
 *
 * This has to remove the occurrences of the color as a foreground and as a background which
 * requires all the colors currently in the document.
 *
 * @param document is the document the color will be removed from.
 * @param colorToRemove is the color that will be removed from the provided document.
 * @param allColors is all the current colors added to the document.
 * @return the updated `StyledDocument`.
 *
 * @since 3.0
 * @author BrandonBuck
 */
fun removeColor(
        document: StyledDocument,
        colorToRemove: TextColor,
        allColors: List<TextColor>
): StyledDocument {
    var updatedDocument = document

    updatedDocument.removeStyle("${colorToRemove.charCode}${colorToRemove.charCode}")

    for (color in allColors) {
        val colorAsForeground = "${colorToRemove.charCode}${color.charCode}"
        val colorAsBackground = "${color.charCode}${colorToRemove.charCode}"

        updatedDocument.removeStyle(colorAsForeground)
        updatedDocument.removeStyle(colorAsBackground)
    }

    return updatedDocument
}
