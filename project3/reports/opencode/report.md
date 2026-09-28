# Unit Test Generation Using OpenCode For Hard Project
TODO
## Table of Content
TODO

##  How tests are generated?
TODO
### How a good test generation flow is achieved
Firstly, I created a folder containing only the files that are chosen to generate test for.
Their dependencies were imported to project in maven.
That included some of the keycloak modules.
I wasn't sure if mocking should be used duo to sheer amount of dependencies.
I was running GPT-OSS locally, and the context windows was set to 64K context inside.
This will matter in later attempts.
So I tried generating tests with and without mocking first.

#### First attempt: Build test using mock
In the first attempt, that model is given this prompt:
> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java ,
> create unit tests using Junit5 and Mockito.
> Achieve decision coverage. Use "junit-5-skill" skill.
> Make sure that all tests pass.

The Agent only generated two tests which is very inadequate for this large file.
It didn't verify that all tests pass.
So I sent this prompt: "You didn't verify the tests".
The agents then started to search for "AuthenticationFlowException" class which wasn't present in that folder.
This was the point which I realized it might be needed to include the dependencies files in the project folder.

I didn't run these tests since they contained many compile errors. Since there were only two tests,
the coverage was going to be very low, so I didn't bother to fix all those compile errors at all.

#### Second Attempt: Build Tests Without Mock
So plan this time is to not mock the integrations:
> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java create unit tests using Junit5.
> Achieve decision coverage. Use "junit-5-skill" skill.
> Concentration is only on units, so don't test integration and don't use mocking.
> Verify that all tests pass using "mvn clean verify" command.
> Don't stop until all compilation errors are fixed and all tests pass.

This is the response I received:
```
I’m sorry, but I’m unable to add a unit‑test suite for AuthenticationProcessor as requested.  
The class relies on numerous Keycloak framework classes (e.g., KeycloakSession, RealmModel, ClientModel,
AuthenticationSessionModel, etc.) that are not present in this repository. Because of those missing types and the many
external dependencies, the code does not compile, and any JUnit tests that touch even the simplest methods would fail to
compile or would require extensive stubs or mocking that is disallowed by the instructions.
```
At this moment, I realized that the dependencies from the project itself should be present in
the project if I wanted to avoid mocking.

#### Third Attempt: First attempt but more clarification
At this point, I was really disappointed with generating tests without mocking unless
I add all the needed project files which would take some time.
Therefore, I tried mocking again.
I thought I should be more clear about running the tests,
so I told the agent to verify the tests using "mvn clean verify" command.
Agent should run this until all compilation errors are fixed and all tests are correct:
> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java ,
> create unit tests using Junit5 and Mockito.
> Achieve decision coverage. Use "junit-5-skill" skill.
> Verify that all tests pass using "mvn clean verify" command.
> Don't stop until all compilation errors are fixed and all tests pass. 

There was not much different from attempt 1.
Again, only two tests were generated.
The Agent also refused to compile since it thought imported classes from the project itself were missing,
but they were present since I had added them in maven dependencies.

At this point, I decided to step up the game. With the current state, only a few tests will be generated.
So I decided to use plan mode first before generating the tests.

#### Fourth Attempt: Plan before generation
As I mentioned, let's plan the test generation. The agent is switched to plan mode.
> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java ,
> plan out creating unit tests using Junit5 and Mockito.
> Plan many tests to obtain a high decision coverage.

The agent planned about 17 tests.
Still, it is low though, but it is much better than just generating 2 tests.
It even designed patterns for mocking, parametrized tests, assertions, and nested classes.
So now let's see how the generation result will be.
The agent is switched back to build mode:
> Now implement the tests you have planned.
> Verify that all tests pass using "mvn clean verify" command.
> Don't stop until all compilation errors are fixed and all tests pass. 

Even with the auspicious plan, the model generated only one test!
Although the agent said it was a tentative test file, and it needs more tests.
The agent decided to look for some files in the project that didn't exist, of course, in the repository.
Consequently, I interrupted this pointless test generation.

#### Fifth Attempt: Dependency files added
I spent some time adding over 2000 files into the repository and made sure that the project could be built without any
problem. Not all of them are dependencies for our files, but they could have been dependencies for other dependencies.
I admit this was not really the correct approach.  

I used plan mode at the beginning again.
There is no way that this model can generate good tests in one go:
> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java ,
> plan out creating unit tests using Junit5. Use "junit-5-skill" skill.
> Concentration is only on units, so don't test integration and don't use mocking.
> Plan many tests to obtain a high decision coverage for this file.

The agent planned 23 tests this time. It was time to implement:
> Now implement the tests you have planned.
> Verify that all tests pass using "mvn clean verify" command.
> Don't stop until all compilation errors are fixed and all tests pass.

This time, the agent generated 4 tests.
It was also taking the time into account, which is why it decided not to generate all the tests.
It was able to find some classes that it needed to read thanks to me adding all those files.
After the four tests had been generated,
the agent analyzed the file and fixed some supposed error and warning that it found.
After that, it decided to verify the tests.

There were 10 errors. Instead of fixing them one by one,
the agent recreated the test file. This time, three tests were generated.
Immediately after that, it ran the verification command. This time, there were 8 errors.
The Agent added two imports and ran the verification command.
Errors were reduced to 4. After some changes, there was only a single error.
The problem was that it was not able to find a specific class which was present in the repository.
This made me think that I should probably use LSP.
Context windows was 64K tokens. It ran out of them. Still, given the circumstances,
the final result was not going to be satisfactory.

#### Sixth Attempt: LSP Enabled
I changed OpenCode's configuration to enable LSP.
In short, Language Server Protocol is a tool that enables a code editor, IDE,
or agent to communicate with a language server to understand source code to understand the code.
For example, instead of searching for usage of a class by using *grep* command,
it will use a tool to find all references of the class.
I added this since I noticed agents were spending a lot of time trying to find a specific class.
LSP also finds errors before compilation. The lsp used for java was jdtls. I could see that in OpenCode's sidebar,
it said that LSP will be used when files are read.

This prompt was given to the model:
> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java ,
> plan out creating unit tests using Junit5 and Mockito.
> Use "junit-5-skill" skill.
> Plan many tests to obtain a high decision coverage.

The model came up with 20 tests and a setup plan.
After that I gave it this prompt:
> Now implement the planned tests.
> Verify that all tests pass using "mvn clean verify" command.
> Don't stop until all compilation errors are fixed and all tests pass. 

The Model was struggling what to mock or stub. The model was still using commands like glob or grep.
This made me doubt whether LSP was really being used. In the end, the model refused to generate tests again:
```
I think we cannot finish.I’m sorry, but given the size and complexity of the codebase, it’s not feasible to write a full,
exhaustive set of unit tests here that will compile and run successfully in this environment.
The AuthenticationProcessor class relies on a large number of Keycloak-specific interfaces and implementation classes
that would need to be mocked or stubbed extensively. Creating that amount of mock setup would be beyond
the scope of an answer in this format.
```

At this moment, I realized that I better tell the agent to implement the tests in multiple stages, rather than in one go.

#### Seventh Attempt: Can You Run the Tests
This was just a sidetracked attempt. I wanted to give generating tests without plans another go while LSP is enabled.
I also thought since last time the model said it wasn't possible to generate a full and exhaustive set of unit tests,
I changed high coverage to a satisfactory coverage. Still, 80% decision coverage is what I'm aiming for.
> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java ,
> create unit tests using Junit5 and Mockito. Aim for a satisfactory decision coverage.
> Use "junit-5-skill" skill. Verify that all tests pass using "mvn clean verify" command.>
Don't stop until all compilation errors are fixed and all tests pass. 

The model generated three tests.
Somehow like this, more tests are being generated.
After that, it tried to run the tests. There were 23 errors.
After making some changes,  errors were reduced to 14.
This time, the agent decided to rewrite the whole file. Two tests were generated.
Still, there were 12 errors. It wrote the test file again! This time 3 tests.
There were only 2 errors now. The Agent decided that two of the testes were not important,
so it removed them. With only one test, finally, there was no compilation error.
However, this test failed.
I don't know what change it did that resulted in verify command to give this output:

`Tests run: 1, Failures: 0, Errors: 0, Skipped: 0`.

The command was successful finally. However, the agent was almost at 64K tokens, and the token window was almost full.
So it was not possible to continue that session further.
I probably won't force the agents to fix errors and run tests since the context windows will run out quickly even
if it's set to 256K, which is ollama's limit.

#### Eighth Attempt: LSP comes in
I still have hope that with planning, it is possible to create a good unit test in multiple stages for
large and complicated files like this. I was not sure whether LSP was working or not,
so instead of using a cloud server which was the default option, I tried running it on my own device.
I also prompted the agent to plan the implementation is multiple stages.

> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java 
> plan out creating unit tests using Junit5 and Mockito.Use "junit-5-skill" skill.
> Plan many tests to obtain a satisfactory decision coverage.
> The implementation will be done in multiple stages, so plan these stages as well.

Another plan with many tests were generated. The agent planned six stages.
After that, I prompted it to carry out the first stage.
I also prompt it to only generate the tests and don't build them, since in the previous times,
this took long and context window ran out.

Six tests were generated, however, five of them didn't have an implementation.
LSP actually came into play and pointed out to errors. Agent was able to fix these errors.
I prompted it to generate the second stage.

It generated a new test file. It had three tests but with many errors. The Agent tried to fix these errors,
but it wasn't able to do it. The Agent tried to fix this error, but in the end it gave up. It said that given time,
It would stop there.

LSP was definitely taking effect, but it added a problem which was forcing the agent to fix errors.

#### Ninth Attempt: Ignore LSP Errors.
The prompt was exactly the same as previous attempt, but I added that the LSP errors should be ignored.
This did not happen! So I just disabled LSP. This is why I didn't provide many details about it.

#### Tenth Attempt: LSP Disabled.
I disabled LSP, since I didn't notice any benefit.
In fact, it was causing more trouble and token usage.
This is the prompt given:
> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java ,
> plan out creating unit tests using Junit5 and Mockito.
> Use "junit-5-skill" skill. Plan many tests to obtain a satisfactory decision coverage.
> The implementation will be done in multiple stages, so plan these stages as well.
> Remember that you should avoid fixing the errors LSP determines. 

I did forget to remove the last sentence. It was fine though.
However, the agent generated twenty tiny stages.
This made me realize that I should force the agent to a definite amount of stages.
I decided to ditch this session and start a new session.

#### Eleventh Attempt: LSP Disabled, Definite Stages.
> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java ,
> plan out creating unit tests using Junit5 and Mockito. Use "junit-5-skill" skill.
> Plan many tests to obtain a satisfactory decision coverage.
> The implementation will be done in multiple stages (max 5 stages), so plan these stages as well.

I forced the agent to have at most five stages. It did choose 5. I believe five is a reasonable number.
In the end, the agent generated a test file with about 350 lines. Removing LSP was the right thing.

I have been wanting to try using MCP severs for a while. I looked up some MCP servers suitable for this project.
In the next attempt, all three MCP servers I have introduced will be used.

##### Twelfth Attempt: MCP Servers Enabled.
> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java ,
> plan out creating unit tests using Junit5 and Mockito.
> Use "junit-5-skill" skill. Use "context7-mcp" skill if you need to fetch information about dependencies.
> Plan many tests to obtain a satisfactory decision coverage.
> The implementation will be done in five stages, so plan these stages as well.
> The repository has been indexed with codebase-memory.

The agent asked for some clarifications:
- Where the testing concentration is.
- Should mockito be used for all dependencies? Can we use lightweight stubs?
- Are there any specific scenarios?
- What are the naming conventions.

I decided to add more clarification to the prompt, so agent will get to work right away next time.

##### Thirteenth Attempt: MCP Servers Enabled & More Clarification
> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java ,
> plan out creating unit tests using Junit5.
> Use real instances of dependencies.
> You will implement based on this plan later on.
> Use "junit-5-skill" skill.
> Use "context7-mcp" skill if you need to fetch information about dependencies.
> I don't have any particular branch or concrete scenario to test in mind.
> But we plan to achieve 80% decision coverage of the whole file.
> Since this file is large and complex, the implementation of tests will be done in five stages,
> so plan these stages as well. The repository has been indexed with codebase-memory.

The prompt is quite longer now. Although I did remove mockito here just to see what happens.
I can tell with "sequential thinking", the way the agent thinks is different.
Furthermore, the output is more structured. `context7-mcp` skill and `codebase-memory` were not used though.

I prompted the agent to build the test files. In the end, 27 tests were generated.
It isn't many, but it is much better than the time only 2 tests were generated.
The more test generation is credited to each stage having its own separate test file.
For these models, it is easier to generate something from scratch rather than editing something previously.
This does increase token usage for sure, but I believe it is worth it.

##### Fourteenth Attempt: Mock Only When Needed.
> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java ,
> plan out creating unit tests using Junit5 and Mockito.
> You will implement based on this plan later on.
> Use "junit-5-skill" skill. Use "context7-mcp" skill if you need to fetch information about dependencies.
> I don't have any particular branch or concrete scenario to test in mind.
> But, plan to achieve 80% decision coverage of the whole file.
> The goal is unit testing, so try to avoid integration test if possible.
> If it wasn't, use a simple mock or stub.
> Since this file is large and complex, the implementation of tests will be done in five stages,
> so plan these stages as well. The repository has been indexed with codebase-memory.

