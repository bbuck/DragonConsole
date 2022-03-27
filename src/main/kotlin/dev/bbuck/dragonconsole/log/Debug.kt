@file:JvmName("Debug")

package dev.bbuck.dragonconsole.log

private var debugOn = false

/**
 * Turn on the debug mode so that debug logs will print.
 *
 * @since 3.0
 * @author Brandon Buck
 */
fun turnOn() {
    debugOn = true
}

/**
 * Turn off the debug mode so that debug logs no longer print.
 *
 * @since 3.0
 * @author Brandon Buck
 */
fun turnOff() {
    debugOn = false
}

/**
 * Print a debug message if the debug mode is enabled.
 *
 * @param msg is what should be printed if debug is enabled.
 *
 * @since 3.0
 * @author Brandon Buck
 */
fun print(msg: String) {
    if (debugOn) {
        println(msg)
    }
}
