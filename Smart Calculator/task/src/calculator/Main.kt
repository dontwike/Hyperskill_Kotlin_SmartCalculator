package calculator

import java.util.*

class CalculatorException: RuntimeException{
    constructor() : super()
    constructor(message: String?) : super(message)
}
object Calculator {
    private val priority3Regex = """\^""".toRegex()
    private val priority2Regex = """[*/]""".toRegex()
    private val priority1Regex = """[+-]+""".toRegex()
    private val operatorRegex = """($priority3Regex|$priority2Regex|$priority1Regex)""".toRegex()
    private val variableRegex = """[a-zA-Z]+""".toRegex()
    private val numberRegex = """[-+]?\d+""".toRegex()
    private val operandRegex = """($numberRegex|$variableRegex)""".toRegex()
    private val expressionRegex = """\(* *$operandRegex *\)*( *$operatorRegex *\(* *$operandRegex *\)*)*""".toRegex()
    private val assignmentRegex = """$variableRegex *= *(($operandRegex|$expressionRegex))""".toRegex()
    private val assignmentLeftRegex = """$variableRegex *=.*""".toRegex()
    private val commandRegex = """/(commands|list|exit|help|variables)| """.toRegex()
    private val variables = mutableMapOf<String, String>()

    fun run() {
        println("Enter a command, assign a variable, or enter an expression to evaluate. \nYou can type \u001B[0;32m\\commands\u001B[m for the command list, \u001B[0;32m\\help\u001B[0m for more information, or \u001B[0;32m\\exit\u001B[0m to shut down the calculator.")
        do {
            val input = readln().trim()
            try {
                when {
                    (input == "") -> {}
                    input.startsWith('/') -> {
                        if (input.matches(commandRegex)) runCommand(input)
                        else throw CalculatorException("Unknown Command")
                    }
                    input.contains("=") -> {
                        if (!assignmentLeftRegex.matches(input)) throw CalculatorException("Invalid identifier")
                        if (!assignmentRegex.matches(input)) throw CalculatorException("Invalid assignment")
                        mapIdentifier(input)
                    }
                    else -> {
                        if (input.isExpression()) println(input.mathRPN())
                        else throw CalculatorException("Invalid expression")
                    }
                }
            } catch (e: CalculatorException) {
                println(e.message)
            }
        } while (input != "/exit")
    }

    private fun runCommand(command: String) {
        when (command) {
            "/help" -> showHelp()
            "/variables" -> aboutVariables()
            "/list" -> listVariables()
            "/exit" -> exit()
            "/commands" -> commandList()
        }
    }

    private fun commandList() = println("""
    Command List:
    /commands   - Shows all commands available in the command list.
    /help       - Prints the help message.
    /list       - Lists currently assigned variables.
    /variables  - Gives you more information about how the program handles variables
    /exit       - Turns off the program
    """.trimIndent())

    private fun listVariables() {
        variables.forEach{ (key, value) -> println("$key = $value")}
    }
    private fun showHelp() = println("""
        This program does math!
        
        Here are some things it can do:
        
    Assign a variable to a number:
        [variable_name]=[a_number]
        a = 2
        b = 3
        
    Assign a variable to another variable that's already defined:
        [variable_name]=[another_variable_name]
        a = 2 
        b = a 
        now, both a and b are equal to 2
    
    Assign a variable to an expression:
        
        c = 2*a+b^6
        this only works if 'a' and 'b' are already defined.
        
    Re-assign variables.
        Input:
        a = 1 
        b = a 
        a = 3 
        Note, b is now also equal to 3
        You can learn more about this by typing /variables
        
    Evaluate expressions using variables or numbers
        Supported operators: +, -, *, /
        1 + 2 
        output: 3
        3 * 5 
        output: 15
        
    Evaluate more complicated expressions, taking care to follow expression priority:
        2 - 1 * 8 + 6 / 2 
        output: -3
        3 + 8 * ((4 + 3) * 2 + 1) - 6 / (2 + 1) 
        output: 121
        
    That's about everything.
    """.trimIndent())
    private fun aboutVariables() = println("""
        Some notes about variables:
        
        variable names can only contain letters from the latin alphabet
        a2, _myVar, and Th1s are all invalid variables
        
        variables are case sensitive, so if
        a = 1
        but you try to evaluate A + 6
        the output will be unknown variable!
        
        Variables are also evaluated "lazily"
        meaning that if A = 1 and B = A
        when A changes, so does B
        
    """.trimIndent())
    private fun exit() = println("Bye!")

