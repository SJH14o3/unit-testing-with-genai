# Coverage Gaps

**File:** `AuthenticationProcessor.java`

**Branches:** 42 / 168 covered — 126 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 174 | `getClientAuthAttributes() : Map<String, String>` |
| 271 | `generateCode() : String` |
| 277 | `newEvent() : EventBuilder` |
| 290 | `getFlowPath() : String` |
| 294 | `setAutheticatedUser(UserModel) : void` |
| 302 | `clearAuthenticatedUser() : void` |
| 306 | `getClientData() : String` |
| 310 | `getClientData(KeycloakSession,AuthenticationSessionModel) : String` |
| 316 | `getSignedAuthSessionId() : String` |
| 321 | `getRefreshUrl(boolean) : URI` |
| 357 | `Result(AuthenticationExecutionModel,ClientAuthenticator,List<AuthenticationExecutionModel>)` |
| 363 | `newEvent() : EventBuilder` |
| 368 | `getCategoryRequirementFromCurrentFlow(String) : AuthenticationExecutionModel.Requirement` |
| 381 | `getExecution() : AuthenticationExecutionModel` |
| 386 | `getTopLevelFlow() : AuthenticationFlowModel` |
| 399 | `getAuthenticator() : Authenticator` |
| 408 | `getClientAuthenticator() : ClientAuthenticator` |
| 454 | `failure(AuthenticationFlowError,Response) : void` |
| 462 | `failure(AuthenticationFlowError,Response,String,String) : void` |
| 479 | `getUser() : UserModel` |
| 484 | `setUser(UserModel) : void` |
| 489 | `getAuthenticationSelections() : List<AuthenticationSelectionOption>` |
| 494 | `setAuthenticationSelections(List<AuthenticationSelectionOption>) : void` |
| 499 | `clearUser() : void` |
| 504 | `getRealm() : RealmModel` |
| 509 | `getClient() : ClientModel` |
| 514 | `setClient(ClientModel) : void` |
| 519 | `getClientAuthAttributes() : Map<String, String>` |
| 540 | `getAuthenticationSession() : AuthenticationSessionModel` |
| 545 | `getFlowPath() : String` |
| 550 | `getConnection() : ClientConnection` |
| 555 | `getUriInfo() : UriInfo` |
| 560 | `getSession() : KeycloakSession` |
| 565 | `getHttpRequest() : HttpRequest` |
| 578 | `getProtector() : BruteForceProtector` |
| 583 | `getEvent() : EventBuilder` |
| 588 | `getForwardedErrorMessage() : FormMessage` |
| 593 | `generateAccessCode() : String` |
| 599 | `getChallenge() : Response` |
| 608 | `form() : LoginFormsProvider` |
| 634 | `getActionUrl(String) : URI` |
| 658 | `getActionTokenUrl(String) : URI` |
| 673 | `getRefreshExecutionUrl() : URI` |
| 688 | `getRefreshUrl(boolean) : URI` |
| 693 | `cancelLogin() : void` |
| 705 | `resetFlow() : void` |
| 710 | `resetFlow(Runnable) : void` |
| 716 | `fork() : void` |
| 721 | `forkWithSuccessMessage(FormMessage) : void` |
| 728 | `forkWithErrorMessage(FormMessage) : void` |
| 735 | `getForwardedSuccessMessage() : FormMessage` |
| 740 | `setForwardedInfoMessage(String,Object) : void` |
| 745 | `getForwardedInfoMessage() : FormMessage` |
| 750 | `getErrorMessage() : FormMessage` |
| 754 | `getSuccessMessage() : FormMessage` |
| 758 | `getEventDetails() : String` |
| 763 | `getUserErrorMessage() : String` |
| 981 | `redirectToFlow() : Response` |
| 1056 | `authenticationAction(String) : Response` |
| 1093 | `checkClientSession(boolean) : void` |
| 1109 | `authenticateOnly() : Response` |
| 1139 | `attachSession() : ClientSessionContext` |
| 1150 | `attachSession(AuthenticationSessionModel,UserSessionModel,KeycloakSession,RealmModel,ClientConnection,EventBuilder) : ClientSessionContext` |
| 1217 | `evaluateRequiredActionTriggers() : void` |
| 1221 | `finishAuthentication(LoginProtocol) : Response` |
| 1229 | `validateUser(UserModel) : void` |
| 1238 | `authenticationComplete() : Response` |
| 1260 | `nextRequiredAction() : String` |
| 1268 | `createClientAuthenticatorContext(AuthenticationExecutionModel,ClientAuthenticator,List<AuthenticationExecutionModel>) : AuthenticationProcessor.Result` |

## Partially Covered Methods

### Line 417 — `success(String) : void`

| Line | Missing coverage |
|---:|---|
| 419 | `true` branch |

### Line 570 — `attachUserSession(UserSessionModel) : void`

| Line | Missing coverage |
|---:|---|
| 573 | `false` branch |

### Line 769 — `logFailure(String) : void`

| Line | Missing coverage |
|---:|---|
| 772 | `false` branch |

### Line 826 — `handleBrowserException(Exception) : Response`

| Line | Missing coverage |
|---:|---|
| 829 | `true` branch |
| 833 | `false` branch |
| 836 | `true` branch |
| 838 | `true` branch |
| 838 | `false` branch |
| 841 | `true` branch |
| 841 | `false` branch |
| 843 | `true` branch |
| 843 | `false` branch |
| 846 | `true` branch |
| 846 | `false` branch |
| 849 | `true` branch |
| 849 | `false` branch |
| 852 | `true` branch |
| 852 | `false` branch |
| 855 | `true` branch |
| 855 | `false` branch |
| 858 | `true` branch |
| 858 | `false` branch |
| 861 | `true` branch |
| 861 | `false` branch |
| 886 | `true` branch |
| 886 | `false` branch |
| 889 | `true` branch |
| 889 | `false` branch |
| 891 | `true` branch |
| 891 | `false` branch |
| 894 | `true` branch |
| 894 | `false` branch |
| 896 | `true` branch |
| 896 | `false` branch |
| 898 | `true` branch |
| 898 | `false` branch |
| 902 | `true` branch |
| 902 | `false` branch |
| 907 | `true` branch |
| 907 | `false` branch |

### Line 918 — `handleClientAuthException(Exception) : Response`

| Line | Missing coverage |
|---:|---|
| 922 | `false` branch |
| 925 | `true` branch |
| 925 | `false` branch |
| 928 | `true` branch |
| 928 | `false` branch |

### Line 958 — `authenticate() : Response`

| Line | Missing coverage |
|---:|---|
| 961 | `false` branch |

### Line 965 — `authenticateClient() : Response`

| Line | Missing coverage |
|---:|---|
| 970 | `true` branch |
| 971 | `true` branch |

### Line 990 — `resetFlow() : void`

| Line | Missing coverage |
|---:|---|
| 993 | `true` branch |

### Line 1034 — `clone(KeycloakSession,AuthenticationSessionModel) : AuthenticationSessionModel`

| Line | Missing coverage |
|---:|---|
| 1045 | `true` branch |
