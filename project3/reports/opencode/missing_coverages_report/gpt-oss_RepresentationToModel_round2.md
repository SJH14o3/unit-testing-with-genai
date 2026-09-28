# Coverage Gaps

**File:** `RepresentationToModel.java`

**Branches:** 62 / 430 covered — 368 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 252 | `convertDeprecatedCredentialsFormat(UserRepresentation) : void` |
| 304 | `addComposites(RoleModel,RoleRepresentation,RealmModel) : void` |
| 418 | `addClientScopeToClient(RealmModel,ClientModel,String,boolean) : void` |
| 479 | `getUpdatedClient() : ClientModel` |
| 494 | `getKeycloakSession() : KeycloakSession` |
| 761 | `updateClientScope(ClientScopeRepresentation,ClientScopeModel) : void` |
| 781 | `createGroups(KeycloakSession,UserRepresentation,RealmModel,UserModel) : void` |
| 785 | `createGroups(KeycloakSession,UserRepresentation,RealmModel,UserModel,Consumer<GroupModel>) : void` |
| 800 | `createFederatedIdentities(UserRepresentation,KeycloakSession,RealmModel,UserModel) : void` |
| 809 | `createCredentials(UserRepresentation,KeycloakSession,RealmModel,UserModel,boolean) : void` |
| 835 | `createVerifiableCredentials(UserRepresentation,KeycloakSession,UserModel) : void` |
| 844 | `createIssuedVerifiableCredentials(UserRepresentation,KeycloakSession,UserModel) : void` |
| 864 | `toModel(CredentialRepresentation) : CredentialModel` |
| 877 | `createRoleMappings(UserRepresentation,UserModel,RealmModel) : void` |
| 898 | `createClientRoleMappings(ClientModel,UserModel,List<String>) : void` |
| 913 | `toModel(RealmModel,IdentityProviderRepresentation,KeycloakSession) : IdentityProviderModel` |
| 991 | `toModel(IdentityProviderMapperRepresentation) : IdentityProviderMapperModel` |
| 1001 | `toModel(RealmModel,UserConsentRepresentation,KeycloakSession) : UserConsentModel` |
| 1053 | `toModel(UserVerifiableCredentialRepresentation,RealmModel) : UserVerifiableCredentialModel` |
| 1071 | `toModel(AuthenticationFlowRepresentation) : AuthenticationFlowModel` |
| 1084 | `toModel(KeycloakSession,RealmModel,AuthenticationExecutionRepresentation) : AuthenticationExecutionModel` |
| 1104 | `toModel(AuthenticatorConfigRepresentation) : AuthenticatorConfigModel` |
| 1112 | `toModel(KeycloakSession,ComponentRepresentation) : ComponentModel` |
| 1145 | `updateComponent(KeycloakSession,ComponentRepresentation,ComponentModel,boolean) : void` |
| 1198 | `importAuthorizationSettings(ClientRepresentation,ClientModel,KeycloakSession) : void` |
| 1219 | `toModel(ResourceServerRepresentation,AuthorizationProvider,ClientModel) : ResourceServer` |
| 1273 | `importPolicies(AuthorizationProvider,ResourceServer,List<PolicyRepresentation>,String) : Policy` |
| 1334 | `toModel(AbstractPolicyRepresentation,AuthorizationProvider,Policy) : Policy` |
| 1419 | `updateScopes(AbstractPolicyRepresentation,Policy,StoreFactory) : void` |
| 1471 | `updateAssociatedPolicies(AbstractPolicyRepresentation,Policy,StoreFactory) : void` |
| 1527 | `updateResources(AbstractPolicyRepresentation,Policy,AuthorizationProvider) : void` |
| 1594 | `toModel(ResourceRepresentation,ResourceServer,AuthorizationProvider) : Resource` |
| 1692 | `toModel(ScopeRepresentation,ResourceServer,AuthorizationProvider) : Scope` |
| 1696 | `toModel(ScopeRepresentation,ResourceServer,AuthorizationProvider,boolean) : Scope` |
| 1726 | `toModel(PermissionTicketRepresentation,ResourceServer,AuthorizationProvider) : PermissionTicket` |
| 1755 | `createResourceServer(ClientModel,KeycloakSession,boolean) : ResourceServer` |
| 1787 | `updateOrganizationBroker(IdentityProviderRepresentation,KeycloakSession) : void` |
| 1819 | `toModel(OrganizationRepresentation,OrganizationModel) : OrganizationModel` |
| 1838 | `toModel(OrganizationDomainRepresentation) : OrganizationDomainModel` |
| 1842 | `toModel(IssuedVerifiableCredentialRepresentation,String) : IssuedVerifiableCredentialModel` |

## Partially Covered Methods

### Line 159 — `importRoles(RolesRepresentation,RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 160 | `true` branch |
| 164 | `true` branch |
| 164 | `false` branch |
| 169 | `false` branch |
| 172 | `false` branch |
| 177 | `true` branch |
| 177 | `false` branch |
| 179 | `true` branch |
| 179 | `false` branch |
| 186 | `true` branch |
| 186 | `false` branch |
| 192 | `true` branch |
| 192 | `false` branch |
| 195 | `true` branch |
| 195 | `false` branch |

### Line 207 — `importGroup(RealmModel,GroupModel,GroupRepresentation) : void`

| Line | Missing coverage |
|---:|---|
| 221 | `true` branch |
| 227 | `true` branch |
| 230 | `true` branch |
| 230 | `false` branch |
| 236 | `true` branch |
| 236 | `false` branch |
| 244 | `false` branch |

### Line 293 — `createRole(RealmModel,RoleRepresentation) : RoleModel`

| Line | Missing coverage |
|---:|---|
| 294 | `true` branch |
| 295 | `true` branch |

### Line 344 — `createClient(KeycloakSession,RealmModel,ClientRepresentation,Map<String, String>) : ClientModel`

| Line | Missing coverage |
|---:|---|
| 347 | `true` branch |
| 349 | `true` branch |
| 358 | `true` branch |
| 364 | `true` branch |
| 372 | `true` branch |
| 377 | `true` branch |
| 380 | `true` branch |
| 388 | `true` branch |
| 394 | `true` branch |
| 405 | `true` branch |

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
| 456 | `true` branch |
| 460 | `true` branch |
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

### Line 662 — `updateClientScopes(ClientRepresentation,ClientModel) : void`

| Line | Missing coverage |
|---:|---|
| 663 | `true` branch |
| 676 | `true` branch |
| 676 | `false` branch |
| 683 | `true` branch |
| 683 | `false` branch |

### Line 734 — `createClientScope(RealmModel,ClientScopeRepresentation) : ClientScopeModel`

| Line | Missing coverage |
|---:|---|
| 737 | `false` branch |
| 738 | `false` branch |
| 739 | `false` branch |
| 740 | `false` branch |
| 741 | `false` branch |
| 751 | `true` branch |

### Line 1740 — `removeEmptyString(Map<String, String>) : Map<String, String>`

| Line | Missing coverage |
|---:|---|
| 1741 | `true` branch |
| 1746 | `true` branch |
| 1748 | `true` branch |
| 1748 | `false` branch |
