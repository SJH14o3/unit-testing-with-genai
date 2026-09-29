# Coverage Gaps

**File:** `RepresentationToModel.java`

**Branches:** 159 / 430 covered — 271 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 155 | `importRealm(KeycloakSession,RealmRepresentation,RealmModel,Runnable) : void` |
| 159 | `importRoles(RolesRepresentation,RealmModel) : void` |
| 284 | `updateRealm(RealmRepresentation,RealmModel,KeycloakSession) : void` |
| 304 | `addComposites(RoleModel,RoleRepresentation,RealmModel) : void` |
| 479 | `getUpdatedClient() : ClientModel` |
| 484 | `getPreviousClientId() : String` |
| 489 | `getNewClientId() : String` |
| 494 | `getKeycloakSession() : KeycloakSession` |
| 777 | `createUser(KeycloakSession,RealmModel,UserRepresentation) : UserModel` |
| 835 | `createVerifiableCredentials(UserRepresentation,KeycloakSession,UserModel) : void` |
| 844 | `createIssuedVerifiableCredentials(UserRepresentation,KeycloakSession,UserModel) : void` |
| 1084 | `toModel(KeycloakSession,RealmModel,AuthenticationExecutionRepresentation) : AuthenticationExecutionModel` |
| 1112 | `toModel(KeycloakSession,ComponentRepresentation) : ComponentModel` |
| 1692 | `toModel(ScopeRepresentation,ResourceServer,AuthorizationProvider) : Scope` |
| 1842 | `toModel(IssuedVerifiableCredentialRepresentation,String) : IssuedVerifiableCredentialModel` |

## Partially Covered Methods

### Line 207 — `importGroup(RealmModel,GroupModel,GroupRepresentation) : void`

| Line | Missing coverage |
|---:|---|
| 221 | `true` branch |
| 236 | `true` branch |
| 244 | `false` branch |

### Line 252 — `convertDeprecatedCredentialsFormat(UserRepresentation) : void`

| Line | Missing coverage |
|---:|---|
| 253 | `false` branch |
| 256 | `false` branch |
| 259 | `false` branch |
| 265 | `true` branch |
| 265 | `false` branch |

### Line 293 — `createRole(RealmModel,RoleRepresentation) : RoleModel`

| Line | Missing coverage |
|---:|---|
| 295 | `false` branch |

### Line 344 — `createClient(KeycloakSession,RealmModel,ClientRepresentation,Map<String, String>) : ClientModel`

| Line | Missing coverage |
|---:|---|
| 358 | `true` branch |
| 364 | `true` branch |
| 370 | `true` branch |
| 372 | `true` branch |
| 372 | `false` branch |
| 377 | `true` branch |
| 377 | `false` branch |
| 380 | `true` branch |
| 380 | `false` branch |
| 388 | `true` branch |
| 394 | `true` branch |
| 405 | `true` branch |

### Line 418 — `addClientScopeToClient(RealmModel,ClientModel,String,boolean) : void`

| Line | Missing coverage |
|---:|---|
| 420 | `false` branch |

### Line 427 — `updateClient(ClientRepresentation,ClientModel,KeycloakSession) : void`

| Line | Missing coverage |
|---:|---|
| 429 | `true` branch |
| 430 | `true` branch |
| 430 | `false` branch |
| 433 | `true` branch |
| 433 | `false` branch |
| 444 | `false` branch |
| 448 | `true` branch |
| 454 | `true` branch |
| 456 | `true` branch |
| 456 | `false` branch |
| 460 | `true` branch |
| 460 | `false` branch |
| 468 | `true` branch |
| 476 | `false` branch |

### Line 511 — `<init>, line 511()`

| Line | Missing coverage |
|---:|---|
| 548 | `true` branch |
| 561 | `true` branch |

### Line 585 — `determineNewSecret(ClientModel,ClientRepresentation) : String`

| Line | Missing coverage |
|---:|---|
| 586 | `true` branch |
| 595 | `false` branch |
| 597 | `true` branch |
| 597 | `false` branch |

### Line 604 — `defaultWebOrigins(ClientModel) : Set<String>`

| Line | Missing coverage |
|---:|---|
| 606 | `true` branch |
| 611 | `false` branch |

### Line 622 — `filterEmptyValues(Collection<String>) : Set<String>`

| Line | Missing coverage |
|---:|---|
| 623 | `false` branch |

### Line 629 — `updateClientProtocolMappers(ClientRepresentation,ClientModel) : void`

| Line | Missing coverage |
|---:|---|
| 631 | `false` branch |
| 640 | `true` branch |

### Line 662 — `updateClientScopes(ClientRepresentation,ClientModel) : void`