    private fun mapIdentifier(assignment: String) {
        if (!assignment.matches(assignmentRegex)) throw RuntimeException("entered mapIdentifier illegally")
        val (identifierKey, identifierValue) = assignment.split("=").map { it.trim() }
        if (variableRegex.matches(identifierValue) && !variables.containsKey(identifierValue)) throw CalculatorException("Unknown variable")
        if (identifierValue.containsSelfReference(identifierKey)) throw CalculatorException("Cannot assign $identifierKey to $identifierValue because $identifierValue contains $identifierKey")
        variables[identifierKey] = identifierValue
    }
    private fun String.containsSelfReference(assignee: String): Boolean {
        return when {
            this == assignee -> true
            numberRegex.matches(this) -> false
            variableRegex.matches(this) -> variables[this]?.containsSelfReference(assignee) ?: throw CalculatorException("Variable in Expression not in variables List!")
            expressionRegex.matches(this) -> variableRegex.findAll(this).any { it.value.containsSelfReference(assignee) }
            else -> throw CalculatorException("DEBUG: Calculator Exception: $this does not match any valid Regex")
        }
    }

    private fun String.maths(operator: String = "?", second: String = ""): String {
        val a = this.decode().toBigInteger()
        val b = second.decode().toBigInteger()
        val result = when (operator.decode()) {
            "-" -> a - b
            "+" -> a + b
            "*" -> a * b
            "/" -> a / b
            "^" -> (a.toBigDecimal().pow(b.toInt())).toBigInteger()
            "?" -> throw CalculatorException("Did not pass operator to maths function")
            else -> throw CalculatorException("Something unexpected in maths function")
        }
        return result.toString()
    }
    private fun String.toExpressionList(): MutableList<String> {
        var workingString = this.replace(" ", "")
        val workingList = mutableListOf<String>()
        var searchingForOperand = true
        do {
            var match: MatchResult?
            if (searchingForOperand) {
                match = "\\(.*".toRegex().matchEntire(workingString)
                if (match != null) {
                    match = "\\(+".toRegex().find(workingString)!!
                    repeat(match.value.length) {
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
        return workingList
    }
    private fun String.mathRPN(): String {
        val postfix: LinkedList<String> = this.toPostfixList()
        val workingStack: Stack<String> = Stack()
        while (postfix.isNotEmpty()) {
            val inHand = postfix.pop()?: throw CalculatorException("Pop on empty stack")
            if (operandRegex.matches(inHand)) {
                workingStack.push(inHand)
                continue
            }
            if (operatorRegex.matches(inHand)) {
                val secondOperand = workingStack.pop()?: throw CalculatorException("Pop on empty stack")
                val firstOperand = workingStack.pop()?: throw CalculatorException("Pop on empty stack")
                workingStack.push(firstOperand.maths(operator = inHand,second = secondOperand))
                continue
            }
            throw CalculatorException("inHand string neither operator or operand... How'd that get in there?")
        }
        return workingStack.pop().decode()
    }
    private fun String.toPostfixList(): LinkedList<String> {
        val expressionList = this.toExpressionList()
        val tempStack: LinkedList<String> = LinkedList()
        val postfixList = LinkedList<String>()
        for (item in expressionList) {
            when {
                operandRegex.matches(item) -> postfixList.add(item)

                tempStack.isEmpty() -> tempStack.push(item)
                (tempStack.peek() == "(") -> if(item == ")") tempStack.pop() else tempStack.push(item)
                (item.priority() > tempStack.peek().priority()) -> tempStack.push(item)
                (item == "(") -> tempStack.push(item)

                (item.priority() <= tempStack.peek().priority()) -> postfixList.addAll(tempStack.popStack(item))
                (item == ")") -> postfixList.addAll(tempStack.popStack(item))
                else -> {}
            }
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
    private fun String.isExpression(): Boolean {
        if (this.count{it == '('} != this.count{it == ')'}) throw CalculatorException("Invalid # of parenthesis")
        if (!expressionRegex.matches(this)) throw CalculatorException("Invalid identifier")
        return true
    }

    private fun String.decode(): String {
        var returnString = this
        if (operandRegex.matches(this)) {
            while (!numberRegex.matches(returnString)) {
                if (!variables.containsKey(this)) {
                    throw CalculatorException("Unknown variable")
                }
                returnString = variables[returnString]?: throw CalculatorException("Uh-oh! Fun new error")
                if (expressionRegex.matches(returnString)) returnString = returnString.mathRPN()
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
