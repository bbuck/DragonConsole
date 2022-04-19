package dev.bbuck.dragonconsole

import dev.bbuck.dragonconsole.file.getConsoleFont as loadConsoleFont
import dev.bbuck.dragonconsole.file.readDCResource
import dev.bbuck.dragonconsole.text.ANSI
import dev.bbuck.dragonconsole.text.TextColor
import dev.bbuck.dragonconsole.text.addNewColor
import dev.bbuck.dragonconsole.ui.InputController
import dev.bbuck.dragonconsole.ui.PromptPanel
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.AdjustmentListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JOptionPane
import javax.swing.JOptionPane.showMessageDialog
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextPane
import javax.swing.KeyStroke
import javax.swing.event.CaretListener
import javax.swing.text.AbstractDocument
import javax.swing.text.DefaultEditorKit
import javax.swing.text.JTextComponent
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
    }

    var keepScrollBarMax = false
    var useANSIColorCodes = false

    private var defaultColor = "xb"
    private var systemColor = "cb"
    private var errorColor = "rb"
    var inputColor = "xb"
        set(value) {
            field = value
            setInputAttribute()
        }

    private var colorCodeChar = '&'

    private val previousEntries = mutableListOf<String>()
    private val numberOfPreviousEntries = 10
    private var currentPreviousEntry = 0

    private val textColors = mutableListOf<TextColor>()
    private val inputControl = InputController(null)
    private val consolePrompt = PromptPanel(">> ", defaultColor)

    private val consoleFont = loadConsoleFont()?.deriveFont(Font.PLAIN, 14f)

    private var consolePane: JTextPane? = null
    private var consoleStyledDocument: StyledDocument? = null
    private var inputArea: JTextArea? = null
    private var consoleScrollPane: JScrollPane? = null

    private var commandProcessor: CommandProcessor? = null

    init {
        setConsoleSize(cWidth, cHeight)

        if (consoleFont != null) {
            consolePrompt.setPromptFont(consoleFont)
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
        consolePane.setFont(consoleFont)
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
        inputArea.setFont(consoleFont)
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

    public fun append(output: String) {
        // TODO(bbuck) implement
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
}