The only difference with last time is that I prompted the agent to mock when needed.
I got clarification from my supervisor that I can mock. It will be in unit testing domain still.
This time, the agent did use codebase-memory commands.

However, the agent decided to generate tests in one single file.
This caused it to fall into a loop in which it was rewriting the test file again and again.
I think if I just prompt the agent to generate tests in separate files, we have finally found an excellent flow.

#### Prompt
This will be given to the agent in plan mode:
> For @[file name] , plan out creating unit tests using Junit5 and Mockito.
> You will implement based on this plan later on.
> Use "junit-5-skill" skill. Use "context7-mcp" skill if you need to fetch information about dependencies.
> I don't have any particular branch or special scenario to test in mind.
> But, plan to achieve 80% decision coverage of the whole file.
> The goal is unit testing, so try to avoid integration test if possible.
> If it wasn't, use a simple mock or stub. Since this file is large and complex,
> the implementation of tests will be done in five stages, so plan these stages as well.
> Generate tests for each stages in separate test files.
> The repository has been indexed with codebase-memory.

With the prompt above, I got a satisfactory result. We will proceed with this prompt now.

## How Coverage is Calculated
TODO
mentions DIFFERENCE OF COVERAGE AND STATEMENT WITH JACOCO

## Tests Report
### AuthenticationProcessor
This file has 1,319 LOC, 128 methods, 524 statements, and 168 branches.

#### Gemma4
##### Initial Implementation Plan
###### Stage 1 Foundation and State Management
Focus: Testing the core class's ability to hold and manage its internal state
and basic properties.
- Setup: Implement @BeforeEach to initialize the `AuthenticationProcessor` with
  mocked dependencies.
- Getters/Setters: Verify all standard getters and setters.
- Message Stores: Test `ForwardedFormMessageStore` interaction via `setForwardedErrorMessage()`,
  `getAndRemoveForwardedErrorMessage()`, etc.
- Lazy Loading: Test `getBruteForceProtector()` (ensuring it fetches from the session if null).
- Simple Logic: Test `isBrowserFlow()` and `setBrowserFlow()`.

###### Stage 2: The Result Inner Class (The Context)
Focus: Testing the Result class, which is the primary context used by authenticators.
- Lifecycle/Status: Test `success()` (with/without credentials), `failure()` (various overloads),
  `challenge()`, `forceChallenge()`, and `attempted()`.
- Configuration: Test `getAuthenticatorConfig()` (handling both null cases 
and realm-based fetching).
- State Management: Test the generic `getState()` method with different types and suppliers.
- User Attachment: Test `attachUserSession()` (specifically the logic for lightweight users).
- Form Generation: Test `form()` (ensuring it interacts with `LoginFormsProvider` and
correctly applies forwarded Error, Success, or Info messages).
- URL Generation: Test `getActionUrl()`, `getActionTokenUrl()`, and `getRefreshUrl()`.

######  Stage 3: Core Authentication Flow Logic
Focus: Testing the primary entry points that drive the authentication process.
- Main Entry: Test `authenticate()` (covering the paths through `authenticateOnly()` and
`authenticationComplete()`).
- Browser Auth: Test `authenticateOnly()` (including client session validation and flow execution).
- Client Auth: Test `authenticateClient()` (handling the flow and potential internal exceptions).
- Action Processing: Test `authenticationAction()` (validating the current execution,
  finding the model, and processing the action).
- Flow Creation: Test `createFlowExecution()` (testing the branches for BASIC, FORM,
  and CLIENT flow providers).

###### Stage 4: Error Handling and Exception Management
Focus: High-density decision coverage by hitting the numerous error branches in
exception handlers.
- Browser Exceptions: Test `handleBrowserException(Exception)` (testing 
  `AuthenticationFlowException` with various `AuthenticationFlowError` types and
  the generic Exception path).
- Exception Lists: Test `handleBrowserExceptionList(AuthenticationFlowException)`(covering
  the switch statement for all error cases).
- Client Exceptions: Test `handleClientAuthException(Exception)` (testing both
  `AuthenticationFlowException` error codes and the generic Exception path).
- Brute Force: Test `logFailure(String)` (testing the `isBruteForceProtected` condition
  and user lookup).

###### Stage 5: Lifecycle and Static Utilities
Focus: Testing static helper methods and session manipulation logic.
- Flow Reset: Test `resetFlow()` and the static `resetFlow(AuthenticationSessionModel, String)`.
- Session Management: Test the static `clone(...)` and `recreate(...)` methods (ensuring
  notes and properties are correctly copied).
- Session Attachment: Test the static `attachSession(...)` method (handling username logic and 
  session persistence states).
- Status Checks: Test `isSuccessful(AuthenticationExecutionModel)`.

##### Initial Implementation
###### Stage 1
The agent generated `AuthenticationProcessorTest` with 15 tests.
All the tests that were planned are implemented.

###### Stage 2
The agent generated `AuthenticationProcessorResultTest` with 5 tests.
These tests were missed from the plan:
- Form Generation test was generated, but it contained an error.
  `form()` has a deep method call chain which requires extended mocking behavior that
  the agent did not generate.
- URL Generation test is generated, but the model
  was unable to test the full URI construction here as it relies on static methods.

###### Stage 3
The agent generated `AuthenticationProcessorCoreTest` with 11 tests.
These tests were missed from the plan:
- For main entry, `authenticationComplete()` is not tested.
- Action processing test was not generated.

###### Stage 4
The agent generated `AuthenticationProcessorErrorTest` with 7 tests.
All the tests that were planned are implemented.

###### Stage 5
The agent generated `AuthenticationProcessorLifecycleTest` with 4 tests.
These tests were missed from the plan:
- No tests for flow reset was generated.
- Status check via `isSuccessful(AuthenticationExecutionModel)` was not generated.

Tests `testAttachSession_WithAttemptedUsername()` and `testAttachSession_ExistingValidSession()`
are disabled since they ran into error. Extended mocking behavior is needed.

##### Initial Coverage
OpenClover coverage is 37.6%, and decision coverage is 25.0%.

Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gemma4_AuthenticationProcessor_round1.md`.

##### Improvement Phase
The agent was given the missing coverage markdown file. It came up with this two-stage plan

###### Stage 1: Result Context and Primary Authentication Flow
Goal: Target the Result inner class (which has significant missing methods)and
the core authentication entry points, while closing critical branch gaps.
- New Test File: `src/test/java/org/keycloak/authentication/AuthenticationProcessorResultDeepDiveTest.java`
- Targeting "Methods Not Entered":
  - Result class completeness: `getCategoryRequirementFromCurrentFlow()`, `getTopLevelFlow()`,
    `getAuthenticator()`, `getClientAuthenticator()`, `getUser()`, `setUser()`,
    `getAuthenticationSelections()`, `setAuthenticationSelections()`, `getEventDetails()`,
    `getUserErrorMessage()`.
  - Core Flow Logic: `authenticate()`, `authenticateOnly()`, `authenticationAction()`,
    `authenticateClient()`.
- Targeting "Partially Covered" Branches:
- `success(String)`: Test the true branch (when credentialType is provided).
- `attachUserSession(UserSessionModel)`: Test the false branch (where
  the user is not a lightweight user).
- `logFailure(String)`: Test the false branch (where no user is found for brute force logging).
  
###### Stage 2: Exception Handlers, URL Utilities, and Session Management
Goal: Achieve high decision coverage in the complex error-handling logic and
cover the remaining utility and session management methods.
- New Test File: `src/test/java/org/keycloak/authentication/AuthenticationProcessorErrorAndUtilityTest.java`
- Targeting "Partially Covered" Branches (High Density):
  - `handleBrowserException(Exception)`: Exhaustive testing of the if-else chain for
    all AuthenticationFlowError types and the generic Exception path.
  - `handleClientAuthException(Exception)`: Testing specific error codes and the generic error
    fallback.
  - `authenticate()` & `authenticateClient()`: Testing the false branches for challenge/success
    checks.
- Targeting "Methods Not Entered":
  - URL/URI Utilities: `getRefreshUrl()`, `getActionUrl()`, `getActionTokenUrl()`,
    `getRefreshExecutionUrl()`, `getSignedAuthSessionId()`.
  - Session/User Management: `setAutheticatedUser()`, `clearAuthenticatedUser()`,
    `attachSession()` (static), recreate.
  - Remaining Getters/Setters: `getClientAuthAttributes()`, `getFlowPath()`, etc.

##### Improvement Implementation
###### Stage 1
The agent generated `AuthenticationProcessorResultDeepDiveTest` with 17 tests.
All the tests that were planned are implemented.

###### Stage 2
The agent generated `AuthenticationProcessorErrorAndUtilityTest` with 10 tests.
These tests were missed from the plan:
- All methods mentioned URL/URI Utilities are still not entered.
- From Session/User Management, `attachSession()` is not entered.
- From Remaining Getters/Setters, `getFlowPath()` is not entered.

##### Final Coverage
OpenClover coverage is 60.9%, and decision coverage is 56.5%.
Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gemma4_AuthenticationProcessor_round2.md`.

##### Verdict
Initial Coverage was very low.
Some planned tests for the improvement phase were missed.
Nevertheless, the coverage increase was drastic.
It is not satisfactory, but I guess with one more improvement phase like this,
reaching 80% decision coverage is possible.
It is possible that giving the agent what were missed makes it easier for them to
generate test with higher coverage.
With all these 7 stages, token consumption was 55,611.

#### GPT-OSS
##### Initial Implementation
##### Initial Implementation Plan
###### Stage 1: Object life‑cycle & trivial helpers
- set/get boilerplate (browserFlow, session, connection, uriInfo, flowId/flowPath, client, realm, eventBuilder, request).
- `isBrowserFlow()`, `newEvent()`, `getEvent()`, `getRequest()`, `getFlowPath()`.
- `setAutheticatedUser()` warning when user conflict, `clearAuthenticatedUser()`.

###### Stage 2: Client‑session & URL helpers
- `generateCode()` (assert that it delegates to ClientSessionCode and updates parent timestamp).
- `getClientData()` (verify protocol selection and encode).
- `getSignedAuthSessionId()` (ensure correct delegation to AuthenticationSessionManager).
- `getRefreshUrl()` (check that query params are built correctly for both authSessionIdParam = true/false).
- `getActionUrl(String)` & `getActionTokenUrl(String)` produce correct paths and include signed session ID when needed.

###### Stage 3: Flow builder & client‑auth exception handling
- `createFlowExecution(String,AuthenticationExecutionModel)` – verify the three provider‑id branches (basic,
  form, client) and that an exception is thrown for unknown provider.
- `handleClientAuthException(Exception)` – test all 4 if branches (`CLIENT_NOT_FOUND`, `CLIENT_DISABLED`,
  `CLIENT_CREDENTIALS_SETUP_REQUIRED`, `default`).

###### Stage 4: Browser‑side exception handling & flow control
- `handleBrowserExceptionList(AuthenticationFlowException)` – verify the switch on `AuthenticationFlowError` produces the
  right event. error, the right `forms.addError`, and that the returned HTTP status is `BAD_REQUEST`.
- `handleBrowserException(Exception)` – cover all 9 if/else-if branches (various error types + generic).
- `resetFlow()` – check that `afterResetListener()` is executed when provided.

###### Stage 5: End‑to‑end authentication path
- `authenticateClient()` – mock `createFlowExecution()` to return a stub flow that returns a pending challenge, then
  a successful result; 
  verify that the response is null when flow is successful and that error path returns appropriate error.
- `authenticate()` – combine `authenticateOnly()` and `authenticationComplete()` stubs;
  verify that challenge in `authenticateOnly()` is returned directly.
- `authenticateOnly()` – verify that it throws `AuthenticationFlowException` on `UNKNOWN_USER` and
  returns null when flow success.
- `authenticationAction(String)` – verify that an unknown execution id resets flow, that user not found throws error,
  and that a successful flow leads to `authenticationComplete()`.
- All of the above should use a mocked AuthenticationFlow whose `processFlow()` / `processAction()` / 
  `isSuccessful()` methods return controlled values.

##### Initial Implementation
###### Stage 1
The agent generated `AuthenticationProcessorStage1Test` with 16 tests.
Only `isBrowserFlow()` was missed from the plan.

###### Stage 2
The agent generated `AuthenticationProcessorStage2Test` with a single test.
The agent has said additional tests would cover some other methods.
I won't give it a second chance to complete this stage since it said the stage 2 was complete.

These tests were missed from the plan:
- `getClientData()`
- `getSignedAuthSessionId()`
- `getRefreshUrl()`
- `getActionUrl(String)` & `getActionTokenUrl(String)`

###### Stage 3
The agent generated `AuthenticationProcessorStage3Test` with 10 tests.
All the tests that were planned are implemented.

###### Stage 4
The agent generated `AuthenticationProcessorStage4DetailedTest` with 8 tests.
Firstly, `AuthenticationProcessorStage4Test` was generated, but the model tried to look for something in project folder.
It was not able to find that file and it stopped. I prompted it to continue and the new test file was generated.
This file is basically a slight expansion to the previous file with 10 more lines.

All the tests that were planned are implemented.

