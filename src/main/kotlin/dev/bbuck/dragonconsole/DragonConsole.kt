package dev.bbuck.dragonconsole

import dev.bbuck.dragonconsole.file.getConsoleFont as loadConsoleFont
import dev.bbuck.dragonconsole.file.readDCResource
import dev.bbuck.dragonconsole.text.ANSI
import dev.bbuck.dragonconsole.text.TextColor
import dev.bbuck.dragonconsole.text.addNewColor
import dev.bbuck.dragonconsole.text.changeFont
import dev.bbuck.dragonconsole.text.removeColor
import dev.bbuck.dragonconsole.ui.InputController
import dev.bbuck.dragonconsole.ui.PromptPanel
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.AdjustmentEvent
import java.awt.event.AdjustmentListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JOptionPane
import javax.swing.JOptionPane.showMessageDialog
import javax.swing.JPanel
import javax.swing.JScrollBar
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextPane
import javax.swing.KeyStroke
import javax.swing.SwingUtilities
import javax.swing.event.CaretEvent
import javax.swing.event.CaretListener
import javax.swing.text.AbstractDocument
import javax.swing.text.DefaultEditorKit
import javax.swing.text.JTextComponent
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyledDocument

val VERSION = "3"
val SUB_VERSION = "1"
val BUG_FIX = "0"
val VERSION_TAG = ""

val DEFAULT_WIDTH = 725
val DEFAULT_HEIGHT = 450

val DEFAULT_BACKGROUND = Color.BLACK
val DEFAULT_FOREGROUND = Color.GRAY.brighter()
val DEFAULT_CARET = DEFAULT_FOREGROUND
val DEFAULT_MAC_BACKGROUND = Color.WHITE
val DEFAULT_MAC_FOREGROUND = Color.BLACK
val DEFAULT_MAC_CARET = Color.BLACK

val NUMBER_OF_PREVOIUS_ENTRIES = 10

val OVERRIDE_KEY_BINDINGS =
        arrayOf<JTextComponent.KeyBinding>(
                JTextComponent.KeyBinding(
                        KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK),
                        DefaultEditorKit.copyAction
                ),
                JTextComponent.KeyBinding(
                        KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK),
                        DefaultEditorKit.pasteAction
                ),
                JTextComponent.KeyBinding(
                        KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK),
                        DefaultEditorKit.beepAction
                ),
        )

