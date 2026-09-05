# TODOs
- Refine Some notes to mention part
- mention GPT-OSS didn't always use the skill. When unprompted, it was very unlikely. Even when prompted, it rarely didn't use it (12% of the times (3 of 25)) 
- Mention test files available in the report are the version with fixed errors. However, they still have the failed assertions and why I need to fix these assertion errors rather than ignoring it, is to increase coverage.
- mention that I do run the tests now
- mention how token used is calculated for group
- mention a JaCoCo weakness which is counting deprecated methods
- mention why coverage changed from group 2 (lack of time)
- consider including Jacoco report with all tests in files.
- in complete summary, run the project with every single tests model generated.

# Unit Test Generation Using OpenCode for Medium Project
We are dealing with a much larger code, from 86 to 10100 LOC. This report won't be as detailed previously. In addition, generated tests will not be available in this file in code fences, so this file will purely contain analysis of generated tests.  
Two local models "Gemma4:26b" and "GPT-OSS:20b" are going to be utilized to generate tests for the whole project. Each model will generate tests individually (one file per session) and in the specified groups (all files of the group in a single session). Therefore, for each file, 4 unit tests will be generated. In the next part, I have explained why I came up with this way of testing.
Moreover, order will be changed from of all generated tests of a model to generated tests of a single file.

# Reason behind the said method of testing
I tried three ways to generate unit tests for the whole project with gemma4e:e4b. For a comparison reference, the test generated for "com/google/gson/JsonArray.java" is used. Prompts were same apart from referencing which files to generate tests for.

## 1. Generating test for all files of project in one session.
Firstly, I used plan mode, and prompted the agent to plan out multiple stages for this generation. It came up with four stages:
1. Core Data Structures and Annotations
2. Core Processing Infrastructure
3. Utility and Strategy Implementations
4. Internal Utilities and Complex Adapters
After the plan was ready, I prompted it to proceed with stage 1. After each stage was completed, I had to prompt it to proceed with the following stage.
In the end, 22 files were generated, and each file contained few tests. JsonArrayTest only had 68 lines.

## 2. Generating test for a single file in a session
I didn't use plan mode. The given group is the "1. Core JSON Data Model". It contained 5 files (JsonParser wasn't part of this group at that moment). Anyway, JsonArrayTest was generated with 249 lines.

## 3. Generating test for all files of a group in a session
Plan mode wasn't used. After 1-2 files were generated, the agent stopped and I had to prompt it to continue. JsonArrayTest was generated with 341 lines.

## Result
Local models cannot handle a large amount of input, so it is better to partition the input into relatively similar groups and attempt to generate tests for each group separately. It will be interesting to see how much different there will be if instead of group, each file is given separately. However, this is going to be a very tedious task, and also the model will use more context. That's why I will keep track of the total amount of context used.

## Some additional notes to mention
Gemma4 always used "junit-5-skill" no matter if I prompted it explicitly or not. On the contrary, GPT-OSS did not use it the majority of the times when I didn't prompt it to use skill. So to compensate for this, I explicitly will prompt the agents to use the skill.

### Given Prompt
Files to be tested are many. So, the focus is to achieve decision coverage. The skills used for the previous project are still installed. However, since GPT-OSS:20b won't use a skill majority of the times unlike gemma4:26b, I wrote it in the prompt explicitly. When I had started generating tests, I noticed that the agents tried to run the tests which wasn't necessary, therefore I added to the prompt to avoid running the tests. Files will be linked to the prompt. At last, this is the prompt given to the agents:
> For these files, generate unit tests using Junit5. Achieve decision coverage. Use "junit-5-skill" skill. Concentration is only on units, so don't test integration and don't use mocking. do not build and run the tests, only generate them.

### How Coverage is calculated?
I started to use a tool called jacoco which measures instructions and branch/decision coverage. Since I prompted the agents to achieve decision coverage, this is a perfect tool to assess the coverage. This tool can be added to a project dependencies as a plugin, and upon build of a project, it will generate a report in html. This file contains coverage assessment of the whole project. Moreover, it is possible to see each file and its method coverage individually. This tool probably made my life much easier, since not only don't I have to assess coverage on my own, but also I don't need to worry about human mistakes. At the end of analysis for each file, there is a summary that contains the assessment of coverage by this tool for generated tests. Note that all tests must pass in order to be able to generate a report. As a result, I fixed assertions that generated wrongly, but the generated test files still have the wrong assertions.

## Group 1: Core JSON Data Model
# TODO:
GPT-Group didn't generate JsonNull in the first prompt.
GPT-Single for JsonArray's second attempt to use skill, first didn't despite the prompt.
### JsonArray
This file has 258 statements and 24 decisions.
#### Gemma Group
The generated file contained 3 errors:
- null was directly passed to add method. It must be cast into a supported type first.
- A string was passed as second argument of set method. A JsonPrimitive with a string value must be passed.
- Like previous error, but for add function of a list of JsonElement.

After fixing the errors, all the 14 tests passed.  
list of tests:
1. empty array initialization
2. empty array with initial capacity initialization
3. deepCopy() of non-empty array
4. deepCopy() of empty array
5. add() with various types
6. adding null
7. addAll()
8. set()
9. removal with element
10. removal with index
11. contain()
12. iterator()
13. asList()
14. equals() and hashCode()

Missing coverage:
- for four of add(...) methods, only non-null branch was tested (addElement_handlesNull() should have handled this).
- Like previous but for set(...)
- getAsSingleElement() and all getAs... have not been tested at all.
- in equals(), 2 of 6 branches are missed.


#### Gemma Single
The generated file contained 4 errors:
- A string was passed as second argument of set method. A JsonPrimitive with a string value must be passed.
- An integer was passed as second argument of set method. A JsonPrimitive with an integer value must be passed.
- In testGetAsMethodsSuccess(), different types of primitives are passed in their java form, not JsonPrimitive form.
- null was directly passed to add method. It must be cast into a supported type first.

After fixing all errors, all 27 tests passed.

list of tests:
1. emptyArray initialization
2. emptyArray with initial capacity initialization
3. deepCopy() with non-empty array
4. deepCopy() with empty array
5. add(Boolean) should add boolean or JsonNull
6. add(Character) should add character or JsonNull
7. add(Number) should add number or JsonNull
8. add(String) should add string or JsonNull
9. add(JsonElement) should add JsonElement or JsonNull
10. addAll()
11. set() with non-null
12. set() with null
13. set() with index out of bounds
14. removal with element
15. removal with index
16. removal with index out of bounds
17. contain()
18. size()
19. isEmpty()
20. iterator()
21. valid get()
22. get() with index out of bounds
23. getAs... success [parametrized] with provider (counted as a single test)
24. getAs... failure
25. asList()
26. equals()
27. hashCode()

Tests were in order corresponding to the original file.
In testGetAsMethodsFailure(), getAsInt() is tested twice in each assert group.

Missing coverage:
- getAsNumber() and getAsFloat() were not tested at all
- null argument for add(JsonElement)

#### GPT-OSS Group
The generated file contained 2 errors:
- missing package statement
- passing integer as second argument of set(). Fixed with replacing it with *new JsonPrimitive(int)*

After fixing all errors, 5 tests passed and 1 test failed.  in setReplacesAndHandlesNull(), *arr.set()* returns the previous element which the agent though it would be null, but it's a *JsonPrimitive*.

list of tests:
1. add null to array in various types
2. filled array deepCopy()
3. addAll()
4. set()
5. removal test with element and index
6. getAsSingle() for array with 2 elements

Missing coverage:
- deepCopy empty array case
- missed adding null as Boolean and Character
- no successful getAsSingleElement() test
- missed 2 of 6 branches in equals()
- contain(), isEmpty(), iterator(), getAs...(), and hashCode() were not tested at all

#### GPT-OSS Single
There was no error

19 tests passed and 2 tests failed. Failed tests are:
1. the last assertion in testGetAsSingleElementSingle() will throw as exception. Funny that even the agent had realized this in the comment next to the assertion, but it did not fix it. Assertion is commented
2. This one is actually Gson fault! In JsonPrimitive.deepCopy(), the same object (reference) is returned, that's why assertSame in testDeepCopyNonEmpty() fails!

list of tests:
1. empty deepCopy
2. non-empty deepCopy
3. add(Boolean) with null argument
4. add(Character) with null argument
5. add(Number) with null argument
6. add(String) with null argument
7. add(JsonElement) with null argument
8. addAll()
9. set()
10. successful removal with element
11. failed removal with element
12. removal with index
13. contain()
14. isEmpty()
15. iterator()
16. valid get()
17. get() with index out of bounds
18. getAsSingleElement() works with array size of 1
19. getAsSingleElement() throws exception when size != 1
20. equals() and hashCode()
21. asList()

Missing coverage:
- add(), null Boolean and Character
- set(), null element
- missed 2 of 6 branches in equals()
- for getAs...(), only getAsNumber and getAsString are covered. The rest were not covered at all

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      3       |       4       |       2       |       0        |
| Statement coverage             |     70%      |      96%      |      65%      |      81%       |
| Decision coverage              |     62%      |      95%      |      75%      |      79%       |
| total tests                    |      14      |      27       |       6       |       21       |
| failed tests                   |      0       |       0       |       1       |       2        |
| method names start with test   |      no      |      yes      |      no       |      yes       |
| private variables used         |     yes      |      yes      |      no       |       no       |
| tokens used                    |    10,693    |    24,550     |     8,110     |     15,420     |
| tokens per decision coverage % |     172      |      258      |      108      |      195       |
All agents did pretty decent. Choosing the model and method to do the job comes down to a balance between token usage and coverage. If aiming for around 80% coverage, GPT-OSS in single file is the most reasonable option. If aiming for best coverage no matter of cost, Gemma4 Single is winner.

### JsonElement
This file has 172 statements and 8 decisions.  
The Class is abstract, therefore tests will be indirect through subclasses.  
getAsCharacter() is deprecated. However, it is counted in coverage since the agents generated test for it.

#### Gemma Group
The generated file contained no errors.

10 tests passed and 4 tests failed. Failed tests are getAsBoolean_throwsException(), getAsNumber_throwsException(), getAsString_throwsException(), and getAsDouble_throwsException(). JsonArray is used to call the respective methods. However, these methods have been overridden and another exception is thrown. This can be fixed by using JsonObject instead.

list of tests:
1. isJsonArray() correctly identifies JsonArray
2. isJsonObject() correctly identifies JsonObject
3. isJsonPrimitive() correctly identifies JsonPrimitive
4. isJsonNull() correctly identifies JsonNull
5. getAsJsonObject() throws exception if not an object
6. getAsJsonArray() throws exception if not an array
7. getAsJsonPrimitive() throws exception if not a primitive
8. getAsJsonNull() throws exception if not a null
9. getAsBoolean() throws exception if not a primitive or array
10. getAsNumber() throws exception if not a primitive or array
11. getAsString() throws exception if not a primitive or array
12. getAsDouble() throws exception if not a primitive or array
13. toString() returns correct JSON representation
14. deepCopy() for JsonElement subclasses

Missing coverage:
- a condition in getAsJsonNull() is not covered
- IOException in toString is not checked
- getAsFloat(), getAsLong(), getAsInt(), getAsByte(), getAsCharacter(), getAsBigDecimal(), getAsBigInteger() and getAsShort() are not tested at all.

#### Gemma Single
The generated file contained no error.

After fixing all errors, 17 tests passed and 4 tests failed. Failed tests are testGetAsJsonObjectThrows(), testGetAsJsonArrayThrows(), testGetAsJsonPrimitiveThrows(), and testGetAsJsonNullThrows(). In these tests, IllegalStateException is thrown, not ClassCastException.

list of tests:
1. isJsonArray() correctly identifies JsonArray
2. isJsonObject() correctly identifies JsonObject
3. isJsonPrimitive() correctly identifies JsonPrimitive
4. isJsonNull() correctly identifies JsonNull
5. successful getAsJsonObject()
6. successful getAsJsonArray()
7. successful getAsJsonPrimitive()
8. successful getAsJsonNull()
9. failed getAsJsonObject()
10. failed getAsJsonArray()
11. failed getAsJsonPrimitive()
12. failed getAsJsonNull()
13. getAsBoolean()
14. getAsNumber()
15. getAsString()
16. getAsDouble()
17. getAsInt()
18. getASLong()
19. getAsFloat()
20. getAsByte()
21. getAsShort()

For getAsJson...(), the object that is tested should have been a JsonElement prototype. This would have made more sense.

Missing coverage:
- all getAs...() are not covered, although there are tests for them. Agent should have tested in a way that the code in JsonElement would be executed, not overridden code.
- IOException in toString is not tested.

#### GPT-OSS Group
The Generated file contained 1 error, which was missing package statement.

After fixing the error, all 2 tests passed.

list of tests:
1. is...()
2. getAs...() throwing exception

This was a poor test generation. Using two tests for a unit generation is deficient.

Missing coverage:
- getAsJsonPrimitive() misses a false branch
- all getAs...() are not covered. This is the same as Gemma4 Group. There are a few assertions, but they are from classes that have overridden these methods.
- IOException in toString is not tested.

#### GPT-OSS Single
The Generated file contained 2 errors:
- missing package statement
- passing null as second argument to 

After fixing all errors, 9 tests passed and 13 tests failed! Failed tests are:
- asserting false on a null object isJsonNull() call! Assertion commented.
- from getAsBooleanThrowsUnsupported() onward, these methods do not throw exception, in fact, a value is returned instead. Tests are replaced with assertEquals.

list of tests:
1. isJson...() return true when correct
2. valid getAsJsonObject()
3. invalid getAsJsonObject
4. valid getAsJsonArray()
5. invalid getAsJsonArray()
6. valid getAsJsonPrimitive()
7. invalid getAsJsonPrimitive()
8. valid getAsJsonNull()
9. invalid getAsJsonNull()
10. getAsBoolean() throwing exception
11. getAsNumber() throwing exception
12. getAsString() throwing exception
13. getAsDouble() throwing exception
14. getAsFloat() throwing exception
15. getAsLong() throwing exception
16. getAsInt() throwing exception
17. getAsByte() throwing exception
18. getAsCharacter() throwing exception
19. getAsBigDecimal() throwing exception
20. getAsBigInteger() throwing exception
21. getAsShort() throwing exception
22. toString() converts to Json

The failed tests of this agent are ridiculous.

Missing coverage:
- other that getAsJsonObject(), getAsJsonArray(), getAsJsonPrimitive(), and getAsJsonNull(), other getAs... are not covered, for the same reason as previous models.
- IOException in toString is not tested.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      0       |       0       |       1       |       2        |
| Statement coverage             |     62%      |      47%      |      37%      |      47%       |
| Decision coverage              |     87%      |     100%      |      62%      |      100%      |
| Total tests                    |      14      |      21       |       2       |       22       |
| Failed tests                   |      4       |       4       |       0       |       13       |
| Method names start with test   |      no      |      yes      |      no       |       no       |
| Private variables used         |      no      |      no       |      no       |       no       |
| Tokens used                    |    7,178     |    32,307     |     5,444     |     16,547     |
| Tokens per decision coverage % |      83      |      323      |      88       |      165       |
The main reason why statement coverage has significantly dropped is not using a subclass that has not overridden getAs...() methods. In addition, IOException in toString was not tested by any of the models. It's probably hard to replicate, so it's not a big deal. Gemma4 group generated test had the best performance here.

### JsonNull
This file has 16 statements and 0 decision.
This is a very small file; In fact, this file has the lowest LOC between all the files. full decision coverage is possible with few tests.
#### Gemma Group
The Generated file contained no errors.

All 4 tests passed.

list of tests:
1. JsonNull.INSTANCE is a singleton
2. deepCopy() returns the same instance
3. hashCode() is consistent
4. equals() works correctly

Every statement and decision is covered.

#### Gemma Single
The Generated file contained 1 error which was missing package statement.

After fixing the error, all 4 tests passed.

list of tests:
1. JsonNull.INSTANCE should not be null
2. deepCopy should return the same instance
3. hashCode should be consistent with class hash code
4. equals should work correctly for JsonNull instances

Every statement and decision is covered.

#### GPT-OSS Group
The Generated file contained 1 error which was missing package statement.

After fixing the error, all 3 tests passed. 

list of tests:
1. JsonNull is singleton
2. deepCopy() returns the same instance
3. equals() works correctly

Every statement and decision is covered.

#### GPT-OSS Single
The Generated file contained 1 error which was missing package statement.

