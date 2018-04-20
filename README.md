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
java -jar spikeb.jar
```

Run the test cases:

```bash
make test
```

## Remarks

This code is `mostly` working up through ISEQ 4. I say mostly because I have only tested a very limited number of valid test cases, and multidimensional array indexing is undefined.


## Specification Issues

The memory layout of multidimensional arrays is currently unclear, so that makes indexing multidimensional arrays difficult. Currently, this code
has undefined behavior when it comes to multi array indexing

## Known Bugs

1. Asignment ('=') Error: *FIXED*

    Currently, the code does not check if the left side of the '=' operator is assignable. This means constants can be 'assigned' values without error.
    for example:

    ```
    1 = 3;
    ```

    Would be valid in the given the implementation of the grammar and type checking.

2. NullPointerException

    This code has gotten excesive in the use of null values. The code attempts to check for null values whenever it is used, but due to the wide spread use, it is likely there is
    a null test that has been missed. While there is not currently any null pointer exceptions for the given test cases, there is `most likely` a test case that will end up with
    a null pointer exception. Due to the limited amount of time and test cases, this bug has not been found yet.

