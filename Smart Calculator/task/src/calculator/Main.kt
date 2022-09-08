package calculator

import kotlin.system.exitProcess

object Calculator {
    private val operator = """[+-]+""".toRegex()
    private val operand = """[-+]?\d+""".toRegex()
    private val expressionRegex = """$operand( $operator $operand)*""".toRegex()
    private val commandList = """/(exit|help)| """.toRegex()

    fun start() {
        do {
            val input = readln().trim()
            if (input == "") continue //it might be possible to replace this with when(?)
            if (input.startsWith('/')) {
                if (input.matches(commandList)) runCommand(input)
                else println("Unknown Command")
                continue
            }
            if (input.matches(expressionRegex)) input.math()
            else println("Invalid expression")
        } while (true)
    }

    private fun runCommand(command: String) {
        when (command) {
            "/help" -> showHelp()
            "/exit" -> exit()
        }
    }

    private fun exit() {
        println("Bye!")
        exitProcess(0)
    }

    private fun showHelp() = println("The program calculates the sum of numbers")

    private fun String.math() {
        var returnString = this
        while (!operand.matches(returnString)) {
            val subExpression = returnString.expressionMatch()
            val (o1, op, o2) = subExpression.split(" ")
            val result = when (op.simplified()) {
                "+" -> (o1.toInt() + o2.toInt()).toString()
                "-" -> (o1.toInt() - o2.toInt()).toString()
                else -> throw RuntimeException("Attempted to perform invalid operator function")
            }
            returnString = returnString.replace(subExpression, result)
        }
        returnString = returnString.replace("+", "") //I REALLY don't like this line of code. Please fold it into smth else.
        println(returnString)
    }

    private fun String.expressionMatch(): String { //I will have to modify this to find priority for multiplication/division soon
        val regPat = """$operand $operator $operand""".toRegex()
        return regPat.find(this)?.value?: "Didn't find a matchResult"
    }
    private fun String.simplified(): String {
        var returnString = this
        while (!returnString.matches("[-+]".toRegex())) {
                returnString = returnString.replace("--", "+")
                    .replace("++", "+")
                    .replace("+-", "-")
                    .replace("-+", "-")
        }
        return returnString
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