After fixing the error, all 4 tests passed.

list of tests:
1. deepCopy() returns the singleton instance
2. hashCode() is constant across instances
3. equals returns true for another JsonNull instance
4. equals returns false for non-JsonNull objects

Every statement and decision is covered.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      0       |       1       |       1       |       1        |
| Statement coverage             |     100%     |     100%      |     100%      |      100%      |
| Decision coverage              |     100%     |     100%      |     100%      |      100%      |
| total tests                    |      4       |       4       |       3       |       4        |
| failed tests                   |      0       |       0       |       0       |       0        |
| method names start with test   |      no      |      yes      |      no       |       no       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    1,391     |    11,126     |     1,055     |     10,297     |
| tokens per decision coverage % |      14      |      111      |      11       |      103       |
This file only had 4 methods. Achieving full coverage was a piece of cake. This file is tiny so that tests generated by agents given grouped files completely covered it with using very few tokens.

### JsonObject
This file has 171 statements and 18 decisions.
#### Gemma Group
The Generated file contained no error.

All 10 tests passed.

list of tests:
1. constructor creates empty object
2. addProperty() works for different types
3. add(String, JsonElement) handles null value
4. remove(String) removes member and returns it
5. remove(String) returns null if member not found
6. has(String) works correctly
7. getAsJsonPrimitive()/Array()/Object() works
8. keySet() and entrySet() work
9. asMap() returns mutable map view
10. equals() and hashCode() work correctly

This agent generated two utility methods jsonArraySize(JsonObject) and objObjectHashCode(JsonObject) for no reason. It is possible to get array size and hash code from methods of the JsonObject.

Missing coverage:
- missing null branch in all addProperty() methods
- in equals(), 2 of 6 branches are not covered
- deepCopy() was not tested at all.

#### Gemma Single
The Generated file contained 4 errors:
- in newJsonObjectIsEmpty(), non-defined variable jsontoObject used instead of jsonObject.
- above addStringProperty(), a line explaining something was not commented.
- in addStringPropertyNull(), null is passed without string cast.
- in equalsSameMembers(), non-defined variable objermedObj2 used instead of obj2.

After fixing all errors, all 27 tests passed.

list of tests:
1. New JsonObject should be empty
2. size() should return the correct number of members
3. add(String, JsonElement) adds element
4. add(String, JsonElement) converts null value to JsonNull
5. [parametrized] addProperty() with various types (counted as 4 tests)
6. addProperty(String, String) handles null
7. addProperty(String, Number) handles null
8. addProperty(String, Boolean) handles null
9. addProperty(String, Character) handles null
10. remove(String) removes existing key and returns element
11. remove(String) returns null for non-existent key
12. get(String) returns correct element
13. getAsJsonPrimitive(String) returns JsonPrimitive
14. getAsJsonArray(String) returns JsonArray
15. getAsJsonObject(String) returns JsonObject
16. getAsJsonPrimitive() throws ClassCastException if not a primitive
17. has(String) checks for key existence
18. entrySet() returns all entries
19. keySet() returns all keys
20. asMap() returns mutable map view
21. equals() compares objects with same members
22. equals() returns false for different members
23. equals() handles identity and null
24. deepCopy() creates an independent copy

Calling non-existing variable was unprecedented.

Missing coverage:
- missing null case for addProperty() with Number, Character, and Boolean value.
- a single branch in equals is missed.

#### GPT-OSS Group
The Generated file contained a single error, not stating the package.

After fixing all errors, 3 tests passed and a single test failed.
- in getAsJsonXThrowsOnWrongType(), ClassCastException is returned instead of IllegalStateException in both assertions.

list of tests:
- add() and remove()
- getAs...() throws exception on wrong type
- asMap()
- equals() and hashCode()

Four tests are not enough to cover this file at all. A lot of methods are not even tested.

Missing coverage:
- null JsonElement not covered in add()
- add() with null Number not covered.
- add() for Boolean, Character, and String are not tested at all.
- missing 3 of 6 branches in equals()
- deepCopy(), entrySet(), keySet(), size(), isEmpty(), getAsJsonPrimitive(), getAsJsonArray(), and getAsJsonObject()  were not tested at all.

#### GPT-OSS Single
The Generated file contained an error, which is missing package statement:

After fixing the error, all 8 tests passed.

list of tests:
1. add() handles null values
2. addProperty() overloads store correct primitive types
3. deepCopy() creates an independent copy
4. remove() returns removed element and handles missing key
5. has(), get(), entrySet(), keySet(), size(), and isEmpty() work correctly
6. asMap() provides a mutable view of the underlying members
7. getAsJsonPrimitive() and others throw ClassCastException on wrong type notes
8. equals() and hashCode() are consistent irrespective of insertion order

Missing coverage:
- 2 of 6 branches are missing in equals()
- getAsJsonArray() and getAsJsonObject() are not tested at all.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      0       |       4       |       1       |       1        |
| Statement coverage             |     78%      |      92%      |      39%      |      92%       |
| Decision coverage              |     55%      |      77%      |      27%      |      88%       |
| total tests                    |      10      |      27       |       4       |       8        |
| failed tests                   |      0       |       0       |       1       |       0        |
| method names start with test   |      no      |      no       |      no       |      yes       |
| private variables used         |     yes      |      yes      |      no       |       no       |
| tokens used                    |    5,493     |    15,170     |     4,166     |     13,412     |
| tokens per decision coverage % |     100      |      197      |      154      |      152       |
Surprisingly, GPT-OSS in single file did better than Gemma4 despite having less parameters and being an older model. However, in group file, it did the worst. GPT-OSS in single mode is the obvious winner. The 152 tokens per decision coverage percent is very reasonable.

### JsonPrimitive
This file has 415 statements and 88 decisions.
#### Gemma Group
The Generated file contained 3 errors. All errors are commented. Here are the errors:
- Made a dummy JsonableNumber class to inherit from JsonPrimitive, which is a final class! Fixed by replacing the usage to JsonPrimitive.
- Made another dummy BooleanPrimitive class. Fixed like above.
- in getAsFloat_works(), agent was trying to fix a syntax error but forgot to remove the faulty code.

After fixing all errors, all 17 tests passed.

list of tests:
1. Constructor with Boolean works
2. Constructor with Number works
3. Constructor with String works
4. Constructor with Character works
5. deepCopy() returns this
6. getAsBoolean() parses string 'true'
7. getAsNumber() handles String
8. getAsString() works for Number and Boolean
9. getAsBigDecimal() works
10. getAsBigInteger() works
11. getAsFloat() works
12. getAsLong() works
13. getAsShort() works
14. getAsInt() works
15. getAsByte() works
16. getAsCharacter() is deprecated but works
17. hashCode() and equals() work correctly

This file contains many statements and decisions, but 17 tests are inadequate to achieve high coverage.

Missing coverage:
- In getAsNumber(), only value is an instance of Number is tested. String and miscellaneous are not tested.
- In getAsString(), the miscellaneous branch is not tested.
- In getAsBigDecimal(), BigDecimal branch is not tested.
- In getAsBigInteger(), BigInteger and true isIntegral branches are not tested.
- In getAsFloat(), getAsLong(), getAsShort(), and getAsByte(), a single branch is not tested.
- In getAsCharacter() throwing of UnsupportedOperationException is not tested.
- In hashCode(), "value == null", "isIntegral(this)", and "value instanceof Number" are never true.
- In equals(), 10 branches are not tested.
- In isIntegral(), a single branch is not tested.
- getAsDouble(), hashOfDoubleValue are not tested at all.


#### Gemma Single
The Generated file contained a single error. In testBigInteger(), a line was cut off but continued in the next line.

After fixing all errors, all 17 tests passed.

list of tests:
1. Should handle boolean values correctly
2. Should parse boolean from string
3. Should handle integer values
4. Should handle double values
5. Should handle BigDecimal values
6. Should handle BigInteger values
7. Should parse number from string
8. Should throw exception when getting number from boolean
9. Should handle string values
10. Should handle character as string
11. Should throw exception if string is empty for getAsCharacter
12. Should respect equality for different numeric types with the same value
13. Should respect equality for BigDecimal scale difference
14. Should handle NaN equality
15. [Parametrized] Should correctly identify types and values (counted as 3 tests)

I was expecting to obtain better result. Decision coverage has doubled, but still it isn't enough.

Missing coverage:
- In getAsString(), the miscellaneous branch is not tested.
- In getAsBigDecimal(), parsing as String is not tested.
- In getAsLong(), parsing as String is not tested.
- In hashCode(), null, Number, and Miscellaneous instances are not tested.
- In equals(), 12 branches are not tested.
- In isIntegral(), 4 branches are not tested.
- getAsFloat(), getAsShort(), and getAsByte() are not tested at all.

#### GPT-OSS Group
The Generated file contained a single error which is missing package statement.

After fixing the error, all 5 tests passed.

list of tests:
1. boolean conversion
2. number parsing
3. string conversion
4. character handling
5. empty string getCharacter() exception

This was a terrible test generation. 5 tests for such a file is extremely insufficient.

Missing coverage:
- in getAsNumber(), getAsDouble(), getAsFloat(), getAsLong(), and getAsInt(), a single branch was not tested.
- deepCopy(), isString(), getAsBigDecimal(), getAsBigInteger(), getAsShort(), getAsByte(), hashCode(), hashOfDoubleValue(), equals(), and isIntegral() were not tested at all.

#### GPT-OSS Single
The Generated file contained no errors.

11 tests passed and 1 test failed. Failed test is equalsAndHashCodeConsistency(). Agent thought that initializing a JsonPrimitive with argument 42 and "42" should result in equal objects which isn't the case. This was fixed by changing assertEquals to assertNotEquals. Also, their hash code was expected to be equals and I couldn't fix it properly, so I just added two assertions verifying each hashCode separately.

list of tests:
1. boolean constructor and access
2. number constructor and access
3. string constructor and access
4. character constructor and access
5. getAsNumber() from string
6. getAsNumber() from non-numeric string throws RuntimeException
7. getAsCharacter() from empty throws UnsupportedOperationException
8. equals() and hashCode() consistency
9. bigDecimal equality with scale
10. negative zero equality
11. NaN equality
12. non-number value equals false to a number value

Missing coverage:
- in getAsBoolean(), getAsNumber(), getAsBigDecimal(), getAsFloat(), getAsLong(), getAsInt(), hashOfDoubleValue(). a single branch was not tested.
- in getAsString(), and hashCode(), two branches were not covered.
- in equals, 15 branches are not tested.
- deepCopy(), getAsBigInteger(), getAsShort(), and getAsByte() are not tested at all.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      3       |       1       |       1       |       0        |
| Statement coverage             |     49%      |      77%      |      29%      |      71%       |
| Decision coverage              |     31%      |      62%      |      14%      |      57%       |
| total tests                    |      17      |      17       |       5       |       12       |
| failed tests                   |      0       |       0       |       0       |       1        |
| method names start with test   |      no      |      yes      |      no       |       no       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    12,671    |    16,968     |     9,610     |     13,627     |
| tokens per decision coverage % |     409      |      274      |      686      |      239       |
All models did poorly for this file, especially in equals() which was a complex method in terms of branching. Many other branches were also missed. Single models are clearly the better option here. Gemma4 did better overall.

### JsonParser
This file has 77 statements and 6 decisions.
#### Gemma Group
The Generated file contained an error which was missing JsonReader import.

After fixing the errors, 6 tests passed and 1 test failed. The failed test was expecting an JsonSyntaxException in parseReader_multipleElements_throwsException() but JsonReader fixes this with only reading the first element. Making a syntax error in input json will result in JsonSyntaxException.

list of tests:
1. parseString works for valid JSON
2. parseString throws exception for invalid JSON
3. parseReader works for valid JSON
4. parseReader throws exception for invalid JSON
5. parseReader throws exception for multiple top-level elements
6. parse(String) follows parseString
7. parse(Reader) follows parseReader

Missing coverage:
- in parseReader(Reader), 2 branches and any of the exceptions are not tested.
- in parseReader(JsonReader), a branch and any of exceptions are not covered
- parse() is not tested, however, it's a deprecated method.

#### Gemma Single
The Generated file contained 2 errors:
- in deprecatedMethods_delegateCorrectly(), parse(...) are called statically for the last two assertions. Fixed with instantiating JsonParser.

After fixing all errors, all 9 tests passed.

list of tests:
1. parseString: valid JSON object
2. parseString: valid JSON array
3. parseString: valid JSON null
4. parseString: throws JsonSyntaxException for invalid JSON
5. parseReader: valid JSON string
6. parseReader: throws JsonSyntaxException for incomplete document (trailing data)
7. parseReader: handles IOException
8. parseReader(JsonReader): respects strictness and handles LEGACY_STRICT
9. Deprecated methods: verify they delegate correctly

Missing coverage:
- in parseReader(Reader), a branch is missing, therefore, exception is not thrown.
- in parseReader(JsonReader), an exception is not thrown and a single branch is also not covered.

#### GPT-OSS Group
The Generated file contained 1 error which is missing package statement.

After fixing the error, all 4 tests passed.

list of tests:
1. valid parse string
2. parse string with multiple elements throws JsonSyntaxException
3. parse reader trailing data throws JsonSyntaxException
4. parse reader empty returns JsonNull

Having only 4 tests. Because deprecated methods are not tested, statement coverage is reduced. it is as good as Gemma4 Single if these methods are not counted.

Missing coverage:
- the constructor without argument is not tested, however, it's deprecated.
- in parseReader(Reader), a branch is missing, therefore, exception is not thrown.
- in parseReader(JsonReader), an exception is not thrown and a single branch is also not covered.
- three deprecated methods.

#### GPT-OSS Single
The Generated file contained 3 errors:
- missing package statement.
- wrong directory for importing Strictness.
- a conceptional test that overrides a method that it can't. I commented it to avoid syntax error.

After fixing all errors, 5 tests passed and 1 test failed. Failed test is:
- in parseReader_multipleTopLevelElements_throwsException(), exception message is asserted. This is a complete wrong behavior. The assertion is commented.

list of tests:
1. parseString parses valid JSON string into JsonObject
2. parseString throws JsonSyntaxException for malformed JSON
3. parseReader parses valid Reader into JsonObject
4. parseReader throws JsonSyntaxException when data remains after the first element
5. parseReader throws JsonIOException when Reader throws IOException
6. parseReader(JsonReader) restores original strictness after parsing

Missing coverage:
- the constructor without argument is not tested, however, it's deprecated.
- in parseReader(Reader), two branches are missing, therefore, exception is not thrown.
- in parseReader(JsonReader), an exception is not thrown and a single branch is also not covered.
- three deprecated methods.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      1       |       2       |       1       |       3        |
| Statement coverage             |     63%      |      75%      |      59%      |      59%       |
| Decision coverage              |     50%      |      66%      |      66%      |      50%       |
| total tests                    |      7       |       9       |       4       |       6        |
| failed tests                   |      1       |       0       |       0       |       1        |
| method names start with test   |      no      |      no       |      no       |       no       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    4,321     |    13,018     |     3,277     |     11,618     |
| tokens per decision coverage % |      86      |      197      |      50       |      232       |
All agents didn't test exceptions thrown in parseReader(Reader) and parseReader(JsonReader), which are rare exceptions. as a result, this can be neglected. If we also ignore deprecated methods, all agents achieve almost full coverage. Since this is a relatively small file, grouping this file with other small files is preferred. GPT-OSS group used the least tokens. Nevertheless, it had the best coverage.

## Group 2: Streaming parser/writer state machines
# TODO:
gemini single was given to continue. Also, it was the second attempt. in the first, it started to run the project with gradle which failed.
### JsonWriter
This file has 848 statements and 136 decisions.
#### Gemma Group
The Generated file contained a single error. Code was trying to access Strictness.STRICT using Strict::STRICT.

After fixing the error, 2 tests passed and 2 tests failed. Failed tests are:
- in testStrictness(), strictness is set to STRICT first and *jsonWriter.value(Double.NaN)* throws exception. In the second part of this test, this is tested but exception is asserted. I changed the strictness of the first part to LENIENT.
- Made almost the same mistake. *jsonWriter.beginArray()* will throw an exception which is asserted in part two but not part one. assertion is commented. Running this method again, and I realized many codes throws exception. Figuring out the intention of the test method, I just commented this of the part.

list of tests:
1. value types
2. formatting styles
3. strictness
4. nesting problem

