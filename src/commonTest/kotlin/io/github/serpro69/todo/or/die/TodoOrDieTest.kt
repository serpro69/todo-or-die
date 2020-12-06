package io.github.serpro69.todo.or.die

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test

class TodoOrDieTest {

    @Test
    fun shouldDieIfTodoIsPastDue() {
        kotlin.test.assertFailsWith(OverdueError::class) {
            TODO("1970-01-01") { "Find The Answer to the Ultimate Question of Life, The Universe, and Everything" }
        }
    }

    @Test
    fun shouldPassIfTodoIsNotDue() {
        TODO("2061-05-14") { "Massively decrease the net amount of entropy of the universe" }
    }

    @Test
    fun shouldPassIfTodoIsToday() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        TODO(today.toString()) { "Reverse the workings of the second law of thermodynamics" }
    }
}