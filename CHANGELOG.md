# Hyperskill_Kotlin_SmartCalculator

I decided to move the blog-post style ramblings into this CHANGELOG, on recommendation of a friend. Thanks, Loren!

### Stage 1/8: 2+2

The first stage of the course had lots of lessons listed as prerequisites in order to complete the first stage. From "Kotlin basics" like var declaration and working with strings to things like logging, memory allocation and self-documenting code.

The task of the first stage is to take an input of 2 integers on a single line, and print their sum. That's a pretty straightforward task! So, I decided to plan ahead, make the calculator singleton and add error handling.... just in case.

It's probably "too much code" for right now, but it might save me pain in the future, who knows.

### Stage 2/8: 2+2+

This next stage adds a basic control loop and other simple boxes to check like:
  -make the program continue running until '\exit' is entered
  -allow empty lines & single integer inputs

I tried to plan ahead by breaking everything into smaller tasks. I'm sure soon I'll need subtract, multiply, and divide functions.
I also made sum() allow for multiple operands (though there's still a check for only 2 operands imposed by the project)

I figure I could have written `println(operands.sumOf { it })` 
instead of ```
val sum = operands.sumOf { it }
println(sum)```
But I figure it's easier to parse this way.

. o O ( I wonder if there's a better way to add a list of numbers than `List<Int>.sumOf. {it}`?)

### Stage 3/8: Count them allocation

Well this is a good example of the benefits of thinking ahead.
Added a single-line function for a '\help' command and removed the single-line restriction on the add function.

### Stage 4/8: Add subtractions

This gave me more trouble than expecting.
Now that a valid input is no longer space separated integers, but full phrases with operators and operands, I can't resort to MutableList<Int>.sumOf {it}
I needed to make new functions to parse the input string, find the operators, and then perform the associated function.
Eliminating redundant operators was simple enough with String.replace(), but what gave me trouble was the actual function I used to reduce an input string to a result.
Originally I thought it was best to run .math() recursively.
    1. Find a valid operator
    2. Split the input string into left and right MutableList<String>s
    3. Depending on the operator, call the appropriate function on left.math() and right.math(), 
    4. .math() would then call recursively until each list of Strings became a single string.
    
But calling recursively like that doesn't actually follow the rules of... Maths.
So I went back and just used a while loop to iterate over the input list until it's reduced to a single value
I don't like how right now I'm using
```
this[operatorIndex] = result.toString()
this.removeAt(operatorIndex + 1)
this.removeAt(operatorIndex - 1)
```
But it works. More importantly, it should allow for multiplication & division to be added easily in the future.

### Stage 5/8 Error!

Repeating the theme of the last stage, this one gave me more trouble than I expected.
All I needed to do, really. was change the logic around to work  with regex on strings instead of breaking down into charSequences like I had before.
That and change the input logic to detect invalid commands/expressions.

But I wanted to plan ahead! I wanted to make sure that, when it came time to add `*` and `/`, I would be ready.
And, by trying to do to things at once, I made each of those things harder. 
I ended up writing and re-writing my code two or three times and caused lots of unnecessary pain.
That's okay though. I've learned my lesson and will only need to repeat it a few dozen times for it to really sink in ^_^

I'm not totally satisfied with this version but here are the changes
- Removed the ability to detect extra whitespace(though it can be re-added fairly easily)
- Things work with regex/Strings now instead of `MutableList<Char>`
- Added checks for invalid commands/expression
- Opened this changelog in a different editor and found/corrected lots of typos

Todo:
- There's a single line of code specifically to remove the unnecessary `+` when a user includes it in a single-operand expression, and I want so badly to remove it/fold it into another function but doing so would require me to either re-asses what an "operand" is or change something else in the logic and darn it I just want things to work for right now!