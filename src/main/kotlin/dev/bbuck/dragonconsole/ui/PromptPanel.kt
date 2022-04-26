package dev.bbuck.dragonconsole.ui

import dev.bbuck.dragonconsole.text.TextColor
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import javax.swing.JPanel

class PromptPanel(prompt: String, var defaultColor: String) : JPanel() {
    var promptLabel: PromptLabel

    init {
        promptLabel = PromptLabel(prompt, defaultColor)
        promptLabel.setOpaque(false)
        setLayout(BorderLayout())
        add(promptLabel, BorderLayout.NORTH)
    }

    public fun setPrompt(newPrompt: String) {
        promptLabel.setText(newPrompt)
    }

    public fun addColor(textColor: TextColor) {
        promptLabel.addColor(textColor)
    }

    public fun clearColors() {
        promptLabel.clearColors()
    }

    public fun removeColor(textColor: TextColor) {
        promptLabel.removeColor(textColor)
    }

    public fun setColorCodeChar(colorCodeChar: Char) {
        promptLabel.colorCodeChar = colorCodeChar
    }

    public fun getPrompt(): String {
        return promptLabel.getText()
    }

    public fun setPromptFont(font: Font) {
        promptLabel.setFont(font)
        promptLabel.revalidate()
        promptLabel.repaint()
    }

    public fun setPromptForeground(newColor: Color) {
        promptLabel.setForeground(newColor)
        promptLabel.revalidate()
        promptLabel.repaint()
    }
}
