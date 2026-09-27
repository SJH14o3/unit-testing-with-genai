# Coverage Gaps

**File:** `KeycloakModelUtils.java`

**Branches:** 79 / 236 covered — 157 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 127 | `KeycloakModelUtils()` |
| 338 | `findUserByNameOrEmail(KeycloakSession,RealmModel,String) : UserModel` |
| 388 | `cloneContextRealmClientSessionToSession(KeycloakContext,KeycloakSession) : void` |
| 457 | `runJobInTransactionWithResult(KeycloakSessionFactory,KeycloakSessionTaskWithResult<V>) : V` |
| 498 | `runJobInTransactionWithTimeout(KeycloakSessionFactory,KeycloakSessionTask,int) : void` |
| 508 | `setTransactionLimit(KeycloakSessionFactory,int) : void` |
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
| 634 | `createComponentModel(String,String,String,String,String) : ComponentModel` |
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

### Line 537 — `getComponentModel(KeycloakSessionFactory,String,String) : ComponentModel`

| Line | Missing coverage |
|---:|---|
| 541 | `false` branch |

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

### Line 1169 — `isFlowUsed(KeycloakSession,RealmModel,AuthenticationFlowModel) : boolean`

| Line | Missing coverage |
|---:|---|
| 1172 | `true` branch |
| 1172 | `false` branch |
| 1173 | `true` branch |
| 1173 | `false` branch |
| 1174 | `true` branch |
| 1174 | `false` branch |
| 1175 | `true` branch |
| 1175 | `false` branch |
| 1176 | `true` branch |
| 1176 | `false` branch |
| 1177 | `true` branch |
| 1177 | `false` branch |
| 1178 | `true` branch |
| 1178 | `false` branch |
| 1187 | `true` branch |
| 1187 | `false` branch |

### Line 1203 — `deepDeleteAuthenticationFlow(KeycloakSession,RealmModel,AuthenticationFlowModel,Runnable,Runnable,boolean) : void`

| Line | Missing coverage |
|---:|---|
| 1204 | `true` branch |
| 1208 | `false` branch |

### Line 1227 — `deepDeleteAuthenticationExecutor(KeycloakSession,RealmModel,AuthenticationExecutionModel,Runnable,Runnable,boolean) : void`

| Line | Missing coverage |
|---:|---|
| 1228 | `true` branch |
| 1234 | `true` branch |
| 1240 | `true` branch |
| 1242 | `true` branch |
| 1242 | `false` branch |
| 1244 | `true` branch |
| 1244 | `false` branch |
