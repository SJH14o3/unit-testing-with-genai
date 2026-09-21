# Coverage Gaps

**File:** `AuthenticationProcessor.java`

**Branches:** 47 / 168 covered — 121 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 194 | `getUserSession() : UserSessionModel` |
| 233 | `setFlowId(String) : AuthenticationProcessor` |
| 248 | `setForwardedErrorMessage(FormMessage) : AuthenticationProcessor` |
| 261 | `setForwardedSuccessMessage(FormMessage) : AuthenticationProcessor` |
| 266 | `setForwardedInfoMessage(FormMessage) : AuthenticationProcessor` |
| 277 | `newEvent() : EventBuilder` |
| 306 | `getClientData() : String` |
| 310 | `getClientData(KeycloakSession,AuthenticationSessionModel) : String` |
| 316 | `getSignedAuthSessionId() : String` |
| 321 | `getRefreshUrl(boolean) : URI` |
| 363 | `newEvent() : EventBuilder` |
| 368 | `getCategoryRequirementFromCurrentFlow(String) : AuthenticationExecutionModel.Requirement` |
| 386 | `getTopLevelFlow() : AuthenticationFlowModel` |
| 391 | `getAuthenticatorConfig() : AuthenticatorConfigModel` |
| 399 | `getAuthenticator() : Authenticator` |
| 403 | `getStatus() : FlowStatus` |
| 408 | `getClientAuthenticator() : ClientAuthenticator` |
| 412 | `success() : void` |
| 417 | `success(String) : void` |
| 425 | `failure(AuthenticationFlowError) : void` |
| 432 | `challenge(Response) : void` |
| 439 | `forceChallenge(Response) : void` |
| 446 | `failureChallenge(AuthenticationFlowError,Response) : void` |
| 454 | `failure(AuthenticationFlowError,Response) : void` |
| 462 | `failure(AuthenticationFlowError,Response,String,String) : void` |
| 471 | `attempted() : void` |
| 479 | `getUser() : UserModel` |
| 484 | `setUser(UserModel) : void` |
| 489 | `getAuthenticationSelections() : List<AuthenticationSelectionOption>` |
| 494 | `setAuthenticationSelections(List<AuthenticationSelectionOption>) : void` |
| 499 | `clearUser() : void` |
| 504 | `getRealm() : RealmModel` |
| 509 | `getClient() : ClientModel` |
| 514 | `setClient(ClientModel) : void` |
| 519 | `getClientAuthAttributes() : Map<String, String>` |
| 524 | `getState(Class<T>,ClientAuthenticationFlowContextSupplier<T>) : T` |
| 540 | `getAuthenticationSession() : AuthenticationSessionModel` |
| 545 | `getFlowPath() : String` |
| 550 | `getConnection() : ClientConnection` |
| 555 | `getUriInfo() : UriInfo` |
| 560 | `getSession() : KeycloakSession` |
| 565 | `getHttpRequest() : HttpRequest` |
| 570 | `attachUserSession(UserSessionModel) : void` |
| 578 | `getProtector() : BruteForceProtector` |
| 583 | `getEvent() : EventBuilder` |
| 588 | `getForwardedErrorMessage() : FormMessage` |
| 593 | `generateAccessCode() : String` |
| 599 | `getChallenge() : Response` |
| 603 | `getError() : AuthenticationFlowError` |
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
| 769 | `logFailure(String) : void` |
| 780 | `isSuccessful(AuthenticationExecutionModel) : boolean` |
| 958 | `authenticate() : Response` |
| 965 | `authenticateClient() : Response` |
| 981 | `redirectToFlow() : Response` |
| 1017 | `recreate(KeycloakSession,AuthenticationSessionModel) : AuthenticationSessionModel` |
| 1034 | `clone(KeycloakSession,AuthenticationSessionModel) : AuthenticationSessionModel` |
| 1056 | `authenticationAction(String) : Response` |
| 1093 | `checkClientSession(boolean) : void` |
| 1109 | `authenticateOnly() : Response` |
| 1139 | `attachSession() : ClientSessionContext` |
| 1150 | `attachSession(AuthenticationSessionModel,UserSessionModel,KeycloakSession,RealmModel,ClientConnection,EventBuilder) : ClientSessionContext` |
| 1217 | `evaluateRequiredActionTriggers() : void` |
| 1221 | `finishAuthentication(LoginProtocol) : Response` |
| 1238 | `authenticationComplete() : Response` |
| 1260 | `nextRequiredAction() : String` |
| 1281 | `setForwardedMessage(FormMessage) : void` |
| 1299 | `removeForwardedMessage() : void` |

## Partially Covered Methods

### Line 155 — `getBruteForceProtector() : BruteForceProtector`

| Line | Missing coverage |
|---:|---|
| 156 | `false` branch |

### Line 253 — `getAndRemoveForwardedErrorMessage() : FormMessage`

| Line | Missing coverage |
|---:|---|
| 255 | `true` branch |

### Line 826 — `handleBrowserException(Exception) : Response`

| Line | Missing coverage |
|---:|---|
| 827 | `false` branch |
| 829 | `true` branch |
| 836 | `true` branch |
| 841 | `true` branch |
| 843 | `true` branch |
| 846 | `true` branch |
| 846 | `false` branch |
| 852 | `true` branch |
| 858 | `true` branch |
| 861 | `true` branch |
| 886 | `true` branch |
| 889 | `true` branch |
| 889 | `false` branch |
| 891 | `true` branch |
| 894 | `true` branch |
| 894 | `false` branch |
| 896 | `true` branch |
| 898 | `true` branch |
| 898 | `false` branch |
| 902 | `true` branch |
| 902 | `false` branch |
| 907 | `true` branch |

### Line 918 — `handleClientAuthException(Exception) : Response`

| Line | Missing coverage |
|---:|---|
| 928 | `false` branch |

### Line 942 — `createFlowExecution(String,AuthenticationExecutionModel) : AuthenticationFlow`

| Line | Missing coverage |
|---:|---|
| 944 | `true` branch |

### Line 1290 — `getForwardedMessage() : FormMessage`

| Line | Missing coverage |
|---:|---|
| 1293 | `false` branch |
