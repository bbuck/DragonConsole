package dev.bbuck.dragonconsole.ui

import dev.bbuck.dragonconsole.text.InputString
import dev.bbuck.dragonconsole.text.StoredInput
import java.awt.Toolkit
import javax.swing.JOptionPane
import javax.swing.JOptionPane.showMessageDialog
import javax.swing.JTextPane
import javax.swing.text.AbstractDocument
import javax.swing.text.AttributeSet
import javax.swing.text.DocumentFilter

const val BYPASS = "<DCb />-"

class InputController(var inputAttributeSet: AttributeSet?) : DocumentFilter() {
    private var inputRangeStart = 0
    var rangeEnd = 0
    var protected = false
    val inputString = InputString("")
    var isReceivingInput = false
    var consoleTextPane: JTextPane? = null
    var protectedChar = "*"
    var bypassRemove = false
    var ignoreInput = false
    var stored: StoredInput? = null
    var consoleInputMethod = true

    fun installConsole(textPane: JTextPane) {
        consoleTextPane = textPane
    }

    fun isProtected(): Boolean = protected

    fun getBypassPrefix(): String = BYPASS

    fun reset() {
        inputRangeStart = -1
        rangeEnd = 0
        protected = false
        inputString.clear()
        isReceivingInput = false
    }

    public fun setRangeStart(value: Int) {
        if (!isReceivingInput) {
            return
        }

        inputRangeStart = value

        if (rangeEnd > 0) {
            rangeEnd += value
        }
    }

    public fun getRangeStart() = inputRangeStart

    fun setInput(newInput: String) {
        if (!isReceivingInput || !isInfiniteInput()) {
            return
        }

        val styledDoc = consoleTextPane?.getStyledDocument()
        if (styledDoc == null) {
            return
        }

        val docLength = styledDoc.length
        bypassRemove = true
        styledDoc.remove(inputRangeStart, docLength - inputRangeStart)
        inputString.set(newInput)

        val prefix =
                if (consoleInputMethod) {
                    BYPASS
                } else {
                    ""
                }

        val newString =
                if (protected) {
                    getProtectedString(newInput.length)
                } else {
                    newInput
                }
        styledDoc.insertString(inputRangeStart, "$prefix$newString", inputAttributeSet)
    }

    fun getInputRangeEnd() = rangeEnd

    fun isInfiniteInput(): Boolean = rangeEnd == -1

    fun setInputStyle(inputCode: String): Boolean {
        inputRangeStart = -1
        rangeEnd = 0
        protected = false
        inputString.clear()
        isReceivingInput = true

        if (inputCode == "%i;") {
            rangeEnd = -1

            return false
        }

        var inputStyle = inputCode.substring(2, inputCode.length - 1)
        if (inputStyle.isEmpty()) {
            rangeEnd = -1

            return false
        }

        if (inputStyle.last() == '+' || inputStyle.last() == '-') {
            protected = inputStyle.last() == '+'
            inputStyle = inputStyle.substring(0, inputStyle.length - 1)
        }

        if (inputStyle.length == 0) {
            rangeEnd = -1

            return false
        }

        rangeEnd = inputStyle.toInt()
        inputString.set(getInputRangeString())

        return true
    }

    fun getInputRangeString(): String {
        if (!isReceivingInput || rangeEnd <= 0) {
            return ""
        }

        val start =
                if (inputRangeStart > 0) {
                    inputRangeStart
                } else {
                    0
                }
        val numSpaces = rangeEnd - start
        return " ".repeat(numSpaces)
    }

    fun getInputRangeStart(): Int =
            if (isReceivingInput) {
                inputRangeStart
            } else {
                -1
            }

    fun clearText() {
        reset()
        bypassRemove = true
        val styledDoc = consoleTextPane?.getStyledDocument()
        if (styledDoc == null) {
            return
        }
        styledDoc.remove(0, styledDoc.length)
    }

    fun setBasicInput(startPosition: Int) {
        inputRangeStart = startPosition
        rangeEnd = -1
        protected = false
        inputString.clear()
        isReceivingInput = true
    }

    fun setProtectedChar(protectedChar: Char) {
        this.protectedChar = protectedChar.toString()
    }

    fun getInput(): String {
        isReceivingInput = false

        return inputString.get().trim()
    }