4 tests for a file with these many statements is inadequate.

Missing coverage:
- in setFormattingStyle(), name(), and nullValue(),  two branches in two different points are not covered.
- in closeScope(), 5 branches are missing in total.
- in push(), peek(), value(String), and beforeName(), a single branch is not tested.
- setLenient() is not tested, however, it is deprecated.
- in value(double), 3 of 6 branches missed.
- in close(), exception is not thrown.
- in beforeValue(), two cases are not tested.
- in string(), 4 branches in total are not tested.
- setIndent(), getFormattingStyle(), isLenient(), getStrictness(), setHtmlSafe(), isHtmlSafe(), getSerializeNulls(), value(Boolean), value(float), value(Number), jsonValue(), flush(), and alwaysCreatesValidJsonNumber(), are not tested at all.


#### Gemma Single
The Generated file contained 2 errors:
- FormattingStyle was not imported.
- an unexpected token in beginObject_endObject_works().
- calling undefined method "setLength" on a JsonWriter in valueBoolean_handlesTrueAndFalse.
- Strictness was not imported
- StringWriter does not have setLength(). I think the agent tried to reset the data. I will instantiate that variable again instead, since I didn't find a method to do it.

After fixing all errors, 23 tests passed and 7 tests failed. Failed tests are:
- in name_throwsException_ifAlreadyWroteAName(), after a name written, another name must be written to throw exception.
- in valueBoolean_handlesTrueAndFalse(), writer can only write a single value and writing another value will throw exception. variables *out* (for assertion) and *writer* are reset to fix this. 
- In every test that *out = new StringWriter()* is replaced setLength(0), *writer = new JsonWriter(out)* is also added. This fixed many of the failures.
- in nullValue_respectsSerializeNulls(), in first assertion, '}' at the expected string is wrong. In the second assertion, writing the null value removes the name, so the expected string is "{".

list of tests:
1. Constructor should throw NullPointerException if out is null
2. setIndent() should set compact style if indent is empty
3. setIndent() should set pretty style if indent is non-empty
4. setFormattingStyle() should throw NullPointerException if style is null
5. setLenient() should set strictness correctly
6. setStrictness() should throw NullPointerException if strictness is null
7. setHtmlSafe() should set htmlSafe flag
8. setSerializeNulls() should set serializeNulls flag
9. beginArray() and endArray should work correctly
10. beginObject() and endObject should work correctly
11. endArray() should throw IllegalStateException if not in array context
12. endObject() should throw IllegalStateException if not in object context
13. endArray() should throw IllegalStateException if name is dangling
14. endObject() should throw IllegalStateException if name is dangling
15. name() should throw IllegalStateException if already wrote a name
16. name() should throw IllegalRuntimeException if not in object context
17. value(String) should handle null value
18. value(boolean) should handle true and false
19. value(Boolean) should handle null value
20. value(float) should handle finite values and throw on NaN/Infinity in strict mode
21. value(double) should handle finite values and throw on NaN/Infinity in strict mode
22. value(long) should handle long values
23. value(Number) should handle various number types
24. value(Number) should throw exception for invalid JSON numbers if not lenient
25. nullValue() should respect serializeNulls setting
26. jsonValue(String) should handle null value
27. close() should throw IOException if the document is incomplete
28. close() should work correctly for complete document
29. flush() should throw IllegalStateException if writer is closed
30. string(String) should handle escaping and HTML safe mode

Missing coverage:
- setFormattingStyle(), closeScope(), peek(), name(), value(Boolean), value(Number), jsonValue(String), flush(), string(), newline(), beforeName(), and beforeValue() have missing branches
- getStrictness() is not tested at all.

#### GPT-OSS Group
The Generated file contained 2 errors:
- missing import for Strictness.
- used an improper way to access written json content. It was trying to access these content with a private method which is inaccessible. I replaced it by changing output to a StringWriter

After fixing all errors, 5 tests passed and one test failed. In the failed test, StringWriter and JsonWriter had to be reset in order to expect the correct result.

list of tests:
1. writes a simple array and closes correctly
2. throws on nested beginObject after endObject
3. serialize null when configured
4. honors HTML-safe escaping
5. fails on NaN/Infinity when strictness is LEGACY_STRICT
6. supports arbitrary numbers including BigInteger

Missing coverage:
- setFormattingStyle(), closeScope(), push(), peek(), name(), value(float), value(double), value(Number), nullValue(), flush(), close(), string(String value), beforeName(), alwaysCreatesValidJsonNumber(), newLine() and beforeValue() have missing branches
- setLenient() is not tested at all, however, it's deprecated.
- setIndent(), getFormattingStyle(), isLenient(), getStrictness(), isHtmlSafe(), getSerializeNulls(), value(boolean), value(Boolean), and jsonValue() are not tested at all.

#### GPT-OSS Single
The Generated file contained 2 errors:
- package statement
- wrong name for an import
- missing method declaration line for three functions!
- the last test required converting object passed to assertThrows and assertDoesNotThrow to be effectively final. I don't really understand this, but there was a quick fix for it.

After fixing all errors, 3 tests passed and 4 tests failed. Failed tests are:
- in testStringValueEscaping, expected string was wrong.
- in testNullSerialization(), first an object must begin to write a name to the writer. Then, the expected assertion was wrong. This happened for both parts of this test.
- in testObjectScope(), exception message assertion is wrong. The assertion is commented.
- in testHTMLSafeCharacters, I just changed the last assertion to AssertNotEqual. I couldn't figure out how to fix it.

list of tests:
1. write string value with escaping
2. null value respects serializeNulls flag
3. begin/end object writes correct delimiters and throws on mismatched close
4. write float NaN throws when not lenient
5. write float NaN allowed when lenient
6. html safe characters are escaped when htmlSafe true
7. multiple top-level values allowed only in LENIENT mode

These tests had so many errors. It took me a some time to fix all of them, in order to run them.

Missing coverage:
- beforeName(), beforeValue(), closeScope(int, int, char), flush(), name(String), newline(), peek(), push(int), setFormattingStyle(FormattingStyle), string(String), value(String), value(float), and nullValue() have missing branches.
- setLenient(boolean) is not tested, but it's deprecated.
- alwaysCreatesValidJsonNumber(Class), close(), endArray(), getFormattingStyle(), getSerializeNulls(), getStrictness(), isHtmlSafe(), isLenient(), jsonValue(String), setIndent(String), value(Boolean), value(boolean), value(double), and value(Number)  were not tested at all.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      1       |       2       |       2       |       2        |
| Statement coverage             |     60%      |      83%      |      66%      |      56%       |
| Decision coverage              |     33%      |      67%      |      43%      |      31%       |
| total tests                    |      4       |      30       |       6       |       7        |
| failed tests                   |      2       |       7       |       1       |       4        |
| method names start with test   |     yes      |      no       |      no       |      yes       |
| private variables used         |      no      |      yes      |      no       |       no       |
| tokens used                    |     9961     |    24,421     |     7,192     |     23,264     |
| tokens per decision coverage % |     302      |      364      |      167      |      750       |
In single file, GPT-OSS was better. but in grouped file, Gemma4 was better. Both winners also consumed less token. Overall, Gemma4 Single is winner if user is willing to trade 2.5x token for a significant better performance.

### JsonReader
This file has 3045 statements and 519 decisions. It is the largest file of the project.
#### Gemma Group
The Generated file contained an error. In testLenientReading(), code was asserting a return value from *close()* which returns nothing. Replaced with *reader.nextLong()* since the array still had one more value end.

After fixing the error, all 3 tests passed.

list of tests:
1. reading various tokens
2. lenient reading
3. malformed json throws IOException

Just three tests are generated for the largest file of the project. As a result, coverage is low.

Missing coverage:
- setLenient(boolean) is not tested at all, however, it's deprecated.
- beginArray(), beginObject(), checkLenient(), consumeNonExecutePrefix(), doPeek(), endArray(), fillBuffer(int), getPath(boolean), hasNext(), isLiteral(char), nextBoolean(), nextLong(), nextName(), nextNonWhitespace(boolean), nextNull(), nextQuotedValue(char), nextString(), peekKeyword(), peekNumber(), push(int), and endObject() have missing branches.
- getNestingLimit(), getPreviousPath(), getStrictness(), isLenient(), nextDouble(), nextUnquotedValue(), peek(), readEscapeCharacter(), setNestingLimit(int), skipQuotedValue(char), skipTo(String), skipToEndOfLine(), skipUnquotedValue(), skipValue(), toString(), unexpectedTokenError(String), validateAscii(String), and nextInt() are not tested at all.


#### Gemma Single
The Generated file contained 2 errors:
- The Agent realized it was initializing a class that doesn't exist, but forgot to remove it.
- Non-exising JsonToken.NUMBER_STRING_CONVERSION_TESTING is called but not used.

After fixing all errors, 13 tests passed and 4 tests failed. Failed tests are:
- in hasNext_returnsFalseOnEof(), actually an IOException is thrown. Replaced the assertion with IOException.
- in nextName_returnsName(), for a json object, only the name is received, and the value is ignored. Therefore, an exception is thrown when endObject() is called.
- in close_closesReader(), nothing is thrown since calling close() for StringReader multiple times won't throw exception. Instead, placed read() in the assertion.
- in skipValue_skipsValues(), one additional name-value pair is read, which throws exception.

list of tests:
1. beginArray should throw IllegalStateException if token is not BEGIN_ARRAY
2. endArray should throw IllegalStateException if token is not END_ARRAY
3. beginObject should throw IllegalStateException if token is not BEGIN_OBJECT
4. endObject should throw IllegalStateException if token is not END_OBJECT
5. hasNext should return false on EOF
6. peek should return the correct token for various JSON types
7. nextString should return string value
8. nextBoolean should return boolean value
9. nextNull should consume null token
10. nextLong should return long value
11. nextDouble should return double value
12. nextName should return property name
13. setNestingLimit should throw exception for negative limit
14. close should close the underlying reader
15. setStrictness should work correctly
16. skipValue should skip various values
17. toString should return class name and location

Even 17 tests is insufficient for a large file with over 1000 LOC.

Missing coverage:
- setLenient(boolean) is not tested, however, it's deprecated.
- beginArray(), beginObject(), doPeek(), endArray(), fillBuffer(int), getPath(boolean), hasNext(), isLiteral(char), nextBoolean(), nextDouble(), nextLong(),nextName(), nextNonWhitespace(boolean), nextNull(), nextQuotedValue(char), nextString(), peek(), peekKeyword(), peekNumber(), push(int), setNestingLimit(int), skipQuotedValue(char), skipValue(), unexpectedTokenError(String), and endObject() have missing branches.
- checkLenient(), consumeNonExecutePrefix(), getNestingLimit(), getPreviousPath(), getStrictness(), nextUnquotedValue(), readEscapeCharacter(), skipToEndOfLine(), skipTo(String), skipUnquotedValue(), syntaxError(String), validateAscii(String), and nextInt() are not tested at all.

#### GPT-OSS Group
The Generated file contained 1 error which was missing import of Strictness.

After fixing the error, 4 tests passed and 1 test failed. In invalidJson(), first, object must be read then nextName() will throw exception for the malformed json.

list of tests:
1. parses numbers as long and double
2. parses booleans case-insensitively in lenient mode
3. rejects NaN/Infinity when not lenient
4. handles nested arrays and objects correctly
5. throws on invalid JSON when strict

5 tests for a mammoth of a file is not enough.  
A utility method to instantiate JsonReader is created. The Junit way is to have a private JsonReader that is instantiated with a *@BeforeEach* method.

Missing coverage:
- setLenient(boolean) is not tested, however, it's deprecated.
- beginArray(), beginObject(), checkLenient(), consumeNonExecutePrefix(), doPeek(), endArray(), endObject(), fillBuffer(int), getPath(boolean), nextBoolean(), nextDouble(), nextLong(), nextName(), nextNonWhitespace(boolean), nextQuotedValue(char), nextInt(), peek(), peekKeyword(), peekNumber(), push(int), and isLiteral(char) have missing branches.
- close(), getNestingLimit(), getPreviousPath(), getStrictness(), hasNext(), isLenient(), nextNull(), nextString(), nextUnquotedValue(), setNestingLimit(int), skipQuotedValue(char), skipTo(String), skipToEndOfLine(), skipUnquotedValue(), skipValue(), toString(), unexpectedTokenError(String), validateAscii(String), and readEscapeCharacter() are not tested at all.

#### GPT-OSS Single
The Generated file contained 4 errors:
- missing package statement.
- placing '"' inside a string without a backslash behind it. Three times!

After fixing all errors, 2 tests passed and 7 tests failed. Failed tests are:
- in beginArraySuccess(), an array with three integers is tested but only one of them is read and endArray() is called which throws exception. Fixed by reading two other integers and also asserting them.
- in strictnessToggling(), the exception thrown is IllegalStateException, not IOException. It also needed a beginArray() before the second assertion.

Like GPT-OSS group, a utility method for instantiating JsonReader was generated.  
9 tests are too low to achieve a good coverage.

list of tests:
1. constructor throws NPE when the Reader is null
2. setStrictness throws NPE for null argument
3. setNestingLimit negative throws IllegalArgumentException
4. beginArray successfully reads array start and end
5. beginArray on non-array throws IllegalStateException
6. beginObject successfully reads object start and end
7. hasNext returns false after consuming all elements
8. peek returns correct token types for literals
9. strictness can be toggled between LEGACY_STRICT and LENIENT
10. skipValue correctly skips nested structure
11. exceeding the nesting limit throws MalformedJsonException

Missing coverage:
- beginArray(), beginObject(), checkLenient(), consumeNonExecutePrefix(), doPeek(), endObject(), fillBuffer(int), getPath(boolean), hasNext(), isLiteral(char), nextBoolean(), nextInt(), nextName(), nextNonWhitespace(boolean), nextNull(), nextQuotedValue(char), nextString(), nextUnquotedValue(), peek(), peekKeyword(), peekNumber(), push(int), skipQuotedValue(char), skipValue(), unexpectedTokenError(String), and endArray() have missing branches.
- close(), getNestingLimit(), getPreviousPath(), getStrictness(), isLenient(), nextDouble(), nextLong(), setLenient(boolean), skipTo(String), skipToEndOfLine(), skipUnquotedValue(), syntaxError(String), toString(), validateAscii(String), and readEscapeCharacter() are not tested at all.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      1       |       2       |       1       |       4        |
| Statement coverage             |     37%      |      44%      |      35%      |      43%       |
| Decision coverage              |     25%      |      34%      |      26%      |      34%       |
| total tests                    |      3       |      17       |       5       |       11       |
| failed tests                   |      0       |       4       |       1       |       2        |
| method names start with test   |     yes      |      no       |      no       |       no       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    31,070    |    42,965     |    24,678     |     27,351     |
| tokens per decision coverage % |    1,243     |     1,264     |      949      |      804       |
All models did terrible. It is evident that these small models can't attain a high coverage with tests generated in a single prompt for large files like this. GPT-OSS virtually performed as good as Gemma4 but with a significant token consumption.

### JsonTreeReader
This file has 891 statements and 125 decisions.
#### Gemma Group
The Generated file contained 1 error. Path to import junit was incorrect.

After fixing the error, all 2 tests passed.

list of tests:
1. Read from JsonElement
2. Read from JsonArray

This files contains more than a thousand statements and decisions, yet just two tests are generated.

Missing coverage:
- endObject(), expect(Json Token), nextLong(), nextName(boolean), nextNull(), nextString(), push(Object), and peek() have missing branches.
- close(), getPath(), getPreviousPath(), hasNext(), isAllAscii(String), locationString(), nextBoolean(), nextDouble(), nextInt(), nextJsonElement(), numberFormatException(String, NumberFormatException), promoteName ToValue(), skipValue(), toString(), validateAscii(String), and getPath(boolean) were not tested at all.


#### Gemma Single
The Generated file contained 6 errors:
- in peekIteratorArray() and peekClosed(), the agent realized JsonTreeReader initialization is wrong and initiated another instant correctly, but left the wrong code.
- in arrayStructural(), a JsonArray is instantiated wrongly.
- in skipValue(), JsonArray, JsonElements, and null are added to a JsonObject wrongly.

