# Coverage Gaps

**File:** `ModelToRepresentation.java`

**Branches:** 73 / 188 covered — 115 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 259 | `toGroupHierarchy(KeycloakSession,RealmModel,boolean) : Stream<GroupRepresentation>` |
| 265 | `toGroupHierarchy(GroupModel,boolean) : GroupRepresentation` |
| 270 | `toGroupHierarchy(GroupModel,boolean,String) : GroupRepresentation` |
| 289 | `groupMatchesSearchOrIsPathElement(GroupModel,String,Boolean) : boolean` |
| 486 | `toRepresentation(KeycloakSession,RealmModel,boolean) : RealmRepresentation` |
| 727 | `exportGroups(KeycloakSession,RealmModel,RealmRepresentation) : void` |
| 978 | `toRepresentation(KeycloakSession,RealmModel,IdentityProviderModel) : IdentityProviderRepresentation` |
| 1032 | `toRepresentation(IdentityProviderMapperModel) : IdentityProviderMapperRepresentation` |
| 1043 | `toRepresentation(UserConsentModel) : UserConsentRepresentation` |
| 1066 | `toRepresentation(UserVerifiableCredentialModel,String) : UserVerifiableCredentialRepresentation` |
| 1076 | `toRepresentation(UserVerifiableCredentialModel,RealmModel) : UserVerifiableCredentialRepresentation` |
| 1108 | `toRepresentation(KeycloakSession,RealmModel,AuthenticationExecutionModel) : AuthenticationExecutionExportRepresentation` |
| 1127 | `toRepresentation(AuthenticationExecutionModel) : AuthenticationExecutionRepresentation` |
| 1160 | `toRepresentation(List<ProviderConfigProperty>) : List<ConfigPropertyRepresentation>` |
| 1169 | `toRepresentation(ProviderConfigProperty) : ConfigPropertyRepresentation` |
| 1182 | `toRepresentation(KeycloakSession,ComponentModel,boolean) : ComponentRepresentation` |
| 1190 | `toRepresentationWithoutConfig(ComponentModel) : ComponentRepresentation` |
| 1202 | `toRepresentation(Scope) : ScopeRepresentation` |
| 1213 | `toRepresentation(ResourceServer,ClientModel) : ResourceServerRepresentation` |
| 1227 | `toRepresentation(Policy,AuthorizationProvider) : R` |
| 1231 | `toRepresentation(Policy,AuthorizationProvider,boolean,boolean) : R` |
| 1235 | `toRepresentation(Policy,AuthorizationProvider,boolean,boolean,boolean) : R` |
| 1291 | `toRepresentation(Resource,ResourceServer,AuthorizationProvider) : ResourceRepresentation` |
| 1295 | `toRepresentation(Resource,ResourceServer,AuthorizationProvider,Boolean) : ResourceRepresentation` |
| 1346 | `toRepresentation(PermissionTicket,AuthorizationProvider) : PermissionTicketRepresentation` |
| 1350 | `toRepresentation(PermissionTicket,AuthorizationProvider,boolean) : PermissionTicketRepresentation` |
| 1389 | `toResourceServerRepresentation(KeycloakSession,ClientModel) : ResourceServerRepresentation` |
| 1461 | `toRepresentation(AuthorizationProvider,Policy) : PolicyRepresentation` |
| 1495 | `toRepresentation(RequiredActionConfigModel) : RequiredActionConfigRepresentation` |
| 1501 | `toRepresentation(OrganizationModel) : OrganizationRepresentation` |
| 1505 | `toRepresentation(OrganizationModel,boolean) : OrganizationRepresentation` |
| 1524 | `toRepresentation(OrganizationDomainModel) : OrganizationDomainRepresentation` |
| 1531 | `toRepresentation(IssuedVerifiableCredentialModel,String) : IssuedVerifiableCredentialRepresentation` |
| 1543 | `toRepresentation(IssuedVerifiableCredentialModel,KeycloakSession,RealmModel) : IssuedVerifiableCredentialRepresentation` |

## Partially Covered Methods

### Line 310 — `toRepresentation(KeycloakSession,RealmModel,UserModel,UserRepresentation,boolean) : UserRepresentation`

| Line | Missing coverage |
|---:|---|
| 325 | `false` branch |
| 333 | `true` branch |
| 340 | `false` branch |
| 347 | `false` branch |

### Line 356 — `toRepresentation(KeycloakSession,UserModel,boolean) : UserRepresentation`

| Line | Missing coverage |
|---:|---|
| 362 | `true` branch |

### Line 375 — `toBriefRepresentation(UserModel,UserRepresentation,boolean) : UserRepresentation`

| Line | Missing coverage |
|---:|---|
| 377 | `false` branch |
| 381 | `false` branch |
| 384 | `false` branch |
| 387 | `false` branch |
| 390 | `false` branch |

### Line 399 — `addAttributeToBriefRep(UserModel,UserRepresentation,String) : void`

| Line | Missing coverage |
|---:|---|
| 401 | `false` branch |
| 402 | `false` branch |

### Line 424 — `toRepresentation(AdminEvent) : AdminEventRepresentation`

| Line | Missing coverage |
|---:|---|
| 429 | `false` branch |
| 433 | `false` branch |

### Line 490 — `toRepresentation(KeycloakSession,RealmModel,boolean,boolean) : RealmRepresentation`

| Line | Missing coverage |
|---:|---|
| 513 | `false` branch |
| 520 | `false` branch |
| 570 | `true` branch |
| 632 | `false` branch |
| 633 | `false` branch |
| 634 | `false` branch |
| 635 | `false` branch |
| 636 | `false` branch |
| 637 | `false` branch |
| 638 | `false` branch |
| 642 | `true` branch |
| 652 | `true` branch |
| 659 | `true` branch |
| 663 | `true` branch |
| 674 | `true` branch |
| 678 | `true` branch |
| 689 | `false` branch |

### Line 750 — `toEventsConfigReprensetation(RealmModel) : RealmEventsConfigRepresentation`

| Line | Missing coverage |
|---:|---|
| 754 | `false` branch |

### Line 807 — `toLocalizedMessage(CredentialMetadata.LocalizedMessage) : LocalizedMessage`

| Line | Missing coverage |
|---:|---|
| 808 | `false` branch |
| 811 | `true` branch |
| 811 | `false` branch |

### Line 817 — `toRepresentation(CredentialMetadata) : CredentialMetadataRepresentation`

| Line | Missing coverage |
|---:|---|
| 822 | `false` branch |

### Line 857 — `toRepresentation(ClientScopeModel) : ClientScopeRepresentation`

| Line | Missing coverage |
|---:|---|
| 865 | `false` branch |

### Line 874 — `toRepresentation(ClientModel,KeycloakSession) : ClientRepresentation`

| Line | Missing coverage |
|---:|---|
| 918 | `false` branch |
| 923 | `false` branch |
| 927 | `true` branch |
| 933 | `true` branch |
| 940 | `false` branch |

### Line 948 — `isRealmClient(String,RealmModel,KeycloakSession) : boolean`

| Line | Missing coverage |
|---:|---|
| 951 | `true` branch |
| 954 | `true` branch |
| 957 | `true` branch |

### Line 982 — `toRepresentation(KeycloakSession,RealmModel,IdentityProviderModel,boolean) : IdentityProviderRepresentation`

| Line | Missing coverage |
|---:|---|
| 995 | `false` branch |
| 1004 | `false` branch |
| 1006 | `true` branch |
| 1012 | `false` branch |
