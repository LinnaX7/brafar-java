# BRAFAR: Bidirectional Refactoring, Alignment, Fault Localization, and Repair for Programming Assignments

---
## What is Brafar?

---
Brafar is a general feedback generation system for introductory programming assignments. Given a buggy program $P_b$, one or more correct programs $C$, and a set of test case $T$. First, we will search for the closest correct program $P_c$ based on tree-edit-distance from the given buggy program before Brafar works to exploit the availability of existing correct solutions to perform better. Then, Brafar will generate feedback for the given buggy program $P_b$ through four steps: (1) Bidirectional Refactoring, (2) Alignment, (3) Fault Localization, and (4) Block Repair.

## Dataset

---
The "data" directory contains 400 correct and 601 incorrect programs from 6 different Java programming assignments offered during a second-year university course credited by students.

Brafar tool expects the following inputs:
1. Test-suite: Junit test for this problem, that contains a set of test suite for this problem.
2. Correct-P: Correct program attempts by students, that passes all the test-cases.
3. Wrong-P: Buggy program attempts by students, which fails on one or more test-cases.

Given these inputs, Brafar tries to repair all buggy programs by inferring input-output specification from the closest aligned (refactored) correct programs. These data input files should be organized in the folder structure described below. Please refer to the 6 programming assignments present within "data" directory for example.

```
|-data
    |-Problemxx
    |    |-test
    |    |   |-XXXTest.java
    |    |   
    |    |-correct
    |    |   |-001
    |    |   |   |-src
    |    |   |   |   |-xxx.java
    |    |   |
    |    |   |-002
    |    |   |   |-src
    |    |   |   |   |-xxx.java
    |    |   |    
    |    |   |-...
    |    |   |
    |    | 
    |    |-wrong
    |    |   |-001
    |    |   |   |-src
    |    |   |   |   |-xxx.java
    |    |   |
    |    |   |-002
    |    |   |   |-src
    |    |   |   |   |-xxx.java
    |    |   | 
    |    |   |-...
    |    |   |
    |-...
```

## Setup

---
###  Environment
* Java 11
* MacOS/Linux

### Build
* Build the project in the ```brafar-system.jar```.
  (We have already built Brafar tool in the ```brafar-system.jar```)

## Running Brafar

---
The Brafar tool takes a given buggy program, a set of correct programs and a set of testsuite as input. 

### Running example

The ```example/``` directory contains a running example:
```
|-example
|    |-test
|    |   |-SpecialNumberTest.java
|    |   
|    |-correct
|    |   |-001
|    |   |   |-src
|    |   |   |   |-SpecialNumber.java
|    |   |-002
|    |   |   |-src
|    |   |   |   |-SpecialNumber.java
|    |   |-...
|    |   |   |-src
|    |   |   |   |-SpecialNumber.java
|    |   |
|    |-...
|    |
|    |-wrong
|    |   |-src
|    |   |   |-SpecialNumber.java
|    |   |
|    |
```

* The below command runs Brafar on the example code. 
```shell
java -jar brafar-system.jar --BuggyProgramSourceDir ./example/wrong/src --CorrectProgramsSourceDir ./example/correct --MethodToFix isSpecial@SpecialNumber --ProgramTestClass SpecialNumberTest --ProgramTestSourceDir ./example/test
```
* Work Dir
```
brafar_system
```

### Command line arguments
* ```--BuggyProgramSourceDir <BuggyProgramSourceDir>```         Path to the buggy
program's source
directory.
* ```--CorrectProgramsSourceDir <CorrectProgramSourceDir>```     Path to the
correct programs'
source directory.
* ```--MethodToFix <MethodToFix>```                            Method to fix.
Format:
MethodName@Package
Name.ClassName .
* ```--ProgramTestClass <ProgramTestClass>```               Class to the
program test.
Format:
PackageName.ClassN
ame .
* ```--ProgramTestSourceDir <ProgramTestSourceDir>```      Path to the
program test
source directory,
separated by
semicolon (;).
* ```--Help```                                              Print the help
  message.

### Output
S_Brafar will first format the buggy program and all the correct programs. Then S_Brafar will search the closest correct program for the buggy program as the repair reference. Then S_Brafar will use the searched reference program to repair the buggy program through four stages: Bidirectional Refactoring, Alignment, Fault Localization and Block Repair. The repair result is stored in ```output``` directory.
* The formatted codes are stored in ```output/wrong``` and ```output/correct```
* The refactored codes are stored in ```output/refactored```
* The repaired code is stored in ```output/repaired```