After fixing all errors, 13 tests passed and 6 tests failed. Failed tests are:
- in peekEmpty(), created file is not empty, in fact it must read the null first, then it will reach *END_DOCUMENT* token.
- in peekIteratorArray(), the first expected assertion is *BEGIN_ARRAY* token. In addition, array must begin the array in order to read the values inside the array.
- in peekIteratorObject(), the first expected assertion is *BEGIN_OBJECT* token. Then, object must begin, and value must also be read after the key is read.
- in arrayStructural() and objectStructural(), the model also made a mistake in token order and traversal.
- in pathTests(), agent did not traverse the object to get to the expected path.

list of tests:
1. peek empty reader returns END_DOCUMENT
2. peek JsonObject returns BEGIN_OBJECT
3. peek JsonArray returns BEGIN_ARRAY
4. peek JsonPrimitive returns correct token
5. peek JsonNull returns NULL
6. peek Iterator (Array) returns next element or END_ARRAY
7. peek Iterator (Object) returns NAME or END_OBJECT
8. peek closed reader throws IllegalStateException
9. nextString with String and Number
10. nextBoolean
11. nextNull
12. nextDouble with Number and String
13. nextDouble handles NaN and Infinity (non-lenient)
14. nextLong and nextInt with Number and String
15. beginArray and endArray
16. beginObject and endObject
17. skipValue cases
18. isAllAscii
19. getPath and getPreviousPath

As we can see, the model made so many errors in token order.

Missing coverage:
- endArray(), endObject(), getPath(boolean), nextBoolean(), nextDouble(), nextInt(), nextLong(), nextName(boolean), nextNull(), push(Object), skipValue(), validateAscii(String), and peek() have missing branches.
- getPreviousPath(), hasNext promoteName ToValue(), numberFormatException(String, NumberFormatException), toString(), and nextJsonElement() are not tested at all.

#### GPT-OSS Group
The Generated file contained 5 errors:
- missing IOException import.
- missing JsonToken import.
- missing MalformedJsonException import.
- in readSimpleStructure(), agent called getClass() on a primitive type.
- in unsupportedElement(), agent tried instantiating an abstract class. Instead, I created a class with no implementation inheriting that class for the expected result.

After fixing all errors, 2 tests passed and 1 test failed. In promoteName(), a key is read, therefore, tree has traversed and *promoteNameToValue()* throws exception. A *nextName* and an assertion are removed to fix this.

list of tests:
1. read a simple element tree
2. promotes name to a value when requested
3. throws on unsupported JsonElement subtype

A utility method for instantiating JsonTreeReader was generated. Although, there are JUnit alternatives for this.

Missing coverage:
- endArray(), expect(Json Token), getPath(boolean), nextInt(), nextName(boolean), push(Object), nextString(), and peek() have missing branches.
- nextLong(), close(), getPath(), getPreviousPath(), hasNext(), isAllAscii(String), locationString(), nextBoolean(), nextDouble(), nextJsonElement(), nextNull(), numberFormatException(String, NumberFormatException), skipValue(), toString(), validateAscii(String), and getPath(boolean) are not covered at all.

#### GPT-OSS Single
The Generated file contained 4 errors:
- missing package statement.
- wrong path for importing JsonToken.
- missing unhandled exceptions. Changed method signatures to throw the exception.
- implementation of CustomElement was not complete.

After fixing all errors, 8 tests passed and 3 tests failed. Failed tests are:
- in nextDoubleTest() last assertion, NumberFormatException is thrown not IOException.
- in skipValueTest(), *skipValue* must be called twice before the first assertion.
- in getPathTest(), two first assertions expected the wrong string. Appending "." to end of the expected strings fix it.

list of tests:
1. peek detects JsonObject, JsonArray, JsonPrimitive, JsonNull, and custom element
2. nextString handles STRING and NUMBER tokens
3. nextBoolean reads BOOLEAN token
4. nextNull reads NULL token
5. nextDouble accepts NUMBER and STRING, rejects others
6. nextLong accepts NUMBER and STRING with ascii, rejects others
7. nextInt accepts NUMBER and STRING with ascii, rejects others
8. skipValue handles NAME, END_ARRAY, END_OBJECT, default cases
9. promoteNameToValue pushes key as JsonPrimitive
10. getPath reflects current stack location
11. isAllAscii correctly detects non-ASCII characters

There is a private JsonTreeReader with a *@BeforeEach* to instantiate it, but the agent didn't use it.

Missing coverage:
- endArray(), endObject(), expect(Json Token), getPath(boolean), nextBoolean(), nextDouble(), nextLong(), nextNull(), nextString(), nextInt(), push(Object),skip Value(), and peek() have missing branches.
- close(), getPreviousPath(), hasNext(), toString(), and nextJsonElement() are not tested at all

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      1       |       6       |       5       |       4        |
| Statement coverage             |     39%      |      63%      |      41%      |      68%       |
| Decision coverage              |     25%      |      57%      |      26%      |      61%       |
| total tests                    |      2       |      19       |       3       |       11       |
| failed tests                   |      0       |       6       |       1       |       3        |
| method names start with test   |     yes      |      no       |      no       |       no       |
| private variables used         |      no      |      no       |      no       |      yes       |
| tokens used                    |    9,079     |    19,706     |     7,211     |     17,113     |
| tokens per decision coverage % |     363      |      346      |      277      |      281       |
Conspicuously, GPT-OSS generated tests are better. In both single and group, with less token consumption, they made more coverage. This was a medium-size file, and it looks like it is better to generate test for it without other files presence.

### JsonTreeWriter
This file has 364 statements and 56 decisions.
#### Gemma Group
The Generated file contained no error.

The single generated test failed. The model tried to receive the result object after it closed the writer.

list of tests:
1. write to json element

One single test won't achieve a satisfactory coverage.

Missing coverage:
- close(), endArray(), endObject(), get(), name(String), value(String), and put(JsonElement) have missing branches.
- flush(), jsonValue(String), nullValue(), value(Boolean), value(boolean), value(double), value(float), and value(Number) are not tested at all


#### Gemma Single
The Generated file contained no errors.

17 tests passed and a test failed. In closeBehavior(), *assertNotNull* must be called before *writer.close()*.

list of tests:
1. Write primitive string
2. Write primitive boolean
3. Write primitive long
4. Write null value
5. Write number (double/float)
6. Write simple object
7. Write object with null value
8. Throws exception when name is null
9. Throws exception when name used without object
10. Throws exception when endObject called without error
11. Write simple array
12. Nested array and object
13. Throws exception when endArray is invalid
14. Throws exception when get() called with stack not empty
15. Verify NaN and Infinity handling (non-lenient)
16. Throws exception on jsonValue call
17. Verify closing behavior
18. Parametrized Handle nulls in value overrides

Missing coverage:
- endArray(), endObject(), name(String), put(JsonElement), value(float), and value(double) have missing branches.
- flush(), value(Boolean), and value(Number) are not tested at all

#### GPT-OSS Group
The Generated file contained a single error which was not importing IOException.

After fixing the error, 1 test passed and 3 tests failed. In buildArray(), objectWithNames(), and nestedStructure(), *JsonTreeWriter.get()* is called after *JsonTreeWriter.close()*. Fixed my moving *close()* call after the *get()* call.

list of tests:
1. builds a simple array element
2. handles name/value pairs correctly
3. throws exception if endArray is called in the wrong context
4. supports nested structures

Four tests for a moderate-size file is inadequate.

Missing coverage:
- close(), endObject(), get(), name(String), put(JsonElement), value(String), and endArray() have missing branches.
- flush(), jsonValue(String), nullValue(), value(Boolean), value(boolean), value(double), value(float), and value(Number) are not tested at all

#### GPT-OSS Single
The Generated file contained 2 errors:
- missing package statement.
- missing import of IOException.

After fixing all errors, 11 tests passed and 2 tests failed. Failed tests are:- failed test 1

list of tests:
1. in testValueNumber(), a variable is initialized wrongly and the agent realized it, still it forgot to clean it.
2. in testClosedProductImmutability(), JsonTreeWriter.get() is called after JsonTreeWriter.close() which throws exception. The exception is asserted instead.

Missing coverage:
- endObject(), value(Boolean), value(Number), and value(String) have missing branches.
- flush(), jsonValue(String), and value(Number) are not tested at all

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      0       |       0       |       1       |       2        |
| Statement coverage             |     56%      |      80%      |      57%      |      83%       |
| Decision coverage              |     32%      |      58%      |      33%      |      67%       |
| total tests                    |      1       |      18       |       4       |       13       |
| failed tests                   |      1       |       1       |       3       |       2        |
| method names start with test   |     yes      |      no       |      no       |      yes       |
| private variables used         |      no      |      yes      |      no       |       no       |
| tokens used                    |    5,094     |    14,105     |     4,046     |     12,866     |
| tokens per decision coverage % |     159      |      243      |      123      |      192       |
GPT-OSS is the winner again. Not only is coverage higher, but also less token used.

### JsonStreamParser
This file has 75 statements and 4 decisions.
#### Gemma Group
The Generated file contained an error which was wrong package statement.

After fixing the error, the single test passed.

list of tests:
1. parse multiple elements

Interestingly, with just a single test, a surprising coverage was achieved.

Missing coverage:
- next() and hasNext() have missing branches.
- remove() was not tested at all.


#### Gemma Single
The Generated file contained an error which was a creation of a dummy class that inherited from final class JsonStreamParser.

After fixing the error, 3 tests passed and 3 tests failed. Failed tests are:
- in nextThrowsExceptionWhenEmpty(), when *parser.hasNext()* is called, *JsonIOException.class* is thrown. In the next assertion, also *JsonIOException.class* is thrown.
- in hasNextReturnsFalseForEmptyInput(), last test mistake also happened. This test is completely redundant.
- in hasNextThrowsJsonSyntaxExceptionOnMalformedJson, a weird Gson behavior is seen. While *hasNext* does not throw exception, *next* does! This is potentially another Gson fault.

list of tests:
1. next() returns JsonElement when elements are present
2. next() throws NoSuchElementException when no elements are available
3. hasNext() returns false for empty input
4. remove() throws UnsupportedOperationException
5. hasNext() throws JsonSyntaxException when JSON is malformed
6. hasNext() throws JsonIOException when IOException occurs

Missing coverage:
- next() and hasNext() have missing branches.

#### GPT-OSS Group
The Generated file contained an error which was not importing *NoSuchElementException*.

After fixing the error, all 3 tests passed.

list of tests:
1. parses multiple JSON elements in a single string
2. throws NoSuchElementException when no more elements
3. is thread-safe when synchronized

Without considering exceptions, full coverage attained.

Missing coverage:
- hasNext() and next() have missing statements which all are exceptions.
- remove() is not tested at all, which also just throws exception.

#### GPT-OSS Single
The Generated file contained an error which was missing package statement.

After fixing the error, 3 tests passed and 3 tests failed. Failed tests are:
- in hasNextReturnsTrueUntilEnd(), an array is given. The agent didn't realize that the array is only counted a single element.
- in hasNextFalseWhenDocumentComplete(), another quark is seen. While the test as expecting false on *hasNext()* call, exception is thrown for the empty json file.
- The gson weird behavior mentioned in Gemma4 Single happened in hasNextThrowsJsonSyntaxExceptionOnMalformedInput().

list of tests:
1. has next() return true until end
2. has next() false when document completed
3. next() returns correct element
4. next() throws NoSuchElementException when none
5. has next throws JsonSyntaxException on malformed input
6. remove() ThrowsUnsupportedOperationException

Missing coverage:
- next() and hasNext() have statements which are exceptions.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      1       |       1       |       1       |       1        |
| Statement coverage             |     64%      |      77%      |      69%      |      82%       |
| Decision coverage              |     75%      |      75%      |     100%      |      100%      |
| total tests                    |      1       |       6       |       3       |       6        |
| failed tests                   |      0       |       3       |       0       |       3        |
| method names start with test   |     yes      |      no       |      no       |       no       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    1,236     |    12,151     |      982      |     11,354     |
| tokens per decision coverage % |      16      |      162      |      10       |      113       |
This was a small file, so all agents did well on coverage. GPT-OSS is again winner, performing better even with less token consumption. 11,354 tokens to achieve a very high coverage is reasonable for this file.

## Group 3: Reflection & Type System
# TODO:
Gemini single starting printing "Decision-making error:" non-stop! Also, ReflectionHelper started printing nonsense. Also, in the second attempt, it stopped after planning the test in its thoughts, then it stopped, and I gave it "continue" but it stopped after 29 seconds, so I just started a new session and third time the charm it was.  
GPT_single generated some other test classes for ReflectionAccessFilter.
### TypeToken
This file has 689 statements and 100 decisions.
#### Gemma Group
The Generated file contained 2 errors:
- in testGetType(), *getRawType()* cannot be called after getType().
- in testGetArray(), token must be of TypeToken class.

After fixing all errors, all 7 tests passed.

list of tests:
1. getRawType()
2. getType()
3. equals()
4. hashCode()
5. getParameterized()
6. getArray()
7. get()

Missing coverage:
- isAssignableFrom(Class), isAssignableFrom(Type), and isAssignableFrom(TypeToken) are not tested at all. However, they are deprecated methods.
- getType TokenTypeArgument(), isAssignableFrom(Type, ParameterizedType, Map), isCapturingTypeVariablesForbidden(), verifyNoTypeVariable(Type), getParameterized(Type, Type[]), and equals(Object) have missing branches.
- buildUnsupportedTypeException(Type, Class[]), get(Type), isAssignableFrom(Type, GenericArrayType), matches(Type, Type, Map), typeEquals(ParameterizedType, ParameterizedType, Map), and), and isAssignableFrom(Type, ParameterizedType, Map) are not tested at all.

#### Gemma Single
The Generated file contained 2 errors:
- in testToString(), a non-working dummy type is used.
- in testGetParameterizedInvalidRawType(), the invalid type was initialized incorrectly.

After fixing all errors, 12 tests passed and 3 tests failed. Failed tests are:
- in testIsAssignableFromClass(), the second assertion is also true. Agent realized this too.
- in testIsAssignableFromType(), it looks like the model didn't fix the issue that had realized.
- in testNonDirectSubclass(), agent is testing a class created in the file itself?!

list of tests:
1. basic TypeToken construction and properties
2. TypeToken equals and hashCode
3. toString
4. get(Class) method
5. get(Type) method
6. getArray method
7. getParameterized method with valid arguments
8. getParameterized throws exception for invalid rawType
9. getParameterized throws exception for argument mismatch
10. isAssignableFrom with Class
11. isAssignableFrom with Type
12. capturing type variables error
13. IllegalStateException for raw TypeToken
14. IllegalStateException for raw TypeToken subclass
15. IllegalStateException for non-direct subclass

In two methods, the agent decided to fix its issues, but it didn't fix them in the final result.  
Also, testCapturingTypeVariablesError() and testRawTypeTokenThrows() have empty body.  
Five utility classes are created. Two of them were not used.

Missing coverage:
- isAssignableFrom(Type) has missing branches, and isAssignableFrom(TypeToken) is not tested at all. However, they are deprecated methods.
- getTypeTokenTypeArgument(), isCapturingTypeVariablesForbidden(), verifyNoTypeVariable(Type), and getParameterized(Type, Type[]) have missing branches.
- buildUnsupportedTypeException(Type, Class[]), get(Type), isAssignableFrom(Type, GenericArrayType), matches(Type, Type, Map), typeEquals(ParameterizedType, ParameterizedType, Map), and isAssignableFrom(Type, ParameterizedType, Map) are not tested at all.

#### GPT-OSS Group
The Generated file contained an error. It was wrong import path for GsonTypes.

After fixing the error, 2 tests passed and 6 tests failed. Failed tests are:
- in typeTokenArray(), the last assertion does a wrong casting.
- in captureTypeVariableForbidden(), nothing is thrown.

list of tests:
1. TypeToken of a parameterized type
2. TypeToken of an array type
3. getParameterized returns correct type
4. getParameterized with wrong #args throws
5. getParameterized on non-generic class
6. capturing type variable is forbidden
7. equals and hashCode honor underlying Type
8. toString delegates to GsonTypes#typeToString

Missing coverage:
- isAssignableFrom(Class) and isAssignableFrom(Type) are not tested at all. However, they are deprecated methods.
- equals(Object), getTypeTokenTypeArgument(), isCapturingTypeVariablesForbidden(), verifyNoTypeVariable(Type), and getParameterized(Type, Type[]) have missing branches.
- buildUnsupportedTypeException(Type, Class[]), get(Type), getArray(Type), isAssignableFrom(Type Token), isAssignableFrom(Type, GenericArrayType), matches(Type, Type, Map), typeEquals(ParameterizedType, ParameterizedType, Map), and isAssignableFrom(Type, ParameterizedType, Map) are not tested at all.

