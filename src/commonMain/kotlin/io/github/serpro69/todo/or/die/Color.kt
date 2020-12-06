package io.github.serpro69.todo.or.die

/**
 * Provides ANSI color codes for output.
 */
internal enum class Color(ansiCode: Int) {
    NONE(0),
    RED(31),
    YELLOW(33),
    BLUE(34),
    WHITE(37);

    private val ansiString: String = "\u001B[${ansiCode}m"

    override fun toString(): String {
        return ansiString
    }
}