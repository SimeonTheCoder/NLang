```
                                                                                       
                                                                                       
                                                                                       
                                                                                       
        ██                                                                             
       ████          █                                                                 
      ██████      █████                                                                
     ████████     ██████      ███                                                      
    ███████████    ██████     ███                                                      
   ██████ ██████    ██████    ███          ███████     ███ █████      █████████        
  █████    ██████     █████   ███         ██    ███    █████  ███    ███   ████        
  ██████    ██████  ██████    ███               ███    ███    ███   ███     ███        
   ██████    ████████████     ███          ████████    ███    ███   ███     ███        
     █████    ██████████      ███        ████   ███    ███    ███   ███     ███        
      █████    ████████       ███        ████  ████    ███    ███    ██████████        
       ██        █████        ███████████  █████ ████  ███    ███      ████ ███        
                  ███                                                       ███        
                   █                                                 █████████         
                                                                                       
                                                                                       
                                                                                       
                                                                                       
                                                                                
```
# NLang

A node based functional programming language, running on the JVM. Functions are the core of NLang with variables existing only as registers.

Instructions headers and enumeration values are **case insensitive**.

# Commands
### Installation
To install Nlang, open powershell and navigate to the location where you want to create a project. Then, run the following commands:

```
curl -o download.bat https://raw.githubusercontent.com/SimeonTheCoder/NLang/main/build/download.bat
./download
```

### Building custom libraries
NLang allows the extension of the base instruction set by custom libraries.
If you want to use custom libraries, simply call:

```
./npack --get USERNAME REPO_NAME
```