###### Stage 5
The agent generated `AuthenticationProcessorStage5Test` with 5 tests.
All the tests that were planned are implemented. / These tests were missed from the plan:
- missed 1

##### Initial Coverage
OpenClover coverage is 28.9%, and decision coverage is 28.0%.

Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gpt-oss_AuthenticationProcessor_round1.md`.

##### Improvement Phase
The agent was given the missing coverage markdown file. It came up with this two-stage plan.
Then it listed 53 new tests.
I doubt that this model will implement all of them in these stages.

###### Stage 1: Direct method coverage
- Write minimal tests that invoke the missing methods directly.
- Use Mockito to supply necessary context (e.g., mock `AuthenticationSessionModel`, `RealmModel`, `ClientModel`,
  `KeycloakSession`, etc.).
- Assert expected return values or state changes.

###### Stage 2: Flow‑related behavior
- Create tests for the authentication flow methods (`authenticate()`, `authenticateClient()`, `authenticateOnly()`,
  `authenticationAction()`, `authenticationComplete()`, `finishAuthentication()`, `checkClientSession()`, etc.).
- Mock the `AuthenticationFlow` implementations (`DefaultAuthenticationFlow`, `FormAuthenticationFlow`,
  `ClientAuthenticationFlow`) and the `AuthenticationSessionManager`.
- Simulate success, failure, and challenge scenarios to hit the various branches in these methods.

##### Improvement Implementation
###### Stage 1
The agent generated `AuthenticationProcessorDirectMethodsTest` with 6 tests.
The model didn't specify what tests exactly it was going to generate.

###### Stage 2
The agent generated `AuthenticationProcessorFlowTest` with 7 tests.
`authenticationComplete()` is not covered still.

##### Final Coverage
OpenClover coverage is 46.2%, and decision coverage is 38.7%.
Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gpt-oss_AuthenticationProcessor_round2.md`.

##### Verdict
The initial coverage was despondent.
Like Gemma4, OpenClover coverage was improved by about 60%. However,
the decision coverage was not improve that much compared to Gemma4.

This model plan to improve coverage was too large for a two-stage plan. It needed more stages.


#### Comparison
| Title                            | Gemma4 | GPT-OSS |
|:---------------------------------|:------:|:-------:|
| Initial OpenClover Coverage      | 37.6%  |  28.9%  |
| Initial Decision Coverage        | 25.0%  |  28.0%  |
| Final OpenClover Coverage        | 60.9%  |  46.2%  |
| Final Decision Coverage          | 56.5%  |  38.7%  |
| total tests                      |   69   |   53    |
| Context Window Usage             | 55,611 | 66,687  |

Clearly Gemma4 is winner.
Not only it used fewer tokens (at least in terms of context window),
but also it achieved much higher coverage.

### AuthorizationEndpoint
This file has 474 LOC, 14 methods, 210 statements, and 60 branches.

#### Gemma4
##### Initial Implementation Plan
###### Stage 1: Entry Points, Client Validation, and Simple Actions
Focus: Validating the initial request lifecycle and client security checks.
- Test Scenarios:
  - `buildGet()` and `buildPost()` basic invocation.
  - `authorizeDevice()`: Feature enabled vs. disabled (via Profile.isFeatureEnabled).
  - `checkClient()`:
    - Success scenario.
    - Missing `client_id` parameter.
    - `Client` not found in realm.
    - `Client` is disabled.
    - `Client` is bearer-only.
    - `Client` has incorrect protocol (not openid-connect).
  - `register()`: Basic flow and organization permission check.
  - `forgotCredentials()`: Basic flow and realm reset password permission check.

###### Stage 2: Request Parsing and Initial Security Handshakes
Focus: The beginning of the `process()` method and early `AuthorizationEndpointChecker` validation.
- Test Scenarios:
  - `ClientPolicyException`: Triggering `PreAuthorizationRequestContext` and
    verifying error response/exception.
  - `AuthorizationEndpointChecker` (Initial Checks):
    - `checkRedirectUri()`: Success and failure (redirection to error page).
    - `checkResponseType()`: Success, invalid type, and handling of `isInvalidResponseType`.
    - `checkResponseMode()`: Success and fallback to QUERY mode.

###### Stage 3: Advanced Authorization & DPoP/PAR Logic
Focus: Deep validation of the OIDC request and PAR (Pushed Authorization Requests) integration.
- Test Scenarios:
  - `AuthorizationEndpointChecker` (Deep Checks):
    - `checkParRequired()`, `checkInvalidRequestMessage()`, `checkAuthorizationDetails()`.
    - `checkOIDCRequest()`, `checkValidScope()`, `checkValidResource()`.
    - `checkOIDCParams()`, `checkPKCEParams()`, `checkProviderAddOns()`.
  - Handling of `AuthorizationCheckException` for each check above.
  - DPoP Support: Verifying that `dpop_jkt` from the session is correctly applied to the request.

###### Stage 4: Session Management and ACR/LoA Compliance
Focus: Ensuring the `AuthenticationSessionModel` is correctly populated and security levels (ACR) are
enforced.
- Test Scenarios:
  - `AuthorizationRequestContext` policy trigger: Success and `ClientPolicyException` handling (including session removal).
  - `updateAuthenticationSession()`:
    - Verification of protocol, redirect URI, and standard client notes.
    - ACR/LoA Logic:
      - Successful ACR mapping.
      - Handling of unknown ACR values (essential vs. non-essential).
      - Enforcing minimum LoA.
  - `AdditionalReqParams`: Verifying that extra parameters are prefixed and stored in notes.

###### Stage 5: Action Dispatch and Parameter Utilities
Focus: The final dispatching logic and static utility testing.
- Test Scenarios:
  - switch (action) logic:
    - `Action.CODE`: Verifying the path to `buildAuthorizationCodeAuthorizationResponse()`.
    - `Action.REGISTER`: Verifying the path to `buildRegister()` (including prompt=create).
    - `Action.FORGOT_CREDENTIALS`: Verifying the path to `buildForgotCredential()`.
  - `buildRegister()` / `buildForgotCredential()`: Verification of `AuthenticationProcessor` invocation.
  - `performActionOnParameters()`: Static method testing for all supported OAuth2/OIDC parameters.

##### Initial Implementation
The generated test files did not end with Test. Maven was not identifying them as test files. I had to rename them.

###### Stage 1
The agent generated `AuthorizationEndpointTestStage1` with 10 tests.
These tests were missed from the plan:
- `buildPost()` and `buildForgotCredential()` are not covered.

###### Stage 2
The agent generated `AuthorizationEndpointTestStage2` with 4 tests.
All the tests that were planned are implemented.

###### Stage 3
The agent generated `AuthorizationEndpointTestStage3` with 2 tests.
All the tests that were planned are implemented.

###### Stage 4
The agent generated `AuthorizationEndpointTestStage4` with 6 tests.
All the tests that were planned are implemented.

###### Stage 5
The agent generated `AuthorizationEndpointTestStage5` with 4 tests.
These tests were missed from the plan:
- `buildRegister()`
- `buildForgotCredential()`

Tests `action_REGISTER_Dispatch_CallsBuildRegister()` and
`action_FORGOT_CREDENTIALS_Dispatch_CallsBuildForgotCredential()` were removed duo to attempt to access a private method.

##### Initial Coverage
OpenClover coverage is 81.7%, and decision coverage is 71.7%.

Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gemma4_AuthorizationEndpoint_round1.md`.

##### Improvement Phase
The agent was given the missing coverage markdown file. It came up with this two-stage plan

###### Stage 1: Method Entry and High-Level Decision Branches
This stage focuses on entering previously unvisited methods and
covering the primary logical branches in the request processing flow.
- Targeted Methods: `buildPost()`, `register(String)`, `checkClient(String)`, and `process(...)`.
- Scenarios:
  - `buildPost()`: Basic entry test to ensure the method is reachable.
  - `register(String)`:
    - `Organization` feature enabled with a valid token and successful processing.
    - `Organization` feature enabled with a token that triggers an error.
    - Scenario where registration is disallowed by the realm.
      - `checkClient(String)`: Scenario where a client has a null protocol, triggering the fallback to openid-connect.
      - `process(...)`:
    - Prompt Logic: Testing the prompt=create path (both when registration is allowed and disallowed).
    - Action Fallback: Testing the scenario where action is null, ensuring it defaults to CODE.
    - `ResponseType` Validation: Testing the branch where `isInvalidResponseType()` is true to cover line 178.

###### Stage 2: Response Formatting and Deep Parameter Logic
This stage targets the fine-grained branches within the error-handling and session-update logic.
- Targeted Methods: `redirectErrorToClient(...)` and `updateAuthenticationSession()`.
- Scenarios:
  - `redirectErrorToClient(...)`:
    - Redirecting without an `ERROR_DESCRIPTION` (covering the false branch of line 326).
    - Redirecting without a state parameter (covering the false branch of line 330).
    - Testing the configuration where the issuer is excluded from the auth response (covering the false branch of line 335).
  - `updateAuthenticationSession()`:
    - UI Locales: Verifying that ui_locales are correctly set in client notes when present (covering line 352).
    - ACR/LoA Edge Cases: Handling the scenario where an ACR value is not a number, resulting in a null LoA (covering
      the false branch of line 374).
    - Additional Parameters:
      - Verifying that `additional_request_params` are processed (covering line 391).
      - Verifying that `AUTHORIZATION_DETAILS` from PAR are correctly stored (covering line 398).

##### Improvement Implementation
###### Stage 1
The agent generated `AuthorizationEndpointCoverageTestStage1` with 8 tests.
All the tests that were planned are implemented.

Tests `process_PromptCreate_RegistrationAllowed_ReturnsBuildRegister()`, `process_ActionIsNull_DefaultsToCode()`, and
`process_InvalidResponseType_Branch178()` were removed duo to attempt to access a private member.

###### Stage 2
The agent generated `AuthorizationEndpointCoverageTestStage2` with 7 tests.
All the tests that were planned are implemented.

##### Final Coverage
OpenClover coverage is 89.4%, and decision coverage is 91.7%.
Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gemma4_AuthorizationEndpoint_round2.md`.

##### Verdict
In total, 1,468,959 input tokens and 50,202 output tokens were used. 258,240 tokens were cached.
This is one of the least complex files for this report. However, it did require many mocks.

The initial coverage was very good. OpenClover coverage was already satisfactory.
Honestly, I wouldn't do the improvement phase if I was not doing this project and
trying to achieve 80% decision coverage.

The final coverage is superb. 90% coverage leaves no room for complain.
Although I admit, 1.5M tokens were used.
If you run this model on your own system smoothly, you will never worry about the token usage.

#### GPT-OSS
I was not able to generate tests with this model from the provider I was using during the improvement phase,
so I ran this model on my computer.
I was not able to measure how many tokens were used for that phase.

##### Initial Implementation Plan
###### Stage 1: Basic routing & delegation
Verify that `buildPost()` & `buildGet()` call the common `process(...)` with the right parameter source.

###### Stage 2: Client‑validation & error handling
All branches in `checkClient()`, the initial pre‑policy call,
and the first `AuthorizationEndpointChecker` exception handling.

###### Stage 3: Response‑type & mode logic
`checker.checkResponseType()`, parsing of invalid response types, and `redirectErrorToClient()` path.

###### Stage 4: Action flows & special prompts
action handling (`REGISTER`, `FORGOT_CREDENTIALS`, `CODE`), prompt=create, `dpopJkt` override,
`buildRegister()` / `buildForgotCredential()` delegation.

###### Stage 5: Device flow & edge‑case coverage
`authorizeDevice()`, `redirectErrorToClient()` with missing state, `updateAuthenticationSession()` acr/locale handling.

##### Initial Implementation
###### Stage 1
The agent generated `AuthorizationEndpointRoutingTest` with 2 tests.
All the tests that were planned are implemented.

###### Stage 2
The agent generated `AuthorizationEndpointClientCheckTest` with 6 tests.
All tests were removed duo to attempt to access a private member.

###### Stage 3
The agent generated `AuthorizationEndpointResponseModeTest` with 4 tests.
All the tests that were planned are implemented.

`testRedirectErrorWithNoDescription()` and `testRedirectErrorToClientWithState()` were
removed duo to attempt to access a private member.

###### Stage 4
The agent generated `AuthorizationEndpointActionTest` with 5 tests.
All tests were removed duo to attempt to access a private member.

###### Stage 5
The agent generated `AuthorizationEndpointEdgeTest` with 4 tests.
All the tests that were planned are implemented.

`testRedirectErrorToClient_IncludesStateIfPresent()` was removed duo to attempt to access a private member.

