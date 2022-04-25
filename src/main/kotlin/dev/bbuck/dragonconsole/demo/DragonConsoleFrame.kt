package dev.bbuck.dragonconsole.demo

import dev.bbuck.dragonconsole.DragonConsole
import java.awt.Toolkit
import javax.swing.JFrame

/**
 * A simple entrypoint for a GUI with a DragonConsole already added. Constructing a new frame
 * without a title uses a default title and without a console creates a new console instance.
 *
 * @param title is the title of the frame.
 * @param console is the console instance to use in the frame if one has already been defined.
 */
class DragonConsoleFrame
@JvmOverloads
constructor(
        title: String = "DragonConsoleFrame ${DragonConsole.getVersion()}",
        val console: DragonConsole = DragonConsole()
) : JFrame(title) {
    init {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        setResizable(false)
        add(console)
        pack()
        console.setInputFocus()
        centerWindow()
    }

    // support the console only which won't be generated via JvmOverloads
    constructor(dragonConsole: DragonConsole) : this(console = dragonConsole)

    // centers the window in the current screen
    private fun centerWindow() {
        val defaultToolkit = Toolkit.getDefaultToolkit()
        val screenSize = defaultToolkit.screenSize
        setLocation((screenSize.width / 2) - (width / 2), (screenSize.height / 2) - (height / 2))
    }
}
