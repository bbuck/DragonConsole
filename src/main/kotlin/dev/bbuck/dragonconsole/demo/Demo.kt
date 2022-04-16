@file:JvmName("Demo")

package dev.bbuck.dragonconsole.demo

import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    SwingUtilities.invokeLater {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        val frame = DragonConsoleFrame()
        frame.console.setCommandProcessor(DemoProcessor())
        frame.console.append("&ob>> ")
        frame.setVisible(true)
    }
}
