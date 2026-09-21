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
In the end, the agent generated a test file with about 350 lines. Removing LSP was a right thing.

I have been wanting to try using MCP severs for a while. I looked up some MCP servers suitable for this project.
In the next attempt, all three MCP servers I have introduced will be used.

##### Twelfth Attempt: MCP Servers Enabled.
> For @src/main/java/org/keycloak/authentication/AuthenticationProcessor.java ,
> plan out creating unit tests using Junit5 and Mockito.
> Use "junit-5-skill" skill. Use "context7-mcp" skill if you need to fetch information about dependencies.
> Plan many tests to obtain a satisfactory decision coverage.
> The implementation will be done in five stages, so plan these stages as well.
> The repository has been indexed with codebase-memory.

The agent ask for some clarifications:
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
The more test generations is credited to each stage having its own separate test file.
For these models, it is easier to generate something from scratch rather than editing something previously.

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
OpenClover coverage is 60.8%, and decision coverage is 56.5%.
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
| Final OpenClover Coverage        | 60.8%  |  46.2%  |
| Final Decision Coverage          | 56.5%  |  38.7%  |
| total tests                      |   69   |   53    |
| Tokens used                      | 55,611 | 66,687  |
| Tokens per OpenClover Coverage % |  915   |  1,443  |

Clearly Gemma4 is winner. Not only it used fewer tokens, but also it achieved much higher coverage. It is surprising
that Gemma4 used fewer tokens. This wasn't the case in the previous project.
