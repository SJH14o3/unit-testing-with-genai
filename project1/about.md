## The Small Project
This is a small project containing 4 units. it has been made for unit testing
demonstration.
repository link: https://github.com/AntonisGkortzis/UnitTestingDemoProject  
We do focus on *unit_test_demo\src\main\java* in *dmst.seip.angor.junitdemo* package.
this is where all 4 units are located.
as the project name yells, there are already generated unit tests. this provides an
 opportunity to compare generated tests with already present tests.  
The units are:
- FileIO
- MyMath
- MyAdvancedMath
- NameSearch

### FileIO Unit
This unit contains a single method that returns lines of a file. possible
edge cases are:
- no file with provided path exists
- file is empty

### MyMath Unit
This unit contains two methods.
- a method that returns sign of an integer. the only possible edge case is zero as input
- a method that reverses a number. edge cases are:
  - zero as input 
  - minimum integer as input (since maximum positive is [minimum integer * - 1] - 1)

### MyAdvancedMath Unit
This unit does more complex math operations compared to the previous unit. it contains
three methods which are:
- a method to add two non-negative numbers. edge cases are:
  - either x and y be negative number
  - adding x and y causing overflow
- a method to calculate two power of given number as integer. edge cases are:
  - power is below zero (in reality, the result is a fraction number)
  - power is above 30 (causing overflow)
- a method to reverse all the elements in given integer array. it will use the *reverse(int)* method
from *MyMath* Unit. To me, changing the array elements and returning it is a mistake
since arrays are passed as references and every change will also apply to the
original array. Other than *MyMath.reverse(int)* edge cases, we have:
  - empty array

### NameSearch Unit
This unit contains a single method that search for matching
prefix in lines of a file. It utilizes FileIO unit to read a file then it will try to match the lines with the matching prefix.   
some edge cases are:
- empty prefix
- empty file