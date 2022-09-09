package calculator

import java.util.*
import kotlin.math.pow

class CalculatorException: RuntimeException{
    constructor() : super()
    constructor(message: String?) : super(message)
}
object Calculator {
    private val priority3Regex = """\^""".toRegex()
    private val priority2Regex = """[*/]""".toRegex()
    private val priority1Regex = """[+-]+""".toRegex()
    private val operatorRegex = """($priority3Regex|$priority2Regex|$priority1Regex)""".toRegex()
    private val identifierRegex = """[a-zA-Z]+""".toRegex()
    private val numberRegex = """[-+]?\d+""".toRegex()
    private val operandRegex = """($numberRegex|$identifierRegex)""".toRegex()
    private val expressionRegex = """\(* *$operandRegex *\)*( *$operatorRegex *\(* *$operandRegex *\)*)*""".toRegex()
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
                if (input.matches(expressionRegex)) input.mathRPN()
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

    private fun String.maths(operator: String = "?", second: String = ""): String {
        val result = when (operator.decode()) {
            "-" -> this.decode().toInt() - second.decode().toInt()
            "+" -> this.decode().toInt() + second.decode().toInt()
            "*" -> this.decode().toInt() * second.decode().toInt()
            "/" -> this.decode().toInt() / second.decode().toInt()
            "^" -> (this.decode().toDouble().pow(second.decode().toDouble())).toInt()
            "?" -> throw CalculatorException("Did not pass operator to maths function")
            else -> throw CalculatorException("Something unexpected in maths function")
        }
        return result.toString()
    }
    private fun String.toExpressionList(): MutableList<String> {
        var workingString = this.replace(" ", "")
        val workingList = mutableListOf<String>()
        var leftParens = 0
        var rightParens = 0
        var searchingForOperand = true
        do {
            var match: MatchResult?
            if (searchingForOperand) {
                match = "\\(.*".toRegex().matchEntire(workingString)
                if (match != null) {
                    match = "\\(+".toRegex().find(workingString)!!
                    repeat(match.value.length) {
                        leftParens ++
                        workingString = workingString.substring(1)
                        workingList.add("(")
                    }
                }

                if (!"$operandRegex.*".toRegex().matches(workingString)) throw CalculatorException("invalid exp")
                match = operandRegex.find(workingString) ?: throw CalculatorException("invalid exp")
                workingList.add(match.value)
                workingString = workingString.substring(match.value.length)

                match = "\\).*".toRegex().matchEntire(workingString)
                if (match != null) {
                    match = "\\)+".toRegex().find(workingString)!!
                    repeat(match.value.length) {
                        rightParens ++
                        workingString = workingString.substring(1)
                        workingList.add(")")
                    }
                }
                searchingForOperand = false
                continue
            }
            else {
                if ("\\(.*".toRegex().matches(workingString)) throw CalculatorException("invalid exp")
                if (!"$operatorRegex.*".toRegex().matches(workingString)) throw CalculatorException("invalid exp")
                match = operatorRegex.find(workingString) ?: throw CalculatorException("invalid exp")
                workingList.add(match.value)
                workingString = workingString.substring(match.value.length)
                if ("\\).*".toRegex().matches(workingString)) throw CalculatorException("invalid exp")
                searchingForOperand = true
            }
        } while (workingString.isNotEmpty())
        if (searchingForOperand || leftParens != rightParens) throw CalculatorException("invalid exp")
        return workingList
        //go through list trying to find lparen?,operand,rparen?,operator,lparen?,operand,rparen?
        //as this proceeds, keep track somehow of how far in the list we've gotten through
        //maybe remove from the workingString as we go through...
        //when workingString empty or gone though, end loop
        //check that the list is valid by checking left to right paren count
        //return workingList
    }
    private fun String.mathRPN() {
        val postfix: LinkedList<String> = this.toPostfixList()
        val workingStack: Stack<String> = Stack()
        while (postfix.isNotEmpty()) {
            val inHad = postfix.pop()?: throw CalculatorException("Pop on empty stack")
            if (operandRegex.matches(inHad)) {
                workingStack.push(inHad)
                continue
            }
            if (operatorRegex.matches(inHad)) {
                val secondOperand = workingStack.pop()?: throw CalculatorException("Pop on empty stack")
                val firstOperand = workingStack.pop()?: throw CalculatorException("Pop on empty stack")
                workingStack.push(firstOperand.maths(operator = inHad,second = secondOperand))
                continue
            }
            throw CalculatorException("inHand string neither operator or operand... How'd that get in there?")
            //check first of postfix (.peek()?
            //if operand, add to workingStack
            //if operator, DoMath with it and the top 2 of the stack
            //if neither, throw CalculatorException("Problem in mathRPN")
        }
        println(workingStack.pop().decode())
    }
    private fun String.toPostfixList(): LinkedList<String> {
        val expressionList = this.toExpressionList()
        val tempStack: LinkedList<String> = LinkedList()
        val postfixList = LinkedList<String>()
        for (item in expressionList) {
            when (true) {
                operandRegex.matches(item) -> postfixList.add(item)

                tempStack.isEmpty(),
                (tempStack.peek() == "("),
                (item.priority() > tempStack.peek().priority()),
                (item == "(") -> tempStack.push(item)

                (item.priority() <= tempStack.peek().priority()),
                (item == ")") -> postfixList.addAll(tempStack.popStack(item))
                else -> {}
            } //this statement is cluttered, some checks are redundant due to how the .priority function works. But cleaning it is less important than making sure the calculator works.
        }
        postfixList.addAll(tempStack.popStack())
        return postfixList
    }
    private fun LinkedList<String>.popStack(compareVal: String = ""): Queue<String> {
        val toReturn: Queue<String> = LinkedList()
        while (this.isNotEmpty()) {
            if (this.peek().priority() >= compareVal.priority()) {
                toReturn.add(this.pop())
            }
            else if (this.peek() == "(") {
                if (compareVal == ")") {
                    this.pop()
                }
                break
            }
            else break
        } //TODO this can get cleaned up a little
        if (compareVal != "" && compareVal != ")") this.push(compareVal)
        return toReturn
    }
    private fun String.priority(): Int {
        return when (true) {
            "\\(".toRegex().matches(this) -> -2
            "\\)".toRegex().matches(this) -> -1
            priority3Regex.matches(this) -> 3
            priority2Regex.matches(this) -> 2
            priority1Regex.matches(this) -> 1
            else -> 0
        }
    }
    private fun String.expressionMatch(): String {

        val regPat = """$operandRegex *$operatorRegex *$operandRegex""".toRegex()
        return regPat.find(this)?.value?: throw CalculatorException("Didn't find a matchResult")
    }
    private fun String.decode(): String {
        var returnString = this
        if (operandRegex.matches(this)) {
            while (!numberRegex.matches(returnString)) {
                if (!variables.containsKey(this)) {
                    throw CalculatorException("Unknown variable")
                }
                returnString = variables[returnString]!!
            }
        }
        if (priority1Regex.matches(returnString)) {
            while (!"[-+]".toRegex().matches(returnString)) {
                returnString = returnString.replace("--", "+")
                    .replace("++", "+")
                    .replace("+-", "-")
                    .replace("-+", "-")
            }
        }
        return returnString
    } //Turns variables into numbers and removes extra + or -
}

fun main() {
    Calculator.run()
}
