package calculator

import kotlin.system.exitProcess

object Calculator {

    fun start() {
        do {
            when (val input = readln().trim()) {
                "/exit" -> break
                "" -> continue
                else -> processInput(input)
            }
        } while (true)
        println("Bye!")
    }

    private fun processInput(input: String) {
        val operands = input.split(" ").map {it.toIntOrNull()?: throw IllegalArgumentException("\"$it\" is not an integer.")}
        add(operands)
    }

    private fun add(operands: List<Int>) {
        if (operands.size > 2) throw IllegalArgumentException("add() was passed the wrong number of integers.")
        val sum = operands.sumOf { it }
        println(sum)
    }
}

fun main() {
    try {
        Calculator.start()
    } catch (e: RuntimeException) {
        println(e.message)
        exitProcess(1)
    }
}
