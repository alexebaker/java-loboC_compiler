# Lobo-C Compiler

CS 554 - Final version of the Lobo-C Compiler


## Contact Information

    Author: Alexander Baker
    Email: alexebaker@unm.edu
    Date: 05/11/2018


## Usage

Build the project:

```bash
make jar
```

Run the project:

```bash
java -jar loboc.jar
```

Run the test cases:

```bash
make test
```


## Remarks

This code is `mostly` working up through ISEQ 5. I say mostly because I have only tested a very limited number of valid test cases, and multidimensional array indexing is undefined


## ISEQ 5

This extension adds 3 operators to loboc c:

    - % (mod)
    - ! (factorial)
    - ** (exponent)
    
These changes are backwards compatile with ISEQ 4 and before, so there is no special command to enable the ISEQ 5 extension

There are a couple things to note about these 3 operators. Since Lobo-C does not have floating point numbers, there cannot be a negative number in the exponent.
Factorial (!) supports signed numbers. The result of factorial of a negative number, in other words:

```$xslt
x! = 1 when x <= 0
```

The mod opperator will accept signed parameters, however, due to the MIPS arcitecture, any negative numbers for either operand is undefined. (HP_AppA.pdf, Pg. A-55)

These extentions involved making changes to the grammar and type rules. `spike5-grammar.txt` and `spike5-type-rules.txt` show the complete set of grammar and type rules for ISEQ 5.
Below is show of the diff between spike 5 and spike 4:

grammar:
```bash
>>> diff spike4-grammar.txt spike5-grammar.txt
27c27,28
<    TERM           <- FACTOR | TERM FACTOR_OP FACTOR
---
>    TERM           <- EXPO | TERM FACTOR_OP EXPO
>    EXPO           <- FACTOR | EXPO EXPO_OP FACTOR
36c37,38
<    FACTOR_OP      <- "*" | "/"
---
>    FACTOR_OP      <- "*" | "/" | "%"
>    EXPO_OP        <- "**"
40c42
<    POSTUN_OP      <- "--" | "++"
---
>    POSTUN_OP      <- "--" | "++" | "!"

```

Type rules:
```bash
>>> diff spike4-type-rules.txt spike5-type-rules.txt
84a85,104
> ---
> OP: unary !
> 
>   U! => U
>   S! => U
> 
> ---
> OP: binary **
> 
>   U ** U => U
>   S ** U => S
> 
> ---
> OP: Binary %
> 
>   U % U => U
>   U % S => S
>   S % U => U
>   S % S => S
> 
```


## Test Cases

Test cases for loboc are found in `tlc/cs554loboctests`.

The following are ISEQ 5 specific test cases:

    - tlc/cs554loboctests/is5_spike5b_mod.loboc
    - tlc/cs554loboctests/is5_spike5b_fact.loboc
    - tlc/cs554loboctests/is5_spike5b_expo.loboc


## Specification Issues

The memory layout of multidimensional arrays is currently unclear, so that makes indexing multidimensional arrays difficult. Currently, this code
has undefined behavior when it comes to multidimensional array indexing


## Known Bugs

1. NullPointerException

    This code has gotten excesive in the use of null values. The code attempts to check for null values whenever it is used, but due to the wide spread use, it is likely there is
    a null test that has been missed. While there is not currently any null pointer exceptions for the given test cases, there is `most likely` a test case that will end up with
    a null pointer exception. Due to the limited amount of time and test cases, this bug has not been found yet.