| Line | Missing coverage |
|---:|---|
| 676 | `true` branch |
| 676 | `false` branch |
| 683 | `false` branch |

### Line 734 — `createClientScope(RealmModel,ClientScopeRepresentation) : ClientScopeModel`

| Line | Missing coverage |
|---:|---|
| 737 | `true` branch |
| 738 | `false` branch |
| 739 | `true` branch |
| 740 | `false` branch |
| 741 | `true` branch |
| 751 | `true` branch |

### Line 761 — `updateClientScope(ClientScopeRepresentation,ClientScopeModel) : void`

| Line | Missing coverage |
|---:|---|
| 762 | `false` branch |
| 763 | `true` branch |
| 765 | `false` branch |
| 767 | `true` branch |

### Line 785 — `createGroups(KeycloakSession,UserRepresentation,RealmModel,UserModel,Consumer<GroupModel>) : void`

| Line | Missing coverage |
|---:|---|
| 788 | `false` branch |

### Line 800 — `createFederatedIdentities(UserRepresentation,KeycloakSession,RealmModel,UserModel) : void`

| Line | Missing coverage |
|---:|---|
| 801 | `false` branch |

### Line 809 — `createCredentials(UserRepresentation,KeycloakSession,RealmModel,UserModel,boolean) : void`

| Line | Missing coverage |
|---:|---|
| 811 | `false` branch |
| 813 | `true` branch |
| 816 | `true` branch |

### Line 877 — `createRoleMappings(UserRepresentation,UserModel,RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 881 | `true` branch |
| 887 | `false` branch |

### Line 898 — `createClientRoleMappings(ClientModel,UserModel,List<String>) : void`

| Line | Missing coverage |
|---:|---|
| 899 | `true` branch |
| 905 | `true` branch |

### Line 913 — `toModel(RealmModel,IdentityProviderRepresentation,KeycloakSession) : IdentityProviderModel`

| Line | Missing coverage |
|---:|---|
| 937 | `true` branch |
| 947 | `false` branch |
| 948 | `true` branch |
| 959 | `true` branch |
| 970 | `true` branch |

### Line 1001 — `toModel(RealmModel,UserConsentRepresentation,KeycloakSession) : UserConsentModel`

| Line | Missing coverage |
|---:|---|
| 1003 | `true` branch |
| 1011 | `false` branch |
| 1014 | `false` branch |
| 1016 | `true` branch |
| 1016 | `false` branch |
| 1020 | `true` branch |
| 1020 | `false` branch |
| 1028 | `true` branch |
| 1028 | `false` branch |
| 1040 | `false` branch |
| 1041 | `false` branch |
| 1043 | `true` branch |

### Line 1053 — `toModel(UserVerifiableCredentialRepresentation,RealmModel) : UserVerifiableCredentialModel`

| Line | Missing coverage |
|---:|---|
| 1054 | `true` branch |
| 1059 | `true` branch |

### Line 1145 — `updateComponent(KeycloakSession,ComponentRepresentation,ComponentModel,boolean) : void`

| Line | Missing coverage |
|---:|---|
| 1146 | `false` branch |
| 1150 | `true` branch |
| 1154 | `true` branch |
| 1158 | `true` branch |
| 1162 | `true` branch |
| 1167 | `true` branch |
| 1171 | `false` branch |
| 1174 | `true` branch |
| 1179 | `true` branch |
| 1185 | `true` branch |
| 1190 | `false` branch |

### Line 1198 — `importAuthorizationSettings(ClientRepresentation,ClientModel,KeycloakSession) : void`

| Line | Missing coverage |
|---:|---|
| 1199 | `false` branch |
| 1209 | `false` branch |

### Line 1219 — `toModel(ResourceServerRepresentation,AuthorizationProvider,ClientModel) : ResourceServer`

| Line | Missing coverage |
|---:|---|
| 1253 | `true` branch |
| 1253 | `false` branch |
| 1257 | `true` branch |
| 1257 | `false` branch |
| 1260 | `true` branch |
| 1260 | `false` branch |

### Line 1273 — `importPolicies(AuthorizationProvider,ResourceServer,List<PolicyRepresentation>,String) : Policy`

| Line | Missing coverage |
|---:|---|
| 1277 | `true` branch |
| 1277 | `false` branch |
| 1284 | `true` branch |
| 1284 | `false` branch |
| 1293 | `true` branch |
| 1293 | `false` branch |
| 1297 | `true` branch |
| 1297 | `false` branch |
| 1299 | `true` branch |
| 1299 | `false` branch |
| 1316 | `true` branch |
| 1316 | `false` branch |
| 1320 | `true` branch |
| 1320 | `false` branch |
| 1326 | `true` branch |
| 1326 | `false` branch |

