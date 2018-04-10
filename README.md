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
java -jar spike5.jar
```

Run the test cases:

```bash
make test
```


## Specification Issues

Since keywords 'true' and 'false' are not used, standard C coneventions for booleans are used whenever a bool is expected
(e.g. 0 is false, not 0 is true). This includes the cond expression. For example, the following cond expr will be constant folded:

```
a = 1 ? 2 : 3;
```

to

```
a = 2;
```

The result of a bool expr will also be saved as a 0 or 1. For example:

```
bool a;
a = 5==5;
```

folds to:

```
a = 1;
```

and

```
bool a;
a = 5==6;
```

folds to:

```
a = 0;
```


## Known Bugs

Asignment ('=') Error:

Currently, the code does not check if the left side of the '=' operator is assignable. This means constants can be 'assigned' values without error.
for example:

```
1 = 3;
```

Would be valid in the given the implementation of the grammar and type checking.
