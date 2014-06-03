COMS22201: Language Engineering Coursework
==========================================

This is my solution to the [COMS22201](https://www.cs.bris.ac.uk/Teaching/Resources/COMS22201/) coursework from year 2012/2013. We were to create a compiler for a programming language "Camle". The solution is written in Java and Antler 3.

Building
--------
It requires antler3 and JDK 6.

In a Linux terminal type
`make`


Using the compiler 
------------------
Type:
`antlr3 camle file_to_compile.le`

There are some options that can be specified:

 * `-syn` - generates syntax tree
 * `-irt` - generates IR tree
 * `-cg` - generates assembly code

What was implemented from Camle specification
---------------------------------------------

I have completed the following parts:

  * Arithmetic operations
  * Variables
  * Assignment statment
  * read statement, 
  * conditional statements
  * repeat loops
  * simple error checking and reporting

Simple tests are provided for all of these features.