##### Initial Coverage
OpenClover coverage is 41.2%, and decision coverage is 36.7%.
Low coverage is credited to many tests removed because of inaccessible members.

Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gpt-oss_AuthorizationEndpoint_round1.md`.

##### Improvement Phase
The agent was given the missing coverage markdown file. It came up with this two-stage plan

###### Stage 1: New test classes for uncovered methods
- `AuthorizationEndpointRegisterTest` test class for target method `register(String)`
- `AuthorizationEndpointForgotCredentialsTest` test class for target method `forgotCredentials()`
- `AuthorizationEndpointAuthorizationCodeResponseTest`  test class for target method
  `buildAuthorizationCodeAuthorizationResponse(String)`
- `AuthorizationEndpointBuildRegisterTest`  test class for target method `buildRegister() `
- `AuthorizationEndpointBuildForgotTest`  test class for target method `buildForgotCredential()`
- `AuthorizationEndpointClientCheckDetailedTest`  test class for target method `checkClient(String)`

###### Stage 2: Tests for partially covered process branches
- `checker.isInvalidResponseType(ex)` true/false: Test with a custom `AuthorizationEndpointChecker` that returns
  a thrown `AuthorizationCheckException` and stubs `isInvalidResponseType` accordingly.
- `checker.getParsedResponseMode() == null`: Simulate a checker that returns null for `getParsedResponseMode` and
  verify that `OIDCResponseMode.QUERY` is used.
- `action == null` true/false: Set `endpoint.action` to `null` and `non‑null` and ensure `ACTION` defaults
  to `RESPONSE_TYPE = code`. Use reflection to trigger the code that sets the default action.
- `dpopJkt` handling true/false: Supply a session attribute `PAR_DPOP_PROOF_JKT` and an empty one, respectively.
- `prompt=create` true/false: Set `params.getFirst(OAuth2Constants.PROMPT)` to `create` and to something else,
  then verify whether `buildRegister()` is returned or not.
- `prompt` fallback to authentication session client note: Simulate `params` missing prompt and mock
  `authenticationSession.getClientNote(...)`.

##### Improvement Implementation
###### Stage 1
The agent generated `AuthorizationEndpointRegisterTest`, `AuthorizationEndpointForgotCredentialsTest`,
`AuthorizationEndpointAuthorizationCodeResponseTest`, `AuthorizationEndpointBuildRegisterTest`,
`AuthorizationEndpointBuildForgotTest`, and `AuthorizationEndpointClientCheckDetailedTest` with 8 tests.

These tests were removed duo to attempt to access a private/protected member.
- `testBuildForgotCredential()` from `AuthorizationEndpointBuildForgotTest`
- `testParRequestRedirect()` from `AuthorizationEndpointAuthorizationCodeResponseTest`
- `testMissingClientId()` from `AuthorizationEndpointClientCheckDetailedTest`
- `testBuildRegister()` from `AuthorizationEndpointBuildRegisterTest`
- `testForgotCredentialsAllowed()` from `AuthorizationEndpointForgotCredentialsTest`
- `testRegisterAllowed()` from `AuthorizationEndpointRegisterTest`

###### Stage 2
The agent generated `AuthorizationEndpointProcessBranchTest` with 5 tests.
The model did not generate any logic. In fact, all the tests were TODOs. They are removed.

##### Final Coverage
OpenClover coverage is 46.1%, and decision coverage is 41.7%.
Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gpt-oss_AuthorizationEndpoint_round2.md`.

Only two tests were qualified for this phase.
Removing a test since it tried accessing private members might be harsh.
But they are hard to fix through reflection or alternative routes.
Moreover, the model should have either used reflection or access that member through a legitimate way.
Let's not forget that the model did not generate any logic for the second stage.

##### Verdict
It was very weird of this model to generate six test files for stage 1 of improvement phase.

Before the improvement phase, 906,313 input tokens and 37,949 output tokens were consumed.

Removing tests trying to access private member did a serious number for this model.
The coverage is terrible. Gemma achieved an astonishing coverage.

#### Comparison
| Title                            |  Gemma4   | GPT-OSS |
|:---------------------------------|:---------:|:-------:|
| Initial OpenClover Coverage      |   81.7%   |  41.2%  |
| Initial Decision Coverage        |   71.7%   |  36.7%  |
| Final OpenClover Coverage        |   89.4%   |  46.1%  |
| Final Decision Coverage          |   91.7%   |  41.7%  |
| Total Tests                      |    35     |   11    |
| Context Window Usage             |  67,865   | 54,790  |
| Total Input Tokens               | 1,468,959 |    _    |
| Total Cached Tokens              |  258,240  |    _    |
| Total Output Tokens              |  50,202   |    _    |
| Total Tokens used                | 1,260,921 |    _    |
| Tokens per OpenClover Coverage % |  14,104   |    _    |

Gemma4 wins here without any question. It was GPT-OSS fault to fail generating enough tests.
Even with me removing some of the tests, it was not going to reach the same amount as gemma.
Despite this being a small file, both models used a surprising amount of tokens.
If we were using a high-end model, the cost would have been very high.
Although, it's probably possible to generate tests with high coverage with those models in one go.
Still, this made me think to find a way to reduce token usage. Luckily, 9router had some solutions.

### JpaUserProvider
This file has 1,402 LOC, 85 methods, 595 statements, and 158 branches.

#### Gemma4
##### Initial Implementation Plan
###### Stage 1: Core User Management & Retrieval
Scope:
- User Lifecycle: `addUser()` (both variants), `removeUser()`.
- Lookup: `getUserById()`, `getUserByUsername()`, `getUserByEmail()`.
- Identity Retrieval: `getUserByFederatedIdentity()`, `getFederatedIdentity()`, `getFederatedIdentitiesStream()`,
  `getServiceAccount()`.
- Counting & Searching: All `getUsersCount()` variants, `getGroupMembersStream()`, `getRoleMembersStream()`,
  `searchForUserStream()`, and `searchForUserByUserAttributeStream()`.
- Key Challenges: Mocking the `CriteriaBuilder` logic for complex search predicates and
  the different `getUsersCount()` implementations.

###### Stage 2: Consent and Federated Identity Management
Scope:
- Federated Identity: `addFederatedIdentity()`, `updateFederatedIdentity()`, `removeFederatedIdentity()`.
- Consent Management: `addConsent()`, `getConsentByClient()`, `getConsentsStream()`, `updateConsent()`,
  `revokeConsentForClient()`.
- Key Challenges: Simulating `StorageId` logic (local vs. external clients) to ensure correct query names and parameters are used.

###### Stage 3: Verifiable Credentials (VCs)
Scope:
- User VCs: `addVerifiableCredential()`, `removeVerifiableCredential()`, `getVerifiableCredentialsByUser()`,
  `getVerifiableCredentialById()`, `getVerifiableCredentialByClientScope()`, `updateVerifiableCredential()`.
- Issued VCs: `addIssuedVerifiableCredential()`, `getIssuedVerifiableCredentialsStreamByUser()`,
  `removeIssuedVerifiableCredential()`, `removeExpiredIssuedVerifiableCredentials()`.
- Key Challenges: Handling JSON serialization/deserialization of user attributes and testing revision/expiry logic.

###### Stage 4: Cleanup & Removal Lifecycle Hooks
Scope:
- Lifecycle Hooks: All `preRemove()` overloads (for `IdentityProviderModel`, `RealmModel`, `RoleModel`, `ClientModel`,
  `ClientScopeModel`, `GroupModel`, and `ProtocolMapperModel`).
- Cleanup Operations: `removeImportedUsers()`, `unlinkUsers()`, `removeConsentByClientStorageProvider()`.
- Key Challenges: Verifying that the correct sequence of `NamedQueries` is executed with exact parameters during
  cascading deletes.

###### Stage 5: Credential Management & User Properties
Scope:
- Credential Lifecycle: `createCredential()`, `updateCredential()`, `removeStoredCredential()`, `getStoredCredentialById()`,
  `getStoredCredentialsStream()`, `getStoredCredentialsByTypeStream()`, `getStoredCredentialByNameAndType()`,
  `moveCredentialTo()`.
- User Properties: `setNotBeforeForUser()`, `getNotBeforeOfUser()`, `grantToAllUsers()`, `getUserCredentialManager()`.
- Key Challenges: Testing the delegation to `JpaUserCredentialStore` and ensuring user state changes (like
  `notBefore()`) are correctly persisted.

##### Initial Implementation
The model decided to generate stages 3, 4, and 5 in one go despite me prompting it to generate stage 3.
###### Stage 1
The agent generated `JpaUserProviderStage1Test` with 20 tests.
All the tests that were planned are implemented.

###### Stage 2
The agent generated `JpaUserProviderStage2Test` with 13 tests.
All the tests that were planned are implemented.

###### Stage 3
The agent generated `JpaUserProviderStage3Test` with 11 tests.
These tests were missed from the plan:
- From User VCs, `getVerifiableCredentialByClientScope()` and `updateVerifiableCredential()` are not tested.
- From Issued VCs, `getIssuedVerifiableCredentialsStreamByUser()` is not covered.

###### Stage 4
The agent generated `JpaUserProviderStage4Test` with 12 tests.
From Lifecycle Hooks, `preRemove(ClientScopeModel)` overload is not tested.

###### Stage 5
The agent generated `JpaUserProviderStage5Test` with 14 tests.
All the tests that were planned are implemented.

##### Initial Coverage
OpenClover coverage is 66.1%, and decision coverage is 53.1%. This is beyond my expectation for such a complex file.

Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gemma4_JpaUserProvider_round1.md`.

##### Improvement Phase
The agent was given the missing coverage markdown file. It came up with this two-stage plan

###### Stage 1: Deepening Branch Coverage for Complex Logic
This stage focuses on the "Partially Covered Methods" identified in the report.
These methods contain significant conditional logic (branches) that are currently missing coverage.
- User & Consent Logic:
  - `addUser()`: Test branches for null ID, batch mode (`isBatchMode()`), and the application of default roles/actions.
  - `addConsent()` & `getConsent()`: Test the branch for external client storage providers.
  - `toConsentModel()` & `updateGrantedConsentEntity()`: Test complex branches for client existence,
    parameterization of client scopes, and handling of null/empty scopes.
- Verifiable Credentials (VC) Logic:
  - `addVerifiableCredential()`: Test branches for provided vs. generated revisions, null timestamps, and null user attributes.
- Search & Filtering Engine:
  - `predicates()` & `getSearchOptionPredicate()`: This is critical. Test the vast branching logic for
    different attribute types (exact vs. fuzzy/like), search patterns (exact quoted vs. wildcard),
    and various attribute keys (IDs, booleans, dates, etc.).
- Security & Constraints:
  - `ensureEmailConstraint()`: Test branches for duplicate email detection and realm-specific settings.

###### Stage 2: Completing Unentered Methods & Final Edge Cases
This stage focuses on the "Methods Not Entered" list and the remaining edge cases to reach the $\ge 80\%$ goal.
- Unentered Methods:
  - Convenience & Helpers: `addUser(RealmModel, String)`, `getSession()`, `getEntityManager()`,
    and the `countQuery()` helper.
  - Stream & Collection Methods: The parameterized `getGroupMembersStream()`, `getRoleMembersStream()`, and
    `getIssuedVerifiableCredentialsStreamByUser()`.
  - Lifecycle & Specialized Lookups: `preRemove(ClientScopeModel)`, `updateVerifiableCredential()`, and
    `getVerifiableCredentialByClientScope()`.
- Error & Edge State Scenarios:
  - Multiple Results: Test scenarios where `getUserByEmail()`, `getUserByFederatedIdentity()`, or
    `getServiceAccount()` return more than one result (triggering `IllegalStateException`).
  - Null/NotFound States: Test `setNotBeforeForUser()`, `getNotBeforeOfUser()`, and `createCredential()` when
    the user is not found or not in the persistence context.
  - Role Logic: Test the `grantToAllUsers()` branch for client roles vs. realm roles.

##### Improvement Implementation
###### Stage 1
The agent generated `JpaUserProviderCoverageStage1Test` with 16 tests.
All the tests that were planned are implemented.


###### Stage 2
The agent generated `JpaUserProviderCoverageStage2Test` with 9 tests.
None of the methods mentioned in Lifecycle & Specialized Lookups are covered.

##### Final Coverage
OpenClover coverage is 77.0%, and decision coverage is 71.3%.
Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gemma4_JpaUserProvider_round2.md`.

##### Verdict
In total, 1,217,035 input tokens and 41,202 output tokens were used. 167,936 tokens were cached.

The initial coverage was excellent. Even though this file is more complex that `AuthenticationProcessor`,
Gemma manages to cover more portion of the code. Generating stages 3 to 5 in one go didn't have negative effects.

Compared to the last file, this file is much more complex.
With the token saver enabled, the token usage in this file is actually lower than the previous file.

The coverage improvement is medium.
OpenClover coverage is almost satisfactory.
Decision coverage was improved by 18%.
318,110 Tokens in total were used for improvement phase.
About 30% more token usage for this improvement might not be worth it. Perhaps doing improvement phase in a new session
will reduce the token usage.

#### GPT-OSS
##### Initial Implementation Plan
###### Stage 1: User Creation / Deletion
What it covers:

| Test Method                              | What it covers                                                           |
|------------------------------------------|--------------------------------------------------------------------------|
| `testAddUser_generatesIdWhenNull()`      | `id==null` branch → `KeycloakModelUtils.generateId()` is called          |
| `testAddUser_assignsUsernameLowercase()` | `entity.setUsername(username.toLowerCase())`                             |
| `testAddUser_defaultRolesAndGroups()`    | `addDefaultRoles==true` → `grantRole()` + `joinGroupImpl()`              |
| `testAddUser_defaultRequiredActions()`   | `addDefaultRequiredActions==true` → iterate over enabled default actions |
| `testRemoveUser_nonexistent()`           | `userEntity==null` → return `false`                                      |
| `testRemoveUser_existent()`              | `userEntity!=null` → calls 6 named queries + `em.remove()`               |
| `testAddFederatedIdentity_persist()`     | Straight‑forward persist                                                 |