#### GPT-OSS Single
The Generated file contained 5 errors:
- missing import for List.
- in defaultConstructorCapturesType(), second assertion had a wrong method chain.
- in getParameterized_nonClassRawType(), there is a placeholder that caused error. fixed by implementing an inner class.
- in assignableParameterizedSame(), had to change listString1 and listString2 to type TypeToken<?>, instead of  TypeToken<String>. I don't think getParameterized() returns TypeToken<?>.
- in assignableGenericArray(), had to change the TypeToken<...> to TypeToken<?>.
- for equalsAndHashCode(), did what the same thing in the previous error.

After fixing all errors, 10 tests passed and 4 tests failed. Failed tests are:
- in getParameterized_ownerTypeRequired(), exception is not thrown.
- in getArray_success(), two sides of an assertEquals were not equal.
- in assignableWildcard(), the assertion was incorrect.
- in toStringDelegate(), the expected string was wrong.

list of tests:
1. default constructor captures generic type correctly
2. getParameterized() creates parameterized type for Map
3. getParameterized() throws when rawType is not a Class
4. getParameterized() throws when argument count mismatched
5. getParameterized() delegates to get(Class) for non-generic class
6. getParameterized() throws when owner type is required
7. getParameterized() throws when type argument does not satisfy bounds
8. getArray() creates array type token and returns the correct raw type
9. isAssignableFrom() works for the same raw type
10. isAssignableFrom() handles parameterized types with same parameters
11. isAssignableFrom() handles wildcard upper bounds
12. isAssignableFrom() handles generic array types
13. equals() and hashCode() are consistent across equivalent tokens
14. toString() delegates to GsonTypes.typeToString

Missing coverage:
- isAssignableFrom(Class) is not tested at all, and isAssignableFrom(Type) has missing branches. However, these are deprecated methods.
- equals(Object), getTypeTokenTypeArgument(), isAssignableFrom(Type, GenericArrayType), isAssignableFrom(Type, ParameterizedType, Map), isCapturingTypeVariablesForbidden(), matches(Type, Type, Map), typeEquals(ParameterizedType, ParameterizedType, Map),verifyNoTypeVariable(Type), and getParameterized(Type, Type[]) have missing branches.
- buildUnsupportedTypeException(Type, Class[]) and get(Type) are not tested at all.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      2       |       2       |       1       |       5        |
| Statement coverage             |     29%      |      40%      |      40%      |      67%       |
| Decision coverage              |     21%      |      33%      |      32%      |      58%       |
| total tests                    |      7       |      15       |       8       |       14       |
| failed tests                   |      0       |       3       |       2       |       4        |
| method names start with test   |     yes      |      yes      |      no       |       no       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    9,465     |    17,448     |     8,508     |     15,616     |
| tokens per decision coverage % |     451      |      529      |      266      |      270       |
Summary: This is a quite complicated file. Only GPT-OSS Single generated test is somewhat satisfactory, and it's the best option even considering the cost.

### ConstructorConstructor
This file has 412 statements and 72 decisions.

#### Gemma Group
The Generated file contained no error:

all 5 tests passed.

list of tests:
1. check instantiable
2. get instance creator constructor
3. get default constructor
4. new collection constructor array list
5. new map constructor linked hash map

A dummy class and interface were created.

Missing coverage:
- get(TypeToken, boolean), newDefaultConstructor(Class, ReflectionAccessFilter.FilterResult), and newSpecialCollectionConstructor(Type, Class) have missing branches.
- toString(), newDefaultImplementationConstructor(Class), newMapConstructor(Class), newUnsafeAllocator(Class), and newCollectionConstructor(Class) are not tested at all.

#### Gemma Single
The Generated file contained 4 errors:
- missing import for HashMap.
- Wrongly generated text. Correct values were "reflect", "HashMap", and "assertNotNull".

After fixing all errors, 9 tests passed and 2 tests failed. Failed tests are:
- in get_throws_when_unsafe_disabled() and get_throws_when_blocked(), exception is not thrown.

list of tests:
1. checkInstantiable: return null for concrete class
2. checkInstantiable: return error for interface
3. checkInstantiable: return error for abstract class
4. get: return InstanceCreatorConstructor when type match found
5. get: return InstanceCreatorConstructor when raw type match found
6. get: handle EnumSet with valid parameterized type
7. get: handle EnumMap with valid parameterized type
8. get: handle ArrayList via default implementation
9. get: handle LinkedHashMap via default implementation
10. get: throw exception when allowUnsafe is false and no creator available
11. get: throw exception when reflect access is blocked

It was the first time ever that I saw an AI agent generate names wrongly.  
Some utility classes, interfaces, etc. are generated.

Missing coverage:
- get(TypeToken, boolean), newDefaultConstructor(Class, ReflectionAccessFilter.FilterResult), and newSpecialCollectionConstructor(Type, Class) have missing branches.
- newDefaultImplementationConstructor(Class), newMapConstructor(Class), newUnsafeAllocator(Class), toString(), and newCollectionConstructor(Class) are not tested at all.

#### GPT-OSS Group
The Generated file contained 3 errors:
- in rawTypeInstanceCreator(), defaultListConstructor(), and defaultMapConstructor(), had to cast some assigned values.

After fixing all errors, 8 tests passed and aa test failed. Failed tests was:
- in rawTypeInstanceCreator(), had to pass and extra argument to getParameterized() which was *Object.class*.

list of tests:
1. instanceCreator wins over type creator
2. raw type instanceCreator used when subclass creator missing
3. EnumSet special constructor
4. EnumMap special constructor
5. default constructor for plain class
6. default implementation for List
7. default implementation for Map
8. Throws for abstract class
9. Throws for interface


A utility class, interface, and enum were created.  
A helper method to create a ConstructorConstructor was created.

Missing coverage:
- checkInstantiable(Class), newCollectionConstructor(Class), newDefaultConstructor(Class, ReflectionAccessFilter.FilterResult), newMapConstructor(Class), newSpecialCollectionConstructor(Type), and get(TypeToken, boolean) have missing branches.
- newUnsafeAllocator(Class) and toString() are not tested at all.


#### GPT-OSS Single
The Generated file contained 4 errors. The agent was trying to instantiate TypeToken in two methods, which has a private constructor. Couldn't find a proper way to fix this. The other errors are fixed. An import was required and ReflectionAccessFilter had to be adjusted.

6 tests passed and 3 tests failed. Failed tests are:
- in interface_checkInstantiable_throws(), msg is actually null.
- in inaccessibleConstructor_noUnsafe(), exception is not thrown.
- in instanceCreator_forRawType(), AI failed to construct a Number.

list of tests:
1. Interface returns proper error message
2. Abstract class returns proper error message
3. Concrete class with public no-args constructor
4. Private no-args constructor is made accessible
5. Non-accessible constructor without allowUnsafe throws
6. InstanceCreator provided for type
7. InstanceCreator provided for raw type
8. Allows unsafe when filter allows and useJdkUnsafe true
9. BLOCK_ALL filter prevents unsafe and throws

Missing coverage:
- newUnsafeAllocator(Class), get(TypeToken, boolean), newDefaultConstructor(Class, ReflectionAccessFilter.FilterResult), newDefaultImplementationConstructor(Class), newSpecialCollectionConstructor(Type, Class), and checkInstantiable(Class) have missing branches.
- get(TypeToken), toString(), and newMapConstructor(Class) are not tested at all.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      0       |       4       |       3       |       4        |
| Statement coverage             |     31%      |      45%      |      53%      |      41%       |
| Decision coverage              |     22%      |      38%      |      44%      |      37%       |
| total tests                    |      5       |      11       |       9       |       9        |
| failed tests                   |      0       |       2       |       1       |       3        |
| method names start with test   |     yes      |      no       |      no       |       no       |
| private variables used         |      no      |      no       |      no       |      yes       |
| tokens used                    |    10,257    |    18,790     |     9,219     |     15,258     |
| tokens per decision coverage % |     466      |      494      |      210      |      412       |
Reflection is very complicated. AI agents are not able to create tests that can attain high coverage. GPT-OSS Group had the best performance here even with the lowest token cost.

### GsonTypes
This file has 886 statements and 166 decisions.
#### Gemma Group
The Generated file contained no error.

All 5 tests passed.

list of tests:
1. getRawType()
2. equals()
3. typeToString()
4. getArrayComponentType()
5. getMapKeyAndValueTypes()

5 tests for such a large file is inadequate.

Missing coverage:
- canonicalize(Type), equals(Type, Type), getArrayComponentType(Type), getMapKeyAndValueTypes(Type, Class), getRawType(Type), requiresOwnerType(Type), typeToString(Type), and checkNotPrimitive(Type) have missing branches.
- GsonTypes(), arrayOf(Type), declaringClassOf(TypeVariable), getCollectionElementType(Type, Class), getGenericSupertype(Type, Class, Class), getSupertype(Type, Class, Class), indexOf(Object[], Object), newParameterizedTypeWithOwner(Type, Class, Type[]), resolveTypeVariable(Type, Class, TypeVariable), subtypeOf(Type), supertypeOf(Type), resolve(Type, Class, Type), and resolve(Type, Class, Type, Map) are not tested at all.

#### Gemma Single
The Generated file contained a single error:
- in equals_comparesTypesCorrectly(), there is a statement *assertNull(null, null)*!

After fixing the error, all 9 tests passed.

list of tests:
1. newParameterizedTypeWithOwner creates a new parameterized type
2. arrayOf creates a generic array type
3. subtypeOf handles Class and WildcardType
4. supertypeOf handles Class and WildcardType
5. canonicalize handles Class, ParameterizedType, GenericArrayType, and WildcardType
6. getRawType returns correct raw type
7. equals handles various type comparisons
8. typeToString returns correct name
9. getArrayComponentType returns correct component type

Missing coverage:
- checkNotPrimitive(Type), equals(Type, Type), getRawType(Type), requiresOwnerType(Type), subtypeOf(Type), supertypeOf(Type), typeToString(Type), and canonicalize(Type) have missing branches.
- GsonTypes(), declaringClassOf(TypeVariable), getCollectionElementType(Type, Class), getGenericSupertype(Type, Class, Class), getMapKeyAndValueTypes(Type, Class), getSupertype(Type, Class, Class), indexOf(Object[], Object), resolve(Type, Class, Type), resolveTypeVariable(Type, Class, TypeVariable), and resolve(Type, Class, Type, Map) are not tested at all.

#### GPT-OSS Group
The Generated file contained 3 errors:
- in rawTypeForGenericArray(), *TypeToken.arrayOf* is called which doesn't exist. *GsonTypes.arrayOf* fixed it.
- in resolveTypeVariable(), the same thing as above happened, for *TypeToken.subtypeOf*.
- in resolveTypeVariable(), *ParameterizedType pt* was not correctly instantiated.

After fixing all errors, 6 tests passed and 3 tests failed. Failed tests are:
- in resolveTypeVariable(), the last assertion is wrong, could be effect of me fixing the error.
- in collectionElementType(), the second assertion was incorrect.
- in requiresOwnerType, the assertion is wrong.

list of tests:
1. getRawType works for Class
2. getRawType handles ParameterizedType
3. getRawType for GenericArrayType
4. getRawType for TypeVariable mapped to bound
5. equals compares parameterized types
6. resolve type variable mapping
7. getCollectionElementType
8. getMapKeyAndValueTypes
9. requiresOwnerType returns true for inner class

Two helper classes are created.

Missing coverage:
- canonicalize(Type), declaringClassOf(TypeVariable), equals(Type, Type), getCollectionElementType(Type, Class), getGenericSupertype(Type, Class, Class), getRawType(Type), getSupertype(Type, Class, Class), indexOf(Object[], Object), resolve(Type, Class, Type, Map), resolveTypeVariable(Type, Class, TypeVariable), subtypeOf(Type), requiresOwnerType(Type), and checkNotPrimitive(Type) have missing branches.
- GsonTypes(), getArrayComponentType(Type), supertypeOf(Type), and typeToString(Type) are not tested at all.

#### GPT-OSS Single
The Generated file contained 2 errors:
- missing package statement.
- missing import of Map.

After fixing all errors, 7 tests passed and 1 test failed. In getRawTypeBranches(), the third assertion is wrong.

list of tests:
1. new parameterizedType with owner requires owner type
2. arrayOf returns correct type
3. wildcard bounds
4. canonicalize() branches
5. getRawType() branches
6. equals() branches
7. mapKey() and valueTypes() with properties
8. resolve() type variable

a helper class was generated.

Missing coverage:
- checkNotPrimitive(Type), declaringClassOf(TypeVariable), equals(Type, Type), getGenericSupertype(Type, Class, Class), getMapKeyAndValueTypes(Type, Class), getRawType(Type), indexOf(Object[], Object), requiresOwnerType(Type), resolve(Type, Class, Type, Map), resolveTypeVariable(Type, Class, TypeVariable), subtypeOf(Type), supertypeOf(Type), and canonicalize(Type) have missing branches.
- GsonTypes(), getArrayComponentType(Type), getCollectionElementType(Type, Class), typeToString(Type), and getSupertype(Type, Class, Class) are not tested at all.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      0       |       1       |       3       |       2        |
| Statement coverage             |     17%      |      35%      |      58%      |      50%       |
| Decision coverage              |     16%      |      30%      |      47%      |      42%       |
| total tests                    |      5       |       9       |       9       |       8        |
| failed tests                   |      0       |       0       |       3       |       1        |
| method names start with test   |     yes      |      no       |      no       |       no       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    16,177    |    21,854     |    14,541     |     17,781     |
| tokens per decision coverage % |     1011     |      728      |      309      |      423       |
GPT-OSS generated better tests. Somehow, generated test with other files is better than single file. It can be credited to having more tests. Still, the coverage is below expectation.

### ReflectionHelper
This file has 396 statements and 32 decisions.
#### Gemma Group
The Generated file contained no error.

4 tests passed and 2 tests failed. Failed tests are:
- in testIsStaticCorrect(), the agent passed an argument to *isStatic* that caused exception.
- in testIsAnonymousOrNonStaticLocal(), the assertion was wrong.

list of tests:
1. field toString
2. constructor toString
3. isStatic()
4. isStatic() correct
5. isAnonymousOrNonStaticLocal()
6. tryMakeAccessible()

Utility class *TestClass* was created.

Missing coverage:
- RecordNotSupportedHelper subclass is not tested at all.
- in RecordSupportedHelper subclass, only the constructor is tested.
- appendExecutableParameters(AccessibleObject, StringBuilder), static{...}, tryMakeAccessible(Constructor), and isAnonymousOrNonStaticLocal(Class) have missing branches.
- createExceptionForRecordReflectionException(ReflectiveOperationException), createExceptionForUnexpectedIllegalAccess(IllegalAccessException), getAccessor(Class, Field), getCanonicalRecordConstructor(Class), getInaccessibleTroubleshootingSuffix(Exception), getRecordComponentNames(Class), isRecord(Class), makeAccessible(AccessibleObject), and getAccessibleObjectDescription(AccessibleObject, boolean) are not tested at all.

#### Gemma Single
The Generated file contained no error.

6 tests passed and 1 test failed. in testCreateExceptionForUnexpectedIllegalAccess(), exceptions are thrown, not returned. Anyhow, managed to assert the exception using a try-catch block.

list of tests:
1. isStatic should return true for static fields and classes
2. fieldToString should return formatted field name
3. constructorToString should return formatted constructor signature
4. getAccessibleObjectDescription should handle Field, Method, and Constructor
5. makeAccessible should make object accessible
6. tryMakeAccessible should return null on success
7. createExceptionForUnexpectedIllegalAccess should throw RuntimeException

a utility class TestClass was generated.

Missing coverage:
- RecordNotSupportedHelper subclass is not tested at all.
- in RecordSupportedHelper subclass, only the constructor is tested.
- appendExecutableParameters(AccessibleObject, StringBuilder), makeAccessible(AccessibleObject), static {...}, tryMakeAccessible(Constructor), and getAccessibleObjectDescription(AccessibleObject, boolean) have missing branches.
- createExceptionForRecordReflectionException(ReflectiveOperationException), getAccessor(Class, Field), getCanonicalRecordConstructor(Class), getRecordComponentNames(Class), isAnonymousOrNonStaticLocal(Class), isRecord(Class), and getInaccessibleTroubleshootingSuffix(Exception) are not tested at all.

