# Coverage Gaps

**File:** `AuthenticationProcessor.java`

**Branches:** 65 / 168 covered — 103 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 316 | `getSignedAuthSessionId() : String` |
| 363 | `newEvent() : EventBuilder` |
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
| 981 | `redirectToFlow() : Response` |
| 1017 | `recreate(KeycloakSession,AuthenticationSessionModel) : AuthenticationSessionModel` |
| 1034 | `clone(KeycloakSession,AuthenticationSessionModel) : AuthenticationSessionModel` |
| 1217 | `evaluateRequiredActionTriggers() : void` |
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

### Line 321 — `getRefreshUrl(boolean) : URI`

| Line | Missing coverage |
|---:|---|
| 327 | `true` branch |

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

### Line 942 — `createFlowExecution(String,AuthenticationExecutionModel) : AuthenticationFlow`

| Line | Missing coverage |
|---:|---|
| 944 | `true` branch |

### Line 958 — `authenticate() : Response`

| Line | Missing coverage |
|---:|---|
| 961 | `true` branch |

### Line 965 — `authenticateClient() : Response`

| Line | Missing coverage |
|---:|---|
| 970 | `true` branch |

### Line 1056 — `authenticationAction(String) : Response`

| Line | Missing coverage |
|---:|---|
| 1060 | `true` branch |
| 1060 | `false` branch |
| 1067 | `true` branch |
| 1067 | `false` branch |
| 1077 | `true` branch |
| 1077 | `false` branch |
| 1083 | `true` branch |
| 1083 | `false` branch |
| 1084 | `true` branch |
| 1084 | `false` branch |
| 1087 | `true` branch |
| 1087 | `false` branch |

### Line 1093 — `checkClientSession(boolean) : void`

| Line | Missing coverage |
|---:|---|
| 1098 | `false` branch |
| 1102 | `true` branch |

### Line 1109 — `authenticateOnly() : Response`

| Line | Missing coverage |
|---:|---|
| 1116 | `true` branch |
| 1124 | `true` branch |
| 1125 | `true` branch |
| 1125 | `false` branch |
| 1132 | `true` branch |

### Line 1139 — `attachSession() : ClientSessionContext`

| Line | Missing coverage |
|---:|---|
| 1142 | `false` branch |

### Line 1150 — `attachSession(AuthenticationSessionModel,UserSessionModel,KeycloakSession,RealmModel,ClientConnection,EventBuilder) : ClientSessionContext`

| Line | Missing coverage |
|---:|---|
| 1153 | `true` branch |
| 1153 | `false` branch |
| 1159 | `true` branch |
| 1159 | `false` branch |
| 1162 | `true` branch |
| 1162 | `false` branch |
| 1168 | `true` branch |
| 1168 | `false` branch |
| 1172 | `true` branch |
| 1172 | `false` branch |
| 1179 | `true` branch |
| 1179 | `false` branch |
| 1189 | `true` branch |
| 1189 | `false` branch |
| 1194 | `true` branch |
| 1194 | `false` branch |
| 1199 | `true` branch |
| 1199 | `false` branch |

### Line 1290 — `getForwardedMessage() : FormMessage`

| Line | Missing coverage |
|---:|---|
| 1293 | `false` branch |
