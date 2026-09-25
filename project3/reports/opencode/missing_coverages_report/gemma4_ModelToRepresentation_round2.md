# Coverage Gaps

**File:** `ModelToRepresentation.java`

**Branches:** 107 / 188 covered — 81 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 289 | `groupMatchesSearchOrIsPathElement(GroupModel,String,Boolean) : boolean` |
| 486 | `toRepresentation(KeycloakSession,RealmModel,boolean) : RealmRepresentation` |
| 1076 | `toRepresentation(UserVerifiableCredentialModel,RealmModel) : UserVerifiableCredentialRepresentation` |
| 1108 | `toRepresentation(KeycloakSession,RealmModel,AuthenticationExecutionModel) : AuthenticationExecutionExportRepresentation` |
| 1160 | `toRepresentation(List<ProviderConfigProperty>) : List<ConfigPropertyRepresentation>` |
| 1213 | `toRepresentation(ResourceServer,ClientModel) : ResourceServerRepresentation` |
| 1461 | `toRepresentation(AuthorizationProvider,Policy) : PolicyRepresentation` |
| 1543 | `toRepresentation(IssuedVerifiableCredentialModel,KeycloakSession,RealmModel) : IssuedVerifiableCredentialRepresentation` |

## Partially Covered Methods

### Line 310 — `toRepresentation(KeycloakSession,RealmModel,UserModel,UserRepresentation,boolean) : UserRepresentation`

| Line | Missing coverage |
|---:|---|
| 325 | `false` branch |
| 333 | `true` branch |
| 340 | `false` branch |

### Line 356 — `toRepresentation(KeycloakSession,UserModel,boolean) : UserRepresentation`

| Line | Missing coverage |
|---:|---|
| 362 | `true` branch |

### Line 375 — `toBriefRepresentation(UserModel,UserRepresentation,boolean) : UserRepresentation`

| Line | Missing coverage |
|---:|---|
| 390 | `false` branch |

### Line 399 — `addAttributeToBriefRep(UserModel,UserRepresentation,String) : void`

| Line | Missing coverage |
|---:|---|
| 402 | `false` branch |

### Line 490 — `toRepresentation(KeycloakSession,RealmModel,boolean,boolean) : RealmRepresentation`

| Line | Missing coverage |
|---:|---|
| 570 | `true` branch |
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
| 1006 | `true` branch |
| 1012 | `false` branch |

### Line 1043 — `toRepresentation(UserConsentModel) : UserConsentRepresentation`

| Line | Missing coverage |
|---:|---|
| 1048 | `true` branch |
| 1048 | `false` branch |
| 1050 | `true` branch |
| 1050 | `false` branch |

### Line 1182 — `toRepresentation(KeycloakSession,ComponentModel,boolean) : ComponentRepresentation`

| Line | Missing coverage |
|---:|---|
| 1184 | `true` branch |

### Line 1235 — `toRepresentation(Policy,AuthorizationProvider,boolean,boolean,boolean) : R`

| Line | Missing coverage |
|---:|---|
| 1239 | `false` branch |
| 1242 | `false` branch |
| 1243 | `true` branch |
| 1268 | `true` branch |

### Line 1295 — `toRepresentation(Resource,ResourceServer,AuthorizationProvider,Boolean) : ResourceRepresentation`

| Line | Missing coverage |
|---:|---|
| 1313 | `false` branch |
| 1319 | `true` branch |
| 1319 | `false` branch |
| 1328 | `false` branch |
| 1334 | `true` branch |
| 1334 | `false` branch |

### Line 1350 — `toRepresentation(PermissionTicket,AuthorizationProvider,boolean) : PermissionTicketRepresentation`

| Line | Missing coverage |
|---:|---|
| 1362 | `true` branch |
| 1369 | `true` branch |
| 1369 | `false` branch |
| 1379 | `true` branch |
| 1381 | `true` branch |
| 1381 | `false` branch |

### Line 1389 — `toResourceServerRepresentation(KeycloakSession,ClientModel) : ResourceServerRepresentation`

| Line | Missing coverage |
|---:|---|
| 1395 | `false` branch |
| 1411 | `true` branch |
| 1411 | `false` branch |

### Line 1505 — `toRepresentation(OrganizationModel,boolean) : OrganizationRepresentation`

| Line | Missing coverage |
|---:|---|
| 1506 | `true` branch |
| 1513 | `true` branch |
