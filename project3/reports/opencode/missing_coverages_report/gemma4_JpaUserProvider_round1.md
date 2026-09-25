# Coverage Gaps

**File:** `JpaUserProvider.java`

**Branches:** 84 / 158 covered — 74 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 159 | `addUser(RealmModel,String) : UserModel` |
| 375 | `createUserConsentClientScopeEntity(UserConsentEntity,ClientScopeModel,String,Collection<UserConsentClientScopeEntity>,Collection<UserConsentClientScopeEntity>) : void` |
| 472 | `getVerifiableCredentialByClientScope(String,String) : UserVerifiableCredentialModel` |
| 481 | `updateVerifiableCredential(String,String) : UserVerifiableCredentialModel` |
| 682 | `preRemove(ClientScopeModel) : void` |
| 695 | `getGroupMembersStream(RealmModel,GroupModel) : Stream<UserModel>` |
| 700 | `getRoleMembersStream(RealmModel,RoleModel) : Stream<UserModel>` |
| 742 | `close() : void` |
| 814 | `getUsersCount(RealmModel,String) : int` |
| 829 | `getUsersCount(RealmModel,String,Set<String>) : int` |
| 850 | `getUsersCount(RealmModel,Map<String, String>) : int` |
| 866 | `getUsersCount(RealmModel,Map<String, String>,Set<String>) : int` |
| 913 | `getGroupMembersStream(RealmModel,GroupModel,String,Boolean,Integer,Integer) : Stream<UserModel>` |
| 1183 | `getIssuedVerifiableCredentialsStreamByUser(String) : Stream<IssuedVerifiableCredentialModel>` |
| 1355 | `getSession() : KeycloakSession` |
| 1360 | `getEntityManager() : EntityManager` |
| 1374 | `countQuery(CriteriaQuery<Long>,CriteriaBuilder,From<?, UserEntity>,List<Predicate>) : CriteriaQuery<Long>` |

## Partially Covered Methods

### Line 122 — `addUser(RealmModel,String,String,boolean,boolean) : UserModel`

| Line | Missing coverage |
|---:|---|
| 124 | `true` branch |
| 136 | `false` branch |
| 141 | `true` branch |
| 148 | `true` branch |

### Line 231 — `addConsent(RealmModel,String,UserConsentModel) : void`

| Line | Missing coverage |
|---:|---|
| 241 | `false` branch |

### Line 292 — `getGrantedConsentEntity(String,String,LockModeType) : UserConsentEntity`

| Line | Missing coverage |
|---:|---|
| 294 | `false` branch |
| 297 | `false` branch |
| 305 | `true` branch |

### Line 315 — `toConsentModel(RealmModel,UserConsentEntity) : UserConsentModel`

| Line | Missing coverage |
|---:|---|
| 316 | `true` branch |
| 321 | `true` branch |
| 328 | `true` branch |
| 336 | `false` branch |
| 339 | `true` branch |
| 339 | `false` branch |
| 341 | `true` branch |
| 341 | `false` branch |

### Line 352 — `updateGrantedConsentEntity(UserConsentEntity,UserConsentModel) : void`

| Line | Missing coverage |
|---:|---|
| 357 | `true` branch |
| 357 | `false` branch |

### Line 394 — `addVerifiableCredential(String,UserVerifiableCredentialModel) : UserVerifiableCredentialModel`

| Line | Missing coverage |
|---:|---|
| 405 | `true` branch |
| 408 | `false` branch |
| 411 | `false` branch |
| 417 | `true` branch |

### Line 544 — `setNotBeforeForUser(RealmModel,UserModel,int) : void`

| Line | Missing coverage |
|---:|---|
| 547 | `true` branch |

### Line 553 — `getNotBeforeOfUser(RealmModel,UserModel) : int`

| Line | Missing coverage |
|---:|---|
| 556 | `true` branch |

### Line 562 — `grantToAllUsers(RealmModel,RoleModel) : void`

| Line | Missing coverage |
|---:|---|
| 564 | `true` branch |
| 564 | `false` branch |

### Line 651 — `preRemove(RealmModel,ClientModel) : void`

| Line | Missing coverage |
|---:|---|
| 654 | `false` branch |

### Line 728 — `getUserByEmail(RealmModel,String) : UserModel`

| Line | Missing coverage |
|---:|---|
| 735 | `true` branch |

### Line 746 — `getUserByFederatedIdentity(RealmModel,FederatedIdentityModel) : UserModel`

| Line | Missing coverage |
|---:|---|
| 753 | `true` branch |

### Line 764 — `getServiceAccount(ClientModel) : UserModel`

| Line | Missing coverage |
|---:|---|
| 770 | `true` branch |
| 772 | `true` branch |

### Line 781 — `getUsersCount(RealmModel,boolean) : int`

| Line | Missing coverage |
|---:|---|
| 790 | `true` branch |

### Line 800 — `getUsersCount(RealmModel,Set<String>) : int`

| Line | Missing coverage |
|---:|---|
| 802 | `true` branch |

### Line 1056 — `getFederatedIdentity(RealmModel,UserModel,String) : FederatedIdentityModel`

| Line | Missing coverage |
|---:|---|
| 1059 | `false` branch |

### Line 1087 — `createCredential(RealmModel,UserModel,CredentialModel) : CredentialModel`

| Line | Missing coverage |
|---:|---|
| 1092 | `false` branch |

### Line 1150 — `addIssuedVerifiableCredential(IssuedVerifiableCredentialModel) : IssuedVerifiableCredentialModel`

| Line | Missing coverage |
|---:|---|
| 1154 | `false` branch |
| 1158 | `true` branch |
| 1158 | `false` branch |
| 1172 | `true` branch |

### Line 1202 — `ensureEmailConstraint(List<UserEntity>,RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 1205 | `true` branch |
| 1211 | `true` branch |
| 1215 | `true` branch |

### Line 1223 — `getSearchOptionPredicate(String,CriteriaBuilder,From<?, UserEntity>) : Predicate`

| Line | Missing coverage |
|---:|---|
| 1228 | `true` branch |
| 1240 | `false` branch |

### Line 1256 — `predicates(Map<String, String>,Root<UserEntity>,Map<String, String>) : List<Predicate>`

| Line | Missing coverage |
|---:|---|
| 1268 | `true` branch |
| 1278 | `true` branch |
| 1278 | `false` branch |
| 1286 | `true` branch |
| 1286 | `false` branch |
| 1299 | `true` branch |
| 1299 | `false` branch |
| 1305 | `true` branch |
| 1305 | `false` branch |
| 1313 | `false` branch |
| 1328 | `true` branch |
| 1328 | `false` branch |
| 1334 | `true` branch |
| 1334 | `false` branch |
| 1348 | `true` branch |

### Line 1365 — `addSearchPredicates(String,CriteriaBuilder,From<?, UserEntity>,List<Predicate>) : void`

| Line | Missing coverage |
|---:|---|
| 1366 | `true` branch |