    override fun insertString(
            filterBypass: FilterBypass,
            offset: Int,
            stringValue: String,
            attributeSet: AttributeSet
    ) {
        when {
            stringValue.startsWith(BYPASS) -> {
                val insertable = stringValue.substring(BYPASS.length)
                filterBypass.insertString(offset, insertable, attributeSet)
            }
            (isReceivingInput && isInfiniteInput() && offset >= inputRangeStart) -> {
                val insertable =
                        if (protected) {
                            getProtectedString(stringValue.length)
                        } else {
                            stringValue
                        }
                filterBypass.insertString(offset, insertable, inputAttributeSet)
                inputString.insert(offset - inputRangeStart, insertable)
            }
            else -> Toolkit.getDefaultToolkit().beep()
        }
    }

    override fun replace(
            filterBypass: FilterBypass,
            offset: Int,
            length: Int,
            stringValue: String,
            attributeSet: AttributeSet
    ) {
        if (stringValue.startsWith(BYPASS)) {
            val withoutBypass = stringValue.substring(BYPASS.length)
            val replaceString =
                    if (protected) {
                        restoreProtectedString(withoutBypass)
                    } else {
                        withoutBypass
                    }
            filterBypass.replace(offset, length, replaceString, attributeSet)

            return
        }

        if (ignoreInput) {
            return
        }

        if (!isReceivingInput || inputRangeStart <= 0 || offset < inputRangeStart) {
            Toolkit.getDefaultToolkit().beep()

            return
        }

        if (isInfiniteInput()) {
            if (protected) {
                filterBypass.replace(offset, length, protectedChar, inputAttributeSet)
            } else {
                filterBypass.replace(offset, length, stringValue, inputAttributeSet)
            }

            inputString.replace((offset - inputRangeStart), length, stringValue)

            return
        }

        if ((offset + 1) <= rangeEnd) {
            val inserted = inputString.rangeInsert((offset - inputRangeStart), stringValue)

            if (inserted) {
                val replaceString =
                        if (protected) {
                            protectedChar
                        } else {
                            stringValue
                        }
                filterBypass.replace(offset, length, replaceString, inputAttributeSet)

                if (inputString.endIsEmpty()) {
                    filterBypass.remove(rangeEnd - 1, 1)
                } else {
                    filterBypass.remove(rangeEnd, 1)
                }

                return
            }
        }

        Toolkit.getDefaultToolkit().beep()
    }

    override fun remove(filterBypass: FilterBypass, offset: Int, length: Int) {
        if (ignoreInput) {
            return
        }

        if (bypassRemove) {
            bypassRemove = false
            filterBypass.remove(offset, length)

            return
        }

        if (!isReceivingInput || inputRangeStart <= 0 || offset < inputRangeStart) {
            Toolkit.getDefaultToolkit().beep()

            return
        }

        if (isInfiniteInput()) {
            filterBypass.remove(offset, length)
            val start = offset - inputRangeStart
            if (start < inputString.length()) {
                inputString.remove(start, length)
            }

            return
        }

        filterBypass.remove(offset, length)
        filterBypass.insertString((rangeEnd - 1), " ", inputAttributeSet)

        if (consoleTextPane?.caretPosition == rangeEnd) {
            consoleTextPane?.caretPosition = rangeEnd - 1
        }

        inputString.rangeRemove((offset - inputRangeStart), length)
    }

    public fun hasStoredInput(): Boolean = stored != null

    public fun storeInput() {
        stored =
                StoredInput(
                        isInfiniteInput(),
                        protected,
                        (rangeEnd - inputRangeStart),
                        InputString(inputString.get())
                )
        reset()
    }

    public fun restoreInput(): Boolean {
        val storedInput = stored

        if (storedInput == null || !isReceivingInput) {
            stored = null

            return false
        }

        if (!storedInput.matches(isInfiniteInput(), protected, (rangeEnd - inputRangeStart))) {
            stored = null

            return false
        }

        inputString.set(storedInput.input.get())
        val end =
                if (isInfiniteInput()) {
                    0
                } else {
                    rangeEnd - inputRangeStart
                }

        try {
            val replaceString =
                    if (consoleInputMethod) {
                        BYPASS + inputString.get()
                    } else {
                        inputString.get()
                    }
            val document = consoleTextPane?.styledDocument as AbstractDocument
            document.replace(inputRangeStart, end, replaceString, inputAttributeSet)
        } catch (exc: Exception) {
            showMessageDialog(
                    null,
                    "Error #0013\nFailed to restore the Input!\n${exc.message}",
                    "Error Caught",
                    JOptionPane.ERROR_MESSAGE
            )
        }

        stored = null

        return true
    }

    private fun getProtectedString(length: Int): String = protectedChar.repeat(length)

    private fun restoreProtectedString(original: String): String =
            protectedChar.repeat(original.length)
}
