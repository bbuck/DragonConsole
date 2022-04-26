@file:JvmName("FileProcessor")

package dev.bbuck.dragonconsole.file

import java.awt.Font
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import javax.swing.JOptionPane
import javax.swing.JOptionPane.showMessageDialog

/**
 * Reads the contents of the file pointed to by the give file path.
 *
 * @param filePath is the absolute or relative path to the file.
 * @return the entire contents of the file as a string.
 * @throws IllegalArgumentException if the file does not exist
 *
 * @since 3.0
 * @author Brandon Buck
 */
fun readText(filePath: String): String = readText(File(filePath))

/**
 * Reads the contents of the given file.
 *
 * @param file is the file that will have it's content read.
 * @return the entire contents of the file as a string.
 * @throws IllegalArgumentException if the file does not exist
 *
 * @since 3.0
 * @author Brandon Buck
 */
fun readText(file: File): String {
    if (!file.exists()) {
        throw IllegalArgumentException("File does not exist at path \"${file.absolutePath}\"")
    }

    val contents = StringBuilder()

    val fileReader: FileReader?
    val lineReader: BufferedReader?

    try {
        fileReader = FileReader(file)
        lineReader = BufferedReader(fileReader)

        var line = lineReader.readLine()
        while (line != null) {
            contents.append(line).append('\n')
            line = lineReader.readLine()
        }

        lineReader.close()
        fileReader.close()
    } catch (e: IOException) {
        showMessageDialog(
                null,
                "Error #0008\nFailed to read the given file.\n${e.message}",
                "Exception Reading File",
                JOptionPane.ERROR_MESSAGE
        )
    }

    return contents.toString()
}

/**
 * Reads a file that is stored in the JAR alongside DragonConsole.
 *
 * @param file is the name of the resource that should be read.
 * @return the entire contents of the file as a string.
 *
 * @since 3.0
 * @author Brandon Buck
 */
fun readDCResource(file: String): String {
    val contents = StringBuilder()

    try {
        val inStream =
                object {}.javaClass.getResourceAsStream("/com/eleet/dragonconsole/resources/$file")
        val inStreamReader = InputStreamReader(inStream)
        val lineReader = BufferedReader(inStreamReader)

        var line = lineReader.readLine()
        while (line != null) {
            contents.append(line).append('\n')
            line = lineReader.readLine()
        }

        lineReader.close()
        inStreamReader.close()
        inStream.close()
    } catch (e: IOException) {
        showMessageDialog(
                null,
                "Error #0009\nFailed to read the file from the jar!\n${e.message}",
                "Exception Reading Resource",
                JOptionPane.ERROR_MESSAGE
        )
    }

    return contents.toString()
}

/**
 * Reads the font file stored alongside the DragonConsole in the JAR file.
 *
 * @return the font read from the JAR.
 *
 * @since 3.0
 * @author Brandon Buck
 */
fun getConsoleFont(): Font? {
    var font: Font? = null

    try {
        val inStream =
                object {}.javaClass.getResourceAsStream("/com/eleet/dragonconsole/font/dvsm.ttf")
        font = Font.createFont(Font.PLAIN, inStream)
        inStream.close()
    } catch (e: IOException) {
        showMessageDialog(
                null,
                "Error #0010\nFailed to load the font file from the jar!\n${e.message}",
                "Exception Reading Font",
                JOptionPane.ERROR_MESSAGE
        )
    }

    return font
}
