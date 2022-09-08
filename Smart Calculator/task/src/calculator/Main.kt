package calculator

class CalculatorException: RuntimeException{
    constructor() : super()
    constructor(message: String?) : super(message)
}
object Calculator {
    private val operatorRegex = """[+-]+""".toRegex()
    private val identifierRegex = """[a-zA-Z]+""".toRegex()
    private val numberRegex = """[-+]?\d+""".toRegex()
    private val operandRegex = """($numberRegex|$identifierRegex)""".toRegex()
    private val expressionRegex = """$operandRegex( *$operatorRegex *$operandRegex)*""".toRegex()
    private val assignmentRegex = """$identifierRegex *= *$operandRegex""".toRegex()
    private val assignmentLeftRegex = """$identifierRegex *=.*""".toRegex()
    private val commandRegex = """/(exit|help)| """.toRegex()
    private val variables = mutableMapOf<String, String>()

    fun run() {
        do {
            val input = readln().trim()
            try {
                if (input == "") continue //it might be possible to replace this with when(?)
                if (input.startsWith('/')) {
                    if (input.matches(commandRegex)) runCommand(input)
                    else throw CalculatorException("Unknown Command")
                    continue
                }
                if (input.contains("=")) {
                    if (!assignmentLeftRegex.matches(input)) throw CalculatorException("Invalid identifier")
                    if (!assignmentRegex.matches(input)) throw CalculatorException("Invalid assignment")
                    mapIdentifier(input)
                    continue
                }
                if (input.matches(expressionRegex)) input.math()
                else throw CalculatorException("Invalid identifier")
            } catch (e: CalculatorException) {
                println(e.message)
            }
        } while (input != "/exit")
    }

    private fun runCommand(command: String) {
        when (command) {
            "/help" -> showHelp()
            "/exit" -> exit()
        }
    }
    private fun showHelp() = println("The program calculates the sum of numbers")
    private fun exit() = println("Bye!")

    private fun mapIdentifier(assignment: String) {
        if (!assignment.matches(assignmentRegex)) throw RuntimeException("entered mapIdentifier illegally")
        val (identifierKey, identifierValue) = assignment.split("=").map { it.trim() }
        if (identifierRegex.matches(identifierValue) && !variables.containsKey(identifierValue)) println("Unknown variable")
        else variables[identifierKey] = identifierValue
    }

    private fun String.math() {
        var returnString: String = this
        while (!operandRegex.matches(returnString)) {
            val subExpression = returnString.expressionMatch()
            val (o1, op, o2) = subExpression.split(" ").map {it.trim().varToNum()}
            val result = when (op.operatorTrim()) {
                "+" -> (o1.toInt() + o2.toInt()).toString()
                "-" -> (o1.toInt() - o2.toInt()).toString()
                else -> throw CalculatorException("Attempted to perform invalid operator function")
            }
            returnString = returnString.replace(subExpression, result)
        }
        returnString = returnString.varToNum()
        println(returnString.toInt())
    }
    private fun String.varToNum(): String {
        var returnOperand = this
        if (operandRegex.matches(this)) {
            while (!numberRegex.matches(returnOperand)) {
                if (!variables.containsKey(this)) {
                    throw CalculatorException("Unknown variable")
                }
                returnOperand = variables[returnOperand]!!
            }
        }
        return returnOperand
    }
    private fun String.expressionMatch(): String { //I will have to modify this to find priority for multiplication/division soon
        val regPat = """$operandRegex *$operatorRegex *$operandRegex""".toRegex()
        return regPat.find(this)?.value?: throw CalculatorException("Didn't find a matchResult")
    }
    private fun String.operatorTrim(): String {
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
    Calculator.run()
}
