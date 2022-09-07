package calculator

import kotlin.system.exitProcess

object Calculator {

    fun add() {
        val inputArray = readln().trim().split(" ").map {it.toIntOrNull()?: throw IllegalArgumentException("\"$it\" is not an integer.")}
        val (firstInt, secondInt) = if (inputArray.size == 2) inputArray else throw IllegalArgumentException("add() was passed the wrong number of integers.")
        println(firstInt + secondInt)
    }
}

fun main() {
    try {
        Calculator.add()
    } catch (e: RuntimeException) {
        println(e.message)
        exitProcess(1)
    }
}
