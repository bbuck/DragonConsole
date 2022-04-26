package dev.bbuck.dragonconsole.text

import dev.bbuck.dragonconsole.log.debugLog

// Leaving undocumented, this looks like a poor implementation of StringBuilder
// and is obselete as of the Kotlin clean up
@Deprecated("Poor man's StringBuilder", ReplaceWith("StringBuilder"))
public class InputString(initial: String) {
    val builder: StringBuilder = StringBuilder()

    init {
        builder.append(initial)
    }

    public fun clear() {
        builder.clear()
    }

    public fun append(other: String) {
        builder.append(other)
        debugLog("\"$builder\" - append(\"$other\")")
    }

    public fun insert(where: Int, toInsert: String) {
        builder.insert(where, toInsert)

        debugLog("\"$builder\" - insert($where, \"$toInsert\")")
    }

    public fun remove(start: Int, length: Int) {
        val end = start + length

        builder.delete(start, end)

        debugLog("\"$builder\" - remove($start, $length)")
    }

    public fun rangeRemove(start: Int, length: Int) {
        remove(start, length)
        builder.append(' ')

        debugLog("\"$builder\" - rangeRemove($start, $length)")
    }

    public fun rangeInsert(where: Int, value: String): Boolean {
        if (where >= builder.length || !endIsEmpty()) {
            return false
        }

        builder.replace(where, where + 1, value)

        debugLog("\"$builder\" - rangeInsert($where, \"$value\")")

        return true
    }

    public fun replace(start: Int, length: Int, value: String) {
        if (start >= builder.length || start + length >= builder.length) {
            append(value)

            return
        }

        val end = start + length

        builder.replace(start, end, value)

        debugLog("\"$builder\" - replace($start, $length, \"$value\")")
    }

    public fun set(newValue: String) {
        builder.clear().append(newValue)
    }

    public fun get(): String = builder.toString()

    public fun endIsEmpty(): Boolean = builder.last() == ' '

    public fun length(): Int = builder.length

    public override fun toString(): String = builder.toString()
}
