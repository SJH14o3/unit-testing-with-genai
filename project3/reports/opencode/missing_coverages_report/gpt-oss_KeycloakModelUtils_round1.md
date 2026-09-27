# Coverage Gaps

**File:** `KeycloakModelUtils.java`

**Branches:** 69 / 236 covered — 167 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 127 | `KeycloakModelUtils()` |
| 229 | `getPemFromKey(Key) : String` |
| 233 | `getPemFromCertificate(X509Certificate) : String` |
| 237 | `generateKeyPairCertificate(String) : CertificateRepresentation` |
| 243 | `generateKeyPairCertificate(String,int,Calendar) : CertificateRepresentation` |
| 338 | `findUserByNameOrEmail(KeycloakSession,RealmModel,String) : UserModel` |
| 354 | `runJobInTransaction(KeycloakSessionFactory,KeycloakSessionTask) : void` |
| 364 | `runJobInTransaction(KeycloakSessionFactory,KeycloakContext,KeycloakSessionTask) : void` |
| 388 | `cloneContextRealmClientSessionToSession(KeycloakContext,KeycloakSession) : void` |
| 457 | `runJobInTransactionWithResult(KeycloakSessionFactory,KeycloakSessionTaskWithResult<V>) : V` |
| 470 | `runJobInTransactionWithResult(KeycloakSessionFactory,KeycloakContext,KeycloakSessionTaskWithResult<V>,String) : V` |
| 498 | `runJobInTransactionWithTimeout(KeycloakSessionFactory,KeycloakSessionTask,int) : void` |
| 508 | `setTransactionLimit(KeycloakSessionFactory,int) : void` |
| 529 | `getTransactionLimit() : Optional<Integer>` |
| 533 | `componentModelGetter(String,String) : Function<KeycloakSessionFactory, ComponentModel>` |
| 537 | `getComponentModel(KeycloakSessionFactory,String,String) : ComponentModel` |
| 546 | `getComponentFactory(KeycloakSessionFactory,Class<T>,Scope,String) : ProviderFactory<T>` |
| 566 | `ScopeComponentModel(Class<?>,Scope,String,String)` |
| 576 | `getProviderType() : String` |
| 581 | `getProviderId() : String` |
| 586 | `getName() : String` |
| 591 | `getId() : String` |
| 596 | `getParentId() : String` |
| 601 | `get(String,boolean) : boolean` |
| 606 | `get(String,long) : long` |
| 611 | `get(String,int) : int` |
| 616 | `get(String,String) : String` |
| 621 | `get(String) : String` |
| 627 | `getMasterRealmAdminManagementClientId(String) : String` |
| 634 | `createComponentModel(String,String,String,String,String) : ComponentModel` |
| 660 | `toLowerCaseSafe(String) : String` |
| 703 | `deepFindAuthenticationExecutions(RealmModel,AuthenticationFlowModel,List<AuthenticationExecutionModel>) : void` |
| 714 | `resolveAttribute(GroupModel,String,boolean) : Collection<String>` |
| 722 | `resolveAttribute(UserModel,String,boolean) : Collection<String>` |
| 788 | `findGroupByPath(KeycloakSession,RealmModel,String[]) : GroupModel` |
| 893 | `buildGroupPath(StringBuilder,String,GroupModel,boolean,String) : void` |
| 904 | `buildGroupPath(GroupModel) : String` |
| 912 | `buildGroupPath(GroupModel,GroupModel) : String` |
| 920 | `getOrgId(GroupModel) : String` |
| 959 | `findGroupByPath(KeycloakSession,RealmModel,OrganizationModel,String) : GroupModel` |
| 988 | `getOrganizationForIdpMapper(KeycloakSession,IdentityProviderModel) : OrganizationModel` |
| 1022 | `getGroupForIdpMapper(KeycloakSession,RealmModel,IdentityProviderMapperModel,BrokeredIdentityContext) : GroupModel` |
| 1058 | `getClientScopeMappingsStream(ClientModel,ScopeContainerModel) : Stream<RoleModel>` |
| 1067 | `getRoleFromString(RealmModel,String) : RoleModel` |
| 1169 | `isFlowUsed(KeycloakSession,RealmModel,AuthenticationFlowModel) : boolean` |
| 1203 | `deepDeleteAuthenticationFlow(KeycloakSession,RealmModel,AuthenticationFlowModel,Runnable,Runnable,boolean) : void` |
| 1227 | `deepDeleteAuthenticationExecutor(KeycloakSession,RealmModel,AuthenticationExecutionModel,Runnable,Runnable,boolean) : void` |
| 1254 | `getClientScopeByName(RealmModel,String) : ClientScopeModel` |
| 1266 | `findClientScopeById(RealmModel,ClientModel,String) : ClientScopeModel` |
| 1288 | `convertClientScopeName(String) : String` |
| 1296 | `setupAuthorizationServices(RealmModel) : void` |
| 1306 | `suspendJtaTransaction(KeycloakSessionFactory,Runnable) : void` |
| 1333 | `getIdentityProviderDisplayName(KeycloakSession,IdentityProviderModel) : String` |
| 1353 | `getSecretLengthByAuthenticationType(String,String) : int` |
| 1364 | `setDefaultGroups(KeycloakSession,RealmModel,Stream<String>) : void` |
| 1384 | `runOnRealm(KeycloakSession,RealmModel,Function<KeycloakSession, T>) : T` |
| 1403 | `getAcceptedClientScopeProtocols(ClientModel) : List<String>` |

