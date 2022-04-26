package dev.bbuck.dragonconsole.text

class StoredInput(
        var isInfinite: Boolean,
        var protected: Boolean,
        var range: Int,
        val input: InputString
) {
    fun matches(isInfinite: Boolean, protected: Boolean, range: Int): Boolean {
        if (this.isInfinite != isInfinite) {
            return false
        }

        if (this.isInfinite) {
            return this.protected == protected
        }

        if (this.range == range && this.protected == protected) {
            return true
        }

        return false
    }
}
