tlc - Test Lobo-C

Quick start:

(1) Untar in clean directory, cd there
(2) Try './tlc -help' for 'short' help, './tlc -man' for more than you want
(3) Try './tlc -list' to see info on the supplied test files
(4) Try './tlc -c LOBOC_COMPILER_PATH -test' to run the supplied tests,
    e.g., './tlc -c ../mycode/spike5b -test' or whatever.  Exit status
    $? means all tests passed.

EXAMPLE:
    code$ mkdir tlctest
    code$ mv tlc-v0.5.tar tlctest/
    code$ pushd tlctest/
    code/tlctest$ tar xvf tlc-v0.5.tar
    tlc
    loboc0.s
    README.txt
    Makefile
    is1_10.loboc
    is1_11.loboc
    is1_12.loboc
    code/tlctest$ ./tlc -list
    ./is1_10.loboc:1: [run:9 cases]
    ./is1_11.loboc:1: [run:8 cases]
    ./is1_12.loboc:1: [run:11 cases]
    >>>Tests = 28 cases in 3 tests
    code/tlctest$ ./tlc -c ../spike5b -test
    >>>Tests 3 = 3. RUN[Correct = 28, Incorrect = 0]
    code/tlctest$ echo $?
    0
    code/tlctest$ popd
    code$

NOTE: This code is young.  Don't necessarily believe everything it
claims in its help.  Stick to simple use cases.
