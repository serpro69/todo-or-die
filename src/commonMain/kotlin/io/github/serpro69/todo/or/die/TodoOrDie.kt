package io.github.serpro69.todo.or.die

import io.github.serpro69.todo.or.die._Config.messageLevel
import io.github.serpro69.todo.or.die._Config.printCantDie
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Promotes a healthy coding procrastination [by] a specified date
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
            false -> failedToDo(by, task.invoke())
            true -> when (messageLevel) {
                Level.STACKTRACE -> {
                    try {
                        failedToDo(by, task.invoke())
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

/**
 * Promotes a healthy coding procrastination by throwing an exception if the [predicate] returns `true`
 * and the [task] hasn't been resolved.
 *
 * Examples:
 * ```
 * // This will always fail
 * TODO("Find The Answer to the Ultimate Question of Life, The Universe, and Everything") { true }
 *
 * // This will never fail
 * TODO("Reverse the workings of the second law of thermodynamics") { false }
 *
 * @param task description of what needs to be done
 * @param predicate condition for the task.
 *
 * @throws OverdueError when [predicate] returns `true`
 */
@Suppress("FunctionName")
@Throws(OverdueError::class)
fun TODO(task: () -> String, predicate: () -> Boolean) {
    if (predicate.invoke()) {
        when (printCantDie) {
            false -> failedToDo(task.invoke())
            true -> when (messageLevel) {
                Level.STACKTRACE -> {
                    try {
                        failedToDo(task.invoke())
                    } catch (e: OverdueError) {
                        e.printStackTrace()
                    }
                }
                else -> {
                    print(msg = "Failed to complete the task with a predicate:\n=> $task", level = messageLevel)
                }
            }
        }
    }
}

private fun failedToDo(task: String): Nothing {
    throw OverdueError("Failed to complete the task with a predicate:\n=> ${task}\n")
}

private fun failedToDo(by: String, task: String): Nothing {
    throw OverdueError("Overdue todo task that was due on '$by':\n=> ${task}\n")
}

private fun print(msg: String, level: Level) {
    when (level) {
        Level.DEBUG -> Logger.debug(msg)
        Level.INFO -> Logger.info(msg)
        Level.WARN -> Logger.warn(msg)
        else -> Logger.error(msg)
    }
}
