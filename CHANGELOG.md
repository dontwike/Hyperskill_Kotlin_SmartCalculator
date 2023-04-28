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

### Stage 6/8 Variables

Cool. Cool. Cool.
okay so back in Stage 3 I think? at some point the description said "you code should not throw Exceptions" 
Anyway, I totally misunderstood that as not using Exceptions at all, even for control flow purposes. But no. Actually. That's silly. I can totally do that.
Which is why things went from throwing relevant Exceptions to `println([exception text here])` which, really messed with control flow.

Also wow, Regex continues to cause me problems, but I'm getting a little more comfortable with it.

Oh yeah, the changes:
- You can now assign and sum variables (variables can only contain letters)
- Back to catching thrown exceptions for control flow oh my goodness it's so much easier this way.
- Renamed a few functions to make them clearer
- Got rid of the line of code that was bothering me when I realized `.toInt()` clears the extraneous `+`

### Stage 7/8 I've got the power

We did it!
We added multiplication! And operator preference!
And we did it in a way I kinda totally didn't expect, so the "planning ahead" bit kinda didn't work but that's OK.
So now, when we do maths, we instead convert the input to a LinkedList/Deque based on reverse polish notation RPN. Then pop items from the front of the Deque onto a stack. if the evaluated object is an operator, pop two entries off the working stack and perform the associated function for the evaluated object.
Then print out the result when the Deque is empty. We always decode what we pop off the stack in the event someone enters `n + 1` then `n`. So that it gives `1` instead of just repeating `n` back at the user.
That's actually something to think about/plan for. "when do we solve/decode a variable into it's number-value"
I'm aiming for as late as possible because at some point in the future I think it'd be cool to allow you to assign expressions to variables. Like. 
```
a = 3
b = 4
c = a + b // c = 7
a = 2 // Now, c equals 6
```
I think that's called lazy evaluation? Like with sequences, how it doesn't calculate it until necessary.
To do that though I'd need to add something for expression evaluation. Since the regex for it just checks that parens are in the right places, not that there's equal amounts.
Probably just make a function like `.isExpression()` that both checks Regex and that the # of left and right parens is equal. Shouldn't be hard. Plus then I can shave 2 lines of code in the `.toPostfixList()` code.  

I'm rambling though. It was fun! Regex is pain! (I do enjoy it but gosh it's a lot of translating sometimes.)
There was this whole thing where I wanted to take the ` *\(* *` and ` *\)* *` out of the `expressionRegex` and put it in `operandRegex` but that ended up breaking things in `toPostfixList()` so I had to undo it. Oops!
Guess they're right when they say don't fix what ain't broke.

Alright this one's getting kinda long. I'm done. Next stage should be BigInteger. Not too worried about that, but I could be wrong again.

Changes:
- Added support for * / () and ^

### Stage 8/8 Very Big

Haha, ok I was right. This was very easy.
Just changed some `.toInt()` to `.toBigInteger()` and `.toDouble()` to `.toBigDecimal()` with some nipping and tucking for the `^` operator.
Sorta expected it to be like that.
Wow, I'm done!

Changes:
- Added support for larger numbers
- Fleshed out the /help command and added a /variables command to give some information on variables in the calculator

That's a wrap, send it!

### Update v1.1: Expression assignment

So I got a little bored, and I told myself I'd go back and add these things.
- You can assign an expression to a variable, so long as the variables in the expression are already in the variable List.
so like 
```
a = 1
b = 2
c = b^2 + 3*a
```
works. and if you change `b` to be `3` or something, `c` is updated accordingly.

What you CANNOT do is assign an expression that relies on itself. For example:
```
a = 1
b = a
a = b //this gives you an error, since trying to evaluate it would cause a stackoverflow
```

- Added some color to the startup prompt, makes it more colorful, so it's easier to parse.
- Then there's just another command to show the variables list and their associated values.
- Bugfix for redundant parenthesis. `((4))` no longer throws an exception and is instead evaluated properly..

That's all for now. I'm sure I can think of other, more productive improvements to this. But it was nice to get this update out of the way.
