package dev.bbuck.dragonconsole

import dev.bbuck.dragonconsole.file.readText
import java.io.File

public open class CommandProcessor {
    protected var console: DragonConsole? = null
        private set

    public fun install(console: DragonConsole) {
        this.console = console
    }

    public fun uninstall() {
        console = null
    }

    public open fun processCommand(input: String) = output(input + "\n")

    @Deprecated("Use the output(message) method instead")
    public fun outputToConsole(message: String) = output(message)

    public fun outputSystem(message: String) {
        console?.appendSystemMessage(message)
    }

    public fun outputError(message: String) {
        console?.appendErrorMessage(message)
    }

    public open fun output(message: String) {
        console?.append(message)
    }

    public fun readText(filePath: String): String = readText(filePath)

    public fun readText(file: File): String = readText(file)

    public fun convertToANSIColors(toConvert: String): String =
            console?.convertToANSIColors(toConvert) ?: toConvert

    public fun convertToDCColors(toConvert: String): String =
            console?.convertToDCColors(toConvert) ?: toConvert
}