#### GPT-OSS Group
The Generated file had one error. in ctorDescription(), it was required to add NoSuchMethodException to method signature.

After fixing the error, 4 tests passed and 2 tests failed. Failed tests are:
- in makeAccessibleSuccess(), exception is thrown and can't test the purpose of the method. I just disabled it since it's not testing *ReflectionHelper* class.
- in tryMakeAccessibleFailure(), msg is null.

list of tests:
1. getAccessibleObjectDescription() formats field
2. getAccessibleObjectDescription() formats method
3. getAccessibleObjectDescription() formats constructor
4. makeAccessible() succeeds for a private field (disabled)
5. tryMakeAccessible() returns null on success
6. tryMakeAccessible() returns failure message for inaccessible ctor

Dummy class and interface were created.

Missing coverage:
- RecordNotSupportedHelper subclass is not tested at all.
- in RecordSupportedHelper subclass, only the constructor is tested.
- getAccessibleObjectDescription(AccessibleObject, boolean), static {...}, and tryMakeAccessible(Constructor) have missing branches.
- createExceptionForRecordReflectionException(ReflectiveOperationException), createExceptionForUnexpectedIllegalAccess(IllegalAccessException), getAccessor(Class, Field), getCanonicalRecordConstructor(Class), getRecordComponentNames(Class), isAnonymousOrNonStaticLocal(Class), isRecord(Class), isStatic(Class), makeAccessible(AccessibleObject), and getInaccessibleTroubleshootingSuffix(Exception) are not tested at all.

#### GPT-OSS Single
The Generated file contained 4 errors:
- in testMakeAccessibleSuccess, field::setAccessible is ambiguous.
- in testMakeAccessibleFailure and testTryMakeAccessibleFailure, it was needed to add NoSuchMethodException to method signature.
- in descriptionUnknown, getAccessibleObjectDescription requires AccessibleObject for first argument which I couldn't instantiate. Test is commented.
- in descriptionConstructor, had to change ctor type from *Constructor<? extends SampleRecord>* to *Constructor<?>*.

After fixing all errors, 7 tests passed and 7 tests failed. Failed tests are:
- in testMakeAccessibleSuccess(), no exception is thrown.
- in testMakeAccessibleFailure(), no exception is thrown.
- in descriptionField(), assertion is incorrect.
- in descriptionMethod(), assertion is incorrect.
- in descriptionConstructor(), assertion is incorrect.
- in testIsAnonymousOrNonStaticLocal(), the last assertion is incorrect.
- in testTryMakeAccessibleFailure(), msg is null.

list of tests:
1. makeAccessible success
2. makeAccessible failure
3. description field
4. description method
5. description constructor
6. description uppercase
7. isStatic()
8. isAnonymousOrNonStaticLocal()
9. testTryMakeAccessible() success
10. tryMakeAccessible() failure
11. record detection
12. get record component names
13. getAccessor()
14. canonical record constructor

a utility record is generated.

Missing coverage:
- RecordNotSupportedHelper subclass is not tested at all.
- in RecordSupportedHelper subclass, getCanonicalRecordConstructor(Class) getRecordComponentNames(Class) isRecord(Class) getAccessor(Class Field) have missing branches which are rare exceptions. Basically, complete coverage achieved in this class.
- isAnonymousOrNonStaticLocal(Class), makeAccessible(AccessibleObject), static {...} tryMakeAccessible(Constructor), and getAccessibleObjectDescription(AccessibleObject, boolean) have missing branches.
- createExceptionForRecordReflectionException(ReflectiveOperationException), createExceptionForUnexpectedIllegalAccess(IllegalAccessException), and getInaccessibleTroubleshootingSuffix(Exception) are not tested at all.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      0       |       0       |       1       |       4        |
| Statement coverage             |     29%      |      45%      |      39%      |      73%       |
| Decision coverage              |     21%      |      40%      |      37%      |      65%       |
| total tests                    |      6       |       7       |       5       |       14       |
| failed tests                   |      2       |       1       |       1       |       7        |
| method names start with test   |     yes      |      yes      |      no       |      yes       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    7,744     |    16,116     |     6,961     |     11,669     |
| tokens per decision coverage % |     369      |      403      |      188      |      180       |
GPT-OSS is clearly the winner. There is no reason not to use GPT-OSS Single for this file. With more prompts, higher coverage can be achieved for sure.

### ReflectionAccessFilterHelper
This file has 101 statements and 22 decisions.
#### Gemma Group
The Generated file contained an error. *FilterResult.DENY* was not defined. It was replaced with *BLOCK_ALL*.

After fixing all errors, 9 tests passed and a single test failed. in canAccessTest(), an exception is thrown. replaced an assertion with assertThrows.

list of tests:
1. isJavaType() true
2. isJavaType() false
3. isAndroidType() true
4. isAndroidType() false
5. isAnyPlatformType true
6. isAnyPlatformType() false
7. getFilterResult() returns first non indecisive
8. getFilterResult() returns deny
9. getFilterResult() returns allow if empty or all indecisive
10. canAccess() test

Missing coverage:
- AccessChecker.static{...}, isAndroidType(String), isAnyPlatform Type(Class), and isJavaType(String) have missing branches.
- CanAccess(AccessibleObject, Object) method and  are not tested at all.


#### Gemma Single
The Generated file contained 3 errors:
- could not resolve *javax.annotation.Nonnull* from isJavaTypeTest(). Hence, removed from the provider.
- could not resolve *FilterResult.DENY*. Replaced with *FilterResult.BLOCK_ALL*.
- could not resolve *ReflectionAccess*. Commented assertions that it was used. Apparently it was supposed to be a stub.

After fixing all errors, 10 tests passed and a test failed. Failed tests are:
- in canAccessDefault(), last assertions throws an exception. *assertThrows* was used instead.

list of tests:
1. [parametrized] isJavaType checks if class name starts with java. or javax (counted 4)
2. [parametrized] isAndroidType checks for android., androidx., or java. types (counted 3)
3. isAnyPlatformType identifies Java, Kotlin, or Scala types
4. getFilterResult returns first non-INDECISIVE result
5. getFilterResult returns ALLOW if all filters are INDECISIVE
6. canAccess returns true (default behavior for non-Java 9 environments)

Missing coverage:
- AccessChecker.static{...}, isAndroidType(String), isAnyPlatformType(Class), and isJavaType(String) have missing branches.
- canAccess(AccessibleObject, Object) is not tested at all.


#### GPT-OSS Group
The Generated file contained 4 errors:
- wrong import path for *ReflectionAccessFilterHelper*. Still, the import was unused.
- missing import of *List*
- usage of *ReflectionAccessFilter.FilterResult.ALPHABET* which is not defined. Replaced with *DENY*.
- in getFilterResultDecisive(), *List.of()* didn't need the argument that was faulty code.

After fixing all errors, all 5 tests passed.

list of tests:
1. isJavaType detects java package
2. isJavaType rejects non-Java types
3. isAndroidType falls back to java check
4. isAnyPlatformType includes java and kotlin
5. getFilterResult uses first decisive filter

Not testing AccessChecker reduced coverage heavily.  
A dummy class was generated.

Missing coverage:
- isAndroidType(String), isAnyPlatformType(Class), isJavaType(String), and getFilterResult(List, Class) have missing branches.
- AccessChecker subclass is not tested at all.

#### GPT-OSS Single
The Generated file contained 3 errors:
- in isJavaTypeJavaPackage(), isAnyPlatformType(), and isAndroidType(), a wrong argument is passed to *isAnyPlatformType()*.
- in isAnyPlatformType(), a non-imported dependency is passed to *isAnyPlatformType()*. Code was commented.
- in getFilterResultFirstDecisive(), *FilterResult.DENY* is used which is undefined. Replaced with *FilterResult.BLOCK_ALL*

After fixing all errors, 3 tests passed and 4 tests failed. Failed tests are:
- in isJavaTypeJavaPackage(), a wrong assertion was inverted.
- in isAndroidType(), the wrong assertions were inverted.
- in isAnyPlatformType(), a wrong assertion was inverted.
- in canAccessTest(), a wrong assertion was inverted.

list of tests:
1. isJavaType returns true for java and javax packages
2. isAndroidType returns true for android and androidx packages and Java types
3. isAnyPlatformType returns true for kotlin, kotlinx, scala and Android types
4. getFilterResult returns ALLOW when list is empty
5. getFilterResult returns first non-indecisive result
6. getFilterResult returns ALLOW when all filters indecisive
7. canAccess delegates to AccessChecker and returns true for accessible method

Missing coverage:
- AccessChecker.static {...}, isAndroidType(String), isAnyPlatformType(Class), and isJavaType(String) have missing branches.

#### Comparison
| Title                        | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-----------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                 |      3       |       3       |       4       |       3        |
| Statement coverage           |     81%      |      84%      |      55%      |      92%       |
| Decision coverage            |     64%      |      64%      |      50%      |      64%       |
| total tests                  |      10      |      11       |       5       |       7        |
| failed tests                 |      1       |       1       |       0       |       4        |
| method names start with test |      no      |      no       |      no       |       no       |
| private variables used       |      no      |      no       |      no       |       no       |
| tokens used                  |    2,719     |    12,291     |     2,444     |     11,456     |
| tokens per stmt coverage %   |      34      |      146      |      44       |      143       |
It's hard to achieve good coverage. I can only test in one environment, and can't test in Android or others. Lower coverage can be accounted for this. I had to add some dependencies to the project. This was why some agents were passing String instead of class to isJavaType(), in order to replicate android or other environments. For this table, I calculated cost per coverage % by statement. In GPT-OSS group, tests had lower coverage and Gemma4 wins for group files. However, in single file, GPT-OSS made a comeback. Since this is a small file, group models should be preferred.
mention hard to test in this environment

### ReflectionAccessFilter
This file has 75 statements and 8 decisions.
#### Gemma Group
The Generated file contained no error.

all 4 tests passed.

list of tests:
1. block all java
2. block all java indecisive
3. block all android indecisive
4. block all platform indecisive

Missing coverage:
- BLOCK_ALL_ANDROID.check(Class<?>) and BLOCK_ALL_PLATFORM.check() have missing branches.
- BLOCK_INACCESSIBLE_JAVA.toString(), BLOCK_ALL_JAVA.check(Class), BLOCK_ALL_JAVA.toString(), BLOCK_ALL_ANDROID.toString(), and BLOCK_ALL_PLATFORM.toString() are not tested at all.


#### Gemma Single
The Generated file contained 2 errors:
- in verifyFilters(), there was an unexpected new line.
- in provideFilterTestData(), a non-existing javax class was imported. I changed it to an existing javax class.

After fixing all errors, 5 tests passed and 5 tests failed. Failed tests are:
- in verifyFilters(), five of the expectedResult were incorrect.

list of tests:
1. [parametrized] Verify all static filters against various class types (counted as 9)
2. Verify toString implementations

Missing coverage:
- BLOCK_INACCESSIBLE_JAVA.check(Class), BLOCK_ALL_JAVA.check(Class), BLOCK_ALL_ANDROID.check(Class), and BLOCK_ALL_PLATFORM have.check(Class) have missing branches.

#### GPT-OSS Group
The Generated file contained an error. *List* was not imported.

After fixing the error, 5 tests passed and a test failed. in blockAllPlatformPlatform(), there was a wrong assertion.

list of tests:
1. BLOCK_INACCESSIBLE_JAVA on java class
2. BLOCK_INACCESSIBLE_JAVA on non-java class
3. BLOCK_ALL_JAVA on java class
4. BLOCK_ALL_ANDROID on android class simulated
5. BLOCK_ALL_PLATFORM covers java and android
6. filter result falls back to ALLOW when all INDECISIVE

Helper class *CompositeFilter* was generated.

Missing coverage:
- BLOCK_ALL_JAVA.check(Class) and BLOCK_ALL_ANDROID.check(Class) have missing branches.
- BLOCK_INACCESSIBLE_JAVA.toString().toString(), BLOCK_ALL_JAVA.toString(), BLOCK_ALL_ANDROID.toString(), and BLOCK_ALL_PLATFORM.toString() are not tested at all.

#### GPT-OSS Single
The Generated file contained 2 errors:
- android and kotlin dummies were generated in the wrong folder.

After fixing all errors, all 9 tests passed.

list of tests:
1. BLOCK_INACCESSIBLE_JAVA: java type ⇒ BLOCK_INACCESSIBLE
2. BLOCK_INACCESSIBLE_JAVA: non-java type ⇒ INDECISIVE
3. BLOCK_ALL_JAVA: java type ⇒ BLOCK_ALL
4. BLOCK_ALL_JAVA: non-java type ⇒ INDECISIVE
5. BLOCK_ALL_ANDROID: android type ⇒ BLOCK_ALL
6. BLOCK_ALL_ANDROID: non-android type ⇒ INDECISIVE
7. BLOCK_ALL_PLATFORM: platform type (android) ⇒ BLOCK_ALL
8. BLOCK_ALL_PLATFORM: platform type (kotlin) ⇒ BLOCK_ALL
9. BLOCK_ALL_PLATFORM: non-platform type ⇒ INDECISIVE

The model did a clever trick. It created two dummy classes which I believe were for replicating android and kotlin environment.  
A simple helper class *NonJavaClass* was generated.

Missing coverage:
- in all ReflectionAccessFilters, toString() was not tested at all.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      0       |       2       |       1       |       1        |
| Statement coverage             |     79%      |      96%      |      88%      |      91%       |
| Decision coverage              |     82%      |      82%      |      91%      |      100%      |
| total tests                    |      4       |      10       |       6       |       9        |
| failed tests                   |      0       |       5       |       1       |       0        |
| method names start with test   |     yes      |      no       |      no       |       no       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    2,203     |    15,395     |     1,980     |     13,777     |
| tokens per decision coverage % |      27      |      188      |      22       |      138       |
GPT-OSS is winner again. This is a small file, so it is preferred to generate test for this file along with some other files to reduce token usage.

### FieldAttributes
This file has 48 statements and 2 decisions.
#### Gemma Group
The Generated file contained no error.

3 tests passed and 1 test failed. in testGetAnnotation(), a not null assertion was wrong.

list of tests:
1. FieldAttributes test
2. getAnnotation()
3. toString()
4. constructor throws exception on null

Missing coverage:
- hasModifier() has a missing branch.


#### Gemma Single
The Generated file contained an error. in setUp(), clazz had the wrong type.

After fixing the error, all 11 tests passed.

list of tests:
1. Constructor should throw NullPointerException when field is null
2. getDeclaringClass should return the correct declaring class
3. getName should return the correct field name
4. getDeclaredType should return the correct generic type
5. getDeclaredClass should return the correct class type
6. getAnnotation should return annotation if present, otherwise null
7. getAnnotations should return all annotations in the field
8. hasModifier should return true if modifier is present
9. hasModifier should return false if modifier is not present
10. toString should return the field's toString representation
11. Decision coverage for generic types and private fields

Full statement and decision coverage achieved.

#### GPT-OSS Group
The Generated file contained 2 errors:
- Wrong import path for *FieldAttributes*. Why would it import itself? This happened in *ReflectionAccessFilterHelper* too.
- missing import of *java.lang.reflect.Modifier*.

After fixing all errors, all 5 tests passed.

list of tests:
1. construction stores field
2. getDeclaredType and getDeclaredClass
3. annotation retrieval
4. modifier checks
5. toString delegates to field

a utility retention and a helper *Sample* class were generated.

Full statement and decision coverage achieved.

#### GPT-OSS Single
The Generated file contained an error. in testGetAnnotations(), variable *Set<Annotation> anns* was instantiated with a *Collection<Annotation>. I Fixed the typed.

After fixing the error, all 11 tests passed.

list of tests:
1. Constructor throws NPE when null is passed
2. getDeclaringClass returns the defining class
3. getName returns the field name
4. getDeclaredType returns the generic type
5. getDeclaredClass returns the raw class of the field
6. getAnnotation returns annotation instance when present
7. getAnnotation returns null when annotation absent
8. getAnnotations returns all annotations in the field
9. hasModifier returns true for existing modifier
10. hasModifier returns false for non-present modifier
11. toString delegates to the underlying field

A utility retention and a helper *GenericClass* was created.

Full statement and decision coverage achieved.

