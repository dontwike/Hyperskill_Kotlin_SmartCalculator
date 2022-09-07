package calculator

import kotlin.system.exitProcess

object Calculator {

    fun start() {
        do {
            when (val input = readln().trim()) {
                "/exit" -> break
                "/help" -> showHelp()
                "" -> continue
                else -> {
                    println(input.split(" ").filter { it != "" }.toMutableList().math())
                }
            }
        } while (true)
        println("Bye!")
    }

    private fun showHelp() = println("The program calculates the sum of numbers")

    private fun MutableList<String>.math(): String {
        while (this.size != 1) {
            if (this.size % 2 == 0) throw IllegalArgumentException("Invalid number of inputs")
            val operatorIndex = this.findOperator()
            val left = this[operatorIndex - 1].toIntOrNull()?: throw Exception("${this[operatorIndex - 1]} is not a valid input")
            val right = this[operatorIndex + 1].toIntOrNull()?: throw Exception("${this[operatorIndex + 1]} is not a valid input")
            val operator = this[operatorIndex]

            val result = when (operator) {
                "+" -> left + right
                "-" -> left - right
                else -> throw IllegalArgumentException("found invalid operator")
            }

            this[operatorIndex] = result.toString()
            this.removeAt(operatorIndex + 1)
            this.removeAt(operatorIndex - 1)
        }
        return this[0]
    }

    private fun MutableList<String>.findOperator(): Int {
        if (this[1].contains("+") || this[1].contains("-")) {this.simplifyOperator(1)}
        return 1
    }

    private fun MutableList<String>.simplifyOperator(pos: Int) {
        var workingString = this[pos]
        while (workingString.length != 1) {
            if (workingString[0] != '+' && workingString[0] != '-') throw IllegalArgumentException("Invalid operator: ${this[pos]}")
            workingString = workingString.simplified()
        }
        this[pos] = workingString
    }
    private fun String.simplified(): String = this.replace("--", "+").replace("++", "+")
        .replace("+-", "-").replace("-+", "-")
}

fun main() {
    try {
        Calculator.start()
    } catch (e: RuntimeException) {
        println(e.message)
        exitProcess(1)
    }
}
