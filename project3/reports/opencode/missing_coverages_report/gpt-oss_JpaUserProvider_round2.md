# Coverage Gaps

**File:** `JpaUserProvider.java`

**Branches:** 52 / 158 covered — 106 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 172 | `removeUser(UserEntity) : void` |
| 198 | `updateFederatedIdentity(RealmModel,UserModel,FederatedIdentityModel) : void` |
| 212 | `removeFederatedIdentity(RealmModel,UserModel,String) : boolean` |
| 269 | `updateConsent(RealmModel,String,UserConsentModel) : void` |
| 281 | `revokeConsentForClient(RealmModel,String,String) : boolean` |
| 375 | `createUserConsentClientScopeEntity(UserConsentEntity,ClientScopeModel,String,Collection<UserConsentClientScopeEntity>,Collection<UserConsentClientScopeEntity>) : void` |
| 439 | `removeVerifiableCredential(String,String) : boolean` |
| 459 | `getVerifiableCredentialsByUser(String) : Stream<UserVerifiableCredentialModel>` |
| 472 | `getVerifiableCredentialByClientScope(String,String) : UserVerifiableCredentialModel` |
| 481 | `updateVerifiableCredential(String,String) : UserVerifiableCredentialModel` |
| 520 | `getVerifiableCredentialsEntitiesByUser(String) : Stream<UserVerifiableCredentialEntity>` |
| 572 | `preRemove(RealmModel) : void` |
| 638 | `unlinkUsers(RealmModel,String) : void` |
| 677 | `preRemove(ProtocolMapperModel) : void` |
| 695 | `getGroupMembersStream(RealmModel,GroupModel) : Stream<UserModel>` |
| 700 | `getRoleMembersStream(RealmModel,RoleModel) : Stream<UserModel>` |
| 718 | `getUserByUsername(RealmModel,String) : UserModel` |
| 728 | `getUserByEmail(RealmModel,String) : UserModel` |
| 742 | `close() : void` |
| 746 | `getUserByFederatedIdentity(RealmModel,FederatedIdentityModel) : UserModel` |
| 814 | `getUsersCount(RealmModel,String) : int` |
| 829 | `getUsersCount(RealmModel,String,Set<String>) : int` |
| 850 | `getUsersCount(RealmModel,Map<String, String>) : int` |
| 866 | `getUsersCount(RealmModel,Map<String, String>,Set<String>) : int` |
| 893 | `getGroupMembersStream(RealmModel,GroupModel,Integer,Integer) : Stream<UserModel>` |
| 913 | `getGroupMembersStream(RealmModel,GroupModel,String,Boolean,Integer,Integer) : Stream<UserModel>` |
| 957 | `getRoleMembersStream(RealmModel,RoleModel,Integer,Integer) : Stream<UserModel>` |
| 981 | `searchForUserStream(RealmModel,String,Integer,Integer) : Stream<UserModel>` |
| 990 | `searchForUserStream(RealmModel,Map<String, String>,Integer,Integer) : Stream<UserModel>` |
| 1016 | `searchForUserByUserAttributeStream(RealmModel,String,String) : Stream<UserModel>` |
| 1035 | `findFederatedIdentity(UserModel,String,LockModeType) : FederatedIdentityEntity` |
| 1046 | `getFederatedIdentitiesStream(RealmModel,UserModel) : Stream<FederatedIdentityModel>` |
| 1056 | `getFederatedIdentity(RealmModel,UserModel,String) : FederatedIdentityModel` |
| 1082 | `updateCredential(RealmModel,UserModel,CredentialModel) : void` |
| 1087 | `createCredential(RealmModel,UserModel,CredentialModel) : CredentialModel` |
| 1098 | `removeStoredCredential(RealmModel,UserModel,String) : boolean` |
| 1108 | `getStoredCredentialById(RealmModel,UserModel,String) : CredentialModel` |
| 1113 | `toModel(CredentialEntity) : CredentialModel` |
| 1117 | `getStoredCredentialsStream(RealmModel,UserModel) : Stream<CredentialModel>` |
| 1122 | `getStoredCredentialsByTypeStream(RealmModel,UserModel,String) : Stream<CredentialModel>` |
| 1135 | `getStoredCredentialByNameAndType(RealmModel,UserModel,String,String) : CredentialModel` |
| 1140 | `moveCredentialTo(RealmModel,UserModel,String,String) : boolean` |
| 1145 | `getUserCredentialManager(UserModel) : UserCredentialManager` |
| 1150 | `addIssuedVerifiableCredential(IssuedVerifiableCredentialModel) : IssuedVerifiableCredentialModel` |
| 1183 | `getIssuedVerifiableCredentialsStreamByUser(String) : Stream<IssuedVerifiableCredentialModel>` |
| 1190 | `removeIssuedVerifiableCredential(String) : boolean` |
| 1202 | `ensureEmailConstraint(List<UserEntity>,RealmModel) : void` |
| 1223 | `getSearchOptionPredicate(String,CriteriaBuilder,From<?, UserEntity>) : Predicate` |
| 1251 | `userInEntityManagerContext(String) : UserEntity` |
| 1256 | `predicates(Map<String, String>,Root<UserEntity>,Map<String, String>) : List<Predicate>` |
| 1355 | `getSession() : KeycloakSession` |
| 1360 | `getEntityManager() : EntityManager` |
| 1365 | `addSearchPredicates(String,CriteriaBuilder,From<?, UserEntity>,List<Predicate>) : void` |
| 1374 | `countQuery(CriteriaQuery<Long>,CriteriaBuilder,From<?, UserEntity>,List<Predicate>) : CriteriaQuery<Long>` |
| 1382 | `removeExpiredIssuedVerifiableCredentials() : void` |
| 1390 | `toIssuedVcModel(IssuedVerifiableCredentialEntity) : IssuedVerifiableCredentialModel` |

## Partially Covered Methods

### Line 122 — `addUser(RealmModel,String,String,boolean,boolean) : UserModel`

| Line | Missing coverage |
|---:|---|
| 136 | `false` branch |

### Line 164 — `removeUser(RealmModel,UserModel) : boolean`

| Line | Missing coverage |
|---:|---|
| 167 | `false` branch |

### Line 292 — `getGrantedConsentEntity(String,String,LockModeType) : UserConsentEntity`

| Line | Missing coverage |
|---:|---|
| 294 | `false` branch |
| 297 | `false` branch |
| 305 | `true` branch |
| 307 | `true` branch |

### Line 315 — `toConsentModel(RealmModel,UserConsentEntity) : UserConsentModel`

| Line | Missing coverage |
|---:|---|
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
| 405 | `false` branch |
| 408 | `false` branch |
| 411 | `false` branch |

### Line 562 — `grantToAllUsers(RealmModel,RoleModel) : void`

| Line | Missing coverage |
|---:|---|
| 564 | `true` branch |

### Line 711 — `getUserById(RealmModel,String) : UserModel`

| Line | Missing coverage |
|---:|---|
| 714 | `false` branch |