public class DragonConsole(
        private var cWidth: Int,
        private var cHeight: Int,
        private var useInlineInput: Boolean,
        private var printDefaultMessage: Boolean
) : JPanel(), KeyListener, CaretListener, AdjustmentListener {
    companion object {
        @JvmStatic val INTENSE_ORANGE = Color.ORANGE
        @JvmStatic val ORANGE = INTENSE_ORANGE.darker()
        @JvmStatic val INTENSE_PURPLE = Color(128, 0, 255)
        @JvmStatic val PURPLE = INTENSE_PURPLE.darker()
        @JvmStatic val INTENSE_GOLD = Color(241, 234, 139)
        @JvmStatic val GOLD = INTENSE_GOLD.darker()

        @JvmStatic
        public fun getVersion(): String {
            return "v$VERSION.$SUB_VERSION.$BUG_FIX$VERSION_TAG"
        }
    }

    var keepScrollBarMax = false
    var useANSIColorCodes = false

    var defaultColor = "xb"
    var systemColor = "cb"
    var errorColor = "rb"
    var inputColor = "xb"
        set(value) {
            field = value
            setInputAttribute()
        }
    private var currentStyle = defaultColor
        set(value) {
            val oldStyle = field
            var newStyle = ""
            if (value.length == 2) {
                if (value.contains("0")) {
                    newStyle = defaultColor
                } else if (value.contains("-")) {
                    if (!value.equals("--")) {
                        if (value[0] == '-') {
                            newStyle = "${oldStyle[0]}${value[1]}"
                        } else {
                            newStyle = "${value[0]}${oldStyle[1]}"
                        }
                    }
                } else {
                    newStyle = value
                }

                val foreground = if (containsColorCode(newStyle[0])) newStyle[0] else oldStyle[0]
                val background = if (containsColorCode(newStyle[1])) newStyle[1] else oldStyle[1]

                field = "$foreground$background"
            } else {
                field = oldStyle
            }
        }
    private var ansiStyle: SimpleAttributeSet? = null

    var colorCodeChar = '&'

    var inputFieldNewLine = true
    var ignoreInput = false
        set(value) {
            field = value
            inputControl.ignoreInput = value
        }
    var inputCarryOver = true

    private val previousEntries = mutableListOf<String>()
    private var currentPreviousEntry = 0

    private val textColors = mutableListOf<TextColor>()
    private val inputControl = InputController(null)
    private val consolePrompt = PromptPanel(">> ", defaultColor)

    private var currentConsoleFont = loadConsoleFont()?.deriveFont(Font.PLAIN, 14f)

    private var consolePane: JTextPane? = null
    private var consoleStyledDocument: StyledDocument? = null
    private var inputArea: JTextArea? = null
    private var consoleScrollPane: JScrollPane? = null
    private var commandProcessor: CommandProcessor? = null

    private var ignoreAdjustment = false
    private var isScrollBarAtMax = false

    init {
        setConsoleSize(cWidth, cHeight)

        val currentConsoleFont = currentConsoleFont
        if (currentConsoleFont != null) {
            consolePrompt.setPromptFont(currentConsoleFont)
        }

        if (useInlineInput) {
            consolePane =
                    object : JTextPane() {
                        override fun paste() {
                            try {
                                val pasteText =
                                        (Toolkit.getDefaultToolkit()
                                                .getSystemClipboard()
                                                .getData(DataFlavor.stringFlavor)) as
                                                String
                                getDocument().insertString(getCaretPosition(), pasteText, null)
                            } catch (ex: Exception) {
                                showMessageDialog(
                                        this,
                                        "Error #0001\nFailed to paste text to the document!\n${ex.message}",
                                        "Error Caught!",
                                        JOptionPane.ERROR_MESSAGE
                                )
                            }
                        }
                    }

            consolePane?.addKeyListener(this)
            consolePane?.addCaretListener(this)
        } else {
            consolePane = JTextPane()
            consolePane?.setEditable(false)
        }

        val consolePane = consolePane
        if (consolePane == null) {
            throw IllegalStateException("No text pane was created, this should be impossible")
        }

        consolePane.setBackground(DEFAULT_BACKGROUND)
        consolePane.setForeground(DEFAULT_FOREGROUND)
        consolePane.setCaretColor(DEFAULT_CARET)
        consolePane.setFont(currentConsoleFont)
        consolePane.setBorder(null)

        val keymap = consolePane.getKeymap()
        JTextComponent.loadKeymap(keymap, OVERRIDE_KEY_BINDINGS, consolePane.getActions())

        inputControl.installConsole(consolePane)
        inputControl.consoleInputMethod = useInlineInput

        consoleStyledDocument = consolePane.styledDocument
        if (useInlineInput) {
            (consoleStyledDocument as AbstractDocument).documentFilter = inputControl
        }

        inputArea = JTextArea()
        val inputArea = inputArea
        if (inputArea == null) {
            throw IllegalStateException("inputArea is null, this is impossible")
        }
        inputArea.setBackground(DEFAULT_BACKGROUND)
        inputArea.setForeground(DEFAULT_FOREGROUND)
        inputArea.setCaretColor(DEFAULT_CARET)
        inputArea.setWrapStyleWord(true)
        inputArea.setLineWrap(true)
        inputArea.setFont(currentConsoleFont)
        inputArea.setBorder(null)
        inputArea.addKeyListener(this)

        consoleScrollPane = JScrollPane(consolePane)
        consoleScrollPane?.setBorder(null)
        consoleScrollPane?.getVerticalScrollBar()?.addAdjustmentListener(this)

        val inputPanel = JPanel(BorderLayout())
        inputPanel.add(consolePrompt, BorderLayout.WEST)
        inputPanel.add(inputArea, BorderLayout.CENTER)

        val splitPane = JPanel(BorderLayout())
        splitPane.add(consoleScrollPane, BorderLayout.CENTER)
        splitPane.add(inputPanel, BorderLayout.SOUTH)

        setLayout(BorderLayout())
        if (useInlineInput) {
            add(consoleScrollPane, BorderLayout.CENTER)
        } else {
            add(splitPane, BorderLayout.CENTER)
        }

        setOutputStyles()
        setDefaultStyle()
    }

    constructor() : this(DEFAULT_WIDTH, DEFAULT_HEIGHT, true, true)
    constructor(useInlineInput: Boolean) : this(DEFAULT_WIDTH, DEFAULT_HEIGHT, useInlineInput, true)
    constructor(
            useInlineInput: Boolean,
            printDefaultMessage: Boolean
    ) : this(DEFAULT_WIDTH, DEFAULT_HEIGHT, useInlineInput, printDefaultMessage)
    constructor(width: Int, height: Int) : this(width, height, true, true)
    constructor(
            width: Int,
            height: Int,
            useInlineInput: Boolean
    ) : this(width, height, useInlineInput, true)

    public fun setConsoleSize(dimension: Dimension) {
        super.setMaximumSize(dimension)
        super.setMinimumSize(dimension)
        super.setPreferredSize(dimension)
    }

    public fun setConsoleSize(width: Int, height: Int) = setConsoleSize(Dimension(width, height))

    public fun addTextColor(code: Char, color: Color) {
        val newColor = TextColor(code, color)
        textColors.add(newColor)
        if (!useInlineInput) {
            consolePrompt.addColor(newColor)
        }

        val consoleStyledDocument = consoleStyledDocument
        if (consoleStyledDocument == null) {
            return
        }

        this.consoleStyledDocument = addNewColor(consoleStyledDocument, newColor, textColors)
    }

    public fun removeTextColor(code: Char): TextColor? {
        val consoleStyledDocument = consoleStyledDocument
        if (consoleStyledDocument == null) {
            return null
        }

        val toRemove = textColors.find { it.charCode == code }
        if (toRemove == null) {
            return null
        }

        textColors.remove(toRemove)
        removeColor(consoleStyledDocument, toRemove, textColors)
        if (!useInlineInput) {
            consolePrompt.removeColor(toRemove)
        }

        return toRemove
    }

    public fun removeTextColor(color: Color): TextColor? {
        val consoleStyledDocument = consoleStyledDocument
        if (consoleStyledDocument == null) {
            return null
        }

        val toRemove = textColors.find { it.color == color }
        if (toRemove == null) {
            return null
        }

        textColors.remove(toRemove)
        removeColor(consoleStyledDocument, toRemove, textColors)
        if (!useInlineInput) {
            consolePrompt.removeColor(toRemove)
        }

        return toRemove
    }

    public fun updateTextColor(code: Char, newColor: Color) {
        val removed = removeTextColor(code)

        if (removed == null) {
            return
        }

        addTextColor(code, newColor)
    }

    public fun updateTextColor(color: Color, newCode: Char) {
        val removed = removeTextColor(color)

        if (removed == null) {
            return
        }

        addTextColor(newCode, color)
    }

    public fun clearTextColors() {
        val consoleStyledDocument = consoleStyledDocument
        if (consoleStyledDocument == null) {
            return
        }

        textColors.forEach { color -> removeColor(consoleStyledDocument, color, textColors) }
        textColors.clear()
        if (!useInlineInput) {
            consolePrompt.clearColors()
        }
    }

    public fun setDefaultStyle() {
        consolePane?.setBackground(DEFAULT_BACKGROUND)
        consolePane?.setCaretColor(DEFAULT_CARET)
        consolePane?.setForeground(DEFAULT_FOREGROUND)
        inputArea?.setBackground(DEFAULT_BACKGROUND)
        inputArea?.setCaretColor(DEFAULT_CARET)
        inputArea?.setForeground(DEFAULT_FOREGROUND)
        consolePrompt.setPromptForeground(DEFAULT_FOREGROUND)
        consolePrompt.setBackground(DEFAULT_BACKGROUND)

        defaultColor = "xb"
        systemColor = "cb"
        errorColor = "rb"
        inputColor = "xb"

        if (useInlineInput) {
            consolePrompt.defaultColor = defaultColor
        }
        clearConsole()
        printDefault()
    }

    public fun setMacStyle() {
        consolePane?.setBackground(DEFAULT_MAC_BACKGROUND)
        consolePane?.setCaretColor(DEFAULT_MAC_CARET)
        consolePane?.setForeground(DEFAULT_MAC_FOREGROUND)
        inputArea?.setBackground(DEFAULT_MAC_BACKGROUND)
        inputArea?.setCaretColor(DEFAULT_MAC_CARET)
        inputArea?.setForeground(DEFAULT_MAC_FOREGROUND)
        consolePrompt.setPromptForeground(DEFAULT_MAC_FOREGROUND)
        consolePrompt.setBackground(DEFAULT_MAC_BACKGROUND)

        defaultColor = "bw"
        systemColor = "ow"
        errorColor = "rw"
        inputColor = "bw"

        if (useInlineInput) {
            consolePrompt.defaultColor = defaultColor
        }
        clearConsole()
        printDefault()
    }

    public fun clearConsole(): Unit = inputControl.clearText()

    @Deprecated("Language was changed from display to append", ReplaceWith("appendSystemMessage"))
    public fun displaySystemMessage(message: String) {
        appendSystemMessage(message)
    }

    public fun appendSystemMessage(message: String) {
        print(message, systemColor)
    }

    @Deprecated("Language was changed from display to append", ReplaceWith("appendErrorMessage"))
    public fun displayErrorMessage(message: String) {
        print(message, errorColor)
    }

    public fun appendErrorMessage(message: String) {
        print(message, errorColor)
    }

    public fun append(output: String) {
        if (!ignoreInput && inputCarryOver && inputControl.isReceivingInput) {
            inputControl.storeInput()
        }

        var hasInput = false
        val processed = StringBuilder()

        var i = 0
        while (i < output.length) {
            if (output[i] == colorCodeChar) {
                if (i + 1 < output.length && output[i + 1] == colorCodeChar) {
                    processed.append(colorCodeChar)
                    i += 1
                } else if (i + 2 < output.length) {
                    print(processed.toString())
                    processed.clear()

                    currentStyle = output.substring(i + 1, i + 3)
                    i += 2
                }
            } else if (output[i] == '\u001b') {
                if (output.indexOf('m', i) < output.length) {
                    print(processed.toString())
                    processed.clear()

                    val consoleStyledDocument = consoleStyledDocument
                    if (consoleStyledDocument != null) {
                        ansiStyle =
                                ANSI.getANSIAttribute(
                                        ansiStyle,
                                        output.substring(i, output.indexOf('m', i) + 1),
                                        consoleStyledDocument.getStyle(defaultColor)
                                )
                    }
                    i = output.indexOf('m', i)
                }
            } else if (output[i] == '%' && !ignoreInput) {
                if (i + 1 < output.length && output[i + 1] == '%') {
                    processed.append('%')
                    i += 1
                } else if (output.indexOf(';', i) > i) {
                    if (output[i + 1] == 'i') {
                        hasInput = true
                        val inputCommand = output.substring(i, output.indexOf(';', i) + 1)

                        if (inputControl.setInputStyle(inputCommand)) {
                            print(processed.toString())
                            processed.clear()

                            inputControl.setRangeStart(consoleStyledDocument?.length ?: 0)
                            print(inputControl.getInputRangeString(), defaultColor)
                        } else {
                            i = output.length
                            print(processed.toString())
                            processed.clear()
                            inputControl.setRangeStart(consoleStyledDocument?.length ?: 0)
                        }

                        i = output.indexOf(';', i)
                    }
                } else {
                    processed.append(output[i])
                }
            } else {
                processed.append(output[i])
            }

            i += 1
        }

        print(processed.toString())

        if (!hasInput) {
            inputControl.setBasicInput(consoleStyledDocument?.getLength() ?: 0)
        }

        setConsoleCaretPosition()
    }

    @Deprecated("Language was changed from display to append", ReplaceWith("append"))
    public fun displayString(toDisplay: String) {
        append(toDisplay)
    }

    public fun appendWithoutProcessing(output: String) {
        print(output, defaultColor)
        inputControl.setBasicInput(consoleStyledDocument?.length ?: 0)
    }

    public fun setProtectedCharacter(protectedChar: Char) {
        inputControl.setProtectedChar(protectedChar)
    }

    public fun setInputFocus() {
        if (useInlineInput) {
            return
        }

        inputArea?.requestFocusInWindow()
    }

    public fun isUseANSIColorCodes(): Boolean = useANSIColorCodes

    public fun setPrompt(newPrompt: String) {
        consolePrompt.setPrompt(newPrompt)
    }

    public fun setCommandProcessor(newProcessor: CommandProcessor) {
        val commandProcessor = commandProcessor
        if (commandProcessor != null) {
            commandProcessor.uninstall()
        }

        this.commandProcessor = newProcessor
        this.commandProcessor?.install(this)
    }

    public fun setConsoleFont(newConsoleFont: Font) {
        currentConsoleFont = newConsoleFont
        setOutputStyles()
        consolePane?.setFont(newConsoleFont)
        inputArea?.setFont(newConsoleFont)
        consolePrompt.setPromptFont(newConsoleFont)
        val consoleStyledDocument = consoleStyledDocument
        if (consoleStyledDocument != null) {
            changeFont(consoleStyledDocument, newConsoleFont)
        }
    }

    public fun convertToANSIColors(toConvert: String): String =
            ANSI.convertDCtoANSIColors(toConvert, textColors, colorCodeChar)

    public fun convertToDCColors(toConvert: String): String =
            ANSI.convertANSIToDCColors(toConvert, textColors, colorCodeChar, defaultColor)

    override public fun keyTyped(event: KeyEvent) {
        // empty
    }
    override public fun keyReleased(event: KeyEvent) {
        // empty
    }

    override public fun keyPressed(event: KeyEvent) {
        if (!useInlineInput) {
            if (keepScrollBarMax || isScrollBarAtMax) {
                ignoreAdjustment = true
                setScrollBarMax()
            }
        }

        when (event.getKeyCode()) {
            KeyEvent.VK_TAB -> event.consume()
            KeyEvent.VK_ENTER -> {
                if (ignoreInput) {
                    return
                }

                if (useInlineInput) {
                    event.consume()
                    val isProtected = inputControl.isProtected()
                    val input = inputControl.getInput()

                    val commandProcessor = commandProcessor
                    if (commandProcessor != null) {
                        commandProcessor.processCommand(input)
                    } else {
                        appendWithoutProcessing(input)
                    }

                    if (!isProtected) {
                        addPreviousEntry(input)
                    }
                } else {
                    if (inputFieldNewLine || event.isShiftDown()) {
                        inputArea?.append("\n")

                        return
                    }

                    event.consume()
                    val input = inputArea?.getText() ?: ""
                    val commandProcessor = commandProcessor
                    if (commandProcessor != null) {
                        commandProcessor.processCommand(input)
                    } else {
                        appendWithoutProcessing(input)
                    }

                    addPreviousEntry(input)
                }
            }
            KeyEvent.VK_RIGHT -> {
                if (!event.isShiftDown()) {
                    return
                }

                event.consume()

                if (!useInlineInput ||
                                (inputControl.isReceivingInput && inputControl.isInfiniteInput())
                ) {
                    currentPreviousEntry -= 1
                    if (currentPreviousEntry < 0) {
                        currentPreviousEntry = previousEntries.size - 1
                    }

                    setPreviousEntryText()
                }
            }
            KeyEvent.VK_LEFT -> {
                if (!event.isShiftDown()) {
                    return
                }

                event.consume()

                if (!useInlineInput ||
                                (inputControl.isReceivingInput && inputControl.isInfiniteInput())
                ) {
                    currentPreviousEntry += 1
                    if (currentPreviousEntry >= previousEntries.size) {
                        currentPreviousEntry = 0
                    }

                    setPreviousEntryText()
                }
            }
        }
    }

    public override fun caretUpdate(event: CaretEvent) {
        if (ignoreInput) {
            return
        }

        val location = event.dot

        if (!(inputControl.isReceivingInput && location == event.mark)) {
            return
        }

        val consolePane = consolePane
        if (consolePane == null) {
            return
        }

        if (location < inputControl.getInputRangeStart()) {
            consolePane.caretPosition = inputControl.getInputRangeStart()

            return
        }

        if (!inputControl.isInfiniteInput() && location > inputControl.getInputRangeEnd()) {
            consolePane.caretPosition = inputControl.getInputRangeEnd()

            return
        }
    }

    public override fun adjustmentValueChanged(event: AdjustmentEvent) {
        if (event.source !is JScrollBar) {
            return
        }

        if (ignoreAdjustment) {
            ignoreAdjustment = false

            return
        }

        val scrollBar = event.source as JScrollBar
        val value = event.value
        val maxValue = scrollBar.maximum - scrollBar.model.extent

        isScrollBarAtMax = value == maxValue
    }

    protected fun print(output: String, style: String) {
        ignoreAdjustment = true

        val actualOutput =
                if (useInlineInput) {
                    "${inputControl.getBypassPrefix()}$output"
                } else {
                    output
                }

        try {
            consoleStyledDocument?.insertString(
                    (consoleStyledDocument?.length ?: 0),
                    actualOutput,
                    consoleStyledDocument?.getStyle(style)
            )
        } catch (ex: Exception) {
            showMessageDialog(
                    this,
                    "Error #0006\nFailed to print the text with the given style!\n${ex.message}",
                    "Error Caught!",
                    JOptionPane.ERROR_MESSAGE
            )
        }
    }

    protected fun print(output: String, attributeSet: SimpleAttributeSet) {
        ignoreAdjustment = true

        val actualOutput =
                if (useInlineInput) {
                    "${inputControl.getBypassPrefix()}$output"
                } else {
                    output
                }

        try {
            consoleStyledDocument?.insertString(
                    (consoleStyledDocument?.length ?: 0),
                    actualOutput,
                    attributeSet
            )
        } catch (ex: Exception) {
            showMessageDialog(
                    this,
                    "Error #0007\nFailed to print the text with the ANSI style!\n${ex.message}",
                    "Error Caught!",
                    JOptionPane.ERROR_MESSAGE
            )
        }
    }

    protected fun print(output: String) {
        if (output.isEmpty()) {
            return
        }

        val ansiStyle = ansiStyle
        if (useANSIColorCodes && ansiStyle != null) {
            print(output, ansiStyle)
        } else {
            print(output, currentStyle)
        }
    }

    protected fun setScrollBarMax() {
        val consoleScrollPane = consoleScrollPane
        if (consoleScrollPane == null) {
            return
        }

        if (!isScrollBarAtMax) {
            return
        }

        val verticalBar = consoleScrollPane.verticalScrollBar
        val console = this

        SwingUtilities.invokeLater {
            try {
                if (verticalBar.isVisible()) {
                    verticalBar.setValue(verticalBar.maximum - verticalBar.model.extent)
                }

                console.repaint()
            } catch (ex: Exception) {
                showMessageDialog(
                        console,
                        "Error #0005\nFailed to set the JScrollBar to max value!\n${ex.message}",
                        "Error Caught!",
                        JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private fun fillConsoleColors() {
        addTextColor('r', ANSI.INTENSE_RED)
        addTextColor('R', ANSI.RED)
        addTextColor('l', ANSI.INTENSE_BLUE)
        addTextColor('L', ANSI.BLUE)
        addTextColor('g', ANSI.INTENSE_GREEN)
        addTextColor('G', ANSI.GREEN)
        addTextColor('y', ANSI.INTENSE_YELLOW)
        addTextColor('Y', ANSI.YELLOW)
        addTextColor('x', ANSI.WHITE)
        addTextColor('X', ANSI.INTENSE_BLACK)
        addTextColor('c', ANSI.INTENSE_CYAN)
        addTextColor('C', ANSI.CYAN)
        addTextColor('m', ANSI.INTENSE_MAGENTA)
        addTextColor('M', ANSI.MAGENTA)

        addTextColor('o', INTENSE_ORANGE)
        addTextColor('O', ORANGE)
        addTextColor('p', INTENSE_PURPLE)
        addTextColor('P', PURPLE)
        addTextColor('d', INTENSE_GOLD)
        addTextColor('D', GOLD)

        addTextColor('b', ANSI.BLACK)
        addTextColor('w', ANSI.INTENSE_WHITE)
    }

    private fun getStyleColorFromCode(code: Char): Color {
        val found = textColors.find { it.charCode == code }
        if (found != null) {
            return found.color
        }

        return Color.WHITE
    }

    private fun setInputAttribute() {
        val consoleStyledDocument = consoleStyledDocument
        if (consoleStyledDocument == null) {
            return
        }

        inputControl.inputAttributeSet = consoleStyledDocument.getStyle(inputColor)
        if (!useInlineInput) {
            inputArea?.setForeground(getStyleColorFromCode(inputColor[0]))
            inputArea?.setBackground(getStyleColorFromCode(inputColor[1]))
        }
    }

    private fun setOutputStyles() {
        fillConsoleColors()
        setInputAttribute()
    }

    private fun printDefault() {
        if (!printDefaultMessage) {
            return
        }

        var color = "b"
        // TOOD(bbuck) make less terrible
        if (consolePane?.getBackground()?.equals(Color.WHITE) == true) {
            color = "w"
        }

        try {
            append(readDCResource("logo_$color"))
        } catch (ex: Exception) {
            showMessageDialog(
                    this,
                    "Error #0002\nFailed to read teh logo from the jar!\n${ex.message}",
                    "Error Caught!",
                    JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun addPreviousEntry(entry: String) {
        previousEntries.add(entry)
        if (previousEntries.size > NUMBER_OF_PREVOIUS_ENTRIES) {
            previousEntries.removeAt(0)
        }
        currentPreviousEntry = previousEntries.size
    }

    private fun setPreviousEntryText() {
        if (currentPreviousEntry < 0 || currentPreviousEntry >= previousEntries.size) {
            return
        }

        val entry = previousEntries[currentPreviousEntry]
        if (useInlineInput) {
            if (inputControl.isReceivingInput && inputControl.isInfiniteInput()) {
                inputControl.setInput(entry)
            }
        } else {
            inputArea?.setText(entry)
            if (keepScrollBarMax || isScrollBarAtMax) {
                ignoreAdjustment = true
                setScrollBarMax()
            }
        }
    }

    private fun containsColorCode(code: Char): Boolean {
        val color = textColors.find { it.charCode == code }

        return color != null
    }

    private fun setConsoleCaretPosition() {
        val caretLocation = inputControl.getInputRangeStart()
        if (ignoreInput) {
            consolePane?.caretPosition = 0
        } else {
            if (caretLocation > -1) {
                consolePane?.caretPosition = caretLocation
            } else {
                consolePane?.caretPosition = consoleStyledDocument?.length ?: 0
            }

            if (inputCarryOver && inputControl.hasStoredInput()) {
                inputControl.restoreInput()
            }

            if (keepScrollBarMax || isScrollBarAtMax) {
                setScrollBarMax()
            }
        }
    }
}
