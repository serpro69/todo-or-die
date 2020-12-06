package io.github.serpro69.todo.or.die

/**
 * A standalone dedicated logger for the application.
 * The main motivation for having this is to avoid extra dependencies and configuration for logging.
 */
internal object Logger {

    private fun print(msg: String, color: Color) {
        val message = msg.split("\n").joinToString("\n") {
            "$color$it${Color.NONE}"
        }
        println(message)
    }

    /**
     * Prints DEBUG [msg] to stdout.
     *
     * The message is not colorized at this level.
     */
    fun debug(msg: String) = print(msg, Color.WHITE)

    /**
     * Prints INFO [msg] to stdout.
     *
     * The message is colored in [Color.BLUE] ANSI color.
     */
    fun info(msg: String) = print(msg, Color.BLUE)

    /**
     * Prints INFO [msg] to stdout.
     *
     * The message is colored in [Color.YELLOW] ANSI color.
     */
    fun warn(msg: String) = print(msg, Color.YELLOW)

    /**
     * Prints INFO [msg] to stdout.
     *
     * The message is colored in [Color.RED] ANSI color.
     */
    fun error(msg: String) = print(msg, Color.RED)
}