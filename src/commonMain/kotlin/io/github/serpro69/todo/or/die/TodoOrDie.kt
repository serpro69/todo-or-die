package io.github.serpro69.todo.or.die

import io.github.serpro69.todo.or.die._Config.messageLevel
import io.github.serpro69.todo.or.die._Config.printCantDie
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Promotes a healthy procrastination of doing something [by] a specified date
 * by throwing an exception if the due date has passed and the [task] hasn't been resolved.
 *
 * Examples:
 * ```
 * // This will fail if current system date > "1970-01-01"
 * TODO("1970-01-01") { "Find The Answer to the Ultimate Question of Life, The Universe, and Everything" }
 *
 * // This will never fail
 * TODO(today) { "Reverse the workings of the second law of thermodynamics" }
 *
 * // This will start failing from "2061-05-15"
 * TODO("2061-05-14") { "Massively decrease the net amount of entropy of the universe" }
 * ```
 *
 * @param by due date for the task. [OverdueError] will be raised when this date is overdue
 * @param task description of what needs to be done
 *
 * @throws OverdueError when `localSystemDate > by`
 */
@Suppress("FunctionName")
@Throws(OverdueError::class)
fun TODO(by: String, task: () -> String) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dueBy = LocalDate.parse(by)

    if (today > dueBy) {
        when (printCantDie) {
            false -> failedTodo(by, task)
            true -> when (messageLevel) {
                Level.STACKTRACE -> {
                    try {
                        failedTodo(by, task)
                    } catch (e: OverdueError) {
                        e.printStackTrace()
                    }
                }
                else -> {
                    print(msg = "Overdue todo task that was due on '$by':\n${task.invoke()}\n", level = messageLevel)
                }
            }
        }
    }
}

private fun failedTodo(by: String, task: () -> String): Nothing =
    throw OverdueError("Overdue todo task that was due on '$by':\n=> ${task.invoke()}\n")

private fun print(msg: String, level: Level) {
    when (level) {
        Level.DEBUG -> Logger.debug(msg)
        Level.INFO -> Logger.info(msg)
        Level.WARN -> Logger.warn(msg)
        else -> Logger.error(msg)
    }
}

/* TODO
    Add another function: TODO(task: String, condition: () -> Boolean) - fails based on the `condition`
 */