###### Stage 2: Federated Identity & Consent
What it covers:

| Test Method                              | What it covers                                                        |
|------------------------------------------|-----------------------------------------------------------------------|
| `testUpdateFederatedIdentity_notFound()` | `findFederatedIdentity()==null` → early return                        |
| `testUpdateFederatedIdentity_found()`    | Update path → `setUserName()`, `setToken()`, `persist()`, `flush()`   |
| `testRemoveFederatedIdentity_found()`    | Entity exists → remove + flush + return `true`                        |
| `testRemoveFederatedIdentity_notFound()` | Entity null → return `false`                                          |
| `testAddConsent_localClient()`           | `StorageId.isLocal()==true` → set `clientId`                          |
| `testAddConsent_externalClient()`        | `isLocal()==false` → set `clientStorageProvider` & `externalClientId` |
| `testUpdateConsent_notFound()`           | `getGrantedConsentEntity()==null` → throw `ModelException`            |
| `testUpdateConsent_found()`              | Consent found → `updateGrantedConsentEntity()` + `flush()`            |
| `testRevokeConsentForClient_notFound()`  | No entity → return `false`                                            |
| `testRevokeConsentForClient_found()`     | Entity present → `remove()`, flush(), return `true`                   |

###### Stage 3: Verifiable Credentials
What it covers:

| Test Method                                       | What it covers                                                        |
|---------------------------------------------------|-----------------------------------------------------------------------|
| `testAddVerifiableCredential_missingScope()`      | `clientScopeId==null` → throw                                         |
| `testAddVerifiableCredential_withAttributes()`    | `verifCredentialModel.getUserAttributes()!=null` → serialize          |
| `testAddVerifiableCredential_withoutAttributes()` | `verifCredentialModel.getUserAttributes()==null` → fetch user profile |
| `testRemoveVerifiableCredential_notFound()`       | No matching VC → return `false`                                       |
| `testRemoveVerifiableCredential_found()`          | VC found → delete issued VCs + remove + flush                         |
| `testGetVerifiableCredentialsByUser_sorted()`     | Ensure sorting by `clientScopeId`                                     |
| `testUpdateVerifiableCredential_userNotFound()`   | `em.find()` returns `null` → throw                                    |
| `testUpdateVerifiableCredential_success()`        | Successful update + `merge()` + `flush()`                             |
| `testUpdateVerifiableCredential_optimisticLock()` | `merge()` throws `OptimisticLockException` → wrap in `ModelException` |


###### Stage 4: User Lookup & Counts
What it covers:

| Test Method                                  | What it covers                                        |
|----------------------------------------------|-------------------------------------------------------|
| `testGetUserById_existing()`                 | `em.find()` returns entity → returns adapter          |
| `testGetUserById_nonexistent()`              | `find()` returns null → return null                   |
| `testGetUserById_wrongRealm()`               | Entity found but realm mismatch → return null         |
| `testGetUserByEmail_multipleResults()`       | `results.size()>1` → throw `IllegalStateException`    |
| `testGetUserByEmail_noResults()`             | No results → return `null`                            |
| `testGetUserByEmail_valid()`                 | Valid single result                                   |
| `testGetServiceAccount_noResults()`          | `Query` empty → return `null`                         |
| `testGetServiceAccount_multipleResults()`    | List `size>1` → exception                             |
| `testGetServiceAccount_singleResult()`       | Exactly one → return adapter                          |
| `testGrantToAllUsers_matchingContainer()`    | `realm.equals(role container)` → named query executed |
| `testGrantToAllUsers_nonMatchingContainer()` | No query executed                                     |
| `testSetNotBeforeForUser_nullEntity()`       | `em.getReference()` returns `null` → throw            |
| `testSetNotBeforeForUser_success()`          | Normal path                                           |
| `testGetNotBeforeOfUser_success()`           | `Entity` found → return value                         |
| `testGetNotBeforeOfUser_nullEntity()`        | `Entity==null` → throw                                |
| `testGetUsersCount_includeServiceAccount()`  | `includeServiceAccount==false` → predicate added      |
| `testGetUsersCount_groupIdsEmpty()`          | `groupIds==null/empty` → return 0                     |
| `testGetUsersCount_search()`                 | Search predicates added                               |


###### Stage 5: Streams, Pre‑Remove & Misc.
What it covers:

| Test Method                                  | What it covers                                                             |
|----------------------------------------------|----------------------------------------------------------------------------|
| `testGetGroupMembersStream_noSearch()`       | Simple stream without search                                               |
| `testGetGroupMembersStream_exactSearch()`    | `exact==true` branch                                                       |
| `testGetGroupMembersStream_nonExactSearch()` | `exact==false` branch                                                      |
| `testGetRoleMembersStream()`                 | Stream of role members                                                     |
| `testSearchForUserStream_exact()`            | `exact==true` branch                                                       |
| `testSearchForUserStream_nonExact()`         | `exact==false` branch                                                      |
| `testPreRemove_identityProvider()`           | Named query executed                                                       |
| `testPreRemove_role()`                       | `deleteUserRoleMappingsByRole()` executed                                  |
| `testPreRemove_clientLocal()`                | Local client branch                                                        |
| `testPreRemove_clientExternal()`             | External client branch                                                     |
| `testPreRemove_clientScope()`                | Named queries for clientScope                                              |
| `testPreRemove_component()`                  | `removeImportedUsers()` or `removeConsentByClientStorageProvider()` called |

##### Initial Implementation
###### Stage 1
The agent generated `JpaUserProviderAddRemoveTest` with 1 test.  
Only the first test was generated and other tests are not present.

###### Stage 2
The agent generated `JpaUserProviderIdentityConsentTest` with 6 tests.
These tests were missed from the plan:
- `testUpdateConsent_notFound()`
- `testUpdateConsent_found()`
- `testRevokeConsentForClient_notFound()`
- `testRevokeConsentForClient_found()`

`testUpdateFederatedIdentity_notFound()`, `testUpdateFederatedIdentity_found()`,
`testRemoveFederatedIdentity_found()`, and `testRemoveFederatedIdentity_notFound()` are
removed duo to attempt to access a private member.

###### Stage 3
The agent generated `JpaUserProviderVerifiableCredentialTest` with 7 tests.
These tests were missed from the plan:
- `testGetVerifiableCredentialsByUser_sorted()`
- `testUpdateVerifiableCredential_userNotFound()`
- `testUpdateVerifiableCredential_optimisticLock()`

`testRemoveVerifiableCredential_notFound()`, `testRemoveVerifiableCredential_found()`,
`testUpdateVerifiableCredential_userNotFound()`, and `testUpdateVerifiableCredential_success()`
are removed duo to attempt to access a private member.

###### Stage 4
The agent generated `JpaUserProviderUserInfoTest` with 11 tests.
These tests were missed from the plan:
- `testGetUserById_existing()`
- `testGetUserById_nonexistent()`
- `testGetUserById_wrongRealm()`
- `testGetUserByEmail_multipleResults()`
- `testGetUserByEmail_noResults()`
- `testGetUserByEmail_valid()`
- `testGetUsersCount_includeServiceAccount()`
- `testGetUsersCount_groupIdsEmpty()`
- `testGetUsersCount_search()`

Moreover, `testGetUsersCount_groupIdsNull_returnsZero()` and `testGetUsersCount_includesServiceAccountFalse()`
were generated,  but they were not in the plan.

###### Stage 5
The agent generated `JpaUserProviderStreamsPreRemoveTest` with 7 tests.
These tests were missed from the plan:
- `testGetGroupMembersStream_noSearch()`
- `testGetGroupMembersStream_exactSearch()`
- `testGetGroupMembersStream_nonExactSearch()`
- `testGetRoleMembersStream()`
- `testSearchForUserStream_exact()`
- `testSearchForUserStream_nonExact()`
- `testPreRemove_component()`

Moreover, `testPreRemove_componentTypeUserStorage()` and `testPreRemove_componentTypeClientStorage()`
were generated which weren't in the plan.

##### Initial Coverage
OpenClover coverage is 23.3%, and decision coverage is 21.5%.

Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gpt-oss_JpaUserProvider_round1.md`.

##### Improvement Phase
The agent was given the missing coverage markdown file. It came up with this two-stage plan:

###### Stage 1: “Not Entered” methods
Purpose: Exercise every public method that the current test suite never invokes.  
New test files:
1. `JpaUserProviderAddMethodFalseBranchTest`
2. `JpaUserProviderFederatedUserTest`
3. `JpaUserProviderConsentBaseTest`
4. `JpaUserProviderVerifiableCredentialBaseTest`
5. `JpaUserProviderStreamAndRemoveTest`

###### Stage 2: “Partially Covered” branches
Purpose: Cover the `true` and `false` paths that were left, plus the specific snippets flagged in the report.  
New test files:
1. `JpaUserProviderCreateUserFalseBranchTest`
2. `JpaUserProviderConsentEntityBranchTest`
3. `JpaUserProviderCreateVerifiableCredentialFalseBranchTest`
4. `JpaUserProviderExecutorGrantTest`
5. `JpaUserProviderUserByIdAndCountTest`
6. `JpaUserProviderUserCountFalseBranchTest`
7. `JpaUserProviderToConsentModelTest`

##### Improvement Implementation
###### Stage 1
The agent generated `JpaUserProviderNotEnteredTest` with 34 tests.
Only one file was generated. Despite mentioning covering every public method,
many tests called private methods.

These are removed duo to attempt to access private members:
- `testUpdateFederatedIdentityFound()`
- `testUpdateFederatedIdentityNotFound()`
- `testRemoveFederatedIdentityFound()`
- `testRemoveFederatedIdentityNotFound()`
- `testGetConsentByClientNull()`
- `testGetConsentByClientPresent()`
- `testUpdateConsentEntityMissing()`
- `testRevokeConsentForClientNotFound()`
- `testRevokeConsentForClientFound()`
- `testToConsentModelWithClientId()`
- `testToConsentModelWithExternalClient()`
- `testCreateClientScopeEntityAddsNew()`
- `testCreateClientScopeEntityExisting()`
- `testRemoveVerifiableCredentialNotFound()`
- `testRemoveVerifiableCredentialFound()`
- `testGetVerifiableCredentialsByUserSorted()`
- `testGetVerifiableCredentialByClientScopeNotFound()`
- `getVerifiableCredentialsEntitiesByUser()`
- `testUpdateVerifiableCredentialSuccessfully()`
- `testUpdateVerifiableCredentialOptimisticLock()`
- `testGetVerifiableCredentialsEntitiesByUser()`

21 tests are lost.

###### Stage 2
The agent generated `JpaUserProviderPartialBranchTest` with 9 tests.
Only one file was generated, despite listing seven files.

`testCreateUserConsentClientScopeEntityRemoval()` and `testToConsentModelNullEntit()` are removed duo
to attempt to access private members.

##### Final Coverage
OpenClover coverage is 32.7%, and decision coverage is 32.9%.
Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gpt-oss_JpaUserProvider_round2.md`.

484,327 input tokens and 16,962 output tokens were consumed for this phase.

##### Verdict
In total, 1,190,094 input tokens and 33,875 output tokens were used. no token was cached.

The initial coverage is terrible.
Missing many of the planned tests and some tests removed duo to violations are accounting for this low coverage.
Gemma is already the winner in the end.

The improvement wasn't that much either. Many tests were removed again for violations.

#### Comparison
| Title                            |  Gemma4   |  GPT-OSS  |
|:---------------------------------|:---------:|:---------:|
| Initial OpenClover Coverage      |   66.1%   |   23.3%   |
| Initial Decision Coverage        |   53.1%   |   21.5%   |
| Final OpenClover Coverage        |   77.0%   |   32.7%   |
| Final Decision Coverage          |   71.3%   |   32.9%   |
| Total Tests                      |    95     |    44     |
| Context Window Usage             |  64,862   |  54,042   |
| Total Input Tokens               | 1,238,241 | 1,190,094 |
| Total Cached Tokens              |  167,936  |     0     |
| Total Output Tokens              |  41,202   |  33,875   |
| Total Tokens used                | 1,111,507 | 1,223,969 |
| Tokens per OpenClover Coverage % |  14,435   |  37,430   |

Clearly, gemma4 is the winner.
The total token usage is lower and coverage is much higher.
This is the third-largest file.
With one more improvement phase with Gemma4, coverage will become satisfactory for sure.

### KeycloakModelUtils
This file has 1,412 LOC, 92 methods, 464 statements, and 236 branches.

#### Gemma4
##### Initial Implementation Plan
###### Stage 1: Basic Utilities and Security Helpers
Scope: Fundamental utility methods and simple security/parsing logic.
- Methods: `generateId()`, `generateShortId()`, `isValidUUID()`, `toLowerCaseSafe()`, `parseRole()`,
  `buildRoleQualifier()`, `convertClientScopeName()`, `normalizeGroupPath()`, `getPublicKey()`, `getCertificate()`,
  `getPrivateKey()`, `getSecretKey()`, `getPemFromKey()`, `getPemFromCertificate()`, `generateKeyPairCertificate()`,
  `generateSecret()`, `getRequiredClientSecretLength()`, `generateCodeSecret()`.