#### Comparison
| Title                        | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-----------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                 |      0       |       1       |       2       |       1        |
| Statement coverage           |     97%      |     100%      |     100%      |      100%      |
| Decision coverage            |     50%      |     100%      |     100%      |      100%      |
| total tests                  |      4       |      11       |       5       |       11       |
| failed tests                 |      1       |       0       |       0       |       0        |
| method names start with test |     yes      |      yes      |      no       |      yes       |
| private variables used       |      no      |      yes      |      no       |       no       |
| tokens used                  |    1,308     |    12,320     |     1,176     |     11,946     |
| tokens per stmt coverage %   |      14      |      123      |      12       |      119       |
Firstly, because there were only two decisions, statement coverage percentage per token is used to calculate cost. Models did superb here. It was a small and simple file. I believe generating tests for *ReflectionAccessFilterHelper*, *ReflectionAccessFilter* and *FieldAttributes* together in a single prompt is ideal. Tests will achieve high coverage with much lower token consumption compared to generating tests for each file in a prompt. GPT-OSS did slightly better here with lower cost.

## Group 4: Exclusion, Reflection & Policy Engine
### Excluder
This file has 450 statements and 88 decisions.
#### Gemma Group
The Generated file contained no error.

3 tests passed and 3 tests failed. Failed tests are:
- testWithVersion() had wrong assertion. the model also realized that it was false.
- in testDisableInnerClassSerialization(), the assertion is wrong.
- in testCreateTypeAdapter(), the model failed to create TypeAdapter and it's null. The Rest of the method had to be commented.

list of tests:
1. withVersion()
2. withModifiers()
3. disableInnerClassSerialization()
4. excludeFieldsWithoutExposeAnnotation()
5. withExclusionStrategy()
6. create TypeAdapter

Three utility classes were generated. Although, one of them was unused.

Missing coverage:
- clone(), create(Gson, TypeToken), excludeField(Field, boolean), isExcludedByExposeAnnotation(Field, boolean), isExcludedByStrategy(Field, boolean), isExcludedByVersion(Field), isInnerClass(Class), isValidSince(Since), isValidUntil(Until), isValidVersion(Since, Until), withExclusionStrategy(ExclusionStrategy, boolean, boolean), and excludeClass(Class, boolean) have missing branches.

#### Gemma Single
The Generated file contained an error:
- an unused import to a wrong path.

After fixing the error, 2 tests passed and 2 tests failed. Failed tests are:
- in testExcludeField(), after instantiating *since1*, the assertion was incorrect.
- in testExcludeClass(), the first assertion was incorrect.

list of tests:
1. test clone() and with methods
2. test withExclusionStrategy()
3. test excludeField() coverage
4. test excludeClass() coverage

*TestClass*, *InnerClass*, *StaticInnerClass* and *StaticInner* classes and *regardVersion* method were generated as utility.  
*InnerClass* was static and had the same implementation as *StaticInnerClass*.

Missing coverage:
- clone(), excludeClass(Class, boolean), excludeField(Field, boolean), isExcludedByStrategy(Field, boolean), isInnerClass(Class), isValidSince(Since), isValidUntil(Until), isValidVersion(Since, Until), withExclusionStrategy(ExclusionStrategy, boolean, boolean), and isExcludedByExposeAnnotation(Field, boolean) have missing branches.
- create(Gson, TypeToken) is not tested at all.

#### GPT-OSS Group
The Generated file contained an error:
- in excludesTransientAndStatic() and fieldExcludedByStrategy, it was necessary to add *NoSuchFieldException* to method signature.

After fixing the error, all 10 tests passed.

list of tests:
1. ignores version when not set
2. excludes annotated with since
3. excludes annotated with until
4. excludes transient and static
5. respects require expose
6. serialize inner class when allowed
7. excludes inner class when disabled
8. excludes anonymous for deserialization only
9. field excluded by strategy
10. class excluded by strategy

A *Dummy* class was generated.

Missing coverage:
- clone(), excludeField(Field, boolean), isExcludedByExposeAnnotation(Field, boolean), isExcludedByStrategy(Field, boolean), isExcludedByVersion(Field), isInnerClass(Class), isValidSince(Since), isValidUntil(Until), isValidVersion(Since, Until), withExclusionStrategy(ExclusionStrategy, boolean, boolean), and excludeClass(Class, boolean) have missing branches.
- create(Gson, TypeToken) and withModifiers(int[]) are not tested at all.

#### GPT-OSS Single
The Generated file contained 2 errors:
- missing package statement.
- in testVersionValidation(), the model tried accessing private method *isValidSince*. I had to remove the test.

After fixing all errors,6 tests passed and 2 tests failed. Failed tests are:
- in testExcludeFieldByStrategy(), the agent tried to access a field that doesn't exist which threw exception. The instantiated variable was not even used.
- in testIsInnerClassUtility(), the assertion threw exception. It was replaced with *assertThrows*.

list of tests:
1. excludeField() by modifier
2. excludeField() by version
3. excludeField() by expose
4. excludeField() by strategy
5. exclude inner class
6. exclude anonymous class
7. excludeClass() with version
8. is inner class utility

*TestClass*, *InnerTestClass*, and *AnonymousTest* classes were generated as utility.

Missing coverage:
- clone(), excludeClass(Class, boolean), excludeField(Field, boolean), isExcludedByExposeAnnotation(Field, boolean), isExcludedByVersion(Field), isValidSince(Since), isValidUntil(Until), isValidVersion(Since, Until), withExclusionStrategy(ExclusionStrategy, boolean, boolean), isInnerClass(Class), and isExcludedByStrategy(Field, boolean) have missing branches.
- create(Gson, TypeToken) and withModifiers(int[]) are not tested at all.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      0       |       1       |       1       |       2        |
| Statement coverage             |     66%      |      79%      |      62%      |      66%       |
| Decision coverage              |     51%      |      63%      |      58%      |      64%       |
| total tests                    |      6       |       4       |      10       |       8        |
| failed tests                   |      3       |       2       |       0       |       2        |
| method names start with test   |      no      |      yes      |      no       |      yes       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    11,349    |    18,185     |    10,031     |     12,961     |
| tokens per decision coverage % |     223      |      289      |      173      |      203       |
Gemma4 Single was the only class to test create(Gson, TypeToken). This is why other models tests have less statement coverage. For a medium file like this, I was expecting higher coverage. Decision coverage in the tests was poor. If priority is high statement coverage, Gemma4 Single is the option to use. Although, about 50% more cost for 15% more statement coverage might not be great.

### FieldNamingPolicy
This file has 203 statements and 14 decisions.
#### Gemma Group
The Generated file contained 2 errors:
- missing import for *java.lang.reflect.Field*.
- wrong call to *testUpperCaseFirstLetter()* static method.

After fixing all errors, 8 tests passed and 1 tests failed. in testSeparateCamelCase(), assertions were expecting a wrong string.

list of tests:
1. IDENTITY policy
2. UPPER_CAMEL_CASE policy
3. UPPER_CAMEL_CASE_WITH_SPACES policy
4. UPPER_CASE_WITH_UNDERSCORES policy
5. LOWER_CASE_WITH_UNDERSCORES policy
6. LOWER_CASE_WITH_DASHES policy
7. LOWER_CASE_WITH_DOTS policy
8. separateCamelCase() utility
9. upperCaseFirstLetter() utility

a utility class *TestClass* was created.

Missing coverage:
- upperCaseFirstLetter(String) and separateCamelCase(String, char) have missing branches.

#### Gemma Single
The Generated file contained 1 error:
- in *testUpperCamelCaseWithSpaces()*, there was a text generation error.

After fixing all errors, 36 tests passed and a test failed. In testUpperCaseFirstLetter(), the first assertion was wrong.

list of tests:
1. [parametrized] IDENTITY policy returns the same name (5 tests)
2. [parametrized] UPPER_CAMEL_CASE policy capitalizes first letter (5 tests)
3. [parametrized] UPPER_CAMEL_CASE_WITH_SPACES policy capitalizes and adds spaces (5 tests)
4. [parametrized] UPPER_CASE_WITH_UNDERSCORES policy converts to upper case with underscores (5 tests)
5. [parametrized] LOWER_CASE_WITH_UNDERSCORES policy converts to lower case with underscores (5 tests)
6. [parametrized] LOWER_CASE_WITH_DASHES policy converts to lower case with dashes (5 tests)
7. [parametrized] LOWER_CASE_WITH_DOTS policy converts to lower case with dots (5 tests)
8. separateCamelCase() decision coverage
9. upperCaseFirstLetter() decision coverage

Utility class *TestClass* and method *getField* were generated.

Full statement and decision coverage achieved.

#### GPT-OSS Group
The Generated file contained no error.

11 tests passed and 2 tests failed. Failed tests are:
- in separateCamelCaseSeparatesCorrectly(), the first assertion was expecting the wrong string.
- in upperCaseFirstLetterProperlyOpens(), all assertions were expecting the wrong string.

list of tests:
1. unchanged
2. leads with upper case
3. leading underscore preserved
4. adds spaces between words
5. leading underscore with spaces
6. correct upper case underscores
7. handles u r ls
8. preserves leading underscore
9. correct lower case underscores
10. correct lower case dashes
11. correct lower case dots
12. separate camel case separates correctly
13. upper case first letter properly opens

Utility *TestFields* class and *field(String)* method were generated.

Missing coverage:
- upperCaseFirstLetter(String) has missing branches.

#### GPT-OSS Single
The Generated file contained an error which was not including package statement.

After fixing the error, 2 tests passed and 7 tests failed. Failed tests are:
- in utility class *Dummy*, a String field *someFieldName* with *someFieldName* value must have been created. This fixed upperCamelCaseWithSpacesSeparates() and other methods that were trying to access this field through *Dummy.class.getDeclaredField()*.
- Like above, field *aURL* with the same value must have been created. This fixed errors in upperCaseWithUnderscoresTranslates(), lowerCaseWithUnderscoresTranslates(), lowerCaseWithDashes(), and lowerCaseWithDots().
- in separateCamelCaseSeparatesCorrectly(), two assertions were wrong. Somehow, it was asserting the same thing with twice with different values.
- in upperCaseFirstLetterUpperCasePreserved(), three assertions had the wrong value.

list of tests:
1. identity translates name
2. upperCamelCase() adds upper case
3. upperCamelCase() with spaces separates
4. upperCamelCase() underscores translates
5. lowerCaseWith() underscores translates
6. lowerCaseWith() dashes
7. lowerCaseWith() dots
8. separateCamelCase() separates correctly
9. upperCaseFirstLetter() upper case preserved

Utility *Dummy* class and *dummyField* method were generated.

Missing coverage:
- upperCaseFirstLetter(String) is missing a branch.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      2       |       1       |       0       |       1        |
| Statement coverage             |     93%      |     100%      |      98%      |      98%       |
| Decision coverage              |     85%      |     100%      |      85%      |      92%       |
| total tests                    |      9       |      37       |      13       |       9        |
| failed tests                   |      1       |       1       |       2       |       2        |
| method names start with test   |     yes      |      yes      |      no       |       no       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    4,688     |    16,872     |     4,413     |     12,396     |
| tokens per decision coverage % |      55      |      169      |      52       |      135       |
This file had very basic logic. It was easy to attain a good coverage. However, all models managed to expect the wrong value for assertions. With covering one more branch, Gemma4 was better than GPT-OSS in single mode, but 33% more tokens were used. in Group file mode, GPT-OSS obtained 5% more statement coverage with slightly less token usage. Overall, I am pleased with the performance of models for this file.

### LongSerializationPolicy
This file has 48 statements and 4 decisions.
#### Gemma Group
The Generated file contained an error. In testDefaultNonNull(), there was a text generation error.

After fixing the error, all 4 tests passed.

list of tests:
1. DEFAULT policy - null value
2. DEFAULT policy - non-null value
3. STRING policy - null value
4. STRING policy - non-null value

Missing coverage:
- DEFAULT.TypeAdapter<Number> and  STRING.TypeAdapter<Number> are not tested at all.


#### Gemma Single
The Generated file contained an error. in stringSerializationProvider(), there was a text generation fault.

After fixing the error, all 9 tests passed.

list of tests:
1. [parametrized] DEFAULT policy serialization (4 tests).
2. [parametrized] STRING policy serialization (4 tests).
3. Verify typeAdapters are not null.

Full statement and decision coverage attained.

#### GPT-OSS Group
The Generated file contained no error.

All 2 tests passed.

list of tests:
1. DEFAULT serialize to number or JsonNull
2. STRING serialize to string or JsonNull

I don't remember the last time that a test file contained no compile error and all its test passed.

Missing coverage:
- DEFAULT.TypeAdapter<Number> and  STRING.TypeAdapter<Number> are not tested at all.

#### GPT-OSS Single
The Generated file contained an error which was missing package statement.

After fixing the error, all 5 tests passed.

list of tests:
1. DEFAULT serialize non-null long as number
2. DEFAULT serialize null as JsonNull
3. STRING serialize non-null long as string
4. STRING serialize null as JsonNull
5. Both policies provide non-null TypeAdapters

Full statement and decision coverage obtained.

#### Comparison
| Title                        | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-----------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                 |      1       |       1       |       0       |       1        |
| Statement coverage           |     92%      |     100%      |      92%      |      100%      |
| Decision coverage            |     100%     |     100%      |     100%      |      100%      |
| total tests                  |      4       |       9       |       2       |       5        |
| failed tests                 |      0       |       0       |       0       |       0        |
| method names start with test |     yes      |      yes      |      no       |       no       |
| private variables used       |      no      |      no       |      no       |       no       |
| tokens used                  |    1,974     |    12,902     |     1,745     |     10,735     |
| tokens per stmt coverage %   |      21      |      129      |      19       |      107       |
Because all agents achieved full decision coverage, for cost, statement coverage is used. This is a tiny file, so it's better to generate test for this file along with some other small files. For the first time, Gemma4 and GPT-OSS in both single and group mode had the same performance. Obviously, the model with fewer parameters and less token usage is better here which is GPT-OSS.

### ToNumberPolicy
This file has 127 statements and 8 decisions.
#### Gemma Group
The Generated file contained 2 errors:
- misspell of *LAZILY_PARSED_NUMBER*.
- misspell of *JsonReader*. It was originally *JsonPRism*. If the agent hadn't had mentioned its typo, I wouldn't be able to fix it.

After fixing all errors, all 6 tests passed.

list of tests:
1. DOUBLE policy
2. LAZILY_PARSED_NUMBER policy
3. LONG_OR_DOUBLE policy - Long
4. LONG_OR_DOUBLE policy - Double
5. LONG_OR_DOUBLE policy - Exception on NaN/Infinity when not lenient
6. BIG_DECIMAL policy

Missing coverage:
- LONG_OR_DOUBLE.readNumber(JsonReader), LONG_OR_DOUBLE.parseAsDouble(String, JsonReader), and BIG_DECIMAL.readNumber(JsonReader) have missing branches.

#### Gemma Single
The Generated file contained 4 errors:
- misspell of *ToNumberPolicy* in longOrDoublePolicy_fallbackToDoubleOnOverflow().
- longOrDoublePolicy_allowsNaNOrInfinityWhenLenient() must have *@ParameterizedTest* annotation instead of *@Test*.
- in longOrDoublePolicy_throwsJsonParseException(), wrong text is generated.
- in bigDecimalPolicy_readsBigDecimal(), variable *result* must be a *Number* type.

After fixing all errors, all 16 tests passed.

list of tests:
1. DOUBLE policy: read double correctly
2. LAZILY_PARSED_NUMBER policy: reads lazily parsed number
3. [parametrized] LONG_OR_DOUBLE policy: handles integers and decimals (4 tests)
4. LONG_OR_DOUBLE policy: handles large long values by falling back to double
5. [parametrized] LONG_OR_DOUBLE policy: throws MalformedJsonException for NaN/Infinity when not lenient (3 tests)
6. [parametrized] LONG_OR_DOUBLE policy: allows NaN/Infinity when lenient (3 tests)
7. LONG_OR_DOUBLE policy: throws JsonParseException for unparseable strings
8. BIG_DECIMAL policy: reads big decimal correctly
9. BIG_DECIMAL policy: throws JsonParseException for invalid format

A utility function to instantiate *JsonReader* was created.

Full statement and decision coverage achieved.

#### GPT-OSS Group
The Generated file had no error.

6 tests passed and 2 tests failed. Failed tests are:
- in LongOrDouble.throwsOnNonNumber(), another exception was thrown.
- in BigDecimalPolicy.throwsOnInvalid(), nothing is thrown.