**NPack** (NLang's library manager) pulls the library from the list in **NDepo**.

Currently, these are the only existing libraries:
- nlwinplotlib
- nlmath
- nlautodiff
- nlgraph
- nlequstr

### Running
NLang can execute a program or act as a line-by-line interpreter.

To run NLang in interpreter mode, simply run

```
./nlang
```

In order to execute a specific file (for example PROGRAM.nlp):

```
./nlang PROGRAM
```

Programs can also be executed from interpreter way in the following way:

```
./nlang

>> exec PROGRAM
```

NLang uses a specific memory division of 2048 floats total -896 for local memory, 128 for global and 1024 for array memory

In order to change this division, the following arguments can be used:

- `-NX`, For example `-N5`. NLang will define 5 slots per node.
- `-LX`, For example `-L256`. NLang will use 256 local floats. The maximum number of nodes possible are equal to the local memory amount, divided by the number of specified slots per node
- `-GX`, For example `-G512`. NLang will now be able to support up to 512 registers global memory.
- `-AX`, For example `-A1024`. NLang will reserve 1024 float for array memory.

Specifying any other argument will instead put it in order into the global memory  so that `./nlang test 1 2 3` will put values `1`, `2` and `3` in global registers `%1`, `%2` and `%3`.

# Syntax
In NLang, the pipe symbol `|` means that the lines of code ending with it will get executed in parallel.

Nodes with instructions to be executed in parallel are defined exclusively in groups, defined by `{}`.

Every node contains the following information:
 - parent node
 - child nodes
 - instruction
 - slots (numbered 0-N, depending on the specified number of memory slots per node. *By default - 2*)

### Keywords
- `AS` - defines where the result of an instruction is saved at. Accepts only slot addresses.
- `REPEAT` - the number of times to repeat the command in a node.
- `FUNC` - defines a function. Groups can be turned into functions with the `func` keyword.

### Data types
 - NUMBER - a 32b signed float value. Both numbers and addresses can be put in `NUMBER` arguments of instructions.
 - STRING - a string value.
 - FUNCTION - the name of a function; used as a reference to that function. Can also be a node index, starting with `@`. Example: `@2` will be interpreted as a "jump to node with index 2"
 - ENUM - predefined enums by NLang:
   - EQUAL
   - GREATER_THAN
   - LESS_THAN
   - GREATER_EQUAL
   - LESS_EQUAL
   - NOT_EQUAL
   - NUMBER
 - MULTIPLE - an undefined number of `NUMBER` arguments.

# Shorthand

NLang uses a transpiler to convert user-friendly code into one understood by the parser. Many instructions and operations have shorthand:

### IO Operations

- the `INP` instruction can also be written simply as `>`
- the `PRINT` and `PRINTLN` instructions can be written as `<` and `<ln`

### Math

Arithmetic operations can also be written in an easier way:

- `2 + 3` instead of `ADD 2 3`
- `2 - 3` instead of `SUB 2 3`
- `2 * 3` instead of `MUL 2 3`
- `2 / 3` instead of `DIV 2 4`

### Set

The `SET` instruction can also be written in many different ways. Instead of writing `SET A 3`, we can write it as:

- `a = 3`
- `a << 3`
- `3 >> a`

### Defining variables

Instead of having to `ALIAS` variables by hand and risk conflicting global registers, variables can be defined like this:

```
//Single variable, alias of %1
def a

//Multiple variables, aliases of %2, %3 and %4
def b c d
```

### Function calls

`CALL func` can be simplified down to `func()`

### Line breaks

`c = ; a / b` will get converted by the transpiler to:
```
div a b
set c .
```

Putting a `;` at the middle of the line causes the instructions after it to get executed first and it itself is replaced by `.` - the result of the previous instruction.

Multiple can be stacked for more advanced operation:

`a = ; 5 * ; b + c`

Starting a line with `:` will cause the transpiler to append it to the previous line, allowing for more fluid syntax:

```
if (a < b)
	: func_a
	: func_b
```

Will get converted to:

```
if a < b func_a func_b
```

### Conditionals

- `>` instead of `greater_than`
- `<` instead of `less_than`
- `>=` instead of `grater_equal`
- `<=` instead of `less_equal`
- `==` instead of `equal`
- `!=` instead of `not_equal`

# Instructions
- **ADD (NUMBER, NUMBER)**
Calculates the sum `arg0 + arg1`.

- **SUB (NUMBER, NUMBER)**
Calculates the difference `arg0 - arg1`.

- **MUL (NUMBER, NUMBER)**
Calculates the product `arg0 * arg1`

- **DIV (NUMBER, NUMBER)**
Calculates `arg0 / arg1`

- **SET (NUMBER, NUMBER)**
The value of the `arg0` register is set to `arg1`.

- **PRINT (NUMBER)**
Prints a number, without putting a new line at the end.

- **INP ()**
Reads a number from the console.

- **INPARR (STRING)**
Reads an array from the console, separated by spaces. Writes the values to a new array with name `arg0`.

- **CALL (FUNCTION)**
Calls a the function under the name of `arg0`. If a node addres (Example: `@2`) will jump to that node and execute it as if it's a function.

- **IF (NUMBER, NUMBER, ENUM, FUNCTION, FUNCTION)**
Compares `arg0` and `arg1` based on `arg2`.
Valid values for `arg2` are:
	 - EQUAL
	 - GREATER_THAN
	 - LESS_THAN
	 - GREATER_EQUAL
	 - LESS_EQUAL
	 - NOT_EQUAL

If the result of the comparison is `true`, the function `arg3` is called, otherwise - `arg4`.

- **PRINTLN (NUMBER)**
Prints a number, puts a new line at the end.

- **PRINTSTR (STRING)**
  Prints a string.

- **WRITE (STRING, NUMBER)**
Appends arg1 to a file with filename arg0. Does nothing unless a file was made first using `MKFILE` and closed with `CLOSE` after the writing is finished.

- **WRITESTR (STRING, STRING)**
Appends arg1 to a file with filename arg0. Does nothing unless a file was made first using `MKFILE` and closed with `CLOSE` after the writing is finished.

- **MKFILE (STRING)**
Creates a file with a filename arg0.

- **CLOSE (STRING)**
Closes a file for writing and reading. Cannot do that unless a file was opened / made beforehand.

- **OPEN (STRING)**
Opens a file with filename arg0 for reading.

- **READLINE (STRING, ENUM)**
Reads a single line from an already opened file with filename arg0. Arg1 specifies what value should be read. The only valid value for arg1 is `NUMBER`.

- **IMPORT (STRING, STRING, MULTIPLE)**
Calls the function with name arg1 from NLang file with `.nlp` extension and name arg0. The arguments passed down are treated as registers by the called function. The returned output by the `IMPORT` instruction is the value at `g1` in the function's memory.

- **ARR (STRING, LENGTH)**
Defines an array with name `arg0` and length `arg1`.

- **AT (STRING, NUMBER)**
Returns the element at `arg1` in the array with name `arg0`.

- **INDEX (STRING, NUMBER)**
Returns the exact address of the element with index `arg1` in array `arg0`. Used for modifying the values in the array.

- **LEN (STRING)**
Returns the length of the array with name `arg0`.

- **DELARR (STRING)**
Deletes the array with name `arg0`.

- **SETSTR (NUMBER, STRING)**
Sets the string at index `arg0` in the string table to `arg1`.

- **EQUSTR (STRING, STRING)**
Compares `arg0` and `arg1` and returns `1` when they're equal and `0` if not.

- **STRCON (STRING, STRING, NUMBER)**
Concatenates `arg0` and `arg1` and puts the result to the string table at index `arg2`

- **RAND (NUMBER, NUMBER)**
Generates a random number between the two bounds

- **EXIT ()**
Exits the program

- **NULL ()**
Does nothing. Can be used as graph storage.

# Addresses
### Local addresses:
Start with `.` (1 or multiple) and end with a number (slot id). The number of `.` characters specify the level of the parent node that is being referred to.

Take this example:
```
add 2 3
mul . 2
add . ..
```
If we write the code as a tree, we get this:
```
add 2 3
  |- mul . 2
     |- add . ..
```
The parent of `mul . 2` is `add 2 3`  and `mul . 2` is the parent of `add . ..`
In the instruction `mul . 2`, `.` means one level above, a.k.a. the parent node (`add 2 3`). In the case of `add . ..`, `..` refers to its parent parent, so `add 2 3`

If no number follows the `.` character, the local address refers to slot 0. Then, `.1` refers to slot 1 and so on... 

Let's take a look at another example:

```
{
	inp as &1
	inp as &2
}
	add . .1
	println .
```

Here, `&1` and `&2` specify the slot number of the parent node. `&1` specifies that the value in slot 0 of the group node should be equal to the result of the execution of the `inp` instruction. `&2` specifies the value for slot 1.

These slot addresses can only be used in a group and with the **`as`** keyword.

On JVM level, NLang's memory is stored in a HashMap<String, Float>, so at every address there is a value and vice versa.

Local addresses are stored in memory in the following way:

`address = SLOTS_PER_NODE * nodeId + slotId`

Nodes are numbered with a unique index (`nodeId`). The numbering is first done by level, then by order of appearance.

### Global addresses

Global addresses start with `%` and end with a number.

There are two types:
 - Static
 - Dynamic

The values, contained in the static global addresses will be referred to from here on out as "registers". `%5` means the value in register `g5`. Every register holds 32-bit signed floats with a default value 0f.

Dynamic global addresses are similar to pointers. If we wrote a program and called the `memdump` command in interpreter mode, we might get a table like this:

```
897 (%1) -> 3
898 (%2) -> 4
899 (%3) -> 0.7
```
If we put the value `2` in `g4` and call `println %%4`, we would get the value of `g2`, a.k.a. 4 as an answer. If `%%4` was =3, the returned value would be `0.7f`.

### Exact addresses

Exact addresses start with `$` and specify an exact address in memory. They give the most control, but require care as they can reach in both local, global and array memory.

```
//Local node with index 3 for 2 slots, slot 1:
set $9 10

//Global register %3
set $900 8

//Array index 0
set $1024 -3
```

### String addresses

Strings are stored in a separate table and each have their own indecies. A string address starts with `#`.

`#0` -> String at index 0 in the string table
`#2` -> String at index 2 in the string table

Some commands expect a string address by default, where `#` shouldn't be written.