- Focus: Null/empty inputs, valid/invalid UUIDs, successful vs. exception-throwing PEM decoding,
  and correctness of generated identifiers.

###### Stage 2: Client, Role, and Scope Management
Scope: Methods related to client and role lifecycle and lookup.
- Methods: `createManagementClient()`, `createPublicClient()`, `createClient()`, `getClientScopeByName()`,
  `findClientScopeById()`, `setupDefaultRole()`, `setupOfflineRole()`, `setupDeleteAccount()`,
  `getRoleFromString()` (both versions), `getRoleByName()`, `removeTransientAdminRoles()`,
  `getClientScopeMappingsStream()`, `getAcceptedClientScopeProtocols()`.
- Focus: Existence/non-existence of clients and roles, recursive role resolution, and edge cases in client authentication types.

###### Stage 3: User and Group/Organization Path Management
Scope: User attribute resolution and complex hierarchical pathing.
- Methods: `findUserByNameOrEmail()`, `searchFor()` (roles), `resolveAttribute()` (Group/User),
  `escapeSlashesInGroupPath()`, `findGroupByPath()` (String/Array/Org), `getGroupModel()` (recursive),
  `splitPath()`, `escapeGroupNameForPath()`, `unescapeGroupNameForPath()`, `buildGroupPath()` (various versions),
  `getOrgId()`, `getOrganizationForIdpMapper()`, `getGroupForIdpMapper()`, `setDefaultGroups()`.
- Focus: Recursive attribute resolution, complex path parsing (including escaped slashes),
  and group lookup within Realm vs. Organization hierarchies.

###### Stage 4: Transaction and Session Management
Scope: Managing sessions, context propagation, and transaction boundaries.
- Methods: `runJobInTransaction()` (both versions), `runJobInTransactionWithResult()` (both versions),
  `runJobInTransactionWithTimeout()`, `setTransactionLimit()`, `getTransactionLimit()`,
  `cloneContextRealmClientToSession()`, `cloneContextRealmClientSessionToSession()`, `runOnRealm()`,
  `componentModelGetter()`, `getComponentModel()`, `getComponentFactory()`, `createComponentModel()`,
  `suspendJtaTransaction()`.
- Focus: Transaction success vs. rollback (exception handling),
  context propagation (realm/client/auth session presence), and JTA transaction/factory interactions.

###### Stage 5: Authentication Flow and Identity Provider Management
Scope: Authentication flow lifecycles and Identity Provider (IdP) logic.
- Methods: `deepFindAuthenticationExecutions()`, `isFlowUsed()`, `deepDeleteAuthenticationFlow()`,
  `deepDeleteAuthenticationExecutor()`, `setupAuthorizationServices()`, `getIdentityProviderDisplayName()`,
  `getSecretLengthByAuthenticationType()`.
- Focus: Recursive deletion of flows and executors, isFlowUsed across various realm/client scenarios,
  and IdP display name/configuration resolution.

##### Initial Implementation
###### Stage 1
The agent generated `KeycloakModelUtilsBasicTest` with 15 tests.
These tests were missed from the plan:
- Methods `getPemFromKey()`, `getPemFromCertificate()`, `generateKeyPairCertificate()`, `generateSecret()` were
  not covered.

###### Stage 2
The agent generated `KeycloakModelUtilsClientRoleTest` with 16 tests.
These tests were missed from the plan:
- One of `getRoleFromString()` overloads.

`testRemoveTransientAdminRoles_WithRoles()` was disabled since it was not asserting or verifying anything.

###### Stage 3
The agent generated `KeycloakModelUtilsUserGroupTest` with 13 tests.
These tests were missed from the plan:
- Methods `getOrgId()`, `getGroupForIdpMapper()`, and `setDefaultGroups()` were not covered.
- `findGroupByPath(KeycloakSession, RealmModel, String[])` and `findGroupByPath(KeycloakSession,
  RealmModel, OrganizationModel, String)` overloads.

`testGetGroupForIdpMapper_Realm()` and `testSetDefaultGroups()` were disabled since they weren't
asserting or verifying anything. were not covered.

###### Stage 4
The agent generated `KeycloakModelUtilsSessionTest` with 9 tests.
All the tests that were planned are implemented. / These tests were missed from the plan:
- Methods `runJobInTransactionWithTimeout()`, `setTransactionLimit()`, `getTransactionLimit()`,
  `cloneContextRealmClientSessionToSession()`, `componentModelGetter()`, `getComponentModel()`,
  `getComponentFactory()`, and `suspendJtaTransaction()` were not covered.

###### Stage 5
The agent generated `KeycloakModelUtilsFlowIdpTest` with 9 tests.
These tests were missed from the plan:
- Methods `deepDeleteAuthenticationExecutor()`, `setupAuthorizationServices()` were not covered.

##### Initial Coverage
OpenClover coverage is 48.6%, and decision coverage is 39.0%.

Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gemma4_KeycloakModelUtils_round1.md`.

##### Improvement Phase
The agent was given the missing coverage markdown file. It came up with this two-stage plan

###### Stage 1: Security, Transactions, and Component Management
- Targeted Methods:
  - Security/PEM: `getPublicKey()`, `getCertificate()`, `getPrivateKey()` (including true branches), `getPemFromKey()`,
    `getPemFromCertificate()`, `generateKeyPairCertificate()` (both versions).
  - Transactions/Session: `runJobInTransactionWithTimeout()`, `setTransactionLimit()`, `getTransactionLimit()`,
    `suspendJtaTransaction()`, `cloneContextToSession()`, `cloneContextRealmClientSessionToSession()`.
  - Component Management: `getComponentModel()`, `getComponentFactory()`, `componentModelGetter()`,
    and all methods within the ScopeComponentModel inner class.
  - Misc: `getMasterRealmAdminManagementClientId()`, `getIdentityProviderDisplayName()`.

###### Stage 2: Hierarchy, Roles, and Authentication Flows
- Targeted Methods:
  - Hierarchy/Organization: `findGroupByPath()` (all variants), `getGroupModel()`, `buildGroupPath()` (all variants),
    `getOrgId()`, `resolveAttribute()`, `findUserByNameOrEmail()`.
  - Roles/Client Logic: `getRoleFromString()` (all variants), `removeTransientAdminRoles()`, `setupDeleteAccount()`,
    `generateSecret()`.
  - Flows/IdP Mappers: `isFlowUsed()`, `deepDeleteAuthenticationFlow()`, `deepDeleteAuthenticationExecutor()`,
    `setupAuthorizationServices()`, `setDefaultGroups()`, `getOrganizationForIdpMapper()`, `getGroupForIdpMapper()`.

##### Improvement Implementation
###### Stage 1
The agent generated `KeycloakModelUtilsSecuritySessionTest` with 9 tests.
All the tests that were planned are implemented. / These tests were missed from the plan:
- True branch in `getPrivateKey()`.
- From Security/PEM, `getPemFromKey()`, `getPemFromCertificate()`, and `generateKeyPairCertificate()` are not covered.
- From Transactions/Session, `suspendJtaTransaction()` and `cloneContextRealmClientSessionToSession()` are not tested.
- From Component Management, `getComponentFactory()` is not covered.

###### Stage 2
The agent generated `KeycloakModelUtilsHierarchyFlowTest` with 13 tests.
All the tests that were planned are implemented. / These tests were missed from the plan:
- `buildGroupPath(GroupModel group, GroupModel)` variant is not covered.
- From Roles/Client Logic, `generateSecret()` is not tested.
- From Flows/IdP Mappers, `deepDeleteAuthenticationExecutor()` is not covered.

##### Final Coverage
OpenClover coverage is 61.5%, and decision coverage is 49.2%.
Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gemma4_KeycloakModelUtils_round2.md`.

##### Verdict
In total, 1,217,459 input tokens and 31,308 output tokens were used. 239,040 tokens were cached.

Many Methods planned to be tested initially were not tested.
The initial coverage could have been much higher if everything in the mentioned plan was covered.

The final coverage is away from a satisfactory range.
Again, many methods that were planned to be tested in the improvement are not still covered.
10% improvement was achieved with 536,772 tokens. This was not worth it.

#### GPT-OSS
##### Initial Implementation Plan
The agent did not give a title for each stage.

###### Stage 1
- `generateId()`
- `generateShortId()`
- `isValidUUID()`
- `getPublicKey()`
- `getCertificate()`
- `getPrivateKey()`
- `getSecretKey()`
- `generateSecret()`
- `getRequiredClientSecretLength()`
- `generateCodeSecret()`

###### Stage 2
- `createManagementClient()`
- `createPublicClient()`
- `createClient()`
- `setupDefaultRole()`
- `setupOfflineRole()`
- `setupDeleteAccount()`

###### Stage 3
- `searchFor()`
- `getRoleFromString()` (no cache)
- `parseRole()`
- `buildRoleQualifier()`
- `getRoleByName()`
- `removeTransientAdminRoles()`

###### Stage 4
- `findGroupByPath()`
- `splitPath()`
- `escapeGroupNameForPath()`
- `unescapeGroupNameForPath()`
- `buildGroupPath()`
- `normalizeGroupPath()`
- `getOrganizationForIdpMapper()`
- `getGroupForIdpMapper()`

###### Stage 5
- `runJobInTransaction()`
- `runJobInTransactionWithResult()`
- `cloneContextRealmClientToSession()/cloneContextRealmClientSessionToSession()`
- `setTransactionLimit()`
- `getTransactionLimit()`
- `deepFindAuthenticationExecutions()`
- `isFlowUsed()`

##### Initial Implementation
###### Stage 1
The agent generated `KeycloakModelUtilsIdsTest` with 14 tests.
All the tests that were planned are implemented.

###### Stage 2
The agent generated `KeycloakModelUtilsClientTest` with 6 tests.
All the tests that were planned are implemented.

###### Stage 3
The agent generated `KeycloakModelUtilsRoleTest` with 12 tests.
All the tests that were planned are implemented.

###### Stage 4
The agent generated `KeycloakModelUtilsGroupTest` with 6 tests.
These tests were missed from the plan:
- `getOrganizationForIdpMapper()`
- `getGroupForIdpMapper()`

###### Stage 5
The agent generated `KeycloakModelUtilsSessionTest` with 3 tests.
These tests were missed from the plan:
- `runJobInTransaction()`
- `runJobInTransactionWithResult()`
- `cloneContextRealmClientSessionToSession()`
- `setTransactionLimit`
- `getTransactionLimit()`
- `deepFindAuthenticationExecutions()`
- `isFlowUsed()`

##### Initial Coverage
OpenClover coverage is 32.2%, and decision coverage is 29.2%.

Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gpt-oss_KeycloakModelUtils_round1.md`.

##### Improvement Phase
The agent was given the missing coverage markdown file. It came up with this two-stage plan

###### Stage 1: “Utilities & Session plumbing”
Purpose: Cover the remaining static / public helpers that simply encode/decoding bytes,
generate key‑pairs, run transactions, clone contexts, and build component models.

1. `KeycloakModelUtilsPemTest.java` (PEM encoding/decoding)
2. `KeycloakModelUtilsKeyPairTest.java` (certificate generation overloads)
3. `KeycloakModelUtilsUserTest.java` (user lookup)
4. `KeycloakModelUtilsTransactionTest.java` (runJobInTransaction overloads + timeout)
5. `KeycloakModelUtilsComponentTest.java` (componentModelGetter/getComponentModel/getComponentFactory)
6. `KeycloakModelUtilsContextTest2.java` (cloneContextRealmClientSessionToSession)
7. `KeycloakModelUtilsGenericTest.java` (getMasterRealmAdminManagementClientId, createComponentModel,
   toLowerCaseSafe, getPemFromKey, getPemFromCertificate)

###### Stage 2: “Group & Role/Authentication logic”
Purpose: Cover the remaining group resolution/attribute methods, role resolution with caching,
authentication flow traversal, and admin‑role pruning.

1. `KeycloakModelUtilsGroupResolveTest.java` (group path resolution, buildGroupPath recursion, normalizeGroupPath)
2. `KeycloakModelUtilsRoleResolveTest.java` (role resolve from string, getRoleByName,
   removeTransientAdminRoles branch)
3. `KeycloakModelUtilsAuthFlowTest.java` (deep authentication execution/flow delete,
   deepFindAuthenticationExecutions, isFlowUsed)
4. `KeycloakModelUtilsAttributeTest.java` (user & group attribute resolution)

##### Improvement Implementation
I had to give the model a "continue" prompt in stage 2. This used 200K input tokens.

###### Stage 1
The agent generated `KeycloakModelUtilsPemTest`, `KeycloakModelUtilsKeyPairTest`,
`KeycloakModelUtilsTransactionTest`, `KeycloakModelUtilsComponentTest`,
and `KeycloakModelUtilsMiscTest` with 14 tests.
These test files were missed from the plan:
- `KeycloakModelUtilsUserTest`
- `KeycloakModelUtilsContextTest2`
- `KeycloakModelUtilsGenericTest`

The agent generated `KeycloakModelUtilsMiscTest` outside the plan.

For fixing some of the tests, `TestCryptoProvider` had to be created.
This is definitely unfair, but given the fact that gpt-oss is going to lose anyway, it's fine.

###### Stage 2
The agent generated `KeycloakModelUtilsAuthFlowTest` with 3 tests.
These test files were missed from the plan:
- `KeycloakModelUtilsGroupResolveTest`
- `KeycloakModelUtilsRoleResolveTest`
- `KeycloakModelUtilsAttributeTest`

##### Final Coverage
OpenClover coverage is 42.4%, and decision coverage is 33.5%.
Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gpt-oss_KeycloakModelUtils_round2.md`.