## Partially Covered Methods

### Line 189 — `getPublicKey(String) : PublicKey`

| Line | Missing coverage |
|---:|---|
| 190 | `true` branch |

### Line 201 — `getCertificate(String) : X509Certificate`

| Line | Missing coverage |
|---:|---|
| 202 | `true` branch |

### Line 214 — `getPrivateKey(String) : PrivateKey`

| Line | Missing coverage |
|---:|---|
| 215 | `true` branch |

### Line 312 — `searchFor(RoleModel,RoleModel,Set<String>) : boolean`

| Line | Missing coverage |
|---:|---|
| 313 | `true` branch |

### Line 397 — `cloneContextToSession(KeycloakContext,KeycloakSession,boolean) : void`

| Line | Missing coverage |
|---:|---|
| 405 | `false` branch |
| 407 | `false` branch |
| 415 | `true` branch |
| 418 | `false` branch |
| 420 | `false` branch |
| 427 | `true` branch |
| 428 | `true` branch |
| 428 | `false` branch |
| 429 | `true` branch |
| 429 | `false` branch |
| 432 | `true` branch |
| 432 | `false` branch |
| 436 | `true` branch |
| 436 | `false` branch |
| 439 | `true` branch |
| 439 | `false` branch |
| 442 | `true` branch |
| 442 | `false` branch |

### Line 688 — `setupDeleteAccount(ClientModel) : void`

| Line | Missing coverage |
|---:|---|
| 690 | `false` branch |

### Line 769 — `findGroupByPath(KeycloakSession,RealmModel,String) : GroupModel`

| Line | Missing coverage |
|---:|---|
| 774 | `true` branch |

### Line 795 — `getGroupModel(KeycloakSession,RealmModel,GroupModel,String[],int) : GroupModel`

| Line | Missing coverage |
|---:|---|
| 797 | `false` branch |
| 801 | `true` branch |
| 804 | `true` branch |
| 804 | `false` branch |
| 808 | `true` branch |
| 808 | `false` branch |
| 822 | `false` branch |

### Line 840 — `splitPath(String,boolean) : String[]`

| Line | Missing coverage |
|---:|---|
| 841 | `true` branch |
| 844 | `false` branch |
| 847 | `true` branch |

### Line 925 — `normalizeGroupPath(String) : String`

| Line | Missing coverage |
|---:|---|
| 926 | `true` branch |

### Line 1072 — `getRoleFromString(KeycloakSession,RealmModel,String) : RoleModel`

| Line | Missing coverage |
|---:|---|
| 1073 | `false` branch |

### Line 1081 — `getRoleFromStringNoCaching(RealmModel,String) : RoleModel`

| Line | Missing coverage |
|---:|---|
| 1082 | `true` branch |
| 1093 | `false` branch |
| 1100 | `true` branch |
| 1101 | `true` branch |
| 1101 | `false` branch |

### Line 1130 — `getRoleByName(RealmModel,String,String) : RoleModel`

| Line | Missing coverage |
|---:|---|
| 1136 | `true` branch |

### Line 1144 — `removeTransientAdminRoles(RealmModel,String,UserModel,Access) : void`

| Line | Missing coverage |
|---:|---|
| 1145 | `true` branch |