list of tests:
1. DOUBLE parses double
2. LAZILY_PARSED_NUMBER returns lazily parsed number
3. LONG_OR_DOUBLE reads long when possible
4. LONG_OR_DOUBLE reads double when dot present
5. LONG_OR_DOUBLE throws on non number
6. LONG_OR_DOUBLE throws on infinity when not lenient
7. BIG_DECIMAL parses big decimal
8. BIG_DECIMAL throws on invalid

a utility method to instantiate *JsonReader* was generated.

Missing coverage:
- LONG_OR_DOUBLE.readNumber(JsonReader), LONG_OR_DOUBLE.parseAsDouble(String, JsonReader), and BIG_DECIMAL.readNumber(JsonReader) have missing branches.

#### GPT-OSS Single
The Generated file contained 3 errors:
- wrong import path for *java.io.StringReader*.
- in readReturnsDouble(), variable *n* must be a *Number* type.
- in singleValueReader(), the instantiation of a *StringReader* was wrong. besides, inside the try block, *reader.beginObject()* must be called first.

After fixing all errors, all 12 tests passed.

list of tests:
1. DOUBLE read() returns double.
2. LAZILY_PARSED_NUMBER read() returns LazilyParsedNumber.
3. LONG_OR_DOUBLE integer within long range.
4. LONG_OR_DOUBLE integer out of long range falls back to double.
5. LONG_OR_DOUBLE decimal number parsed as double.
6. LONG_OR_DOUBLE infinity non-lenient throws malformed json exception.
7. LONG_OR_DOUBLE lenient returns double infinity.
8. LONG_OR_DOUBLE NaN non-lenient throws malformed json exception.
9. LONG_OR_DOUBLE NaN lenient returns double NaN
10. LONG_OR_DOUBLE malformed number throws json parse exception
11. BIG_DECIMAL valid big decimal
12. BIG_DECIMAL invalid big decimal throws json parse exception

a utility method to instantiate *JsonReader* was generated.

Missing coverage:
- LONG_OR_DOUBLE.parseAsDouble(String, JsonReade) has a missing branch.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      2       |       4       |       0       |       3        |
| Statement coverage             |     66%      |     100%      |      71%      |      94%       |
| Decision coverage              |     50%      |     100%      |      50%      |      88%       |
| total tests                    |      6       |      16       |       8       |       12       |
| failed tests                   |      0       |       0       |       2       |       0        |
| method names start with test   |     yes      |      no       |      no       |       no       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    3,763     |    12,374     |     3,326     |     11,337     |
| tokens per decision coverage % |      75      |      124      |      67       |      129       |
First of all, what surprises me the most, is the fact that there was a big difference between single and group mode. Clearly, single is the better option even though this was a small file. In group mode, GPT-OSS performed slightly better, while in single mode, Gemma4 was slightly better.

### FormattingStyle
This file has 89 statements and 4 decisions.
#### Gemma Group
The Generated file contained 2 errors:
- type in testPretty() and testWithIndent(). The Agent did comment about the typos.

After fixing all errors, all 8 tests passed.

list of tests:
1. COMPACT style
2. PRETTY style
3. withNewline
4. withIndent
5. withSpaceAfterSeparators
6. invalid newline
7. invalid indent
8. null values

Full statement and decision coverage attained.

#### Gemma Single
The Generated file contained an error. In constructor_throwsException_onNullIndent(), *NullPointerException* had a misspelling which the model mentioned.

After fixing the error, all 15 tests passed.

list of tests:
1. COMPACT style has correct defaults
2. PRETTY style has correct defaults
3. [parametrized] withNewline, withIndent, and withSpaceAfterSeparators create new instances with correct values (3 tests)
4. Constructor throws NullPointerException if newline is null
5. Constructor throws NullPointerException if indent is null
6. [parametrized] Constructor throws IllegalArgumentException if newline contains invalid characters (4 tests)
7. [parametrized] Constructor throws IllegalArgumentException if indent contains invalid characters (4 tests)

Full statement and decision coverage obtained.

#### GPT-OSS Group
The Generated file contained an error. It tried to create *FormattingStyle* with a private constructor in invalidNewlineThrows() and invalidIndentThrows(). I managed to change these tests to behave like what the model intended.

After fixing the error, all 3 tests passed.

list of tests:
1. Default styles are immutable
2. Constructor rejects invalid newline
3. Constructor rejects invalid indent

Full statement and decision achieved. Surprisingly, with 3 tests.

#### GPT-OSS Single
The Generated file contained an error which was missing package statement.

After fixing the errors, all 8 tests passed.

list of tests:
1. Compact style defaults
2. Pretty style defaults
3. withNewline with valid value
4. withNewline with invalid value throws IllegalArgumentException
5. withIndent with valid value
6. withIndent with invalid value throws IllegalArgumentException
7. withSpaceAfterSeparators toggle
8. Chaining creates distinct instances

Full statement and decision gained.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      2       |       1       |       1       |       1        |
| Statement coverage             |     100%     |     100%      |     100%      |      100%      |
| Decision coverage              |     100%     |     100%      |     100%      |      100%      |
| total tests                    |      8       |      15       |       3       |       8        |
| failed tests                   |      0       |       0       |       0       |       0        |
| method names start with test   |     yes      |      no       |      no       |       no       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    2,652     |    12,524     |     2,344     |     12,489     |
| tokens per decision coverage % |      27      |      125      |      23       |      125       |
This was another tiny file, and all agents tests covered the whole file. Therefore, group mode is preferable for this file. In addition, notice that the token usage was almost the same.

### Primitives
This file has 136 statements and 58 decisions.
#### Gemma Group
The Generated file contained no error.

All 4 tests passed.

list of tests:
1. isPrimitive()
2. isWrapperType()
3. wrap()
4. unwrap()

Missing coverage:
- all four methods have missing branches.

#### Gemma Single
The Generated file contained no error.

All 35 tests passed.

list of tests:
1. isPrimitive returns true for primitive classes
2. isPrimitive returns false for non-primitive classes
3. isPrimitive returns false for null or non-Class types
4. [parametrized] isWrapperType returns true for all nine wrapper types (9 tests)
5. isWrapperType returns false for non-wrapper types
6. [parametrized] wrap returns correct wrapper class for primitive types (11 tests)
7. [parametrized] unwrap returns correct primitive class for wrapper types (11 tests)

Full statement and decision coverage obtained.

#### GPT-OSS Group
The Generated file had no error.

All 6 tests passed.

list of tests:
1. isPrimitive recognizes primitives
2. isWrapperType recognizes wrappers
3. wrap returns Integer for int
4. wrap returns String for String
5. wrap returns int for Integer
6. wrap returns String for String

Missing coverage:
- all four methods have missing branches.

#### GPT-OSS Single
The Generated file contained an error. It was missing package statement.

After fixing the error, all 24 tests passed.

list of tests:
1. isPrimitive() returns true for primitive classes
2. isPrimitive() returns false for non-primitive classes
3. isWrapperType() recognizes all wrapper types
4. isWrapperType() rejects non-wrapper types
5. [parametrized] wrap returns correct wrapper for primitive types (9 tests)
6. wrap returns argument when not a primitive
7. [parametrized] unwrap returns correct primitive for wrapper types (9 tests)
8. unwrap returns argument when not a wrapper

Missing coverage:
- isPrimitive(Type) is missing a single branch.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      0       |       0       |       0       |       1        |
| Statement coverage             |     79%      |     100%      |      76%      |      100%      |
| Decision coverage              |     63%      |     100%      |      56%      |      98%       |
| total tests                    |      4       |      35       |       6       |       24       |
| failed tests                   |      0       |       0       |       0       |       0        |
| method names start with test   |     yes      |      no       |      no       |      yes       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    2,776     |    12,278     |     2,453     |     12,489     |
| tokens per decision coverage % |      44      |      123      |      44       |      125       |
Interestingly, both models used almost the same amount of tokens. Usually, Gemma4 consumes more. We can see that Gemma has higher coverage, which makes it the winner model for this file. Again, for a small file like this, the coverage difference between single and group mode is surprising.

### JavaVersion
This file has 91 statements and 14 decisions.
#### Gemma Group
The Generated file contained no error.

All 6 tests passed.

list of tests:
1. parseMajorJavaVersion() - legacy 1.8 style
2. parseMajorJavaVersion() - newer 9.0.4 style
3. parseMajorJavaVersion() - invalid format
4. isJava9OrLater()
5. parseDotted() via parseMajorJavaVersion()
6. extractBeginningInt() via parseMajorJavaVersion()

testIsJava9OrLater() had no body.

Missing coverage:
- parseDotted(String), and extractBeginningInt(String) have missing branches.
- getMajorJavaVersion(), and isJava9OrLater() are not tested at all.

#### Gemma Single
The Generated file contained no error.

All 10 tests passed.

list of tests:
1. [parametrized] parseMajorJavaVersion() covers various Java version string formats and decision branches (9 tests)
2. parseMajorJavaVersion() handles empty string by returning default 6

Missing coverage:
- parseDotted(String) has a missing branch.
- getMajorJavaVersion(), and isJava9OrLater() are not tested at all.

#### GPT-OSS Group
The Generated file contained no error.

3 tests passed and 1 test failed. In systemProperty(), the agent tried to set java version to 1.7.0_80 through *System.setProperty("java.version")* which didn't work and the test failed. Since I was using java 17.0.9, I changed the assertion so it works with my version.

list of tests:
1. parseMajorJavaVersion handles legacy 1.x format
2. parseMajorJavaVersion handles recent 9+ format
3. parseMajorJavaVersion defaults to 6 on unknown input
4. getMajorJavaVersion reflects system property

Missing coverage:
- extractBeginningInt(String) and parseDotted(String) have missing branches.
- isJava9OrLater() was not tested at all.

#### GPT-OSS Single
The Generated file contained two errors. In many tests, the code was trying to access *parseDotted(String)* which is private. They have been commented. In addition, extractBeginningIntParameterized(String ,int) was also trying to access *extractBeginningInt(String)* which is private. I Commented this test as well.

After fixing all errors, all 8 tests passed.

list of tests:
1. parseMajorJavaVersion – uses parseDotted
2. parseMajorJavaVersion – falls back to extractBeginningInt
3. parseMajorJavaVersion – defaults to 6 on failure
4. getMajorJavaVersion – reflects static field
5. [parametrized] isJava9OrLater() test (4 tests)

notes

Missing coverage:
- parseDotted(String) has a missing branch.
- isJava9OrLater() was not tested at all.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      0       |       0       |       0       |       2        |
| Statement coverage             |     90%      |      90%      |      81%      |      92%       |
| Decision coverage              |     71%      |      78%      |      64%      |      78%       |
| total tests                    |      6       |      10       |       4       |       8        |
| failed tests                   |      0       |       0       |       1       |       0        |
| method names start with test   |     yes      |      no       |      no       |       no       |
| private variables used         |      no      |      no       |      no       |       no       |
| tokens used                    |    3,331     |    16,840     |     2,944     |     10,751     |
| tokens per decision coverage % |              |               |               |                |
It is important to note that it is not possible to test some of the methods of this file, for we are dealing with java version. An agent tried to change java version temporary, but it didn't work. It is okay if models didn't cover parseDotted(String), extractBeginningInt(String javaVersion), isJava9OrLater() and isJava9OrLater() fully. This means that only parseMajorJavaVersion(String) could be statically changed. Interestingly, this method is left visible for testing purposes. I probably shouldn't have generated tests for this file unless there is a way to easily trick JVM to think the java version is something else.

## Group 5: Adapter infrastructure
# TODO:
Gemini Group didn't generate MapTypeAdapterFactory, CollectionTypeAdapterFactory, and TreeTypeAdapter in the first prompt
### TypeAdapter
This file has 125 statements and 6 decisions.
#### Gemma Group
The Generated file contained 2 errors:
- missing import for *com.google.gson.stream.JsonToken*.
- in fromJsonReader() and fromJsonReaderNull(), *JsonReader* is passed to *fromJson()* which is not possible. A *StringReader* is passed instead.

After fixing all errors, all 13 tests passed.

list of tests:
1. toJson() with writer
2. toJson() with writer null
3. toJson() string
4. toJson() string null
5. toJsonTree()
6. toJsonTree() null
7. fromJson() reader
8. fromJson() reader null
9. fromJson() string
10. fromJso(n string null
11. fromJsonTree()
12. fromJsonTree() null
13. null safe

Since *TypeAdapter* class is abstract, the agent created *TestTypeAdapter* class ,which inherits from it and only the abstract methods are overridden.

Missing coverage:
- NullSafeTypeAdapter.read(JsonReader), NullSafeTypeAdapter.write(JsonWriter, Object), fromJsonTree(JsonElement), toJson(Object), toJsonTree(Object), and nullSafe() have missing branches.
- NullSafeTypeAdapter.toString() is not tested at all.


#### Gemma Single
The Generated file contained one error. in testFromJsonReader(), like group mode, a JsonReader was passed to *fromJson()*.

After fixing the error, all 13 tests passed.

list of tests:
1. toJson() writer
2. toJson() string
3. toJsonTree()
4. fromJson() reader
5. fromJson() string
6. fromJson() tree
7. nullSafe() new
8. nullSafe() existing
9. nullSafe() write null
10. nullSafe() write not null
11. nullSafe() read null
12. nullSafe() read not null
13. nullSafe() to string

Similar to group mode, *TestTypeAdapter* class is created.

Missing coverage:
- fromJsonTree(JsonElement), toJsonTree(Object), and toJson(Object) have missing branches. They are only catching IOExceptions, which are hard to emulate. As a result, if we ignore these, complete coverage was achieved.

#### GPT-OSS Group
The Generated file contained 3 errors:
- missing import for *com.google.gson.stream.JsonReader*, *com.google.gson.stream.JsonWriter*, and *java.io.IOException*.

After fixing all errors, all 4 tests passed.

list of tests:
1. toJson() writes correctly
2. fromJson() parses value
3. toJsonTree() wraps null
4. nullSafeAdapter() delegates

Class *StringAdapter* was created with the same purpose as previous agents.  
I'm not surprised to see the weak coverage when there are four tests.

Missing coverage:
- nullSafe(), toJsonTree(Object), and NullSafeTypeAdapter.write(JsonWriter, Object) have missing branches.
- NullSafeTypeAdapter.read(JsonReader), fromJsonTree(JsonElement), toJson(Object), and NullSafeTypeAdapter.toString() are not tested at all.

#### GPT-OSS Single
The Generated file contained 3 errors:
- missing package statement.
- wrong import path for *com.google.gson.stream.JsonToken* and
- incorrect import path for *com.google.gson.internal.bind.JsonTreeWriter*. Still, the correct import is unused.

After fixing all errors, all 10 tests passed.

list of tests:
1. toJson() string returns expected json
2. toJson() writer writes value without escaping
3. toJson() tree returns json element
4. fromJson() reader parses value
5. fromJson() string parses value
6. fromJson() tree parses value
7. nullSafe() write handles null
8. nullSafe() read handles null
9. nullSafe() singleton behavior
10. nullSafe() to string includes wrapped adapter

Class *StringAdapter* was generated with the same purpose as previous agents.

Missing coverage:
- NullSafeTypeAdapter.read(JsonReader), NullSafeTypeAdapter.write(JsonWriter, Object), toJson(Object), toJsonTree(Object), and fromJsonTree(JsonElement) have missing branches.

#### Comparison
| Title                          | Gemma4 Group | Gemma4 Single | GPT-OSS Group | GPT-OSS Single |
|:-------------------------------|:------------:|:-------------:|:-------------:|:--------------:|
| Errors count                   |      2       |       1       |       3       |       3        |
| Statement coverage             |     73%      |      86%      |      49%      |      78%       |
| Decision coverage              |     50%      |     100%      |      33%      |      67%       |
| total tests                    |      13      |      13       |       4       |       10       |
| failed tests                   |      0       |       0       |       0       |       0        |
| method names start with test   |      no      |      yes      |      no       |       no       |
| private variables used         |     yes      |      no       |      no       |       no       |
| tokens used                    |    2,789     |    14,828     |     1,473     |     15,046     |
| tokens per decision coverage % |      56      |      148      |      45       |      225       |
Gemma4 is the winner here. Although in group mode, both models did poor. As I mentioned, with not considering hard to make IOExceptions, Gemma4 single did attain full coverage, shockingly with less token usage!