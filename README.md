
			                ████             ███                            
			             ██████████           ▓████                 
			           ████▒▒▒▒░░████           ░█████                
			        ████▓▒▒░░░░░░  ░████░░░  ░░░░░░▒█████         
			        █████░░░░         █████░░░░▒▒▒▒████           
			          ████░░░           ████░░▒▒▒▒████                    
			            ▒████             █████▒████                      
			               █████             █████                
			                 ███               █

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
NLang allows the extension of the base instruction set by custom libraries. **Even if none are used, an empty library file has to be generated and built.** This can be done with the following commands:

```
./nlang --init
./nlang --build
```

The `--init` argument creates a new empty library and `--build` compiles it. If you want to use custom libraries, simply call:

```
./nlang --get USERNAME REPO_NAME
```

Currently, the only two existing libraries are by **SimeonTheCoder**:
- nlwinplotlib
- nlmath

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

# Syntax
In NLang, Indentation means dependency. Same indentation = parallel execution.

Nodes with instructions to be executed in parallel are defined exclusively in groups, defined by `()`.

Every node contains the following information:
 - parent node
 - child nodes
 - instruction
 - slots (numbered 0-9)

### Keywords
- `AS` - defines where the result of an instruction is saved at. Accepts only slot addresses.
- `REPEAT` - the number of times to repeat the command in a node.
- `FUNC` - defines a function. Groups can be turned into functions with the `func` keyword.

### Data types
 - NUMBER - a 32b signed float value. Both numbers and addresses can be put in `NUMBER` arguments of instructions.
 - STRING - a string value.
 - FUNCTION - the name of a function; used as a reference to that function.
 - ENUM - predefined enums by NLang:
	 - EQUAL
	 - GREATER_THAN
	 - LESS_THAN
	 - GREATER_EQUAL
	 - LESS_EQUAL
	 - NOT_EQUAL
	 - NUMBER
 - MULTIPLE - an undefined number of `NUMBER` arguments.

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
Arg0 can only be a global address. The value of the arg0 register is set to arg1.

- **PRINT (NUMBER)**
Prints a number, without putting a new line at the end.

- **INP**
Reads a number from the console.

- **CALL (FUNCTION)**
Calls a the function under the name of arg0.

- **IF (NUMBER, NUMBER, ENUM, FUNCTION, FUNCTION)**
Compares arg0 and arg1 based on arg2.
Valid values for arg2 are:
	 - EQUAL
	 - GREATER_THAN
	 - LESS_THAN
	 - GREATER_EQUAL
	 - LESS_EQUAL
	 - NOT_EQUAL

If the result of the comparison is `true`, the function arg3 is called, otherwise - arg4.

- **PRINTLN (NUMBER)**
Prints a number, puts a new line at the end.

- **ALLOC (NUMBER)**
Allocates the memory for addresses `g1` to ``g(arg0)`. Use required only when working with dynamic global addresses.

- **WRITE (STRING, NUMBER)**
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
(
	inp as &1
	inp as &2
)
	add . .1
		println .
```

Here, `&1` and `&2` specify the slot number of the parent node. `&1` specifies that the value in slot 0 of the group node should be equal to the result of the execution of the `inp` instruction. `&2` specifies the value for slot 1.

These slot addresses can only be used in a group and with the **`as`** keyword.

On JVM level, NLang's memory is stored in a HashMap<String, Float>, so at every address there is a value and vice versa.

Local addresses are stored in memory in the following way:

`address = 'a' + (10 * nodeId + slotId)`

Nodes are numbered with a unique index (`nodeId`). The numbering is first done by level, then by order of appearance.

### Global addresses

Global addresses start with `%` and end with a number.

There are two types:
 - Static
 - Dynamic

The values, contained in the static global addresses will be referred to from here on out as "registers". `%5` means the value in register `g5`. Every register holds 32-bit signed floats with a default value 0f.

Dynamic global addresses are similar to pointers. If we wrote a program and called the `memdump` command in interpreter mode, we might get a table like this:

```
g1 -> 3
g2 -> 4
g3 -> 0.7
```
If we put the value `2` in `g4` and call `println %%4`, we would get the value of `g2`, a.k.a. 4 as an answer. If `%%4` was =3, the returned value would be `0.7f`.

**When using dynamic global addresses, use the ALLOC instruction to allocate registers**. The command is not needed for applications that utilize only static ones, since they are going to be allocated by the interpreter at program startup anyway.