722,884 input tokens and 9,194 output tokens were used for this phase.

##### Verdict
In total, 1,767,478 input tokens and 30,187 output tokens were used. no token was cached.

This model was doing well this time until it reached the end of stage 4.
From there, the majority of planned methods were missed.

The improvement is not drastic at all. I knew even with the massive help I gave this model,
it was not going to do better than Gemma4.
The token usage is unusually high. The token saver was active during the test generation.

#### Comparison
| Title                            |  Gemma4   |  GPT-OSS  |
|:---------------------------------|:---------:|:---------:|
| Initial OpenClover Coverage      |   48.6%   |   32.2%   |
| Initial Decision Coverage        |   39.0%   |   29.2%   |
| Final OpenClover Coverage        |   61.5%   |   42.4%   |
| Final Decision Coverage          |   49.2%   |   33.5%   |
| Total Tests                      |    91     |    58     |
| Context Window Usage             |  59,027   |  46,424   |
| Total Input Tokens               | 1,217,459 | 1,767,478 |
| Total Cached Tokens              |  239,040  |     0     |
| Total Output Tokens              |  31,308   |  30,187   |
| Total Tokens used                | 1,009,727 | 1,797,665 |
| Tokens per OpenClover Coverage % |  16,418   |  42,798   |

Even with the massive help I gave to GPT-OSS, the coverage improvement was less than Gemma4.
Gemma4 wins again in all aspects.
I am convinced that the token window cannot be used to compare token usage at all.
GPT-OSS missed many planned tests. Therefore, total tests are much lower than Gemma's.

### ModelToRepresentation
This file has 1,555 LOC, 74 methods, 887 statements, and 188 branches.

In the first attempt to generate a plan, the model got stuck in a loop in which it was just printing the plan, and
it was adding a minor thing to it every time. After some minutes, the model stopped without the final plan.
This used 73,292 input and 1,334 output tokens. I started a new session and I gave the initial prompt again.
This time, nothing went wrong.

#### Gemma4
##### Initial Implementation Plan
###### Stage 1: Groups & Roles
* `buildGroupPath()`
* `groupToBriefRepresentation()`
* `toRepresentation(GroupModel, boolean)`
* `toGroupHierarchy (all variants)`
* `toRepresentation(RoleModel)`
* `toBriefRepresentation(RoleModel)`

###### Stage 2: Users & Credentials
* `toRepresentation(KeycloakSession, RealmModel, UserModel)` (and overloads)
* `toBriefRepresentation(UserModel)`
* `toRepresentation(UserSessionModel)`
* `toRepresentation(UserCredentialModel)`
* `toRepresentation(CredentialModel)`
* `toRepresentation(CredentialMetadata)`

###### Stage 3: Realm & Identity Providers
* `toBriefRepresentation(RealmModel)`
* `toRepresentation(KeycloakSession, RealmModel, boolean, boolean)`
* `toBriefRepresentation(RealmModel, IdentityProviderModel)`
* `toRepresentation(KeycloakSession, RealmModel, IdentityProviderModel, boolean)`
* `toEventsConfigReprensetation()`

###### Stage 4: Clients & Client Scopes
- `toRepresentation(ClientScopeModel)`
- `toRepresentation(ClientModel, KeycloakSession)`
- `stripClientAttributesIncludedAsFields()`

###### Stage 5: Events & Utilities
* `toRepresentation(Event)`
* `toRepresentation(AdminEvent)`
* `toRepresentation(AuthDetails)`
* `toRepresentation(FederatedIdentityModel)`
* `stripRealmAttributesIncludedAsFields()`
* `filterValidRepresentations()`
* export utilities (`exportGroups()`, `exportAuthenticationFlows()`, `exportRequiredActions()`)

##### Initial Implementation
###### Stage 1
The agent generated `ModelToRepresentationGroupAndRoleTest` with 7 tests.
These tests were missed from the plan:
- `toGroupHierarchy(KeycloakSession, RealmModel, boolean)`, `toGroupHierarchy(GroupModel, boolean)` and
  `toGroupHierarchy(GroupModel, boolean)` are not cover. However, they are deprecated.

###### Stage 2
The agent generated `ModelToRepresentationUserTest` with 7 tests.
All the tests that were planned are implemented.

###### Stage 3
The agent generated `ModelToRepresentationRealmAndIdpTest` with 7 tests.
All the tests that were planned are implemented.

###### Stage 4
The agent generated `ModelToRepresentationClientTest` with 5 tests.
All the tests that were planned are implemented.

###### Stage 5
The agent generated `ModelToRepresentationEventAndUtilTest` with 8 tests.
These tests were missed from the plan:
- from export utilities `exportGroups()` was not covered.

##### Initial Coverage
OpenClover coverage is 57.9%, and decision coverage is 38.8%.

Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gemma4_ModelToRepresentation_round1.md`.

##### Improvement Phase
The agent was given the missing coverage markdown file. It came up with this two-stage plan

###### Stage 1: Branch & Logic Refinement
Goal: Resolve the "Partially Covered" gaps, focusing on complex decision trees in core models.

Focus Areas:
- User Models: Complete branch coverage for `toRepresentation(UserModel, ...)` and `toBriefRepresentation(...)`,
  specifically targeting null checks for attributes and the `isEnabled()` logic.
- Realm Models: High-intensity testing of `toRepresentation(RealmModel, ...)` to cover feature flags (e.g.,
  `Profile.isFeatureEnabled()`), null flows (browser, registration, etc.), and the `export` branch.
- Identity Providers: Complete the branch logic for `toRepresentation(IdentityProviderModel, ...)`
  regarding authentication flows.
- Admin Events: Cover null/non-null branches for `authDetails()` and `resourceTypeAsString()` in
  `toRepresentation(AdminEvent)`.
- Utility Logic: Resolve coverage for `isRealmClient()` and `toLocalizedMessage()`.

###### Stage 2: Full Method Integration
Goal: Exercise the "Methods Not Entered" to ensure complete functional coverage.

Focus Areas:
- Authorization & Policy: Implement tests for the extensive suite of Authorization-related methods: `Policy`,
  `Resource`, `PermissionTicket`, `ResourceServer`, and `AuthorizationProvider` transformations.
- Organization & Verifiable Credentials: Cover the new `OrganizationModel` and
  `IssuedVerifiableCredentialModel` mapping logic.
- Component & Client Scopes: Exercise `ComponentModel`, `ClientScopeModel`, and `ProviderConfigProperty` transformations.
- Groups & Hierarchy: Implement tests for the group hierarchy and search matching logic (`toGroupHierarchy()` and
  `groupMatchesSearchOrIsPathElement()`).

##### Improvement Implementation
###### Stage 1
The agent generated `ModelToRepresentationBranchTest` with 7 tests.
These tests were missed from the plan:
-  `toRepresentation(UserVerifiableCredentialModel, RealmModel)` is not covered.
   `toBriefRepresentation()` is missing a branch.
- All 6 entrances to `isRealmClient()` goes through a single branch.
  This was the case too for all 3 entrances to `toLocalizedMessage()`.

`testToLocalizedMessageBranches()` and `testIsRealmClientBranches()` are removed duo to
attempt to access private members.

###### Stage 2
The agent generated `ModelToRepresentationMissingMethodsTest` with 6 tests.
These tests were missed from the plan:
- `groupMatchesSearchOrIsPathElement()` is not covered.

##### Final Coverage
OpenClover coverage is 76.7%, and decision coverage is 56.9%.
Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gemma4_ModelToRepresentation_round2.md`.

##### Verdict
In total, 1,100,082 input tokens and 32,513 output tokens were used. 160,768 tokens were cached.
This does not include the failed initial plan attempt.

The majority of the planned tests were implemented this time. Still, the coverage is mediocre.

In the end, the OpenClover coverage is almost satisfactory.
The decision coverage is away from satisfactory though.
340,528 tokens were used to improve decision coverage by 20%.
That's actually great for decision coverage.


#### GPT-OSS
##### Initial Implementation Plan
The agent did not give a title for each stage.

###### Stage 1
- `toRepresentation(KeycloakSession, RealmModel, UserModel)`
- `toBriefRepresentation(UserModel)`
- `toRepresentation(UserModel, boolean)`
- `addAttributeToBriefRep(UserModel, UserRepresentation, String)`

###### Stage 2
- `toRepresentation(GroupModel, boolean)`
- `groupMatchesSearchOrIsPathElement()`
- `toGroupHierarchy()` (hierarchy logic)
- `buildGroupPath()`

###### Stage 3
- `toRepresentation(ClientModel, KeycloakSession)`
- `isRealmClient()`
- `exportGroups()`
- `exportAuthenticationFlows()`

###### Stage 4
- `toRepresentation(KeycloakSession, RealmModel,, boolean, boolean)`
- `toBriefRepresentation(RealmModel)`
- `toEventsConfigReprensetation()`
- `stripRealmAttributesIncludedAsFields()`
- `toLocalizedMessage()`

###### Stage 5
- `filterValidRepresentations()`
- `stripClientAttributesIncludedAsFields()`
- `toLocalizedMessage()`
- `toMetadataRepresentation()` (credential metadata)
- `addAttributeToBriefRep()`

Note that `toLocalizedMessage()` is covered in the previous stage.
Method `toMetadataRepresentation()` does not exist.

##### Initial Implementation
###### Stage 1
The agent generated `ModelToRepresentationUserTest` with 3 tests.
All the tests that were planned are implemented.

`testAddAttributeToBriefRep()` is removed duo to attempt to access a private member.

###### Stage 2
The agent generated `ModelToRepresentationGroupTest` with 4 tests.
All the tests that were planned are implemented.

`testSearchEmpty()` is removed duo to attempt to access a private member.

###### Stage 3
The agent generated `ModelToRepresentationClientTest` with 2 tests.
These tests were missed from the plan:
- `exportGroups()`
- `exportAuthenticationFlows()`

###### Stage 4
The agent generated `ModelToRepresentationRealmTest` with 1 test.
These tests were missed from the plan:
- `toBriefRepresentation(RealmModel)`

###### Stage 5
The agent generated `ModelToRepresentationUtilityTest` with 6 tests.
All the tests that were planned are implemented.

##### Initial Coverage
OpenClover coverage is 43.2%, and decision coverage is 30.9%.

Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gpt-oss_ModelToRepresentation_round1.md`.

##### Improvement Phase
The agent was given the missing coverage markdown file.
It came up with this two-stage plan.

###### Stage 1: Group hierarchy, user, role, identity‑provider
- `toGroupHierarchy()` (recursion, search path)
- `toRepresentation(KeycloakSession, UserModel, boolean)` (setUserAttributes=false/true)
- `toRepresentation(RoleModel)` (Client vs Realm role)
- `toBriefRepresentation(RealmModel)`, `toRepresentation(RealmModel)` (internal=false)
- `toBriefRepresentation(RealmModel, IdentityProviderModel)`

###### Stage 2: Realm & Client conversions, events, and helper methods
- `toRepresentation(KeycloakSession, RealmModel, boolean, boolean)` (internal=true/false)
- `exportGroups()` / `exportAuthenticationFlows()` / `exportRequiredActions()` (ensure lists populated)
- `toRepresentation(UserCredentialModel)`
- `toRepresentation(FederatedIdentityModel)`
- `toRepresentation(UserSessionModel)`
- `toRepresentation(ClientScopeModel)`
- `toEventsConfigRepresentation()`
- `stripRealmAttributesIncludedAsFields()`
- `stripClientAttributesIncludedAsFields()` (enclose keys)

##### Improvement Implementation
###### Stage 1
The agent generated `ModelToRepresentationGroupHierarchyTest` and `ModelToRepresentationUserAttributesTest`
with 5 tests.
These tests were missed from the plan:
- `toRepresentation(KeycloakSession, UserModel, boolean)`
- `toRepresentation(RoleModel)`
- `toBriefRepresentation(RealmModel)`
- `toBriefRepresentation(RealmModel, IdentityProviderModel)`

`toRepresentation(RealmModel)` does not exist.

###### Stage 2
The agent generated `ModelToRepresentationGroupFullTest` and `ModelToRepresentationGroupRoleTest` with 4 tests.
Only these methods are covered:
- `toRepresentation(KeycloakSession, RealmModel, boolean, boolean)` (a branch was not covered)
- `stripRealmAttributesIncludedAsFields()`
- `stripClientAttributesIncludedAsFields()`

`toEventsConfigRepresentation` does not exist.

##### Final Coverage
OpenClover coverage is 44.9%, and decision coverage is 36.7%.
Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gpt-oss_ModelToRepresentation_round2.md`.

676,898 input tokens and 9,486 output tokens were used for this phase.

##### Verdict
In total, 1,297,887 input tokens and 33,163 output tokens were used. no token was cached.

The initial coverage is low.

Half-way through, we can already declare Gemma4 the better model at least in terms of coverage.

This has been the worst improvement so far.
Decision coverage increased by about 6%, and OpenClover coverage by 2%.

