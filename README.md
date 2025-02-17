#### LexicalErrorHandler.java ####

Keywords Supported

The following keywords are recognized by the lexical analyzer:

Data Types: int, float, double, char, bool, string

Control Structures: if, else, for, while

Function-related: void, return, main

Lexical Errors Detected

The Lexical Error Handler identifies and reports the following types of lexical errors:

1. Illegal or Unrecognized Characters

Example:

@var = 10; //'@' is not a valid character in C++

Output:

Lexical Error: Invalid character '@' at line X

2. Misspelled Keywords

Example:

intr x = 10; //'intr' instead of 'int'

Output:

Lexical Error: misspelled keyword 'intr' at line X

3. Invalid Number Formats

Example 1:

float x = 3..14; //Multiple decimal points

Example 2:

int y = 12abc; //Non-numeric characters in a number

Output:

Lexical Error: Invalid number format at line X

4. Unterminated Strings or Comments

Example 1:

string s = "Hello; //Missing closing quotation

Example 2:

/\* This is an unclosed comment

Output:

Lexical Error: Unclosed string literal at line X
Lexical Error: Unclosed comment at line X

5. Excess or Missing Operators

Example:

x === 10; //'===' is not a valid operator in C++

Output:

Lexical Error: Excess operator '===' at line X

6. Missing Semicolons

Example:

int x = 3 //Missing semicolon

#### LexicalAnalyzer.java ####

Keywords

The following keywords are recognized in the language:

int

if

else

while

for

return

true

false

string

char

float

double

bool

const

switch

case

break

continue

default

void

printf

Operators:

The following operators are recognized:

Arithmetic: +, -, *, /, %, ^

Comparison: =, >, <, !

Logical: &, |

Tokenization Rules:

Identifiers

Must start with a lowercase letter.

Can contain letters, digits, and underscores (_).

Numbers

Integer numbers consist of digits only.

Decimal numbers can contain up to five decimal places.

Strings

Enclosed in double quotes ("...").

Unterminated strings will cause an error.

Comments

Single-line comments start with //.

Multi-line comments start with /* and end with */.

#### Automata ####
Automata classes (NFA, DFA etc) work on the regular expression that is inserted by the user in the main. At first, it generates the NFA transition table, then DFA transition table.

#### Symbol Table ####
Rules: The program extracts symbols (functions, variables, constants) from C code using regular expressions, categorizing them by type (Function, Variable, Constant), scope (Global, Local), and value (if assigned).

Keywords: int, float, double, char, string, void, #define, Global, Local, Function, Variable, Constant.
