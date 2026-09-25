# Coverage Gaps

**File:** `KeycloakModelUtils.java`

**Branches:** 116 / 236 covered — 120 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 127 | `KeycloakModelUtils()` |
| 229 | `getPemFromKey(Key) : String` |
| 233 | `getPemFromCertificate(X509Certificate) : String` |
| 237 | `generateKeyPairCertificate(String) : CertificateRepresentation` |
| 243 | `generateKeyPairCertificate(String,int,Calendar) : CertificateRepresentation` |
| 256 | `generateSecret(ClientModel) : String` |
| 388 | `cloneContextRealmClientSessionToSession(KeycloakContext,KeycloakSession) : void` |
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
| 912 | `buildGroupPath(GroupModel,GroupModel) : String` |
| 1067 | `getRoleFromString(RealmModel,String) : RoleModel` |
| 1081 | `getRoleFromStringNoCaching(RealmModel,String) : RoleModel` |
| 1227 | `deepDeleteAuthenticationExecutor(KeycloakSession,RealmModel,AuthenticationExecutionModel,Runnable,Runnable,boolean) : void` |
| 1306 | `suspendJtaTransaction(KeycloakSessionFactory,Runnable) : void` |

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
| 319 | `true` branch |

### Line 338 — `findUserByNameOrEmail(KeycloakSession,RealmModel,String) : UserModel`

| Line | Missing coverage |
|---:|---|
| 341 | `false` branch |

### Line 397 — `cloneContextToSession(KeycloakContext,KeycloakSession,boolean) : void`

| Line | Missing coverage |
|---:|---|
| 399 | `false` branch |
| 405 | `true` branch |
| 405 | `false` branch |
| 407 | `true` branch |
| 407 | `false` branch |
| 414 | `true` branch |
| 414 | `false` branch |
| 415 | `true` branch |
| 415 | `false` branch |
| 418 | `true` branch |
| 418 | `false` branch |
| 420 | `true` branch |
| 420 | `false` branch |
| 427 | `true` branch |
| 427 | `false` branch |
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

### Line 508 — `setTransactionLimit(KeycloakSessionFactory,int) : void`

| Line | Missing coverage |
|---:|---|
| 510 | `false` branch |
| 511 | `false` branch |

### Line 537 — `getComponentModel(KeycloakSessionFactory,String,String) : ComponentModel`

| Line | Missing coverage |
|---:|---|
| 541 | `true` branch |

### Line 688 — `setupDeleteAccount(ClientModel) : void`

| Line | Missing coverage |
|---:|---|
| 690 | `false` branch |

### Line 722 — `resolveAttribute(UserModel,String,boolean) : Collection<String>`

| Line | Missing coverage |
|---:|---|
| 725 | `false` branch |
| 735 | `true` branch |
| 737 | `true` branch |
| 737 | `false` branch |

### Line 769 — `findGroupByPath(KeycloakSession,RealmModel,String) : GroupModel`

| Line | Missing coverage |
|---:|---|
| 770 | `true` branch |
| 774 | `true` branch |

### Line 788 — `findGroupByPath(KeycloakSession,RealmModel,String[]) : GroupModel`

| Line | Missing coverage |
|---:|---|
| 789 | `true` branch |

### Line 795 — `getGroupModel(KeycloakSession,RealmModel,GroupModel,String[],int) : GroupModel`

| Line | Missing coverage |
|---:|---|
| 797 | `false` branch |
| 804 | `true` branch |
| 808 | `true` branch |
| 822 | `false` branch |

### Line 840 — `splitPath(String,boolean) : String[]`

| Line | Missing coverage |
|---:|---|
| 847 | `true` branch |

### Line 893 — `buildGroupPath(StringBuilder,String,GroupModel,boolean,String) : void`

| Line | Missing coverage |
|---:|---|
| 897 | `false` branch |
| 901 | `true` branch |

### Line 904 — `buildGroupPath(GroupModel) : String`

| Line | Missing coverage |
|---:|---|
| 905 | `true` branch |

### Line 920 — `getOrgId(GroupModel) : String`

| Line | Missing coverage |
|---:|---|
| 922 | `true` branch |

### Line 959 — `findGroupByPath(KeycloakSession,RealmModel,OrganizationModel,String) : GroupModel`

| Line | Missing coverage |
|---:|---|
| 960 | `true` branch |
| 969 | `true` branch |

### Line 988 — `getOrganizationForIdpMapper(KeycloakSession,IdentityProviderModel) : OrganizationModel`

| Line | Missing coverage |
|---:|---|
| 990 | `true` branch |
| 998 | `false` branch |

### Line 1022 — `getGroupForIdpMapper(KeycloakSession,RealmModel,IdentityProviderMapperModel,BrokeredIdentityContext) : GroupModel`

| Line | Missing coverage |
|---:|---|
| 1032 | `true` branch |
| 1040 | `true` branch |
| 1042 | `true` branch |
| 1042 | `false` branch |
| 1050 | `true` branch |

### Line 1072 — `getRoleFromString(KeycloakSession,RealmModel,String) : RoleModel`

| Line | Missing coverage |
|---:|---|
| 1073 | `true` branch |

### Line 1144 — `removeTransientAdminRoles(RealmModel,String,UserModel,Access) : void`

| Line | Missing coverage |
|---:|---|
| 1145 | `true` branch |
| 1152 | `true` branch |
| 1156 | `true` branch |
| 1156 | `false` branch |

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

### Line 1203 — `deepDeleteAuthenticationFlow(KeycloakSession,RealmModel,AuthenticationFlowModel,Runnable,Runnable,boolean) : void`

| Line | Missing coverage |
|---:|---|
| 1208 | `false` branch |

### Line 1296 — `setupAuthorizationServices(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 1298 | `false` branch |

### Line 1364 — `setDefaultGroups(KeycloakSession,RealmModel,Stream<String>) : void`

| Line | Missing coverage |
|---:|---|
| 1368 | `true` branch |
| 1368 | `false` branch |