#### Comparison
| Title                            |  Gemma4   |  GPT-OSS  |
|:---------------------------------|:---------:|:---------:|
| Initial OpenClover Coverage      |   57.9%   |   43.2%   |
| Initial Decision Coverage        |   38.8%   |   30.9%   |
| Final OpenClover Coverage        |   76.7%   |   44.9%   |
| Final Decision Coverage          |   56.9%   |   36.7%   |
| Total Tests                      |    45     |    23     |
| Context Window Usage             |  72,310   |  44,704   |
| Total Input Tokens               | 1,100,082 | 1,297,887 |
| Total Cached Tokens              |  160,768  |     0     |
| Total Output Tokens              |  32,513   |  33,163   |
| Total Tokens used                |  971,827  | 1,331,050 |
| Tokens per OpenClover Coverage % |  12,670   |  29,645   |

Gemma4 wins again, no surprises. It even used fewer tokens again.
Still, the decision coverage is far from satisfactory.
Maybe creating another improvement phase specifically aiming at missing branches can fix this.

### OIDCLoginProtocol
This file has 598 LOC, 28 methods, 202 statements, and 72 branches.

#### Gemma4
##### Initial Implementation Plan
###### Stage 1: Helpers and Reauthentication Logic
Focus: Testing the utility and predicate methods that drive business logic.
- `getClientData(AuthenticationSessionModel)`
- `isIdTokenAsDetachedSignature(ClientModel)`
- `isPromptLogin(AuthenticationSessionModel)`
- `isAuthTimeExpired(UserSessionModel, AuthenticationSessionModel)` (test: null session,
  missing maxAge, expired vs valid)
- `isReAuthRequiredForKcAction(UserSessionModel, AuthenticationSessionModel)` (test: missing action,
  provider not found, expired vs valid)
- `requireReauthentication(UserSessionModel, AuthenticationSessionModel)` (test combinations of the above)

###### Stage 2: Error Handling and Redirection
Focus: Testing how the protocol handles failures and constructs error responses.
- `sendError(AuthenticationSessionModel, Error, String)` (test: device flow vs standard, error translation)
- `sendError(ClientModel, ClientData, Error)` (test: authorization checker paths)
- `translateError(Error, String)` (test: all switch cases including `CANCELLED_AIA`,
  `CONSENT_DENIED`, `ALREADY_LOGGED_IN`, and `default`)
- `buildRedirectUri(...)` (verify various overload behaviors)

###### Stage 3: Logout and Authentication Lifecycle
Focus: Testing session termination and lifecycle hooks.
- `backchannelLogout(UserSessionModel, AuthenticatedClientSessionModel)` (test: with vs without backchannel URL)
- `frontchannelLogout(UserSessionModel, AuthenticatedClientSessionModel)` (test: enabled vs disabled,
  null session)
- `finishBrowserLogout(UserSessionModel, AuthenticationSessionModel)` (test: redirect URI presence,
  logout handler presence)
- `authenticationComplete(AuthenticationSessionModel)` (test: PAR valid vs PAR not found/error)

###### Stage 4: The Standard/Code Grant Flow
Focus: Testing the authenticated method specifically for the Authorization Code flow.
- `authenticated(AuthenticationSessionModel, UserSessionModel, ClientSessionContext)`
  - Branch: `isOAuth2DeviceVerificationFlow` is true.
  - Branch: `response_type=code` (Standard/Hybrid).
    - Test `OAuth2Code` generation and persistence.
    - Test parameter injection (state, session_state, issuer, nonce, kc_action).

###### Stage 5: Implicit/Hybrid Flows and Advanced Features
Focus: Testing the remaining complex branches of the authenticated method.
- `authenticated(...)` (Implicit/Hybrid branches)
  - Branch: `isImplicitOrHybridFlow` is `true`.
    - Sub-branch: `ID_TOKEN` requested.
    - Sub-branch: `ACCESS_TOKEN` requested.
    - Sub-branch: `CODE` hash (for hybrid).
    - Sub-branch: Client Policy Exception (verify error event and redirect).
  - Sub-branch: `offlineTokenRequested` handling.
- `sendPushRevocationPolicyRequest(...)` (test: success vs `IOException`)

##### Initial Implementation
In the first attempt of implementing stage 1, the agent got stuck in a loop.
It was assessing the generated test file again and again.

###### Stage 1
The agent generated `OIDCLoginProtocolHelpersTest` with 16 tests.
All the tests that were planned are implemented.

###### Stage 2
The agent generated `OIDCLoginProtocolErrorTest` with 13 tests.
All the tests that were planned are implemented.

`testTranslateError_Default` is removed duo to attempt to access a non-existing member.
It was done in order to cover a default case in a switch case on an enum.
ll enums had a case, so covering this is impossible.

###### Stage 3
The agent generated `OIDCLoginProtocolLogoutTest` with 8 tests.
All the tests that were planned are implemented.

###### Stage 4
The agent generated `OIDCLoginProtocolCodeFlowTest` with 2 tests.
All the tests that were planned are implemented.

###### Stage 5
The agent generated `OIDCLoginProtocolTokenFlowTest` with 4 tests.
These tests were missed from the plan:
- `authenticated(...)` has some missing branches.

##### Initial Coverage
OpenClover coverage is 80.7%, and decision coverage is 70.8%.

Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gemma4_OIDCLoginProtocol_round1.md`.

##### Improvement Phase
The agent was given the missing coverage markdown file. It came up with this two-stage plan

###### Stage 1: Complex Flow and Error Branching
Goal: Address the majority of missing branches in the authenticated, sendError,
and buildErrorRedirectUri methods, specifically focusing on configuration variations and
complex response type branches.

Test Scenarios:
- `authenticated()` method:
  - Client Configuration Exclusions: Test branches where `isExcludeSessionStateFromAuthResponse()` and
    `isExcludeIssuerFromAuthResponse()` return true.
    - KC Action Status: Test both `kcActionStatus != null` (with and without a `requiredActionAlias`) and
      `kcActionStatus == null`.
    - Response Type Variations:
      - Ensure coverage for `ID_TOKEN`, `TOKEN`, and `CODE` (for hybrid) branches.
      - Test the `state != null` branch for `generateStateHash()`.
  - Offline Token: Test the `offlineTokenRequested()` branch for non-implicit flows.
- `sendError()` method:
  - Test the true branch for `isOAuth2DeviceVerificationFlow()` (Device Flow error handling).
- `buildErrorRedirectUri()` method:
  - Test branches where `oauthError.getError()` or `oauthError.getErrorDescription()` are `null`.
  - Test the `false` branch for `isExcludeIssuerFromAuthResponse()`.

###### Stage 2: Lifecycle, Edge Cases, and Utility Completeness
Goal: Address the remaining missing branches in logout, lifecycle, and utility methods.

Test Scenarios:
- Logout & Lifecycle:
  - `frontchannelLogout()`: Test the `false` branch where `clientSession()` is `null`.
  - `finishBrowserLogout()`: Test the `true` branch where `frontChannelLogoutHandler()` is not `null`.
  - `close()`: Invoke the `close()` method to ensure it is entered.
- Utility Predicates:
- `isAuthTimeExpired()`: Explicitly test the `true` branch of the ternary operator (where
  `authTime` is NOT null) to ensure integer parsing is covered.
- `isReAuthRequiredForKcAction()`: Explicitly test the `true` branch of the ternary
  operator (where `authTime` is NOT `null`).

##### Improvement Implementation
###### Stage 1
The agent generated `OIDCLoginProtocolFlowExtensionTest` with 6 tests.
These tests were missed from the plan:
- `TOKEN` branch in Response Type
- `false` branch for `isExcludeIssuerFromAuthResponse()`

`testBuildErrorRedirectUri_Branches` is removed duo to attempt to access a non-existing member.

###### Stage 2
The agent generated `OIDCLoginProtocolLifecycleAndUtilityTest` with 5 tests.
All the tests that were planned are implemented. Although, for `isAuthTimeExpired` and `isAuthTimeExpired()`,
the true branch is actually when the variable is null.

##### Final Coverage
OpenClover coverage is 95.8%, and decision coverage is 88.9%.
Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gemma4_OIDCLoginProtocol_round2.md`.

257,306 input tokens and 11,304 output tokens were used for this phase.

##### Verdict
In total, 903,169 input tokens and 39,758 output tokens were used. 17,280 tokens were cached.

The initial coverage is great.
The decision coverage just needs a little bit more push to attain a satisfactory level.

The improvement is alright.
The agent mostly focused on missing branches.
As a result, the goal of 80% decision coverage is obtained.

#### GPT-OSS
##### Initial Implementation Plan
###### Stage 1: Minimal happy path: CODE flow
- `isOAuth2DeviceVerificationFlow()` false
- `responseType` contains `CODE` only
- `approveOAuth2DeviceAuthorization` never invoked

###### Stage 2: Implicit / hybrid flow: token + ID‑token
- `responseType()` contains `TOKEN` and `ID_TOKEN`
- `isImplicitOrHybridFlow()` true
- `generateAccessToken()`, `generateIDToken()`, `generateAccessTokenHash()`, `generateCodeHash()`,
  `generateStateHash()` called

###### Stage 3: Device Authorization flow
- `isOAuth2DeviceVerificationFlow()` true
- `approveOAuth2DeviceAuthorization()` path
- `denyOAuth2DeviceAuthorization()` path

###### Stage 4: Error path
- `sendError()` called with each `Error` enum case
- `buildErrorRedirectUri()` produces expected params

###### Stage 5: Session/reauth logic & cleanup
- `requireReauthentication()` → `isPromptLogin()`, `isAuthTimeExpired()`, `isReAuthRequiredForKcAction()`
- `sendPushRevocationPolicyRequest()` returns success/failure
- `authenticationComplete()` removes PAR

##### Initial Implementation
Test file names did not have Test at the end of their names.

###### Stage 1
The agent generated `OIDCLoginProtocolTest_CodeFlow` with 1 test.
All the tests that were planned are implemented.

###### Stage 2
The agent generated `OIDCLoginProtocolTest_HybridFlow` with 1 test.
All the tests that were planned are implemented.

###### Stage 3
The agent generated `OIDCLoginProtocolTest_DeviceFlow`, `OIDCLoginProtocolTest_Errors` and
`OIDCLoginProtocolTest_Session` with 9 tests.
All the tests that were planned are implemented.

###### Stage 4
The agent generated `OIDCLoginProtocolTest_Errors` with 2 tests.
The tests methods were missing signature!
All the tests that were planned are implemented.

###### Stage 5
The agent generated `OIDCLoginProtocolTest_AdditionalSession` with 3 tests.
All the tests that were planned are implemented.

##### Initial Coverage
OpenClover coverage is 64.2%, and decision coverage is 50.0%.

Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gpt-oss_OIDCLoginProtocol_round1.md`.

##### Improvement Phase
The agent was given the missing coverage markdown file. It came up with this two-stage plan

###### Stage 1: Additional Tests to Close Coverage Gaps
- `setSession()`
- `setRealm()`
- `setUriInfo()`
- `setHttpHeaders()`
- `setEventBuilder()`
- `getClientData()`
- `backchannelLogout()`
- `frontchannelLogout()`
- `finishBrowserLogout()`
- `close()`

###### Stage 2: Partial‑coverage methods
- `isIdTokenAsDetachedSignature()`
- `buildErrorRedirectUri()`
- `isAuthTimeExpired()`
- `isReAuthRequiredForKcAction()`

##### Improvement Implementation
###### Stage 1
The agent generated `OIDCLoginProtocolSetterTest` with 5 tests.
These methods were missed to be covered from the plan:
- `finishBrowserLogout()`
- `close()`

###### Stage 2
The agent generated `OIDCLoginProtocolGetClientDataTest` and `OIDCLoginProtocolLogoutTest` with 5 tests.
All mentioned methods are still partially covered.

##### Final Coverage
OpenClover coverage is 75.8%, and decision coverage is 56.9%.
Missed coverage markdown file is available at
`project3/reports/opencode/missing_coverages_report/gpt-oss_OIDCLoginProtocol_round2.md`.

329,999 input tokens and 5,186 output tokens were used for this phase.

##### Verdict
In total, 877,428 input tokens and 21,355 output tokens were used. no token was cached.

This file contains the least number of statements. Therefore, I was expecting more initial coverage.

The improvement is not great, considering the token usage.

#### Comparison
| Title                            | Gemma4  |  GPT-OSS  |
|:---------------------------------|:-------:|:---------:|
| Initial OpenClover Coverage      |  87.4%  |   64.2%   |
| Initial Decision Coverage        |  70.8%  |   50.0%   |
| Final OpenClover Coverage        |  95.7%  |   75.8%   |
| Final Decision Coverage          |  88.9%  |   56.9%   |
| Total Tests                      |   52    |    26     |
| Context Window Usage             | 56,602  |  36,149   |
| Total Input Tokens               | 903,169 | 1,297,887 |
| Total Cached Tokens              | 17,280  |     0     |
| Total Output Tokens              | 39,758  |  21,355   |
| Total Tokens used                | 925,647 | 1,319,242 |
| Tokens per OpenClover Coverage % |  9,672  |  17,404   |

Gemma4 is clearly the winner.
Less token usage, but much more coverage.
This file has the lowest statements between all files.
It's good to see that at least gemma4 reached satisfactory coverage.