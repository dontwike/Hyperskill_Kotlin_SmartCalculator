package calculator

import kotlin.system.exitProcess

object Calculator {

    fun start() {
        do {
            when (val input = readln().trim()) {
                "/exit" -> break
                "/help" -> showHelp()
                "" -> continue
                else -> processInput(input)
            }
        } while (true)
        println("Bye!")
    }

    private fun showHelp() = println("The program calculates the sum of numbers")

    private fun processInput(input: String) {
        val operands = input.split(" ").map {it.toIntOrNull()?: throw IllegalArgumentException("\"$it\" is not an integer.")}
        add(operands)
    }

    private fun add(operands: List<Int>) {
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
