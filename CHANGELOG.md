# Hyperskill_Kotlin_SmartCalculator

I decided to move the blog-post style ramblings into this CHANGELOG, on recommendation of a friend. Thanks Loren!

### Stage 1/8: 2+2

The first stage of the course had lots of lessons listed as prerequisites in order to complete the first stage. From "Kotlin basics" like var declaration and working with strings to things like logging, memory allocation and self-documenting code.

The task of the first stage is to take an input of 2 integers on a single line, and print their sum. That's a pretty straightforward task! So, I decided to plan ahead, make the calculator singleton and add error handling.... just in case.

It's probably "too much code" for right now, but it might save me pain in the future, who knows.

### Stage 2/8: 2+2+

This next stage adds a basic control loop and other simple boxes to check like:
  -make the program continue running until '\exit' is enterred
  -allow empty lines & single integer inputs

I tried to plan ahead by breaking everything into smaller tasks. I'm sure soon i'll need subtract, multiply, and divide functions.
I also made sum() allow for multiple operands (though there's still a check for only 2 operands imposed by the project)

I figure I could have writted `println(operands.sumOf { it })` 
instead of ```
val sum = operands.sumOf { it }
println(sum)```
But I figure it's easier to parse this way.

. o O ( I wonder if there's a better way to add a list of numbers than `List<Int>.sumOf. {it}`? )