### Line 1334 — `toModel(AbstractPolicyRepresentation,AuthorizationProvider,Policy) : Policy`

| Line | Missing coverage |
|---:|---|
| 1345 | `false` branch |
| 1348 | `false` branch |
| 1351 | `true` branch |
| 1362 | `false` branch |
| 1365 | `true` branch |
| 1376 | `false` branch |
| 1379 | `true` branch |
| 1401 | `true` branch |
| 1405 | `false` branch |
| 1407 | `true` branch |
| 1407 | `false` branch |

### Line 1419 — `updateScopes(AbstractPolicyRepresentation,Policy,StoreFactory) : void`

| Line | Missing coverage |
|---:|---|
| 1422 | `false` branch |
| 1426 | `true` branch |
| 1426 | `false` branch |
| 1437 | `true` branch |
| 1437 | `false` branch |
| 1441 | `true` branch |
| 1441 | `false` branch |
| 1444 | `true` branch |
| 1444 | `false` branch |
| 1446 | `true` branch |
| 1446 | `false` branch |
| 1459 | `true` branch |
| 1459 | `false` branch |
| 1463 | `true` branch |
| 1463 | `false` branch |

### Line 1471 — `updateAssociatedPolicies(AbstractPolicyRepresentation,Policy,StoreFactory) : void`

| Line | Missing coverage |
|---:|---|
| 1475 | `false` branch |
| 1479 | `true` branch |
| 1479 | `false` branch |
| 1492 | `true` branch |
| 1492 | `false` branch |
| 1497 | `true` branch |
| 1497 | `false` branch |
| 1500 | `true` branch |
| 1500 | `false` branch |
| 1502 | `true` branch |
| 1502 | `false` branch |
| 1515 | `true` branch |
| 1515 | `false` branch |
| 1519 | `true` branch |
| 1519 | `false` branch |

### Line 1527 — `updateResources(AbstractPolicyRepresentation,Policy,AuthorizationProvider) : void`

| Line | Missing coverage |
|---:|---|
| 1530 | `false` branch |
| 1538 | `true` branch |
| 1538 | `false` branch |
| 1547 | `true` branch |
| 1547 | `false` branch |
| 1557 | `true` branch |
| 1557 | `false` branch |
| 1561 | `true` branch |
| 1561 | `false` branch |
| 1564 | `true` branch |
| 1564 | `false` branch |
| 1566 | `true` branch |
| 1566 | `false` branch |
| 1579 | `true` branch |
| 1579 | `false` branch |
| 1584 | `true` branch |
| 1584 | `false` branch |

### Line 1594 — `toModel(ResourceRepresentation,ResourceServer,AuthorizationProvider) : Resource`

| Line | Missing coverage |
|---:|---|
| 1599 | `true` branch |
| 1606 | `true` branch |
| 1610 | `false` branch |
| 1615 | `true` branch |
| 1619 | `true` branch |
| 1628 | `true` branch |
| 1634 | `false` branch |
| 1646 | `true` branch |
| 1649 | `true` branch |
| 1649 | `false` branch |
| 1675 | `true` branch |
| 1675 | `false` branch |
| 1681 | `true` branch |
| 1681 | `false` branch |

### Line 1696 — `toModel(ScopeRepresentation,ResourceServer,AuthorizationProvider,boolean) : Scope`

| Line | Missing coverage |
|---:|---|
| 1701 | `true` branch |
| 1707 | `false` branch |
| 1708 | `false` branch |

### Line 1726 — `toModel(PermissionTicketRepresentation,ResourceServer,AuthorizationProvider) : PermissionTicket`

| Line | Missing coverage |
|---:|---|
| 1731 | `false` branch |
| 1733 | `true` branch |
| 1733 | `false` branch |

### Line 1755 — `createResourceServer(ClientModel,KeycloakSession,boolean) : ResourceServer`

| Line | Missing coverage |
|---:|---|
| 1756 | `false` branch |
| 1763 | `true` branch |
| 1763 | `false` branch |
| 1767 | `true` branch |
| 1767 | `false` branch |
| 1770 | `true` branch |
| 1770 | `false` branch |
| 1774 | `true` branch |
| 1774 | `false` branch |

### Line 1787 — `updateOrganizationBroker(IdentityProviderRepresentation,KeycloakSession) : void`

| Line | Missing coverage |
|---:|---|
| 1794 | `true` branch |
| 1796 | `false` branch |
| 1798 | `false` branch |
| 1802 | `true` branch |
| 1808 | `true` branch |
| 1810 | `true` branch |

### Line 1819 — `toModel(OrganizationRepresentation,OrganizationModel) : OrganizationModel`

| Line | Missing coverage |
|---:|---|
| 1820 | `true` branch |
