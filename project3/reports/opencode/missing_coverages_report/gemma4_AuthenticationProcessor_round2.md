# Coverage Gaps

**File:** `AuthenticationProcessor.java`

**Branches:** 95 / 168 covered — 73 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 271 | `generateCode() : String` |
| 277 | `newEvent() : EventBuilder` |
| 290 | `getFlowPath() : String` |
| 306 | `getClientData() : String` |
| 310 | `getClientData(KeycloakSession,AuthenticationSessionModel) : String` |
| 316 | `getSignedAuthSessionId() : String` |
| 321 | `getRefreshUrl(boolean) : URI` |
| 363 | `newEvent() : EventBuilder` |
| 381 | `getExecution() : AuthenticationExecutionModel` |
| 454 | `failure(AuthenticationFlowError,Response) : void` |
| 499 | `clearUser() : void` |
| 504 | `getRealm() : RealmModel` |
| 509 | `getClient() : ClientModel` |
| 514 | `setClient(ClientModel) : void` |
| 519 | `getClientAuthAttributes() : Map<String, String>` |
| 545 | `getFlowPath() : String` |
| 550 | `getConnection() : ClientConnection` |
| 555 | `getUriInfo() : UriInfo` |
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
| 981 | `redirectToFlow() : Response` |
| 1139 | `attachSession() : ClientSessionContext` |
| 1150 | `attachSession(AuthenticationSessionModel,UserSessionModel,KeycloakSession,RealmModel,ClientConnection,EventBuilder) : ClientSessionContext` |
| 1217 | `evaluateRequiredActionTriggers() : void` |
| 1221 | `finishAuthentication(LoginProtocol) : Response` |
| 1238 | `authenticationComplete() : Response` |
| 1260 | `nextRequiredAction() : String` |
| 1268 | `createClientAuthenticatorContext(AuthenticationExecutionModel,ClientAuthenticator,List<AuthenticationExecutionModel>) : AuthenticationProcessor.Result` |

## Partially Covered Methods

### Line 826 — `handleBrowserException(Exception) : Response`

| Line | Missing coverage |
|---:|---|
| 829 | `true` branch |
| 836 | `true` branch |
| 841 | `true` branch |
| 846 | `true` branch |
| 852 | `true` branch |
| 858 | `true` branch |
| 861 | `true` branch |
| 889 | `true` branch |
| 894 | `true` branch |
| 896 | `false` branch |
| 898 | `true` branch |
| 902 | `true` branch |
| 907 | `true` branch |
| 907 | `false` branch |

### Line 918 — `handleClientAuthException(Exception) : Response`

| Line | Missing coverage |
|---:|---|
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

### Line 1056 — `authenticationAction(String) : Response`

| Line | Missing coverage |
|---:|---|
| 1067 | `true` branch |
| 1077 | `true` branch |
| 1083 | `true` branch |
| 1084 | `true` branch |
| 1087 | `true` branch |

### Line 1093 — `checkClientSession(boolean) : void`

| Line | Missing coverage |
|---:|---|
| 1098 | `true` branch |
| 1102 | `true` branch |

### Line 1109 — `authenticateOnly() : Response`

| Line | Missing coverage |
|---:|---|
| 1116 | `true` branch |
| 1123 | `true` branch |
| 1125 | `true` branch |
| 1132 | `true` branch |

### Line 1229 — `validateUser(UserModel) : void`

| Line | Missing coverage |
|---:|---|
| 1231 | `true` branch |
| 1235 | `true` branch |
