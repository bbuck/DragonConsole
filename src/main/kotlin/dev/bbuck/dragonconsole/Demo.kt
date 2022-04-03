@file:JvmName("Demo")

package dev.bbuck.dragonconsole

import com.eleet.dragonconsole.DemoProcessor
import javax.swing.SwingUtilities

fun main() {
    SwingUtilities.invokeLater {
        val frame = DragonConsoleFrame()
        frame.console.setCommandProcessor(DemoProcessor())
        frame.console.append("&ob>> ")
        frame.setVisible(true)
    }
}
